package fip.gateway.unionpay.txn;

import fip.gateway.unionpay.core.TOA;
import fip.gateway.unionpay.core.TOABatchHandler;
import fip.gateway.unionpay.domain.TOA200002;
import fip.repository.model.fip.UnipayQryResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-9-22
 * Time: ÉÏÎç7:25
 * To change this template use File | Settings | File Templates.
 */
public class TOA200002Handler extends TOABatchHandler {
    private TOA200002 toa;

    public TOA200002Handler(String msgtxt) {
        this.toa = TOA200002.getToa(msgtxt);
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

    public int getPageNum(){
        String PAGE_SUM = toa.BODY.QUERY_TRANS.PAGE_SUM;
        return Integer.parseInt(PAGE_SUM);
    }
    public List<UnipayQryResult> getToaDetailList() {
        List<UnipayQryResult> resultList = new ArrayList<UnipayQryResult>();
        String qrysn = toa.BODY.QUERY_TRANS.QUERY_SN;
        for (TOA200002.Body.BodyDetail detail : toa.BODY.RET_DETAILS) {
            UnipayQryResult result = new UnipayQryResult();
            result.setQUERY_SN(qrysn);
            result.setORAFILE_ID(detail.ORAFILE_ID);
            result.setSN(detail.SN);
            result.setACCOUNT(detail.ACCOUNT);
            result.setACCOUNT_NAME(detail.ACCOUNT_NAME);
            //result.setAMOUNT(String.valueOf((float )(Long.parseLong(detail.AMOUNT)/100)));
            result.setAMOUNT(detail.AMOUNT);

            result.setRET_CODE(detail.RET_CODE);
            result.setERR_MSG(detail.ERR_MSG);
            result.setCOMPLETE_TIME(detail.COMPLETE_TIME.substring(0,8)+ " " + detail.COMPLETE_TIME.substring(8));
            resultList.add(result);
        }
        return resultList;
    }

    public void process() {

    }
}
