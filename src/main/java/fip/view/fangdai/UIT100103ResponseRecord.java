package fip.view.fangdai;


import fip.gateway.newcms.domain.T100103.T100103ResponseRecord;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2011-3-16
 * Time: 9:43:19
 * To change this template use File | Settings | File Templates.
 */
public class UIT100103ResponseRecord extends T100103ResponseRecord {
    String isLocked;

    public String getLocked() {
        return isLocked;
    }

    public void setLocked(String locked) {
        isLocked = locked;
    }
}
