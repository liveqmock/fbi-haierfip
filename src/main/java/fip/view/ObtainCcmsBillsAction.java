package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.EnumApp;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.CcmsService;
import fip.service.fip.JobLogService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-13
 * Time: 下午2:07
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ObtainCcmsBillsAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ObtainCcmsBillsAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> filteredDetlList;
    private List<FipCutpaydetl> qrydetlList;
    private List<FipCutpaydetl> filteredQrydetlList;
    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl[] selectedRecords;
    private FipCutpaydetl[] selectedQryRecords;
    private FipCutpaydetl selectedRecord;

    private int totalcount;
    private int totalqrycount;
    private String totalamt;
    private String totalqryamt;
    private BigDecimal totalPrincipalAmt;   //本金
    private BigDecimal totalInterestAmt;    //利息
    private BigDecimal totalFxjeAmt;    //罚息

    private Map<String, String> statusMap = new HashMap<String, String>();

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{ccmsService}")
    private CcmsService ccmsService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    private BizType bizType;

    private List<SelectItem> billStatusOptions;
    private String title="";

    @PostConstruct
    public void init() {
        try {
            String bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(bizid)) {
                this.bizType = BizType.valueOf(bizid);
                qrydetlList = new ArrayList<FipCutpaydetl>();
                initDetlList();
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

    private synchronized void initDetlList() {
        detlList = billManagerService.selectBillList(this.bizType, BillType.NORMAL, BillStatus.INIT);
        //detlList = billManagerService.selectBillList(this.bizType, BillType.NORMAL, BillStatus.INIT, "XF-CCMS");
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
    }

    private void initSelectItem(EnumApp e){
        List<SelectItem> items = new ArrayList<SelectItem>();
        SelectItem item;


    }

    public synchronized String onQryCms() {
        try {
            qrydetlList = ccmsService.doQueryCcmsBills(this.bizType, BillType.NORMAL);
            if (qrydetlList.size() == 0) {
                MessageUtil.addWarn("未获取到信贷系统的代扣记录。");
            }
            this.totalqryamt = sumTotalAmt(qrydetlList);
            this.totalqrycount = qrydetlList.size();
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
            MessageUtil.addError("获取记录时出错。" + e.getMessage());
        }
        return null;
    }
    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList){
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public synchronized String onObtain() {
        //TODO 检查本地记录状态
        try {
            List<String> returnMsgs = new ArrayList<String>();
            int count = ccmsService.doObtainCcmsBills(this.bizType, BillType.NORMAL, returnMsgs);
            MessageUtil.addWarn("本次获取记录数：" + count + " 条.");
            for (String returnMsg : returnMsgs) {
                MessageUtil.addWarn(returnMsg);
            }
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
            MessageUtil.addError("获取记录时出错。" + e.getMessage());
        }
        initDetlList();
        return null;
    }
    public synchronized String onObtainMulti() {
        if (selectedQryRecords.length == 0) {
            MessageUtil.addWarn("请先选择记录。");
            return null;
        }

        //TODO 检查本地记录状态

        try {
            List<String> returnMsgs = new ArrayList<String>();
            int count = ccmsService.doMultiObtainCcmsBills(this.bizType, BillType.NORMAL, selectedQryRecords, returnMsgs);
            MessageUtil.addWarn("本次获取记录数：" + count + " 条.");
            for (String returnMsg : returnMsgs) {
                MessageUtil.addWarn(returnMsg);
            }
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
            MessageUtil.addError("获取记录时出错。" + e.getMessage());
        }
        initDetlList();
        return null;
    }

    public synchronized String onDeleteAll() {
        if (detlList.size() == 0) {
            MessageUtil.addWarn("记录为空。");
            return null;
        }
        billManagerService.deleteBillsByKey(detlList);
        initDetlList();
        return null;
    }

    public synchronized String onDeleteMulti() {
        if (selectedRecords.length == 0) {
            MessageUtil.addWarn("请先选择记录。");
            return null;
        }

        billManagerService.deleteBillsByKey(Arrays.asList(selectedRecords));
        initDetlList();
        MessageUtil.addInfo("删除记录成功。");
        return null;
    }

    //============================================================================================

    public List<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipCutpaydetl> detlList) {
        this.detlList = detlList;
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

    public int getTotalcount() {
        return this.totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public BigDecimal getTotalPrincipalAmt() {
        return totalPrincipalAmt;
    }

    public void setTotalPrincipalAmt(BigDecimal totalPrincipalAmt) {
        this.totalPrincipalAmt = totalPrincipalAmt;
    }

    public BigDecimal getTotalInterestAmt() {
        return totalInterestAmt;
    }

    public void setTotalInterestAmt(BigDecimal totalInterestAmt) {
        this.totalInterestAmt = totalInterestAmt;
    }

    public BigDecimal getTotalFxjeAmt() {
        return totalFxjeAmt;
    }

    public void setTotalFxjeAmt(BigDecimal totalFxjeAmt) {
        this.totalFxjeAmt = totalFxjeAmt;
    }

    public Map<String, String> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(Map<String, String> statusMap) {
        this.statusMap = statusMap;
    }

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

    public CcmsService getCcmsService() {
        return ccmsService;
    }

    public void setCcmsService(CcmsService cmsService) {
        this.ccmsService = cmsService;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public List<FipCutpaydetl> getQrydetlList() {
        return qrydetlList;
    }

    public void setQrydetlList(List<FipCutpaydetl> qrydetlList) {
        this.qrydetlList = qrydetlList;
    }

    public int getTotalqrycount() {
        return totalqrycount;
    }

    public void setTotalqrycount(int totalqrycount) {
        this.totalqrycount = totalqrycount;
    }

    public String getTotalqryamt() {
        return totalqryamt;
    }

    public void setTotalqryamt(String totalqryamt) {
        this.totalqryamt = totalqryamt;
    }

    public FipCutpaydetl[] getSelectedQryRecords() {
        return selectedQryRecords;
    }

    public void setSelectedQryRecords(FipCutpaydetl[] selectedQryRecords) {
        this.selectedQryRecords = selectedQryRecords;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public List<SelectItem> getBillStatusOptions() {
        return billStatusOptions;
    }

    public void setBillStatusOptions(List<SelectItem> billStatusOptions) {
        this.billStatusOptions = billStatusOptions;
    }

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public List<FipCutpaydetl> getFilteredQrydetlList() {
        return filteredQrydetlList;
    }

    public void setFilteredQrydetlList(List<FipCutpaydetl> filteredQrydetlList) {
        this.filteredQrydetlList = filteredQrydetlList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
