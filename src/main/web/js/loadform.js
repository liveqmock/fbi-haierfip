function load_form() {

}

//工具函数部分
function initFormElement(elename,elevalue) {
	var formElement = document.getElementById(elename);
    	if (formElement!=null) {
        eleval = decode(elevalue);
        formElement.oldvalue = eleval;
        
      //  alert(formElement.type);
        
        if ( formElement.type == "text") {
          initInput(elename,eleval);
         //隐藏字段
        } else if ( formElement.type == "hidden") {
        	initInput(elename,eleval);
         //密码字段
        } else if ( formElement.type == "password") {
        	initInput(elename,eleval);
        //多选框数据
        }        //多行文本框数据
        else if ( formElement.type == "textarea") {
	        initInput(elename,eleval);
        //多选框数据
        } else if ( formElement.type == "checkbox" ) {
           	initCheckBox(elename,eleval);
        //单选框数据
        } else if ( formElement.type == "radio" ) {
            	initRadio(elename,eleval);
        //下拉菜单数据
        } else if ( formElement.type == "select-one" ) {
		initSelect_One(elename,eleval);
        }
        //列表菜单数据
        else if ( formElement.type == "select-multiple" ) {

        }else if(formElement.type=="int"){
        	initInput(elename,eleval);
        }else if(formElement.type=="money"){
	        initInput(elename,eleval);
        }

    	}
}

//根据多选框的名称,初始值(以,分隔)设定多选框的值
function initCheckBox(cbname,val) {
     	tts = val.split(',');
        ft = document.all(cbname);
    	for (var i = 0 ; i < ft.length ; i++ ) {
              for (var j = 0; j < tts.length ; j++ ) {
               	  if (ft[i].value== tts[j]) {
               		ft[i].checked=true;
                        break;
                  }
            	}
        }
}

//根据单选框的名称和值初始化值
function initRadio(rdname,val) {
    	ft = document.all(rdname);
	for (var i = 0 ; i < ft.length ; i++ ) {
               	if (ft[i].value== val) {
              		ft[i].checked=true;
                       	break;
               	}
	}
}

//初始化文本框
function initInput(ipname,val) {
        
    	ft = document.all(ipname);
    	if (ft!=null) {
                ft.value = val;
    	}
}

//初始化下拉框
function initSelect_One(soname,val) {
    	ft = document.all(soname);
    	if (ft!=null) {
                ft.value = val;
    	}
}