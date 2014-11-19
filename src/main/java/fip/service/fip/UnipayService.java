package fip.service.fip;

import fip.common.constant.BillStatus;
import fip.common.constant.TxSendFlag;
import fip.common.constant.TxpkgStatus;
import fip.gateway.unionpay.CreateMessageHandler;
import fip.gateway.unionpay.RtnCodeEnum;
import fip.gateway.unionpay.domain.*;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipCutpaydetlExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * 代扣采用直连银联方式
   201401 zr 已废弃 现在采用DEP通道方式
 * User: zhanrui
 * Date: 11-8-24
 * Time: 上午7:17
 */
@Service
@Deprecated
public class UnipayService {
    private static final Logger logger = LoggerFactory.getLogger(UnipayService.class);

    private static String DEP_CHANNEL_ID_UNIPAY = "100";

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
    FipCutpaybatMapper cutpaybatMapper;


    /**
     * 发送单笔代扣请求报文 (10004 实时代扣报文)
     * 发送前检查版本号
     * 为防止重复发送  在正式发送前直接置状态为 待查询
     *
     * @param record
     */

    @Transactional
    @Deprecated
    public void sendAndRecvRealTimeTxnMessage(FipCutpaydetl record) {
        try {
            String msgtxt = CreateMessageHandler.getInstance().create100004Msg(record);

            FipCutpaydetl originRecord = cutpaydetlMapper.selectByPrimaryKey(record.getPkid());
            if (!originRecord.getRecversion().equals(record.getRecversion())) {
                throw new RuntimeException("并发更新冲突,UUID=" + record.getPkid());
            } else {
                record.setRecversion(record.getRecversion() + 1);
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                cutpaydetlMapper.updateByPrimaryKey(record);
            }
            //通过MQ发送信息到DEP
            String msgid = depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, record.getOriginBizid());
            handle100004Message(depService.recvDepMessage(msgid));
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }

    /**
     * 处理银联100004实时扣款交易结果
     * 注意：银联100004交易本身的报文格式支持批量处理，但目前（20110901）此报文只做单笔发送处理
     * 单笔处理的流水号 与 fip_cutpaybat表无关 发送请求时的流水号由两部分组成：BATCH_SN + BATCH_DETL_SN
     *
     * @param message
     */
    @Deprecated
    @Transactional
    public void handle100004Message(String message) {
        logger.info(" ========开始处理返回的100004消息==========");
        logger.info(message);

        T100004Toa toa = T100004Toa.getToa(message);
        if (toa != null) {
            String retcode_head = toa.INFO.RET_CODE;      //报文头返回码
            String req_sn = toa.INFO.REQ_SN;              //交易流水号
            String batch_sn = req_sn.substring(0, 11);    //解析交易流水号 得到批次号
            String batch_detl_sn = req_sn.substring(11);  //解析交易流水号 得到批次内的顺序号
            FipCutpaydetlExample example = new FipCutpaydetlExample();
            example.createCriteria().andBatchSnEqualTo(batch_sn).andBatchDetlSnEqualTo(batch_detl_sn)
                    .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0");
            List<FipCutpaydetl> cutpaydetlList = cutpaydetlMapper.selectByExample(example);
            if (cutpaydetlList.size() != 1) {
                logger.error("未查找到对应的扣款记录。" + req_sn);
                throw new RuntimeException("未查找到对应的扣款记录。" + req_sn);
            }
            FipCutpaydetl record = cutpaydetlList.get(0);

            if ("0000".equals(retcode_head)) { //报文头“0000”：处理完成
                //已查找到数据库中对应的记录，可以进行日志记录
                T100004Toa.Body.BodyDetail bodyDetail = toa.BODY.RET_DETAILS.get(0);
                String retcode_detl = bodyDetail.RET_CODE;
                if ("0000".equals(retcode_detl)) { //交易成功的唯一标志
                    if (bodyDetail.ACCOUNT_NO.equals(record.getBiBankactno())) {
                        long recordAmt = record.getPaybackamt().multiply(new BigDecimal(100)).longValue();
                        long returnAmt = Integer.parseInt(bodyDetail.AMOUNT);
                        if (recordAmt == returnAmt) {
                            record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                            record.setDateBankCutpay(new Date());
                        } else {
                            logger.error("返回金额不匹配");
                            appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", "返回金额不匹配:" + returnAmt);
                        }
                    } else {
                        logger.error("帐号不匹配");
                        appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", "帐号不匹配" + bodyDetail.ACCOUNT_NO);
                    }
                } else {  //交易失败
                    record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                }
                record.setTxRetcode(String.valueOf(retcode_detl));
                record.setTxRetmsg(bodyDetail.ERR_MSG);
            } else if ("1002".equals(retcode_head)) {//无法查询到该交易，可以重发  关键！
                record.setBillstatus(BillStatus.RESEND_PEND.getCode());
                record.setTxRetcode(String.valueOf(retcode_head));
                record.setTxRetmsg(toa.INFO.ERR_MSG);
            } else { //待查询 (TODO: 未处理 0001，0002)
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                record.setTxRetcode(String.valueOf(retcode_head));
                record.setTxRetmsg(toa.INFO.ERR_MSG);
            }
            record.setRecversion(record.getRecversion() + 1);
            cutpaydetlMapper.updateByPrimaryKey(record);
        } else { //
            throw new RuntimeException("该笔交易记录为空，可能已被删除。 " + message);
        }
        logger.debug(" ................. 处理返回的消息结束........");
    }

    /**
     * 发送批量代扣请求报文  100001 以及 100004
     * 发送前检查版本号
     * 为防止重复发送  在正式发送前直接置状态为 待查询
     *
     * @param batchRecord 1
     */

    @Transactional
    @Deprecated
    public void sendAndRecvBatchTxnMessage(FipCutpaybat batchRecord) {
        try {
            String txPkgSn = batchRecord.getTxpkgSn();
            List<FipCutpaydetl> sendRecords = billManagerService.checkToMakeSendableRecords(txPkgSn);

            Map paramMap = new HashMap();
            paramMap.put("batBean", batchRecord);
            paramMap.put("detlList", sendRecords);
            String msgtxt;
            if ("SYNC".equals(batchRecord.getTxntype())) { //同步报文 走实时交易接口
                //msgtxt = CreateMessageHandler.getInstance().create100004Msg(batchRecord, sendRecords);
                TIA100004 tia100004 = new TIA100004(paramMap);
                msgtxt = tia100004.toXml(batchRecord.getOriginBizid());
            } else if ("ASYNC".equals(batchRecord.getTxntype())) {
                TIA100001 tia100001 = new TIA100001(paramMap);
                msgtxt = tia100001.toXml(batchRecord.getOriginBizid());
                //msgtxt = CreateMessageHandler.getInstance().create100001Msg(batchRecord, sendRecords);
            } else {
                throw new RuntimeException("批量报文 TXNTYPE 设置错误。");
            }

            FipCutpaybat originBatRecord = cutpaybatMapper.selectByPrimaryKey(batchRecord.getTxpkgSn());
            if (originBatRecord.getRecversion().compareTo(batchRecord.getRecversion())!=0) {
                throw new RuntimeException("并发更新冲突,批量序号=" + batchRecord.getTxpkgSn());
            } else {
                batchRecord.setRecversion(batchRecord.getRecversion() + 1);
                batchRecord.setTxpkgStatus(TxpkgStatus.QRY_PEND.getCode());
                cutpaybatMapper.updateByPrimaryKey(batchRecord);
            }
            //通过MQ发送信息
            String msgid = depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, batchRecord.getOriginBizid());
            billManagerService.updateCutpaydetlListToSendflag(sendRecords, TxSendFlag.SENT.getCode());
            billManagerService.updateCutpaybatToSendflag(txPkgSn, TxSendFlag.SENT.getCode());

            if ("SYNC".equals(batchRecord.getTxntype())) { //同步报文 走实时交易接口
                //handle100004Message(depService.recvDepMessage(msgid));
                TOA100004 toa100004 = TOA100004.getToa(depService.recvDepMessage(msgid));
                handleTOA100004(toa100004);
            } else {
                TOA100001 toa100001 = TOA100001.getToa(depService.recvDepMessage(msgid));
                handleTOA100001(toa100001);
                //handle100001Message(depService.recvDepMessage(msgid));
            }
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }

    /**
     * 处理批量扣款的实时返回报文 无明细记录的结果需要处理
     *
     * @param message
     */
    @Transactional
    @Deprecated
    public void handle100001Message(String message) {
        logger.debug(" ................. 开始处理返回的100001消息........");
        logger.info(message);
        T100001Toa toa = T100001Toa.getToa(message);
        if (toa != null) {
            String msgRetCode = toa.INFO.RET_CODE;
            boolean isReset = false;
            if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100001_FAILE.getValues()).contains(msgRetCode)) {
                // 解包 重置
                isReset = true;
            }
            boolean isBatchOver = true;
            String txpkgSn = toa.INFO.REQ_SN;
            if (toa.BODY.RET_DETAILS != null && !toa.BODY.RET_DETAILS.isEmpty()) {
                for (T100001Toa.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
                    FipCutpaydetlExample example = new FipCutpaydetlExample();
                    example.createCriteria().andTxpkgSnEqualTo(txpkgSn).andTxpkgDetlSnEqualTo(detail.SN);
                    FipCutpaydetl record = cutpaydetlMapper.selectByExample(example).get(0);
                    String log = null;
                    if (isReset) {
                        record.setTxpkgSn("");
                        record.setTxpkgDetlSn("");
                        record.setSendflag(TxSendFlag.UNSEND.getCode());
                        record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                        record.setTxRetmsg("[系统失败，解包重置]");
                        record.setTxRetcode("");
                    } else {
                        String retCode = detail.RET_CODE;
                        // 交易失败
                        if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100001_FAILE.getValues()).contains(retCode)) {
                            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                            record.setTxRetmsg("[报文头信息]:" + msgRetCode + toa.INFO.ERR_MSG + " [报文体信息]:" + detail.ERR_MSG);
                            record.setTxRetcode(String.valueOf(retCode));
                        } else {
                            isBatchOver = false;
                            // 其他情况视为交易结果不明待查询
                            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                            record.setTxRetmsg("[发送成功，交易结果不明，待查询。]");
                            record.setTxRetcode(String.valueOf(retCode));
                        }
                    }
                    record.setRecversion(record.getRecversion() + 1);
                    log = "处理批量交易(100001)结果: [批量包号+序号:" + record.getTxpkgSn() + record.getBatchDetlSn() + "]" + record.getTxRetmsg();
                    appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log);
                    cutpaydetlMapper.updateByPrimaryKey(record);
                }
            }
            if (isBatchOver) {
                setCutpaybatRecordStatus(txpkgSn, TxpkgStatus.DEAL_SUCCESS);
            }
            logger.debug(" ................. 处理返回的消息结束........");
        } else {
            throw new RuntimeException("xml数据转换为对象时转换错误");
        }
    }


    @Deprecated
    public void sendAndRecvRealTimeDatagramQueryMessage(FipCutpaydetl record) {
        try {
            String msgtxt = CreateMessageHandler.getInstance().create200001Msg(record);
            String msgid = depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, record.getOriginBizid());
            //handle200001Message(depService.recvDepMessage(msgid));
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }


    //============================================================================================

    /**
     * 发送代扣查询报文 for 批量
     *
     * @param batchRecord FipCutpaybat
     */

    public void sendAndRecvBatchDatagramQueryMessage(FipCutpaybat batchRecord) {
        try {
            Map paramMap = new HashMap();
            paramMap.put("reqSN", batchRecord.getTxpkgSn());

            TIA200001 tia200001 = new TIA200001(paramMap);
            String msgtxt = tia200001.toXml(batchRecord.getOriginBizid());

            String msgid = depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, batchRecord.getOriginBizid());
            TOA200001 toa200001 = TOA200001.getToa(depService.recvDepMessage(msgid));
            handleTOA200001(toa200001);
        } catch (Exception e) {
            logger.error("MQ消息处理失败", e);
            throw new RuntimeException("MQ消息处理失败", e);
        }
    }


    @Transactional
    public void handleTOA100001(TOA100001 toa) {
        String batchSN = toa.INFO.REQ_SN;
        String headRetCode = toa.INFO.RET_CODE;
        String errMsg = toa.INFO.ERR_MSG;
        appendNewJoblog(batchSN, "fip_cutpaybat", "银联返回", "[返回码:" + headRetCode + "][信息:]" + errMsg);
        if (headRetCode.equals("0000")) {  //处理完成
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        } else if (headRetCode.startsWith("1")) {   //整包失败  '1'开头表示请求报文有错误或者银联系统解释报文出错
            //置批量表中的记录为 “处理失败”
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_FAIL);
            //重置对应的明细记录的标志为待发送
            //resetDetailRecordStatusByBatchQueryResult(batchSN, headRetCode, errMsg);
        } else { //整包状态不明， 待继续查询  (TODO: 未处理 0001，0002)
            //不做处理， 应继续查询。
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        }
        logger.debug(" ..........处理返回的消息结束........");
    }
    @Transactional
    public void handleTOA100004(TOA100004 toa) {
        String batchSN = toa.INFO.REQ_SN;
        String headRetCode = toa.INFO.RET_CODE;
        String errMsg = toa.INFO.ERR_MSG;
        appendNewJoblog(batchSN, "fip_cutpaybat", "银联返回", "[返回码:" + headRetCode + "][信息:]" + errMsg);
        if (headRetCode.equals("0000")) {  //处理完成
            processTOA100004DetailRecordOfBatchPkg(toa);
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_SUCCESS);
        } else if (headRetCode.startsWith("1")) {   //整包失败  '1'开头表示请求报文有错误或者银联系统解释报文出错
            //置批量表中的记录为 “处理失败”
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_FAIL);
            //重置对应的明细记录的标志为待发送
            //resetDetailRecordStatusByBatchQueryResult(batchSN, headRetCode, errMsg);
        } else { //整包状态不明， 待继续查询  (TODO: 未处理 0001，0002)
            //不做处理， 应继续查询。
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        }
        logger.debug(" ..........处理返回的消息结束........");
    }

    @Transactional
    public void handleTOA200001(TOA200001 toa) {
        String batchSN = toa.BODY.QUERY_TRANS.QUERY_SN;
        String headRetCode = toa.INFO.RET_CODE;
        String errMsg = toa.INFO.ERR_MSG;
        appendNewJoblog(batchSN, "fip_cutpaybat", "银联返回", "[返回码:" + headRetCode + "][信息:]" + errMsg);
        if (headRetCode.equals("0000")) {  //处理完成
            processTOA200001DetailRecordOfBatchPkg(toa);
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_SUCCESS);
        } else if (headRetCode.startsWith("1")) {   //整包失败  '1'开头表示请求报文有错误或者银联系统解释报文出错
            //置批量表中的记录为 “处理完成”
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_FAIL);
            //重置对应的明细记录的标志为待发送
            //resetDetailRecordStatusByBatchQueryResult(batchSN, headRetCode, errMsg);
        } else { //整包状态不明， 待继续查询  (TODO: 未处理 0001，0002)
            //不做处理， 应继续查询。
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        }
        logger.debug(" ..........处理返回的消息结束........");
    }

    /**
     * 根据查询报文的返回结果 循环处理明细记录
     *
     * @param toa
     */
    @Transactional
    private void processTOA100004DetailRecordOfBatchPkg(TOA100004 toa) {
        String batchSN = toa.INFO.REQ_SN;
        FipCutpaydetlExample example = new FipCutpaydetlExample();

        BigDecimal amt100 = new BigDecimal(100);
        for (TOA100004.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
            String detailSn = detail.SN;
            example.clear();
            example.createCriteria().andTxpkgSnEqualTo(batchSN).andTxpkgDetlSnEqualTo(detailSn);

            List<FipCutpaydetl> cutpaydetlList = cutpaydetlMapper.selectByExample(example);
            FipCutpaydetl record = cutpaydetlList.get(0);

            String retCode = detail.RET_CODE;
            //业务成功
            if ("0000".equals(retCode)) {
                BigDecimal amt = new BigDecimal(detail.AMOUNT);
                amt = amt.divide(amt100);
                if (detail.ACCOUNT_NO.endsWith(record.getBiBankactno()) && amt.equals(record.getPaybackamt())) {
                    record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    record.setDateBankCutpay(new Date());
                } else {
                    logger.error("记录不匹配(帐号或金额)" + record.getClientname());
                    throw new RuntimeException("记录不匹配(帐号或金额)" + record.getClientname());
                }
            } else {
                record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            }
            record.setTxRetmsg("[返回码:" + retCode + "][返回信息]:" + detail.ERR_MSG);
            record.setTxRetcode(String.valueOf(retCode));
            record.setRecversion(record.getRecversion() + 1);
            String log = "处理批量交易(100004)结果: [批量包号+序号:" + record.getTxpkgSn() + record.getTxpkgDetlSn() + "]" + record.getTxRetmsg();
            appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log);

            cutpaydetlMapper.updateByPrimaryKey(record);
        }
    }
    @Transactional
    private void processTOA200001DetailRecordOfBatchPkg(TOA200001 toa) {
        String batchSN = toa.BODY.QUERY_TRANS.QUERY_SN;
        FipCutpaydetlExample example = new FipCutpaydetlExample();

        BigDecimal amt100 = new BigDecimal(100);
        for (TOA200001.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
            String detailSn = detail.SN;
            example.clear();
            example.createCriteria().andTxpkgSnEqualTo(batchSN).andTxpkgDetlSnEqualTo(detailSn);

            List<FipCutpaydetl> cutpaydetlList = cutpaydetlMapper.selectByExample(example);
            FipCutpaydetl record = cutpaydetlList.get(0);

            String retCode = detail.RET_CODE;
            //业务成功
            if ("0000".equals(retCode)) {
                BigDecimal amt = new BigDecimal(detail.AMOUNT);
                amt = amt.divide(amt100);
                if (detail.ACCOUNT.endsWith(record.getBiBankactno()) && amt.equals(record.getPaybackamt())) {
                    record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    record.setDateBankCutpay(new Date());
                } else {
                    logger.error("记录不匹配(帐号或金额)" + record.getClientname());
                    throw new RuntimeException("记录不匹配(帐号或金额)" + record.getClientname());
                }
            } else {
                record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            }
            record.setTxRetmsg("[返回码:" + retCode + "][返回信息]:" + detail.ERR_MSG);
            record.setTxRetcode(String.valueOf(retCode));
            record.setRecversion(record.getRecversion() + 1);
            String log = "处理批量交易(200001)结果: [批量包号+序号:" + record.getTxpkgSn() + record.getTxpkgDetlSn() + "]" + record.getTxRetmsg();
            appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log);

            cutpaydetlMapper.updateByPrimaryKey(record);
        }
    }

    /**
     * 解包处理
     */
    private void resetDetailRecordStatusByBatchQueryResult(String batchSN, String headRetCode, String errMsg) {
        //String headRetCode = toa.getHeader().RET_CODE;
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andTxpkgSnEqualTo(batchSN).andArchiveflagEqualTo("0").andDeletedflagEqualTo("0");
        List<FipCutpaydetl> detlList = cutpaydetlMapper.selectByExample(example);
        for (FipCutpaydetl fipCutpaydetl : detlList) {
            fipCutpaydetl.setBillstatus(BillStatus.INIT.getCode());
            fipCutpaydetl.setRecversion(fipCutpaydetl.getRecversion() + 1);
            cutpaydetlMapper.updateByPrimaryKey(fipCutpaydetl);
            String log = "处理交易结果: [整包返回码:" + headRetCode + "][信息:]" + errMsg;
            appendNewJoblog(fipCutpaydetl.getPkid(), "fip_cutpaydetl", "银联返回", log);
        }
    }

    /**
     * 设置批量包状态。
     *
     * @param headSn
     */
    @Transactional
    private void setCutpaybatRecordStatus(String headSn, TxpkgStatus status) {
        FipCutpaybat fipCutpaybat = cutpaybatMapper.selectByPrimaryKey(headSn);
        fipCutpaybat.setTxpkgStatus(status.getCode());
        fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
        cutpaybatMapper.updateByPrimaryKey(fipCutpaybat);
    }


    //===============================================================================
    private void appendNewJoblog(String pkid, String tableName, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, tableName, jobname, jobdesc, "数据交换平台", "数据交换平台");
    }

}
