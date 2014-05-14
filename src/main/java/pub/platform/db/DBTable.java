package pub.platform.db;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.*;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import pub.platform.advance.utils.PropertyManager;
import pub.platform.form.config.EnumerationBean;
import pub.platform.form.config.EnumerationType;
import pub.platform.utils.Basic;
import pub.platform.utils.Util;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * <p>
 * Title:
 * </p>
 * <p>
 * Description:
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 *
 * @author not attributable
 * @version 1.0
 */

public class DBTable {

    ConnectionManager manager;

    public DBTable() {
        manager = ConnectionManager.getInstance();

    }

    public static String getFieldString(ResultSet rs) {
        String retStr = "";
        try {

            ResultSetMetaData rsmd = rs.getMetaData();

            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                if (i == 1) {
                    retStr = rsmd.getColumnName(i);
                } else {
                    retStr += "," + rsmd.getColumnName(i);
                }
            }
        } catch (SQLException sqlE) {
            System.err.println("saveDataStr():" + sqlE.getMessage());
        }
        return retStr;
    }

    public static String getDataString(ResultSet rs) {
        String retStr = "";
        try {

            int count = 0;

            while (rs.next()) {

                if (count == 0) {
                    retStr = getRecordString(rs);
                } else {
                    retStr += ";" + getRecordString(rs);

                }
                count++;
            }
        } catch (SQLException sqlE) {
            System.err.println("saveDataStr():" + sqlE.getMessage());
        }

        return retStr;
    }

    public static String getRecordString(ResultSet rs) {
        String retStr = "";
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                if (i == 1) {
                    retStr = getFieldData(rsmd.getColumnType(i), rs.getString(i));

                } else {
                    retStr += "&" + getFieldData(rsmd.getColumnType(i), rs.getString(i));

                }
            }
        } catch (SQLException sqlE) {
            System.err.println("saveDataStr1():" + sqlE.getMessage());
        }

        return retStr;
    }

    public static String getFieldData(int type, String value) {
        String retStr = " ";

        if (value != null) {
            if (type == 93) {
                retStr = Basic.encode(value.trim().substring(0, 10)).trim();
            } else {
                retStr = Basic.encode(value.trim()).trim();
            }
        }

        return retStr;
    }

    // /生成Dataset 类
    // / <param name="tableName">表名</param>
    // / <param name="ID">数据集标示</param>
    // / <param name="fieldData">要有那些项需要得到</param>
    // / <param name="fieldKey">主键</param>
    // / <param name="whereSql">查询条件</param>
    // / <param name="AbsolutePage">第几页</param>
    // / <param name="pagesize">页数</param>

    public String getDataTable(String ID, String SQLStr, String fieldtype, String fieldcn, String enumtype,
                               String visible, String fieldname, int pagesize, boolean checkbl, String whereStr, String fieldwidth) {

        String returnStr = getDataTableStr(ID, SQLStr, fieldtype, fieldcn, enumtype, visible, fieldname, pagesize, 1, 0,
                checkbl, whereStr, fieldwidth);

        returnStr = "<div  id=\"div_" + ID + "\" style=\"OVERFLOW: auto; width:100%\" >" + returnStr + "</div>";

        return returnStr;
    }

    // /生成Dataset 类
    // / <param name="tableName">表名</param>
    // / <param name="ID">数据集标示</param>
    // / <param name="fieldData">要有那些项需要得到</param>
    // / <param name="fieldKey">主键</param>
    // / <param name="whereSql">查询条件</param>
    // / <param name="AbsolutePage">第几页</param>
    // / <param name="pagesize">页数</param>

    public String getDataTableStr(String ID, String SQLStr, String fieldType, String fieldCN, String enumType,
                                  String visible, String fieldname, int pageSize, int AbsolutePage, int RecordCount, boolean checkbl,
                                  String whereStr, String fieldwidth) {

        DatabaseConnection DBCon = manager.getConnection();
        try {
            int iPagesize = 0;
            int iRecordCount = 0;
            int iTotalPage = 0;
            int count = 0;

            StringBuffer StrBuf = new StringBuffer();

            if (pageSize > 0) {
                iPagesize = pageSize;
            } else {
                iPagesize = 99999;

            }

            RecordSet rs;
            // 获取记录数
            //System.out.print(SQLStr + whereStr);
            if (RecordCount <= 0) {
                String selectSql = SQLStr + whereStr;

                //System.out.print(selectSql);

                rs = DBCon.executeQuery(selectSql);
                iRecordCount = rs.getRecordCount();
            } else {
                iRecordCount = RecordCount;

                // 计算页数
            }
            if ((iRecordCount % iPagesize) == 0) {
                iTotalPage = iRecordCount / iPagesize;
            } else {
                iTotalPage = iRecordCount / iPagesize + 1;

            }
            rs = null;

            rs = DBCon.executeQuery(SQLStr + whereStr, iPagesize * (AbsolutePage - 1) + 1, iPagesize);

            StrBuf.append("<form id='form_dbgride' style=\"TOP:0px ;width:100%\">");

            StrBuf
                    .append("<table id='Title_"
                            + ID
                            + "'  bgcolor=\"#ffffff\"    style=\"border-bottom-style:groove;border-top-style:groove;border-left-style:groove;border-right-style:groove;border-width:2\"   width='100%' border='0'  cellspacing='0' cellpadding='0'  >");

            String[] fieldcnArr = fieldCN.split(",");
            String[] fieldTypeArr = fieldType.split(",");
            String[] visibleArr = visible.split(",");
            String[] enumTypeArr = enumType.split(",");
            String[] fieldnameArr = fieldname.split(",");

            StrBuf
                    .append("<tr height='25'  style=\"border-width:2\" oldClassName=\"gridHead\"  onclick = 'makeTableSortable(\"Title_"
                            + ID + "\",\"" + ID + "\")'   class=\"gridHead\" >");

            count = fieldcnArr.length; // /字段个数

            String[] fieldwidthArr; // /字段宽度树组

            double widthSpite = 100.0 / count; // //默认字段宽度

            if (checkbl) {
                count = count + 1;

            }
            if (!fieldwidth.equals("")) {
                fieldwidthArr = fieldwidth.split(",");
            } else {
                fieldwidthArr = new String[count];
                for (int i = 0; i < fieldwidthArr.length; i++) {
                    fieldwidthArr[i] = String.valueOf(widthSpite);
                }
            }

            int index = 0;

            if (checkbl) {
                index = 1;
                StrBuf
                        .append("<td   align=\"center\" valign=\"baseline\" style=\"border-bottom-style:groove;border-right-style:groove;border-width:2\" width='"
                                + fieldwidthArr[0]
                                + "%' > <font style='color:#000000'> <input class=\"checkbox\" type='checkbox' id='chkpar'  style=\"borderStyle :none\" onclick=\"checkClick()\"></font></td>");
            }

            for (int i = 0; i < fieldcnArr.length - 1; i++) {
                if (visibleArr[i].toLowerCase().equals("true")) {
                    StrBuf
                            .append("<td   align=\"center\" valign=\"baseline\"  style=\"border-bottom-style:groove;border-right-style:groove;border-width:2\"  width='"
                                    + fieldwidthArr[i + index] + "%' > <font style='color:#000000'> " + fieldcnArr[i] + "</font></td>");
                }
            }

            if (fieldcnArr.length - 1 > 0)
                if (visibleArr[fieldcnArr.length - 1].toLowerCase().equals("true")) {
                    StrBuf
                            .append("<td   align=\"center\" valign=\"baseline\"  style=\"border-bottom-style:groove;border-width:2\"  width='"
                                    + fieldwidthArr[fieldcnArr.length - 1 + index]
                                    + "%' > <font style='color:#000000'> "
                                    + fieldcnArr[fieldcnArr.length - 1] + "</font></td>");

                }
            StrBuf.append("</tr>");

            StrBuf.append("<tr><td colspan='" + (count) + "' >");
            StrBuf.append("<div  class=\"scrollpane\"  id=\"divfd_" + ID
                    + "\" style=\"OVERFLOW: auto; width:100%;TOP:0px\" >");
            StrBuf
                    .append("<table id='"
                            + ID
                            + "' bgcolor=\"#000000\"  width='100%' border='0' cellspacing='1' cellpadding='1' activeIndex=0  fieldname=\""
                            + fieldname + "\"  SQLStr=\"" + SQLStr + "\" fieldType=\"" + fieldType + "\"  fieldCN=\"" + fieldCN
                            + "\"  enumType=\"" + enumType + "\"  whereStr=\"" + Basic.encode(whereStr) + "\"  pageSize='"
                            + iPagesize + "'  AbsolutePage='" + AbsolutePage + "' TotalPage='" + iTotalPage + "' RecordCount='"
                            + iRecordCount + "' visible='" + visible + "' checkbl='" + checkbl + "'fieldwidth='" + fieldwidth + "'>");

            // 初始化枚举信息
            // EnumerationType.init();
            EnumerationBean enumBean;

            while (rs.next()) {
                String tdStr = "";
                String whStr = "a";

                if (checkbl) {
                    tdStr = "<td align=\"center\"   width='" + fieldwidthArr[0]
                            + "%' >  <input class=\"checkbox\"  type='checkbox' style=\"borderStyle :none\"  name='chkchild'></td>";

                }
                String ValueStr = "";

                for (int j = 0; j < fieldTypeArr.length; j++) {
                    String FieldValue = rs.getString(j);

                    if (enumTypeArr[j].equals("-1")) {
                        if (whStr.equals("a")) {
                            if (fieldTypeArr[j].toLowerCase().equals("datetime")) {
                                whStr = fieldnameArr[j] + "&" + fieldTypeArr[j] + "&" + rs.getCalendar(j);
                            } else {
                                whStr = fieldnameArr[j] + "&" + fieldTypeArr[j] + "&" + FieldValue;
                            }
                        } else {
                            if (fieldTypeArr[j].toLowerCase().equals("datetime")) {
                                whStr += "*" + fieldnameArr[j] + "&" + fieldTypeArr[j] + "&" + rs.getCalendar(j);
                            } else {
                                whStr += "*" + fieldnameArr[j] + "&" + fieldTypeArr[j] + "&" + FieldValue;
                            }
                        }
                    }
                    String enumStr = "";

                    if (visibleArr[j].toLowerCase().equals("true")) {
                        if (FieldValue != null) {
                            if ((!enumTypeArr[j].equals("-1")) && (!enumTypeArr[j].equals("0"))) {
                                enumBean = EnumerationType.getEnu(enumTypeArr[j]);
                                enumStr = (String) enumBean.getValue(FieldValue.trim());
                            }

                            if (fieldTypeArr[j].toLowerCase().equals("datetime")) {
                                tdStr += "<td    width='" + fieldwidthArr[j + index] + "%'   align =\"center\" fieldname=\""
                                        + fieldnameArr[j] + "\" fieldType=\"" + fieldTypeArr[j] + "\"  onclick=\"" + ID
                                        + "_TDclick(this)\" >" + rs.getCalendar(j) + "</td>";
                            } else if (fieldTypeArr[j].toLowerCase().equals("dropdown")) {
                                tdStr += "<td  width='" + fieldwidthArr[j + index] + "%'   align =\"center\" fieldname=\""
                                        + fieldnameArr[j] + "\" fieldType=\"" + fieldTypeArr[j] + "\" onclick=\"" + ID
                                        + "_TDclick(this)\" attr=\"" + FieldValue + "\">" + enumStr + "</td>";
                            } else {
                                tdStr += "<td  width='" + fieldwidthArr[j + index] + "%'   align =\"center\" fieldname=\""
                                        + fieldnameArr[j] + "\" fieldType=\"" + fieldTypeArr[j] + "\" onclick=\"" + ID
                                        + "_TDclick(this)\" >" + FieldValue + "</td>";

                            }
                        } else {
                            tdStr += "<td   width='" + fieldwidthArr[j + index] + "%'   align =\"center\"  fieldname=\""
                                    + fieldnameArr[j] + "\" fieldType=\"" + fieldTypeArr[j] + "\"   onclick=\"" + ID
                                    + "_TDclick(this)\" ></td>";
                        }
                    }

                    if (FieldValue != null) {
                        if (fieldTypeArr[j].toLowerCase().equals("datetime")) {
                            ValueStr += rs.getCalendar(j) + ";";
                        } else {
                            ValueStr += FieldValue + ";";
                        }
                    } else {
                        ValueStr += ";";
                    }
                }

                if (count % 2 == 0) {
                    StrBuf.append("<tr class=\"gridEvenRow\" oldClassName=\"gridEvenRow\" edit=false operate='' whStr='" + whStr
                            + "' ValueStr='" + ValueStr + "'  height='25'   onDblClick=\"" + ID
                            + "_TRDbclick(this)\"  onclick=\"TR_click(this)\"  >");
                } else {
                    StrBuf.append("<tr  class=\"gridEvenRow\" oldClassName=\"gridEvenRow\" edit=false operate='' whStr='" + whStr
                            + "' ValueStr='" + ValueStr + "'  height='25'  onDblClick=\"" + ID
                            + "_TRDbclick(this)\"   onclick=\"TR_click(this)\" >");

                }
                StrBuf.append(tdStr);
                StrBuf.append("</tr>");

            }

            StrBuf.append("</table></div></td></tr>");
            StrBuf.append("<tr class=\"gridEvenRow\" >");
            StrBuf.append("<td  colspan='" + (count) + "' style=\"border-top-style:groove;border-width:2\" >");
            StrBuf
                    .append("<table width='100%' height='100%' cellspacing='0' cellpadding='0'><tr  class=\"gridHead\" ><td class=\"statusBarText\" width='30%'>总条目:"
                            + iRecordCount + "</td>");

            StrBuf
                    .append("<td width='30%' align =\"right\" > <input class=\"buttonGrooveDisable\"  id=\"Covert_Button"
                            + ID
                            + "\" type=button class=button hideFocus=true style=\"height: 20px;width:30px\"   value=转到 onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\" onclick=\"Covert_Click('"
                            + ID + "','Covert_Button" + ID + "','Covert_Text" + ID + "')\">");
            StrBuf.append("<td width='10%' class=\"statusBarText\" valign=\"top\"><input id=\"Covert_Text" + ID
                    + "\" style=\"height: 20px;width:30px\"   value=" + AbsolutePage
                    + " onKeyPress=\"onKeyPressInputInteger(this)\" >页</td>");
            StrBuf.append("<td  class=\"statusBarText\" width='30%' > 总页数:" + iTotalPage + "</td>");

            StrBuf.append("</tr></table> </td></tr>");

            StrBuf.append("</table></form>");

            return StrBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();

            return e.getMessage();
        } finally {
            manager.releaseConnection(DBCon);
            // DBCon.close();
        }

    }

    public String getExcel(String SqlStr, String whereStr) {
        DatabaseConnection DBCon = manager.getConnection();
        StringBuffer StrBuf = new StringBuffer();
        try {
            RecordSet rs = DBCon.executeQuery(SqlStr + whereStr);

            while (rs.next()) {
                for (int i = 0; i < rs.getColumnCount(); i++) {
                    StrBuf.append(rs.getString(i));
                    StrBuf.append("^^");
                }
                StrBuf.append("&&");
            }
        } finally {
            manager.releaseConnection(DBCon);

        }
        return StrBuf.toString();
    }

    public String writExcel(String SqlStr, String whereStr, String fieldCN, String fieldType) {
        DatabaseConnection DBCon = manager.getConnection();
        // sStringBuffer StrBuf = new StringBuffer();

        String savefile = "";

        try {

            String[] fieldCNArr = fieldCN.split(",", -2);
            String[] fieldTypeArr = fieldType.split(",", -2);

            String deptfillstr100 = PropertyManager.getProperty("cims");
            deptfillstr100 = new String(deptfillstr100.getBytes(), "GBK");
            String lastFile = (System.currentTimeMillis() + "").substring(0, 5);
            //System.out.println("deptfillstr100" + deptfillstr100);
            //System.out.println("lastFile" + lastFile);
            File file = new File(deptfillstr100 + "/temp");
            if (!file.exists()) {
                file.mkdir();
            }

            File f = new File(deptfillstr100 + "/temp");
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile() && files[i].exists()) {
                    //System.out.println("File" + Integer.parseInt(Util.strtoint(files[i].getName().substring(0, 5))));

                    if (Integer.parseInt(lastFile) - Integer.parseInt(Util.strtoint(files[i].getName().substring(0, 5))) > 0)
                        files[i].delete();

                }
            }

            savefile = Util.inttostr(System.currentTimeMillis() + "") + ".xls";

            WritableWorkbook book = Workbook.createWorkbook(new File(deptfillstr100 + "/temp/" + savefile));

            // 生成名为“第一页”的工作表，参数0表示这是第一页
            WritableSheet sheet = book.createSheet("Sheet1", 0);

            sheet.getSettings().setDefaultColumnWidth(15);
            sheet.getSettings().setDefaultRowHeight(400);
            // 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
            // 以及单元格内容为test
            Label label;

            for (int i = 0; i < fieldCNArr.length; i++) {
                label = new Label(i, 0, fieldCNArr[i]);

                // 将定义好的单元格添加到工作表中
                sheet.addCell(label);
            }

            /*
            * 生成一个保存数字的单元格 必须使用Number的完整包路径，否则有语法歧义 单元格位置是第二列，第一行，值为789.123
            */

            int reindex = 1;

            jxl.write.Number numberCell;
            RecordSet rs = DBCon.executeQuery(SqlStr + whereStr);
            while (rs.next()) {
                for (int i = 0; i < rs.getColumnCount(); i++) {
                    if (fieldTypeArr[i].toLowerCase().equals("number")) {
                        numberCell = new jxl.write.Number(i, reindex, rs.getDouble(i));
                        sheet.addCell(numberCell);
                    } else {
                        label = new Label(i, reindex, rs.getString(i));
                        sheet.addCell(label);
                    }
                }

                reindex++;

            }

            // 写入数据并关闭文件
            book.write();
            book.close();
            return "/temp/" + savefile;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";

        } finally {
            manager.releaseConnection(DBCon);

        }

    }

    //zhan 2010/7 修改
    public String writeExcel_new(WritableWorkbook book, String SqlStr, String whereStr, String fieldCN, String fieldType,String fieldWidth,String visible, String enumType) {
        DatabaseConnection DBCon = manager.getConnection();

        try {

            String[] fieldCNArr = fieldCN.split(",", -2);
            String[] fieldTypeArr = fieldType.split(",", -2);
            String[] fieldWidthArr = fieldWidth.split(",", -2);
            String[] visibleArr = visible.split(",", -2);
            String[] enumTypeArr = enumType.split(",", -2);


            // 生成名为“第一页”的工作表，参数0表示这是第一页
            WritableSheet sheet = book.createSheet("Sheet1", 0);

            sheet.getSettings().setDefaultColumnWidth(15);
            sheet.getSettings().setDefaultRowHeight(300);
            // 在Label对象的构造子中指名单元格位置是第一列第一行(0,0)
            // 以及单元格内容为test
            Label label;

            // 标题
            WritableCellFormat wcf_title = new WritableCellFormat(new WritableFont(WritableFont.COURIER, 11, WritableFont.BOLD, false, UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.WHITE));
            wcf_title.setBorder(Border.ALL, BorderLineStyle.THIN);
            wcf_title.setAlignment(Alignment.CENTRE);
            wcf_title.setVerticalAlignment(VerticalAlignment.CENTRE);
            wcf_title.setBackground(jxl.format.Colour.BLUE_GREY);
            sheet.setRowView(1, 400, false);
            sheet.setColumnView(0, 5);

            for (int i = 0; i < fieldCNArr.length; i++) {
                //隐藏字段不输出
                if (visibleArr[i].toLowerCase().equals("false")) {
                    continue;
                }

                label = new Label(i + 1, 1, fieldCNArr[i], wcf_title);
                // 将定义好的单元格添加到工作表中
                sheet.addCell(label);
                sheet.setColumnView(i + 1, Integer.parseInt(fieldWidthArr[i]) *2  );

            }

            /*
            * 生成一个保存数字的单元格 必须使用Number的完整包路径，否则有语法歧义 单元格位置是第二列，第一行，值为789.123
            */


            //money
            NumberFormat nf_money = new NumberFormat("#,###,###,##0.00");
            WritableCellFormat wcf_money = new WritableCellFormat(nf_money);
            wcf_money.setFont(new WritableFont(WritableFont.COURIER, 11, WritableFont.NO_BOLD, false));
            // 边框样式
            wcf_money.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            // 折行
            wcf_money.setWrap(false);
            // 垂直对齐
            wcf_money.setVerticalAlignment(VerticalAlignment.CENTRE);
            // 水平对齐
            wcf_money.setAlignment(Alignment.RIGHT);


            //number
//            NumberFormat nf = new NumberFormat("#,###,###,###.##");
//            WritableCellFormat wcf_number = new WritableCellFormat(nf);
            WritableCellFormat wcf_number = new WritableCellFormat();
            wcf_number.setFont(new WritableFont(WritableFont.COURIER, 11, WritableFont.NO_BOLD, false));
            // 边框样式
            wcf_number.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            // 折行
            wcf_number.setWrap(false);
            // 垂直对齐
            wcf_number.setVerticalAlignment(VerticalAlignment.CENTRE);
            // 水平对齐
            wcf_number.setAlignment(Alignment.RIGHT);

            //text:align left
            WritableCellFormat wcf_text_left = new WritableCellFormat();
            wcf_text_left.setFont(new WritableFont(WritableFont.COURIER, 11, WritableFont.NO_BOLD, false));
            // 边框样式
            wcf_text_left.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            // 折行
            wcf_text_left.setWrap(true);
            // 垂直对齐
            wcf_text_left.setVerticalAlignment(VerticalAlignment.CENTRE);
            // 水平对齐
            wcf_text_left.setAlignment(Alignment.LEFT);

            //text:align center
            WritableCellFormat wcf_text_center = new WritableCellFormat();
            wcf_text_center.setFont(new WritableFont(WritableFont.COURIER, 11, WritableFont.NO_BOLD, false));
            // 边框样式
            wcf_text_center.setBorder(jxl.format.Border.ALL, jxl.format.BorderLineStyle.THIN);
            // 折行
            wcf_text_center.setWrap(true);
            // 垂直对齐
            wcf_text_center.setVerticalAlignment(VerticalAlignment.CENTRE);
            // 水平对齐
            wcf_text_center.setAlignment(Alignment.CENTRE);


            //明细数据输出
            int reindex = 1;

            jxl.write.Number numberCell;
            RecordSet rs = DBCon.executeQuery(SqlStr + whereStr);
            EnumerationBean enumBean;

            while (rs.next()) {
                int columnCount = rs.getColumnCount();
                for (int i = 0; i < columnCount; i++) {

                    //隐藏字段不输出
                    if (visibleArr[i].toLowerCase().equals("false")) {
                        continue;
                    }

                    if (fieldTypeArr[i].toLowerCase().equals("number")) {
                        numberCell = new jxl.write.Number(i + 1, reindex + 1, rs.getDouble(i), wcf_number);
                        sheet.addCell(numberCell);
                    } else if (fieldTypeArr[i].toLowerCase().equals("money")) {
                        numberCell = new jxl.write.Number(i + 1, reindex + 1, rs.getDouble(i), wcf_money);
                        sheet.addCell(numberCell);
                    } else if (fieldTypeArr[i].toLowerCase().equals("center")) {
                        label = new Label(i + 1, reindex + 1, rs.getString(i), wcf_text_center);
                        sheet.addCell(label);
                    } else if (fieldTypeArr[i].toLowerCase().equals("dropdown")) {
                        String fieldValue = rs.getString(i);
                        String enumStr = fieldValue;
                        if ((!enumTypeArr[i].equals("-1")) && (!enumTypeArr[i].equals("0"))) {
                            enumStr = "";
                            if (fieldValue != null) {
                                enumBean = EnumerationType.getEnu(enumTypeArr[i]);
                                if (enumBean != null) {
                                    enumStr = ((String) enumBean.getValue(fieldValue.trim())).split(";")[0];
                                }
                            }

                            if (enumStr == null)
                                enumStr = "";
                        }

                        label = new Label(i + 1, reindex + 1, enumStr, wcf_text_center);
//                        label = new Label(i + 1, reindex + 1, rs.getString(i),wcf_text_center);
                        sheet.addCell(label);
                    } else {
                        label = new Label(i + 1, reindex + 1, rs.getString(i), wcf_text_left);
                        sheet.addCell(label);
                    }
                }

                reindex++;

            }

            // 写入数据并关闭文件
//         book.write();
//         book.close();
//         return "/temp/" + savefile;
           return "ok";

        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";

        } finally {
            manager.releaseConnection(DBCon);

        }

    }


    // /生成getDropDownStr 类
    // / <param name="SqlStr">查询语句</param>
    // / <param name="fieldTitle">标题</param>

    public String getDropDownStr(String SqlStr, String fieldTitle) {

        DatabaseConnection DBCon = manager.getConnection();
        StringBuffer StrBuf = new StringBuffer();
        try {
            int iPagesize = 0;
            int iRecordCount, iTotalPage;
            int count = 0;

            RecordSet rs;
            //System.out.print(SqlStr);
            rs = DBCon.executeQuery(SqlStr);

            String[] fieldTitlearr = fieldTitle.split(",");

            StrBuf
                    .append("<table id='Data_dropDown' class=\"dropDownTable\"  width='100%' border='0'  cellspacing='1' cellpadding='1' >");

            StrBuf.append("<tr height='20'   class=\"dropDownHead\" >");

            for (int i = 0; i < fieldTitlearr.length; i++) {
                StrBuf.append("<td align=\"center\">" + fieldTitlearr[i] + "</td></td> ");
            }
            StrBuf.append("</tr>");
            count = 0;

            while (rs.next()) {

                if (count % 2 == 0) {
                    StrBuf.append("<tr  onclick=\"TRClick(this)\" height='20' class=\"gridEvenRow\">");
                } else {
                    StrBuf.append("<tr  onclick=\"TRClick(this)\" height='20' class=\"gridOddRow\">");

                }
                for (int j = 0; j < fieldTitlearr.length; j++) {

                    String FieldValue = rs.getString(j);

                    if (FieldValue != null) {

                        StrBuf.append("<td >" + FieldValue + "</td>");
                    } else {
                        StrBuf.append("<td  ></td>");

                    }
                }
                StrBuf.append("</tr>");

                count++;
            }

            StrBuf.append("</table>");
        } catch (Exception e) {
            e.printStackTrace();

            return e.getMessage();
        } finally {
            manager.releaseConnection(DBCon);
            // DBCon.close();
        }

        return StrBuf.toString();
    }

    // /生成getDropDownStr 类
    // / <param name="SqlStr">查询语句</param>
    // / <param name="fieldTitle">标题</param>

    public String getsqlDataStr(String SqlStr) {
        StringBuffer StrBuf = new StringBuffer();

        RecordSet rs;
        DatabaseConnection DBCon = manager.getConnection();
        try {
            rs = DBCon.executeQuery(SqlStr);
            StrBuf.append("<table  >");

            while (rs.next()) {

                StrBuf.append("<tr >");

                for (int i = 0; i <= rs.getfieldCount(); i++) {

                    String retStr = rs.getMetaData(i).getName();

                    String FieldValue = rs.getString(i);

                    if (FieldValue != null) {
                        StrBuf.append("<td  align =\"center\" fieldname='" + retStr + "'  FieldValue='" + FieldValue + "'></td>");

                    } else {
                        StrBuf.append("<td  align =\"center\" fieldname='" + retStr + "'  FieldValue=''></td>");
                    }

                }

                StrBuf.append("</tr>");
            }

            StrBuf.append("</table>");
        } catch (Exception e) {
            e.printStackTrace();

            return e.getMessage();
        } finally {
            manager.releaseConnection(DBCon);
            // DBCon.close();
        }

        return StrBuf.toString();

    }

    // /生成SaveDateStr 类
    // / <param name="SqlStr">y</param>
    // / <param name="fieldTitle">标题</param>

    public boolean SaveDateStr(String SqlStr) {
        boolean retbool = false;
        DatabaseConnection DBCon = manager.getConnection();
        try {
            if (DBCon.executeUpdate(SqlStr) > 0) {
                retbool = true;

            }
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            manager.releaseConnection(DBCon);
            // DBCon.close();
        }

        return retbool;
    }

    // /查询数据方法
    // / <param name="SqlStr">y</param>
    // / <param name="fieldTitle">标题</param>

    public RecordSet queryData(String SqlStr) {
        RecordSet rs;

        ConnectionManager cm = ConnectionManager.getInstance();
        DatabaseConnection dc = cm.getConnection();

        rs = dc.executeQuery(SqlStr);

        return rs;
    }

    // /生成DataPilot 类
    // / <param name="tableName">表名</param>
    // / <param name="ID">数据集标示</param>

    public String getDataPilot(String ID, String tableName, String buttons) {

        StringBuffer StrBuf = new StringBuffer();
        StrBuf.append("<table class=\"bordergrov\" attrib=datapilot  id=\"" + ID + "\" tableName=\"" + tableName
                + "\"  confirmdelete=true  confirmcancel=true  readonly=true border=0  cellspacing=0 cellpadding=0>");

        if (buttons.equals("default")) {
            buttons = "moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast,appendRecord,editRecord,deleteRecord,cancelRecord,updateRecord";

        }
        if (buttons.equals("readonly")) {
            buttons = "moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast";

        }

        String[] buttonsArr = Basic.splite(buttons, ",");

        StrBuf.append("<tr align=\"center\">");

        for (int i = 0; i < buttonsArr.length; i++) {
            if (buttonsArr[i].equals("moveFirst")) {
                StrBuf
                        .append("<td  > <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_moveFirst   buttontype=\"moveFirst\"   type=button hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"9\"  title=\"移动到第一条记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("prevPage")) {
                StrBuf
                        .append("<td > <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_prevPage  buttontype=\"prevPage\"   type=button  hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"7\"  title=\"向前翻页\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("movePrev")) {
                StrBuf
                        .append("<td > <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_movePrev   buttontype=\"movePrev\"   type=button  hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"3\"  title=\"移动到上一条记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("moveNext")) {
                StrBuf
                        .append("<td> <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_moveNext   buttontype=\"moveNext\"   type=button hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"4\"  title=\"移动到下一条记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\" ></td>");
            } else if (buttonsArr[i].equals("nextPage")) {
                StrBuf
                        .append("<td > <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_nextPage   buttontype=\"nextPage\"   type=button  hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"8\"  title=\"向后翻页\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("moveLast")) {
                StrBuf
                        .append("<td > <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_moveLast   buttontype=\"moveLast\"  type=button  hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\":\"  title=\"移动到最后一条记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("insertRecord")) {
                StrBuf
                        .append("<td> <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_insertRecord   buttontype=\"insertRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"插入\"  title=\"插入一条新记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("appendRecord")) {
                StrBuf
                        .append("<td> <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_appendRecord   buttontype=\"appendRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"添加\"  title=\"添加一条新记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("editRecord")) {
                StrBuf
                        .append("<td> <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_editRecord   buttontype=\"editRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"修改\"  title=\"修改当前记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("deleteRecord")) {
                StrBuf
                        .append("<td> <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_deleteRecord   buttontype=\"deleteRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"删除\"  title=\"删除当前记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("cancelRecord")) {
                StrBuf
                        .append("<td> <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_cancelRecord   buttontype=\"cancelRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"撤销\"  title=\"撤销对当前记录的修改\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else if (buttonsArr[i].equals("updateRecord")) {
                StrBuf
                        .append("<td> <input class=\"buttonGrooveDisable\" id="
                                + ID
                                + "_updateRecord   buttontype=\"updateRecord\"   type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"确认\"  title=\"确认对当前记录的修改\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\""
                                + ID + "_onclick()\"></td>");
            } else {

                String[] buttonArr = Basic.splite(buttonsArr[i], "=");
                if (buttonArr.length > 1) {
                    StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + ID + "_" + buttonArr[1] + " buttontype=\""
                            + buttonArr[1] + "\"   type=button  hideFocus=true style=\"height: 22px; fontFamily: Webdings\" value=\""
                            + buttonArr[0]
                            + "\"    onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"" + ID
                            + "_onclick()\"></td>");

                }
            }
        }
        StrBuf.append("</tr></table>");
        return StrBuf.toString();

    }

}
