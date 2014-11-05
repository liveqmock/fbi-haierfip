package pub.platform.security;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AdminServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        doGet(request, response);

    }

    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        String contextPath = request.getContextPath();
        response.sendRedirect(contextPath + "/pages/security/loginPage.jsp");
//        response.sendRedirect(contextPath + "/system/login");
    }
}