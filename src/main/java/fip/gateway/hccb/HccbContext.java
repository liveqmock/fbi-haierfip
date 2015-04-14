package fip.gateway.hccb;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhanrui on 2015/4/13.
 */
public class HccbContext {
    private Object request;
    private Object response;
    private Map<String, String> paraMap = new HashMap<String, String>();

    public Object getRequest() {
        return request;
    }

    public void setRequest(Object request) {
        this.request = request;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public Map<String, String> getParaMap() {
        return paraMap;
    }

    public void setParaMap(Map<String, String> paraMap) {
        this.paraMap = paraMap;
    }
}
