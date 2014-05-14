var m_objParam = "";
var m_deptname="";
var objArr;
var deptArr;

function body_load()
{

	if (parent.window.paramValue)
		m_objParam = parent.window.paramValue.value;
	else
		m_objParam = "";

        var deptarr =  m_objParam.split("*");

         m_objParam =deptarr[0];
         m_deptname =deptarr[1];


        var tab = document.all("userTable");

         objArr = m_objParam.split("&");
         deptArr = m_deptname.split("&");

        var whereStr =  "and (MOUDLEID='" +deptArr[2]+"')and (ERRORTYPE='" +objArr[2]+"') " ;

        tab.whereStr=whereStr;

		 userTable.actionname     ="sm0085";
          userTable.addmethodname  ="adderror";
          userTable.editmethodname ="editerror";
          userTable.delmethodname ="delerror";

          userTable.dbformname ="feerateedit";

          Table_Refresh("userTable",false,chgArea);

		initDBGrid("userTable");




}



function chgArea()
{


    disableElement();

   divfd_userTable.style.height = 220;
   WidthEquals("Title_userTable","userTable");
   initDBGrid("userTable");
}

function disableElement(){

     	if (userTable.rows[userTable.activeIndex] == undefined)
    			return;

         if (userTable.rows[userTable.activeIndex].operate =="update" )
         		 document.all.errorindex.disabled="true";

	     var el =document.all["ywbh"];
          for ( i= 0;i< el.options.length; i++)
          {
               if  (el.item(i).value ==deptArr[2])
                    el.selectedIndex =i;
          }

          document.all["ywbh"].disabled="true";
          document.all["errortype"].disabled="true";

          var el =document.all["errortype"];
          for ( i= 0;i< el.options.length; i++)
          {
               if  (el.item(i).value ==objArr[2])
                    el.selectedIndex =i;
          }
}

//////////////////
function userTable_updateRecord_click(tab){
   postGridRecord(tab,parent.window.paramValue.value);
}



//////////////////
function userTable_afteroperate_click(tab){
    disableElement();
}



