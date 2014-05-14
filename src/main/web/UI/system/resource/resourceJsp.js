//////初始化信息
function body_load() {
  resourceTable.fdwidth = "1000px";
  divfd_resourceTable.style.height = document.body.offsetHeight - 130;
  resourceTable.actionname = "sm0071";
  // /MenuTable.addmethodname ="addenum";
  resourceTable.editmethodname = "editenum";
  resourceTable.delmethodname = "delenum";
  initDBGrid("resourceTable");
}

// ///主数据表格TD的单击事件
function resourceTable_TDclick(el) {

  el = event.srcElement;

  var trobj = getOwnerTR(el);

  if (trobj.edit == "true") {
    // /根据数据类型生成添加对象
    if (el.fieldtype == "text") {
      CreateText(el);
    }
    if (el.fieldtype == "dropdown") {
      el.enumType = "restype";

      el.fieldTitle = "编码,名称";
      dropdown(el);
    }
  }

}
