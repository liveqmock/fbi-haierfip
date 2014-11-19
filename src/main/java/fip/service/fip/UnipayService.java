package fip.service.fip;

import fip.common.constant.BillStatus;
import fip.common.constant.TxSendFlag;
import fip.common.constant.TxpkgStatus;
import fip.gateway.unionpay.CreateMessageHandler;
import fip.gateway.unionpay.RtnCodeEnum;
import fip.gateway.unionpay.domain.*;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipCutpaydetlExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * ���۲���ֱ��������ʽ
   201401 zr �ѷ��� ���ڲ���DEPͨ����ʽ
 * User: zhanrui
 * Date: 11-8-24
 * Time: ����7:17
 */
@Service
@Deprecated
public class UnipayService {
    private static final Logger logger = LoggerFactory.getLogger(UnipayService.class);

    private static String DEP_CHANNEL_ID_UNIPAY = "100";

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
    FipCutpaybatMapper cutpaybatMapper;


    /**
     * ���͵��ʴ��������� (10004 ʵʱ���۱���)
     * ����ǰ���汾��
     * Ϊ��ֹ�ظ�����  ����ʽ����ǰֱ����״̬Ϊ ����ѯ
     *
     * @param record
     */

    @Transactional
    @Deprecated
    public void sendAndRecvRealTimeTxnMessage(FipCutpaydetl record) {
        try {
            String msgtxt = CreateMessageHandler.getInstance().create100004Msg(record);

            FipCutpaydetl originRecord = cutpaydetlMapper.selectByPrimaryKey(record.getPkid());
            if (!originRecord.getRecversion().equals(record.getRecversion())) {
                throw new RuntimeException("�������³�ͻ,UUID=" + record.getPkid());
            } else {
                record.setRecversion(record.getRecversion() + 1);
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                cutpaydetlMapper.updateByPrimaryKey(record);
            }
            //ͨ��MQ������Ϣ��DEP
            String msgid = depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, record.getOriginBizid());
            handle100004Message(depService.recvDepMessage(msgid));
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }

    /**
     * ��������100004ʵʱ�ۿ�׽��
     * ע�⣺����100004���ױ���ı��ĸ�ʽ֧������������Ŀǰ��20110901���˱���ֻ�����ʷ��ʹ���
     * ���ʴ������ˮ�� �� fip_cutpaybat���޹� ��������ʱ����ˮ������������ɣ�BATCH_SN + BATCH_DETL_SN
     *
     * @param message
     */
    @Deprecated
    @Transactional
    public void handle100004Message(String message) {
        logger.info(" ========��ʼ�����ص�100004��Ϣ==========");
        logger.info(message);

        T100004Toa toa = T100004Toa.getToa(message);
        if (toa != null) {
            String retcode_head = toa.INFO.RET_CODE;      //����ͷ������
            String req_sn = toa.INFO.REQ_SN;              //������ˮ��
            String batch_sn = req_sn.substring(0, 11);    //����������ˮ�� �õ����κ�
            String batch_detl_sn = req_sn.substring(11);  //����������ˮ�� �õ������ڵ�˳���
            FipCutpaydetlExample example = new FipCutpaydetlExample();
            example.createCriteria().andBatchSnEqualTo(batch_sn).andBatchDetlSnEqualTo(batch_detl_sn)
                    .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0");
            List<FipCutpaydetl> cutpaydetlList = cutpaydetlMapper.selectByExample(example);
            if (cutpaydetlList.size() != 1) {
                logger.error("δ���ҵ���Ӧ�Ŀۿ��¼��" + req_sn);
                throw new RuntimeException("δ���ҵ���Ӧ�Ŀۿ��¼��" + req_sn);
            }
            FipCutpaydetl record = cutpaydetlList.get(0);

            if ("0000".equals(retcode_head)) { //����ͷ��0000�����������
                //�Ѳ��ҵ����ݿ��ж�Ӧ�ļ�¼�����Խ�����־��¼
                T100004Toa.Body.BodyDetail bodyDetail = toa.BODY.RET_DETAILS.get(0);
                String retcode_detl = bodyDetail.RET_CODE;
                if ("0000".equals(retcode_detl)) { //���׳ɹ���Ψһ��־
                    if (bodyDetail.ACCOUNT_NO.equals(record.getBiBankactno())) {
                        long recordAmt = record.getPaybackamt().multiply(new BigDecimal(100)).longValue();
                        long returnAmt = Integer.parseInt(bodyDetail.AMOUNT);
                        if (recordAmt == returnAmt) {
                            record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                            record.setDateBankCutpay(new Date());
                        } else {
                            logger.error("���ؽ�ƥ��");
                            appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", "���ؽ�ƥ��:" + returnAmt);
                        }
                    } else {
                        logger.error("�ʺŲ�ƥ��");
                        appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", "�ʺŲ�ƥ��" + bodyDetail.ACCOUNT_NO);
                    }
                } else {  //����ʧ��
                    record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                }
                record.setTxRetcode(String.valueOf(retcode_detl));
                record.setTxRetmsg(bodyDetail.ERR_MSG);
            } else if ("1002".equals(retcode_head)) {//�޷���ѯ���ý��ף������ط�  �ؼ���
                record.setBillstatus(BillStatus.RESEND_PEND.getCode());
                record.setTxRetcode(String.valueOf(retcode_head));
                record.setTxRetmsg(toa.INFO.ERR_MSG);
            } else { //����ѯ (TODO: δ���� 0001��0002)
                record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                record.setTxRetcode(String.valueOf(retcode_head));
                record.setTxRetmsg(toa.INFO.ERR_MSG);
            }
            record.setRecversion(record.getRecversion() + 1);
            cutpaydetlMapper.updateByPrimaryKey(record);
        } else { //
            throw new RuntimeException("�ñʽ��׼�¼Ϊ�գ������ѱ�ɾ���� " + message);
        }
        logger.debug(" ................. �����ص���Ϣ����........");
    }

    /**
     * ������������������  100001 �Լ� 100004
     * ����ǰ���汾��
     * Ϊ��ֹ�ظ�����  ����ʽ����ǰֱ����״̬Ϊ ����ѯ
     *
     * @param batchRecord 1
     */

    @Transactional
    @Deprecated
    public void sendAndRecvBatchTxnMessage(FipCutpaybat batchRecord) {
        try {
            String txPkgSn = batchRecord.getTxpkgSn();
            List<FipCutpaydetl> sendRecords = billManagerService.checkToMakeSendableRecords(txPkgSn);

            Map paramMap = new HashMap();
            paramMap.put("batBean", batchRecord);
            paramMap.put("detlList", sendRecords);
            String msgtxt;
            if ("SYNC".equals(batchRecord.getTxntype())) { //ͬ������ ��ʵʱ���׽ӿ�
                //msgtxt = CreateMessageHandler.getInstance().create100004Msg(batchRecord, sendRecords);
                TIA100004 tia100004 = new TIA100004(paramMap);
                msgtxt = tia100004.toXml(batchRecord.getOriginBizid());
            } else if ("ASYNC".equals(batchRecord.getTxntype())) {
                TIA100001 tia100001 = new TIA100001(paramMap);
                msgtxt = tia100001.toXml(batchRecord.getOriginBizid());
                //msgtxt = CreateMessageHandler.getInstance().create100001Msg(batchRecord, sendRecords);
            } else {
                throw new RuntimeException("�������� TXNTYPE ���ô���");
            }

            FipCutpaybat originBatRecord = cutpaybatMapper.selectByPrimaryKey(batchRecord.getTxpkgSn());
            if (originBatRecord.getRecversion().compareTo(batchRecord.getRecversion())!=0) {
                throw new RuntimeException("�������³�ͻ,�������=" + batchRecord.getTxpkgSn());
            } else {
                batchRecord.setRecversion(batchRecord.getRecversion() + 1);
                batchRecord.setTxpkgStatus(TxpkgStatus.QRY_PEND.getCode());
                cutpaybatMapper.updateByPrimaryKey(batchRecord);
            }
            //ͨ��MQ������Ϣ
            String msgid = depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, batchRecord.getOriginBizid());
            billManagerService.updateCutpaydetlListToSendflag(sendRecords, TxSendFlag.SENT.getCode());
            billManagerService.updateCutpaybatToSendflag(txPkgSn, TxSendFlag.SENT.getCode());

            if ("SYNC".equals(batchRecord.getTxntype())) { //ͬ������ ��ʵʱ���׽ӿ�
                //handle100004Message(depService.recvDepMessage(msgid));
                TOA100004 toa100004 = TOA100004.getToa(depService.recvDepMessage(msgid));
                handleTOA100004(toa100004);
            } else {
                TOA100001 toa100001 = TOA100001.getToa(depService.recvDepMessage(msgid));
                handleTOA100001(toa100001);
                //handle100001Message(depService.recvDepMessage(msgid));
            }
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }

    /**
     * ���������ۿ��ʵʱ���ر��� ����ϸ��¼�Ľ����Ҫ����
     *
     * @param message
     */
    @Transactional
    @Deprecated
    public void handle100001Message(String message) {
        logger.debug(" ................. ��ʼ�����ص�100001��Ϣ........");
        logger.info(message);
        T100001Toa toa = T100001Toa.getToa(message);
        if (toa != null) {
            String msgRetCode = toa.INFO.RET_CODE;
            boolean isReset = false;
            if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100001_FAILE.getValues()).contains(msgRetCode)) {
                // ��� ����
                isReset = true;
            }
            boolean isBatchOver = true;
            String txpkgSn = toa.INFO.REQ_SN;
            if (toa.BODY.RET_DETAILS != null && !toa.BODY.RET_DETAILS.isEmpty()) {
                for (T100001Toa.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
                    FipCutpaydetlExample example = new FipCutpaydetlExample();
                    example.createCriteria().andTxpkgSnEqualTo(txpkgSn).andTxpkgDetlSnEqualTo(detail.SN);
                    FipCutpaydetl record = cutpaydetlMapper.selectByExample(example).get(0);
                    String log = null;
                    if (isReset) {
                        record.setTxpkgSn("");
                        record.setTxpkgDetlSn("");
                        record.setSendflag(TxSendFlag.UNSEND.getCode());
                        record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                        record.setTxRetmsg("[ϵͳʧ�ܣ��������]");
                        record.setTxRetcode("");
                    } else {
                        String retCode = detail.RET_CODE;
                        // ����ʧ��
                        if (Arrays.asList(RtnCodeEnum.UNIONPAY_TRX_CODE_100001_FAILE.getValues()).contains(retCode)) {
                            record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                            record.setTxRetmsg("[����ͷ��Ϣ]:" + msgRetCode + toa.INFO.ERR_MSG + " [��������Ϣ]:" + detail.ERR_MSG);
                            record.setTxRetcode(String.valueOf(retCode));
                        } else {
                            isBatchOver = false;
                            // ���������Ϊ���׽����������ѯ
                            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
                            record.setTxRetmsg("[���ͳɹ������׽������������ѯ��]");
                            record.setTxRetcode(String.valueOf(retCode));
                        }
                    }
                    record.setRecversion(record.getRecversion() + 1);
                    log = "������������(100001)���: [��������+���:" + record.getTxpkgSn() + record.getBatchDetlSn() + "]" + record.getTxRetmsg();
                    appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", log);
                    cutpaydetlMapper.updateByPrimaryKey(record);
                }
            }
            if (isBatchOver) {
                setCutpaybatRecordStatus(txpkgSn, TxpkgStatus.DEAL_SUCCESS);
            }
            logger.debug(" ................. �����ص���Ϣ����........");
        } else {
            throw new RuntimeException("xml����ת��Ϊ����ʱת������");
        }
    }


    @Deprecated
    public void sendAndRecvRealTimeDatagramQueryMessage(FipCutpaydetl record) {
        try {
            String msgtxt = CreateMessageHandler.getInstance().create200001Msg(record);
            String msgid = depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, record.getOriginBizid());
            //handle200001Message(depService.recvDepMessage(msgid));
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }


    //============================================================================================

    /**
     * ���ʹ��۲�ѯ���� for ����
     *
     * @param batchRecord FipCutpaybat
     */

    public void sendAndRecvBatchDatagramQueryMessage(FipCutpaybat batchRecord) {
        try {
            Map paramMap = new HashMap();
            paramMap.put("reqSN", batchRecord.getTxpkgSn());

            TIA200001 tia200001 = new TIA200001(paramMap);
            String msgtxt = tia200001.toXml(batchRecord.getOriginBizid());

            String msgid = depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, batchRecord.getOriginBizid());
            TOA200001 toa200001 = TOA200001.getToa(depService.recvDepMessage(msgid));
            handleTOA200001(toa200001);
        } catch (Exception e) {
            logger.error("MQ��Ϣ����ʧ��", e);
            throw new RuntimeException("MQ��Ϣ����ʧ��", e);
        }
    }


    @Transactional
    public void handleTOA100001(TOA100001 toa) {
        String batchSN = toa.INFO.REQ_SN;
        String headRetCode = toa.INFO.RET_CODE;
        String errMsg = toa.INFO.ERR_MSG;
        appendNewJoblog(batchSN, "fip_cutpaybat", "��������", "[������:" + headRetCode + "][��Ϣ:]" + errMsg);
        if (headRetCode.equals("0000")) {  //�������
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        } else if (headRetCode.startsWith("1")) {   //����ʧ��  '1'��ͷ��ʾ�������д����������ϵͳ���ͱ��ĳ���
            //���������еļ�¼Ϊ ������ʧ�ܡ�
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_FAIL);
            //���ö�Ӧ����ϸ��¼�ı�־Ϊ������
            //resetDetailRecordStatusByBatchQueryResult(batchSN, headRetCode, errMsg);
        } else { //����״̬������ ��������ѯ  (TODO: δ���� 0001��0002)
            //�������� Ӧ������ѯ��
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        }
        logger.debug(" ..........�����ص���Ϣ����........");
    }
    @Transactional
    public void handleTOA100004(TOA100004 toa) {
        String batchSN = toa.INFO.REQ_SN;
        String headRetCode = toa.INFO.RET_CODE;
        String errMsg = toa.INFO.ERR_MSG;
        appendNewJoblog(batchSN, "fip_cutpaybat", "��������", "[������:" + headRetCode + "][��Ϣ:]" + errMsg);
        if (headRetCode.equals("0000")) {  //�������
            processTOA100004DetailRecordOfBatchPkg(toa);
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_SUCCESS);
        } else if (headRetCode.startsWith("1")) {   //����ʧ��  '1'��ͷ��ʾ�������д����������ϵͳ���ͱ��ĳ���
            //���������еļ�¼Ϊ ������ʧ�ܡ�
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_FAIL);
            //���ö�Ӧ����ϸ��¼�ı�־Ϊ������
            //resetDetailRecordStatusByBatchQueryResult(batchSN, headRetCode, errMsg);
        } else { //����״̬������ ��������ѯ  (TODO: δ���� 0001��0002)
            //�������� Ӧ������ѯ��
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        }
        logger.debug(" ..........�����ص���Ϣ����........");
    }

    @Transactional
    public void handleTOA200001(TOA200001 toa) {
        String batchSN = toa.BODY.QUERY_TRANS.QUERY_SN;
        String headRetCode = toa.INFO.RET_CODE;
        String errMsg = toa.INFO.ERR_MSG;
        appendNewJoblog(batchSN, "fip_cutpaybat", "��������", "[������:" + headRetCode + "][��Ϣ:]" + errMsg);
        if (headRetCode.equals("0000")) {  //�������
            processTOA200001DetailRecordOfBatchPkg(toa);
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_SUCCESS);
        } else if (headRetCode.startsWith("1")) {   //����ʧ��  '1'��ͷ��ʾ�������д����������ϵͳ���ͱ��ĳ���
            //���������еļ�¼Ϊ ��������ɡ�
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.DEAL_FAIL);
            //���ö�Ӧ����ϸ��¼�ı�־Ϊ������
            //resetDetailRecordStatusByBatchQueryResult(batchSN, headRetCode, errMsg);
        } else { //����״̬������ ��������ѯ  (TODO: δ���� 0001��0002)
            //�������� Ӧ������ѯ��
            setCutpaybatRecordStatus(batchSN, TxpkgStatus.QRY_PEND);
        }
        logger.debug(" ..........�����ص���Ϣ����........");
    }

    /**
     * ���ݲ�ѯ���ĵķ��ؽ�� ѭ��������ϸ��¼
     *
     * @param toa
     */
    @Transactional
    private void processTOA100004DetailRecordOfBatchPkg(TOA100004 toa) {
        String batchSN = toa.INFO.REQ_SN;
        FipCutpaydetlExample example = new FipCutpaydetlExample();

        BigDecimal amt100 = new BigDecimal(100);
        for (TOA100004.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
            String detailSn = detail.SN;
            example.clear();
            example.createCriteria().andTxpkgSnEqualTo(batchSN).andTxpkgDetlSnEqualTo(detailSn);

            List<FipCutpaydetl> cutpaydetlList = cutpaydetlMapper.selectByExample(example);
            FipCutpaydetl record = cutpaydetlList.get(0);

            String retCode = detail.RET_CODE;
            //ҵ��ɹ�
            if ("0000".equals(retCode)) {
                BigDecimal amt = new BigDecimal(detail.AMOUNT);
                amt = amt.divide(amt100);
                if (detail.ACCOUNT_NO.endsWith(record.getBiBankactno()) && amt.equals(record.getPaybackamt())) {
                    record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    record.setDateBankCutpay(new Date());
                } else {
                    logger.error("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
                    throw new RuntimeException("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
                }
            } else {
                record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            }
            record.setTxRetmsg("[������:" + retCode + "][������Ϣ]:" + detail.ERR_MSG);
            record.setTxRetcode(String.valueOf(retCode));
            record.setRecversion(record.getRecversion() + 1);
            String log = "������������(100004)���: [��������+���:" + record.getTxpkgSn() + record.getTxpkgDetlSn() + "]" + record.getTxRetmsg();
            appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", log);

            cutpaydetlMapper.updateByPrimaryKey(record);
        }
    }
    @Transactional
    private void processTOA200001DetailRecordOfBatchPkg(TOA200001 toa) {
        String batchSN = toa.BODY.QUERY_TRANS.QUERY_SN;
        FipCutpaydetlExample example = new FipCutpaydetlExample();

        BigDecimal amt100 = new BigDecimal(100);
        for (TOA200001.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
            String detailSn = detail.SN;
            example.clear();
            example.createCriteria().andTxpkgSnEqualTo(batchSN).andTxpkgDetlSnEqualTo(detailSn);

            List<FipCutpaydetl> cutpaydetlList = cutpaydetlMapper.selectByExample(example);
            FipCutpaydetl record = cutpaydetlList.get(0);

            String retCode = detail.RET_CODE;
            //ҵ��ɹ�
            if ("0000".equals(retCode)) {
                BigDecimal amt = new BigDecimal(detail.AMOUNT);
                amt = amt.divide(amt100);
                if (detail.ACCOUNT.endsWith(record.getBiBankactno()) && amt.equals(record.getPaybackamt())) {
                    record.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    record.setDateBankCutpay(new Date());
                } else {
                    logger.error("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
                    throw new RuntimeException("��¼��ƥ��(�ʺŻ���)" + record.getClientname());
                }
            } else {
                record.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
            }
            record.setTxRetmsg("[������:" + retCode + "][������Ϣ]:" + detail.ERR_MSG);
            record.setTxRetcode(String.valueOf(retCode));
            record.setRecversion(record.getRecversion() + 1);
            String log = "������������(200001)���: [��������+���:" + record.getTxpkgSn() + record.getTxpkgDetlSn() + "]" + record.getTxRetmsg();
            appendNewJoblog(record.getPkid(), "fip_cutpaydetl", "��������", log);

            cutpaydetlMapper.updateByPrimaryKey(record);
        }
    }

    /**
     * �������
     */
    private void resetDetailRecordStatusByBatchQueryResult(String batchSN, String headRetCode, String errMsg) {
        //String headRetCode = toa.getHeader().RET_CODE;
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andTxpkgSnEqualTo(batchSN).andArchiveflagEqualTo("0").andDeletedflagEqualTo("0");
        List<FipCutpaydetl> detlList = cutpaydetlMapper.selectByExample(example);
        for (FipCutpaydetl fipCutpaydetl : detlList) {
            fipCutpaydetl.setBillstatus(BillStatus.INIT.getCode());
            fipCutpaydetl.setRecversion(fipCutpaydetl.getRecversion() + 1);
            cutpaydetlMapper.updateByPrimaryKey(fipCutpaydetl);
            String log = "�����׽��: [����������:" + headRetCode + "][��Ϣ:]" + errMsg;
            appendNewJoblog(fipCutpaydetl.getPkid(), "fip_cutpaydetl", "��������", log);
        }
    }

    /**
     * ����������״̬��
     *
     * @param headSn
     */
    @Transactional
    private void setCutpaybatRecordStatus(String headSn, TxpkgStatus status) {
        FipCutpaybat fipCutpaybat = cutpaybatMapper.selectByPrimaryKey(headSn);
        fipCutpaybat.setTxpkgStatus(status.getCode());
        fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
        cutpaybatMapper.updateByPrimaryKey(fipCutpaybat);
    }


    //===============================================================================
    private void appendNewJoblog(String pkid, String tableName, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, tableName, jobname, jobdesc, "���ݽ���ƽ̨", "���ݽ���ƽ̨");
    }

}
