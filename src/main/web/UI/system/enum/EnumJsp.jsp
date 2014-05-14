<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="pub.platform.db.*" %>
<html>
<head>
    <title></title>
    <LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbgrid.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>

    <script language="javascript" src="EnumJsp.js"></script>
</head>
<%
    String type = request.getParameter("type");

    String sqlstr = "select EnuType as keycode, EnuType,EnuName,ValueType,EnuDesc from ptEnuMain where  (1=1) ";

    if (type != null && !type.equals("")) {
        String[] typeArr = type.split(",", -2);
        String typeS = "";

        for (int i = 0; i < typeArr.length; i++) {
            if (i > 0)
                typeS = typeS + " , '" + typeArr[i] + "' ";
            else
                typeS = " '" + typeArr[i] + "' ";

        }
        if (!typeS.equals(""))
            sqlstr = "select EnuType as keycode, EnuType,EnuName,ValueType,EnuDesc from ptEnuMain where EnuType in ( "
                    + typeS + ") ";
    }
    DBGrid dbGrid = new DBGrid();
    dbGrid.setGridID("EnumTable");
    dbGrid.setGridType("edit");
    dbGrid.setfieldSQL(sqlstr);
    dbGrid.setfieldcn("枚举ID,枚举ID,枚举名称,值类型,描述");
    dbGrid.setenumType("-1,0,0,0,0");
    dbGrid.setvisible("false,true,true,true,true");
    dbGrid.setfieldName("keycode,EnuType,EnuName,ValueType,EnuDesc");
    dbGrid.setfieldWidth("5,0,20,20,25,30");
    dbGrid.setfieldType("text,text,text,text,text");
    dbGrid.setfieldCheck(";isNull=false,textLength=20;isNull=false,textLength=20;textLength=30;textLength=100");
    dbGrid.setpagesize(10);
    dbGrid.setCheck(true);
    dbGrid.setWhereStr("  order by 1");

    //////数据集按钮
    dbGrid.setdataPilotID("datapilot");
    dbGrid
            .setbuttons("moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast,appendRecord,editRecord,deleteRecord,cancelRecord,updateRecord");
    //.setbuttons("moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast,appendRecord,editRecord,deleteRecord,cancelRecord,updateRecord,转excel=excel");

    DBGrid dbGridDetail = new DBGrid();
    dbGridDetail.setGridID("detailTable");
    dbGridDetail.setGridType("edit");
    dbGridDetail
            .setfieldSQL("select EnuItemValue as keycode, EnuType,EnuItemValue,EnuItemLabel,ENUITEMEXPAND,dispno,EnuItemDesc from ptEnuDetail where  (1=1)");
    dbGridDetail.setfieldcn("枚举值,枚举ID,枚举值,枚举名称,枚举扩展,枚举顺序,描述");
    dbGridDetail.setenumType("-1,-1,0,0,0,0,0");
    dbGridDetail.setvisible("false,false,true,true,true,true,true");
    dbGridDetail.setfieldName("keycode,EnuType,EnuItemValue,Enuitemlabel,enuitemexpand,dispno,EnuItemDesc");
    dbGridDetail.setfieldWidth("5,0,0,25,25,15,15,30");
    dbGridDetail.setfieldType("text,text,text,text,text,int,text");
    dbGridDetail.setfieldCheck(";;isNull=false,textLength=20;isNull=false,textLength=50; ; ; textLength=100");
    dbGridDetail.setpagesize(30);
    dbGridDetail.setCheck(true);
    dbGridDetail.setWhereStr("and(1>1)");

    //////数据集按钮
    dbGridDetail.setdataPilotID("detaildatapilot");
    dbGridDetail.setbuttons("default");
%>
<body bgcolor="#ffffff" onload="body_load()" onresize="body_load()" class="Bodydefault">
<FIELDSET>
    <LEGEND>
        查询条件
    </LEGEND>
    <table style="width:100%" border="0" cellspacing="0" cellpadding="0">
        <form id="deptform">
            <tr height="20">
                <td width="80" class="lbl_right_padding">
                    枚举ID
                </td>
                <td width="80" class="data_input">
                    <input id="cationid" type="text">
                </td>
                <td width="80" class="lbl_right_padding">
                    枚举名称
                </td>
                <td width="60" class="data_input">
                    <input id="actionclass" type="text">
                </td>
                <td width="80" class="lbl_right_padding">
                    描述
                </td>
                <td width="80" class="data_input">
                    <input id="actiondes" type="text">
                </td>
                <td>
                    &nbsp;
                </td>
                <td nowrap="true">
                    <input id="savebut" class="buttonGrooveDisable" onmouseover="button_onmouseover()"
                           onmouseout="button_onmouseout()" type="button" value="查询" onclick="queryClick();">
                    <input name="" class="buttonGrooveDisable" type="reset" value="重填"
                           onmouseover="button_onmouseover()" onmouseout="button_onmouseout()">
                </td>
            </tr>
        </form>
    </table>
</FIELDSET>
<fieldset>
    <LEGEND>
        枚举主列表
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
        枚举主列表操作
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
<FIELDSET>
    <LEGEND>
        枚举明细列表
    </LEGEND>
    <table width="100%" rules="border" class="title1">
        <tr>
            <td>
                <%=dbGridDetail.getDBGrid()%>
            </td>
        </tr>
    </table>
</FIELDSET>
<FIELDSET>
    <LEGEND>
        枚举明细操作
    </LEGEND>
    <table width="100%" rules="border" class="title1">
        <tr>
            <td>
                <span id="title"></span>
            </td>
            <td align="right">
                <%=dbGridDetail.getDataPilot()%>
            </td>
        </tr>
    </table>
</FIELDSET>
</body>
</html>
