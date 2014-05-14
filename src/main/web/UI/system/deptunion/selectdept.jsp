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



	  function load_Role(){
		var domdoc = createDomDocument();

		var retStr = createselectArr("parentdeptid","text","0","sm0034");

		if (retStr != false)
		{

			domdoc.loadXML(retStr);

			var Root = domdoc.documentElement;

			treeInit();
			Nodes_clear();

			var nRoot = vNodes.add("", "", "0", "机构管理", "<%=contextPath%>/images/domain.gif");

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
		var nApp = nFather.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value")), "<%=contextPath%>/images/application.gif");

		nApp.xData.xNode = new Object();
		nApp.xData.xNode.appID = "1";
         nApp.setCheckbox(true);
		nApp.span.title = decode(getAttrValue(node.childNodes[2],"value"));

		nApp.xData.codeName = decode(getAttrValue(node.childNodes[1],"value"));
		if (decode(getAttrValue(node.childNodes[3],"value"))!="0"){
			nApp.xData.waitforLoad = true;

			nApp.add("tvwChild", "", "载入中...");
		}
		node = node.nextSibling;
		count = count +1;


	}

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
		var retStr = createselectArr("parentdeptid","text",n.key,"sm0034");
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
				var nApp = n.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value")), "<%=contextPath%>/images/application.gif");

				nApp.xData.xNode = new Object();
				nApp.xData.xNode.appID = "1";
                    nApp.setCheckbox(true);
				nApp.span.title = decode(getAttrValue(node.childNodes[2],"value"));

				nApp.xData.codeName = decode(getAttrValue(node.childNodes[1],"value"));
				if (decode(getAttrValue(node.childNodes[3],"value"))!="0"){
					nApp.xData.waitforLoad = true;

					nApp.add("tvwChild", "", "载入中...");
				}
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

function tvNodeRightClick(node)
{


}
function saveOnclick(){

	window.returnValue= tree.getCheckedChild(treeRoot);
	close();

}

function closeOnclick(){
	close();
}

	</script>
</head>
<body bgcolor="#ffffff" onload="load_Role();">
	<table style="WIDTH:100%;HEIGHT:100%;left:0px" cellspacing="0" ID="Table1">
              <tr style="HEIGHT:64px">
				<td class="borderGrooveB">
					<font face="SimSun" style="FONT-WEIGHT: bold; FONT-SIZE: 18pt"><img class="shadowAlpha" align="absMiddle" src="<%=contextPath%>/images/domain.gif" width="48" height="48">资源列表</font>
				</td>
			</tr>
			<tr style="HEIGHT:300px">
				<td  class="borderGrooveBRL" bgcolor= "#ffffff" style="WIDTH:230px;height:300px" >

					<ul   style="OVERFLOW:auto;WIDTH:100%;HEIGHT:330px; ;left:-30px;POSITION: absolute;top:60px"  id="rootUl"  onmousemove = "tv_OnMouseMove()"	 onmousedown = "tv_OnMouseDown()" onmouseup = "tv_OnMouseUp()"></ul>

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
