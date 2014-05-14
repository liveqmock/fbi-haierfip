<%@ page import="pub.platform.security.OnLineOpersManager" %>
<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2010-11-19
  Time: 14:55:37
  To change this template use File | Settings | File Templates.
--%>
<%--<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3c.org/TR/1999/REC-html401-19991224/loose.dtd">--%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=GBK" language="java" %>

<%
    String contextPath = request.getContextPath();
    String cookieName = "usernamecookie";
    Cookie cookies[] = request.getCookies();
    String username = "";
    if (cookies != null) {
        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                username = cookie.getValue();
            }
        }
    }
%>


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>用户登录</title>
    <meta http-equiv="X-UA-Compatible" content="IE=EmulateIE7" />

    <link rel="stylesheet" type="text/css" href="<%=contextPath%>/resources/login/images/User_Login.css"/>
    <link rel="stylesheet" type="text/css" href="<%=contextPath%>/resources/login/images/Default.css"/>
    <link rel="stylesheet" type="text/css" href="<%=contextPath%>/resources/login/images/xtree.css"/>
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/login.js"></script>
    <script type="text/javascript">
        function closeWindow() {
            window.open('', '_parent', '');
            window.close();
        }

    </script>
    <META http-equiv=Content-Type content="text/html; charset=UTF-8">
    <META content="MSHTML 6.00.6000.16674" name=GENERATOR>
</head>
<body id="userlogin_body" onLoad="FocusUsername();">
<DIV id="user_login">
    <form action="<%=contextPath%>/pages/frame/homePage_nocas.jsp" method="post" name="winform" onSubmit="return false;">

        <DL>
            <DD id="user_top">
                <UL>
                    <LI class="user_top_l"></LI>
                    <LI class="user_top_c"></LI>
                    <LI class="user_top_r"></LI>
                </UL>
            </DD>
            <DD id="user_main">
                <UL>
                    <LI class="user_main_l"></LI>
                    <LI class="user_main_c">
                        <DIV class="user_main_box">
                            <UL>
                                <LI class="user_main_text">用户名：</LI>
                                <LI class="user_main_input">
                                    <INPUT class="TxtUserNameCssClass" id="username" value="<%=username%>"
                                           onfocus="this.select()" maxLength="20" name="username"
                                           onKeyPress="return focusNext(this.form, 'password', event)">
                                </LI>
                            </UL>
                            <UL>
                                <LI class="user_main_text">密 码：</LI>
                                <LI class="user_main_input">
                                    <INPUT class="TxtPasswordCssClass" id="password" type="password"
                                           name="password"
                                           onKeyPress="return focusNext(this.form, 'DropExpiration', event)">
                                </LI>
                            </UL>
                            <UL>
                                <LI class="user_main_text">Cookie：</LI>
                                <LI class="user_main_input">
                                    <SELECT id="DropExpiration" name="DropExpiration"
                                            onKeyPress="return submitViaEnter(event)">
                                        <OPTION value="Month" selected>保存一个月</OPTION>
                                        <OPTION value="Year">保存一年</OPTION>
                                        <OPTION value="None">不保存</OPTION>
                                    </SELECT></LI>
                            </UL>
                        </DIV>
                    </LI>
                    <LI class="user_main_r">
                        <INPUT class="IbtnEnterCssClass" id="IbtnEnter"
                               style="BORDER-TOP-WIDTH: 0px; BORDER-LEFT-WIDTH: 0px; BORDER-BOTTOM-WIDTH: 0px; BORDER-RIGHT-WIDTH: 0px"
                               type="image" src="<%=contextPath%>/resources/login/images/user_botton.gif"
                               onclick="if( ValidateLength()){  document.winform.submit();}"
                               name="Submit"></LI>
                </UL>
            </DD>
            <DD id="user_bottom">
                <UL>
                    <LI class="user_bottom_l"></LI>
                    <LI class="user_bottom_c"><SPAN style="MARGIN-TOP: 40px">版权所有 <A
                            href="#about:blank">FBI</A> </SPAN></LI>
                    <LI class="user_bottom_r"></LI>
                </UL>
            </DD>

        </DL>
    </form>
</DIV>

<SPAN id="ValrUserName" style="DISPLAY: none; COLOR: red"></SPAN>
<SPAN id="ValrPassword" style="DISPLAY: none; COLOR: red"></SPAN>
<SPAN id="ValrValidateCode" style="DISPLAY: none; COLOR: red"></SPAN>

<DIV id="ValidationSummary1" style="DISPLAY: none; COLOR: red"></DIV>
</body>
</html>