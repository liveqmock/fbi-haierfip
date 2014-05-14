/***********************************************************************************************************************
 * 文件名：公共js函数库 
 * 作者：by leonwoo
 * 功能：
 **********************************************************************************************************************/

/** ---------------------------------------系统所有公共JS消息--------------------------------------------------------- */
var MSG_SYSLOCK = "个贷数据装载处理中，所有业务禁止操作。\r\n请于约五分钟后再进行相关操作！";
var MSG_NORECORD = "没有可以操作的记录！";
var MSG_DELETE_CONFRIM = "确定删除该记录？";
var MSG_DEL_SUCCESS = "删除记录成功！";
var MSG_EXIT_CONFIRM = "确定退出当前操作吗？";
var MSG_ADDMORTINFO = "没有查询到相关贷款记录，是否现在增加该笔贷款记录？";
var MSG_LOANNULL = "该笔抵押数据无相应贷款信息。";
var MSG_SUCCESS = "操作成功。";
var MSG_CLM_CONFIRM = "确定认领该笔贷款吗？";
var MSG_CLM_CANCEL = "确定退回该笔贷款吗？";
var MSG_BATCH_CLM_CONFIRM = "确定认领该批贷款吗？";
var MSG_BATCH_CLM_CANCEL = "确定退回该批贷款吗？";
var MSG_BATCH_EXPRESS = "确定批量快递该批数据吗？";
var MSG_ODSB_READ_CONFIRM = "确定从ODSB读入数据吗？";
var MSG_ODSB_CHECK_CONFIRM = "确定检查从ODSB读入的数据吗？";
var MSG_ODSB_CHECK_RESULT = "检查结果一致。";
var MSG_ODSB_UPDATE_CONFIRM = "确定更新贷款数据吗？";
/** ----------------------------------------------------------------------------------------------------------------- */

/** ------------------------------------------系统所有公共枚举变量---------------------------------------------------- */

// menu type 菜单名称 010 抵押信息登记<br>
/** 抵押信息登记 */
var BUSINODE_010 = "010";
/** 快递抵押信息登记 */
var BUSINODE_020 = "020";
/** 他行开发贷未报抵押登记 */
var BUSINODE_030 = "030";
/** 他行开发贷可报抵押登记 */
var BUSINODE_040 = "040";
/** 未办理抵押原因登记 */
var BUSINODE_050 = "050";
/** 取得回执登记 */
var BUSINODE_060 = "060";
/** 快递回证登记 */
var BUSINODE_070 = "070";
/** 抵押入库登记 */
var BUSINODE_080 = "080";
/** 借证领用登记 */
var BUSINODE_090 = "090";
/** 借证归还登记 */
var BUSINODE_100 = "010";
/** 贷款结清取证登记 */
var BUSINODE_110 = "110";
/** 抵押特殊通道 */
var BUSINODE_120 = "120";
/** 130 贷款信息管理 */
var BUSINODE_130 = "130";
/** 140 客户经理认领 */
var BUSINODE_140 = "140";
/** 150贷款特殊通道 */
var BUSINODE_150 = "150";
/** 160合作项目维护 */
var BUSINODE_160 = "160"
/** ----------------------------------------------------------------------------------------------------------------- */

/**
 * 显示对话框
 */
function showDialog(width, height, url, arg, resizable) {
    // 设置风格
    if (resizable == undefined) {
        resizable = "no";
    }
    var sfeature = "dialogwidth:" + width + "px; dialogheight:" + height + "px;center:yes;help:no;resizable:"
            + resizable + ";scroll:no;status:no";
    var date = new Date();
    // 设置URL扩展，避免IE没有设置每次刷新，导致显示旧页面内容
    var time = getDateString(date) + " " + getTimeString(date);
    if (url.indexOf("?") > 0) {
        url += "&showDialogTime=" + time;
    } else {
        url += "?&showDialogTime=" + time;
    }
    return window.showModalDialog(url, arg, sfeature);
}

/**
 * 显示对话框
 */
function dialog(url, arg, sfeature) {

    // openWin(url);
    // return;

    var sfeature = sfeature;
    var date = new Date();
    // 设置URL扩展，避免IE没有设置每次刷新，导致显示旧页面内容
    var time = getDateString(date) + " " + getTimeString(date);
    if (url.indexOf("?") > 0) {
        url += "&showDialogTime=" + time;
    } else {
        url += "?&showDialogTime=" + time;
    }
    // alert(url);
    return window.showModalDialog(url, arg, sfeature);
    // return window.open(url);
}

/**
 * 新开一窗口
 */
function openWin(url) {

    var date = new Date();
    // 设置URL扩展，避免IE没有设置每次刷新，导致显示旧页面内容
    var time = getDateString(date) + " " + getTimeString(date);
    if (url.indexOf("?") > 0) {
        url += "&showDialogTime=" + time;
    } else {
        url += "?&showDialogTime=" + time;
    }
    // alert(url);
    return window.open(url);
    // return window.open(url);
}

/**
 * 获取日期
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
 * 获取时间
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

// /序号进行重新排序函数，由dbgrid tableBubbleSort函数调用
// 表table 行、列均从0开始计算 by wuyeyuan
function itemTab_afterSort2() {
    var tab = document.all["itemTab"];
    if (tab != null) {
        var pageSize = tab.pageSize;
        // var totalPage=tab.TotalPage;
        var curPage = tab.AbsolutePage;
        var seq = (curPage - 1) * pageSize;
        for ( var i = 0; i < pageSize; i++) {
            // var xh=tab.rows[i].cells[0].innerText;
            var j = new Number(i + seq) + 1;
            if (tab.rows[i])// 判断最后页数据是否存在，存在则改写序号。考虑到最后一页数据不足pagesize时候。
                // 修改每行的第2列数据
                if (tab.rows[i].firstChild.firstChild.type == "checkbox")
                    tab.rows[i].cells[1].innerText = j;
                else
                    tab.rows[i].cells[0].innerText = j;
        }
    }
}

// 循环使得form内容不可修改
function readFun(userTab) {
    for ( var i = 0; i < userTab.length; i++) {
        var obj = userTab.item(i);
        // if(obj.type=="text"||obj.type=="textarea"||obj.type=="button"){
        if (obj.type == "text" || obj.type == "textarea") {
            obj.disabled = true;
            // obj.className = "inputReadonly";
            // obj.readOnly = true;
        } else if (obj.type == "select-one") {
            obj.disabled = true;
            // obj.className = "inputReadonly";
            // obj.readOnly = true;
        }
        // else{
        // obj.readOnly = true;
        // obj.className = "inputReadonly";
        // }
    }
}

//
// ///主数据表格TD的单击事件
function itemTab_TDclick(el) {
    // alert();
    el = event.srcElement;
    var trobj = getOwnerTR(el);
    if (trobj.edit == "true") {
        // /根据数据类型生成添加对象
        if (el.fieldtype == "text" || el.fieldtype == "date") {

            CreateText(el);
        }

        if (el.fieldtype == "number" || el.fieldtype == "money" || el.fieldtype == "percent") {
            CreateNumberText(el);
        }

        if (el.fieldtype == "dropdown") {
            switch (el.fieldname) {
            case "pub_name": {
                // el.dropwidth="220px"
                el.enumType = "pub_name";
                el.fieldTitle = "编码,名称";
                dropdown(el);
                break;
            }
            default:
                break;
            }

        }
    }
}
/*******************************************************************************
 * 可以输入可以选择select控件使用的函数 从list取得值，赋给textbox date 2008-5-12 selectId listboxID
 * textId textboxID
 * 
 ******************************************************************************/
function onListChanged(selectId, textId) {
    try {
        if (document.getElementById(selectId).selectedIndex > -1) {
            var colVar = document.getElementById(selectId).options[document.getElementById(selectId).selectedIndex].text;
            document.getElementById(textId).value = colVar;
        }
    } catch (Exception) {
    }
}

/**
 * 取得系统锁状态
 * 
 * @param lockstatus
 *            text
 * @param input
 *            type
 * @param input
 *            value
 * @param action
 *            name
 * @param action
 *            method name
 */
function getSysLockStatus() {
    var lockStatus = "";
    // var rtnXml = createselectArr("lockStatus", "text", "1,", "sys01",
    // "getSysLockStatus");
    var rtnXml = createExecuteform(queryForm, "update", "sys01", "getSysLockStatus")
    if (rtnXml != "false") {
        var dom = createDomDocument();
        dom.loadXML(rtnXml);
        var fieldList = dom.getElementsByTagName("record")[0];
        for ( var i = 0; i < fieldList.childNodes.length; i++) {
            if (fieldList.childNodes[i].nodeType == 1) {
                oneRecord = fieldList.childNodes[i];
                attrName = oneRecord.getAttribute("name");
                if (attrName == "sysLockStatus") {
                    lockStatus = decode(oneRecord.getAttribute("value"));
                }
            }
        }
    }

    return lockStatus;
}

/**
 * Focus in时编辑值
 * 
 * @param strValue
 *            需要编辑的值
 * @return 编辑后的值
 */
function formatCommaWhenFocusIn(strValue) {
    if (strValue == null) {
        return "";
    }
    var re = /,/g;
    strValue = strValue.replace(re, "");
    return strValue;
}
/**
 * FocusOut千分位追加
 * 
 * @param strValue
 *            需要编辑的值
 * @return 编辑后的值
 */
function formatCommaWhenFocusOut(strValue) {
    if (isDigit(strValue)) {
        var dotBeforeString = strValue.substring(0, strValue.indexOf("."));
        var dotAfterString = strValue.substring(strValue.indexOf("."));
        if (strValue.indexOf(".") == -1) {
            dotBeforeString = dotAfterString;
            dotAfterString = "";
        }
        var re = /(-?\d+)(\d{3})/;
        while (re.test(dotBeforeString)) {
            dotBeforeString = dotBeforeString.replace(re, "$1,$2");
        }
        strValue = dotBeforeString + dotAfterString;
    }
    return strValue;
}

/**
 * Number判断
 * 
 * @param obj
 */
function isDigit(str) {
    var patrn = /^-?[0-9]{1,38}.*$/;
    if (!patrn.test(str)) {
        return false;
    } else {
        return true;
    }
}

/**
* FocusOut千分位追加 
* @param obj
*/
function Txn_LostFocus(obj) {
    if (obj.tagName.toUpperCase() == "INPUT") {
        var strValue = obj.value;
        if ((strValue == null) || (strValue == "")) {
            obj.value = "";
            return;
        }
        var arr = strValue.split(".");
        if (arr.length > 2) {
            return;
        }
        arr = strValue.split(",");
        strValue = "";
        for (var i = 0; i < arr.length; i++) {
            strValue = strValue + arr[i];
        }
        if (isDigit(strValue)) {
            var intValue = parseFloat(strValue, 10);
            strValue = intValue + "";
            var dotBeforeString = strValue.substring(0, strValue.indexOf("."));
            var dotAfterString = strValue.substring(strValue.indexOf("."));
            if (strValue.indexOf(".") == -1) {
                dotBeforeString = dotAfterString;
                dotAfterString = "";
            }
            var re = /(-?\d+)(\d{3})/;
            while (re.test(dotBeforeString)) {
                dotBeforeString = dotBeforeString.replace(re, "$1,$2");
            }
            obj.value = dotBeforeString + dotAfterString;
        } else if (strValue == "") {
            obj.value = strValue;
        }
    }
}
/**
* Focusin 千分位删除
* @param obj
*/
function Txn_GotFocus(obj) {
    if (obj.tagName.toUpperCase() == "INPUT") {
        var strValue = obj.value;
        var re = /,/g;
        obj.value = strValue.replace(re, "");
        //obj.select();
    }
}