<%@ page contentType="application/x-download; charset=UTF-8" %>
<%@ page import="jxl.Workbook" %>
<%@ page import="jxl.write.WritableWorkbook" %>
<%@ page import="org.apache.commons.logging.Log" %>
<%@ page import="org.apache.commons.logging.LogFactory" %>
<%@ page import="pub.platform.db.DBXML" %>
<%@ page import="pub.platform.utils.Basic" %>
<%@ page import="java.io.OutputStream" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="java.util.Date" %>
<%
    Log logger = LogFactory.getLog("expExcelJsp.jsp");

    try {
        DBXML testxml = new DBXML();
        String xmnlStr = request.getParameter("xx");

        //TODO : 重复调用？
        //if (xmnlStr==null || xmnlStr.equals("")) {
        //    return;
        //}

//  String rexml = testxml.getDropDownXML(Basic.decode(xmnlStr));

        String dateFilename = new SimpleDateFormat("_yyyyMMdd_hhmmss").format(new Date());

        request.setCharacterEncoding("GBK");
        response.reset();
        response.setContentType("application/vnd.ms-excel");
        response.addHeader("Content-Disposition", "attachment; filename=" + "export" + dateFilename + ".xls");


        OutputStream outputStream = response.getOutputStream();

        WritableWorkbook workbook = Workbook.createWorkbook(outputStream);
        //..... write the excel
        String xmlStr =  Basic.decode(xmnlStr);
        String rexml = testxml.doExcelHou(xmlStr, workbook);

        workbook.write();
        workbook.close();
        outputStream.close();

        response.flushBuffer();

    } catch (Exception e) {
        logger.error(e);
    }


%>
<%--<%=rexml%>--%>
