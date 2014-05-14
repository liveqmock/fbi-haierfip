package fip.batch.common.model.actchk;

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-7-26
 * Time: ÏÂÎç10:37
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class ZongFenCcbBean {
    private String  txDate;
    private String  txTime;
    private String  txAmount;
    private String  curCode;
    private String  dcFlag;
    private String  abstractStr;
    private String  branchId;
    private String  bankVoucherId;    //Æ¾Ö¤ºÅ Ö÷¼ü
    private String  purpose;
    private String  oprCode;
    private String  outAcctName;
    private String  inAcctName;
    private String  outBranchName;
    private String  inBranchName;
    private String  outAcctId;
    private String  inAcctId;
    private String  memo;

    public String getTxDate() {
        return txDate;
    }

    public void setTxDate(String txDate) {
        this.txDate = txDate;
    }

    public String getTxTime() {
        return txTime;
    }

    public void setTxTime(String txTime) {
        this.txTime = txTime;
    }

    public String getTxAmount() {
        return txAmount;
    }

    public void setTxAmount(String txAmount) {
        this.txAmount = txAmount;
    }

    public String getCurCode() {
        return curCode;
    }

    public void setCurCode(String curCode) {
        this.curCode = curCode;
    }

    public String getDcFlag() {
        return dcFlag;
    }

    public void setDcFlag(String dcFlag) {
        this.dcFlag = dcFlag;
    }

    public String getAbstractStr() {
        return abstractStr;
    }

    public void setAbstractStr(String abstractStr) {
        this.abstractStr = abstractStr;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getBankVoucherId() {
        return bankVoucherId;
    }

    public void setBankVoucherId(String bankVoucherId) {
        this.bankVoucherId = bankVoucherId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getOprCode() {
        return oprCode;
    }

    public void setOprCode(String oprCode) {
        this.oprCode = oprCode;
    }

    public String getOutAcctName() {
        return outAcctName;
    }

    public void setOutAcctName(String outAcctName) {
        this.outAcctName = outAcctName;
    }

    public String getInAcctName() {
        return inAcctName;
    }

    public void setInAcctName(String inAcctName) {
        this.inAcctName = inAcctName;
    }

    public String getOutBranchName() {
        return outBranchName;
    }

    public void setOutBranchName(String outBranchName) {
        this.outBranchName = outBranchName;
    }

    public String getInBranchName() {
        return inBranchName;
    }

    public void setInBranchName(String inBranchName) {
        this.inBranchName = inBranchName;
    }

    public String getOutAcctId() {
        return outAcctId;
    }

    public void setOutAcctId(String outAcctId) {
        this.outAcctId = outAcctId;
    }

    public String getInAcctId() {
        return inAcctId;
    }

    public void setInAcctId(String inAcctId) {
        this.inAcctId = inAcctId;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    public String toString() {
        return "ZongFenCcbBean{" +
                "txDate='" + txDate + '\'' +
                ", txTime='" + txTime + '\'' +
                ", txAmount='" + txAmount + '\'' +
                ", curCode='" + curCode + '\'' +
                ", dcFlag='" + dcFlag + '\'' +
                ", abstractStr='" + abstractStr + '\'' +
                ", branchId='" + branchId + '\'' +
                ", bankVoucherId='" + bankVoucherId + '\'' +
                ", purpose='" + purpose + '\'' +
                ", oprCode='" + oprCode + '\'' +
                ", outAcctName='" + outAcctName + '\'' +
                ", inAcctName='" + inAcctName + '\'' +
                ", outBranchName='" + outBranchName + '\'' +
                ", inBranchName='" + inBranchName + '\'' +
                ", outAcctId='" + outAcctId + '\'' +
                ", inAcctId='" + inAcctId + '\'' +
                ", memo='" + memo + '\'' +
                '}';
    }
}
