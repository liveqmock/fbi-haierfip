package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.hccb.HccbContext;
import fip.gateway.hccb.HccbT1001Handler;
import fip.gateway.hccb.HccbT1003Handler;
import fip.gateway.hccb.model.T1001Response;
import fip.gateway.hccb.model.T1003Request;
import fip.gateway.hccb.model.TxnHead;
import fip.gateway.sbs.DepCtgManager;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.txn.Taa41.Taa41SOFDataDetail;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.dao.PtenudetailMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.security.OperatorManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HCCB С���Ŵ�.
 * User: zhanrui
 * Date: 2014-08-01
 */
@Service
public class HccbService {
    private static final Logger logger = LoggerFactory.getLogger(HccbService.class);

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipRefunddetlMapper fipRefunddetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;
    @Autowired
    private SbsTxnHelper sbsTxnHelper;

    @Transactional
    public synchronized int importDataFromXls(BizType bizType, List<FipCutpaydetl> cutpaydetls, List<String> returnMsgs) {
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        int count = 0;
        for (FipCutpaydetl cutpaydetl : cutpaydetls) {
            iSeqno++;
            assembleCutpayRecord(bizType, batchno, iSeqno, cutpaydetl);

            //TODO �ж�ҵ�������Ƿ��ظ�   ע�� �޸�IOUNO����ʱ��Ҫͬ���޸�commonmapper�е�SQL
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Hccb(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getOriginBizid());
            if (isNotRepeated) {
                fipCutpaydetlMapper.insert(cutpaydetl);
                count++;
            } else {
                returnMsgs.add("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.error("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //��־
        batchInsertLogByBatchno(batchno, "�½���¼", "�»�ȡ�������Ŵ�ϵͳ���ۼ�¼");
        return count;
    }

    //=====================
    //��С����������ѯ��������
    public List<FipCutpaydetl> doQueryHccbBills(BizType bizType) {
        List<FipCutpaydetl> cutpaydetlList = getHccbRecordsBaseInfo();

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;

        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            iSeqno++;
            assembleCutpayRecord(bizType, batchno, iSeqno, cutpaydetl);
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            //cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    //��ȡС�����ۼ�¼  ��������������
    public synchronized int doObtainHccbBills(BizType bizType, List<String> returnMsgs) {
        //1. ��ȡȫ����¼ ÿ����¼ֻ�����ӿڹ�������Ϣ
        List<FipCutpaydetl> cutpaydetls = doQueryHccbBills(bizType);

        //2.�����õ����κ�
        String batchno = billManagerService.generateBatchno();

        //3.����insert ������ ͬ���ӱ����
        return importDataFromXls(bizType, cutpaydetls, returnMsgs);
    }

    //������д
    public synchronized int writebackCutPayRecord2Hccb(List<FipCutpaydetl> cutpaydetlList, boolean isArchive) {
        T1003Request request = new T1003Request();
        request.setHead(new TxnHead().txncode("1003"));

        List<T1003Request.Record> t1003RequestRecords = new ArrayList<T1003Request.Record>();
        BigDecimal amt = new BigDecimal("0.00");
        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            T1003Request.Record record = new T1003Request.Record();
            record.setIouno(cutpaydetl.getIouno());
            record.setPoano(cutpaydetl.getPoano());
            record.setTxnamt(cutpaydetl.getPaybackamt().toString());
            record.setSchpaydate(cutpaydetl.getPaybackdate());

            String billStatus = cutpaydetl.getBillstatus();
            if (BillStatus.CUTPAY_SUCCESS.getCode().equals(billStatus)) {
                record.setResultcode("1");   //���д���ɹ�
            } else if (BillStatus.CUTPAY_FAILED.getCode().equals(billStatus)) {
                record.setResultcode("2");   //���д���ʧ��
            } else if (BillStatus.CUTPAY_QRY_PEND.getCode().equals(billStatus)) {
                record.setResultcode("3");   //״̬����
            } else {
                logger.error("��д��¼ʱ���ִ����¼��");
            }
            amt = amt.add(cutpaydetl.getPaybackamt());
            t1003RequestRecords.add(record);
        }

        request.getBody().setTotalitems("" + t1003RequestRecords.size());
        request.getBody().setTotalamt(amt.toString());
        request.getBody().setRecords(t1003RequestRecords);


        //hccb ͨѶ
        HccbT1003Handler handler = new HccbT1003Handler();
        HccbContext context = new HccbContext();
        context.setRequest(request);
        handler.process(context);

        //��Ӧ��Ϣ����
        Map<String, String> paraMap = context.getParaMap();
        String rtnCode = paraMap.get("rtnCode");
        String rtnMsg = paraMap.get("rtnMsg");

        //�������ݿ⴦�� ֻ����ɹ��������
        if ("0000".equals(rtnCode)) {
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                cutpaydetl.setWritebackflag("1");
                if (isArchive) {
                    cutpaydetl.setArchiveflag("1");   //��д��� ���浵����
                }
                cutpaydetl.setDateCmsPut(new Date());
                cutpaydetl.setRecversion(cutpaydetl.getRecversion() + 1);
                fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }
        } else {
            logger.error("С��ϵͳ��дʧ��" + rtnCode + rtnMsg);
        }

        //��־
        if (rtnMsg.length() >= 20) {
            rtnMsg = rtnMsg.substring(0, 20);
        }
        batchInsertLog("������д", rtnMsg, cutpaydetlList);

        return cutpaydetlList.size();
    }


    //��hccbͨѶ����ȡ��¼�� ת��Ϊ������fipcutpay bean
    private List<FipCutpaydetl> getHccbRecordsBaseInfo() {
        HccbT1001Handler handler = new HccbT1001Handler();
        HccbContext context = new HccbContext();
        handler.process(context);
        List<T1001Response.Record> recvedList = (List<T1001Response.Record>) context.getResponse();

        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        for (T1001Response.Record responseBean : recvedList) {
            FipCutpaydetl cutpaydetl = copyT1001ResponseRecord2FipCutpaydetl(responseBean);
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    private FipCutpaydetl copyT1001ResponseRecord2FipCutpaydetl(T1001Response.Record record) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setIouno(record.getIouno());
        cutpaydetl.setPoano(record.getPoano());
        cutpaydetl.setClientno(record.getCustid());//�ͻ���

        cutpaydetl.setClientname(record.getActname());
        cutpaydetl.setBiBankactname(record.getActname());

        cutpaydetl.setClientid(record.getCertid());//֤������

        if (StringUtils.isEmpty(record.getTxnamt())) {
            throw new RuntimeException("����ֶβ���Ϊ��");
        }
        cutpaydetl.setPaybackamt(new BigDecimal(record.getTxnamt()));

        cutpaydetl.setBiBankactno(record.getActno());
        cutpaydetl.setBiActopeningbank(record.getBankcode());
        cutpaydetl.setBiProvince(record.getProvince());
        cutpaydetl.setBiCity(record.getCity());

        return cutpaydetl;
    }

    //======================
    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               FipCutpaydetl cutpaydetl) {
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        if (StringUtils.isEmpty(cutpaydetl.getPoano())) {
            cutpaydetl.setPoano("0");
        }

        //��������Ϣ
        cutpaydetl.setPrincipalamt(new BigDecimal("0.00")); //�����
        cutpaydetl.setInterestamt(new BigDecimal("0.00"));  //������Ϣ
        cutpaydetl.setPunitiveintamt(new BigDecimal("0.00"));//��Ϣ���
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//ΥԼ����
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//��Ϣ�������
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //������

        //����������Ϣ
        if (bizType.equals(BizType.HCCB)) {
            cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //Ĭ��Ϊ����
        } else {
            throw new RuntimeException("��HCCB���ݣ����ܴ���");
        }

        //����
        cutpaydetl.setRecversion((long) 0);
        cutpaydetl.setDeletedflag("0");
        cutpaydetl.setArchiveflag("0");
        cutpaydetl.setWritebackflag("0");
        cutpaydetl.setAccountflag("0");
        //�ʵ�״̬
        cutpaydetl.setBillstatus(BillStatus.INIT.getCode());
        cutpaydetl.setSendflag("0");

        //zhanrui 20120305  ��ʶ�����Ŵ�������Դ���Ŵ�ϵͳ �����дʱ������Դϵͳ
        cutpaydetl.setRemark3("HCCB");
        cutpaydetl.setDateCmsGet(new Date());

        //����
        cutpaydetl.setBilltype(BillType.NORMAL.getCode());
        cutpaydetl.setClientact("123456"); //����Ϊ��
        return cutpaydetl;
    }


    /**
     * SBS����  20150505  zhanrui
     */
    public synchronized int accountCutPayRecord2SBS(List<FipCutpaydetl> cutpaydetlList, String totalSuccessAmt) {
        int count = 0;

        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();

        OperatorManager operatorManager = SystemService.getOperatorManager();
        if (operatorManager != null) {
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }

        FipJoblog joblog = new FipJoblog();

        try {
            //����ٴκ˶�
            BigDecimal amt = new BigDecimal(0);
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                amt = amt.add(cutpaydetl.getPaybackamt());
            }
            DecimalFormat df = new DecimalFormat("#,##0.00");
            if (!totalSuccessAmt.equals(df.format(amt))) {
                throw new RuntimeException("�ܽ�һ�£���˶ԡ�");
            }

            String sn = "HCCB" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            //String fromActno = "801000026131041001"; //��Ӧ����37101985510051003497
            //String toActno = "801000977202019014";
            String fromActno = sbsTxnHelper.selectSbsActnoFromPtEnuDetail("HCCB_FROM_ACTNO"); //��Ӧ����37101985510051003497
            String toActno = sbsTxnHelper.selectSbsActnoFromPtEnuDetail("HCCB_TO_ACTNO");
            String productCode = "N105";
            String remark = "HCCBС������";
            List<String> paramList = sbsTxnHelper.assembleTaa41Param(sn, fromActno, toActno, amt, productCode, remark);

            //SBS
            byte[] recvBuf = DepCtgManager.processSingleResponsePkg("aa41", paramList);
            SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
            Taa41SOFDataDetail sofDataDetail = new Taa41SOFDataDetail();
            response.setSofDataDetail(sofDataDetail);
            response.init(recvBuf);

            String formcode = response.getFormcode();


            BillStatus billStatus = BillStatus.ACCOUNT_FAILED;
            String logMsg = "";
            if (!formcode.equals("T531")) {     //�쳣�������
                billStatus = BillStatus.ACCOUNT_FAILED;
                logMsg = "SBS����ʧ�ܣ�FORMCODE=" + formcode;
            } else {
                if (sofDataDetail.getSECNUM().trim().equals(sn)) {
                    billStatus = BillStatus.ACCOUNT_SUCCESS;
                    logMsg = "SBS������ɣ�FORMCODE=" + formcode;
                } else {
                    billStatus = BillStatus.ACCOUNT_PEND;
                    logMsg = "SBS�������,�����ص���ˮ�ų������ѯ��FORMCODE=" + formcode;
                }
            }

            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                cutpaydetl.setBillstatus(billStatus.getCode());
                joblog.setJobdesc(logMsg);
                joblog.setTablename("fip_cutpaydetl");
                joblog.setRowpkid(cutpaydetl.getPkid());
                joblog.setJobname("SBS����");
                joblog.setJobtime(new Date());
                joblog.setJobuserid(userid);
                joblog.setJobusername(username);
                fipJoblogMapper.insert(joblog);
                fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }

            return count;
        } catch (Exception e) {
            logger.error("����ʱ���ִ���", e);
            throw new RuntimeException("����ʱ���ִ���", e);
        }
    }

    //============================================

    /**
     * ��鱾�ر��мȴ��¼��״̬ ��������
     * 1��״̬�����ļ�¼
     * 2��δ���͵ļ�¼
     * 3�����ͳɹ��ļ�¼�����ͳɹ��ı������ʻ�д��
     */
    private boolean checkLocalBillsStatus() {
        return true;
    }

    //TODO  ����
    private void batchInsertLogByBatchno(String batchno, String jobName, String jobDesc) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBatchSnEqualTo(batchno);
        List<FipCutpaydetl> fipCutpaydetlList = fipCutpaydetlMapper.selectByExample(example);

        batchInsertLog(jobName, jobDesc, fipCutpaydetlList);
    }

    private void batchInsertLog(String jobName, String jobDesc, List<FipCutpaydetl> fipCutpaydetlList) {
        Date date = new Date();

        OperatorManager operatorManager = SystemService.getOperatorManager();
        String userid;
        String username;
        if (operatorManager == null) {
            userid = "9999";
            username = "BATCH";
        } else {
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }

        for (FipCutpaydetl fipCutpaydetl : fipCutpaydetlList) {
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(fipCutpaydetl.getPkid());
            log.setJobname(jobName);
            log.setJobdesc(jobDesc);
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }

}
