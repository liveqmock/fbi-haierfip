package fip.gateway.sbs.core;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: обнГ5:03
 * To change this template use File | Settings | File Templates.
 */
public abstract class  SOFDataHeader extends AbstractSBSMessage{
    protected static int SOFDATA_OFFSET = 27;

    public int getSOFDATA_OFFSET() {
        return SOFDATA_OFFSET;
    }
}
