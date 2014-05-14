var dw_form;

/**
 * 初始化
 */
function formInit() {
    body_init(queryForm, "expExcel");
    // 初始化数据窗口，校验的时候用
    dw_form = new DataWindow(document.getElementById("queryForm"), "form");
}

/**
 *
 */
function exportExcel_click() {

    //增加系统锁检查
    if (getSysLockStatus() == "1") {
        alert(MSG_SYSLOCK);
        return;
    }
    if (dw_form.validate() != null)
        return;

    //检查 查询权限

    //输出电子表格
    document.getElementById("queryForm").target = "_blank";
    document.getElementById("queryForm").action = "queryTemplateRpt.jsp";
    document.getElementById("queryForm").submit();
}
