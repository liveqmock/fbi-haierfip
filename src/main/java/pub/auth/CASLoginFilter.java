package pub.auth;

import org.jasig.cas.client.util.AssertionHolder;
import org.jasig.cas.client.validation.Assertion;
import pub.platform.form.config.SystemAttributeNames;
import pub.platform.security.OperatorManager;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-7-29
 * Time: 下午3:00
 * To change this template use File | Settings | File Templates.
 */
public class CASLoginFilter implements Filter {
    public void destroy() {
        //目前没有要销毁的对象
    }

    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        //检查用户是否登录
        boolean isLogin = auth((HttpServletRequest) request);
        String contextPath = ((HttpServletRequest) request).getContextPath();
        if (!isLogin) {
            //没有登录，执行没有登录操作
            ((HttpServletResponse) response).sendRedirect(contextPath + "/pages/frame/errorPage.jsp");
        } else {
            ((HttpServletResponse) response).sendRedirect(contextPath + "/pages/frame/homePage.jsp");
        }
        //已经登录，用户继续操作
        //chain.doFilter(req, res);
    }

    public void init(FilterConfig filterConfig) throws ServletException {

//        String defaultUserPassword = filterConfig.getInitParameter("defaultUserPassword");
//        cASLogin.setDefaultUserPassword(defaultUserPassword);
    }

    /*检查用户是否登录*/
    public Boolean auth(HttpServletRequest request) {
        Assertion assertion;
        Map userAttributes;
        String username = "";

        try {
            assertion = AssertionHolder.getAssertion();
            userAttributes = assertion.getAttributes();

            /*第一次登录，CAS验证过用户名、密码成功之后，会将用户名放入内存中，业务系统可以获得*/
            username = assertion.getPrincipal().getName();

            /*业务系统得到后只需检查此用户是否存在于业务系统中, checkIsExist方法自己实现*/
            return checkIsExist(request,username);
        } catch (Exception e) {
            //TODO
            return false;
        }
    }

    private boolean checkIsExist(ServletRequest request, String username) {
        HttpSession session = ((HttpServletRequest) request).getSession();
        OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);

        if (om == null) {
            om = new OperatorManager();
            session.setAttribute(SystemAttributeNames.USER_INFO_NAME, om);
        }
        try {
            om.setRemoteAddr(request.getRemoteAddr());
            om.setRemoteHost(request.getRemoteHost());
            return om.login4sso(username);
        } catch (Exception e) {
            //TODO
            e.printStackTrace();
        }
        return true;
    }
}
