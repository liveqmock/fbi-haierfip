package fip.common.constant;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-7-23
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public enum BankCode implements EnumApp {
    GONGSHANG("102", "工商银行"),
    NONGYE("103", "农业银行"),
    ZHONGGUO("104", "中国银行"),
    JIANSHE("105", "建设银行");

    private String code = null;
    private String title = null;
    private static Hashtable<String, BankCode> aliasEnums;

    BankCode(String code, String title) {
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

    public static BankCode valueOfAlias(String alias) {
        BankCode cutpayChannelEnum = aliasEnums.get(alias);
        return cutpayChannelEnum;
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
