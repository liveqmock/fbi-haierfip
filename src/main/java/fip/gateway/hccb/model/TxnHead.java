package fip.gateway.hccb.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhanrui on 2015/3/31.
 */
@XStreamAlias("head")
public class TxnHead {
    private String msgtype;
    private String txncode;
    private String bizid;
    private String mchtid;
    private String txndate;
    private String txntime;
    private String txnsn;
    private String rtncode;
    private String rtnmsg;
    private String signtype;
    private String signinfo;

    public TxnHead() {
        setMsgtype("0101");
        setTxncode("0000");

        setBizid("HCCB");
        setMchtid("HCCBFIP0001");
        setTxndate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        setTxntime(new SimpleDateFormat("HHmmss").format(new Date()));

        //交易流水号
        setTxnsn(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

        setRtncode("");
        setRtnmsg("");
        setSigntype("MD5");
        setSigninfo("");
    }

    public TxnHead bizid(String s){
        this.bizid = s;
        return this;
    }
    public TxnHead txncode(String s){
        this.txncode = s;
        return this;
    }
    public TxnHead txnsn(String s){
        this.txnsn = s;
        return this;
    }
    public TxnHead rtncode(String s){
        this.rtncode = s;
        return this;
    }
    public TxnHead rtnmsg(String s){
        this.rtnmsg = s;
        return this;
    }

    //===============
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
        return "TxnHead{" +
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
