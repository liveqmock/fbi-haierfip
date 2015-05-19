package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.newcms.controllers.T100109CTL;
import fip.gateway.newcms.controllers.T100110CTL;
import fip.gateway.newcms.domain.T100109.T100109ResponseRecord;
import fip.gateway.newcms.domain.T100110.T100110RequestRecord;
import fip.gateway.sbs.DepCtgManager;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.core.SOFDataDetail;
import fip.gateway.sbs.txn.Ta543.Ta543Handler;
import fip.gateway.sbs.txn.Ta543.Ta543SOFDataDetail;
import fip.gateway.sbs.txn.Taa41.Taa41SOFDataDetail;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.platform.security.OperatorManager;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * ר�������.
 * 201504
 * User: zhanrui
 * Date: 2011-8-13
 */
@Service
public class ZmdService {
    private static final Logger logger = LoggerFactory.getLogger(ZmdService.class);
    private int uniqKeyLen = 20;

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

    /**
     * ��ѯ�Ŵ�ϵͳר����Ĵ��ۼ�¼���ɹ�ѡ���Ի�ȡ��    (��������Ϣ�ʵ�)
     */
    public List<FipCutpaydetl> doQueryZmdBills(BizType bizType, BillType billType) {

        List<T100109ResponseRecord> recvedList = getZmdResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100109ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    /**
     * ��ȡȫ���Ŵ�ϵͳ��¼  (��������Ϣ�ʵ�)
     */
    public synchronized int doObtainZmdBills(BizType bizType, BillType billType, List<String> returnMsgs) {
        List<T100109ResponseRecord> recvedList = getZmdResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100109ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Zmd(cutpaydetl.getClientno(), cutpaydetl.getPaybackdate(), cutpaydetl.getOriginBizid());
            if (isNotRepeated) {
                cutpaydetl.setDateCmsGet(new Date());
                fipCutpaydetlMapper.insert(cutpaydetl);
                count++;
            } else {
                returnMsgs.add("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.error("��ȡ����ʱ�����ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //��־
        batchInsertLogByBatchno(batchno);
        return count;
    }


    /**
     * SBS����
     */
    public synchronized int accountCutPayRecord2SBS(List<FipCutpaydetl> cutpaydetlList) {
        int count = 0;

        String userid = "9999";
        String username = "crontask";

        OperatorManager operatorManager = SystemService.getOperatorManager();
        if (operatorManager != null) {
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }

        FipJoblog joblog = new FipJoblog();

        try {
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                String sn = cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn();
                //String fromActno = "801090106001041001";
                String fromActno = sbsTxnHelper.selectSbsActnoFromPtEnuDetail("ZMD_FROM_ACTNO"); //��Ӧ����37101985510051003497
                String toActno = cutpaydetl.getClientact();//�����˺�
                String productCode = "N103";
                String remark = "ZMD" + cutpaydetl.getTxpkgSn() + cutpaydetl.getTxpkgDetlSn();
                List<String> paramList = sbsTxnHelper.assembleTaa41Param(sn, fromActno, toActno, cutpaydetl.getPaybackamt(), productCode, remark);

                //SBS
                byte[] recvBuf = DepCtgManager.processSingleResponsePkg("aa41", paramList);
                SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
                Taa41SOFDataDetail sofDataDetail = new Taa41SOFDataDetail();
                response.setSofDataDetail(sofDataDetail);
                response.init(recvBuf);

                String formcode = response.getFormcode();
                if (!formcode.equals("T531")) {     //�쳣�������
                    cutpaydetl.setBillstatus(BillStatus.ACCOUNT_FAILED.getCode());
                    joblog.setJobdesc("SBS����ʧ�ܣ�FORMCODE=" + formcode);
                } else {
                    if (sofDataDetail.getSECNUM().trim().equals(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn())) {
                        joblog.setJobdesc("SBS������ɣ�FORMCODE=" + formcode + " �ʺ�:" + cutpaydetl.getClientact());
                        cutpaydetl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                        cutpaydetl.setDateSbsAct(new Date());
                        count++;
                    } else {
                        logger.error("SBS�������,�����ص���ˮ�ų������ѯ��" + cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());
                        joblog.setJobdesc("SBS�������,�����ص���ˮ�ų������ѯ��");
                        cutpaydetl.setBillstatus(BillStatus.ACCOUNT_PEND.getCode());
                        cutpaydetl.setDateSbsAct(new Date());
                    }
                }

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


    //=================================================================================
    private List<T100109ResponseRecord> getZmdResponseRecords(BizType bizType) {
        T100109CTL ctl = new T100109CTL();

        // 0-δ�ۿ� 1-�ѿۿ�
        return ctl.start("0");
    }

    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               T100109ResponseRecord responseBean) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        cutpaydetl.setIouno(""); //��ݺ�
        cutpaydetl.setPoano("");  //�ڴκ�

        cutpaydetl.setContractno(""); //��ͬ��
        cutpaydetl.setPaybackdate(responseBean.getStdjhhkr()); //�ƻ�������

        //�ͻ���Ϣ
        cutpaydetl.setClientno(responseBean.getStdkhh());
        cutpaydetl.setClientname(responseBean.getStdkhmc());
        cutpaydetl.setClientid(responseBean.getStdzjh()); //���֤��

        //SBS������Ϣ
        cutpaydetl.setClientact(responseBean.getStddkzh());   //�����ʺ�

        //��������Ϣ
        cutpaydetl.setPaybackamt(new BigDecimal(responseBean.getStdhkje()));  //������
        cutpaydetl.setPrincipalamt(new BigDecimal("0.00")); //�����
        cutpaydetl.setInterestamt(new BigDecimal("0.00"));  //������Ϣ
        cutpaydetl.setPunitiveintamt(new BigDecimal("0.00"));//��Ϣ���
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//ΥԼ����
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//��Ϣ�������
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //������

        //�ʵ�����
        cutpaydetl.setBilltype("0");

        //����������Ϣ
        if (bizType.equals(BizType.ZMD)) {
            cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //Ĭ��Ϊ����
            cutpaydetl.setBiBankactno(responseBean.getStdhkzh());
            cutpaydetl.setBiBankactname(responseBean.getStdkhmc());
            cutpaydetl.setBiActopeningbank(responseBean.getStdyhh());
            cutpaydetl.setRemark3("ZMD ר����");
            //cutpaydetl.setBiProvince(responseBean.getStdyhsf());
            //cutpaydetl.setBiCity(xfapprepayment.getCity());
        } else {
            throw new RuntimeException("��������");
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
        cutpaydetl.setDateCmsGet(new Date());

        return cutpaydetl;
    }

    /**
     * ��д��Ϣϵͳ
     */
    public int writebackCutPayRecord2Zmd(List<FipCutpaydetl> cutpaydetlList, boolean isArchive) {
        int count = 0;
        T100110CTL t100110ctl = new T100110CTL();

        for (FipCutpaydetl detl : cutpaydetlList) {
            boolean txResult = false;
            FipCutpaydetl dbRecord = fipCutpaydetlMapper.selectByPrimaryKey(detl.getPkid());
            if (!detl.getRecversion().equals(dbRecord.getRecversion())) {
                throw new RuntimeException("�������󣺰汾�Ų�ͬ " + detl.getClientname() + detl.getPkid());
            }
            T100110RequestRecord record = new T100110RequestRecord();
            record.setStdkhmc(detl.getBiBankactname());
            record.setStdkhh(detl.getClientno());
            record.setStdjhhkr(detl.getPaybackdate());
            record.setStdhkje(detl.getPaybackamt().toString());
            record.setStdrtncode(detl.getTxRetcode());
            record.setStdrtnmsg(detl.getTxRetmsg());

            String billStatus = detl.getBillstatus();
            if (BillStatus.CUTPAY_SUCCESS.getCode().equals(billStatus)) {//ע��״̬�Ǵ��۳ɹ�
                record.setStdkkcg("1");
            } else if (BillStatus.CUTPAY_FAILED.getCode().equals(billStatus)) {//ע��״̬�� ��������ʧ��
                record.setStdkkcg("2");
            } else {
                continue;
            }

            //���ʷ��ʹ���
            txResult = t100110ctl.start(record);

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
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(detl.getPkid());
            log.setJobname("��д����");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);

            if (txResult) { //��д�ɹ�
                detl.setWritebackflag("1");
                if (isArchive) {
                    detl.setArchiveflag("1");   //��д��� ���浵����
                }
                detl.setDateCmsPut(new Date());
                log.setJobdesc("��д����ɹ�");
                count++;
            } else {
                detl.setWritebackflag("0");
                log.setJobdesc("����ʧ��");
            }
            fipJoblogMapper.insert(log);
            detl.setRecversion(detl.getRecversion() + 1);
            fipCutpaydetlMapper.updateByPrimaryKey(detl);
        }
        return count;
    }


    //���ո�����״̬����δ��д�ļ�¼
    public List<FipCutpaydetl> selectDetlsByBatRecord(FipCutpaybat cutpaybat, BillStatus status) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andTxpkgSnEqualTo(cutpaybat.getTxpkgSn())
                .andBillstatusEqualTo(status.getCode())
                .andWritebackflagEqualTo("0");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    //�����浵����������recversion
    public void processArchive(List<FipCutpaydetl> cutpaydetlList){
        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            cutpaydetl.setArchiveflag("1");
            fipCutpaydetlMapper.updateByPrimaryKeySelective(cutpaydetl);
        }
    }

    //============================================
    private void batchInsertLogByBatchno(String batchno) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBatchSnEqualTo(batchno);
        List<FipCutpaydetl> fipCutpaydetlList = fipCutpaydetlMapper.selectByExample(example);

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
            log.setJobname("�½���¼");
            log.setJobdesc("�»�ȡ���ۼ�¼");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }
}
