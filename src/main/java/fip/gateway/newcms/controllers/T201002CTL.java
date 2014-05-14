package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T201002.T201002Request;
import fip.gateway.newcms.domain.T201002.T201002Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pgw.NewCmsManager;


/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:22:35
 * To change this template use File | Settings | File Templates.
 */
public class T201002CTL  extends BaseCTL {
    private Log logger = LogFactory.getLog(this.getClass());

    public final static void main(String[] args) throws Exception {

        T201002CTL ctl = new T201002CTL();
        ctl.start();

    }

    public T201002Response getAllRecords() {
        return start();
    }

    public T201002Response start() {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T201002Request.class);
        xstream.processAnnotations(T201002Response.class);

        T201002Request request = new T201002Request();

        request.initHeader("0100", "201002", "3");

        //查询 房贷/消费信贷（1/2） 数据
//        request.setStdcxlx("2");
//        int pkgcnt = 100;
//        int startnum = 1;
//        request.setStdymjls(String.valueOf(pkgcnt));
        //request.setStdqsjls("1");

        request.setStdsqdh("3708291990022332560001");

        NewCmsManager ncm = new NewCmsManager();
        String strXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xstream.toXML(request);
        //System.out.println(strXml);

        //发送请求
        String responseBody = ncm.doPostXml(strXml);

        T201002Response response = (T201002Response) xstream.fromXML(responseBody);

        return response;
    }

}
