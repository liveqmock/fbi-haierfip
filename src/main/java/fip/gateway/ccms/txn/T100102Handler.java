package fip.gateway.ccms.txn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.ccms.domain.T100102.T100102Request;
import fip.gateway.ccms.domain.T100102.T100102Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pgw.CCMSHttpManager;


/**
 * ���������д.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:22:35
 * To change this template use File | Settings | File Templates.
 */
public class T100102Handler extends BaseTxnHandler{
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private XStream xstream;

    public T100102Handler(){
        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100102Request.class);
        xstream.processAnnotations(T100102Response.class);
    }
    public boolean start(T100102Request request) {
        //T100102Request request = new T100102Request();
        request.initHeader("0200", "100102", "3");

        request.setStd400acur(String.valueOf("1"));

        //���
        //request.setBody(requestBody);

        String strXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xstream.toXML(request);

        //��������
        String responseBody = null;
        try {
            CCMSHttpManager httpManager = new CCMSHttpManager(SERVER_ID);
            responseBody = httpManager.doPostXml(strXml);
        } catch (Exception e) {
            logger.error("ͨѶʧ��");
            throw new RuntimeException("ͨѶʧ��", e);
        }

        T100102Response response = (T100102Response) xstream.fromXML(responseBody);

        if ("AAAAAAA".equals(response.getStd400mgid())) {
            return true;
        }else{
            return false;
        }
    }
}
