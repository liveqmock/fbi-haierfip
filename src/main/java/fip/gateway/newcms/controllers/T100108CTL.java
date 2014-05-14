package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T100108.T100108Request;
import fip.gateway.newcms.domain.T100108.T100108RequestList;
import fip.gateway.newcms.domain.T100108.T100108Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pgw.NewCmsManager;


/**
 * 提前还款回写
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:22:35
 * To change this template use File | Settings | File Templates.
 */
public class T100108CTL extends BaseCTL {

    private Log logger = LogFactory.getLog(this.getClass());
    private XStream xstream;
    private NewCmsManager ncm;



    public T100108CTL(){
        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100108Request.class);
        xstream.processAnnotations(T100108Response.class);
        ncm = new NewCmsManager();
    }
    public boolean start(T100108RequestList requestBody) {
        //XStream xstream = new XStream(new DomDriver());
        //xstream.processAnnotations(T100108Request.class);
        //xstream.processAnnotations(T100108Response.class);


        T100108Request request = new T100108Request();
        request.initHeader("0200", "100108", "3");

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

        T100108Response response = (T100108Response) xstream.fromXML(responseBody);

        return "AAAAAAA".equals(response.getStd400mgid());


    }

}
