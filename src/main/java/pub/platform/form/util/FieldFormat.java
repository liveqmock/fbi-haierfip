//Source file: e:\\java\\zt\\platform\\form\\util\\FieldFormat.java

package pub.platform.form.util;

import java.util.regex.*;

import pub.platform.db.DBUtil;
import pub.platform.db.RecordSet;
import pub.platform.form.config.*;
import pub.platform.form.control.SessionContext;
import pub.platform.utils.expression.*;

/**
 * 字段格式化输出类
 *
 * @author qingdao tec
 * @version 1.0
 */
public class FieldFormat {

    public static final int CHARACTOR_PIXEL = 6;

    /**
     * @param ctx
     * @param name
     * @param formInstance
     * @param values
     * @return String
     * @roseuid 3F7F4BAE0070
     */
    public String format(SessionContext ctx, ElementBean eb, FormInstance fi, RecordSet values) {
        try {
            String value = "";
            if (eb.getExpression() != null && eb.getExpression().trim().length() > 0) {
                value = getExpression(ctx, eb.getExpression(), fi, values);
                try {
                        value = DBUtil.doubleToStr1(Double.parseDouble(value));
                    }
                    catch (Exception e) {

                    }
            } else {
                value = values.getString(eb.getName());
                if ( value != null ) {
                    value = value.trim();
                } else {
                    value = "";
                }
//                if ( eb.isNeedEncode() ) {
                    value = DBUtil.fromDB(value);
//                }
                int width = eb.getWidth();
                width /= CHARACTOR_PIXEL;
                if ( eb.getWidth() > 6 && value.length() > width ) {
                    value = value.substring(0,width);
                }

                if (eb.getDataType() == ElementBean.DATATYPE_DECIMAL) {
                    try {
                        value = DBUtil.doubleToStr1(Double.parseDouble(value));
                    }
                    catch (Exception e) {

                    }
                }
                else if (eb.getDataType() == ElementBean.DATATYPE_INTEGER) {
                    try {
                        value = DBUtil.intToStr(Integer.parseInt(value));
                    }
                    catch (Exception e) {

                    }
                } else if ( eb.getDataType() == ElementBean.DATATYPE_ENUMERATION ) {
                    String enumname = eb.getEnutpname();
                    if ( enumname != null ) {
                        EnumerationBean localenum = EnumerationType.getEnu(enumname);
                        Integer intValue = new Integer(value);
                        String tmpStr = (String)localenum.getValue(intValue);
                        if ( tmpStr != null && tmpStr.trim().length() > 0 )
                            value = tmpStr.trim();
                    }
                }
            }

            return value;
        } catch ( Exception ex ) {
            return null;
        }
    }
    //解析表达式
    //$name$变量值取自RecordSet
    //$$name$$变量值取自Context
    //$$$name$$$取自实例
    //$$$$name$$$$取自请求参数
    private final String getExpression(SessionContext ctx,String s,FormInstance fi,RecordSet values) throws Exception {
        StringBuffer sb;
        String tmpStr = s;
        try {
            //1.替换所有的请求参数
            Pattern p1 = Pattern.compile("[$]{4}[a-zA-Z]+[$]{4}");
            Matcher m1 = p1.matcher(tmpStr);
            sb = new StringBuffer();

            while (m1.find()) {
                double unit = 0;
                try {
                    String name  = m1.group();
                    name         = name.substring(4, name.length() - 4);
                    String value = (String) ctx.getParameter(name);
                    unit = Double.parseDouble(value);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                m1.appendReplacement(sb, ""+unit);
            }
            m1.appendTail(sb);
            tmpStr = sb.toString();

            //2.替换所有的实例变量
            Pattern p2 = Pattern.compile("[$]{3}[a-zA-Z]+[$]{3}");
            Matcher m2 = p2.matcher(tmpStr);
            sb = new StringBuffer();

            while (m2.find()) {
                double unit = 0;
                try {
                    String name = m2.group();
                    name = name.substring(3, name.length() - 3);
                    String value = (String) fi.getValue(name).getValueArray()[0];
                    unit = Double.parseDouble(value);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
                m2.appendReplacement(sb, ""+unit);
            }
            m2.appendTail(sb);
            tmpStr = sb.toString();

            //3.替换所有的上下文变量
            Pattern p3 = Pattern.compile("[$]{2}[a-zA-Z]+[$]{2}");
            Matcher m3 = p3.matcher(tmpStr);
            sb = new StringBuffer();
            while (m3.find()) {
                double unit = 0;
                try {
                    String name = m3.group();
                    name = name.substring(2, name.length() - 2);
                    String value = (String) ctx.getAttribute(name);
                    unit = Double.parseDouble(value);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }

                m3.appendReplacement(sb, ""+unit);
            }
            m3.appendTail(sb);
            tmpStr = sb.toString();

            //4.替换所有的结果集变量
            Pattern p4 = Pattern.compile("[$]{1}[a-zA-Z]+[$]{1}");
            Matcher m4 = p4.matcher(tmpStr);
            sb = new StringBuffer();
            while (m4.find()) {
                double unit = 0;
                try {
                    String name = m4.group();
                    name = name.substring(1, name.length() - 1);
                    String value = (String) values.getString(name);
                    unit = Double.parseDouble(value);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }

                m4.appendReplacement(sb, ""+unit);
            }
            m4.appendTail(sb);
            tmpStr = sb.toString();
            try {
                Parser p = new Parser();
                p.parse(tmpStr);
                return ""+p.getResult();
            } catch ( Exception e ) {
                return e.getMessage();
            }
        } catch ( Exception e ) {
            sb = new StringBuffer();
            sb.append(s+"【"+e.getMessage()+"】");
        }

        return sb.toString();
    }
}
