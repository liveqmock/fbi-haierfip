package hfc.view.consume;

import fip.common.utils.MessageUtil;
import fip.gateway.newcms.domain.T201002.T201002Request;
import fip.gateway.newcms.domain.T201002.T201002Response;
import fip.gateway.newcms.domain.common.BaseBean;
import fip.repository.model.Xfapp;
import hfc.common.CmsAppStatusEnum;
import hfc.common.LoanStatusEnum;
import hfc.parambean.AppQryParam;
import hfc.service.WriteBackQryService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pgw.NewCmsManager;
import skyline.service.common.ToolsService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-14
 * Time: 下午11:58
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class WriteBackQryAction {
    private Log logger = LogFactory.getLog(this.getClass());
    @ManagedProperty(value = "#{writeBackQryService}")
    private WriteBackQryService writeBackQryService;
    @ManagedProperty(value = "#{toolsService}")
    private ToolsService toolsService;
    private T201002Response selectedRecord;
    private List<T201002Response> responseList;
    private AppQryParam appQryParam;
    private static final int limitedAppCnt = 150;
    private List<SelectItem> idTypeList;  // 证件类型
    private List<SelectItem> appStatusList; // 申请单状态列表
    // 未发送申请单
    private String unSendAppMsgs;
    private StringBuilder builder = new StringBuilder("");
    private CmsAppStatusEnum appStatusEnum = CmsAppStatusEnum.CMS_EMPLACED;
    private LoanStatusEnum loanStatusEnum = LoanStatusEnum.CMS_NORMAL;

    @PostConstruct
    public void init() {
        responseList = new ArrayList<T201002Response>();
        unSendAppMsgs = "";
        appQryParam = new AppQryParam();
        idTypeList = toolsService.getEnuSelectItemList("IDType", false, true, false);
        appStatusList = toolsService.getEnuSelectItemList("CmsAppStatus", false, true, false);
    }

    /*private boolean isCanQryApp() {
        boolean appnoNull = StringUtils.isEmpty(appQryParam.getAppno());
        boolean idNull = StringUtils.isEmpty(appQryParam.getId());
        boolean clientNameNull = StringUtils.isEmpty(appQryParam.getClientName());
        boolean fromDateNull = StringUtils.isEmpty(appQryParam.getFromDate());
        boolean toDateNull = StringUtils.isEmpty(appQryParam.getToDate());
        if (appnoNull && idNull && clientNameNull && fromDateNull && toDateNull) {
            return false;
        }
        return true;
    }*/

    public String onQryAppResult() {
        /*if (!isCanQryApp()) {
            MessageUtil.addError("至少输入一项查询条件！");
            return null;
        }else*/
        if (!checkDateNull()) {
            MessageUtil.addError("起止日期均需填写！");
            return null;
        }
        List<Xfapp> appList = writeBackQryService.getXfappsByCondition(appQryParam);
        int cnt = appList.size();
        if (cnt <= 0) {
            MessageUtil.addError("没有查询到符合条件的申请单！");
            return null;
        } else if (cnt > limitedAppCnt) {
            MessageUtil.addError("查询到申请单数过多，请细化查询条件！");
            return null;
        } else {
            responseList.clear();
            builder = new StringBuilder("");
            String qryAppStatus = appQryParam.getAppStatus();
            for (Xfapp app : appList) {
                if (CmsAppStatusEnum.NEED_SEND.getCode().equals(app.getAppstatus())) {
                    builder.append(app.getAppno()).append(",");
                } else {
                    T201002Request reqRecord = new T201002Request();
                    reqRecord = new T201002Request();
                    reqRecord.initHeader("0100", "201002", "2");
                    reqRecord.setStdsqdh(app.getAppno());
                    reqRecord.setStdsqdzt(qryAppStatus);
                    String strXml = reqRecord.toXml();

                    NewCmsManager ncm = new NewCmsManager();
                    String responseStr = ncm.doPostXml(strXml);

                    T201002Response response = (T201002Response) BaseBean.toObject(T201002Response.class, responseStr);
                    if ("AAAAAAA".equals(response.getStd400mgid())) {
                        this.responseList.add(response);
                    } /*else{
                        unSendAppMsgs += response.getStdsqdh();
                    }*/
                }
            }
        }
        return null;
    }

    private boolean checkDateNull() {
        boolean fromDateNull = StringUtils.isEmpty(appQryParam.getFromDate());
        boolean toDateNull = StringUtils.isEmpty(appQryParam.getToDate());
        if ((fromDateNull && !toDateNull) || (!fromDateNull && toDateNull)) {
            return false;
        }
        return true;
    }

    public WriteBackQryService getWriteBackQryService() {
        return writeBackQryService;
    }

    public void setWriteBackQryService(WriteBackQryService writeBackQryService) {
        this.writeBackQryService = writeBackQryService;
    }

    public T201002Response getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(T201002Response selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public List<T201002Response> getResponseList() {
        return responseList;
    }

    public void setResponseList(List<T201002Response> responseList) {
        this.responseList = responseList;
    }

    public String getUnSendAppMsgs() {
        unSendAppMsgs = "";
        if (!StringUtils.isEmpty(builder.toString())) {
            unSendAppMsgs = builder.toString();
            unSendAppMsgs = "未上传申请单:" + unSendAppMsgs.substring(0, unSendAppMsgs.length() - 1);
        }

        return unSendAppMsgs;
    }

    public void setUnSendAppMsgs(String unSendAppMsgs) {
        this.unSendAppMsgs = unSendAppMsgs;
    }

    public CmsAppStatusEnum getAppStatusEnum() {
        return appStatusEnum;
    }

    public void setAppStatusEnum(CmsAppStatusEnum appStatusEnum) {
        this.appStatusEnum = appStatusEnum;
    }

    public LoanStatusEnum getLoanStatusEnum() {
        return loanStatusEnum;
    }

    public void setLoanStatusEnum(LoanStatusEnum loanStatusEnum) {
        this.loanStatusEnum = loanStatusEnum;
    }

    public AppQryParam getAppQryParam() {
        return appQryParam;
    }

    public void setAppQryParam(AppQryParam appQryParam) {
        this.appQryParam = appQryParam;
    }

    public List<SelectItem> getIdTypeList() {
        return idTypeList;
    }

    public void setIdTypeList(List<SelectItem> idTypeList) {
        this.idTypeList = idTypeList;
    }

    public ToolsService getToolsService() {
        return toolsService;
    }

    public void setToolsService(ToolsService toolsService) {
        this.toolsService = toolsService;
    }

    public List<SelectItem> getAppStatusList() {
        return appStatusList;
    }

    public void setAppStatusList(List<SelectItem> appStatusList) {
        this.appStatusList = appStatusList;
    }
}
