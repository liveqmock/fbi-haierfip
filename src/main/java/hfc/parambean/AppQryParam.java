package hfc.parambean;

import org.springframework.stereotype.Component;
/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-17
 * Time: ÉÏÎç11:54
 * To change this template use File | Settings | File Templates.
 */
public class AppQryParam {
       private String appno;
       private String idType;
       private String id;
       private String clientName;
       private String fromDate;
       private String toDate;
       private String appStatus;

    public String getAppno() {
        return appno;
    }

    public void setAppno(String appno) {
        this.appno = appno;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getIdType() {
        return idType;
    }

    public void setIdType(String idType) {
        this.idType = idType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getAppStatus() {
        return appStatus;
    }

    public void setAppStatus(String appStatus) {
        this.appStatus = appStatus;
    }
}
