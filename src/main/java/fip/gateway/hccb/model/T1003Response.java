package fip.gateway.hccb.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by zhanrui on 2015/3/31.
 */
@XStreamAlias("root")
public class T1003Response {
    private TxnHead head = new TxnHead();

    public TxnHead getHead() {
        return head;
    }

    public void setHead(TxnHead head) {
        this.head = head;
    }

    @Override
    public String toString() {
        return "T1003Response{" +
                "head=" + head +
                '}';
    }

}
