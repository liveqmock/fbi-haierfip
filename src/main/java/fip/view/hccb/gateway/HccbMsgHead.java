package fip.view.hccb.gateway;

/**
 * Created by zhanrui on 2014/12/10.
 */
public class HccbMsgHead {
    private String msgtype = "";
    private String txncode = "";
    private String bizid = "HCCB";
    private String mchtid = "HCCBFIP0001";
    private String txndate = "";
    private String txntime = "";
    private String txnsn = "";
    private String rtncode = "";
    private String rtnmsg = "";
    private String signtype = "";
    private String signinfo = "";

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getTxncode() {
        return txncode;
    }

    public void setTxncode(String txncode) {
        this.txncode = txncode;
    }

    public String getBizid() {
        return bizid;
    }

    public void setBizid(String bizid) {
        this.bizid = bizid;
    }

    public String getMchtid() {
        return mchtid;
    }

    public void setMchtid(String mchtid) {
        this.mchtid = mchtid;
    }

    public String getTxndate() {
        return txndate;
    }

    public void setTxndate(String txndate) {
        this.txndate = txndate;
    }

    public String getTxntime() {
        return txntime;
    }

    public void setTxntime(String txntime) {
        this.txntime = txntime;
    }

    public String getTxnsn() {
        return txnsn;
    }

    public void setTxnsn(String txnsn) {
        this.txnsn = txnsn;
    }

    public String getRtncode() {
        return rtncode;
    }

    public void setRtncode(String rtncode) {
        this.rtncode = rtncode;
    }

    public String getRtnmsg() {
        return rtnmsg;
    }

    public void setRtnmsg(String rtnmsg) {
        this.rtnmsg = rtnmsg;
    }

    public String getSigntype() {
        return signtype;
    }

    public void setSigntype(String signtype) {
        this.signtype = signtype;
    }

    public String getSigninfo() {
        return signinfo;
    }

    public void setSigninfo(String signinfo) {
        this.signinfo = signinfo;
    }

    @Override
    public String toString() {
        return "HccbMsgHead{" +
                "msgtype='" + msgtype + '\'' +
                ", txncode='" + txncode + '\'' +
                ", bizid='" + bizid + '\'' +
                ", mchtid='" + mchtid + '\'' +
                ", txndate='" + txndate + '\'' +
                ", txntime='" + txntime + '\'' +
                ", txnsn='" + txnsn + '\'' +
                ", rtncode='" + rtncode + '\'' +
                ", rtnmsg='" + rtnmsg + '\'' +
                ", signtype='" + signtype + '\'' +
                ", signinfo='" + signinfo + '\'' +
                '}';
    }
}
