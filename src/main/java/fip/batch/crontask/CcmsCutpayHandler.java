package fip.batch.crontask;

import fip.batch.common.tool.OperationValve;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.CcmsService;
import fip.service.fip.JobLogService;
import fip.service.fip.UnipayDepService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 新消费信贷后台自动代扣处理.
 * User: zhanrui
 * Date: 12-12-3
 * Time: 下午4:02
 * To change this template use File | Settings | File Templates.
 */

@Component
public class CcmsCutpayHandler implements AutoCutpayManager{
    private static final Logger logger = LoggerFactory.getLogger(CcmsCutpayHandler.class);

    @Autowired
    private CcmsService ccmsService;
    @Autowired
    private BillManagerService billManagerService;
    @Autowired
    private JobLogService jobLogService;
    @Autowired
    private UnipayDepService unipayDepService;

    @Autowired @Qualifier("propertyFileOperationValve")
    private OperationValve operationValve;

    private BizType bizType;

    public void processAll(){
        obtainBills();
        performCutpayTxn();
        performResultQueryTxn();
        writebackBills();
    }
    public void processResultQueryAndWriteBack(){
        performResultQueryTxn();
        writebackBills();
    }

    public void obtainBills() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("自动批量处理开关已关闭。");
                return;
            }
            List<String> returnMsgs = new ArrayList<String>();
            int count = ccmsService.doObtainCcmsBills(BizType.XFNEW, BillType.NORMAL, returnMsgs);
            logger.info("本次获取记录数：" + count + " 条.");
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
        }
    }

    @Override
    public void performCutpayTxn() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("自动批量处理开关已关闭。");
                return;
            }
            List<FipCutpaydetl> detlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.INIT);
            detlList.addAll(billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.RESEND_PEND));

            for (FipCutpaydetl cutpaydetl : detlList) {
                processOneCutpayRequestRecord(cutpaydetl);
            }
            logger.info("数据发送结束！");
        } catch (Exception e) {
            logger.info("数据发送结束处理异常" + e.getMessage());
        }

    }

    @Override
    public void performResultQueryTxn() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("自动批量处理开关已关闭。");
                return;
            }
            List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.CUTPAY_QRY_PEND);
            for (FipCutpaydetl cutpaydetl : needQueryDetlList) {
                processOneQueryRecord(cutpaydetl);
            }
            logger.info("结果查询交易发送结束。");
        } catch (Exception e) {
            logger.error("结果查询交易处理异常", e);
        }
    }

    @Override
    public void writebackBills() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("自动批量处理开关已关闭。");
                return;
            }
            List<FipCutpaydetl> successDetlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.CUTPAY_SUCCESS);
            List<FipCutpaydetl> failureDetlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.CUTPAY_FAILED);
            List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.CUTPAY_QRY_PEND);

            int succCnt = ccmsService.writebackCutPayRecord2CCMS(successDetlList, true);

            int failCnt= ccmsService.writebackCutPayRecord2CCMS(failureDetlList, true);

            //回写结果不明记录 不归档
            int qryCnt= ccmsService.writebackCutPayRecord2CCMS(needQueryDetlList, false);

            Thread.sleep(500);

            logger.info("本次回写记录条数(代扣成功)：" + succCnt + " 条(已做归档处理).");
            logger.info("本次回写记录条数(代扣失败)：" + failCnt + " 条(已做归档处理).");
            logger.info("本次回写记录条数(代扣结果不明)：" + qryCnt + " 条(未作归档处理).");
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
        }
    }

    @Override
    public void archiveBills() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("自动批量处理开关已关闭。");
                return;
            }

            //TODO

            logger.info("归档处理结束。");
        } catch (Exception e) {
            logger.error("归档处理异常", e);
        }
    }


    //=============================================
    private void processOneCutpayRequestRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvT1001001Message(record);
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文完成。");
        } catch (Exception e) {
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文失败." + e.getMessage());
            //throw new RuntimeException("数据发送异常，请检查系统线路重新发送！", e);
        }
    }

    private void processOneQueryRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvCutpayT1003001Message(record);
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文完成。");
        } catch (Exception e) {
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文失败." + e.getMessage());
            //throw new RuntimeException("数据发送异常，请检查系统线路重新发送！" ,e);
        }
    }

    private void appendNewJoblog(String pkid, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, "fip_cutpaydetl", jobname, jobdesc, "9999", "BATCH");
    }

    //===
    private boolean isCronTaskOpen(){
        try {
            return operationValve.isOpen("cron_task_mode");
        } catch (Exception e) {
            logger.error("读取参数文件错误", e);
            return false;
        }
    }

    //===========================================================
    public static void main(String... argv){
        ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        CcmsCutpayHandler handler = (CcmsCutpayHandler)context.getBean("ccmsCutpayHandler");
//        handler.obtainBills();
        handler.performCutpayTxn();
        handler.writebackBills();
//        handler.performResultQueryTxn();
        logger.info("end");
        //System.exit(0);
    }
}
