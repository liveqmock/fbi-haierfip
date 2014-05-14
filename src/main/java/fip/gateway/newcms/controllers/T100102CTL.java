package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T100102.T100102Request;
import fip.gateway.newcms.domain.T100102.T100102RequestList;
import fip.gateway.newcms.domain.T100102.T100102RequestRecord;
import fip.gateway.newcms.domain.T100102.T100102Response;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import pgw.NewCmsManager;


/**
 * 正常还款回写.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:22:35
 * To change this template use File | Settings | File Templates.
 */
public class T100102CTL  extends BaseCTL {

    private Log logger = LogFactory.getLog(this.getClass());
    private XStream xstream;
    private NewCmsManager ncm;

    public final static void main(String[] args) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost("http://10.143.19.13:10002/LoanSysPortal/CMSServlet");

        // Execute HTTP request
        System.out.println("executing request " + httppost.getURI());

        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100102Request.class);
        xstream.processAnnotations(T100102Response.class);


        T100102Request request = new T100102Request();

        //包头处理
        //查询类交易   TODO
        request.setStdmsgtype("0100");
        //交易码
        request.setStd400trcd("100102");

        request.setStd400aqid("3");
        request.setStd400tlno("teller");

        request.setStdlocdate("20101010");
        request.setStdloctime("153000");

        request.setStdtermtrc("1");

        //包体处理
        T100102RequestList requestBody = new T100102RequestList();
        T100102RequestRecord record = new T100102RequestRecord();
        record.setStdjjh("111112");
        record.setStdqch("1");
        record.setStdjhkkr("20100802");
        record.setStdkkjg("1");
        requestBody.add(record);

        record = new T100102RequestRecord();
        record.setStdjjh("111112");
        record.setStdqch("2");
        record.setStdjhkkr("20100802");
        record.setStdkkjg("1");
        requestBody.add(record);

        //组包
        request.setBody(requestBody);

        //XML
        String strXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xstream.toXML(request);
        System.out.println(strXml);

        StringEntity xml = new StringEntity(strXml, "GBK");
        httppost.setEntity(xml);

        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpclient.execute(httppost, responseHandler);

        System.out.println("----------------------------------------");
        System.out.println(responseBody);
        System.out.println("----------------------------------------");


        T100102Response response = (T100102Response) xstream.fromXML(responseBody);

        System.out.println(response);


        httpclient.getConnectionManager().shutdown();
    }


    public  T100102CTL(){
        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100102Request.class);
        xstream.processAnnotations(T100102Response.class);
        ncm = new NewCmsManager();
    }
    public boolean start(T100102RequestList requestBody) {
        //XStream xstream = new XStream(new DomDriver());
        //xstream.processAnnotations(T100102Request.class);
        //xstream.processAnnotations(T100102Response.class);


        T100102Request request = new T100102Request();
        request.initHeader("0200", "100102", "3");

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

        T100102Response response = (T100102Response) xstream.fromXML(responseBody);

        if ("AAAAAAA".equals(response.getStd400mgid())) {
            return true;
        }else{
            return false;
        }


    }

}
