<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="pub.platform.html.*" %>
<html>
<head>
    <LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>

    <title>

    </title>
    <script language="javascript">
        <!--
        var dbOperateType = "";
        function SaveClick() {
            if (dbOperateType == "insert") {
                var retxml = createExecuteform(editdept, "insert", "sm0024");
                if (analyzeReturnXML(retxml) + "" == "true") {
                    window.returnValue = "1";
                    window.close();
                }
            }
            if (dbOperateType == "update") {
                var retxml = createExecuteform(editdept, "update", "sm0025");

                if (analyzeReturnXML(retxml) + "" == "true") {
                    window.returnValue = "1";
                    window.close();
                }
            }
        }
        function deptinit() {
            var arg = window.dialogArguments;
            if (arg) {
                var el = document.all["parentmenuid"];
                for (i = 0; i < el.options.length; i++) {
                    if (el.item(i).value == arg.parentid)
                        el.selectedIndex = i;
                }

                dbOperateType = arg.type;

                if (dbOperateType == "insert") {
                    ;
                } else {
                    document.all["menuid"].value = arg.id;
                    document.all["menulabel"].value = arg.caption;
                    document.all["levelindex"].value = arg.index;
                    document.all["targetmachine"].value = arg.targetmachine;
                    if (arg.title != null && arg.title != "null")
                        document.all["menudesc"].value = arg.title;

                }
            }
        }
        //-->
    </script>
</head>
<body bgcolor="#ffffff" onload="deptinit()" class="Bodydefault">
<form id="editdept">
    <fieldset>
        <legend>编辑菜单</legend>
        <table align="center" valign="center" cellspacing="0" border="0" width="100%">

            <tr>
                <td class="lbl_right_padding" nowrap="nowrap">上级菜单</td>
                <td class="data_input" style="width:90%">
                    <%
                        ZtSelect zscn = new ZtSelect("parentmenuid", "parentmenuid");
                        zscn.addAttr("style", "width: 90%");
                        zscn.addAttr("fieldName", "parentmenuid");
                        zscn.addAttr("fieldType", "text");
                        zscn.addOption("菜单管理", "0");
                        zscn.setSqlString("select menuid,menulabel from ptmenu where (isleaf=0) order by menuid,Levelidx  ");
                        zscn.setDisplayAll(false);
                        out.print(zscn);
                    %>
                </td>
            </tr>

            <tr>
                <td class="lbl_right_padding">菜单名称</td>
                <td class="data_input"><input class="text" id="menulabel" style="width:90%" fieldName="menulabel"
                                              isNull="false" textLength="50" fieldType="text" type="text"></td>
            </tr>
            <tr>
                <td class="lbl_right_padding">显示顺序</td>
                <td class="data_input"><input class="text" id="levelindex" style="width:90%" fieldName="levelindex"
                                              isNull="false" textLength="50" fieldType="text" type="text"
                                              onkeypress="onKeyPressInputInteger()"></td>
            </tr>
            <tr>
                <td class="lbl_right_padding">菜单描述</td>
                <td class="data_input"><textarea style="width:90%" id="menudesc" fieldName="menudesc" fieldType="text"
                                                 textLength="200" type="text" rows="4"></textarea></td>
            </tr>
            <tr>
                <td class="lbl_right_padding">菜单分类</td>
                <td class="data_input"><input class="text" id="targetmachine" style="width:90%"
                                              fieldName="targetmachine" isNull="true" textLength="50" fieldType="text"
                                              type="text"></td>
            </tr>
            <input id="menuid" fieldName="menuid" fieldType="text" type="hidden">
        </table>
    </fieldset>
    <fieldset>
        <legend>操作</legend>
        <table cellspacing="0" border="0" align="center">
            <tr>
                <td>
                    <input id="savebut" class="buttonGrooveDisable" onmouseover="button_onmouseover()"
                           onmouseout="button_onmouseout()" type="button" value="确定" onclick="SaveClick();">
                    <input id="closebut" class="buttonGrooveDisable" onmouseover="button_onmouseover()"
                           onmouseout="button_onmouseout()" type="button" value="取消" onclick="window.close()"></td>
            </tr>
        </table>
    </fieldset>
</form>
</body>
</html>
