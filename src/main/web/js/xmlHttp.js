
//************************************************************//
//该文件是本系统客户端的xml操作、系统交互文件，任何人不得擅自更改	  //
//若有更改与本人联系：公共包		                              //
//************************************************************//

/********************************************************************************
 *
 *      文件名：xmlHttp.js
 *
 *      作  用： xml封装和数据传输。
 *
 *      作  者： 公共包
 *
 *      时  间：  yyyy-mm-dd
 *
 *      版  权：  leonwoo
 *
 ********************************************************************************/


//建立Automation对象
function createObject(strName, strDescription)
{
	try
	{
		var stm = new ActiveXObject(strName);

		return stm;
	}
	catch(e)
	{
		var strMsg = "您的计算机没有安装" + strName;

		if (strDescription)
			strMsg += ", " + strDescription;
		throw strMsg;
	}
}

//判断一个对象是否为数组
function paramIsArray(v)
{
	try
	{
		var a = v[0];
		return typeof(a) != "undefined";
	}
	catch(e)
	{
		return false;
	}
}

//将子节点的值转置为属性
function transformNode(xmlNode, strNewNodeName)
{
	var xmlDoc = createDomDocument("<" + strNewNodeName + "/>");
	var root = xmlDoc.documentElement;

	var node = xmlNode.firstChild;

	while(node)
	{
		root.setAttribute(node.nodeName, node.text);
		node = node.nextSibling;
	}

	return xmlDoc;
}

//将某一个根节点的子节点复制到另一个根节点下
function cloneNodes(rootDest, rootSrc, bRecursive)
{
	var node = rootSrc.firstChild;

	while (node)
	{
		rootDest.appendChild(node.cloneNode(bRecursive));

		node = node.nextSibling;
	}
}

function cloneXmlNode(rootDest, rootSrc, strXPath, bRecursive)
{
	var node = rootSrc.selectSingleNode(strXPath);
	var nodeResult = null;

	if (node)
	{
		nodeResult = rootDest.appendChild(node.cloneNode(bRecursive));
	}

	return nodeResult;
}

//在指定节点上增加一个节点
function appendNode(xmlDoc, root, strNodeName)
{
	var nodeText = "";

	if (arguments.length > 3)
		nodeText = arguments[3];

	var node = xmlDoc.createNode(1, strNodeName, "");

	if (nodeText.toString().length > 0)
		node.text = nodeText;

	root.appendChild(node);

	return node;
}

//在指定节点上增加一个属性
function appendAttr(xmlDoc, node, strAttrName)
{
	var nodeText = "";

	if (arguments.length > 3)
		nodeText = arguments[3];

	var attr = xmlDoc.createAttribute(strAttrName);

	if (nodeText != "")
		attr.value = nodeText;

	node.attributes.setNamedItem(attr);

	return attr;
}

//在指定节点上增加一个属性
function appendCDAT(xmlDoc, node, dataValue)
{
	var nodeText = "";

	var CDATASection = xmlDoc.createCDATASection(dataValue);

	node.appendChild(CDATASection);
	
}


function appendAttri(xmlDoc, node, strAttrName,nodeText)
{

	var attr = xmlDoc.createAttribute(strAttrName);

	if (nodeText != "")
		attr.value = nodeText;

	node.attributes.setNamedItem(attr);

	return attr;
}

//返回一个节点的属性值，如果没有这个属性，则返回空串
function getAttrValue(node, strAttrName)
{
	var attr = node.attributes.getNamedItem(strAttrName);

	if (attr)
		return attr.value;
	else
		return "";
}

//返回一个节点的内容，如果节点为空，则返回缺省值
function getNodeText(node, strDefault)
{
	var strResult = "";

	if (node)
		strResult = node.text;
	else
	if (strDefault)
		strResult = strDefault;

	return strResult;
}

function getNodeAttribute(node, strAttr)
{
	var attrText = node.getAttribute(strAttr);

	if (!attrText)
		attrText = "";

	return attrText;
}

function getSingleNodeText(nodeRoot, strPath, strDefault)
{
	var node = nodeRoot.selectSingleNode(strPath);

	return getNodeText(node, strDefault);
}

var innerXmlHttp = null;
var innerHandleStateChange = null;
var innerParam = null;


/******************************************************
描述：	执行服务器端的程序文件
返回：	成功则返回程序执行结果。失败则返回-1
参数：	strPrgmURL	程序文件路径
        strMethod	发送方法（POST或GET）
        strParamString POST方法时的参数字符串
******************************************************/

var xmlhttp=null;
var resText ="";
var returntableID;
var retdateRefresh;

function ExecServerPrgm_synsh(strPrgmURL, strMethod, strParamString, strUserID, strPwd,tableID,dateRefresh)
{

	returntableID =tableID;
	retdateRefresh =dateRefresh;

	xmlhttp = new ActiveXObject("Msxml2.XmlHttp");

    if (strUserID != "" && strUserID != null){
        xmlhttp.Open(strMethod, strPrgmURL, true, strUserID, strPwd);
		if (arguments.length == 7)
			xmlhttp.onreadystatechange=readystatechange;
	}
    else{

		xmlhttp.Open(strMethod, strPrgmURL, true);
		if (arguments.length == 7)
			xmlhttp.onreadystatechange=readystatechange;

	}
    if (strMethod.toUpperCase() == 'POST'){
       xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

	   xmlhttp.Send(strParamString);
    }else{
        xmlhttp.Send();
    }
	//alert(xmlhttp.readyState);




	return resText;
}

function readystatechange(){

	if (xmlhttp.readyState == 4)
	{
		//alert(xmlhttp.responseText);
		retdateRefresh(returntableID,unescape(xmlhttp.responseText));

	}

}


function ExecServerPrgm(strPrgmURL, strMethod, strParamString, strUserID, strPwd)
{


	xmlhttp = new ActiveXObject("Msxml2.XmlHttp");

    if (strUserID != "" && strUserID != null){
        xmlhttp.Open(strMethod, strPrgmURL, false, strUserID, strPwd);
	}
    else{
		xmlhttp.Open(strMethod, strPrgmURL, false);

	}
    if (strMethod.toUpperCase() == 'POST'){
       xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

	   xmlhttp.Send(strParamString);
    }else{
        xmlhttp.Send();
    }


	if ( xmlhttp.status == 200 ) {
	
        resText = unescape(xmlhttp.responseText);
    } else {

        resText = xmlhttp.status;
    }



	return resText;
}


//建立xmlDocument对象
function createDomDocument()
{
	var xmlData;

	try
	{
		xmlData = createObject("Msxml2.DOMDocument");
	}
	catch(e)
	{
		xmlData = createObject("Msxml.DOMDocument");
	}

	xmlData.async = false;

	if (arguments.length > 0)
		xmlData.loadXML(arguments[0]);

	return xmlData;
}




