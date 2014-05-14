package fip.common.constant;

import java.util.Hashtable;

/**
 * 交易状态
 */
public enum PayoutDetlRtnCode implements EnumApp {
    SUCCESS("0000", "交易成功"),
    HALFWAY("1000", "交易处理中"),
    FAIL("2000", "交易失败");

    private String code = null;
    private String title = null;
    private static Hashtable<String, PayoutDetlRtnCode> aliasEnums;

    PayoutDetlRtnCode(String code, String title) {
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

    public static PayoutDetlRtnCode valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }

    public String toRtnMsg() {
        return this.code + "|" + this.title;
    }
}
