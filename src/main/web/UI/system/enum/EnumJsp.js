//////初始化信息
function body_load() {
    ///表示主从数据集高度的定制 格式divfd_ID
    //var divhight = document.body.clientHeight - 180 ;

    divfd_EnumTable.style.height = 220;
    divfd_detailTable.style.height = "150";
    EnumTable.fdwidth = "100%";
    detailTable.fdwidth = "100%";
    EnumTable.actionname = "sm0051";
    EnumTable.addmethodname = "addenum";
    EnumTable.editmethodname = "editenum";
    EnumTable.delmethodname = "delenum";

    initDBGrid("EnumTable");

    detailTable.actionname = "sm0054";
    detailTable.addmethodname = "addenum";
    detailTable.editmethodname = "editenum";
    detailTable.delmethodname = "delenum";

    initDBGrid("detailTable");


}


/////主数据表格TR的双击事件
function EnumTable_TRDbclick(el) {

    ///获取主健信息
    var whArr = EnumTable.rows[EnumTable.activeIndex].whStr.split("&");
    ///分解主健信息构造查询条件

    var whereStr = "and(EnuType = '" + whArr[2] + "') order by 1";
    ///给从表添加查询条件
    detailTable.whereStr = whereStr;

    ///刷新从表记录
    detailTable.AbsolutePage = "1";
    detailTable.RecordCount = "0";
    Table_Refresh("detailTable", true, body_load);


}


/////主数据表格TD的单击事件
function EnumTable_TDclick(el) {

    el = event.srcElement;
    var trobj = getOwnerTR(el);

    if (trobj.edit == "true") {
        ///根据数据类型生成添加对象
        if (el.fieldtype == "text") {
            CreateText(el);
        }

    }


}

////////////枚举添加一条纪录
function EnumTable_appendRecord_click(tab) {

    appendRecord(tab);
    while (detailTable.rows.length > 0) {
        detailTable.deleteRow(detailTable.rows.length - 1);
    }
}


/////从数据表格TD的单击事件
function detailTable_TDclick(el) {

    el = event.srcElement;

    var trobj = getOwnerTR(el);

    if (trobj.edit == "true") {

        if (el.fieldtype == "text") {
            CreateText(el);
        }
        if (el.fieldtype == "int") {
            CreateNumberText(el);
        }
    }


}

function detailTable_afterChecked(obj) {

    //     if (obj.checked){
    //
    //          obj.checked= false;
    //     }
}

/////////////保存枚举
function detailTable_updateRecord_click(tab) {
    var whArr = EnumTable.rows[EnumTable.activeIndex].whStr.split("&");
    ///分解主健信息构造查询条件

    var whereStr = "EnuType&" + whArr[1] + "&" + whArr[2];
    updateRecord(tab, whereStr);


}


////////查询函数
function queryClick() {

    var whereStr = "";

    if (trimStr(document.all["cationid"].value) != "")
        whereStr += " and ( EnuType like '%" + document.all.cationid.value + "%')";

    if (trimStr(document.all["actionclass"].value) != "")
        whereStr += " and ( EnuName like '%" + document.all.actionclass.value + "%')";

    if (trimStr(document.all["actiondes"].value) != "")
        whereStr += " and ( EnuDesc like '%" + document.all.actiondes.value + "%')";


    if (whereStr != document.all["EnumTable"].whereStr) {
        document.all["EnumTable"].whereStr = whereStr + " order by 1 ";
        document.all["EnumTable"].RecordCount = "0";
        document.all["EnumTable"].AbsolutePage = "1";


        Table_Refresh("EnumTable");
    }

}

