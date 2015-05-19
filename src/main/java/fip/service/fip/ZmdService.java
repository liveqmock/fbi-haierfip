package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.newcms.controllers.T100109CTL;
import fip.gateway.newcms.controllers.T100110CTL;
import fip.gateway.newcms.domain.T100109.T100109ResponseRecord;
import fip.gateway.newcms.domain.T100110.T100110RequestRecord;
import fip.gateway.sbs.DepCtgManager;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.core.SOFDataDetail;
import fip.gateway.sbs.txn.Ta543.Ta543Handler;
import fip.gateway.sbs.txn.Ta543.Ta543SOFDataDetail;
import fip.gateway.sbs.txn.Taa41.Taa41SOFDataDetail;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.platform.security.OperatorManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 专卖店代扣.
 * 201504
 * User: zhanrui
 * Date: 2011-8-13
 */
@Service
public class ZmdService {
    private static final Logger logger = LoggerFactory.getLogger(ZmdService.class);
    private int uniqKeyLen = 20;

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

    /**
     * 查询信贷系统专卖店的代扣记录（可供选择性获取）    (不包括罚息帐单)
     */
    public List<FipCutpaydetl> doQueryZmdBills(BizType bizType, BillType billType) {

        List<T100109ResponseRecord> recvedList = getZmdResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100109ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    /**
     * 获取全部信贷系统记录  (不包括罚息帐单)
     */
    public synchronized int doObtainZmdBills(BizType bizType, BillType billType, List<String> returnMsgs) {
        List<T100109ResponseRecord> recvedList = getZmdResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100109ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Zmd(cutpaydetl.getClientno(), cutpaydetl.getPaybackdate(), cutpaydetl.getOriginBizid());
            if (isNotRepeated) {
                cutpaydetl.setDateCmsGet(new Date());
                fipCutpaydetlMapper.insert(cutpaydetl);
                count++;
            } else {
                returnMsgs.add("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.error("获取数据时检查出重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }


    /**
     * SBS记账
     */
    public synchronized int accountCutPayRecord2SBS(List<FipCutpaydetl> cutpaydetlList) {
        int count = 0;

        String userid = "9999";
        String username = "crontask";

        OperatorManager operatorManager = SystemService.getOperatorManager();
        if (operatorManager != null) {
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }

        FipJoblog joblog = new FipJoblog();

        try {
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                String sn = cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn();
                //String fromActno = "801090106001041001";
                String fromActno = sbsTxnHelper.selectSbsActnoFromPtEnuDetail("ZMD_FROM_ACTNO"); //对应建行37101985510051003497
                String toActno = cutpaydetl.getClientact();//贷款账号
                String productCode = "N103";
                String remark = "ZMD" + cutpaydetl.getTxpkgSn() + cutpaydetl.getTxpkgDetlSn();
                List<String> paramList = sbsTxnHelper.assembleTaa41Param(sn, fromActno, toActno, cutpaydetl.getPaybackamt(), productCode, remark);

                //SBS
                byte[] recvBuf = DepCtgManager.processSingleResponsePkg("aa41", paramList);
                SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
                Taa41SOFDataDetail sofDataDetail = new Taa41SOFDataDetail();
                response.setSofDataDetail(sofDataDetail);
                response.init(recvBuf);

                String formcode = response.getFormcode();
                if (!formcode.equals("T531")) {     //异常情况处理
                    cutpaydetl.setBillstatus(BillStatus.ACCOUNT_FAILED.getCode());
                    joblog.setJobdesc("SBS入帐失败：FORMCODE=" + formcode);
                } else {
                    if (sofDataDetail.getSECNUM().trim().equals(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn())) {
                        joblog.setJobdesc("SBS入帐完成：FORMCODE=" + formcode + " 帐号:" + cutpaydetl.getClientact());
                        cutpaydetl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                        cutpaydetl.setDateSbsAct(new Date());
                        count++;
                    } else {
                        logger.error("SBS入帐完成,但返回的流水号出错，请查询。" + cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());
                        joblog.setJobdesc("SBS入帐完成,但返回的流水号出错，请查询。");
                        cutpaydetl.setBillstatus(BillStatus.ACCOUNT_PEND.getCode());
                        cutpaydetl.setDateSbsAct(new Date());
                    }
                }

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


    //=================================================================================
    private List<T100109ResponseRecord> getZmdResponseRecords(BizType bizType) {
        T100109CTL ctl = new T100109CTL();

        // 0-未扣款 1-已扣款
        return ctl.start("0");
    }

    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               T100109ResponseRecord responseBean) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        cutpaydetl.setIouno(""); //借据号
        cutpaydetl.setPoano("");  //期次号

        cutpaydetl.setContractno(""); //合同号
        cutpaydetl.setPaybackdate(responseBean.getStdjhhkr()); //计划还款日

        //客户信息
        cutpaydetl.setClientno(responseBean.getStdkhh());
        cutpaydetl.setClientname(responseBean.getStdkhmc());
        cutpaydetl.setClientid(responseBean.getStdzjh()); //身份证号

        //SBS开户信息
        cutpaydetl.setClientact(responseBean.getStddkzh());   //贷款帐号

        //还款金额信息
        cutpaydetl.setPaybackamt(new BigDecimal(responseBean.getStdhkje()));  //还款金额
        cutpaydetl.setPrincipalamt(new BigDecimal("0.00")); //还款本金
        cutpaydetl.setInterestamt(new BigDecimal("0.00"));  //还款利息
        cutpaydetl.setPunitiveintamt(new BigDecimal("0.00"));//罚息金额
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//违约金金额
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//罚息复利金额
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //冗余金额

        //帐单类型
        cutpaydetl.setBilltype("0");

        //还款渠道信息
        if (bizType.equals(BizType.ZMD)) {
            cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //默认为银联
            cutpaydetl.setBiBankactno(responseBean.getStdhkzh());
            cutpaydetl.setBiBankactname(responseBean.getStdkhmc());
            cutpaydetl.setBiActopeningbank(responseBean.getStdyhh());
            cutpaydetl.setRemark3("ZMD 专卖店");
            //cutpaydetl.setBiProvince(responseBean.getStdyhsf());
            //cutpaydetl.setBiCity(xfapprepayment.getCity());
        } else {
            throw new RuntimeException("渠道错误");
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
        cutpaydetl.setDateCmsGet(new Date());

        return cutpaydetl;
    }

    /**
     * 回写信息系统
     */
    public int writebackCutPayRecord2Zmd(List<FipCutpaydetl> cutpaydetlList, boolean isArchive) {
        int count = 0;
        T100110CTL t100110ctl = new T100110CTL();

        for (FipCutpaydetl detl : cutpaydetlList) {
            boolean txResult = false;
            FipCutpaydetl dbRecord = fipCutpaydetlMapper.selectByPrimaryKey(detl.getPkid());
            if (!detl.getRecversion().equals(dbRecord.getRecversion())) {
                throw new RuntimeException("并发错误：版本号不同 " + detl.getClientname() + detl.getPkid());
            }
            T100110RequestRecord record = new T100110RequestRecord();
            record.setStdkhmc(detl.getBiBankactname());
            record.setStdkhh(detl.getClientno());
            record.setStdjhhkr(detl.getPaybackdate());
            record.setStdhkje(detl.getPaybackamt().toString());
            record.setStdrtncode(detl.getTxRetcode());
            record.setStdrtnmsg(detl.getTxRetmsg());

            String billStatus = detl.getBillstatus();
            if (BillStatus.CUTPAY_SUCCESS.getCode().equals(billStatus)) {//注意状态是代扣成功
                record.setStdkkcg("1");
            } else if (BillStatus.CUTPAY_FAILED.getCode().equals(billStatus)) {//注意状态是 银联代扣失败
                record.setStdkkcg("2");
            } else {
                continue;
            }

            //单笔发送处理
            txResult = t100110ctl.start(record);

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
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(detl.getPkid());
            log.setJobname("回写处理");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);

            if (txResult) { //回写成功
                detl.setWritebackflag("1");
                if (isArchive) {
                    detl.setArchiveflag("1");   //回写完成 做存档处理
                }
                detl.setDateCmsPut(new Date());
                log.setJobdesc("回写处理成功");
                count++;
            } else {
                detl.setWritebackflag("0");
                log.setJobdesc("处理失败");
            }
            fipJoblogMapper.insert(log);
            detl.setRecversion(detl.getRecversion() + 1);
            fipCutpaydetlMapper.updateByPrimaryKey(detl);
        }
        return count;
    }


    //按照给定的状态查找未回写的记录
    public List<FipCutpaydetl> selectDetlsByBatRecord(FipCutpaybat cutpaybat, BillStatus status) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andTxpkgSnEqualTo(cutpaybat.getTxpkgSn())
                .andBillstatusEqualTo(status.getCode())
                .andWritebackflagEqualTo("0");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    //仅作存档处理，不考虑recversion
    public void processArchive(List<FipCutpaydetl> cutpaydetlList){
        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            cutpaydetl.setArchiveflag("1");
            fipCutpaydetlMapper.updateByPrimaryKeySelective(cutpaydetl);
        }
    }

    //============================================
    private void batchInsertLogByBatchno(String batchno) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBatchSnEqualTo(batchno);
        List<FipCutpaydetl> fipCutpaydetlList = fipCutpaydetlMapper.selectByExample(example);

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
            log.setJobname("新建记录");
            log.setJobdesc("新获取代扣记录");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }
}
