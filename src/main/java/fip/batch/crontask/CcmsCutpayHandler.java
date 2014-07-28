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
 * �������Ŵ���̨�Զ����۴���.
 * User: zhanrui
 * Date: 12-12-3
 * Time: ����4:02
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
                logger.info("�Զ������������ѹرա�");
                return;
            }
            List<String> returnMsgs = new ArrayList<String>();
            int count = ccmsService.doObtainCcmsBills(BizType.XFNEW, BillType.NORMAL, returnMsgs);
            logger.info("���λ�ȡ��¼����" + count + " ��.");
        } catch (Exception e) {
            logger.error("��ȡ��¼ʱ����", e);
        }
    }

    @Override
    public void performCutpayTxn() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("�Զ������������ѹرա�");
                return;
            }
            List<FipCutpaydetl> detlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.INIT);
            detlList.addAll(billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.RESEND_PEND));

            for (FipCutpaydetl cutpaydetl : detlList) {
                processOneCutpayRequestRecord(cutpaydetl);
            }
            logger.info("���ݷ��ͽ�����");
        } catch (Exception e) {
            logger.info("���ݷ��ͽ��������쳣" + e.getMessage());
        }

    }

    @Override
    public void performResultQueryTxn() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("�Զ������������ѹرա�");
                return;
            }
            List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.CUTPAY_QRY_PEND);
            for (FipCutpaydetl cutpaydetl : needQueryDetlList) {
                processOneQueryRecord(cutpaydetl);
            }
            logger.info("�����ѯ���׷��ͽ�����");
        } catch (Exception e) {
            logger.error("�����ѯ���״����쳣", e);
        }
    }

    @Override
    public void writebackBills() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("�Զ������������ѹرա�");
                return;
            }
            List<FipCutpaydetl> successDetlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.CUTPAY_SUCCESS);
            List<FipCutpaydetl> failureDetlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.CUTPAY_FAILED);
            List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayOnline(BizType.XFNEW, BillStatus.CUTPAY_QRY_PEND);

            int succCnt = ccmsService.writebackCutPayRecord2CCMS(successDetlList, true);

            int failCnt= ccmsService.writebackCutPayRecord2CCMS(failureDetlList, true);

            //��д���������¼ ���鵵
            int qryCnt= ccmsService.writebackCutPayRecord2CCMS(needQueryDetlList, false);

            Thread.sleep(500);

            logger.info("���λ�д��¼����(���۳ɹ�)��" + succCnt + " ��(�����鵵����).");
            logger.info("���λ�д��¼����(����ʧ��)��" + failCnt + " ��(�����鵵����).");
            logger.info("���λ�д��¼����(���۽������)��" + qryCnt + " ��(δ���鵵����).");
        } catch (Exception e) {
            logger.error("��ȡ��¼ʱ����", e);
        }
    }

    @Override
    public void archiveBills() {
        try {
            if (!isCronTaskOpen()) {
                logger.info("�Զ������������ѹرա�");
                return;
            }

            //TODO

            logger.info("�鵵���������");
        } catch (Exception e) {
            logger.error("�鵵�����쳣", e);
        }
    }


    //=============================================
    private void processOneCutpayRequestRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvT1001001Message(record);
            appendNewJoblog(pkid, "���Ϳۿ�����", "���������ۿ���������ɡ�");
        } catch (Exception e) {
            appendNewJoblog(pkid, "���Ϳۿ�����", "���������ۿ�������ʧ��." + e.getMessage());
            //throw new RuntimeException("���ݷ����쳣������ϵͳ��·���·��ͣ�", e);
        }
    }

    private void processOneQueryRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvCutpayT1003001Message(record);
            appendNewJoblog(pkid, "���Ͳ�ѯ����", "����������ѯ��������ɡ�");
        } catch (Exception e) {
            appendNewJoblog(pkid, "���Ͳ�ѯ����", "����������ѯ������ʧ��." + e.getMessage());
            //throw new RuntimeException("���ݷ����쳣������ϵͳ��·���·��ͣ�" ,e);
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
            logger.error("��ȡ�����ļ�����", e);
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
