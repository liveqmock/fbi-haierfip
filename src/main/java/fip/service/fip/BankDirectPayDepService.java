package fip.service.fip;

import fip.common.constant.BillStatus;
import fip.common.constant.TxpkgStatus;
import fip.gateway.sbs.DepCtgManager;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipCutpaydetlExample;
import org.apache.commons.lang.StringUtils;
import org.fbi.dep.model.txn.TOA900n052;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ����ֱ������  ͨ��DEPתSBS.
 * �ص��Ƕ���շ��Ĵ���
 * User: zhanrui
 * Date: 12-8-30
 * Time: ����11:56
 * To change this template use File | Settings | File Templates.
 */
@Service
public class BankDirectPayDepService {
    private static final Logger logger = LoggerFactory.getLogger(BankDirectPayDepService.class);
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


    //������۴�������������������
    @Transactional
    public synchronized void performRequestHandleBatchPkg(String txnDate, FipCutpaybat cutpaybat, String userid, String username) {
        if (!checkBatchTableRecord(cutpaybat)) {
            throw new RuntimeException("�汾��״̬����" + cutpaybat.getTxpkgSn());
        }

        List<FipCutpaydetl> cutpaydetlList = queryCutpaydetlList(cutpaybat);
        //TODO ���ͱ���ǰ����bat��detl�İ汾�ţ� ���ύ����

        //���ȸ����������ı�״̬���������� �ٷ���
        billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.QRY_PEND, "1");
        //TODO ��Ϊsql������
        billManagerService.updateCutpaydetlListStatus4NewTransactional(cutpaydetlList, BillStatus.CUTPAY_QRY_PEND);

        jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "���д���������", "��ʼ����", userid, username);
        //TODO ��������detl�����־

        //�����������
        try {
            processOneRequestHandleBatchPkg(txnDate, cutpaybat, cutpaydetlList);
        } catch (Exception e) {
            jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "���д���������", "���ʹ���" + e.getMessage(), userid, username);
            throw new RuntimeException(e);
        }

        jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "���д���������", "�������", userid, username);
        //TODO ��������detl�����־
    }

    //��������ѯ��������
    @Transactional
    public synchronized void performResultQueryBatchPkg(String txnDate, FipCutpaybat cutpaybat, String userid, String username) {
        if (!checkBatchTableRecord(cutpaybat)) {
            throw new RuntimeException("�汾��״̬����" + cutpaybat.getTxpkgSn());
        }

        byte[] response = processOneResultQueryBatchPkg(txnDate, cutpaybat);
        String formcode = new String(response, 21, 4);
        logger.info(new String(response));

        if (!formcode.equals("T541")) {             //�쳣�������
            String errmsg = "[SBS���ش�����Ϣ: " + formcode + " " + getErrMsgFromResponse(response) + " ]";
            jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "���н����ѯ����", errmsg, userid, username);
            if ("WB02".equals(formcode)) {          //�ñ�ҵ�񲻴��ڻ��ѱ����оܾ�    ���ѯԭ��״̬����
                //�����������ı�״̬����������
                billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.SEND_PEND, "1");
                throw new RuntimeException("�ñ�ҵ�񲻴��ڻ��ѱ����оܾ��������������" + errmsg);
            } else {                                //����������Ҫ���н����ѯ�ٴ�ȷ��
                //---20140623 zhanrui ��Խ�����������ƽ̨�������仯 �������⴦��
                if (errmsg.contains("CCB-0130Z110B358")) {  //CCB-0130Z110B358:δ�ҵ��������۵���
                    billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.SEND_PEND, "1");
                    throw new RuntimeException("�ñ�ҵ�񲻴��ڻ��ѱ����оܾ��������������" + errmsg);
                }else if (errmsg.contains("CCB-0130Z110BB22")) {  //CCB-0130Z110BB22:��û�в�ȷ������Ľ�����ˮ
                    billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.SEND_PEND, "1");
                    throw new RuntimeException("�ñ�ҵ�񲻴��ڻ��ѱ����оܾ��������������" + errmsg);
                }else {
                    billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.QRY_PEND, "1");
                    throw new RuntimeException("���д������������뷢������ѯ���׽���ȷ�ϡ�" + errmsg);
                }
            }
        } else {                                    //���ķ��ͳɹ�
            //�������
            TOA900n052 toa = unmarshall(response);

            if ("1".equals(toa.body.FLOFLG)) {
                //TODO
                throw new RuntimeException("SBS���ر����д��ں�������ϵͳ�ݲ�֧�֣�����ϵϵͳ����Ա��");
            }

            String succmsg = "���з��سɹ�������" + toa.body.SUCCNT + " ʧ�ܱ�����" + toa.body.FALCNT +
                    " �ɹ���" + toa.body.SUCAMT + " ʧ�ܽ�" + toa.body.FALAMT;
            jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "���н����ѯ����", succmsg, userid, username);

            //��������ϸ��¼
            updateLocalDBRecordStatusByResponse(cutpaybat, toa, userid, username);
            //�趨���������
            billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.DEAL_SUCCESS, "1");
        }
    }

    //��������ϸ��¼
    private void updateLocalDBRecordStatusByResponse(FipCutpaybat cutpaybat, TOA900n052 toa, String userid, String username) {
        List<FipCutpaydetl> cutpaydetlList = queryCutpaydetlList(cutpaybat);
        if (Integer.parseInt(toa.body.FALCNT) == 0) {
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                cutpaydetl.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                cutpaydetl.setDateBankCutpay(new Date());
                cutpaydetl.setRecversion(cutpaydetl.getRecversion() + 1);
                cutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
                jobLogService.insertNewJoblog(cutpaydetl.getPkid(), "fip_cutpaydetl", "�����ѯ����", "���۴���ɹ�", userid, username);
            }
        } else {
            TOA900n052.Body.BodyDetail[] details = new TOA900n052.Body.BodyDetail[toa.body.RET_DETAILS.size()];
            int step = 0;
            for (TOA900n052.Body.BodyDetail bodyDetail : toa.body.RET_DETAILS) {
                details[step++] = bodyDetail;
            }

            boolean[] detailFlags = new boolean[details.length];
            for (int i = 0; i < detailFlags.length; i++) {
                System.out.println(detailFlags[i]);
                detailFlags[i] = false;
            }
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                String actno = cutpaydetl.getBiBankactno().trim();
                BigDecimal payamt = cutpaydetl.getPaybackamt();
                boolean isFound = false;
                for (int i = 0; i < detailFlags.length; i++) {
                    if (!detailFlags[i]) {
                        String rtnActno = details[i].ACTNUM.trim();
                        BigDecimal rtnAmt = new BigDecimal(details[i].TXNAMT.trim());
                        if (actno.equals(rtnActno) && payamt.compareTo(rtnAmt) == 0) {
                            isFound = true;
                            detailFlags[i] = true;  //�Ѵ��������¼���Ժ��ٴ���
                            break;
                        }
                    }
                }
                if (isFound) {
                    //����Ϊ�ۿ�ʧ��
                    cutpaydetl.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                    String reason = details[0].REASON;

                    //20140123  zr  �ж��������
                    cutpaydetl.setTxRetcode("XXXX");
                    cutpaydetl.setTxRetmsg(reason);
                    if (reason.contains("����")) {
                        cutpaydetl.setTxRetcode("3008");
                    }

                    jobLogService.insertNewJoblog(cutpaydetl.getPkid(), "fip_cutpaydetl", "�����ѯ����", "���۴���ʧ��:" + reason, userid, username);
                } else {
                    //����Ϊ�ۿ�ɹ�
                    cutpaydetl.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    cutpaydetl.setTxRetcode("0000");
                    //����
                    cutpaydetl.setDateBankCutpay(new Date());
                    jobLogService.insertNewJoblog(cutpaydetl.getPkid(), "fip_cutpaydetl", "�����ѯ����", "���۴���ɹ�", userid, username);
                }
                cutpaydetl.setRecversion(cutpaydetl.getRecversion() + 1);
                cutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }
        }

        checkResultOfToaAndLocalDbAfterUpdateStatus(cutpaybat, toa);
    }

    //�˶��޸�״̬��Ļ��ܽ���Ƿ���TOAһ��
    private void  checkResultOfToaAndLocalDbAfterUpdateStatus(FipCutpaybat cutpaybat, TOA900n052 toa){
        List<FipCutpaydetl> cutpaydetlList = queryCutpaydetlList(cutpaybat);
        BigDecimal succDbAmt = new BigDecimal(0.00);
        BigDecimal succToaAmt = new BigDecimal(toa.body.SUCAMT.trim());
        BigDecimal failDbAmt = new BigDecimal(0.00);
        BigDecimal failToaAmt = new BigDecimal(toa.body.FALAMT.trim());

        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            if (cutpaydetl.getBillstatus().equals(BillStatus.CUTPAY_SUCCESS.getCode())) {
                succDbAmt = succDbAmt.add(cutpaydetl.getPaybackamt());
            }else if (cutpaydetl.getBillstatus().equals(BillStatus.CUTPAY_FAILED.getCode())) {
                failDbAmt = failDbAmt.add(cutpaydetl.getPaybackamt());
            }
        }
        if (succToaAmt.compareTo(succDbAmt) != 0 || failToaAmt.compareTo(failDbAmt) != 0) {
            throw new RuntimeException("�����ѯ���ؿۿ��������ݿ���ܲ�����");
        }
    }

    //�����������ı�״̬
    private void updateCutpaybatRecordStatus(FipCutpaybat cutpaybat) {
        long recversion = cutpaybat.getRecversion() + 1;
        cutpaybat.setTxpkgStatus(TxpkgStatus.QRY_PEND.getCode());
        cutpaybat.setRecversion(recversion);
        cutpaybatMapper.updateByPrimaryKey(cutpaybat);
    }


    private List<FipCutpaydetl> queryCutpaydetlList(FipCutpaybat cutpaybat) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andTxpkgSnEqualTo(cutpaybat.getTxpkgSn())
                //.andBillstatusEqualTo(BillStatus.PACKED.getCode())
                .andArchiveflagEqualTo("0")
                .andDeletedflagEqualTo("0");
        return cutpaydetlMapper.selectByExample(example);
    }


    //��鲢���汾���ֺܷ˶ԡ�״̬���
    private boolean checkBatchTableRecord(FipCutpaybat cutpaybat) {

        return true;
    }


    //==========================����������==============================================================

    /**
     * �����������۴���������   ���� ����
     */
    private void processOneRequestHandleBatchPkg(String txnDate, FipCutpaybat cutpaybat, List<FipCutpaydetl> cutpaydetlList) {
        int recordNumPerMsg = 130; //ÿ�������д��ۼ�¼����

        List<FipCutpaydetl> cutpaydetlsOneMsg = new ArrayList<FipCutpaydetl>();

        int onemsgStep = 0;
        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            cutpaydetlsOneMsg.add(cutpaydetl);
            onemsgStep++;
            if (onemsgStep == recordNumPerMsg) {
                byte[] response = processOneRequestHandleMsg(txnDate, cutpaybat, cutpaydetlsOneMsg, false);
                logger.info(new String(response));

                String formcode = new String(response, 21, 4);
                if (!"W105".equals(formcode)) {
                    String errmsg = "[SBS���ش�����Ϣ: " + formcode + " " + getErrMsgFromResponse(response) + " ]";
                    throw new RuntimeException("SBSͨѶ���Ľ�������" + errmsg);
                }
                cutpaydetlsOneMsg = new ArrayList<FipCutpaydetl>();
                onemsgStep = 0;
            }
        }

        byte[] response = processOneRequestHandleMsg(txnDate, cutpaybat, cutpaydetlsOneMsg, true);
        logger.info(new String(response));
        String formcode = new String(response, 21, 4);
        if (!"T531".equals(formcode)) {
            String errmsg = "[SBS���ش�����Ϣ: " + formcode + " " + getErrMsgFromResponse(response) + " ]";
            if ("MB01".equals(formcode)) {   //�������г�ʱ,���������ҪMPC��ѯȷ��
                //�����������ı�״̬����������
                billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.QRY_PEND, "1");
                throw new RuntimeException("SBS�������г�ʱ��������ȷ��������н����ѯ���׽���ȷ�ϡ�" + errmsg);
            } else {
                billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.SEND_PEND, "1");
                throw new RuntimeException("SBS����ʧ�ܣ������������" + errmsg);
            }
        }
    }

    //�������۴���������
    private byte[] processOneRequestHandleMsg(String txnDate, FipCutpaybat cutpaybat, List<FipCutpaydetl> cutpaydetls, boolean isLastMsg) {
        StringBuffer data = new StringBuffer();
        DecimalFormat amtdf = new DecimalFormat("#############0.00");

        for (FipCutpaydetl cutpaydetl : cutpaydetls) {
            String amt = amtdf.format(cutpaydetl.getPaybackamt());
            data = data.append(amt); //���
            data = data.append("|");
            data = data.append("|"); //��ϸ��ע��һ��Ϊ��
            data = data.append(cutpaydetl.getBiBankactno().trim()); //�ʺ�
            data = data.append("|");
            data = data.append(cutpaydetl.getBiBankactname().trim()); //����
            data = data.append("|");
            data = data.append("|"); //֤���� Ϊ��
        }
        List<String> paramList = getRequestHandleReqParamList(txnDate, cutpaybat, cutpaydetls.size(), isLastMsg);
        paramList.add(data.toString());  //  ���������ļ�����  29000

        return DepCtgManager.processSingleResponsePkg("n050", paramList);
    }


    //����������Ĳ���
    private List<String> getRequestHandleReqParamList(String txnDate, FipCutpaybat cutpaybat, int currcount, boolean isLastMsg) {
        List<String> paramList = new ArrayList<String>();

        paramList.add(txnDate);                       //��������
        paramList.add(StringUtils.rightPad(cutpaybat.getBizSn(), 18, " "));        //ҵ���ţ������Ŵ�ϵͳ��XF+8λ����+6λ˳���       ����ϵͳ��FD+8λ����+6λ˳��ţ�

        //��ˮ�ű�������޸�Ϊ������ʮ��λǰ����  20140523  zhanrui
        //paramList.add(StringUtils.rightPad(cutpaybat.getTxpkgSn(), 16, " "));      //���İ��ţ���������ʱʹ�ã�
        paramList.add(StringUtils.leftPad(cutpaybat.getTxpkgSn(), 16, "0"));      //���İ��ţ���������ʱʹ�ã�

        DecimalFormat df = new DecimalFormat("#############0.00");

        String totalamt = df.format(cutpaybat.getTotalamt());
        paramList.add("+" + StringUtils.leftPad(totalamt, 16, '0'));                  //�ܽ��  17

        String totalcount = String.valueOf(cutpaybat.getTotalcount());
        paramList.add(StringUtils.leftPad(totalcount, 7, '0'));                         // �ܱ���        7

        paramList.add(StringUtils.leftPad("" + currcount, 7, '0'));                          // �����ܱ���        7

        paramList.add(isLastMsg ? "0" : "1");                                         //  �Ƿ��к�����  0-��1-��
        paramList.add(StringUtils.rightPad(cutpaybat.getTransferact(), 22, ' '));      //  ת���ʻ�  22

        if (cutpaybat.getUsage() != null) {
            paramList.add(StringUtils.rightPad(cutpaybat.getUsage(), 12, ' '));         //  ��; 12
        } else {
            paramList.add(StringUtils.rightPad("99999998", 12, ' '));                   //  ��; 12
        }

        if (cutpaybat.getRemark() != null) {
            paramList.add(StringUtils.rightPad(cutpaybat.getRemark(), 30, ' '));         //  ��ע,   30
        } else {
            paramList.add(StringUtils.rightPad("REMARK:", 30, ' '));                     //  ��ע,   30
        }

        if (cutpaybat.getRemark1() != null) {
            paramList.add(StringUtils.rightPad(cutpaybat.getRemark1(), 32, ' '));        //  ��ע1,  32
        } else {
            paramList.add(StringUtils.rightPad("REMARK1", 32, ' '));                    //  ��ע1,  32
        }

        if (cutpaybat.getRemark2() != null) {
            paramList.add(StringUtils.rightPad(cutpaybat.getRemark2(), 32, ' '));        //  ��ע2,  32
        } else {
            paramList.add(StringUtils.rightPad("REMARK2", 32, ' '));                     //  ��ע2,  32
        }

        paramList.add(cutpaybat.getBankid());                     //  ���д���,  3
        paramList.add("+0000000000000.00");                       //  ʧ�ܽ��       17
        paramList.add("0000000");                                 //  ʧ�ܱ���        7
        paramList.add("BAW");                                     //  ������� BAP-��������,BAS-������������

        return paramList;
    }

    //==========================�����ѯ����==============================================================

    /**
     * ���ͽ����ѯ����
     */
    private byte[] processOneResultQueryBatchPkg(String txnDate, FipCutpaybat cutpaybat) {
        List<FipCutpaydetl> cutpaydetlList = queryCutpaydetlList(cutpaybat);
        List<String> paramList = new ArrayList<String>();

        paramList.add(StringUtils.rightPad(cutpaybat.getBizSn(), 18, " "));        //ҵ���ţ������Ŵ�ϵͳ��XF+8λ����+6λ˳���       ����ϵͳ��FD+8λ����+6λ˳��ţ�

        //��ˮ�ű�������޸�Ϊ������ʮ��λǰ����  20140523  zhanrui
        //paramList.add(StringUtils.rightPad(cutpaybat.getTxpkgSn(), 16, " "));      //���İ��ţ���������ʱʹ�ã�
        paramList.add(StringUtils.leftPad(cutpaybat.getTxpkgSn(), 16, "0"));      //���İ��ţ���������ʱʹ�ã�

        paramList.add(txnDate);                                                    //��������
        paramList.add("000001");                                                   //��ʼ���� 6

        return DepCtgManager.processSingleResponsePkg("n052", paramList);
    }

    private TOA900n052 unmarshall(byte[] buffer) {
        TOA900n052 toa = new TOA900n052();

        int k = 0;
        try {
            int pos = 29;
            byte[] bSuccnt = new byte[6];
            byte[] bFalcnt = new byte[6];
            byte[] bSucamt = new byte[17];
            byte[] bFalamt = new byte[17];
            byte[] bFloflg = new byte[1];
            byte[] bCurcnt = new byte[6];
            byte[] bRemark1 = new byte[99];
            byte[] bRemark2 = new byte[99];

            System.arraycopy(buffer, pos, bSuccnt, 0, bSuccnt.length);
            pos += bSuccnt.length;
            System.arraycopy(buffer, pos, bFalcnt, 0, bFalcnt.length);
            pos += bFalcnt.length;
            System.arraycopy(buffer, pos, bSucamt, 0, bSucamt.length);
            pos += bSucamt.length;
            System.arraycopy(buffer, pos, bFalamt, 0, bFalamt.length);
            pos += bFalamt.length;

            System.arraycopy(buffer, pos, bFloflg, 0, bFloflg.length);
            pos += bFloflg.length;
            System.arraycopy(buffer, pos, bCurcnt, 0, bCurcnt.length);
            pos += bCurcnt.length;
            System.arraycopy(buffer, pos, bRemark1, 0, bRemark1.length);
            pos += bRemark1.length;
            System.arraycopy(buffer, pos, bRemark2, 0, bRemark2.length);
            pos += bRemark2.length;


            toa.body.SUCCNT = new String(bSuccnt);
            toa.body.FALCNT = new String(bFalcnt);
            toa.body.SUCAMT = new String(bSucamt);
            toa.body.FALAMT = new String(bFalamt);

            toa.body.FLOFLG = new String(bFloflg);
            toa.body.CURCNT = new String(bCurcnt);
            toa.body.REMARK1 = new String(bRemark1);
            toa.body.REMARK2 = new String(bRemark2);

            int curcnt = Integer.parseInt(toa.body.CURCNT);

            byte[] bActnum = new byte[32];
            byte[] bActnam = new byte[60];
            byte[] bReason = new byte[40];
            byte[] bTxnamt = new byte[17];


            for (k = 0; k < curcnt; k++) {
                TOA900n052.Body.BodyDetail record = new TOA900n052.Body.BodyDetail();
                System.arraycopy(buffer, pos, bActnum, 0, bActnum.length);
                pos += bActnum.length;
                System.arraycopy(buffer, pos, bActnam, 0, bActnam.length);
                pos += bActnam.length;
                System.arraycopy(buffer, pos, bReason, 0, bReason.length);
                pos += bReason.length;
                System.arraycopy(buffer, pos, bTxnamt, 0, bTxnamt.length);
                pos += bTxnamt.length;
                record.ACTNUM = new String(bActnum);
                record.ACTNAM = new String(bActnam);
                record.REASON = new String(bReason);
                record.TXNAMT = new String(bTxnamt);
                toa.body.RET_DETAILS.add(record);
            }
            return toa;
        } catch (Exception e) {
            System.out.println("���Ľ��ʱ�������⣺" + k);
            logger.error("���Ľ��ʱ�������⣺" + k);
            throw new RuntimeException(e);
        }
    }


    //===========================================================================================
    /*
   ��ȡ����Ӧ�����еĴ�����Ϣ
    */
    private String getErrMsgFromResponse(byte[] buffer) {

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