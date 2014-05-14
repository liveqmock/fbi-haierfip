/*****************************************************\
*			公共Javascript脚本程序				*
\*****************************************************/

/*****************************************************\
*                   定义全局变量			       	  *
\*****************************************************/
// Global variables
var isCSS, isW3C, isIE4, isNN4, isIE6CSS;
var isSelfEvent = 0;
var menu;
var debug = false;
//var copyright = '---
var copyright = '';
// Initialize upon load to let all browsers establish content objects
function initDHTMLAPI( ) {
    if (document.images) {
        isCSS = (document.body && document.body.style) ? true : false;
        isW3C = (isCSS && document.getElementById) ? true : false;
        isIE4 = (isCSS && document.all) ? true : false;
        isNN4 = (document.layers) ? true : false;
        isIE6CSS = (document.compatMode && document.compatMode.indexOf("CSS1") >= 0) ?
            true : false;
    }
}
//离开时cookie处理
function doCookieOut(){
     var obj = null;

	if(document.getElementById("userno")){
      	obj = document.getElementById("userno");
	}else
     if(document.getElementById("userNo")){
      	obj = document.getElementById("userNo");
	}else
     if(document.getElementById("USERNO")){
      	obj = document.getElementById("USERNO");
	}else
     if(document.getElementById("usercard")){
      	obj = document.getElementById("usercard");
	}else
     if(document.getElementById("usercardid")){
      	obj = document.getElementById("usercardid");
	}else
     if(document.getElementById("UserId")){
      	obj = document.getElementById("UserId");
	}else
     if(document.getElementById("UserNo")){
      	obj = document.getElementById("UserNo");
	}else
     if(document.getElementById("UserCardID")){
      	obj = document.getElementById("UserCardID");
	}
    // alert('out.userno'+trimStr(obj.value));
     if(obj){
          if(trimStr(obj.value)!=''){
      		setCookie("userno",trimStr(obj.value));
          }
     }
     //alert('out success!');
}
//开始时cookie处理
function doCookieIn(){
     var ___userno = getCookie("userno");
     if(___userno == null ){
      	return;
     }
	//alert('in.userno'+___userno);
     var obj = null;
	if(document.getElementById("userno")){
      	obj = document.getElementById("userno");
	}else
     if(document.getElementById("userNo")){
      	obj = document.getElementById("userNo");
	}else
     if(document.getElementById("USERNO")){
      	obj = document.getElementById("USERNO");
	}else
     if(document.getElementById("usercard")){
      	obj = document.getElementById("usercard");
	}else
     if(document.getElementById("usercardid")){
      	obj = document.getElementById("usercardid");
	}else
     if(document.getElementById("UserId")){
      	obj = document.getElementById("UserId");
	}else
     if(document.getElementById("UserNo")){
      	obj = document.getElementById("UserNo");
	}else
     if(document.getElementById("UserCardID")){
      	obj = document.getElementById("UserCardID");
	}
     if(obj){
          obj.value = ___userno;
     }
     //alert('in success!');
}
//alert(window.document);
//alert(document.onmousedown = );
/*****************************************************\
*      获得某一段文本的最大长度		              	    *
\*****************************************************/
function getMaxLength(inputString){
	var maxlength=0;
	for(var i=0;i<inputString.length;i++){
		if(escape(inputString.charAt(i)).length==6){
			maxlength=maxlength + 2;
		}else {
			maxlength=maxlength + 1;
		}
	}
	return maxlength;
}




/*****************************************************\
*      将无任何值的Select控件的长度设为固定长度       *
\*****************************************************/
function setNullSelectToBlank(obj,size){
	//default value
	if(size==null){
			size=15;
	}

	var length= obj.options.length;
	if(length<=0){
		obj.options[0]=new Option("", "-10000", true, false);
		setSelectSize(obj,size);
	}
}




/*****************************************************\
*      获得当前Select控件里的文本中的最大长度         *
\*****************************************************/
function getSelectSize(obj){
	var size = obj.options.length;
	var maxlength = 0;
	for(var i=0;i<size;i++){
		var alength = getMaxLength(obj.options[i].text);
		if(alength>maxlength){
			maxlength=alength;
		}
	}

	var textlength = maxlength+2;
	return textlength;
}





/*****************************************************\
*      设置Select控件最大长度         *
\*****************************************************/

function setSelectSize(obj,textsize){
	var size = obj.options.length;
	var maxlength = 0;
	var index = 0;
	if(size>0){
		for(var i=0;i<size;i++){
			var alength = getMaxLength(obj.options[i].text);
			if(alength>maxlength){
				maxlength=alength;
				index=i;
			}
		}

		var textlength = maxlength+2;
		if(textlength<textsize){
			var substr1 = "";
			for(var i = 0;i<(textsize-textlength);i++){
				substr1 = substr1 + " ";
			}
			obj.options[index].text = obj.options[index].text+substr1;
		}
	}else{
		setNullSelectToBlank(obj,textsize);
	}
}
/************************************************************************\
*      让两个控件的长度相等（目前只限于text和select控件之间进行)         *
*	    @param sourceobj：源控件（被参考）   anothobj:模仿控件  （参考） *
\************************************************************************/
function setBothSizeEqual(sourceobj,anothobj){
	if((sourceobj.type=='text')==true){
			var textlength = sourceobj.size;
			setSelectSize(anothobj,textlength);
	}else if((sourceobj.type=='select-one')==true){
			var selectlength = getSelectSize(sourceobj);
			anothobj.size = selectlength;
	}
}
/************************************************************************\
*                        设定Table的高度								 *
\************************************************************************/
function setTableHight(obj,height1){
	obj.style.height=height1;
}

/************************************************************************\
*                        设定Table的宽度								 *
\************************************************************************/
function setTableWidth(obj,width1){
     //alert(width1);
     obj.style.width = width1;
	//alert(obj.width);
}
/************************************************************************\
*                        添加where条件: field = 'value'					 *
\************************************************************************/
function addNewWhereEqual(strfieldname,strvalue,isString){
	var isstring1 = isString;
	var sString = "";
	if((isstring1==undefined)==true){
		alert('警告：addNewWhereEqual的isString未定义！');
		return '';
	}
	if((strvalue==undefined)==true){
		return '';
	}
	if(isString==1){
		sString = "'";
	}
	var strRev = " and ";
	strRev = strRev + "(";
	strRev = strRev + strfieldname;
	strRev = strRev + " = " + sString + strvalue + sString;
	strRev = strRev +")";

	return strRev;

}
/************************************************************************\
*                      添加Where语句:between ... and ...				 *
\************************************************************************/
function addNewWhereBetween(strfieldname,strval1,strval2,isString){
	var isstring1 = isString;
	var sString = "";
	if((isstring1==undefined)==true){
		alert('警告：addNewWhereBetween的isString未定义！');
		return '';
	}
	if(isString==1){
		sString = "'";
	}
	var strRev = " and ";
	strRev = strRev + "(";
	strRev = strRev + strfieldname;
	strRev = strRev + " between " + sString + strval1 + sString + " and "
					+ sString + strval2 + sString;
	strRev = strRev +")";

	return strRev;
}
/************************************************************************\
*          添加Where语句:between TO_DATE(...) and TO_DATE(...)			 *
\************************************************************************/
function addNewWhereDateBetween(strfieldname,strval1,strval2){
	var sString ="yyyy-mm-dd";
	var strRev = " and ";
	strRev = strRev + "(";
	strRev = strRev + strfieldname;
	strRev = strRev + " between TO_DATE('"+ strval1 + "','"+sString
					+"') and TO_DATE('"+ strval2 + "','"+sString+"'))";

	return strRev;
}
/************************************************************************\
*          添加Where语句:between TO_DATE(...) and TO_DATE(...)	时间	 *
\************************************************************************/
function addNewWhereDateTimeBetween(strfieldname,strval1,strval2){
	var sString ="yyyy-mm-dd hh24:mi:ss";
	var strRev = " and ";
	strRev = strRev + "(";
	strRev = strRev + strfieldname;
	strRev = strRev + " between TO_DATE('"+ strval1 + "','"+sString
					+"') and TO_DATE('"+ strval2 + "','"+sString+"'))";

	return strRev;
}
/************************************************************************\
*                      添加Where语句(DATE):操作符(<,>)   				 *
\************************************************************************/
function addNewWhereDateOperation(strfieldname,strval1,stroperation){

	var sString ="yyyy-mm-dd hh24:mi:ss";

	var strRev = " and ";
	strRev = strRev + "(";
	strRev = strRev + strfieldname;
	strRev = strRev + " " + stroperation + " TO_DATE('" + strval1 +"','"+sString+"'))";

	return strRev;
}

/************************************************************************\
*                      添加Where语句:操作符(<,>)   					     *
\************************************************************************/
function addNewWhereOperation(strfieldname,strval1,stroperation,isString){
	var isstring1 = isString;
	var sString = "";
	if((isstring1==undefined)==true){
		alert('警告：addNewWhereOperation的isString未定义！');
		return '';
	}
	if(isString==1){
		sString = "'";
	}
	var strRev = " and ";
	strRev = strRev + "(";
	strRev = strRev + strfieldname;
	strRev = strRev + " " + stroperation + " " + sString + strval1 + sString;
	strRev = strRev +")";

	return strRev;
}
/************************************************************************\
*                      介绍：组SQL的WHERE中的Like语句段(两头,如'%xxx%')   *
\************************************************************************/
function addNewWhereLike(strfieldname,strval1){
	var strRev = " and ";
	strRev = strRev + "(";
	strRev = strRev + strfieldname;
	strRev = strRev + " like '%" + strval1 + "%'";
	strRev = strRev +")";
	return strRev;
}
/************************************************************************\
*                      介绍：组SQL的WHERE中的Like语句段(like 'XXX%',后段)  *
\************************************************************************/

function addNewWhereLikeFront(strfieldname,strval1){
	var strRev = " and ";
	strRev = strRev + "(";
	strRev = strRev + strfieldname;
	strRev = strRev + " like '" + strval1 + "%'";
	strRev = strRev +")";
	return strRev;
}

/************************************************************************\
*                      日期控件值默认为'0000-00-00'				    *
\************************************************************************/
function datereset(obj){
	if(trimStr(obj.value) == ""){
		obj.value="0000-00-00";
	}
}
/************************************************************************\
*                      		检查日期合法性							     *
\************************************************************************/
function validateDate(fld) {
	if(trimStr(fld.value) =="" ||trimStr(fld.value)=='0000-00-00'){
		return true;
	}
    if (!checkDate(fld)) {
        // focus if validation fails
        fld.focus();
		fld.select();
		return false;
    }
	return true;
}

/*
* 判断日期，格式：yyyy-mm-dd
* */
function checkDate(fld) {
    var mo, day, yr;
    var entry;
    //haiyu huang 2010-6-24 添加判断
    if (typeof(fld) == "object")
        entry = fld.value;
    else entry = fld;
	//alert(entry);
    var re = /\d{4}\b[-]\b\d{1,2}[-]\d{1,2}/;

    if (re.test(entry)) {
		//alert('12345');
        var delimChar = "-";
        var delim1 = entry.indexOf(delimChar);
        var delim2 = entry.lastIndexOf(delimChar);
        yr = parseInt(entry.substring(0, delim1), 10);
        mo = parseInt(entry.substring(delim1+1, delim2), 10);
        day= parseInt(entry.substring(delim2+1), 10);
        var testDate = new Date(yr, mo-1, day);
        //alert(testDate);
        if (testDate.getDate( ) == day) {
            if (testDate.getMonth( ) + 1 == mo) {
                if (testDate.getFullYear( ) == yr) {
                    return true;
                } else {
                    alert("输入年份有误！");
                }
            } else {
                alert("输入月份有误！");
            }
        } else {
            alert("输入日份有误!");
        }
    } else {
        alert("不正确日期格式. 日期输入格式如下 yyyy-mm-dd.");
    }
    return false;
}
/************************************************************************\
*                     根据格式为"yyyy-mm-dd"转为Date()型返回		     *
\************************************************************************/
function getDateByString(strIn){
        var entry=strIn;
        var delimChar = "-";
        var delim1 = entry.indexOf(delimChar);
        var delim2 = entry.lastIndexOf(delimChar);
        var yr = parseInt(entry.substring(0, delim1), 10);
        var mo = parseInt(entry.substring(delim1+1, delim2), 10);
        var day= parseInt(entry.substring(delim2+1), 10);
        return new Date(yr, mo-1, day);
}
/************************************************************************\
*        				修改Table属性的公共函数						     *
\************************************************************************/
function EditTableParam(tab,tabledata,index){
	tab.SQLStr = tabledata[index].sqlstr;
	tab.fieldType = tabledata[index].fieldtype;
	tab.fieldCN = tabledata[index].fieldcn;
	tab.enumType = tabledata[index].enumtype;
	tab.visible = tabledata[index].visible;
	tab.fieldname = tabledata[index].fieldname;
	tab.fieldwidth = tabledata[index].fieldwidth;
     tab.whereStr = tabledata[index].whereStr;
	//alert(tabledata[index].pagesize);
	tab.checkbl = tabledata[index].checkbl;
	tab.pageSize = tabledata[index].pagesize;
	tab.RecordCount = tabledata[index].recordcount;
	tab.countSQL = tabledata[index].countSQL;
}
/************************************************************************\
*        				对齐head 和 body的公共函数					     *
\************************************************************************/


function MyWidthEquals(headTable,bodyTable){
	var alllength = 0;
	var x =6;
	var bodytable = document.all[bodyTable];
     var headtable = document.all[headTable];
	if(bodytable.rows.length>0){
          var bodytable_row0_obj = bodytable.rows[0];
          var size = bodytable_row0_obj.cells.length;
          var maxlength = new Array(size);

		for(var i=0;i<size;i++){
			maxlength[i]=0;
		}

		var headtable_row0_obj = headtable.rows[0];
          var bodytable_rows_length = bodytable.rows.length;
		for(var i=0;i<bodytable_rows_length;i++){
			var bodytable_rowsobj = bodytable.rows[i];
               var bodytable_rowsobj_length = bodytable_rowsobj.cells.length;
			for(var j=0; j<bodytable_rowsobj_length;j++){

				var realbodylength = getMaxLength(bodytable_rowsobj.cells[j].innerText);

				var realheadlength =getMaxLength(headtable_row0_obj.cells[j].innerText);

				if(realheadlength < realbodylength){
					if(maxlength[j]<realbodylength){
						maxlength[j] = realbodylength;
					}
					headtable_row0_obj.cells[j].width = realbodylength*(x);
					bodytable_rowsobj.cells[j].width = realbodylength*(x);
				}else{
					if(maxlength[j]<realheadlength){
						maxlength[j] = realheadlength;
					}


					headtable_row0_obj.cells[j].width = realheadlength*(x);

					bodytable_rowsobj.cells[j].width = realheadlength*(x);

				}

			}

		}

		for(var i=0;i<size;i++){
               //alert(maxlength[i]);
			alllength += maxlength[i];
		}
	}
	document.getElementById("divt_"+bodyTable).style.width = alllength*x+33*6-20;
	document.getElementById("All_"+bodyTable).style.width = alllength*x+33*6;

}


/*function getRealValueOfInnerHTML(str1){
	alert(str1);
	var istart = str1.indexOf(">");
	var iend = str1.lastIndexOf("<");
	var strrev = str1.substring((istart+1),iend);
	return strrev;
}*/

function clearWhereStr(tab){
		tab.whereStr="";
}

// Return the available content width space in browser window
function getInsideWindowWidth() {
    if (window.innerWidth) {
        return window.innerWidth;
    } else if (isIE6CSS) {
        // measure the html element's clientWidth
        return document.body.parentElement.clientWidth;
    } else if (document.body && document.body.clientWidth) {
        return document.body.clientWidth;
    }
    return 0;
}

//init xml object template architecture
/*function getXmlTemplate(actionName){
	var xmlDoc = createDoc();
	var rootNode = xmlDoc.documentElement;
	var actionNode = createActionNode(xmlDoc,actionName);
	return
}*/
// append new field to parent recorder
function appendFieldNode(xmldoc,parentobj,fieldname,fieldtype,value){
	var fieldNode = createFieldNode(xmldoc,fieldname,fieldtype,value);
	parentobj.appendChild(fieldNode);
}

//analysis the value of the return xml message from the server
function getXmlValue(XMLStr){
		if(trimStr(XMLStr).length<=31){
			return "";
		}
		var xmlDoc = createDomDocument();

		xmlDoc.loadXML(XMLStr);

		var rootNode = xmlDoc.documentElement.firstChild;

		if (rootNode.getAttribute("type") =="0")
		{
			if (rootNode.getAttribute("result") =="true"){
				return rootNode.text;
			}
		}
		return "";
}
function getSelectControl(textId,selectId){

	var textobj = document.getElementById(textId);
	var selectobj = document.getElementById(selectId);
	var div_selectobj = document.getElementById("div_"+selectId);
	div_selectobj.style.visibility = "visible";
	selectobj.focus();
	div_selectobj.style.position = "absolute";
	setBothSizeEqual(textobj,selectobj);
	div_selectobj.style.left = getElementPosition(textId)['left']+3;
	div_selectobj.style.top =  getElementPosition(textId)['top']+1;

	return true;
}

function setTextValue(textobj1,objId){
	var obj = document.getElementById(objId);
	var divobject = document.getElementById("div_"+objId);
	var textobj = document.getElementById(textobj1);
	textobj.attr = trimStr(obj.options[obj.selectedIndex].value);
	textobj.innerText = trimStr(obj.options[obj.selectedIndex].innerText);
	if((isSelfEvent==0) == false){
		onSelectChangeEvent(textobj1,objId);
	}
	setDivObjHidden();
}

function getElementPosition(elemID) {
    var offsetTrail = document.getElementById(elemID);
    var offsetLeft = 0;
    var offsetTop = 0;
    while (offsetTrail) {
        offsetLeft += offsetTrail.offsetLeft;
        offsetTop += offsetTrail.offsetTop;
        offsetTrail = offsetTrail.offsetParent;
    }
    if (navigator.userAgent.indexOf("Mac") != -1 &&
        typeof document.body.leftMargin != "undefined") {
        offsetLeft += document.body.leftMargin;
        offsetTop += document.body.topMargin;
    }
    return {left:offsetLeft, top:offsetTop};
}

function setDivObjHidden(){
	for(var i=0;i<activeObjName.length;i++){
		document.getElementById('div_'+activeObjName[i]).style.visibility = "hidden";
	}
}

function initDivObjects(){
	for(var i=0;i<activeObjName.length;i++){
		document.getElementById(activeObjName[i]).onblur = setDivObjHidden;
	}
}

function hideSysContext(){
	event.returnValue=false;
}

function setSelValToText(selectId,textId){

	var selectobj = document.getElementById(selectId);
	var textobj = document.getElementById(textId);

	if(selectobj.length>0){
		textobj.value = selectobj.options[selectobj.selectedIndex].innerText;
	}

}

function updateAllData(tab,m_objParam,updateActionName,fieldArray){
	if(fieldArray==''){
		return false;
	}

	TdFinal();

	var xmlDoc = createDoc();

	var rootNode =createRootNode(xmlDoc);

	var updateNode =null;
	for (var i = 0; i < tab.rows.length; i++)
	{
			if (updateNode == null)
				updateNode = createActionNode(xmlDoc,updateActionName);

			var RecorderNode = createRecorderNode(xmlDoc,"update");

			for(var j=0;j<tab.rows[i].cells.length;j++ )
			{
				if ((tab.rows[i].cells[j].fieldname !=undefined )&&(tab.rows[i].cells[j].fieldType !=undefined))
				{
					///检查输入项
					if(checkTDValue(tab.rows[i].cells[j])=="false")
						return false;

					///添加dropdown项

					if (tab.rows[i].cells[j].fieldType.toLowerCase() =="dropdown")
					{
						if(tab.rows[i].cells[j].attr !=undefined)
						{
							if(isArrayHasTheItem(fieldArray,tab.rows[i].cells[j].fieldname)){
								var fieldNode = createFieldNode(xmlDoc,tab.rows[i].cells[j].fieldname,tab.rows[i].cells[j].fieldType,tab.rows[i].cells[j].attr);
								RecorderNode.appendChild(fieldNode);
							}
						}
					}else if (tab.rows[i].cells[j].innerText !="" )
					{
						if(isArrayHasTheItem(fieldArray,tab.rows[i].cells[j].fieldname)){
							var fieldNode = createFieldNode(xmlDoc,tab.rows[i].cells[j].fieldname,tab.rows[i].cells[j].fieldType,tab.rows[i].cells[j].innerText);
								RecorderNode.appendChild(fieldNode);

						}
					}
				}
			}


			////添加主健值
			appendFieldStrToRecorder(RecorderNode,tab.rows[i].whStr,xmlDoc);


			updateNode.appendChild(RecorderNode);

		}

		/////添加外部参数值
     	if(trimStr(m_objParam) != ''){
			var myRecordNode =  createRecorderNode(xmlDoc,"update");

			appendFieldStrToRecorder(myRecordNode,m_objParam,xmlDoc);
			if (updateNode == null)
					updateNode = createActionNode(xmlDoc,updateActionName);
			updateNode.appendChild(myRecordNode);
     	}
	//alert(xmlDoc.xml);
	if (rootNode.childNodes.length >0 )
	{
		var retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlExcuteJsp.jsp","POST","sys_request_xml="+xmlDoc.xml);
		//alert("aaaaa:"+retStr);

		////返回信息检查
		if (myAnalyzeReturnXML(retStr)== "true")
		{
               if((trimStr(getXmlValue(retStr)) == "0") == true){
                    alert('操作成功!');
               }else{
                	return retStr;
               }
			return true;
		}else{

               return false;
		}
	}
}

function appendFieldStrToRecorder(recorder,Str1,xmlDoc){
			if ((Str1 !=undefined)&&(Str1!=""))
			{
				var whereSArr =Str1.split("*");

				for (var j=0; j< whereSArr.length; j++)
				{

					if (whereSArr[j]!=undefined){
						var whArr = whereSArr[j].split("&");

						var fieldNode = createFieldNode(xmlDoc, whArr[0],whArr[1],whArr[2]);
						recorder.appendChild(fieldNode);
					}
				}
			}
}

function isArrayHasTheItem(array1,str1){
	for(var i = 0; i<array1.length; i++){
		if(trimStr(array1[i].toLowerCase())==str1.toLowerCase()){
			return true;
		}

	}
	return false;
}

function autoPrint(name){
     var obj = document.getElementById(name);
     //alert (obj);
     obj.Print(true);
}

function  StrToDate(value){
    var mo, day, yr;
    var entry = value;
	//alert(entry);
    var re = /\d{4}\b[-]\b\d{1,2}[-]\d{1,2}/;

    if (re.test(entry)) {
        var delimChar = "-";
        var delim1 = entry.indexOf(delimChar);
        var delim2 = entry.lastIndexOf(delimChar);
        yr = parseInt(entry.substring(0, delim1), 10);
        mo = parseInt(entry.substring(delim1+1, delim2), 10);
        day= parseInt(entry.substring(delim2+1), 10);
        var testDate = new Date(yr, mo-1, day);
        return testDate;
    } else {
        alert("不正确日期格式. 日期输入格式如下 yyyy-mm-dd.");
    }
    return "null";
	//add code here
}

function DateToStr(date){
 	var month = (date.getMonth()+1);
     if( month<10){
          month = ('0'+month);

     }
     var day = (date.getDate());
     if( day<10){
      	day = ('0'+day);
     }
     var strdate = date.getFullYear() + "-" + month + "-" + day;
     return strdate;

}

function getDateOfMonthByString(str,splitstr){

	var datearray = str.split(splitstr);

     var year = parseInt(datearray[0],10);
     var month = parseInt(datearray[1],10);
     var day = parseInt(datearray[2],10);

     var date = new Date(year,month-1,day);

     return date.getDay();
}

function getCHNFontByDay(index){
 	var arraylist =['星期日','星期一','星期二','星期三','星期四','星期五','星期六'];
     return arraylist[index];
}
function replaceStr(str1,searchStr,replaceStr){
	var re = new RegExp(searchStr , "g");
	var result = str1.replace(re, replaceStr);
     return result;
}

function TdFinal(){
	///给最后的TD付值

	if  (typeof(_TableInput_Text)!="undefined")
		document.all["_TableInput_Text"].getAttribute("_TD").innerText = document.all["_TableInput_Text"].value;

	if  (typeof(_TableNumber_Text)!="undefined")
		document.all["_TableNumber_Text"].getAttribute("_TD").innerText = document.all["_TableNumber_Text"].value;

}

function setworkFrameBlank(win){
//     if(debug){
//     	alert(win);
//     	alert(win.window);
//     	alert(win.window.frames["workFrame"].src);
//     }
 	win.window.frames["workFrame"].document.write('');
}

		//////分析普通数据包返回值

	function myAnalyzeReturnXML(XMLStr){

		if(XMLStr =="false")
			return "false";

		var xmlDoc = createDomDocument();

		xmlDoc.loadXML(XMLStr);

		var rootNode = xmlDoc.documentElement.firstChild;

		if (rootNode.getAttribute("type") =="1")
		{
			if (rootNode.getAttribute("result") =="true"){
				retXML = rootNode.xml;

				xmlDoc.free;
				return retXML;
			}else{
				alert(decode(rootNode.text));

				xmlDoc.free;
				return "false";
			}
		}else{
			if (rootNode.getAttribute("result") =="true"){
				xmlDoc.free;
				return "true";
			}else{
               	if(decode(rootNode.text).indexOf("超时")< 0){
					alert(decode(rootNode.text));
               	}else{
                    	var params = window.dialogArguments;
                         params.clear();
                         params.addNew('error','-1000','1');
                         window.returnValue = false;
                         window.close();
               	}

				xmlDoc.free;
				return "false";
			}


		}
	}

/*
 * 隐藏报表输出按钮和打印按钮
 */
function RefreshReport(){
	var viewer=document.all("MRViewer");  // MyViewer是报表对象的ID
	// 隐藏打印按钮、打印
	viewer.InvisibleButtons="Print,Separator3,Export,Separator4";
	viewer.Preview();
}

/*
 * 更新某用户的联系电话信息
 */
function getDpByArray(array,action){
	var dp = new DataPacket();
	dp.init("<root/>");
	dp.init("<root/>");
    dp.addAction();
    dp.addActionProp("actionname",action);
    dp.addActionProp("type","query");
    dp.addRecorder();
    dp.addRecorderProp("type","query");
    for (var i = 0 ; i <array.length ; i ++){
		var larray = array[i];
          var name = larray[0];
          var value = larray[1];
          var type = larray[2];
     	if(type == null){
			type = "text";
     	}
          dp.addField(name,value,type);
    }
    return dp;
}
//提示查询结果记录的个数为空
function alertTableNull(tablename){
     var count = document.getElementById(tablename).rows.length;
	if (count == 0){
		alert('没有找到满足条件的任何纪录!');
	}
}

// enter --> next focus()
function focusNext(form, elemName, evt) {
    			evt = (evt) ? evt : event;
    			var charCode = (evt.charCode) ? evt.charCode :
        			((evt.which) ? evt.which : evt.keyCode);

    			if (charCode == 13 || charCode == 3) {
        			form.elements[elemName].focus();
        			return false;
    			}
    			return true;
}

function getDownList(obj){
	var drop_frm = document.createElement("<iframe class=\"titleSpacer\" id=\"_road_frame\" style=\"position: absolute; width:140; height:200;top:0 ;left:0;visibility :hidden ; filter:blendTrans(duration=0.3);z-index : 100000\" frameBorder=\"0\"> </iframe>");
}
var i=0;
function openCenteredWindow(url) {
     var myWindow;
     var width = 1024;
     var height = 786;
     var left = parseInt((screen.availWidth/2) - (width/2));
     var top = parseInt((screen.availHeight/2) - (height/2));
     var windowFeatures = "width=" + width + ",height=" + height +
        		",status,resizable,left=" + left + ",top=" + top +
        		",screenX=" + left + ",screenY=" + top;
     myWindow = window.open(url, "subWind"+i.toString(), windowFeatures);
     i++;
    return myWindow;
}
function rejectAndUpdateInvDetailByWord12Inv(word1,word2,invno){
    	var dp = new DataPacket();
    	dp.init("<root/>");
    	dp.addAction();
    	dp.addActionProp("actionname","ruw12");
    	dp.addActionProp("type","insert");
    	dp.addRecorder();
    	dp.addRecorderProp("type","delete");
    	dp.addField("WORD1",word1,"text");
    	dp.addField("WORD2",word2,"text");
	dp.addField("INVNO",invno,"text");
	//alert(operno+':'+bussseq+':'+iscomplex);
     dp.send();
     if ( dp.isSuccess()) {
          return true;
     }else{
          alert(dp.getMessage());
          return false;
     }
	return true;
}


// utility function called by getCookie()
function getCookieVal(offset) {
    var endstr = document.cookie.indexOf (";", offset);
    if (endstr == -1) {
        endstr = document.cookie.length;
    }
    return unescape(document.cookie.substring(offset, endstr));
}

// primary function to retrieve cookie by name
function getCookie(name) {
    var arg = name + "=";

    var alen = arg.length;
    //alert(document.cookie);
    var clen = document.cookie.length;
    //alert('cookie.length='+clen);
    var i = 0;
    while (i < clen) {
        var j = i + alen;
        if (document.cookie.substring(i, j) == arg) {
            return getCookieVal(j);
        }
        i = document.cookie.indexOf(" ", i) + 1;
        if (i == 0) break;
    }
    //alert('getCookie success!');
    return "";
}

// store cookie value with optional details as needed
function setCookie(name, value, expires, path, domain, secure) {
    document.cookie = name + "=" + escape (value) +
        ((expires)?"; expires=" + expires : "") +
        ((path)?"; path=" + path : "") +
        ((domain)?"; domain=" + domain : "") +
        ((secure)?"; secure" : "");
	//alert('setCookie success!');
}

//docuemnt.getElementById()
function id2obj(id){
     return document.getElementById(id);
}

function getInt(obj){
     obj = id2obj(obj);
	if(trimStr(obj.value)==''){
      	return 0;
	}else{
     	var i = parseInt(obj.value,10);
          if(isNaN(i)){
          	alert('值非法 !请重新录入!');
          	obj.focus();
               obj.select();
          	return 0;
          }else{
           	return i;
          }
	}
}
function init_action(formelement){
     formelement = document.getElementById(formelement);

     for (var i=0; i< formelement.length; i++)
	{
		var element= formelement.item(i);
        if ( element.type=='text' ) {
        		element.onblur = calculate;
	    }
	}
}
function isDaysBetweenTooLong(s_daystart,s_dayend,days){
     var date_start = StrToDate(s_daystart);
     var date_end   = StrToDate(s_dayend);
//     alert(parseInt(date_end.getFullYear(),10)-parseInt(date_start.getFullYear(),10));
//     alert(parseInt(date_end.getMonth(),10)-parseInt(date_start.getMonth(),10));
//     alert(date_end.getDate()-date_start.getDate());
//	alert(((parseInt(date_end.getFullYear(),10)-parseInt(date_start.getFullYear(),10))*365
//     +(parseInt(date_end.getMonth(),10)-parseInt(date_start.getMonth(),10))*30
//     +(date_end.getDate()-date_start.getDate())));
     if(((parseInt(date_end.getFullYear(),10)-parseInt(date_start.getFullYear(),10))*365
     +(parseInt(date_end.getMonth(),10)-parseInt(date_start.getMonth(),10))*30
     +(date_end.getDate()-date_start.getDate()))>days){
     	alert('间隔时间不能超过'+days+'天!');
     	return false;
     }else if(((parseInt(date_end.getFullYear(),10)-parseInt(date_start.getFullYear(),10))*365
     +(parseInt(date_end.getMonth(),10)-parseInt(date_start.getMonth(),10))*30
     +(date_end.getDate()-date_start.getDate()))<0){
         	alert('开始日期不能大于结束日期！');
     	return false;
     }
     return true;
}

