package fip.gateway.sbs;

import fip.gateway.JmsManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 通过DEP收发SBS报文 走core接口
 * User: zhanrui
 * Date: 20120831
 * Time: 20:58:47
 */
public class DepCtgManager {
    private static Logger logger = LoggerFactory.getLogger(DepCtgManager.class);
    private static int iCommareaSize = 32000;

    public static byte[] processSingleResponsePkg(String txnCode, List paramList) {
        //默认
        String termCode = "MT01";
        String tlrCode = "MT01";
        return processSingleResponsePkg(txnCode, paramList, termCode, tlrCode);
    }

    public static byte[] processSingleResponsePkg(String txnCode, List paramList, String termCode) {
        //柜员号与终端号相同
        return processSingleResponsePkg(txnCode, paramList, termCode, termCode);
    }

    public static byte[] processSingleResponsePkg(String txnCode, List paramList, String termCode, String tlrCode) {
        if (termCode == null || "".equals(termCode)) {
            throw new IllegalArgumentException("终端号为空");
        }
        if (termCode.length() != 4) {
            throw new IllegalArgumentException("终端号长度错");
        }
        if (tlrCode == null || "".equals(tlrCode)) {
            throw new IllegalArgumentException("柜员号为空");
        }
        if (tlrCode.length() != 4) {
            throw new IllegalArgumentException("柜员号长度错");
        }

        try {
            //通讯耗时
            String requestBuffer = "";
            byte[] abytCommarea = new byte[iCommareaSize];

            //包头内容，xxxx交易，010网点，MPC1终端，MPC1柜员，包头定长51个字符
            requestBuffer = "TPEI" + txnCode + "  010       " + termCode + tlrCode;

            //打包包头
            System.arraycopy(getBytes(requestBuffer), 0, abytCommarea, 0, requestBuffer.length());

            //打包包体
            setBufferValues(paramList, abytCommarea);

            String sendTime = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());

            logger.info("交易" + txnCode + " 发送报文: " + sendTime + format16(truncBuffer(abytCommarea)));

            long starttime = System.currentTimeMillis();

            //通过MQ与DEP通讯 走CORE接口
            byte[] toabuf = JmsManager.getInstance().sendAndRecvForDepCoreInterface(abytCommarea);

            long endtime = System.currentTimeMillis();
            logger.info("===本包通讯DEP-MQ耗时:" + (endtime - starttime) + "ms.");

            return toabuf;
        } catch (Exception e) {
            logger.error("与SBS通讯出现问题(DEP-MQ)：", e);
            throw new RuntimeException(e.getMessage());
        }
    }


    //===================================================================================================
    private static byte[] getBytes(String source) throws java.io.UnsupportedEncodingException {
        String strDataConv = "ASCII";
        return source.getBytes(strDataConv);
    }

    private static void setBufferValues(List list, byte[] bb) throws UnsupportedEncodingException {
        int start = 51;
        for (int i = 1; i <= list.size(); i++) {
            String value = list.get(i - 1).toString();
            setVarData(start, value, bb);
            start = start + value.getBytes("GBK").length + 2;
        }
    }


    private static void setVarData(int pos, String data, byte[] aa) throws UnsupportedEncodingException {
        short len = (short) data.getBytes("GBK").length;

        byte[] slen = new byte[2];
        slen[0] = (byte) (len >> 8);
        slen[1] = (byte) (len);
        System.arraycopy(slen, 0, aa, pos, 2);
        System.arraycopy(data.getBytes(), 0, aa, pos + 2, len);
    }


    /**
     * 16进制格式化输出
     */
    private static String format16(byte[] buffer) {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        int n = 0;
        byte[] lineBuffer = new byte[16];
        for (byte b : buffer) {
            if (n % 16 == 0) {
                result.append(String.format("%05x: ", n));
                lineBuffer = new byte[16];
            }
            result.append(String.format("%02x ", b));
            lineBuffer[n % 16] = b;
            n++;
            if (n % 16 == 0) {
                result.append(new String(lineBuffer));
                result.append("\n");
            }
            if (n >= 1024) {
                result.append("报文过大，已截断...");
                break;
            }
        }
        for (int k = 0; k < (16 - n % 16); k++) {
            result.append("   ");
        }
        result.append(new String(lineBuffer));
        result.append("\n");
        return result.toString();
    }

    private static byte[] truncBuffer(byte[] buffer) {
        int count = 0;
        for (int i = 0; i < iCommareaSize; i++) {
            if (buffer[iCommareaSize - 1 - i] == 0x00) {
                count++;
            } else {
                break;
            }
        }
        byte[] outBuffer = new byte[iCommareaSize - count];
        System.arraycopy(buffer, 0, outBuffer, 0, outBuffer.length);
        return outBuffer;
    }
}
