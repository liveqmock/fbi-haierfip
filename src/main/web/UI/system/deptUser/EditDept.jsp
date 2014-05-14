<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="pub.platform.html.*,pub.platform.advance.utils.*" %>
<html>
  <head>
    <LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
    <script language="javascript" src="<%=contextPath%>/UI/support/DataWindow.js"></script>
    <title></title>
    
    <%
  String deptfillstr10 = "";
  String deptfillstr20 = "";
  String deptfillstr100 = "";
  String deptfillstr150 = "";

  deptfillstr10 = PropertyManager.getProperty("deptfillstr10");

  deptfillstr10 = new String(deptfillstr10.getBytes(), "GBK");

  deptfillstr20 = PropertyManager.getProperty("deptfillstr20");

  deptfillstr20 = new String(deptfillstr20.getBytes(), "GBK");

  deptfillstr100 = PropertyManager.getProperty("deptfillstr100");

  deptfillstr100 = new String(deptfillstr100.getBytes(), "GBK");

  deptfillstr150 = PropertyManager.getProperty("deptfillstr150");

  deptfillstr150 = new String(deptfillstr150.getBytes(), "GBK");
%>
    <script language="javascript">
	<!--
	   	
  var dbOperateType = "";
  var parentou = "";
  var topparent = "";

  function SaveClick() {
    var dw_column = new DataWindow(document.getElementById("editdept"), "form");
    if (dw_column.validate() != null)
      return;
    
    if (dbOperateType == "insert") {

      var retxml = createExecuteform(editdept, "insert", "sm0031");
      if (analyzeReturnXML(retxml) + "" == "true") {
        window.returnValue = "1";
        window.close();
      }

    }
    if (dbOperateType == "update") {
      var retxml = createExecuteform(editdept, "update", "sm0032");

      if (analyzeReturnXML(retxml) + "" == "true") {
        window.returnValue = "1";
        window.close();
      }
    }
  }

  function deptinit() {
    var arg = window.dialogArguments;

    if (arg) {
      dbOperateType = arg.type;

      topparent = arg.topparent;
      parentou = arg.parent;

      if (dbOperateType == "insert") {
        document.all["parentdeptid"].value = arg.parentid;
      } else {
        document.all["deptid"].value = arg.parentid;
        var retxml = createselect(editdept, "sm0035");

        fillform(editdept, analyzeReturnXML(retxml));

      }
    }
  }
  //-->
</script>
  </head>
  <body bgcolor="#ffffff" onload="deptinit()">
    
	<form id="editdept">
      <fieldset>
      <legend>机构信息</legend>
        <table align="center" valign="center" width="100%">
          <tr>
            <td width="20%" class="lbl_right_padding">机构名称    </td>
            <td width="30%" colspan="3" class="data_input">
              <input class="text" id="deptname" style="width: 96%" fieldName="deptname" isNull="false" textLength="100" >
            </td>
            
          </tr>
          <tr>
            <td width="20%" class="lbl_right_padding">机构代码 </td>
            <td width="30%" class="data_input">
              <input class="text" id="deptid" style="width: 90%" fieldName="deptid" textLength="100" isNull="false" >
            </td>
            <td width="20%" class="lbl_right_padding">机构级别</td>
            <td width="30%" class="data_input">
              <input class="text" id="FILLSTR10" style="width: 90%" fieldName="FILLSTR10" intLength="2" >
            </td>
          </tr>
          <tr>
            <td class="lbl_right_padding">机构描述 </td>
            <td class="data_input" colspan="3">
              <textarea id="deptdesc" fieldName="deptdesc" fieldType="text" style="width:96%" textlength="200"  rows="4"></textarea>
            </td>
          </tr>
          </tr>
          
          <input id="parentdeptid" fieldName="parentdeptid" fieldType="text" type="hidden">
        </table>
      </fieldset>
      <fieldset>
      <legend>操作</legend>
      <table align="center">
	   <tr>
        <td >
		  <input id="savebut" class="buttonGrooveDisable"  onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="确定" onclick="SaveClick();">
		  <input id="closebut" class="buttonGrooveDisable" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="取消" onclick="window.close()">
        </td>
        </tr>
	 </table>
    </fieldset>
	</form>


</body>
</html>
