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
 * ר���꣨�Ŵ�ϵͳ����̨�Զ����۴���.  ��������
 * 1����ȡ���ۼ�¼
 * 2. ��������
 * 3. SBS����
 * 4����д�Ŵ�ϵͳ
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
    private BizType channelBizType = BizType.FD; //ʹ�÷����������̻���

    public synchronized void processAll() {
        if (!isCronTaskOpen()) {
            if (BizType.ZMD.equals(bizType)) {
                SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "ר������������: ��ʱ����δ����");
            }
            return;
        }

        try {
            //����֪ͨ
            if (BizType.ZMD.equals(bizType)) {
                SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "ר�����������ۿ�ʼ");
            } else {
                throw new RuntimeException("ҵ�������󣬷�ZMD");
            }

            try {
                Thread.sleep(10 * 1000);
            } catch (InterruptedException e) {
                //
            }

            //ǰ������鵱ǰϵͳ�Ƿ����δ������������ѯ�ļ�¼�������ڣ�����ɽ����ѯ
            int count = 0;
            boolean isExistPendQryRecord = true;
            while (count < 2 && isExistPendQryRecord) {
                List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);
                if (needQueryBatList.size() > 0) {
                    logger.info(getBizName() + "��������: ϵͳ�д���δ��ɽ��ȷ�ϵļ�¼:" + needQueryBatList.size());
                    performResultQueryTxn();
                    count++;
                    isExistPendQryRecord = true;
                } else {
                    isExistPendQryRecord = false;
                }
            }

            //��ʽ��ȡ���ۼ�¼ǰ���ټ��һ�α��ؼ�¼״̬
            List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);
            if (needQueryBatList.size() == 0) {
                obtainBills();
                performCutpayTxn();

                try {
                    Thread.sleep(10 * 60 * 1000);  //ʮ���Ӻ���н����ѯ
                } catch (InterruptedException e) {
                    //
                }

                performResultQueryTxn();
                sbsBookAll();

                SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "ר����������");
            } else {
                SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "ר������۴���δ��ɵļ�¼�����δ�����ͣ.");
            }
        } catch (Exception e) {
            logger.error(getBizName() + "�������۴���", e);
            //����֪ͨ
            String sms = e.getMessage();
            if (sms == null) {
                sms = e.toString();
            }
            sms = sms.length() <= 100 ? sms : sms.substring(0, 100);
            SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "ר�������������쳣:" + sms);
        }
    }

    private synchronized void obtainBills() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("�Զ������������ѹرա�");
        }
        List<String> returnMsgs = new ArrayList<String>();
        int count = zmdService.doObtainZmdBills(bizType, BillType.NORMAL, returnMsgs);
        logger.info(getBizName() + "�Զ��������ۡ����ۼ�¼��ȡ�����λ�ȡ��¼����" + count + " ��.");
    }

    private synchronized void performCutpayTxn() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("�Զ������������ѹرա�");
        }
        List<FipCutpaydetl> detlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.INIT);
        batchPkgService.packUnipayBatchPkg(bizType, detlList, channelBizType.getCode());
        List<FipCutpaybat> sendablePkgList = batchPkgService.selectSendableBatchs(bizType, CutpayChannel.UNIPAY, TxSendFlag.UNSEND);
        logger.info(getBizName() + "�Զ��������ۡ����ۼ�¼��������������");

        for (FipCutpaybat pkg : sendablePkgList) {
            boolean isSent = false;
            int count = 0;
            while (!isSent && count < 3) { //ʧ��������ط�, ÿ����ʱ30��
                try {
                    String unipayRtnCode = unipayDepService.sendAndRecvT1001003Message(pkg);
                    count++;
                    if (!StringUtils.isEmpty(unipayRtnCode)) {
                        if ("1002".equals(unipayRtnCode)) {  //�޷���ѯ���ý��ף������ط�
                            isSent = false;
                            Thread.sleep(30 * 1000);
                            logger.info(getBizName() + "�Զ��������ۡ����ۼ�¼���͡����ط�, ���ش��룺" + unipayRtnCode);
                        } else {
                            isSent = true;
                            logger.info(getBizName() + "�Զ��������ۡ����ۼ�¼���͡����������, ���ش��룺" + unipayRtnCode);
                        }
                    } else {
                        logger.error("�Զ�����������۽��׷��ͣ�������Ӧ��ϢΪ��, �����ط�������¼" + pkg.getTxpkgSn());
                        break;
                    }
                } catch (Exception e) {
                    logger.error("�Զ�����������۽��׷���ʧ��", e);
                    count++;
                    isSent = false;
                    logger.info(getBizName() + "�Զ��������ۡ����ۼ�¼���͡����ط�");
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
            throw new RuntimeException("�Զ������������ѹرա�");
        }

        List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);

        //��������
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
                int mm = 10 + stepMinutes * count;//��ʼ���ۺ�10���Ӻ�ſ�ʼ���н����ѯ
                logger.info(getBizName() + " ��ʱ�����߳�[" + Thread.currentThread().getName() + "]������... ʱ��[" + mm + "]����.");
            }
        } catch (InterruptedException e) {
            logger.info(getBizName() + "�߳��ж�", e);
        }
    }

    private void doSendAndRecvOneBatchPkg(FipCutpaybat bat) {
        boolean isQryOver = false;
        int count = 0;
        while (!isQryOver && count < 60) { //�ط�   3Сʱ
            try {
                String unipayRtnCode = unipayDepService.sendAndRecvCutpayT1003003Message(bat);
                count++;
                if (!StringUtils.isEmpty(unipayRtnCode)) {
                    if (unipayRtnCode.startsWith("0") || unipayRtnCode.startsWith("1")) {  //�޷���ѯ���ý��ף������ط�
                        isQryOver = true;  //��ȷ���سɹ���ʧ��
                        logger.info(getBizName() + "�Զ��������ۡ����۽����ѯ�����������, ���ش��룺" + unipayRtnCode);
                    } else {
                        isQryOver = false;
                        logger.info(getBizName() + "�Զ��������ۡ����۽����ѯ����δ������ȷ����������Ӻ������ѯ, ���ش��룺" + unipayRtnCode);
                        Thread.sleep(3 * 60 * 1000); //δ�����ȷ��� 3���Ӻ������ѯ
                    }
                } else {
                    logger.error("�Զ�����������۽��׽����ѯ��������Ӧ��ϢΪ��, �����ط���ѯ����" + bat.getTxpkgSn());
                    break;
                }
            } catch (Exception e) {
                logger.error("�Զ�������������ѯ���״���ʧ��", e);
                count++;
                isQryOver = false;
                try {
                    Thread.sleep(3 * 60 * 1000);   //�����쳣
                } catch (InterruptedException e1) {
                    //
                }
            }
        }
    }

    //���ÿ���������Ĵ���
    private void writebackBills(String txPkgSn) {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("�Զ������������ѹرա�");
        }
        List<FipCutpaydetl> successDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_SUCCESS, txPkgSn);
        List<FipCutpaydetl> failureDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_FAILED, txPkgSn);
        //List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayBatchDetail(this.bizType, BillStatus.CUTPAY_QRY_PEND, txPkgSn);


        int succCnt = zmdService.writebackCutPayRecord2Zmd(successDetlList, true);
        int failCnt = zmdService.writebackCutPayRecord2Zmd(failureDetlList, true);
        //��д���������¼ ���鵵
        //int qryCnt = zmdService.writebackCutPayRecord2Zmd(needQueryDetlList, false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }
        logger.info(getBizName() + "�Զ��������ۡ����۽����д" + txPkgSn + "�������λ�д��¼����(���۳ɹ�)��" + succCnt + " ��(�����鵵����).");
        logger.info(getBizName() + "�Զ��������ۡ����۽����д" + txPkgSn + "�������λ�д��¼����(����ʧ��)��" + failCnt + " ��(�����鵵����).");
    }

    //ȫ���������Ĵ���
    private synchronized void writebackBillsAll() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("�Զ������������ѹرա�");
        }

        List<FipCutpaydetl> successDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_SUCCESS);
        List<FipCutpaydetl> failureDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_FAILED);
        //List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayBatchDetail(this.bizType, BillStatus.CUTPAY_QRY_PEND);


        int succCnt = zmdService.writebackCutPayRecord2Zmd(successDetlList, true);
        int failCnt = zmdService.writebackCutPayRecord2Zmd(failureDetlList, true);
        //��д���������¼ ���鵵
        //int qryCnt = zmdService.writebackCutPayRecord2Zmd(needQueryDetlList, false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }
        logger.info(getBizName() + "�Զ��������ۡ����۽����д�������λ�д��¼����(���۳ɹ�)��" + succCnt + " ��(�����鵵����).");
        logger.info(getBizName() + "�Զ��������ۡ����۽����д�������λ�д��¼����(����ʧ��)��" + failCnt + " ��(�����鵵����).");
    }


    //SBS������
    private synchronized void sbsBookAll() {
        try {
            //���� ���۳ɹ��ġ������˵ġ�����ʧ�ܵ�
            List<FipCutpaydetl> detlListSucc = billManagerService.selectBillList(this.bizType, BillStatus.CUTPAY_SUCCESS);
            //�Զ��������˴���
            billManagerService.updateCutpaydetlBillStatus(detlListSucc, BillStatus.ACCOUNT_PEND);

            List<FipCutpaydetl> detlList = billManagerService.selectBillList(this.bizType, BillStatus.ACCOUNT_PEND, BillStatus.ACCOUNT_FAILED);
            int succ = zmdService.accountCutPayRecord2SBS(detlList);
            logger.info(getBizName() + "�Զ��������ۡ�SBS���ˡ���" + succ + " ��.");
        } catch (Exception e) {
            //�����쳣ʱ�������쳣����������
            logger.info(getBizName() + "�Զ��������ۡ�SBS���ˡ����ִ���.", e);
            String sms = e.getMessage();
            if (sms == null) {
                sms = e.toString();
            }
            sms = sms.length() <= 100 ? sms : sms.substring(0, 100);
            SmsHelper.asyncSendSms(PropertyManager.getProperty("zmd_batch_phones"), "ר�������,SBS�����쳣:" + sms);
        }
    }


    //=============================================
    private boolean isCronTaskOpen() {
        try {
            return operationValve.isOpen("cron_task_mode");
        } catch (Exception e) {
            logger.error("��ȡ�����ļ�����", e);
            return false;
        }
    }

    private String getBizName() {
        if (BizType.ZMD.equals(bizType)) {
            return "ר����";
        }  else {
            return "ҵ�����ʹ���";
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
