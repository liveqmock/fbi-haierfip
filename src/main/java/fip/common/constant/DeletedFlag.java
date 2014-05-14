package fip.common.constant;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-7
 * Time: ÏÂÎç7:04
 * To change this template use File | Settings | File Templates.
 */
public enum DeletedFlag implements EnumApp{
    UNDELETED("0", "Î´É¾³ý"),
    DELETED("1", "ÒÑÉ¾³ý");

    private String code = null;
    private String title = null;
    private static Hashtable<String, DeletedFlag> aliasEnums;

    DeletedFlag(String code, String title) {
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

    public static DeletedFlag valueOfAlias(String alias) {
        DeletedFlag txSendFlag = aliasEnums.get(alias);
        return txSendFlag;
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
