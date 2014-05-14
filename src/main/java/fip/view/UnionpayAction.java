package fip.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.gateway.unionpay.RtnMessageHandler;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.CmsService;
import fip.service.fip.JobLogService;
import fip.service.fip.UnipayService;
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
import java.util.*;

/**
 * 银联实时代扣处理.
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UnionpayAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayAction.class);

    private List<FipCutpaydetl> detlList;
    private FipCutpaydetl[] selectedRecords;

    private List<FipCutpaydetl> needQueryDetlList;
    private FipCutpaydetl[] selectedNeedQryRecords;

    private List<FipCutpaydetl> failureDetlList;
    private FipCutpaydetl[] selectedFailRecords;

    private List<FipCutpaydetl> successDetlList;
    private FipCutpaydetl[] selectedAccountRecords;
    private FipCutpaydetl[] selectedConfirmAccountRecords;

    private List<FipCutpaydetl> actDetlList;


    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl selectedRecord;


    private int totalcount;
    private String totalamt;
    private int totalFailureCount;
    private String totalFailureAmt;
    private int totalSuccessCount;
    private String totalSuccessAmt;
    private int totalAccountCount;
    private String totalAccountAmt;

    private BigDecimal totalPrincipalAmt;   //本金
    private BigDecimal totalInterestAmt;    //利息
    private BigDecimal totalFxjeAmt;    //罚息

    private Map<String, String> statusMap = new HashMap<String, String>();

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;
    @ManagedProperty(value = "#{rtnMessageHandler}")
    private RtnMessageHandler rtnMessageHandler;

    @ManagedProperty(value = "#{unipayService}")
    private UnipayService unipayService;
    @ManagedProperty(value = "#{cmsService}")
    private CmsService cmsService;


    private String bizid;
    private String pkid;
    private BizType bizType;


    private String userid, username;

    @PostConstruct
    public void init() {
        try {
            OperatorManager om = SystemService.getOperatorManager();
            this.userid = om.getOperatorId();
            this.username = om.getOperatorName();

            this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(this.bizid)) {
                this.bizType = BizType.valueOf(this.bizid);
                initList();
            }
        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public void initList() {
        detlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.INIT);
        detlList.addAll(billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.RESEND_PEND));
        needQueryDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        failureDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_FAILED);
        successDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_SUCCESS);
        actDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.ACCOUNT_PEND);
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

    public String onSendRequestAll() {
        for (FipCutpaydetl cutpaydetl : this.detlList) {
            if (!processOneCutpayRequestRecord(cutpaydetl)) break;
        }
        MessageUtil.addInfo("数据发送结束！请查看处理结果明细。");
        initList();
        return null;
    }

    public String onSendRequestMulti() {
        for (FipCutpaydetl cutpaydetl : this.selectedRecords) {
            if (!processOneCutpayRequestRecord(cutpaydetl)) break;
        }
        MessageUtil.addInfo("数据发送结束！请查看处理结果明细。");
        initList();
        return null;
    }

    public String onQueryAll() {
        if (needQueryDetlList.isEmpty()) {
            MessageUtil.addWarn("没有要发送的记录！");
            return null;
        }
        for (FipCutpaydetl cutpaydetl : this.needQueryDetlList) {
            if (!processOneQueryRecord(cutpaydetl)) break;
        }
        MessageUtil.addInfo("查询交易发送结束！请查看处理结果明细。");
        initList();
        return null;
    }

    public String onQueryMulti() {
        if (selectedNeedQryRecords == null || selectedNeedQryRecords.length <= 0) {
            MessageUtil.addWarn("没有或未选中要发送的记录！");
            return null;
        }
        for (FipCutpaydetl cutpaydetl : this.selectedNeedQryRecords) {
            if (!processOneQueryRecord(cutpaydetl)) break;
        }
        MessageUtil.addInfo("查询交易发送结束！请查看处理结果明细。");
        initList();
        return null;
    }

    /**
     * 扣款请求
     *
     * @param record
     * @return
     */
    private boolean processOneCutpayRequestRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayService.sendAndRecvRealTimeTxnMessage(record);
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文完成。");
        } catch (Exception e) {
            MessageUtil.addError("数据发送异常，请检查系统线路重新发送！");
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文失败." + e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 查询请求
     *
     * @param record
     * @return
     */
    private boolean processOneQueryRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayService.sendAndRecvRealTimeDatagramQueryMessage(record);
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文完成。");
        } catch (Exception e) {
            MessageUtil.addError("数据发送异常，请检查系统线路重新发送！");
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文失败." + e.getMessage());
            return false;
        }
        return true;
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
            initList();
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
            initList();
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
            String excelFilename = "实时代扣失败记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
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
            String excelFilename = "实时代扣成功记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, this.successDetlList);
        }
        return null;
    }


    /**
     * 存档失败记录
     *
     * @return
     */
    public String onArchiveAllFailureRecord() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("失败记录集为空。");
            return null;
        } else {
            int count = billManagerService.archiveBills(this.failureDetlList);
            initList();
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
            initList();
            MessageUtil.addWarn("更新记录条数：" + count);
        }
        return null;
    }


    public String reset() {
        this.detlRecord = new FipCutpaydetl();
        return null;
    }


    private void appendNewJoblog(String pkid, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, "fip_cutpaydetl", jobname, jobdesc, userid, username);
    }
    //====================================================================================


    public FipCutpaydetl[] getSelectedConfirmAccountRecords() {
        return selectedConfirmAccountRecords;
    }

    public void setSelectedConfirmAccountRecords(FipCutpaydetl[] selectedConfirmAccountRecords) {
        this.selectedConfirmAccountRecords = selectedConfirmAccountRecords;
    }

    public List<FipCutpaydetl> getActDetlList() {
        return actDetlList;
    }

    public void setActDetlList(List<FipCutpaydetl> actDetlList) {
        this.actDetlList = actDetlList;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public BigDecimal getTotalPrincipalAmt() {
        return totalPrincipalAmt;
    }

    public BigDecimal getTotalInterestAmt() {
        return totalInterestAmt;
    }

    public BigDecimal getTotalFxjeAmt() {
        return totalFxjeAmt;
    }


    public FipCutpaydetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipCutpaydetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipCutpaydetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaydetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public List<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipCutpaydetl> detlList) {
        this.detlList = detlList;
    }


    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public List<FipCutpaydetl> getNeedQueryDetlList() {
        return needQueryDetlList;
    }

    public void setNeedQueryDetlList(List<FipCutpaydetl> needQueryDetlList) {
        this.needQueryDetlList = needQueryDetlList;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
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

    public RtnMessageHandler getRtnMessageHandler() {
        return rtnMessageHandler;
    }

    public void setRtnMessageHandler(RtnMessageHandler rtnMessageHandler) {
        this.rtnMessageHandler = rtnMessageHandler;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public UnipayService getUnipayService() {
        return unipayService;
    }

    public void setUnipayService(UnipayService unipayService) {
        this.unipayService = unipayService;
    }

    public FipCutpaydetl[] getSelectedNeedQryRecords() {
        return selectedNeedQryRecords;
    }

    public void setSelectedNeedQryRecords(FipCutpaydetl[] selectedNeedQryRecords) {
        this.selectedNeedQryRecords = selectedNeedQryRecords;
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

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public FipCutpaydetl[] getSelectedAccountRecords() {
        return selectedAccountRecords;
    }

    public void setSelectedAccountRecords(FipCutpaydetl[] selectedAccountRecords) {
        this.selectedAccountRecords = selectedAccountRecords;
    }

    public FipCutpaydetl[] getSelectedFailRecords() {
        return selectedFailRecords;
    }

    public void setSelectedFailRecords(FipCutpaydetl[] selectedFailRecords) {
        this.selectedFailRecords = selectedFailRecords;
    }
}
