package fip.gateway.sbs.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: ÏÂÎç3:37
 * To change this template use File | Settings | File Templates.
 */
public class SBSRequest {
    private String txncode;
    private List<String>  paramList;

    public SBSRequest(String txncode, List<String> paramList){
        this.txncode = txncode;
        this.paramList = new ArrayList<String>();
        for (String s : paramList) {
            this.paramList.add(s);
        }
    }

    public List<String> getParamList() {
        return paramList;
    }

    public String getTxncode() {
        return txncode;
    }
}
