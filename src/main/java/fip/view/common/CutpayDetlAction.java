package fip.view.common;

import fip.common.constant.BillType;
import fip.common.constant.CutpayChannel;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipJoblog;
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
public class CutpayDetlAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CutpayDetlAction.class);

    private List<FipJoblog> logList;
    private FipCutpaydetl selectedRecord;

    private CutpayChannel channelEnum = CutpayChannel.NONE;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{cmsService}")
    private CmsService cmsService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    private String bizid;
    private String pkid;

    private Map<String, String> channelMap = new HashMap<String, String>();

    @PostConstruct
    public void init() {
        try {

            for (CutpayChannel cutpayChannelEnum : CutpayChannel.values()) {
                channelMap.put(cutpayChannelEnum.getCode(),cutpayChannelEnum.getTitle());
            }
//            Map sessionMap = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
//            this.pkid = (String)sessionMap.get("pkid");
//            this.bizid = (String)sessionMap.get("bizid");


            String paramPkid = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pkid");
            this.selectedRecord = billManagerService.selectBillByKey(paramPkid);
            this.logList = jobLogService.selectJobLogsByOriginPkid("fip_cutpaydetl",paramPkid);

        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public String onShowDetail() {
        this.selectedRecord = new FipCutpaydetl();
        this.selectedRecord.setAppno("asdasdasd");
        return null;
    }

    public String getEnumTitle_BillType(String alias){
        return BillType.valueOfAlias(alias).getTitle();

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
}
