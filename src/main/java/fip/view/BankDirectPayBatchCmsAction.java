package fip.view;

import fip.common.SystemService;
import fip.common.constant.*;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.*;
import fip.view.common.JxlsManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.security.OperatorManager;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * ����ֱ������ ��������  for �Ŵ�����ϵͳ
 * User: zhanrui
 * Date: 2012-08-28
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class BankDirectPayBatchCmsAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(BankDirectPayBatchCmsAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaybat> needQueryBatList;
    private List<FipCutpaybat> historyBatList;
    private List<FipCutpaydetl> failureDetlList;
    private List<FipCutpaydetl> successDetlList;
    private List<FipCutpaydetl> actDetlList;

    private List<FipCutpaydetl> filteredDetlList;
    private List<FipCutpaydetl> filteredFailureDetlList;
    private List<FipCutpaydetl> filteredSuccessDetlList;
    private List<FipCutpaydetl> filteredActDetlList;

    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl[] selectedRecords;
    private FipCutpaydetl selectedRecord;

    private FipCutpaybat selectedSendableRecord;
    private FipCutpaybat[] selectedSendableRecords;
    private FipCutpaybat[] selectedQueryRecords;
    private FipCutpaybat[] selectedHistoryRecords;
    private FipCutpaybat selectedQueryRecord;
    private FipCutpaybat selectedHistoryRecord;

    private FipCutpaydetl[] selectedFailRecords;
    private FipCutpaydetl[] selectedAccountRecords;
    private FipCutpaydetl[] selectedConfirmAccountRecords;

    private int totalcount;
    private String totalamt;
    private int totalFailureCount;
    private String totalFailureAmt;
    private int totalSuccessCount;
    private String totalSuccessAmt;
    private int totalAccountCount;
    private String totalAccountAmt;

    private BillStatus status = BillStatus.CUTPAY_FAILED;
    private TxpkgStatus batchStatus = TxpkgStatus.QRY_PEND;
    private UnipayPkgType txnType = UnipayPkgType.SYNC;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    @ManagedProperty(value = "#{batchPkgService}")
    private BatchPkgService batchPkgService;
    @ManagedProperty(value = "#{bankDirectPayDepService}")
    private BankDirectPayDepService bankService;
    @ManagedProperty(value = "#{cmsService}")
    private CmsService cmsService;

    private String bizid;
    private String pkid;
    private BizType bizType;

    private String userid, username;

    private List<FipCutpaybat> sendablePkgList;
    private List<FipCutpaydetl> batchInfoList;

    @PostConstruct
    public void init() {
        try {
            OperatorManager om = SystemService.getOperatorManager();
            this.userid = om.getOperatorId();
            this.username = om.getOperatorName();

            this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");

            if (!StringUtils.isEmpty(this.bizid)) {
                this.bizType = BizType.valueOf(this.bizid);
                initDataList();
            }
        } catch (Exception e) {
            logger.error("��ʼ��ʱ���ִ���");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "��ʼ��ʱ���ִ���", "�������ݿ�������⡣"));
        }

    }

    private synchronized void initDataList() {
        detlList = billManagerService.selectRecords4NoChannelBatch(bizType, BillStatus.INIT, BankCode.JIANSHE);
        sendablePkgList = batchPkgService.selectBatchRecordList(bizType, CutpayChannel.NONE, TxpkgStatus.SEND_PEND);
        historyBatList = batchPkgService.selectHistoryBatchRecordList(bizType, CutpayChannel.NONE, TxpkgStatus.DEAL_SUCCESS);
        //sendablePkgList = batchPkgService.selectSendableBatchs(bizType, CutpayChannel.NONE, TxSendFlag.UNSEND);

        failureDetlList = billManagerService.selectRecords4NoChannelBatch(bizType, BillStatus.CUTPAY_FAILED, BankCode.JIANSHE);
        filteredFailureDetlList = failureDetlList;

        needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.NONE);

        successDetlList = billManagerService.selectRecords4NoChannelBatch(bizType, BillStatus.CUTPAY_SUCCESS, BankCode.JIANSHE);
        filteredSuccessDetlList = successDetlList;

        actDetlList = billManagerService.selectRecords4NoChannelBatch(bizType, BillStatus.ACCOUNT_PEND, BankCode.JIANSHE);

        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalSuccessCount = successDetlList.size();
        this.totalFailureAmt = sumTotalAmt(failureDetlList);
        this.totalFailureCount = failureDetlList.size();
        this.totalAccountAmt = sumTotalAmt(actDetlList);
        this.totalAccountCount = actDetlList.size();
    }

    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList) {
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public synchronized String onTxPkgAll() {
        if (detlList.isEmpty()) {
            MessageUtil.addWarn("��¼Ϊ�գ��޷������");
            return null;
        }
        try {
            batchPkgService.packCcbBatchPkg(bizType, detlList);
            MessageUtil.addInfo("�������������ɣ�");
        } catch (Exception e) {
            logger.error("��������������ִ���", e);
            MessageUtil.addError("��������������ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    //20121121 zhanrui
    //�����£���һ�����Σ�����ƥ�䣬ֻ����Ѿ��۹���
    public synchronized String onTxPkgAll4FilterByLastPoano() {
        if (detlList.isEmpty()) {
            MessageUtil.addWarn("��¼Ϊ�գ��޷������");
            return null;
        }
        try {
            List<FipCutpaydetl> filtedList = batchPkgService.selectRecordByLastPoano(bizType, detlList);
            if (filtedList.isEmpty()) {
                MessageUtil.addWarn("��ƥ��ļ�¼��");
                return null;
            }
            batchPkgService.packCcbBatchPkg(bizType, filtedList);
            MessageUtil.addInfo("�������������ɣ�");
        } catch (Exception e) {
            logger.error("��������������ִ���", e);
            MessageUtil.addError("��������������ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    public synchronized String onTxPkgMulti() {
        if (selectedRecords.length <= 0) {
            MessageUtil.addWarn("δѡ�м�¼���޷������");
            return null;
        }
        try {
            batchPkgService.packCcbBatchPkg(bizType, Arrays.asList(selectedRecords));
            MessageUtil.addInfo("�������������ɣ�");
        } catch (Exception e) {
            logger.error("��������������ִ���", e);
            MessageUtil.addError("��������������ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }
    public synchronized String onChangeChannel() {
        if (selectedRecords.length <= 0) {
            MessageUtil.addWarn("δѡ�м�¼��");
            return null;
        }
        try {
            billManagerService.chgChannel(Arrays.asList(selectedRecords), CutpayChannel.UNIPAY);
            MessageUtil.addInfo("�������������ɣ�");
        } catch (Exception e) {
            logger.error("��������������ִ���", e);
            MessageUtil.addError("��������������ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }


    public synchronized String onSendRequestAll() {
        if (sendablePkgList == null || sendablePkgList.isEmpty()) {
            MessageUtil.addWarn("û�пɷ����������ݰ���");
            return null;
        }
        try {
            for (FipCutpaybat pkg : sendablePkgList) {
                processOneBatchRequestRecord(pkg);
            }
            MessageUtil.addInfo("���ݷ��ʹ��������");
        } catch (Exception e) {
            logger.error("���ݷ��ʹ�����ִ���", e);
            MessageUtil.addError("���ݷ��ʹ�����ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    public synchronized String onSendRequestMulti() {
        if (selectedSendableRecords == null || selectedSendableRecords.length <= 0) {
            MessageUtil.addWarn("û�л�δѡ��Ҫ���͵��������ݰ���");
            return null;
        }
        try {
            for (FipCutpaybat pkg : selectedSendableRecords) {
                processOneBatchRequestRecord(pkg);
            }
            MessageUtil.addInfo("���ݷ��ʹ��������");
        } catch (Exception e) {
            logger.error("���ݷ��ʹ�����ִ���", e);
            MessageUtil.addError("���ݷ��ʹ�����ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    /**
     * ���
     *
     * @return
     */
    public synchronized String onUnpackMulti() {
        if (selectedSendableRecords == null || selectedSendableRecords.length <= 0) {
            MessageUtil.addWarn("û�л�δѡ��Ҫ������������ݰ���");
            return null;
        }
        for (FipCutpaybat pkg : selectedSendableRecords) {
            batchPkgService.unpackOneBatchPkg(pkg);
        }
        initDataList();
        MessageUtil.addInfo("���ݽ�����������");
        return null;
    }

    public synchronized String onUnpackMultiForQryList() {
        if (selectedQueryRecords == null || selectedQueryRecords.length <= 0) {
            MessageUtil.addWarn("û�л�δѡ��Ҫ������������ݰ���");
            return null;
        }

/*
        for (FipCutpaybat pkg : selectedQueryRecords) {
            if (pkg.getTxpkgStatus().equals(TxpkgStatus.QRY_PEND.getCode())) {
                if (pkg.getSendflag().equals(TxSendFlag.UNSEND.getCode())) {
                }
            }
        }
*/

        try {
            for (FipCutpaybat pkg : selectedQueryRecords) {
                batchPkgService.unpackOneBatchPkg(pkg);
            }
        } catch (Exception e) {
            MessageUtil.addError("����������" + e.getMessage());
        }
        initDataList();
        MessageUtil.addInfo("���ݽ�����������");
        return null;
    }

    private void processOneBatchRequestRecord(FipCutpaybat batpkg) {
        String pkid = batpkg.getTxpkgSn();
        SimpleDateFormat sFmt = new SimpleDateFormat("yyyyMMdd");
        String txnDate = sFmt.format(new Date());

        bankService.performRequestHandleBatchPkg(txnDate, batpkg, this.userid, this.username);
        appendNewJoblog(pkid, "���Ϳۿ�����", "���Ϳۿ���������ɡ�");
    }


    public String onQueryAll() {
        if (needQueryBatList.isEmpty()) {
            MessageUtil.addWarn("û�пɲ�ѯ�����������ݰ���");
            return null;
        }
        try {
            for (FipCutpaybat xfactcutpaybat : needQueryBatList) {
                processOneQueryRecord(xfactcutpaybat);
            }
            MessageUtil.addInfo("��ѯ������������ɣ���鿴���صĴ�������");
        } catch (Exception e) {
            logger.error("���ݷ��ʹ�����ִ���", e);
            MessageUtil.addError("���ݲ�ѯ������ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    public String onQueryMulti() {
        if (selectedQueryRecords == null || selectedQueryRecords.length <= 0) {
            MessageUtil.addWarn("û�л�δѡ��Ҫ��ѯ���������ݰ���");
            return null;
        }
        try {
            for (FipCutpaybat xfactcutpaybat : this.selectedQueryRecords) {
                processOneQueryRecord(xfactcutpaybat);
            }
            MessageUtil.addInfo("��ѯ������������ɣ���鿴���صĴ�������");
        } catch (Exception e) {
            logger.error("���ݷ��ʹ�����ִ���", e);
            MessageUtil.addError("���ݲ�ѯ������ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    private void processOneQueryRecord(FipCutpaybat cutpayBat) {
        SimpleDateFormat sFmt = new SimpleDateFormat("yyyyMMdd");
        String txnDate = sFmt.format(new Date());

        String txPkgSn = cutpayBat.getTxpkgSn();
        bankService.performResultQueryBatchPkg(txnDate, cutpayBat, this.userid, this.username);
        appendNewJoblog(txPkgSn, "�����ѯ����", "");
    }


    public String onConfirmAccountAll() {
        if (filteredSuccessDetlList.isEmpty()) {
            MessageUtil.addWarn("��¼Ϊ�գ�");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                cmsService.obtainMerchantActnoFromCms(this.filteredSuccessDetlList);
            }
            billManagerService.updateCutpaydetlBillStatus(this.filteredSuccessDetlList, BillStatus.ACCOUNT_PEND);
            initDataList();
            MessageUtil.addInfo("ȷ��������ɣ�");
        } catch (Exception e) {
            MessageUtil.addError("��������ȷ�ϳ��ִ���" + e.getMessage());
        }

        return null;
    }

    public String onConfirmAccountMulti() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼.");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                cmsService.obtainMerchantActnoFromCms(Arrays.asList(this.selectedConfirmAccountRecords));
            }
            billManagerService.updateCutpaydetlBillStatus(Arrays.asList(this.selectedConfirmAccountRecords), BillStatus.ACCOUNT_PEND);
            initDataList();
            MessageUtil.addInfo("ȷ��������ɣ�");
        } catch (Exception e) {
            MessageUtil.addError("��������ȷ�ϳ��ִ���" + e.getMessage());
        }
        return null;
    }


    public String onExportFailureList() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return null;
        } else {
            String excelFilename = "��������ʧ�ܼ�¼�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, this.failureDetlList);
        }
        return null;
    }

    public String onExportSuccessList() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return null;
        } else {
            String excelFilename = "�������۳ɹ���¼�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, this.successDetlList);
        }
        return null;
    }


    /**
     * �浵ʧ�ܼ�¼�뵱
     *
     * @return
     */
    public String onArchiveAllFailureRecord() {
        List<String> rtnMsgs = new ArrayList<String>();

        try {
            if (this.filteredFailureDetlList.size() == 0) {
                MessageUtil.addWarn("ʧ�ܼ�¼��Ϊ�ա�");
                return null;
            } else {
                //20130412 zr ��ʧ�ܼ�¼�Ĵ浵����ʱ�Զ������Ŵ�ϵͳ��д
                //int count = billManagerService.archiveBills(this.filteredFailureDetlList);
                cmsService.writebackCutPayRecord2CMS_ForFailureReord(filteredFailureDetlList, rtnMsgs);
                if (rtnMsgs.size() == 0) {
                    MessageUtil.addInfo("�鵵���д������ɡ�");
                }
                unlockIntr(this.filteredFailureDetlList);
            }
        } catch (Exception e) {
            MessageUtil.addError("���ݴ������" + e.getMessage());
        }
        for (String rtnMsg : rtnMsgs) {
            MessageUtil.addError(rtnMsg);
        }
        initDataList();
        return null;
    }

    public String onArchiveMultiFailureRecord() {
        List<String> rtnMsgs = new ArrayList<String>();
        try {
            if (this.selectedFailRecords.length == 0) {
                MessageUtil.addWarn("��ѡ���¼...");
                return null;
            } else {
                List<FipCutpaydetl> fipCutpaydetlList = Arrays.asList(this.selectedFailRecords);
                //20130412 zr ��ʧ�ܼ�¼�Ĵ浵����ʱ�Զ������Ŵ�ϵͳ��д
                //int count = billManagerService.archiveBills(fipCutpaydetlList);
                cmsService.writebackCutPayRecord2CMS_ForFailureReord(fipCutpaydetlList, rtnMsgs);
                if (rtnMsgs.size() == 0) {
                    MessageUtil.addInfo("�鵵���д������ɡ�");
                }
                unlockIntr(fipCutpaydetlList);
            }
        } catch (Exception e) {
            MessageUtil.addError("���ݴ������" + e.getMessage());
        }
        for (String rtnMsg : rtnMsgs) {
            MessageUtil.addError(rtnMsg);
        }
        initDataList();
        return null;
    }

    private void unlockIntr(List<FipCutpaydetl> failureDetlList) {
        //20130109 zr �������ڵļ�¼ ���Զ���������
        for (FipCutpaydetl fipCutpaydetl : failureDetlList) {
            if (fipCutpaydetl.getBilltype().equals(BillType.OVERDUE.getCode())) {
                if (cmsService.unlockIntr4Overdue(fipCutpaydetl)) {
                    MessageUtil.addInfo("��Ϣ�����ɹ���" + fipCutpaydetl.getIouno() + fipCutpaydetl.getClientname());
                }else{
                    MessageUtil.addError("��Ϣ��������" + fipCutpaydetl.getIouno() + fipCutpaydetl.getClientname());
                }
            }
        }
    }

    public String reset() {
        this.detlRecord = new FipCutpaydetl();
        return null;
    }


    private void initAmt() {
//        totalamt = new BigDecimal(0);
    }

    public String qryDetailsByTxpkgSn() {
        String txpkgSn = selectedSendableRecord.getTxpkgSn();
        batchInfoList = billManagerService.selectRecordsByTxpkgSn(txpkgSn);
        return null;
    }

/*
    private void countAmt(List<FipCutpaydetl> records) {
        initAmt();
        for (FipCutpaydetl cutpayBat : records) {
            totalamt = totalamt.add(cutpayBat.getPaybackamt());
        }
    }
*/


    private void appendNewJoblog(String pkid, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, "fip_cutpaybat", jobname, jobdesc, userid, username);
    }

    //====================================================================================

    public List<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipCutpaydetl> detlList) {
        this.detlList = detlList;
    }

    public List<FipCutpaybat> getNeedQueryBatList() {
        return needQueryBatList;
    }

    public void setNeedQueryBatList(List<FipCutpaybat> needQueryBatList) {
        this.needQueryBatList = needQueryBatList;
    }

    public List<FipCutpaydetl> getFailureDetlList() {
        return failureDetlList;
    }

    public void setFailureDetlList(List<FipCutpaydetl> failureDetlList) {
        this.failureDetlList = failureDetlList;
    }

    public List<FipCutpaydetl> getSuccessDetlList() {
        return successDetlList;
    }

    public void setSuccessDetlList(List<FipCutpaydetl> successDetlList) {
        this.successDetlList = successDetlList;
    }

    public FipCutpaydetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipCutpaydetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public FipCutpaydetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaydetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public FipCutpaybat getSelectedSendableRecord() {
        return selectedSendableRecord;
    }

    public void setSelectedSendableRecord(FipCutpaybat selectedSendableRecord) {
        this.selectedSendableRecord = selectedSendableRecord;
    }

    public FipCutpaybat[] getSelectedSendableRecords() {
        return selectedSendableRecords;
    }

    public void setSelectedSendableRecords(FipCutpaybat[] selectedSendableRecords) {
        this.selectedSendableRecords = selectedSendableRecords;
    }

    public FipCutpaybat[] getSelectedQueryRecords() {
        return selectedQueryRecords;
    }

    public void setSelectedQueryRecords(FipCutpaybat[] selectedQueryRecords) {
        this.selectedQueryRecords = selectedQueryRecords;
    }

    public FipCutpaybat getSelectedQueryRecord() {
        return selectedQueryRecord;
    }

    public void setSelectedQueryRecord(FipCutpaybat selectedQueryRecord) {
        this.selectedQueryRecord = selectedQueryRecord;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

/*
    public BigDecimal getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(BigDecimal totalamt) {
        this.totalamt = totalamt;
    }
*/

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }


    public String getBizid() {
        return bizid;
    }

    public void setBizid(String bizid) {
        this.bizid = bizid;
    }

    public String getPkid() {
        return pkid;
    }

    public void setPkid(String pkid) {
        this.pkid = pkid;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<FipCutpaybat> getSendablePkgList() {
        return sendablePkgList;
    }

    public void setSendablePkgList(List<FipCutpaybat> sendablePkgList) {
        this.sendablePkgList = sendablePkgList;
    }

    public List<FipCutpaydetl> getBatchInfoList() {
        return batchInfoList;
    }

    public void setBatchInfoList(List<FipCutpaydetl> batchInfoList) {
        this.batchInfoList = batchInfoList;
    }

    public BatchPkgService getBatchPkgService() {
        return batchPkgService;
    }

    public void setBatchPkgService(BatchPkgService batchPkgService) {
        this.batchPkgService = batchPkgService;
    }

    public BankDirectPayDepService getBankService() {
        return bankService;
    }

    public void setBankService(BankDirectPayDepService bankService) {
        this.bankService = bankService;
    }

    public List<FipCutpaydetl> getActDetlList() {
        return actDetlList;
    }

    public void setActDetlList(List<FipCutpaydetl> actDetlList) {
        this.actDetlList = actDetlList;
    }

    public FipCutpaydetl[] getSelectedConfirmAccountRecords() {
        return selectedConfirmAccountRecords;
    }

    public void setSelectedConfirmAccountRecords(FipCutpaydetl[] selectedConfirmAccountRecords) {
        this.selectedConfirmAccountRecords = selectedConfirmAccountRecords;
    }

    public CmsService getCmsService() {
        return cmsService;
    }

    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public int getTotalFailureCount() {
        return totalFailureCount;
    }

    public void setTotalFailureCount(int totalFailureCount) {
        this.totalFailureCount = totalFailureCount;
    }

    public String getTotalFailureAmt() {
        return totalFailureAmt;
    }

    public void setTotalFailureAmt(String totalFailureAmt) {
        this.totalFailureAmt = totalFailureAmt;
    }

    public int getTotalSuccessCount() {
        return totalSuccessCount;
    }

    public void setTotalSuccessCount(int totalSuccessCount) {
        this.totalSuccessCount = totalSuccessCount;
    }

    public String getTotalSuccessAmt() {
        return totalSuccessAmt;
    }

    public void setTotalSuccessAmt(String totalSuccessAmt) {
        this.totalSuccessAmt = totalSuccessAmt;
    }

    public int getTotalAccountCount() {
        return totalAccountCount;
    }

    public void setTotalAccountCount(int totalAccountCount) {
        this.totalAccountCount = totalAccountCount;
    }

    public String getTotalAccountAmt() {
        return totalAccountAmt;
    }

    public void setTotalAccountAmt(String totalAccountAmt) {
        this.totalAccountAmt = totalAccountAmt;
    }

    public FipCutpaydetl[] getSelectedFailRecords() {
        return selectedFailRecords;
    }

    public void setSelectedFailRecords(FipCutpaydetl[] selectedFailRecords) {
        this.selectedFailRecords = selectedFailRecords;
    }

    public FipCutpaydetl[] getSelectedAccountRecords() {
        return selectedAccountRecords;
    }

    public void setSelectedAccountRecords(FipCutpaydetl[] selectedAccountRecords) {
        this.selectedAccountRecords = selectedAccountRecords;
    }

    public TxpkgStatus getBatchStatus() {
        return batchStatus;
    }

    public void setBatchStatus(TxpkgStatus batchStatus) {
        this.batchStatus = batchStatus;
    }

    public UnipayPkgType getTxnType() {
        return txnType;
    }

    public void setTxnType(UnipayPkgType txnType) {
        this.txnType = txnType;
    }

    public List<FipCutpaybat> getHistoryBatList() {
        return historyBatList;
    }

    public void setHistoryBatList(List<FipCutpaybat> historyBatList) {
        this.historyBatList = historyBatList;
    }

    public FipCutpaybat[] getSelectedHistoryRecords() {
        return selectedHistoryRecords;
    }

    public void setSelectedHistoryRecords(FipCutpaybat[] selectedHistoryRecords) {
        this.selectedHistoryRecords = selectedHistoryRecords;
    }

    public FipCutpaybat getSelectedHistoryRecord() {
        return selectedHistoryRecord;
    }

    public void setSelectedHistoryRecord(FipCutpaybat selectedHistoryRecord) {
        this.selectedHistoryRecord = selectedHistoryRecord;
    }

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public List<FipCutpaydetl> getFilteredFailureDetlList() {
        return filteredFailureDetlList;
    }

    public void setFilteredFailureDetlList(List<FipCutpaydetl> filteredFailureDetlList) {
        this.filteredFailureDetlList = filteredFailureDetlList;
    }

    public List<FipCutpaydetl> getFilteredSuccessDetlList() {
        return filteredSuccessDetlList;
    }

    public void setFilteredSuccessDetlList(List<FipCutpaydetl> filteredSuccessDetlList) {
        this.filteredSuccessDetlList = filteredSuccessDetlList;
    }

    public List<FipCutpaydetl> getFilteredActDetlList() {
        return filteredActDetlList;
    }

    public void setFilteredActDetlList(List<FipCutpaydetl> filteredActDetlList) {
        this.filteredActDetlList = filteredActDetlList;
    }
}
