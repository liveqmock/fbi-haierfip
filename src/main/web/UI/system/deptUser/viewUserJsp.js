var m_objParam = "";

function userTable_TRDbclick(el){

}

function body_load()
{

	 chgArea();

}


function userTable_TDclick(el){
}

function reset_click()
{

    emptyTable(userTable);
    //iq0011_body_onload();
}
function chgArea()
{

   //div_gcoreusercard.style.width = 950;
   divfd_userTable.style.height = 420;
   WidthEquals("Title_userTable","userTable");

}

function datapilot_onclick(){

	var datapiolt=getOwnerTable(event.srcElement);

	var tab =document.all[datapiolt.tableName];

	var pagesize=tab.rows.length;



	switch(event.srcElement.buttontype){
		case "moveFirst":{

			moveFirst(tab);

			break;
		}
		case "prevPage":{
			prevPage(tab);
			break;
		}
		case "movePrev":{
		    movePrev(tab);
			break;
		}
		case "moveNext":{
			moveNext(tab);
			break;
		}
		case "nextPage":{
			nextPage(tab);
			break;
		}
		case "moveLast":{
			moveLast(tab);
			break;
		}


	}
}

////////²éÑ¯º¯Êý
function queryClick(){

	var  whereStr ="";

	if(document.all.dept.selectedIndex >0)
		whereStr += " and ( PTDept.DeptID='"+document.all.dept.value+"')";

	if (trimStr(document.all["name"].value) != "")
		whereStr += " and ( PTOper.OperName='"+document.all.name.value+"')";

	if (trimStr(document.all["id"].value) != "")
		whereStr += " and ( PTOper.OperID='"+document.all["id"].value+"')";

	if(document.all.role.selectedIndex >0)
		whereStr += " and ( PTRole.RoleID='"+document.all.role.value+"')";

	if (whereStr !=document.all["userTable"].whereStr){
		document.all["userTable"].whereStr=whereStr;
		document.all["userTable"].RecordCount="0";
		document.all["userTable"].AbsolutePage="1";


		Table_Refresh("userTable");
    }

}
