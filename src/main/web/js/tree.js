/********************************************************************************
 *
 *      文件名：tree.js
 *
 *      作  用： 树型控件。
 *
 *      作  者： 公共包
 *
 *      时  间：  yyyy-mm-dd
 *
 *      版  权：  leonwoo
 *
 ********************************************************************************/


var root = null;
var curSelectNode = null;
var rootUl = null;

curSelectNode = null;

var vNodes = new Array();
var dicNodesWithKey = new Array();
var nodeKeys = new Array();


var autoKeyCount = 0;

var strRootDir = "";

var imgPlus = g_jsContextPath + "/images/plus.gif";
var imgMinus = g_jsContextPath + "/images/minus.gif";


var chekedKeyStr = "";
var chekedCaptionStr = "";
function Tree() {

    treeInit();
    this.node = vNodes;
    this.clear = Nodes_clear
    this.getCheckedChild = getCheckedChild;

}

function getRootObject() {
    return rootUl;
}

function getRootDir() {
    return strRootDir;
}

function setRootDir(s) {
    strRootDir = s;

    var strPrefix = "";

    if (s.length > 0)
        strPrefix = "/";

    imgPlus = strPrefix + s + g_jsContextPath + "/images/plus.gif";
    imgMinus = strPrefix + s + g_jsContextPath + "/images/minus.gif";
}

function getItemByKey(key) {
    var keyNode = null;

    for (var i = 0; i < vNodes.length; i++) {
        if (vNodes[i].key == key) {
            keyNode = vNodes[i];
            break;
        }
    }

    return keyNode;
}


function treeInit() {


    vNodes.add = Nodes_add;
    vNodes.remove = Nodes_remove;
    vNodes.clear = Nodes_clear;

    dicNodesWithKey.Item = Dic_Item;
    dicNodesWithKey.Remove = Dic_Remove;
    dicNodesWithKey.Exists = Dic_Exists;
    dicNodesWithKey.RemoveAll = Dic_RemoveAll;
    dicNodesWithKey.Add = Dic_Add;
}


function Dic_ItemIndex(key) {
    var i;
    for (i = 0; i < nodeKeys.length; i++) {
        if (nodeKeys[i] == key) {
            return i;
        }
    }
    return -1;
}

function Dic_Item(key) {
    var idx;
    idx = Dic_ItemIndex(key);
    if (idx >= 0) {
        return dicNodesWithKey[i];
    }
    else {
        return null;
    }
}


function Dic_Remove(key) {
    var idx;
    idx = Dic_ItemIndex(key);
    if (idx >= 0) {
        dicNodesWithKey.splice(idx, 1);
        nodeKeys.splice(idx, 1);
    } else {
    }
}


function Dic_Exists(key) {
    var idx;
    idx = Dic_ItemIndex(key);
    if (idx >= 0) {
        return true;
    }
    else {
        return false;
    }
}

function Dic_RemoveAll() {
    var i;
    for (i = 0; i < dicNodesWithKey.length; i++) {
        dicNodesWithKey.pop();
    }
    for (i = 0; i < nodeKeys.length; i++) {
        nodeKeys.pop();
    }
}


function Dic_Add(key, item) {
    if (Dic_Exists(key)) {
        alert("key exists.");
    } else {
        dicNodesWithKey.push(item);
        nodeKeys.push(item);
    }
}


function clearRef() {
    var i;
    //alert(vNodes.length);
    for (i = 0; i < vNodes.length; i++) {
        vNodes[i].clearRef();
    }
    while (vNodes.length > 0) {
        vNodes.pop();
    }
    dicNodesWithKey.RemoveAll();
    curSelectNode = null;
    vRoot = null;
    vRootLast = null;
}


function imgClick() {
    var n = event.srcElement;

    while (!n.node)
        n = n.parentElement;

    if (n) {
        n = n.node;
        document.node = n;
        n.setExpanded(!n.getExpanded(), true);
    }
}


function boxClick() {
    var n = event.srcElement;

    while (!n.node)
        n = n.parentElement;

    if (n) {
        n = n.node;
        if (n.children > 0) {
            var nchild = n.firstChild;
            var lastchild = n.lastChild;

            nchild.checkbox.checked = n.checkbox.checked;
            lastchild.checkbox.checked = n.checkbox.checked;
            // if(nchild.children >0){
            //childboxClick(nchild)
            //}
            while (nchild != lastchild) {

                nchild.checkbox.checked = n.checkbox.checked;
                if (nchild.children > 0) {
                    childboxClick(nchild)
                }
                nchild = nchild.next;
            }
        }


    }
}

function childboxClick(n) {

    //	while(!n.node)
    //	n = n.parentElement;

    if (n) {
        //n = n.node;


        if (n.children > 0) {
            var nchild = n.firstChild;
            var lastchild = n.lastChild;

            nchild.checkbox.checked = n.checkbox.checked;
            lastchild.checkbox.checked = n.checkbox.checked;

            while (nchild != lastchild) {
                nchild.checkbox.checked = n.checkbox.checked;
                if (nchild.children > 0) {
                    childboxClick(nchild)
                }
                nchild = nchild.next;
            }
        }


    }


}


function getCheckedChild(nod) {


    if (nod) {

        if (nod.children > 0) {
            var nchild = nod.firstChild;
            var lastchild = nod.lastChild;


            while (nchild != lastchild) {
                if (nchild.checkbox.checked) {
                    if (chekedKeyStr == "") {
                        if (nchild.key != undefined)
                            chekedKeyStr += nchild.key;
                    } else {
                        if (nchild.key != undefined)
                            chekedKeyStr += ";" + nchild.key;
                    }


                }
                if (nchild.children > 0)
                    getCheckedChild(nchild);

                nchild = nchild.next;
            }

            if (lastchild.checkbox.checked) {
                if (chekedKeyStr == "") {
                    if (lastchild.key != undefined)
                        chekedKeyStr += lastchild.key;
                } else {
                    if (lastchild.key != undefined)
                        chekedKeyStr += ";" + lastchild.key;
                }

            }

            if (lastchild.children > 0)
                getCheckedChild(lastchild);
        }


    }
    return  chekedKeyStr;


}

var li;
var div;
var img;
var imgSpan;
var checkbox;
var imgIcon;
var span;
var ul;

function node(text) {
    li = document.createElement("li");
    li.align = "left";
    div = document.createElement("div");
    div.align = "left";


    img = document.createElement("span");

    with (img.style) {
        filter = "";
        visibility = "hidden";
        fontFamily = "Arial";
        fontSize = "8pt";
        width = "9px";
        height = "9px";

        overflow = "hidden";
        cursor = "default";

        borderLeft = "1px solid #c0c0c0";
        borderTop = "1px solid #c0c0c0";
        borderRight = "1px solid #c0c0c0";
        borderBottom = "1px solid #c0c0c0";

        marginRight = "2px";
        verticalAlign = "middle";
    }

    imgSpan = document.createElement("span");

    with (imgSpan.style) {
        filter = "";
        width = "16px";
        height = "16px";
        fontFamily = "Symbol";
        fontSize = "9px";
        position = "relative";
        textAlign = "center";
        left = "-4px";
        top = "-3px";
    }

    imgSpan.innerText = "+";
    img.appendChild(imgSpan);
    img.imgSpan = imgSpan;

    //img.innerText = "+";
    //img.src=imgPlus;
    //img.align="middle";
    img.onclick = imgClick;

    checkbox = document.createElement("input");

    checkbox.type = "checkbox";
    checkbox.style.borderStyle = "none";
    checkbox.onclick = boxClick;

    imgIcon = document.createElement("span");
    with (imgIcon.style) {
        width = 16;
        height = 16;
        display = "none";

        backgroundPosition = "center center";
        backgroundRepeat = "no-repeat";
        verticalAlign = "baseline";
    }

    span = document.createElement("span");

    with (span) {
        style.marginLeft = "4px";
        className = "tvNode";
        innerText = text;
        title = text;
        style.cursor = "hand";
    }

    ul = document.createElement("ul");
    with (ul) {
        style.display = "none";
        style.listStyleType = "none";
        style.marginLeft = "15px";

    }

    with (div) {
        appendChild(img);
        appendChild(checkbox);
        appendChild(imgIcon);
        appendChild(span);
        noWrap = true;
    }

    li.appendChild(div);
    li.appendChild(ul);

    li.node = this;

    this.children = 0;

    this.xData = new Object();

    this.li = li;
    this.span = span;
    this.ul = ul;
    this.img = img;
    this.checkbox = checkbox;
    this.imgIcon = imgIcon;

    this.setCheckbox(false);
    this.setExpanded(false, false);


}

node.prototype.getText = Node_getText;
node.prototype.putText = Node_setText;
node.prototype.getIndex = Node_getIndex;
node.prototype.getRoot = getRoot;
node.prototype.getParent = Node_getParent;
node.prototype.getNext = Node_getNext;
node.prototype.getLastSibling = Node_getLastSibling;
node.prototype.getFirstSibling = Node_getFirstSibling;
node.prototype.getChildren = Node_getChildren;
node.prototype.getChild = Node_getChild;
node.prototype.clearRef = Node_clearRef;
node.prototype.getChecked = Node_getChecked;
node.prototype.setChecked = Node_setChecked;
node.prototype.getCheckbox = Node_getCheckbox;
node.prototype.setCheckbox = Node_setCheckbox;
node.prototype.getExpanded = Node_getExpanded;
node.prototype.setExpanded = Node_setExpanded;
node.prototype.getIcon = Node_getIcon;
node.prototype.setIcon = Node_setIcon;
node.prototype.remove = Node_remove;
node.prototype.removeChildren = Node_removeChildren;
node.prototype.add = Node_add;


function Node_add(relationship, key, text, icon) {
    return(Nodes_add(this, relationship, key, text, icon));
}


function Node_clearRef() {
    this.xData = null;
    this.li.node = null;
    this.li = null;
    this.span = null
    this.ul = null
    this.img = null;
    this.checkbox = null;
    this.imgIcon = null;

    this.previous = null;
    this.next = null;
    this.firstChild = null;
    this.lastChild = null;
    this.parent = null;
}


function Node_getIcon() {
    return this.imgIcon.style.backgroundImage;
}


function Node_setIcon(s) {
    if (s) {
        this.imgIcon.style.backgroundImage = "url(" + s + ")";
        this.imgIcon.style.display = "inline";
    }
}


function Node_getExpanded() {
    if (this.ul.style.display == "none")
        return false;

    return true;
}


function Node_setExpanded(v, bFromClick) {


    if (v) {
        this.ul.style.display = "block";
        this.img.imgSpan.innerText = "-";


        tvNodeExpand();
    }
    else {
        this.ul.style.display = "none";
        this.img.imgSpan.innerText = "+";
        //this.img.src=imgPlus;

        //eventNodeCollapse.fire(oEvent);
    }
}


function Node_getCheckbox() {
    if (this.checkbox.style.display == "none")    return false;
    return true;
}


function Node_setCheckbox(v) {
    if (v) {
        this.checkbox.style.display = "inline";
    } else {
        this.checkbox.style.display = "none";
    }
}


function Node_setChecked(v) {
    this.checkbox.checked = v;
}

function Node_getChecked() {
    return this.checkbox.checked;
}

function Node_getChild() {
    return this.firstChild;
}

function Node_getChildren() {
    return this.children;
}

function Node_getFirstSibling() {
    if (this.parent) {
        return this.parent.firstChild;
    } else {
        return vRoot;
    }
}

function Node_getLastSibling() {
    if (this.parent) {
        return this.parent.lastChild;
    } else {
        return vRootLast;
    }
}

function Node_getNext() {
    return this.next;
}

function Node_getPrevious() {
    return this.previous;
}

function Node_getParent() {
    return this.parent;
}

function Node_getText() {
    return(this.span.innerText);
}

function Node_setText(t) {
    this.span.innerHTML = t;
}

function Node_getIndex() {
    var i;
    for (i = 0; i < vNodes.length; i++) {
        if (this == vNodes[i])
            return i;
    }
}

function Node_removeChildren() {
    while (this.children > 0) {
        Nodes_remove(this.firstChild.getIndex());
    }
}

function Node_remove() {
    Nodes_remove(this.getIndex());
}

var vRoot,vRootLast;
function getRoot() {
    return vRoot;
}

function Nodes_clear() {
    var i;
    dicNodesWithKey.RemoveAll();
    curSelectNode = null;
    vRoot = null;
    vRootLast = null;
    for (i = 0; i < vNodes.length; i++) {
        vNodes[i].clearRef();
    }
    while (vNodes.length > 0) {
        vNodes.pop();
    }

    while (document.all("rootUl").children.length > 0) {
        document.all("rootUl").removeChild(document.all("rootUl").children[0]);
    }
}

function Nodes_remove(idx) {
    if (idx >= 0 && idx < vNodes.length) {
        var n = vNodes[idx];

        if (n == curSelectNode)        //ZWD modified
            curSelectNode = curSelectNode.parent;

        while (n.children > 0) {
            Nodes_remove(n.firstChild.getIndex());
        }

        if (n.key) {
            if (dicNodesWithKey.Exists(n.key))
                dicNodesWithKey.Remove(n.key);
        }

        if (n.previous)
            n.previous.next = n.next;
        if (n.next)
            n.next.previous = n.previous;

        if (n.parent) {
            n.parent.ul.removeChild(n.li);

            if (n.parent.firstChild == n)
                n.parent.firstChild = n.next;

            if (n.parent.lastChild == n)
                n.parent.lastChild = n.previous;

            n.parent.children--;

            if (n.parent.children == 0) {
                n.parent.img.style.visibility = "hidden";
            }
        }
        else {
            rootUl.removeChild(n.li);

            if (vRoot == n)
                vRoot = n.next;
            if (vRootLast == n)
                vRoot = n.previous;
        }
        vNodes.splice(idx, 1);
        n.clearRef();
    }
}

function Nodes_add(relative, relationship, key, text, icon) {
    var relativeNode = null;
    var n = new node(text);
    n.caption = text;

    var newLevel = 0;

    if (key) {
        if (dicNodesWithKey.Exists(key)) {
            alert("key alerady exists.");
            return;
        }
    }

    if (relative) {
        var relType = typeof(relative);

        if (relType == "string") {
            if (dicNodesWithKey.Exists(relative)) {
                relativeNode = dicNodesWithKey.Item(relative);
            }
        }
        else
        if (relType == "number") {
            if (round(relative) < Node.slength) {
                relativeNode = vNodes[round(relative)];
            }
        }
        else
        if (relType == "object") {
            if (relative instanceof node) {
                if (vNodes.length > 0) {
                    if (relative.getRoot() == vRoot)
                        relativeNode = relative;
                }
            }
        }

        if (relativeNode) {
            switch (relationship) {
                case "tvwFirst":
                    if (relativeNode.parent) {
                        relativeNode = relativeNode.parent;
                        n.parent = relativeNode;
                        n.next = relativeNode.firstChild;
                        relativeNode.firstChild.previous = n;
                        relativeNode.firstChild = n;
                        relativeNode.children++;
                        relativeNode.ul.insertBefore(n.li, relativeNode.ul.children[0]);
                    }
                    else {
                        n.next = vRoot;
                        vRoot.previous = n;
                        vRoot = n;
                        rootUl.insertBefore(n.li, rootUl.children[0]);
                    }
                    newLevel = relativeNode.level;
                    break;
                case "tvwLast":
                    if (relativeNode.parent) {
                        relativeNode = relativeNode.parent;
                        n.parent = relativeNode;
                        n.previous = relativeNode.lastChild;
                        relativeNode.lastChild.next = n;
                        relativeNode.lastChild = n;
                        relativeNode.children++;
                        relativeNode.ul.appendChild(n.li);
                    }
                    else {
                        n.previous = vRootLast;
                        vRootLast.next = n;
                        nRootLast = n;
                        rootUl.appendChild(n.li);
                    }
                    newLevel = relativeNode.level;
                    break;
                case "tvwNext":
                    n.previouse = relativeNode;
                    if (relativeNode.parent) {
                        if (relativeNode.next) {
                            relativeNode.next.previous = n;
                        }
                        else {
                            relativeNode.parent.lastChild = n;
                        }
                        relativeNode.parent.children++;
                    }
                    else {
                        if (relativeNode.next) {
                            relativeNode.next.previous = n;
                        }
                        else {
                            vRootLast = n;
                        }
                    }
                    relativeNode.next = n;
                    relativeNode.li.insertAdjacentElement("afterEnd", n.li);
                    newLevel = relativeNode.level;
                    break;
                case "tvwPrevious":
                    n.next = relativeNode;
                    if (relativeNode.parent) {
                        if (relativeNode.previous) {
                            relativeNode.previous.next = n;
                        }
                        else {
                            relativeNode.parent.firstChild = n;
                        }
                        relativeNode.parent.children++;
                    }
                    else {
                        if (relativeNode.previous) {
                            relativeNode.previous.next = n;
                        }
                        else {
                            vRoot = n;
                        }
                    }

                    relativeNode.previous = n;
                    relativeNode.li.insertAdjacentElement("beforeBegin", n.li);
                    newLevel = relativeNode.level;
                    break;
                case "tvwChild":

                    n.parent = relativeNode;
                    if (relativeNode.children > 0) {
                        n.previous = relative.lastChild;
                        relative.lastChild.next = n;
                        relative.lastChild = n;
                    }
                    else {
                        relative.firstChild = n;
                        relative.lastChild = n;
                    }
                    relativeNode.children++;
                    relativeNode.img.style.visibility = "visible";
                    relativeNode.ul.appendChild(n.li);
                    newLevel = relativeNode.level + 1;
                    break;
                default :
                    alert("relationship error");
                    return;
            }
        }
        else {
            return;
        }
    }
    else {
        if (vNodes.length > 0) {
            n.previous = vRootLast;
            vRootLast.next = n;
            vRootLast = n;
        }
        else {
            vRoot = n;
            vRootLast = n;
        }

        document.all("rootUl").appendChild(n.li);
    }

    vNodes.push(n);

    if (key) {
        n.key = key;
        dicNodesWithKey.Add(key, n);
    }

    if (icon)
        n.setIcon(icon);

    n.level = newLevel;

    return n;
}


function getNodes() {
    return vNodes;
}

function getSelectedItem() {
    return curSelectNode;
}

function setSelectedItem(n) {
    if (curSelectNode) {
        if (curSelectNode == n)
            return;

        curSelectNode.span.style.backgroundColor = "transparent";
        curSelectNode.span.style.color = "black";
    }

    curSelectNode = n;

    if (curSelectNode != null) {
        curSelectNode.span.style.backgroundColor = "blue";
        curSelectNode.span.style.color = "white";
    }
}

function selectNode(n) {

    setSelectedItem(n);

    document.node = n;

    tvNodeSelected();


}

var mouseDownNode;

function tv_OnMouseDown() {
    if (event.button == 1 && window.event.srcElement.className == "tvNode") {
        var n = event.srcElement.parentElement.parentElement.node;

        selectNode(n);
        mouseDownNode = n;
    }

}

function tv_OnMouseMove() {
    if (event.button == 1 && window.event.srcElement.className == "tvNode") {

        mouseDownNode = null;
        document.all("rootUl").dragDrop();
    }
}

function tv_OnMouseUp() {

    if (window.event.srcElement.className == "tvNode" && event.button == 2) {

        var n = event.srcElement.parentElement.parentElement.node;


        tvNodeRightClick(n);
    }
}

function dStart() {
    var oEvent;

    oEvent = createEventObject();
    eventOnDragStart.fire(oEvent);
}

