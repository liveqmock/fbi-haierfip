/**
 * 数据窗口控件
 * @param dwObj
 * @param dwType 数据窗口对象类型
 *   form : Form型
 *   grid : Grid型
 */
function DataWindow( dwObj, dwType )
{
	this.dw = dwObj;
	this.type = dwType;
	this.ownerDoc = this.dw.ownerDocument;
	this.ownerWin = this.ownerDoc.parentWindow;
	//if (this.type=="grid")
	//{
	//	this.fieldArr=dwObj.fieldname.split(",");
	//	this.columnCount=this.fieldArr.length;
	//}
	//Debug Mode
}

DataWindow.isDebug = true;
DataWindow.alertMsg= "";

DataWindow.prototype.getColumnObject = DataWindow_getColumnObject;
DataWindow.prototype.RowCount = DataWindow_RowCount;
DataWindow.prototype.getColumnIndex = DataWindow_getColumnIndex;
DataWindow.prototype.getSelectRow = DataWindow_getSelectRow;
DataWindow.prototype.getSelectRows = DataWindow_getSelectRows;
DataWindow.prototype.fireEvent = DataWindow_fireEvent;
DataWindow.prototype.selectRow = DataWindow_selectRow;
DataWindow.prototype.insertRow = DataWindow_insertRow;
DataWindow.prototype.editRow = DataWindow_editRow;
DataWindow.prototype.deleteRow = DataWindow_deleteRow;
DataWindow.prototype.cancelRow = DataWindow_cancelRow;
DataWindow.prototype.getItemDisplay = DataWindow_getItemDisplay;
DataWindow.prototype.getItemString = DataWindow_getItemString;
DataWindow.prototype.getItemExpand = DataWindow_getItemExpand;
DataWindow.prototype.setItemString = DataWindow_setItemString;
DataWindow.prototype.getItemInt = DataWindow_getItemInt;
DataWindow.prototype.setItemInt = DataWindow_setItemInt;
DataWindow.prototype.getItemFloat = DataWindow_getItemFloat;
DataWindow.prototype.setItemFloat = DataWindow_setItemFloat;
DataWindow.prototype.validate = DataWindow_validate;
DataWindow.prototype.validatePrimaryKey = DataWindow_validatePrimaryKey;
DataWindow.prototype.disableColumn = DataWindow_disableColumn;
DataWindow.prototype.enableColumn = DataWindow_enableColumn;
DataWindow.prototype.disableColumn2 = DataWindow_disableColumn2;
DataWindow.prototype.enableColumn2 = DataWindow_enableColumn2;
DataWindow.prototype.disableColumnAll = DataWindow_disableColumnAll;
DataWindow.prototype.enableColumnAll = DataWindow_enableColumnAll;
DataWindow.prototype.enableColumnNull = DataWindow_enableColumnNull;
DataWindow.prototype.disableColumnNull = DataWindow_disableColumnNull;
DataWindow.prototype.enableInputOption = DataWindow_enableInputOption;
DataWindow.prototype.disableInputOption = DataWindow_disableInputOption;
DataWindow.prototype.getColumnLabel = DataWindow_getColumnLabel;
DataWindow.prototype.getColumnCName = DataWindow_getColumnCName;
DataWindow.prototype.setOnChangeHandler = DataWindow_setOnChangeHandler;
DataWindow.prototype.setEventHandler = DataWindow_setEventHandler;
DataWindow.prototype.removeOnChangeHandler = DataWindow_removeOnChangeHandler;
DataWindow.prototype.removeEventHandler = DataWindow_removeEventHandler;
DataWindow.prototype.setColumn = DataWindow_setColumn;
DataWindow.prototype.reInitOldValue = DataWindow_reInitOldValue;
DataWindow.prototype.isModified = DataWindow_isModified;
DataWindow.prototype.getOperate = DataWindow_getOperate;
DataWindow.prototype.isValidRow = DataWindow_isValidRow;
DataWindow.prototype.disposeComboBox = DataWindow_disposeComboBox;

DataWindow.prototype.dispose = DataWindow_dispose;

DataWindow.getColumnValue = DataWindow_getColumnValue;
DataWindow.setColumnValue = DataWindow_setColumnValue;
DataWindow.isDate = DataWindow_isDate;
DataWindow.validateColumn = DataWindow_validateColumn;
DataWindow.validateAlert = DataWindow_validateAlert;
DataWindow.RTrim = DataWindow_RTrim;
DataWindow.getRealLength = DataWindow_getRealLength;
DataWindow.DateAdd = DataWindow_DateAdd;
DataWindow.setFocus = DataWindow_setFocus;


/**
 * 设置数据窗口中某个字段对象的onchange事件处理函数。只对form类型有效
 *
 * @param colName 字段名
 * @param handlerName 事件处理函数名称
 */
function DataWindow_setOnChangeHandler( colName, handlerName )
{
	this.setEventHandler(colName, "onchange", handlerName);
}


/**
 * 设置数据窗口中某个字段对象的eventName事件处理函数。只对form类型有效
 *
 * @param colName 字段名
 * @param eventName 事件名
 * @param handlerName 事件处理函数名称
 */
function DataWindow_setEventHandler( colName, eventName, handlerName )
{
	if (this.type=="grid") return;
	var colObj=this.getColumnObject( 1, colName );
	if (colObj==null)
	{
		if (DataWindow.isDebug) DataWindow_log("不能为不存在的对象设置"+eventName+"处理函数！\n字段名为："+colName);
		return;
	}
	var bSuccess=false;
	if (typeof(handlerName)=="function")
		bSuccess=colObj.attachEvent(eventName, handlerName);
	else if (typeof(handlerName)=="string")
		bSuccess=colObj.attachEvent(eventName, eval(handlerName));
	else
	{
		if (DataWindow.isDebug) DataWindow_log( "无效的参数handlerName，请检查参数！\n字段名字："+colName+"事件名："+eventName+" 处理函数："+handlerName );
		return false;
	}
	if (!bSuccess)
	{
		if (DataWindow.isDebug) DataWindow_log( "绑定事件处理函数不成功，请检查参数！\n字段名字："+colName+"事件名："+eventName+" 处理函数："+handlerName );
		return false;
	}
}


/**
 * 取消数据窗口中某个字段对象的onchange事件处理函数。只对form类型有效
 *
 * @param colName 字段名
 * @param handlerName 事件处理函数名称
 */
function DataWindow_removeOnChangeHandler( colName, handlerName )
{
	this.removeEventHandler(colName, "onchange", handlerName);
}

/**
 * 取消数据窗口中某个字段对象的eventName事件处理函数。只对form类型有效
 *
 * @param colName 字段名
 * @param eventName 事件名
 * @param handlerName 事件处理函数名称
 */
function DataWindow_removeEventHandler( colName, eventName, handlerName )
{
	if (this.type=="grid") return;
	var colObj=this.getColumnObject( 1, colName );
	if (colObj==null)
	{
		if (DataWindow.isDebug) DataWindow_log("不能为不存在的对象设置"+eventName+"处理函数！\n字段名为："+colName);
		return;
	}
	var bSuccess=false;
	if (typeof(handlerName)=="function")
		bSuccess=colObj.detachEvent(eventName, handlerName);
	else if (typeof(handlerName)=="string")
		bSuccess=colObj.detachEvent(eventName, eval(handlerName));
	else
	{
		if (DataWindow.isDebug) DataWindow_log( "无效的参数handlerName，请检查参数！\n字段名字："+colName+"事件名："+eventName+" 处理函数："+handlerName );
		return false;
	}
	if (!bSuccess)
	{
		if (DataWindow.isDebug) DataWindow_log( "取消事件处理函数不成功，请检查参数！\n字段名字："+colName+"事件名："+eventName+" 处理函数："+handlerName );
		return false;
	}
}



/**
 * 设置数据窗口一个字段的焦点
 * @param colName 字段对象或字段名字
 */
function DataWindow_setFocus( colName )
{
	if (colName==undefined || colName==null || colName=="" )
	{
		if (DataWindow.isDebug) DataWindow_log( "要设置焦点的对象为空！" );
		return;
	}
	if (typeof(colName)=="object")
	{
		try
		{
			colName.focus();
			if (colName.tagName.toLowerCase()=="input" ||
				colName.tagName.toLowerCase()=="textarea" )
			colName.select();
		}
		catch (e)
		{ if (DataWindow.isDebug) DataWindow_error( "控件焦点设置错误！\n字段名为："+colName.id+"\n错误信息："+e.message ); }
	}
}


/**
 * 是否是日期型值
 */
function DataWindow_isDate( strDate )
{
	try
	{
		var y = strDate.substr(0,4)*1;
		var m = strDate.substr(5,2)*1;
		var d = strDate.substr(8,2)*1;

		date = new Date(y, m-1, d);

		var y2 = date.getFullYear();
		var m2=date.getMonth() +1;
		var d2=date.getDate();

		if (m2<10) m2="0"+m2;
		if (d2<10) d2="0"+d2;

		if((y2+ "-" + m2+"-"+d2) == strDate)
		{
			return true;
		}
		return false;
	}
	catch(e)
	{
		return false;
	}
}


/**
 * 日期加减函数
 */
function DataWindow_DateAdd( strDate, nAfter, sPart)
{
	if (!DataWindow.isDate(strDate))
	{
		if (DataWindow.isDebug)	DataWindow_log( "相加前的日期格式错误！\n字符串为"+strDate );
		return "";
	}
	if (isNaN(nAfter))
	{
		if (DataWindow.isDebug)	DataWindow_log( "请输入数字型参数！\n参数值为"+nAfter );
		return strDate;
	}
	sPart=sPart.toLowerCase();
	if (sPart!="year" && sPart!="month" && sPart!="day")
	{
		if (DataWindow.isDebug)	DataWindow_log( "请输入正确的参数值！\n参数值为"+sPart );
		return strDate;
	}
	var y = strDate.substr(0,4)*1;
	var m = strDate.substr(5,2)*1;
	var d = strDate.substr(8,2)*1;

	var date = new Date(y, m-1, d);
	if (sPart=="year")
		date.setFullYear( date.getFullYear()+nAfter);
	else if (sPart=="month")
		date.setMonth( date.getMonth()+nAfter);
	else if (sPart=="day")
		date.setDate( date.getDate()+nAfter);
	var dateReturn=new Date(date.valueOf());
	var y2=dateReturn.getFullYear();
	var m2=dateReturn.getMonth() +1;
	var d2=dateReturn.getDate();

	if (m2<10) m2="0"+m2;
	if (d2<10) d2="0"+d2;

	return y2+ "-" + m2+"-"+d2;
}

/**
 * 去掉右边空格
 */
function DataWindow_RTrim(str)
{
	if ((typeof(str) != "string") || !str)
		return "";
	for(var i=str.length-1; i>=0; i--)
		{
			if (str.charCodeAt(i, 10)!=32)
				break;
		}
	return str.substr(0, i+1);
}

/**
 * 计算字符串的真实长度（按照字节计算，而不是按照缺省的字符计算）
 */
function DataWindow_getRealLength( theValue )
{
	var  bytelen=0;
	for (var i=0; i<theValue.length; i++)
	{
		if (theValue.charCodeAt(i) > 255) bytelen = bytelen + 2;
		else bytelen++;
	}
	return bytelen;
}

/**
 * 验证警告
 */
function DataWindow_validateAlert(element, message, isQuiet)
{
  
	var alertMsg;
	if (element.alertTitle != undefined)
		alertMsg="" + element.alertTitle + "" + message;
	else{
		//alertMsg="该输入项：" + message + "\n字段名为：" + element.id;
		//edit by wuyeyuan
		//如果没有alertTtile属性，则默认抓取该字段标签
		var label=element.parentElement.previousSibling.innerText;
		label=trimStr(label);
		if(label.substring(label.length-1,label.length)=="*")
			label=label.substring(0,label.length-1);
		//else	
		alertMsg="" + label + "" + message;
		}
	try
	{
		if (isQuiet) DataWindow.alertMsg=alertMsg;
		else
		{
			alert( alertMsg );
			DataWindow.alertMsg="";
		}
		element.focus();
		element.select();
	}
	catch(e)
	{
	}
}

/**
 * 判断值是否在下拉框中存在
 */
function DataWindow_isValueExist(element, value, isQuiet, valueMustInOptions)
{
	//其他控件认为没有问题，可以通过检查
	if (element.tagName.toLowerCase()!="input" || element.fieldtype!="select")
		return true;
	//使用enableInputOption设置的控件，自动通过检查
	if (element.canInputOption!=undefined && element.canInputOption=="true")
		return true;
	//检查值是否在下拉列表中存在的检查
	var optionsArr=element.options;
	var isValueExist=false;
	for (var i=0; i<optionsArr.length; i++)
	{
		if (optionsArr[i].value==value)
		{
			isValueExist=true;
			break;
		}
	}
	if (!isValueExist)
	{
		DataWindow_validateAlert( element, "中选项选择错误：选项不在列表中！", isQuiet );
		return false;
	}
	return true;
}


/**
 * 检验字段值是否合法
 * @param isQuiet
 * @param valueMustInOptions 
 */
function DataWindow_validateColumn(element, isQuiet, valueMustInOptions)
{
	//设置缺省值
	if (valueMustInOptions==undefined)
		valueMustInOptions=true;
	// 获取输入值
	var value = DataWindow.RTrim(element.value);
	//新增下拉框值的取得使用attr属性
	if (element.tagName.toLowerCase()=="input" && element.fieldtype=="select")
	{
		value = DataWindow.RTrim(element.attr);
		//alert( element.id + "---" + element.attr + ":"+ element.value );
	}
	// 验证空
	if((element.isNull != undefined) && (element.isNull.toLowerCase() == "false"))
	{
		if(value.length == 0)
		{
			DataWindow.validateAlert(element, "不得为空", isQuiet);
			return false;
		}
	}
	if (valueMustInOptions)	//检查必须在列表中
	{
		if (!DataWindow_isValueExist(element, value, isQuiet, valueMustInOptions))
		return false;
	}
	// 验证最大长度
	if(element.maxSize != undefined)
	{
		if(DataWindow.getRealLength(value) > (element.maxSize*1))
		{
			DataWindow.validateAlert(element, "实际输入长度为:" + DataWindow.getRealLength(value) + "，大于最大允许输入长度" + element.maxSize, isQuiet);
			return false;
		 }
	}
	// 验证最小长度
	if(element.minSize != undefined)
	{
		if(DataWindow.getRealLength(value) < (element.minSize*1))
		{
			DataWindow.validateAlert(element, "实际输入长度为:" + DataWindow.getRealLength(value) + "，小于最小允许输入长度" + element.minSize, isQuiet);
				return false;
		}
	}
	// 验证字符型长度
	if(element.textLength != undefined)
	{
		if(DataWindow.getRealLength(value) > (element.textLength*1))
		{
			DataWindow.validateAlert(element, "实际输入长度为:" + DataWindow.getRealLength(value) + "，大于最大允许输入长度" + element.textLength, isQuiet);
			return false;
		 }
	}
	// 验证日期
	if(element.fieldType != undefined && element.fieldType == "date")
	{
		if(value.length > 0 && DataWindow.isDate(value) == false)
		{
			DataWindow.validateAlert(element, "输入值不是合法日期类型，正确输入格式：YYYY-MM-DD", isQuiet);
				return false;
		}
	}
	// 整数验证
	if( (element.intLength != undefined)&&(element.floatLength == undefined) )
	{
		var IntLength=element.intLength;
		for (var i=0; i<value.length; i++)
		{
			var ch = value.substr(i, 1);
			if (ch < "0" || ch > "9" )
			{
				if(ch == "-" && i == 0)
				{
					continue;
				}
				else
				{
					DataWindow.validateAlert(element, "输入值不是合法整数类型", isQuiet);
					return false;
				}
			}
		}
		//长度检测
		if (value.length > IntLength*1)
		{
			DataWindow.validateAlert(element,":输入的整数位数不能超过" + IntLength + "位", isQuiet);
			return false;
		}
	}
	// 验证浮点数
	if((element.intLength != undefined)&&(element.floatLength != undefined))
	{
		var pointIndex = -1;
		for (var i=0; i<value.length; i++)
		{
			var ch = value.substr(i, 1);
			if (ch < "0" || ch > "9")
			{
				if(ch == ".")
				{
					if(pointIndex != -1)
					{
						DataWindow.validateAlert(element, "输入的数字只能有一个小数点", isQuiet);
						return false;
					}
					else
					{
						pointIndex = i;
					}
				}
				else if(ch == "-" && i ==0)
				{
					continue;
				}
				else
				{
					DataWindow.validateAlert(element, "输入值不是合法浮点数类型", isQuiet);
					return false;
				}
			}
		}
		if (pointIndex == -1)
		pointIndex = value.length;
		var IntLength=element.intLength;
		var FloatLength=element.floatLength;
		//整数位数检查
		if ((value.substring(0, pointIndex) * 1).toString().length > IntLength*1)
		{
			DataWindow.validateAlert(element, "输入的整数位数不能超过" + IntLength + "位", isQuiet);
			return false;
		}
		//小数位数检查
		var strFrac = value.substring(pointIndex + 1, value.length);
		if (strFrac.length > 0)
		{
			if ( strFrac.length >FloatLength*1 )
			{
				DataWindow.validateAlert(element, "输入的小数位数不能超过" + FloatLength + "位", isQuiet);
				return false;
			}
		}
	}
	// 验证最大值
	if(element.maxValue != undefined && value.length > 0)
	{
		if((value*1) > (element.maxValue*1))
		{
			DataWindow.validateAlert(element, "实际输入值为:" + value + "，大于最大允许输入值" + element.maxValue, isQuiet);
				return false;
		}
	}
	// 验证最小值
	if(element.minValue != undefined && value.length > 0)
	{
		if((value*1) < (element.minValue*1))
		{
			DataWindow.validateAlert(element, "实际输入值为:" + value + "，小于最小允许输入值" + element.minValue, isQuiet);
				return false;
		}
	}
	return true;
}




/**
 * 类方法：从一个字段对象中获得数据值
 * @param colObj 字段对象
 */
function DataWindow_getColumnValue( colObj )
{
	if (colObj==undefined || colObj==null)
	{
		if (DataWindow.isDebug) DataWindow_log("getColumnValue函数参数错误！被访问的字段对象为空！");
		return "";
	}
	var colType=colObj.type.toLowerCase();
	if(colType=="checkbox"||colType=="radio")
	{
		if (colObj.checked)
			return "1";
		else
			return "0";
	}
	else
	{
		if (colObj.tagName.toLowerCase()=="input" && colObj.fieldtype!=undefined && colObj.fieldtype=="select")	//2005.06.23新增下拉框控件
			return colObj.attr;
		else
			return colObj.value;
	}
}

/**
 * 设置一个字段对象的值
 * @param colObj 字段对象
 * @param colValue 字段值
 */
function DataWindow_setColumnValue( colObj, colValue )
{
	if( colObj.tagName.toUpperCase() == "INPUT")
	{
		if(colObj.type=="checkbox" || colObj.type=="radio")
		{
			if (colValue=="1")
				colObj.checked = true;
			else
				colObj.checked = false;
			return;
		}
		if (colObj.fieldtype=="select")	//新增下拉框控件
		{
			//colObj.attr=colValue;
			//colObj.comboBox = colObj.ownerDocument.parentWindow.ComboBox_Create(colObj.id);
		  	colObj.comboBox.focusDefaultValue(colValue);
			return;
		}
	}
	colObj.value = colValue;
}


/**
 * 获得一个字段在表单中的顺序。（主要用于grid型：字段在fieldName中的顺序）
 * @return int 字段顺序
 */
function DataWindow_getColumnIndex( colName )
{
	if (this.type=="form")
		return 0;
	else
	{
		var fieldArr=this.dw.fieldname.split(",");
		var columnCount=fieldArr.length;
		var nIndex = -1;
		for ( i=0; i<columnCount; i++ )
		{
			if (fieldArr[i]==colName)
			{
				nIndex=i;
				break;
			}
		}
		return nIndex;
	}
}

/**
 * 根据字段名，获得字段的中文名。目前只对grid有效。
 * @param colName 字段名
 * @return String 中文名。如果没有，返回""
 */
function DataWindow_getColumnCName( colName )
{
	if (this.type=="form")
	{
		colObj=this.getColumnObject(0, colName);
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log( "不能从不存在的对象中获得字段中文名！\n字段名为："+colName );
			return "";
		}
		else if (colObj.alertTitle==undefined || colObj.alertTitle==null)
		{
			objLabel=this.getColumnLabel(0, colName );
			if (objLabel!=null) return objLabel.innerText;
			else return colName;
		}
		else return colObj.alertTitle;
	}
	else
	{
		var colNum=this.getColumnIndex(colName);
		if (colNum==-1)
		{
			if (DataWindow.isDebug) DataWindow_log("被访问的字段不存在！\n字段名为："+colName);
			return "";
		}
		var fieldArr=this.dw.fieldCN.split(",");
		var columnCount=fieldArr.length;
		if (colNum<0 && colNum>=columnCount)
			return "";
		else
			return fieldArr[colNum];
	}
}

/**
 * 根据字段名获得一个数据窗口中的字段对象
 * @param rowNum 行号
 * @param colName 字段名
 * @return Object 字段对象
 */
function DataWindow_getColumnObject( rowNum, colName )
{
	if (this.type=="form")
	{
		var colObj=this.dw.getAttribute(colName);
		if (colObj==undefined || colObj==null)	//字段不存在
		{
			//if (DataWindow.isDebug)
			//	DataWindow_log("访问的字段不存在！\n字段名为："+colName);
			return null;
		}
		if (colObj.type==undefined)	//访问的不是字段
		{
			//if (DataWindow.isDebug)
			//	DataWindow_log("访问的不是字段对象！\n字段名为："+colName);
			return null;
		}
		return colObj;
	}
	else
		return null;
}

/**
 * 获得数据窗口中一个字段的标签对象。暂时只对form类型有效
 * @param rowNum 行号
 * @param colName 字段名
 * @return Object 标签对象
 */
function DataWindow_getColumnLabel( rowNum, colName )
{
	if (this.type=="form")
	{
		var objLabel=this.dw.all(colName+"_t");
		if (objLabel==null)
		{
			//if (DataWindow.isDebug) DataWindow_log("访问字段的标签DIV不存在！\n字段名为："+colName);
			return null;
		}
		return objLabel;
	}
	else
	{
		return null;
	}
}

function DataWindow_setColumn( colName )
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject(1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能将焦点设置到不存在的对象中！字段不存在。\n字段名为："+colName);
			return;
		}
		DataWindow.setFocus(colObj);
	}
}


/**
 * 返回数据窗口的记录条数
 * @return int 记录条数
 */
function DataWindow_RowCount( bValid )
{
	if (this.type=="form")
	{
		if (this.dw.DataWindowRowCount!=undefined)
			return this.dw.DataWindowRowCount;
		else
		{
			if (DataWindow.isDebug)	DataWindow_log("没有为Form类型窗口填充数据！默认记录数为1。" );
			return 1;
		}
	}
	else
	{
		if (bValid)	//检查有效行数
		{
			var nNum=0;
			var nCount=this.dw.rows.length;
			for( var i=0; i<nCount; i++)
				if (this.isValidRow(i)) nNum++;
			return nNum;
		}
		else
			return this.dw.rows.length;
	}
}


/**
 * 获得数据窗口当前被选择的行号
 * @return int 行号
 */
function DataWindow_getSelectRow()
{
	if (this.type=="form")
		return 1;
	else
	{
		if (this.dw.rows.length==0)
			return -1;
		return parseInt(this.dw.activeIndex);
	}
}


/**
 *
 */
function DataWindow_getSelectRows()
{
	if (this.type=="form")
		return "1";
	else
	{
		var returnStr="";
		var formObj=this.ownerDoc.getElementById( "form_"+this.dw.id );
		for (var i=0; i< formObj.length; i++)
		{
			var obj= formObj.item(i);
		   	if (obj.type=="checkbox")
		   	{
				if (obj.id !="chkpar_lcGrid" && obj.checked)
				{
					var trobj=getOwnerTR(obj);
					if (returnStr=="")
						returnStr=trobj.rowIndex+"";
					else
						returnStr=returnStr+","+trobj.rowIndex+"";
				}
			}
		}
		return returnStr;
	}
}

/**
 * 选择DataWindow中的一行，目前只对grid有效
 * @param rowNum 需要选择的行
 */
function DataWindow_selectRow( rowNum )
{
	if (this.type=="form")
		return true;
	else
	{
		if (rowNum>=0 && rowNum<this.dw.rows.length)
		{
			var rowObj=this.dw.rows(rowNum);
			rowObj.fireEvent("onclick");
			return true;
		}
		else
		{
			if (DataWindow.isDebug)	DataWindow_log( "选择了不存在的行!" );
			return false;
		}
	}
}


/**
 * 从数据窗口中获得显示的内容。而不是值。只对grid有效
 * @param rowNum 行号
 * @param colName 字段名
 * @return 字符串。如果被访问的字段不存在，返回""
 */
function DataWindow_getItemDisplay(rowNum, colName)
{
	if (this.type=="form")
	{
		return "";
	}
	else
	{
		if (rowNum<0 || rowNum>=this.dw.rows.length) return "";
		var cells=this.dw.rows(rowNum).cells;
		var nLength=cells.length;
		for (var i=0; i<nLength; i++)
		{
			if (cells(i).fieldname==colName)
				return cells(i).innerText;
		}
		return "";
	}
}


/**
 * 从DataWindow中获得一个字段的值
 * @param rowNum 行号
 * @param colName 字段名
 * @return 字符串。如果被访问的字段不存在，返回""
 */
function DataWindow_getItemString(rowNum, colName)
{
	if (arguments.length!=2)
	{
		if (DataWindow.isDebug) DataWindow_error("本函数只支持两个参数！\n函数名为：getItemString");
		return 0;
	}
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( rowNum, colName );
		if (colObj==null) 	//对象为空
		{
			if (DataWindow.isDebug) DataWindow_log("不能从不存在的对象中获得字段值！\n字段名为："+colName);
			return "";
		}
		else
			return DataWindow.getColumnValue(colObj);
	}
	else
	{
		var rowCount=this.RowCount();
		if (rowNum<0 || rowNum>=rowCount)
		{
			if (DataWindow.isDebug) DataWindow_log( "数据访问越界！\n该对象记录数为:"+rowCount+"，而要访问的数据行号为："+rowNum );
			return "";
		}
		var colIndex=this.getColumnIndex( colName );
		if ( colIndex==-1 )
		{
		if (DataWindow.isDebug) DataWindow_log( "没有找到指定名字的字段！\n字段名为："+colName );
			return "";
		}
		if (this.dw.rows[rowNum].ValueStr==undefined)
		{
			if (DataWindow.isDebug) DataWindow_log( "被访问的行没有valueStr属性，可能新增的一条记录！\n行号是："+rowNum+" 字段名："+colName );
			return "";
		}
		var valueStr=this.dw.rows[rowNum].ValueStr;
		var valueArr=valueStr.split(";");
		return valueArr[colIndex];
	}
}


function DataWindow_fireEvent( rowNum, colName, eventName )
{
	if (this.type=="grid") return false;
	var colObj=this.getColumnObject( rowNum, colName );
	if (colObj==null) 	//对象为空
	{
		if (DataWindow.isDebug) DataWindow_log("不能触发不存在的对象的事件！\n字段名为："+colName);
		return false;
	}
	else
	{
		if (colObj.disabled)
		{
			colObj.disabled=false;
			var retValue=colObj.fireEvent(eventName);
			colObj.disabled=true;
			return retValue;
		}
		else
			return colObj.fireEvent(eventName);
	}

}

/**
 * 对Select选择框，取得被选中内容的expand属性值。只对form有效
 * @param rowNum 行号
 * @param colName 字段名
 * @param attrName 属性名。
 * @return 字符串。如果被访问的字段不存在，返回""；如果不是select，返回""
 */
function DataWindow_getItemExpand(rowNum, colName, attrName)
{
	if (arguments.length!=2 && arguments.length!=3)
	{
		if (DataWindow.isDebug) DataWindow_error("本函数只支持两个或三个参数！\n函数名为：getItemExpand");
		return "";
	}
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( rowNum, colName );
		if (colObj==null) 	//对象为空
		{
			if (DataWindow.isDebug) DataWindow_log("不能从不存在的对象中获得字段值！\n字段名为："+colName);
			return "";
		}
		else
		{
			var expandAttr;
			if (arguments.length==2)
				expandAttr="expand";
			else
				expandAttr=attrName;
			if (colObj.tagName.toUpperCase()=="INPUT" && colObj.fieldtype=="select")	//新增下拉框控件
			{
				//colObj.comboBox = this.ownerWin.ComboBox_Create( colObj.id );
				if (colObj.comboBox.activeIndex==-1) return "";
				else return colObj.options[colObj.comboBox.activeIndex].getAttribute(expandAttr);
			}
			else if (colObj.tagName.toUpperCase()!="SELECT")
			{
				return colObj.getAttribute(expandAttr);
			}
			else
			{
				if (colObj.selectedIndex==-1) return "";
				else return colObj.options(colObj.selectedIndex).getAttribute(expandAttr);
			}
		}
	}
	else
		return "";
}



/**
 * 向数据窗口设置数据，暂时只对form类型有效
 * @param rowNum 行号
 * @param colName 字段名
 * @param colValue 字段值
 * @return boolean 成功，返回true；失败，返回false
 */
function DataWindow_setItemString(rowNum, colName, colValue)
{
	if (arguments.length!=3)
	{
		if (DataWindow.isDebug) DataWindow_error("本函数只支持三个参数！\n函数名为：setItemString");
		return false;
	}
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( rowNum, colName );
		if (colObj==null)	//对象不存在
		{
			if (DataWindow.isDebug) DataWindow_log("不能向空字段中设置数据！\n字段名为："+colName);
			return false;
		}
		else
		{
			DataWindow.setColumnValue(colObj, colValue );
			//2005.03.09 Orchid 修改：直接触发刷新功能，而不是试图触发事件。因为在字段disabled情况下，事件无法触发
			if (colObj.dbgridid == undefined) return true;	//没有关联DBGrid
			this.ownerWin.ElementFillGrid( colObj );
			//colObj.fireEvent("onchange");
			return true;
		}
	}
	else
	{
		//if (DataWindow.isDebug) DataWindow_error("向grid窗口设置字段值的功能尚未实现！");
		//return false;
		var rowCount=this.RowCount();
		if (rowNum<0 || rowNum>=rowCount)
		{
			if (DataWindow.isDebug) DataWindow_log( "数据访问越界！\n该对象记录数为:"+rowCount+"，而要访问的数据行号为："+rowNum );
			return false;
		}
		var colIndex=this.getColumnIndex( colName );
		if ( colIndex==-1 )
		{
			if (DataWindow.isDebug) DataWindow_log( "没有找到指定名字的字段！\n字段名为："+colName );
			return false;
		}
		//产生fieldvaluearr数组
		var fieldnamearr=this.dw.fieldname.split(",");
		var fieldvaluearr=new Array();
		if ( this.dw.rows[rowNum].ValueStr != undefined )
			fieldvaluearr=this.dw.rows[rowNum].ValueStr.split(";");
		else
		{
			if (DataWindow.isDebug) DataWindow_log( "被访问的行没有valueStr属性，可能新增的一条记录！\n行号是："+rowNum+" 字段名："+colName );
			fieldvaluearr=new Array(fieldnamearr.length);
			for (var j=0; j<fieldnamearr.length; j++)
				fieldvaluearr[j]="";
		}
		if (colValue==undefined || colValue==null) colValue="";
		colValue=""+colValue;
		var re=/;/g;
		colValue=colValue.replace(re,"；");
		fieldvaluearr[colIndex]=colValue;
		//生成ValueStr属性
		var valuStr="undefined";
		for (var j=0; j< fieldnamearr.length; j++)
		{
			if (valuStr =="undefined")
			{
				if (fieldvaluearr[j]=="")	valuStr ="";
				else valuStr =fieldvaluearr[j];
			}
			else
			{
				if (fieldvaluearr[j]=="") valuStr=valuStr+";";
				else valuStr =valuStr+";"+fieldvaluearr[j];
			}
		}
		this.dw.rows[rowNum].ValueStr = valuStr;
		//设置DBGrid中的内容
		for (var j=0; j< this.dw.rows[rowNum].cells.length; j++)
		{
		   	if (this.dw.rows[rowNum].cells[j].fieldname != undefined)
				if (this.dw.rows[rowNum].cells[j].fieldname.toUpperCase()== colName.toUpperCase())
				{
					this.dw.rows[rowNum].cells[j].innerText=colValue;
				}
		}
		this.editRow(rowNum);
	}
}

/**
 * 从DataWindow中获得一个字段的整数值
 * @param rowNum 行号
 * @param colName 字段名
 * @return int。如果被访问的字段不存在，返回0
 */
function DataWindow_getItemInt( rowNum, colName )
{
	if (arguments.length!=2)
	{
		if (DataWindow.isDebug) DataWindow_error("本函数只支持两个参数！\n函数名为：getItemInt");
		return 0;
	}
	var sTemp=this.getItemString(rowNum,colName);
	if (sTemp=="")
		return 0;
	else
	{
		var nTemp=parseInt(sTemp,10);
		if (isNaN(nTemp))
		{
			if (DataWindow.isDebug) DataWindow_log( "格式转换错误!\n字符串值为："+sTemp );
			return 0;
		}
		else
			return nTemp;
	}
}


/**
 * 向数据窗口设置数据，暂时只对form类型有效
 * @param rowNum 行号
 * @param colName 字段名
 * @param colValue 字段值
 * @return boolean 成功，返回true；失败，返回false
 */
function DataWindow_setItemInt( rowNum, colName,colValue )
{
	if (arguments.length!=3)
	{
		if (DataWindow.isDebug) DataWindow_error("本函数只支持三个参数！\n函数名为：setItemInt");
		return false;
	}
	this.setItemString(rowNum,colName,colValue);
}

/**
 * 从DataWindow中获得一个字段的整数值
 * @param rowNum 行号
 * @param colName 字段名
 * @return int。如果被访问的字段不存在，返回0
 */
function DataWindow_getItemFloat( rowNum, colName )
{
	if (arguments.length!=2)
	{
		if (DataWindow.isDebug) DataWindow_error("本函数只支持两个参数！\n函数名为：getItemFloat");
		return 0;
	}
	var sTemp=this.getItemString(rowNum,colName);
	if (sTemp=="")
		return 0;
	else
	{
		var nTemp=parseFloat(sTemp,10);
		if (isNaN(nTemp))
		{
			if (DataWindow.isDebug) DataWindow_log( "格式转换错误!\n字符串值为："+sTemp );
			return 0;
		}
		else
			return nTemp;
	}
}


/**
 * 向数据窗口设置数据，暂时只对form类型有效
 * @param rowNum 行号
 * @param colName 字段名
 * @param colValue 字段值
 * @return boolean 成功，返回true；失败，返回false
 */
function DataWindow_setItemFloat( rowNum, colName,colValue )
{
	if (arguments.length!=3)
	{
		if (DataWindow.isDebug) DataWindow_error("本函数只支持三个参数！\n函数名为：setItemFloat");
		return false;
	}
	this.setItemString(rowNum,colName,colValue);
}


/**
 * 在数据窗口中插入一条记录，只对grid类型数据窗口有用
 * @rowNum 可选参数。在指定行号之前插入记录
 * @return int 新插入记录的行号。如果是form类型，返回-1
 */
function DataWindow_insertRow( rowNum )
{
	if (this.type=="form")
	{
		if (this.dw.DataWindowRowCount==undefined)
			this.dw.DataWindowRowCount=1;
		else
			this.dw.DataWindowRowCount=this.dw.DataWindowRowCount+1;
		return -1;
	}
	this.ownerWin.DBGrid_insertRow(this.dw, rowNum);
	return this.dw.activeIndex;
}

/**
 * 从数据窗口中删除一条记录，只对grid有效
 *
 */
function DataWindow_deleteRow( rowNum )
{
	if (this.type=="grid")
	{
		this.selectRow( rowNum );
		this.ownerWin.DBGrid_deleteRow( this.dw, rowNum );
	}
}

/**
 * 修改数据窗口一条记录，只对grid有效
 *
 */
function DataWindow_editRow( rowNum )
{
	if (this.type=="grid")
	{
		this.selectRow( rowNum );
		this.ownerWin.DBGrid_editRow( this.dw, rowNum );
	}
}

/**
 * 放弃对数据窗口一条记录的修改，只对grid有效
 *
 */
function DataWindow_cancelRow( rowNum )
{
	if (this.type=="grid")
	{
		this.selectRow( rowNum );
		this.ownerWin.DBGrid_cancelRow( this.dw, rowNum );
	}
}


/**
 * 检查数据窗口中的数据是否符合较验规则。暂时不支持没有绑定form的grid型窗口
 * @param isQuiet 安静方式的检测
 * @param valueMustInOptions 值是否必须在下拉框列表中，缺省值为true
 * @return Object 较验不通过的对象。如果全部通过，返回null
 */
function DataWindow_validate( isQuiet, valueMustInOptions )
{
	if (valueMustInOptions==undefined)
		valueMustInOptions==true;
	var elementObj;
	if (this.type=="form")
	{
		elementObj=null;
		var elementsArr=this.dw.elements;
		for( i=0; i<elementsArr.length; i++ )
		{
			elementObj=elementsArr.item(i);
			if (DataWindow.validateColumn(elementObj, isQuiet, valueMustInOptions)==false)
				return elementObj;
		}
		return null;
	}
	else
	{
		if (this.dw.dbformname == undefined)
			return null;
		//循环grid的每一行，先填充到关联的form中，然后对form进行检查，较验
		for( j=0; j<this.dw.rows.length; j++ )
		{
			if (!this.isValidRow(j)) continue;	//已经删除的记录，不用校验
			this.selectRow(j);
			var formObj=this.ownerDoc.getElementById( this.dw.dbformname );
			var elementsArr=formObj.elements;
			for( i=0; i<elementsArr.length; i++ )
			{
				elementObj=elementsArr.item(i);
				if (DataWindow.validateColumn(elementObj, isQuiet, valueMustInOptions)==false)
					return elementObj;
			}
		}
		return null;
	}
}


/**
 * 检查数据窗口中的数据是否主键冲突
 * @param pkStr 主键字段列表，使用","分割。例如："BH,XM,ZJMC,ZJHM"。
 */
function DataWindow_validatePrimaryKey( pkStr )
{
	if (this.type=="form")
		return true;
	else
	{
		var pkArr=pkStr.split(",");
		if (pkArr.length==0)
		{
			if (DataWindow.isDebug) DataWindow_log( "请输入需要检测的主键字段!" );
			return true;
		}
		var pkTitle="";
		for (var pki=0; pki<pkArr.length; pki++)
		{
			pkTitle+=this.getColumnCName(pkArr[pki])+"、";
		}
		pkTitle=pkTitle.substring(0,pkTitle.length-1);
		var nRowCount=this.RowCount();
		for( var i=0; i<nRowCount; i++ )
		{
			if (!this.isValidRow(i)) continue;
			var pkValue="";
			for( var pki=0; pki<pkArr.length; pki++)
			{
				pkValue+=this.getItemString(i,pkArr[pki]);
			}
			for( var j=i+1; j<nRowCount; j++)
			{
				if (!this.isValidRow(j)) continue;
				var pkValue2="";
				for( var pki=0; pki<pkArr.length; pki++)
				{
					pkValue2+=this.getItemString(j,pkArr[pki]);
				}
				if (pkValue==pkValue2)
				{
					this.selectRow(i);
					alert( "第"+(i+1)+","+(j+1)+"条记录关键字重复！\n关键字字段："+pkTitle );
					return false;
				}
			}
		}
		return true;
	}
}

/**
 * 设置数据窗口中的一个字段的disabled属性。暂时只对form类型窗口有效。
 * @param colName 字段名
 * @return
 */
function DataWindow_disableColumn2( colName )
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( 1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能对不存在的对象设置disabled属性！\n字段名为："+colName);
			return false;
		}
		else
		{
			colObj.disabled2="true";
		}
	}
}


/**
 * 设置数据窗口中的一个字段的disabled属性。暂时只对form类型窗口有效。
 * @param colName 字段名
 * @return
 */
function DataWindow_disableColumn( colName )
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( 1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能对不存在的对象设置disabled属性！\n字段名为："+colName);
			return false;
		}
		else
		{
			//if ((colObj.tagName.toLowerCase()=="input" && (colObj.type=="text" || colObj.type=="password")) || colObj.tagName.toLowerCase()=="textarea")
			//	colObj.readOnly=true;
			//else
				colObj.disabled=true;
		}
	}
}


/**
 * 取消数据窗口中一个字段的disabled属性。暂时只对form类型窗口有效。
 * @param colName 字段名
 * @return
 */
function DataWindow_enableColumn2( colName )
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( 1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能对不存在的对象设置enable属性！\n字段名为："+colName);
			return false;
		}
		else colObj.disabled2="false";
	}
}



/**
 * 取消数据窗口中一个字段的disabled属性。暂时只对form类型窗口有效。
 * @param colName 字段名
 * @return
 */
function DataWindow_enableColumn( colName )
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( 1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能对不存在的对象设置enable属性！\n字段名为："+colName);
			return false;
		}
		else
		{
			if (colObj.disabled2==undefined || colObj.disabled2=="false")
			{
				//if ((colObj.tagName.toLowerCase()=="input" && (colObj.type=="text" || colObj.type=="password")) || colObj.tagName.toLowerCase()=="textarea")
				//	colObj.readOnly=false;
				//else
					colObj.disabled=false;
			}
		}
	}
}

/**
 * 对一个数据窗口中的所有控件设置disabled属性。暂时只对form类型有效。
 * @return
 */
function DataWindow_disableColumnAll()
{
	if (this.type=="form")
	{
		var elementsArr=this.dw.elements;
		for( i=0; i<elementsArr.length; i++ )
		{
			elementObj=elementsArr.item(i);
			this.disableColumn( elementObj.id );
		}
	}
}

/**
 * 对一个数据窗口中的所有控件取消disabled属性。暂时只对form类型有效。
 * @return
 */
function DataWindow_enableColumnAll()
{
	if (this.type=="form")
	{
		var elementsArr=this.dw.elements;
		for( i=0; i<elementsArr.length; i++ )
		{
			elementObj=elementsArr.item(i);
			this.enableColumn( elementObj.id );
		}
	}
}

/**
 * 设置数据窗口中某个字段内容可以是手工录入选项，不必在下拉框列表中
 * @param colName 字段名 
 */
function DataWindow_enableInputOption(colName)
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( 1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能对不存在的对象enableInputOption！\n字段名为："+colName);
			return false;
		}
		//只操作新增的控件类型
		if (colObj.tagName.toLowerCase()=="input" && colObj.fieldtype=="select" )
			colObj.canInputOption="true";
	}
}

/**
 * 设置数据窗口中某个字段内容不能是手工录入选项，必须在下拉框列表中
 * @param colName 字段名
 */
function DataWindow_disableInputOption(colName)
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( 1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能对不存在的对象disableInputOption！\n字段名为："+colName);
			return false;
		}
		if (colObj.tagName.toLowerCase()=="input" && colObj.fieldtype=="select" )
			colObj.canInputOption="false";
	}
}


/**
 * 允许一个字段可以为空值
 * @param colName 字段名
 */
function DataWindow_enableColumnNull( colName )
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( 1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能对不存在的对象enableNull！\n字段名为："+colName);
			return false;
		}
		else colObj.isNull="true";

		var colLabel=this.getColumnLabel( 1, colName );
		if (colLabel==null)
		{
			if (DataWindow.isDebug) DataWindow_log("无法获得指定字段的标签对象！\n字段名为："+colName);
			return false;
		}
		else colLabel.style.color="black";
	}
}


/**
 * 不允许一个字段的值为空值
 * @param colName 字段名
 */
function DataWindow_disableColumnNull( colName )
{
	if (this.type=="form")
	{
		var colObj=this.getColumnObject( 1, colName );
		if (colObj==null)
		{
			if (DataWindow.isDebug) DataWindow_log("不能对不存在的对象disableNull！\n字段名为："+colName);
			return false;
		}
		else colObj.isNull="false";
		var colLabel=this.getColumnLabel( 1, colName );
		if (colLabel==null)
		{
			if (DataWindow.isDebug) DataWindow_log("无法获得指定字段的标签对象！\n字段名为："+colName);
			return false;
		}
		else colLabel.style.color="red";
	}
}

/**
 * 重新设置数据窗口的OldValue属性。
 * 用在数据提交成功后，第二次提交的时候，如果不修改，
 * oldvalue传递到后台依然是修改前的数据，而不是刚刚存盘的修改后数据。
 */
function DataWindow_reInitOldValue()
{
	if (this.type=="form")
	{
		elementObj=null;
		var elementsArr=this.dw.elements;
		for( i=0; i<elementsArr.length; i++ )
		{
			elementObj=elementsArr.item(i);
			elementObj.oldvalue=elementObj.value;
		}
	}
	else
	{
		var nRowCount=this.RowCount();
		for (var i=nRowCount-1; i>=0; i-- )
		{
			this.dw.rows(i).className="gridEvenRow";	//清除被选择的行号
			this.dw.rows(i).OldValueStr=this.dw.rows(i).ValueStr;
			if (this.dw.rows(i).operate=="insert")
				this.dw.rows(i).operate="update";
			else if (this.dw.rows(i).operate=="delete")
				this.dw.rows(i).removeNode(true);
		}
		if (this.dw.rows.length>0)
		{
			this.dw.activeIndex=0;
			this.selectRow(0);
		}
		else
			this.dw.activeIndex=-1;
	}
}

/**
 * 判断DataWindow中的值是否已经修改过。
 * 
 */
function DataWindow_isModified()
{
	var isModified=false;
	if (this.type=="form")
	{
		elementObj=null;
		var elementsArr=this.dw.elements;
		for( i=0; i<elementsArr.length; i++ )
		{
			elementObj=elementsArr.item(i);
			if (elementObj.value!=elementObj.oldvalue)
			{
				if (DataWindow.isDebug)
				{
					DataWindow_log( "字段内容已修改！\n字段名为："+elementObj.id
						+"\nvalue="+elementObj.value
						+" oldvalue="+elementObj.oldValue );
				}
				isModified=true;
				break;
			}
		}
	}
	else
	{
		var nRowCount=this.RowCount();
		for (var i=0; i<nRowCount; i++ )
		{
			if (this.dw.rows(i).operate=="insert"
				|| this.dw.rows(i).operate=="delete")
			{
				isModified=true;
				break;
			}
			else if (this.dw.rows(i).operate=="update"
					&& this.dw.rows(i).OldValueStr!=this.dw.rows(i).ValueStr)
			{
				if (DataWindow.isDebug)
				{
					DataWindow_log( "内容已修改！\n行号为："+i
						+"ValueStr="+this.dw.rows(i).ValueStr
						+"OldValueStr="+this.dw.rows(i).OldValueStr );
				}
				isModified=true;
				break;
			}
		}
	}
	return isModified;
}

/**
 * 取得grid类型数据窗口某一行记录的operation属性。
 * operation属性用来表示对当前记录的操作类型。
 * insert, update, delete表示已经修改过的行。""表示没有操作的行。
 */
function DataWindow_getOperate( rowNum )
{
	if (this.type=="form")
		return null;
	if (rowNum<0 && rowNum>=this.dw.rows.length) return null;
	return this.dw.rows(rowNum).operate;
}

/**
 * 检查一条记录是否为有效记录
 */
function DataWindow_isValidRow( rowNum )
{
	if (this.getOperate(rowNum)=="delete")
		return false;
	else
		return true;
}

function DataWindow_disposeComboBox()
{
	if (this.type=="form")	//清空ComboBox对象占用内存
	{
		elementObj=null;
		var elementsArr=this.dw.elements;
		for( i=0; i<elementsArr.length; i++ )
		{
			elementObj=elementsArr.item(i);
			if (elementObj.tagName.toLowerCase()=="input" && elementObj.fieldtype=="select")
			{
				if (elementObj.comboBox!=undefined)
				{
					elementObj.comboBox.dispose();
					elementObj.comboBox=null;
				}
			}
		}
	}
}

/**
 * 释放DataWindow对象的内存
 */
function DataWindow_dispose()
{
	//this.disposeComboBox();
	this.dw = null;
	this.type = null;
	this.ownerDoc = null;
	this.ownerWin = null;
	if (this.type=="grid")
	{
		this.fieldArr=null;
		this.columnCount=null;
	}
	/*
	this.getColumnObject  = null;
	this.RowCount  = null;
	this.getColumnIndex  = null;
	this.getSelectRow  = null;
	this.getSelectRows  = null;
	this.fireEvent  = null;
	this.selectRow  = null;
	this.insertRow  = null;
	this.editRow  = null;
	this.deleteRow  = null;
	this.cancelRow  = null;
	this.getItemDisplay  = null;
	this.getItemString  = null;
	this.getItemExpand  = null;
	this.setItemString  = null;
	this.getItemInt  = null;
	this.setItemInt  = null;
	this.getItemFloat  = null;
	this.setItemFloat  = null;
	this.validate  = null;
	this.validatePrimaryKey  = null;
	this.disableColumn  = null;
	this.enableColumn  = null;
	this.disableColumn2  = null;
	this.enableColumn2  = null;
	this.disableColumnAll  = null;
	this.enableColumnAll  = null;
	this.enableColumnNull  = null;
	this.disableColumnNull  = null;
	this.enableInputOption  = null;
	this.disableInputOption  = null;
	this.getColumnLabel  = null;
	this.getColumnCName  = null;
	this.setOnChangeHandler  = null;
	this.setEventHandler  = null;
	this.removeOnChangeHandler  = null;
	this.removeEventHandler  = null;
	this.setColumn  = null;
	this.reInitOldValue  = null;
	this.isModified  = null;
	this.getOperate  = null;
	this.isValidRow  = null;
	this.disposeComboBox = null;

	this.dispose  = null;

	DataWindow.getColumnValue  = null;
	DataWindow.setColumnValue  = null;
	DataWindow.isDate  = null;
	DataWindow.validateColumn  = null;
	DataWindow.validateAlert  = null;
	DataWindow.RTrim  = null;
	DataWindow.getRealLength  = null;
	DataWindow.DateAdd  = null;
	DataWindow.setFocus  = null;

	DataWindow.isDebug  = null;
	DataWindow.alertMsg = null;
	*/
}


//////////////////////////////////////////////////////////////////////
/////////////////////// Debug Function ///////////////////////////////
// Using Debug.js Function. If release, no debug
function DataWindow_log( v )
{
	if (typeof(Debug_log)=="function")
		Debug_log( v );
	else
		window.status=v;
}

function DataWindow_error( v )
{
	if (typeof(Debug_error)=="function")
	{
		window.status=v;
		Debug_error( v );
	}
	else
		window.status=v;
}
/////////////////////// Debug Function ///////////////////////////////
//////////////////////////////////////////////////////////////////////

