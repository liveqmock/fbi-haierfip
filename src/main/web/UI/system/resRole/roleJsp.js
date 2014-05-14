var dhxLayout;
function doOnLoad() {

    changeheigth();
    onUserDocumentLoad();

    dhxLayout = new dhtmlXLayoutObject("parentId", "2U");
    dhxLayout.cells("a").setText("角色清单");
    dhxLayout.cells("b").setText("资源明细");

    //var rightHight = document.body.offsetHeight;
    dhxLayout.cells("a").setWidth(240);
    dhxLayout.cells("a").attachObject("menuId");
    dhxLayout.cells("b").attachObject("gridId");
}

function changeheigth() {
    document.all("rootUl").style.height = document.body.offsetHeight - 65;
}

function onUserDocumentLoad() {

    var domdoc = createDomDocument();

    var retStr = createselectArr("RoleID", "text", "0", "sm0077");

    if (retStr != false) {

        domdoc.loadXML(retStr);

        var Root = domdoc.documentElement;

        treeInit();
        Nodes_clear();


        var nRoot = vNodes.add("", "", "0", "角色授权", g_jsContextPath+"/images/frame/domain.gif");

        nRoot.xData.xNode = new Object();
        nRoot.xData.xNode.appID = "0";

        nRoot.xData.xNode.childCount = 0;
        nRoot.xData.waitforLoad = false;

        if (Root.childNodes.length > 0) {
            showApplications(Root.firstChild, nRoot);
        }
        nRoot.setExpanded(true);
    }


}

function showApplications(eleRoot, nFather, bManagedApp) {
    var node = eleRoot;
    var count = 0;

    while (node) {

        var nApp = nFather.add("tvwChild", decode(getAttrValue(node.childNodes[0], "value")), decode(getAttrValue(node.childNodes[1], "value")), "/images/frame/about.gif");

        nApp.xData.xNode = new Object();
        nApp.xData.xNode.appID = "1";
        nApp.xData.waitforLoad = false;

        node = node.nextSibling;
        count = count + 1;


    }


}


function tvNodeRightClick(node) {

}

function tvNodeExpand() {

}
///装载树形子节点

function tvNodeSelected() {

    document.all("paramValue").value = "RoleID&text&" + document.node.key;

    if (document.node.xData.xNode.appID == "1") {

        innerDocTD.innerHTML = "<iframe id='frmContainer' src='resJsp.jsp' style='WIDTH:98%;HEIGHT:98%' frameborder='0' scrolling='auto'></iframe>";

    } else
        innerDocTD.innerHTML = "<iframe id='frmContainer' src='' style='WIDTH:98%;HEIGHT:98%' frameborder='0' scrolling='auto'></iframe>";

}
