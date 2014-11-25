package fip.service.fip;

import fip.common.constant.BillStatus;
import fip.common.constant.TxSendFlag;
import fip.common.constant.TxpkgStatus;
import fip.gateway.JmsManager;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.fbi.dep.model.base.TIA;
import org.fbi.dep.model.txn.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.advance.utils.PropertyManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 银联交易 通过DEP处理.
 * User: zhanrui
 * Date: 12-3-14
 * Time: 上午7:17
 */
@Service
public class UnipayDepService {
    private static final Logger logger = LoggerFactory.getLogger(UnipayDepService.class);

    private static String DEP_CHANNEL_ID_UNIPAY = "100";
    private static String APP_ID = PropertyManager.getProperty("app_id");
    private static String DEP_USERNAME = PropertyManager.getProperty("jms.username");
    private static String DEP_PWD = PropertyManager.getProperty("jms.password");


    @Resource
    private JmsTemplate jmsSendTemplate;

    @Resource
    private JmsTemplate jmsRecvTemplate;

    @Resource
    private DepService depService;

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    FipCutpaydetlMapper cutpaydetlMapper;

    @Autowired
    FipRefunddetlMapper refunddetlMapper;

    @Autowired
    FipCutpaybatMapper cutpaybatMapper;

    /**
     * 代扣交易 单笔处理  同步和异步
     */
    @Transactional
    public void sendAndRecvT1001001Message(FipCutpaydetl record) {
        try {
            TIA1001001 tia = new TIA1001001();
            initTiaHeader(tia, record.getOriginBizid(), record.getBatchSn(), record.getBatchDetlSn(), "1001001");
            assembleTia1001001Body(tia, record);
            jobLogService.checkAndUpdateRecversion(record);

            //通过MQ发送信息到DEP
            TOA1001001 toa = (TOA1001001) JmsManager.getInstance().sendAndRecv(tia);
            processCutpayToa1001001(record, toa);
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }

    @Transactional
    private void processCutpayToa1001001(FipCutpaydetl record, TOA1001001 toa) {
        String response_sn = toa.header.REQ_SN;
        String request_sn = record.getBatchSn() + record.getBatchDetlSn();
        if (!response_sn.equals(APP_ID + request_sn)) {
            String s = "返回报文序列号" + response_sn + "与请求序列号" + request_sn + "不匹配。";
            logger.error(s);
            throw new RuntimeException(s);
        }
        String rtnCode = toa.header.RETURN_CODE;
        String log = "数据交换平台返回：[" + rtnCode + "] 银联返回结果：" + toa.header.RETURN_MSG + "";

        //截取银联返回码
        String unipayRtnCode = "";
        String unipayRtnMsg = toa.header.RETURN_MSG;
        Pattern p = Pattern.compile("\\[(.*)\\]");
        Matcher m = p.matcher(unipayRtnMsg);
        if (m.find()) {
            unipayRtnCode = m.group(1);
        }

        if ("0000".equals(rtnCode)) {//交易成功
            BigDecimal amt = toa.body.AMOUNT;
            if (toa.body.ACCOUNT_NO.equals(record.getBiBankactno()) && amt.compareTo(record.getPaybackamt()) == 0) {
                record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                record.setDateBankCutpay(new Date());
            } else {
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                log = "记录不匹配(帐号或金额)" + record.getClientname();
                logger.error("记录不匹配(帐号或金额)" + record.getClientname());
                //throw new RuntimeException("记录不匹配(帐号或金额)" + record.getClientname());
            }
        } else if ("1000".equals(rtnCode)) {  //交易失败
            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(unipayRtnMsg);
            log = "银联返回：" + toa.header.RETURN_MSG;
        } else if ("2000".equals(rtnCode)) {  //交易不明
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(unipayRtnMsg);
            log = "银联返回：" + toa.header.RETURN_MSG;
        } else { // TODO 应处理异常情况
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(toa.header.RETURN_MSG);
            log = "银联返回：" + toa.header.RETURN_MSG;
        }
        cutpaydetlMapper.updateByPrimaryKey(record);
        jobLogService.insertNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log, "数据交换平台", "数据交换平台");
    }

    /**
     * 代付交易 单笔处理  同步和异步
     */
    @Transactional
    public void sendAndRecvT1001002Message(FipRefunddetl record) {
        try {
            TIA1001002 tia = new TIA1001002();
            initTiaHeader(tia, record.getOriginBizid(), record.getBatchSn(), record.getBatchDetlSn(), "1001002");
            assembleTia1001002Body(tia, record);
            jobLogService.checkAndUpdateRecversion4Refund(record);

            //通过MQ发送信息到DEP
            TOA1001002 toa = (TOA1001002) JmsManager.getInstance().sendAndRecv(tia);
            processRefundToa1001002(record, toa);
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }

    /**
     * 批量代付交易 单笔处理
     */
    public TOA1002001 sendAndRecvT1002001Message(FipPayoutbat bat, FipPayoutdetl detl) {
        try {
            TIA1002001 tia = new TIA1002001();
            initTiaHeader(tia, bat.getWsysId().toUpperCase(), detl.getReqSn(), detl.getSn(), "1002001");
            assembleTia1002001Body(tia, bat, detl);
//          TODO log  jobLogService.checkAndUpdateRecversion4Refund(record);
            //通过MQ发送信息到DEP
            TOA1002001 toa = (TOA1002001) JmsManager.getInstance().sendAndRecv(tia);
            String response_sn = toa.header.REQ_SN;
            String request_sn = APP_ID + detl.getReqSn() + detl.getSn();
            if (!response_sn.equals(request_sn)) {
                String s = "返回报文序列号" + response_sn + "与请求序列号" + request_sn + "不匹配。";
                logger.error(s);
                throw new RuntimeException(s);
            }
            return toa;
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }

    public TOA1003001 sendAndRecvPayoutT1003001Message(FipPayoutbat bat, FipPayoutdetl detl) {
        try {
            TIA1003001 tia = new TIA1003001();
            initTiaHeader(tia, bat.getWsysId().toUpperCase(), detl.getReqSn(), detl.getSn(), "1003001");
            assemblePayoutTia1003001Body(tia, detl);
            tia.getHeader().REQ_SN = APP_ID + new SimpleDateFormat("yyyyMMddHHmmssss").format(new Date());
//          todo log  jobLogService.checkAndUpdateRecversion(record);
            //通过MQ发送信息到DEP
            TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia);
            return toa;
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }

    @Transactional
    private void processRefundToa1001002(FipRefunddetl record, TOA1001002 toa) {
        String response_sn = toa.header.REQ_SN;
        String request_sn = record.getBatchSn() + record.getBatchDetlSn();
        if (!response_sn.equals(APP_ID + request_sn)) {
            String s = "返回报文序列号" + response_sn + "与请求序列号" + request_sn + "不匹配。";
            logger.error(s);
            throw new RuntimeException(s);
        }
        String rtnCode = toa.header.RETURN_CODE;
        String log = "数据交换平台返回：[" + rtnCode + "] 银联返回结果：" + toa.header.RETURN_MSG + "";
        if ("0000".equals(rtnCode)) {//交易成功
            BigDecimal amt = toa.body.AMOUNT;
            if (toa.body.ACCOUNT_NO.equals(record.getBiBankactno()) && amt.compareTo(record.getPayamt()) == 0) {
                record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                record.setDateBankPay(new Date());
            } else {
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                log = "记录不匹配(帐号或金额)" + record.getClientname();
                logger.error("记录不匹配(帐号或金额)" + record.getClientname());
                //throw new RuntimeException("记录不匹配(帐号或金额)" + record.getClientname());
            }
        } else if ("1000".equals(rtnCode)) {  //交易失败
            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            log = "银联返回：" + toa.header.RETURN_MSG;
        } else if ("2000".equals(rtnCode)) {  //交易不明
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            log = "银联返回：" + toa.header.RETURN_MSG;
        } else { // TODO 应处理异常情况
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            log = "银联返回：" + toa.header.RETURN_MSG;
        }
        refunddetlMapper.updateByPrimaryKey(record);
        jobLogService.insertNewJoblog(record.getPkid(), "fip_refunddetl", "银联返回", log, "数据交换平台", "数据交换平台");
    }

    /**
     * 结果查询交易 1003001
     */
    @Transactional
    public void sendAndRecvCutpayT1003001Message(FipCutpaydetl record) {
        try {
            TIA1003001 tia = new TIA1003001();
            initTiaHeader(tia, record.getOriginBizid(), record.getBatchSn(), record.getBatchDetlSn(), "1003001");
            assembleCutpayTia1003001Body(tia, record);
            jobLogService.checkAndUpdateRecversion(record);

            //通过MQ发送信息到DEP
            //TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia);
            TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia, 15000); //自定义超时时间 注意：可能造成mq队列中遗留大量消息
            processCutpayToa1003001(record, toa);
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }

    @Transactional
    public void sendAndRecvRefundT1003001Message(FipRefunddetl record) {
        try {
            TIA1003001 tia = new TIA1003001();
            initTiaHeader(tia, record.getOriginBizid(), record.getBatchSn(), record.getBatchDetlSn(), "1003001");
            assembleRefundTia1003001Body(tia, record);
            jobLogService.checkAndUpdateRecversion4Refund(record);
            //通过MQ发送信息到DEP
            TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia);
            processRefundToa1003001(record, toa);
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }


    @Transactional
    private void processCutpayToa1003001(FipCutpaydetl record, TOA1003001 toa) {
        String response_sn = toa.header.REQ_SN;
        String request_sn = record.getBatchSn() + record.getBatchDetlSn();
        if (!response_sn.equals(APP_ID + request_sn)) {
            String s = "返回报文序列号" + response_sn + "与请求序列号" + request_sn + "不匹配。";
            logger.error(s);
            throw new RuntimeException(s);
        }
        String rtnCode = toa.header.RETURN_CODE;
        String log = "数据交换平台返回：[" + rtnCode + "] 银联返回结果：" + toa.header.RETURN_MSG + "";

        //截取银联返回码
        String unipayRtnCode = "";
        String unipayRtnMsg = toa.header.RETURN_MSG;
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(unipayRtnMsg);
        if (m.find()) {
            unipayRtnCode = m.group(1);
        }

        if ("0000".equals(rtnCode)) {//交易成功
            BigDecimal amt = toa.body.AMOUNT;
            if (toa.body.ACCOUNT_NO.equals(record.getBiBankactno()) && amt.compareTo(record.getPaybackamt()) == 0) {
                record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                record.setDateBankCutpay(new Date());
                record.setTxRetcode("0000");
            } else {
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                log = "记录不匹配(帐号或金额)" + record.getClientname();
                logger.error("记录不匹配(帐号或金额)" + record.getClientname());
                //throw new RuntimeException("记录不匹配(帐号或金额)" + record.getClientname());
            }
        } else if ("1000".equals(rtnCode)) {  //交易失败
            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(unipayRtnMsg);
            log = "银联返回：" + unipayRtnMsg;
        } else if ("2000".equals(rtnCode)) {  //交易不明
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(unipayRtnMsg);
            log = "银联返回：" + unipayRtnMsg;
        } else { // TODO 应处理异常情况
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(toa.header.RETURN_MSG);
            log = "银联返回：" + unipayRtnMsg;
        }
        cutpaydetlMapper.updateByPrimaryKey(record);
        jobLogService.insertNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log, "数据交换平台", "数据交换平台");
    }

    @Transactional
    private void processRefundToa1003001(FipRefunddetl record, TOA1003001 toa) {
        String response_sn = toa.header.REQ_SN;
        String request_sn = record.getBatchSn() + record.getBatchDetlSn();
        if (!response_sn.equals(APP_ID + request_sn)) {
            String s = "返回报文序列号" + response_sn + "与请求序列号" + request_sn + "不匹配。";
            logger.error(s);
            throw new RuntimeException(s);
        }
        String rtnCode = toa.header.RETURN_CODE;
        String log = "数据交换平台返回：[" + rtnCode + "] 银联返回结果：" + toa.header.RETURN_MSG + "";
        if ("0000".equals(rtnCode)) {//交易成功
            BigDecimal amt = toa.body.AMOUNT;
            if (toa.body.ACCOUNT_NO.equals(record.getBiBankactno()) && amt.compareTo(record.getPayamt()) == 0) {
                record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                record.setDateBankPay(new Date());
            } else {
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                log = "记录不匹配(帐号或金额)" + record.getClientname();
                logger.error("记录不匹配(帐号或金额)" + record.getClientname());
            }
        } else if ("1000".equals(rtnCode)) {  //交易失败
            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            log = "银联返回：[" + toa.header.RETURN_MSG + "]";
        } else if ("2000".equals(rtnCode)) {  //交易不明
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            log = "银联返回：[" + toa.header.RETURN_MSG + "]";
        } else { // TODO 应处理异常情况
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            log = "银联返回：[" + toa.header.RETURN_MSG + "]";
        }
        refunddetlMapper.updateByPrimaryKey(record);
        jobLogService.insertNewJoblog(record.getPkid(), "fip_refunddetl", "银联返回", log, "数据交换平台", "数据交换平台");
    }

    /*
    private void initTiaHeader(TIA tia, FipCutpaydetl cutpaydetl, String txnCode){
        String app_id = APP_ID;
        tia.getHeader().APP_ID = app_id;
        tia.getHeader().BIZ_ID = cutpaydetl.getOriginBizid();
        tia.getHeader().CHANNEL_ID = "100";
        tia.getHeader().USER_ID = DEP_USERNAME;
        tia.getHeader().PASSWORD = DEP_PWD;
        tia.getHeader().REQ_SN = app_id + cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn();
        tia.getHeader().TX_CODE = txnCode;
    }
    */
    private void initTiaHeader(TIA tia, String bizId, String batchSn, String batchDetlSn, String txnCode) {
        String app_id = APP_ID;
        tia.getHeader().APP_ID = app_id;
        tia.getHeader().BIZ_ID = bizId;
        tia.getHeader().CHANNEL_ID = "100";
        tia.getHeader().USER_ID = DEP_USERNAME;
        tia.getHeader().PASSWORD = DEP_PWD;
        tia.getHeader().REQ_SN = app_id + batchSn + batchDetlSn;
        tia.getHeader().TX_CODE = txnCode;
    }

    private void assembleTia1001001Body(TIA1001001 tia, FipCutpaydetl record) {
        tia.body.ACCOUNT_NO = record.getBiBankactno();
        tia.body.ACCOUNT_NAME = record.getBiBankactname();
        tia.body.ACCOUNT_PROP = "0"; // 个人
        tia.body.ACCOUNT_TYPE = "00"; //00银行卡，01存折。不填默认为银行卡00。
        tia.body.AMOUNT = record.getPaybackamt().toString();
        tia.body.BANK_CODE = record.getBiActopeningbank();
        tia.body.CITY = record.getBiCity();
        tia.body.PROVINCE = record.getBiProvince();

        //------
        //20121023 zhanrui 身份证号暂时放在备注中，用于DEP中的报文处理
        //tia.body.REMARK = "HAIERFIP";
        tia.body.REMARK = record.getClientid();
        //-------
    }

    private void assembleTia1001002Body(TIA1001002 tia, FipRefunddetl record) {
        tia.body.ACCOUNT_NO = record.getBiBankactno();
        tia.body.ACCOUNT_NAME = record.getBiBankactname();
        tia.body.ACCOUNT_PROP = "0"; // 个人
        tia.body.ACCOUNT_TYPE = "00"; //00银行卡，01存折。不填默认为银行卡00。
        tia.body.AMOUNT = record.getPayamt().toString();
        tia.body.BANK_CODE = record.getBiActopeningbank();
        tia.body.CITY = record.getBiCity();
        tia.body.PROVINCE = record.getBiProvince();

        //------
        //20121023 zhanrui 身份证号暂时放在备注中，用于DEP中的报文处理
        //tia.body.REMARK = "HAIERFIP";
        tia.body.REMARK = record.getClientid();
        //-------
    }

    private void assembleTia1002001Body(TIA1002001 tia, FipPayoutbat bat, FipPayoutdetl detl) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        tia.body.TRANS_SUM = new TIA1002001.Body.BodyHeader();
        tia.body.TRANS_SUM.TOTAL_ITEM = "1";
        tia.body.TRANS_SUM.TOTAL_SUM = df.format(new BigDecimal(detl.getAmount()).divide(new BigDecimal("100.0")));
        TIA1002001.Body.BodyDetail bodyDetail = new TIA1002001.Body.BodyDetail();
        bodyDetail.SN = "0001";
        bodyDetail.ACCOUNT_NO = detl.getAccountNo();
        bodyDetail.ACCOUNT_NAME = detl.getAccountName();
        bodyDetail.ACCOUNT_PROP = detl.getAccountProp(); // 个人
        bodyDetail.ACCOUNT_TYPE = detl.getAccountType(); //00银行卡，01存折。不填默认为银行卡00。
        bodyDetail.AMOUNT = tia.body.TRANS_SUM.TOTAL_SUM;
        bodyDetail.BANK_CODE = detl.getBankCode();
        bodyDetail.CITY = detl.getCity();
        bodyDetail.PROVINCE = detl.getProvince();
        bodyDetail.REMARK = detl.getSbsAccountNo() + detl.getRemark();
        bodyDetail.RESERVE1 = detl.getReserve1();
        tia.body.TRANS_DETAILS.add(bodyDetail);
    }

    private void assembleCutpayTia1003001Body(TIA1003001 tia, FipCutpaydetl record) {
        tia.body.QUERY_SN = APP_ID + record.getBatchSn() + record.getBatchDetlSn();
        tia.body.REMARK = "HAIERFIP";
    }

    private void assembleRefundTia1003001Body(TIA1003001 tia, FipRefunddetl record) {
        tia.body.QUERY_SN = APP_ID + record.getBatchSn() + record.getBatchDetlSn();
        tia.body.REMARK = "HAIERFIP";
    }

    private void assemblePayoutTia1003001Body(TIA1003001 tia, FipPayoutdetl detl) {
        tia.body.QUERY_SN = APP_ID + detl.getReqSn() + detl.getSn();
        tia.body.REMARK = "HAIERFIP";
    }


    //=======================================================================================
    //20140808  批量代扣交易 1001003   zhanrui
    public synchronized String sendAndRecvT1001003Message(FipCutpaybat batchRecord) {
        String txPkgSn = batchRecord.getTxpkgSn();
        List<FipCutpaydetl> detailRecords = billManagerService.checkToMakeSendableRecords(txPkgSn);

        TIA1001003 tia = new TIA1001003();

        //报文头
        tia.getHeader().APP_ID = APP_ID;
        tia.getHeader().BIZ_ID = batchRecord.getChannelBizid();
        tia.getHeader().CHANNEL_ID = "100";
        tia.getHeader().USER_ID = DEP_USERNAME;
        tia.getHeader().PASSWORD = DEP_PWD;
        tia.getHeader().REQ_SN = batchRecord.getOriginBizid() + "-B-" + batchRecord.getTxpkgSn();
        tia.getHeader().TX_CODE = "1001003";

        //报文体
        BigDecimal totalAmt = new BigDecimal(0);
        for (FipCutpaydetl detailRecord : detailRecords) {
            TIA1001003.Body.BodyDetail item = new TIA1001003.Body.BodyDetail();
            item.SN = detailRecord.getTxpkgDetlSn();
            item.BANK_CODE = detailRecord.getBiActopeningbank();
            item.ACCOUNT_TYPE = ""; //账号类型 银行卡或存折
            item.ACCOUNT_NO = detailRecord.getBiBankactno();
            item.ACCOUNT_NAME = detailRecord.getBiBankactname();
            item.PROVINCE = detailRecord.getBiProvince();
            item.CITY = detailRecord.getBiCity();
            item.ACCOUNT_PROP = "0"; // 个人
            item.AMOUNT = String.valueOf(detailRecord.getPaybackamt());
            item.ID_TYPE = detailRecord.getClientidtype();
            item.ID = detailRecord.getClientid();

            tia.body.TRANS_DETAILS.add(item);
            totalAmt = totalAmt.add(detailRecord.getPaybackamt());
        }
        tia.body.TRANS_SUM.TOTAL_ITEM = String.valueOf(detailRecords.size());
        tia.body.TRANS_SUM.TOTAL_SUM = String.valueOf(totalAmt);

        //处理batch记录的状态  设置为“待进行结果查询”
        FipCutpaybat originBatRecord = cutpaybatMapper.selectByPrimaryKey(batchRecord.getTxpkgSn());
        if (originBatRecord.getRecversion().compareTo(batchRecord.getRecversion()) != 0) {
            throw new RuntimeException("并发更新冲突,批量序号=" + batchRecord.getTxpkgSn());
        } else {
            batchRecord.setRecversion(batchRecord.getRecversion() + 1);
            batchRecord.setTxpkgStatus(TxpkgStatus.QRY_PEND.getCode());
            cutpaybatMapper.updateByPrimaryKey(batchRecord);
        }

        TOA1001003 toa;
        try {
            //通过MQ发送信息到DEP
            toa = (TOA1001003) JmsManager.getInstance().sendAndRecv(tia);
        } catch (Exception e) {
            throw new RuntimeException("MQ处理失败", e);
        }

        billManagerService.updateCutpaydetlListToSendflag(detailRecords, TxSendFlag.SENT.getCode());
        billManagerService.updateCutpaybatToSendflag(txPkgSn, TxSendFlag.SENT.getCode());
        return processCutpayToa1001003(batchRecord, detailRecords, toa);
    }

    private String  processCutpayToa1001003(FipCutpaybat batchRecord, List<FipCutpaydetl> detailRecords, TOA1001003 toa) {
        //String batchSN = toa.header.REQ_SN;
        String batchSN = batchRecord.getTxpkgSn();
        if (!toa.header.REQ_SN.endsWith(batchSN)) {
            throw new RuntimeException("乱包：响应报文与请求报文序列号不一致!");
        }

        String headRetCode = toa.header.RETURN_CODE;
        String headRetMsg = toa.header.RETURN_MSG;
        jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "银联返回", "[" + headRetCode + "]" + headRetMsg, "数据交换平台", "数据交换平台");
        if (headRetCode.equals("0000")) {  //处理完成
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        } else if (headRetCode.startsWith("1")) {   //整包失败  '1'开头表示请求报文有错误或者银联系统解释报文出错
            //置批量表中的记录为 “处理失败”
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_FAIL);
            //重置对应的明细记录的标志为待发送
            //resetDetailRecordStatusByBatchQueryResult(batchSN, headRetCode, headRetMsg);
        } else { //整包状态不明， 待继续查询
            //不做处理， 应继续查询。
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        }
        logger.debug(" ..........处理返回的消息结束........");
        return headRetCode;

        //TODO  处理明细记录的 银联相应信息
    }

    private void setCutpaybatRecordStatus(String headSn, TxpkgStatus status) {
        FipCutpaybat fipCutpaybat = cutpaybatMapper.selectByPrimaryKey(headSn);
        fipCutpaybat.setTxpkgStatus(status.getCode());
        fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
        cutpaybatMapper.updateByPrimaryKey(fipCutpaybat);
    }


    /**
     * 批量结果查询交易 1003003
     */
    public synchronized String sendAndRecvCutpayT1003003Message(FipCutpaybat batchRecord) {
        TOA1003003 toa;
        try {
            TIA1003003 tia = new TIA1003003();
            //报文头
            tia.getHeader().APP_ID = APP_ID;
            tia.getHeader().BIZ_ID = batchRecord.getChannelBizid();
            tia.getHeader().CHANNEL_ID = "100";
            tia.getHeader().USER_ID = DEP_USERNAME;
            tia.getHeader().PASSWORD = DEP_PWD;
            tia.getHeader().REQ_SN = batchRecord.getOriginBizid() + "-B-" + batchRecord.getTxpkgSn();
            tia.getHeader().TX_CODE = "1003003";

            //报文体
            tia.body.QUERY_SN = batchRecord.getOriginBizid() + "-B-" + batchRecord.getTxpkgSn();
            tia.body.REMARK = "";

            // /TODO ？
            // jobLogService.checkAndUpdateRecversion(batchRecord);

            //通过MQ发送信息到DEP
            //TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia);
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "银联交易结果查询", "发起结果查询（DEP:1003003）请求", "数据交换平台", "数据交换平台");
            toa = (TOA1003003) JmsManager.getInstance().sendAndRecv(tia, 15000);
        } catch (Exception e) {
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "银联交易结果查询", "发起请求，MQ处理失败, 请查看日志", "数据交换平台", "数据交换平台");
            throw new RuntimeException("MQ消息处理失败" + e.getMessage(), e);
        }

        try {
            return processCutpayToa1003003(batchRecord, toa);
        } catch (Exception e) {
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "银联交易结果查询", "批量结果查询T1003003响应报文处理失败", "数据交换平台", "数据交换平台");
            throw new RuntimeException("批量结果查询T1003003响应报文处理失败" + e.getMessage(), e);
        }
    }


    private String processCutpayToa1003003(FipCutpaybat batchRecord, TOA1003003 toa) {
        //检查响应报文是否与请求报文相对应
        String reqSn = batchRecord.getOriginBizid() + "-B-" + batchRecord.getTxpkgSn();

        if (!reqSn.equals(toa.getHeader().REQ_SN)) {
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "银联交易结果查询", "响应报文乱包，响应报文与请求报文不对应！" , "数据交换平台", "数据交换平台");
            throw new RuntimeException("乱包：响应报文与请求报文不对应！");
        }

        //检查查询SN的一致性
        String querySn = reqSn;
        if (!querySn.equals(toa.body.QUERY_SN)) {
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "银联交易结果查询", "响应报文中的查询序列号错误！" , "数据交换平台", "数据交换平台");
            throw new RuntimeException("响应报文中的查询序列号错误！");
        }

        //处理TOA头部返回码
        String headRtnCode = toa.header.RETURN_CODE;
        jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "银联交易结果查询", "响应报文：[" +  headRtnCode + "]" + toa.header.RETURN_MSG, "数据交换平台", "数据交换平台");

        //先处理报文体中的明细报文
        processTOA1003003Body(batchRecord, toa);

        //再处理报文头对应的 cutpaybat表状态
        if ("0000".equals(headRtnCode)) {
            setCutpaybatRecordStatus(batchRecord.getTxpkgSn(), TxpkgStatus.DEAL_SUCCESS);
        } else if (headRtnCode.startsWith("1")) { //失败，可以重发或解包重发
            /*
            1000	报文内容检查错或者处理错（具体内容见返回错误信息）
            1001	报文解释错
            1002	无法查询到该交易，可以重发
             */
            setCutpaybatRecordStatus(batchRecord.getTxpkgSn(), TxpkgStatus.DEAL_FAIL);
        } else if (headRtnCode.startsWith("2")) { //处理中
            //
        } else {
           //
        }

        return headRtnCode;
    }

    private void processTOA1003003Body(FipCutpaybat batchRecord, TOA1003003 toa) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        for (TOA1003003.Body.BodyDetail bodyDetail : toa.body.RET_DETAILS) {
            String detailSn = bodyDetail.SN;

            example.clear();
            example.createCriteria().andTxpkgSnEqualTo(batchRecord.getTxpkgSn()).andTxpkgDetlSnEqualTo(detailSn);
            List<FipCutpaydetl> cutpaydetlList = cutpaydetlMapper.selectByExample(example);
            FipCutpaydetl record = cutpaydetlList.get(0);

            String retCode = bodyDetail.RET_CODE;
            String retMsg = bodyDetail.ERR_MSG;

            //检查是否已经查询过
            if (!StringUtils.isEmpty(record.getTxRetcode())) {
                if (record.getTxRetcode().equals(retCode)) {
                    continue; //略过
                }
            }

            record.setTxRetcode(retCode);
            record.setTxRetmsg(retMsg);

            String txRetMsg = "[" + bodyDetail.ERR_MSG + "]";
            record.setTxRetmsg(txRetMsg);
            record.setTxRetcode(retCode);

            if ("0000".equals(retCode)) { //业务成功
                if (bodyDetail.ACCOUNT_NO.endsWith(record.getBiBankactno())
                        && bodyDetail.AMOUNT.compareTo(record.getPaybackamt()) == 0) {
                    record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    record.setDateBankCutpay(new Date());
                } else {
                    record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                    txRetMsg = "记录不匹配(帐号或金额)" + record.getClientname();
                    record.setTxRetcode("XXXX");
                    record.setTxRetmsg(txRetMsg);
                    logger.error("记录不匹配(帐号或金额)" + record.getClientname());
                }
            } else if (retCode.startsWith("1")) { //1开头表示请求报文有错误或者我司系统解释报文出错（商户系统需要明确处理该）
                record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            } else if (retCode.startsWith("2")) { //2开头表示处于中间处理状态
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            } else {
                record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            }

            record.setRecversion(record.getRecversion() + 1);
            String log = "[批量包号+序号:" + record.getTxpkgSn() + "_" + record.getTxpkgDetlSn() + "]" + record.getTxRetmsg();
            //appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log);
            jobLogService.insertNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log, "数据交换平台", "数据交换平台");

            cutpaydetlMapper.updateByPrimaryKey(record);
        }
    }

}
