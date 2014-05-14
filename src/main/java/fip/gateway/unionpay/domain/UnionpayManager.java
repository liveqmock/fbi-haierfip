package fip.gateway.unionpay.domain;

import com.EasyLink.security.Crypt;
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
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-7-25
 * Time: 上午10:15
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class UnionpayManager {
    private String serverUrl;
    private Log logger = LogFactory.getLog(this.getClass());

    private HttpClient httpclient = null;
    private HttpPost httppost = null;

    public UnionpayManager() {
        logger.info("初始化银联接口网关。");

        try {
            serverUrl = PropertyManager.getProperty("UNIONPAY_SERVER_URL");
            httpclient = new DefaultHttpClient();
            //请求超时
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000 * 5);
            //读取超时
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 60000 * 5);
            httppost = new HttpPost(serverUrl);

            httppost.getURI();
        } catch (Exception e) {
            logger.error("初始化银联接口网关错误!", e);
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
            xmlStr = this.signMsg(xmlStr);
            logger.info("发送报文(已签名)： " + xmlStr);
            StringEntity xmlSE = new StringEntity(xmlStr, "GBK");
            httppost.setEntity(xmlSE);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            responseBody = httpclient.execute(httppost, responseHandler);

/*            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            Header[] headers = response.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                System.out.println(headers[i]);
            }
            System.out.println("----------------------------------------");
            String responseString = null;
            if (response.getEntity() != null) {
                responseString = EntityUtils.toString(response.getEntity());      //返回服务器响应的HTML代码
                System.out.println(responseString);                                   //打印出服务器响应的HTML代码
            }*/
        } catch (UnsupportedEncodingException e) {
            errmsg = "与银联系统的接口通讯报文格式转换错误!";
            logger.error(errmsg, e);
            throw new RuntimeException(errmsg, e);
        } catch (IOException e) {
            errmsg = "与银联系统的接口通讯连接错误!";
            logger.error(errmsg, e);
            throw new RuntimeException(errmsg, e);
        } catch (Exception e) {
            errmsg = "与银联系统的接口的通讯错误!";
            logger.error(errmsg, e);
            throw new RuntimeException(errmsg, e);
        } finally {
            //TODO  close conn
        }

        if (responseBody == null || responseBody.equals("")) {
            throw new RuntimeException("通讯可能出现错误，返回报文为空！");
        }

        //验签
        if (this.verifySign(responseBody)) {
            logger.info("验签正确，处理服务器返回的报文");
        }

        logger.info("接收报文： " + responseBody);
        return responseBody;
    }

    //===================
    private String signMsg(String strData) {
        String strRnt = "";
        //签名
        Crypt crypt = new Crypt();
        //String pathPfx = "D:\\pdscert\\ORA@TEST1.pfx";
        String pathPfx = "D:\\pdscert\\TESTUSER.pfx";
        String strMsg = strData.replaceAll("<SIGNED_MSG></SIGNED_MSG>", "");
        System.out.println("签名原文:" + strMsg);
        if (crypt.SignMsg(strMsg, pathPfx, "123456")) {
            String signedMsg = crypt.getLastSignMsg();
            strRnt = strData.replaceAll("<SIGNED_MSG></SIGNED_MSG>", "<SIGNED_MSG>" + signedMsg + "</SIGNED_MSG>");
            System.out.println("请求交易报文:" + strRnt);
        } else {
            logger.error(crypt.getLastErrMsg());
            strRnt = strData;
        }
        return strRnt;
    }

    private boolean verifySign(String strXML) {
        //签名
        Crypt crypt = new Crypt();
//		String pathCer = "D:\\pdscert\\ORA@TEST1.cer";
        String pathCer = "D:\\pdscert\\ORA@TEST1.cer";
//		String pathCer = "D:\\pdscert\\bak\\TESTUSER.cer";
        int iStart = strXML.indexOf("<SIGNED_MSG>");
        if (iStart != -1) {
            int end = strXML.indexOf("</SIGNED_MSG>");
            String signedMsg = strXML.substring(iStart + 12, end);
            String strMsg = strXML.substring(0, iStart) + strXML.substring(end + 13);
            logger.debug(signedMsg);
            logger.debug(strMsg);

            if (crypt.VerifyMsg(signedMsg, strMsg, pathCer)) {
                logger.info("verify ok");
                return true;
            } else {
                logger.error(crypt.getLastErrMsg());
                logger.error("verify error");
                return false;
            }
        }
        return true;
    }

}
