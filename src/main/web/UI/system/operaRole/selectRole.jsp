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
       var nRoot;
	  function load_Role(){

		var arg = window.dialogArguments;
		var operid ="";
		
		if (arg)
		{			
		   operid = arg.operid ;			
	    }
		var domdoc = createDomDocument();

		var retStr = createselectArr("operid","text",operid,"sm0062");

		if (retStr != false)
		{

			domdoc.loadXML(retStr);

			var Root = domdoc.documentElement;


               tree.clear();


			 nRoot =  tree.node.add("", "", "0", "角色列表", "<%=contextPath%>/images/frame/domain.gif");

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


}

function showApplications(eleRoot, nFather, bManagedApp)
{
	var node = eleRoot;
	var count = 0;

	while(node)
	{
     	var nApp = nFather.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value")), "<%=contextPath%>/images/frame/user.gif");

		nApp.xData.xNode = new Object();

          nApp.xData.waitforLoad = false;
		nApp.setCheckbox(true);
		node = node.nextSibling;
		count = count +1;


	}

}
function tvNodeExpand(){
}
function tvNodeSelected(){

}

function tvNodeRightClick(node)
{


}
function saveOnclick(){
	window.returnValue= tree.getCheckedChild(nRoot);
	close();

}

function closeOnclick(){
	close();
}

	</script>
</head>
<body bgcolor="#ffffff" onload="load_Role();" class="Bodydefault">
	<table style="WIDTH:100%;HEIGHT:100%;left:0px" cellspacing="0" ID="Table1">
              <tr style="HEIGHT:64px">
				<td class="borderGrooveB">
					<font face="SimSun" style="FONT-WEIGHT: bold; FONT-SIZE: 18pt"><img class="shadowAlpha" align="absMiddle" src="<%=contextPath%>/images/frame/domain.gif" width="48" height="48">角色列表</font>
				</td>
			</tr>
			<tr style="HEIGHT:300px">
				<td   bgcolor= "#ffffff" style="WIDTH:230px;height:300px" >
						
						<ul  style="OVERFLOW:auto;WIDTH:100%;HEIGHT:330px; ;left:-30px;POSITION: absolute;top:75px"    id="rootUl"  onmousemove = "tv_OnMouseMove()"	onmousedown = "tv_OnMouseDown()" onmouseup = "tv_OnMouseUp()"></ul>
						
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
