<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page import="pub.platform.db.DBXML"%>
<%@ page import="pub.platform.utils.Basic"%>
<%
	request.setCharacterEncoding("GBK");
    DBXML  testxml =  new DBXML();
    String xmnlStr = request.getParameter("tabStr");
    String rexml = testxml.getDataTableXML(Basic.decode(xmnlStr));

%>

<%=rexml%>
