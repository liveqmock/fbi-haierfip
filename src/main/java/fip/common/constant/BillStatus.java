package fip.common.constant;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-7-23
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public enum BillStatus implements EnumApp {
    INIT("00", "初始获取"),
    PACKED("01", "已打包"),
    RESEND_PEND("02", "待重发"),
    CUTPAY_QRY_PEND("10", "银行结果不明"),
    CUTPAY_FAILED("11", "银行处理失败"),
    CUTPAY_SUCCESS("12", "银行处理成功"),
    ACCOUNT_PEND("20", "待入帐"),
    ACCOUNT_QRY_PEND("21", "入帐结果不明"),
    ACCOUNT_FAILED("22", "入帐失败"),
    ACCOUNT_SUCCESS("23", "入帐成功"),
    CMS_PEND("30", "待回写"),
    CMS_QRY_PEND("31", "回写结果不明"),
    CMS_FAILED("32", "回写失败"),
    CMS_SUCCESS("33", "回写成功");
    //REFUND_PEND("40", "待退款"),
    //REFUND_QRY_PEND("41", "退款结果不明"),
    //REFUND_FAILED("42", "退款失败"),
    //REFUND_SUCCESS("43", "退款成功");

    private String code = null;
    private String title = null;
    private static Hashtable<String, BillStatus> aliasEnums;

    BillStatus(String code, String title) {
        this.init(code, title);
    }

    @SuppressWarnings("unchecked")
    private void init(String code, String title) {
        this.code = code;
        this.title = title;
        synchronized (this.getClass()) {
            if (aliasEnums == null) {
                aliasEnums = new Hashtable();
            }
        }
        aliasEnums.put(code, this);
        aliasEnums.put(title, this);
    }

    public static BillStatus valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
