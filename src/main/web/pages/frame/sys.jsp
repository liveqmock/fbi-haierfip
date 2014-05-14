<%--
*******************************************************************
*    程序名称:    main.jsp
*    程序标识:
*    功能描述:    主工作区的欢迎画面。
*    连接网页:    青岛市
*    传递参数:
*    作   者:
*    开发日期:    2003-05-29
*    修 改 人:
*    修改日期:
*    版   权:   leonwoo
*
*
*******************************************************************
--%>
<%@ page contentType="text/html; charset=gb2312"%>
<%@page import="pub.platform.security.OperatorManager"%>
<%@page import="pub.platform.form.config.SystemAttributeNames"%>
<%@page import="pub.platform.db.DBGrid"%>
<%@page import="pub.platform.html.ZtSelect"%>
<%@page import="java.util.*"%>
<%
OperatorManager omgr = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);

    //TODO 需先检测是否已存在此cookie
    Cookie cookie = new Cookie("usernamecookie", "aaa1");
    cookie.setMaxAge(365 * 24 * 60 * 60); //保存365天
    cookie.setPath( request.getContextPath() + "/pages");
    response.addCookie(cookie);
    cookie = new Cookie("passwordcookie", "bbb");
    cookie.setPath("/pages");
    cookie.setMaxAge(365 * 24 * 60 * 60); //保存365天
    response.addCookie(cookie);


%>
<html>
  <head>
    <title></title>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
    <link href="../../css/ccb.css" rel="stylesheet" type="text/css">
  </head>
  <body style="margin:0px;padding:0px" class="Bodydefault">

     asdasd
  </body>
</html>
