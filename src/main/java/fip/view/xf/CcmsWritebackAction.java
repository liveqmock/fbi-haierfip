package fip.view.xf;

import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.CcmsService;
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
public class CcmsWritebackAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CcmsWritebackAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> successDetlList;
    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl[] selectedRecords;
    private FipCutpaydetl selectedRecord;

    private int totalcount;
    private int totalSuccessCount;
    private String totalamt;
    private String totalSuccessAmt;

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{ccmsService}")
    private CcmsService ccmsService;

    private String bizid;
    private String pkid;
    private BizType bizType;


    @PostConstruct
    public void init() {
        try {
            this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(this.bizid)) {
                this.bizType = BizType.valueOf(bizid);
                initList();
            }
        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }
    }

    private void initList() {
        detlList = billManagerService.selectBillList(this.bizType, BillStatus.ACCOUNT_SUCCESS, BillStatus.CMS_FAILED);
        successDetlList = billManagerService.selectBillList(this.bizType, BillStatus.CMS_SUCCESS);

        this.totalamt = sumTotalAmt(detlList);
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalcount = detlList.size();
        this.totalSuccessCount = successDetlList.size();
    }

    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList) {
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }


/*
    public String onShowDetail() {
        return "common/cutpayDetlFields.xhtml";
    }

    public void showDetailListener(ActionEvent event) {
        this.pkid = (String) event.getComponent().getAttributes().get("pkid");
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("bizid", this.bizid);
        sessionMap.put("pkid", this.pkid);
    }
*/

    public String onWritebackAll() {
        try {
            ccmsService.writebackCutPayRecord2CCMS(this.detlList, true, this.bizType);
            MessageUtil.addInfo("信贷系统回写处理结束。");
        } catch (Exception e) {
            logger.error("信贷系统回写时出现错误，请查询。", e);
            MessageUtil.addError("信贷系统回写时出现错误，请查询。");
        }
        initList();
        return null;
    }

    public String onWritebackMulti() {
        try {
            ccmsService.writebackCutPayRecord2CCMS(Arrays.asList(this.selectedRecords), true, this.bizType);
            MessageUtil.addInfo("信贷系统回写处理结束，请查看处理结果明细。");
        } catch (Exception e) {
            logger.error("信贷系统回写时出现错误，请查询。", e);
            MessageUtil.addError("信贷系统回写时出现错误，请查询。");
        }
        initList();
        return null;
    }


    public String reset() {
        this.detlRecord = new FipCutpaydetl();
        return null;
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

    public CcmsService getCcmsService() {
        return ccmsService;
    }

    public void setCcmsService(CcmsService ccmsService) {
        this.ccmsService = ccmsService;
    }

    public int getTotalSuccessCount() {
        return totalSuccessCount;
    }

    public void setTotalSuccessCount(int totalSuccessCount) {
        this.totalSuccessCount = totalSuccessCount;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
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
