package mock.onekeyactchk.domain;

import fip.common.constant.EnumApp;

import java.util.Hashtable;

/**
 * User: zhanrui
 */
public enum TxnStatus implements EnumApp {
    INIT("00", "初始"),
    INFORM_SUCC("10", "启动对账成功"),
    INFORM_FAIL("11", "启动对账失败"),
    ACCT_UNDERWAY("20", "对账进行中"),
    ACCT_FAIL_EXCEPTION("21", "对账过程异常"),
    ACCT_SUCC_BANLANCE("30", "对账结果:平帐"),
    ACCT_SUCC_NOTBANLANCE("31", "对账结果:不平");

    private String code = null;
    private String title = null;
    private static Hashtable<String, TxnStatus> aliasEnums;

    TxnStatus(String code, String title) {
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

    public static TxnStatus valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
