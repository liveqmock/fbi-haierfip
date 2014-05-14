package fip.gateway.sbs.core;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午5:03
 * To change this template use File | Settings | File Templates.
 */
public class SOFDataHeader4SingleRecord extends SOFDataHeader{
    /**
     * 20	SOF-DATA-LEN	包长度	X(2)	为0时，无输出数据
     */
    private short datalen;

    {
        offset = SOFDATA_OFFSET ;
        fieldTypes = new int[]{2};
        fieldLengths = new int[]{2};
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

    public short getDatalen() {
        return datalen;
    }

    public void setDatalen(short datalen) {
        this.datalen = datalen;
    }
}
