package fip.gateway.unionpay;

import fip.common.constant.TxpkgStatus;
import fip.common.constant.BillStatus;
import fip.common.constant.TxSendFlag;
import fip.gateway.unionpay.domain.T100001Toa;
import fip.gateway.unionpay.domain.T100004Toa;
import fip.gateway.unionpay.domain.T200001Toa;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.model.*;
import fip.service.fip.BillManagerService;
import fip.service.fip.JobLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
@Deprecated
public class RtnMessageHandler {

    private static final Logger logger = LoggerFactory.getLogger(RtnMessageHandler.class);

    @Autowired
    FipCutpaydetlMapper mapper;

    @Autowired
    FipCutpaybatMapper batMapper;

    @Autowired
    BillManagerService billManagerService;

    @Autowired
    private JobLogService jobLogService;

    /**
     * 处理银联100004实时扣款交易结果
     * 注意：银联100004交易本身的报文格式支持批量处理，但目前（20110901）此报文只做单笔发送处理
     * 单笔处理的流水号 与 fip_cutpaybat表无关 发送请求时的流水号由两部分组成：BATCH_SN + BATCH_DETL_SN
     *
     * @param message
     */
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
            List<FipCutpaydetl> cutpaydetlList = mapper.selectByExample(example);
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
            } else { //待查询
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                record.setTxRetcode(String.valueOf(retcode_head));
                record.setTxRetmsg(toa.INFO.ERR_MSG);
            }
            record.setRecversion(record.getRecversion() + 1);
            mapper.updateByPrimaryKey(record);
        } else { //
            throw new RuntimeException("该笔交易记录为空，可能已被删除。 " + message);
        }
        logger.debug(" ................. 处理返回的消息结束........");
    }

    @Deprecated
    public void handle100004Message_bak(String message) {
        logger.info(" ................. 开始处理返回的100004消息........");
        logger.info(message);

        T100004Toa toa = T100004Toa.getToa(message);
        if (toa != null) {
            String retCode = toa.INFO.RET_CODE;
            FipCutpaydetl record = null;

            FipCutpaydetlExample example = new FipCutpaydetlExample();

            String query_sn = toa.INFO.REQ_SN;
            String batchno = query_sn.substring(0, 11);
            String seqno = query_sn.substring(11, 18);

            example.createCriteria().andBatchSnEqualTo(batchno).andBatchDetlSnEqualTo(seqno);
            List<FipCutpaydetl> recordList = mapper.selectByExample(example);

            if (!recordList.isEmpty()) {
                record = recordList.get(0);
                if (record != null) {
                    //====================== 处理Dep反馈码-=============
                    if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100004_SUCCESS.getValues()).contains(retCode)) {
                        record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100004_FAILE.getValues()).contains(retCode)) {
                        record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                    } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100004_WAIT.getValues()).contains(retCode)) {
                        record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                    } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100004_AGAIN.getValues()).contains(retCode)) {
                        //record.setBillstatus(XFBillStatus.SEND_FAILED.getCode());
                        record.setSendflag(TxSendFlag.UNSEND.getCode());
                    } else {
                        // 其他情况视为发送成功，待查询
                        record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                    }
                    record.setTxRetcode(String.valueOf(retCode));
                    record.setTxRetmsg(toa.INFO.ERR_MSG);
                    record.setRecversion(record.getRecversion() + 1);
                }
            } else {
                logger.error("未找到对应的交易流水号:" + query_sn);
                //TODO
                throw new RuntimeException("未找到对应的交易流水号:" + query_sn);
            }

            record.setTxRetmsg("[报文头信息]:" + toa.INFO.ERR_MSG);

            if (toa.BODY != null) {
                if (toa.BODY.RET_DETAILS != null && toa.BODY.RET_DETAILS.size() == 1) {
                    T100004Toa.Body.BodyDetail bodyDetail = toa.BODY.RET_DETAILS.get(0);
                    if (record != null) {
                        if (bodyDetail.ACCOUNT_NO.equals(record.getBiBankactno())) {
                            record.setTxRetmsg(record.getTxRetmsg() + " [报文体信息]:" + bodyDetail.ERR_MSG);
                        }
                    }
                }
            }

            String log = "处理交易(100004)结果:" + record.getTxRetmsg();
            appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log);

            mapper.updateByPrimaryKey(record);

            logger.debug(" ................. 处理返回的消息结束........");
        } else {
            throw new RuntimeException("该笔交易记录为空，可能已被删除。交易流水号: " + toa.INFO.REQ_SN);
        }
    }

    public void handle200001BatchMessage(String message) {
        logger.debug("................. 开始处理返回的200001批量查询的消息........");
        logger.info(message);

        T200001Toa toa = T200001Toa.getToa(message);
        boolean isReset = false;
        boolean isBatchTxOver = true;
        if (toa != null) {
            String msgRetCode = toa.INFO.RET_CODE;
            //  处理完成
            String txpkgSn = toa.INFO.REQ_SN;
            appendNewJoblog(txpkgSn, "fip_cutpaybat", "银联返回", "[返回码:" + msgRetCode + "][信息:]" + toa.INFO.ERR_MSG);
            if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_200001_WAIT.getValues()).contains(msgRetCode)) {
                isBatchTxOver = false;
            } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_200001_AGAIN.getValues()).contains(msgRetCode)) {
                isBatchTxOver = false;
                billManagerService.updateCutpaybatToSendflag(txpkgSn, TxSendFlag.UNSEND.getCode());
            } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_200001_FAILE.getValues()).contains(msgRetCode)) {
                isReset = true;  // 解包
            } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_200001_SUCCESS.getValues()).contains(msgRetCode)) {
                String query_sn = toa.BODY.QUERY_TRANS.QUERY_SN;
                FipCutpaydetlExample example = new FipCutpaydetlExample();
                for (T200001Toa.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
                    example.clear();
                    String sn = detail.SN;
                    if ("0001".equals(sn)) {  //单包返回
                        query_sn = query_sn.substring(0, 11);
                        example.createCriteria().andBatchSnEqualTo(query_sn);
                    } else {
                        example.createCriteria().andTxpkgSnEqualTo(query_sn).andTxpkgDetlSnEqualTo(sn);
                    }
                    List<FipCutpaydetl> cutpaydetlList = mapper.selectByExample(example);
                    FipCutpaydetl record = cutpaydetlList.get(0);
                    String retCode = detail.RET_CODE;
                    if (isReset) {
                        record.setTxpkgSn("");
                        record.setTxpkgDetlSn("");
                        record.setSendflag(TxSendFlag.UNSEND.getCode());
                        record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                        record.setTxRetmsg("[系统失败，解包重置]");
                        record.setTxRetcode("");
                    }
                    //  业务成功，
                    //String[] values_100001 = RtnCodeEnum.UNIONPAY_TRX_CODE_100001_SUCCESS.getValues();
                    //if (Arrays.asList(values_100001).contains(retCode)) {
                    if ("0000".equals(retCode)) {
                        if (detail.ACCOUNT.endsWith(record.getBiBankactno())) {
                            //TODO 检查金额
                            record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                            record.setDateBankCutpay(new Date());
                        } else {
                            logger.error("记录不匹配");
                            throw new RuntimeException("记录不匹配" + record.getClientname());
                        }
                    } else {
                        record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                    }

                    record.setTxRetmsg("[返回码:" + retCode + "][返回信息]:" + detail.ERR_MSG);
                    record.setTxRetcode(String.valueOf(retCode));
                    record.setRecversion(record.getRecversion() + 1);
                    String log = "处理批量交易(200001)结果: [批量包号+序号:" + record.getTxpkgSn() + record.getTxpkgDetlSn() + "]" + record.getTxRetmsg();
                    appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log);

                    mapper.updateByPrimaryKey(record);
                }
            }
            if (isBatchTxOver) {
                FipCutpaybat fipCutpaybat = batMapper.selectByPrimaryKey(txpkgSn);
                fipCutpaybat.setTxpkgStatus(TxpkgStatus.DEAL_SUCCESS.getCode());
                fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
                batMapper.updateByPrimaryKey(fipCutpaybat);
            }
            logger.debug(" ................. 处理返回的消息结束........");
        } else {
            logger.error("xml数据转换为对象时转换错误");
            throw new RuntimeException("xml数据转换为对象时转换错误");
        }

    }

    public synchronized void handle200001BatchMessage_bak(String message) {
        logger.debug(" ................. 开始处理返回的200001批量查询的消息........");
        logger.info(message);
        T200001Toa toa = T200001Toa.getToa(message);
        boolean isReset = false;
        boolean isBatchTxOver = true;
        if (toa != null) {
            String msgRetCode = toa.INFO.RET_CODE;
            //  处理完成
            String txpkgSn = toa.INFO.REQ_SN;
            if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_200001_WAIT.getValues()).contains(msgRetCode)) {
                isBatchTxOver = false;
                appendNewJoblog(txpkgSn, "fip_cutpaybat", "银联返回", "结果不明，需再次查询。");
            } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_200001_AGAIN.getValues()).contains(msgRetCode)) {
                isBatchTxOver = false;
                billManagerService.updateCutpaybatToSendflag(txpkgSn, TxSendFlag.UNSEND.getCode());
            } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_200001_FAILE.getValues()).contains(msgRetCode)) {
                isReset = true;  // 解包
            } else if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_200001_SUCCESS.getValues()).contains(msgRetCode)) {
                for (T200001Toa.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
                    FipCutpaydetlExample example = new FipCutpaydetlExample();
                    example.createCriteria().andTxpkgSnEqualTo(txpkgSn).andTxpkgDetlSnEqualTo(detail.SN);
                    FipCutpaydetl record = mapper.selectByExample(example).get(0);
                    String retCode = detail.RET_CODE;
                    if (isReset) {
                        record.setTxpkgSn("");
                        record.setTxpkgDetlSn("");
                        record.setSendflag(TxSendFlag.UNSEND.getCode());
                        record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                        record.setTxRetmsg("[系统失败，解包重置]");
                        record.setTxRetcode("");
                    }
                    //  业务成功，
                    if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100001_SUCCESS.getValues()).contains(retCode)) {
                        record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    } else {
                        record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                    }

                    record.setTxRetmsg("[报文头信息]:" + msgRetCode + toa.INFO.ERR_MSG + " [报文体信息]:" + detail.ERR_MSG);
                    record.setTxRetcode(String.valueOf(retCode));
                    record.setRecversion(record.getRecversion() + 1);
                    String log = "处理批量交易(200001)结果: [批量包号+序号:" + record.getTxpkgSn() + record.getBatchDetlSn() + "]" + record.getTxRetmsg();
                    appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "银联返回", log);

                    mapper.updateByPrimaryKey(record);
                }
            }
            if (isBatchTxOver) {
                FipCutpaybat fipCutpaybat = batMapper.selectByPrimaryKey(txpkgSn);
                fipCutpaybat.setTxpkgStatus(TxpkgStatus.DEAL_SUCCESS.getCode());
                fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
                batMapper.updateByPrimaryKey(fipCutpaybat);
            }
            logger.debug(" ................. 处理返回的消息结束........");
        } else {
            throw new RuntimeException("xml数据转换为对象时转换错误");
        }

    }

    public synchronized void handle100001Message(String message) {
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
                    FipCutpaydetl record = mapper.selectByExample(example).get(0);

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

                    mapper.updateByPrimaryKey(record);
                }
            }
            if (isBatchOver) {
                FipCutpaybat fipCutpaybat = batMapper.selectByPrimaryKey(txpkgSn);
                fipCutpaybat.setTxpkgStatus(TxpkgStatus.DEAL_SUCCESS.getCode());
                fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
                batMapper.updateByPrimaryKey(fipCutpaybat);
            }
            logger.debug(" ................. 处理返回的消息结束........");
        } else {
            throw new RuntimeException("xml数据转换为对象时转换错误");
        }
    }


    //===============================================================================
    private void appendNewJoblog(String pkid, String tableName, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, tableName, jobname, jobdesc, "数据交换平台", "数据交换平台");
    }

}