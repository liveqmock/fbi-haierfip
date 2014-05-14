//调试级别 log, alert, none
//log : 写在页面的div logPanel中
//alert : 使用alert函数显示消息
//status : 显示在状态行中
//message : 存储在变量中，使用F1查看
//off : 不显示也不保存，忽略调试信息
Debug_Method="log";
var DebugMessage="";
var logPanelObj;
var DebugMessageArr=[];

function Debug_CurrentDate()
{
	var date=new Date();
	var years = date.getFullYear();
	var months = date.getMonth()+1;
	var days = date.getDate();

	if (months < 10) months = "0" + months;
	if (days < 10) days = "0" + days;

	return years+ "-" + months + "-" + days;
}

function Debug_CurrentTime()
{
	var date=new Date();
	var hours = date.getHours();
	var minutes = date.getMinutes();
	var seconds = date.getSeconds();
	var Milliseconds = date.getMilliseconds();

	if (hours < 10) hours = "0" + hours;
	if (minutes < 10) minutes = "0" + minutes;
	if (seconds < 10) seconds = "0" + seconds;
	if (Milliseconds<10) Milliseconds = "00" + Milliseconds;
	else if (Milliseconds<100) Milliseconds = "0" + Milliseconds;

	return hours + ":" + minutes + ":" + seconds + ":" + Milliseconds;
}

function Debug_init()
{
	if (logPanelObj==undefined || typeof(logPanelObj)==undefined)
	{
		if (document.body==undefined) return false;
		document.body.insertAdjacentHTML("afterBegin","<div id=logPanel "+
		"style='border-bottom-style:groove;"+
		"border-right-style:groove;"+
		"border-top-style:groove;"+
		"border-left-style:groove;"+
		"border-width: 2px;z-index:100;position:absolute;top:5px;left:5px;"+
		"visibility:hidden;background-color:cornsilk;width:100%;'>"+
		"<font style='font-family:Tahoma;font-size:12pt;font-weight:bold'> Debug Log Window: </font> "+

		"<input style='font-family:Tahoma;font-size:9pt;border-style:solid;border-width:1px;"+
			"background-color:cornsilk;border-color:inactivecaptiontext;' "+
			"type=button onclick=\"logPanel_log.innerHTML='';\" "+
			"onmouseover=\"Debug_onmouseover()\" onmouseout=\"Debug_onmouseout()\" value=' Clear Log '></input>&nbsp;"+
		"<input style='font-family:Tahoma;font-size:9pt;border-style:solid;border-width:1px;"+
			"background-color:cornsilk;border-color:inactivecaptiontext;' "+
			"type=button onclick=\"logPanel_log.innerHTML+='<hr style=height:1px>';\" "+
			"onmouseover=\"Debug_onmouseover()\" onmouseout=\"Debug_onmouseout()\" value=' Insert HR '></input>"+
		"<hr style='height:1px'><div id='logPanel_log' style='overflow-y:auto;height:300px;font-family:宋体;font-size:9pt;'></div></div>"
		);
		for( var i=0; i<DebugMessageArr.length; i++)
		{
			logPanel_log.innerHTML+=DebugMessageArr[i];
			DebugMessageArr[i]=null;
		}
		DebugMessageArr=null;
		logPanelObj="1";
	}
	return true;
}

function Debug_log( v )
{
	Debug_output( v, false );
}

function Debug_error( v )
{
	Debug_output( v, true );
}

function Debug_output( v, isError )
{
	v=""+v;
	var re=/</g;
	v=v.replace(re,"&lt");
	re=/>/g;
	v=v.replace(re,"&gt");

	var s;
	if (Debug_Method=="log")
	{
		if (isError) s="<li><font color=red>"+Debug_CurrentTime()+"</font> "+v+"</li>";
		else 		 s="<li><font color=blue>"+Debug_CurrentTime()+"</font> "+v+"</li>";
		if (Debug_init())	//创建logPanel成功
			logPanel_log.innerHTML+=s;
		else
			DebugMessageArr[DebugMessageArr.length]=s;
	}
	else if (Debug_Method=="alert") alert( Debug_CurrentTime() + " " + v );
	else if (Debug_Method=="status") window.status=Debug_CurrentTime() + " " + v;
	else if (Debug_Method=="message") DebugMessage += (Debug_CurrentTime() + " " + v+"\n");
}

function Debug_start()
{
	if (Debug_Method=="log")
	{
		window.onhelp = function()
		{
			if (logPanelObj==undefined || typeof(logPanelObj)==undefined) return false;
			logPanel.style.visibility=logPanel.style.visibility=='hidden'?"visible":"hidden";
			return false;
		}
	}
	else if (Debug_Method=="alert")
	{
	}
	else if (Debug_Method=="message")
	{
		window.onhelp = function()
		{
			alert( DebugMessage );
			DebugMessage="";
			return false;
		}
	}
}

function Debug_onmouseover()
{
	var element=event.srcElement;
	element.style.borderStyle="solid";
	element.style.borderColor="highlight";
	element.style.borderWidth="1px";
}

function Debug_onmouseout()
{
	var element=event.srcElement;
	var element=event.srcElement;
	element.style.borderStyle="solid";
	element.style.borderColor="inactivecaptiontext";
	element.style.borderWidth="1px";
}

Debug_start();