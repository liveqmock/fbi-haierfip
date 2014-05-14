package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipJoblog;
import fip.service.fip.BatchPkgService;
import fip.service.fip.BillManagerService;
import fip.service.fip.CmsService;
import fip.service.fip.JobLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
//@SessionScoped
public class CutpayQryListAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CutpayQryListAction.class);

    private List<FipJoblog> logList;
    private FipCutpaydetl selectedRecord;
    private FipCutpaydetl[] selectedRecords;
    private List<FipCutpaydetl> detlList;

    private CutpayChannel channelEnum = CutpayChannel.NONE;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{batchPkgService}")
    private BatchPkgService batchPkgService;
    @ManagedProperty(value = "#{cmsService}")
    private CmsService cmsService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    private String bizid;
    private String pkid;

    private BizType bizType;


    private Map<String, String> channelMap = new HashMap<String, String>();
    private BillStatus status = BillStatus.CUTPAY_FAILED;


    @PostConstruct
    public void init() {
        try {
            for (CutpayChannel cutpayChannelEnum : CutpayChannel.values()) {
                channelMap.put(cutpayChannelEnum.getCode(),cutpayChannelEnum.getTitle());
            }


            String paramPkid = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pkid");
            FipCutpaybat cutpaybat =  batchPkgService.selectRecord(paramPkid);

            this.bizType = BizType.valueOf(cutpaybat.getOriginBizid());

            detlList = billManagerService.selectRecordsByTxpkgSn(paramPkid);


        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public String onShowDetail() {
        return "common/cutpayDetlFields.xhtml";
    }

    public void showDetailListener(ActionEvent event) {
        String pkid = (String) event.getComponent().getAttributes().get("pkid");
        Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        sessionMap.put("bizid", this.bizid);
        sessionMap.put("pkid", pkid);
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

    public List<FipJoblog> getLogList() {
        return logList;
    }

    public void setLogList(List<FipJoblog> logList) {
        this.logList = logList;
    }

    public CmsService getCmsService() {
        return cmsService;
    }

    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
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

    public BatchPkgService getBatchPkgService() {
        return batchPkgService;
    }

    public void setBatchPkgService(BatchPkgService batchPkgService) {
        this.batchPkgService = batchPkgService;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }
}
