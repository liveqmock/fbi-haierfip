var m_objParam = "";
var m_deptname="";
var m_parentDept;

function userTable_TRDbclick(el){
	var sfeature = "dialogwidth:620px; Dialogheight:250px;center:yes;help:no;resizable:yes;scroll:no;status:no";
      var tab=getOwnerTable(event.srcElement);
	  var trobj =tab.rows[tab.activeIndex];
      var whArr = m_objParam.split("&");
      var deptnamearr = m_deptname.split("&");
 
     if ((trobj.whStr !=undefined)&&(trobj.whStr!=""))
     {
           var whereSArr =tab.rows[tab.activeIndex].whStr.split("*");


           var userArr = whereSArr[0].split("&");
            whArr = whereSArr[1].split("&");

          var spath = "Useredit.jsp";
          var arg = new Object();
          arg.type = "select";
          arg.deptID = whArr[2];
          arg.userID = userArr[2];
          arg.deptName = deptnamearr[2];
          arg.topparent = m_parentDept[2];
          


          var  goupstr = window.showModalDialog(spath,arg,sfeature);
           if (goupstr =="1"){
               Table_Refresh("userTable");
               body_load();
          }
     }
}

function body_load()
{
//    divfd_userTable.style.height = document.body.clientHeight - 450 +"px";
//    userTable.fdwidth="1200px";


//     alert("aaa="+divfd_userTable.style.height);
	if (parent.window.paramValue)
		m_objParam = parent.window.paramValue.value;
	else
		m_objParam = "";

   var deptarr =  m_objParam.split("*");
   m_deptname =deptarr[1];
   m_objParam =deptarr[0];
   m_parentDept =deptarr[2].split("&");
//alert(m_deptname);

   var tab = document.all("userTable");

   var whArr = m_objParam.split("&");
   
   //alert(whArr);
   
   var whereStr =  "and(trim("+whArr[0] + ")='" +whArr[2]+"') order by operid" ;

	tab.whereStr=whereStr;
     userTable.actionname     ="sm0043";
	userTable.posedelete =true;


	Table_Refresh("userTable",false,chgArea);



}



function reset_click()
{

    emptyTable(userTable);
    //iq0011_body_onload();
}
function chgArea()
{

   //div_gcoreusercard.style.width = 950;
   divfd_userTable.style.height = document.body.offsetHeight - 130;
   WidthEquals("Title_userTable","userTable");
   initDBGrid("userTable");
}



////////////////////////////
function userTable_appendRecord_click(tab){
     
     
    var whArr = m_objParam.split("&");
    var deptnamearr = m_deptname.split("&");
  
   
	var sfeature = "dialogwidth:620px; Dialogheight:250px;center:yes;help:no;resizable:yes;scroll:no;status:no";

     var spath = "Useredit.jsp";
     var arg = new Object();
     arg.type = "insert";
     arg.deptID = whArr[2];
     arg.deptName = deptnamearr[2];
     arg.topparent = m_parentDept[2];
     
     if (tab.RecordCount/1 >0)
     	arg.firstUser =tab.rows[0].cells[1].innerText+"";
     else
        arg.firstUser ="false"
     


     var  goupstr = window.showModalDialog(spath,arg,sfeature);
      if (goupstr =="1"){

          body_load();
     }

}

function userTable_editRecord_click(tab){
	
	var whArr = m_objParam.split("&");
    var deptnamearr = m_deptname.split("&");
      
	var sfeature = "dialogwidth:620px; Dialogheight:250px;center:yes;help:no;resizable:yes;scroll:no;status:no";
      var trobj =tab.rows[tab.activeIndex];

     if ((trobj.whStr !=undefined)&&(trobj.whStr!=""))
     {
          var whereSArr =tab.rows[tab.activeIndex].whStr.split("*");


          var userArr = whereSArr[0].split("&");
            whArr = whereSArr[1].split("&");

          var spath = "Useredit.jsp";
          var arg = new Object();
          arg.type = "update";


          arg.deptID = whArr[2];
          arg.userID = userArr[2];
          arg.deptName = deptnamearr[2];
		  arg.topparent = m_parentDept[2];
		  
          var  goupstr = window.showModalDialog(spath,arg,sfeature);
           if (goupstr =="1"){

               body_load();
          }
     }


}


function userTable_deleteRecord_click(tab,datapiolt){
	if (confirm("您确定要删除当前记录吗？")){
	
		var delString = "";
		for (var i=0; i< tab.rows.length; i++){
		
			if (tab.rows[i].cells[0].children[0].checked){
				if (delString ==""){
					delString = "topparent&"+m_parentDept[2]+"*userid&"+tab.rows[i].cells[1].innerText;
				}else{
				
					delString =delString + ";topparent&"+m_parentDept[2]+"*userid&"+tab.rows[i].cells[1].innerText;
				}			   
			}		
		}
		
		if (delString =="")
			return;	
	   
	     
		if (tab.checkbl.toLowerCase() =="true")
		{
			var xmlDoc = createDoc();

			var rootNode =createRootNode(xmlDoc);

			var actionNode = createActionNode(xmlDoc,tab.actionname);

			if (tab.delmethodname != undefined && tab.delmethodname !="")
				appendAttri(xmlDoc, actionNode, "methodname", tab.delmethodname);

			for (var i = 0; i < tab.rows.length; i++)
			{
				if (tab.rows[i].operate !="insert")
				{
					if (tab.rows[i].cells[0].children[0].checked){

						var wherrstr =tab.rows[i].whStr;

						if (m_objParam !="")
						{
							wherrstr =wherrstr+"*"+m_objParam;
						}
						nodel = false;

						var reorderNode = createDeleteRecord(wherrstr,xmlDoc);
						actionNode.appendChild(reorderNode);
					}

				}

			}

			if (nodel){
				alert("请在多选框选择删除项目！");
				return;
			}

			rootNode.appendChild(actionNode);	

			var retStr = ExecServerPrgm(g_jsContextPath+"/BI/util/SqlExcuteJsp.jsp","POST","sys_request_xml="+xmlDoc.xml);

			if (analyzeReturnXML(retStr)== "true")
			{				
										
				tab.AbsolutePage= "1";
				tab.RecordCount ="0";

				Table_Refresh_asy(tab.id);
			}	     
	    }
	   
	  }

}
