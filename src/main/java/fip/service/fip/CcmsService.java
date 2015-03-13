package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.ccms.domain.T100101.T100101ResponseRecord;
import fip.gateway.ccms.domain.T100102.T100102Request;
import fip.gateway.ccms.domain.T200101.T200101ResponseRecord;
import fip.gateway.ccms.domain.T200102.T200102Request;
import fip.gateway.ccms.txn.T100101Handler;
import fip.gateway.ccms.txn.T100102Handler;
import fip.gateway.ccms.txn.T200101Handler;
import fip.gateway.ccms.txn.T200102Handler;
import fip.repository.dao.*;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.security.OperatorManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 消费信贷正常还款处理.
 * 20150312 zr 消费金融
 * User: zhanrui
 * Date: 2011-8-13
 */
@Service
public class CcmsService {
    private static final Logger logger = LoggerFactory.getLogger(CcmsService.class);
    private  int uniqKeyLen = 20;

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipRefunddetlMapper fipRefunddetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;

    /**
     * 查询消费信贷系统的代扣记录（可供选择性获取）    (不包括罚息帐单)
     */
    public List<FipCutpaydetl> doQueryCcmsBills(BizType bizType, BillType billType) {

        List<T100101ResponseRecord> recvedList = getCcmsResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    //查询新消费信贷系统的 代付记录
    public List<FipRefunddetl> doQueryCcmsRefundBills(BizType bizType, BillType billType) {

        List<T200101ResponseRecord> recvedList = getCcmsRefundResponseRecords(bizType);
        List<FipRefunddetl> detlList = new ArrayList<FipRefunddetl>();
        int iSeqno = 0;
        for (T200101ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipRefunddetl refunddetl = assembleRefundRecord(bizType, "000000", iSeqno, responseBean);
            if (refunddetl == null) {
                continue;
            }
            refunddetl.setPkid(UUID.randomUUID().toString());
            detlList.add(refunddetl);
        }
        return detlList;
    }

    /**
     * 获取全部信贷系统记录  (不包括罚息帐单)
     *
     * @return
     */
    @Transactional
    public synchronized int doObtainCcmsBills(BizType bizType, BillType billType, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCcmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            //TODO 判断业务主键是否重复   注意 修改IOUNO长度时需要同步修改commonmapper中的SQL
            //boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Ccms(cutpaydetl.getIouno().substring(0, 20), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
            String uniqKey = cutpaydetl.getIouno().substring(0, uniqKeyLen);
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Ccms(uniqKeyLen, uniqKey, cutpaydetl.getPoano(), cutpaydetl.getBilltype(), cutpaydetl.getOriginBizid());
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

    @Transactional
    public synchronized int doMultiObtainCcmsBills(BizType bizType, BillType billType, FipCutpaydetl[] selectedCutpaydetls, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCcmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //判断是否在获取范围内
            for (FipCutpaydetl selectedCutpaydetl : selectedCutpaydetls) {
                if (responseBean.getStdjjh().equals(selectedCutpaydetl.getIouno())
                        && responseBean.getStdqch().equals(selectedCutpaydetl.getPoano())) {
                    iSeqno++;
                    FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);
                    if (cutpaydetl == null) {
                        continue;
                    }
                    //TODO 判断业务主键是否重复   注意 修改IOUNO长度时需要同步修改commonmapper中的SQL
                    //boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Ccms(cutpaydetl.getIouno().substring(0, 20), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
                    String uniqKey = cutpaydetl.getIouno().substring(0, uniqKeyLen);
                    boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Ccms(uniqKeyLen, uniqKey, cutpaydetl.getPoano(), cutpaydetl.getBilltype(), cutpaydetl.getOriginBizid());
                    if (isNotRepeated) {
                        cutpaydetl.setDateCmsGet(new Date());
                        fipCutpaydetlMapper.insert(cutpaydetl);
                        count++;
                    } else {
                        returnMsgs.add("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        logger.info("获取新消费信贷数据时检查出重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    }

                }
            }
        }
        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }

    //代付记录
    @Transactional
    public synchronized int doObtainCcmsRefundBills(BizType bizType, BillType billType) {
        List<T200101ResponseRecord> recvedList = getCcmsRefundResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno4Refund();
        int iSeqno = 0;
        for (T200101ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipRefunddetl refunddetl = assembleRefundRecord(bizType, batchno, iSeqno, responseBean);
            if (refunddetl == null) {
                continue;
            }
            //TODO 判断业务主键是否重复  注意 修改IOUNO长度时需要同步修改commonmapper中的SQL
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4CcmsRefund(refunddetl.getIouno().substring(0, 20), refunddetl.getPoano());
            if (isNotRepeated) {
                refunddetl.setDateInit(new Date());
                fipRefunddetlMapper.insert(refunddetl);
                count++;
            } else {
                logger.error("获取新消费信贷数据时检查出重复记录：" + refunddetl.getIouno() + refunddetl.getClientname());
            }
        }

        //日志
        batchInsertLogByBatchno4Refund(batchno);
        return count;
    }

    //代付记录
    @Transactional
    public synchronized int doMultiObtainCcmsRefundBills(BizType bizType, BillType billType, FipRefunddetl[] selecteddetls) {
        List<T200101ResponseRecord> recvedList = getCcmsRefundResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno4Refund();
        int iSeqno = 0;
        for (T200101ResponseRecord responseBean : recvedList) {
            //判断是否在获取范围内
            for (FipRefunddetl selectedDetl : selecteddetls) {
                if (responseBean.getStdjjh().equals(selectedDetl.getIouno())) {
                    iSeqno++;
                    FipRefunddetl refunddetl = assembleRefundRecord(bizType, batchno, iSeqno, responseBean);
                    if (refunddetl == null) {
                        continue;
                    }
                    //TODO 判断业务主键是否重复     注意 修改IOUNO长度时需要同步修改commonmapper中的SQL
                    boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4CcmsRefund(refunddetl.getIouno().substring(0, 20), refunddetl.getPoano());
                    if (isNotRepeated) {
                        refunddetl.setDateInit(new Date());
                        fipRefunddetlMapper.insert(refunddetl);
                        count++;
                    } else {
                        logger.info("获取新消费信贷数据时检查出重复记录：" + refunddetl.getIouno() + refunddetl.getClientname());
                    }

                }
            }
        }
        //日志
        batchInsertLogByBatchno4Refund(batchno);
        return count;
    }


    //====================
    private List<T100101ResponseRecord> getCcmsResponseRecords(BizType bizType) {
        //获取新信贷数据LIST
        T100101Handler ctl = new T100101Handler();
        ctl.setSERVER_ID(getServerId(bizType));

/*
        String bizFlag = "";
        if (bizType.equals(BizType.FD)) {
            bizFlag = "1";
        } else if (bizType.equals(BizType.XFNEW)) {
            bizFlag = "2";
        }
*/
        //查询 房贷/消费信贷（1/2） 数据
        String bizFlag = "2";
        return ctl.start(bizFlag);
    }

    private String getServerId(BizType bizType) {
        String servId = "";
        if (bizType.equals(BizType.XFNEW)) {
            servId = "CCMS_SERVER_URL";
        }else if (bizType.equals(BizType.XFJR)) {
            servId = "XFJR_SERVER_URL";
        } else {
            throw  new RuntimeException("业务类别错误.");
        }
        return servId;
    }

    private List<T200101ResponseRecord> getCcmsRefundResponseRecords(BizType bizType) {
        //获取数据LIST
        T200101Handler ctl = new T200101Handler();
        ctl.setSERVER_ID(getServerId(bizType));


/*        String bizFlag = "";
        if (bizType.equals(BizType.FD)) {
            bizFlag = "1";
        } else if (bizType.equals(BizType.XFNEW)) {
            bizFlag = "2";
        }*/

        //查询 房贷/消费信贷（1/2） 数据
        String bizFlag = "2";
        return ctl.start(bizFlag);
    }


    /**
     * 不包括 罚息帐单处理 ！！！
     * Billtype: 帐单性质 （“0”：基本帐单， “1”逾期帐单, “2”提前还款帐单, "3"消费信贷首付帐单）
     */

    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               BillType billType,
                                               T100101ResponseRecord responseBean) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        cutpaydetl.setIouno(responseBean.getStdjjh()); //借据号

        String poano = responseBean.getStdqch();
        if (StringUtils.isEmpty(poano)) {
            poano = "0";
        }
        cutpaydetl.setPoano(poano);  //期次号

        cutpaydetl.setContractno(responseBean.getStdhth()); //合同号
        cutpaydetl.setPaybackdate(responseBean.getStdjyrq()); //交易日期
//        cutpaydetl.setPaybackdate(responseBean.getStdjhhkr()); //计划还款日
        cutpaydetl.setPaybackdate(responseBean.getStdjyrq()); //计划还款日 暂使用交易日期

        //客户信息
        cutpaydetl.setClientno(responseBean.getStdkhh());
        cutpaydetl.setClientname(responseBean.getStdkhmc());
        cutpaydetl.setClientid(responseBean.getStdkhsfz()); //身份证号 20130712 zr

        //SBS开户信息
        cutpaydetl.setClientact(responseBean.getStddkzh());   //贷款帐号

        //还款金额信息
        cutpaydetl.setPaybackamt(new BigDecimal(responseBean.getStdhkje()));  //还款金额
        cutpaydetl.setPrincipalamt(new BigDecimal(responseBean.getStdhkbj())); //还款本金
        cutpaydetl.setInterestamt(new BigDecimal(responseBean.getStdhklx()));  //还款利息
        cutpaydetl.setPunitiveintamt(new BigDecimal(responseBean.getStdfxje()));//罚息金额
        //TODO
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//违约金金额
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//罚息复利金额

        cutpaydetl.setReserveamt(new BigDecimal(responseBean.getStdryje()));  //冗余金额

        //帐单类型
        cutpaydetl.setBilltype(billType.getCode());

        //还款渠道信息
        if (bizType.equals(BizType.XFNEW)||bizType.equals(BizType.XFJR)) {
            cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //默认为银联
            cutpaydetl.setBiBankactno(responseBean.getStdhkzh());
            cutpaydetl.setBiBankactname(responseBean.getStdkhmc());

            cutpaydetl.setBiActopeningbank(responseBean.getStdyhh());
            cutpaydetl.setBiProvince(responseBean.getStdyhsf());
//            cutpaydetl.setBiCity(xfapprepayment.getCity());
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

        //zhanrui 20120305  标识消费信贷数据来源自信贷系统 便与回写时区分来源系统
        if (bizType.equals(BizType.XFNEW)) {
            cutpaydetl.setRemark3("XF-CCMS");
        }
        if (bizType.equals(BizType.XFJR)) {
            cutpaydetl.setRemark3("XF-XFJR");
        }

        cutpaydetl.setDateCmsGet(new Date());

        return cutpaydetl;
    }

    /**
     * 不包括 罚息帐单处理 ！！！
     * 区分建行直连渠道版本  暂不用   20120912  zhanrui
     */

    /*
    private FipCutpaydetl assembleCutpayRecordNew4CcbDirect(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               BillType billType,
                                               T100101ResponseRecord responseBean) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        cutpaydetl.setIouno(responseBean.getStdjjh()); //借据号

        String poano = responseBean.getStdqch();
        if (StringUtils.isEmpty(poano)) {
            poano = "0";
        }
        cutpaydetl.setPoano(poano);  //期次号

        cutpaydetl.setContractno(responseBean.getStdhth()); //合同号
        cutpaydetl.setPaybackdate(responseBean.getStdjyrq()); //交易日期
//        cutpaydetl.setPaybackdate(responseBean.getStdjhhkr()); //计划还款日
        cutpaydetl.setPaybackdate(responseBean.getStdjyrq()); //计划还款日 暂使用交易日期

        //客户信息
        cutpaydetl.setClientno(responseBean.getStdkhh());
        cutpaydetl.setClientname(responseBean.getStdkhmc());

        //SBS开户信息
        cutpaydetl.setClientact(responseBean.getStddkzh());   //贷款帐号

        //还款金额信息
        cutpaydetl.setPaybackamt(new BigDecimal(responseBean.getStdhkje()));  //还款金额
        cutpaydetl.setPrincipalamt(new BigDecimal(responseBean.getStdhkbj())); //还款本金
        cutpaydetl.setInterestamt(new BigDecimal(responseBean.getStdhklx()));  //还款利息
        cutpaydetl.setPunitiveintamt(new BigDecimal(responseBean.getStdfxje()));//罚息金额
        //TODO
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//违约金金额
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//罚息复利金额

        cutpaydetl.setReserveamt(new BigDecimal(responseBean.getStdryje()));  //冗余金额

        //帐单类型
        cutpaydetl.setBilltype(billType.getCode());

        //还款渠道信息
        if (bizType.equals(BizType.XFNEW)) {
            cutpaydetl.setBiBankactno(responseBean.getStdhkzh());
            cutpaydetl.setBiBankactname(responseBean.getStdkhmc());

            String bankid = responseBean.getStdyhh();
            cutpaydetl.setBiActopeningbank(bankid);
            cutpaydetl.setBiProvince(responseBean.getStdyhsf());
//            cutpaydetl.setBiCity(xfapprepayment.getCity());
            if ("105".equals(bankid)) {
                cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode());
            }else{
                cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //默认为银联
            }
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

        //zhanrui 20120305  标识消费信贷数据来源自信贷系统 便与回写时区分来源系统
        cutpaydetl.setRemark3("XF-CCMS");

        cutpaydetl.setDateCmsGet(new Date());

        return cutpaydetl;
    }
    */

    //代付记录
    private FipRefunddetl assembleRefundRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               T200101ResponseRecord responseBean) {
        FipRefunddetl refunddetl = new FipRefunddetl();
        refunddetl.setOriginBizid(bizType.getCode());
        refunddetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        refunddetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        refunddetl.setIouno(responseBean.getStdjjh()); //借据号

        String poano = responseBean.getStdqch();
        if (StringUtils.isEmpty(poano)) {
            poano = "0";
        }
        refunddetl.setPoano(poano);  //期次号

        refunddetl.setContractno(responseBean.getStdhth()); //合同号
        //refunddetl.setPaybackdate(responseBean.getStdjyrq()); //交易日期
        //refunddetl.setPaybackdate(responseBean.getStdjyrq()); //计划还款日 暂使用交易日期

        //客户信息
        refunddetl.setClientno(responseBean.getStdkhh());
        refunddetl.setClientname(responseBean.getStdkhmc());

        //SBS开户信息
        //refunddetl.setClientact(responseBean.getStddkzh());   //贷款帐号

        refunddetl.setPayamt(new BigDecimal(responseBean.getStdhkje()));  //还款金额
        //还款金额信息
        //refunddetl.setPaybackamt(new BigDecimal(responseBean.getStdhkje()));  //还款金额
        //refunddetl.setPrincipalamt(new BigDecimal(responseBean.getStdhkbj())); //还款本金
        //refunddetl.setInterestamt(new BigDecimal(responseBean.getStdhklx()));  //还款利息
        //refunddetl.setPunitiveintamt(new BigDecimal(responseBean.getStdfxje()));//罚息金额
        //refunddetl.setBreakamt(new BigDecimal("0.00"));//违约金金额
        //refunddetl.setCompoundintamt(new BigDecimal("0.00"));//罚息复利金额

        //refunddetl.setReserveamt(new BigDecimal(responseBean.getStdryje()));  //冗余金额

        //帐单类型
        //refunddetl.setBilltype(billType.getCode());

        //还款渠道信息
        if (bizType.equals(BizType.XFNEW)||bizType.equals(BizType.XFJR)) {
            refunddetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //默认为银联
            refunddetl.setBiBankactno(responseBean.getStdhkzh());
            refunddetl.setBiBankactname(responseBean.getStdkhmc());

            refunddetl.setBiActopeningbank(responseBean.getStdyhh());
            refunddetl.setBiProvince(responseBean.getStdyhsf());
            //refunddetl.setBiCity(xfapprepayment.getCity());
        } else {
            throw new RuntimeException("渠道错误");
        }


        //其他
        refunddetl.setRecversion((long) 0);
        refunddetl.setDeletedflag("0");
        refunddetl.setArchiveflag("0");
        refunddetl.setWritebackflag("0");
        refunddetl.setAccountflag("0");
        //帐单状态
        refunddetl.setBillstatus(BillStatus.INIT.getCode());
        //refunddetl.setSendflag("0");

        //zhanrui 20120305  标识消费信贷数据来源自信贷系统 便与回写时区分来源系统
        //refunddetl.setRemark3("XF-CCMS");
        refunddetl.setDateInit(new Date());

        return refunddetl;
    }

    //============================================

    //TODO  事务
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
        }else{
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

    //代付记录批量日志
    private void batchInsertLogByBatchno4Refund(String batchno) {
        FipRefunddetlExample example = new FipRefunddetlExample();
        example.createCriteria().andBatchSnEqualTo(batchno);
        List<FipRefunddetl> detlList = fipRefunddetlMapper.selectByExample(example);

        Date date = new Date();
        OperatorManager operatorManager = SystemService.getOperatorManager();
        String userid;
        String username;
        if (operatorManager == null) {
            userid = "9999";
            username = "BATCH";
        }else{
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }
        for (FipRefunddetl detl : detlList) {
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_refunddetl");
            log.setRowpkid(detl.getPkid());
            log.setJobname("新建记录");
            log.setJobdesc("新获取代付记录");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }

    /**
     * 回写信息系统（正常帐单及提前还款帐单）
     */
    //@Transactional 回写时不做整体事务处理
    public int writebackCutPayRecord2CCMS(List<FipCutpaydetl> cutpaydetlList, boolean isArchive, BizType bizType) {
        int count = 0;
        T100102Handler t100102ctl = new T100102Handler();
        t100102ctl.setSERVER_ID(getServerId(bizType));


        for (FipCutpaydetl detl : cutpaydetlList) {
            boolean txResult = false;
            if (detl.getBilltype().equals(BillType.NORMAL.getCode())) { //正常还款
                FipCutpaydetl dbRecord = fipCutpaydetlMapper.selectByPrimaryKey(detl.getPkid());
                if (!detl.getRecversion().equals(dbRecord.getRecversion())) {
                    throw new RuntimeException("并发错误：版本号不同 " + detl.getClientname() + detl.getPkid());
                }
                T100102Request record = new T100102Request();
                record.setStdjjh(detl.getIouno());
                record.setStdjyrq(detl.getPaybackdate());//TODO
                record.setStdjhkkr(detl.getPaybackdate());
                record.setStdcgkkje(detl.getPaybackamt().toString());//TODO bigdecimal
                record.setStddkzh(detl.getClientact());
                record.setStdhth(detl.getContractno());
                record.setStdrtncode(detl.getTxRetcode());
                record.setStdrtnmsg(detl.getTxRetmsg());

                String billStatus = detl.getBillstatus();
                if (BillStatus.CUTPAY_SUCCESS.getCode().equals(billStatus)) {
                    record.setStdkkjg("1");   //银行处理成功
                } else if (BillStatus.CUTPAY_FAILED.getCode().equals(billStatus)) {
                    record.setStdkkjg("2");   //银行处理失败
                } else if (BillStatus.CUTPAY_QRY_PEND.getCode().equals(billStatus)) {
                    record.setStdkkjg("3");   //状态不明
                } else {
                    logger.error("回写记录时出现错误记录。");
                }

                //单笔发送处理
                txResult = t100102ctl.start(record);
            } else {
                logger.error("回写新消费信贷系统出错，帐单类型不支持");
                throw new RuntimeException("回写新消费信贷系统出错，帐单类型不支持");
            }

            Date date = new Date();
            OperatorManager operatorManager = SystemService.getOperatorManager();
            String userid;
            String username;
            if (operatorManager == null) {
                userid = "9999";
                username = "BATCH";
            }else{
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

    public int writebackRefundRecord2CCMS(List<FipRefunddetl> detlList, boolean isArchive, BizType bizType) {
        int count = 0;
        T200102Handler ctl = new T200102Handler();
        ctl.setSERVER_ID(getServerId(bizType));

        for (FipRefunddetl detl : detlList) {
            boolean txResult = false;

            FipRefunddetl dbRecord = fipRefunddetlMapper.selectByPrimaryKey(detl.getPkid());
            if (!detl.getRecversion().equals(dbRecord.getRecversion())) {
                throw new RuntimeException("并发错误：版本号不同" + detl.getPkid());
            }

            T200102Request record = new T200102Request();
            record.setStdjjh(detl.getIouno());
            record.setStdjyrq(new SimpleDateFormat("yyyy-MM-dd").format(detl.getDateBankPay()));//TODO
            record.setStdjhkkr(detl.getStartdate());
            record.setStdcgkkje(detl.getPayamt().toString());
            record.setStddkzh("");
            record.setStdhth(detl.getContractno());
            record.setStdrtncode(detl.getTxRetcode());
            record.setStdrtnmsg(detl.getTxRetmsg());


            //1-成功 2-失败
            //回写失败记录时，要注意此时cutpaydetlList中可能含有信贷系统中的记录
            //if (isSuccess) {
            //    recordT102.setStdkkjg("1");
            //} else {
            //    recordT102.setStdkkjg("2");
            //}

            String billStatus = detl.getBillstatus();
            if (BillStatus.CUTPAY_SUCCESS.getCode().equals(billStatus)) {
                record.setStdkkjg("1");   //银行处理成功
            } else if (BillStatus.CUTPAY_FAILED.getCode().equals(billStatus)) {
                record.setStdkkjg("2");   //银行处理失败
            } else if (BillStatus.CUTPAY_QRY_PEND.getCode().equals(billStatus)) {
                record.setStdkkjg("3");   //状态不明
            } else {
                logger.error("回写记录时出现错误记录。");
            }

            //单笔发送处理
            txResult = ctl.start(record);

            Date date = new Date();
            OperatorManager operatorManager = SystemService.getOperatorManager();
            String userid;
            String username;
            if (operatorManager == null) {
                userid = "9999";
                username = "BATCH";
            }else{
                userid = operatorManager.getOperatorId();
                username = operatorManager.getOperatorName();
            }
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_refunddetl");
            log.setRowpkid(detl.getPkid());
            log.setJobname("回写处理");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);

            if (txResult) {
                detl.setWritebackflag("1");
                if (isArchive) {
                    detl.setArchiveflag("1");   //回写完成 做存档处理
                }
                detl.setDateCmsPut(new Date());
                log.setJobdesc("回写处理成功");
                count++;
            } else {
                detl.setWritebackflag("0");
                log.setJobdesc("回写处理失败");
            }
            fipJoblogMapper.insert(log);
            detl.setRecversion(detl.getRecversion() + 1);
            fipRefunddetlMapper.updateByPrimaryKey(detl);
        }
        return count;
    }
}
