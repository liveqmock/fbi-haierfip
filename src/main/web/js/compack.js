/***
 * 通信组织数据包的类（HttpCom和DataPacket）
 * 窗口之间传递参数的类(Params 和 Param)
 *
 * 编程人员：王德良
 *
 * 时间：2004年3月10日
 *****/
var debug = false;


function HttpCom() {
    //类的属性
    HttpCom.prototype.url           = null;
    HttpCom.prototype.sendMethod    = "POST";
    HttpCom.prototype.request       = null;
    HttpCom.prototype.response      = null;
    HttpCom.prototype.success       = false;
    HttpCom.prototype.userid        = "";
    HttpCom.prototype.passwd        = null;

    HttpCom.prototype.STATIC_URL          = g_jsContextPath+"/BI/util/SqlExcuteJsp.jsp";
    HttpCom.prototype.STATIC_METHOD       = "POST";
    HttpCom.prototype.STATIC_CONTENT_NAME = "sys_request_xml";
    //类的方法
    HttpCom.prototype.setUrl        = httpCom_setUrl;
    HttpCom.prototype.getUrl        = httpCom_getUrl;
    HttpCom.prototype.send          = httpCom_send;
    HttpCom.prototype.setSendMethod = httpCom_setSendMethod;
    HttpCom.prototype.getSendMethod = httpCom_getSendMethod;

    HttpCom.prototype.setRequest    = httpCom_setRequest;
    HttpCom.prototype.getRequest    = httpCom_getRequest;

    HttpCom.prototype.setResponse   = httpCom_setResponse;
    HttpCom.prototype.getResponse   = httpCom_getResponse;

    HttpCom.prototype.isSuccess     = httpCom_isSuccess;

    HttpCom.prototype.setUserid     = httpCom_setUserId;
    HttpCom.prototype.getUserid     = httpCom_getUserId;

    HttpCom.prototype.setPasswd     = httpCom_setPasswd;
    HttpCom.prototype.getPasswd     = httpCom_getPasswd;
}

function httpCom_setUrl(sUrl) {
    this.url = sUrl;
}

function httpCom_getUrl() {
    return this.url;
}

function httpCom_setSendMethod(sSendMethod) {
    this.sendMethod = sSendMethod;
}

function httpCom_getSendMethod() {
    return this.sendMethod;
}

function httpCom_setRequest(sRequest) {
    this.request = sRequest;
}

function httpCom_getRequest() {
    return this.request;
}

function httpCom_setResponse(sResponse) {
    this.response = sResponse;
}

function httpCom_getResponse() {
    return this.response;
}

function httpCom_isSuccess() {
    return this.success;
}

function httpCom_setUserId(sUserId) {
    this.userid = sUserId;
}

function httpCom_getUserId() {
    return this.userid;
}

function httpCom_setPasswd(sPasswd) {
    this.passwd = sPasswd;
}

function httpCom_getPasswd() {
    return this.passwd;
}

function httpCom_send() {
    var xmlHttp = new ActiveXObject("Msxml2.XmlHttp");
    if ( this.sendMethod == undefined || this.sendMethod == null || this.sendMethod == "" ) {
        this.sendMethod = "POST";
    }

    if ( this.userid != null && this.userid != "" ) {
        xmlHttp.Open(this.sendMethod,this.url,false,this.userid,this.passwd);
    } else {
        xmlHttp.Open(this.sendMethod,this.url,false);
    }

    if ( this.sendMethod == "POST" ) {
        xmlHttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
        var tmpRequest = this.STATIC_CONTENT_NAME + "=" + this.request;

        xmlHttp.Send(tmpRequest);
    } else {
        xmlHttp.Send();
    }

    if ( xmlHttp.status == 200 ) {
        this.success  = true;
        this.response = unescape(xmlHttp.responseText);
    } else {
        this.success  = false;
        this.response = xmlHttp.status;
    }
}

function DataPacket() {
    try {
        this.xmlData     = new ActiveXObject("Msxml2.DOMDocument");
        this.responseXml = new ActiveXObject("Msxml2.DOMDocument");
    } catch ( e ) {
        this.xmlData     = new ActiveXObject("Msxml.DOMDocument");
        this.responseXml = new ActiveXObject("Msxml.DOMDocument");
    }

    this.xmlData.async                   = false;
    //类属性
    DataPacket.prototype.xmlData;
    DataPacket.prototype.curNode;//当前节点
    DataPacket.prototype.curActionNode;//当前Action节点
    DataPacket.prototype.success;
    DataPacket.prototype.message;
    DataPacket.prototype.messageTp; //0-空数据 1-消息数据 2-XML数据 3-HTML数据 4-自定义XML数据
    DataPacket.prototype.responseXml;
    DataPacket.prototype.STATIC_ACTION_NAME        = "actionname";
    DataPacket.prototype.STATIC_RECORDER_TYPE_NAME = "type";
    DataPacket.prototype.httpCom;

    //类方法　
    DataPacket.prototype.init            = DP_init;
    DataPacket.prototype.addAction       = DP_AddAction;
    DataPacket.prototype.addActionProp   = DP_AddActionProp;
    DataPacket.prototype.addRecorder     = DP_AddRecoder;
    DataPacket.prototype.addRecorderProp = DP_AddRecorderProp;
    DataPacket.prototype.addField        = DP_AddField;

    DataPacket.prototype.send            = DP_send;
    DataPacket.prototype.isSuccess       = DP_isSuccess;
    DataPacket.prototype.getMessage      = DP_getMessage;
    DataPacket.prototype.getMessageTp    = DP_getMessageTp;
    DataPacket.prototype.getResponseXml  = DP_getResponseXml;
    DataPacket.prototype.packForm        = DP_packetForm;
    DataPacket.prototype.mapForm         = DP_mapForm;
    DataPacket.prototype.packTagForm     = DP_packTagForm;
    DataPacket.prototype.packTagGrid     = DP_packTagGrid;
    DataPacket.prototype.DP_packTagGridSingle   = DP_packTagGridSingle;
    DataPacket.prototype.DP_packTagGridFrom     = DP_packTagGridFrom;
}

function DP_init(initXML,actionname,methodname) {
    if ( initXML == undefined ) {
        initXML = "<root/>";
    }

    if ( initXML != undefined && initXML != null ) {
        this.xmlData.loadXML(initXML);
    } else {
        this.xmlData.loadXML();
    }
    var root = this.xmlData.documentElement;

    this.curNode  = root;
    if ( actionname != undefined ) {
        this.init();
        this.addAction();
        this.addActionProp("actionname",actionname);
        if ( methodname != undefined && methodname != "" )
            this.addActionProp("methodname",methodname);
    }
}
function DP_packTagForm(tag,formId,recordType,m_objParam) {
    this.addRecorder();
    this.addRecorderProp("type",formId+"_"+recordType);

    var packForm = tag.document.all[formId];
    for (var i = 0 ; i < packForm.length ; i++ ) {
        var formElement = packForm.item(i);

        if( formElement.tagName.toUpperCase() == "INPUT"){
        		if (formElement.type=="text" || formElement.type=="hidden" || formElement.type=="password"){
        			if (formElement.fieldtype !="select")
        	   			this.addField(formElement.id,formElement.value,"text",formElement.oldvalue);
        	   		else
        	   			this.addField(formElement.id,formElement.attr,"text",formElement.oldvalue);
        		}
                if(formElement.type=="radio"){
                	if (formElement.checked)
                     	this.addField(formElement.id,"1","text",formElement.oldvalue);

                }
                 if(formElement.type=="checkbox"){
                	if (formElement.checked)
                     	this.addField(formElement.id,"1","text",formElement.oldvalue);
                    else
                    	this.addField(formElement.id,"0","text",formElement.oldvalue);
                }
        }
        if( formElement.tagName.toUpperCase() == "TEXTAREA"){
        		this.addField(formElement.id,formElement.value,"text",formElement.oldvalue);
       }
       if( formElement.tagName.toUpperCase() == "SELECT"){
        		this.addField(formElement.id,formElement.value,"text",formElement.oldvalue);
       }
    }
}
function DP_packTagGrid(tag,gridId,recordType,m_objParam) {
    var grid = tag.document.all[gridId];
    if (grid.dbformname == undefined)
        return this.DP_packTagGridSingle(tag,gridId,recordType,m_objParam);
    else
        return this.DP_packTagGridFrom(tag,gridId,recordType,m_objParam);
}

function DP_packTagGridSingle(tag,gridId,recordType,m_objParam) {
    var grid = tag.document.all[gridId];

	for (var i = 0; i < grid.rows.length; i++) {
	    if ( grid.rows[i].operate =="update" || grid.rows[i].operate =="insert" || grid.rows[i].operate =="delete") {
            this.addRecorder();
	        this.addRecorderProp("type",gridId+"_"+recordType+"_"+grid.rows[i].operate);



             for(var j=0;j<grid.rows[i].cells.length;j++ ) {
                    if ((grid.rows[i].cells[j].fieldname !=undefined )&&(grid.rows[i].cells[j].fieldtype !=undefined)){
                         //if(checkTDValue(grid.rows[i].cells[j])=="false") {
                         //	return false;
                         //}
                         if (grid.rows[i].cells[j].fieldtype.toLowerCase() =="dropdown"){
                              if(grid.rows[i].cells[j].attr !=undefined){
                                  this.addField(grid.rows[i].cells[j].fieldname,grid.rows[i].cells[j].attr,grid.rows[i].cells[j].fieldtype,grid.rows[i].cells[j].oldvalue);
                              }
                         }else if (grid.rows[i].cells[j].fieldtype.toLowerCase() =="other"){
                              if(grid.rows[i].cells[j].attr !=undefined){
                                  this.addField(grid.rows[i].cells[j].fieldname,grid.rows[i].cells[j].attr,grid.rows[i].cells[j].fieldtype,grid.rows[i].cells[j].oldvalue);
                              }
                         }else if (grid.rows[i].cells[j].innerText !="" ){
                             this.addField(grid.rows[i].cells[j].fieldname,grid.rows[i].cells[j].innerText,grid.rows[i].cells[j].fieldtype,grid.rows[i].cells[j].oldvalue);
                         }
                    }
               }

			if ((grid.rows[i].whStr !=undefined)&&(grid.rows[i].whStr!=""))	{
				var whereSArr =grid.rows[i].whStr.split("*");
				for (var j=0; j< whereSArr.length; j++)	{
					if (whereSArr[j]!=undefined){
						var whArr = whereSArr[j].split("&");
						this.addField(whArr[0],whArr[2],whArr[1]);
					}
				}
			}
			if(m_objParam !="" ){
				var paramSArr =m_objParam.split("*");
				for (var j=0; j< paramSArr.length; j++)	{
					if ((paramSArr[j]!=undefined)&&(paramSArr[j] !="")){
					    var whArr = paramSArr[j].split("&");
						this.addField(whArr[0],whArr[2],whArr[1]);
					}
				}
			}
		}
	}
	return true;
}
function DP_packTagGridFrom(tag,gridId,recordType,m_objParam) {
     var grid = tag.document.all[gridId];

     var elementobj= undefined;

     var filednamearr  = grid.fieldname.split(",");
     var fieldCheckArr = grid.fieldCheck.split(";");
     var filedcn       = grid.fieldCN.split(",");

	 for (var i = 0; i < grid.rows.length; i++) {
	    if ( grid.rows[i].operate =="update" || grid.rows[i].operate =="insert" || grid.rows[i].operate =="delete") {
            if (grid.rows[i].ValueStr ==undefined)
               	continue;
          	var filedvaluearr = grid.rows[i].ValueStr.split(";");
               if (grid.rows[i].OldValueStr != undefined){
               	var filedoldvaluearr = grid.rows[i].OldValueStr.split(";");
               }else{
               	var filedoldvaluearr = new Array(filednamearr.length);

               }

            grid.activeIndex= i;

            gridfillform(grid, tag.document.all(grid.dbformname));

            this.addRecorder();
	        this.addRecorderProp("type",gridId+"_"+recordType+"_"+grid.rows[i].operate);


            for (var j=0; j < filednamearr.length; j++){
                elementobj= undefined;
                for (var k=0; k< tag.document.all(grid.dbformname).length;k++) {
                     if (filednamearr[j].toUpperCase()== tag.document.all(grid.dbformname).item(k).id.toUpperCase()){
                         var elementobj= tag.document.all(grid.dbformname).item(k);
                         break;
                     }
                 }
                 if (elementobj != undefined){
                     elementobj.alertTitle =filedcn[j];
                     var fieldOneCheckArr = fieldCheckArr[j].split(",");
                     for(var k=0 ;k< fieldOneCheckArr.length; k++){
                          if(fieldOneCheckArr[k].length > 0){
                               var fieldValueCheckArr = fieldOneCheckArr[k].split("=");

                               if(fieldValueCheckArr[0]=="isNull")
                                    elementobj.isNull =  fieldValueCheckArr[1];
                               else if(fieldValueCheckArr[0]=="textLength")
                                    elementobj.textLength =  fieldValueCheckArr[1];
                               else if(fieldValueCheckArr[0]=="intLength")
                                    elementobj.intLength =  fieldValueCheckArr[1];
                               else if(fieldValueCheckArr[0]=="floatLength")
                                    elementobj.floatLength =  fieldValueCheckArr[1];
                               else if(fieldValueCheckArr[0]=="eMail")
                                    elementobj.eMail =  fieldValueCheckArr[1];
                          }

                     }
//                               if ( checkInputValue(elementobj) =="false")
//                                      return false;
                 }

                 if (filedoldvaluearr[j] == undefined || filedoldvaluearr[j] == null)
                    filedoldvaluearr[j] ="";

                 if ( filedvaluearr[j] != null && filedvaluearr[j] != " " ) {
                    this.addField(filednamearr[j],filedvaluearr[j],"text",filedoldvaluearr[j]);
                 } else
                    this.addField(filednamearr[j],"","text",filedoldvaluearr[j]);
            }

             if ((grid.rows[i].whStr !=undefined)&&(grid.rows[i].whStr!=""))	{
				var whereSArr =grid.rows[i].whStr.split("*");
				for (var j=0; j< whereSArr.length; j++)	{
					if (whereSArr[j]!=undefined){
						var whArr = whereSArr[j].split("&");
						this.addField(whArr[0],whArr[2],whArr[1]);
					}
				}
			}
			if(m_objParam !="" ){
				var paramSArr =m_objParam.split("*");
				for (var j=0; j< paramSArr.length; j++)	{
					if ((paramSArr[j]!=undefined)&&(paramSArr[j] !="")){
					    var whArr = paramSArr[j].split("&");
						this.addField(whArr[0],whArr[2],whArr[1]);
					}
				}
			}
		}
	}
	return true;
}

function DP_AddAction() {
    this.curNode  = DP_Private_AddNode(this.xmlData.documentElement,this.xmlData,"action","");
    this.curActionNode = this.curNode;
}

function DP_AddActionProp(name,value) {
    DP_Private_AddProp(this.xmlData,this.curNode,name,value);
}

function DP_AddRecoder() {
    this.curNode  = DP_Private_AddNode(this.curActionNode,this.xmlData,"recorder","");
}

function DP_AddRecorderProp(name,value) {
    DP_Private_AddProp(this.xmlData,this.curNode,name,value);
}

function DP_AddField(name,value,type,oldvalue) {
    var fieldNode = DP_Private_AddNode(this.curNode,this.xmlData,"field","");
    DP_Private_AddProp(this.xmlData,fieldNode,"name",name);
    DP_Private_AddProp(this.xmlData,fieldNode,"type",type);
    DP_Private_AddProp(this.xmlData,fieldNode,"value",encode(value));
	if (arguments.length == 4)
		 DP_Private_AddProp(this.xmlData,fieldNode,"oldValue",encode(oldvalue));
}

function DP_Private_AddNode(curNode,xmlData,name,value) {
    if ( name == undefined || name == null )
        return;
    var node = xmlData.createNode(1,name,"");
    if ( value != undefined && value != null )
        node.text = value;
    curNode.appendChild(node);

    return node;
}

function DP_Private_AddProp(xmlData,node,name,value) {
    if ( name == undefined || name == null )
        return;
    var attr = xmlData.createAttribute(name);
    attr.value = value;

    node.attributes.setNamedItem(attr);
}

function DP_send() {
    this.success   = false;
    this.message   = "";
    this.messageTp = 0;

    var httpCom = new HttpCom();
    httpCom.setUrl(httpCom.STATIC_URL);
    httpCom.setSendMethod(httpCom.STATIC_METHOD);
    httpCom.setRequest(this.xmlData.xml);

    httpCom.send();

    if ( httpCom.isSuccess() ) {


         var retvalue =   analyzeReturnXML(httpCom.getResponse());
         if (retvalue =="false")
         		this.success = false;
          else
          	this.success = true;

        //return;
        this.responseXml.loadXML(httpCom.getResponse());

        var rootNode = this.responseXml.documentElement.firstChild;

        if ( rootNode.getAttribute("type") == "1" ) { //返回XML数据
            this.messageTp = 2;
            if ( rootNode.getAttribute("result") == "true" ) {
                this.success = true;
            } else {
                this.success = false;
            }
            this.messageTp = 2;
            this.responseXml.loadXML(rootNode.xml);
        } else if ( rootNode.getAttribute("type") == "2" ) {//返回HTML数据

            if (  rootNode.getAttribute("result") == "true" ) {
                this.success = true;
            } else {
                this.success = false;
            }
            if ( rootNode.text == "" ) {
                this.messageTp = 0;
            } else {
                this.messageTp = 3;
                this.message   = decode(rootNode.xml);
            }

        } else if ( rootNode.getAttribute("type") == "3" ) {//返回自定义数据

            if (  rootNode.getAttribute("result") == "true" ) {
                this.success = true;
            } else {
                this.success = false;
            }
            if ( rootNode.text == "" ) {
                this.messageTp = 0;
            } else {
                this.messageTp = 4;
                this.message   = decode(rootNode.xml);
            }

        } else {
            if (  rootNode.getAttribute("result") == "true" ) {
                this.success = true;
            } else {
                this.success = false;
            }
            if ( rootNode.text == "" ) {
                this.messageTp = 0;
            } else {
                this.messageTp = 1;
                this.message   = decode(rootNode.text);
            }
        }
    } else {
        this.success   = false;
        this.message   = httpCom.getResponse();
        this.messageTp = 1;
    }
}

function DP_isSuccess() {
    return this.success;
}

function DP_getMessage() {
    return this.message;
}

function DP_getMessageTp() {
    return this.messageTp;
}

function DP_getResponseXml() {
    return this.responseXml;
}

function DP_packetForm(packForm,actionName,actionPropName,actionPropValue,recordTypeValue) {
    this.addAction();

    this.addActionProp(this.STATIC_ACTION_NAME,actionName);
    if ( (typeof(actionPropName) != "undefined" && actionPropName != "undefined" && actionPropName != null && actionPropName != "" ) &&
         (typeof(actionPropValue) != "undefined" && actionPropValue != "undefined" && actionPropValue != null && actionPropValue != "" ) ) {
        this.addActionProp(actionPropName,actionPropValue);
    }
    this.addRecorder();

    if ( typeof(recordTypeValue) != "undefined" && recordTypeValue != "undefined" && recordTypeValue != null && recordTypeValue != "" ) {
        this.addRecorderProp(this.STATIC_RECORDER_TYPE_NAME,recordTypeValue);
    }

    for (var i = 0 ; i < packForm.length ; i++ ) {
        var formElement = packForm.item(i);

        if( formElement.tagName.toUpperCase() == "INPUT"){
        		if (formElement.type=="text" || formElement.type=="hidden" || formElement.type=="password"){
        	   		this.addField(formElement.id,formElement.value,"text");
        		}
                if(formElement.type=="checkbox"||formElement.type=="radio"){
                	if (formElement.checked)
                     	this.addField(formElement.id,"1","text");
                    else
                    	this.addField(formElement.id,"0","text");
                }
        }
        if( formElement.tagName.toUpperCase() == "TEXTAREA"){
        		this.addField(formElement.id,formElement.value,"text");

       }
       if( formElement.tagName.toUpperCase() == "SELECT"){
        		this.addField(formElement.id,formElement.value,"text");

       }

    }
}

function DP_mapForm() {
    if ( this.success ) {
        if ( this.messageTp == 2 ) {
            var actionNode   = this.responseXml;
            var recorderNode = actionNode.firstChild.firstChild;
            for ( var i = 0 ; i < recorderNode.childNodes.length ; i++ ) {
                var fieldNode  = recorderNode.childNodes[i];
                var fieldname  = fieldNode.getAttribute("name");
                var fieldtype  = fieldNode.getAttribute("type");
                var fieldvalue = decode(fieldNode.getAttribute("value"));
                var curElement = document.all(fieldname);
                if ( fieldtype == "text" || fieldtype == "int" || fieldtype == "date" || fieldtype == "datetime" ) {
                    if ( curElement.length == undefined ) {
                        curElement.value   = fieldvalue;
                    } else {
                        curElement.item(0) = fieldvalue;
                    }
                } else if ( fieldtype == "checkbox" ) {
                    if ( curElement.length == undefined ) {
                        curElement.value   = fieldvalue;
                    } else {
                        for ( var j = 0 ; j < curElement.length ; j++ ) {
                            if ( curElement.item(j).value == fieldvalue ) {
                                curElement.item(j).checked = true;
                            } else {
                                curElement.item(j).checked = false;
                            }
                        }
                    }
                } else if ( fieldtype == "dropdown" ) {
                    curElement.oldValue  = fieldvalue;
                    curElement.dropvalue = fieldvalue;
                } else if ( fieldtype == "select" ) {
                    if ( curElement.length == undefined ) {
                        curElement.value   = fieldvalue;
                    } else {
                        for ( var j = 0 ; j < curElement.length ; j++ ) {
                            if ( curElement.item(j).value == fieldvalue ) {
                                curElement.item(j).selected = true;
                            } else {
                                curElement.item(j).selected = false;
                            }
                        }
                    }
                } else if ( fieldtype == "attr" ) {
                     curElement.attr = fieldvalue;
                }
            }
        } else {
            alert(this.message);
        }
    }
}

Params.prototype.count;
Params.prototype.param;
Params.prototype.paramx;
Params.prototype.add                    = Params_add;
Params.prototype.addNew                 = Params_addNew;
Params.prototype.clear                  = Params_clear;
Params.prototype.getNamedParamValue     = Param_getNamedParamValue;
Params.prototype.getNamedParam          = Param_getNamedParam;
Params.prototype.getCount               = Param_getCount;
Params.prototype.getIndexedParam        = Param_getIndexedParam;
Params.prototype.getIndexedParamValue   = Param_getIndexedParamValue;

function Params() {
    this.count  = 0;
    this.param  = new Array();
    this.paramx = new Array();
}

function Params_add(param) {
    this.param[param.getName()]     = param;
    this.paramx[this.paramx.length] = param;
    this.count++;
}

function Params_addNew(name,value,type) {
    var param = new Param(name,value,type);
    this.param[param.getName()]     = param;
    this.paramx[this.paramx.length] = param;
    this.count++;
}

function Params_clear() {
    this.param  = new Array();
    this.paramx = new Array();
    this.count  = 0;
}
function Param_getNamedParamValue(sName) {
    var tmp = this.param[sName];
    if ( tmp == undefined )
        return null;
    return tmp;
}
function Param_getNamedParam(sName) {
    return this.param[sName];
}
function Param_getCount() {
    return this.count;
}
function Param_getIndexedParam(iIndex) {
    return this.paramx[iIndex];
}
function Param_getIndexedParamValue(iIndex) {
    return this.paramx[iIndex].value;
}

Param.prototype.name;
Param.prototype.value;
Param.prototype.type;
Param.prototype.getName  = Param_getName;
Param.prototype.getValue = Param_getValue;
Param.prototype.getType  = Param_getType;

function Param(name,value,type) {
    this.name  = name;
    this.value = value;
    this.type  = type;
}
function Param_getName() {
    return this.name;
}
function Param_getValue() {
    return this.value;
}
function Param_getType() {
    return this.type;
}

function BusinessControllor() {

    BusinessControllor.prototype.run      = BC_run;//主体函数，运行入口

    BusinessControllor.prototype.init     = BC_init;//初始化

    BusinessControllor.prototype.preInput = BC_preInput;//预输入

    BusinessControllor.prototype.check    = BC_check;//打包前检查

    BusinessControllor.prototype.ask      = BC_ask;//询问是否复合业务，是否打印用户证

    BusinessControllor.prototype.pack     = BC_pack;//打数据包

    BusinessControllor.prototype.send     = BC_send;//发送数据包

    BusinessControllor.prototype.output   = BC_output;//输出结果

    BusinessControllor.prototype.finalize = BC_finalize;//清理环境

    BusinessControllor.prototype.dp      = null;//数据包
    BusinessControllor.prototype.params  = null;//与ModalDialog的交互环境
    BusinessControllor.prototype.sc      = null;//会话环境

    BusinessControllor.prototype.actprop      = "type";
    BusinessControllor.prototype.actpropvalue = "ccc";
    BusinessControllor.prototype.recordtype   = "query";


    BusinessControllor.prototype.printVoucher = false; //打印业务凭单
    BusinessControllor.prototype.printInvoice = false; //打印发票
    BusinessControllor.prototype.printCard    = false; //打印用户证
    BusinessControllor.prototype.askCombine   = false; //是否提示进行复合业务

}

function BC_run() {
    //try {
        if ( this.init() ) {
            if ( this.preInput() ) {
                if ( this.check() ) {
                    this.ask();
                    if ( this.pack() ) {
                        if ( this.send() ) {
                            this.output();
                        }
                    }
                }
                this.finalize();
            }
        }
    //} catch ( Exception ) {
      //  alert("程序发生例外！");
    //}
}

function BC_init() {

    this.dp     = new DataPacket();
    this.params = new Params();
    this.sc     = new Params();

    return true;
}

function BC_preInput() {
    return true;
}

function BC_check() {
    return true;
}

function BC_ask() {
    return true;
}

function BC_pack() {
    return true;
}
function BC_send() {
    try {
        this.dp.send();
    } catch ( Exception ) {
        alert("程序发生例外，发送数据失败！");
    }
    return true;
}
function BC_output() {
    return true;
}
function BC_finalize() {
    return true;
}


function prototype_ask() {
    this.isMulti     = "0";
    this.isPrintCard = "0";
    if ( this.askCombine ) {
        if ( confirm("是否复合业务？") ) {
            this.isMulti = "1";
        } else {
            //if ( confirm("是否打印用户证？") ) {
                this.isPrintCard = "1";
            //}
        }
    }
}

function prototype_pack() {
    this.dp.init("<root/>");
    this.dp.packForm(document.all.winform,this.actionname,this.actprop,this.actpropvalue,this.recordtype);
    for ( var i = 0 ; i < this.params.getCount() ; i++ ) {
        var param = this.params.getIndexedParam(i);


        this.dp.addField(param.getName(),param.getValue(),param.getType());
    }
    this.dp.addField("isMulti",this.isMulti,"text");
    this.dp.addField("isPrintCard",this.isPrintCard,"text");

    if ( debug )
        alert(this.dp.xmlData.xml);
    return true;
}

function prototype_output() {
    if ( this.dp.isSuccess() ) {
        window.returnValue = true;


        if ( this.dp.getMessageTp() == 1 ) {
            alert(this.dp.getMessage());
            window.close();
            return;
        } else if ( this.dp.getMessageTp() == 2 ) {
            if ( this.isMulti == "1" ) {
                alert(this.dp.getMessage());
                window.close();
                return;
            }
        } else if ( this.dp.getMessageTp() == 3 ) {
            if ( this.isMulti == "1" ) {
                alert(this.dp.getMessage());
                window.close();
                return;
            }
            //6.打印业务受理回执单
            var message  = this.dp.getMessage();
            alert(message);
            var xmlDoc   = new ActiveXObject("Msxml2.DOMDocument");
            var objNodeList;
            xmlDoc.async = false;
            xmlDoc.resolveExternals = false;
            xmlDoc.loadXML(message);

            objNodeList       = xmlDoc.getElementsByTagName("bussNo");

            var businessLogNo = objNodeList.item(0).text;

            objNodeList       = xmlDoc.getElementsByTagName("userNo");
            var userNo        = objNodeList.item(0).text;
            objNodeList       = xmlDoc.getElementsByTagName("isPrtCard");
            var prtCard       = objNodeList.item(0).text;
            objNodeList       = xmlDoc.getElementsByTagName("isMulti");
            var isMulti       = objNodeList.item(0).text;

            if ( (this.printVoucher == true) && confirm("业务处理成功，是否打印业务受理回执单？") ) {
                do {
                    alert("请将业务受理回执单插入打印机，然后点击确定！");
                    argument = "tw003.jsp?busniessLogNo="+businessLogNo;
                    if ( debug )
                        alert(argument);
                    this.params.clear();

                    var  dd = window.showModalDialog(argument,this.params,"Dialogheight:400px;Dialogwidth:800px;menubar:yes;status:no;toolbar:yes;scrollbars:no;resizable:no;");
                } while ( !confirm("回执单打印是否成功？") );
            }
            //7.打印用户证
            if ( this.isPrintCard ) {
                if ( (this.printCard == true) && confirm("是否打印用户证？") ) {
                    do {
                        alert("请将用户证插入打印机，然后点击确定！");
                        argument = "/jsp/report/ucPrint.jsp?USERNO="+userNo;
                        this.params.clear();
                        var  dd = window.showModalDialog(argument,this.params,"Dialogheight:400px;Dialogwidth:800px;menubar:yes;status:no;toolbar:yes;scrollbars:no;resizable:no;");
                    } while ( !confirm("用户证打印是否成功？") );
                    var dp = new DataPacket();
                    dp.init("<root/>");
                    dp.addAction();
                    dp.addActionProp("actionname","cp001");
                    dp.addActionProp("type","update");
                    dp.addRecorder();
                    dp.addRecorderProp("type","update");
                    dp.addField("userNo",userNo,"text");
                    dp.addField("result","1","text");
                    dp.send();
                    dp.isSuccess();
                }
            }
            //8、打印发票
            if ( (this.printInvoice == true) && confirm("是否打印发票?") ) {
                do {
				var modalFeature = "Dialogheight:600px;Dialogwidth:800px;menubar:yes;status:no;toolbar:no;help:no;scrollbars:no;resizable:no;";
          		this.params.clear();
          		//添加需要传递的参数
          		this.params.add(new Param("USERNO",userNo,"1"));
               	this.params.add(new Param("ISCOMPLEX",isMulti,"1"));
                    this.params.add(new Param("BUSSSEQ",businessLogNo,"1"));
                    //配置需要转向到的JSP页面。注意：这个Param对象的名称必须为"action"
          		this.params.add(new Param("action","/jsp/invoice/invoiceInput.jsp","1"));
               	this.params.add(new Param("title","发票录入","1"));
          		var dd = window.showModalDialog("/html/parentFrame.html",this.params,modalFeature);
                    if ( dd == true ) {

                    	var modalFeature = "Dialogheight:500px;Dialogwidth:800px;menubar:yes;status:no;toolbar:no;help:no;scrollbars:no;resizable:no;";
                         window.showModalDialog("/html/parentFrame.html",this.params,modalFeature);
                    } else {
                        break;
                    }

                } while (  !confirm("发票打印是否成功？") );
            }
            alert("业务办理完毕！");
            window.close();
        } else if ( this.dp.getMessageTp() == 4 ) {
            if ( this.isMulti == "1" ) {
                alert(this.dp.getMessage());
                window.close();
                return;
            }
            //6.打印业务受理回执单
            var message  = this.dp.getMessage();

            var xmlDoc   = new ActiveXObject("Msxml2.DOMDocument");
            var objNodeList;
            xmlDoc.async = false;
            xmlDoc.resolveExternals = false;
            xmlDoc.loadXML(message);

            objNodeList       = xmlDoc.getElementsByTagName("bussNo");

            var businessLogNo = objNodeList.item(0).text;

            objNodeList       = xmlDoc.getElementsByTagName("userNo");
            var userNo        = objNodeList.item(0).text;
            objNodeList       = xmlDoc.getElementsByTagName("isPrtCard");
            var prtCard       = objNodeList.item(0).text;
            objNodeList       = xmlDoc.getElementsByTagName("isMulti");
            var isMulti       = objNodeList.item(0).text;
		//alert(userNo+"="+businessLogNo+"="+prtCard+"="+isMulti);
            if ( (this.printVoucher == true) && confirm("业务处理成功，是否打印业务受理回执单？") ) {
                do {
                    alert("请将业务受理回执单插入打印机，然后点击确定！");
                    var userNos = userNo.split(",");
                    argument = "/jsp/report/backHoldPrint.jsp?USERNO="+userNos[0]+"&ISCOMPLEX="+isMulti+"&BUSSSEQ="+businessLogNo;
                    if ( debug )
                        alert(argument);
                    this.params.clear();

                    var  dd = window.showModalDialog(argument,this.params,"Dialogheight:600px;Dialogwidth:800px;menubar:yes;status:no;toolbar:yes;scrollbars:no;resizable:no;");
                } while ( !confirm("回执单打印是否成功？") );
            }
            //7.打印用户证
            if ( this.isPrintCard ) {
                if ( !isprintcard(userNo) ) {
                      if ( (this.printCard == true) && confirm("是否打印用户证？") ) {
                          do {
                              alert("请将用户证插入打印机，然后点击确定！");
                              argument = "/jsp/report/ucPrint.jsp?USERNO="+userNo;
                              this.params.clear();
                              var  dd = window.showModalDialog(argument,this.params,"Dialogheight:400px;Dialogwidth:800px;menubar:yes;status:no;toolbar:yes;scrollbars:no;resizable:no;");
                          } while ( !confirm("用户证打印是否成功？") );
                          var dp = new DataPacket();
                          dp.init("<root/>");
                          dp.addAction();
                          dp.addActionProp("actionname","cp001");
                          dp.addActionProp("type","update");
                          dp.addRecorder();
                          dp.addRecorderProp("type","update");
                          dp.addField("userNo",userNo,"text");
                          dp.addField("result","1","text");
                          dp.send();
                          dp.isSuccess();
                      }
                }
            }
            //8、打印发票
//            var _operno = "";
//            var _bussseq = "";
//            var _iscomplex = "";
			var isFirstPrint = true;
			try {
			    var userNos = userNo.split(",");
			    userNo = userNos[0];
		    } catch ( Exception ) {}
            if ( (this.printInvoice == true) && (hasFeeAvailable(userNo,businessLogNo,isMulti) == true)) {
                do {
				if(!isFirstPrint){
                    	if(!rejectAndUpdateInvDetail(userNos[0],businessLogNo,isMulti)){
                         	alert('作废发票失败(发票打印失败后,作废已达发票!');
                         	return ;
                    	}
				}

				var modalFeature = "Dialogheight:600px;Dialogwidth:800px;menubar:yes;status:no;toolbar:no;help:no;scrollbars:no;resizable:no;";
          		this.params.clear();
          		//添加需要传递的参数
                    var userNos = userNo.split(",");
          		this.params.add(new Param("USERNO",userNos[0],"1"));
               	this.params.add(new Param("ISCOMPLEX",isMulti,"1"));
                    this.params.add(new Param("BUSSSEQ",businessLogNo,"1"));
                    //配置需要转向到的JSP页面。注意：这个Param对象的名称必须为"action"
          		this.params.add(new Param("action","/jsp/invoice/invoiceInput.jsp","1"));
               	this.params.add(new Param("title","发票录入","1"));
          		var dd = window.showModalDialog("/html/parentFrame.html",this.params,modalFeature);
                    if ( dd == true ) {

                         if(this.params.getNamedParamValue("ISAUTO") != null){
                              //alert(this.params.getNamedParamValue("ISAUTO").getValue());
                         	if(this.params.getNamedParamValue("ISAUTO").getValue() == '0'){
                          		break;
                         	}
                         }
                    	var modalFeature = "Dialogheight:500px;Dialogwidth:800px;menubar:yes;status:no;toolbar:no;help:no;scrollbars:no;resizable:no;";
                         window.showModalDialog("/html/parentFrame.html",this.params,modalFeature);
                    	isFirstPrint = false;
                    	if(confirm("发票打印是否成功？")){
                           break;
                    	}else{
                              continue;
                    	}
                    } else {
                         if(this.params.getNamedParamValue("error") != null){
                         	if(this.params.getNamedParamValue("error").getValue() == '-1000'){
                         		alert('由于您登录超时，请到发票补打模块补打本次业务的发票，谢谢！');
                          		break;
                         	}else if(this.params.getNamedParamValue("error").getValue() == '-2000'){
                              	break;
                         	}
                         }
                    	isFirstPrint = true;
               		continue;
                    }

                } while ( true );
            }
            alert("业务办理完毕！");

            window.close();
        } else {
            window.close();
        }

    } else {
        alert(this.dp.getMessage());
        return;
    }
}

//num1>num2 返回1 num1=num2 返回0 num1<num2 -1
function compare(num1,num2) {
    try {
        if ( num1 == "undefined" ) {
            if ( num2 == "undefined" ) {
                return 0;
            } else if ( num2 != "0" ) {
                return -1;
            } else
                return 0;
        }

        if ( num2 == "undefined" ) {
            return 1;
        }

        if ( num1 == null ) {
            if ( num2 == null ) {
                return 0;
            } else if ( num2 != "0" ) {
                return -1;
            } else
                return 0;
        }

        if ( num2 == null ) {
            return 1;
        }

        if ( num1.length == 0 ) {
            if ( num2.length == 0 ) {
                return 0;
            }
            return -1;
        }
        if ( num2.length == 0 )
            return 1;

        if ( parseInt(num1) > parseInt(num2) ) {
            return 1;
        } else if ( parseInt(num1) == parseInt(num2) ) {
            return 0;
        } else {
            return -1;
        }
    } catch ( Exception ) {
        alert("输入中存在非法数字，请检查！");
    }
}
/*********************************************************************************************
 *
 *   测试
 *
 *
 *************************************/

function testParams() {
    var params = new Params();
    params.add(new Param("name","wdl","1"));
    params.add(new Param("sex","男","1"));
    params.add(new Param("age","26","1"));
    params.add(new Param("salary","1000","1"));
    alert(params.getCount());
    var  dd = window.showModalDialog("test3.htm",params,"menubar=yes,status=yes,toolbar=yes,scrollbars=yes,resizable=yes");
    alert(dd);
    alert(params.getCount());

    for ( var i = 0 ; i < params.getCount();i++ ) {
        var p = params.getIndexedParam(i);
        alert(p);
        document.all(p.getName()).value = p.getValue();
    }
}

function testModal() {
    var params = window.dialogArguments;

    for ( var i = 0 ; i < params.getCount() ; i++ ) {
        var p = params.getIndexedParam(i);

        document.all(p.getName()).value = p.getValue();
    }
}
function testReturnValue() {
    var params = window.dialogArguments;
    params.clear();
    params.addNew("name","cyn","1");
    window.returnValue = true;
    window.close();
}

function test_packform() {
    var dp = new DataPacket();

    dp.init("<root/>");
    dp.packForm(document.all("testform"),"e001","type","ccc","add");

    dp.addAction();
    dp.addActionProp("actionname","wdl");
    dp.addRecorder();
    dp.addRecorderProp("type","del");

    dp.addField("aa","111","2222");
    dp.addField("a2","111","2222");
    dp.addField("a3","111","2222");

    dp.packForm(document.all("testform"),"e001","type","ccc","add");

    alert(dp.xmlData.xml);
    dp.send();
    alert(dp.isSuccess());
    alert(dp.getMessageTp());
    alert(dp.getMessage());
    if ( dp.isSuccess() ) {
        if ( dp.getMessageTp() == 2 ) {
            var xmlDoc = dp.getResponseXml();
        } else {
            alert(dp.getMessage());
        }
    }
}

function test_main() {
    var httpCom = new HttpCom();
    httpCom.setUrl("tw001.jsp");
    httpCom.setSendMethod("POST");

    httpCom.setRequest('<root><action actionName="1111">cccc</action></root>');

    httpCom.send();

    alert(httpCom.isSuccess());
    alert(httpCom.getResponse());

    var dp = new DataPacket();
    dp.init("<root/>");
    dp.addAction();
    dp.addActionProp("actionname","ac001");
    dp.addActionProp("type","insert");
    dp.addRecorder();
    dp.addRecorderProp("type","delete");
    dp.addField("username","0010","text");
    dp.addField("passwd","111111","text");
    dp.addAction();
    dp.addActionProp("actionname","ad001");
    dp.addActionProp("type","insert");
    dp.addRecorder();
    dp.addRecorderProp("type","add");
    dp.addField("ccccc","0010","text");
    dp.addField("ddddd","111111","text");

    alert(dp.xmlData.xml);
    alert(send(dp));
}


/*
   发送数据
   DataPacket: request
*/
function send(request) {
    var httpCom = new HttpCom();
    httpCom.setUrl(httpCom.STATIC_URL);
    httpCom.setSendMethod(httpCom.STATIC_METHOD);
    httpCom.setRequest(request.xmlData.xml);

    httpCom.send();
    var response = new DataPacket();
    response.init(httpCom.getResponse());
    return response.xmlData;
}

function rejectAndUpdateInvDetail(operno,bussseq,iscomplex){
    	var dp = new DataPacket();
    	dp.init("<root/>");
    	dp.addAction();
    	dp.addActionProp("actionname","rauid");
    	dp.addActionProp("type","insert");
    	dp.addRecorder();
    	dp.addRecorderProp("type","delete");
    	dp.addField("USERNO",operno,"text");
    	dp.addField("BUSSSEQ",bussseq,"text");
	dp.addField("ISCOMPLEX",iscomplex,"text");
	//alert(operno+':'+bussseq+':'+iscomplex);
     dp.send();
     if ( dp.isSuccess()) {
          return true;
     }else{
          alert(dp.getMessage());
          return false;
     }
	return true;
}

function hasFeeAvailable(usernos,bussseqs,ismulti){
	var dp = new DataPacket();
    	dp.init("<root/>");
    	dp.addAction();
    	dp.addActionProp("actionname","hasfee");
    	dp.addActionProp("type","insert");
    	dp.addRecorder();
    	dp.addRecorderProp("type","delete");
    	dp.addField("USERNO",usernos,"text");
    	dp.addField("BUSSSEQ",bussseqs,"text");
    	dp.addField("ISMULTI",ismulti,"text");

     dp.send();
     if ( trimStr(dp.getMessage())=='1') {
          return true;
     }else{
          if(dp.getMessage().indexOf('超时')> -1){
           	alert('由于您登录超时，请到发票补打模块补打本次业务的发票，谢谢！');
          }
          return false;
     }
}
function isprintcard(userno){

	var XMLStr = createselectArrNOCheck("userno","text",userno,"sc0604");

	var xmlDoc = createDomDocument();
	xmlDoc.loadXML(XMLStr);


	var rootNode = xmlDoc.documentElement.firstChild;

	if (rootNode.text =="1")
		    return false;
    else
            return true;

}
function checkSteal(userNo) {
    var dp = new DataPacket();
    dp.init("<root/>");
    dp.addAction();
    dp.addActionProp("actionname","chkstl");
    dp.addActionProp("type","query");
    dp.addRecorder();
    dp.addRecorderProp("type","query");
    dp.addField("userNo",userNo,"text");


    dp.send();
    if ( !dp.isSuccess() ) {
        if ( !confirm(dp.getMessage()+"，是否继续？") ) {
           window.close();
        }
    }
}
