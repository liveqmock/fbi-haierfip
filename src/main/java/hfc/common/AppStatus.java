package hfc.common;

import java.util.HashMap;
import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-15
 * Time: 下午2:30
 * To change this template use File | Settings | File Templates.
 */
public enum AppStatus {
    APP_REJECT("0", "初审拒绝"),
    APP_COMMIT("1", "申请提交"),
    APP_UPSUCCESS("2", "上传成功");

    private String code = null;
    private String title = null;
    private static Hashtable<String, AppStatus> aliasEnums;
    private HashMap<String, String> enumValueMap;
    public static Hashtable<String, AppStatus> aliasEnums1;

    AppStatus(String code, String title) {
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
            if (aliasEnums1 == null) {
                aliasEnums1 = new Hashtable();
            }
        }
        aliasEnums.put(code, this);
        aliasEnums.put(title, this);
        aliasEnums1.put(code, this);
        aliasEnums1.put(title, this);
//        aliasEnums.get("1").code;
    }

    public static AppStatus valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }

    public Hashtable<String, AppStatus> getAliasEnums1() {
        return aliasEnums1;
    }

    public void setAliasEnums1(Hashtable<String, AppStatus> aliasEnums1) {
        this.aliasEnums1 = aliasEnums1;
    }
}
