package fip.gateway.unionpay.txn;

import fip.gateway.unionpay.core.TOA;
import fip.gateway.unionpay.core.TOABatchHandler;
import fip.gateway.unionpay.domain.TOA100004;
import fip.repository.model.fip.UnipayQryResult;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-9-22
 * Time: ÉÏÎç7:25
 * To change this template use File | Settings | File Templates.
 */
public class TOA100004Handler extends TOABatchHandler {
    private TOA100004 toa;

    public TOA100004Handler(String msgtxt) {
        this.toa = TOA100004.getToa(msgtxt);
        rtnMainMsgMap.put("RTN_CODE", toa.INFO.RET_CODE);
        rtnMainMsgMap.put("ERR_MSG", toa.INFO.ERR_MSG);
    }

    public Map<String, String> getRtnMainMsgMap() {
        return rtnMainMsgMap;
    }

    @Override
    public TOA getTOA() {
        return this.toa;
    }

    public List<UnipayQryResult> getToaDetailList() {
        return null;
    }


    public void process() {

    }
}
