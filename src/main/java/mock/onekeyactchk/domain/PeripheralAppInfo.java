package mock.onekeyactchk.domain;

/**
 * Created by zhanrui on 2014/11/30.
 */
public class PeripheralAppInfo {
    private String  appId;
    private String  appName;
    private String  appChnCode;
    private String  status;
    private String  informTime;
//    private String  informRtnCode;
//    private String  informRtnMsg;
    private int  informTimes;
    private String  resultQryTime;
//    private String  resultQryRtnCode;
//    private String  resultQryRtnMsg;
    private int  resultQryTimes;
    private String  operId;
    private String  operName;
    private String  url;
    private String  rtnCode;
    private String  rtnMsg;
    private String  smsDesc;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppChnCode() {
        return appChnCode;
    }

    public void setAppChnCode(String appChnCode) {
        this.appChnCode = appChnCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getInformTime() {
        return informTime;
    }

    public void setInformTime(String informTime) {
        this.informTime = informTime;
    }


    public int getInformTimes() {
        return informTimes;
    }

    public void setInformTimes(int informTimes) {
        this.informTimes = informTimes;
    }

    public String getResultQryTime() {
        return resultQryTime;
    }

    public void setResultQryTime(String resultQryTime) {
        this.resultQryTime = resultQryTime;
    }


    public int getResultQryTimes() {
        return resultQryTimes;
    }

    public void setResultQryTimes(int resultQryTimes) {
        this.resultQryTimes = resultQryTimes;
    }

    public String getOperId() {
        return operId;
    }

    public void setOperId(String operId) {
        this.operId = operId;
    }

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getRtnCode() {
        return rtnCode;
    }

    public void setRtnCode(String rtnCode) {
        this.rtnCode = rtnCode;
    }

    public String getRtnMsg() {
        return rtnMsg;
    }

    public void setRtnMsg(String rtnMsg) {
        this.rtnMsg = rtnMsg;
    }

    public String getSmsDesc() {
        return smsDesc;
    }

    public void setSmsDesc(String smsDesc) {
        this.smsDesc = smsDesc;
    }
}
