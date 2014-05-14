package pgw;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import pub.platform.advance.utils.PropertyManager;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * 与新信贷系统接口.
 * User: zhanrui
 * Date: 2010-10-11
 * Time: 11:15:25
 * To change this template use File | Settings | File Templates.
 */
public class NewCmsManager {

    //生产机地址
    //private String serverUrl = "http://10.143.20.33:10002/LoanSysPortal/CMSServlet";
    //测试机地址
    //private String serverUrl = "http://10.143.19.13:10002/LoanSysPortal/CMSServlet";
    private String serverUrl;
    private Log logger = LogFactory.getLog(this.getClass());

    private HttpClient httpclient = null;
    private HttpPost httppost = null;

    public NewCmsManager() {
        logger.info("初始化新信贷接口网关。");

        try {
            serverUrl = PropertyManager.getProperty("HUATENG_SERVER_URL");
            httpclient = new DefaultHttpClient();
            //请求超时
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000*5);
            //读取超时
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000 * 5);
            httppost = new HttpPost(serverUrl);

            httppost.getURI();
        } catch (Exception e) {
            logger.error("初始化新信贷接口网关错误!", e);
            //TODO  close conn
            httpclient.getConnectionManager().shutdown();
            throw new RuntimeException(e);
        }
    }

    public void close() {

    }

    public String doPostXml(String xmlStr) {
        logger.info("发送报文： " + xmlStr);
        String responseBody = null;
        String errmsg = "";
        try {
            StringEntity xmlSE = new StringEntity(xmlStr, "GBK");
            httppost.setEntity(xmlSE);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpclient.execute(httppost, responseHandler);
        } catch (UnsupportedEncodingException e) {
            errmsg = "与信贷系统的接口通讯报文格式转换错误!";
            logger.error(errmsg, e);
            throw new RuntimeException(errmsg,e);
        } catch (IOException e) {
            errmsg = "与信贷系统的接口通讯连接错误!";
            logger.error(errmsg, e);
            throw new RuntimeException(errmsg,e);
        } catch (Exception e) {
            errmsg = "与信贷系统的接口的通讯错误!";
            logger.error(errmsg, e);
            throw new RuntimeException(errmsg,e);
        } finally {
            //TODO  close conn
        }

        if (responseBody == null || responseBody.equals("")) {
            //throw new RuntimeException("通讯可能出现错误，返回报文为空！");
            return "";
        }
        logger.info("接收报文： " + responseBody);
        return responseBody;
    }
}
