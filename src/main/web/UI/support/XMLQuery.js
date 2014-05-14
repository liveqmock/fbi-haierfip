/**
 * 
 */
function XMLQuery( )
{
	this.returnStr     = "";
	this.fieldName	= "";
	this.fieldType	= "";
	this.fieldValue	= "";

	this.success	= false;

	this.actionCount = 0;
	this.recordCount = 0;

	this.curAction = -1;
	this.curRecord = -1;

	this.resultXMLDoc = createDomDocument();
	this.actionXMLDoc = createDomDocument();

	this.actionResult = false;
};

XMLQuery.prototype.init = XMLQuery_init;
XMLQuery.prototype.dispose = XMLQuery_dispose;
XMLQuery.prototype.addField = XMLQuery_addField;
XMLQuery.prototype.query = XMLQuery_query;
XMLQuery.prototype.query_async = XMLQuery_query_async;
XMLQuery.prototype.queryForm = XMLQuery_queryForm;
XMLQuery.prototype.setReturnStr = XMLQuery_setReturnStr;
XMLQuery.prototype.isSuccess = XMLQuery_isSuccess;
XMLQuery.prototype.selectAction = XMLQuery_selectAction;
XMLQuery.prototype.getFieldValue = XMLQuery_getFieldValue;
XMLQuery.prototype.getActionResult = XMLQuery_getActionResult;

function XMLQuery_init(actionName,methodName)
{
};

function XMLQuery_dispose()
{
	if (this.xmlDoc!=null)
	{
		this.xmlDoc.free;
		this.xmlDoc=null;
	}
	if (this.actionXMLDoc!=null)
	{
		this.actionXMLDoc.free;
		this.actionXMLDoc=null;
	}
	if (this.resultXMLDoc!=null)
	{
		this.resultXMLDoc.free;
		this.resultXMLDoc=null;
	}
};

function XMLQuery_addField( name, type, value )
{
	this.fieldName+=","+encode(name);
	this.fieldType+=","+encode(type);
	this.fieldValue+=","+encode(value);
};

function XMLQuery_isSuccess()
{
	return this.success;
};

function XMLQuery_query( actionName,methodName )
{
	var xmlDoc = createDomDocument("<root/>");
	var rootNode = xmlDoc.documentElement;

	var actionNode = appendNode(xmlDoc, xmlDoc.documentElement, "action");
	appendAttri(xmlDoc, actionNode, "actionname",actionName);
	if (arguments.length == 2)
	   	appendAttri(xmlDoc, actionNode, "methodname",methodName);

	var recordNode = appendNode(xmlDoc, xmlDoc.documentElement, "record");
	appendAttri(xmlDoc, recordNode, "type", "insert");

	var nameArr=this.fieldName.split(",");
	var typeArr=this.fieldType.split(",");
	var valueArr=this.fieldValue.split(",");
	var nFieldCount=nameArr.length;
	for( var i=0; i<nFieldCount; i++ )
	{
		var fieldNode = createFieldNode(xmlDoc, decode(nameArr[i]), decode(typeArr[i]), decode(valueArr[i]));
		recordNode.appendChild(fieldNode);
	}
	actionNode.appendChild(recordNode);
	rootNode.appendChild(actionNode);

	var retStr = ExecServerPrgm("/BI/util/SqlExcuteJsp.jsp","POST","sys_request_xml="+xmlDoc.xml);
	xmlDoc.free;	xmlDoc=null;
	this.setReturnStr( retStr );
};

function XMLQuery_query_async( actionName,methodName )
{
	var xmlDoc = createDomDocument("<root/>");
	var rootNode = xmlDoc.documentElement;

	var actionNode = appendNode(xmlDoc, xmlDoc.documentElement, "action");
	appendAttri(xmlDoc, actionNode, "actionname",actionName);
	if (arguments.length == 2)
	   	appendAttri(xmlDoc, actionNode, "methodname",methodName);

	var recordNode = appendNode(xmlDoc, xmlDoc.documentElement, "record");
	appendAttri(xmlDoc, recordNode, "type", "insert");

	var nameArr=this.fieldName.split(",");
	var typeArr=this.fieldType.split(",");
	var valueArr=this.fieldValue.split(",");
	var nFieldCount=nameArr.length;
	for( var i=0; i<nFieldCount; i++ )
	{
		var fieldNode = createFieldNode(xmlDoc, decode(nameArr[i]), decode(typeArr[i]), decode(valueArr[i]));
		recordNode.appendChild(fieldNode);
	}
	actionNode.appendChild(recordNode);
	rootNode.appendChild(actionNode);
	//var tmpHTML = ExecServerPrgm_synsh("/BI/util/SqlExcuteJsp.jsp","POST","sys_request_xml="+xmlDoc.xml,"","","", test);
	var tmpHTML = ExecServerPrgm_synsh("/BI/util/SqlExcuteJsp.jsp","POST","sys_request_xml="+xmlDoc.xml,"","","", XMLQuery_ReadyStateChange);
	xmlDoc.free;	xmlDoc=null;
	this.success=true;
}

function XMLQuery_ReadyStateChange()
{
	if (xmlhttp.readyState == 4)
	{
		window.status=new Date()+unescape(xmlhttp.responseText);
	}
	window.status=new Date()+unescape(xmlhttp.responseText)+"异步过程处理完毕";
	alert( unescape(xmlhttp.responseText) );
}

function XMLQuery_queryForm( formName, actionName, methodName )
{
	var xmlDoc = createDomDocument("<root/>");
	createupdate( formName, xmlDoc, actionName, methodName );
	if (xmlDoc.documentElement.childNodes.length >0 )
	retStr = ExecServerPrgm("/BI/util/SqlSelectJsp.jsp","POST","sys_request_xml="+xmlDoc.xml);
	xmlDoc.free;	xmlDoc=null;
	this.setReturnStr( retStr );
};

function XMLQuery_setReturnStr( retStr )
{
	this.returnStr=retStr;
	var retValue=analyzeReturnXML( retStr );
	if (retValue=="false")
	{
		this.success=false;
	}
	else
	{
		this.resultXMLDoc.loadXML( retStr );
		this.actionCount=this.resultXMLDoc.documentElement.childNodes.length;
		if (this.actionCount>0)
		{
			this.selectAction(0);
			this.success=true;
		}
		else
		{
			this.success=false;	//没有action，认为有问题
		}
	}
}

function XMLQuery_selectAction( actNum )
{
	if (actNum<0 || actNum>=this.actionCount)
	{
		alert( "选择的Action编号不存在！\nAction总数为："+this.actionCount+"　选择的编号为："+actNum );
		return false;
	}
	this.curAction=actNum;
	this.actionXMLDoc.loadXML( this.resultXMLDoc.documentElement.childNodes[actNum].xml );
	this.recordCount=this.actionXMLDoc.documentElement.childNodes.length;
	this.actionResult=this.actionXMLDoc.documentElement.getAttribute("result");
	return true;
};

function XMLQuery_getActionResult( actNum )
{
	if (arguments.length==0)
		return this.actionResult;
};

function XMLQuery_getFieldValue( recNum, fieldName )
{
	if (recNum<0 || recNum>=this.recordCount)
	{
		alert( "选择的Record编号不存在！\nRecord总数为："+this.recordCount+"　选择的编号为："+recNum );
		return "";
	}
	var recordNodes=this.actionXMLDoc.documentElement.childNodes;
	var fieldCount=recordNodes[recNum].childNodes.length;
	for( var i=0; i<fieldCount; i++ )
	{
		var node=recordNodes[recNum].childNodes[i];
		if (node.getAttribute("name")==fieldName)
		{
			return decode(node.getAttribute("value"));
		}
	}
	return "";
};