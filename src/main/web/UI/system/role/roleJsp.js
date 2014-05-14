//////初始化信息
function body_load()
{
	///表示主从数据集高度的定制 格式divfd_ID

	 divfd_roleTable.style.height="100%";
	 roleTable.fdwidth="100%";
     roleTable.actionname     ="sm0063";
     ///MenuTable.addmethodname  ="addenum";
     roleTable.editmethodname ="editenum";
     roleTable.delmethodname  ="delenum";

     initDBGrid("roleTable");

}


/////主数据表格TD的单击事件
function roleTable_TDclick(el){

	el = event.srcElement;

	var trobj =getOwnerTR(el);

	if (trobj.edit=="true")
	{
		///根据数据类型生成添加对象
		if (el.fieldtype=="text"){
			CreateText(el);
		}

	}
}

