package fip.gateway.ccms.domain.T200101;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fip.gateway.ccms.domain.common.MsgHeader;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-10-10
 * Time: 17:18:39
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ROOT")
public class T200101Response extends MsgHeader {
    @XStreamAlias("LIST")
    private T200101ResponseList body;

    public T200101ResponseList getBody() {
        return body;
    }

    public void setBody(T200101ResponseList body) {
        this.body = body;
    }
}
