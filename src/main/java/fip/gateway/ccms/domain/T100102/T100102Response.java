package fip.gateway.ccms.domain.T100102;

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
public class T100102Response extends MsgHeader {
    @XStreamAlias("LIST")
    private fip.gateway.ccms.domain.T100102.T100102ResponseList body;

    public fip.gateway.ccms.domain.T100102.T100102ResponseList getBody() {
        return body;
    }

    public void setBody(fip.gateway.ccms.domain.T100102.T100102ResponseList body) {
        this.body = body;
    }
}
