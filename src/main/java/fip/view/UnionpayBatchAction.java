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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UnionpayBatchAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayBatchAction.class);

    protected List<FipCutpaydetl> detlList;
    protected List<FipCutpaybat> needQueryBatList;
    protected List<FipCutpaybat> historyBatList;
    protected List<FipCutpaydetl> failureDetlList;
    protected List<FipCutpaydetl> successDetlList;
    protected List<FipCutpaydetl> actDetlList;

    protected FipCutpaydetl detlRecord = new FipCutpaydetl();
    protected FipCutpaydetl[] selectedRecords;
    protected FipCutpaydetl selectedRecord;

    protected FipCutpaybat selectedSendableRecord;
    protected FipCutpaybat[] selectedSendableRecords;
    protected FipCutpaybat[] selectedQueryRecords;
    protected FipCutpaybat[] selectedHistoryRecords;
    protected FipCutpaybat selectedQueryRecord;
    protected FipCutpaybat selectedHistoryRecord;

    protected FipCutpaydetl[] selectedFailRecords;
    protected FipCutpaydetl[] selectedAccountRecords;
    protected FipCutpaydetl[] selectedConfirmAccountRecords;

    protected int totalcount;
    protected String totalamt;
    protected int totalFailureCount;
    protected String totalFailureAmt;
    protected int totalSuccessCount;
    protected String totalSuccessAmt;
    protected int totalAccountCount;
    protected String totalAccountAmt;

    protected BillStatus status = BillStatus.CUTPAY_FAILED;
    protected TxpkgStatus batchStatus = TxpkgStatus.QRY_PEND;
    protected UnipayPkgType txnType = UnipayPkgType.SYNC;

    @ManagedProperty(value = "#{billManagerService}")
    protected BillManagerService billManagerService;
    @ManagedProperty(value = "#{jobLogService}")
    protected JobLogService jobLogService;

    @ManagedProperty(value = "#{batchPkgService}")
    protected BatchPkgService batchPkgService;
    @ManagedProperty(value = "#{unipayService}")
    protected UnipayService unipayService;
    @ManagedProperty(value = "#{unipayDepService}")
    protected UnipayDepService unipayDepService;

    protected String bizid;
    protected String pkid;
    protected BizType bizType;
    protected String channelBizId;   //��������ʱ��ʹ�õ��̻��Ŷ�Ӧ��bizid���û������̻��Ž��д���

    protected String userid, username;

    protected List<FipCutpaybat> sendablePkgList;
    protected List<FipCutpaydetl> batchInfoList;

    @PostConstruct
    public void init() {
        try {
            OperatorManager om = SystemService.getOperatorManager();
            this.userid = om.getOperatorId();
            this.username = om.getOperatorName();

            this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");

            this.channelBizId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("channel_bizid");
            if (StringUtils.isEmpty(this.channelBizId)) {
                this.channelBizId = this.bizid;
            }

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

    protected void initDataList() {
        detlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.INIT);
        //detlList.addAll(billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.RESEND_PEND));

        sendablePkgList = batchPkgService.selectSendableBatchs(bizType, CutpayChannel.UNIPAY, TxSendFlag.UNSEND);

        needQueryBatList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);
        historyBatList = batchPkgService.selectHistoryBatchRecordList(bizType, CutpayChannel.UNIPAY, TxpkgStatus.DEAL_SUCCESS);

        failureDetlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.CUTPAY_FAILED);
        successDetlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.CUTPAY_SUCCESS);
        actDetlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.ACCOUNT_PEND);

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

    public String onTxPkgAll() {
        if (detlList.isEmpty()) {
            MessageUtil.addWarn("��¼Ϊ�գ��޷������");
            return null;
        }
        try {
            batchPkgService.packUnipayBatchPkg(bizType, detlList, channelBizId);
            initDataList();
            MessageUtil.addInfo("�������������ɣ�");
        } catch (Exception e) {
            MessageUtil.addError("��������������ִ���" + e.getMessage());
        }

        return null;
    }

    public String onTxPkgMulti() {
        if (selectedRecords.length <= 0) {
            MessageUtil.addWarn("δѡ�м�¼���޷������");
            return null;
        }
        try {
            batchPkgService.packUnipayBatchPkg(bizType, Arrays.asList(selectedRecords),channelBizId);
            initDataList();
            MessageUtil.addInfo("�������������ɣ�");
        } catch (Exception e) {
            initDataList();
            MessageUtil.addError("��������������ִ���" + e.getMessage());
        }
        return null;
    }


    public String onSendRequestAll() {
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
            MessageUtil.addError("���ݷ��ʹ�����ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    public String onSendRequestMulti() {
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
            MessageUtil.addError("���ݷ��ʹ�����ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    /**
     * ���
     * @return
     */
    public String onUnpackMulti() {
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
    public String onUnpackMultiForQryList() {
        if (selectedQueryRecords == null || selectedQueryRecords.length <= 0) {
            MessageUtil.addWarn("û�л�δѡ��Ҫ������������ݰ���");
            return null;
        }
        for (FipCutpaybat pkg : selectedQueryRecords) {
            batchPkgService.unpackOneBatchPkg(pkg);
        }
        initDataList();
        MessageUtil.addInfo("���ݽ�����������");
        return null;
    }

    private void processOneBatchRequestRecord(FipCutpaybat batpkg) {
        String pkid = batpkg.getTxpkgSn();
        try {
            //unipayService.sendAndRecvBatchTxnMessage(batpkg);
            unipayDepService.sendAndRecvT1001003Message(batpkg);
            appendNewJoblog(pkid, "���Ϳۿ�����", "���������ۿ���������ɡ�");
        } catch (Exception e) {
            MessageUtil.addError("���ݷ����쳣������ϵͳ��·���·��ͣ�");
            appendNewJoblog(pkid, "���Ϳۿ�����", "���������ۿ�������ʧ��." + e.getMessage());
            throw new RuntimeException("���������ۿ�������ʧ��." + e.getMessage());
        }
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
            MessageUtil.addError("���ݲ�ѯ������ִ���" + e.getMessage());
        }
        initDataList();
        return null;
    }

    private void processOneQueryRecord(FipCutpaybat record) {
        String txPkgSn = record.getTxpkgSn();
        try {
            //unipayService.sendAndRecvBatchDatagramQueryMessage(record);
            unipayDepService.sendAndRecvCutpayT1003003Message(record);
        } catch (Exception e) {
            MessageUtil.addError("���ݷ����쳣������ϵͳ��·���·��ͣ�" + e.getMessage());
            appendNewJoblog(txPkgSn, "�����������׽����ѯ", "����ʧ��." + e.getMessage());
        }
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
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("ʧ�ܼ�¼��Ϊ�ա�");
            return null;
        } else {
            int count = billManagerService.archiveBills(this.failureDetlList);
            initDataList();
            MessageUtil.addWarn("���¼�¼������" + count);
        }
        return null;
    }

    public String onArchiveMultiFailureRecord() {
        if (this.selectedFailRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼...");
            return null;
        } else {
            int count = billManagerService.archiveBills(Arrays.asList(this.selectedFailRecords));
            initDataList();
            MessageUtil.addWarn("���¼�¼������" + count);
        }
        return null;
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
        for (FipCutpaydetl record : records) {
            totalamt = totalamt.add(record.getPaybackamt());
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

    public UnipayService getUnipayService() {
        return unipayService;
    }

    public void setUnipayService(UnipayService unipayService) {
        this.unipayService = unipayService;
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

    public UnipayDepService getUnipayDepService() {
        return unipayDepService;
    }

    public void setUnipayDepService(UnipayDepService unipayDepService) {
        this.unipayDepService = unipayDepService;
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

}

