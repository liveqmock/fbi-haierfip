function StartMenu()
{
     
    image1 = new Image();
    image1.src = "/images/frame/plus.gif";
    image1.src = "/images/frame/minus.gif";
    image1.src = "/images/frame/domain.gif";
    image1.src = "/images/frame/including.gif";
    image1.src = "/images/frame/application.gif";
    
	var domdoc = createDomDocument();
    // alert(xmlStr);
   // xmlStr = encode(xmlStr);
	domdoc.loadXML(xmlStr);



	var tree = new Tree();
    tree.clear();

	var Root = domdoc.documentElement;




	var nRoot = tree.node.add("", "", "0", "区级公务卡管理系统", "/images/frame/domain.gif");

	nRoot.xData.xNode = new Object();

	nRoot.xData.waitforLoad = false;
	nRoot.xData.xNode.action="#";
	if (Root.childNodes.length >0){
		showApplications(Root.firstChild, nRoot);
	}
	nRoot.setExpanded(true);

	domdoc.free;
	
	
	
	window.clearTimeout();
	window.setTimeout('openbirthday()',2000);
	window.clearTimeout();


}
function openbirthday(){
  return ;
}


function showApplications(eleRoot, nFather, bManagedApp)
{
	var node = eleRoot;
	var nApp;

	while(node)
	{
		if (getAttrValue(node,"childcount")/1 >0)
			nApp = nFather.add("tvwChild", getAttrValue(node,"menuItemId"), getAttrValue(node,"text"), "/images/frame/including.gif");
		else
			nApp = nFather.add("tvwChild", getAttrValue(node,"menuItemId"), getAttrValue(node,"text"), "/images/frame/application.gif");

		nApp.xData.xNode = new Object();
		
		nApp.xData.xNode.action = getAttrValue(node,"action");
		nApp.xData.xNode.openwin = getAttrValue(node,"openwin");
		nApp.xData.xNode.winwidth = getAttrValue(node,"winwidth");
		nApp.xData.xNode.winheight = getAttrValue(node,"winheight");

		nApp.span.title = getAttrValue(node,"title");
		if (getAttrValue(node,"childcount")/1 >0){
			nApp.xData.waitforLoad = true;

			nApp.add("tvwChild", "", "载入中...");
		}
		node = node.nextSibling;
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

		var retStr = createselectArr("parentdeptid,level","text,text",n.key+","+n.level,"sm0034","getchildmenu");
		
	
		var domdoc = createDomDocument();
		domdoc.loadXML(retStr);

		var Root = domdoc.documentElement;
		var node = Root.firstChild.firstChild;

		n.xData.waitforLoad = false;
		n.removeChildren();

		var nApp;
		while(node)
		{

			if (getAttrValue(node,"childcount")/1 >0)
				nApp = n.add("tvwChild", getAttrValue(node,"menuItemId"), getAttrValue(node,"text"), "/images/frame/including.gif");
			else
				nApp = n.add("tvwChild", getAttrValue(node,"menuItemId"), getAttrValue(node,"text"), "/images/frame/application.gif");



			nApp.xData.xNode = new Object();
			nApp.xData.xNode.xml = node.xml;
			nApp.xData.xNode.action = getAttrValue(node,"action");
			nApp.xData.xNode.openwin = getAttrValue(node,"openwin");
			nApp.xData.xNode.winwidth = getAttrValue(node,"winwidth");
			nApp.xData.xNode.winheight = getAttrValue(node,"winheight");

			nApp.span.title = getAttrValue(node,"title");
			if (getAttrValue(node,"childcount")/1 >0){
				nApp.xData.waitforLoad = true;

				nApp.add("tvwChild", "", "载入中...");
			}
			node = node.nextSibling;


	    }

		domdoc.free;

	}
	catch(e){
	}

}
function tvNodeSelected(){
	var node =document.node;

	if ((node.xData.xNode.action !="#"))
	{
		if (node.xData.xNode.openwin =="0")
		{
			if (window.event.keyCode==0)
				parent.window.workFrame.location.replace(node.xData.xNode.action);
			if (window.event.keyCode==16)
				window.open(node.xData.xNode.action);
		}else{

			 var arguments = node.xData.xNode.action;

			 var dialogArg = "dialogwidth:"+node.xData.xNode.winwidth +"px; Dialogheight:"+node.xData.xNode.winheight+"px;center:yes;help:no;resizable:no;scroll:no;status:no";

			 var  dd    = window.showModalDialog(arguments, "", dialogArg);

		}

	}else if (window.event.keyCode==0)
		imgClick();

}

function tvNodeRightClick(node)
{


}
