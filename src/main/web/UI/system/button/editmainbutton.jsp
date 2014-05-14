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

		function SaveClick(){

			if (dbOperateType =="insert"){
				var retxml= createExecuteform(editdept,"insert","sm0082");

				if (analyzeReturnXML(retxml)+""=="true"){
					window.returnValue ="1";

					window.close();
				}
			}
			if (dbOperateType =="update")
			{
				var retxml= createExecuteform(editdept,"update","sm0082","editenum");

				if (analyzeReturnXML(retxml)+""=="true"){
					window.returnValue ="1";
					window.close();
				}
			}
		}
			function deptinit(){
				var arg = window.dialogArguments;

				if (arg)
				{
                       dbOperateType = arg.type;

                         if (dbOperateType =="insert"){
                              ;
                         }else{
                              document.all["keycode"].value= arg.id;
                              document.all["buttonlab"].value= arg.caption;
                              if (arg.title != null && arg.title !="null")
                              	document.all["buttondesc"].value= arg.title;

                         }
				}
			}
        	//-->
		</script>
</head>
<body bgcolor="#ffffff" onload="deptinit()">
	<form id="editdept">
 		<table align="center" valign="center" style="top:30">


			<tr><td>菜单名称</td><td><input class="text" id="buttonlab" style="width:160px"  isNull="false" textLength="50"  type="text"></td></tr>
               <tr><td>菜单描述</td><td><textarea id="buttondesc"   textLength="200"  type="text" rows="4"></textarea></td></tr>
			<input id="keycode"  type="hidden">

			<tr><td colspan="2" align="right">
				<input id="savebut" class="buttonGrooveDisable"  onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="确定" onclick="SaveClick();">
				<input id="closebut" class="buttonGrooveDisable" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="取消" onclick="window.close()"></td></tr>
		</table>
	</form>


</body>
</html>
