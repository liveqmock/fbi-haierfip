package fip.gateway.newcms.domain.T100110;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fip.gateway.newcms.domain.common.MsgHeader;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-10-10
 * Time: 17:18:39
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ROOT")
public class T100110Response extends MsgHeader {
    @XStreamAlias("LIST")
    private T100110ResponseList body;

    public T100110ResponseList getBody() {
        return body;
    }

    public void setBody(T100110ResponseList body) {
        this.body = body;
    }
}
