//**************************************************//
//该文件是本系统的dbgrid文件，任何人不得擅自更改	        //
//若有更改与本人联系：公共包					        //
//**************************************************//

/*******************************************************************************
 *
 * 文件名： dbgrid.js
 *
 * 作 用： 数据控件调用方法。
 *
 * 作 者： 公共包
 *
 * 时 间： yyyy-mm-dd
 *
 * 版 权： leonwoo
 *
 ******************************************************************************/

function CreateText(el) {

    var pos = getAbsPosition(el);

    if (typeof (_TableInput_Text) == "undefined") {
        parentHTML = "<INPUT type=\"text\" id=\"_TableInput_Text\" ";

        if (el.textLength != undefined) {
            parentHTML += " maxLength=" + el.textLength;
        }
        parentHTML += "  style=\"position: absolute; left:"
                + (pos[0] - 1)
                + ";top:"
                + (pos[1])
                + "; height:"
                + (el.offsetHeight - 4)
                + ";width:"
                + (el.offsetWidth - 1)
                + ";FONT-SIZE:12;BORDER-RIGHT: medium none; BORDER-TOP: medium none;BORDER-LEFT: medium none;BORDER-BOTTOM: medium none\" onKeyPress=\"onKeyPressInput(this)\"  onblur=\"TextInit()\">";

        obj = document.createElement(parentHTML);

        document.body.appendChild(obj);

    } else {
        var _TD = _TableInput_Text.getAttribute("_TD");

        _TD.innerText = _TableInput_Text.value;

        if (el.textLength != undefined) {
            _TableInput_Text.maxLength = el.textLength;
        }

        _TableInput_Text.style.left = pos[0] - 1;
        _TableInput_Text.style.top = pos[1];
        _TableInput_Text.style.height = el.offsetHeight - 2;
        _TableInput_Text.style.width = el.offsetWidth - 1;
        _TableInput_Text.style.visibility = "";
    }

    _TableInput_Text.setAttribute("_TD", el);

    _TableInput_Text.value = el.innerText;

    _TableInput_Text.select();

    return _TableInput_Text;

}
var tabElement;
var trElement;

function onKeyPressInput(el) {
    el.getAttribute("_TD").innerText = el.value;
    if (el.attr != undefined)
        el.getAttribute("_TD").attr = el.attr;

    enterMove(el.getAttribute("_TD"));

}
function enterMove(tdElement) {
    tabElement = getOwnerTable(tdElement);

    if (tabElement.isentermove == true || tabElement.isentermove == "true")
        ;
    else
        return;

    if (window.event.keyCode == 13) {

        trElement = getOwnerTR(tdElement);

        if (tabElement.minIndex == undefined) {
            tabElement.minIndex = tdElement.cellIndex;

        } else {
            if (tabElement.minIndex > tdElement.cellIndex) {
                tabElement.minIndex = tdElement.cellIndex;
            }

        }

        for ( var i = tdElement.cellIndex + 1; i < trElement.cells.length; i++) {
            if (trElement.cells[i].fieldtype == "text" || trElement.cells[i].fieldtype == "number") {
                trElement.cells[i].click();
                break;
            }

        }

        if (tdElement.cellIndex == trElement.cells.length - 1) {
            if (trElement.rowIndex + 1 < tabElement.rows.length) {

                if (trElement.rowIndex * 22 > 250) {
                    // alert(trElement.rowIndex * 22);
                    document.all("divfd_" + tabElement.id).scrollTop = trElement.rowIndex * 22;
                }

                trElement = tabElement.rows[trElement.rowIndex + 1];

                window.clearTimeout();
                window.setTimeout('movenexttr()', 200);
                window.clearTimeout();

            }

        }

    }

}

function movenexttr() {
    var beginIndex = 0;

    if (tabElement.minIndex != undefined) {
        beginIndex = tabElement.minIndex;
    }

    for ( var i = beginIndex; i < trElement.cells.length; i++) {
        if (trElement.cells[i].fieldtype == "text" || trElement.cells[i].fieldtype == "number") {

            trElement.cells[i].click()

            break;
        }

    }

}

function TextInit() {

    if (typeof (_TableInput_Text) != "undefined") {
        document.all["_TableInput_Text"].getAttribute("_TD").innerText = document.all["_TableInput_Text"].value;
        document.all["_TableInput_Text"].style.visibility = "hidden";
    }

}

function NumberTextInit() {
    if (typeof (_TableNumber_Text) != "undefined") {
        document.all["_TableNumber_Text"].getAttribute("_TD").innerText = document.all["_TableNumber_Text"].value;

        document.all["_TableNumber_Text"].style.visibility = "hidden";
    }

}

function CreateNumberText(el) {

    var inputLength = 0;

    if (el.id != "Splite") {

        var pos = getAbsPosition(el);

        if (typeof (_TableNumber_Text) == "undefined") {

            parentHTML = "<INPUT type=\"text\" id=\"_TableNumber_Text\" "

            if (el.intLength != undefined)
                inputLength = parseInt(el.intLength);

            if (el.floatLength != undefined)
                inputLength += parseInt(el.floatLength) + 1;

            if (inputLength != 0)
                parentHTML += " maxLength=" + inputLength;

            parentHTML += "  style=\"position: absolute; left:"
                    + pos[0]
                    + ";top:"
                    + pos[1]
                    + "; height:"
                    + (el.offsetHeight - 2)
                    + ";width:"
                    + (el.offsetWidth - 2)
                    + ";FONT-SIZE:12 BORDER-RIGHT: medium none; BORDER-TOP: medium none;BORDER-LEFT: medium none;BORDER-BOTTOM: medium none\" onKeyPress=\"onKeyPressTDFloat(this)\" onblur=\"NumberTextInit()\">";

            obj = document.createElement(parentHTML);

            document.body.appendChild(obj);

        } else {

            var _TD = _TableNumber_Text.getAttribute("_TD");

            _TD.innerText = _TableNumber_Text.value;

            if (el.intLength != undefined)
                inputLength = parseInt(el.intLength);

            if (el.floatLength != undefined)
                inputLength += parseInt(el.floatLength) + 1;

            if (inputLength != 0)
                _TableNumber_Text.maxLength = inputLength;

            _TableNumber_Text.style.left = pos[0];
            _TableNumber_Text.style.top = pos[1];
            _TableNumber_Text.style.height = el.offsetHeight - 2;
            _TableNumber_Text.style.width = el.offsetWidth - 2;

            _TableNumber_Text.style.visibility = "";

        }

        _TableNumber_Text.setAttribute("_TD", el);

        _TableNumber_Text.value = el.innerText;
        _TableNumber_Text.select();

    }

}

// 如果某个为输入域为实数，那么过滤掉所有非数字字符
function onKeyPressTDFloat(el) {
    var nKey = window.event.keyCode;

    if (nKey > 57 || (nKey != 46 && nKey < 48))
        window.event.keyCode = 0;

    if (el.getAttribute("_TD")) {

        if (el.getAttribute("_TD").tagName.toUpperCase() == "TD")
            el.getAttribute("_TD").innerText = el.value;
    }

    enterMove(el.getAttribute("_TD"));

}

// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

function tMouseUp() {
    // var myColumn = window.event.srcElement;

    // if (myColumn.callFunc != null)
    // myColumn.callFunc("onMouseUp", myColumn);
}

function tDblClick() {
    var myColumn = window.event.srcElement;

    if (myColumn.callFunc != null)
        myColumn.callFunc("onDblClick", myColumn);
}

function dbGridSelectedCallback(tr, arg) {
    if (tr.selected) {
        tr.selected = false;
        tr.className = tr.oldClass;
    }
}

function setRowHighlight(tr, bHighlight) {
    resetTRStyle(tr);

    if (bHighlight) {
        tr.oldClass = tr.className;
        tr.className = "gridSelectedHighlight";
    } else {
        tr.className = tr.oldClass;
    }
}

function setRowSelected(tr, bSelected, bClearAll) {
    if (bClearAll)
        enumSelectedRows(tr, "clearSelected", dbGridSelectedCallback)

    tr.selected = bSelected;
    setRowHighlight(tr, bSelected);
}

function tClick() {
    var myColumn = window.event.srcElement;

    if (myColumn.callFunc != null)
        myColumn.callFunc("onClick", myColumn);

    var body = getOwnerBody(myColumn);

    if (!body.noSelect) {
        var bMultiSelect = body.multiSelect && event.ctrlKey;

        var tr = getOwnerTR(myColumn);

        if (tr.datasrc && tr.datasrc.indexOf("AutoBuildRow") >= 0) {
            var bSelected;

            if (tr.selected)
                bSelected = false;
            else
                bSelected = true;

            setRowSelected(tr, bSelected, !bMultiSelect);
        }
    }

}

function makeTableSortable(sortTable, tableId) {

    // return;

    var mTab = null;

    if (typeof (sortTable) == "string")
        mtab = document.getElementById(sortTable);
    else
        mtab = sortTable;

    var sorttab = document.getElementById(tableId);

    if (mtab.rows.length > 0) {
        var row = mtab.rows.item(0);
        var nI = 0;

        for (nI; nI < row.cells.length; nI++) {

            if (row.cells(nI).id != "Splite") {

                if (row.cells(nI).onclick == null) {

                    var sortTag = row.cells(nI).children[1];

                    // var sortTag = document.createElement("span");

                    with (sortTag.style) {
                        fontSize = "9pt";
                        fontFamily = "webdings";
                        // Visibility = "hidden";
                        // vertical-align="center";
                        // width="5";

                    }
                    sortTag.verticalAlign = "top";
                    sortTag.innertext = "";

                    row.cells(nI).sortTag = sortTag;

                    row.cells(nI).setAttribute("sorttab", sorttab);
                    if (row.cells(nI).id != "checkID") {
                        // row.cells(nI).children[0].appendChild(sortTag);
                        row.cells(nI).onclick = tableSort;
                    }

                    // row.cells(nI).style.cursor = "hand";
                }

            }
        }
    }
}

function otdStyleTocall(myColumn, imageURL, strTitle, xmlNode) {
    if (arguments.length == 2)
        myColumn.xmlNode = arguments[1];
    else {
        myColumn.style.backgroundPosition = "center";
        myColumn.style.backgroundAttachment = "fixed";
        myColumn.style.backgroundRepeat = "no-repeat";
        myColumn.style.backgroundImage = "url(" + imageURL + ")";
        myColumn.title = strTitle;
        myColumn.xmlNode = xmlNode;
    }
}

function enumSelectedRows(oTableObj, arg, callBack) {
    var oTable = getOwnerTable(oTableObj);
    var nCount = 0;

    if (oTable) {
        var oBody = oTable.gridBody;

        if (oBody) {
            var i = 0;

            while (i < oBody.rows.length) {
                var row = oBody.rows[i];
                var bNotInc = false;

                if (row.selected) {
                    nCount++;

                    if (callBack)
                        bNotInc = callBack(row, arg);
                }

                if (!bNotInc)
                    i++;
            }
        }
    }

    return nCount;
}

// Sort
function tableSort(obj, bByNumeric) {
    var sortCol = obj;

    if (!sortCol)
        sortCol = getOwnerTD(window.event.srcElement)

    var sorttab = sortCol.getAttribute("sorttab");

    var nSortColIdx = sortCol.cellIndex;
    var tab = getOwnerTable(sortCol);

    setSortColumn(sortCol);

    var funSort;

    if (sortCol.sortDesc)
        funSort = sortNumDesc;// 降序排列
    else
        funSort = sortNumAsc;

    tableBubbleSort(sorttab, nSortColIdx, 1, funSort);

    changeGridRowOddEvenColor(sorttab);

}

function setSortColumn(sortCol) {
    var nSortColIdx = sortCol.cellIndex;
    var tab = getOwnerTable(sortCol);

    if (tab.sortColIdx == nSortColIdx) {
        if (sortCol.sortDesc)
            sortCol.sortDesc = false;
        else
            sortCol.sortDesc = true;
    } else {
        var oldSortCol = getOwnerTR(sortCol).cells(tab.sortColIdx);
        oldSortCol.sortTag.innerText = "";
        tab.sortColIdx = nSortColIdx;
        sortCol.sortDesc = false;
    }

    sortCol.sortTag.style.visibility = "visible";

    if (sortCol.sortDesc)
        sortCol.sortTag.innerText = "5";
    else
        sortCol.sortTag.innerText = "6";
}

function sortDesc(vValue1, vValue2) {
    return (vValue1 > vValue2);
}

function sortAsc(vValue1, vValue2) {
    return (vValue1 < vValue2);
}

function sortNumAsc(vValue1, vValue2) {
    try {
        var vVal1 = vValue1 / 1;
        var vVal2 = vValue2 / 1;
        if (!isNaN(vVal1) && !isNaN(vVal2))
            return (vVal1 < vVal2);
    } catch (e) {

    }
    return (vValue1 < vValue2);
}

function sortNumDesc(vValue1, vValue2) {
    // vValue1 = Number(getRidOfChar(vValue1, ','));
    // vValue2 = Number(getRidOfChar(vValue2, ','));
    try {
        var vVal1 = vValue1 / 1;
        var vVal2 = vValue2 / 1;
        if (!isNaN(vVal1) && !isNaN(vVal2))
            return (vVal1 > vVal2);
    } catch (e) {

    }
    return (vValue1 > vValue2);
}

function getRidOfChar(str, chr) {
    var aBuf;
    var strBuf = "";
    var nI;
    var count = 10;
    // aBuf = str.split(chr);

    if (str != null) {

        if (str.length < 10) {
            count = str.length;
        }
        for (nI = 0; nI < count; nI++)
            strBuf = strBuf + str.charCodeAt(nI);

        return strBuf;
    } else
        return "";
}

function getSortText(oTD) {
    if (typeof (oTD.sortText) != "undefined")
        return oTD.sortText;
    else
        return oTD.innerText;
}

// 排序
function tableBubbleSort(tab, nSortColIdx, nFromRow, functionSort) {
    if (tab.rows.length == 0)
        return;

    tab.sortFieldName = tab.rows.item(0).cells.item(nSortColIdx).fieldname;

    var end = tab.rows.length - 1;

    for ( var aa = 0; aa < end && isAutoBuildRow(tab.rows.item(aa)); aa++)
        for ( var bb = aa + 1; bb <= end && isAutoBuildRow(tab.rows.item(bb)); bb++) {

            if (functionSort(getSortText(tab.rows.item(aa).cells.item(nSortColIdx)),
                    getSortText(tab.rows.item(bb).cells.item(nSortColIdx)))) {

                tab.moveRow(bb, aa);
            }
        }
    // 增加回调函数接口 ，
    // 用于在列表中增加一列序号，重新生成列序号
    if (isfireuserEvent(tab.id + "_afterSort")) {
        fireUserEvent(tab.id + "_afterSort");
    }
    // 综合查询调用
    if (isfireuserEvent(tab.id + "_duplicate")) {
        fireUserEvent(tab.id + "_duplicate");
    }
}

function setTRStyle(vobj) {
    if (vobj.className != "gridSelectedHighlight") {
        vobj.oldClassName = vobj.className;
        vobj.className = "gridHighlight";

        if (vobj.callFunc)
            vobj.callFunc("onMouseOverRow", vobj);
    }
}

function isAutoBuildRow(oTR) {
    var bResult = true;

    if (typeof (oTR.datasrc) != "undefined")
        bResult = (oTR.datasrc.indexOf("AutoBuildRow") != -1);

    return bResult;
}

function changeGridRowOddEvenColor(oTable) {

    if (typeof (oTable) == "object") {
        if (oTable.rows.length == 0)
            return;

        // oTable.rows[0].className = "gridHead";

        for ( var i = 0; i < oTable.rows.length; i++) {
            var oRow = oTable.rows[i];

            if (oRow.oldclass == undefined) {

                if ((i % 2) == 0) { // Odd row
                    oRow.className = "gridOddRow";
                    oRow.oldClassName = "gridOddRow";
                } else { // Even row
                    oRow.className = "gridEvenRow";
                    oRow.oldClassName = "gridEvenRow";
                }

                oRow.oldClassName = oRow.className;
            }

        }
    }
}

// ///////////////////////////////////////////////////////////////////////////////////////////////////////////

function button_onmousedown() {
    var button = event.srcElement;
    var menu = button.getAttribute("menu");
    if (menu) {
        if (isPopupMenuVisible())
            hide_popupMenu();

        show_popupMenu(menu, button);
    }
}

function TRMove(el) {

    var oTable = getOwnerTable(el);

    if (oTable.gridType.toLowerCase() == "color") {
        for ( var i = 0; i < oTable.rows.length; i++) {

            oTable.rows[i].className = "";
            oTable.rows[i].bgColor = oTable.rows[i].oldclass;
        }
        oTable.activeIndex = el.rowIndex;

        oTable.rows[oTable.activeIndex].bgColor = "#cccccc";
    } else {
        if (oTable.activeIndex / 1 > -1)
            oTable.rows[oTable.activeIndex].className = "gridEvenRow";

        oTable.activeIndex = el.rowIndex;

        el.className = "gridSelectedHighlight";

    }

}

var retbodyReload;
var empty_ishint = true;
var query_empty_title = "无符合当前查询条件的记录！"

// /////////actionname 名称
document.ACTION_NAME = "";
// ///////方法名称
document.ADDMETHOD_NAME = "";
document.EDITMETHOD_NAME = "";
document.DELMETHOD_NAME = "";

// ///////同步刷新数据
function Table_Refresh(id, ishint, bodyReload, empty_title, login) {

    // ///////////////////////判断是否超时

    // if(login){//alert("1")
    // }
    // else{
    // alert("2");
    // if (!checkLogin())
    // return false;
    // }

    // retbodyReload = undefined;
    empty_ishint = true;

    if (arguments.length >= 2)
        empty_ishint = ishint;

    if (arguments.length >= 3)
        retbodyReload = bodyReload;

    if (arguments.length == 4)
        query_empty_title = empty_title;

    var xmlDoc = createDomDocument("<root/>");

    var tab = document.all(id);

    document.ACTION_NAME = tab.actionname;
    document.ADDMETHOD_NAME = tab.addmethodname;
    document.EDITMETHOD_NAME = tab.editmethodname;
    document.DELMETHOD_NAME = tab.delmethodname;
    document.POSEDELETE = tab.posedelete;

    document.dbformname = tab.dbformname;

    var rootNode = xmlDoc.documentElement;

    var childNode = appendNode(xmlDoc, xmlDoc.documentElement, "field");

    appendAttri(xmlDoc, childNode, "id", id);
    appendAttri(xmlDoc, childNode, "gridType", tab.gridType);
    appendAttri(xmlDoc, childNode, "SQLStr", tab.SQLStr);
    appendAttri(xmlDoc, childNode, "fieldtype", tab.fieldtype);

    appendAttri(xmlDoc, childNode, "fieldCN", tab.fieldCN);
    appendAttri(xmlDoc, childNode, "enumType", tab.enumType);
    appendAttri(xmlDoc, childNode, "visible", tab.visible);
    appendAttri(xmlDoc, childNode, "fieldname", tab.fieldname);

    appendAttri(xmlDoc, childNode, "pageSize", tab.pageSize);
    appendAttri(xmlDoc, childNode, "AbsolutePage", tab.AbsolutePage);
    appendAttri(xmlDoc, childNode, "countSQL", tab.countSQL);

    appendAttri(xmlDoc, childNode, "RecordCount", tab.RecordCount);
    appendAttri(xmlDoc, childNode, "tralign", tab.tralign);

    appendAttri(xmlDoc, childNode, "checkbl", tab.checkbl);

    appendAttri(xmlDoc, childNode, "whereStr", encode(decode(tab.whereStr)));
    appendAttri(xmlDoc, childNode, "fieldwidth", tab.fieldwidth);
    appendAttri(xmlDoc, childNode, "fieldCheck", tab.fieldCheck);
    appendAttri(xmlDoc, childNode, "bottomVisible", tab.bottomVisible);
    appendAttri(xmlDoc, childNode, "isTotal", tab.isTotal);

    // zr
    appendAttri(xmlDoc, childNode, "sumfield", tab.sumfield);

    rootNode.appendChild(childNode);

    show_status_label(window, "正在查询数据...", true);

    // alert(xmlDoc.xml);
    // return;

    var tmpHTML = ExecServerPrgm_synsh(g_jsContextPath+"/BI/util/DataTableJsp.jsp", "POST", "tabStr=" + encode(xmlDoc.xml), "", "", id,
            Table_Refresh_Date);

    xmlDoc.free;

}

// ///////同步刷新数据
function Table_Refresh_asy(id) {

    // ///////////////////////判断是否超时
    // if (!checkLogin())
    // return false;

    var xmlDoc = createDomDocument("<root/>");

    var tab = document.all(id);

    var div_table = document.all("div_" + id);
    var divfd_table = document.all("divfd_" + id);

    document.ACTION_NAME = tab.actionname;
    document.ADDMETHOD_NAME = tab.addmethodname;
    document.EDITMETHOD_NAME = tab.editmethodname;
    document.DELMETHOD_NAME = tab.delmethodname;
    document.POSEDELETE = tab.posedelete;

    document.dbformname = tab.dbformname;

    var rootNode = xmlDoc.documentElement;

    var childNode = appendNode(xmlDoc, xmlDoc.documentElement, "field");

    appendAttri(xmlDoc, childNode, "id", id);
    appendAttri(xmlDoc, childNode, "gridType", tab.gridType);
    appendAttri(xmlDoc, childNode, "SQLStr", tab.SQLStr);
    appendAttri(xmlDoc, childNode, "fieldtype", tab.fieldtype);

    appendAttri(xmlDoc, childNode, "fieldCN", tab.fieldCN);
    appendAttri(xmlDoc, childNode, "enumType", tab.enumType);
    appendAttri(xmlDoc, childNode, "visible", tab.visible);
    appendAttri(xmlDoc, childNode, "fieldname", tab.fieldname);

    appendAttri(xmlDoc, childNode, "pageSize", tab.pageSize);
    appendAttri(xmlDoc, childNode, "AbsolutePage", tab.AbsolutePage);
    appendAttri(xmlDoc, childNode, "countSQL", tab.countSQL);

    appendAttri(xmlDoc, childNode, "RecordCount", tab.RecordCount);
    appendAttri(xmlDoc, childNode, "tralign", tab.tralign);

    appendAttri(xmlDoc, childNode, "checkbl", tab.checkbl);

    appendAttri(xmlDoc, childNode, "whereStr", encode(decode(tab.whereStr)));
    appendAttri(xmlDoc, childNode, "fieldwidth", tab.fieldwidth);
    appendAttri(xmlDoc, childNode, "fieldCheck", tab.fieldCheck);
    appendAttri(xmlDoc, childNode, "bottomVisible", tab.bottomVisible);
    appendAttri(xmlDoc, childNode, "isTotal", tab.isTotal);

    rootNode.appendChild(childNode);

    var tmpHTML = ExecServerPrgm(g_jsContextPath+"/BI/util/DataTableJsp.jsp", "POST", "tabStr=" + encode(xmlDoc.xml));

    if (document.all["div_" + id]) {
        document.all["div_" + id].innerHTML = tmpHTML;

        document.all(id).whereStr = tab.whereStr;
    }

    document.all("div_" + id).style.width = div_table.style.width;
    document.all("div_" + id).style.height = div_table.style.height;

    document.all("divfd_" + id).style.width = divfd_table.style.width;
    document.all("divfd_" + id).style.height = divfd_table.style.height;

    document.all(id).fdwidth = tab.fdwidth;

    compentHidden(document.all(id));

    document.all(id).actionname = document.ACTION_NAME;
    document.all(id).addmethodname = document.ADDMETHOD_NAME;
    document.all(id).editmethodname = document.EDITMETHOD_NAME;
    document.all(id).delmethodname = document.DELMETHOD_NAME;
    document.all(id).posedelete = document.POSEDELETE;

    document.all(id).dbformname = document.dbformname;

    reLoadCount = 0;

    initDBGrid(id);

    xmlDoc.free;

}

// /////刷新数据
function Table_Refresh_Date(id, tmpHTML) {
    try {
        var tab = document.all(id);

        var div_table = document.all("div_" + id);
        var divfd_table = document.all("divfd_" + id);

        hide_status_label(window);

        if (document.all["div_" + id]) {
            document.all["div_" + id].innerHTML = tmpHTML;

            document.all(id).whereStr = tab.whereStr;
        }

        compentHidden(document.all(id));

        if (isfireuserEvent(id + "_afterRefresh"))
            fireUserEvent(id + "_afterRefresh", [ document.all(id) ]);

        document.all("div_" + id).style.width = div_table.style.width;
        document.all("div_" + id).style.height = div_table.style.height;

        document.all("divfd_" + id).style.width = divfd_table.style.width;
        document.all("divfd_" + id).style.height = divfd_table.style.height;
        document.all(id).fdwidth = tab.fdwidth;

        if (retbodyReload != undefined) {
            retbodyReload();
        }

        if (empty_ishint && document.all(id).rows.length == 0)
            alert(query_empty_title);

        document.all(id).actionname = document.ACTION_NAME;
        document.all(id).addmethodname = document.ADDMETHOD_NAME;
        document.all(id).editmethodname = document.EDITMETHOD_NAME;
        document.all(id).delmethodname = document.DELMETHOD_NAME;
        document.all(id).posedelete = document.POSEDELETE;

        document.all(id).dbformname = document.dbformname;

        reLoadCount = 0;

        initDBGrid(id);
    } catch (ex) {
    }

}

function Data_operate(SqlStr) {

    if (SqlStr != "") {
        var xmlDoc = createDomDocument("<reqDataTableStr/>");

        var rootNode = xmlDoc.documentElement;
        rootNode.text = "nosel";

        var childNode = appendNode(xmlDoc, xmlDoc.documentElement, "childNode");

        childNode.text = SqlStr;
        rootNode.appendChild(childNode);

        var reqXml = encode(xmlDoc.xml);

        xmlDoc.free;
        return ExecServerPrgm("../server/SaveData.jsp", "POST", "xx=" + reqXml);

    }

}

function WidthEquals(headTable, bodyTable) {

    var tableName = bodyTable.split("_");
    if (tableName.length == 1)
        tableName = headTable.split("_");

    if (tableName.length == 1)
        return;

    if (document.all(tableName[1]).rows.length > 0) {

        for ( var j = 0; j < document.all(tableName[1]).rows[0].cells.length; j++) {
            document.all("Title_" + tableName[1]).rows[0].cells[j].width = document.all(tableName[1]).rows[0].cells[j].offsetWidth;
            if (document.all[tableName[1]].isTotal == "true")
                document.all("tableTotal_" + tableName[1]).rows[0].cells[j].width = document.all(tableName[1]).rows[0].cells[j].offsetWidth;

        }

    }

}

function Covert_Click(TableID, buttonID, textID) {

    var divHeight = document.all["div_" + TableID].style.height;
    var divWidth = document.all["div_" + TableID].style.width;

    var divfdHeight = document.all["divfd_" + TableID].style.height;

    var tab = document.all[TableID];

    if ((document.all[textID].value / 1 <= tab.TotalPage / 1) && (document.all[textID].value / 1 > 0))
        if (tab.AbsolutePage / 1 == document.all[textID].value / 1)
            return;

    if (document.all[buttonID].operate == "1") {
        if ((document.all[textID].value / 1 <= tab.TotalPage / 1) && (document.all[textID].value / 1 > 0)) {
            tab.AbsolutePage = document.all[textID].value;
            Table_Refresh_asy(tab.id);
        }
        for ( var i = 0; i < tab.rows.length - 1; i++) {

            tab.rows[i].onclick = TRClick_GetDate;
        }

        document.all[buttonID].operate = "1";
    } else {

        if ((document.all[textID].value / 1 <= tab.TotalPage / 1) && (document.all[textID].value / 1 > 0)) {
            tab.AbsolutePage = document.all[textID].value;
            Table_Refresh_asy(tab.id);
        }

    }

    if (divHeight != "")
        document.all["div_" + TableID].style.height = divHeight;
    if (divWidth != "")
        document.all["div_" + TableID].style.width = divWidth;
    if (divfdHeight != "")
        document.all["divfd_" + TableID].style.height = divfdHeight;

    initDBGrid(TableID);

}

function onKeyPressCovertInputInteger(el) {
    var nKey = window.event.keyCode;

    if (nKey == 13) {
        Covert_Click(el.tableid, el.buttonid, el.id);
    }

    if (nKey < 48 || nKey > 57) {

        window.event.keyCode = 0;
    }

}

// ////TR单击事件
function TR_click() {

    el = getOwnerTR(event.srcElement);

    var oTable = getOwnerTable(el);

    // compentHidden(oTable);

    if (oTable.gridType.toLowerCase() == "color") {
        for ( var i = 0; i < oTable.rows.length; i++) {

            oTable.rows[i].className = "";
            oTable.rows[i].bgColor = oTable.rows[i].oldclass;
        }
        oTable.activeIndex = el.rowIndex;

        oTable.rows[oTable.activeIndex].bgColor = "#cccccc";
    } else {
        if (oTable.activeIndex / 1 > -1)
            oTable.rows[oTable.activeIndex].className = "gridEvenRow";

        oTable.activeIndex = el.rowIndex;

        el.className = "gridSelectedHighlight";

    }
    if (oTable.dbformname != undefined)
        gridfillform(oTable, document.all(oTable.dbformname));

    if (isfireuserEvent(oTable.id + "_afteroperate_click"))
        fireUserEvent(oTable.id + "_afteroperate_click", [ oTable ]);

}

// //////////Gride_OnMouseUp
function Gride_OnMouseUp(el) {

    if (event.button == 2) {

        TR_click();
        var oTable = getOwnerTable(el);

        if ((oTable.menuText != undefined) && (oTable.menuText != "")) {
            hide();

            document.TR = el;
            menuinit(oTable.menuText);
            show(event.x, event.y);
            return;
        }
        if ((oTable.menu != undefined) && (oTable.menu)) {
            hide();
            document.TR = el;
            show(event.x, event.y);
        }

    }

}

function checkClick(fromName) {

    var nameArr = fromName.split("_");
    if (nameArr[0] != "child") {

        var chkparobj = document.all("chkpar_" + nameArr[1]);
        var isChecked = chkparobj.checked;
        var form_dbgride = document.all[fromName];
        // alert(form_dbgride.length);
        var length = form_dbgride.length;
        for ( var i = 0; i < length; i++) {
            var obj = form_dbgride.item(i);
            if (obj.type == "checkbox") {
                obj.checked = isChecked;
            }
        }
    }

    if (isfireuserEvent(nameArr[1] + "_afterChecked"))
        fireUserEvent("dbgrid_afterChecked", [ fromName ]);

}

function dbgrid_afterChecked(fromName) {

    var obj = event.srcElement;
    var nameArr = fromName.split("_");

    fireUserEvent(nameArr[1] + "_afterChecked", [ obj ]);

    if (nameArr[0] != "child") {

        var chkparobj = document.all("chkpar_" + nameArr[1]);
        var isChecked = chkparobj.checked;
        var form_dbgride = document.all[fromName];
        // alert(form_dbgride.length);
        var length = form_dbgride.length;
        for ( var i = 0; i < length; i++) {
            var obj = form_dbgride.item(i);
            if (obj.type == "checkbox") {
                obj.checked = isChecked;
            }
        }
    }

}

// ////////////////////////////////////////////datapilot_function/////////////////////////////////////////

// 移动第一页
function moveFirst(tab) {
    if (tab.AbsolutePage > 1) {
        tab.AbsolutePage = 1
        Table_Refresh_asy(tab.id);
    }
}

// 移动前一页
function prevPage(tab) {
    if (tab.AbsolutePage > 1) {

        tab.AbsolutePage = tab.AbsolutePage / 1 - 1;

        Table_Refresh_asy(tab.id);

    }
}

// 移动前一行
function movePrev(tab) {
    if (tab.activeIndex > 0) {
        TRMove(tab.rows[tab.activeIndex - 1]);

    } else {

        if (tab.AbsolutePage > 1) {

            tab.AbsolutePage = tab.AbsolutePage / 1 - 1;

            Table_Refresh_asy(tab.id);

        }

    }
}

// 移动后一行
function moveNext(tab) {
    if (tab.activeIndex < tab.rows.length - 1) {
        TRMove(tab.rows[tab.activeIndex / 1 + 1]);

    } else {
        if (tab.AbsolutePage / 1 < tab.TotalPage) {

            tab.AbsolutePage = tab.AbsolutePage / 1 + 1;

            Table_Refresh_asy(tab.id);
        }
    }
}

// 移动后一页
function nextPage(tab) {

    if (tab.AbsolutePage / 1 < tab.TotalPage) {

        tab.AbsolutePage = tab.AbsolutePage / 1 + 1;

        Table_Refresh_asy(tab.id);
    }
}
// 移动最后一页
function moveLast(tab) {
    if (tab.AbsolutePage / 1 < tab.TotalPage) {
        tab.AbsolutePage = tab.TotalPage;
        Table_Refresh_asy(tab.id);
    }
}

// 添加记录
function appendRecord(tab) {
    if (tab.rows.length == 0)
        tab.activeIndex = -1;

    var row = tab.insertRow(tab.activeIndex / 1 + 1);

    row.style.height = 25;
    // row.ondblclick=TRDbclick;
    row.onclick = TR_click;
    row.edit = "true";

    row.operate = "insert";

    var fieldtype = tab.fieldtype.split(",");
    var fieldname = tab.fieldname.split(",");
    var visible = tab.visible.split(",");
    var align = tab.tralign.split(",");
    var fieldCheckArr = tab.fieldCheck.split(";");

    if (tab.checkbl.toLowerCase() == "true") {

        var checkbox = document.createElement("input");

        checkbox.type = "checkbox";
        checkbox.style.borderStyle = "none";
        checkbox.name = "chkchild";

        var cell = row.insertCell();
        cell.align = "center";

        cell.width = document.all("Title_" + tab.id).rows[0].cells[cell.cellIndex].offsetWidth;
        cell.appendChild(checkbox);

    }

    for (i = 0; i < fieldtype.length; i++) {

        if (visible[i] == "true") {
            var cell = row.insertCell();

            cell.width = document.all("Title_" + tab.id).rows[0].cells[cell.cellIndex].offsetWidth;

            if (align[i] != undefined && align[i] != "undefined") {
                cell.align = align[i];
            }

            if (fieldCheckArr[i] != undefined) {

                var fieldOneCheckArr = fieldCheckArr[i].split(",");

                for ( var j = 0; j < fieldOneCheckArr.length; j++) {
                    if (fieldOneCheckArr[j].length > 0) {
                        var fieldValueCheckArr = fieldOneCheckArr[j].split("=");

                        if (fieldValueCheckArr[0] == "isNull")
                            cell.isNull = fieldValueCheckArr[1];
                        else if (fieldValueCheckArr[0] == "textLength")
                            cell.textLength = fieldValueCheckArr[1];
                        else if (fieldValueCheckArr[0] == "intLength")
                            cell.intLength = fieldValueCheckArr[1];
                        else if (fieldValueCheckArr[0] == "floatLength")
                            cell.floatLength = fieldValueCheckArr[1];
                        else if (fieldValueCheckArr[0] == "eMail")
                            cell.eMail = fieldValueCheckArr[1];
                        else if (fieldValueCheckArr[0] == "MinLength")
                            cell.MinLength = fieldValueCheckArr[1];

                    }

                }
            }

            cell.fieldtype = fieldtype[i];
            cell.fieldname = fieldname[i];
            cell.oldvalue = "";
            // cell.onclick=TDclick;
        }

    }

    WidthEquals(tab.id, "Title_" + tab.id);
    changeGridRowOddEvenColor(tab);

    tab.activeIndex = row.rowIndex;

}

// 修改记录
function editRecord(tab) {

    if (tab.rows.length == 0)
        return;

    tab.rows[tab.activeIndex].edit = "true";
    if (tab.rows[tab.activeIndex].operate != "insert") {
        if (tab.rows[tab.activeIndex].operate == "delete") {
            tab.rows[tab.activeIndex].oldoperate = tab.rows[tab.activeIndex].operate;
            tab.rows[tab.activeIndex].style.textDecorationLineThrough = false;

        }
        tab.rows[tab.activeIndex].operate = "update";
    }

}

// 删除记录
function deleteRecord(tab, datapiolt, m_objParam, delTitle) {

    if (tab.rows.length == 0)
        return;

    if (tab.posedelete == true || tab.posedelete == "true") {

        gridPoseDelete(tab, datapiolt, m_objParam);
        return;

    }
    if (tab.checkbl.toLowerCase() == "true") {
        for ( var i = tab.rows.length - 1; i > -1; i--) {
            if (tab.rows[i].operate != "insert") {
                if (tab.rows[i].cells[0].children[0] != undefined && tab.rows[i].cells[0].children[0].checked) {
                    tab.rows[i].style.textDecorationLineThrough = true;

                    if (tab.rows[i].operate == "update")
                        tab.rows[i].oldoperate = tab.rows[i].operate;

                    tab.rows[i].edit = "false"
                    tab.rows[i].operate = "delete";

                }

            } else {

                if (tab.rows[i].cells[0].children[0] != undefined && tab.rows[i].cells[0].children[0].checked) {
                    tab.rows[i].cells[0].children[0].checked = false;
                    tab.rows[i].removeNode(true);

                    if (tab.rows.length > 0)
                        tab.activeIndex = tab.rows.length - 1;

                }
            }
        }
    } else {
        if (tab.rows[tab.activeIndex].operate != "insert") {

            tab.rows[tab.activeIndex].style.textDecorationLineThrough = true;

            if (tab.rows[tab.activeIndex].operate == "update")
                tab.rows[tab.activeIndex].oldoperate = tab.rows[tab.activeIndex].operate;

            tab.rows[tab.activeIndex].edit = "false";

            tab.rows[tab.activeIndex].operate = "delete";

        } else {

            if (tab.activeIndex == undefined || tab.activeIndex >= tab.rows.length)
                return;

            tab.rows[tab.activeIndex].removeNode(true);

            if (tab.rows.length > 0)
                tab.activeIndex = tab.rows.length - 1;

        }
    }

}

function gridPoseDelete(tab, datapiolt, m_objParam, delTitle) {

    if (tab.actionname == undefined || tab.actionname == "") {
        alert("action标示不能为空！");
        return;
    }

    if (true) {

        var nodel = true;
        deltitle
        var deltitle = "您确定要删除当前记录吗？";

        if (tab.checkbl.toLowerCase() == "true") {
            var xmlDoc = createDoc();

            var rootNode = createRootNode(xmlDoc);

            var actionNode = createActionNode(xmlDoc, tab.actionname);

            if (tab.delmethodname != undefined && tab.delmethodname != "")
                appendAttri(xmlDoc, actionNode, "methodname", tab.delmethodname);

            for ( var i = 0; i < tab.rows.length; i++) {
                if (tab.rows[i].operate != "insert") {

                    if (tab.rows[i].cells[0].children[0] != undefined && tab.rows[i].cells[0].children[0].checked) {

                        var wherrstr = tab.rows[i].whStr;

                        if (m_objParam != "") {
                            wherrstr = wherrstr + "*" + m_objParam;
                        }
                        nodel = false;

                        var reorderNode = createDeleteRecord(wherrstr, xmlDoc);
                        actionNode.appendChild(reorderNode);
                    }

                }

            }

            if (nodel) {
                alert("请在多选框选择删除项目！");
                return;
            }

            rootNode.appendChild(actionNode);

            if (arguments.length == 4)
                deltitle = delTitle;

            if (confirm(deltitle)) {

                var retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlExcuteJsp.jsp", "POST", "sys_request_xml=" + xmlDoc.xml);

                if (analyzeReturnXML(retStr) == "true") {
                    tab.AbsolutePage = "1";
                    tab.RecordCount = "0";

                    Table_Refresh_asy(tab.id);
                }
            }

        } else {
            if (tab.rows[tab.activeIndex].operate != "insert") {
                var xmlDoc = createDoc();

                var rootNode = createRootNode(xmlDoc);

                var actionNode = createActionNode(xmlDoc, actionName);

                if (tab.delmethodname != undefined && tab.delmethodname != "")
                    appendAttri(xmlDoc, actionNode, "methodname", tab.delmethodname);

                var wherrstr = tab.rows[tab.activeIndex].whStr;

                if (m_objParam != "") {
                    wherrstr = wherrstr + "*" + m_objParam;
                }

                nodel = false;
                var reorderNode = createDeleteRecord(wherrstr, xmlDoc);
                actionNode.appendChild(reorderNode);

                rootNode.appendChild(actionNode);

                if (nodel) {
                    alert("无法删除当前项目！");
                    return;
                }

                if (arguments.length == 4)
                    deltitle = delTitle;

                if (confirm(deltitle)) {
                    var retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlExcuteJsp.jsp", "POST", "sys_request_xml=" + xmlDoc.xml);

                    if (analyzeReturnXML(retStr) == "true") {
                        // tab.AbsolutePage= "1";
                        tab.RecordCount = "0";

                        Table_Refresh_asy(tab.id);
                    }
                }

            }

        }
    }

}

// 撤消对当前记录
function cancelRecord(tab, datapiolt) {
    if (tab.rows.length == 0)
        return;

    compentHidden(tab);

    if (tab.rows[tab.activeIndex].operate == "insert") {
        tab.deleteRow(tab.activeIndex);
        if (tab.rows.length > 0 && tab.activeIndex > tab.rows.length - 1)
            tab.activeIndex = tab.rows.length - 1;
    } else {
        if (tab.rows[tab.activeIndex].operate == "update") {

            tab.rows[tab.activeIndex].edit = "false";

            if (tab.rows[tab.activeIndex].oldoperate == "delete") {
                tab.rows[tab.activeIndex].style.textDecorationLineThrough = true;

                tab.rows[tab.activeIndex].operate = "delete";
                tab.rows[tab.activeIndex].oldoperate = "";
            } else
                tab.rows[tab.activeIndex].operate = "";

        }

        if (tab.rows[tab.activeIndex].operate == "delete") {
            tab.rows[tab.activeIndex].style.textDecorationLineThrough = false;

            if (tab.rows[tab.activeIndex].oldoperate == "update") {

                tab.rows[tab.activeIndex].edit = "true";
                tab.rows[tab.activeIndex].operate = "update";
                tab.rows[tab.activeIndex].oldoperate = "";
            } else
                tab.rows[tab.activeIndex].operate = "";
        }
    }

}

// 保存对当前记录
function updateRecord(tab, m_objParam) {

    var xmlDoc = createDoc();

    // /给最后的TD付值

    if (tab.actionname == undefined || tab.actionname == "") {
        alert("action标示不能为空！");
        return;
    }

    if (typeof (_TableInput_Text) != "undefined")
        document.all["_TableInput_Text"].getAttribute("_TD").innerText = document.all["_TableInput_Text"].value;

    if (typeof (_TableNumber_Text) != "undefined")
        document.all["_TableNumber_Text"].getAttribute("_TD").innerText = document.all["_TableNumber_Text"].value;

    var rootNode = createRootNode(xmlDoc);

    var updateNode = null;
    var insertNode = null;
    var deleteNode = null;

    for ( var i = 0; i < tab.rows.length; i++) {

        // //////// 删除记录信息
        if (tab.rows[i].operate == "delete") {

            if (deleteNode == null)
                deleteNode = createActionNode(xmlDoc, tab.actionname);

            if (tab.delmethodname != undefined && tab.delmethodname != "")
                appendAttri(xmlDoc, deleteNode, "methodname", tab.delmethodname);

            var wherrstr = tab.rows[i].whStr;

            if (m_objParam != "") {
                wherrstr = wherrstr + "*" + m_objParam;
            }

            var reorderNode = createDeleteRecord(wherrstr, xmlDoc);

            for ( var j = 0; j < tab.rows[i].cells.length; j++) {
                if ((tab.rows[i].cells[j].fieldname != undefined) && (tab.rows[i].cells[j].fieldtype != undefined)) {

                    var fieldNode = createFieldNode(xmlDoc, tab.rows[i].cells[j].fieldname,
                            tab.rows[i].cells[j].fieldtype, tab.rows[i].cells[j].oldvalue,
                            tab.rows[i].cells[j].oldvalue);
                    reorderNode.appendChild(fieldNode);

                }
            }

            deleteNode.appendChild(reorderNode);

        }

        // /添加更新记录

        if (tab.rows[i].operate == "update") {

            if (updateNode == null)
                updateNode = createActionNode(xmlDoc, tab.actionname);

            if (tab.editmethodname != undefined && tab.editmethodname != "")
                appendAttri(xmlDoc, updateNode, "methodname", tab.editmethodname);

            var RecorderNode = createRecorderNode(xmlDoc, "update");

            for ( var j = 0; j < tab.rows[i].cells.length; j++) {
                if ((tab.rows[i].cells[j].fieldname != undefined) && (tab.rows[i].cells[j].fieldtype != undefined)) {
                    // /检查输入项
                    if (checkTDValue(tab.rows[i].cells[j]) == "false")
                        return false;

                    // /添加dropdown项

                    if (tab.rows[i].cells[j].fieldtype.toLowerCase() == "dropdown") {
                        if (tab.rows[i].cells[j].attr != undefined) {
                            var fieldNode = createFieldNode(xmlDoc, tab.rows[i].cells[j].fieldname,
                                    tab.rows[i].cells[j].fieldtype, tab.rows[i].cells[j].attr,
                                    tab.rows[i].cells[j].oldvalue);
                            RecorderNode.appendChild(fieldNode);

                        }
                    } else if (tab.rows[i].cells[j].fieldtype.toLowerCase() == "other") {
                        if (tab.rows[i].cells[j].attr != undefined) {
                            var fieldNode = createFieldNode(xmlDoc, tab.rows[i].cells[j].fieldname,
                                    tab.rows[i].cells[j].fieldtype, tab.rows[i].cells[j].attr,
                                    tab.rows[i].cells[j].oldvalue);
                            RecorderNode.appendChild(fieldNode);

                        }
                    } else {
                        var fieldNode = createFieldNode(xmlDoc, tab.rows[i].cells[j].fieldname,
                                tab.rows[i].cells[j].fieldtype, tab.rows[i].cells[j].innerText,
                                tab.rows[i].cells[j].oldvalue);
                        RecorderNode.appendChild(fieldNode);

                    }
                }
            }

            // //添加主健值

            if ((tab.rows[i].whStr != undefined) && (tab.rows[i].whStr != "")) {
                var whereSArr = tab.rows[i].whStr.split("*");

                for ( var j = 0; j < whereSArr.length; j++) {
                    if (whereSArr[j] != undefined) {
                        var whArr = whereSArr[j].split("&");

                        var fieldNode = createFieldNode(xmlDoc, whArr[0], whArr[1], whArr[2]);
                        RecorderNode.appendChild(fieldNode);

                    }
                }
            }

            // ///添加外部参数值
            if (m_objParam != "") {

                var paramSArr = m_objParam.split("*");

                for ( var j = 0; j < paramSArr.length; j++) {
                    if ((paramSArr[j] != undefined) && (paramSArr[j] != "")) {
                        var fieldNode = createFieldStrNode(xmlDoc, paramSArr[j]);
                        RecorderNode.appendChild(fieldNode);

                    }
                }

            }

            updateNode.appendChild(RecorderNode);

        }

        // //////添加插入值
        if (tab.rows[i].operate == "insert") {
            if (insertNode == null) {
                insertNode = createActionNode(xmlDoc, tab.actionname);
            }

            if (tab.addmethodname != undefined && tab.addmethodname != "")
                appendAttri(xmlDoc, insertNode, "methodname", tab.addmethodname);

            var RecorderNode = createRecorderNode(xmlDoc, "insert");

            for ( var j = 0; j < tab.rows[i].cells.length; j++) {

                if ((tab.rows[i].cells[j].fieldname != undefined) && (tab.rows[i].cells[j].fieldtype != undefined)) {
                    // /检查输入项
                    if (checkTDValue(tab.rows[i].cells[j]) == "false")
                        return false;

                    // /添加dropdown项

                    if (tab.rows[i].cells[j].fieldtype.toLowerCase() == "dropdown") {
                        if (tab.rows[i].cells[j].attr != undefined) {
                            var fieldNode = createFieldNode(xmlDoc, tab.rows[i].cells[j].fieldname,
                                    tab.rows[i].cells[j].fieldtype, tab.rows[i].cells[j].attr,
                                    tab.rows[i].cells[j].oldvalue);
                            RecorderNode.appendChild(fieldNode);
                        }
                    } else if (tab.rows[i].cells[j].fieldtype.toLowerCase() == "other") {
                        if (tab.rows[i].cells[j].attr != undefined) {
                            var fieldNode = createFieldNode(xmlDoc, tab.rows[i].cells[j].fieldname,
                                    tab.rows[i].cells[j].fieldtype, tab.rows[i].cells[j].attr,
                                    tab.rows[i].cells[j].oldvalue);
                            RecorderNode.appendChild(fieldNode);

                        }
                    } else {
                        var fieldNode = createFieldNode(xmlDoc, tab.rows[i].cells[j].fieldname,
                                tab.rows[i].cells[j].fieldtype, tab.rows[i].cells[j].innerText,
                                tab.rows[i].cells[j].oldvalue);
                        RecorderNode.appendChild(fieldNode);
                    }
                }

            }

            // ///添加外部参数值
            if (m_objParam != "") {

                var paramSArr = m_objParam.split("*");

                for ( var j = 0; j < paramSArr.length; j++) {
                    if ((paramSArr[j] != undefined) && (paramSArr[j] != "")) {
                        var fieldNode = createFieldStrNode(xmlDoc, paramSArr[j]);
                        RecorderNode.appendChild(fieldNode);

                    }
                }

            }

            insertNode.appendChild(RecorderNode);
        }
    }

    if (insertNode != null) {
        rootNode.appendChild(insertNode);
    }

    if (updateNode != null) {
        rootNode.appendChild(updateNode);
    }
    if (deleteNode != null)
        rootNode.appendChild(deleteNode);

    var checkedRetStr = "";
    // ///向上传送信息
    if (rootNode.childNodes.length > 0) {

        // alert(xmlDoc.xml);

        retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlExcuteJsp.jsp", "POST", "sys_request_xml=" + xmlDoc.xml);

        // //返回信息检查

        checkedRetStr = analyzeReturnXML(retStr);

        if (checkedRetStr == "true") {
            // tab.AbsolutePage= "1";
            tab.RecordCount = "0";

            Table_Refresh_asy(tab.id, true);
        }
    }
    return checkedRetStr;
}

// //隐藏控件
function compentHidden(tab) {

    if (typeof (_TableInput_Text) != "undefined")
        document.all["_TableInput_Text"].getAttribute("_TD").innerText = document.all["_TableInput_Text"].value;

    if (typeof (_TableNumber_Text) != "undefined")
        document.all["_TableNumber_Text"].getAttribute("_TD").innerText = document.all["_TableNumber_Text"].value;

    var fieldtype = tab.fieldtype.split(",");

    for ( var i = 0; i < fieldtype.length; i++) {

        if (fieldtype[i] == "dropdown") {
            dropInit();

        }
        if (fieldtype[i] == "date") {
            CalInit();

        }
        if (fieldtype[i] == "text") {
            TextInit();

        }
        if (fieldtype[i] == "number") {
            NumberTextInit();

        }
    }

}

// ///检查TD的输入内容
function checkTDValue(element) {

    if ((element.isNull != undefined) && (element.isNull.toLowerCase() == "false")) {
        if (checkTDNull(element) == "false") {
            return "false";
        }
    }

    if (element.fieldtype.toLowerCase() == "number") {

        if ((element.intLength != undefined) && (element.floatLength != undefined))
            if (checkTDfloatLength(element, element.intLength, element.floatLength) == "false")
                return "false";

        if ((element.intLength != undefined) && (element.floatLength == undefined))
            if (checkTDIntLength(element, element.intLength) == "false")
                return "false";
    }

    if (element.fieldtype.toLowerCase() == "text") {

        if (element.textLength != undefined)
            if (checkTDStrLength(element, element.textLength) == "false")
                return "false";
        if (element.MinLength != undefined)
            if (checkTDStrMinLength(element, element.MinLength) == "false")
                return "false";

        if ((element.eMail != undefined) && (element.eMail.toLowerCase() == "true"))
            if (checkTDemail(element) == "false")
                return "false";

    }

    return "true";
}

function editTable(tab) {

    for ( var i = 0; i < tab.rows.length; i++) {

        tab.rows[i].edit = "true";

    }

}

// ///从数据表格按钮条的单击事件
function dataPilotButton_onclick() {

    var datapiolt = getOwnerTable(event.srcElement);

    var tab = document.all[datapiolt.tableName];

    var tablename = tab.id;

    switch (event.srcElement.buttontype) {
    case "moveFirst": {

        moveFirst(tab);

        break;
    }
    case "prevPage": {
        prevPage(tab);
        break;
    }
    case "movePrev": {
        movePrev(tab);
        break;
    }
    case "moveNext": {
        moveNext(tab);
        break;
    }
    case "nextPage": {
        nextPage(tab);
        break;
    }
    case "moveLast": {
        moveLast(tab);
        break;
    }

    case "appendRecord": {
        // //detailTable_TRDbclick 数据表格TR双击事件；detailTable_TDclick 数据表格TD单击事件
        if (isfireuserEvent(tablename + "_appendRecord_click"))
            fireUserEvent(tablename + "_appendRecord_click", [ tab ]);
        else {
            // 2005-03-09 Orchid 新增回调函数判断是否可以继续操作
            if (isfireuserEvent(tablename + "_beforeAppendRecord"))
                if (!fireUserEvent(tablename + "_beforeAppendRecord", [ tab ]))
                    break;
            appendRecord(tab);
        }

        if (isfireuserEvent(tablename + "_afterAppend"))
            fireUserEvent(tablename + "_afterAppend", [ tab ]);

        break;
    }
    case "editRecord": {
        if (isfireuserEvent(tablename + "_editRecord_click"))
            fireUserEvent(tablename + "_editRecord_click", [ tab ]);
        else {
            // 2005-03-09 Orchid 新增回调函数判断是否可以继续操作
            if (isfireuserEvent(tablename + "_beforeEditRecord"))
                if (!fireUserEvent(tablename + "_beforeEditRecord", [ tab ]))
                    break;
            editRecord(tab);
        }

        if (isfireuserEvent(tablename + "_afterEdit"))
            fireUserEvent(tablename + "_afterEdit", [ tab ]);

        break;
    }
    case "deleteRecord": {
        // //第三个参数表示外部添加条件信息 格式 name1&type1&value1*name2&type2&value2
        // //第四个参数表示删除actionname 表示
        if (isfireuserEvent(tablename + "_deleteRecord_click"))
            fireUserEvent(tablename + "_deleteRecord_click", [ tab, datapiolt ]);
        else {
            // 2005-03-09 Orchid 新增回调函数判断是否可以继续操作
            if (isfireuserEvent(tablename + "_beforeDeleteRecord"))
                if (!fireUserEvent(tablename + "_beforeDeleteRecord", [ tab, datapiolt ]))
                    break;
            deleteRecord(tab, null, "");
        }

        if (isfireuserEvent(tablename + "_afterDelete"))
            fireUserEvent(tablename + "_afterDelete", [ tab ]);
        break;
    }
    case "cancelRecord": {
        if (isfireuserEvent(tablename + "_cancelRecord_click"))
            fireUserEvent(tablename + "_cancelRecord_click", [ tab, datapiolt ]);
        else {
            // 2005-03-09 Orchid 新增回调函数判断是否可以继续操作
            if (isfireuserEvent(tablename + "_beforeCancelRecord"))
                if (!fireUserEvent(tablename + "_beforeCancelRecord", [ tab, datapiolt ]))
                    break;
            cancelRecord(tab, datapiolt);
        }

        if (isfireuserEvent(tablename + "_afterCancel"))
            fireUserEvent(tablename + "_afterCancel", [ tab ]);
        break;
    }
    case "updateRecord": {
        // //第二个参数表示外部添加条件信息 传入的是主数据集的主健信息
        // //第三个参数表示更新actionname 表示
        // //第四个参数表示添加actionname 表示
        if (isfireuserEvent(tablename + "_updateRecord_click"))
            fireUserEvent(tablename + "_updateRecord_click", [ tab ]);
        else {
            // 2005-03-09 Orchid 新增回调函数判断是否可以继续操作
            if (isfireuserEvent(tablename + "_beforeUpdateRecord"))
                if (!fireUserEvent(tablename + "_beforeUpdateRecord", [ tab ]))
                    break;
            postGridRecord(tab, "");
        }
        if (isfireuserEvent(tablename + "_afterUpdate"))
            fireUserEvent(tablename + "_afterUpdate", [ tab ]);

        break;
    }

    case "print": {
        // //detailTable_TRDbclick 数据表格TR双击事件；detailTable_TDclick 数据表格TD单击事件
        if (isfireuserEvent(tablename + "_print_click"))
            fireUserEvent(tablename + "_print_click", [ tab ]);
        else
            print_DBGrid(tablename);

        break;
    }
    case "excel": {
        // //detailTable_TRDbclick 数据表格TR双击事件；detailTable_TDclick 数据表格TD单击事件
        if (isfireuserEvent(tablename + "_excel_click"))
            fireUserEvent(tablename + "_excel_click", [ tab ]);
        else
            printLExcel("0", tab);

        break;
    }

    default: {
        if (isfireuserEvent(tablename + "_" + event.srcElement.buttontype + "_click"))
            fireUserEvent(tablename + "_" + event.srcElement.buttontype + "_click", [ tab ]);

    }

    }
    // ////////
    initDBGrid(tablename);

    if (tab.dbformname != undefined)
        gridfillform(tab, document.all(tab.dbformname));

    if (isfireuserEvent(tablename + "_afteroperate_click"))
        fireUserEvent(tablename + "_afteroperate_click", [ tab ]);

    // 2005-03-09 Orchid 新增。解决添加后的处理问题
    switch (event.srcElement.buttontype) {
    case "appendRecord": {
        if (isfireuserEvent(tablename + "_afterAppendRecord"))
            fireUserEvent(tablename + "_afterAppendRecord", [ tab ]);
        break;
    }
    case "editRecord": {
        if (isfireuserEvent(tablename + "_afterEditRecord"))
            fireUserEvent(tablename + "_afterEditRecord", [ tab ]);
        break;
    }
    case "cancelRecord": {
        if (isfireuserEvent(tablename + "_afterCancelRecord"))
            fireUserEvent(tablename + "_afterCancelRecord", [ tab ]);
        break;
    }
    case "deleteRecord": {
        if (isfireuserEvent(tablename + "_afterDeleteRecord"))
            fireUserEvent(tablename + "_afterDeleteRecord", [ tab ]);
        break;
    }
    case "updateRecord": {
        if (isfireuserEvent(tablename + "_afterUpdateRecord"))
            fireUserEvent(tablename + "_afterUpdateRecord", [ tab ]);
        break;
    }
    }
    // 2005-03-09 Orchid 新增。解决添加后的处理问题
}

function postGridRecord(gridobj, m_objParam) {

    if (isfireuserEvent(gridobj.id + "_beforePost"))
        if (fireUserEvent(gridobj.id + "_beforePost") == false)
            return false;

    if (gridobj.dbformname == undefined)
        return updateRecord(gridobj, m_objParam);
    else
        return postGridForm(gridobj, m_objParam);

}

function emptyTable(tab) {
    while (tab.rows.length >= 1) {

        tab.deleteRow(tab.rows.length - 1);

    }
    all_recordcount.innerText = "总条目:0";
    all_page.innerText = "总页数:0";

}

function newturnPage1_1(TableID, buttonID, textID) {
    onKeyPressInputInteger(this);
    return turnPageByEnter(event, TableID);
}

function turnPageByEnter(evt, id) {
    evt = (evt) ? evt : event;
    var button = document.getElementById("Covert_Button" + id);
    var charCode = (evt.charCode) ? evt.charCode : ((evt.which) ? evt.which : evt.keyCode);
    if (charCode == 13 || charCode == 3) {
        button.onclick();
        return false;
    }
    return true;
}

function gridfillform(gridobj, fromobj) {

    if (gridobj == undefined)
        return;

    if (fromobj == undefined)
        return;

    fromobj.onsubmit = function formsubmit() {
        return false
    };

    if (gridobj.activeIndex == undefined || gridobj.rows[gridobj.activeIndex] == undefined)
        return;

    var objdisable = true;

    if (gridobj.rows[gridobj.activeIndex].edit == "true")
        objdisable = false;

    var filednamearr = gridobj.fieldname.split(",");

    if (gridobj.rows[gridobj.activeIndex].ValueStr != undefined)
        var filedvaluearr = gridobj.rows[gridobj.activeIndex].ValueStr.split(";");
    else {
        var filedvaluearr = new Array(filednamearr.length);
        for ( var j = 0; j < filednamearr.length; j++)
            filedvaluearr[j] = "";

    }

    for ( var i = 0; i < fromobj.length; i++) {
        var obj = fromobj.item(i);

        if (obj.disabled2 != undefined && obj.disabled2 == "true")
            obj.disabled = true;
        else {

            if (obj.tagName.toUpperCase() == "INPUT")
                obj.disabled = objdisable;
            // obj.readOnly=objdisable;
            if (obj.tagName.toUpperCase() == "SELECT")
                obj.disabled = objdisable;
            // obj.readOnly=objdisable;
            if (obj.tagName.toUpperCase() == "TEXTAREA")
                obj.disabled = objdisable;
            // obj.readOnly=objdisable;

        }

        for ( var j = 0; j < filednamearr.length; j++) {
            if (filednamearr[j].toUpperCase() == obj.id.toUpperCase()) {

                if (obj.tagName.toUpperCase() == "INPUT") {

                    if (obj.type == "text" || obj.type == "hidden" || obj.type == "password") {

                        if (obj.fieldtype == "select") {
                            obj.onafterKeyup = ElementFillGrid;
                            obj.onblur = ElementFillGrid;
                            obj.dropOldValue = trimStr(filedvaluearr[j]);
                            obj.oldvalue = trimStr(filedvaluearr[j]);
                            if (obj.comboBox == undefined)
                                obj.comboBox = new ComboBox(obj.id);
                            obj.comboBox.focusDefaultValue(trimStr(filedvaluearr[j]));

                        } else {
                            obj.oldvalue = trimStr(filedvaluearr[j]);
                            obj.value = trimStr(filedvaluearr[j]);
                            obj.onkeyup = ElementFillGrid;
                            obj.onblur = ElementFillGrid;
                        }
                        obj.dbgridid = gridobj.id;

                    }

                    if (obj.type == "checkbox" || obj.type == "radio") {
                        obj.onblur = ElementFillGrid;
                        obj.onclick = ElementFillGrid;
                        obj.dbgridid = gridobj.id;

                        obj.oldvalue = trimStr(filedvaluearr[j]);
                        if (obj.oldvalue == "1")
                            obj.checked = true;
                        else
                            obj.checked = false;
                    }

                }
                if (obj.tagName.toUpperCase() == "TEXTAREA") {
                    obj.oldvalue = trimStr(filedvaluearr[j]);
                    obj.value = trimStr(filedvaluearr[j]);
                    obj.onkeyup = ElementFillGrid;
                    obj.onblur = ElementFillGrid;
                    obj.dbgridid = gridobj.id;
                }

                if (obj.tagName.toUpperCase() == "SELECT") {
                    obj.oldvalue = trimStr(filedvaluearr[j]);
                    // obj.value =trimStr(filedvaluearr[j]);

                    obj.selectedIndex - 1;

                    if (obj.options.length > 0) {
                        for ( var k = 0; k < obj.options.length; k++) {
                            if (trimStr(obj.item(k).value) == trimStr(filedvaluearr[j])) {
                                obj.selectedIndex = k;
                            }

                        }
                    } else {
                        for ( var k = 0; k < gridobj.rows[gridobj.activeIndex].cells.length; k++) {
                            if (gridobj.rows[gridobj.activeIndex].cells[k].fieldname.toUpperCase() == filednamearr[j]
                                    .toUpperCase()) {

                                obj.text = gridobj.rows[gridobj.activeIndex].cells[k].innerText;

                            }

                        }

                    }
                    obj.onblur = ElementFillGrid;
                    // obj.onchange = ElementFillGrid;
                    obj.dbgridid = gridobj.id;

                }
            }

        }

    }
}
//
function ElementFillGrid(element) {

    var obj;

    if (element == undefined) {
        if (event == null)
            return;
        obj = event.srcElement;
    } else
        obj = element;

    var gridobj = document.all[obj.dbgridid];

    if (gridobj == undefined)
        return;

    var fromobj = document.all(gridobj.dbformname);
    var filednamearr = gridobj.fieldname.split(",");

    if (gridobj.rows[gridobj.activeIndex].ValueStr != undefined)
        var filedvaluearr = gridobj.rows[gridobj.activeIndex].ValueStr.split(";");
    else {
        var filedvaluearr = new Array(filednamearr.length);
        for ( var j = 0; j < filednamearr.length; j++)
            filedvaluearr[j] = "";

    }
    for ( var i = 0; i < fromobj.length; i++) {
        var obj = fromobj.item(i);

        for ( var j = 0; j < filednamearr.length; j++) {
            if (filednamearr[j].toUpperCase() == obj.id.toUpperCase()) {
                if (obj.type == "checkbox" || obj.type == "radio") {
                    if (obj.checked)
                        filedvaluearr[j] = "1";
                    else
                        filedvaluearr[j] = "0";
                } else {
                    if (obj.tagName.toUpperCase() != "SELECT") {
                        if (obj.tagName.toUpperCase() == "INPUT" && obj.fieldtype == "select") {
                            filedvaluearr[j] = obj.attr;
                        } else {
                            var objValue = obj.value;
                            objValue = valuereplase(objValue, '"', "″");
                            objValue = valuereplase(objValue, ";", "；");
                            filedvaluearr[j] = objValue;
                        }

                    } else {
                        if (obj.selectedIndex > -1) {
                            var objValue = obj.value;
                            objValue = valuereplase(objValue, '"', "″");
                            objValue = valuereplase(objValue, ";", "；");
                            filedvaluearr[j] = objValue;
                        } else
                            filedvaluearr[j] = "";
                    }
                }
            }
        }

        for ( var j = 0; j < gridobj.rows[gridobj.activeIndex].cells.length; j++) {

            if (gridobj.rows[gridobj.activeIndex].cells[j].fieldname != undefined)
                if (gridobj.rows[gridobj.activeIndex].cells[j].fieldname.toUpperCase() == obj.id.toUpperCase()) {

                    if (obj.type == "checkbox" || obj.type == "radio") {
                        if (obj.checked)
                            gridobj.rows[gridobj.activeIndex].cells[j].innerText = "1";
                        else
                            gridobj.rows[gridobj.activeIndex].cells[j].innerText = "0";

                    } else {
                        if (obj.tagName.toUpperCase() != "SELECT")
                            gridobj.rows[gridobj.activeIndex].cells[j].innerText = obj.value;
                        else {
                            if (obj.selectedIndex > -1)
                                gridobj.rows[gridobj.activeIndex].cells[j].innerText = obj.item(obj.selectedIndex).text;

                        }
                    }
                }
        }

    }
    var valuStr = "undefined";
    for ( var j = 0; j < filednamearr.length; j++) {

        if (valuStr == "undefined") {
            if (filedvaluearr[j] == "")
                valuStr = "";
            else
                valuStr = filedvaluearr[j];
        } else {
            if (filedvaluearr[j] == "")
                valuStr = valuStr + ";";
            else
                valuStr = valuStr + ";" + filedvaluearr[j];
        }

    }

    gridobj.rows[gridobj.activeIndex].ValueStr = valuStr;

}

//
function postGridForm(gridobj, m_objParam) {

    if (gridobj == undefined)
        return;

    if (gridobj.actionname == undefined || gridobj.actionname == "") {
        alert("action标示不能为空！");
        return;
    }

    var xmlDoc = createDoc();
    var rootNode = createRootNode(xmlDoc);

    var updateNode = null;
    var insertNode = null;
    var deleteNode = null;

    var elementobj = undefined;

    var filednamearr = gridobj.fieldname.split(",");
    var fieldCheckArr = gridobj.fieldCheck.split(";");
    var filedcn = gridobj.fieldCN.split(",");

    for ( var i = 0; i < gridobj.rows.length; i++) {

        // //////// 删除记录信息
        if (gridobj.rows[i].operate == "delete") {

            if (deleteNode == null)
                deleteNode = createActionNode(xmlDoc, gridobj.actionname);

            if (gridobj.delmethodname != undefined && gridobj.delmethodname != "")
                appendAttri(xmlDoc, deleteNode, "methodname", gridobj.delmethodname);

            var wherrstr = gridobj.rows[i].whStr;

            if (m_objParam != "") {
                wherrstr = wherrstr + "*" + m_objParam;
            }

            var reorderNode = createDeleteRecord(wherrstr, xmlDoc);

            for ( var j = 0; j < gridobj.rows[i].cells.length; j++) {
                if ((gridobj.rows[i].cells[j].fieldname != undefined)
                        && (gridobj.rows[i].cells[j].fieldtype != undefined)) {

                    var fieldNode = createFieldNode(xmlDoc, gridobj.rows[i].cells[j].fieldname,
                            gridobj.rows[i].cells[j].fieldtype, gridobj.rows[i].cells[j].oldvalue,
                            gridobj.rows[i].cells[j].oldvalue);
                    reorderNode.appendChild(fieldNode);

                }
            }

            deleteNode.appendChild(reorderNode);

        }

        if (gridobj.rows[i].operate == "insert") {
            if (gridobj.rows[i].ValueStr == undefined)
                continue;

            var filedvaluearr = gridobj.rows[i].ValueStr.split(";");

            gridobj.activeIndex = i;
            gridfillform(gridobj, document.all(gridobj.dbformname));

            if (insertNode == null)
                insertNode = createActionNode(xmlDoc, gridobj.actionname);

            if (gridobj.addmethodname != undefined && gridobj.addmethodname != "")
                appendAttri(xmlDoc, insertNode, "methodname", gridobj.addmethodname);

            var RecorderNode = createRecorderNode(xmlDoc, "insert");

            for ( var j = 0; j < filednamearr.length; j++) {

                elementobj = undefined;

                for ( var k = 0; k < document.all(gridobj.dbformname).length; k++) {
                    if (filednamearr[j].toUpperCase() == document.all(gridobj.dbformname).item(k).id.toUpperCase()) {
                        var elementobj = document.all(gridobj.dbformname).item(k);
                        break;
                    }
                }
                if (elementobj != undefined) {
                    elementobj.alertTitle = filedcn[j];

                    var fieldOneCheckArr = fieldCheckArr[j].split(",");

                    for ( var k = 0; k < fieldOneCheckArr.length; k++) {
                        if (fieldOneCheckArr[k].length > 0) {
                            var fieldValueCheckArr = fieldOneCheckArr[k].split("=");

                            if (fieldValueCheckArr[0] == "isNull")
                                elementobj.isNull = fieldValueCheckArr[1];
                            else if (fieldValueCheckArr[0] == "textLength")
                                elementobj.textLength = fieldValueCheckArr[1];
                            else if (fieldValueCheckArr[0] == "intLength")
                                elementobj.intLength = fieldValueCheckArr[1];
                            else if (fieldValueCheckArr[0] == "floatLength")
                                elementobj.floatLength = fieldValueCheckArr[1];
                            else if (fieldValueCheckArr[0] == "eMail")
                                elementobj.eMail = fieldValueCheckArr[1];

                        }

                    }
                    if (checkInputValue(elementobj) == "false")
                        return false;

                }
                var fieldNode = createFieldNode(xmlDoc, filednamearr[j], "text", filedvaluearr[j]);
                RecorderNode.appendChild(fieldNode);

            }

            // ///添加外部参数值
            if (m_objParam != "") {

                var paramSArr = m_objParam.split("*");

                for ( var j = 0; j < paramSArr.length; j++) {
                    if ((paramSArr[j] != undefined) && (paramSArr[j] != "")) {
                        var fieldNode = createFieldStrNode(xmlDoc, paramSArr[j]);
                        RecorderNode.appendChild(fieldNode);

                    }
                }

            }
            insertNode.appendChild(RecorderNode);
        }

        if (gridobj.rows[i].operate == "update") {

            var filedvaluearr = gridobj.rows[i].ValueStr.split(";");
            var filedoldvaluearr = gridobj.rows[i].OldValueStr.split(";");

            gridobj.activeIndex = i;

            gridfillform(gridobj, document.all(gridobj.dbformname));

            if (updateNode == null)
                updateNode = createActionNode(xmlDoc, gridobj.actionname);

            if (gridobj.editmethodname != undefined && gridobj.editmethodname != "")
                appendAttri(xmlDoc, updateNode, "methodname", gridobj.editmethodname);

            var RecorderNode = createRecorderNode(xmlDoc, "update");

            for ( var j = 0; j < filednamearr.length; j++) {

                elementobj = undefined;

                for ( var k = 0; k < document.all(gridobj.dbformname).length; k++) {
                    if (filednamearr[j].toUpperCase() == document.all(gridobj.dbformname).item(k).id.toUpperCase()) {
                        var elementobj = document.all(gridobj.dbformname).item(k);
                        break;
                    }
                }
                if (elementobj != undefined) {
                    elementobj.alertTitle = filedcn[j];

                    var fieldOneCheckArr = fieldCheckArr[j].split(",");

                    for ( var k = 0; k < fieldOneCheckArr.length; k++) {
                        if (fieldOneCheckArr[k].length > 0) {
                            var fieldValueCheckArr = fieldOneCheckArr[k].split("=");

                            if (fieldValueCheckArr[0] == "isNull")
                                elementobj.isNull = fieldValueCheckArr[1];
                            else if (fieldValueCheckArr[0] == "textLength")
                                elementobj.textLength = fieldValueCheckArr[1];
                            else if (fieldValueCheckArr[0] == "intLength")
                                elementobj.intLength = fieldValueCheckArr[1];
                            else if (fieldValueCheckArr[0] == "floatLength")
                                elementobj.floatLength = fieldValueCheckArr[1];
                            else if (fieldValueCheckArr[0] == "eMail")
                                elementobj.eMail = fieldValueCheckArr[1];

                        }

                    }
                    if (checkInputValue(elementobj) == "false")
                        return false;

                }
                var fieldNode = createFieldNode(xmlDoc, filednamearr[j], "text", filedvaluearr[j], filedoldvaluearr[j]);
                RecorderNode.appendChild(fieldNode);

            }
            // ///添加外部参数值
            if (m_objParam != "") {

                var paramSArr = m_objParam.split("*");

                for ( var j = 0; j < paramSArr.length; j++) {
                    if ((paramSArr[j] != undefined) && (paramSArr[j] != "")) {
                        var fieldNode = createFieldStrNode(xmlDoc, paramSArr[j]);
                        RecorderNode.appendChild(fieldNode);

                    }
                }

            }

            updateNode.appendChild(RecorderNode);

        }

    }

    if (insertNode != null) {
        rootNode.appendChild(insertNode);
    }

    if (updateNode != null) {
        rootNode.appendChild(updateNode);
    }
    if (deleteNode != null) {

        rootNode.appendChild(deleteNode);

    }

    var checkedRetStr = "";

    // ///向上传送信息
    if (rootNode.childNodes.length > 0) {
        // alert(xmlDoc.xml);
        retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlExcuteJsp.jsp", "POST", "sys_request_xml=" + xmlDoc.xml);

        checkedRetStr = analyzeReturnXML(retStr);
        // //返回信息检查
        if (checkedRetStr == "true") {
            // tab.AbsolutePage= "1";
            gridobj.RecordCount = "0";

            Table_Refresh_asy(gridobj.id, true);
        }
    }
    return checkedRetStr;

}

// /////////////////////////////////////////////////////////////////////////////////////////////////////
var isMouDown = false;
var OldPlace = null;
var nowPlace = null;
var curTd = null;

var tableALL = null;

function domousedown() {
    var obj = window.event.srcElement;

    try {

        var nextTd = null;

        if (obj == null || (obj.tagName.toUpperCase() != "SPAN" && obj.tagName.toUpperCase() != "TD"))
            return;

        if (obj.tagName.toUpperCase() == "SPAN") {
            obj = obj.parentElement;
        }
        ;

        var objTableName = getOwnerTable(obj).id;
        var tableName = objTableName.split("_");
        if (tableName.length > 1)
            objTableName = tableName[1];

        var divTi = document.all["divfd_" + objTableName];

        nextTd = obj.previousSibling;

        while (nextTd.style.display == "none") {
            nextTd = nextTd.previousSibling;
        }
        ;

        var absLeft = 0;

        var odiv = document.all["div_" + objTableName];

        while (odiv != document.body) {
            absLeft += odiv.offsetLeft;
            odiv = odiv.offsetParent;
        }
        ;

        if (window.event.x + document.body.scrollLeft + divTi.scrollLeft > absLeft + obj.offsetLeft + obj.offsetWidth
                - 13) {

            curTd = obj;
            isMouDown = true;
            OldPlace = window.event.x + window.document.body.scrollLeft + divTi.scrollLeft;
        } else if (window.event.x + document.body.scrollLeft + divTi.scrollLeft > absLeft + obj.offsetLeft
                && window.event.x + document.body.scrollLeft + divTi.scrollLeft < absLeft + obj.offsetLeft + 13) {
            if (nextTd.cellIndex == 0)
                return;

            curTd = nextTd;
            isMouDown = true;
            OldPlace = window.event.x + window.document.body.scrollLeft + divTi.scrollLeft;
        }
        ;
    } catch (ex) {
    }
    ;
};

function domousemove() {
    try {
        var obj = window.event.srcElement;
        if (obj == null || (obj.tagName.toUpperCase() != "SPAN" && obj.tagName.toUpperCase() != "TD"))
            return;

        if (obj.tagName.toUpperCase() == "SPAN") {
            obj = obj.parentElement;
        }
        ;

        var objTableName = getOwnerTable(obj).id;
        var tableName = objTableName.split("_");
        if (tableName.length > 1)
            objTableName = tableName[1];

        var divTou = document.all["divt_" + objTableName];
        var divTi = document.all["divfd_" + objTableName];
        var divbot = document.all["divb_" + objTableName];

        var absLeft = 0;

        var odiv = document.all["div_" + objTableName];

        while (odiv != document.body) {
            absLeft += odiv.offsetLeft;
            odiv = odiv.offsetParent;
        }
        ;
        // alert("111");

        if (window.event.x + document.body.scrollLeft + divTi.scrollLeft > absLeft + obj.offsetLeft + obj.offsetWidth
                - 13
                || (window.event.x + document.body.scrollLeft + divTi.scrollLeft > absLeft + obj.offsetLeft
                        && window.event.x + document.body.scrollLeft + divTi.scrollLeft < absLeft + obj.offsetLeft + 13 && obj.cellIndex != 1)
                || isMouDown) {
            try {
                divTou.style.cursor = "col-resize";
            } catch (ex) {
                // divTou.style.cursor="move";
            }
            ;
        } else {
            divTou.style.cursor = "default";

        }

    } catch (ex) {
        alert(ex.description);
    }
    ;
};

function domouseup() {

    if (isMouDown) {
        try {

            var objTableName = getOwnerTable(curTd).id;
            var tableName = objTableName.split("_");
            if (tableName.length > 1)
                objTableName = tableName[1];

            var divTi = document.all["divfd_" + objTableName];
            nowPlace = window.event.x + document.body.scrollLeft + divTi.scrollLeft;

            if (isNaN(OldPlace - nowPlace)) {
                isMouDown = false;
                return;
            }
            ;

            curTd.width = curTd.width - (OldPlace - nowPlace);

            document.all[objTableName].fdwidth = document.all[objTableName].offsetWidth - (OldPlace - nowPlace);

            isMouDown = false;

            var tdIndex = countOT(curTd);

            for ( var i = 0; i < document.all[objTableName].rows.length; i++) {
                var otd = document.all[objTableName].rows[i].cells[tdIndex];
                if (otd != null) {
                    otd.width = otd.width - (OldPlace - nowPlace);
                }
                ;
            }
            ;

            for ( var i = 0; i < document.all[objTableName].rows[0].cells.length; i++) {

                document.all["Title_" + objTableName].rows[0].cells[i].width = document.all[objTableName].rows[0].cells[i].offsetWidth;

            }
            ;

            initDBGrid(objTableName);

        } catch (ex) {
            isMouDown = false;

            if (document.all[objTableName].rows.length > 0)
                for ( var i = 0; i < document.all[objTableName].rows[0].cells.length; i++) {

                    document.all["Title_" + objTableName].rows[0].cells[i].width = document.all[objTableName].rows[0].cells[i].offsetWidth;

                }
            ;

        }
        ;

    }
    ;
};

function cellPerentTowidth(tableName) {

    for ( var i = 0; i < document.all[tableName].rows.length; i++) {
        for ( var j = 0; j < document.all[tableName].rows[i].cells.length; j++)
            document.all[tableName].rows[i].cells[j].width = document.all[tableName].rows[i].cells[j].offsetWidth;

    }

    for ( var i = 0; i < document.all["Title_" + tableName].rows[0].cells.length; i++) {

        var otd = document.all["Title_" + tableName].rows[0].cells[i];

        otd.width = otd.offsetWidth;

    }
    ;
}

function domouseout() {
    document.body.style.cursor = "default";
};

function countOT(otd) {

    if (otd == null || otd.tagName.toUpperCase() != "TD")
        return;
    for ( var i = 0; i < otd.parentElement.cells.length; i++) {
        var td = otd.parentElement.cells[i];
        if (td == otd)
            return i;
    }
    ;
};

var _DbGridObject;

var divbot;
this.doscroll = doscroll;
function doscroll() {

    var obj = window.event.srcElement;
    var tableName = obj.id.split("_");

    if (tableName.length > 1)
        tableName = tableName[1];

    var disWid = document.all["divfd_" + tableName].scrollLeft;
    // alert(disWid);
    document.all["divt_" + tableName].scrollLeft = disWid;
    if (document.all[tableName].isTotal == "true")
        document.all["divTotal_" + tableName].scrollLeft = disWid;

};
var reLoadCount = 0;

function body_afterResize(el) {
    try {
        var tableName = el.id.split("_")[1];

        if (document.all[tableName].fdwidth != undefined) {
            document.all[tableName].style.width = document.all[tableName].fdwidth;
        }
        if (document.all[tableName].fdwidth == undefined) {
            document.all[tableName].style.width = document.body.offsetWidth - 50;
        }
        document.all["divfd_" + tableName].style.width = document.body.offsetWidth - 50;
        document.all["divt_" + tableName].style.width = document.all["divfd_" + tableName].clientWidth;

        if (document.all[tableName].bottomVisible == "true")
            document.all["divb_" + tableName].style.width = document.all["divfd_" + tableName].clientWidth;

        if (document.all[tableName].isTotal == "true") {
            document.all["divTotal_" + tableName].style.width = document.all["divfd_" + tableName].clientWidth;
            document.all["tableTotal_" + tableName].style.width = document.all[tableName].style.width;
        }

        document.all[tableName].parentNode.style.width = document.all[tableName].style.width;
        // document.all["bottom_"+tableName].style.width =
        // document.all[tableName].style.width;
        document.all["Title_" + tableName].style.width = document.all[tableName].style.width;

        WidthEquals("Title_" + tableName, tableName);
    } catch (ex) {
    }

}

// ////////////初始化DbGrid 基本信息
function initDBGrid(tableName) {

    _DbGridObject = document.all[tableName];

    for ( var i = 0; i < _DbGridObject.rows.length; i++) {

        if (isfireuserEvent(tableName + "_TRDbclick"))
            _DbGridObject.rows[i].ondblclick = window[tableName + "_TRDbclick"];

        if (isfireuserEvent(tableName + "_TRclick"))
            _DbGridObject.rows[i].ondblclick = window[tableName + "_TRclick"];

        if (isfireuserEvent(tableName + "_TDclick"))
            for ( var j = 0; j < _DbGridObject.rows[i].cells.length; j++) {
                _DbGridObject.rows[i].cells[j].onclick = window[tableName + "_TDclick"];

            }
    }

    if (_DbGridObject.dbformname != undefined) {
        var fromobj = document.all(_DbGridObject.dbformname);
        for ( var i = 0; i < fromobj.length; i++) {
            var obj = fromobj.item(i);
            if (obj.tagName.toUpperCase() == "INPUT") {
                obj.disabled = true;
                // alert();
                // obj.readOnly=true;
            }
            if (obj.tagName.toUpperCase() == "SELECT")
                obj.disabled = true;
            // obj.readOnly=true;
            if (obj.tagName.toUpperCase() == "TEXTAREA")
                obj.disabled = true;
            // obj.readOnly=true;
        }
    }

    // ////////////////添加resize 和 oncontextmenu 事件
    onbodyprivateenvent();
    // ///////////////////////////

    if (_DbGridObject.rows.length > 0 && _DbGridObject.activeIndex != undefined)
        _DbGridObject.rows[_DbGridObject.activeIndex].className = "gridSelectedHighlight";

    if (_DbGridObject.dbformname != undefined)
        gridfillform(_DbGridObject, document.all(_DbGridObject.dbformname));

    // 数据集求和
    tableTotal(_DbGridObject);

    document.all["divt_" + tableName].attachEvent("onmousedown", this.domousedown);
    document.all["divt_" + tableName].attachEvent("onmouseup", this.domouseup);
    document.all["divt_" + tableName].attachEvent("onmousemove", this.domousemove);
    document.all["divt_" + tableName].attachEvent("onmouseout", this.domouseout);
    document.all["divfd_" + tableName].attachEvent("onscroll", this.doscroll);

    body_afterResize(document.all["div_" + tableName]);

    if (reLoadCount == 0 && _DbGridObject.rows.length > 0) {
        cellPerentTowidth(tableName);

        WidthEquals("Title_" + tableName, tableName);
    }
    reLoadCount = 1;

    if (document.all[tableName].printSQL == undefined || document.all[tableName].printSQL == "")
        init_printConent(tableName);

    // edit by leonwoo
    // 增加回调函数接口 ，
    // 用于在列表中增加一列序号，重新生成列序号
    // 重新调用排序函数
    if (typeof (itemTab_afterSort) == "function")
        itemTab_afterSort();

}
// //// 数据集求和
function tableTotal(tab) {
    if (tab.isTotal == "true") {
        var toatalCount = 0;
        for ( var i = 0; i < document.all["Title_" + tab.id].rows[0].cells.length; i++) {

            if (tab.rows.length > 0 && trimStr(tab.rows[0].cells[i].fieldtype.toLowerCase()) == "number") {
                toatalCount = 0;
                for ( var j = 0; j < tab.rows.length; j++) {
                    if (!isNaN(parseFloat(trimStr(tab.rows[j].cells[i].innerText))))
                        toatalCount = toatalCount + parseFloat(trimStr(tab.rows[j].cells[i].innerText));

                }
                document.all["tableTotal_" + tab.id].rows[0].cells[i].innerText = toatalCount;
            }

        }
    }
}

function init_printConent(tableName) {
    var tableObject = document.all[tableName];

    var sqlStr = tableObject.SQLStr;

    var edindex = sqlStr.indexOf("from");
    fieldStr = sqlStr.substring(6, edindex);

    var fieldArr = fieldStr.split(",");
    var enumType = tableObject.enumType.split(",");
    var fieldtype = tableObject.fieldtype.split(",");

    for ( var i = 0; i < fieldtype.length; i++) {
        if (fieldtype[i] == "dropdown") {
            fieldArr[i] = " (select ENUITEMLABEL from ptEnuDetail where EnuItemValue=" + fieldArr[i] + " and EnuType='"
                    + enumType[i] + "') ";

        }

    }

    document.all[tableName].printSQL = "SELECT " + fieldArr.join(",") + " FROM " + sqlStr.substring(edindex + 4);

}

// ////////////////////////////////////////////打印excel///////////////////////////////////////////////////////

var titleArr;
var dataArr;
var visibleArr;
// 打印列表页
function printLExcel(printtype, tab) {

    var titleTab = document.all("Title_" + tab.id)
    titleArr = new Array();
    var TmptitleArr = tab.fieldCN.split(",");
    visibleArr = tab.visible.split(",");
    var index = 0;
    for ( var i = 0; i < TmptitleArr.length; i++) {
        if (visibleArr[i] != "false") {
            titleArr[index] = TmptitleArr[i];
            index = index + 1;
        }

    }

    dataArr = new Array();

    // //////////打印当前页记录
    if (printtype == "0") {

        var xmlDoc = createDomDocument("<reqStr/>");

        var rootNode = xmlDoc.documentElement;
        // if( tab.RecordCount/1<200){
        // appendAttri(xmlDoc, rootNode, "type", "excel");
        // }else
        appendAttri(xmlDoc, rootNode, "type", "excelhou");

        var childNode = appendNode(xmlDoc, xmlDoc.documentElement, "childNode");
        appendAttri(xmlDoc, childNode, "fieldType", tab.fieldtype);
        appendAttri(xmlDoc, childNode, "fieldCN", tab.fieldCN);
        appendAttri(xmlDoc, childNode, "fieldWidth", tab.fieldwidth);
        appendAttri(xmlDoc, childNode, "enumType", tab.enumType);
        //zhanrui 2010/07/28
        appendAttri(xmlDoc, childNode, "visible", tab.visible);

        appendAttri(xmlDoc, childNode, "SQLStr", tab.SQLStr);
        appendAttri(xmlDoc, childNode, "whereStr", encode(decode(tab.whereStr)));

        rootNode.appendChild(childNode);
        // alert(xmlDoc.xml);
        // if( tab.RecordCount/1<200){
        // ExecServerPrgm_synsh(g_jsContextPath+"/BI/util/DropDownJsp.jsp","POST","xx="+encode(xmlDoc.xml),"","",tab.id,Refresh_Excel);
        // }else

        // ExecServerPrgm_synsh(g_jsContextPath+"/BI/util/expExcelJsp.jsp","POST","xx="+encode(xmlDoc.xml),"","",tab.id,open_Excel);

        openWindowWithPost(g_jsContextPath+"/BI/util/expExcelJsp.jsp", "Excel", encode(xmlDoc.xml));
        return;
    }

    // //////////打印当前页记录
    if (printtype != "0") {
        for ( var i = 0; i < tab.rows.length; i++) {
            dataArr[i] = new Array();
            index = 0;
            TmptitleArr = tab.rows[i].ValueStr.split(";");
            for ( var j = 0; j < visibleArr.length; j++) {

                if (visibleArr[j] != "false") {
                    dataArr[i][index] = TmptitleArr[j];
                    index = index + 1;
                }
            }
        }
        writeDataExcel();
    }

}
function open_Excel(id, retStr) {

    if (retStr != "") {

        open(retStr);
    }

}

//
function openWindowWithPost(url, name, value) {
    var newWindow = window.open(url, name);
    if (!newWindow)
        return false;
    var html = "";
    html += "<html><head></head><body><form id='formid' method='post' action='" + url + "'>";
    html += "<input type='hidden' name='xx'" + " value='" + value + "'/>";
    html += "</form><script type='text/javascript'>document.getElementById(\"formid\").submit();setTimeout(\"self.close()\",10000);</script></body></html>";
    newWindow.document.write(html);
    return newWindow;
}

function Refresh_Excel(id, retStr) {
    if (retStr != "") {
        retArr = retStr.split("&&");
        for ( var i = 0; i < retArr.length; i++) {
            dataArr[i] = new Array();
            TmptitleArr = retArr[i].split("^^");
            index = 0;
            for ( var j = 0; j < visibleArr.length; j++) {

                if (visibleArr[j] != "false") {
                    dataArr[i][index] = TmptitleArr[j];
                    index = index + 1;
                }
            }
        }
    }

    writeDataExcel();
}

function writeDataExcel() {
    var app = new ActiveXObject("Excel.Application");
    var xlbook = app.Workbooks.Add();
    var xlsheet = xlbook.Worksheets(1);
    xlsheet.Application.visible = true;
    xlsheet.PageSetup.Orientation = 2; // 横向

    var pStr = new Array("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
            "T", "U", "V", "W", "X", "Y", "Z", "AA", "AB", "AC", "AD", "AE", "AF", "AG", "AH", "AI", "AJ", "AK", "AL",
            "AM", "AN", "AO", "AP", "AQ", "AR", "AS", "AT", "AU", "AV");
    var pPos = "";
    with (xlsheet) {
        // 表格
        pPos = pStr[0] + (1) + ":" + pStr[titleArr.length > 0 ? titleArr.length - 1 : titleArr.length]
                + (dataArr.length + 1);

        for ( var i = 0; i < titleArr.length; i++) {
            pPos = pStr[i] + (1);
            pPos += ":" + pPos;
            range(pPos).Font.Bold = true;

            range(pPos).Value = titleArr[i];

        }
        // 内容
        for ( var i = 0; i < dataArr.length; i++) {
            for ( var j = 0; j < dataArr[0].length; j++) {
                pPos = pStr[j] + (i + 2);
                pPos += ":" + pPos;

                range(pPos).Value = dataArr[i][j];

            }
        }
        // 宽度自适应
        for ( var j = 0; j < titleArr.length; j++) {
            pPos = pStr[j];
            pPos += ":" + pPos;
            Columns(pPos).EntireColumn.AutoFit;
        }
    }
}
