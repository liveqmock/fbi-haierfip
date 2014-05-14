package com.ccb.util;

public class CcbLoanConst {

    //海尔HFC 序号类型
    /**
     * APP内部序号
     */
    public static final String XFAPP_PKID = "XFAPP_PKID";

    /**
     * 抵押管理 申请抵押编号
     */
    public static final String MORTTYPE = "MORTREG";
    /**
     * 合作项目标志
     */
    public static final String COOPTYPE = "COOP";
    /**
     * 流水号
     */
    public static final String TASKSEQ = "TASK";

    /**
     * 业务类型，操作日志表专用
     */
    public static final String LOG_MORTREG = "MORTREG";

    // 报表模板名称

    /**
     * 基本查询
     */
    public static final String BASIC_RPT_MODEL = "basicRptModel";
    /**
     * 快递输出excel模板
     */
    public static final String RPT_MODEL_EXPRESS = "expressRptModel";
    /**
     * 综合查询统计一
     */
    public static final String MISC_RPT_MODEL_01 = "miscRptModel01";
    /**
     * 综合查询统计二
     */
    public static final String MISC_RPT_MODEL_02 = "miscRptModel02";
    /**
     * 综合查询统计三
     */
    public static final String MISC_RPT_MODEL_03 = "miscRptModel03";
    /**
     * 权证入库统计表
     */
    public static final String MISC_RPT_PAPER_STAT = "paperStatModel";

    /**
     * 买单工资统计表模板1
     */
    public static final String RPT_PAY_BILL_01 = "payBill01";
    /**
     * 买单工资统计表模板2---
     */
    public static final String RPT_PAY_BILL_02 = "payBill02";
    /**
     * 买单工资统计表模板3---
     */
    public static final String RPT_PAY_BILL_03 = "payBill03";
    /**
     * 买单工资统计表模板4---
     */
    public static final String RPT_PAY_BILL_04 = "payBill04";
    /**
     * 买单工资统计表模板5---
     */
    public static final String RPT_PAY_BILL_05 = "payBill05";
    /**
     * 买单工资统计表模板6---
     */
    public static final String RPT_PAY_BILL_06 = "payBill06";
    /**
     * 买单工资统计表模板7---
     */
    public static final String RPT_PAY_BILL_07 = "payBill07";
    /**
     * 买单工资统计表模板8---
     */
    public static final String RPT_PAY_BILL_08 = "payBill08";
    /**
     * 买单工资统计表模板9---
     */
    public static final String RPT_PAY_BILL_09 = "payBill09";
    /**
     * 买单工资统计表模板10---
     */
    public static final String RPT_PAY_BILL_10 = "payBill10";
    /**
     * 买单工资统计表模板11---
     */
    public static final String RPT_PAY_BILL_11 = "payBill11";
    /**
     * 买单工资统计表模板12---
     */
    public static final String RPT_PAY_BILL_12 = "payBill12";
    /**
     * ODSB数据比对结果
     */
    public static final String RPT_ODSB_CHECK = "ODSBCheckRpt";
    /**
     * ODSB合作项目数据报表检查模板
     */
    public static final String RPT_ODSB_CHECK_PROJ = "ODSBCheckProjRpt";

    /**
     * 1、已登记资料 10 （原10）<br>
     * 2、已发出快递 20 <br>
     * 3、已快递回证 21 <br>
     * 4、权证已入库 30 <br>
     * 5、已换证取证 40 <br>
     * 6、已换证回证 41 <br>
     * 7、已结清取证 50 <br>
     * 8、未抵押撤卷 51 <br>
     * 9、未抵押结清 52<br>
     */
    /**
     * 已登记 10
     */
    public static final String MORT_FLOW_REGISTED = "10";
    /**
     * 已发出快递 20
     */
    public static final String MORT_FLOW_SENT = "20";
    /**
     * 已快递回证 21
     */
    public static final String MORT_FLOW_RETURN = "21";
    /**
     * 已取得回执 22
     */
    public static final String MORT_FLOW_RETURNNOTE = "22";
    /**
     * 权证已入库 30
     */
    public static final String MORT_FLOW_SAVED = "30";
    /**
     * 已换证取证 40
     */
    public static final String MORT_FLOW_CHANGED = "40";
    /**
     * 已换证回证 41
     */
    public static final String MORT_FLOW_CHANGED_RETURN = "41";
    /**
     * 已结清取证 50
     */
    public static final String MORT_FLOW_CLEARED = "50";
    /**
     * 未抵押撤卷 51
     */
    public static final String MORT_FLOW_NOMORT_GETBOOK = "51";
    /**
     * 未抵押结清 52
     */
    public static final String MORT_FLOW_NOMORT_CLEARED = "52";

    // 业务类型
    // 菜单名称
    /**
     * 抵押信息登记
     */
    public static final String BUSINODE_010 = "010";
    /**
     * 快递抵押信息登记
     */
    public static final String BUSINODE_020 = "020";
    /**
     * 他行开发贷未报抵押登记
     */
    public static final String BUSINODE_030 = "030";
    /**
     * 他行开发贷可报抵押登记
     */
    public static final String BUSINODE_040 = "040";
    /**
     * 未办理抵押原因登记
     */
    public static final String BUSINODE_050 = "050";
    /**
     * 取得回执登记
     */
    public static final String BUSINODE_060 = "060";
    /**
     * 快递回证登记
     */
    public static final String BUSINODE_070 = "070";
    /**
     * 抵押入库登记
     */
    public static final String BUSINODE_080 = "080";
    /**
     * 换证领用登记
     */
    public static final String BUSINODE_090 = "090";
    /**
     * 换证归还登记
     */
    public static final String BUSINODE_100 = "010";
    /**
     * 贷款结清取证登记
     */
    public static final String BUSINODE_110 = "110";
    /**
     * 抵押特殊通道
     */
    public static final String BUSINODE_120 = "120";
    /**
     * 贷款管理
     */
    public static final String BUSINODE_130 = "130";
    /**
     * 客户经理认领
     */
    public static final String BUSINODE_140 = "140";
    /**
     * 贷款特殊通道
     */
    public static final String BUSINODE_150 = "150";
    /**
     * 合作项目管理
     */
    public static final String BUSINODE_160 = "160";

    // misc静态变量
    /**
     * pulldown显示记录数
     */
    public static final int PULLDOWN_PAGE_CNT = 10;
    /**
     * 菜单公用一个页面，判断页面操作权限类型标志位
     */
    public static final String MENU_ACTION = "menuAction";
    /**
     * 菜单公用一个页面，判断页面操作权限类型
     */
    public static final String MENU_SELECT = "select";

    // 操作类型
    /**
     * 增加
     */
    public static final String OPER_ADD = "ADD";
    /**
     * 修改
     */
    public static final String OPER_EDIT = "EDIT";
    /**
     * 删除
     */
    public static final String OPER_DEL = "DELETE";
    /**
     * 批量修改 仅限快递
     */
    public static final String OPER_BATCHEDIT = "BATCH_EDIT";
    /**
     * 客户经理认领
     */
    public static final String OPER_CLM = "CLAIM";
    /**
     * 客户经理批量认领
     */
    public static final String OPER_BATCH_CLM = "BATCH_CLAIM";
    /**
     * 退回
     */
    public static final String OPER_BACK = "BACK";
    /**
     * 批量退回
     */
    public static final String OPER_BATCH_BACK = "BATCH_BACK";
}
