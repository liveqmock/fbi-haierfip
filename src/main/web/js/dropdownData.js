//**************************************************//
//该文件是本系统生成下拉数据文件，任何人不得擅自更改	    //
//若有更改与本人联系：公共包					        //
//**************************************************//


/********************************************************************************
 *
 *      文件名： dropdownData.js
 *
 *      作  用： 下拉框控件。
 *
 *      作  者： 公共包
 *
 *      时  间：  yyyy-mm-dd
 *
 *      版  权：  吴业元
 *
 ********************************************************************************/



var bool = true;
var dropDownValue ="";
var _editor ;


function dropdown(el){
	if (typeof(dropdownText_button)!="undefined"){
       	dropTextHideen();
		dropdownText_button.removeNode(true);
	}

	var dropwidth ="140";
	var dropheight="140";

	
	if (typeof(el.dropwidth)!="undefined" && el.dropwidth !="" )
	{
		dropwidth = el.dropwidth;
	}

	if (typeof(el.dropheight)!="undefined" && el.dropheight !="" )
	{
		dropheight = el.dropheight;
	}
	

		var drop_frm = document.createElement("<iframe  id=\"_dropdownText_frame\" style=\"position: absolute; width:"+dropwidth+"; height:"+dropheight+";top:0 ;left:0;visibility :hidden ; filter:blendTrans(duration=0.3);z-index : 100000\" frameBorder=\"0\"> </iframe>");
	
        var drop_obj = document.createElement("<DIV id=\"_dropdownText_box\" style=\"position: absolute;OVERFLOW: auto;  width:"+dropwidth+"; height:"+dropheight+";top:0 left:0; visibility :hidden ;Z-INDEX:100001\"></DIV>");

        parentHTML = "<INPUT type=button id=\"dropdownText_button\" class=\"buttonGrooveDisable\" value=6  style=\"position: absolute; HEIGHT:20;left:" +(el.offsetLeft + el.offsetWidth-16) + ";top:" + (el.offsetTop +1) + ";width:15;FONT-SIZE:8;FONT-FAMILY: webdings;Z-INDEX:10001;\" onclick=\"DropDownClick(this)\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\">";
        obj=document.createElement(parentHTML);

        document.body.appendChild(obj);

        dropdownText_button.setAttribute("edior",el);

        if (typeof(_dropdownText_box)=="undefined")
        {
			document.body.appendChild(drop_frm);
            document.body.appendChild(drop_obj);
        }

		document.all("_dropdownText_frame").style.width = dropwidth;
		document.all("_dropdownText_frame").style.height = dropheight;
		document.all("_dropdownText_frame").height = dropheight;	
		
		document.all("_dropdownText_box").style.width = dropwidth;
		document.all("_dropdownText_box").style.height = dropheight;	


        var pos =getAbsPosition(el);
        dropdownText_button.style.top = pos[1]-1;
        dropdownText_button.style.left =pos[0] + el.offsetWidth-16;
        dropdownText_button.style.height = el.offsetHeight;
		//_dropdownText_box.style.width =el.offsetWidth;


}


function dropTextHideen(){
	if (typeof(_dropdownText_box)!="undefined"){
	
		
		for(var i=0; i<_dropdownText_box.children.length; i++){
			_dropdownText_box.children[i].removeNode(true);
		}

		_dropdownText_box.style.visibility = "hidden";
		document.all["_dropdownText_frame"].style.visibility = "hidden";
		bool = true;
	}

}


function dropInit(){

	if (event == null || event.srcElement == null)
	{
		if (typeof(dropdownText_button)!="undefined")
		{
			document.all["dropdownText_button"].style.visibility = "hidden";
			dropTextHideen();
		}
		return;
	}

	if (typeof(dropdownText_button)!="undefined"){
		if ((event.srcElement !=dropdownText_button.getAttribute("edior"))&&(event.srcElement.id != "dropdownText_button" ))
					
		{
			document.all["dropdownText_button"].style.visibility = "hidden";
			dropTextHideen();
		}
	}
	
}


function DropDownClick(el){
    	
	_editor = el.getAttribute("edior");
	
	var sqlStr = _editor.sqlStr;
	var fieldTitle = _editor.fieldTitle;
	
	if (_editor.tagName.toUpperCase() == "TD"){
		var tab= document.all["Title_"+getOwnerTable(_editor).id];
		
		if (tab.rows[0].cells[_editor.cellIndex].dropdownHTML !=undefined)
		{
			
			document.all["_dropdownText_box"].innerHTML=tab.rows[0].cells[_editor.cellIndex].dropdownHTML;
			visibleHidden(el);
			return;
		}
	
	}
	

	if (_editor.tagName.toUpperCase() == "INPUT"){
	
		if (_editor.dropdownHTML !=undefined)
		{
			document.all["_dropdownText_box"].innerHTML=_editor.dropdownHTML;
			visibleHidden(el);
			return;
		}
	
	
	}


	var xmlDoc = createDomDocument("<reqStr/>");

	var rootNode = xmlDoc.documentElement;
   

	if (_editor.enumType != undefined){
		appendAttri(xmlDoc, rootNode, "type", "enum");

		var childNode = appendNode(xmlDoc, xmlDoc.documentElement, "childNode");
		appendAttri(xmlDoc, childNode, "enumType", _editor.enumType);
		appendAttri(xmlDoc, childNode, "fieldTitle", fieldTitle);
		
		rootNode.appendChild(childNode);


	}else{
		appendAttri(xmlDoc, rootNode, "type", "sql");

		var childNode = appendNode(xmlDoc, xmlDoc.documentElement, "childNode");

		appendAttri(xmlDoc, childNode, "SqlStr", sqlStr);
		appendAttri(xmlDoc, childNode, "fieldTitle", fieldTitle);
	
		rootNode.appendChild(childNode);
	}
	
	

    var  tmpHTML = ExecServerPrgm(g_jsContextPath+"/BI/util/DropDownJsp.jsp","POST","xx="+encode(xmlDoc.xml));
	
	document.all["_dropdownText_box"].innerHTML=tmpHTML;
	

	if (_editor.tagName.toUpperCase() == "TD"){
		var tab= document.all["Title_"+getOwnerTable(_editor).id];
	
		tab.rows[0].cells[_editor.cellIndex].dropdownHTML =tmpHTML;
		
	
	}
	
	if (_editor.tagName.toUpperCase() == "INPUT"){
	
		_editor.dropdownHTML =tmpHTML;
	
	}

	visibleHidden(el);

	xmlDoc.free;
}

function visibleHidden(el){
	
	var minheigth;

	if (bool){
		_editor = el.getAttribute("edior");

		 var pos =getAbsPosition(_editor);
		
		minheigth =document.all["_dropdownText_box"].offsetHeight;
		
		
		document.all["_dropdownText_frame"].style.height =minheigth;
		document.all["_dropdownText_box"].style.height =minheigth;
		
	

		var abttop =pos[1] +el.offsetHeight;
		
		var abtheight = pos[1] +el.offsetHeight+document.all["_dropdownText_frame"].offsetHeight;
        
		if (abtheight >document.body.clientHeight + document.body.scrollTop )
        {
			abttop =  pos[1] - document.all["_dropdownText_frame"].offsetHeight;
        }

		_dropdownText_box.style.top = abttop;
		_dropdownText_box.style.left =pos[0] + el.offsetWidth-16;

		document.all["_dropdownText_frame"].style.top = abttop;
		document.all["_dropdownText_frame"].style.left =pos[0] + el.offsetWidth-16;
		
		//document.all["_dropdownText_frame"].style.height=document.all["Data_dropDown"].offsetHeight;
	
	
		document.all["_dropdownText_frame"].style.visibility = "";
		_dropdownText_box.style.visibility = "";
		
		bool = false;
		return;
	}else{
		dropTextHideen();
			
		bool = true;

	}

}

function TRClick(el){
  

	if (_editor.tagName.toLowerCase()=="input")
	{
		_editor.value =el.cells[1].innerText;
		_editor.attr = el.cells[0].innerText;
	}


	 if (_editor.tagName.toLowerCase()=="td")
	 {
		 
		 var tab = getOwnerTable(_editor);
		 
		 var oldvalue= _editor.innerText;
		
		
		 _editor.innerText =el.cells[1].innerText;
		 _editor.attr =el.cells[0].innerText;

		 if (oldvalue !=_editor.innerText)

		 {
						  
			  if (tab.func != undefined)
			 {
				tab.func();
			 }
		 }
	 }
	 if (isfireuserEvent("dropDown_Change"))
			fireUserEvent("dropDown_Change",[_editor]);
	
    dropTextHideen();


}

