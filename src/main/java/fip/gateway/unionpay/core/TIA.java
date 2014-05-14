package fip.gateway.unionpay.core;

import pub.platform.advance.utils.PropertyManager;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-1-31
 * Time: 下午2:45
 */
public abstract class TIA implements Serializable {
    public abstract TIAHeader getHeader();
    public abstract TIABody getBody();

    public String toXml(String bizid) {
        if (bizid == null || "".endsWith(bizid)) {
            throw new RuntimeException("BIZID标识为空！");
        }
        if ("1".equals(PropertyManager.getProperty("unipay_debug_mode"))) { //测试
            bizid = "TEST";
        }
        bizid = bizid.toUpperCase();
        String xml = this.toString();
        String s1 = xml.replaceFirst("<USER_NAME></USER_NAME>",
                "<USER_NAME>" + PropertyManager.getProperty("unionpay_user_name_" + bizid) + "</USER_NAME>");
        String s2 = s1.replaceFirst("<USER_PASS></USER_PASS>",
                "<USER_PASS>" + PropertyManager.getProperty("unionpay_user_pass_" + bizid) + "</USER_PASS>");
        String s3 = s2.replaceFirst("<BUSINESS_CODE></BUSINESS_CODE>",
                "<BUSINESS_CODE>" + PropertyManager.getProperty("unionpay_business_code_" + bizid) + "</BUSINESS_CODE>");
        String s4 = s3.replaceFirst("<MERCHANT_ID></MERCHANT_ID>",
                "<MERCHANT_ID>" + PropertyManager.getProperty("unionpay_merchant_id_" + bizid) + "</MERCHANT_ID>");
        return s4;
    }
}
