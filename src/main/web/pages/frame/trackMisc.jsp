<!--
/*********************************************************************
* 功能描述: 登录系统后显示此页
* 开发日期: 2010/08/02
* 修 改 人: zxb
* 修改日期:
* 版 权: 公司
***********************************************************************/
-->
<%@page contentType="text/html; charset=GBK" %>
<%@include file="/global.jsp" %>
<html>
<head>
    <META http-equiv="Content-Type" content="text/html; charset=GBK">
    <title></title>
    <script language="javascript" src="trackMisc.js"></script>
    <script language="javascript" src="<%=contextPath%>/UI/support/pub.js"></script>
</head>
<body bgcolor="#ffffff" onLoad="body_load()" class="Bodydefault">
<div id="cardbaseinfo_msg" class="queryPanalDiv">
    <fieldset style="padding-top:30px;padding-bottom:0px">
        <%--<fieldset>--%>
        <legend>公务卡基本信息</legend>
        <br>
        <table border="0" cellspacing="0" cellpadding="5" width="50%">
                <tr>
                <td width="60%" class="lbl_right_padding">公务卡基本信息未发送记录数</td>
                <td width="40%" class="data_input"  id="_cardbase_newCnt"></td>
            </tr>
                 <tr>
                <td width="60%" class="lbl_right_padding">公务卡基本信息注销记录数</td>
                <td width="40%" class="data_input"  id="_cardbase_nullCnt"></td>
            </tr>
        </table>
        <br>
    </fieldset>
</div>
<div id="card_info_msg" class="queryPanalDiv">
    <fieldset style="padding-top:30px;padding-bottom:0px">
        <%--<fieldset>--%>
        <legend>公务卡消费信息</legend>
        <br>
        <table border="0" cellspacing="0" cellpadding="5" width="50%">
            <form id="queryForm" name="queryForm">
                <input type="hidden" id="operType" name="operType" value="initQuery"/>
                <tr>
                <td width="60%" class="lbl_right_padding">消费信息初始状态(正等待发送)记录数</td>
                <td width="40%" class="data_input"  id="_consume_sendCnt"></td>
            </tr>
                <tr>
                <td width="60%" class="lbl_right_padding">消费信息异常状态(需重新发送)记录数</td>
                <td width="40%" class="data_input"  id="_consume_exceptionCnt"></td>
            </tr>
            </form>
        </table>
        <br>
    </fieldset>
</div>
<div id="payback_info_msg" class="queryPanalDiv">
    <fieldset style="padding-top:30px;padding-bottom:0px">
        <%--<fieldset>--%>
        <legend>公务卡还款信息</legend>
        <br>
        <table border="0" cellspacing="0" cellpadding="5" width="50%">
                <tr>
                <td width="60%" class="lbl_right_padding">公务卡消费信息未还款记录数</td>
                <td width="40%" class="data_input"  id="_payback_intCnt"></td>
            </tr>
                
        </table>
        <br>
    </fieldset>
</div>
<!--<div id="help_msg" class="queryPanalDiv">
    <fieldset style="padding-top:30px;padding-bottom:0px">
        <legend>建议操作</legend>
        <br>
        <table border="0" cellspacing="0" cellpadding="5" width="50%">
                <tr>
                <td width="45%" class="lbl_right_padding">建议</td>
                <td width="55%" class="data_input"  id="_help_msg"></td>
            </tr>
        </table>
        <br>
    </fieldset>
</div>-->
</body>
</html>
