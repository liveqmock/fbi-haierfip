package fip.common.constant;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-7
 * Time: 下午7:04
 * To change this template use File | Settings | File Templates.
 */
public enum TxSendFlag implements EnumApp{
    UNSEND("0", "未发送过"),
    SENT("1", "已发送过");

    private String code = null;
    private String title = null;
    private static Hashtable<String, TxSendFlag> aliasEnums;

    TxSendFlag(String code, String title) {
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

    public static TxSendFlag valueOfAlias(String alias) {
        TxSendFlag txSendFlag = aliasEnums.get(alias);
        return txSendFlag;
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
