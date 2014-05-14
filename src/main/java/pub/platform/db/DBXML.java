package pub.platform.db;

import jxl.write.WritableWorkbook;
import org.jdom.*;
import org.jdom.input.SAXBuilder;

import pub.platform.form.config.*;
import pub.platform.html.*;
import pub.platform.utils.*;

import java.io.*;
import java.lang.*;
import java.util.List;

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

public class DBXML {
    public DBXML() {
    }

    public String getDataTableXML(String xmlStr) {
        String outstr = "";
        Document doc;
        Element rootNode, child;

        ConnectionManager cm = ConnectionManager.getInstance();

        try {
            cm.get();

            Reader reader = new StringReader(xmlStr);
            SAXBuilder ss = new SAXBuilder();
            doc = ss.build(reader);

            rootNode = doc.getRootElement();

            List list = rootNode.getChildren();

            Element childRoot = (Element) list.get(0);

            DBGrid dbGrid = new DBGrid();
            dbGrid.setGridID(childRoot.getAttributeValue("id"));
            dbGrid.setGridType(childRoot.getAttributeValue("gridType"));
            dbGrid.setfieldSQL(childRoot.getAttributeValue("SQLStr"));
            dbGrid.setfieldcn(childRoot.getAttributeValue("fieldCN"));
            dbGrid.setenumType(childRoot.getAttributeValue("enumType"));
            dbGrid.setvisible(childRoot.getAttributeValue("visible"));
            dbGrid.setfieldName(childRoot.getAttributeValue("fieldname"));
            dbGrid.setfieldWidth(childRoot.getAttributeValue("fieldwidth"));
            dbGrid.setfieldType(childRoot.getAttributeValue("fieldtype"));
            dbGrid.setfieldCheck(childRoot.getAttributeValue("fieldCheck"));
            dbGrid.setcountSQL(childRoot.getAttributeValue("countSQL"));

            dbGrid.setpagesize(Integer.parseInt(childRoot.getAttributeValue("pageSize")));
            dbGrid.setAbsolutePage(Integer.parseInt(childRoot.getAttributeValue("AbsolutePage")));
            dbGrid.setRecordCount(Integer.parseInt(childRoot.getAttributeValue("RecordCount")));
            dbGrid.setCheck(childRoot.getAttributeValue("checkbl").toLowerCase().trim().equals("true"));
            dbGrid.setAlign(childRoot.getAttributeValue("tralign"));

/*
      if (childRoot.getAttributeValue("bottomVisible").toLowerCase().equals("true"))
        dbGrid.setGridBottomVisible(true);
      else
        dbGrid.setGridBottomVisible(false);
*/
        //zr
      if (childRoot.getAttributeValue("bottomVisible").toLowerCase().equals("true")){
            dbGrid.setGridBottomVisible(true);
//            dbGrid.setSumfield(childRoot.getAttributeValue("sumfield"));
      }else
        dbGrid.setGridBottomVisible(false);

            dbGrid.setWhereStr(Basic.decode(childRoot.getAttributeValue("whereStr")));

            outstr = dbGrid.getEditDataTable();
            // System.out.println(outstr);

        } catch (JDOMException ex) {
            System.out.print(ex.getMessage());

        } finally {
            cm.release();
        }
        return outstr;
    }

    public String getUserSerial(String operID) {

        ConnectionManager cm = ConnectionManager.getInstance();

        String userSerial = Util.getUserSerial(cm.getConnection(), operID);

        StringBuffer StrBuf = new StringBuffer();

        StrBuf.append("<root>");
        StrBuf.append("<action type=\"1\" result=\"true\">");

        StrBuf.append("<record> ");

        StrBuf.append("<field ");
        StrBuf.append(" name =\"userserial\"");
        StrBuf.append(" type =\"text\"");
        StrBuf.append("  value =\"" + userSerial + "\"");
        StrBuf.append(" />");
        StrBuf.append("</record>");
        StrBuf.append("</action>");

        StrBuf.append("</root>");

        cm.release();
        return StrBuf.toString();

    }

    public String getDropDownXML(String xmlStr) {
        String outstr = "";
        Document doc;
        Element rootNode, child;
        // System.out.println(xmlStr);

        try {
            Reader reader = new StringReader(xmlStr);
            SAXBuilder ss = new SAXBuilder();
            doc = ss.build(reader);

            rootNode = doc.getRootElement();

            if (rootNode.getAttributeValue("type").equals("lp")) {
                return getloadspell(rootNode.getText());
            }

            if (rootNode.getAttributeValue("type").equals("lm")) {
                return getLoadName(rootNode.getText());
            }

            List list = rootNode.getChildren();

            Element childRoot = (Element) list.get(0);
            String fieldTitle = childRoot.getAttributeValue("fieldTitle");

            if (rootNode.getAttributeValue("type").equals("enum")) {
                String enumType = childRoot.getAttributeValue("enumType");

                return getEnumType(enumType, fieldTitle);

            }
            if (rootNode.getAttributeValue("type").equals("sql")) {
                String SqlStr = childRoot.getAttributeValue("SqlStr");
                DBTable datatable = new DBTable();
                return datatable.getDropDownStr(SqlStr, fieldTitle);

            }

            if (rootNode.getAttributeValue("type").equals("excel")) {
                String SqlStr = childRoot.getAttributeValue("SQLStr");
                //System.out.println(SqlStr);

                String whereStr = Basic.decode(childRoot.getAttributeValue("whereStr"));
                //System.out.println(whereStr);
                DBTable datatable = new DBTable();
                return datatable.getExcel(SqlStr, whereStr);

            }

            if (rootNode.getAttributeValue("type").equals("excelhou")) {
                String SqlStr = childRoot.getAttributeValue("SQLStr");
                String fieldCN = childRoot.getAttributeValue("fieldCN");
                String fieldType = childRoot.getAttributeValue("fieldType");
                String whereStr = Basic.decode(childRoot.getAttributeValue("whereStr"));

                DBTable datatable = new DBTable();
                return datatable.writExcel(SqlStr, whereStr, fieldCN, fieldType);

            }
            if (rootNode.getAttributeValue("type").equals("comboBox")) {

                String comboBoxID = childRoot.getAttributeValue("comboBoxID");

                JOption jOption = new JOption(comboBoxID);

                String enumType = childRoot.getAttributeValue("enumType");
                String titleStr = childRoot.getAttributeValue("titleStr");
                String sqlStr = childRoot.getAttributeValue("sqlStr");
                String defaultoption = childRoot.getAttributeValue("defaultOption");

                String titleVisible = childRoot.getAttributeValue("titleVisible");
                String keyVisible = childRoot.getAttributeValue("keyVisible");

                if (defaultoption != null) {
                    String[] options = defaultoption.split(",", -2);

                    if (options.length == 2) {
                        jOption.addOption(options[0], options[1]);
                    }
                }
                if (titleVisible.toLowerCase().trim().equals("false"))
                    jOption.setTitleVisible(false);
                if (keyVisible.toLowerCase().trim().equals("false"))
                    jOption.setKeyVisible(false);

                jOption.setEnumType(enumType);
                jOption.setSQlStr(sqlStr);
                jOption.setTitleStr(titleStr);
                return jOption.getDropHtml();

            }

        } catch (JDOMException ex) {
            System.out.print(ex.getMessage());

        }

        return outstr;
    }

    public String doExcelHou(String xmlStr, WritableWorkbook wwb) {
        String outstr = "";
        Document doc;
        Element rootNode, child;
        // System.out.println(xmlStr);

        try {
            Reader reader = new StringReader(xmlStr);
            SAXBuilder ss = new SAXBuilder();
            doc = ss.build(reader);

            rootNode = doc.getRootElement();

            if (rootNode.getAttributeValue("type").equals("lp")) {
                return getloadspell(rootNode.getText());
            }

            if (rootNode.getAttributeValue("type").equals("lm")) {
                return getLoadName(rootNode.getText());
            }

            List list = rootNode.getChildren();

            Element childRoot = (Element) list.get(0);
            String fieldTitle = childRoot.getAttributeValue("fieldTitle");

            if (rootNode.getAttributeValue("type").equals("enum")) {
                String enumType = childRoot.getAttributeValue("enumType");

                return getEnumType(enumType, fieldTitle);

            }
            if (rootNode.getAttributeValue("type").equals("sql")) {
                String SqlStr = childRoot.getAttributeValue("SqlStr");
                DBTable datatable = new DBTable();
                return datatable.getDropDownStr(SqlStr, fieldTitle);

            }

            if (rootNode.getAttributeValue("type").equals("excel")) {
                String SqlStr = childRoot.getAttributeValue("SQLStr");
                //System.out.println(SqlStr);

                String whereStr = Basic.decode(childRoot.getAttributeValue("whereStr"));
                //System.out.println(whereStr);
                DBTable datatable = new DBTable();
                return datatable.getExcel(SqlStr, whereStr);

            }

            //!
            if (rootNode.getAttributeValue("type").equals("excelhou")) {
                String SqlStr = childRoot.getAttributeValue("SQLStr");
                String fieldCN = childRoot.getAttributeValue("fieldCN");
                String fieldType = childRoot.getAttributeValue("fieldType");
                String fieldWidth = childRoot.getAttributeValue("fieldWidth");
                String visible = childRoot.getAttributeValue("visible");
                String enumType = childRoot.getAttributeValue("enumType");
                String whereStr = Basic.decode(childRoot.getAttributeValue("whereStr"));

                DBTable datatable = new DBTable();
                return datatable.writeExcel_new(wwb, SqlStr, whereStr, fieldCN, fieldType,fieldWidth,visible,enumType);

            }
            if (rootNode.getAttributeValue("type").equals("comboBox")) {

                String comboBoxID = childRoot.getAttributeValue("comboBoxID");

                JOption jOption = new JOption(comboBoxID);

                String enumType = childRoot.getAttributeValue("enumType");
                String titleStr = childRoot.getAttributeValue("titleStr");
                String sqlStr = childRoot.getAttributeValue("sqlStr");
                String defaultoption = childRoot.getAttributeValue("defaultOption");

                String titleVisible = childRoot.getAttributeValue("titleVisible");
                String keyVisible = childRoot.getAttributeValue("keyVisible");

                if (defaultoption != null) {
                    String[] options = defaultoption.split(",", -2);

                    if (options.length == 2) {
                        jOption.addOption(options[0], options[1]);
                    }
                }
                if (titleVisible.toLowerCase().trim().equals("false"))
                    jOption.setTitleVisible(false);
                if (keyVisible.toLowerCase().trim().equals("false"))
                    jOption.setKeyVisible(false);

                jOption.setEnumType(enumType);
                jOption.setSQlStr(sqlStr);
                jOption.setTitleStr(titleStr);
                return jOption.getDropHtml();

            }

        } catch (JDOMException ex) {
            System.out.print(ex.getMessage());

        }

        return outstr;
    }


    // //获取路拼信息

    public String getloadspell(String words) {
        return "";
    }

    // ///获取枚举中信息

    public String getEnumType(String enumType, String fieldTitle) {

        try {
            EnumerationBean enumBean = EnumerationType.getEnu(enumType);

            // Iterator keys=enumBean.getEnu().keySet().iterator();
            Object[] keys = enumBean.getKeys().toArray();

            StringBuffer StrBuf = new StringBuffer();

            String[] fieldTitlearr = fieldTitle.split(",");

            StrBuf
                    .append("<table id='Data_dropDown' class=\"dropDownTable\"  width='100%' border='0'  cellspacing='1' cellpadding='1' >");

            StrBuf.append("<tr height='20'   class=\"dropDownHead\" >");

            for (int i = 0; i < fieldTitlearr.length; i++) {
                StrBuf.append("<td align=\"center\">" + fieldTitlearr[i] + "</td> ");
            }
            StrBuf.append("</tr>");

            for (int i = 0; i < keys.length; i++) {
                Object key = keys[i];

                StrBuf.append("<tr  onclick=\"TRClick(this)\" height='20' class=\"gridEvenRow\">");
                String[] enumStr = ((String) enumBean.getValue(key)).split(";");
                StrBuf.append("<td  align =\"left\" >" + key.toString() + "</td>");
                StrBuf.append("<td  align =\"left\" >" + enumStr[0] + "</td>");
                StrBuf.append("</tr>");
            }
            StrBuf.append("</table>");

            return StrBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    // ////过滤路名

    public String getLoadName(String spells) {
        return "";
    }

    public String SqlExcute(String xmlStr) {
        String outstr = "false";
        Document doc;
        Element rootNode;
        String intStr = Basic.decode(xmlStr);

        try {
            Reader reader = new StringReader(intStr);
            SAXBuilder ss = new SAXBuilder();
            doc = ss.build(reader);

            rootNode = doc.getRootElement();

            List list = rootNode.getChildren();

            DBTable datatable = new DBTable();

            for (int i = 0; i < list.size(); i++) {
                Element childRoot = (Element) list.get(i);
                //System.out.print(childRoot.getText());
                outstr = String.valueOf(datatable.SaveDateStr(childRoot.getText()));
            }

        } catch (JDOMException ex) {
            System.out.print(ex.getMessage());

        }
        return outstr;
    }

    public String QuerySQL(String xmlStr) {
        String outstr = "";
        Document doc;
        Element rootNode;
        String intStr = Basic.decode(xmlStr);

        try {
            Reader reader = new StringReader(intStr);
            SAXBuilder ss = new SAXBuilder();
            doc = ss.build(reader);

            rootNode = doc.getRootElement();

            String SqlStr = "select " + rootNode.getAttributeValue("fieldStr");
            SqlStr = SqlStr + "  from  " + rootNode.getAttributeValue("tableStr");
            SqlStr = SqlStr + "  where (1=1) " + rootNode.getAttributeValue("whereStr");

            DBTable datatable = new DBTable();

            RecordSet rs = datatable.queryData(SqlStr);
            String[] fieldArr = rootNode.getAttributeValue("fieldStr").split(",");

            outstr = "<queryDataS success=\"true\">";
            while (rs.next()) {
                outstr = outstr + "<queryData";
                for (int i = 0; i < fieldArr.length; i++) {
                    outstr = outstr + "  " + fieldArr[i].trim() + "=\"" + rs.getString(fieldArr[i].trim()) + "\"";
                }
                outstr = outstr + "/>";
            }
            outstr = outstr + "</queryDataS>";

        } catch (JDOMException ex) {
            outstr = "<queryDataS success=false>";
            outstr = outstr + ex.getMessage() + "</queryDataS>";

        }
        return Basic.encode(outstr);
    }

}
