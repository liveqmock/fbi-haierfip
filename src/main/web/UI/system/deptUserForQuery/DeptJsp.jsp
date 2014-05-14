<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%
 String query="";
 if(request.getParameter("query")!=null){
   query=request.getParameter("query");
 }
%>
<html >
<head>

	<title>
		部门
	</title>
		<LINK href="<%=contextPath%>/css/catv.css" type="text/css" rel="stylesheet">
		<script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
		<script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
		<script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
          <script language="javascript" src="<%=contextPath%>/js/tree.js"></script>
          <script language="javascript" src="<%=contextPath%>/js/menu.js"></script>

		<script language="javascript" src="DeptJsp.js"></script>

</head>
<BODY onload="changeheigth();onUserDocumentLoad();" onclick="body_Click()" onresize="changeheigth()"  oncontextmenu="event.returnValue=false;">
		<input type="hidden" id="paramValue" NAME="paramValue">

		<table style="WIDTH:100%;HEIGHT:64px;PADDING-LEFT: 0px" cellspacing="0" ID="Table1">
			<tr >
				<td colspan="2"  class="borderGrooveB">
					<font face="SimSun" style="FONT-WEIGHT: bold; FONT-SIZE: 18pt"><img class="shadowAlpha" align="absMiddle" src="<%=contextPath%>/images/domain.gif" width="48" height="48">部门人员管理</font>
				</td>
			</tr>
		</table>
               <table style="WIDTH:100%;HEIGHT:100%;PADDING-LEFT: 0px" cellspacing="0" ID="Table1">

			<tr>
				<td  class="borderGrooveBRL" bgcolor="#ffffff" style="WIDTH:180px;" >
					<ul   id="rootUl" style="OVERFLOW:auto;WIDTH:100%;HEIGHT:515px; ;left:-30px;POSITION: absolute;top:70px" onmousemove = "tv_OnMouseMove()"	onmousedown = "tv_OnMouseDown()" onmouseup = "tv_OnMouseUp()"></ul>
				</td>

				<td class="borderGrooveBR"  id="innerDocTD">
					<iframe bgcolor="#d4d0c8" id='frmContainer' src='' style="WIDTH: 100%; HEIGHT: 100%" frameborder='0' scrolling='auto'>
					</iframe>
				</td>
			</tr>
		</table>
	</BODY>
</html>
