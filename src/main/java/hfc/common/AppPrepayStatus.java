package hfc.common;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-15
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
public enum AppPrepayStatus {
    INIT("0","初始"),
    CUTPAY_RECORD_GENERATED("1","已生成扣款记录"),
    CUTPAY_SUCCESS("2","代扣成功"),
    CUTPAY_FAILUER("3","代扣失败");

    private String code = null;
    private String title = null;
    private static Hashtable<String, AppPrepayStatus> aliasEnums;

    AppPrepayStatus(String code, String title) {
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

    public static AppPrepayStatus valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }
     public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
