package fip.gateway.ccms.txn;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-1-13
 * Time: обнГ2:11
 * To change this template use File | Settings | File Templates.
 */
public class BaseTxnHandler {
    protected  String SERVER_ID = "CCMS_SERVER_URL";

    public String getSERVER_ID() {
        return SERVER_ID;
    }

    public void setSERVER_ID(String SERVER_ID) {
        this.SERVER_ID = SERVER_ID;
    }
}
