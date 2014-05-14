<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<html>
<head>
<title>

</title>
	<LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
     <script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
     <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
     <script language="javascript" src="<%=contextPath%>/js/dbutil.js"></script>
     <script language="javascript" src="<%=contextPath%>/js/tree.js"></script>
	 <script language="javascript">
	  var tree = new Tree();
       var arg ;
       var treeRoot;

       var isall="";
       var isdistsup="";
       var isdept="";



	function onUserDocumentLoad()
     {

       	var arg = window.dialogArguments;

          if (arg)
          {
          	   isall = arg.isall;
               isdistsup = arg.isdistsup;
               isdept = arg.isdept;
          }

          var domdoc = createDomDocument();

          var retStr = createselectArr("parentdeptid,isall,isdistsup,isdept","text,text,text,text","0,"+isall+","+isdistsup+","+isdept,"sm0046");

          if (retStr != false)
          {

               domdoc.loadXML(retStr);

               var Root = domdoc.documentElement;

               treeInit();
               Nodes_clear();

			if(isdept =="1")
               	var nRoot = vNodes.add("", "", "0", "CATV部门列表", "<%=contextPath%>/images/domain.gif");
               else
               	var nRoot = vNodes.add("", "", "0", "CATV人员列表", "<%=contextPath%>/images/domain.gif");

               treeRoot =nRoot;

               nRoot.xData.xNode = new Object();
               nRoot.xData.xNode.appID = "0";

               nRoot.xData.xNode.childCount = 0;
               nRoot.xData.waitforLoad = false;
			   nRoot.setCheckbox(true);

               if (Root.childNodes.length >0){
                    showApplications(Root.firstChild, nRoot);
               }
               nRoot.setExpanded(true);
          }

          domdoc.free;
     }

     function showApplications(eleRoot, nFather, bManagedApp)
     {
          var node = eleRoot;
          var count = 0;

          while(node)
          {

               if(decode(getAttrValue(node.childNodes[2],"value"))=="0")
                    var nApp = nFather.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value"))+"("+decode(getAttrValue(node.childNodes[0],"value"))+")", "<%=contextPath%>/images/about.gif");
               else
                    var nApp = nFather.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value"))+"("+decode(getAttrValue(node.childNodes[0],"value"))+")", "<%=contextPath%>/images/user.gif");

               nApp.xData.xNode = new Object();
               nApp.xData.xNode.appID =decode(getAttrValue(node.childNodes[2],"value"));
			nApp.setCheckbox(true);


                if(decode(getAttrValue(node.childNodes[2],"value"))=="0" ){
                    nApp.xData.waitforLoad = true;
                    nApp.add("tvwChild", "", "载入中...");
               }else
                    nApp.xData.waitforLoad = false;

               node = node.nextSibling;
               count = count +1;
          }

     }
     function tvNodeRightClick(node)
     {

     }

     function tvNodeExpand(){
          try
          {
               var n = document.node;

               if (n.xData.waitforLoad)
                    loadChildren(n);
          }
          catch(e)
          {

          }
     }
     ///装载树形子节点
     function loadChildren(n)
     {
          try{
               var retStr = createselectArr("parentdeptid,isall,isdistsup,isdept","text,text,text,text",n.key+","+isall+","+isdistsup+","+isdept,"sm0046");

               if (retStr != "false")
               {

                    var domdoc = createDomDocument();
                    domdoc.loadXML(retStr);

                    var Root = domdoc.documentElement;
                    var node = Root.firstChild;

                    n.xData.waitforLoad = false;
                    n.removeChildren();
                    while(node)
                    {

                         if(decode(getAttrValue(node.childNodes[2],"value"))=="0")
                              var nApp = n.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value"))+"("+decode(getAttrValue(node.childNodes[0],"value"))+")", "<%=contextPath%>/images/about.gif");
                         else
                              var nApp = n.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value"))+"("+decode(getAttrValue(node.childNodes[0],"value"))+")", "<%=contextPath%>/images/user.gif");

                         nApp.xData.xNode = new Object();
                         nApp.xData.xNode.appID =decode(getAttrValue(node.childNodes[2],"value"));
                         nApp.setCheckbox(true);

                         if(decode(getAttrValue(node.childNodes[2],"value"))=="0"){

                         	nApp.xData.waitforLoad = true;
                         	nApp.add("tvwChild", "", "载入中...");

                         }else
                              nApp.xData.waitforLoad = false;

                         node = node.nextSibling;


                    }
                    domdoc.free;
               }
          }
          catch(e){
          }

     }

function tvNodeSelected(){
	imgClick();
}


function saveOnclick(){

	window.returnValue= getCheckedDeptUser(treeRoot);
	close();

}

function closeOnclick(){
	close();
}

function getCheckedDeptUser(nod){

     if (nod)
     {
         if (nod.children >0) {
              var nchild = nod.firstChild;
              var lastchild = nod.lastChild;

              while(nchild !=lastchild){
                    if(nchild.checkbox.checked){
                         if (chekedKeyStr =="")
                         {
                              if (nchild.key != undefined){

                                   if (isdept =="0"){
                              		if (nchild.xData.xNode.appID!="0")
                                             chekedKeyStr +=nchild.key;
                                   }else
                                   	chekedKeyStr +=nchild.key;
                              }
                         }else{
                              if (nchild.key != undefined){
                                   if (isdept =="0"){
                              		if (nchild.xData.xNode.appID!="0")
                                             chekedKeyStr +=","+nchild.key;
                                   }else
                                   	chekedKeyStr +=","+nchild.key;
                              }
                         }


                    }
                    if (nchild.children >0)
                         getCheckedDeptUser(nchild);

                    nchild = nchild.next;
              }

               if(lastchild.checkbox.checked){

                    if (chekedKeyStr =="")
                    {
                         if (lastchild.key != undefined){
                               if (isdept =="0"){

                              	if (lastchild.xData.xNode.appID!="0")
                                         chekedKeyStr +=lastchild.key;
                               	}else
                               		chekedKeyStr +=lastchild.key;
                         }
                    }else{
                         if (lastchild.key != undefined){
                               if (isdept =="0"){
                              	if (lastchild.xData.xNode.appID!="0")
                                         chekedKeyStr +=","+lastchild.key;
                               }else
                               	chekedKeyStr +=","+lastchild.key;
                         }
                    }

               }

               if (lastchild.children >0)
                    getCheckedDeptUser(lastchild);
          }


     }
     return  chekedKeyStr;


     }




	</script>
</head>
<body bgcolor="#ffffff" onload="onUserDocumentLoad();">
	<table style="WIDTH:100%;HEIGHT:100%;left:0px" cellspacing="0" ID="Table1">
              <tr style="HEIGHT:64px">
				<td class="borderGrooveB">
					<font face="SimSun" style="FONT-WEIGHT: bold; FONT-SIZE: 18pt"><img class="shadowAlpha" align="absMiddle" src="<%=contextPath%>/images/domain.gif" width="48" height="48">人员列表</font>
				</td>
			</tr>
			<tr style="HEIGHT:300px">
				<td  class="borderGrooveBRL" bgcolor= "#ffffff" style="WIDTH:230px;height:300px" >

					<ul
                              style="OVERFLOW:auto;WIDTH:100%;HEIGHT:330px; ;left:-30px;POSITION: absolute;top:60px"  id="rootUl"  onmousemove = "tv_OnMouseMove()"	 onmousedown = "tv_OnMouseDown()" onmouseup = "tv_OnMouseUp()"></ul>

					</td>

			</tr>
			 <tr style="HEIGHT:50px" >
				<td  align="right" avlign="top">
					<input class="buttonGrooveDisable" id="savebuuton" value="保存"  type=button  style="height: 22px;width:45px; "  onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  onclick="saveOnclick()">
					<input class="buttonGrooveDisable" id="colsebuuton" value="取消"  type=button  style="height: 22px;width:45px; "  onmouseover="button_onmouseover()" onmouseout="button_onmouseout()"  onclick="closeOnclick()">

					</td>
			</tr>
	</table>
</body>
</html>
