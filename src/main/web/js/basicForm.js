function initFormElement(elename,elevalue) {var formElement = document.getElementById(elename);if (formElement!=null) {        eleval = decode(elevalue);formElement.oldvalue = eleval;if ( formElement.type == "text") {initInput(elename,eleval);        } else if ( formElement.type == "hidden") {        initInput(elename,eleval);        } else if ( formElement.type == "password") {        initInput(elename,eleval);        }        
        else if ( formElement.type == "textarea") {        initInput(elename,eleval);        } else if ( formElement.type == "checkbox" ) {           initCheckBox(elename,eleval);        } else if ( formElement.type == "radio" ) {            initRadio(elename,eleval);        } else if ( formElement.type == "select-one" ) {initSelect_One(elename,eleval);}        
        else if ( formElement.type == "select-multiple" ) {        }    }}function initCheckBox(cbname,val) {     tts = val.split(',');ft = document.all(cbname);if ( tts[0]==1) {              ft.checked=true;                  }}function initRadio(rdname,val) {    ft = document.all(rdname);for (var i = 0 ;i < ft.length ;i++ ) {               if (ft[i].value== val) {              ft[i].checked=true;break;}}}function initInput(ipname,val) {    ft = document.all(ipname);if (ft!=null) {                ft.value = val;ft.oldvalue=val;}}function initSelect_One(soname,val) {    ft = document.all(soname);if (ft!=null) {                ft.value = val;ft.oldvalue=val;}}function selectContent(src,des)
{  for( var oindex =src.length-1 ;oindex >= 0 ;oindex--)
  {  var olength=des.length;if(src.options[oindex].selected)
  {    var otext=src.options[oindex].text;var ovalue=src.options[oindex].value;des.options[olength]=new Option(otext,ovalue);      src.remove(oindex);}  }  for( var oindex = 0 ;oindex < src.length ;oindex++)
  {  var otext=src.options[oindex].text;var ovalue=src.options[oindex].value;src.options[oindex]=new Option(otext,ovalue);}}function changeContent(src,des)
{     var oindex=src.selectedIndex;if (oindex == -1) {     return false;}     var olength=des.length;var otext=src.options[oindex].text;var ovalue=src.options[oindex].value;des.options[olength]=new Option(otext,ovalue);src.remove(oindex);for( var oindex = 0 ;oindex < src.length ;oindex++)
       {            var otext=src.options[oindex].text;var ovalue=src.options[oindex].value;src.options[oindex]=new Option(otext,ovalue);}}function getDateTime(vday,vhour,vmi) {     var dtstr="";if (vday!="")
     if (vhour!="")
          if (vmi!="")
               dtstr = vday+" "+vhour+":"+vmi;else
               dtstr = vday+" "+vhour;else
          dtstr = vday;return dtstr;}function getDateTime(s) {var today       = new Date();todayDay   = today.getDate();todayMonth = today.getMonth();todayYear  = today.getFullYear();todayHour  = today.getHours();todayMinute= today.getMinutes();todaySecond= today.getSeconds();if (s == '1')
     return todayYear+"-"+todayMonth+"-"+todayDay+" "+todayHour+":"+todayMinute+":"+todaySecond;else
     return todayYear+"-"+todayMonth+"-"+todayDay+" 00:00:00";}function getSelectContent(sel) {   var rtn = "";for( var oindex = 0 ;oindex < sel.length ;oindex++)
   {        var ovalue=sel.options[oindex].value;rtn+=ovalue+"|";}   
   return rtn;}function invokeFunction(function_name,param){     var paramstr="";if (arguments.length == 2)
          for(i=0;i<param.length;i++){               if (i==0)
                    paramstr="param["+i+"]";else
                    paramstr=paramstr+",param["+i+"]";}   
    eval("result="+function_name+"("+paramstr+");");}function isChecked(obj) {var ischecked=false;for (var i=0 ;i<obj.length;i++) {if (obj[i].checked) {ischecked = true;break;}}return ischecked;}function load_form() {}function fieldToRead(obj) {if (typeof(obj) != 'undefined') {obj.readOnly=true;if (typeof(obj.onfocus) == "function")
obj.onfocus = function(){};}}function fieldToWrite(obj) {if (typeof(obj) != 'undefined') {obj.readOnly=false;if (typeof(obj.onfocus) == "function")
obj.onfocus = function(){setday(this);};}}