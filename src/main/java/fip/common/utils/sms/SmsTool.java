package fip.common.utils.sms;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import pub.platform.advance.utils.PropertyManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

/**
 * 发送短信的工具类
 */
public class SmsTool {
    //从配置文件中获取发送短信的配置.
    private static String OPERID = PropertyManager.getProperty("SMS_OPERID");
    private static String OPERPASS = PropertyManager.getProperty("SMS_OPERPASS");
    private static String SMS_SEND_URL = PropertyManager.getProperty("SMS_SEND_URL");
    //失败重复次数
    private final static int REPEAT_NUM = Integer.parseInt(PropertyManager.getProperty("SMS_REPEAT_NUM").trim());

    protected final static Log logger = LogFactory.getLog(SmsTool.class);
    protected final static String PREFIX = "<?xml version=\"1.0\" encoding=\"GBK\"?>\r\n<CoreSMS><OperID>" + OPERID
            + "</OperID><OperPass>" + OPERPASS + "</OperPass><Action>Submit</Action>"
            + "<Category>0</Category><Body><SendTime></SendTime><AppendID></AppendID>";
    protected final static String SUFFIX = "</Body></CoreSMS>";

    private static String buildMessage(String cellphone, String message) {
        StringBuilder builder = new StringBuilder(PREFIX);
        builder.append("<Message>");
        builder.append("<DesMobile>").append(cellphone).append("</DesMobile>");
        builder.append("<Content>").append(message).append("</Content>");
        builder.append("</Message>");
        builder.append(SUFFIX);
        return builder.toString();
    }

    private static boolean executeSend(String msg) throws HttpException, IOException {
        if (logger.isInfoEnabled()) {
            logger.info("发送至短信平台报文:" + msg);
        }

        HttpClient httpclient = new DefaultHttpClient();
        String content = "";
        try {
            HttpPost httppost = new HttpPost(SMS_SEND_URL);

            //请求超时
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000 * 5);
            //读取超时
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000 * 10);

            StringEntity xmlSE = new StringEntity(msg, "GBK");
            httppost.setEntity(xmlSE);

            HttpResponse response = httpclient.execute(httppost);

            int statusCode = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != statusCode) {
                logger.error("短信平台通讯响应:" + statusCode);
                return false;
            }

            HttpEntity entity = response.getEntity();

            if (entity != null) {
                content = EntityUtils.toString(entity);
            } else{
                logger.error("短信平台通讯响应报文内容为空");
                return false;
            }
        } finally {
            httpclient.getConnectionManager().shutdown();
        }


        return verifyResponseResult(content);
    }

    //校验短信平台响应报文的响应码
    private static boolean verifyResponseResult(String content) {
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(new ByteArrayInputStream(content.getBytes()));
            logger.info("短信平台通讯响应报文:" + document.asXML());
            Element root = document.getRootElement();
            String[] values = new String[3];
            Boolean found = false;
            getResultCode(root, values, found);
            String resultCode = "";
            resultCode = values[1];
            if ("0".equals(resultCode)) {
                return true;
            } else {
                logger.error("短信平台通讯响应报文:" + resultCode);
                return false;
            }
        } catch (DocumentException ex) {
            logger.error(ex.getMessage(), ex);
            throw new RuntimeException(ex.getMessage());
        }
    }


    private static void getResultCode(Element element, String[] results, Boolean found) {
        if (!found) {
            for (Iterator i = element.elementIterator(); i.hasNext() && !found; ) {//获得交易列表
                Element node = (Element) i.next();
                if ("Code".equals(node.getName())) {
                    results[0] = node.getName();
                    results[1] = node.getTextTrim();
                    found = true;
                } else {
                    getResultCode(node, results, found);
                }
            }
        }
    }

    //按顺序发送指定的短讯内容到指定手机号码中, 例如: 发送短讯(message) 到 手机(cellphone)
    public static boolean sendMessage(String cellphone, String message) {

        try {
            if (StringUtils.isEmpty(cellphone)) {
                return true;
            }
            boolean result = executeSend(buildMessage(cellphone, message));
            //FIXME 关闭,发送失败自动重复, 最多重发三次
            for (int i = 0; i < REPEAT_NUM && !result; i++) {
                result = executeSend(buildMessage(cellphone, message));
            }
            return result;
        } catch (HttpException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            sendMessage("13905320001", "海尔短信平台测试短信");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
