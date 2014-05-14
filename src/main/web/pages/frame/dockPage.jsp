<%@ page contentType="text/html; charset=gb2312" %>
<%
    String path = request.getContextPath();
%>
<html>
<head>
    <title>LearnIT Content Viewer</title>
    <meta http-equiv="Content-Type" content="text/html; charset=gb2312">
    <link href="<%=path %>/css/ccb.css" type="text/css" rel="stylesheet">
    <script type="text/javascript">
        var status = 1;
        function switchSysBar() {

            if (1 == window.status) {
                window.status = 0;
                //                document.getElementById("switchPoint").innerHTML = '<img src="<%=path%>/images/rightarrow.gif" title="展开左栏" />';
                document.getElementById("switchPoint").innerHTML = '<img src="../../images/splitter_r.gif" title="展开左栏" />';
                parent.document.getElementById("menuFrame").cols = "0,12,*";
            }
            else {
                window.status = 1;
                //                document.getElementById("switchPoint").innerHTML = '<img src="<%=path%>/images/leftarrow.gif" title="隐藏左栏" />';
                document.getElementById("switchPoint").innerHTML = '<img src="../../images/splitter_l.gif" title="隐藏左栏" align="right" />';
                parent.document.getElementById("menuFrame").cols = "200,12,*";
            }
        }


    </script>
</head>
<%--<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" scroll="no" style="background-color:#cddcf0">--%>
<body leftmargin="0" topmargin="0" rightmargin="0" bottommargin="0" scroll="no"
      background="<%=path %>/images/splitter_bg.gif">
<table align="center" cellSpacing="0" cellPadding="0" width="100%" height="100%" border="0">
    <tr align="center">
        <td align="center" valign="middle" width="5px">
            <table cellSpacing="0" cellPadding="0" border="0">
                <tr onclick="switchSysBar()" height="100%">
                    <td onmouseover="this.style.cursor='hand'" onmouseout="this.style.cursor='default'" title="隐藏菜单"
                        align="right" id="switchPoint">
                        <%--<span id="switchPoint"><img src="<%=contextPath%>/images/leftarrow.gif" title="隐藏左栏">--%>
                        <span id="switchPoint"><img src="<%=path %>/images/splitter_l.gif" title="隐藏左栏"
                                                    align="right"></span>
                    </td>
                </tr>
            </table>
        </td>
    </tr>
</table>
</body>
</html>
