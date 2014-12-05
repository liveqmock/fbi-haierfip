package fip.batch.crontask;

import fip.batch.common.tool.OperationValve;
import fip.common.constant.*;
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
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 新消费信贷后台自动代扣处理.  批量代扣
 * User: zhanrui
 * Date: 2014-11-24
 */

@Component
public class CcmsCutpayBatchHandler implements AutoCutpayManager {
    private static final Logger logger = LoggerFactory.getLogger(CcmsCutpayBatchHandler.class);

    @Autowired
    private CcmsService ccmsService;
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

    private final BizType bizType = BizType.XFNEW;
    private final BizType channelBizType = BizType.XFNEW;

    public synchronized void processAll() {
        try {
            int count = 0;
            boolean isExistPendQryRecord = true;
            while (count < 2 && isExistPendQryRecord) {
                List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);
                if (needQueryBatList.size() > 0) {
                    logger.info("消费信贷批量代扣: 系统中存在未完成结果确认的记录:" + needQueryBatList.size());
                    performCutpayTxn();
                    count++;
                    isExistPendQryRecord = true;
                } else {
                    isExistPendQryRecord = false;
                }
            }

            obtainBills();
            performCutpayTxn();

            try {
                Thread.sleep(10 * 60 * 1000);  //十分钟后进行结果查询
            } catch (InterruptedException e) {
                //
            }


            performResultQueryTxn();
            writebackBills();
        } catch (Exception e) {
            logger.error("消费信贷批量代扣错误。", e);
            //TODO 结果记录在数据库表中
        }
    }

    public synchronized void obtainBills() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }
        List<String> returnMsgs = new ArrayList<String>();
        int count = ccmsService.doObtainCcmsBills(BizType.XFNEW, BillType.NORMAL, returnMsgs);
        logger.info("消费信贷自动批量代扣【代扣记录获取】本次获取记录数：" + count + " 条.");
    }

    @Override
    public synchronized void performCutpayTxn() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }
        List<FipCutpaydetl> detlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.INIT);
        batchPkgService.packUnipayBatchPkg(bizType, detlList, channelBizType.getCode());
        List<FipCutpaybat> sendablePkgList = batchPkgService.selectSendableBatchs(bizType, CutpayChannel.UNIPAY, TxSendFlag.UNSEND);
        logger.info("消费信贷自动批量代扣【代扣记录打包】：处理完成");

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
                            logger.info("消费信贷自动批量代扣【代扣记录发送】：重发, 返回代码：" + unipayRtnCode);
                        } else {
                            isSent = true;
                            logger.info("消费信贷自动批量代扣【代扣记录发送】：处理完成, 返回代码：" + unipayRtnCode);
                        }
                    } else {
                        logger.error("自动批量处理代扣交易发送：银联响应信息为空, 不再重发此条记录" + pkg.getTxpkgSn());
                        break;
                    }
                } catch (Exception e) {
                    logger.error("自动批量处理代扣交易发送失败", e);
                    count++;
                    isSent = false;
                    logger.info("消费信贷自动批量代扣【代扣记录发送】：重发");
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e1) {
                        //
                    }
                }
            }
        }
    }

    @Override
    public synchronized void performResultQueryTxn() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }

        List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);

        //并发进行
        Executor executor = Executors.newCachedThreadPool();
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
                        logger.info("消费信贷自动批量代扣【代扣结果查询】：处理完成, 返回代码：" + unipayRtnCode);
                    } else {
                        isQryOver = false;
                        logger.info("消费信贷自动批量代扣【代扣结果查询】：未返回明确结果，三分钟后继续查询, 返回代码：" + unipayRtnCode);
                        Thread.sleep(3 * 60 * 1000); //未查回明确结果 3分钟后继续查询
                    }
                } else {
                    logger.error("自动批量处理代扣交易结果查询：银联响应信息为空, 不再重发查询交易" + bat.getTxpkgSn());
                    break;
                }
                //查询10次以后 每次都回写
                if (count >= 10) {
                    writebackBills();
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

    @Override
    public synchronized void writebackBills() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("自动批量处理开关已关闭。");
        }
        List<FipCutpaydetl> successDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_SUCCESS);
        List<FipCutpaydetl> failureDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_FAILED);
        List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayBatchDetail(this.bizType, BillStatus.CUTPAY_QRY_PEND);


        int succCnt = ccmsService.writebackCutPayRecord2CCMS(successDetlList, true);
        int failCnt = ccmsService.writebackCutPayRecord2CCMS(failureDetlList, true);
        //回写结果不明记录 不归档
        int qryCnt = ccmsService.writebackCutPayRecord2CCMS(needQueryDetlList, false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }
        logger.info("消费信贷自动批量代扣【代扣结果回写】：本次回写记录条数(代扣成功)：" + succCnt + " 条(已做归档处理).");
        logger.info("消费信贷自动批量代扣【代扣结果回写】：本次回写记录条数(代扣失败)：" + failCnt + " 条(已做归档处理).");
        logger.info("消费信贷自动批量代扣【代扣结果回写】：本次回写记录条数(代扣结果不明)：" + qryCnt + " 条(未作归档处理).");
    }

    @Override
    public void archiveBills() {
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

    //===========================================================
    public static void main(String... argv) {
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        CcmsCutpayBatchHandler handler = (CcmsCutpayBatchHandler) context.getBean("ccmsCutpayHandler");
//        handler.obtainBills();
        handler.performCutpayTxn();
        handler.writebackBills();
//        handler.performResultQueryTxn();
        logger.info("end");
        //System.exit(0);
    }
}
