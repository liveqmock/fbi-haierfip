// Decompiled by Jad v1.5.7g. Copyright 2000 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/SiliconValley/Bridge/8617/jad.html
// Decompiler options: packimports(3) fieldsfirst ansi
// Source File Name:   Time.java

package pub.platform.utils;

import org.apache.ecs.html.Script;
import org.apache.ecs.html.TD;

public final class Time
{

    private String _$11787;
    private String _$1885;

    public Time(String strMsg)
    {
        _$1885 = strMsg;
    }

    public void setBgColor(String strColor)
    {
        _$11787 = strColor;
    }

    public String getTime()
    {
        StringBuffer strBuf = new StringBuffer();
        Script script = new Script();
        script.setLanguage("javascript");
        script.addElement("function watch()");
        script.addElement("{");
        script.addElement("    var date = new Date();");
        script.addElement("\tvar hours = date.getHours();");
        script.addElement("\tvar minutes = date.getMinutes();");
        script.addElement("    var seconds = date.getSeconds();");
        script.addElement("\tif( minutes < 10 )");
        script.addElement("\t\tminutes = '0' + minutes;");
        script.addElement("\tif( seconds < 10 )");
        script.addElement("\t\tseconds = '0' + seconds;");
        script.addElement("\tdocument.all.time.innerText = '" + _$1885 + "' + hours + ':' + minutes + ':' + seconds;");
        script.addElement("\twindow.setTimeout( 'watch()' , 1000 );");
        script.addElement("}");
        strBuf.append(script.toString());
        script = new Script();
        script.addElement("watch();");
        TD td = new TD();
        td.setAlign("center");
        td.setNoWrap(true);
        if(_$11787 != null)
            td.setBgColor(_$11787);
        td.setID("time");
        td.addElement(script);
        strBuf.append(td.toString());
        return strBuf.toString();
    }
}
