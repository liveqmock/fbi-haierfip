
//**************************************************//
//该文件是本系统基础公共文件，任何人不得擅自更改			//
//若有更改与本人联系：公共包					        //
//**************************************************//

/********************************************************************************
 *
 *      文件名： basic.js
 *
 *      作  用：  系统基础的调用方法。
 *
 *      作  者： 公共包
 *
 *      时  间：  yyyy-mm-dd
 *
 *      版  权：  leonwoo
 *
 ********************************************************************************/
//navigator.userProfile.clearRequest();

///字符串加码
function encode(strIn)
{

	if(strIn ==undefined)
		return "";

	var intLen=strIn.length;
	var strOut="";
	var strTemp;
	var strFlag="";

	for(var i=0;i<intLen;i++){
		strTemp=strIn.charCodeAt(i);

		if (strTemp>255 || strTemp<0){
			if (strFlag==""){
				strFlag="#";
				strOut=strOut+"[#"+fillLeftWithZero(strTemp.toString(16),4);
			}
			else if (strFlag=="~"){
				strFlag="#";
				strOut=strOut+"#"+fillLeftWithZero(strTemp.toString(16),4);
			}
			else if (strFlag=="#"){
				strOut=strOut+fillLeftWithZero(strTemp.toString(16),4);
			}
		}
		else{
			if (strTemp < 48 || (strTemp > 57 && strTemp < 65) || (strTemp > 90 && strTemp < 97) || strTemp > 122){
				if (strFlag==""){
					strFlag="~";
					strOut=strOut+"[~"+fillLeftWithZero(strTemp.toString(16),2);
				}
				else if (strFlag=="#"){
					strFlag="~";
					strOut=strOut+"~"+fillLeftWithZero(strTemp.toString(16),2);
				}
				else if (strFlag=="~"){
					strOut=strOut+fillLeftWithZero(strTemp.toString(16),2);
				}
			}
			else{
				if (strFlag=="#" || strFlag=="~"){
					strFlag="";
					strOut=strOut+"]"+strIn.charAt(i);
				}
				else{
					strOut=strOut+strIn.charAt(i);
				}
			}
		}
	}
	return (strOut);
}


/////字符串解码
function decode(strIn)
{
	if(strIn ==undefined)
		return "";

	var intLen=strIn.length;
	var strOut="";
	var strTemp;
	var strFlag="";

	for(var i=0;i<intLen;i++)
	{
		strTemp=strIn.charAt(i);

		if (strTemp=="["){
			i++;
			strTemp=strIn.charAt(i);
		}
		if (strTemp=="]"){
			strFlag="";
			continue;
		}
		if (strTemp=="~"){
			i++;
			strFlag="~";
		}
		if (strTemp=="#"){
			i++;
			strFlag="#";
		}
		switch (strFlag){
			case "~":{
				strTemp=strIn.substring(i,i+2);
				strTemp=parseInt(strTemp,16);
				strTemp=String.fromCharCode(strTemp);
				strOut=strOut+strTemp;
				i++;
				break;
			}
			case "#":{
				strTemp=strIn.substring(i,i+4);
				if (strTemp.toUpperCase()=="FFFF"){
					i+=4;
					strTemp=strIn.substring(i,i+4);
				}
				strTemp=parseInt(strTemp,16);
				strTemp=String.fromCharCode(strTemp);
				strOut=strOut+strTemp;
				i+=3;
				break;
			}
			case "":{
				strOut=strOut+strTemp;
				break;
			}
		}
	}
	return (strOut);
}

/////根据长度右边添加0
function fillLeftWithZero(strIn, outLen){
	if (typeof(strIn)!="string") return strIn;

	for (var i=strIn.length;i<outLen;i++)
		strIn="0"+strIn;

	return strIn;
}

/////去掉左边控格
function LTrim(str){
	if ((typeof(str) != "string") || !str) return "";
	for(var i=0; i<str.length; i++){if (str.charCodeAt(i, 10)!=32) break;}
	return str.substring(i, str.length);
}

//去掉右边控格
function RTrim(str){
	if ((typeof(str) != "string") || !str) return "";
	for(var i=str.length-1; i>=0; i--){if (str.charCodeAt(i, 10)!=32) break;}
	return str.substr(0, i+1);
}

//去掉控格
function trimStr(str){
	if ((typeof(str) != "string") || !str) return "";
	return LTrim(RTrim(str));
}



//得到某个元素的起始点的X坐标
function absLeft(ele)
{
	var e = ele, left = 0;

	while(e.tagName != "BODY")
	{
		left += e.offsetLeft;
		e = e.offsetParent;
	}

	return left;
}

//得到某个元素的起始点的Y坐标
function absTop(ele)
{
	var e = ele,top = 0;

	while(e.tagName != "BODY")
	{
		top += e.offsetTop;
		e = e.offsetParent;
	}

	return top;
}



//如果某个输入域为整数，那么过滤掉所有非数字字符
function onKeyPressInputInteger()
{
	var nKey = window.event.keyCode;


	if ((nKey < 48 && nKey!=13 && nKey!=3 ) || nKey > 57 ){

		window.event.keyCode = 0;
	}

}

//如果某个输入域为实数，那么过滤掉所有非数字字符（包含小数点）
function onKeyPressInputFloat()
{
	var nKey = window.event.keyCode;

	if (nKey > 57 || (nKey != 46 && nKey != 45 && nKey < 48))
		window.event.keyCode = 0;
}


///输入正确的日期

function onKeyPressCalendar()
{
	
	
	var nKey = window.event.keyCode;

	if (nKey > 57 || (nKey != 45 && nKey < 48))
		window.event.keyCode = 0;
		
	CalendarMaker(event.srcElement);
}

///过滤所有的非电话输入项

function onKeyPressPhone()
{
	var nKey = window.event.keyCode;
	//alert(nKey);
	//if (nKey > 57 || (nKey != 45 && nKey < 48&&nKey != 41&&nKey != 40))
		//window.event.keyCode = 0;
}
//////////////////////////////////////////////////////////////////////////////////////

//////比较两个时间短
function comparDate(Date1,Date2){


	var DateArr1= Date1.split("-");

	var DateArr2= Date2.split("-");

	if (DateArr1[0]/1 > DateArr2[0]/1)
		return true;
	else if (DateArr1[0]/1  < DateArr2[0]/1)
		return false;

	else {

		if (DateArr1[1]/1 > DateArr2[1]/1)
			return true;
		else if (DateArr1[1]/1 < DateArr2[1]/1)
			return false;
		else {

			if (DateArr1[2]/1 <= DateArr2[2]/1)
				return false;
			else
				return true;


		}


	}

}




//控件按钮鼠标移进
function button_onmouseover(){
	try{
		if (event.srcElement.disabled) return;
		event.srcElement.className="buttonGrooveActive";
	}
	catch(e){

	}
}
//控件按钮鼠标移出
function button_onmouseout(){
	try{
		if (event.srcElement.disabled) return;
		event.srcElement.className="buttonGrooveDisable";
	}
	catch(e){

	}
}

////////////////////////////////////////////////////////DBGRID////////////////////////////////////////////////////////////////////////
/////获取对象的TD
function getOwnerTD(element)
{
	while (element.tagName.toUpperCase() != "TD" && element.tagName.toUpperCase() != "TH")
	{
		element = element.parentNode;
		if (element == null)
			break;
	}

	return(element);
}


/////获取对象TR
function getOwnerTR(element)
{
	try{
		while (element.tagName.toUpperCase() != "TR")//行 TD单元格 TH表头
		{
			element = element.parentNode;

			if (element == null)
				break;
		}

		return(element);
	}
	catch(e){
		return(element);
	}
}


/////获取对象TBODY
function getOwnerBody(element)
{
	var tag = element.tagName.toUpperCase();

	while (tag != "TABLE" && tag != "TBODY" && tag != "TH" && tag != "THEAD" && tag != "TFOOT")
	{
		element = element.parentNode;

		if (element == null)
			break;

		tag = element.tagName.toUpperCase();
	}

	return(element);
}

//////获取对象Table
function getOwnerTable(element)
{
	while (element.tagName.toUpperCase() != "TABLE")//行 TD单元格 TH表头
	{
		element = element.parentNode;

		if (element == null)
			break;
	}

	return(element);
}


//检查TD的内容不能为空
function checkTDNull(element)
{

	var value = RTrim(element.innerText);

	if (value.length == 0){
		TRMove(getOwnerTR(element));
		alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+" '中的值不能为空");

		return "false";
	}
	return "true";
}

//检查TD的内容长度
function checkTDStrLength(element,StrLength){
	var value = RTrim(element.innerText);

	if (value.length >StrLength*1 ){
		TRMove(getOwnerTR(element));

		alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+" '中的值的长度不能大于" + StrLength);

		return "false";
	}
	return "true";

}
//检查TD的内容长度
function checkTDStrMinLength(element,StrLength){
	var value = RTrim(element.innerText);

	if (value.length < StrLength*1 ){
		TRMove(getOwnerTR(element));
		alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+" '中的值的长度不能小于" + StrLength);

		return "false";
	}
	return "true";

}


//检查TD内容的int的长度
function checkTDIntLength(element,IntLength){

	var value = RTrim(element.innerText);

	for (var j = 0; j<  value.length; j++){

		var ch = value.substr(j, 1);

		if (ch < "0" || ch > "9" ){
			TRMove(getOwnerTR(element));
			alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+ "  '中的值不合法");

			return "false";

		}

	}

	if (value.length > IntLength*1)
	{
		TRMove(getOwnerTR(element));
		alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+"  '中的值不能超过" + IntLength + "位");
		return "false";

	}

	return "true";
}



///检查TD内容的float型的整数和小数部分的长度
function checkTDfloatLength(element,IntLength,floatLength)
{
	var nPointIndex = -1;

	var value = RTrim(element.innerText);

	for (var j = 0; j <  value.length; j++){

		var ch = value.substr(j, 1);

		if (ch < "0" || ch > "9" ){
			if (ch == "."){
				if (nPointIndex != -1){

					TRMove(getOwnerTR(element));
					alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+"  '中的值只能有一个小数点");

					return "false";

				}else
					nPointIndex = j;
			}

		}

	}

	if (nPointIndex == -1)
		nPointIndex = value.length;

	if ((value.substring(0, nPointIndex) * 1).toString().length > IntLength*1)
	{
			TRMove(getOwnerTR(element));
			alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+"  '中的值整数位数不能超过" + IntLength + "位");

			return "false";


	}

	var strFrac = value.substring(nPointIndex + 1, value.length);

	if (strFrac.length > 0){

		if ( strFrac.length >floatLength*1 ){
			TRMove(getOwnerTR(element));
			alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+"  '中的值小数位数不能超过" + floatLength + "位");
			return "false";

		}

	}
	return "true";
}

///////检查email输入信息

function  checkTDemail(element){
	var nPointIndex = -1;

	var value = RTrim(element.innerText);

	if(value=="")
		return "true";

	for (var j = 0; j <  value.length; j++){

		var ch = value.substr(j, 1);

		if (ch == "@"){
			if (nPointIndex != -1){

				TRMove(getOwnerTR(element));
				alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+"  '中的值只能有一个@");

				return "false";

			}else
				nPointIndex = j;
		}

	}

	if (nPointIndex == -1){

		TRMove(getOwnerTR(element));
		alert("在  ' "+ document.all("Title_"+getOwnerTable(element).id).rows[0].cells[element.cellIndex].innerText+"  '中的值不存在@");

		return "false";
	}

	return "true";

}

/////////////////////////////////////////////////////////////////////////Input///////////////////////////////////////////////////////////////////////




//检查Input的内容不能为空
function checkInputNull(element)
{

	if (element.tagName.toUpperCase()=="SELECT")
	{

		if (element.selectedIndex ==-1)
		{
			if (element.isNullTitle != undefined)
				alert(element.isNullTitle);
			else if (element.alertTitle != undefined)
				alert(element.alertTitle+":选择值不能为空");
			else
				alert("在该项选择值不能为空");

			return "false";

		}
		return "true";
	}


	var value = RTrim(element.value);

	if (value.length == 0){
		element.focus();

		if (element.isNullTitle != undefined)
			alert(element.isNullTitle);
		else if (element.alertTitle != undefined)
			alert(element.alertTitle+":输入值不能为空");
		else
			alert("在该项输入值不能为空");

		return "false";
	}
	return "true";
}

//检查Input的内容长度
function checkInputStrLength(element,StrLength){
	var value = RTrim(element.value);

	if (value.length >StrLength*1 ){
		element.focus();


		if (element.textLengthTitle != undefined)
			alert(element.textLengthTitle);
		else if (element.alertTitle != undefined)
			alert(element.alertTitle+":输入值的长度不能大于" + StrLength);
		else
			alert("在该项输入值的长度不能大于" + StrLength);

		return "false";
	}
	return "true";

}
//检查Input的内容Min长度
function checkInputStrMinLength(element,StrLength){
	var value = RTrim(element.value);

	if (value.length < StrLength*1 ){
		element.focus();

		if (element.minLengthTitle != undefined)
			alert(element.minLengthTitle);
		else if (element.alertTitle != undefined)
			alert(element.alertTitle+":输入值的长度不能小于" + StrLength);
		else
			alert("在该项输入值的长度不能小于" + StrLength);

		return "false";
	}
	return "true";

}


//检查Input的int的长度
function checkInputIntLength(element,IntLength){

	var value = RTrim(element.value);

	for (var j = 0; j<  value.length; j++){

		var ch = value.substr(j, 1);

		if (ch < "0" || ch > "9" ){
			element.focus();
			if (element.alertTitle != undefined)
				alert(element.alertTitle+":输入合法数字");
			else
				alert("在该项输入合法数字");

			return "false";

		}

	}

	if (value.length > IntLength*1)
	{
		element.focus();
		if (element.alertTitle != undefined)
			alert(element.alertTitle+":输入的整数位数不能超过" + IntLength + "位");
		else
			alert("在该项输入的整数位数不能超过" + IntLength + "位");

		return "false";

	}

	return "true";
}



//检查Input的float型的整数和小数部分的长度
function checkInputfloatLength(element,IntLength,floatLength)
{
	var nPointIndex = -1;

	var value = RTrim(element.value);

	for (var j = 0; j <  value.length; j++){

		var ch = value.substr(j, 1);

		if (ch < "0" || ch > "9" ){
			if (ch == "."){
				if (nPointIndex != -1){

					element.focus();
					if (element.alertTitle != undefined)
						alert(element.alertTitle+":输入的数字只能有一个小数点");
					else
						alert("在该项输入的数字只能有一个小数点");

					return "false";

				}else
					nPointIndex = j;
			}

		}

	}

	if (nPointIndex == -1)
		nPointIndex = value.length;

	if ((value.substring(0, nPointIndex) * 1).toString().length > IntLength*1)
	{
			element.focus();

			if (element.alertTitle != undefined)
				alert(element.alertTitle+":输入的整数位数不能超过" + IntLength + "位");
			else
				alert("在该项输入的整数位数不能超过" + IntLength + "位");

			return "false";


	}

	var strFrac = value.substring(nPointIndex + 1, value.length);

	if (strFrac.length > 0){

		if ( strFrac.length >floatLength*1 )
		{
			element.focus();

			if (element.alertTitle != undefined)
				alert(element.alertTitle+":输入的小数位数不能超过" + floatLength + "位");
			else
				alert("在该项输入的小数位数不能超过" + floatLength + "位");
			return "false";

		}

	}
	return "true";
}
///////检查email输入信息

function  checkInputemail(element){
	var nPointIndex = -1;

	var value = RTrim(element.value);

	if(value=="")
		return "true";

	for (var j = 0; j <  value.length; j++){

		var ch = value.substr(j, 1);

		if (ch == "@"){
			if (nPointIndex != -1){

				element.focus();
				if (element.alertTitle != undefined)
					alert(element.alertTitle+":输入的值只能有一个@");
				else
					alert("在该项输入的值只能有一个@");

				return "false";

			}else
				nPointIndex = j;
		}

	}

	if (nPointIndex == -1){

		element.focus();
		if (element.alertTitle != undefined)
			alert(element.alertTitle+":输入的值不存在@");
		else
			alert("在该项输入的值不存在@");

		return "false";
	}

	return "true";

}



/////检查Input的输入内容
function checkInputValue(element){

	if ((element.isNull != undefined)&&(element.isNull.toLowerCase() =="false"))
	{


		if (checkInputNull(element) =="false")
		{
			return "false";
		}
	}



     if ((element.intLength != undefined)&&(element.floatLength != undefined))
          if (checkInputfloatLength(element,element.intLength,element.floatLength)=="false")
               return "false";

     if ((element.intLength != undefined)&&(element.floatLength == undefined))
          if (checkInputIntLength(element,element.intLength)=="false")
               return "false";

     if (element.textLength != undefined)
          if (checkInputStrLength(element,element.textLength)=="false")
               return "false";

     if (element.minLength != undefined)
          if (checkInputStrMinLength(element,element.minLength)=="false")
               return "false";
     if ((element.eMail != undefined)&&(element.eMail.toLowerCase()=="true"))
          if (checkInputemail(element)=="false")
               return "false";




	return "true";
}


/////检查FORM的输入内容
function checkForm(formelement){

  
	for (var i=0; i< formelement.length; i++)
	{

		var element= formelement.item(i);


          if (checkInputValue(element)=="false")
              return "false";
	}

	return "true";
}


////获取绝对位置

function getAbsPosition(obj, offsetObj){


	var _offsetObj=(offsetObj)?offsetObj:document.body;
	var x=obj.offsetLeft;
	var y=obj.offsetTop;

	var tmpObj=obj.offsetParent;

	while ((tmpObj!=_offsetObj) && tmpObj){
		x+=tmpObj.offsetLeft+tmpObj.clientLeft-tmpObj.scrollLeft;
		y+=tmpObj.offsetTop+tmpObj.clientTop-tmpObj.scrollTop;
		tmpObj=tmpObj.offsetParent;
	}
	return ([x, y]);
}


///////formSubmit

function formSubmit(type,url,infoNo){
	if(type=="info"){

		parent.window.frmContainerInfo.responseFrame.location.replace(url+"?UserCardID="+infoNo);
		divParent="parent.window";
		window.clearTimeout();
		window.setTimeout('setActiveTabIndex(parent.window.userinforTabpage,"0")',1500);
		window.clearTimeout();
	}


	if(type=="req"){

		parent.window.frmContainerInfo.requestFrame.location.replace(url+"?UserCardID="+infoNo);

	}
}


//////////初始化界面信息/////////////////////////////////////

function body_init(formelement,querybutton){

	for (var i=0; i< formelement.length; i++)
	{

		var element= formelement.item(i);

	   if ( element.type != "button" && element.type !="submit" && element.type !="reset" ) {
		    if(element.tabIndex != undefined){

				if (arguments.length== 2)
				{
					element.buttonid=querybutton;
				}

			    element.onkeydown=enterToTab;
		    }
	    } else {
		   element.onkeydown = enterToClick;
	    }
	}

     onbodyprivateenvent();     
}


function text_onmouseover(){try{
   //showHelp(event.srcElement);
}catch(e)
{}}

function text_onfocus(){try{
//showHelp(event.srcElement);e
vent.srcElement.className ="active_editor";}
catch(e){}}

/*function body_init(querybutton){for(var i=0; i< document.all.length; i++)
{var element = document.all[i];
if (querybutton)
element.buttonid = querybutton;
if (element.tagName.toUpperCase() == "INPUT" && element.type.toUpperCase() == "TEXT" )
{element.onmouseover = text_onmouseover;
//element.onmouseout  = hiddenhelp;
element.onfocus     = text_onfocus;
//element.onblur      = text_onblur;
element.onkeydown   = enterToTab;element.oldvalue    = "";
if (element.fieldType =="date")
{element.onkeypress    = addcalender;element.style.imeMode = "disabled";}


if (element.fieldType =="money")
{
	element.onkeypress= onKeyPressInputFloat;
	//element.onkeyup= chineseNumber;
	element.style.imeMode="disabled";
	//alert();
}

if (element.fieldType =="int")
{
	element.onkeypress= onKeyPressInputInteger;
	element.style.imeMode="disabled";
	//alert(element.style.imeMode);
}

if (element.fieldType=="email")
{element.onkeypress = onKeyPressInputeMail;     
  element.style.imeMode="disabled";}}
if (element.tagName.toUpperCase() == "TEXTAREA")
{element.onmouseover = text_onmouseover;
//element.onmouseout  = hiddenhelp;
element.onfocus     = text_onfocus;
//element.onblur      = text_onblur;
element.oldvalue    = "";}
if (element.tagName.toUpperCase() == "SELECT")
{element.onmouseover = text_onmouseover;
//element.onmouseout  = hiddenhelp;
element.onkeydown   = enterToTab;
element.oldvalue    = "";
}}}

*/

function enterToClick(){
	var nKey = window.event.keyCode;

	if (nKey == 13) {
	    try {
		  if ( window.event.srcElement.onenterdown == "true" ) {
			 eval(window.event.srcElement.onclick());
		  }
	   } catch ( Exception ) {
	   }
	}

}

//////////////从enter转向tab////////////
function enterToTab(){

	var nKey = window.event.keyCode;
	var element = window.event.srcElement;

	if (nKey == 13){
		body_Click();
		 if(element.buttonid == undefined)
			 window.event.keyCode = 9;
		 else {
			 document.all(element.buttonid).focus();
			// document.all(element.buttonid).onclick();
		}

	}

}



/////////////////////////////////////////////////////////////////////////////////////////////////////////
function get_status_label(text){
	if (!text) text="正在下载数据...";


	if (typeof(_status_label)=="undefined"){
		document.body.insertAdjacentHTML("beforeEnd", "<marquee id=_status_label isvisible=1  scrollamount=3 behavior=alternate direction=right"+
			" style=\"position: absolute; visibility: hidden; width: 128; height: 22; background-color: #d4d0c8; font-size: 12; border: 1 solid silver; padding-top:3; z-index: 10000; filter:alpha(opacity=70)\"></marquee>");
	   // alert(typeof(div_status_label));
		//var obj = document.createElement("<div id=div_status_label><marquee id=_status_label   scrollamount=3 behavior=alternate direction=right"+
			//	" style=\"position: absolute; visibility: hidden; width: 128; height: 22; background-color: #d4d0c8; font-size: 12; border: 1 solid silver; padding-top:3; z-index: 10000; filter:alpha(opacity=70)\"></marquee></div>");

		//document.body.appendChild(obj);
	}else{

		//var status_label = "<marquee id=_status_label isvisible=1  scrollamount=3 behavior=alternate direction=right"+
		//	" style=\"position: absolute; visibility: hidden; width: 128; height: 22; background-color: #d4d0c8; font-size: 12; border: 1 solid silver; padding-top:3; z-index: 10000; filter:alpha(opacity=70)\"></marquee>"

		//div_status_label.innerHTML =status_label;
	}


	_status_label.innerHTML=text;
}

function show_status_label(parent_window, text, center){
	parent_window.get_status_label(text);

	if (!_status_label) return;

	parent_window.document_onmousemove();
	parent_window._status_label.style.visibility="visible";
	if (center){

		parent_window._status_label.style.posLeft=(document.body.clientWidth - _status_label.offsetWidth) / 2;
		parent_window._status_label.style.posTop=(document.body.clientHeight - _status_label.offsetHeight) / 2;
	}
	else{
		parent_window.document.onmousemove=document_onmousemove;
	}
}

function hide_status_label(parent_window){
	if (!parent_window) return;


	parent_window.document.onmousemove=null;

	if (!parent_window._status_label) return;



	parent_window._status_label.style.visibility="hidden";
	//parent_window.div_status_label.innerHTML ="";


}

function document_onmousemove(){
  if(event != null){
    locate_status_label(event.x + document.body.scrollLeft, event.y + document.body.scrollTop);
  }
}

function locate_status_label(x, y){
	if (!_status_label) return;

	if (x==0 && y==0) return;

	var posX=document.body.clientWidth + document.body.scrollLeft -_status_label.offsetWidth;
	var posY=document.body.clientHeight + document.body.scrollTop -_status_label.offsetHeight
	posX=(x<posX)?x:posX;
	posY=(y<posY)?y:posY;

	_status_label.style.posLeft=posX;
	_status_label.style.posTop=posY;
}
///////////////////////////打印gride列表
function print_DBGrid(id){

	var tab = document.all(id);



	if (tab.RecordCount/1==0)
	{
		alert("请先查询!");
		return;
	}

	if (tab.RecordCount/1>=2000)
	{
		alert("查的记录过多,请精确查询!");
		return;
	}


	 var arguments = g_jsContextPath+"/UI/system/public/print/makeprt.jsp";

    var dialogArg = "dialogwidth:550px; Dialogheight:450px;center:yes;help:no;resizable:no;scroll:no;status:no";
	var arg = new Object();


	arg.fieldcn = encode(tab.fieldCN);
	arg.visible = encode(tab.visible);
	arg.filedwidth = encode(tab.fieldwidth);

	arg.align = encode(tab.tralign);

	arg.sqlstr = encode(tab.printSQL);


	arg.wherestr = tab.whereStr;
	arg.ischeck  = tab.checkbl;
	arg.printtitle  = tab.printtitle;

    var  dd    = window.showModalDialog(arguments, arg, dialogArg);


}

///////////////////////////打印gride列表(包括打印隐藏域)
function allprint_DBGrid(id){

	var tab = document.all(id);

	if (tab.RecordCount/1==0)
	{
		alert("请先查询!");
		return;
	}




	 var arguments = g_jsContextPath+"/UI/system/public/print/allmakeprt.jsp";

    var dialogArg = "dialogwidth:550px; Dialogheight:450px;center:yes;help:no;resizable:no;scroll:no;status:no";
	var arg = new Object();


	arg.fieldcn = encode(tab.fieldCN);
	arg.visible = encode(tab.visible);
	arg.filedwidth = encode(tab.fieldwidth);

	arg.align = encode(tab.tralign);

	arg.sqlstr = encode(tab.printSQL);


	arg.wherestr = tab.whereStr;
	arg.ischeck  = tab.checkbl;
	arg.printtitle  = tab.printtitle;

    var  dd    = window.showModalDialog(arguments, arg, dialogArg);



}


function  refresh_select(selectId,refSql,defaultValue,defaultTitle){

     if (document.all[selectId] == undefined)
     	return;


     var xmlDoc = createDomDocument("<root/>");
     var rootNode = xmlDoc.documentElement;


     var actionNode = appendNode(xmlDoc, xmlDoc.documentElement, "action");
     appendAttri(xmlDoc, actionNode, "actionname","sm0083");

     var recNode = appendNode(xmlDoc, xmlDoc.documentElement, "recorder");
     appendAttri(xmlDoc, recNode, "type", "select");


	 var fieldNode = createFieldNode(xmlDoc,"selectsql","text",encode(refSql));
     recNode.appendChild(fieldNode);


     actionNode.appendChild(recNode);
     rootNode.appendChild(actionNode);
	
	
     var retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlSelectJsp.jsp","POST","sys_request_xml="+xmlDoc.xml);

//  alert(retStr);


     var xmlDoc = createDomDocument();

     xmlDoc.loadXML(analyzeReturnXML(retStr));

     var rootNode = xmlDoc.documentElement;

     var obj = document.all[selectId];

     for ( i= obj.options.length -1;i >-1; i--)
     {
          obj.options.remove(i);
     }


     if (rootNode.getAttribute("result") =="true")
     {
		  if (rootNode.childNodes.length >0 && arguments.length == 4 && defaultTitle!=undefined){

                 var opt = document.createElement("OPTION");
		  		 opt.setAttribute("first","defaultTitle");
                 obj.add(opt);
		  }

          for (var i=0; i< rootNode.childNodes.length; i++){
             var opt = document.createElement("OPTION");

              for (var j=0; j< rootNode.childNodes[i].childNodes.length; j++)
               {
                    var node =rootNode.childNodes[i].childNodes[j];


                     opt.setAttribute(node.getAttribute("name"),decode(node.getAttribute("value")));

               }
                obj.add(opt);
          }
     }


     if (arguments.length > 2)
     {
     	for (i=0;i<obj.options.length ; i++){
     		if (arguments.length ==4 && defaultValue==""){
     			if (obj.item(i).value=="first")
             		obj.selectedIndex =i;
     		}else{
     			if (obj.item(i).value==defaultValue)
             		obj.selectedIndex =i;

     		}
     	}

     }

}



function  aicRefresh_select(selectId,refSql,defaultValue,defaultTitle){

     if (document.all[selectId] == undefined)
     	return;


     var xmlDoc = createDomDocument("<root/>");
     var rootNode = xmlDoc.documentElement;


     var actionNode = appendNode(xmlDoc, xmlDoc.documentElement, "action");
     appendAttri(xmlDoc, actionNode, "actionname","sm0083");

     var recNode = appendNode(xmlDoc, xmlDoc.documentElement, "recorder");
     appendAttri(xmlDoc, recNode, "type", "select");


	 var fieldNode = createFieldNode(xmlDoc,"selectsql","text",encode(refSql));
     recNode.appendChild(fieldNode);


     actionNode.appendChild(recNode);
     rootNode.appendChild(actionNode);
	
	
     var retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/AicSqlSelectJsp.jsp","POST","sys_request_xml="+xmlDoc.xml);

 //  alert(retStr);


     var xmlDoc = createDomDocument();

     xmlDoc.loadXML(analyzeReturnXML(retStr));

     var rootNode = xmlDoc.documentElement;

     var obj = document.all[selectId];

     for ( i= obj.options.length -1;i >-1; i--)
     {
          obj.options.remove(i);
     }


     if (rootNode.getAttribute("result") =="true")
     {
		  if (rootNode.childNodes.length >0 && arguments.length == 4 && defaultTitle!=undefined){

                 var opt = document.createElement("OPTION");
		  		 opt.setAttribute("first","defaultTitle");
                 obj.add(opt);
		  }

          for (var i=0; i< rootNode.childNodes.length; i++){
             var opt = document.createElement("OPTION");

              for (var j=0; j< rootNode.childNodes[i].childNodes.length; j++)
               {
                    var node =rootNode.childNodes[i].childNodes[j];


                     opt.setAttribute(node.getAttribute("name"),decode(node.getAttribute("value")));

               }
                obj.add(opt);
          }
     }


     if (arguments.length > 2)
     {
     	for (i=0;i<obj.options.length ; i++){
     		if (arguments.length ==4 && defaultValue==""){
     			if (obj.item(i).value=="first")
             		obj.selectedIndex =i;
     		}else{
     			if (obj.item(i).value==defaultValue)
             		obj.selectedIndex =i;

     		}
     	}

     }

}




function  getUserSerial (operID){



     var xmlDoc = createDomDocument("<root/>");
     var rootNode = xmlDoc.documentElement;


     var actionNode = appendNode(xmlDoc, xmlDoc.documentElement, "action");
     appendAttri(xmlDoc, actionNode, "actionname","sm0083");
     appendAttri(xmlDoc, actionNode, "methodname","gettUserSerial");

     var recNode = appendNode(xmlDoc, xmlDoc.documentElement, "recorder");
     appendAttri(xmlDoc, recNode, "type", "select");

	 var fieldNode = createFieldNode(xmlDoc,"userid","text",operID);
     recNode.appendChild(fieldNode);


     actionNode.appendChild(recNode);
     rootNode.appendChild(actionNode);

     var retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlSelectJsp.jsp","POST","sys_request_xml="+xmlDoc.xml);



    var userSerial =analyzeReturnXML(retStr);

    return userSerial;
}


/////////////////////////触发事件
function fireUserEvent(function_name, param){

	var result;

    var paramstr="";
    if (arguments.length == 2)
          for(i=0; i<param.length; i++){
               if (i==0)
                    paramstr="param["+i+"]";
                else
                    paramstr=paramstr+",param["+i+"]";
          }

     if (isfireuserEvent(function_name)){
     	eval("result="+function_name+"("+paramstr+");");
     }

	return result;

}

function isfireuserEvent(function_name){

     var result;
     eval("result=(typeof("+function_name+")!=\"undefined\");");

    return result;
}




//////////////////////////身份证交验///////////////////////////




//*********************************************************************************
//  函数名：
//            validatezjhm
//  函数说明：
//            较验传入的字符串是否是格式正确的身份证号码
//            检查长度、是否包含非数字、包含的日期正确性、18位较验位检测
//  输入参数：
//            strValue2            待校验的身份证号码
//  返回值：
//            false：          不符合标准，同时给出提示
//            true：          符合标准，没有提示信息
//*********************************************************************************

function validatezjhm( strValue2 )
{
       if((strValue2.length != 15) && (strValue2.length != 18))
       {
              //alert(strValue2.length);
              alert("身份证号码长度不正确！");
              return false;
       }

       //判断输入是否全是数字
       nLen = strValue2.length;
       for(i = 0; i < nLen; i++)
       {
              strCh = strValue2.charAt(i);
              if((i == 17) && (strCh == "X"))
              {
                     continue;
              }

              nValue2 = parseInt(strCh, 10);
              if(isNaN(nValue2))
              {
                     break;
              }
       }

       if( i < nLen)
       {
              alert("身份证号码中有非数字！");
              return false;
       }

       if(strValue2.length == 15)
       {
              strValue3 = strValue2.substr(6,6);
              strValue3 = "19" + strValue3;
       }
       else
       {
              strValue3 = strValue2.substr(6,8);
       }

       strTmp = strValue3.substr(0,4) + "-" + strValue3.substr(4,2) + "-" + strValue3.substr(6,2);
       strValue3 = strTmp;

       if(Validate(2, strValue3) == "")
       {
              alert("身份证号码中的日期不正确！");
              return false;
       }

       if(strValue2.length == 18)
       {
              i = CheckIDAsMod11(strValue2);
              if(i != 1)
              {
                     alert("身份证号码校验错误，请重新输入！");
                     return false;
              }
       }
       return true;
}





//*********************************************************************************
//  函数名：
//            CheckIDAsMod11
//  函数说明：
//            18位身份证号码较验位生成
//  输入参数：
//            strID    待校验的身份证号码
//  返回值：
//           0：          不符合标准
//           1：          符合标准
//*********************************************************************************

function CheckIDAsMod11(strID)
{
       var arrWeight;
       var arrCheck;
       var nLen;
       var i,k;
       var nTotal;
       var nCheckNum;
       var strCh;

       arrWeight = new Array(1, 2, 4, 8, 5, 10, 9 ,7, 3, 6);
       arrCheck = new Array("1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2");
       nTotal = 0;
       nLen = strID.length;

       strCh = strID.charAt(nLen - 1);

       for(i = 1; i < nLen; i++)
       {
              k = parseInt(strID.charAt(nLen - i - 1));
              nTotal = nTotal + arrWeight[i % 10] * k;
       }

       nCheckNum = nTotal % 11;

       if(arrCheck[nCheckNum] == strCh)
       {
              return 1;
       }       return 0;
}





//*********************************************************************************
//  函数名：
//            Validate
//  函数说明：
//            校验输入数据的合法性
//  输入参数：
//            nType             存储校验类型：1 检验是否为空；2 日期检验
//            strField            待校验的字符串
//  返回值：
//            ""：          不符合标准,返回空字符串
//      strTrim：       符合标准，返回经过规范化的strField
//*********************************************************************************

function Validate(nType,strField)
{
       var strTrim;
       var i;
       var strSeparator;
       strTrim = trimStr(strField);

       switch(nType)
       {
              case 1:           //检验非空
                     return strTrim;
                     break;

              case 2:           //日期
                     if (strTrim =="")                                                                            //输入的日期串为空
                     {
                            return strTrim;
                     }

                     //查找年月日间的分隔符
                     for( i = 0; i < strTrim.length; i++ )
                     {
                            if( ( strTrim.charAt(i) > "9" || strTrim.charAt(i) < "0") && strTrim.charAt(i) != " " )
                            {
                                   strSeparator = strTrim.charAt(i);
                                   break;
                            }
                     }

                     var strYear;
                     var strMonth;
                     var strDate;
                     var arrDate

                     arrDate = strTrim.split(strSeparator);
                     if (!(arrDate.length == 3))                                                              //年月日之间没有正确的分隔符
                     {
                            return "";
                     };

                     strYear = trimStr(arrDate[0]);
                     strMonth = trimStr(arrDate[1]);
                     strDate = trimStr(arrDate[2]);

                     if (isNaN(strYear)||isNaN(strMonth)||isNaN(strDate))             //年、月、日表示不正确
                     {
                            return "";
                     }

                     if ((strYear.charAt(0)=="0")||(strMonth.length>2)||(strDate.length>2)||(strYear.length>4))
                     {
                            return "";
                     }

                     if ((strYear=="")||(strMonth=="")||(strDate==""))
                     {
                            return "";
                     }

                     if (strMonth.charAt(0)=="0")
                     {
                            strMonth = strMonth.substring(1,2);
                            if (strMonth=="")
                            {
                                   return "";
                            }
                     }

                     if(strDate.charAt(0)=="0")
                     {
                            strDate = strDate.substring(1,2);
                            if (strDate == "")
                            {
                                   return "";
                            }
                     }

                     if ((parseInt(strMonth) < 1)||(parseInt(strMonth) > 12))      //月份表示不合格
                     {
                            return "";
                     }

                     if (parseInt(strDate) < 1)                                                               //日期表示不合格
                     {
                            return "";
                     }

                     switch (strMonth)
                     {
                            case "1":
                            case "01":
                            case "3":
                            case "03":
                            case "5":
                            case "05":
                            case "7":
                            case "07":
                            case "8":
                            case "08":
                            case "10":
                            case "12":
                            {
                                   if (parseInt(strDate) > 31)                                                //日期表示不合格
                                   {
                                          return "";
                                   }
                            }
                            break;

                            case "4":
                            case "04":
                            case "6":
                            case "06":
                            case "9":
                            case "09":
                            case "11":
                            {
                                   if (parseInt(strDate) > 30)                                                //日期表示不合格
                                   {
                                          return "";
                                   }
                            }
                            break;

                            case "2":
                            case "02":
                            {
                                   if (((parseInt(strYear) % 4 == 0) && (!(parseInt(strYear) % 100==0)))||(parseInt(strYear)% 400 == 0))
                                   {
                                          if ((parseInt(strDate) > 29))                               //闰年2月
                                          {
                                                 return "";
                                          }
                                   }
                                   else
                                   {
                                          if ((parseInt(strDate) > 28))                               //平年2月
                                          {
                                                 return "";
                                          }
                                   }
                            }
                            break;
                     }      //switch2

                     strTrim = strYear + "-" + strMonth + "-" + strDate;
                     return strTrim;
                     break;
       }//switch1
}


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


function oncontmenu(){

	event.returnValue=false;
}


function onbodyprivateenvent(){

 	if (document.body != null&& typeof(document.body.onload)=="function"){
      
     	//if (typeof(document.body.oncontextmenu) !="function")
     	//	document.body.oncontextmenu=oncontmenu;

       //  if (typeof(document.body.onkeydown) !="function")
         	//	document.body.onkeydown = body_onkeypress;

	}


}


function body_onkeypress(){

	if (event.ctrlKey )
     	event.keyCode="0";


}



/////////////////////////////////////////////////////////////////////////////////////////////////////


function checkLogin(){

	return;
	
	var auth = new ActiveXObject("Msxml2.XMLHTTP") ;
	auth.open("POST","/pages/security/uionline.jsp",false) ;

	var xmlstr="<root><action></action></root>";		
	auth.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	 
	auth.send(xmlstr);

	
   if(trimStr(analyzeReturnXML(auth.responseText))!="true"){
   		alert ('操作员超时，请重新签到！'); 
   		if(top)
   			{ top.location.href='/admin/*'; } 
   		else 
   			{ location.href = '/admin/*';} 
   
   		return false;
   
   }

	return true;
}



function killSession(){
   if (parent.window.reload =="true")
      return;
      
	var auth = new ActiveXObject("Msxml2.XMLHTTP") ;
	auth.open("POST","/pages/security/killlineuser.jsp",false) ;

	var xmlstr="<root><action></action></root>";		
	auth.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	 
	auth.send(xmlstr);

}
function DelImgSign(){

	var auth = new ActiveXObject("Msxml2.XMLHTTP") ;
	auth.open("POST","/pages/security/killImgSign.jsp",false) ;

	var xmlstr="<root><action></action></root>";		
	auth.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
	 
	auth.send(xmlstr);

}

function CalendarMaker(el){

	if (el.value.length>1){
		if(el.value.substr(0,2)/1 < 19 || el.value.substr(0,2)/1 > 30)
			window.event.keyCode = 0;
	
	}

	if (el.value.length ==4){
		el.value = el.value+"-";	
	}
	
	if (el.value.length ==6){
		if(el.value.substr(5,1)/1 >1)
			window.event.keyCode = 0;
	}
	
	if (el.value.length ==7){
		if(el.value.substr(5,2)/1 >12)
			window.event.keyCode = 0;
		else
			el.value = el.value+"-";

	}
	
	if (el.value.length ==9){
		if(el.value.substr(8,1)/1 >3)
			window.event.keyCode = 0;
	}
	
	if (el.value.length ==9){
		if(el.value.substr(8,1)/1 ==3)
			if (window.event.keyCode !=48 && window.event.keyCode !=49)
				window.event.keyCode =0;
	}
	
	if (el.value.length>9){
		window.event.keyCode = 0;
	}
	
	
}

function CalendarAnytoMaker(el){




}
/////////////替换";"

function valuereplase(strValue,oldrp,newrp){
	try {
		strValue = strValue +"";
		var rpvalue = strValue.replace(/;/g,newrp);
		return rpvalue;
	} catch ( Exception ) {
			return strValue;
	}



}

/////////////替换","
function valueComma(strValue){
	try {
		strValue = strValue +"";
		var rpvalue = strValue.replace(/,/g,"，");
		rpvalue = valuereplase(rpvalue,"","；")
		return rpvalue;
	} catch ( Exception ) {
			return strValue;
	}
}


/////body单击事件 隐藏控健的 初始化信息
function body_Click(){

  ////隐藏路控健信息
 if (typeof(document.all["_load_box"])!="undefined")
	 droploadInit();

///隐藏日期控件信息
if (typeof(document.all["dropdown_button"])!="undefined")
	 CalInit();

////隐藏下拉菜单信息
if (typeof(document.all["dropdownText_button"])!="undefined")
	 dropInit();

////隐藏右健菜单信息
if (typeof(document.all["MenuContainer"])!="undefined")
	 hide();

////隐藏月控件信息
if (typeof(document.all["_dropMonth_frame"])!="undefined")
	 MonthInit();
onbodyprivateenvent();

}