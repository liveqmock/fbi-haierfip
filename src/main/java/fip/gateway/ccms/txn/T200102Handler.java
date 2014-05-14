package fip.gateway.ccms.txn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.ccms.domain.T200102.T200102Request;
import fip.gateway.ccms.domain.T200102.T200102Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pgw.CCMSHttpManager;


/**
 * 正常代发回写.
 * User: zhanrui
 * Date: 2012-7-3
 * Time: 13:22:35
 * To change this template use File | Settings | File Templates.
 */
public class T200102Handler extends BaseTxnHandler{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private XStream xstream;
    private CCMSHttpManager httpManager;

    public T200102Handler(){
        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T200102Request.class);
        xstream.processAnnotations(T200102Response.class);
        httpManager = new CCMSHttpManager(SERVER_ID);
    }
    public boolean start(T200102Request request) {
        request.initHeader("0200", "200102", "3");
        request.setStd400acur(String.valueOf("1"));

        String strXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xstream.toXML(request);

        //发送请求
        String responseBody = null;
        try {
            responseBody = httpManager.doPostXml(strXml);
        } catch (Exception e) {
            logger.error("通讯失败");
            throw new RuntimeException("通讯失败", e);
        }

        T200102Response response = (T200102Response) xstream.fromXML(responseBody);

        if ("AAAAAAA".equals(response.getStd400mgid())) {
            return true;
        }else{
            return false;
        }
    }
}
