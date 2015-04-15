package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T100110.T100110Request;
import fip.gateway.newcms.domain.T100110.T100110RequestList;
import fip.gateway.newcms.domain.T100110.T100110RequestRecord;
import fip.gateway.newcms.domain.T100110.T100110Response;
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
 * 专卖店回写.
 * User: zhanrui
 * Date: 2010-8-27
 */
public class T100110CTL extends BaseCTL {

    private Log logger = LogFactory.getLog(this.getClass());
    private XStream xstream;
    private NewCmsManager ncm;

    public  static void main(String[] args) throws Exception {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://10.143.19.13:10002/LoanSysPortal/CMSServlet");

        System.out.println("executing request " + httppost.getURI());

        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100110Request.class);
        xstream.processAnnotations(T100110Response.class);


        T100110Request request = new T100110Request();

        //包头处理
        //查询类交易
        request.setStdmsgtype("0100");
        //交易码
        request.setStd400trcd("100110");

        request.setStd400aqid("3");
        request.setStd400tlno("teller");

        request.setStdlocdate("20101010");
        request.setStdloctime("153000");

        request.setStdtermtrc("1");

        //包体处理
        T100110RequestList requestBody = new T100110RequestList();
        T100110RequestRecord record = new T100110RequestRecord();
        record.setStdkhmc("客户名称1");
        record.setStdkhh("123456");
        record.setStdjhhkr("20150414");
        record.setStdkkcg("2");
        requestBody.add(record);

        record = new T100110RequestRecord();
        record.setStdkhmc("客户名称2");
        record.setStdkhh("123457");
        record.setStdjhhkr("20150414");
        record.setStdkkcg("2");
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


        T100110Response response = (T100110Response) xstream.fromXML(responseBody);

        System.out.println(response);

        httpclient.getConnectionManager().shutdown();
    }


    public T100110CTL() {
        xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100110Request.class);
        xstream.processAnnotations(T100110Response.class);
        ncm = new NewCmsManager();
    }

//    public boolean start(T100110RequestList requestBody) {
    public boolean start(T100110RequestRecord record) {
        T100110Request request = new T100110Request();
        request.initHeader("0200", "100110", "3");

        T100110RequestList requestBody = new T100110RequestList();
        requestBody.add(record);
        request.setStd400acur(String.valueOf(requestBody.getContent().size()));

        //组包
        request.setBody(requestBody);

        String strXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xstream.toXML(request);

        //发送请求
        String responseBody = null;
        try {
            responseBody = ncm.doPostXml(strXml);
        } catch (Exception e) {
            logger.error("通讯失败", e);
            throw new RuntimeException("通讯失败", e);
        }

        T100110Response response = (T100110Response) xstream.fromXML(responseBody);

        return "AAAAAAA".equals(response.getStd400mgid());
    }
}
