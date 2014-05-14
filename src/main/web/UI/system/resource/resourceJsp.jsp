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
    <script language="javascript" src="resourceJsp.js"></script>
  </head>
  <%
        DBGrid dbGrid = new DBGrid();
        dbGrid.setGridID("resourceTable");
        dbGrid.setGridType("edit");
        dbGrid
            .setfieldSQL("select ResID as keycode,ResID,ParentResID,ResName,ResType,ResDesc from PTResource where  (1=1) ");
        dbGrid.setfieldcn("资源ID,资源主键,资源父主键,资源名称,资源类型,资源描述");
        dbGrid.setenumType("-1,0,0,0,restype,0");
        dbGrid.setvisible("false,true,true,true,true,true");
        dbGrid.setfieldName("keycode,ResID,ParentResID,ResName,ResType,ResDesc");
        dbGrid.setfieldWidth("5,0,15,20,20,20,20");
        dbGrid.setfieldType("text,text,text,text,dropdown,text");
        dbGrid
            .setfieldCheck(";isNull=false,textLength=6;textLength=6;isNull=false,textLength=100;isNull=false;textLength=50");
        dbGrid.setpagesize(80);
        dbGrid.setCheck(true);
        dbGrid.setWhereStr(" order by PTResource.ResID");
        //////数据集按钮
        dbGrid.setdataPilotID("datapilot");
        dbGrid.setbuttons("default");
  %>
  <body bgcolor="#ffffff" onload="body_load()" class="Bodydefault">
    <FIELDSET>
      <LEGEND>
        资源列表
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
