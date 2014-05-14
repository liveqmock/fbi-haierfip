<%@ page contentType="text/html; charset=GBK" %>

<%@ page import="java.util.*" %>
<%@ page import="pub.platform.form.config.SystemAttributeNames" %>
<%@ page import="pub.platform.security.OperatorManager" %>
<%@ page import="pub.platform.security.OnLineOpersManager" %>
<%
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    String cookieFlag = request.getParameter("DropExpiration");
    String path = request.getContextPath();
    Cookie cookies[] = request.getCookies();
    int cookieMaxAge = 0;
    if ("Month".equalsIgnoreCase(cookieFlag)) {
        cookieMaxAge = 30 * 24 * 60 * 60;
    } else if ("Year".equalsIgnoreCase(cookieFlag)) {
        cookieMaxAge = 365 * 24 * 60 * 60;
    }
    if (!"None".equalsIgnoreCase(cookieFlag)) {
        boolean isFound = false;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (("usernamecookie").equals(cookie.getName())) {
                    if (!username.equals(cookie.getValue())) {
                        cookie.setValue(username);
                        cookie.setPath(request.getContextPath() + "/pages");
                        cookie.setMaxAge(cookieMaxAge);
                        response.addCookie(cookie);
                    }
                    isFound = true;
                }
            }
        }
        if (!isFound) {
            Cookie cookie = new Cookie("usernamecookie", username);
            cookie.setMaxAge(cookieMaxAge);
            cookie.setPath(request.getContextPath() + "/pages");
            response.addCookie(cookie);
        }
    }

%>

<%
    // 判断是否重复登录
    /*if(OnLineOpersManager.isOnline(username,application)){
         out.println("<script language=\"javascript\">" +
          "alert ('该用户已登录，请勿重复登录！');" +
          " if(top){ top.location.href='" +
          path + "/pages/security/loginPage.jsp'; } else { location.href = '" +
          path + "/pages/security/loginPage.jsp';} </script>");
    }*/
    /*OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
    if (om == null) {
        om = new OperatorManager();
        session.setAttribute(SystemAttributeNames.USER_INFO_NAME, om);
    }*/
    OperatorManager om = new OperatorManager();
    session.setAttribute(SystemAttributeNames.USER_INFO_NAME, om);
    try {
        //if(!om.ImgSign(imgsign))
        //	out.println("<script language=\"javascript\">alert ('输入校验码有误！'); if(top){ top.location.href='/index.jsp'; } else { location.href = '/index.jsp';} </script>");
        om.setRemoteAddr(request.getRemoteAddr());
        om.setRemoteHost(request.getRemoteHost());
        //String imgsign = request.getParameter("imgsign");
        boolean isLogin = false;
        isLogin = om.login(username, password);
        if (!isLogin) {
            out.println("<script language=\"javascript\">alert ('输入用户名或密码有误！'); if(top){ top.location.href='" + path + "/pages/security/loginPage.jsp'; } else { location.href = '" + path + "/pages/security/loginPage.jsp';} </script>");
        } else {
            //out.println("<script language=\"javascript\">alert ('"+session.getId()+"！');</script>");

            if (!OnLineOpersManager.isHasUserList(application)) {
                OnLineOpersManager.setUserListToServer(application);
                OnLineOpersManager.addOperToServer(session.getId() + username, om, application);
            } else {
                OnLineOpersManager.addOperToServer(session.getId() + username, om, application);
            }
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

%>