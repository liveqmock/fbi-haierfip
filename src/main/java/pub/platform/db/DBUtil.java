/**
 * Copyright 2003 ZhongTian, Inc. All rights reserved.
 *
 * qingdao tec PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * $Id: DBUtil.java,v 1.1 2006/05/17 09:19:30 wiserd Exp $
 * File:DBUtil.java
 * Date Author Changes
 * March 10 2003 wangdeliang Created
 */

package pub.platform.db;

import pub.platform.advance.utils.PropertyManager;
import pub.platform.form.config.SystemAttributeNames;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

/**
 * 数据库工具包
 *
 * @author <a href="mailto:wuyeyuan@tom.com">wuyeyuan</a>
 * @version $Revision: 1.1 $ $Date: 2006/05/17 09:19:30 $
 */

public class DBUtil {

    /**
     * 将日期类型转换成SQL92的标准
     *
     * @param date
     *            日期
     * @return
     */
    static Logger logger = Logger.getLogger("zt.platform.db.DBUtil");

    public static String formatWithSql92Date(Date date) {
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh : mm : ss");
            return "{ts '" + sdf.format(date) + "'}";
        } else {
            return "NULL";
        }
    }

    public static String formatDateTime(String DateTimeStr) {
        if (DateTimeStr != null) {
            return "{ts '" + DateTimeStr + "'}";
        } else {
            return null;
        }
    }

    /**
     * 按要求转码
     *
     * @param content
     * @param fromEncoding
     * @param toEncoding
     * @return
     */
    public static String toDB(String content, String fromEncoding, String toEncoding) {
        // if ( content == null )
        // return null;
        // try {
        // byte[] tt;
        // if ( fromEncoding == null ) {
        // tt = content.getBytes();
        // } else {
        // //logger.info("formEncoding is "+fromEncoding);
        // tt = content.getBytes(fromEncoding);
        // }
        // if ( toEncoding == null )
        // return new String(tt);
        // return new String(tt,toEncoding);
        // } catch ( Exception e ) {
        // return content;
        // }

        return content;
    }

    /**
     * 按要求转码
     *
     * @param content
     * @param fromEncoding
     * @param toEncoding
     * @return
     */
    public static String fromDB(String content, String fromEncoding, String toEncoding) {
        return toDB(content, fromEncoding, toEncoding);
    }

    /**
     * 按要求将web编码转为DB编码(by wxj) 从配置文件中读取转码要求，将字符串转码。
     *
     * @param p_value
     * @return
     */
    public static String toDB(String p_value) {
        String encod1 = PropertyManager.getProperty(SystemAttributeNames.WEB_SERVER_ENCODING);
        String encod2 = PropertyManager.getProperty(SystemAttributeNames.DB_SERVER_ENCODING);
        return toDB(p_value, encod1, encod2);
    }

    /**
     * 按要求将web编码转为DB编码(by wxj) 从配置文件中读取转码要求，将字符串转码。
     *
     * @param p_value
     * @return
     */
    public static String fromDB(String p_value) {
        String encod1 = PropertyManager.getProperty(SystemAttributeNames.DB_SERVER_ENCODING);
        String encod2 = PropertyManager.getProperty(SystemAttributeNames.WEB_SERVER_ENCODING);
        return fromDB(p_value, encod1, encod2);
    }

    /**
     * 进行toSQL转换，将'转换为''
     */
    public static final String toSql(String p_str) {
        if (p_str == null || p_str.length() < 1)
            return "";
        return p_str.replaceAll("'", "''");
    }

    /**
     * 进行toHtml转换
     */
    public static final String toHtml(String p_str) {
        if (p_str == null || p_str.length() == 0)
            return "";
        p_str = p_str.replaceAll("<", "&lt;");
        p_str = p_str.replaceAll(">", "&gt;");
        p_str = p_str.replaceAll("&", "&amp;");
        p_str = p_str.replaceAll("\"", "&quot;");
        // p_str=p_str.replaceAll("\r","<br>");
        return p_str;
    }

    public static String cutZero(String v) {
        if (v.indexOf(".") > -1) {
            while (true) {
                if (v.lastIndexOf("0") == (v.length() - 1)) {
                    v = v.substring(0, v.lastIndexOf("0"));
                } else {
                    break;
                }
            }
            if (v.lastIndexOf(".") == (v.length() - 1)) {
                v = v.substring(0, v.lastIndexOf("."));
            }
        }
        return v;
    }

    /**
     * 将double转换为字符串，去掉科学计数法 至少保留二位，去掉多余零
     */
    public static String doubleToStr(double d) {
        String rtn = "";
        java.text.DecimalFormat df = new java.text.DecimalFormat("###0.000000");
        rtn = df.format(d);
        return cutZero(rtn);
    }

    public static String doubleToStr1(double d) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("###,###,###,##0.00");
        return df.format(d);
    }

    public static String intToStr(int d) {
        java.text.DecimalFormat df = new java.text.DecimalFormat("###########0");
        return df.format(d);
    }

    /**
     * 取表中某一行，某一列的数据 自己负责取连接
     */
    public static String getCellValue(String p_table, String p_field, String p_where) {
        if (p_field == null || p_table == null || p_where == null)
            return null;
        ConnectionManager manager = ConnectionManager.getInstance();
        DatabaseConnection con = manager.getConnection();
        String sql = "select " + p_field;
        sql += " from " + p_table;
        sql += " where " + p_where;
        try {
            RecordSet rs = con.executeQuery(sql);
            if (rs.next()) {
                String tmp = rs.getString(0);
                if (tmp != null)
                    return tmp.trim();
            }
            rs.close();
            return null;
        } catch (Exception ex) {
            return null;
        } finally {
            manager.releaseConnection(con);
        }
    }

    /**
     * 取表中某一行，某一列的数据 使用传进来的连接
     */
    public static String getCellValue(DatabaseConnection p_con, String p_table, String p_field, String p_where) {
        if (p_field == null || p_table == null || p_where == null)
            return null;
        String sql = "select " + p_field;
        sql += " from " + p_table;
        sql += " where " + p_where;
        try {
            RecordSet rs = p_con.executeQuery(sql);
            if (rs.next()) {
                String tmp = rs.getString(0);
                if (tmp != null)
                    return tmp.trim();
            }
            rs.close();
            return null;
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 取出汉字对应的拼音
     *
     */
    public static String getSpell(DatabaseConnection con, String chsChar) {
        String strSpell = "";
        StringBuffer sbSpell = new StringBuffer();
        try {
            // 判断汉字是否为空
            if (!chsChar.equals("")) {
                RecordSet rs = null;
                char[] arrChar = chsChar.toCharArray();
                for (int i = 0; i < arrChar.length; i++) {
                    // 如果是多音字，则取第一条即可
                    rs = con.executeQuery(" select PY from ptpyk where HZ='" + arrChar[i]
                            + "' and rownum<=1 order by py");
                    while (rs.next()) {
                        sbSpell.append(rs.getString("PY"));
                    }
                }
                rs.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        strSpell = sbSpell.toString();
        return strSpell;
    }

    public static void main(String[] args) {
        ConnectionManager manager = ConnectionManager.getInstance();
        DatabaseConnection con = manager.getConnection();
        try {
            System.out.println(getSpell(con, "王乃恭"));

        } catch (Exception e) {

        } finally {
            manager.releaseConnection(con);
        }
    }

    public static void main2(String[] args) {
        ConnectionManager manager = ConnectionManager.getInstance();
        DatabaseConnection con = manager.getConnection();
        try {
            RecordSet rs = con.executeQuery("select opername,operid from ptoper");
            while (rs.next()) {
                // System.out.println(getSpell(con, "王乃恭"));
                String spell = getSpell(con, rs.getString("opername"));
                con.executeUpdate("update ptoper set operid='" + spell + ".qd' where operid='" + rs.getString("operid")
                        + "'");
                // System.out.println(spell);
            }
        } catch (Exception e) {

        } finally {
            manager.releaseConnection(con);
        }
    }
}
