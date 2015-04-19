package fip.batch.crontask;

import fip.batch.common.tool.OperationValve;
import fip.common.constant.*;
import fip.common.utils.sms.SmsHelper;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import pub.platform.advance.utils.PropertyManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 专卖店（信贷系统）后台自动代扣处理.  批量代扣
 * 1。获取代扣记录
 * 2. 银联代扣
 * 3. SBS记账
 * 4。回写信贷系统
 * <p/>
 * User: zhanrui
 * Date: 2015-04-17
 */

//@Component
public class ZmdCutpayBatchHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZmdCutpayBatchHandler.class);

    @Autowired
    private ZmdService zmdService;
    @Autowired
    private BillManagerService billManagerService;
    @Autowired
    private JobLogService jobLogService;
    @Autowired
    private UnipayDepService unipayDepService;
    @Autowired
    private BatchPkgService batchPkgService;

    @Autowired
    @Qualifier("propertyFileOperationValve")
    private OperationValve operationValve;

    private BizType bizType = BizType.ZMD;
    private BizType channelBizType = BizType.FD; //使用房贷的银联商户号

    public synchronized void processAll() {
        if (!isCronTaskOpen()) {
            if (BizType.ZMD.equals(bizType)) {
                SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "专卖店批量代扣: 定时任务未开启");
            }
            return;
        }

        try {
            //短信通知
            if (BizType.ZMD.equals(bizType)) {
                SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "专卖店批量代扣开始");
            } else {
                throw new RuntimeException("业务代码错误，非ZMD");
            }

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                //
            }

            //前处理：检查当前系统是否存在未完成银联结果查询的记录，若存在，先完成结果查询
            int count = 0;
            boolean isExistPendQryRecord = true;
            while (count < 2 && isExistPendQryRecord) {
                List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);
                if (needQueryBatList.size() > 0) {
                    logger.info(getBizName() + "批量代扣: 系统中存在未完成结果确认的记录:" + needQueryBatList.size());
                    performResultQueryTxn();
                    count++;
                    isExistPendQryRecord = true;
                } else {
                    isExistPendQryRecord = false;
                }
            }

            //正式获取代扣记录前，再检查一次本地记录状态
            List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);
            if (needQueryBatList.size() == 0) {
                obtainBills();
                performCutpayTxn();

                try {
                    Thread.sleep(10 * 60 * 1000);  //十分钟后进行结果查询
                } catch (InterruptedException e) {
                    //
                }

                performResultQueryTxn();
                sbsBookAll();

                SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "专卖店代扣完成");
            } else {
                SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "专卖店代扣存在未完成的记录，本次代扣暂停.");
            }
        } catch (Exception e) {
            logger.error(getBizName() + "批量代扣错误。", e);
            //短信通知
            String sms = e.getMessage();
            if (sms == null) {
                sms = e.toString();
            }
            sms = sms.length() <= 100 ? sms : sms.substring(0, 100);
            SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "专卖店批量代扣异常:" + sms);
        }
    }

    private synchronized void obtainBills() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }
        List<String> returnMsgs = new ArrayList<String>();
        int count = zmdService.doObtainZmdBills(bizType, BillType.NORMAL, returnMsgs);
        logger.info(getBizName() + "自动批量代扣【代扣记录获取】本次获取记录数：" + count + " 条.");
    }

    private synchronized void performCutpayTxn() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }
        List<FipCutpaydetl> detlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.INIT);
        batchPkgService.packUnipayBatchPkg(bizType, detlList, channelBizType.getCode());
        List<FipCutpaybat> sendablePkgList = batchPkgService.selectSendableBatchs(bizType, CutpayChannel.UNIPAY, TxSendFlag.UNSEND);
        logger.info(getBizName() + "自动批量代扣【代扣记录打包】：处理完成");

        for (FipCutpaybat pkg : sendablePkgList) {
            boolean isSent = false;
            int count = 0;
            while (!isSent && count < 3) { //失败情况下重发, 每次延时30秒
                try {
                    String unipayRtnCode = unipayDepService.sendAndRecvT1001003Message(pkg);
                    count++;
                    if (!StringUtils.isEmpty(unipayRtnCode)) {
                        if ("1002".equals(unipayRtnCode)) {  //无法查询到该交易，可以重发
                            isSent = false;
                            Thread.sleep(30 * 1000);
                            logger.info(getBizName() + "自动批量代扣【代扣记录发送】：重发, 返回代码：" + unipayRtnCode);
                        } else {
                            isSent = true;
                            logger.info(getBizName() + "自动批量代扣【代扣记录发送】：处理完成, 返回代码：" + unipayRtnCode);
                        }
                    } else {
                        logger.error("自动批量处理代扣交易发送：银联响应信息为空, 不再重发此条记录" + pkg.getTxpkgSn());
                        break;
                    }
                } catch (Exception e) {
                    logger.error("自动批量处理代扣交易发送失败", e);
                    count++;
                    isSent = false;
                    logger.info(getBizName() + "自动批量代扣【代扣记录发送】：重发");
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e1) {
                        //
                    }
                }
            }
        }
    }

    private synchronized void performResultQueryTxn() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }

        List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);

        //并发进行
        ExecutorService executor = Executors.newCachedThreadPool();
        for (final FipCutpaybat bat : needQueryBatList) {
            try {
                Thread.sleep(15 * 1000);
            } catch (InterruptedException e) {
                //
            }
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    doSendAndRecvOneBatchPkg(bat);
                }
            };
            executor.execute(task);
        }

        executor.shutdown();
        int count = 0;
        int stepMinutes = 5;
        try {
            while (!executor.awaitTermination(stepMinutes, TimeUnit.MINUTES)) {
                count++;
                int mm = 10 + stepMinutes * count;//初始代扣后10分钟后才开始进行结果查询
                logger.info(getBizName() + " 定时代扣线程[" + Thread.currentThread().getName() + "]运行中... 时间[" + mm + "]分钟.");
            }
        } catch (InterruptedException e) {
            logger.info(getBizName() + "线程中断", e);
        }
    }

    private void doSendAndRecvOneBatchPkg(FipCutpaybat bat) {
        boolean isQryOver = false;
        int count = 0;
        while (!isQryOver && count < 60) { //重发   3小时
            try {
                String unipayRtnCode = unipayDepService.sendAndRecvCutpayT1003003Message(bat);
                count++;
                if (!StringUtils.isEmpty(unipayRtnCode)) {
                    if (unipayRtnCode.startsWith("0") || unipayRtnCode.startsWith("1")) {  //无法查询到该交易，可以重发
                        isQryOver = true;  //明确返回成功或失败
                        logger.info(getBizName() + "自动批量代扣【代扣结果查询】：处理完成, 返回代码：" + unipayRtnCode);
                    } else {
                        isQryOver = false;
                        logger.info(getBizName() + "自动批量代扣【代扣结果查询】：未返回明确结果，三分钟后继续查询, 返回代码：" + unipayRtnCode);
                        Thread.sleep(3 * 60 * 1000); //未查回明确结果 3分钟后继续查询
                    }
                } else {
                    logger.error("自动批量处理代扣交易结果查询：银联响应信息为空, 不再重发查询交易" + bat.getTxpkgSn());
                    break;
                }
            } catch (Exception e) {
                logger.error("自动批量处理结果查询交易处理失败", e);
                count++;
                isQryOver = false;
                try {
                    Thread.sleep(3 * 60 * 1000);   //出现异常
                } catch (InterruptedException e1) {
                    //
                }
            }
        }
    }

    //针对每个批量包的处理
    private void writebackBills(String txPkgSn) {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }
        List<FipCutpaydetl> successDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_SUCCESS, txPkgSn);
        List<FipCutpaydetl> failureDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_FAILED, txPkgSn);
        //List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayBatchDetail(this.bizType, BillStatus.CUTPAY_QRY_PEND, txPkgSn);


        int succCnt = zmdService.writebackCutPayRecord2Zmd(successDetlList, true);
        int failCnt = zmdService.writebackCutPayRecord2Zmd(failureDetlList, true);
        //回写结果不明记录 不归档
        //int qryCnt = zmdService.writebackCutPayRecord2Zmd(needQueryDetlList, false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }
        logger.info(getBizName() + "自动批量代扣【代扣结果回写" + txPkgSn + "】：本次回写记录条数(代扣成功)：" + succCnt + " 条(已做归档处理).");
        logger.info(getBizName() + "自动批量代扣【代扣结果回写" + txPkgSn + "】：本次回写记录条数(代扣失败)：" + failCnt + " 条(已做归档处理).");
    }

    //全部批量包的处理
    private synchronized void writebackBillsAll() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }

        List<FipCutpaydetl> successDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_SUCCESS);
        List<FipCutpaydetl> failureDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_FAILED);
        //List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayBatchDetail(this.bizType, BillStatus.CUTPAY_QRY_PEND);


        int succCnt = zmdService.writebackCutPayRecord2Zmd(successDetlList, true);
        int failCnt = zmdService.writebackCutPayRecord2Zmd(failureDetlList, true);
        //回写结果不明记录 不归档
        //int qryCnt = zmdService.writebackCutPayRecord2Zmd(needQueryDetlList, false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }
        logger.info(getBizName() + "自动批量代扣【代扣结果回写】：本次回写记录条数(代扣成功)：" + succCnt + " 条(已做归档处理).");
        logger.info(getBizName() + "自动批量代扣【代扣结果回写】：本次回写记录条数(代扣失败)：" + failCnt + " 条(已做归档处理).");
    }


    //SBS账务处理
    private synchronized void sbsBookAll() {
        try {
            //处理 代扣成功的、待入账的、入账失败的
            List<FipCutpaydetl> detlListSucc = billManagerService.selectBillList(this.bizType, BillStatus.CUTPAY_SUCCESS);
            //自动做待入账处理
            billManagerService.updateCutpaydetlBillStatus(detlListSucc, BillStatus.ACCOUNT_PEND);

            List<FipCutpaydetl> detlList = billManagerService.selectBillList(this.bizType, BillStatus.ACCOUNT_PEND, BillStatus.ACCOUNT_FAILED);
            int succ = zmdService.accountCutPayRecord2SBS(detlList);
            logger.info(getBizName() + "自动批量代扣【SBS记账】：" + succ + " 条.");
        } catch (Exception e) {
            //出现异常时，忽略异常，继续处理
            logger.info(getBizName() + "自动批量代扣【SBS记账】出现错误.", e);
            String sms = e.getMessage();
            if (sms == null) {
                sms = e.toString();
            }
            sms = sms.length() <= 100 ? sms : sms.substring(0, 100);
            SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "专卖店代扣,SBS处理异常:" + sms);
        }
    }


    //=============================================
    private boolean isCronTaskOpen() {
        try {
            return operationValve.isOpen("cron_task_mode");
        } catch (Exception e) {
            logger.error("读取参数文件错误", e);
            return false;
        }
    }

    private String getBizName() {
        if (BizType.ZMD.equals(bizType)) {
            return "专卖店";
        }  else {
            return "业务类型错误";
        }
    }
    //=====


    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public void setChannelBizType(BizType channelBizType) {
        this.channelBizType = channelBizType;
    }

    //===========================================================
    public static void main(String... argv) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        ZmdCutpayBatchHandler handler = (ZmdCutpayBatchHandler) context.getBean("zmdCutpayBatchHandler");
        handler.processAll();

//        handler.obtainBills();
//        handler.performCutpayTxn();
//        handler.writebackBillsAll();
//        handler.performResultQueryTxn();
//        logger.info("end");
    }
}
