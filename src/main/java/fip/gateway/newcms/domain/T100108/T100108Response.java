package fip.gateway.newcms.domain.T100108;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fip.gateway.newcms.domain.common.MsgHeader;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2012-10-10
 * Time: 17:18:39
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ROOT")
public class T100108Response extends MsgHeader {
    @XStreamAlias("LIST")
    private T100108ResponseList body;

    public T100108ResponseList getBody() {
        return body;
    }

    public void setBody(T100108ResponseList body) {
        this.body = body;
    }
}
