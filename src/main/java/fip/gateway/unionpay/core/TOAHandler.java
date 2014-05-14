package fip.gateway.unionpay.core;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-2-23
 * Time: обнГ3:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class TOAHandler {
    protected Map<String, String> rtnMainMsgMap = new HashMap<String, String>();


    public String getRtnCode() {
        return rtnMainMsgMap.get("RTN_CODE");
    }

    public String getErrMsg() {
        return rtnMainMsgMap.get("ERR_MSG");
    }

    public abstract TOA getTOA();
}
