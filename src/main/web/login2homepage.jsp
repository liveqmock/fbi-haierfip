<%@ page language="java" import="java.util.*" pageEncoding="ISO-8859-1" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";

//    String uaapuser = request.getHeader("uid");
    //String uaapuser = "9999";

    String uaapuser = request.getParameter("username");
    String password = request.getParameter("password");

    if (!"tiankun".equals(password)) {
        response.sendRedirect(basePath+"close.jsp");
    }

//    HttpSession session = request.getSession();
    session.setAttribute("uaapuser",uaapuser);
//    response.sendRedirect(basePath+"/pages/frame/homePage.jsp?username=9999&password=111");
    response.sendRedirect(basePath+"pages/frame/homePage.jsp");
%>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <%--<base href="<%=basePath%>">--%>

    <title>My JSP 'index.jsp' starting page</title>
    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <!--
     <link rel="stylesheet" type="text/css" href="styles.css">
     -->
</head>

<body>
This is my JSP page. <br>


<%--<jsp:forward page="/pages/frame/homePage.jsp">--%>
    <%--<jsp:param name="username" value="<%=uaapuser%>"/>--%>
    <%--<jsp:param name="password" value="<%=password%>"/>--%>

<%--</jsp:forward>--%>

</body>
</html>
