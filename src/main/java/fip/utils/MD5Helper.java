package fip.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5
 */
public class MD5Helper {
    final static char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static MessageDigest messagedigest = null;

    static {
        try {
            messagedigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            System.err.println(MD5Helper.class.getName() + "MD5初始失败!");
            e.printStackTrace();
        }
    }

    public static String getMD5String(String s) {
        return getMD5String(s.getBytes());
    }

    public static String getMD5String(byte[] bytes) {
        messagedigest.update(bytes);
        return bufferToHex(messagedigest.digest());
    }

    private static String bufferToHex(byte bytes[]) {
        return bufferToHex(bytes, 0, bytes.length);
    }

    private static String bufferToHex(byte bytes[], int m, int n) {
        StringBuffer stringbuffer = new StringBuffer(2 * n);
        int k = m + n;
        for (int l = m; l < k; l++) {
            appendHexPair(bytes[l], stringbuffer);
        }
        return stringbuffer.toString();
    }

    private static void appendHexPair(byte bt, StringBuffer stringbuffer) {
        // 取字节中高 4 位的数字转换
        char c0 = hexDigits[(bt & 0xf0) >> 4];
        // 取字节中低 4 位的数字转换
        char c1 = hexDigits[bt & 0xf];
        stringbuffer.append(c0);
        stringbuffer.append(c1);
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String md5 = null;
//        md5 = MD5Helper.getMD5String(new String("汉字".getBytes("UTF-8"), "GBK"));
//        md5 = MD5Helper.getMD5String("汉字");
        String str = "<?xml version=\"1.0\" encoding=\"gbk\"?><ROOT><INFO><TXN_CODE>9009101</TXN_CODE><REQ_SN>1748</REQ_SN></INFO><BODY xsi:type=\"financialResultsNoticeBody\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><BANKSN>140725010001124</BANKSN><BNKDAT>20140731</BNKDAT><BNKTIM>000000</BNKTIM><PBKNUM>302452036174</PBKNUM><PBKNAM></PBKNAM><PAYACT>B2BCZ20140001</PAYACT><PAYNAM>B2BCZ</PAYNAM><TXNAMT>10000</TXNAMT><DCTYPE>C</DCTYPE><RECACT>1</RECACT><RECNAM>日日顺分公司20140001</RECNAM><IBKACT>88888888888888</IBKACT><IBKNUM>302452036174</IBKNUM><IBKNAM></IBKNAM><RETAUX></RETAUX></BODY></ROOT>"
                + "20140802" + "SCFAPP001SCF20140711001";
        md5 = MD5Helper.getMD5String(str.getBytes("UTF-8"));
        System.out.println("md5:" + md5);
    }
}
