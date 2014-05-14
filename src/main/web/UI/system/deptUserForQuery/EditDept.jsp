<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="pub.platform.html.*" %>
<%@ page import="pub.platform.advance.utils.PropertyManager" %>
<html>
<head>
	<LINK href="<%=contextPath%>/css/catv.css" type="text/css" rel="stylesheet">
	<script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>

<title>

</title>


<%




     String deptfillstr10="";
     String deptfillstr20="";
     String deptfillstr100="";
      String deptfillstr150="";


    deptfillstr10 = PropertyManager.getProperty("deptfillstr10");

    deptfillstr10 = new String(deptfillstr10.getBytes(),"GBK");


    deptfillstr20 = PropertyManager.getProperty("deptfillstr20");

    deptfillstr20 = new String(deptfillstr20.getBytes(),"GBK");


     deptfillstr100 = PropertyManager.getProperty("deptfillstr100");

    deptfillstr100 = new String(deptfillstr100.getBytes(),"GBK");

     deptfillstr150 = PropertyManager.getProperty("deptfillstr150");

    deptfillstr150 = new String(deptfillstr150.getBytes(),"GBK");

%>



<script language="javascript">
	<!--
	   	var dbOperateType = "";
	   	var parentou   ="";
	   	var topparent ="";
	   	

		function SaveClick(){

			if (dbOperateType =="insert"){

				var retxml= createExecuteform(editdept,"insert","sm0031");
				if (analyzeReturnXML(retxml)+""=="true"){
					window.returnValue ="1";
					window.close();
				}
				
			}
			if (dbOperateType =="update")
			{
				var retxml= createExecuteform(editdept,"update","sm0032");

				if (analyzeReturnXML(retxml)+""=="true"){
					window.returnValue ="1";
					window.close();
				}
			}
		}
			function deptinit(){
				var arg = window.dialogArguments;

				if (arg)
				{
                		dbOperateType = arg.type;
                		
                		topparent = arg.topparent;
                		parentou = arg.parent;

						if (dbOperateType =="insert"){
							document.all["parentdeptid"].value= arg.parentid;
						}else{
							document.all["deptid"].value= arg.parentid;
							var retxml= createselect(editdept,"sm0035");

							fillform(editdept,analyzeReturnXML(retxml));

                    }
				}
			}
        	//-->
		</script>
</head>
<body bgcolor="#ffffff" onload="deptinit()">
	<form id="editdept">
 		<table align="center" valign="center" style="top:30">
          
			<tr><td>部门名称</td><td><input class="text" id="deptname" style="width:160px"  fieldName="deptname" isNull="false" maxlength="50" fieldType="text" type="text"></td>

             		<td><%=deptfillstr10%></td><td><input class="text" id="FILLSTR10" style="width:160px"  fieldName="FILLSTR10"  maxlength="10" fieldType="text" type="text"></td>
               </tr>
               <tr>
                    <td><%=deptfillstr20%></td><td><input class="text" id="FILLSTR20" style="width:160px"  fieldName="FILLSTR20"  maxlength="20" fieldType="text" type="text"></td>

               	<td><%=deptfillstr100%></td><td><input class="text" id="FILLSTR100" style="width:160px"  fieldName="FILLSTR100"  maxlength="100" fieldType="text" type="text"></td>
               </tr>
               <tr>
                    <td>部门描述</td><td><textarea id="deptdesc" fieldName="deptdesc"  fieldType="text"  maxlength="200"  type="text" rows="4"></textarea></td>

				<td><%=deptfillstr150%></td><td><textarea id="FILLSTR150" fieldName="FILLSTR150"  fieldType="text"  maxlength="150"  type="text" rows="4"></textarea></td></tr>

               </tr>
               <input id="deptid" fieldName="deptid"  fieldType="text" type="hidden">
			<input id="parentdeptid" fieldName="parentdeptid"  fieldType="text" type="hidden">
			<tr><td colspan="4" align="right">
				<input id="savebut" class="buttonGrooveDisable"  onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="确定" onclick="SaveClick();">
				<input id="closebut" class="buttonGrooveDisable" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  type="button" value="取消" onclick="window.close()"></td></tr>
		</table>
	</form>


</body>
</html>
