package fip.repository.model.fip;

import java.math.BigDecimal;

/**
 * User: zhanrui
 * Date: 2014-11-24
 */
public class LazyDataCutpaydetlParam {
    private String seqNo;  //batch_sn + batch_detl_sn
    private String iouNo;
    private String billStatus;
    private String bankId;
    private String custName;
    private String startDate;
    private String endDate;
    private String bizId;
    private String archiveflag;
    private String deletedflag;
    private BigDecimal startAmt;
    private BigDecimal endAmt;

    private int offset;
    private int pagesize;
    private String sortField;
    private String sortOrder;


    public String getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(String seqNo) {
        this.seqNo = seqNo;
    }

    public String getIouNo() {
        return iouNo;
    }

    public void setIouNo(String iouNo) {
        this.iouNo = iouNo;
    }

    public String getBillStatus() {
        return billStatus;
    }

    public void setBillStatus(String billStatus) {
        this.billStatus = billStatus;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getStartAmt() {
        return startAmt;
    }

    public void setStartAmt(BigDecimal startAmt) {
        this.startAmt = startAmt;
    }

    public BigDecimal getEndAmt() {
        return endAmt;
    }

    public void setEndAmt(BigDecimal endAmt) {
        this.endAmt = endAmt;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getPagesize() {
        return pagesize;
    }

    public void setPagesize(int pagesize) {
        this.pagesize = pagesize;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getArchiveflag() {
        return archiveflag;
    }

    public void setArchiveflag(String archiveflag) {
        this.archiveflag = archiveflag;
    }

    public String getDeletedflag() {
        return deletedflag;
    }

    public void setDeletedflag(String deletedflag) {
        this.deletedflag = deletedflag;
    }
}
