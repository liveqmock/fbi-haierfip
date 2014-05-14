package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T100104.T100104Request;
import fip.gateway.newcms.domain.T100104.T100104RequestList;
import fip.gateway.newcms.domain.T100104.T100104Response;
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
public class T100104CTL extends BaseCTL {

    private Log logger = LogFactory.getLog(this.getClass());
    private XStream xstream;
    private NewCmsManager ncm;



    public T100104CTL(){
        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100104Request.class);
        xstream.processAnnotations(T100104Response.class);
        ncm = new NewCmsManager();
    }
    public boolean start(T100104RequestList requestBody) {
        //XStream xstream = new XStream(new DomDriver());
        //xstream.processAnnotations(T100104Request.class);
        //xstream.processAnnotations(T100104Response.class);


        T100104Request request = new T100104Request();
        request.initHeader("0200", "100104", "3");

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

        T100104Response response = (T100104Response) xstream.fromXML(responseBody);

        return "AAAAAAA".equals(response.getStd400mgid());


    }

}
