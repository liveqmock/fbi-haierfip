/******************************字符串操作处理函数******************************/
// Trim() , Ltrim() , RTrim() 函数
String.prototype.Trim = function() {
    return this.replace(/(^\s*)|(\s*$)/g, "");
}
String.prototype.LTrim = function() {
    return this.replace(/(^\s*)/g, "");
}
String.prototype.RTrim = function() {
    return this.replace(/(\s*$)/g, "");
}
// Trim() , Ltrim() , RTrim() 函数
function Trim(str) {
    return str.replace(/(^\s*)|(\s*$)/g, "");
}
function LTrim(p_str) {
    var str = p_str;
    if (str.charAt(0) == " ") {
        str = str.slice(1);
        str = LTrim(str);
    }
    return str;
}
function RTrim(str) {
    return str.replace(/(\s*$)/g, "");
}
/******************************************************************************/

/*************************表单数据提交前有效性检查函数*************************/

//判断是否是空
function isEmpty(p_obj) {
    var obj = eval(p_obj);
    if (LTrim(obj.value) == "") {
        return true;
    }
    return false;
}
//判断长度
function checkMaxLength(p_obj, p_maxlength) {
    if (!p_obj) return false;
    //empty则不检查
    if (isEmpty(p_obj)) return false;
    var txtlength = p_obj.value.length;
    var rellength = 0;
    for (var i = 0; i < txtlength; i++) {
        if (escape(p_obj.value.charAt(i)).length == 6) {
            rellength += 2;
        }
        else {
            rellength += 1;
        }
    }
    if (rellength > parseInt(p_maxlength)) {
        return false;
    }
    return true;
}

//判断是否是int类型,可以为负数
function checkInteger(p_obj) {
    var strref = "1234567890-";
    if (isEmpty(p_obj)) return false;
    if (LTrim(p_obj.value) == '-') {
        return false;
    }
    for (i = 0; i < p_obj.value.length; i++) {
        var tmpchar = p_obj.value.substring(i, i + 1);
        if (strref.indexOf(tmpchar, 0) == -1) {
            return false;
        }
        if (i > 0 && tmpchar == "-") {
            return false;
        }
    }
    return true;
}
//判断是否是decimal类型,数字可以为负数
function checkDecimal(p_obj) {
    var strref = "1234567890-.";
    if (isEmpty(p_obj)) return false;
    if (p_obj.value == '-') return false;
    if (p_obj.value == '.') return false;
    var j = 0;
    for (i = 0; i < p_obj.value.length; i++) {
        var tmpchar = p_obj.value.substring(i, i + 1);
        if (strref.indexOf(tmpchar, 0) == -1) {
            return false;
        }
        else {
            if (tmpchar == ".") {
                j++;
                //只能有一个.
                if (j > 1) return false;
                //.后应该有数字
                if (i == (p_obj.value.length - 1)) return false;
            }
            if (i > 0 && tmpchar == "-") {
                return false;
            }
        }
    }
    return true;
}
//判断是否闰年
function isLeapYear(year) {
    if ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)) {
        return true;
    }
    return false;
}
//判断日期是否正确
function checkDate(p_obj) {
    var datetime;
    if (isEmpty(p_obj))return false;
    datetime = Trim(p_obj.value);
    checkDate_str(datetime);
    return true;
}

//判断日期是否正确
function checkDate_str(datetime) {
    var year,month,day;

    datetime = Trim(datetime);
    if (datetime.length == 0)return false;

    //长度8位检查
    if (datetime.length != 8) {
        return false;
    }
    year = datetime.substring(0, 4);
    if (isNaN(year) == true) {
        return false;
    }
    month = datetime.substring(4, 6);
    if (isNaN(month) == true) {
        return false;
    }
    day = datetime.substring(6, 8);
    if (isNaN(day) == true) {
        return false;
    }
    if (month < 1 || month > 12) {
        return false;
    }
    if (day < 1 || day > 31) {
        return false;
    }
    else {
        if (month == 2) {
            if (isLeapYear(year) && day > 29) {
                return false;
            }
            if (!isLeapYear(year) && day > 28) {
                return false;
            }
        }
        if ((month == 4 || month == 6 || month == 9 || month == 11) && (day > 30)) {
            return false;
        }
    }
    return true;
}

//判断只含年月的日期是否正确           添加 sunzg  05-5-17
function checkDate2(p_obj) {
    var datetime;
    var year,month;
    if (isEmpty(p_obj))return false;
    datetime = Trim(p_obj.value);
    //长度8位检查
    if (datetime.length != 6) {
        return false;
    }
    year = datetime.substring(0, 4);
    if (isNaN(year) == true) {
        return false;
    }
    month = datetime.substring(4, 6);
    if (isNaN(month) == true) {
        return false;
    }

    if (month < 1 || month > 12) {
        return false;
    }
    return true;
}

//判断两个日期的先后关系
function compTwinDate(val_bgndt, val_enddt) {
    if (val_bgndt.Trim() == '' || val_enddt.Trim() == '') {
        return true;
    }
    if (val_bgndt >= val_enddt) {
        return false;
    }
    return true;
}

//表单数据手工检查函数，由formDataAutoCheck调起，由每个表单改写此函数实现自己的逻辑
//注意：每个表单改写此函数时位置一定在引用main.js后面
function formDataHandCheck() {
    return true;
}
//表单数据自动检查主函数，由按钮响应事件调起
function formDataAutoCheck(p_frmnm) {
    var frmobj = document.all[p_frmnm];
    var allname;
    for (var i = 0; i < frmobj.length; i++) {
        var obj = frmobj.item(i);
        allname = obj.name;
        //只读类型不检查
        if (obj.getAttribute("readonly")) continue;
        //nulls
        if (obj.getAttribute("nulls") == "n") {
            if (isEmpty(obj)) {
                alert("必添项不能为空!");
                obj.focus();
                return false;
            }
        }
        //not empty
        if (!isEmpty(obj)) {
            //char
            if (obj.getAttribute("coltype") == "char") {
                var maxlen = obj.getAttribute("maxlength");
                if (maxlen == null || maxlen.length < 1) continue;
                if (!checkMaxLength(obj, maxlen)) {
                    alert("长度超出了限制长度！");
                    obj.focus();
                    return false;
                }
            }
            //int
            else if (obj.getAttribute("coltype") == "int") {
                if (!checkInteger(obj)) {
                    alert("整数数据输入错误!");
                    obj.focus();
                    return false;
                }
            }
            //deci
            else if (obj.getAttribute("coltype") == "deci") {
                if (!checkDecimal(obj)) {
                    alert("数字数据输入错误!");
                    obj.focus();
                    return false;
                }
            }
            //date
            else if (obj.getAttribute("coltype") == "date") {
                if (!checkDate(obj)) {
                    alert("日期格式输入错误!");
                    obj.focus();
                    return false;
                }
            }
            //date2
            else if (obj.getAttribute("coltype") == "date2") {
                if (!checkDate2(obj)) {
                    alert("月份格式输入错误!");
                    obj.focus();
                    return false;
                }
            }
        }
    }
    //表单内手工检查部分
    return formDataHandCheck();
}
/******************************************************************************/

/*************************页面中系统功能按钮的响应函数*************************/
//在点击提交、更新、删除、刷新按钮时，使所有的按钮失效，防止网络慢造成重复点击
function DisableAllButtion() {
    if (document.form1) {
        for (var i = 0; i < document.form1.elements.length; i++) {
            var elem = document.form1.elements[i];
            if (elem.type.toLowerCase() == "button")
                elem.disabled = true;
        }
    }
    if (document.all("btn_add")) document.all("btn_add").disabled = true;
    if (document.all("btn_mod")) document.all("btn_mod").disabled = true;
    if (document.all("btn_del")) document.all("btn_del").disabled = true;
    if (document.all("btn_fsh")) document.all("btn_fsh").disabled = true;
    if (document.all("btn_ext")) document.all("btn_ext").disabled = true;
    if (document.all("btn_sav")) document.all("btn_sav").disabled = true;
    if (document.all("btn_cmt")) document.all("btn_cmt").disabled = true;
    if (document.all("btn_prt")) document.all("btn_prt").disabled = true;
    if (document.all("SystemButton1")) document.all("SystemButton1").disabled = true;
    if (document.all("SystemButton2")) document.all("SystemButton2").disabled = true;
    if (document.all("SystemButton3")) document.all("SystemButton3").disabled = true;
    if (document.all("SystemButton4")) document.all("SystemButton4").disabled = true;
    if (document.all("SystemButton5")) document.all("SystemButton5").disabled = true;
    if (document.all("SystemButton6")) document.all("SystemButton6").disabled = true;
}
//提交按钮点击事件
function btn_add_click() {
    if (!formDataAutoCheck('form1')) return false;
    document.all("act").value = "add";
    document.all("MessageDiv").innerText = "数据添加中，请等待...";
    DisableAllButtion();
    document.all("form1").submit();
    return true;
}

//更新按钮点击事件
function btn_mod_click() {
    if (!formDataAutoCheck('form1')) return false;
    document.all("act").value = "mod";
    document.all("MessageDiv").innerText = "数据更新中，请等待...";
    DisableAllButtion();
    document.all("form1").submit();
    return true;
}
//删除按钮点击事件
function btn_del_click() {
    if (!confirm("你确定要删除吗？")) {
        return false;
    }
    document.all("act").value = "del";
    document.all("MessageDiv").innerText = "数据删除中，请等待...";
    DisableAllButtion();
    document.all("form1").submit();
    return true;
}
//保存按钮点击事件
function btn_sav_click() {
    if (!formDataAutoCheck('form1')) return false;
    document.all("MessageDiv").innerText = "数据保存中，请等待...";
    DisableAllButtion();
    document.all("form1").submit();
    return true;
}
//提交按钮点击事件
function btn_cmt_click() {
    if (!confirm("你确定要提交吗？")) {
        return false;
    }
    if (!formDataAutoCheck('form1')) return false;
    document.all("MessageDiv").innerText = "数据提交中，请等待...";
    if (document.all("iscmt")) {
        document.all("iscmt").value = "1";
    }
    DisableAllButtion();
    document.all("form1").submit();
    return true;
}
//刷新按钮点击事件
function btn_fsh_click() {
    DisableAllButtion();
    window.location.reload();
}
//退出按钮点击事件
function btn_ext_click() {
    window.close();
}
//打开一个新窗口
function openNewWindow(url) {
    var xx = (window.screen.width - 600) / 2;
    var yy = (window.screen.height - 450) / 2;
    window.open(url, '', 'left=' + xx + ',top=' + yy + ',height=450, width=600,toolbar=no, menubar=no, scrollbars=no,resizable=no,location=no, status=no');
}

function open5sortWindow(url) {
    var xx = (window.screen.width - 600) / 2;
    var yy = (window.screen.height - 450) / 2;
    window.open(url, '', 'left=' + xx + ',top=' + yy + ',height=500, width=800,toolbar=no, menubar=no, scrollbars=yes,resizable=yes,location=no, status=no');
}
function openNewWindowByTitle(url, wid, hgt, title) {
    if (wid == undefined) wid = 600;
    if (hgt == undefined) hgt = 450
    var xx = (window.screen.width - wid) / 2;
    var yy = ((window.screen.height - 28) - hgt) / 2;
    if (xx < 0) xx = 0;
    if (yy < 0) yy = 0;
    //20050425因为从历史审批时打开有问题
    //window.open (url, title, 'left='+xx+',top='+yy+',width='+width+',height='+height+', toolbar=no, menubar=no, scrollbars=yes,resizable=no,location=no, status=no');
    window.open(url, '', 'left=' + xx + ',top=' + yy + ',width=' + wid + ',height=' + hgt + ', toolbar=no, menubar=no, scrollbars=yes,resizable=no,location=no, status=no');
}
function openNewWindow1(url, width, height) {
    openNewWindowByTitle(url, width, height, "new1");
}
function openNewWindow2(url, width, height) {
    openNewWindowByTitle(url, width, height, "new2");
}
function openNewWindow3(url, width, height) {
    openNewWindowByTitle(url, width, height, "new3");
}
function openNewWindow4(url, width, height) {
    openNewWindowByTitle(url, width, height, "new4");
}
//弹出参照窗口
function openRefWindow(url) {
    window.showModalDialog(url, window, "dialogWidth=500px;dialogHeight=540px;help=no;status=no;edge=raised");
}
/**
 * 显示对话框
 * 2011-07-13 haiyu
 */
function dialog(url, arg, sfeature) {
    var sfeature = sfeature;
    var date = new Date();
    // 设置URL扩展，避免IE没有设置每次刷新，导致显示旧页面内容
    var time = getDateString(date) + " " + getTimeString(date);
    if (url.indexOf("?") > 0) {
        url += "&showDialogTime=" + time;
    } else {
        url += "?&showDialogTime=" + time;
    }
    return window.showModalDialog(url, arg, sfeature);
    ;
}

/**
 * 获取日期   2011-07-13 haiyu
 */
function getDateString(date) {
    var years = date.getFullYear();
    var months = date.getMonth() + 1;
    var days = date.getDate();

    if (months < 10)
        months = "0" + months;
    if (days < 10)
        days = "0" + days;

    return years + "-" + months + "-" + days;
}

/**
 * 获取时间   2011-07-13 haiyu
 */
function getTimeString(date) {
    var hours = date.getHours();
    var minutes = date.getMinutes();
    var seconds = date.getSeconds();

    if (hours < 10)
        hours = "0" + hours;
    if (minutes < 10)
        minutes = "0" + minutes;
    if (seconds < 10)
        seconds = "0" + seconds;

    return hours + ":" + minutes + ":" + seconds;
}


/******************************************************************************/
/**
 * 计算执行利率 的java script 公式
 * 2005-04-02  sunzg add
 * @param baserate 基本利率
 * @param floatrate 最低利率浮动幅度
 * @return
 */
function getExecRate(baserate, floatrate) {
    var x = parseFloat(baserate);
    var y = parseFloat(floatrate);
    var z = x * (y / 100 + 1);
    return z.toFixed(4);
}

//用户修改密码检查
function checkPWD(obj1, obj2) {
    if (obj1.value.length <= 0) {
        alert("用户密码不能为空！");
        obj1.focus();
        obj1.select();
        return false;
    }
    else if (obj2.value.length <= 0) {
        alert("确认密码不能为空！");
        obj2.focus();
        obj2.select();
        return false;
    }
    else if (obj1.value != obj2.value) {
        alert("用户密码和确认密码不相同！");
        obj2.focus();
        obj2.select();
        return false;
    }
    return true;
}


//提交到本页面,用于使用refer选择信息后带入相关资料
function selectDo() {
    document.all.form1.action = document.all.url.value;
    document.all.form1.submit();
}

/*
 * load时将焦点定位到form中的指定字段
 *
 * lj added in 20090409
 */
function setLoadFocus(obj) {
    document.forms[0].onload = setFocus(obj);
}

function setFocus(obj) {
    document.getElementsByName(obj)[0].focus();
}

//lj added in 20090409

//判断是否是decimal类型,数字可以为负数,可以为空
function checkDec(p_obj) {
    var strref = "1234567890-.";
    //    if (isEmpty(p_obj)) return false;
    if (p_obj.value == '-') return false;
    if (p_obj.value == '.') return false;
    var j = 0;
    for (var i = 0; i < p_obj.value.length; i++) {
        var tmpchar = p_obj.value.substring(i, i + 1);
        if (strref.indexOf(tmpchar, 0) == -1) {
            p_obj.value = parseFloat(p_obj.value);
            return false;
        }
        else {
            if (tmpchar == ".") {
                j++;
                //只能有一个.
                if (j > 1) {
                    p_obj.value = parseFloat(p_obj.value);
                    return false;
                }
                //.后应该有数字
                //if (i == (p_obj.value.length - 1)) return false;
                if (isNaN(parseFloat(p_obj.value))) {
                    p_obj.value = parseFloat(p_obj.value);
                    return false;
                }
            }
            if (i > 0 && tmpchar == "-") {
                p_obj.value = parseFloat(p_obj.value);
                return false;
            }
        }
    }
    return true;
}

//判断是否是int类型,可以为负数,可以为空
function checkInt(p_obj) {
    var strref = "1234567890-";
    //    if (isEmpty(p_obj)) return false;
    if (LTrim(p_obj.value) == '-') {
        return false;
    }
    for (i = 0; i < p_obj.value.length; i++) {
        var tmpchar = p_obj.value.substring(i, i + 1);
        if (strref.indexOf(tmpchar, 0) == -1) {
            return false;
        }
        if (i > 0 && tmpchar == "-") {
            return false;
        }
    }
    return true;
}

//判断是否是Phone类型,可以为空
function checkPhone(p_obj) {
    var strref = "1234567890-";
    if (p_obj.value == '-') return false;
    var j = 0;
    for (var i = 0; i < p_obj.value.length; i++) {
        var tmpchar = p_obj.value.substring(i, i + 1);
        if (strref.indexOf(tmpchar, 0) == -1) {
            return false;
        }
        else if (tmpchar == "-" && (i == 0 || i == p_obj.value.length - 1)) {
            return false;
        }
    }
    return true;
}

function chkdecWithAlStr(str, alstr) {//obj name
    var obj = document.getElementsByName(str)[0];
    if (!checkDec(obj)) {
        if (alstr == null)alstr = "数字数据输入错误!";
        alert(alstr);
        obj.focus();
        return false;
    }
    return true;
}

function chkintWithAlStr(str, alstr) {//obj name
    var obj = document.getElementsByName(str)[0];
    if (!checkInt(obj)) {
        if (alstr == null)alstr = "整形数据输入错误!";
        alert(alstr);
        obj.focus();
        return false;
    }
    return true;
}

function chkdec(str) {//obj name
    return chkdecWithAlStr(str, null);
}
function chkint(str) {//obj name
    return chkintWithAlStr(str, null);
}

/**
 * 返回兼容IE和FF的event
 */
function getEvent() {
    if (document.all) {
        return window.event;//如果是ie
    }
    var func = getEvent.caller;
    while (func != null) {
        var arg0 = func.arguments[0];
        if (arg0) {
            if ((arg0.constructor == Event || arg0.constructor == MouseEvent)
                    || (typeof(arg0) == "object" && arg0.preventDefault && arg0.stopPropagation)) {
                return arg0;
            }
        }
        func = func.caller;
    }
    return null;
}
/**
 * 返回兼容IE和FF的element
 */
function getElement() {
    var evt = getEvent();
    var element = evt.srcElement || evt.target;
    return element;
}
/**
 * 添加事件*/
function addEventHandler(target,type,func) {
    if (target.addEventListener) {
        taget.addEventListener(type,func,false);
    } else if (target.attachEvent) {
        target.attachEvent("on" + type,func);
    } else {
        target["on" + type] = func;
    }
}
/**
 * 创建element*/

function createEl(type,name) {
    var element = null;
    try {
        // First try the IE way; if this fails then use the standard way
        element = document.createElement('<'+type+' name="'+name+'">');
    } catch(e) {}
    if (!element) {
        element = document.createElement(type);
        element.name = name;
    }
    return element;
}
/**
 * 创建商品录入行*/
function appendCommRow(tabObj) {
    var row = tabObj.insertRow(tabObj.rows.length);
    var idNo = tabObj.rows.length - 2;  //取得新加行商品序列号
    row.className = "page_form_tr";
    for (var i = 1; i < 7; i++) {
        var cell = row.insertCell();
        if (i == 1) {
            cell.style.display = "none";
            var inputElNo = createEl("input","noCOMMNO");
            inputElNo.type = "text";
            inputElNo.id = "COMMNO" + idNo;
            inputElNo.value = idNo;
            cell.appendChild(inputElNo);
        } else if (i == 2) {
            cell.className = "page_form_td";
            var inputElNY = createEl("input","noCOMMNMTYPE");
            inputElNY.type = "text";
            inputElNY.id = "COMMNMTYPE" + idNo;
            inputElNY.style.width = "190px";
            inputElNY.value = "";
            addEventHandler(inputElNY,"keyup",function(){checkCommExtend();});
            cell.appendChild(inputElNY);
        } else if (i == 3) {
            cell.className = "page_form_td";
            var lblEl1 = document.createElement('label');
            var inputEl1 = createEl("input","APPTYPE"+idNo);
            inputEl1.type="radio";
            inputEl1.value="1";
            inputEl1.className = "page_form_radio";
            lblEl1.appendChild(inputEl1);
            lblEl1.innerHTML = lblEl1.innerHTML + "家电";
//            lblEl1.innerText = "家电";
            var lblEl2 = document.createElement('label');
//            lblEl2.innerText = "整体厨房";
            var inputEl2 = createEl("input","APPTYPE"+idNo);
            inputEl2.type="radio";
            inputEl2.value="2";
            inputEl2.className = "page_form_radio";
            lblEl2.appendChild(inputEl2);
            lblEl2.innerHTML = lblEl2.innerHTML + "整体厨房";
            cell.appendChild(lblEl1);
            cell.appendChild(lblEl2);
        } else if (i == 4) {
            cell.className = "page_form_td";
            var inputEl = createEl("input","noNUM");
            inputEl.type = "text";
            inputEl.id = "NUM" + idNo;
            inputEl.style.width = "70px";
            inputEl.value = "";
            addEventHandler(inputEl,"keyup",function(){checkCommExtend();});
            addEventHandler(inputEl,"blur",function(){countTotalNumExtd('TOTALNUM');});
            cell.appendChild(inputEl);
        } else if (i == 5) {
            cell.className = "page_form_td";
            var inputEl = createEl("input","noAMT");
            inputEl.type = "text";
            inputEl.id = "AMT" + idNo;
            inputEl.style.width = "100px";
            inputEl.value = "";
            addEventHandler(inputEl,"keyup",function(){checkCommExtend();});
            addEventHandler(inputEl,"blur",function(){countTotalNumExtd('TOTALAMT');});
            cell.appendChild(inputEl);
        } else if (i == 6) {
            cell.className = "page_form_td";
            var inputEl = createEl("input","noRECEIVEAMT");
            inputEl.type = "text";
            inputEl.id = "RECEIVEAMT" + idNo;
            inputEl.style.width = "100px";
            inputEl.value = "";
            addEventHandler(inputEl,"keyup",function(){checkCommExtend();});
            addEventHandler(inputEl,"blur",function(){countTotalNumExtd('RECEIVEAMT');});
            cell.appendChild(inputEl);
        }
    }
}