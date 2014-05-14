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
    <script language="javascript" src="resJsp.js"></script>
  </head>
  <%
        DBGrid dbGrid = new DBGrid();
        dbGrid.setGridID("resTable");
        dbGrid.setGridType("edit");
        dbGrid
            .setfieldSQL("select roleR.RoleID ,r.ResID,m.MenuLabel,r.ResType from PTRoleRes roleR,PTResource r ,PTMenu m where  (roleR.ResID=r.ResID)and(r.resname=trim(m.MenuID)) ");
        dbGrid.setfieldcn("角色ID,资源ID,资源名称,资源类型");
        dbGrid.setenumType("-1,-1,0,restype");
        dbGrid.setvisible("false,true,true,true");
        dbGrid.setfieldName("RoleID,ResID,ResName,ResType");
        dbGrid.setfieldWidth("5,0,20,35,40");
        dbGrid.setfieldType("text,text,text,dropdown");
        dbGrid.setfieldCheck(" ; ; ; ");
        dbGrid.setpagesize(30);
        dbGrid.setCheck(true);
        dbGrid.setWhereStr("and(1>1)");
        //////数据集按钮
        dbGrid.setdataPilotID("datapilot");
        dbGrid.setbuttons("moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast,appendRecord,deleteRecord");
  %>
  <body bgcolor="#ffffff" onload="body_load()" onresize="body_load()" class="Bodydefault">
    <FIELDSET>
      <LEGEND>
        角色列表
      </LEGEND>
      <table width="100%" rules="border" class="title1">
        <tr>
          <td>
            <%=dbGrid.getDBGrid()%>
          </td>
        </tr>
      </table>
    </FIELDSET>
    <FIELDSET>
      <LEGEND>
        操作
      </LEGEND>
      <table width="100%" rules="border" class="title1">
        <tr>
          <td>
            <span id="title"></span>
          </td>
          <td align="right">
            <%=dbGrid.getDataPilot()%>
          </td>
        </tr>
      </table>
    </FIELDSET>
  </body>
</html>
