<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="pub.platform.html.*" %>
<%
 //response.setContentType("application/msexcel;charset=gb2312");

%>
<html>
<head>
<title>

</title>

<LINK href="<%=contextPath%>/css/catv.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
	<script language="javascript" src="<%=contextPath%>/js/dropdownData.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/Calendar.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/tree.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/getDeptuser.js"></script>
    
       <script language="javascript" src="<%=contextPath%>/js/JComboBox.js"></script>
</head>




<script language="javascript">
	<!--


	function SaveClick(){


			 var retxml= createExecuteform(feerateedit,"insert","sm0041","piliangadd");

			if (analyzeReturnXML(retxml)+""=="true"){
				window.returnValue ="1";
				window.close();
			}

	}


     function Saverole(){


			 var retxml= createExecuteform(feerateedit,"insert","sm0041","roleadd");

			if (analyzeReturnXML(retxml)+""=="true"){
				window.returnValue ="1";
				window.close();
			}

	}

      function Savedept(){


			 var retxml= createExecuteform(feerateedit,"insert","sm0041","deptadd");

			if (analyzeReturnXML(retxml)+""=="true"){
				window.returnValue ="1";
				window.close();
			}

	}
	
	function birthdayadd(){

         /*
			 var retxml= createExecuteform(feerateedit,"insert","sm0041","birthdayadd");

			if (analyzeReturnXML(retxml)+""=="true"){
				window.returnValue ="1";
				window.close();
			}*/
			
		//window.location.href =	"<%=contextPath%>/UI/birthday.xls";
		window.open("<%=contextPath%>/UI/birthday.xls")
			
			

	}
	
	
	function getserial(){
		getUserSerial(document.all.parentdeptid.value);
	}
	
	
	function testselect_beforeSelect(el){
	
		el.enumType = "AJ_CLJG";
        el.fieldTitle = "编码,名称";
		el.dropwidth ="200px";
	
	
	}
	
	function getdept(){
			 var arguments = "<%=contextPath%>/UI/system/deptUser/getUserJsp.jsp";

              var dialogArg = "dialogwidth:350px; Dialogheight:450px;center:yes;help:no;resizable:no;scroll:no;status:no";
               var arg = new Object();

               ///isall 判断是否显示所有的部门和人员 1 ----是；0----否
               arg.isall="0";

               ///isdistsup 判断是否区分领导 1 ----是；0----否（在isall情况下成立）
          	   arg.isdistsup="0";

                ///isdistsup 判断是否只显示部门   1 ----是；0----否（在isall情况下成立）
               arg.isdept="0";
               
               arg.ischeked="0";//////判断是否出来多选框  1------是；0------否


              var  dd    = window.showModalDialog(arguments, arg, dialogArg);
          	alert(dd);


     }
	function dropdownDept_afterDrop(el){
		alert(el.attr);
	
	
	}

	function initcomp(){
		
		//document.all["issuper"].comboBox =new ComboBox("issuper");
		
		
		//document.all["uper"].comboBox =new ComboBox("uper");
		
	
	}
	function deptchange(){
		//alert("2222");
	
	}
//-->

</script>


<body bgcolor="#ffffff" onload="initcomp()" onclick="body_Click()">
<form id="feerateedit">
<table style="position:absolute;top:10">


          <tr>


			<td>父部门编号:</td><td><input id="parentdeptid"   class="text" type="text"  onKeyPress="onKeyPressCalendar()" maxlength=50></td>


		</tr>
		</table>
</form>

<input id="keycode" fieldName="keycode"  fieldType="text" type="hidden">
<table style="position:absolute;top:30">


          <tr>


			<td>父部门编号:</td><td><input id="parentdeptid"   class="text" type="text"  isNull=false onKeyPress="onKeyPressCalendar()" maxlength=50></td>


		</tr>
		
		 <tr>


			<td>下拉:</td><td><input id="testselect"     isNull=false onKeyUp="getSelect()" maxlength=50></td>


		</tr>
		 <tr><td>是否领导:</td><td width="200">
			 <%
				JComboBox zscc = new JComboBox("issuper", "BOOLTYPE","60");
				zscc.addAttr("style", "width: 100px");
				zscc.setDisplayAll(false);
				zscc.addOption("","");
				zscc.setTitleVisible(false);
				//zscc.setSqlString("select * from ptoper where deptid='37020006003'");
                 zscc.addAttr("onchange","deptchange()");
				out.print(zscc);
		    %>
		    
		    <%
				//ZtSelect zscc1 = new ZtSelect("issuper", "BOOLTYPE");
				//zscc1.addAttr("style", "width: 100px");
				//zscc1.setDisplayAll(false);
                  // zscc1.addAttr("onchange","deptchange()");
				//out.print(zscc1);
		    %>
	</td></tr>
	
	
	<tr><td>是否领导:</td><td width="200">
			 <%
				JComboBox zscc1 = new JComboBox("uper", "AJ_CFJGLX","60");
				zscc1.addAttr("style", "width: 100px");
				zscc1.setDisplayAll(false);
				//zscc1.addOption("","");
				zscc1.setKeyVisible(false);
				//zscc.setSqlString("select * from ptoper where deptid='37020006003'");
                  // zscc.addAttr("onchange","deptchange()");
				out.print(zscc1);
		    %>
		    <%
				//ZtSelect zscc1 = new ZtSelect("issuper", "BOOLTYPE");
				//zscc1.
				//zscc1.addAttr("style", "width: 100px");
				//zscc1.setDisplayAll(false);
                  // zscc1.addAttr("onchange","deptchange()");
				out.print(  " <a href='http://192.168.7.81:7001/temp/aabjbacgiebhb.xls'  target=_blank >3232</a>" );
		    %>
	</td></tr>
   
	</table>

  <input id="savebut" class="buttonGrooveDisable" style=" LEFT: 350px; POSITION: absolute; TOP: 150px;" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="确定" onclick="SaveClick();">
  <input id="closebut" class="buttonGrooveDisable" style=" LEFT: 430px; POSITION: absolute; TOP: 150px;" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="取消" onclick="window.close()"></td></tr>



 <input  class="buttonGrooveDisable" style=" LEFT: 350px; POSITION: absolute; TOP: 250px;" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="保存权限" onclick="Saverole();">
<input  class="buttonGrooveDisable" style=" LEFT: 550px; POSITION: absolute; TOP: 250px;" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="添加部门" onclick="Savedept();">

<input  class="buttonGrooveDisable" style=" LEFT: 550px; POSITION: absolute; TOP: 250px;" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="人员序列号" onclick="getserial();">

<input  class="buttonGrooveDisable" style=" LEFT: 550px; POSITION: absolute; TOP:350px;" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="人员生日" onclick="birthdayadd();">
<input id="getdept" style=" LEFT: 550px; POSITION: absolute; TOP:450px;" class="buttonGrooveDisable"  onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="取出部门" onclick="getdept();">
			
</body>
</html>
