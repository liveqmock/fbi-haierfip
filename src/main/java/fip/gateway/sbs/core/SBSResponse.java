package fip.gateway.sbs.core;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: ÏÂÎç5:15
 * To change this template use File | Settings | File Templates.
 */
public abstract class SBSResponse {
    protected byte[] buffer;

    protected String formcode;
    protected String forminfo;

    protected SOFHeader sofHeader = new SOFHeader();

    public byte[] getBuffer() {
        return buffer;
    }

    public String getFormcode() {
        return formcode;
    }

    public void setSofHeader(SOFHeader sofHeader) {
        this.sofHeader = sofHeader;
    }

    public String getForminfo() {
        return forminfo;
    }

    //==========================================
    abstract public void init(byte[] buffer);
}
