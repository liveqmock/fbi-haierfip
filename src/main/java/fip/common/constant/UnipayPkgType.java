package fip.common.constant;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-7-23
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public enum UnipayPkgType implements EnumApp {
    SYNC("SYNC", "实时代扣"),
    ASYNC("ASYNC", "批量代扣");

    private String code = null;
    private String title = null;
    private static Hashtable<String, UnipayPkgType> aliasEnums;

    UnipayPkgType(String code, String title) {
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

    public static UnipayPkgType valueOfAlias(String alias) {
        UnipayPkgType cutpayChannelEnum = aliasEnums.get(alias);
        return cutpayChannelEnum;
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
