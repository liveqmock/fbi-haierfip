//Source file: e:\\java\\zt\\platform\\form\\util\\event\\EventType.java

package pub.platform.form.util.event;


/**
 * 事件类型
 *
 * @author qingdao tec
 * @version 1.0
 */
public class EventType {

    /**
     * FORM加载事件
     */
    public static final int LOAD_EVENT_TYPE = 0;

    /**
     * FORM增加页面事件
     */
    public static final int INSERT_VIEW_EVENT_TYPE = 1;

    /**
     * FORM增加事件
     */
    public static final int INSERT_EVENT_TYPE = 2;

    /**
     * FORM修改页面事件
     */
    public static final int EDIT_VIEW_EVENT_TYPE = 3;

    /**
     * FORM修改事件
     */
    public static final int EDIT_EVENT_TYPE = 4;

    /**
     * FORM删除页面事件
     */
    public static final int DELETE_VIEW_EVENT_TYPE = 5;

    /**
     * FORM删除事件
     */
    public static final int DELETE_EVENT_TYPE = 6;

    /**
     * FORM查询页面事件
     */
    public static final int FIND_VIEW_EVENT_TYPE = 7;

    /**
     * FORM查询事件
     */
    public static final int FIND_EVENT_TYPE = 8;

    /**
     * FORM按钮事件
     */
    public static final int BUTTON_EVENT_TYPE = 9;

    /**
     * 卸载事件
     */
    public static final int UNLOAD_EVENT_TYPE = 10;

    /**
     * 参考事件
     */
    public static final int REFERENCE_FIELD_EVENT_TYPE = 11;
    /**
     *插入小型查询
     */
    public static final int INSERT_SMALL_QUERY_EVENT_TYPE = 12;
    /**
     * 修改小型查询
     */
    public static final int EDIT_SMALL_QUERY_EVENT_TYPE = 13;
    /**
     * 判断事件类型是否合法
     * @param eventid
     * @return
     */
    public static boolean validate(String eventid) {
        return true;
    }
}
