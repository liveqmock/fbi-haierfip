package fip.common.constant;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-7-23
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public enum BizType implements EnumApp {
    XF("XF", "消费信贷"),
    XFNEW("XFNEW", "新消费信贷"),
    XFSF("XFSF", "消费信贷首付款"),
    FD("FD", "住房按揭"),
    FDC("FDC", "房地产分期"),
    HCSP("HCSP", "售后"),
    ZYQD("ZYQD", "自有渠道"),
    GM("GM", "个人买方信贷");

    private String code = null;
    private String title = null;
    private static Hashtable<String, BizType> aliasEnums;

    BizType(String code, String title) {
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

    public static BizType valueOfAlias(String alias) {
        BizType bizType = aliasEnums.get(alias);
        return bizType;
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
