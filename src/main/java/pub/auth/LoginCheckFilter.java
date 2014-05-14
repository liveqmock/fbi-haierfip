package pub.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.form.config.SystemAttributeNames;
import pub.platform.security.OperatorManager;

import javax.servlet.*;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-1-19
 * Time: 下午5:01
 * To change this template use File | Settings | File Templates.
 */
public class LoginCheckFilter extends HttpServlet implements Filter {
    private static Logger logger = LoggerFactory.getLogger(LoginCheckFilter.class);
    private String loginPage = "";

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        //logger.debug("REQUEST URL：" + httpServletRequest.getServletPath() + "?" + httpServletRequest.getQueryString());
        //logger.debug("REQUEST PARAM：" + httpServletRequest.getParameterMap());
        String url = httpServletRequest.getServletPath();
        if (url.indexOf("pages") >= 0) {
            chain.doFilter(request, response);
        } else {
            HttpSession session = httpServletRequest.getSession();
            OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);

            if (om != null) {
                chain.doFilter(request, response);
            } else {
                if (httpServletRequest.getHeader("x-requested-with") != null
                        && httpServletRequest.getHeader("x-requested-with")
                        .equalsIgnoreCase("XMLHttpRequest")) {
                     //AJAX
                    //httpServletResponse.addHeader("X-ERROR-PAGE", httpServletRequest.getContextPath() +  "/pages/security/timeout.jsp");
                    httpServletResponse.addHeader("X-ERROR-PAGE", httpServletRequest.getContextPath());
                } else {
                    // 普通http请求
                    //根据WEB.XML中配置的参数进行跳转
                    RequestDispatcher rd = request.getRequestDispatcher(loginPage);
                    rd.forward(request, response);
                }
            }
        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
        this.loginPage = filterConfig.getInitParameter("loginPage");
    }

    public void destroy() {
    }
}