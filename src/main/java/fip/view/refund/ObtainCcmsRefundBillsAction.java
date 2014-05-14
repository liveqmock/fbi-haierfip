package fip.view.refund;

import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.EnumApp;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipRefunddetl;
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
 * 获取新消费信贷系统的 代发 记录.
 * User: zhanrui
 * Date: 2012-06-10
 * Time: 下午2:07
 */
@ManagedBean
@ViewScoped
public class ObtainCcmsRefundBillsAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ObtainCcmsRefundBillsAction.class);

    private List<FipRefunddetl> detlList;
    private List<FipRefunddetl> qrydetlList;
    private FipRefunddetl detlRecord = new FipRefunddetl();
    private FipRefunddetl[] selectedRecords;
    private FipRefunddetl[] selectedQryRecords;
    private FipRefunddetl selectedRecord;

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

    @PostConstruct
    public void init() {
        try {
            String bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(bizid)) {
                this.bizType = BizType.valueOf(bizid);
                qrydetlList = new ArrayList<FipRefunddetl>();
                initDetlList();
            }
        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }
    }

    private void initDetlList() {
        detlList = billManagerService.selectRefundBillList(this.bizType, BillStatus.INIT);
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
    }

    private void initSelectItem(EnumApp e){
        List<SelectItem> items = new ArrayList<SelectItem>();
        SelectItem item;
    }

    public String onQryCms() {
        try {
            qrydetlList = ccmsService.doQueryCcmsRefundBills(this.bizType, BillType.NORMAL);
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
    private String sumTotalAmt(List<FipRefunddetl> qrydetlList){
        BigDecimal amt = new BigDecimal(0);
        for (FipRefunddetl detl : qrydetlList) {
            amt = amt.add(detl.getPayamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public String onObtain() {
        //TODO 检查本地记录状态
        try {
            int count = ccmsService.doObtainCcmsRefundBills(this.bizType, BillType.NORMAL);
            MessageUtil.addWarn("本次获取记录数：" + count + " 条.");
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
            MessageUtil.addError("获取记录时出错。" + e.getMessage());
        }
        initDetlList();
        return null;
    }
    public String onObtainMulti() {
        if (selectedQryRecords.length == 0) {
            MessageUtil.addWarn("请先选择记录。");
            return null;
        }

        //TODO 检查本地记录状态

        try {
            int count = ccmsService.doMultiObtainCcmsRefundBills(this.bizType, BillType.NORMAL, selectedQryRecords);
            MessageUtil.addWarn("本次获取记录数：" + count + " 条.");
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
            MessageUtil.addError("获取记录时出错。" + e.getMessage());
        }
        initDetlList();
        return null;
    }

    public String onDeleteAll() {
        if (detlList.size() == 0) {
            MessageUtil.addWarn("记录为空。");
            return null;
        }
        billManagerService.deleteRefundBillsByKey(detlList);
        initDetlList();
        return null;
    }

    public String onDeleteMulti() {
        if (selectedRecords.length == 0) {
            MessageUtil.addWarn("请先选择记录。");
            return null;
        }

        billManagerService.deleteRefundBillsByKey(Arrays.asList(selectedRecords));
        initDetlList();
        return null;
    }

    //============================================================================================

    public List<FipRefunddetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipRefunddetl> detlList) {
        this.detlList = detlList;
    }

    public FipRefunddetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipRefunddetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipRefunddetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipRefunddetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public FipRefunddetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipRefunddetl selectedRecord) {
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

    public List<FipRefunddetl> getQrydetlList() {
        return qrydetlList;
    }

    public void setQrydetlList(List<FipRefunddetl> qrydetlList) {
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

    public FipRefunddetl[] getSelectedQryRecords() {
        return selectedQryRecords;
    }

    public void setSelectedQryRecords(FipRefunddetl[] selectedQryRecords) {
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
}
