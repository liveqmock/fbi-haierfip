////////////////////////////////////////////////////////////////////////////
//Begin Basic Function
/**
 * disable一个按钮
 */
function disableButton( btnName )
{
	var btnObj=document.getElementById( btnName );
	if (btnObj==undefined || btnObj==null)
		return;
	btnObj.disabled=true;
}

function enableButton( btnName )
{
	var btnObj=document.getElementById( btnName );
	if (btnObj==undefined || btnObj==null)
		return;
	btnObj.disabled=false;
}

function hideButton( btnName )
{
	var btnObj=document.getElementById( btnName );
	if (btnObj==undefined || btnObj==null)
		return;
	btnObj.style.display="none";
}

function showButton( btnName )
{
	var btnObj=document.getElementById( btnName );
	if (btnObj==undefined || btnObj==null)
		return;
	btnObj.style.display="inline-block";
}

function setButtonCaption( btnName, sCaption )
{
	var btnObj=document.getElementById( btnName );
	if (btnObj==undefined || btnObj==null)
		return;
	btnObj.value=sCaption;
}

function getItemString( itemName )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return "";
	switch( itemObj.tagName.toUpperCase() )
	{
		case "INPUT":
			var fieldType=itemObj.fieldtype;
			if (fieldType!=undefined && fieldType=="select")
				return itemObj.attr;
			var itemType=itemObj.type.toUpperCase();
			if (itemType=="RADIO")
			{
				var radios=document.getElementsByName(itemName);
				for (var i=0; i<radios.length; i++ )
					if (radios[i].checked==true) return radios[i].value;
				return "";
			}
			else if (itemType=="CHECKBOX")
			{
				if (itemObj.checked==true) return itemObj.value;
				else	return "";
			}
			else
				return itemObj.value;
		case "TEXTAREA":
		case "SELECT":
			return itemObj.value;
		case "DIV":
		case "TD":
			return itemObj.innerText;
	}
}

function getItemExpand( itemName, attrName )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return "";

	var expandAttr;
	if (arguments.length==1)
		expandAttr="expand";
	else
		expandAttr=attrName;

	switch( itemObj.tagName.toUpperCase() )
	{
		case "SELECT":
			if (itemObj.selectedIndex==-1) return "";
			else return itemObj.options(itemObj.selectedIndex).getAttribute(expandAttr);
			break;
		default:
			return itemObj.getAttribute(expandAttr);
			break;
	}
}


function setItemString(itemName, itemValue)
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	switch( itemObj.tagName.toUpperCase() )
	{
		case "INPUT":
			if (itemObj.fieldtype!=undefined && itemObj.fieldtype=="select")	//新增下拉框控件
			{
				setValueSelect( itemName, itemValue );
				return;
			}
			var itemType=itemObj.type.toUpperCase();
			if (itemType=="RADIO")
			{
				var radios=document.getElementsByName(itemName);
				for (var i=0; i<radios.length; i++ )
				{
					if (radios[i].value==itemValue)	radios[i].checked=true;
					else radios[i].checked=false;
				}
			}
			else if (itemType=="CHECKBOX")
			{
				var checks=document.getElementsByName(itemName);
				for (var i=0; i<checks.length; i++ )
				{
					if (checks[i].value==itemValue)
					{
						checks[i].checked=true;
						break;
					}
				}
			}
			else
				itemObj.value=itemValue;
			break;
		case "TEXTAREA":
		case "SELECT":
			itemObj.value=itemValue;
			break;
		case "DIV":
		case "TD":
			itemObj.innerText=itemValue;
			break;
	}
	if (itemObj.dbgridid == undefined) return;
	ElementFillGrid( itemObj );
}

//根据现实的内容（text）而不是值（value）设置下拉框的被选中项目
function setSelectText(itemName, itemText)
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	switch( itemObj.tagName.toUpperCase() )
	{
		case "SELECT":
			var optionsArr=itemObj.options;
			for( var i=0; i<optionsArr.length; i++)
			{
				if (itemText==optionsArr[i].text)
				{
					itemObj.value=optionsArr[i].value;
					break;
				}
			}
			break;
		default:
			alert( "本函数只处理Select控件！" );
			return;
			break;
	}
	if (itemObj.dbgridid == undefined) return;
	ElementFillGrid( itemObj );
}

function setFocus( itemName )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	try
	{
		itemObj.focus();
	}
	catch( e )
	{
	}
}

function showItem( itemName )
{	showButton( itemName );	}

function hideItem( itemName )
{	hideButton( itemName );	}

function disableItem( itemName )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	switch( itemObj.tagName.toUpperCase() )
	{
		case "INPUT":
			var itemType=itemObj.type.toUpperCase();
			if (itemType=="RADIO")
			{
				var radios=document.getElementsByName(itemName);
				for (var i=0; i<radios.length; i++ )
					radios[i].disabled=true;
			}
			else
				itemObj.disabled=true;
			break;
		default:
			itemObj.disabled=true;
			break;
	}
}

function enableItem( itemName )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	switch( itemObj.tagName.toUpperCase() )
	{
		case "INPUT":
			var itemType=itemObj.type.toUpperCase();
			if (itemType=="RADIO")
			{
				var radios=document.getElementsByName(itemName);
				for (var i=0; i<radios.length; i++ )
				{
					radios[i].disabled=false;
				}
			}
			else
				itemObj.disabled=false;
			break;
		default:
			itemObj.disabled=false;
			break;
	}
}

function disableItemAll()
{
	var forms=document.forms;
	for( var j=0; j<forms.length; j++)
	{
		var elementsArr=forms(j).elements;
		for( i=0; i<elementsArr.length; i++ )
		{
			elementObj=elementsArr.item(i);
			disableItem( elementObj.id );
		}
	}
}

function enableItemAll()
{
	var forms=document.forms;
	for( var j=0; j<forms.length; j++)
	{
		var elementsArr=forms(j).elements;
		for( i=0; i<elementsArr.length; i++ )
		{
			elementObj=elementsArr.item(i);
			enableItem( elementObj.id );
		}
	}
}

function enableItemNull(itemName)
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	itemObj.isNull="true";
}

function disableItemNull(itemName)
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	itemObj.isNull="false";
}

function enableInputOption( itemName )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	if (itemObj.tagName.toLowerCase()=="input" && itemObj.fieldtype=="select")
		itemObj.canInputOption="true";
}


function disableInputOption( itemName )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	if (itemObj.tagName.toLowerCase()=="input" && itemObj.fieldtype=="select")
		itemObj.canInputOption="false";
}

function addOption( itemName, value, label )
{
	if (existOption( itemName, value )) return;
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return;
	if (itemObj.tagName.toUpperCase()!="SELECT" )
		return;
	var newOption=document.createElement("OPTION");
	newOption.text=label;
	newOption.value=value;
	itemObj.add(newOption);
}

function existOption( itemName, value )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
		return false;
	if (itemObj.tagName.toUpperCase()!="SELECT" )
		return false;
	var options=itemObj.options;
	for (var i=0; i<options.length; i++)
		if (options(i).value==value) return true;
	return false;
}

function deleteOption( itemName, value )
{
	if (!existOption( itemName, value )) return;
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null) return;
	var options=itemObj.options;
	for (var i=0; i<options.length; i++)
		if (options(i).value==value) itemObj.remove(i);
}

function setOnChangeHandler( itemName, handler )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
	{
		alert( "不存在的字段：" + itemName );
		return;
	}
	if (typeof(handler)=="function")
		itemObj.onchange=handler;
	else if (typeof(handler)=="string")
		itemObj.onchange=eval(handler);
	else
	{
		alert( "无效的处理函数句柄" );
		return;
	}
}

function setEventHandler( itemName, event, handler )
{
	var itemObj=document.getElementById(itemName);
	if (itemObj==undefined || itemObj==null)
	{
		alert( "不存在的字段：" + itemName );
		return;
	}
	if (typeof(handler)=="string")
		eval("document.getElementById(itemName)."+event+"=eval(txnhandler);");
	else
	{
		alert( "无效的处理函数句柄" );
		return;
	}
}

function CurrentDate()
{
	var date=new Date();
	var years = date.getFullYear();
	var months = date.getMonth()+1;
	var days = date.getDate();

	if (months < 10) months = "0" + months;
	if (days < 10) days = "0" + days;

	return years+ "-" + months + "-" + days;
}

function CurrentTime()
{
	var date=new Date();
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	var Milliseconds = date.getMilliseconds();

	if (hours < 10) hours = "0" + hours;
	if (minutes < 10) minutes = "0" + minutes;
	if (seconds < 10) seconds = "0" + seconds;
	if (Milliseconds<10) Milliseconds = "00" + Milliseconds;
	else if (Milliseconds<100) Milliseconds = "0" + Milliseconds;

	return hours + ":" + minutes + ":" + seconds + ":" + Milliseconds;
}

function openWindow( URL, width, height )
{
	var li_height, li_width;
	if (width ==undefined) li_width =800;
	else li_width=width;
	if (height==undefined) li_height=600;
	else li_height=height;
	var timeStamp=CurrentDate()+"-"+CurrentTime();
	if (URL.indexOf("?")!=-1)
		URL=URL+"&timestamp="+timeStamp;
	else
		URL=URL+"?timestamp="+timeStamp;
	var retArr=showModalDialog(URL, "", "dialogWidth:"+li_width+"px;dialogHeight="+li_height+"px;status:no;scroll:no;resizable:yes;");
	return retArr;
}

function callFunction( funcName, param )
{
	if (eval("typeof("+funcName+")")=="function")
	{
		var paramstr="";
		if (arguments.length == 2)
			for(i=0; i<param.length; i++)
			{
				if (i==0) paramstr="param["+i+"]";
				else paramstr=paramstr+",param["+i+"]";
			}
		eval("result="+funcName+"("+paramstr+");");
		return result;
	}
	else
	{
		alert( "不存在的函数调用！"+funcName );
		return false;
	}
}

//初始化设置Enter到Tab键转换的程序
function initEnter2Tab()
{
	var formsArr=document.forms;
	for( var i=0; i<formsArr.length; i++)
	{
		body_init(formsArr[i]);
	}
}
//End Basic Function
////////////////////////////////////////////////////////////////////////////


//计算一个字符串的hashCode值
//应该得出的值与Java计算的值一样
function hashCode( sVal )
{
	var MAX_INT_POSITIVE = 4294967296; // 2^32
	var MAX_INT = 2147483648;  // 2^31
	var hash=0;
	var nCount=sVal.length;
	for( var i=0; i<nCount; i++)
	{
		hash = 31*hash + sVal.charCodeAt(i);
		while (Math.abs(hash)>MAX_INT && hash>0)
		{
			hash=hash-MAX_INT_POSITIVE;
		}
		while (Math.abs(hash)>MAX_INT && hash<0)
		{
			hash=hash + MAX_INT_POSITIVE;
		}
	}
	return hash;
}

//////////////////////////Message Dialog Function//////////////////////////
//var _dialogWin;
function createMessageDialog(text)
{
	if (!text) text="正在下载数据...";
	if (typeof(_messageDialog)=="undefined")
		document.body.insertAdjacentHTML("beforeEnd", "<marquee id=_messageDialog isvisible=1  scrollamount=3 behavior=alternate direction=right"+
			" style=\"position: absolute; visibility: hidden; width: 300; height: 22; background-color: #d4d0c8; font-size: 12; border: 1 solid silver; padding-top:3; z-index: 10000; filter:alpha(opacity=70)\"></marquee>");
	_messageDialog.innerHTML=text;
}

function showMessageDialog(text, center)
{
	if (center==undefined) center=true;
	createMessageDialog(text);
	if (!_messageDialog) return;
	//_dialogWin=showModelessDialog("<%=contextPath%>/UI/cims/qydj/messageDialog.htm",text,"dialogWidth:300px;dialogHeight:50px;help:no;scroll:no;status:no;center:yes;");
	_messageDialog.style.visibility="visible";
	if (center)
	{
		_messageDialog.style.posLeft=(document.body.clientWidth - _messageDialog.offsetWidth) / 2;
		_messageDialog.style.posTop=(document.body.clientHeight - _messageDialog.offsetHeight) / 2;
	}
}

function hideMessageDialog()
{
	if (!_messageDialog) return;
	_messageDialog.style.visibility="hidden";
	//_dialogWin.closeWindow();
	//_dialogWin=null;
}

function showMessage( v )
{
	showMessageDialog(v,true);
}

function hideMessage()
{
	hideMessageDialog();
}
//////////////////////////Message Dialog Function//////////////////////////


/////////////////////// DBGrid Function //////////////////////////////
function DBGrid_insertRow( tab, rowNum )
{
	if (rowNum==undefined)	rowNum=tab.rows.length-1;
	tab.activeIndex=rowNum;
	appendRecord( tab );
	initDBGrid( tab.id );
	return tab.activeIndex;
}

function DBGrid_deleteRow( tab, rowNum )
{
	if (rowNum==undefined)
		rowNum=tab.activeIndex;
	tab.activeIndex=rowNum;
	deleteRecord(tab, null, "");
	initDBGrid( tab.id );
}

function DBGrid_editRow( tab, rowNum )
{
	if (rowNum==undefined)
		rowNum=tab.activeIndex;
	tab.activeIndex=rowNum;
	editRecord(tab);
	initDBGrid( tab.id );
}

function DBGrid_cancelRow( tab, rowNum )
{
	if (rowNum==undefined)
		rowNum=tab.activeIndex;
	tab.activeIndex=rowNum;
	cancelRecord(tab, null);
	initDBGrid( tab.id );
}

/////////////////////// DBGrid Function //////////////////////////////

/////////////////////// ComboBox Function ////////////////////////////
//在页面中创建需要的ComboBox对象，用于DataWindow中在控制页面创建对象
function ComboBox_Create( id )
{
	var combo=new ComboBox(id);
	return combo;
}

//为新的下拉框控件刷新内容
function refreshSelect(objName, sqlStr, defValue)
{
	var obj=document.getElementById(objName);
	if (obj==undefined || obj==null ) return;

	//属性不对，返回
	if (obj.tagName.toLowerCase()!="input" || obj.fieldtype!="select") return;

	obj.sqlStr=sqlStr;
	if (arguments.length==3)
		obj.defvalue=defValue;
	else
		obj.defvalue=obj.oldvalue;
	//obj.comboBox = new ComboBox(obj.id);
	obj.comboBox.DataRefresh();
}

//对新增的控件设置值，并显示内容
function setValueSelect( objName, value )
{
	var obj=document.getElementById(objName);
	if (obj==undefined || obj==null ) return;

	//属性不对，返回
	if (obj.tagName.toLowerCase()!="input" || obj.fieldtype!="select") return;

	obj.attr=value;
	//刷新内容
	obj.comboBox = new ComboBox(obj.id);
  	obj.comboBox.focusDefaultValue(value);
}

//刷新企业类型下拉框的代码
function refreshSelect_QYLX( qylbColName, qylxColName )
{
	var objQYLB=document.getElementById(qylbColName);
	if (objQYLB==null || objQYLB==undefined )
	{
		alert( "指定名字的字段不存在，请检查程序是否正确！字段名："+qylbColName );
		return;
	}
	var objQYLX=document.getElementById(qylxColName);
	if (objQYLX==null || objQYLX==undefined )
	{
		alert( "指定名字的字段不存在，请检查程序是否正确！字段名："+qylxColName );
		return;
	}
	if (objQYLX.tagName.toLowerCase()=="input" && objQYLX.fieldtype=="select")
		refreshSelect(qylxColName,"select ENUITEMVALUE as value, ENUITEMLABEL as text, ENUITEMEXPAND as expand, ENUITEMDESC as describe from PTENUDETAIL where enutype='QYLX' AND ENUITEMDESC='"+getItemString(qylbColName)+"' order by dispno",objQYLX.oldvalue);
	else
		refresh_select(qylxColName,"select ENUITEMVALUE as value ,ENUITEMVALUE || '-' || ENUITEMLABEL as text ,ENUITEMEXPAND as expand, ENUITEMDESC as describe from PTENUDETAIL where enutype='QYLX' AND ENUITEMDESC='"+getItemString(qylbColName)+"' order by dispno",objQYLX.oldvalue, "企业类型");
}

//刷新企业类型下拉框的代码
function refreshSelect_GPGSS( gxdwColName, gpgssColName )
{
	var objGXDW=document.getElementById(gxdwColName);
	if (objGXDW==null || objGXDW==undefined )
	{
		alert( "指定名字的字段不存在，请检查程序是否正确！字段名："+gxdwColName );
		return;
	}
	var objGPGSS=document.getElementById(gpgssColName);
	if (objGPGSS==null || objGPGSS==undefined )
	{
		alert( "指定名字的字段不存在，请检查程序是否正确！字段名："+gpgssColName );
		return;
	}
	var ls_GXDW=getItemString("GXDW");
	var sqlStr="";
	if (objGPGSS.tagName.toLowerCase()=="input" && objGPGSS.fieldtype=="select")
	{
		if (ls_GXDW.length==6 && ls_GXDW.substr(4,2)=="00")
			sqlStr="SELECT DEPTID AS value, DEPTNAME as text FROM ptdept where mkdm='99' order by deptid";
		else
			sqlStr="SELECT DEPTID AS value, DEPTNAME as text FROM ptdept where mkdm='99' and fillstr10='"+ls_GXDW+"' order by deptid";

		refreshSelect(gpgssColName,sqlStr,objGPGSS.oldvalue);
	}
	else
	{
		if (ls_GXDW.length==6 && ls_GXDW.substr(4,2)=="00")
			sqlStr="SELECT DEPTID AS value,subStr(DEPTID,9,11) || '-' || DEPTNAME as text FROM ptdept where mkdm='99' order by deptid";
		else
			sqlStr="SELECT DEPTID AS value,subStr(DEPTID,9,11) || '-' || DEPTNAME as text FROM ptdept where mkdm='99' and fillstr10='"+ls_GXDW+"' order by deptid";
		refresh_select(gpgssColName,sqlStr,objGPGSS.oldvalue, "管片工商所");
	}
}

//刷新行业代码下拉框的代码
function refreshSelect_HYDM( hymlColName, hydmColName )
{
	var objHYML=document.getElementById(hymlColName);
	if (objHYML==null || objHYML==undefined )
	{
		alert( "指定名字的字段不存在，请检查程序是否正确！字段名："+hymlColName );
		return;
	}
	var objHYDM=document.getElementById(hydmColName);
	if (objHYDM==null || objHYDM==undefined )
	{
		alert( "指定名字的字段不存在，请检查程序是否正确！字段名："+hydmColName );
		return;
	}
	if (objHYDM.tagName.toLowerCase()=="input" && objHYDM.fieldtype=="select")
		refreshSelect(hydmColName,"select ENUITEMVALUE as value ,ENUITEMLABEL as text ,ENUITEMEXPAND as expand, ENUITEMDESC as describe from PTENUDETAIL where enutype='HYDM' AND ENUITEMEXPAND='"+getItemString(hymlColName)+"' order by dispno",objHYDM.oldvalue);
	else
		refresh_select(hydmColName,"select ENUITEMVALUE as value ,ENUITEMVALUE || '-' || ENUITEMLABEL as text ,ENUITEMEXPAND as expand, ENUITEMDESC as describe from PTENUDETAIL where enutype='HYDM' AND ENUITEMEXPAND='"+getItemString(hymlColName)+"' order by dispno",objHYDM.oldvalue, "行业代码");
}
/////////////////////// ComboBox Function ////////////////////////////


//////////////////////////////////////////////////////////////////////
function showHelp( idHelp )
{
	var URL="";
	switch( idHelp )
	{
		case "sl":
		case "hz":
			URL="<%=contextPath%>/UI/cims/qydj/help/help_slhz.htm";
			break;
		case "yd":
		case "rd":
			URL="<%=contextPath%>/UI/cims/qydj/help/help_ydrd.htm";
			break;
	}
	window.open(URL);
}


function czjl( BH, NBXH, YWLX, OPERATION )
{
	var xq=new XMLQuery();
	xq.addField("BH","text",BH);
	xq.addField("NBXH","text",NBXH);
	xq.addField("YWLX","text",YWLX);
	xq.addField("OPERATION","text",OPERATION);
	xq.query("qylcsa","czjl_save");
	var retValue=xq.isSuccess();
	xq.dispose();
	xq=null;
	return retValue;
}


function rlxx( BH )
{
	var xq=new XMLQuery();
	xq.addField("BH","text",BH);
	xq.query("qyrl","rl");
	var retValue=xq.isSuccess();
	xq.dispose();
	xq=null;
	return retValue;
}

