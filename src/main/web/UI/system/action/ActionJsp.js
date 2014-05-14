//////初始化信息
var menu;

function body_load()
{

    divfd_ActionTable.style.height = document.body.clientHeight - 180 + "px";
	 //divfd_ActionTable.style.height="100%";
	 ActionTable.fdwidth="100%";
     ActionTable.actionname     ="sm0011";
     ActionTable.addmethodname  ="addenum";
     ActionTable.editmethodname ="editenum";
     ActionTable.delmethodname  ="delenum";
     
     initDBGrid("ActionTable");

}


/////主数据表格TD的单击事件
function ActionTable_TDclick(el){


	el = event.srcElement;

	var trobj =getOwnerTR(el);

	if (trobj.edit=="true")
	{
		///根据数据类型生成添加对象
		if (el.fieldtype=="text")
		{

               CreateText(el);
		}
		if (el.fieldType=="number")
		{
			CreateNumberText(el);
		}
	}


}


////////查询函数
function queryClick(){

	var  whereStr =" 1=1 ";

	if (trimStr(document.all["cationid"].value) != "")
		whereStr += " and ( LogicCode like '%"+document.all.cationid.value+"%')";

	if (trimStr(document.all["actionclass"].value) != "")
		whereStr += " and ( LogicClass like '%"+document.all.actionclass.value+"%')";

	if (trimStr(document.all["actiondes"].value) != "")
		whereStr += " and ( LogicDesc like '%"+document.all.actiondes.value+"%')";

	

	if (whereStr !=document.all["ActionTable"].whereStr){
		document.all["ActionTable"].whereStr=whereStr +" order by 1 ";
		document.all["ActionTable"].RecordCount="0";
		document.all["ActionTable"].AbsolutePage="1";


		Table_Refresh("ActionTable");
    }

}
