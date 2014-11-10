<%@ page contentType="text/html; charset=GBK" %>
<%@ include file="/pages/security/loginassistor.jsp" %>
<%
    String contextPath = request.getContextPath();
    /*2011-4-7 Cookie设置添加到loginassistor.jsp中*/
%>
<%--<script language="javascript" src="<%=request.getContextPath() %>/js/basic.js"></script>--%>
<script language="javascript" src="./js/basic.js"></script>
<html>
<head>
    <title>FIP资金交换平台</title>
    <link href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    <meta content="mshtml 6.00.2800.1106" name="generator">
</head>
<script>
    function unloadevent() {
    }
    function promtMsg() {
    }
</script>
<frameset rows="80px,*" border="0" framespacing="0" frameborder="0" noresize="noresize" scrolling="no"
          onload="promtMsg()" onunload="unloadevent()">
    <frame name="roofFrame" src="roofPage.jsp" frameborder="0" noresize="noresize" scrolling="no">
    <frameset id="menuFrame" framespacing="0" cols="200,12,*" frameborder="0" framespacing="0">
        <frame id="menuList" src="menuPage.jsp" frameborder="0" noresize="noresize" scrolling="no">
        <frame id="dockFrame" src="dockPage.jsp" frameborder="0" noresize="noresize" scrolling="no">
        <frame id="workFrame" src="<%=contextPath%>/pages/frame/trackMisc.xhtml" frameborder="0" noresize="noresize"
               scrolling="auto">
    </frameset>
</frameset>
</html>
