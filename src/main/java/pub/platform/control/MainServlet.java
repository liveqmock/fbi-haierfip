package pub.platform.control;

import javax.servlet.*;
import javax.servlet.http.*;

import pub.platform.form.config.SystemAttributeNames;
import pub.platform.form.startup.FormEngineStartup;
import pub.platform.form.util.SessionAttributes;
import pub.platform.security.OperatorManager;

import java.io.*;
import java.util.*;

public class MainServlet extends HttpServlet
{
    public static final String PERMISSION_ERROR_PAGE = "/permissionerror.jsp";
    public static final String LOGIN_ERROR_PAGE      = "/login.jsp";
    public static final String HOME_PAGE             = "/home.jsp";
    public static final String USER_NAME             = "username";
    public static final String USER_PASSWORD         = "password";
    public static final String MAPPINGFILE           = "/WEB-INF/maps.properties";

    //Initialize global variables
    public void init() throws ServletException
    {
        try {
            RequestMapping.init(getServletContext().getResourceAsStream(MAPPINGFILE));
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    //Process the HTTP Get request
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
//        HttpSession session = request.getSession(true);
//        UserManager um = (UserManager)session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
//        if ( um == null ) {
//            um = new UserManager();
//            session.setAttribute(SystemAttributeNames.USER_INFO_NAME,um);
//        }
//        String curUri = request.getRequestURI();
//
//        if ( curUri == null ) {
//            curUri = "/login.do";
//        }
//
//        String nextUri = RequestMapping.map(curUri);
//
//        String contextPath = request.getContextPath();
//
//        if (um.isLogin() && !(nextUri.toLowerCase().equals("login.jsp"))) {
//            if (request.getParameter(SessionAttributes.REQUEST_FORM_ID_NAME) != null ||
//                request.getParameter(SessionAttributes.REQUEST_INSATNCE_ID_NAME) != null ||
//                request.getParameter(SessionAttributes.REQUEST_EVENT_ID_NAME) != null
//                ) { //FORM请求
//                if (!um.checkPermission(nextUri, 2)) {
//                    nextUri = contextPath + nextUri;
//                }
//                else {
//                    nextUri = contextPath + PERMISSION_ERROR_PAGE;
//                }
//            } else if (nextUri != null && nextUri.endsWith(".jsp")) { //JSP请求
//                if (nextUri.endsWith("logout.jsp")) { //签退
//                    try {
//                        um.logout();
//                    }
//                    catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                    nextUri = contextPath + "/login.jsp";
//                }
//                if (um.checkPermission(nextUri, 1)) { //检查JSP的访问权限
//                    nextUri = contextPath + nextUri;
//                }
//                else {
//                    nextUri = contextPath + PERMISSION_ERROR_PAGE;
//                }
//            } else { //其他请求
//                nextUri = nextUri;
//            }
//        } else { //用户签到
//            String username = request.getParameter(USER_NAME);
//            String password = request.getParameter(USER_PASSWORD);
//            System.out.println("name=" + username + " and password=" + password);
//            if (username != null && password != null) {
//                boolean isLogin = false;
//                try {
//                    isLogin = um.login(username, password);
//                }
//                catch (Exception e) {
//                    e.printStackTrace();
//                }
//                if (isLogin) {
//                    nextUri = contextPath + HOME_PAGE;
//                }
//                else {
//                    session.setAttribute(SystemAttributeNames.LOGIN_ERROR_MESSAGE_NAME,
//                                         "用户名不存在,或密码错误");
//                    nextUri = contextPath + "/login.jsp";
//                }
//            }
//            else if (nextUri != null && nextUri.endsWith(".jsp")) {
//                nextUri = contextPath + "/login.jsp";
//            }
//        }
//
//        RequestDispatcher rd = getServletContext().getRequestDispatcher(nextUri);
//        rd.include(request, response);
    }
    //Process the HTTP Post request
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
    {
        doGet(request, response);
    }
    //Clean up resources
    public void destroy()
    {
    }
}