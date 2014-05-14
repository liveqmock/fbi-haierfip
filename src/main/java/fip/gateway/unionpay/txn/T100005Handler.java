package fip.gateway.unionpay.txn;

import fip.gateway.domain.DEP1001002TOA;
import fip.gateway.unionpay.domain.T100005Toa;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 实时代付（退款）.
 * User: zhanrui
 * Date: 11-9-22
 * Time: 上午7:25
 * To change this template use File | Settings | File Templates.
 */
public class T100005Handler {
    private static String UNIPAY_TXNCODE = "100005";
    private static String DEP_TXNCODE = "1001002";
    private String msgtxt;
    private T100005Toa toa;
    Map<String, String> rtnMainMsgMap = new HashMap<String, String>();

    public T100005Handler(String msgtxt) {
        this.msgtxt = msgtxt;
        this.toa = T100005Toa.getToa(this.msgtxt);
        rtnMainMsgMap.put("RTN_CODE", toa.INFO.RET_CODE);
        rtnMainMsgMap.put("ERR_MSG", toa.INFO.ERR_MSG);
    }

    public String getRtnCode() {
        return rtnMainMsgMap.get("RTN_CODE");
    }

    public String getErrMsg() {
        return rtnMainMsgMap.get("ERR_MSG");
    }

    public Map<String, String> getRtnMainMsgMap() {
        return rtnMainMsgMap;
    }

    public DEP1001002TOA getToaBean() {
        DEP1001002TOA depToa = new DEP1001002TOA();

        //HEADER处理
        depToa.REQUEST_SN = toa.INFO.REQ_SN;
        depToa.TX_CODE = DEP_TXNCODE;

        String ret_code = toa.INFO.RET_CODE;
        String rtn_msg = "[" + toa.INFO.RET_CODE + "]";
        if (!StringUtils.isEmpty(toa.INFO.ERR_MSG)) {
            rtn_msg = rtn_msg + toa.INFO.ERR_MSG;
        }
        if (ret_code.equals("0000")) {
            ret_code = "0000";
        } else if (ret_code.startsWith("1")) {
            ret_code = "1000";
        } else if (ret_code.startsWith("2")) {
            ret_code = "2000";
        } else {
            ret_code = "2000";  //TODO 默认为状态不确定
        }
        depToa.HEAD_RTN_CODE = ret_code;
        depToa.HEAD_RTN_MSG = rtn_msg;

        //BODY处理
        T100005Toa.Body.BodyDetail detail =  toa.BODY.RET_DETAILS.get(0);
        if (detail != null) {
            depToa.ACCOUNT_NO = detail.ACCOUNT_NO;
            depToa.ACCOUNT_NAME = detail.ACCOUNT_NAME;
            depToa.AMOUNT = detail.AMOUNT;
            depToa.REMARK = detail.REMARK;
            depToa.RECORD_RET_CODE = detail.RET_CODE;
            depToa.RECORD_RTN_MSG = detail.ERR_MSG;
            depToa.RESERVE1 = detail.RESERVE1;
        }

        return depToa;
    }


    public void process() {

    }
}
