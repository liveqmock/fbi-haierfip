package fip.view.refund;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipRefunddetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.CcmsService;
import fip.service.fip.JobLogService;
import fip.service.fip.UnipayDepService;
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
 * 银联代付处理（全部走DEP APP接口） for 新消费信贷系统ccms.
 * User: zhanrui
 * Date: 2012-07-03
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UnipayDepCcmsRefundAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnipayDepCcmsRefundAction.class);

    private List<FipRefunddetl> detlList;
    private FipRefunddetl[] selectedRecords;

    private List<FipRefunddetl> needQueryDetlList;
    private FipRefunddetl[] selectedNeedQryRecords;

    private List<FipRefunddetl> failureDetlList;
    private FipRefunddetl[] selectedFailRecords;

    private List<FipRefunddetl> successDetlList;
    private FipRefunddetl[] selectedAccountRecords;
    private FipRefunddetl[] selectedConfirmAccountRecords;

    private List<FipRefunddetl> actDetlList;


    private FipRefunddetl detlRecord = new FipRefunddetl();
    private FipRefunddetl selectedRecord;


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

    @ManagedProperty(value = "#{unipayDepService}")
    private UnipayDepService unipayDepService;
    @ManagedProperty(value = "#{ccmsService}")
    private CcmsService ccmsService;


    private String bizid;
    private String sysid = "";   //暂不适用
    private String pkid;
    private BizType bizType;


    private String userid, username;

    @PostConstruct
    public void init() {
        try {
            OperatorManager om = SystemService.getOperatorManager();
            this.userid = om.getOperatorId();
            this.username = om.getOperatorName();

            this.sysid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sysid");
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
        detlList = billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.INIT);
        detlList.addAll(billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.RESEND_PEND));
        needQueryDetlList = billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        failureDetlList = billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_FAILED);
        successDetlList = billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_SUCCESS);
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalSuccessCount = successDetlList.size();
        this.totalFailureAmt = sumTotalAmt(failureDetlList);
        this.totalFailureCount = failureDetlList.size();
    }

    private String sumTotalAmt(List<FipRefunddetl> qrydetlList) {
        BigDecimal amt = new BigDecimal(0);
        for (FipRefunddetl detl : qrydetlList) {
            amt = amt.add(detl.getPayamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public String onSendRequestAll() {
        if (detlList.isEmpty()) {
            MessageUtil.addWarn("没有要发送的记录！");
            return null;
        }
        try {
            for (FipRefunddetl detl : this.detlList) {
                processOneRefundRequestRecord(detl);
            }
            MessageUtil.addInfo("数据发送结束！请查看处理结果明细。");
        } catch (Exception e) {
            MessageUtil.addError("数据发送结束处理异常" + e.getMessage());
        }
        initList();
        return null;
    }

    public String onSendRequestMulti() {
        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addWarn("未选中要处理的记录！");
            return null;
        }
        try {
            for (FipRefunddetl detl : this.selectedRecords) {
                processOneRefundRequestRecord(detl);
            }
            MessageUtil.addInfo("数据发送结束！请查看处理结果明细。");
        } catch (Exception e) {
            MessageUtil.addError("数据发送结束处理异常" + e.getMessage());
        }
        initList();
        return null;
    }

    public String onQueryAll() {
        if (needQueryDetlList.isEmpty()) {
            MessageUtil.addWarn("没有要发送的记录！");
            return null;
        }
        try {
            for (FipRefunddetl detl : this.needQueryDetlList) {
                processOneQueryRecord(detl);
            }
            MessageUtil.addInfo("查询交易发送结束！请查看处理结果明细。");
        } catch (Exception e) {
            MessageUtil.addError("查询交易处理异常" + e.getMessage());
        }
        initList();
        return null;
    }

    public String onQueryMulti() {
        if (selectedNeedQryRecords == null || selectedNeedQryRecords.length <= 0) {
            MessageUtil.addWarn("没有或未选中要发送的记录！");
            return null;
        }
        try {
            for (FipRefunddetl detl : this.selectedNeedQryRecords) {
                processOneQueryRecord(detl);
            }
            MessageUtil.addInfo("查询交易发送结束！请查看处理结果明细。");
        } catch (Exception e) {
            MessageUtil.addError("查询交易处理异常" + e.getMessage());
        }
        initList();
        return null;
    }

    /**
     * 代付请求
     *
     * @param record
     * @return
     */
    private void processOneRefundRequestRecord(FipRefunddetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvT1001002Message(record);
            appendNewJoblog(pkid, "发送银联代付请求", "发送银联代付请求报文完成。");
        } catch (Exception e) {
            appendNewJoblog(pkid, "发送银联代付请求", "发送银联代付请求报文失败." + e.getMessage());
            throw new RuntimeException("数据发送异常，请检查系统线路重新发送！" + e.getMessage());
        }
    }

    /**
     * 查询请求
     *
     * @param record
     * @return
     */
    private void processOneQueryRecord(FipRefunddetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvRefundT1003001Message(record);
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文完成。");
        } catch (Exception e) {
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文失败." + e.getMessage());
            throw new RuntimeException("数据发送异常，请检查系统线路重新发送！" + e.getMessage());
        }
    }


    public String onExportFailureList() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            String excelFilename = "实时代付失败记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportRefundList(excelFilename, this.failureDetlList);
        }
        return null;
    }

    public String onExportSuccessList() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            String excelFilename = "实时代付成功记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportRefundList(excelFilename, this.successDetlList);
        }
        return null;
    }


    //zhanrui 20110306
    public String onWriteBackAllUncertainlyRecords() {
        if (this.needQueryDetlList.size() == 0) {
            MessageUtil.addWarn("记录集为空。");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackRefundRecord2CCMS(this.needQueryDetlList, false);
                MessageUtil.addWarn("回写成功记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackAllSuccessRecords() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("记录集为空。");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackRefundRecord2CCMS(this.successDetlList, true);
                MessageUtil.addWarn("回写成功记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackAllFailRecords() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("记录集为空。");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackRefundRecord2CCMS(this.failureDetlList, true);
                billManagerService.archiveRefundBills(this.failureDetlList);
                MessageUtil.addWarn("回写记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedUncertainlyyRecords() {
        if (this.selectedNeedQryRecords.length == 0) {
            MessageUtil.addWarn("请选择记录...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipRefunddetl> detlList = Arrays.asList(this.selectedNeedQryRecords);
                count = ccmsService.writebackRefundRecord2CCMS(detlList, false);
                MessageUtil.addWarn("更新记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedSuccessRecords() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("请选择记录...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipRefunddetl> detlList = Arrays.asList(this.selectedConfirmAccountRecords);
                count = ccmsService.writebackRefundRecord2CCMS(detlList, true);
                MessageUtil.addWarn("更新记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedFailRecords() {
        if (this.selectedFailRecords.length == 0) {
            MessageUtil.addWarn("请选择记录...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipRefunddetl> detlList = Arrays.asList(this.selectedFailRecords);
                count = ccmsService.writebackRefundRecord2CCMS(detlList, true);
                //billManagerService.archiveRefundBills(detlList);
                MessageUtil.addWarn("更新记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String reset() {
        this.detlRecord = new FipRefunddetl();
        return null;
    }


    private void appendNewJoblog(String pkid, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, "fip_refunddetl", jobname, jobdesc, userid, username);
    }
    //====================================================================================


    public FipRefunddetl[] getSelectedConfirmAccountRecords() {
        return selectedConfirmAccountRecords;
    }

    public void setSelectedConfirmAccountRecords(FipRefunddetl[] selectedConfirmAccountRecords) {
        this.selectedConfirmAccountRecords = selectedConfirmAccountRecords;
    }

    public List<FipRefunddetl> getActDetlList() {
        return actDetlList;
    }

    public void setActDetlList(List<FipRefunddetl> actDetlList) {
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


    public FipRefunddetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipRefunddetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipRefunddetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipRefunddetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public FipRefunddetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipRefunddetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public List<FipRefunddetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipRefunddetl> detlList) {
        this.detlList = detlList;
    }


    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public List<FipRefunddetl> getNeedQueryDetlList() {
        return needQueryDetlList;
    }

    public void setNeedQueryDetlList(List<FipRefunddetl> needQueryDetlList) {
        this.needQueryDetlList = needQueryDetlList;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<FipRefunddetl> getFailureDetlList() {
        return failureDetlList;
    }

    public void setFailureDetlList(List<FipRefunddetl> failureDetlList) {
        this.failureDetlList = failureDetlList;
    }

    public List<FipRefunddetl> getSuccessDetlList() {
        return successDetlList;
    }

    public void setSuccessDetlList(List<FipRefunddetl> successDetlList) {
        this.successDetlList = successDetlList;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public UnipayDepService getUnipayDepService() {
        return unipayDepService;
    }

    public void setUnipayDepService(UnipayDepService unipayDepService) {
        this.unipayDepService = unipayDepService;
    }

    public FipRefunddetl[] getSelectedNeedQryRecords() {
        return selectedNeedQryRecords;
    }

    public void setSelectedNeedQryRecords(FipRefunddetl[] selectedNeedQryRecords) {
        this.selectedNeedQryRecords = selectedNeedQryRecords;
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

    public FipRefunddetl[] getSelectedAccountRecords() {
        return selectedAccountRecords;
    }

    public void setSelectedAccountRecords(FipRefunddetl[] selectedAccountRecords) {
        this.selectedAccountRecords = selectedAccountRecords;
    }

    public FipRefunddetl[] getSelectedFailRecords() {
        return selectedFailRecords;
    }

    public void setSelectedFailRecords(FipRefunddetl[] selectedFailRecords) {
        this.selectedFailRecords = selectedFailRecords;
    }

    public CcmsService getCcmsService() {
        return ccmsService;
    }

    public void setCcmsService(CcmsService ccmsService) {
        this.ccmsService = ccmsService;
    }

    public String getSysid() {
        return sysid;
    }

    public void setSysid(String sysid) {
        this.sysid = sysid;
    }
}
