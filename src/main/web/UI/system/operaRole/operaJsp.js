
var dhxLayout;
function doOnLoad() {

    changeheigth();
    onUserDocumentLoad();

    dhxLayout = new dhtmlXLayoutObject("parentId", "2U");
    dhxLayout.cells("a").setText("机构清单");
    dhxLayout.cells("b").setText("用户明细");

    //var rightHight = document.body.offsetHeight;
    dhxLayout.cells("a").setWidth(240);
    dhxLayout.cells("a").attachObject("menuId");
    dhxLayout.cells("b").attachObject("gridId");
}


 function changeheigth(){
	document.all("rootUl").style.height=document.body.offsetHeight -65;
}


function onUserDocumentLoad()
{
		var domdoc = createDomDocument();

		var retStr = createselectArr("parentdeptid","text","0","sm0061");

		if (retStr != false)
		{

			domdoc.loadXML(retStr);

			var Root = domdoc.documentElement;

			treeInit();
			Nodes_clear();


			var nRoot = vNodes.add("", "", "0", "人员授权", "/images/frame/domain.gif");

			nRoot.xData.xNode = new Object();
			nRoot.xData.xNode.appID = "0";

			nRoot.xData.xNode.childCount = 0;
			nRoot.xData.waitforLoad = false;

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
			var nApp = nFather.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value"))+"("+decode(getAttrValue(node.childNodes[0],"value"))+")", "/images/frame/about.gif");
		else
			var nApp = nFather.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value"))+"("+decode(getAttrValue(node.childNodes[0],"value"))+")", "/images/frame/user.gif");

		nApp.xData.xNode = new Object();
		nApp.xData.xNode.appID =decode(getAttrValue(node.childNodes[2],"value"));



           if(decode(getAttrValue(node.childNodes[2],"value"))=="0"){
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
		var retStr = createselectArr("parentdeptid","text",n.key,"sm0061");

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
					var nApp = n.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value"))+"("+decode(getAttrValue(node.childNodes[0],"value"))+")", "/images/frame/about.gif");
				else
					var nApp = n.add("tvwChild", decode(getAttrValue(node.childNodes[0],"value")), decode(getAttrValue(node.childNodes[1],"value"))+"("+decode(getAttrValue(node.childNodes[0],"value"))+")", "/images/frame/user.gif");

				nApp.xData.xNode = new Object();
				nApp.xData.xNode.appID =decode(getAttrValue(node.childNodes[2],"value"));
                     //nApp.xData.waitforLoad = true;
          		//nApp.add("tvwChild", "", "载入中...");

                    //alert(decode(getAttrValue(node.childNodes[2],"value")));
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

	document.all("paramValue").value = "OperID&text&"+document.node.key;
    innerDocTD.innerHTML = "<iframe id='frmContainer' src='roleJsp.jsp' style='WIDTH:98%;HEIGHT:98%' frameborder='0' scrolling='auto'></iframe>";

/*
	if (document.node.xData.xNode.appID =="1")
	{

		innerDocTD.innerHTML = "<iframe id='frmContainer' src='roleJsp.jsp' style='WIDTH:98%;HEIGHT:98%' frameborder='0' scrolling='auto'></iframe>";

	}else
		innerDocTD.innerHTML = "<iframe id='frmContainer' src='/UI/system/blank.html' style='WIDTH:98%;HEIGHT:98%' frameborder='0' scrolling='auto'></iframe>";
*/
	imgClick();
}
