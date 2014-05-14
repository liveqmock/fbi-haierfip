<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="pub.platform.db.*"%>
<%@ page import="pub.platform.html.*" %>
<html>
<head>
<title>

</title>
	<LINK href="<%=contextPath%>/css/catv.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbgrid.js"></script>
	<script language="javascript" src="<%=contextPath%>/js/dropdownData.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
    <script language="javascript" src="viewUserJsp.js"></script>

</head>




<%


    DBGrid  dbGrid= new DBGrid();
    dbGrid.setGridID("userTable");
     dbGrid.setGridType("edit");
    dbGrid.setfieldSQL("select PTDept.DEPTNAME,PTOper.OPERID,PTOper.OPERNAME,PTRole.ROLEDESC from PTOper,PTDept,PTRole,PTOperRole where (PTRole.RoleID=PTOperRole.RoleID)and(PTOper.OperID=PTOperRole.OperID)and(PTOper.DeptID=PTDept.DeptID)");
    dbGrid.setfieldcn("部门名称,用户ID,用户名称,角色描述");
    dbGrid.setenumType(",0,0,0,0") ;
    dbGrid.setvisible("true,true,true,true");
    dbGrid.setfieldName("DEPTNAME,OPERID,OPERNAME,ROLEDESC");
    dbGrid.setfieldWidth("25,25,25,25");
    dbGrid.setfieldType("text,text,text,text");
    dbGrid.setfieldCheck(" ; ; ; ; ");


    dbGrid.setpagesize(100);
    dbGrid.setCheck(false);

    //////数据集按钮
    dbGrid.setdataPilotID("datapilot");
    dbGrid.setbuttons("moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast");
%>
<body bgcolor="#ffffff" onload="body_init(deptform,'savebut');body_load()">
 <table style=" position: absolute;top:10px;left:40px;width:640px"  border="0" cellspacing="0" cellpadding="0">
    <form id="deptform">
     <tr height="20">
          <td width="80">&nbsp;部门名称&nbsp;</td>
          <td width="80"> <%
               ZtSelect zscc = new ZtSelect("dept", "dept");
               zscc.addAttr("style", "width: 100px");
               zscc.addOption("", "");
               zscc.setSqlString("select DeptID,DeptName from PTDept where DeptName <> 'null' order by DeptID");

               zscc.setDisplayAll(false);
               out.print(zscc);
          %></td>
		   <td width="80">&nbsp;用户ID&nbsp;</td> <td width="60"><input id="id" type="text" size="14"  maxlength="4"></td>
          <td width="80">&nbsp;用户名称&nbsp;</td>
          <td width="80"> <input id="name" type="text" size="16"> </td>
          <td width="80">&nbsp;角色描述&nbsp;</td>
          <td width="80"> <%
               zscc = new ZtSelect("role", "role");
               zscc.addAttr("style", "width: 100px");
               zscc.addOption("", "");
               zscc.setSqlString("select RoleID,RoleDesc from PTRole");

               zscc.setDisplayAll(false);
               out.print(zscc);
          %></td>
     </tr>
      <input id="savebut" class="buttonGrooveDisable" style=" LEFT: 650px; POSITION: absolute; TOP:0px;" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="查询" onclick="queryClick();">
     <input name="" class="buttonGrooveDisable"  type="reset" Style="position :absolute;LEFT:700px;top:0px" value="重填" onclick="reset_click();"  onmouseover="button_onmouseover()" onmouseout="button_onmouseout()">

     </form>
 </table>

<table style="position: absolute;top:40" width="100%" rules="border" class="title1">
  <tr><td><span id="title"></span></td>
          <td align="right">
	         <%=dbGrid.getDataPilot()%>
          </td></tr>
</table>


<table style="position: absolute;top:70" width="100%" rules="border" class="title1" >
    <tr><td>
	<%=dbGrid.getDBGrid()%>
    </td></tr>

</table>
</body>
</html>
