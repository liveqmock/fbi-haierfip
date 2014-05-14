package hfc.common;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-14
 * Time: 上午10:58
 * To change this template use File | Settings | File Templates.
 */

/**
 * CMS返回贷款形态
 */
public enum LoanStatusEnum {
    /**
     * 0-正常
     * 1-逾期
     * 2-呆滞
     * 3-呆帐
     * 4-结清
     */
    CMS_NORMAL("0", "正常"),
    CMS_TIME_BEYOND("1", "逾期"),
    CMS_IDLE("2", "呆滞"),
    CMS_LIFELESS("3", "呆账"),
    CMS_SETTLE("4", "结清");
    private String code = null;
    private String title = null;
    private static Hashtable<String, LoanStatusEnum> aliasEnums;

    LoanStatusEnum(String code, String title) {
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

    public static LoanStatusEnum valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
