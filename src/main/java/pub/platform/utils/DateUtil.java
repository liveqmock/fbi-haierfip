package pub.platform.utils;

import java.util.Calendar;
import java.util.Date;

import org.apache.ecs.html.Script;
import org.apache.ecs.html.TD;

public class DateUtil {

	public static String getCurrentDate() {
		return StringUtils.toDateFormat(new Date(),"yyyy-MM-dd");
		
	}

	public static String getCurrentDateTime() {
		return StringUtils.toDateFormat(new Date(),"yyyy-MM-dd HH:mm:ss");
	}
	
	/**
	 * 获得系统的业务日期字符串，批量操作时使用
	 * 文件上传时名称中指定数据的日期，所以批量会在指定日期之后开始运行
	 * @return
	 */
    public static String getBussDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
		return StringUtils.toDateFormat(calendar.getTime(),"yyyy-MM-dd");
    }	
    
	public static String getBussDateStr() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH,-1);
		return StringUtils.toDateFormat(calendar.getTime(),"yyyyMMdd");
    }	    
    
    /**
     * 获得系统的业务日期字符串，批量操作时使用
     * @return
     */
	public static String getDateStr() {
		return StringUtils.toDateFormat(new Date(),"yyyyMMdd");	
	}
	
    public static String getTime(String strMsg)
    {
        StringBuffer strBuf = new StringBuffer();
        Script script = new Script();
        script.setLanguage( "javascript" );
        script.addElement( "function watch()" );
        script.addElement( "{" );
        script.addElement( "    var date = new Date();" );
        script.addElement( "	var hours = date.getHours();" );
        script.addElement( "	var minutes = date.getMinutes();" );
        script.addElement( "    var seconds = date.getSeconds();" );
        script.addElement( "	if( minutes < 10 )" );
        script.addElement( "		minutes = '0' + minutes;" );
        script.addElement( "	if( seconds < 10 )" );
        script.addElement( "		seconds = '0' + seconds;" );
        script.addElement( "	document.all.time.innerText = '" + strMsg + "' + hours + ':' + minutes + ':' + seconds;" );
        script.addElement( "	window.setTimeout( 'watch()' , 1000 );" );
        script.addElement( "}" );
        strBuf.append( script.toString() );
        script = new Script();
        script.addElement( "watch();" );
        TD td = new TD();
        td.setAlign( "center" );
        td.setNoWrap( true );

        td.setID( "time" );
        td.addElement( script );
        strBuf.append( td.toString() );
        return strBuf.toString();
    }	
    
    public static void main(String[] args) {
    	System.out.println(DateUtil.getBussDate());
    }
}
