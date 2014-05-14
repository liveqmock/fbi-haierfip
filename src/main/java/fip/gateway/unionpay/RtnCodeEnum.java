package fip.gateway.unionpay;

import pub.platform.advance.utils.PropertyManager;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-5
 * Time: ÏÂÎç3:55
 * To change this template use File | Settings | File Templates.
 */
public enum RtnCodeEnum {

     UNIONPAY_TRX_CODE_100004_SUCCESS("unionpay_trx_code_100004_success"),
    UNIONPAY_TRX_CODE_100004_FAILE("unionpay_trx_code_100004_faile"),
    UNIONPAY_TRX_CODE_100004_WAIT("unionpay_trx_code_100004_wait"),
    UNIONPAY_TRX_CODE_100004_AGAIN("unionpay_trx_code_100004_again"),

    UNIONPAY_TRX_CODE_200001_SUCCESS("unionpay_trx_code_200001_success"),
    UNIONPAY_TRX_CODE_200001_FAILE("unionpay_trx_code_200001_faile"),
    UNIONPAY_TRX_CODE_200001_WAIT("unionpay_trx_code_200001_wait"),
    UNIONPAY_TRX_CODE_200001_AGAIN("unionpay_trx_code_200001_again") ,

    UNIONPAY_TRX_CODE_100001_SUCCESS("unionpay_trx_code_100001_success"),
    UNIONPAY_TRX_CODE_100001_FAILE("unionpay_trx_code_100001_faile"),
    UNIONPAY_TRX_CODE_100001_WAIT("unionpay_trx_code_100001_wait"),
    UNIONPAY_TRX_CODE_100001_AGAIN("unionpay_trx_code_100001_again");

    private String value;
    private String[] values;
    private RtnCodeEnum(String key) {
       if(key != null && key.startsWith("fbidep")) {
            value = PropertyManager.getProperty(key);
       }else
       values = PropertyManager.getProperty(key).split(",");
    }
     public String[] getValues() {
         return this.values;
     }
    public String getValue() {
        return this.value;
    }

}
