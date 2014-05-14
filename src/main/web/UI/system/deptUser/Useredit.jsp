<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK"%>
<%@ page import="pub.platform.utils.BusinessDate"%>
<%@ page import="pub.platform.html.*,pub.platform.advance.utils.*"%>
<%@ page import="pub.platform.security.OperatorManager"%>
<%@ page import="pub.platform.form.config.SystemAttributeNames"%>
<html>
<head>
<title></title>
<LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
<script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
<script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
<script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
<script language="javascript" src="<%=contextPath%>/js/tree.js"></script>
<script language="javascript" src="<%=contextPath%>/js/getDeptuser.js"></script>
</head>
<%
        BusinessDate busDa = new BusinessDate();

        String fillint12 = "";
        String fillstr150 = "";
        String fillstr600 = "";

        fillint12 = PropertyManager.getProperty("operfillint12");

        fillint12 = new String(fillint12.getBytes(), "GBK");

        fillstr150 = PropertyManager.getProperty("operfillstr150");

        fillstr150 = new String(fillstr150.getBytes(), "GBK");

        fillstr600 = PropertyManager.getProperty("operfillstr600");

        fillstr600 = new String(fillstr600.getBytes(), "GBK");
  %>
<script language="javascript">
	<!--
	var dbOperateType = "";
	var topparent="";
	var deptName="";
	var firstuser="";


	function SaveClick(){
		//dropDeptInit();

		if (dbOperateType =="insert" ||dbOperateType =="update"){

			if (checkForm(feerateedit)!="true")
				return;

			var retxml= "";

			if (dbOperateType =="insert"){
				 retxml =  createExecuteform(feerateedit,"insert","sm0041","");
			}else if (dbOperateType =="update"){
				retxml= createExecuteform(feerateedit,"update","sm0042","");
			}

			if (analyzeReturnXML(retxml)+""=="true"){

				window.returnValue ="1";
				window.close();
		    }
		 }else
		    window.close();
	}

	function feeeditinit(){
		var arg = window.dialogArguments;

		if (arg)
		{
			dbOperateType = arg.type;
			topparent     = arg.topparent;
			deptName      = arg.deptName;
			firstuser     = arg.firstUser;
			document.all.operid.oldvalue ="";
			if (dbOperateType !="insert"){


				document.all["keycode"].value= arg.userID;


				var xmlDoc = createDoc();

				var rootNode =createRootNode(xmlDoc);

				var actionNode = createActionNode(xmlDoc,"sm0044");

				rootNode.appendChild(actionNode);

				var RecorderNode = createRecorderNode(xmlDoc,"select");
				actionNode.appendChild(RecorderNode);

				var fieldNode = createFieldNode(xmlDoc,"deptid","text",arg.deptID);
				RecorderNode.appendChild(fieldNode);

				var fieldNode = createFieldNode(xmlDoc,"keycode","text",arg.userID);
				RecorderNode.appendChild(fieldNode);

				var retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlSelectJsp.jsp","POST","sys_request_xml="+xmlDoc.xml);

				fillform(feerateedit,analyzeReturnXML(retStr));


			}
			if (dbOperateType =="update"){

				//document.all.operid.disabled=true;

			}
			if (dbOperateType =="select"){
				for (var i=0; i< feerateedit.length; i++)
				{
					var obj= feerateedit.item(i);

						obj.disabled=true;
				}
			}
			body_init(feerateedit);


         	var el =document.all["deptid"];
            for ( i= 0;i< el.options.length; i++)
            {
                 if  (el.item(i).value ==arg.deptID)
                      el.selectedIndex =i;
            }
		}
	}



//-->

</script>
<body bgcolor="#ffffff" onLoad="feeeditinit()" class="Bodydefault">
<fieldset>
<legend> 用户信息 </legend>
<form id="feerateedit" style="margin:0px;padding:0px">
  <input id="keycode" fieldName="keycode" fieldType="text" type="hidden">
  <table cellspacing="0" border="0" width="100%">
    <tr>
      <td class="lbl_right_padding"> 所在机构 </td>
      <td class="data_input" colspan="5"><%
                    String whereStr = " where 1=1 ";
                    String childsql = "  union  select deptid,deptname,deptname from ptdept where  PARENTDEPTID in (select deptid from ptdept ";

                    String sqlStr = "select deptid,deptname,deptname from ptdept ";

                    ZtSelect zscn = new ZtSelect("deptid", "deptid");

                    zscn.addAttr("style", "width: 300px");

                    zscn.setSqlString(sqlStr + whereStr + childsql + whereStr + " ) ");

                    zscn.setDisplayAll(false);
                    out.print(zscn);
              %>
      </td>
    </tr>
    <tr>
      <td class="lbl_right_padding"> 操作员号 </td>
      <td class="data_input"><input id="operid" class="text" type="text" isNull="false" maxlength="20">
        <span class="red_star" >*</span> </td>
      <td class="lbl_right_padding"> 操作员姓名 </td>
      <td class="data_input" colspan="3"><input id="opername" class="text" type="text" isNull="false" maxlength="20">
        <span class="red_star" >*</span> </td>
    </tr>
    <tr>
      <td class="lbl_right_padding"> 操作员密码 </td>
      <td class="data_input"><input id="operpasswd" class="text" type="password" isNull="false" maxlength="20">
        <span class="red_star" >*</span> </td>
      <td class="lbl_right_padding"> 是否领导 </td>
      <td class="data_input"><%
                    ZtSelect zscc = new ZtSelect("issuper", "BOOLTYPE");
                    zscc.addAttr("style", "width: 100px");
                    zscc.setDisplayAll(false);
                    out.print(zscc);
              %>
      </td>
      <td class="lbl_right_padding"> 性&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;别 </td>
      <td class="data_input"><%
                    zscc = new ZtSelect("sex", "SEX");
                    zscc.addAttr("style", "width: 100px");

                    zscc.setDisplayAll(false);
                    out.print(zscc);
              %>
      </td>
    </tr>
    <tr>
      <td class="lbl_right_padding"> 电子邮件 </td>
      <td class="data_input"><input id="Email" class="text" type="text" eMail="true">
      </td>
      <td class="lbl_right_padding"> 手&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;机 </td>
      <td class="data_input"><input id="MobilePhone" class="text" type="text" onKeyPress="onKeyPressInputInteger()"  maxLength="13">
      </td>
      <td class="lbl_right_padding"> 电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话 </td>
      <td class="data_input"><input id="OperPhone" class="text" type="text" onKeyPress="onKeyPressPhone()">
      </td>
    </tr>
    <tr>
      <td class="lbl_right_padding"> 其他电话 </td>
      <td class="data_input"><input id="OtherPhone" class="text" type="text" onKeyPress="onKeyPressPhone()">
      </td>
      <td class="lbl_right_padding"> 操作员类型 </td>
      <td class="data_input"><%
                    zscc = new ZtSelect("opertype", "opertype");
                    zscc.addAttr("style", "width: 100px");

                    zscc.setDisplayAll(false);
                    out.print(zscc);
              %>
      </td>
      <td class="lbl_right_padding"> 是&nbsp;否可&nbsp;用 </td>
      <td class="data_input"><%
                    zscc = new ZtSelect("operenabled", "BOOLTYPE", "1");
                    zscc.addAttr("style", "width: 100px");

                    zscc.setDisplayAll(false);
                    out.print(zscc);
              %>
      </td>
    </tr>
    <tr>
      <td class="lbl_right_padding"><%=fillint12%> </td>
      <td class="data_input"><input id="fillint12" class="text" type="text" onKeyUp="onKeyPressInputInteger();"  maxLength="12">
      </td>
      <td class="lbl_right_padding"><%=fillstr150%> </td>
      <td class="data_input"><input id="fillstr150" class="text" type="text">
      </td>
      <td class="lbl_right_padding"><%=fillstr600%> </td>
      <td class="data_input"><input id="fillstr600" class="text" type="text">
      </td>
    </tr>
  </table>
</form>
</fieldset>
<fieldset>
<legend> 操作 </legend>
<table width="100%">
  <tr align="center">
    <td><input id="savebut" class="buttonGrooveDisable"  onmouseover="button_onmouseover()" onMouseOut="button_onmouseout()" type="button"
              value="确定" onClick="SaveClick();">
      <input id="closebut" class="buttonGrooveDisable"  onmouseover="button_onmouseover()" onMouseOut="button_onmouseout()" type="button"
              value="取消" onClick="window.close()">
    </td>
  </tr>
</table>
</fieldset>
</body>
</html>
