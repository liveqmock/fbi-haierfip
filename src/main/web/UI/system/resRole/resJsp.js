var m_objParam;
//////初始化信息
function body_load() {
    ///表示主从数据集高度的定制 格式divfd_ID
    if (parent.window.paramValue)
        m_objParam = parent.window.paramValue.value;
    else
        m_objParam = "";

    var tab = document.all("resTable");

    var whArr = m_objParam.split("&");
    var whereStr = "and(" + whArr[0] + "='" + whArr[2] + "')  order by  r.ResID";

    tab.whereStr = whereStr;


    resTable.actionname = "sm0075";
    divfd_resTable.style.height =  document.body.offsetHeight - 130;
    resTable.fdwidth = "100%";

    resTable.delmethodname = "delenum";
    resTable.posedelete = true;


    Table_Refresh("resTable", false);


    initDBGrid("resTable");

}


function resTable_appendRecord_click(tab) {
    var whArr = m_objParam.split("&");
    var sfeature = "dialogwidth:350px; Dialogheight:450px;center:yes;help:no;resizable:yes;scroll:no;status:no";

    var spath = "selectRes.jsp";
    var arg = new Object();
    arg.RoleID = whArr[2];

    var goupstr = window.showModalDialog(spath, arg, sfeature);

    if (goupstr == undefined || goupstr == "")
        return;


    var roleArr = goupstr.split(";");
    var whArr = m_objParam.split("&");

    if (roleArr.length > 0) {
        var xmlDoc = createDoc();
        var rootNode = createRootNode(xmlDoc);
        var insertNode = createActionNode(xmlDoc, "sm0075");


        for (var i = 0; i < roleArr.length; i++) {
            var RecorderNode = createRecorderNode(xmlDoc, "insert");


            var fieldNode = createFieldNode(xmlDoc, "ResID", "text", roleArr[i]);
            RecorderNode.appendChild(fieldNode);

            var fieldNode = createFieldNode(xmlDoc, "RoleID", "text", whArr[2]);
            RecorderNode.appendChild(fieldNode);
            insertNode.appendChild(RecorderNode);
        }

        rootNode.appendChild(insertNode);


        retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlExcuteJsp.jsp", "POST", "sys_request_xml=" + xmlDoc.xml);


        ////返回信息检查
        if (analyzeReturnXML(retStr) == "true") {
            tab.AbsolutePage = "1";
            tab.RecordCount = "0";

            Table_Refresh(tab.id);
        }
    }


}
