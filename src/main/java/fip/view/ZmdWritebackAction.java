package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.CmsService;
import fip.service.fip.ZmdService;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 专卖店回写处理
 * User: zhanrui
 * Date: 2015-04-14
 */
@ManagedBean
@ViewScoped
public class ZmdWritebackAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ZmdWritebackAction.class);

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
    @ManagedProperty(value = "#{zmdService}")
    private ZmdService zmdSevice;

    private String bizid;
    private String pkid;
    private BizType bizType;

    @PostConstruct
    public void init() {
        this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
        if (!StringUtils.isEmpty(this.bizid)) {
            this.bizType = BizType.valueOf(bizid);
        }
        if (!"ZMD".equals(this.bizid)) {
            throw new RuntimeException("业务类别错误.");
        }
        initList();
    }

    private synchronized void initList() {
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

    public synchronized String onWritebackAll() {
        //List<String> returnMsgs = new ArrayList<String>();
        int cnt = 0;
        try {
            cnt = zmdSevice.writebackCutPayRecord2Zmd(this.detlList, true);
            MessageUtil.addWarn("信贷系统(专卖店代扣)回写处理结束。");
        } catch (Exception e) {
            logger.error("信贷系统(专卖店代扣)回写时出现错误，请查询。", e);
            MessageUtil.addError("信贷系统(专卖店代扣)回写时出现错误，请查询。");
        }
        initList();
/*
        for (String returnMsg : returnMsgs) {
            MessageUtil.addWarn(returnMsg);
        }
*/
        MessageUtil.addWarn("回写处理记录条数：[" + cnt + "]");
        return null;
    }

    public synchronized String onWritebackMulti() {
        //List<String> returnMsgs = new ArrayList<String>();
        int cnt = 0;
        try {
            cnt = zmdSevice.writebackCutPayRecord2Zmd(Arrays.asList(this.selectedRecords), true);
            MessageUtil.addWarn("信贷系统(专卖店代扣)回写处理结束，请查看处理结果明细。");
        } catch (Exception e) {
            logger.error("信贷系统(专卖店代扣)回写时出现错误，请查询。", e);
            MessageUtil.addError("信贷系统(专卖店代扣)回写时出现错误，请查询。");
        }
        initList();
/*
        for (String returnMsg : returnMsgs) {
            MessageUtil.addWarn(returnMsg);
        }
*/
        MessageUtil.addWarn("回写处理记录条数：[" + cnt + "]");

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

    public ZmdService getZmdSevice() {
        return zmdSevice;
    }

    public void setZmdSevice(ZmdService zmdSevice) {
        this.zmdSevice = zmdSevice;
    }
}
