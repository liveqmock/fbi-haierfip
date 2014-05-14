//////初始化信息
function body_load()
{
	if (parent.window.paramValue)
		m_objParam = parent.window.paramValue.value;
	else
		m_objParam = "";


   var tab = document.all("MenuTable");


   var whereStr =  "and (parentmenuid='" +m_objParam+"') order by Levelidx" ;

	tab.whereStr=whereStr;

     MenuTable.actionname     ="sm0021";
     ///MenuTable.addmethodname  ="addenum";
     MenuTable.editmethodname ="editenum";
     MenuTable.delmethodname  ="delenum";

	Table_Refresh("MenuTable",false);



     initDBGrid("MenuTable");

}



/////主数据表格TD的单击事件
function MenuTable_TDclick(el){

	el = event.srcElement;

	var trobj =getOwnerTR(el);

	if (trobj.edit=="true")
	{
		///根据数据类型生成添加对象
		if (el.fieldtype=="text"){
			CreateText(el);
		}
		if (el.fieldtype=="number"){
			CreateNumberText(el);
		}

		if (el.fieldtype=="dropdown"){

               switch (el.fieldname){
                    case "targetMachine":{
                         el.enumType = "matchinetype";
                         el.fieldTitle = "编码,名称";
                         break;
                    }
                    case "OpenWindow":{
                         el.enumType = "openwindow";
                         el.fieldTitle = "编码,名称";
                         break;

                    }

               }
               dropdown(el);
		}
	}


}

////////////枚举添加一条纪录
function MenuTable_updateRecord_click(tab){

	updateRecord(tab,"ParentMenuID&text&"+parent.window.paramValue.value);

}

