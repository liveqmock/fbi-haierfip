package hfc.common;

import java.util.Hashtable;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-14
 * Time: 上午10:58
 * To change this template use File | Settings | File Templates.
 */
public enum CmsAppStatusEnum {

    APP_INVALID("0","申请作废"),
    NEED_SEND("1", "申请提交"),
    SEND_SUCCESS("2", "上传成功"),

    /**
     * /**
     * 申请未提交-00
     * 审批中-01
     * 已放款-02
     * 已拒绝-03
     * 申请单号不存在-A0
     */
    CMS_UNSUBMIT("00", "等待签约中"),
    CMS_EXAMINING("01", "审批中"),
    CMS_EMPLACED("02", "已放款"),
    CMS_REFUSED("03", "已拒绝"),
    CMS_NO_APP("A0", "无此单号");

    private String code = null;
    private String title = null;
    private static Hashtable<String, CmsAppStatusEnum> aliasEnums;

    CmsAppStatusEnum(String code, String title) {
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

    public static CmsAppStatusEnum valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }
}
