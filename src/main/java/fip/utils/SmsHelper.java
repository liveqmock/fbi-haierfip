package fip.utils;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by zhanrui on 2014/12/1.
 */
public class SmsHelper {
    private static final Logger logger = LoggerFactory.getLogger(SmsHelper.class);

    private static int MSG_HEADER_LENGTH = 8;

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
        Thread t = new Thread(task);
        t.start();
    }

    private static String sendMsgToDep(String msg) throws IOException {
        InetAddress addr = InetAddress.getByName("10.143.18.20");
        Socket socket = new Socket();
        int timeout_ms = 30 * 1000;
        try {
            socket.connect(new InetSocketAddress(addr, 61002), timeout_ms);
            socket.setSoTimeout(timeout_ms);

            //组报文头
            String smsTxnCode = "0011";
            msg = smsTxnCode + msg;
            String msgLen = StringUtils.rightPad("" + (msg.getBytes("GBK").length + MSG_HEADER_LENGTH), MSG_HEADER_LENGTH, " ");

            OutputStream os = socket.getOutputStream();
            os.write((msgLen + msg).getBytes("GBK"));
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

            return new String(inBodyBuf, "GBK");
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                //
            }
        }
    }
}
