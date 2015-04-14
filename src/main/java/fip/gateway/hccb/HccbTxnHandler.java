package fip.gateway.hccb;

/**
 * Created by zhanrui on 2015/4/13.
 */
public interface HccbTxnHandler {
    static String hccbServerUri = "HCCB_SERVER_URL";

    void process(HccbContext context);
}
