package fip.gateway.sbs.core;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午5:03
 * To change this template use File | Settings | File Templates.
 */
public class SOFDataHeader4MultiRecord extends SOFDataHeader{
    /**
     * 20	SOF-DATA-LEN	包长度	X(2)	为0时，无输出数据
     * 15	TXXX-TOTCNT	总记录数	X(6) 	满6位前补0
     * 15	TXXX-CURCNT	本包内记录数	X(6)	满6位前补0
     */
    private short datalen;
    private String totcnt;
    private String curcnt;

    {
        offset = SOFDATA_OFFSET ;
        fieldTypes = new int[]{2, 1, 1};
        fieldLengths = new int[]{2, 6, 6};
    }

    public String getTotcnt() {
        return totcnt;
    }

    public void setTotcnt(String totcnt) {
        this.totcnt = totcnt;
    }

    public String getCurcnt() {
        return curcnt;
    }

    public void setCurcnt(String curcnt) {
        this.curcnt = curcnt;
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
