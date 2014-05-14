package fip.common.constant;

import java.util.Hashtable;

/**
 * 银联xml代收付交易返回码
 * 0000 代表阶段交易结束
 */
public enum PayoutBatRtnCode implements EnumApp {
    TXN_OVER("0000", "处理完成"),
    TXN_HALFWAY("1000", "交易处理中"),

    MSG_SAVE_SUCCESS("0010", "交易已接收，系统处理中"),

    MSG_TIME_OUT("1100", "通信超时"),

    MSG_VERIFY_ILLEGAL("2001", "报文验证失败"),
    MSG_TRANSFORM_EXCEPTION("2010", "报文转换错误"),
    MSG_PARSE_FAILED("2020", "报文解析错误"),
    MSG_CONTENT_FAILED("2030", "报文内容有误"),
    MSG_REQSN_EXIST("2200", "交易流水号已存在"),
    MSG_SBS_ACT_NOT_ALLOWED("2210", "报文中含有系统不允许进行该交易的SBS账号"),
    MSG_SAVE_ERROR("2300", "数据保存异常"),
    MSG_QRYSN_NOT_EXIST("2310", "没有查询到该笔交易"),

    UNKNOWN_EXCEPTION("2900", "其他未知异常");

    private String code = null;
    private String title = null;
    private static Hashtable<String, PayoutBatRtnCode> aliasEnums;

    PayoutBatRtnCode(String code, String title) {
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

    public static PayoutBatRtnCode valueOfAlias(String alias) {
        return aliasEnums.get(alias);
    }

    public String getCode() {
        return this.code;
    }

    public String getTitle() {
        return this.title;
    }

    public String toRtnMsg() {
        return this.code + "|" + this.title;
    }
}
