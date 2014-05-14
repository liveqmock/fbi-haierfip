package pub.auth;

import pub.platform.form.config.SystemAttributeNames;
import pub.platform.security.OperatorManager;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-1-18
 * Time: 下午4:54
 * To change this template use File | Settings | File Templates.
 */
public class SecurityPhaseListener implements PhaseListener {
    public void afterPhase(PhaseEvent event) {
    }

    public void beforePhase(PhaseEvent event) {

        FacesContext fc = event.getFacesContext();
        /*
        HttpServletResponse response = (HttpServletResponse)fc.getExternalContext().getResponse();
        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addHeader("Cache-Control", "no-store");
        response.addHeader("Cache-Control", "must-revalidate");
        */

        ExternalContext ec = fc.getExternalContext();
        HttpSession session = (HttpSession) ec.getSession(true);
        HttpServletResponse response = (HttpServletResponse) ec.getResponse();
        HttpServletRequest request = (HttpServletRequest) ec.getRequest();

        //UIViewRoot uiv = new UIViewRoot();
        //String viewid = fc.getViewRoot().getViewId();
        //NavigationHandler nh = fc.getApplication().getNavigationHandler();

        OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
        if (om == null) {
            try {
                String path = request.getContextPath();
                response.setCharacterEncoding("GBK");
                response.getWriter().write("<script language=\"javascript\">alert ('JSF操作员超时，请重新登录！'); if(top){ top.location.href='" + path + "/pages/security/loginPage.jsp'; } else { location.href = '" + path + "/pages/security/loginPage.jsp';} </script>");
            } catch (IOException e) {
                System.out.println("Response 处理错误!");
            }
            fc.responseComplete();
        }

    }

    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }

/*
    public void doRedirect(FacesContext fc, String redirectPage) throws FacesException {
        ExternalContext ec = fc.getExternalContext();

        try {
            // workaround for PrimeFaces
            new RequestContextImpl(ec);
            if (ec.getRequestParameterMap().containsKey(Constants.PARTIAL_PROCESS_PARAM)
                    && !ec.getRequestParameterMap().get(Constants.PARTIAL_PROCESS_PARAM).equals("@all")) {
                fc.setViewRoot(new PartialViewRoot(new UIViewRoot()));
            }

            // fix for renderer kit (Mojarra's and PrimeFaces's ajax redirect)
            if ((RequestContext.getCurrentInstance().isAjaxRequest()
                    || fc.getPartialViewContext().isPartialRequest())
                    && fc.getResponseWriter() == null
                    && fc.getRenderKit() == null) {
                ServletResponse response = (ServletResponse) ec.getResponse();
                ServletRequest request = (ServletRequest) ec.getRequest();
                response.setCharacterEncoding(request.getCharacterEncoding());

                RenderKitFactory factory =
                        (RenderKitFactory) FactoryFinder.getFactory(FactoryFinder.RENDER_KIT_FACTORY);
                RenderKit renderKit =
                        factory.getRenderKit(fc, fc.getApplication().getViewHandler().calculateRenderKitId(fc));
                ResponseWriter responseWriter =
                        renderKit.createResponseWriter(response.getWriter(), null, request.getCharacterEncoding());
                fc.setResponseWriter(responseWriter);
            }

            ec.redirect(ec.getRequestContextPath() + (redirectPage != null ? redirectPage : ""));
        } catch (IOException e) {
            System.out.println("Redirect to the specified page '" + redirectPage + "' failed");
            throw new FacesException(e);
        }
    }
*/

}
