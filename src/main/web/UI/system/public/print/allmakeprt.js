var sqlStr ="";
var filedwidth="";
 var selectStr="";
var fromStr="";
var wherestr="";


////构造通用查询的xml
function init(){



	var visible="";
	var fieldCN="";

	var align="";
	var ischeck="";

	var fieldStr="";
	var printtitle="";

	var bgindex=0;
	var edindex=0;


	 var arg = window.dialogArguments;

	if (arg)
	{
			sqlStr = decode(arg.sqlstr);

			wherestr=decode(arg.wherestr);
			fieldCN=decode(arg.fieldcn);
			visible=decode(arg.visible);
			filedwidth=decode(arg.filedwidth);
			align=decode(arg.align);
			ischeck =arg.ischeck;
			printtitle　= arg.printtitle;
	}

	if (sqlStr=="" || sqlStr =="undefined"|| sqlStr ==undefined)
		return;

	if (printtitle=="" || printtitle =="undefined"|| printtitle ==undefined)
		;
	else
		document.all("prttitle").value=printtitle;


	 bgindex= sqlStr.indexOf("SELECT");
	selectStr = sqlStr.substring(0,bgindex+6);



	edindex   = sqlStr.indexOf("FROM");
	fromStr = sqlStr.substring(edindex) + " "+ wherestr ;

	var fill1Str ="rownum";


	var orderindex = fromStr.lastIndexOf("order");

	if (orderindex !=-1){
		fill1Str = " row_number() over ( " +fromStr.substring(orderindex)+ " ) ";

	}else
		fromStr = fromStr+" order by  rownum";




	fieldStr =sqlStr.substring(bgindex+6,edindex);

	var visibleArr      =  visible.split(",");

	if (fieldStr.indexOf(";")>0)
		var fieldArr        =  fieldStr.split(";");
	else
		var fieldArr        =  fieldStr.split(",");

	var fieldCNArr      =  fieldCN.split(",");
	var filedwidthArr   =  filedwidth.split(",");
	var alignArr        =  align.split(",");
	var opt        = new Array();


	opt[0]      = document.createElement("option");

	opt[0].text	   = "序号";
	opt[0].value	   = fill1Str;
	opt[0].attrib	   = 5;
	opt[0].primary    = 2;

	document.all("addedFilters").add(opt[0]);


	for (var i=0 ; i < visibleArr.length; i++)
	{
		       opt[i]      = document.createElement("option");
	
				opt[i].text	   = fieldCNArr[i];
				opt[i].value	   = fieldArr[i];

				if (ischeck=="true")
					opt[i].attrib	   = filedwidthArr[i+1];
				else
					opt[i].attrib	   = filedwidthArr[i];


				if (alignArr[i]=="center")
					opt[i].primary    = 2;
				else if (alignArr[i]=="right")
					opt[i].primary    = 1;
				else
					opt[i].primary    = 0;

				document.all("addedFilters").add(opt[i]);

		
	}



}

///添加一列查询信息
function addWebFilter(el) {
	if (el.selectedIndex == -1) return;

	var opt = document.createElement("OPTION");
	var index = el.selectedIndex;
	opt.text = el.item(el.selectedIndex).text;
	opt.value = el.item(el.selectedIndex).value;
	opt.attrib = el.item(el.selectedIndex).attrib;
	opt.primary  = el.item(el.selectedIndex).primary;

	if (document.all("addedFilters").selectedIndex != -1) {
		document.all("addedFilters").add(opt, document.all("addedFilters").selectedIndex + 1);
		document.all("addedFilters").selectedIndex += 1;
	}
	else {
		document.all("addedFilters").add(opt);
		document.all("addedFilters").selectedIndex = document.all("addedFilters").length -1;
	}

	if (el.selectedIndex == -1) return;
		el.remove(el.selectedIndex);

	el.selectedIndex = index;

	changefield();

}

///添加所有查询信息
function addAllWebFilter(el) {
	for ( i= 0;i< el.options.length; i++)
	{
		var opt = document.createElement("OPTION");

		opt.text = el.item(i).text;
		opt.value = el.item(i).value;
		opt.attrib = el.item(i).attrib;
		opt.primary  = el.item(i).primary;

		if (document.all("addedFilters").selectedIndex != -1) {
			document.all("addedFilters").add(opt, document.all("addedFilters").selectedIndex + 1);
			document.all("addedFilters").selectedIndex += 1;
		}
		else {
			document.all("addedFilters").add(opt);
			document.all("addedFilters").selectedIndex = document.all("addedFilters").length -1;
		}

	}
	for ( i= el.options.length -1;i >-1; i--)
	{
		el.options.remove(i);
	}

	changefield();

}
//删除一列查询信息
function removeWebFilter(el) {
	if (el.selectedIndex == -1) return;

	var opt = document.createElement("OPTION");
	var index = el.selectedIndex;

	opt.text = el.item(el.selectedIndex).text;
	opt.value = el.item(el.selectedIndex).value;
	opt.attrib = el.item(el.selectedIndex).attrib;
	opt.primary  = el.item(el.selectedIndex).primary;

	if (document.all("filterSelect").selectedIndex != -1) {
		document.all("filterSelect").add(opt, document.all("filterSelect").selectedIndex + 1);
		document.all("filterSelect").selectedIndex += 1;
	}
	else {
		document.all("filterSelect").add(opt);
		document.all("filterSelect").selectedIndex = document.all("filterSelect").length -1;
	}

	if (el.selectedIndex == -1) return;
		el.remove(el.selectedIndex);

	document.all("addedFilters").selectedIndex = index;


	changefield();
}



///删除所有查询信息
function removeAllWebFilter(el) {
	for ( i= 0;i< el.options.length; i++)
	{

		var opt = document.createElement("OPTION");

		opt.text = el.item(i).text;
		opt.value = el.item(i).value;
		opt.attrib = el.item(i).attrib;
		opt.primary  = el.item(i).primary;

		if (document.all("filterSelect").selectedIndex != -1) {
			document.all("filterSelect").add(opt, document.all("filterSelect").selectedIndex + 1);
			document.all("filterSelect").selectedIndex += 1;
		}
		else {
			document.all("filterSelect").add(opt);
			document.all("filterSelect").selectedIndex = document.all("filterSelect").length -1;
		}
	}

	for ( i= el.options.length -1;i >-1; i--)
	{
		el.options.remove(i);
	}

	changefield();

}
///向上移动一条信息
function moveUp(el) {
	var index = el.selectedIndex;

	if ((index < 1) || (index == -1)) return;

	var tmp = el.item(index - 1);

	var opt = document.createElement("OPTION");
	opt.text = tmp.text;
	opt.value = tmp.value;
	opt.attrib = tmp.attrib;
	opt.primary  = tmp.primary;

	document.all("addedFilters").remove(index-1);
	document.all("addedFilters").add(opt, index);
	document.all("addedFilters").selectedIndex = index - 1;

	changefield();
}
///向下移动已条信息
function moveDown(el) {
	var index = el.selectedIndex;

	if ((index > el.length - 2) || (index == -1)) return;

	var tmp = el.item(index + 1);

	var opt = document.createElement("OPTION");
	opt.text = tmp.text;
	opt.value = tmp.value;
	opt.attrib = tmp.attrib;
	opt.primary  = tmp.primary;

	document.all("addedFilters").remove(index + 1);
	document.all("addedFilters").add(opt, index);
	document.all("addedFilters").selectedIndex = index + 1;

	changefield();
}

function pint_click(){


	var formStr ="<form name=\"hiddenPrint_form\" method=\"post\" target=\"_blank\"> <input type=\"hidden\"  id=\"sqlstr\" name=\"sqlstr\">";
		formStr +="<input type=\"hidden\" name=\"titles\"> <input type=\"hidden\" name=\"widths\"> <input type=\"hidden\" name=\"alignments\">";
		formStr +="<input type=\"hidden\" name=\"initdisps\"> <input type=\"hidden\" name=\"rpttitle\"> ";


	if (typeof(hiddenPrint_form)=="undefined"){

		document.body.insertAdjacentHTML("beforeEnd",formStr);

	}

	 if (trimStr(document.all("prttitle").value)==""){
		 alert("打印主题不能为空！");
		return;

	}


	var titles="";
	var widths="";
	var aligns="";
	var visibles="";
	var fieldStr="";
	var fieldcount=17;


	var seltPrt =document.all("addedFilters");

	if (seltPrt.options.length ==0)
		return;

	if (fieldcount >  seltPrt.options.length){
		for ( i= 0;i< seltPrt.options.length; i++)
		{
			var opt = document.createElement("OPTION");

			if (titles=="")
				titles = seltPrt.item(i).text;
			else
				titles = titles+","+seltPrt.item(i).text;


			if (widths=="")
				widths = seltPrt.item(i).attrib;
			else
				widths = widths+","+seltPrt.item(i).attrib;


			if (fieldStr=="")
				fieldStr = seltPrt.item(i).value +"  fill"+(i+1);
			else
				fieldStr = fieldStr+","+seltPrt.item(i).value +"  fill"+(i+1);


			if (visibles=="")
				visibles = 1;
				else
				visibles = visibles+","+1;


			 if (aligns=="")
				aligns = seltPrt.item(i).primary+"";
			else
				aligns = aligns+","+seltPrt.item(i).primary;
		}

		for ( i= seltPrt.options.length ;i< fieldcount; i++)
		{


			if (titles=="")
				titles = "AA"+(i+1);
			else
				titles = titles+","+"AA"+(i+1);


			if (widths=="")
				widths = 0;
			else
				widths = widths+","+0;

			 if (fieldStr=="")
				fieldStr = "' '  fill"+(i+1);
			else
				fieldStr = fieldStr+","+" ' '  fill"+(i+1);


			if (visibles=="")
				visibles = 0;
				else
				visibles = visibles+","+0;


			 if (aligns=="")
				aligns = 2+"";
			else
				aligns = aligns+","+2;
		}




	}else{

		for ( i= 0;i< fieldcount; i++)
		{
			var opt = document.createElement("OPTION");

			if (titles=="")
				titles = seltPrt.item(i).text;
			else
				titles = titles+","+seltPrt.item(i).text;


			if (widths=="")
				widths = seltPrt.item(i).attrib;
			else
				widths = widths+","+seltPrt.item(i).attrib;


			if (fieldStr=="")
				fieldStr = seltPrt.item(i).value +"  fill"+(i+1);
			else
				fieldStr = fieldStr+","+seltPrt.item(i).value +"  fill"+(i+1);


			if (visibles=="")
				visibles = 1;
				else
				visibles = visibles+","+1;


			 if (aligns=="")
				aligns = seltPrt.item(i).primary+"";
			else
				aligns = aligns+","+seltPrt.item(i).primary;
		}



	}


	sqlStr = selectStr + "  "　+ fieldStr + "  " + fromStr

	document.hiddenPrint_form.action = "gridcrpt.jsp";
	document.hiddenPrint_form.sqlstr.value = encode(encode(sqlStr));


	 document.hiddenPrint_form.titles.value = encode(encode(titles));
	 document.hiddenPrint_form.widths.value = widths;

	 document.hiddenPrint_form.alignments.value =aligns;
	 document.hiddenPrint_form.initdisps.value = visibles;
	 document.hiddenPrint_form.rpttitle.value =encode(encode(document.all("prttitle").value));

	 document.hiddenPrint_form.submit();


}


function changefield(){
	 var seltPrt =document.all("addedFilters");

	document.all("childtitle").value = "";
	document.all("childwidth").value  = "";
	document.all("childalign").selectedIndex  = -1;


	 if (seltPrt.selectedIndex == -1) return;


	 document.all("childtitle").value = seltPrt.item(seltPrt.selectedIndex).text;
	 document.all("childwidth").value  = seltPrt.item(seltPrt.selectedIndex).attrib;
	 document.all("childalign").selectedIndex  = seltPrt.item(seltPrt.selectedIndex).primary/1;

}

function addfield(){


	 var seltPrt =document.all("addedFilters");


	  document.all("childtitle").value = "";
	  document.all("childwidth").value  = "5";
	  document.all("childalign").selectedIndex  = 0;

	  var opt = document.createElement("OPTION");

	 opt.text = "";
	 opt.value = "new";
	 opt.attrib = "";

	 document.all("addedFilters").add(opt, seltPrt.options.length);

	 seltPrt.selectedIndex =seltPrt.options.length-1;


	 editfield();
}

function editfield(){
	document.all("childtitle").disabled=false;
	document.all("childwidth").disabled=false;
	document.all("childalign").disabled=false;

	document.all("savebut").disabled=false;
	document.all("calbut").disabled=false;

}

function savefield(){


	 var seltPrt =document.all("addedFilters");


	 if (seltPrt.selectedIndex == -1) return;

	 if (trimStr(document.all("childtitle").value)==""){
		 alert("标题名称不能为空！");
		return;

	}

	 if (isNaN(parseInt(trimStr(document.all("childwidth").value)))){
		 alert("标题宽度必须为整数型！");
		return;

	}



	  seltPrt.item(seltPrt.selectedIndex).text = document.all("childtitle").value;
	 seltPrt.item(seltPrt.selectedIndex).attrib= document.all("childwidth").value;
	 seltPrt.item(seltPrt.selectedIndex).primary=document.all("childalign").selectedIndex;
	 if  (seltPrt.item(seltPrt.selectedIndex).value=="new")
		 seltPrt.item(seltPrt.selectedIndex).value=" ' ' "


	document.all("childtitle").disabled=true;
	document.all("childwidth").disabled=true;
	document.all("childalign").disabled=true;

	document.all("savebut").disabled=true;
	document.all("calbut").disabled=true;

}


function calfield(){


	 var seltPrt =document.all("addedFilters");


	 if (seltPrt.selectedIndex == -1) return;

	 if  (seltPrt.item(seltPrt.selectedIndex).value=="new"){
		seltPrt.remove(seltPrt.selectedIndex);

	 }


	seltPrt.selectedIndex =seltPrt.options.length-1;

	changefield();

	document.all("childtitle").disabled=true;
	document.all("childwidth").disabled=true;
	document.all("childalign").disabled=true;

	document.all("savebut").disabled=true;
	document.all("calbut").disabled=true;

}











