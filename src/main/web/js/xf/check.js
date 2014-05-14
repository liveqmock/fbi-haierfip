function checkWinform() {
    return check(winform);
}
//---------------------------------------------------------------
//表单域有效性检查
//  
//zhouwei 2003-10-22  
//---------------------------------------------------------------

var defaultLength = -1;		//默认的长度判断
var defaultType = -1;		//默认的类型判断
var defaultInfo = '';		//默认的提示信息
//precision="14" decimalDigits="2"
var defaultPrecision = 14;	//默认的Float型的长度
var defaultDecimalDigits = 2;	//默认的Float型的精度

//---------------------------------------------------------------
//功能：检查指定的表单的所有文本数据
//参数：
//		formName -- 表单名称
//---------------------------------------------------------------

function check(formName) {

    for (var i = 0; i < formName.elements.length; i++) {

        var field = formName.elements(i);
        if (field.type == 'text' || field.type == 'textarea' || field.type == 'password')
        {
            mayNull = ((field.mayNull == null) ? true : false);
            if (field.mayNull == null) {
                mayNull = true;
            }
            else if (field.mayNull == "1") {
                mayNull = true;
            }
            else {
                mayNull = false;
            }
            //alert("length:"+field.value.length);
            //alert('1:'+field.name);
            minLength = ((field.minLength == null) ? defaultLength : field.minLength);
            dataType = ((field.dataType == null) ? defaultType : field.dataType);
            errInfo = ((field.errInfo == null) ? defaultInfo : field.errInfo);
            precision = ((field.precision == null || field.precision == 0) ? defaultPrecision : field.precision);
            decimalDigits = ((field.decimalDigits == null || field.decimalDigits == 0) ? defaultDecimalDigits : field.decimalDigits);
            //解决内存变量类型的控件值检验
            /*if((dataType ==3)&&((field.precision == '0' && field.decimalDigits== '0')||(){
             precision = 2;
             }*/
            if (!checkText(field, mayNull, minLength, dataType, errInfo, precision, decimalDigits)) {

                return false;
            }
        }
    }
    return true;
}

//--------------------------------------------------------------
//功能：检查指定的字段的有效性
//参数：
//		field -- 待检查字段
//		mayNull -- 可以为空（true/false，未指定则为true）
//		minLength -- 最小长度（未指定则为默认值-defaultLength）
//		dataType -- 数据类型（未指定则为默认值-defaultType）
//		errInfo -- 错误提示信息（未指定则为默认值-defaultInfo）
//--------------------------------------------------------------
function checkText(field, mayNull, minLength, dataType, errInfo, precision, decimalDigits) {
    //alert(field.name);

    if (mayNull && field.value.length == 0)
        return true;

    if (!mayNull && (field.value.length == 0 || !hasVisibleChar(field.value))) {
        errInfo = errInfo + '不能为空';
        infoAlert(field, errInfo);
        return false;
    }

    //var cncharnum = (escape(field.value).length - field.value.length)/5;
    //var fieldlength = field.value.length+cncharnum;
    //alert(field.value.length +'\r'+ fieldlength +'\r'+ escape(field.value));

    //alert("'"+trim(field.value)+"'");
    var fieldlength = getMaxLength(trim(field.value));
    field.value = trim(field.value);
    //alert(fieldlength);
    if ((field.type == 'text' && fieldlength > field.maxLength) || (field.type == 'textarea' && fieldlength > field.maxlength)) {
        errInfo = errInfo + '长度太长';
        infoAlert(field, errInfo);
        return false;
    }
    if (fieldlength < minLength)
    {
        errInfo = errInfo + '长度太短';
        infoAlert(field, errInfo);
        return false;
    } else {
        if (dataType != -1)
        {
            if (dataType == 2) {
                if (!isInteger(field.value)) {
                    errInfo = errInfo + '非法的整数';
                    infoAlert(field, errInfo);
                    return false;
                }
                if (isTooBigInt(field.value)) {
                    errInfo = errInfo + '过大的整数';
                    infoAlert(field, errInfo);
                    return false;
                }
            }
            if (dataType == 3) {

                if (!isFloat(field.value)) {
                    errInfo = errInfo + '非法的数字';
                    infoAlert(field, errInfo);
                    return false;
                } else {
                    if (precision == -1) {
                        return true;
                    } else if (decimalDigits == -1) {
                        decimalDigits = 0;
                    }

                    var integerLength = precision - decimalDigits;
                    //// sdj
                    /*if(integerLength > 0){
                     if(beforeDotLength(field.value) > integerLength){
                     errInfo = errInfo+'非法的数字，整数部分超长';
                     infoAlert(field,errInfo);
                     return false;
                     }
                     }*/
                    ///liwei
                    //alert(Math.pow(10,integerLength)-Math.pow(10,(-decimalDigits)));
                    //if(field.value<0.00 || field.value >(Math.pow(10,integerLength)-Math.pow(10,(-decimalDigits)))){
                    if (field.value > (Math.pow(10, integerLength) - Math.pow(10, (-decimalDigits)))) {
                        errInfo = errInfo + '非法的数字，整数部分超长';
                        infoAlert(field, errInfo);
                        return false;
                    }

                }
            }
            if (dataType == 5) {
                if (!isDate(field.value)) {
                    errInfo = errInfo + '非法的日期';
                    infoAlert(field, errInfo);
                    return false;
                }
            }
            if (dataType == 8) {
                if (!isEmail(field.value)) {
                    errInfo = errInfo + '非法的邮件地址';
                    infoAlert(field, errInfo);
                    return false;
                }
            }

        }
    }

    return true;
}

//---------------------------------------------------------------
//功能：检查是否为整数类型
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function isInteger(inputString)
{
    return (!isNaN(parseInt(inputString)) && onlyNumbers(inputString)) ? true : false;
}

//---------------------------------------------------------------
//功能：检查是否为浮点类型
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function isFloat(inputString)
{
    return (!isNaN(parseFloat(inputString)) && onlyNumbersAndDot(inputString)) ? true : false;
}

//---------------------------------------------------------------
//功能：检查是否为时间类型
//模式：02/02/2002
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function isDate(inputString)
{
    if (inputString.length != 8)
    {
        return false;
    }
    else
    {
        year1 = parseInt(inputString.substring(0, 4), 10);
        month1 = parseInt(inputString.substring(4, 6), 10) - 1;
        day1 = parseInt(inputString.substr(6, 8), 10);
        //alert(year1+"-"+month1+"-"+day1);
        date1 = new Date(year1, month1, day1);
        if (isNaN(date1))
        {
            return false;
        }
        else
        {
            year2 = date1.getFullYear();
            month2 = date1.getMonth();
            day2 = date1.getDate();
            if ((year2 != year1) || (month2 != month1) || (day2 != day1))
            {
                return false;
            }
        }
    }
    return true;
}

//---------------------------------------------------------------
//功能：检查是否为年月类型 例：200506
//模式：02/02/2002
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function isDate2(inputString)
{
    if (inputString.length != 6)
    {
        return false;
    }
    else
    {
        year1 = parseInt(inputString.substring(0, 4), 10);
        month1 = parseInt(inputString.substring(4, 6), 10) - 1;
        //day1 = parseInt(inputString.substr(6,8),10);
        //alert(year1+"-"+month1+"-"+day1);
        date1 = new Date(year1, month1);
        if (isNaN(date1))
        {
            return false;
        }
        else
        {
            year2 = date1.getFullYear();
            month2 = date1.getMonth();
            //day2 = date1.getDate();
            if ((year2 != year1) || (month2 != month1))//|| (day2!=day1))
            {
                return false;
            }
        }
    }
    return true;
}

//---------------------------------------------------------------
//功能：检查是否为电子邮件类型
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function isEmail(inputString)
{
    var atIndex = inputString.indexOf('@');
    var dotIndex = inputString.indexOf('.', atIndex);
    theSub = inputString.substring(0, dotIndex + 1);
    if ((atIndex < 1) || (atIndex != inputString.lastIndexOf('@')) || (dotIndex < atIndex + 2) || (inputString.length <= theSub.length))
    {
        return false;
    }
    return true;
}

//---------------------------------------------------------------
//功能：显示错误提示信息
//参数：
//		field -- 对应的字段
//		errInfo -- 提示信息
//---------------------------------------------------------------
function infoAlert(field, errInfo)
{
    alert(errInfo);
    field.select();
    field.clear = 1;
}

//---------------------------------------------------------------
//功能：检查是否为全数字
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function onlyNumbers(inputString)
{
    allNum = "0123456789";
    for (var i = 0; i < inputString.length; i++)
    {
        cIdx = inputString.substring(i, i + 1);
        if (allNum.indexOf(cIdx) < 0)
            return false;
    }
    return true;
}

//---------------------------------------------------------------
//功能：检查是否为全数字或小数点（只有一个）
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function onlyNumbersAndDot(inputString)
{
    allNum = "0123456789.-";
    for (var i = 0; i < inputString.length; i++)
    {
        cIdx = inputString.substring(i, i + 1);
        if (allNum.indexOf(cIdx) < 0)
            return false;
    }

    if (inputString.indexOf('.') != inputString.lastIndexOf('.'))
        return false;

    return true;
}

//---------------------------------------------------------------
//功能：浮点数小数点前位数
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function beforeDotLength(inputString)
{
    dotI = inputString.indexOf('.');
    if (dotI < 0)
        return inputString.length;
    else
        return inputString.substring(0, dotI).length;
}

//---------------------------------------------------------------
//功能：浮点数小数点后位数
//参数：
//		inputString -- 输入值
//---------------------------------------------------------------
function afterDotLength(inputString)
{
    dotI = inputString.indexOf('.');
    if (dotI < 0)
        return 0;
    else
        return inputString.substring(dotI + 1).length;
}
function hasVisibleChar(inputString) {
    //alert(inputString.length);
    //alert('before length:'+inputString.length+'  value:'+inputString+ '\rafter length:'+escape(inputString).length+' value:'+escape(inputString));

    //alert();
    for (var i = 0; i < inputString.length; i++) {
        if (inputString.charAt(i) == ' ' || inputString.charAt(i) == '\r' || inputString.charAt(i) == '\n'
                || inputString.charAt(i) == '\t') {
            continue;//break;
        } else {
            return true;
        }

    }
    return false;
}

function getMaxLength(inputString) {
    var maxlength = 0;
    for (var i = 0; i < inputString.length; i++) {
        if (escape(inputString.charAt(i)).length == 6) {
            maxlength = maxlength + 2;
        } else {
            maxlength = maxlength + 1;
        }
    }
    return maxlength;
}


function firstIndexOfVisibleChar(inputString) {
    var i;
    for (i = 0; i < inputString.length; i++) {
        if (inputString.charAt(i) != ' ') {
            break;
        }
    }
    return i;
}

function lastIndexOfVisibleChar(inputString) {
    var i;
    for (i = inputString.length - 1; i < inputString.length; i--) {
        if (inputString.charAt(i) != ' ') {
            break;
        }
    }
    return i + 1;
}

function trim(inputString) {
    if (hasVisibleChar(inputString)) {
        var begin = firstIndexOfVisibleChar(inputString);
        var end = lastIndexOfVisibleChar(inputString);
        return inputString.substring(begin, end);
    } else {
        return '';
    }

}

function isTooBigInt(aValue) {
    var maxValue = Math.pow(2, 31) - 1;
    //alert(maxValue);
    if (parseInt(aValue) >= maxValue) {
        return true;
    } else {
        return false;
    }
}

function firstDateNotLaterThanSecond(date1obj, date2obj, name1, name2) {
    var idate1 = 0;
    var idate2 = 0;
    if (date1obj != "undefined" && date2obj != "undefined") {
        idate1 = date1obj.value;
        idate2 = date2obj.value;
    } else {
        alert('输入参数不存在.');
        return false;
    }
    if (idate1 != 0 && idate2 != 0) {
        if (idate1 > idate2) {
            alert('"' + name2 + '"' + '早于' + '"' + name1 + '"');
            return false;
        } else {
            return true;
        }
    } else {
        alert('时间格式不正确.');
        return false;
    }
}

function validNumber(aMumber, integerLength, decimalDigits) {
    if (onlyNumbersAndDot(aMumber)) {
        if (aMumber < 0 || aMumber > (Math.pow(10, integerLength) - Math.pow(10, (-decimalDigits)))) {
            return false;
        } else {
            return true;
        }
    } else {
        return false;
    }
}

function EntertoFocusNextX()
{
    try
    {
        if (true)
        {
            //alert(event.srcElement.type.toLowerCase());
            if (typeof(event.srcElement.name) != "undefined" && event.srcElement.name != null && (event.srcElement.type.toLowerCase() == 'text' || event.srcElement.type.toLowerCase().indexOf('select') >= 0 ))
            {
                var i = 0;
                for (; i < document.all.length; i++)
                {
                    if (document.all[i].name == event.srcElement.name)
                    {
                        break;
                    }
                }

                if (i < document.all.length)
                {
                    for (t = i + 1; t < document.all.length; t++)
                    {
                        if (document.all[t].name != null && document.all[t].style.visibility != "hidden" && document.all[t].type != "hidden" && document.all[t].readOnly != true && document.all[t].disabled != true)
                        {
                            //alert("found");
                            document.all[t].focus();
                            break;
                        }
                    }
                } //end if

                event.returnValue = false;

            }
        }
    }
    catch (Error) {
    }
    ;
}
//添加keydown事件
function ctlkey() {
    //
    if ((event.ctrlKey && window.event.keyCode == 13)) {
        //alert("key down");
        //pressSaveButton(winform.Plat_Form_Request_Event_ID,2);

        //if ( document.all.addbtn != "undefined" ) {
        //		document.all.addbtn.click();
        //	}
        //if ( document.all.searchbtn != "undefined" ) {
        //		document.all.searchbtn.click();
        //	}
        if (document.all.savebtn != "undefined") {
            document.all.savebtn.click();
        }
    }
    else if (window.event.keyCode == 13)
    {
        EntertoFocusNextX();
    }
    //if(event.ctrlKey && window.event.keyCode==13){submitonce(document.form1);document.form1.submit();}
    //if(event.altKey && (window.event.keyCode==83 || window.event.keyCode==115)){submitonce(document.form1);document.form1.submit();}
}
var ie = (document.all) ? true : false
if (ie)
{
    window.document.onkeydown = ctlkey;
}


//*********************************
// jsp表单检查字段为空情况
// Note：只可用于名称-值紧邻两列情况
// author：liujian
//*********************************
function inputObjType(obj) {
    var tp = "";
    switch (obj[0].tagName) {//标签类型
        case "INPUT":
            tp = obj[0].getAttribute("type");
            break;
        case "SELECT":
            tp = obj[0].getAttribute("type");
            break;
        case "TEXTAREA":
            tp = "textarea";
            break;
        default: tp = "textarea";
    }
    return tp;
}

function inputObjValue(obj) {
    var obv = "";
    //alert(obj[0].getAttribute("type"));
    if (inputObjType(obj) == "checkbox" || inputObjType(obj) == "radio") {
        for (var i = 0; i < obj.length; i++) {
            if (obj[i].checked == true) {
                obv = '_' + obj[i].id;
            }
        }
    } else if (inputObjType(obj) == "select-one" || inputObjType(obj) == "text" || inputObjType(obj) == "textarea" || inputObjType(obj) == "password") {
        obv = obj[0].value.Trim();
    }
    return obv.replace("_","");
}

function inputObjpreObj(obj) {
    var pobj = obj[0].parentNode.previousSibling;

    if (inputObjType(obj) == "checkbox" || inputObjType(obj) == "radio") {
        pobj = obj[0].parentNode.parentNode.previousSibling;
    }
    return pobj;
}


function isEmptyItem(str) {
    var obj = document.getElementsByName(str);
    if (inputObjValue(obj) == '') {
        alert(inputObjpreObj(obj).innerText.replace(/\r\n/g,"").replace(/ /g,"") + ' 不能为空！');
        obj[0].focus();
        return false;
    }
    return true;
}

function isEmptyItem1(str,msg) {
    var obj = document.getElementsByName(str);
    if (inputObjValue(obj) == '') {
        alert(msg.replace(/\r\n/g,"").replace(/ /g,""));
        obj[0].focus();
        return false;
    }
    return true;
}

function isPhone(str) {
    var obj = document.getElementsByName(str);
    var val=inputObjValue(obj);
    if (val != ''&&!checkPhone(obj[0])){
//        alert(inputObjpreObj(obj).innerText.replace(/\r\n/g,"").replace(/ /g,"") + ' 不是有效的电话号码！\n如有区号，请添加‘-’');
        alert(inputObjpreObj(obj).innerText.replace(/\r\n/g,"").replace(/ /g,"") + ' 不是有效的电话号码！');
        obj[0].focus();
        return false;
    }
    return true;
}

function isZipCode(str) {
    var obj = document.getElementsByName(str);
    var val=inputObjValue(obj);
    if (val != ''&&!ChkUtil.isZipCode(val)){
//        alert(inputObjpreObj(obj).innerText.replace(/\r\n/g,"").replace(/ /g,"") + ' 不是有效的电话号码！\n如有区号，请添加‘-’');
        alert(inputObjpreObj(obj).innerText.replace(/\r\n/g,"").replace(/ /g,"") + ' 不是有效的邮政编码！');
        obj[0].focus();
        return false;
    }
    return true;
}

function isEmail(str) {
    var obj = document.getElementsByName(str);
    var val=inputObjValue(obj);
    if (val != ''&&!ChkUtil.isEmail(val)){
//        alert(inputObjpreObj(obj).innerText.replace(/\r\n/g,"").replace(/ /g,"") + ' 不是有效的电话号码！\n如有区号，请添加‘-’');
        alert(inputObjpreObj(obj).innerText.replace(/\r\n/g,"").replace(/ /g,"") + ' 不是有效的电子邮件地址！');
        obj[0].focus();
        return false;
    }
    return true;
}
//*********************************
// jsp表单检查字段为空情况 end
//*********************************


//返回实际数据长度（中文占2位）
String.prototype.getBytes = function() {
    var cArr = this.match(/[^\x00-\xff]/ig);
    return this.length + (cArr == null ? 0 : cArr.length);
} 