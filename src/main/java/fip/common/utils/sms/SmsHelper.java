package fip.common.utils.sms;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.advance.utils.PropertyManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by zhanrui on 2014/12/1.
 * 通过DEP平台的短信接口发送SMS
 */
public class SmsHelper {
    private static final Logger logger = LoggerFactory.getLogger(SmsHelper.class);

    private static int MSG_HEADER_LENGTH = 8;

    //txMsg: 电话号码|信息内容
    //多个电话号码以“，”分割
    public static void asyncSendSms(final String phones, final String msg) {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    sendMsgToDep(phones + "|" + msg);
                } catch (IOException e) {
                    logger.error("短信发送失败", e);
                }
            }
        };
        new Thread(task).start();
    }

    //txMsg: 电话号码|信息内容
    //多个电话号码以“，”分割
    public static String syncSendSms(final String phones, final String msg) throws IOException {
        String rtnMsg = "0000"; //已正常发送
        try {
            rtnMsg = sendMsgToDep(phones + "|" + msg);
        } catch (IOException e) {
            rtnMsg = "9999"; //处理异常
            logger.error("短信发送失败", e);
        }
        return rtnMsg;
    }

    public static String sendMsgToDep(String txMsg) throws IOException {
        InetAddress addr = InetAddress.getByName(PropertyManager.getProperty("haier.dep.server.ip"));
        Socket socket = new Socket();
        int timeout_ms = 30 * 1000;
        try {
            int port = PropertyManager.getIntProperty("haier.dep.server.port.sms");
            socket.connect(new InetSocketAddress(addr, port), timeout_ms);
            socket.setSoTimeout(timeout_ms);

            //组报文头
            String smsTxnCode = "0011";
            txMsg = smsTxnCode + txMsg;
            String msgLen = StringUtils.rightPad("" + (txMsg.getBytes("GBK").length + MSG_HEADER_LENGTH), MSG_HEADER_LENGTH, " ");

            OutputStream os = socket.getOutputStream();
            os.write((msgLen + txMsg).getBytes("GBK"));
            os.flush();
            InputStream is = socket.getInputStream();

            byte[] inHeaderBuf = new byte[MSG_HEADER_LENGTH];
            int readNum = is.read(inHeaderBuf);
            if (readNum == -1) {
                throw new RuntimeException("服务器连接已关闭!");
            }
            if (readNum < MSG_HEADER_LENGTH) {
                throw new RuntimeException("读取报文头长度部分错误...");
            }

            int bodyLen = Integer.parseInt((new String(inHeaderBuf).trim())) - MSG_HEADER_LENGTH;
            byte[] inBodyBuf = new byte[bodyLen];

            readNum = is.read(inBodyBuf);   //阻塞读
            if (readNum != bodyLen) {
                throw new RuntimeException("报文长度错误,应为:[" + bodyLen + "], 实际获取长度:[" + readNum + "]");
            }

            return new String(inBodyBuf, "GBK");  //返回报文体内容
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //
            }
        }
    }
}
