<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="pub.platform.db.*"%>

<html>
<head>
<title>

</title>
	<LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbgrid.js"></script>
	<script language="javascript" src="<%=contextPath%>/js/dropdownData.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
    <script language="javascript" src="MenuJsp.js"></script>

</head>


<%

    DBGrid  dbGrid= new DBGrid();
    dbGrid.setGridID("MenuTable");
    dbGrid.setGridType("edit");
    dbGrid.setfieldSQL("select Buttonindex as keycode,Buttonid,Buttonlabel,Buttondesc  from PTMenu where  (isleaf='1') ");
    dbGrid.setfieldcn("菜单ID,菜单名称,服务器地址,菜单行为,菜单顺序,菜单描述,打开方式,窗体宽度,窗体高度");
    dbGrid.setenumType("-1,0,matchinetype,0,0,0,openwindow,0,0") ;
    dbGrid.setvisible("false,true,true,true,true,true,true,true,true");
    dbGrid.setfieldName("keycode,MenuLabel,targetMachine,MenuAction,levelindex,MenuDesc,OpenWindow,WindowWidth,WindowHeight");
    dbGrid.setfieldWidth("5,0,12,12,20,7,15,10,10,10");
    dbGrid.setfieldType("text,text,dropdown,text,text,text,dropdown,number,number");
    dbGrid.setfieldCheck(";textLength=40; ; textLength=200;isNull=false,intLength=3;textLength=50;;;");
    dbGrid.setpagesize(30);
    dbGrid.setCheck(true);
    dbGrid.setWhereStr(" and (1>1) order by Levelidx");
    //////数据集按钮
    dbGrid.setdataPilotID("datapilot");
    dbGrid.setbuttons("default");

%>
<body bgcolor="#ffffff"  onload="body_load()">


<table style="position: absolute;top:0" width="100%" rules="border" class="title1">
  <tr><td><span id="title"></span></td>
          <td align="right">
	         <%=dbGrid.getDataPilot()%>
          </td></tr>
</table>


<table style="position: absolute;top:30" width="100%" rules="border" class="title1" >
    <tr><td>
	<%=dbGrid.getDBGrid()%>
    </td></tr>

</table>

</body>
</html>
