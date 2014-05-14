package fip.gateway.newcms.domain.T201002;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fip.common.utils.BeanHelper;
import fip.gateway.newcms.domain.common.MsgHeader;

@Deprecated
public class T201002ShowResponse extends T201002Response {
    String appno;  //…Í«Îµ•±‡∫≈

    public T201002ShowResponse() {
    }

    public T201002ShowResponse(T201002Response response) {
        BeanHelper.copy(this, response);
    }

    public String getAppno() {
        return appno;
    }

    public void setAppno(String appno) {
        this.appno = appno;
    }
}
