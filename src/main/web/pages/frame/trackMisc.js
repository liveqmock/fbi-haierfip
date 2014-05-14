function body_load() {
    initHomePageData();
}

function initHomePageData() {
    var newCnt = 0;
    var nullCnt = 0;
    var initCnt = 0;
    var exepCnt = 0;
    var notPayCnt = 0;
    var init_data = "";
    var help_msg = "";
    show_status_label(window, "正在读入数据...", true);
    // formelement, exctype, actionName, methodname
    rtnXml = createExecuteform(queryForm, "update", "hom001", "initQuery");
    if (analyzeReturnXML(rtnXml) != "false") {
        hide_status_label(window);
        var dom = createDomDocument();
        dom.loadXML(rtnXml);
        var fieldList = dom.getElementsByTagName("record")[0];
        for (var i = 0; i < fieldList.childNodes.length; i++) {
            if (fieldList.childNodes[i].nodeType == 1) {
                oneRecord = fieldList.childNodes[i];
                attrName = oneRecord.getAttribute("name");
                if (attrName == "importData") {
                    init_data = decode(oneRecord.getAttribute("value"));
                    var arr = init_data.split("_");
                    initCnt = arr[0];
                    exepCnt = arr[1];
                    notPayCnt = arr[2];
                    newCnt = arr[3];
                    nullCnt = arr[4];
//                    if (initCnt > "0") {
//                            help_msg = "<a href=" + "../../UI/gwk/consumeinfo/consumeSendList.jsp" + ">" + "点击进入消费信息发送界面</a>";
//                    }
//                    if (exepCnt > "0") {
//                        if (help_msg == "")
//                            help_msg += "<a href=" + "../../UI/gwk/consumeinfo/consumeSendMgrList.jsp" + ">" + "点击进行消费信息异常管理</a>";
//                        else
//                             help_msg += "<br><a href=" + "../../UI/gwk/consumeinfo/consumeSendMgrList.jsp" + ">" + "点击进行消费信息异常管理</a>";
//                    }
                }
            }
        }
    }
    // 显示提示信息
    document.getElementById("card_info_msg").style.display = "block";
    document.getElementById("_consume_sendCnt").innerHTML = initCnt;
    document.getElementById("_consume_exceptionCnt").innerHTML = exepCnt;
    document.getElementById("_payback_intCnt").innerHTML = notPayCnt;
    document.getElementById("_cardbase_newCnt").innerHTML = newCnt;
    document.getElementById("_cardbase_nullCnt").innerHTML = nullCnt;
//    document.getElementById("_help_msg").innerHTML = help_msg;

}
