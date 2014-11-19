package fip.service.fip;

import fip.common.constant.*;
import fip.gateway.sbs.DepCtgManager;
import fip.repository.model.FipPayoutbat;
import fip.repository.model.FipPayoutdetl;
import org.apache.commons.lang.StringUtils;
import org.fbi.dep.model.txn.TOA1002001;
import org.fbi.dep.model.txn.TOA1003001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* sbs-n057 -> unionpay 1002001 -> sbs-n058/n059
 */
@Service
@Deprecated

public class PayoutTxnService {
    private static final Logger logger = LoggerFactory.getLogger(PayoutTxnService.class);

    @Autowired
    private PayoutbatService payoutbatService;
    @Autowired
    private PayoutDetlService payoutDetlService;
    @Autowired
    private UnipayDepService unipayDepService;
    @Autowired
    private JobLogService jobLogService;

    // 【ROUND-1】：sbs代付 N057
    public int processN057(List<FipPayoutbat> payoutbatList) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        int failCnt = 0;
        int sucCnt = 0;
        for (FipPayoutbat bat : payoutbatList) {
            List<FipPayoutdetl> detlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.INIT);
            if (payoutbatService.isNoTxnStepClash(bat)) {
                for (FipPayoutdetl detl : detlList) {
                    // sbs n057 参数
                    List<String> paramlist = assembleTn057Param(detl);
                    // sbs交易,途径dep

                    byte[] sbsResBytes = DepCtgManager.processSingleResponsePkg("n057", paramlist);
                    logger.info(new String(sbsResBytes));

                    String formcode = new String(sbsResBytes, 21, 4);
                    String rtnWsysSn = new String(sbsResBytes, 72, 18).trim();
                    jobLogService.insertNewJoblog(detl.getPkid(), "fip_payoutdetl", detl.getReqSn() + detl.getSn() + "SBSN057", "SBS返回码:" + formcode, "Haierfip", "资金交换平台");

                    if (!"T531".equals(formcode)) {
                        // 交易结束，失败
                        detl.setRetCode(PayoutDetlRtnCode.FAIL.getCode());
                        detl.setErrMsg(PayoutDetlRtnCode.FAIL.getTitle());
                        failCnt++;
                        String errmsg = "[SBS返回错误信息: " + formcode + " " + getSBSErrMsgFromResponse(sbsResBytes) + " ]";
                        logger.error("SBS通讯或报文解析错误。" + errmsg);
                    } else
                    // 判断流水号是否一致
                    if (!rtnWsysSn.equals(detl.getReqSn() + detl.getSn())) {
                        String errmsg = "[错误信息: 流水号不一致," + "]";
                        throw new RuntimeException("SBS报文解析错误。" + errmsg);
                    }
                    sucCnt++;
                    detl.setSbsTxnTime(sdf);
                    payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.SBSN057);
                }
                // 所有代付 n057 均失败，则该笔代付均失败，无需发往银联
                if ((failCnt != 0) && (failCnt == detlList.size())) {
                    bat.setRetCode(PayoutBatRtnCode.TXN_OVER.getCode());
                    bat.setErrMsg(PayoutBatRtnCode.TXN_OVER.getTitle());
                }
                // 状态更新
                bat.setSbsTxnTime(sdf);
                bat.setRemark(PayoutDetlTxnStep.SBSN057.toRtnMsg());
                payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.SBSN057);
            } else {
                throw new RuntimeException("sbs-n057并发交易冲突。交易流水号：" + bat.getReqSn());
            }
        }
        return sucCnt;
    }

    // 【ROUND-2】：通过dep发往银联代付
    public int processUnionpayPayout(List<FipPayoutbat> payoutbatList) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        int sucCnt = 0;
        for (FipPayoutbat bat : payoutbatList) {
            List<FipPayoutdetl> detlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.SBSN057);
            int failCnt = 0;
            if (payoutbatService.isNoTxnStepClash(bat)) {
                for (FipPayoutdetl detl : detlList) {

                    TOA1002001 toa = unipayDepService.sendAndRecvT1002001Message(bat, detl);
                    jobLogService.insertNewJoblog(detl.getPkid(), "fip_payoutdetl", detl.getReqSn() + detl.getSn() + "[Dep1002001]",
                            "银联返回:" + toa.header.RETURN_CODE + toa.header.RETURN_MSG, "Haierfip", "资金交换平台");

                    if (!DepUnipayTxnStatus.TXN_SUCCESS.getCode().equals(toa.header.RETURN_CODE)) {
                        // 交易结束，失败，须执行 N059
                        failCnt++;
                        String errmsg = "[返回错误信息: " + toa.header.RETURN_CODE + toa.header.RETURN_MSG + "]";
                        detl.setUnionpayTxnTime(sdf);
                        payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.UNIONPAY_PAYOUT_FAIL);
                        logger.error("银联返回错误。" + errmsg);
                    } else {
                        detl.setUnionpayTxnTime(sdf);
                        sucCnt += payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.SENT_UNIONPAY);
                    }
                }
                // 所有代付均失败，则该笔代付均失败，bat失败
                if ((failCnt != 0) && (failCnt == detlList.size())) {
                    // 状态更新
                    bat.setUnionpayTxnTime(sdf);
                    bat.setRemark(PayoutBatTxnStep.UNIONPAY_TXN_OVER.toRtnMsg());
                    payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.UNIONPAY_TXN_OVER);
                } else {
                    // 状态更新
                    bat.setUnionpayTxnTime(sdf);
//                bat.setUnionpayErrMsg();
                    bat.setRemark(PayoutDetlTxnStep.SENT_UNIONPAY.toRtnMsg());
                    payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.UNIONPAY_TXN_PAYOUT);
                }
            } else {
                throw new RuntimeException("发送银联代付并发交易冲突。交易流水号：" + bat.getReqSn());
            }
        }
        return sucCnt;
    }

    // 【ROUND-3】：通过dep发往银联查询
    @Transactional
    public void processUnionpayQry(List<FipPayoutbat> payoutbatList) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        for (FipPayoutbat bat : payoutbatList) {
            List<FipPayoutdetl> detlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.SENT_UNIONPAY);
            int unknownCnt = 0;
            if (payoutbatService.isNoTxnStepClash(bat)) {
                for (FipPayoutdetl detl : detlList) {

                    TOA1003001 toa = unipayDepService.sendAndRecvPayoutT1003001Message(bat, detl);
                    jobLogService.insertNewJoblog(detl.getPkid(), "fip_payoutdetl", detl.getReqSn() + detl.getSn() + "[Dep1003001]",
                            "银联返回:" + toa.header.RETURN_CODE + toa.header.RETURN_MSG, "Haierfip", "资金交换平台");
                    detl.setQryrtnTime(sdf);
                    // 如果处理成功
                    if (DepUnipayTxnStatus.TXN_SUCCESS.getCode().equals(toa.header.RETURN_CODE)) {
                        detl.setQryrtnAccountNo(toa.body.ACCOUNT_NO);
                        detl.setQryrtnAccountName(toa.body.ACCOUNT_NAME);
                        detl.setQryrtnAmount(toa.body.AMOUNT.toString());
                        detl.setQryrtnRetCode(toa.header.RETURN_CODE);
                        detl.setQryrtnErrMsg(toa.header.RETURN_MSG);
                        detl.setQryrtnRemark(toa.body.REMARK);
                        payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.UNIONPAY_PAYOUT_SUCCESS);
                    } else if (DepUnipayTxnStatus.TXN_FAILED.getCode().equals(toa.header.RETURN_CODE)) { // 失败
                        detl.setQryrtnAccountNo(toa.body.ACCOUNT_NO);
                        detl.setQryrtnAccountName(toa.body.ACCOUNT_NAME);
                        detl.setQryrtnAmount(toa.body.AMOUNT.toString());
                        detl.setQryrtnRetCode(toa.header.RETURN_CODE);
                        detl.setQryrtnErrMsg(toa.header.RETURN_MSG);
                        detl.setQryrtnRemark(toa.body.REMARK);
                        String errmsg = "[返回错误信息: " + toa.header.RETURN_CODE + toa.header.RETURN_MSG + "]";
                        logger.error("银联返回错误。" + errmsg);
                        payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.UNIONPAY_PAYOUT_FAIL);

                    } else if (DepUnipayTxnStatus.TXN_QRY_PEND.getCode().equals(toa.header.RETURN_CODE)) { // 不明
                        // 结果不明，不做业务处理，须再次查询
                        unknownCnt++;
                    }
                }

                // 没有不明结果的交易
                if (unknownCnt == 0) {
                    bat.setRemark(PayoutBatTxnStep.UNIONPAY_TXN_OVER.toRtnMsg());
                    payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.UNIONPAY_TXN_OVER);
                } else {
                    // 须再次查询
                }
            } else {
                throw new RuntimeException("查询银联代付并发交易冲突。交易流水号：" + bat.getReqSn());
            }
        }
    }

    // 【ROUND-4】：通过dep发起n058或n059交易
    public int processSbsPayoutConfirm(List<FipPayoutbat> payoutbatList) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        int sucCnt = 0;
        for (FipPayoutbat bat : payoutbatList) {
            List<FipPayoutdetl> sucDetlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.UNIONPAY_PAYOUT_SUCCESS);
            int cnt = 0;
            for (FipPayoutdetl record : sucDetlList) {
                // n058
                cnt += processN058(record);
                sucCnt++;
            }
            List<FipPayoutdetl> failDetlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.UNIONPAY_PAYOUT_FAIL);
            for (FipPayoutdetl record : failDetlList) {
                // n059
                cnt += processN059(record);
            }
            // 确认bat全部交易结束
            if (cnt != 0 && cnt == (sucDetlList.size() + failDetlList.size())) {
                bat.setRetCode(PayoutBatRtnCode.TXN_OVER.getCode());
                bat.setErrMsg(PayoutBatRtnCode.TXN_OVER.getTitle());
                bat.setRemark(PayoutBatTxnStep.ALL_TXN_OVER.toRtnMsg());
                bat.setSbsTxnTime(sdf);
                payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.ALL_TXN_OVER);
            }
        }
        return sucCnt;
    }

    @Transactional
    private int processN058(FipPayoutdetl record) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        if (payoutDetlService.isNoTxnStepClash(record)) {
            // sbs n058 参数
            List<String> paramlist = assembleTn058Param(record);
            // sbs交易,途径dep
            byte[] sbsResBytes = DepCtgManager.processSingleResponsePkg("n058", paramlist);
            logger.info(new String(sbsResBytes));

            String formcode = new String(sbsResBytes, 21, 4);
            String rtnWsysSn = new String(sbsResBytes, 72, 18).trim();
            jobLogService.insertNewJoblog(record.getPkid(), "fip_payoutdetl", record.getReqSn() + record.getSn() + "SBSN058",
                    "SBS返回:" + formcode, "Haierfip", "资金交换平台");

            if (!"T531".equals(formcode)) {
                // 交易失败，除记录日志外，不做任何处理，须再次发起n058，完成入账确认
                String errmsg = "[SBS返回错误信息: " + formcode + " " + getSBSErrMsgFromResponse(sbsResBytes) + " ]";
                logger.error("SBS通讯或报文解析错误。" + errmsg);
                return 0;
            } else {
                // 判断流水号是否一致
                if (!rtnWsysSn.equals(record.getReqSn() + record.getSn())) {
                    String errmsg = "[错误信息: 流水号不一致,sbs:" + rtnWsysSn + "]";
                    throw new RuntimeException("SBS报文解析错误。" + errmsg);
                }
                record.setSbsTxnTime(sdf);
                record.setRetCode(PayoutDetlRtnCode.SUCCESS.getCode());
                record.setErrMsg(PayoutDetlRtnCode.SUCCESS.getTitle());
                return payoutDetlService.updatePayoutDetlTxnStep(record, PayoutDetlTxnStep.SBSN058);
            }
        } else {
            throw new RuntimeException("sbs-n058并发交易冲突。流水号：" + record.getReqSn() + record.getSn());
        }
    }

    @Transactional
    private int processN059(FipPayoutdetl record) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        if (payoutDetlService.isNoTxnStepClash(record)) {
            // sbs n059 参数
            List<String> paramlist = assembleTn059Param(record);
            // sbs交易,途径dep
            byte[] sbsResBytes = DepCtgManager.processSingleResponsePkg("n059", paramlist);
            logger.info(new String(sbsResBytes));

            String formcode = new String(sbsResBytes, 21, 4);
            String rtnWsysSn = new String(sbsResBytes, 72, 18).trim();
            jobLogService.insertNewJoblog(record.getPkid(), "fip_payoutdetl", record.getReqSn() + record.getSn() + "SBSN059",
                    "SBS返回:" + formcode, "Haierfip", "资金交换平台");
            if (!"T531".equals(formcode)) {
                // 交易失败，除记录日志外，不做任何处理，须再次发起n059，完成入账确认
                String errmsg = "[SBS返回错误信息: " + formcode + " " + getSBSErrMsgFromResponse(sbsResBytes) + " ]";
                logger.error("SBS通讯或报文解析错误。" + errmsg);
                return 0;
            } else {
                // 判断流水号是否一致
                if (!rtnWsysSn.equals(record.getReqSn() + record.getSn())) {
                    String errmsg = "[错误信息: 流水号不一致,sbs:" + rtnWsysSn + "]";
                    throw new RuntimeException("SBS报文解析错误。" + errmsg);
                }
                record.setSbsTxnTime(sdf);
                record.setRetCode(PayoutDetlRtnCode.FAIL.getCode());
                record.setErrMsg(PayoutDetlRtnCode.FAIL.getTitle());
                return payoutDetlService.updatePayoutDetlTxnStep(record, PayoutDetlTxnStep.SBSN059);
            }
        } else {
            throw new RuntimeException("sbs-n059并发交易冲突。流水号：" + record.getReqSn() + record.getSn());
        }
    }

    // 组装n058报文 同n057
    private List<String> assembleTn058Param(FipPayoutdetl detail) {
        return assembleTn057Param(detail);
    }

    // 组装n059报文  同n057
    private List<String> assembleTn059Param(FipPayoutdetl detail) {
        return assembleTn057Param(detail);
    }

    // 组装n057报文
    private List<String> assembleTn057Param(FipPayoutdetl detail) {
        List<String> txnparamList = new ArrayList<String>();
        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        DecimalFormat df = new DecimalFormat("#############0.00");
        String sbsActno = detail.getSbsAccountNo();

        // 1 外围系统流水号  18 包流水号+记录序号
        String sn = detail.getReqSn() + detail.getSn();
        if (sn.length() > 18) {
            sn = sn.substring(sn.length() - 18, sn.length());
        } else {
            sn = StringUtils.rightPad(sn, 18, " ");
        }
        txnparamList.add(sn);
        // 2 机构 010
        txnparamList.add("010");
        // 3 委托日期 8
        txnparamList.add(txndate);
        // 4 客户号 7位
        String cusidt = sbsActno.startsWith("8010") ? sbsActno.substring(4, 11) : sbsActno.substring(0, 7);
        txnparamList.add(cusidt);
        // 5 交易类型 CPY－银联代发
        txnparamList.add("CPY");
        // 6 交易货币 001-人民币
        txnparamList.add("001");
        // 7 交易金额 银联接口中金额单位是分，sbs交易金额单位是元
        txnparamList.add(df.format(new BigDecimal(detail.getAmount()).divide(new BigDecimal(100.0))));
        // 8 汇款类型 M－信汇 T－电汇
        txnparamList.add("T");
        // 9 汇款账户类型 01
        txnparamList.add("01");
        // 10 付款帐户 8010
        txnparamList.add(sbsActno.startsWith("8010") ? sbsActno : "8010" + sbsActno);
        // 11 费用帐户类型 1
        txnparamList.add("1");
        // 12 费用账户
        txnparamList.add(" ");
        // 13 收款人账号
        txnparamList.add(detail.getAccountNo());
        // 14 收款人姓名
        txnparamList.add(detail.getAccountName());
        // 15 收款行行名
        txnparamList.add(" ");
        // 16 代理行行名
        txnparamList.add(" ");
        // 17 人行账号
        txnparamList.add(" ");
        // 18 汇款人名称
        txnparamList.add(" ");
        // 19 汇款用途
        txnparamList.add(StringUtils.isEmpty(detail.getRemark()) ? detail.getReqSn() + detail.getSn() + "银联代付" : detail.getRemark());
        // 20 支票类型
        txnparamList.add(" ");
        // 21 收款行行号 12位
        txnparamList.add("            ");
        // 22 支票密码	X(10)	固定值	空格
        txnparamList.add("          ");
        // 23 保留项	X(1)	固定值	空格
        txnparamList.add(" ");
        // 24 FS流水号 备用
        txnparamList.add(" ");
        // 25 交易流水号 备用
        txnparamList.add(" ");
        return txnparamList;
    }

    /*
      获取sbs报错应答报文中的错误信息
    */
    private String getSBSErrMsgFromResponse(byte[] buffer) {

        byte[] bLen = new byte[2];
        System.arraycopy(buffer, 27, bLen, 0, 2);
        short iLen = (short) (((bLen[0] << 8) | bLen[1] & 0xff));
        byte[] bLog = new byte[iLen];
        System.arraycopy(buffer, 29, bLog, 0, iLen);
        String log = new String(bLog);
        log = StringUtils.trimToEmpty(log);

        return log;
    }
}
