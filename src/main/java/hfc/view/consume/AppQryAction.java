package hfc.view.consume;

import fip.common.utils.MessageUtil;
import fip.repository.model.Xfapp;
import hfc.common.AppStatus;
import hfc.service.fenqi.ListBeanService;
import hfc.service.fenqi.XfappService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-9-26
 * Time: ÏÂÎç2:31
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class AppQryAction {
    private static final Logger logger = LoggerFactory.getLogger(AppQryAction.class);
    private Xfapp xfapp = new Xfapp();
    private List<Xfapp> xfappList;
    private Xfapp selectRecord;
    private String strAppno = "";
    private String strCustName = "";
    private String strAppStatus;
    private String strIdType;
    private String strId;
    private List<SelectItem> appStatusList;
    private List<SelectItem> idTypeList;
    private AppStatus appStatus = AppStatus.APP_REJECT;

    @ManagedProperty(value = "#{xfappService}")
    private XfappService xfappService;
    @ManagedProperty(value = "#{listBeanService}")
    private ListBeanService listBeanService;

    @PostConstruct
    private void page_Load() {
        queryRecords("", "","","","");
    }

    public String onBtnQueryClick() {
        queryRecords(strAppno, strCustName,strAppStatus,strIdType,strId);
        return null;
    }

    public void queryRecords(String appno, String custname,String appStatus,String idType,String strId) {
        xfappList = xfappService.selectXfappInfoRecords(appno,custname,appStatus,idType,strId);
    }

    public String getStrAppno() {
        return strAppno;
    }

    public List<SelectItem> getAppStatusList() {
        appStatusList = listBeanService.getEnumOptions("AppStatus","");
        return appStatusList;
    }

    public List<SelectItem> getIdTypeList() {
        idTypeList = listBeanService.getEnumOptions("IDType"," ");
        return idTypeList;
    }

    public String getStrIdType() {
        return strIdType;
    }

    public void setStrIdType(String strIdType) {
        this.strIdType = strIdType;
    }

    public String getStrId() {
        return strId;
    }

    public void setStrId(String strId) {
        this.strId = strId;
    }

    public String getStrAppStatus() {
        return strAppStatus;
    }

    public void setStrAppStatus(String strAppStatus) {
        this.strAppStatus = strAppStatus;
    }

    public void setStrAppno(String strAppno) {
        this.strAppno = strAppno;
    }

    public String getStrCustName() {
        return strCustName;
    }

    public void setStrCustName(String strCustName) {
        this.strCustName = strCustName;
    }

    public Xfapp getXfapp() {
        return xfapp;
    }

    public void setXfapp(Xfapp xfapp) {
        this.xfapp = xfapp;
    }

    public List<Xfapp> getXfappList() {
        return xfappList;
    }

    public void setXfappList(List<Xfapp> xfappList) {
        this.xfappList = xfappList;
    }

    public XfappService getXfappService() {
        return xfappService;
    }

    public void setXfappService(XfappService xfappService) {
        this.xfappService = xfappService;
    }

    public Xfapp getSelectRecord() {
        return selectRecord;
    }

    public void setSelectRecord(Xfapp selectRecord) {
        this.selectRecord = selectRecord;
    }

    public ListBeanService getListBeanService() {
        return listBeanService;
    }

    public void setListBeanService(ListBeanService listBeanService) {
        this.listBeanService = listBeanService;
    }

    public AppStatus getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(AppStatus appStatus) {
        this.appStatus = appStatus;
    }
}
