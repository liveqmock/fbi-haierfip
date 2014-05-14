//Source file: e:\\java\\zt\\platform\\utils\\ErrorCode.java

package pub.platform.utils;


/**
 * 系统错误代号定义类
 *
 * 0  - 成功
 * <0 - 失败
 * 添加错误代号，必须在getMessage()方法中添加相应的注释信息。
 *
 * 例如：
 * switch ( number ) {
 *    case -1:
 *           return 无有效数据库资源；
 *    case -2:
 *           return 变量不能为空；
 *   default:
 *           return “变量”＋number+"未定义！"
 * }
 *
 * @author 请替换
 * @version 1.0
 */
public final class ErrorCode {
    public static final int SUCCESS_NO = 0;
    public static final int ERROR_NOT_EVENT = -100;
    public static final int EXCEPTION_THROWN = -101;
    public static final int FORM_LOAD_FAIL = -102;
    public static final int FORM_INSTANCE_NOT_EXIST = -103;
    public static final int FORM_INSTANCE_VALUE_ERROR = -104;
    public static final int RECORD_SET_IS_NULL = -105;
    public static final int LIST_SQL_DEFINED_ERROR = -106;
    public static final int REF_GET_FORMBEAN_ERROR = -107;
    public static final int REF_GET_ELEMENT_ERROR = -108;
    public static final int REF_DEFINED_ERROR = -109;
    public static final int BUTTON_EVENT_ERROR = -110;
    public static final int NO_RECORD_DELETED  = -111;
    public static final int NO_RECORD_EDITED   = -112;

    /**
     * @param number
     * @return String
     * @roseuid 3F8208E800A4
     */
    public String getMessage(int number) {
        switch ( number ) {
            case ERROR_NOT_EVENT :
                return "触发事件错误";
            case EXCEPTION_THROWN :
                return "ActionController主控线程捕获到例外";
            case FORM_LOAD_FAIL:
                return "FORM加载失败！";
            case FORM_INSTANCE_NOT_EXIST:
                return "该实例不存在！";
            case FORM_INSTANCE_VALUE_ERROR:
                return "FORM输入值非法！";
            case RECORD_SET_IS_NULL:
                return "无记录返回！";
            case LIST_SQL_DEFINED_ERROR:
                return "LIST FORM的查询SQL定义出错！";
            case BUTTON_EVENT_ERROR:
                return "BUTTON的值为空！";
            case NO_RECORD_DELETED:
                return "";
            default :
                return "错误代号不存在！";
        }
    }
}
