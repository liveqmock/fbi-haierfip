<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="pub.platform.utils.BusinessDate" %>
<%@ page import="pub.platform.html.*,pub.platform.advance.utils.*" %>
<html>
<head>
<title>´íÎóÐÅÏ¢ÌáÊ¾</title>
<LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
	<script language="javascript" src="<%=contextPath%>/js/dropdownData.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/Calendar.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/tree.js"></script>
    <script language="javascript" src="<%=contextPath%>/js/getDeptuser.js"></script>
</head>



<script language="javascript">
	<!--

	function feeeditinit(){

     	var arg = window.dialogArguments;

          if (arg){

               var xmlDoc = createDomDocument();

		     xmlDoc.loadXML(arg.errorMsg);

		     var rootNode = xmlDoc.documentElement;


               var errorid = rootNode.getAttribute("moudleid") +"_"+rootNode.getAttribute("errortype") +"_" +rootNode.getAttribute("errorindex");

               var errormsg = rootNode.getAttribute("message");


                //document.all["errorid"].innerText = errorid;
                document.all["errormsg"].innerText = "´íÎó±àºÅ£º   "+errorid+";\n´íÎóÄÚÈÝ£º   "+errormsg;

                node = rootNode.firstChild;

                while(node){
                        if ( trimStr(document.all["expmsg"].value) != "")
						document.all["expmsg"].value = document.all["expmsg"].value +" \n \n" +node.text;
                         else
                             document.all["expmsg"].value  =node.text;

				    node = node.nextSibling;

                }


           }
         window.dialogHeight ="137px"
           
         document.all["errorclose"].focus();  
           
	}

     function hiddenmsg(){

          if (window.dialogHeight =="272px"){
          	 window.dialogHeight="137px";
          	hiddbut.value="ÏÔÊ¾ÏêÏ¸ÐÅÏ¢"

         }else{
              window.dialogHeight ="272px"
              hiddbut.value="Òþ²ØÏêÏ¸ÐÅÏ¢"
         }

     }


</script>


<body bgcolor="#ffffff" onload="feeeditinit()">



 <table style="position:absolute;top:10;width:100%"  >
	
		<tr>
			<td  align="center" width="60px">
				<img   src="<%=contextPath%>/images/Warning.GIF">
				</td></td>
			
			<td  align="left"noWrap width="400px">
				 <textarea id="errormsg"  rows="5" cols="60" STYLE="overflow:hidden"   disabled="disabled" ></textarea>
				
			
			</td>


  	    </tr>

         <tr>
			<td colspan="2" align="right"><input id="hiddbut" type="button" value="ÏÔÊ¾ÏêÏ¸ÐÅÏ¢" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  onclick="hiddenmsg()">
			<input id="errorclose" type="button" value="    ¹Ø±Õ    " width="60px" onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  onclick="window.close()">
			
			</td>

		 </tr>
  	    <tr>
			<td colspan="2" align="center">
               <textarea id="expmsg"  rows="8" cols="65"  disabled="disabled" ></textarea>





               </td>
		 </tr>

	</table>


</body>
</html>
