package fip.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
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
import java.util.*;

/**
 * 银联代扣处理（全部走DEP APP接口） for 新消费信贷系统ccms.
 * User: zhanrui
 * Date: 2012-03-18
 * Time: 12:52:46
 */
@ManagedBean
@ViewScoped
public class UnionpayDepCcmsAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayDepCcmsAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> filteredDetlList;
    private FipCutpaydetl[] selectedRecords;

    private List<FipCutpaydetl> needQueryDetlList;
    private List<FipCutpaydetl> filteredNeedQueryDetlList;
    private FipCutpaydetl[] selectedNeedQryRecords;

    private List<FipCutpaydetl> failureDetlList;
    private List<FipCutpaydetl> filteredFailureDetlList;
    private FipCutpaydetl[] selectedFailRecords;

    private List<FipCutpaydetl> successDetlList;
    private List<FipCutpaydetl> filteredSuccessDetlList;
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

    @ManagedProperty(value = "#{unipayDepService}")
    private UnipayDepService unipayDepService;
    @ManagedProperty(value = "#{ccmsService}")
    private CcmsService ccmsService;


    private String bizid;
    private String sysid = "";   //暂不适用
    private String pkid;
    private BizType bizType;

    private String title="";

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
                if (this.bizType.equals(BizType.XFNEW)) {
                    this.title = "消费信贷系统";
                } else if (this.bizType.equals(BizType.XFJR)) {
                    this.title = "消费金融系统";
                } else {
                    this.title = "====系统错误=======";
                }
            }
        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public synchronized void initList() {
        detlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.INIT);
        logger.info("Step1");
        detlList.addAll(billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.RESEND_PEND));
        logger.info("Step2");
        needQueryDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        logger.info("Step3");
        failureDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_FAILED);
        logger.info("Step4");
        successDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_SUCCESS);
        logger.info("Step5");
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalSuccessCount = successDetlList.size();
        this.totalFailureAmt = sumTotalAmt(failureDetlList);
        this.totalFailureCount = failureDetlList.size();
        logger.info("Step end");
    }

    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList) {
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public synchronized String onSendRequestAll() {
        if (detlList.isEmpty()) {
            MessageUtil.addWarn("没有要发送的记录！");
            return null;
        }
        try {
            for (FipCutpaydetl cutpaydetl : this.detlList) {
                processOneCutpayRequestRecord(cutpaydetl);
            }
            MessageUtil.addInfo("数据发送结束！请查看处理结果明细。");
        } catch (Exception e) {
            MessageUtil.addError("数据发送结束处理异常" + e.getMessage());
        }
        initList();
        return null;
    }

    public synchronized String onSendRequestMulti() {
        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addWarn("未选中要处理的记录！");
            return null;
        }
        try {
            for (FipCutpaydetl cutpaydetl : this.selectedRecords) {
                processOneCutpayRequestRecord(cutpaydetl);
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
            for (FipCutpaydetl cutpaydetl : this.needQueryDetlList) {
                processOneQueryRecord(cutpaydetl);
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
            for (FipCutpaydetl cutpaydetl : this.selectedNeedQryRecords) {
                processOneQueryRecord(cutpaydetl);
            }
            MessageUtil.addInfo("查询交易发送结束！请查看处理结果明细。");
        } catch (Exception e) {
            MessageUtil.addError("查询交易处理异常" + e.getMessage());
        }
        initList();
        return null;
    }

    /**
     * 扣款请求
     *
     * @param record
     * @return
     */
    private void processOneCutpayRequestRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvT1001001Message(record);
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文完成。");
        } catch (Exception e) {
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文失败." + e.getMessage());
            throw new RuntimeException("数据发送异常，请检查系统线路重新发送！" + e.getMessage());
        }
    }

    /**
     * 查询请求
     *
     * @param record
     * @return
     */
    private void processOneQueryRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvCutpayT1003001Message(record);
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文完成。");
        } catch (Exception e) {
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文失败." + e.getMessage());
            throw new RuntimeException("数据发送异常，请检查系统线路重新发送！" + e.getMessage());
        }
    }


    //TODO  暂时沿用房贷模板，但模板中clientno字段有问题
    public String onExportFailureList() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            String excelFilename = "实时代扣失败记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, "xfCutpayList.xls", this.failureDetlList);
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
            jxls.exportCutpayList(excelFilename, "xfCutpayList.xls", this.successDetlList);
        }
        return null;
    }

    //回写全部不明记录，不存档
    public String onWriteBackAllUncertainlyRecords() {
        if (this.needQueryDetlList.size() == 0) {
            MessageUtil.addWarn("记录集为空。");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackCutPayRecord2CCMS(this.needQueryDetlList, false, this.bizType);
                MessageUtil.addWarn("回写成功记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    //回写全部成功记录 做存档处理
    public String onWriteBackAllSuccessCutpayRecords() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("记录集为空。");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackCutPayRecord2CCMS(this.successDetlList, true, this.bizType);
                MessageUtil.addWarn("回写成功记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackAllFailCutpayRecords() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("记录集为空。");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackCutPayRecord2CCMS(this.failureDetlList, true, this.bizType);
                //count = billManagerService.archiveBillsNoCheckRecvision(this.failureDetlList);
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
                List<FipCutpaydetl> cutpaydetlList = Arrays.asList(this.selectedNeedQryRecords);
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, false, this.bizType);
                MessageUtil.addWarn("更新记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedSuccessCutpayRecords() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("请选择记录...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipCutpaydetl> cutpaydetlList = Arrays.asList(this.selectedConfirmAccountRecords);
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, true, this.bizType);
                MessageUtil.addWarn("更新记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedFailCutpayRecords() {
        if (this.selectedFailRecords.length == 0) {
            MessageUtil.addWarn("请选择记录...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipCutpaydetl> cutpaydetlList = Arrays.asList(this.selectedFailRecords);
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, true, this.bizType);
//                billManagerService.archiveBills(cutpaydetlList);
                MessageUtil.addWarn("更新记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initList();
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

    public FipCutpaydetl[] getSelectedNeedQryRecords() {
        return selectedNeedQryRecords;
    }

    public void setSelectedNeedQryRecords(FipCutpaydetl[] selectedNeedQryRecords) {
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

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public List<FipCutpaydetl> getFilteredNeedQueryDetlList() {
        return filteredNeedQueryDetlList;
    }

    public void setFilteredNeedQueryDetlList(List<FipCutpaydetl> filteredNeedQueryDetlList) {
        this.filteredNeedQueryDetlList = filteredNeedQueryDetlList;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
