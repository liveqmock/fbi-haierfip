package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.hccb.HccbContext;
import fip.gateway.hccb.HccbT1001Handler;
import fip.gateway.hccb.HccbT1003Handler;
import fip.gateway.hccb.model.T1001Response;
import fip.gateway.hccb.model.T1003Request;
import fip.gateway.hccb.model.TxnHead;
import fip.gateway.sbs.DepCtgManager;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.txn.Taa41.Taa41SOFDataDetail;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.dao.PtenudetailMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.security.OperatorManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HCCB 小额信贷.
 * User: zhanrui
 * Date: 2014-08-01
 */
@Service
public class HccbService {
    private static final Logger logger = LoggerFactory.getLogger(HccbService.class);

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipRefunddetlMapper fipRefunddetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;
    @Autowired
    private SbsTxnHelper sbsTxnHelper;

    @Transactional
    public synchronized int importDataFromXls(BizType bizType, List<FipCutpaydetl> cutpaydetls, List<String> returnMsgs) {
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        int count = 0;
        for (FipCutpaydetl cutpaydetl : cutpaydetls) {
            iSeqno++;
            assembleCutpayRecord(bizType, batchno, iSeqno, cutpaydetl);

            //TODO 判断业务主键是否重复   注意 修改IOUNO长度时需要同步修改commonmapper中的SQL
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Hccb(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getOriginBizid());
            if (isNotRepeated) {
                fipCutpaydetlMapper.insert(cutpaydetl);
                count++;
            } else {
                returnMsgs.add("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.error("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //日志
        batchInsertLogByBatchno(batchno, "新建记录", "新获取新消费信贷系统代扣记录");
        return count;
    }

    //=====================
    //从小贷服务器查询代扣数据
    public List<FipCutpaydetl> doQueryHccbBills(BizType bizType) {
        List<FipCutpaydetl> cutpaydetlList = getHccbRecordsBaseInfo();

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;

        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            iSeqno++;
            assembleCutpayRecord(bizType, batchno, iSeqno, cutpaydetl);
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            //cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    //获取小贷代扣记录  不做整体事务处理
    public synchronized int doObtainHccbBills(BizType bizType, List<String> returnMsgs) {
        //1. 获取全部记录 每条记录只包括接口过来的信息
        List<FipCutpaydetl> cutpaydetls = doQueryHccbBills(bizType);

        //2.重新拿到批次号
        String batchno = billManagerService.generateBatchno();

        //3.单笔insert 无事务 同电子表格导入
        return importDataFromXls(bizType, cutpaydetls, returnMsgs);
    }

    //批量回写
    public synchronized int writebackCutPayRecord2Hccb(List<FipCutpaydetl> cutpaydetlList, boolean isArchive) {
        T1003Request request = new T1003Request();
        request.setHead(new TxnHead().txncode("1003"));

        List<T1003Request.Record> t1003RequestRecords = new ArrayList<T1003Request.Record>();
        BigDecimal amt = new BigDecimal("0.00");
        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            T1003Request.Record record = new T1003Request.Record();
            record.setIouno(cutpaydetl.getIouno());
            record.setPoano(cutpaydetl.getPoano());
            record.setTxnamt(cutpaydetl.getPaybackamt().toString());
            record.setSchpaydate(cutpaydetl.getPaybackdate());

            String billStatus = cutpaydetl.getBillstatus();
            if (BillStatus.CUTPAY_SUCCESS.getCode().equals(billStatus)) {
                record.setResultcode("1");   //银行处理成功
            } else if (BillStatus.CUTPAY_FAILED.getCode().equals(billStatus)) {
                record.setResultcode("2");   //银行处理失败
            } else if (BillStatus.CUTPAY_QRY_PEND.getCode().equals(billStatus)) {
                record.setResultcode("3");   //状态不明
            } else {
                logger.error("回写记录时出现错误记录。");
            }
            amt = amt.add(cutpaydetl.getPaybackamt());
            t1003RequestRecords.add(record);
        }

        request.getBody().setTotalitems("" + t1003RequestRecords.size());
        request.getBody().setTotalamt(amt.toString());
        request.getBody().setRecords(t1003RequestRecords);


        //hccb 通讯
        HccbT1003Handler handler = new HccbT1003Handler();
        HccbContext context = new HccbContext();
        context.setRequest(request);
        handler.process(context);

        //响应信息处理
        Map<String, String> paraMap = context.getParaMap();
        String rtnCode = paraMap.get("rtnCode");
        String rtnMsg = paraMap.get("rtnMsg");

        //本地数据库处理 只处理成功返回情况
        if ("0000".equals(rtnCode)) {
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                cutpaydetl.setWritebackflag("1");
                if (isArchive) {
                    cutpaydetl.setArchiveflag("1");   //回写完成 做存档处理
                }
                cutpaydetl.setDateCmsPut(new Date());
                cutpaydetl.setRecversion(cutpaydetl.getRecversion() + 1);
                fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }
        } else {
            logger.error("小贷系统回写失败" + rtnCode + rtnMsg);
        }

        //日志
        if (rtnMsg.length() >= 20) {
            rtnMsg = rtnMsg.substring(0, 20);
        }
        batchInsertLog("批量回写", rtnMsg, cutpaydetlList);

        return cutpaydetlList.size();
    }


    //与hccb通讯，获取记录后 转换为基本的fipcutpay bean
    private List<FipCutpaydetl> getHccbRecordsBaseInfo() {
        HccbT1001Handler handler = new HccbT1001Handler();
        HccbContext context = new HccbContext();
        handler.process(context);
        List<T1001Response.Record> recvedList = (List<T1001Response.Record>) context.getResponse();

        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        for (T1001Response.Record responseBean : recvedList) {
            FipCutpaydetl cutpaydetl = copyT1001ResponseRecord2FipCutpaydetl(responseBean);
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    private FipCutpaydetl copyT1001ResponseRecord2FipCutpaydetl(T1001Response.Record record) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setIouno(record.getIouno());
        cutpaydetl.setPoano(record.getPoano());
        cutpaydetl.setClientno(record.getCustid());//客户号

        cutpaydetl.setClientname(record.getActname());
        cutpaydetl.setBiBankactname(record.getActname());

        cutpaydetl.setClientid(record.getCertid());//证件号码

        if (StringUtils.isEmpty(record.getTxnamt())) {
            throw new RuntimeException("金额字段不能为空");
        }
        cutpaydetl.setPaybackamt(new BigDecimal(record.getTxnamt()));

        cutpaydetl.setBiBankactno(record.getActno());
        cutpaydetl.setBiActopeningbank(record.getBankcode());
        cutpaydetl.setBiProvince(record.getProvince());
        cutpaydetl.setBiCity(record.getCity());

        return cutpaydetl;
    }

    //======================
    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               FipCutpaydetl cutpaydetl) {
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        if (StringUtils.isEmpty(cutpaydetl.getPoano())) {
            cutpaydetl.setPoano("0");
        }

        //还款金额信息
        cutpaydetl.setPrincipalamt(new BigDecimal("0.00")); //还款本金
        cutpaydetl.setInterestamt(new BigDecimal("0.00"));  //还款利息
        cutpaydetl.setPunitiveintamt(new BigDecimal("0.00"));//罚息金额
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//违约金金额
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//罚息复利金额
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //冗余金额

        //还款渠道信息
        if (bizType.equals(BizType.HCCB)) {
            cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //默认为银联
        } else {
            throw new RuntimeException("非HCCB数据，不能处理");
        }

        //其他
        cutpaydetl.setRecversion((long) 0);
        cutpaydetl.setDeletedflag("0");
        cutpaydetl.setArchiveflag("0");
        cutpaydetl.setWritebackflag("0");
        cutpaydetl.setAccountflag("0");
        //帐单状态
        cutpaydetl.setBillstatus(BillStatus.INIT.getCode());
        cutpaydetl.setSendflag("0");

        //zhanrui 20120305  标识消费信贷数据来源自信贷系统 便与回写时区分来源系统
        cutpaydetl.setRemark3("HCCB");
        cutpaydetl.setDateCmsGet(new Date());

        //其它
        cutpaydetl.setBilltype(BillType.NORMAL.getCode());
        cutpaydetl.setClientact("123456"); //不能为空
        return cutpaydetl;
    }


    /**
     * SBS记账  20150505  zhanrui
     */
    public synchronized int accountCutPayRecord2SBS(List<FipCutpaydetl> cutpaydetlList, String totalSuccessAmt) {
        int count = 0;

        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();

        OperatorManager operatorManager = SystemService.getOperatorManager();
        if (operatorManager != null) {
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }

        FipJoblog joblog = new FipJoblog();

        try {
            //金额再次核对
            BigDecimal amt = new BigDecimal(0);
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                amt = amt.add(cutpaydetl.getPaybackamt());
            }
            DecimalFormat df = new DecimalFormat("#,##0.00");
            if (!totalSuccessAmt.equals(df.format(amt))) {
                throw new RuntimeException("总金额不一致，请核对。");
            }

            String sn = "HCCB" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //String fromActno = "801000026131041001"; //对应建行37101985510051003497
            //String toActno = "801000977202019014";
            String fromActno = sbsTxnHelper.selectSbsActnoFromPtEnuDetail("HCCB_FROM_ACTNO"); //对应建行37101985510051003497
            String toActno = sbsTxnHelper.selectSbsActnoFromPtEnuDetail("HCCB_TO_ACTNO");
            String productCode = "N105";
            String remark = "HCCB小贷代扣";
            List<String> paramList = sbsTxnHelper.assembleTaa41Param(sn, fromActno, toActno, amt, productCode, remark);

            //SBS
            byte[] recvBuf = DepCtgManager.processSingleResponsePkg("aa41", paramList);
            SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
            Taa41SOFDataDetail sofDataDetail = new Taa41SOFDataDetail();
            response.setSofDataDetail(sofDataDetail);
            response.init(recvBuf);

            String formcode = response.getFormcode();


            BillStatus billStatus = BillStatus.ACCOUNT_FAILED;
            String logMsg = "";
            if (!formcode.equals("T531")) {     //异常情况处理
                billStatus = BillStatus.ACCOUNT_FAILED;
                logMsg = "SBS入帐失败：FORMCODE=" + formcode;
            } else {
                if (sofDataDetail.getSECNUM().trim().equals(sn)) {
                    billStatus = BillStatus.ACCOUNT_SUCCESS;
                    logMsg = "SBS入帐完成：FORMCODE=" + formcode;
                } else {
                    billStatus = BillStatus.ACCOUNT_PEND;
                    logMsg = "SBS入帐完成,但返回的流水号出错，请查询。FORMCODE=" + formcode;
                }
            }

            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                cutpaydetl.setBillstatus(billStatus.getCode());
                joblog.setJobdesc(logMsg);
                joblog.setTablename("fip_cutpaydetl");
                joblog.setRowpkid(cutpaydetl.getPkid());
                joblog.setJobname("SBS记帐");
                joblog.setJobtime(new Date());
                joblog.setJobuserid(userid);
                joblog.setJobusername(username);
                fipJoblogMapper.insert(joblog);
                fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }

            return count;
        } catch (Exception e) {
            logger.error("入帐时出现错误。", e);
            throw new RuntimeException("入帐时出现错误。", e);
        }
    }

    //============================================

    /**
     * 检查本地表中既存记录的状态 不允许有
     * 1、状态不明的记录
     * 2、未发送的记录
     * 3、发送成功的记录（发送成功的必须入帐回写）
     */
    private boolean checkLocalBillsStatus() {
        return true;
    }

    //TODO  事务
    private void batchInsertLogByBatchno(String batchno, String jobName, String jobDesc) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBatchSnEqualTo(batchno);
        List<FipCutpaydetl> fipCutpaydetlList = fipCutpaydetlMapper.selectByExample(example);

        batchInsertLog(jobName, jobDesc, fipCutpaydetlList);
    }

    private void batchInsertLog(String jobName, String jobDesc, List<FipCutpaydetl> fipCutpaydetlList) {
        Date date = new Date();

        OperatorManager operatorManager = SystemService.getOperatorManager();
        String userid;
        String username;
        if (operatorManager == null) {
            userid = "9999";
            username = "BATCH";
        } else {
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }

        for (FipCutpaydetl fipCutpaydetl : fipCutpaydetlList) {
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(fipCutpaydetl.getPkid());
            log.setJobname(jobName);
            log.setJobdesc(jobDesc);
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }

}
