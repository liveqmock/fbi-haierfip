package fip.repository.model.actchk;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-8-7
 * Time: ÏÂÎç12:38
 * To change this template use File | Settings | File Templates.
 */
public class ActchkVO {
    private String pkid1;
    private String pkid2;
    private String msgSn1;
    private String msgSn2;
    private String actnoIn1;
    private String actnoIn2;
    private String actnoOut1;
    private String actnoOut2;
    private BigDecimal txnAmt1;
    private BigDecimal txnAmt2;
    private String dcFlag1;
    private String dcFlag2;
    private String sentSysId1;
    private String sentSysId2;
    private String txnDate1;
    private String txnDate2;
    private String chksts;

    public String getPkid1() {
        return pkid1;
    }

    public void setPkid1(String pkid1) {
        this.pkid1 = pkid1;
    }

    public String getPkid2() {
        return pkid2;
    }

    public void setPkid2(String pkid2) {
        this.pkid2 = pkid2;
    }

    public String getMsgSn1() {
        return msgSn1;
    }

    public void setMsgSn1(String msgSn1) {
        this.msgSn1 = msgSn1;
    }

    public String getMsgSn2() {
        return msgSn2;
    }

    public void setMsgSn2(String msgSn2) {
        this.msgSn2 = msgSn2;
    }

    public String getActnoIn1() {
        return actnoIn1;
    }

    public void setActnoIn1(String actnoIn1) {
        this.actnoIn1 = actnoIn1;
    }

    public String getActnoIn2() {
        return actnoIn2;
    }

    public void setActnoIn2(String actnoIn2) {
        this.actnoIn2 = actnoIn2;
    }

    public String getActnoOut1() {
        return actnoOut1;
    }

    public void setActnoOut1(String actnoOut1) {
        this.actnoOut1 = actnoOut1;
    }

    public String getActnoOut2() {
        return actnoOut2;
    }

    public void setActnoOut2(String actnoOut2) {
        this.actnoOut2 = actnoOut2;
    }


    public String getDcFlag1() {
        return dcFlag1;
    }

    public void setDcFlag1(String dcFlag1) {
        this.dcFlag1 = dcFlag1;
    }

    public String getDcFlag2() {
        return dcFlag2;
    }

    public void setDcFlag2(String dcFlag2) {
        this.dcFlag2 = dcFlag2;
    }

    public String getSentSysId1() {
        return sentSysId1;
    }

    public void setSentSysId1(String sentSysId1) {
        this.sentSysId1 = sentSysId1;
    }

    public String getSentSysId2() {
        return sentSysId2;
    }

    public void setSentSysId2(String sentSysId2) {
        this.sentSysId2 = sentSysId2;
    }

    public String getTxnDate1() {
        return txnDate1;
    }

    public void setTxnDate1(String txnDate1) {
        this.txnDate1 = txnDate1;
    }

    public String getTxnDate2() {
        return txnDate2;
    }

    public void setTxnDate2(String txnDate2) {
        this.txnDate2 = txnDate2;
    }

    public BigDecimal getTxnAmt1() {
        return txnAmt1;
    }

    public void setTxnAmt1(BigDecimal txnAmt1) {
        this.txnAmt1 = txnAmt1;
    }

    public BigDecimal getTxnAmt2() {
        return txnAmt2;
    }

    public void setTxnAmt2(BigDecimal txnAmt2) {
        this.txnAmt2 = txnAmt2;
    }

    public String getChksts() {
        return chksts;
    }

    public void setChksts(String chksts) {
        this.chksts = chksts;
    }
}
