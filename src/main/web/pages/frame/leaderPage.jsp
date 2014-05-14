<%@ page contentType="text/html; charset=GB2312" %>
<%@ include file="/pages/security/loginassistor.jsp" %>
<script language="javascript" src="<%=contextPath%>/js/basic.js"></script>

<html>
     <head>
          <title></title>

          <link href="<%=contextPath%>/css/catv.css" type="text/css" rel="stylesheet">
          
     </head>
   
<body>
<iframe  id='frmContainer' src='http://192.168.7.85:7001/UI/cims/assistant/RealTimeQuery.jsp' style="WIDTH: 100%; HEIGHT: 100%" frameborder='0' scrolling='auto'></iframe>
</body>
</html>
