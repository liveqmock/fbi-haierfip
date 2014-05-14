<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK"%>
<%@ page import="pub.platform.db.*"%>
<%@ page import="pub.platform.html.*"%>
<html>
  <head>
    <title></title>
    <LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbgrid.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dropdownData.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
    <script language="javascript" src="UserJsp.js"></script>
  </head>
  <%
        DBGrid dbGrid = new DBGrid();
        dbGrid.setGridID("userTable");
        dbGrid.setGridType("edit");
        dbGrid
            .setfieldSQL("select operid as keycode,DeptID,operid,opername,issuper,sex,opertype from ptoper where  (1=1) ");
        dbGrid.setfieldcn("用户ID,dd,用户ID,姓名,是否领导,性别,用户类型");
        dbGrid.setenumType("-1,-1,0,0,BOOLTYPE,SEX,opertype");
        dbGrid.setvisible("false,false,true,true,true,true,true");
        dbGrid.setfieldName("keycode,deptid,operid,opername,issuper,sex,opertype");
        dbGrid.setfieldWidth("5,0,0,15,20,20,20,20");
        dbGrid.setfieldType("text,text,text,text,dropdown,dropdown,dropdown");
        dbGrid.setfieldCheck(" ; ; isNull=false,textLength=4;textLength=20; ; ; ");
        dbGrid.setWhereStr("and(1>1)");

        dbGrid.setpagesize(30);
        dbGrid.setCheck(true);

        //////数据集按钮
        dbGrid.setdataPilotID("datapilot");
        ///dbGrid.setbuttons("moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast,appendRecord,editRecord,deleteRecord,打印=print");
        dbGrid.setbuttons("moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast,appendRecord,editRecord,deleteRecord");
  %>
  <body bgcolor="#ffffff" onload="body_load()" onresize="body_load()" class="Bodydefault">
    <FIELDSET>
      <LEGEND>
        用户列表
      </LEGEND>
      <table  width="100%" rules="border" class="title1">
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
