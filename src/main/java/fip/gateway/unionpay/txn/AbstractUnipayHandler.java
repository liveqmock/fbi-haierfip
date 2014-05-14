package fip.gateway.unionpay.txn;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-12-13
 * Time: 下午3:22
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractUnipayHandler {
    protected  Map<String, String> rtnMainMsgMap = new HashMap<String, String>();


    protected String convertRtnCode(String header_code, String detail_code) {
        if (header_code == null || header_code.equals("")) {
            throw new IllegalArgumentException("返回码不能为空.");
        }
        String rtn;
        if (header_code.equals("0000")) {
            if (detail_code.equals("0000")) {
                rtn = "0000";
            }else{
                rtn = "1000";
            }
        } else if (header_code.startsWith("1")) {
            rtn = "1000";
        } else if (header_code.startsWith("2")) {
            rtn = "2000";
        } else {
            rtn = "2000";  //TODO 默认为状态不确定
        }
        return rtn;
    }

    public Map<String, String> getRtnMainMsgMap() {
        return rtnMainMsgMap;
    }

    public void setRtnMainMsgMap(Map<String, String> rtnMainMsgMap) {
        this.rtnMainMsgMap = rtnMainMsgMap;
    }
}
