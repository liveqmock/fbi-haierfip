<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>


<html>
<head>
	<LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
	  <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
     <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
     <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
     <script language="javascript" src="<%=contextPath%>/js/tree.js"></script>

     <script language="javascript" src="<%=contextPath%>/js/getDeptuser.js"></script>
<title>

</title>


<script language="javascript">
	<!--
	   	var dbOperateType = "";

		function SaveClick(){
			 var arguments = "<%=contextPath%>/UI/sys/deptUser/getUserJsp.jsp";

              var dialogArg = "dialogwidth:350px; Dialogheight:450px;center:yes;help:no;resizable:no;scroll:no;status:no";
               var arg = new Object();

               ///isall 判断是否显示所有的部门和人员 1 ----是；0----否
               arg.isall="0";

               ///isdistsup 判断是否区分领导 1 ----是；0----否（在isall情况下成立）
          	arg.isdistsup="0";

                ///isdistsup 判断是否只显示部门   1 ----是；0----否（在isall情况下成立）
               arg.isdept="0";


              var  dd    = window.showModalDialog(arguments, arg, dialogArg);
          	alert(dd);


     }

        	//-->
		</script>
</head>
<body bgcolor="#ffffff">
	<form id="editdept">

 		<table align="center" valign="center" style="top:30">


			<tr><td colspan="2" align="right">
				<input id="savebut" class="buttonGrooveDisable"  onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="确定" onclick="SaveClick();">
				<input id="closebut" class="buttonGrooveDisable" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="取消" onclick="window.close()"></td>


				<input id="dddd" type="text" isall="0" isdistsup="0" isdept="0" depid="0100;0200;466" style="position:absolute;top:100px" onclick="dropdownDept(this)">




			</tr>
		</table>
	</form>


</body>
</html>
