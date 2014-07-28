package fip.service.fip;

import fip.common.constant.BillStatus;
import fip.gateway.JmsManager;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipPayoutbat;
import fip.repository.model.FipPayoutdetl;
import fip.repository.model.FipRefunddetl;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * �������� ͨ��DEP����.
 * User: zhanrui
 * Date: 12-3-14
 * Time: ����7:17
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
     * ���۽��� ���ʴ���  ͬ�����첽
     *
     * @param record
     */
    @Transactional
    public void sendAndRecvT1001001Message(FipCutpaydetl record) {
        try {
            TIA1001001 tia = new TIA1001001();
            initTiaHeader(tia, record.getOriginBizid(), record.getBatchSn(), record.getBatchDetlSn(), "1001001");
            assembleTia1001001Body(tia, record);
            jobLogService.checkAndUpdateRecversion(record);

            //ͨ��MQ������Ϣ��DEP
            TOA1001001 toa = (TOA1001001) JmsManager.getInstance().sendAndRecv(tia);
            processCutpayToa1001001(record, toa);
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }

    @Transactional
    private void processCutpayToa1001001(FipCutpaydetl record, TOA1001001 toa) {
        String response_sn = toa.header.REQ_SN;
        String request_sn = record.getBatchSn() + record.getBatchDetlSn();
        if (!response_sn.equals(APP_ID + request_sn)) {
            String s = "���ر������к�" + response_sn + "���������к�" + request_sn + "��ƥ�䡣";
            logger.error(s);
            throw new RuntimeException(s);
        }
        String rtnCode = toa.header.RETURN_CODE;
        String log = "���ݽ���ƽ̨���أ�[" + rtnCode + "] �������ؽ����" + toa.header.RETURN_MSG + "";

        //��ȡ����������
        String unipayRtnCode = "";
        String unipayRtnMsg = toa.header.RETURN_MSG;
        Pattern p = Pattern.compile("\\[(.*)\\]");
        Matcher m = p.matcher(unipayRtnMsg);
        if (m.find()) {
            unipayRtnCode = m.group(1);
        }

        if ("0000".equals(rtnCode)) {//���׳ɹ�
            BigDecimal amt = toa.body.AMOUNT;
            if (toa.body.ACCOUNT_NO.equals(record.getBiBankactno()) && amt.compareTo(record.getPaybackamt()) == 0) {
                record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                record.setDateBankCutpay(new Date());
            } else {
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                log = "��¼��ƥ��(�ʺŻ���)" + record.getClientname();
                logger.error("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
                //throw new RuntimeException("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
            }
        } else if ("1000".equals(rtnCode)) {  //����ʧ��
            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(unipayRtnMsg);
            log = "�������أ�" + toa.header.RETURN_MSG;
        } else if ("2000".equals(rtnCode)) {  //���ײ���
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(unipayRtnMsg);
            log = "�������أ�" + toa.header.RETURN_MSG;
        } else { // TODO Ӧ�����쳣���
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(toa.header.RETURN_MSG);
            log = "�������أ�" + toa.header.RETURN_MSG;
        }
        cutpaydetlMapper.updateByPrimaryKey(record);
        jobLogService.insertNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", log, "���ݽ���ƽ̨", "���ݽ���ƽ̨");
    }

    /**
     * �������� ���ʴ���  ͬ�����첽
     *
     * @param record
     */
    @Transactional
    public void sendAndRecvT1001002Message(FipRefunddetl record) {
        try {
            TIA1001002 tia = new TIA1001002();
            initTiaHeader(tia, record.getOriginBizid(), record.getBatchSn(), record.getBatchDetlSn(), "1001002");
            assembleTia1001002Body(tia, record);
            jobLogService.checkAndUpdateRecversion4Refund(record);

            //ͨ��MQ������Ϣ��DEP
            TOA1001002 toa = (TOA1001002) JmsManager.getInstance().sendAndRecv(tia);
            processRefundToa1001002(record, toa);
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }

    /**
     * ������������ ���ʴ���
     */
    public TOA1002001 sendAndRecvT1002001Message(FipPayoutbat bat, FipPayoutdetl detl) {
        try {
            TIA1002001 tia = new TIA1002001();
            initTiaHeader(tia, bat.getWsysId().toUpperCase(), detl.getReqSn(), detl.getSn(), "1002001");
            assembleTia1002001Body(tia, bat, detl);
//          TODO log  jobLogService.checkAndUpdateRecversion4Refund(record);
            //ͨ��MQ������Ϣ��DEP
            TOA1002001 toa = (TOA1002001) JmsManager.getInstance().sendAndRecv(tia);
            String response_sn = toa.header.REQ_SN;
            String request_sn = APP_ID + detl.getReqSn() + detl.getSn();
            if (!response_sn.equals(request_sn)) {
                String s = "���ر������к�" + response_sn + "���������к�" + request_sn + "��ƥ�䡣";
                logger.error(s);
                throw new RuntimeException(s);
            }
            return toa;
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }

    public TOA1003001 sendAndRecvPayoutT1003001Message(FipPayoutbat bat, FipPayoutdetl detl) {
        try {
            TIA1003001 tia = new TIA1003001();
            initTiaHeader(tia, bat.getWsysId().toUpperCase(), detl.getReqSn(), detl.getSn(), "1003001");
            assemblePayoutTia1003001Body(tia, detl);
            tia.getHeader().REQ_SN = APP_ID + new SimpleDateFormat("yyyyMMddHHmmssss").format(new Date());
//          todo log  jobLogService.checkAndUpdateRecversion(record);
            //ͨ��MQ������Ϣ��DEP
            TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia);
            return toa;
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }

    @Transactional
    private void processRefundToa1001002(FipRefunddetl record, TOA1001002 toa) {
        String response_sn = toa.header.REQ_SN;
        String request_sn = record.getBatchSn() + record.getBatchDetlSn();
        if (!response_sn.equals(APP_ID + request_sn)) {
            String s = "���ر������к�" + response_sn + "���������к�" + request_sn + "��ƥ�䡣";
            logger.error(s);
            throw new RuntimeException(s);
        }
        String rtnCode = toa.header.RETURN_CODE;
        String log = "���ݽ���ƽ̨���أ�[" + rtnCode + "] �������ؽ����" + toa.header.RETURN_MSG + "";
        if ("0000".equals(rtnCode)) {//���׳ɹ�
            BigDecimal amt = toa.body.AMOUNT;
            if (toa.body.ACCOUNT_NO.equals(record.getBiBankactno()) && amt.compareTo(record.getPayamt()) == 0) {
                record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                record.setDateBankPay(new Date());
            } else {
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                log = "��¼��ƥ��(�ʺŻ���)" + record.getClientname();
                logger.error("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
                //throw new RuntimeException("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
            }
        } else if ("1000".equals(rtnCode)) {  //����ʧ��
            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            log = "�������أ�" + toa.header.RETURN_MSG;
        } else if ("2000".equals(rtnCode)) {  //���ײ���
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            log = "�������أ�" + toa.header.RETURN_MSG;
        } else { // TODO Ӧ�����쳣���
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            log = "�������أ�" + toa.header.RETURN_MSG;
        }
        refunddetlMapper.updateByPrimaryKey(record);
        jobLogService.insertNewJoblog(record.getPkid(), "fip_refunddetl", "��������", log, "���ݽ���ƽ̨", "���ݽ���ƽ̨");
    }

    /**
     * ��ѯ���׽�� 1003001
     *
     * @param record
     */
    @Transactional
    public void sendAndRecvCutpayT1003001Message(FipCutpaydetl record) {
        try {
            TIA1003001 tia = new TIA1003001();
            initTiaHeader(tia, record.getOriginBizid(), record.getBatchSn(), record.getBatchDetlSn(), "1003001");
            assembleCutpayTia1003001Body(tia, record);
            jobLogService.checkAndUpdateRecversion(record);

            //ͨ��MQ������Ϣ��DEP
            //TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia);
            TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia, 15000); //�Զ��峬ʱʱ�� ע�⣺�������mq����������������Ϣ
            processCutpayToa1003001(record, toa);
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }

    @Transactional
    public void sendAndRecvRefundT1003001Message(FipRefunddetl record) {
        try {
            TIA1003001 tia = new TIA1003001();
            initTiaHeader(tia, record.getOriginBizid(), record.getBatchSn(), record.getBatchDetlSn(), "1003001");
            assembleRefundTia1003001Body(tia, record);
            jobLogService.checkAndUpdateRecversion4Refund(record);
            //ͨ��MQ������Ϣ��DEP
            TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia);
            processRefundToa1003001(record, toa);
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }


    @Transactional
    private void processCutpayToa1003001(FipCutpaydetl record, TOA1003001 toa) {
        String response_sn = toa.header.REQ_SN;
        String request_sn = record.getBatchSn() + record.getBatchDetlSn();
        if (!response_sn.equals(APP_ID + request_sn)) {
            String s = "���ر������к�" + response_sn + "���������к�" + request_sn + "��ƥ�䡣";
            logger.error(s);
            throw new RuntimeException(s);
        }
        String rtnCode = toa.header.RETURN_CODE;
        String log = "���ݽ���ƽ̨���أ�[" + rtnCode + "] �������ؽ����" + toa.header.RETURN_MSG + "";

        //��ȡ����������
        String unipayRtnCode = "";
        String unipayRtnMsg = toa.header.RETURN_MSG;
        Pattern p = Pattern.compile("\\[(.*?)\\]");
        Matcher m = p.matcher(unipayRtnMsg);
        if (m.find()) {
            unipayRtnCode = m.group(1);
        }

        if ("0000".equals(rtnCode)) {//���׳ɹ�
            BigDecimal amt = toa.body.AMOUNT;
            if (toa.body.ACCOUNT_NO.equals(record.getBiBankactno()) && amt.compareTo(record.getPaybackamt()) == 0) {
                record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                record.setDateBankCutpay(new Date());
                record.setTxRetcode("0000");
            } else {
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                log = "��¼��ƥ��(�ʺŻ���)" + record.getClientname();
                logger.error("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
                //throw new RuntimeException("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
            }
        } else if ("1000".equals(rtnCode)) {  //����ʧ��
            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(unipayRtnMsg);
            log = "�������أ�" + unipayRtnMsg;
        } else if ("2000".equals(rtnCode)) {  //���ײ���
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(unipayRtnMsg);
            log = "�������أ�" + unipayRtnMsg;
        } else { // TODO Ӧ�����쳣���
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            record.setTxRetcode(unipayRtnCode);
            record.setTxRetmsg(toa.header.RETURN_MSG);
            log = "�������أ�" + unipayRtnMsg;
        }
        cutpaydetlMapper.updateByPrimaryKey(record);
        jobLogService.insertNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", log, "���ݽ���ƽ̨", "���ݽ���ƽ̨");
    }

    @Transactional
    private void processRefundToa1003001(FipRefunddetl record, TOA1003001 toa) {
        String response_sn = toa.header.REQ_SN;
        String request_sn = record.getBatchSn() + record.getBatchDetlSn();
        if (!response_sn.equals(APP_ID + request_sn)) {
            String s = "���ر������к�" + response_sn + "���������к�" + request_sn + "��ƥ�䡣";
            logger.error(s);
            throw new RuntimeException(s);
        }
        String rtnCode = toa.header.RETURN_CODE;
        String log = "���ݽ���ƽ̨���أ�[" + rtnCode + "] �������ؽ����" + toa.header.RETURN_MSG + "";
        if ("0000".equals(rtnCode)) {//���׳ɹ�
            BigDecimal amt = toa.body.AMOUNT;
            if (toa.body.ACCOUNT_NO.equals(record.getBiBankactno()) && amt.compareTo(record.getPayamt()) == 0) {
                record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                record.setDateBankPay(new Date());
            } else {
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                log = "��¼��ƥ��(�ʺŻ���)" + record.getClientname();
                logger.error("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
            }
        } else if ("1000".equals(rtnCode)) {  //����ʧ��
            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            log = "�������أ�[" + toa.header.RETURN_MSG + "]";
        } else if ("2000".equals(rtnCode)) {  //���ײ���
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            log = "�������أ�[" + toa.header.RETURN_MSG + "]";
        } else { // TODO Ӧ�����쳣���
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            log = "�������أ�[" + toa.header.RETURN_MSG + "]";
        }
        refunddetlMapper.updateByPrimaryKey(record);
        jobLogService.insertNewJoblog(record.getPkid(), "fip_refunddetl", "��������", log, "���ݽ���ƽ̨", "���ݽ���ƽ̨");
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
        tia.body.ACCOUNT_PROP = "0"; // ����
        tia.body.ACCOUNT_TYPE = "00"; //00���п���01���ۡ�����Ĭ��Ϊ���п�00��
        tia.body.AMOUNT = record.getPaybackamt().toString();
        tia.body.BANK_CODE = record.getBiActopeningbank();
        tia.body.CITY = record.getBiCity();
        tia.body.PROVINCE = record.getBiProvince();

        //------
        //20121023 zhanrui ���֤����ʱ���ڱ�ע�У�����DEP�еı��Ĵ���
        //tia.body.REMARK = "HAIERFIP";
        tia.body.REMARK = record.getClientid();
        //-------
    }

    private void assembleTia1001002Body(TIA1001002 tia, FipRefunddetl record) {
        tia.body.ACCOUNT_NO = record.getBiBankactno();
        tia.body.ACCOUNT_NAME = record.getBiBankactname();
        tia.body.ACCOUNT_PROP = "0"; // ����
        tia.body.ACCOUNT_TYPE = "00"; //00���п���01���ۡ�����Ĭ��Ϊ���п�00��
        tia.body.AMOUNT = record.getPayamt().toString();
        tia.body.BANK_CODE = record.getBiActopeningbank();
        tia.body.CITY = record.getBiCity();
        tia.body.PROVINCE = record.getBiProvince();

        //------
        //20121023 zhanrui ���֤����ʱ���ڱ�ע�У�����DEP�еı��Ĵ���
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
        bodyDetail.ACCOUNT_PROP = detl.getAccountProp(); // ����
        bodyDetail.ACCOUNT_TYPE = detl.getAccountType(); //00���п���01���ۡ�����Ĭ��Ϊ���п�00��
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
}
