<%@ page contentType="text/html; charset=UTF-8"%>

<%@ page import="pub.platform.form.control.*"%>
<%@ page import="pub.platform.form.control.impl.*"%>
<%@ page import="pub.platform.form.util.SessionAttributes"%>
<%@ page import="pub.platform.utils.*"%>


<%


	request.setCharacterEncoding("GBK");
    ServiceProxy sp = new SeviceProxyHttpImpl();
    sp.proxyService(request,response);
    SessionContext sc = (SessionContext)session.getAttribute(SessionAttributes.SESSION_CONTEXT_NAME);
    SQLResponse sqlReq = (SQLResponse)sc.getRequestAttribute(NewActionController.RESPONSE_XML_NAME);

%>
<%=sqlReq.getXmlStr()%>
