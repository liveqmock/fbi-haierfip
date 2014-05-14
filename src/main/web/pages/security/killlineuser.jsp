<%@ page contentType="text/html; charset=GBK" %>

<%@ page import="javax.servlet.http.HttpSession" %>
<%@ page import="pub.platform.form.config.SystemAttributeNames" %>

<%@ page import="pub.platform.security.OperatorManager" %>
   
<%
   
  try {
    	
       session.invalidate();      
   
  } catch ( Exception ex ) { 
    
  }

%>
