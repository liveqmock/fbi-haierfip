package fip.view.grmf;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2011-1-28
 * Time: 16:27:45
 * To change this template use File | Settings | File Templates.
 */
public class GmRepayDetlBean {
    String seqno;
    String iouno;
    String issueno;
    String contractno;
    String clientno;
    String clientname;
    String loanactno;
    String repaymentactno;
    BigDecimal repaymentamt;
    BigDecimal principalamt;
    BigDecimal interestamt;
    BigDecimal penaltyamt;
    BigDecimal otheramt;
    String repaymentdate; //计划还款日 YYYYMMDD (提前还款时 放入审批号 用于返还信贷系统)
    String regioncd;
    String bankcd;
    String billstatus;
    Date createtime;
    String failreason;
    String remark;
    String preflag;
    String journalno;

    public String getSeqno() {
        return seqno;
    }

    public void setSeqno(String seqno) {
        this.seqno = seqno;
    }

    public String getIouno() {
        return iouno;
    }

    public void setIouno(String iouno) {
        this.iouno = iouno;
    }

    public String getIssueno() {
        return issueno;
    }

    public void setIssueno(String issueno) {
        this.issueno = issueno;
    }

    public String getContractno() {
        return contractno;
    }

    public void setContractno(String contractno) {
        this.contractno = contractno;
    }

    public String getClientno() {
        return clientno;
    }

    public void setClientno(String clientno) {
        this.clientno = clientno;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public String getLoanactno() {
        return loanactno;
    }

    public void setLoanactno(String loanactno) {
        this.loanactno = loanactno;
    }

    public String getRepaymentactno() {
        return repaymentactno;
    }

    public void setRepaymentactno(String repaymentactno) {
        this.repaymentactno = repaymentactno;
    }

    public BigDecimal getRepaymentamt() {
        return repaymentamt;
    }

    public void setRepaymentamt(BigDecimal repaymentamt) {
        this.repaymentamt = repaymentamt;
    }

    public BigDecimal getPrincipalamt() {
        return principalamt;
    }

    public void setPrincipalamt(BigDecimal principalamt) {
        this.principalamt = principalamt;
    }

    public BigDecimal getInterestamt() {
        return interestamt;
    }

    public void setInterestamt(BigDecimal interestamt) {
        this.interestamt = interestamt;
    }

    public BigDecimal getPenaltyamt() {
        return penaltyamt;
    }

    public void setPenaltyamt(BigDecimal penaltyamt) {
        this.penaltyamt = penaltyamt;
    }

    public BigDecimal getOtheramt() {
        return otheramt;
    }

    public void setOtheramt(BigDecimal otheramt) {
        this.otheramt = otheramt;
    }

    public String getRepaymentdate() {
        return repaymentdate;
    }

    public void setRepaymentdate(String repaymentdate) {
        this.repaymentdate = repaymentdate;
    }

    public String getRegioncd() {
        return regioncd;
    }

    public void setRegioncd(String regioncd) {
        this.regioncd = regioncd;
    }

    public String getBankcd() {
        return bankcd;
    }

    public void setBankcd(String bankcd) {
        this.bankcd = bankcd;
    }

    public String getBillstatus() {
        return billstatus;
    }

    public void setBillstatus(String billstatus) {
        this.billstatus = billstatus;
    }

    public Date getCreatetime() {
        return createtime;
    }

    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    public String getFailreason() {
        return failreason;
    }

    public void setFailreason(String failreason) {
        this.failreason = failreason;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPreflag() {
        return preflag;
    }

    public void setPreflag(String preflag) {
        this.preflag = preflag;
    }

    public String getJournalno() {
        return journalno;
    }

    public void setJournalno(String journalno) {
        this.journalno = journalno;
    }
}
