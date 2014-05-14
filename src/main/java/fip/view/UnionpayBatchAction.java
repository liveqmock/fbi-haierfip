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

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaybat> needQueryBatlList;
    private List<FipCutpaydetl> failureDetlList;
    private List<FipCutpaydetl> successDetlList;
    private List<FipCutpaydetl> actDetlList;

    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl[] selectedRecords;
    private FipCutpaydetl selectedRecord;

    private FipCutpaybat selectedSendableRecord;
    private FipCutpaybat[] selectedSendableRecords;
    private FipCutpaybat[] selectedQueryRecords;
    private FipCutpaybat selectedQueryRecord;

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
    @ManagedProperty(value = "#{unipayService}")
    private UnipayService unipayService;
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
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    private void initDataList() {
        detlList = billManagerService.selectRecords4UnipayBatch(bizType, BillStatus.INIT);
        //detlList.addAll(billManagerService.selectRecords4UnipayBatch(this.bizType, BillStatus.RESEND_PEND));

        sendablePkgList = batchPkgService.selectSendableBatchs(bizType, CutpayChannel.UNIPAY, TxSendFlag.UNSEND);
        needQueryBatlList = batchPkgService.selectNeedConfirmBatchRecords(bizType, CutpayChannel.UNIPAY);
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
            MessageUtil.addWarn("记录为空，无法打包！");
            return null;
        }
        try {
            batchPkgService.packUnipayBatchPkg(bizType, detlList);
            initDataList();
            MessageUtil.addInfo("数据批量打包完成！");
        } catch (Exception e) {
            MessageUtil.addError("数据批量打包出现错误！" + e.getMessage());
        }

        return null;
    }

    public String onTxPkgMulti() {
        if (selectedRecords.length <= 0) {
            MessageUtil.addWarn("未选中记录，无法打包！");
            return null;
        }
        try {
            batchPkgService.packUnipayBatchPkg(bizType, Arrays.asList(selectedRecords));
            initDataList();
            MessageUtil.addInfo("数据批量打包完成！");
        } catch (Exception e) {
            initDataList();
            MessageUtil.addError("数据批量打包出现错误！" + e.getMessage());
        }
        return null;
    }


    public String onSendRequestAll() {
        if (sendablePkgList == null || sendablePkgList.isEmpty()) {
            MessageUtil.addWarn("没有可发送批量数据包！");
            return null;
        }
        try {
            for (FipCutpaybat pkg : sendablePkgList) {
                processOneBatchRequestRecord(pkg);
            }
            MessageUtil.addInfo("数据发送处理结束！");
        } catch (Exception e) {
            MessageUtil.addError("数据发送处理出现错误！" + e.getMessage());
        }
        initDataList();
        return null;
    }

    public String onSendRequestMulti() {
        if (selectedSendableRecords == null || selectedSendableRecords.length <= 0) {
            MessageUtil.addWarn("没有或未选中要发送的批量数据包！");
            return null;
        }
        try {
            for (FipCutpaybat pkg : selectedSendableRecords) {
                processOneBatchRequestRecord(pkg);
            }
            MessageUtil.addInfo("数据发送处理结束！");
        } catch (Exception e) {
            MessageUtil.addError("数据发送处理出现错误！" + e.getMessage());
        }
        initDataList();
        return null;
    }

    /**
     * 解包
     * @return
     */
    public String onUnpackMulti() {
        if (selectedSendableRecords == null || selectedSendableRecords.length <= 0) {
            MessageUtil.addWarn("没有或未选中要解包的批量数据包！");
            return null;
        }
        for (FipCutpaybat pkg : selectedSendableRecords) {
            batchPkgService.unpackOneBatchPkg(pkg);
        }
        initDataList();
        MessageUtil.addInfo("数据解包处理结束！");
        return null;
    }
    public String onUnpackMultiForQryList() {
        if (selectedQueryRecords == null || selectedQueryRecords.length <= 0) {
            MessageUtil.addWarn("没有或未选中要解包的批量数据包！");
            return null;
        }
        for (FipCutpaybat pkg : selectedQueryRecords) {
            batchPkgService.unpackOneBatchPkg(pkg);
        }
        initDataList();
        MessageUtil.addInfo("数据解包处理结束！");
        return null;
    }

    private void processOneBatchRequestRecord(FipCutpaybat batpkg) {
        String pkid = batpkg.getTxpkgSn();
        try {
            unipayService.sendAndRecvBatchTxnMessage(batpkg);
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文完成。");
        } catch (Exception e) {
            MessageUtil.addError("数据发送异常，请检查系统线路重新发送！");
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文失败." + e.getMessage());
            throw new RuntimeException("发送银联扣款请求报文失败." + e.getMessage());
        }
    }


    public String onQueryAll() {
        if (needQueryBatlList.isEmpty()) {
            MessageUtil.addWarn("没有可查询批量代扣数据包！");
            return null;
        }
        try {
            for (FipCutpaybat xfactcutpaybat : needQueryBatlList) {
                processOneQueryRecord(xfactcutpaybat);
            }
            MessageUtil.addInfo("查询交易请求处理完成！请查看返回的处理结果。");
        } catch (Exception e) {
            MessageUtil.addError("数据查询处理出现错误！" + e.getMessage());
        }
        initDataList();
        return null;
    }

    public String onQueryMulti() {
        if (selectedQueryRecords == null || selectedQueryRecords.length <= 0) {
            MessageUtil.addWarn("没有或未选择要查询的批量数据包！");
            return null;
        }
        try {
            for (FipCutpaybat xfactcutpaybat : this.selectedQueryRecords) {
                processOneQueryRecord(xfactcutpaybat);
            }
            MessageUtil.addInfo("查询交易请求处理完成！请查看返回的处理结果。");
        } catch (Exception e) {
            MessageUtil.addError("数据查询处理出现错误！" + e.getMessage());
        }
        initDataList();
        return null;
    }

    private void processOneQueryRecord(FipCutpaybat record) {
        String txPkgSn = record.getTxpkgSn();
        try {
            unipayService.sendAndRecvBatchDatagramQueryMessage(record);
            appendNewJoblog(txPkgSn, "发起银联查询交易", "银联查询交易号：200001");
        } catch (Exception e) {
            MessageUtil.addError("数据发送异常，请检查系统线路重新发送！");
            appendNewJoblog(txPkgSn, "发起银联查询交易", "银联交易号200001: 发送失败." + e.getMessage());
            throw new RuntimeException("数据发送异常，请检查系统线路重新发送！");
        }
    }


    public String onConfirmAccountAll() {
        if (successDetlList.isEmpty()) {
            MessageUtil.addWarn("记录为空！");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                cmsService.obtainMerchantActnoFromCms(this.successDetlList);
            }
            billManagerService.updateCutpaydetlBillStatus(this.successDetlList, BillStatus.ACCOUNT_PEND);
            initDataList();
            MessageUtil.addInfo("确认完成！");
        } catch (Exception e) {
            MessageUtil.addError("数据确认出现错误！" + e.getMessage());
        }

        return null;
    }

    public String onConfirmAccountMulti() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("请选择记录.");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                cmsService.obtainMerchantActnoFromCms(Arrays.asList(this.selectedConfirmAccountRecords));
            }
            billManagerService.updateCutpaydetlBillStatus(Arrays.asList(this.selectedConfirmAccountRecords), BillStatus.ACCOUNT_PEND);
            initDataList();
            MessageUtil.addInfo("确认完成！");
        } catch (Exception e) {
            MessageUtil.addError("数据确认出现错误！" + e.getMessage());
        }
        return null;
    }


    public String onExportFailureList() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            String excelFilename = "批量代扣失败记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, this.failureDetlList);
        }
        return null;
    }

    public String onExportSuccessList() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            String excelFilename = "批量代扣成功记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, this.successDetlList);
        }
        return null;
}


    /**
     * 存档失败记录请当
     *
     * @return
     */
    public String onArchiveAllFailureRecord() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("失败记录集为空。");
            return null;
        } else {
            int count = billManagerService.archiveBills(this.failureDetlList);
            initDataList();
            MessageUtil.addWarn("更新记录条数：" + count);
        }
        return null;
    }

    public String onArchiveMultiFailureRecord() {
        if (this.selectedFailRecords.length == 0) {
            MessageUtil.addWarn("请选择记录...");
            return null;
        } else {
            int count = billManagerService.archiveBills(Arrays.asList(this.selectedFailRecords));
            initDataList();
            MessageUtil.addWarn("更新记录条数：" + count);
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

    public List<FipCutpaybat> getNeedQueryBatlList() {
        return needQueryBatlList;
    }

    public void setNeedQueryBatlList(List<FipCutpaybat> needQueryBatlList) {
        this.needQueryBatlList = needQueryBatlList;
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
}
