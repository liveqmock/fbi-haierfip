package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
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
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据是否归档查询记录
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class CutpayArchiveQryAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CutpayArchiveQryAction.class);

    private FipCutpaydetl selectedRecord;
    private FipCutpaydetl[] selectedRecords;
    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> filteredDetlList;

    private CutpayChannel channelEnum = CutpayChannel.NONE;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    private String bizid;

    private Map<String, String> channelMap = new HashMap<String, String>();
    private BillStatus status = BillStatus.CUTPAY_FAILED;
    private BizType bizType;

    private int totalcount;
    private String totalamt;

    @PostConstruct
    public void init() {
        try {
            for (CutpayChannel cutpayChannelEnum : CutpayChannel.values()) {
                channelMap.put(cutpayChannelEnum.getCode(), cutpayChannelEnum.getTitle());
            }

            this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");

            if (!StringUtils.isEmpty(this.bizid)) {
                this.bizType = BizType.valueOf(bizid);
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
        detlList = billManagerService.selectBillListForCurrMonth(this.bizType, true);
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
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

    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public FipCutpaydetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaydetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public CutpayChannel getChannelEnum() {
        return channelEnum;
    }

    public void setChannelEnum(CutpayChannel channelEnum) {
        this.channelEnum = channelEnum;
    }

    public Map<String, String> getChannelMap() {
        return channelMap;
    }

    public void setChannelMap(Map<String, String> channelMap) {
        this.channelMap = channelMap;
    }

    public List<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipCutpaydetl> detlList) {
        this.detlList = detlList;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public String getBizid() {
        return bizid;
    }

    public void setBizid(String bizid) {
        this.bizid = bizid;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }
}
