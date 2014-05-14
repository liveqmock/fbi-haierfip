package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T100106.T100106Request;
import fip.gateway.newcms.domain.T100106.T100106RequestList;
import fip.gateway.newcms.domain.T100106.T100106Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pgw.NewCmsManager;


/**
 * 正常还款回写.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:22:35
 * To change this template use File | Settings | File Templates.
 */
public class T100106CTL extends BaseCTL {

    private Log logger = LogFactory.getLog(this.getClass());
    private XStream xstream;
    private NewCmsManager ncm;


    public T100106CTL(){
        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100106Request.class);
        xstream.processAnnotations(T100106Response.class);
        ncm = new NewCmsManager();
    }
    public boolean start(T100106RequestList requestBody) {

        T100106Request request = new T100106Request();
        request.initHeader("0200", "100106", "3");

        request.setStd400acur(String.valueOf(requestBody.getContent().size()));

        //组包
        request.setBody(requestBody);


        String strXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xstream.toXML(request);

        //发送请求
        String responseBody = null;
        try {
            //NewCmsManager ncm = new NewCmsManager();
            responseBody = ncm.doPostXml(strXml);
        } catch (Exception e) {
            logger.error("通讯失败");
            throw new RuntimeException("通讯失败", e);
        }

        T100106Response response = (T100106Response) xstream.fromXML(responseBody);

        return "AAAAAAA".equals(response.getStd400mgid());


    }

}
