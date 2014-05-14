package fip.common.constant;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-7-23
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public enum DepUnipayTxnStatus implements EnumApp {
    TXN_QRY_PEND("2000", "交易结果不明"),
    TXN_FAILED("1000", "交易失败"),
    TXN_SUCCESS("0000", "交易成功");

    private String code = null;
    private String title = null;
    private static Hashtable<String, DepUnipayTxnStatus> aliasEnums;

    DepUnipayTxnStatus(String code, String title) {
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

    public static DepUnipayTxnStatus valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
