package fip.gateway.sbs.core;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-15
 * Time: ÉÏÎç11:43
 * To change this template use File | Settings | File Templates.
 */
public class SOFHeader extends AbstractSBSMessage {
    private String fmh;
    private String branchId;
    private String termId;
    private String outDevice;
    private String formDev;
    private String formSys;
    private String formCode;
    private String status;

    {
        offset = 0;
        fieldTypes = new int[]{1, 1, 1, 1, 1, 1, 1, 1};
        fieldLengths = new int[]{3, 10, 4, 1, 1, 2, 4, 2};
    }

    public String getFmh() {
        return fmh;
    }

    public void setFmh(String fmh) {
        this.fmh = fmh;
    }

    public String getBranchId() {
        return branchId;
    }

    public void setBranchId(String branchId) {
        this.branchId = branchId;
    }

    public String getTermId() {
        return termId;
    }

    public void setTermId(String termId) {
        this.termId = termId;
    }

    public String getOutDevice() {
        return outDevice;
    }

    public void setOutDevice(String outDevice) {
        this.outDevice = outDevice;
    }

    public String getFormDev() {
        return formDev;
    }

    public void setFormDev(String formDev) {
        this.formDev = formDev;
    }

    public String getFormSys() {
        return formSys;
    }

    public void setFormSys(String formSys) {
        this.formSys = formSys;
    }

    public String getFormCode() {
        return formCode;
    }

    public void setFormCode(String formCode) {
        this.formCode = formCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int[] getFieldLengths() {
        return fieldLengths;
    }

    public void setFieldLengths(int[] fieldLengths) {
        this.fieldLengths = fieldLengths;
    }
}
