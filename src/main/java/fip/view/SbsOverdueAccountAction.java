package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.SbsSevice;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 逾期帐单入帐处理
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class SbsOverdueAccountAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SbsOverdueAccountAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> successDetlList;
    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl[] selectedRecords;
    private FipCutpaydetl selectedRecord;

    private int totalcount;
    private int totalSuccessCount;
    private String totalamt;
    private String totalSuccessAmt;

    private Map<String, String> statusMap = new HashMap<String, String>();

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{sbsSevice}")
    private SbsSevice sbsSevice;

    private String bizid;
    private String pkid;
    private BizType bizType;


    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            this.bizid = context.getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(this.bizid)) {
                this.bizType = BizType.valueOf(bizid);
                initList();
            }
        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }
    }

    private void initList() {
        detlList = billManagerService.selectBillList(this.bizType, BillStatus.ACCOUNT_PEND, BillStatus.ACCOUNT_FAILED, BillType.OVERDUE);
        successDetlList = billManagerService.selectBillList(this.bizType, BillStatus.ACCOUNT_SUCCESS);
        this.totalamt = sumTotalAmt(detlList);
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalcount = detlList.size();
        this.totalSuccessCount = successDetlList.size();
    }

    public String onAccountAll() {
        if (this.detlList.isEmpty()) {
            MessageUtil.addWarn("没有需要处理的记录...");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                sbsSevice.accountPrepayRecord2SBS(this.detlList);
            } else {
                sbsSevice.accountCutPayRecord2SBS(this.detlList);
            }
            MessageUtil.addError("SBS入帐处理结束，请查看处理结果明细。");
        } catch (Exception e) {
            logger.error("SBS入帐时出现错误，请查询。", e);
            MessageUtil.addError("SBS入帐时出现错误，请查询。" + e.getMessage());
        }
        initList();
        return null;
    }

    public String onAccountMulti() {
        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addWarn("未选择处理记录...");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                sbsSevice.accountPrepayRecord2SBS(Arrays.asList(this.selectedRecords));
            } else {
                sbsSevice.accountCutPayRecord2SBS(Arrays.asList(this.selectedRecords));
            }
            MessageUtil.addError("SBS入帐处理结束，请查看处理结果明细。");
        } catch (Exception e) {
            logger.error("SBS入帐时出现错误，请查询。", e);
            MessageUtil.addError("SBS入帐时出现错误，请查询。" + e.getMessage());
        }
        initList();
        return null;
    }


    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList) {
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }


    //====================================================================================


    public int getTotalcount() {
        return totalcount;
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


    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<FipCutpaydetl> getSuccessDetlList() {
        return successDetlList;
    }

    public void setSuccessDetlList(List<FipCutpaydetl> successDetlList) {
        this.successDetlList = successDetlList;
    }

    public SbsSevice getSbsSevice() {
        return sbsSevice;
    }

    public void setSbsSevice(SbsSevice sbsSevice) {
        this.sbsSevice = sbsSevice;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public Map<String, String> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(Map<String, String> statusMap) {
        this.statusMap = statusMap;
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

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }
}
