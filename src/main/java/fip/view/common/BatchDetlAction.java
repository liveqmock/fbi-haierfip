package fip.view.common;

import fip.common.constant.CutpayChannel;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipJoblog;
import fip.service.fip.BatchPkgService;
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
 * 批量报文详细内容 日志.
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
public class BatchDetlAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(BatchDetlAction.class);

    private List<FipJoblog> logList;
    private FipCutpaybat selectedRecord;

    private CutpayChannel channelEnum = CutpayChannel.NONE;

    @ManagedProperty(value = "#{batchPkgService}")
    private BatchPkgService batchPkgService;
//    @ManagedProperty(value = "#{billManagerService}")
//    private BillManagerService billManagerService;
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

            String paramPkid = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("pkid");
            this.selectedRecord = batchPkgService.selectRecord(paramPkid);
            this.logList = jobLogService.selectJobLogsByOriginPkid("fip_cutpaybat",paramPkid);

        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }



    //====================================================================================


    public List<FipJoblog> getLogList() {
        return logList;
    }

    public void setLogList(List<FipJoblog> logList) {
        this.logList = logList;
    }

    public FipCutpaybat getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaybat selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public CutpayChannel getChannelEnum() {
        return channelEnum;
    }

    public void setChannelEnum(CutpayChannel channelEnum) {
        this.channelEnum = channelEnum;
    }

    public BatchPkgService getBatchPkgService() {
        return batchPkgService;
    }

    public void setBatchPkgService(BatchPkgService batchPkgService) {
        this.batchPkgService = batchPkgService;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public Map<String, String> getChannelMap() {
        return channelMap;
    }

    public void setChannelMap(Map<String, String> channelMap) {
        this.channelMap = channelMap;
    }
}
