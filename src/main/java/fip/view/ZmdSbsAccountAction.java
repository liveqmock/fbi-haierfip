package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.SbsSevice;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 专卖店代扣 SBS 入账
 * User: zhanrui
 * Date: 2015-04-15
 */
@ManagedBean
@ViewScoped
public class ZmdSbsAccountAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ZmdSbsAccountAction.class);

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
    @ManagedProperty(value = "#{zmdService}")
    private ZmdService zmdService;

    private String bizid;
    private BizType bizType;


    @PostConstruct
    public void init() {
        FacesContext context = FacesContext.getCurrentInstance();
        this.bizid = context.getExternalContext().getRequestParameterMap().get("bizid");
        if (!StringUtils.isEmpty(this.bizid)) {
            if ("ZMD".equals(this.bizid)) {
                this.bizType = BizType.valueOf(bizid);
                initList();
            } else {
                throw new RuntimeException("业务号错误");
            }
        }
    }

    private synchronized void initList() {
        detlList = billManagerService.selectBillList(this.bizType, BillStatus.ACCOUNT_PEND, BillStatus.ACCOUNT_FAILED);
        successDetlList = billManagerService.selectBillList(this.bizType, BillStatus.ACCOUNT_SUCCESS);
        this.totalamt = sumTotalAmt(detlList);
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalcount = detlList.size();
        this.totalSuccessCount = successDetlList.size();
    }

    public synchronized String onAccountAll() {
        if (this.detlList.isEmpty()) {
            MessageUtil.addWarn("没有需要处理的记录...");
            return null;
        }
        try {
            int succ = zmdService.accountCutPayRecord2SBS(this.detlList);
            zmdService.processArchive(this.detlList);
            MessageUtil.addWarn("入帐结束，成功笔数:[" + succ +"]" );
        } catch (Exception e) {
            logger.error("SBS入帐时出现错误，请查询。", e);
            MessageUtil.addError("SBS入帐时出现错误，请查询。" + e.getMessage());
        }
        initList();
        return null;
    }

    public synchronized String onAccountMulti() {
        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addWarn("未选择处理记录...");
            return null;
        }
        try {
            int succ =  zmdService.accountCutPayRecord2SBS(Arrays.asList(this.selectedRecords));
            zmdService.processArchive(Arrays.asList(this.selectedRecords));
            MessageUtil.addWarn("入帐结束，成功笔数:[" + succ +"]" );
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

    public ZmdService getZmdService() {
        return zmdService;
    }

    public void setZmdService(ZmdService zmdService) {
        this.zmdService = zmdService;
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
