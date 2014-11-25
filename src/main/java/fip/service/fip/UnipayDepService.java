package fip.service.fip;

import fip.common.constant.BillStatus;
import fip.common.constant.TxSendFlag;
import fip.common.constant.TxpkgStatus;
import fip.gateway.JmsManager;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
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
import java.util.List;
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
     * �����ѯ���� 1003001
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


    //=======================================================================================
    //20140808  �������۽��� 1001003   zhanrui
    public synchronized String sendAndRecvT1001003Message(FipCutpaybat batchRecord) {
        String txPkgSn = batchRecord.getTxpkgSn();
        List<FipCutpaydetl> detailRecords = billManagerService.checkToMakeSendableRecords(txPkgSn);

        TIA1001003 tia = new TIA1001003();

        //����ͷ
        tia.getHeader().APP_ID = APP_ID;
        tia.getHeader().BIZ_ID = batchRecord.getChannelBizid();
        tia.getHeader().CHANNEL_ID = "100";
        tia.getHeader().USER_ID = DEP_USERNAME;
        tia.getHeader().PASSWORD = DEP_PWD;
        tia.getHeader().REQ_SN = batchRecord.getOriginBizid() + "-B-" + batchRecord.getTxpkgSn();
        tia.getHeader().TX_CODE = "1001003";

        //������
        BigDecimal totalAmt = new BigDecimal(0);
        for (FipCutpaydetl detailRecord : detailRecords) {
            TIA1001003.Body.BodyDetail item = new TIA1001003.Body.BodyDetail();
            item.SN = detailRecord.getTxpkgDetlSn();
            item.BANK_CODE = detailRecord.getBiActopeningbank();
            item.ACCOUNT_TYPE = ""; //�˺����� ���п������
            item.ACCOUNT_NO = detailRecord.getBiBankactno();
            item.ACCOUNT_NAME = detailRecord.getBiBankactname();
            item.PROVINCE = detailRecord.getBiProvince();
            item.CITY = detailRecord.getBiCity();
            item.ACCOUNT_PROP = "0"; // ����
            item.AMOUNT = String.valueOf(detailRecord.getPaybackamt());
            item.ID_TYPE = detailRecord.getClientidtype();
            item.ID = detailRecord.getClientid();

            tia.body.TRANS_DETAILS.add(item);
            totalAmt = totalAmt.add(detailRecord.getPaybackamt());
        }
        tia.body.TRANS_SUM.TOTAL_ITEM = String.valueOf(detailRecords.size());
        tia.body.TRANS_SUM.TOTAL_SUM = String.valueOf(totalAmt);

        //����batch��¼��״̬  ����Ϊ�������н����ѯ��
        FipCutpaybat originBatRecord = cutpaybatMapper.selectByPrimaryKey(batchRecord.getTxpkgSn());
        if (originBatRecord.getRecversion().compareTo(batchRecord.getRecversion()) != 0) {
            throw new RuntimeException("�������³�ͻ,�������=" + batchRecord.getTxpkgSn());
        } else {
            batchRecord.setRecversion(batchRecord.getRecversion() + 1);
            batchRecord.setTxpkgStatus(TxpkgStatus.QRY_PEND.getCode());
            cutpaybatMapper.updateByPrimaryKey(batchRecord);
        }

        TOA1001003 toa;
        try {
            //ͨ��MQ������Ϣ��DEP
            toa = (TOA1001003) JmsManager.getInstance().sendAndRecv(tia);
        } catch (Exception e) {
            throw new RuntimeException("MQ����ʧ��", e);
        }

        billManagerService.updateCutpaydetlListToSendflag(detailRecords, TxSendFlag.SENT.getCode());
        billManagerService.updateCutpaybatToSendflag(txPkgSn, TxSendFlag.SENT.getCode());
        return processCutpayToa1001003(batchRecord, detailRecords, toa);
    }

    private String  processCutpayToa1001003(FipCutpaybat batchRecord, List<FipCutpaydetl> detailRecords, TOA1001003 toa) {
        //String batchSN = toa.header.REQ_SN;
        String batchSN = batchRecord.getTxpkgSn();
        if (!toa.header.REQ_SN.endsWith(batchSN)) {
            throw new RuntimeException("�Ұ�����Ӧ���������������кŲ�һ��!");
        }

        String headRetCode = toa.header.RETURN_CODE;
        String headRetMsg = toa.header.RETURN_MSG;
        jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "��������", "[" + headRetCode + "]" + headRetMsg, "���ݽ���ƽ̨", "���ݽ���ƽ̨");
        if (headRetCode.equals("0000")) {  //�������
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        } else if (headRetCode.startsWith("1")) {   //����ʧ��  '1'��ͷ��ʾ�������д����������ϵͳ���ͱ��ĳ���
            //���������еļ�¼Ϊ ������ʧ�ܡ�
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_FAIL);
            //���ö�Ӧ����ϸ��¼�ı�־Ϊ������
            //resetDetailRecordStatusByBatchQueryResult(batchSN, headRetCode, headRetMsg);
        } else { //����״̬������ ��������ѯ
            //�������� Ӧ������ѯ��
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        }
        logger.debug(" ..........�����ص���Ϣ����........");
        return headRetCode;

        //TODO  ������ϸ��¼�� ������Ӧ��Ϣ
    }

    private void setCutpaybatRecordStatus(String headSn, TxpkgStatus status) {
        FipCutpaybat fipCutpaybat = cutpaybatMapper.selectByPrimaryKey(headSn);
        fipCutpaybat.setTxpkgStatus(status.getCode());
        fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
        cutpaybatMapper.updateByPrimaryKey(fipCutpaybat);
    }


    /**
     * ���������ѯ���� 1003003
     */
    public synchronized String sendAndRecvCutpayT1003003Message(FipCutpaybat batchRecord) {
        TOA1003003 toa;
        try {
            TIA1003003 tia = new TIA1003003();
            //����ͷ
            tia.getHeader().APP_ID = APP_ID;
            tia.getHeader().BIZ_ID = batchRecord.getChannelBizid();
            tia.getHeader().CHANNEL_ID = "100";
            tia.getHeader().USER_ID = DEP_USERNAME;
            tia.getHeader().PASSWORD = DEP_PWD;
            tia.getHeader().REQ_SN = batchRecord.getOriginBizid() + "-B-" + batchRecord.getTxpkgSn();
            tia.getHeader().TX_CODE = "1003003";

            //������
            tia.body.QUERY_SN = batchRecord.getOriginBizid() + "-B-" + batchRecord.getTxpkgSn();
            tia.body.REMARK = "";

            // /TODO ��
            // jobLogService.checkAndUpdateRecversion(batchRecord);

            //ͨ��MQ������Ϣ��DEP
            //TOA1003001 toa = (TOA1003001) JmsManager.getInstance().sendAndRecv(tia);
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "�������׽����ѯ", "��������ѯ��DEP:1003003������", "���ݽ���ƽ̨", "���ݽ���ƽ̨");
            toa = (TOA1003003) JmsManager.getInstance().sendAndRecv(tia, 15000);
        } catch (Exception e) {
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "�������׽����ѯ", "��������MQ����ʧ��, ��鿴��־", "���ݽ���ƽ̨", "���ݽ���ƽ̨");
            throw new RuntimeException("MQ��Ϣ����ʧ��" + e.getMessage(), e);
        }

        try {
            return processCutpayToa1003003(batchRecord, toa);
        } catch (Exception e) {
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "�������׽����ѯ", "���������ѯT1003003��Ӧ���Ĵ���ʧ��", "���ݽ���ƽ̨", "���ݽ���ƽ̨");
            throw new RuntimeException("���������ѯT1003003��Ӧ���Ĵ���ʧ��" + e.getMessage(), e);
        }
    }


    private String processCutpayToa1003003(FipCutpaybat batchRecord, TOA1003003 toa) {
        //�����Ӧ�����Ƿ������������Ӧ
        String reqSn = batchRecord.getOriginBizid() + "-B-" + batchRecord.getTxpkgSn();

        if (!reqSn.equals(toa.getHeader().REQ_SN)) {
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "�������׽����ѯ", "��Ӧ�����Ұ�����Ӧ�����������Ĳ���Ӧ��" , "���ݽ���ƽ̨", "���ݽ���ƽ̨");
            throw new RuntimeException("�Ұ�����Ӧ�����������Ĳ���Ӧ��");
        }

        //����ѯSN��һ����
        String querySn = reqSn;
        if (!querySn.equals(toa.body.QUERY_SN)) {
            jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "�������׽����ѯ", "��Ӧ�����еĲ�ѯ���кŴ���" , "���ݽ���ƽ̨", "���ݽ���ƽ̨");
            throw new RuntimeException("��Ӧ�����еĲ�ѯ���кŴ���");
        }

        //����TOAͷ��������
        String headRtnCode = toa.header.RETURN_CODE;
        jobLogService.insertNewJoblog(batchRecord.getTxpkgSn(), "fip_cutpaybat", "�������׽����ѯ", "��Ӧ���ģ�[" +  headRtnCode + "]" + toa.header.RETURN_MSG, "���ݽ���ƽ̨", "���ݽ���ƽ̨");

        //�ȴ��������е���ϸ����
        processTOA1003003Body(batchRecord, toa);

        //�ٴ�����ͷ��Ӧ�� cutpaybat��״̬
        if ("0000".equals(headRtnCode)) {
            setCutpaybatRecordStatus(batchRecord.getTxpkgSn(), TxpkgStatus.DEAL_SUCCESS);
        } else if (headRtnCode.startsWith("1")) { //ʧ�ܣ������ط������ط�
            /*
            1000	�������ݼ�����ߴ�����������ݼ����ش�����Ϣ��
            1001	���Ľ��ʹ�
            1002	�޷���ѯ���ý��ף������ط�
             */
            setCutpaybatRecordStatus(batchRecord.getTxpkgSn(), TxpkgStatus.DEAL_FAIL);
        } else if (headRtnCode.startsWith("2")) { //������
            //
        } else {
           //
        }

        return headRtnCode;
    }

    private void processTOA1003003Body(FipCutpaybat batchRecord, TOA1003003 toa) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        for (TOA1003003.Body.BodyDetail bodyDetail : toa.body.RET_DETAILS) {
            String detailSn = bodyDetail.SN;

            example.clear();
            example.createCriteria().andTxpkgSnEqualTo(batchRecord.getTxpkgSn()).andTxpkgDetlSnEqualTo(detailSn);
            List<FipCutpaydetl> cutpaydetlList = cutpaydetlMapper.selectByExample(example);
            FipCutpaydetl record = cutpaydetlList.get(0);

            String retCode = bodyDetail.RET_CODE;
            String retMsg = bodyDetail.ERR_MSG;

            //����Ƿ��Ѿ���ѯ��
            if (!StringUtils.isEmpty(record.getTxRetcode())) {
                if (record.getTxRetcode().equals(retCode)) {
                    continue; //�Թ�
                }
            }

            record.setTxRetcode(retCode);
            record.setTxRetmsg(retMsg);

            String txRetMsg = "[" + bodyDetail.ERR_MSG + "]";
            record.setTxRetmsg(txRetMsg);
            record.setTxRetcode(retCode);

            if ("0000".equals(retCode)) { //ҵ��ɹ�
                if (bodyDetail.ACCOUNT_NO.endsWith(record.getBiBankactno())
                        && bodyDetail.AMOUNT.compareTo(record.getPaybackamt()) == 0) {
                    record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    record.setDateBankCutpay(new Date());
                } else {
                    record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                    txRetMsg = "��¼��ƥ��(�ʺŻ���)" + record.getClientname();
                    record.setTxRetcode("XXXX");
                    record.setTxRetmsg(txRetMsg);
                    logger.error("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
                }
            } else if (retCode.startsWith("1")) { //1��ͷ��ʾ�������д��������˾ϵͳ���ͱ��ĳ����̻�ϵͳ��Ҫ��ȷ����ã�
                record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            } else if (retCode.startsWith("2")) { //2��ͷ��ʾ�����м䴦��״̬
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            } else {
                record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            }

            record.setRecversion(record.getRecversion() + 1);
            String log = "[��������+���:" + record.getTxpkgSn() + "_" + record.getTxpkgDetlSn() + "]" + record.getTxRetmsg();
            //appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", log);
            jobLogService.insertNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", log, "���ݽ���ƽ̨", "���ݽ���ƽ̨");

            cutpaydetlMapper.updateByPrimaryKey(record);
        }
    }

}
