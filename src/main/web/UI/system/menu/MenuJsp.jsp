<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK"%>
<%@ page import="pub.platform.db.*"%>
<html>
  <head>
    <title></title>
    <LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbgrid.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dropdownData.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
    <script language="javascript" src="MenuJsp.js"></script>
  </head>
  <%
        DBGrid dbGrid = new DBGrid();
        dbGrid.setGridID("MenuTable");
        dbGrid.setGridType("edit");
        dbGrid
            .setfieldSQL("select MenuID as keycode,MenuLabel,MenuAction,Levelidx,MenuDesc,targetmachine from PTMenu where  (isleaf='1') ");
        dbGrid.setfieldcn("菜单ID,菜单名称,菜单行为,菜单顺序,菜单描述,所属系统");
        dbGrid.setenumType("-1,0,0,0,0,0");
        dbGrid.setvisible("false,true,true,true,true,true");
        dbGrid
            .setfieldName("keycode,MenuLabel,MenuAction,levelindex,MenuDesc,targetmachine");
        dbGrid.setfieldWidth("5,0,12,35,7,14,10");
        dbGrid.setfieldType("text,text,text,text,text,text");
        dbGrid.setfieldCheck(";textLength=40; textLength=400;isNull=false,intLength=3;textLength=50;textLength=30");
        dbGrid.setpagesize(30);
        dbGrid.setCheck(true);
        dbGrid.setWhereStr(" and (1>1) order by Levelidx");
        //////数据集按钮
        dbGrid.setdataPilotID("datapilot");
        dbGrid.setbuttons("default");
  %>
  <body bgcolor="#ffffff" onload="body_load()" onresize="body_load()" class="Bodydefault">
    <fieldset>
      <legend>
        菜单列表
      </legend>
      <table width="100%" rules="border" class="title1">
        <tr>
          <td>
            <%=dbGrid.getDBGrid()%>
          </td>
        </tr>
      </table>
    </fieldset>
    <fieldset>
      <legend>
        操作
      </legend>
      <table width="100%" class="title1">
        <tr>
          <td align="right">
            <%=dbGrid.getDataPilot()%>
          </td>
        </tr>
      </table>
    </fieldset>
  </body>
</html>
