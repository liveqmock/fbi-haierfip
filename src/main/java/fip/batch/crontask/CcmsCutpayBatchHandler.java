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
 * �������Ŵ���̨�Զ����۴���.  ��������
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
                    logger.info("�����Ŵ���������: ϵͳ�д���δ��ɽ��ȷ�ϵļ�¼:" + needQueryBatList.size());
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
                Thread.sleep(10 * 60 * 1000);  //ʮ���Ӻ���н����ѯ
            } catch (InterruptedException e) {
                //
            }


            performResultQueryTxn();
            writebackBills();
        } catch (Exception e) {
            logger.error("�����Ŵ��������۴���", e);
            //TODO �����¼�����ݿ����
        }
    }

    public synchronized void obtainBills() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("�Զ������������ѹرա�");
        }
        List<String> returnMsgs = new ArrayList<String>();
        int count = ccmsService.doObtainCcmsBills(BizType.XFNEW, BillType.NORMAL, returnMsgs);
        logger.info("�����Ŵ��Զ��������ۡ����ۼ�¼��ȡ�����λ�ȡ��¼����" + count + " ��.");
    }

    @Override
    public synchronized void performCutpayTxn() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("�Զ������������ѹرա�");
        }
        List<FipCutpaydetl> detlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.INIT);
        batchPkgService.packUnipayBatchPkg(bizType, detlList, channelBizType.getCode());
        List<FipCutpaybat> sendablePkgList = batchPkgService.selectSendableBatchs(bizType, CutpayChannel.UNIPAY, TxSendFlag.UNSEND);
        logger.info("�����Ŵ��Զ��������ۡ����ۼ�¼��������������");

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
                            logger.info("�����Ŵ��Զ��������ۡ����ۼ�¼���͡����ط�, ���ش��룺" + unipayRtnCode);
                        } else {
                            isSent = true;
                            logger.info("�����Ŵ��Զ��������ۡ����ۼ�¼���͡����������, ���ش��룺" + unipayRtnCode);
                        }
                    } else {
                        logger.error("�Զ�����������۽��׷��ͣ�������Ӧ��ϢΪ��, �����ط�������¼" + pkg.getTxpkgSn());
                        break;
                    }
                } catch (Exception e) {
                    logger.error("�Զ�����������۽��׷���ʧ��", e);
                    count++;
                    isSent = false;
                    logger.info("�����Ŵ��Զ��������ۡ����ۼ�¼���͡����ط�");
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
            throw new RuntimeException("�Զ������������ѹرա�");
        }

        List<FipCutpaybat> needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);

        //��������
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
        while (!isQryOver && count < 60) { //�ط�   3Сʱ
            try {
                String unipayRtnCode = unipayDepService.sendAndRecvCutpayT1003003Message(bat);
                count++;
                if (!StringUtils.isEmpty(unipayRtnCode)) {
                    if (unipayRtnCode.startsWith("0") || unipayRtnCode.startsWith("1")) {  //�޷���ѯ���ý��ף������ط�
                        isQryOver = true;  //��ȷ���سɹ���ʧ��
                        logger.info("�����Ŵ��Զ��������ۡ����۽����ѯ�����������, ���ش��룺" + unipayRtnCode);
                    } else {
                        isQryOver = false;
                        logger.info("�����Ŵ��Զ��������ۡ����۽����ѯ����δ������ȷ����������Ӻ������ѯ, ���ش��룺" + unipayRtnCode);
                        Thread.sleep(3 * 60 * 1000); //δ�����ȷ��� 3���Ӻ������ѯ
                    }
                } else {
                    logger.error("�Զ�����������۽��׽����ѯ��������Ӧ��ϢΪ��, �����ط���ѯ����" + bat.getTxpkgSn());
                    break;
                }
                //��ѯ10���Ժ� ÿ�ζ���д
                if (count >= 10) {
                    writebackBills();
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

    @Override
    public synchronized void writebackBills() {
        if (!isCronTaskOpen()) {
            throw new RuntimeException("�Զ������������ѹرա�");
        }
        List<FipCutpaydetl> successDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_SUCCESS);
        List<FipCutpaydetl> failureDetlList = billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.CUTPAY_FAILED);
        List<FipCutpaydetl> needQueryDetlList = billManagerService.selectRecords4UnipayBatchDetail(this.bizType, BillStatus.CUTPAY_QRY_PEND);


        int succCnt = ccmsService.writebackCutPayRecord2CCMS(successDetlList, true);
        int failCnt = ccmsService.writebackCutPayRecord2CCMS(failureDetlList, true);
        //��д���������¼ ���鵵
        int qryCnt = ccmsService.writebackCutPayRecord2CCMS(needQueryDetlList, false);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            //
        }
        logger.info("�����Ŵ��Զ��������ۡ����۽����д�������λ�д��¼����(���۳ɹ�)��" + succCnt + " ��(�����鵵����).");
        logger.info("�����Ŵ��Զ��������ۡ����۽����д�������λ�д��¼����(����ʧ��)��" + failCnt + " ��(�����鵵����).");
        logger.info("�����Ŵ��Զ��������ۡ����۽����д�������λ�д��¼����(���۽������)��" + qryCnt + " ��(δ���鵵����).");
    }

    @Override
    public void archiveBills() {
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
