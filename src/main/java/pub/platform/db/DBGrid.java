package pub.platform.db;

import java.math.BigDecimal;
import java.lang.*;
import java.text.DecimalFormat;
import java.util.ArrayList;

import pub.platform.form.config.*;
import pub.platform.utils.Basic;

public class DBGrid {
    private String ID = "";
    private String gridType = "read";

    private boolean gridTitleVisible = true;
    private boolean gridBottomVisible = true;
    private String align = "left";// ////

    private String alignStr = "";// ////

    private String tempalign = "";// ////

    private String fieldSQL = "";// /SQL查询语句

    private String countSQL = "";// /////求记录数语句

    private String fieldCN = "";// //字段中文解释
    private String enumType = "";// /枚举类型
    private String visible = "";// /是否可见
    private String fieldName = "";// /字段名称
    private String fieldWidth = "";// //字段宽度
    private String fieldCheck = "";// ///字段输入项检查

    private String sumfield = "";// 合计字段

    private String fdwidth = "";

    private boolean checkbl = false;
    private String fieldType = "";// //字段类型
    private int pageSize = 100;// //页记录条树
    private int AbsolutePage = 1;// 绝对页数
    private int RecordCount = 0;// /记录条树
    private int TotalPage = 0;// /总页页数

    private String whereStr = "";// //查询条件

    private String[] fieldCNArr; // //字段中文解释数组
    private String[] enumTypeArr; // /枚举类型树组
    private String[] visibleArr; // /是否可见树组
    private String[] fieldNameArr; // /字段名称树组
    private String[] fieldWidthArr;// //字段宽度树组
    private String[] fieldTypeArr; // //字段类型树组
    private String[] fieldCheckArr;// ///字段输入项检查数组

    private String[] sumfieldArr;//


    // 改造的定义接口调用 ---------------------------------------20050127
    private ArrayList fieldCNArrContainer = new ArrayList(); // //字段中文解释数组容器
    private ArrayList enumTypeArrContainer = new ArrayList(); // /枚举类型树组容器
    private ArrayList visibleArrContainer = new ArrayList(); // /是否可见树组容器
    private ArrayList fieldNameArrContainer = new ArrayList(); // /字段名称树组容器
    private ArrayList fieldWidthArrContainer = new ArrayList(); // //字段宽度树组容器
    private ArrayList fieldTypeArrContainer = new ArrayList(); // //字段类型树组容器
    private ArrayList fieldCheckArrContainer = new ArrayList(); // ///字段输入项检查数组容器


    //zr   增加金额总计DBGRID BOTTOM
    private ArrayList<String> sumfieldContainer = new ArrayList<String>();


    public void setSumfield(String sumfield) {
        this.sumfield = sumfield;

        if (!sumfield.equals(""))
            sumfieldArr = sumfield.split(",", -2);
    }


    public void initSumfield(String label, String fieldsname) {
        sumfieldContainer.add(label);
        sumfieldContainer.add(fieldsname);
    }


    private String[] getStringArray(ArrayList list) {
        String[] tt = new String[list.size()];

        for (int i = 0; i < list.size(); i++) {
            tt[i] = (String) list.get(i);
        }

        return tt;
    }

    private String getStringComma(ArrayList list) {
        return getStringComma(list, ",");
    }

    private String getStringComma(ArrayList list, String comma) {
        StringBuffer temp = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i == list.size() - 1) {
                temp.append(list.get(i));
            } else {
                temp.append(list.get(i)).append(comma);
            }

        }

        // System.out.println(temp.toString());
        return temp.toString();
    }

    private void composeField() {

        if (fieldCNArrContainer.size() > 0) {
            fieldCN = getStringComma(fieldCNArrContainer);
            this.setfieldcn(fieldCN);
            // System.out.println("fieldCN"+fieldCN);
            // fieldCNArr = getStringArray(fieldCNArrContainer);
            // System.out.println("fieldCNArr");
        }
        if (enumTypeArrContainer.size() > 0) {
            enumType = getStringComma(enumTypeArrContainer);
            this.setenumType(enumType);
            // System.out.println("enumType"+enumType);
            // enumTypeArr = getStringArray(enumTypeArrContainer);
            // System.out.println("enumTypeArr");
        }
        if (visibleArrContainer.size() > 0) {
            visible = getStringComma(visibleArrContainer);
            this.setvisible(visible);
            // System.out.println("visible"+visible);
            // visibleArr = getStringArray(visibleArrContainer);
            // System.out.println("visibleArr");
        }
        if (fieldNameArrContainer.size() > 0) {
            fieldName = getStringComma(fieldNameArrContainer);
            this.setfieldName(fieldName);
            // System.out.println("fieldName"+fieldName);
            // fieldNameArr = getStringArray(fieldNameArrContainer);
            // System.out.println("fieldNameArr");
        }
        if (fieldWidthArrContainer.size() > 0) {
            fieldWidth = getStringComma(fieldWidthArrContainer);
            this.setfieldWidth(fieldWidth);
            // System.out.println("fieldWidth" + fieldWidth);
            // fieldWidthArr = getStringArray(fieldWidthArrContainer);
            // System.out.println("fieldWidthArr"+fieldWidthArrContainer.size());

        }
        if (fieldTypeArrContainer.size() > 0) {
            fieldType = getStringComma(fieldTypeArrContainer);
            this.setfieldType(fieldType);
            // System.out.println("fieldType"+fieldType);
            // fieldTypeArr = getStringArray(fieldTypeArrContainer);
            // System.out.println("fieldTypeArr");
        }
        if (fieldCheckArrContainer.size() > 0) {
            fieldCheck = getStringComma(fieldCheckArrContainer, ";");
            this.setfieldCheck(fieldCheck);
            // System.out.println("fieldCheck"+fieldCheck);

            // fieldCheckArr = getStringArray(fieldCheckArrContainer);
            // System.out.println("fieldCheckArr");
        }

        //zr
        if (sumfieldContainer.size() > 0) {
            sumfield = getStringComma(sumfieldContainer);
            this.setSumfield(sumfield);
            // System.out.println("fieldCheck"+fieldCheck);

            // fieldCheckArr = getStringArray(fieldCheckArrContainer);
            // System.out.println("fieldCheckArr");
        }

    }

    public void setField(String cnName, String type, String width, String enName, String visible, String enutype) {
        setCheckField(cnName, type, width, enName, visible, enutype, "");
    }

    public void setCheckField(String cnName, String type, String width, String enName, String visible, String enutype,
                              String checkstr) {
        if (cnName == null || type == null || width == null || enName == null
                || (visible == null || (!visible.equals("true") && !visible.equals("false"))) || enutype == null
                || checkstr == null) {
            throw new IllegalArgumentException("参数中不能为NULL！！！");
        }

        if (cnName.equals("") || type.equals("") || width.equals("") || enName.equals("")
                || (visible.equals("") || (!visible.equals("true") && !visible.equals("false"))) || enutype.equals("")) {
            throw new IllegalArgumentException("参数中不能为空！！！");
        }

        fieldCNArrContainer.add(cnName);
        fieldTypeArrContainer.add(type);
        fieldWidthArrContainer.add(width);
        fieldNameArrContainer.add(enName);
        visibleArrContainer.add(visible);
        enumTypeArrContainer.add(enutype);
        fieldCheckArrContainer.add(checkstr);
    }

    // -------------------------------------------------20050127

    private RecordSet rs;
    private int Count = 0;

    private String enumStr = "";

    private String fieldValueStr = ""; // //记录的字符串

    ConnectionManager manager; // ////连接管理

    // /////////////////////////////////////////////////////////////////////
    private String dataPilotID = ""; // ///; 数据按钮集
    private String buttons = "moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast"; // /按钮集
    private String[] buttonsArr; // /按钮数组

    // /////////////////////////////////////////////////////////////////////

    public DBGrid() {
        manager = ConnectionManager.getInstance();
    }

    public void setGridID(String gridID) {
        this.ID = gridID;
    }

    public void setGridType(String gridType) {
        this.gridType = gridType;
    }

    // ////是否显示表头
    public void setGridTitleVisible(boolean titleVisible) {
        this.gridTitleVisible = titleVisible;
    }

    // ////是否显示表未
    public void setGridBottomVisible(boolean bottomVisible) {
        this.gridBottomVisible = bottomVisible;
    }

    public void setAlign(String align) {
        this.align = align;
        if (tempalign.equals(""))
            tempalign = align;
        else
            tempalign = tempalign + "," + align;

    }

    public void setfieldSQL(String fieldSQL) {

        this.fieldSQL = fieldSQL;
    }

    public void setcountSQL(String countSQL) {

        this.countSQL = countSQL;
    }

    public void setfdwidth(String fdWidth) {

        this.fdwidth = fdWidth;
    }

    public void setfieldcn(String fieldCN) {
        this.fieldCN = fieldCN;

        if (!fieldCN.equals(""))
            fieldCNArr = fieldCN.split(",", -2);
    }

    public void setenumType(String enumType) {
        this.enumType = enumType;
        if (!enumType.equals(""))
            enumTypeArr = enumType.split(",", -2);
    }

    public void setvisible(String visible) {
        this.visible = visible;
        if (!visible.equals(""))
            visibleArr = visible.split(",", -2);
    }

    public void setfieldName(String fieldName) {
        this.fieldName = fieldName;

        if (!fieldName.equals(""))
            fieldNameArr = fieldName.split(",", -2);
    }

    public void setfieldWidth(String fieldWidth) {
        this.fieldWidth = fieldWidth;

        if (!fieldWidth.equals("")) {
            fieldWidthArr = fieldWidth.split(",", -2);

        }
    }

    public void setfieldType(String fieldType) {
        this.fieldType = fieldType;

        if (fieldType != null)
            fieldTypeArr = fieldType.split(",", -2);
    }

    public void setpagesize(int pagesize) {
        this.pageSize = pagesize;
    }

    public void setCheck(boolean checkbl) {
        this.checkbl = checkbl;
        if (this.checkbl)
            fieldWidthArrContainer.add(0, "2");
    }

    // 绝对页数
    public void setAbsolutePage(int AbsolutePage) {
        this.AbsolutePage = AbsolutePage;
    }

    // //记录行数
    public void setRecordCount(int RecordCount) {
        this.RecordCount = RecordCount;
    }

    // //查询条件
    public void setWhereStr(String WhereStr) {
        this.whereStr = WhereStr;

    }

    // /////添加数据项检查属性
    public void setfieldCheck(String fieldCheck) {
        this.fieldCheck = fieldCheck;

        if (!fieldCheck.equals(""))
            fieldCheckArr = fieldCheck.split(";", -2);
    }

    // /////////////////////////////////////////////读取数据集数性质值//////////////////////////////////////////////
    // /取得每页记录条数
    private void getpagesize() {
        if (pageSize < 0) {
            pageSize = 99999;

        }
    }

    // /取得记录条数
    private void getRecordCount() {
        if (RecordCount <= 0) {
            String SqlStr = "";
            String selectSql = "";

            // 改为嵌套获得方式，不必再设置countSQL语句了
            if (countSQL.equals("")) {
                String[] SqlStrArr = fieldSQL.split("order", -2);
                //
                // String fidName ="*";
                //
                // SqlStr ="select count("+fidName+") as reccount ";
                //
                // for (int i=1; i<SqlStrArr.length; i++ ){
                // SqlStr += " from " + SqlStrArr[i];
                // }

                // 获取记录数
                selectSql = SqlStrArr[0] + whereStr;
                selectSql = "select count(*) from (" + selectSql + ") temp";
            } else {
                selectSql = countSQL + whereStr;
            }

            // System.out.println(selectSql);
            DatabaseConnection DBCon = manager.getConnection();
            try {
                RecordSet rs = DBCon.executeQuery(selectSql);// add
                if (rs.next()) {
                    RecordCount = rs.getInt(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                manager.releaseConnection(DBCon);
                // DBCon.close();
            }

        }
    }

    // //返回总页数
    private void getTotalPage() {
        if ((RecordCount % pageSize) == 0) {
            TotalPage = RecordCount / pageSize;
        } else {
            TotalPage = RecordCount / pageSize + 1;
        }
    }

//zhanrui

    // /取得
    private String getSumFieldAmt(String fieldname) {
        String SqlStr = "";
        String selectSql = "";
        String result = " ";

        String[] SqlStrArr = fieldSQL.split("order", -2);
        // 获取记录数
        selectSql = SqlStrArr[0] + whereStr;
        selectSql = "select sum(" + fieldname + ") from (" + selectSql + ") temp";

        //System.out.println(selectSql);
        DatabaseConnection DBCon = manager.getConnection();
        try {
            RecordSet rs = DBCon.executeQuery(selectSql);// add
            if (rs.next()) {
                BigDecimal amt = new BigDecimal(rs.getDouble(0)).setScale(2, BigDecimal.ROUND_HALF_UP);
                DecimalFormat df = new DecimalFormat("##,###,###,###,###.00");
                result = df.format(amt);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            manager.releaseConnection(DBCon);
            // DBCon.close();
        }

        return result;
    }


    // //返回字段数
    private void getCount() {

        Count = fieldCNArr.length;
    }

    // //返回字段宽度数组
    private void getfieldwidth() {

        getCount();
        int count = Count;

        if (checkbl)
            count = Count + 1;

        if (fieldWidthArr == null) {

            double widthSpite = 100.0 / count; // //默认字段宽度

            fieldWidthArr = new String[count];

            for (int i = 0; i < fieldWidthArr.length; i++) {
                fieldWidthArr[i] = String.valueOf(widthSpite);
            }

        } else {
            float widthCount = 0;

            for (int i = 0; i < fieldWidthArr.length; i++) {
                if (checkbl) {
                    if (i == 0 || (i > 0 && visibleArr[i - 1].toLowerCase().equals("true")))
                        widthCount += Integer.parseInt(fieldWidthArr[i]);
                } else {
                    if (visibleArr[i].toLowerCase().equals("true"))
                        widthCount += Integer.parseInt(fieldWidthArr[i]);
                }
            }

            for (int i = 0; i < fieldWidthArr.length; i++) {
                if (checkbl) {
                    if (i == 0 || (i > 0 && visibleArr[i - 1].toLowerCase().equals("true")))
                        fieldWidthArr[i] = String.valueOf((Integer.parseInt(fieldWidthArr[i]) / widthCount) * 100);

                } else {
                    if (visibleArr[i].toLowerCase().equals("true"))
                        fieldWidthArr[i] = String.valueOf((Integer.parseInt(fieldWidthArr[i]) / widthCount) * 100);
                }
            }
        }
    }

    // /////////////////////////////////////构造数据集控件///////////////////////////////////////////////////

    // /生成可编辑的grid方法
    public String getDBGrid() {

        if (fieldNameArrContainer.size() > 0) {
            composeField();
        }

        String returnStr = "";

        returnStr = "  <TABLE  id=tblWorkArea  cellPadding=0  width=\"100%\" ><TR><TD  id=tdWorkArea vAlign=center algin=left height=\"100%\"><div   id=\"div_"
                + ID
                + "\"  antoresize=\"true\" onresize=\"body_afterResize(this)\"  >"
                + getEditDataTable()
                + "</div></td></tr></table>";

        return returnStr;
    }

    // /生成可编辑的数据表
    public String getEditDataTable() {
        if (fieldNameArrContainer.size() > 0) {
            composeField();
        }

        String returnStr = "";
        getCount();
        getfieldwidth();// /获取字段宽度
        getRecordCount();
        getTotalPage();

        String gridfieldStr = getFieldGrid();

        if (gridTitleVisible && gridBottomVisible)
            returnStr = getGridTitle() + getDataGridTitle() + gridfieldStr + getGridEnd();

        if (gridTitleVisible && !gridBottomVisible)
            returnStr = getGridTitle() + getDataGridTitle() + gridfieldStr + getTitleNOEnd();

        if (!gridTitleVisible && gridBottomVisible)
            returnStr = getGridNOTitle() + gridfieldStr + getNOTitleGridEnd();

        if (!gridTitleVisible && !gridBottomVisible)
            returnStr = getGridNOTitle() + gridfieldStr + getNOTitleNOEnd();

        return returnStr;
    }

    /*
    *
    * ///生成只读的grid方法 public String getReadDBGrid() {
    *
    * String returnStr ="";
    *
    * return returnStr; }
    *
    * ///生成只读的数据表
    *
    * public String getReadDataTable() {
    *
    * String returnStr ="";
    *
    *
    *
    * return returnStr; }
    */
    // /取得数据表格的标题
    private String getGridNOTitle() {
        try {
            StringBuffer StrBuf = new StringBuffer();

            // StrBuf.append("<form id=\"form_dbgride\" style=\"TOP:0px ;width:100%\">");
            StrBuf
                    .append("<table id=\""
                            + ID
                            + "\" class=\"gridTable\" width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" activeIndex=0  fieldname=\""
                            + fieldName + "\"  SQLStr=\"" + fieldSQL + "\" fieldtype=\"" + fieldType + "\"  fieldCN=\"" + fieldCN
                            + "\"  enumType=\"" + enumType + "\"  whereStr=\"" + Basic.encode(whereStr) + "\"  pageSize=\""
                            + pageSize + "\"  AbsolutePage=\"" + AbsolutePage + "\" TotalPage=\"" + TotalPage + "\" RecordCount=\""
                            + RecordCount + "\" visible=\"" + visible + "\" checkbl=\"" + checkbl + "\"  fieldwidth=\"" + fieldWidth
                            + "\" fieldCheck=\"" + fieldCheck + "\"  gridType=\"" + gridType + "\"  tralign=\"" + alignStr
                            + "\"  countSQL=\"" + countSQL + "\"  bottomVisible=\"" + gridBottomVisible + "\" >");

            return StrBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // /取得数据表格的标题
    private String getGridTitle() {
        try {
            StringBuffer StrBuf = new StringBuffer();
            int count = Count;

            if (checkbl)
                count = Count + 1;

            StrBuf.append("<form id=\"form_" + ID
                    + "\" style=\"TOP:0px ;width:100%;margin:0px;padding:0px\"  class=\"gridForm\">");
            // StrBuf.append("<table id=\"All_" + ID +
            // "\"   bgcolor=\"#ffffff\" class=\"borderGroove\"   width=\"100%\" border=\"0\"  cellspacing=\"0\" cellpadding=\"0\"  >");

            // StrBuf.append("<tr class=\"gridTable\" ><td colspan=\"" + (count) +
            // "\" >");

            StrBuf.append("<div  class=\"divToucss\"  id=\"divt_" + ID
                    + "\" style=\"Z-INDEX: 1; OVERFLOW: hidden; WIDTH: 100%;height:22\" >");

            StrBuf.append("<table   id=\"Title_" + ID
                    + "\"    width=\"100%\" height=22 border=\"0\"  cellspacing=\"1\" cellpadding=\"0\"  >");

            StrBuf.append("<tr  height=\"22\"  onclick = 'makeTableSortable(\"Title_" + ID + "\",\"" + ID + "\")'  >");

            int index = 0;

            if (checkbl) {
                index = 1;
                StrBuf
                        .append("<td  noWrap id=\"checkID\"   align=\"center\" vAlign=\"center\" height=\"22\" width=\""
                                + fieldWidthArr[0]
                                + "%\" ><SPAN  unselectable=\"on\" vAlign=\"center\" style=\"height:25 ; width:100% \"><input class=\"checkbox\" type=\"checkbox\" id=\"chkpar_"
                                + ID + "\"   style=\"borderStyle :none\" onclick=\"checkClick('form_" + ID
                                + "')\"></SPAN><SPAN ></SPAN></td>");
            }

            for (int i = 0; i < Count - 1; i++) {
                if (visibleArr[i].toLowerCase().equals("true"))
                    StrBuf.append("<td   noWrap  align=\"center\" vAlign=\"center\" height=\"22\" width=\""
                            + fieldWidthArr[i + index]
                            + "%\" > <SPAN  unselectable=\"on\" align=\"right\"  style=\"height:25;width:80% \" >" + fieldCNArr[i]
                            + "</SPAN><SPAN  style=\" height:25 ;width:20% \"></SPAN></td>");

            }

            if (Count - 1 >= 0)
                if (visibleArr[Count - 1].toLowerCase().equals("true"))
                    StrBuf.append("<td    noWrap  align=\"center\" vAlign=\"center\" height=\"22\" width=\""
                            + fieldWidthArr[Count - 1 + index]
                            + "%\" ><SPAN  unselectable=\"on\"align=\"right\" style=\" height:25 ;width:80% \" > "
                            + fieldCNArr[Count - 1] + "</SPAN><SPAN   style=\"height:25 ; width:20% \"></SPAN></td>");

            StrBuf.append("</tr></table></div>");
            return StrBuf.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // /取得数据表格的标题
    private String getDataGridTitle() {
        try {
            StringBuffer StrBuf = new StringBuffer();

            int count = Count;

            if (checkbl)
                count = Count + 1;

            // StrBuf.append("<tr><td colspan=\"" + (count) + "\" >");
            // edit by wuyeyuan at 2010/1/5 begin
            // StrBuf.append("<div class=\"borderGroove\"   id=\"divfd_" + ID +
            // "\" style=\"OVERFLOW: auto;WIDTH: 100%; height:350px ;background-color:#ffffff;\"  onscroll=\"body_Click();TextInit();NumberTextInit();\">");
            // edit by wuyeyuan at 2010/1/5 end

            int tbodyheightpx = this.pageSize * 22 + 2;
            StrBuf.append("<div  id=\"divfd_" + ID + "\" style=\"OVERFLOW: auto;WIDTH: 100%; height:" + tbodyheightpx
                    + "px ;\"  onscroll=\"body_Click();TextInit();NumberTextInit();\">");

            StrBuf
                    .append("<div  class=\"divTiSkin\"  onmouseover=\"this.style.cursor='hand'\"  onmouseout=\"this.style.cursor='default'\"    onscroll=\"body_Click();TextInit();NumberTextInit();\">");

            StrBuf.append("<table  id=\"" + ID
                    + "\"    width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" activeIndex=0  fieldname=\""
                    + fieldName + "\"  SQLStr=\"" + fieldSQL + "\" fieldtype=\"" + fieldType + "\"  fieldCN=\"" + fieldCN
                    + "\"  enumType=\"" + enumType + "\"  whereStr=\"" + Basic.encode(whereStr) + "\"  pageSize=\"" + pageSize
                    + "\"  AbsolutePage=\"" + AbsolutePage + "\" TotalPage=\"" + TotalPage + "\" RecordCount=\"" + RecordCount
                    + "\" visible=\"" + visible + "\" checkbl=\"" + checkbl + "\"  fieldwidth=\"" + fieldWidth
                    + "\" fieldCheck=\"" + fieldCheck + "\" gridType=\"" + gridType + "\" tralign=\"" + alignStr
                    + "\"  countSQL=\"" + countSQL + "\"  bottomVisible=\"" + gridBottomVisible + "\""
                    + "\"  sumfield=\"" + sumfield + "\"" +
                    " >");

            return StrBuf.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // ////取得主健值
    private String getKeyStr(RecordSet RS) {
        try {
            String whStr = "a";

            fieldValueStr = "";
            for (int i = 0; i < Count; i++) {
                if (enumTypeArr[i].equals("-1")) {
                    if (whStr.equals("a")) {
                        whStr = fieldNameArr[i] + "&" + fieldTypeArr[i] + "&" + getFieldValue(RS, i);
                    } else {
                        whStr += "*" + fieldNameArr[i] + "&" + fieldTypeArr[i] + "&" + getFieldValue(RS, i);
                    }
                }

                fieldValueStr += getFieldValue(RS, i) + ";";
            }

            if (whStr.equals("a"))
                return "";
            else
                return whStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // //获取字段值
    private String getFieldValue(RecordSet RS, int i) {

        // 初始化枚举信息
        try {
            EnumerationBean enumBean;

            String fieldValue = "";
            this.setAlign("left");
            if (fieldTypeArr[i].toLowerCase().equals("datetime")) {
                fieldValue = RS.getCalendar(i).toString();
                this.setAlign("center");
            } else if (fieldTypeArr[i].toLowerCase().equals("date")) {
                fieldValue = RS.getString(i);
                this.setAlign("center");
            } else if (fieldTypeArr[i].toLowerCase().equals("dropdown")) {
                fieldValue = RS.getString(i);
                enumStr = fieldValue;
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
            } else if (fieldTypeArr[i].toLowerCase().equals("sql")) {

                fieldValue = RS.getString(i);

                enumStr = "";

                if ((!enumTypeArr[i].equals("-1")) && (!enumTypeArr[i].equals("0"))) {

                    if (fieldValue != null) {
                        String sSql = enumTypeArr[i].replaceAll("\\$", fieldValue);
                        DatabaseConnection dc = ConnectionManager.getInstance().get();

                        // System.out.println("sSql"+sSql);
                        RecordSet rs1 = dc.executeQuery(sSql);
                        if (rs1.next()) {
                            enumStr = rs1.getString(0);
                        }

                        // System.out.println("enumStr"+enumStr);

                    }

                    if (enumStr == null)
                        enumStr = "";

                    // fieldValue = enumStr;
                }
            } else if (fieldTypeArr[i].toLowerCase().equals("money")) {
                double tMoney = RS.getDouble(i);
                // fieldValue = Util.getMoneyString(tMoney);
                fieldValue = DBUtil.doubleToStr1(tMoney);
                // fieldValue = RS.getString(i);
                // this.setAlign("right");
            } else if (fieldTypeArr[i].toLowerCase().equals("center")) {
                fieldValue = RS.getString(i);
                this.setAlign("center");
            } else if (fieldTypeArr[i].toLowerCase().equals("left")) {
                fieldValue = RS.getString(i);
                this.setAlign("left");
            } else if (fieldTypeArr[i].toLowerCase().equals("right")) {
                fieldValue = RS.getString(i);
                this.setAlign("right");
            } else {
                fieldValue = RS.getString(i);
            }

            if ((fieldValue == null) || (fieldValue.toLowerCase().equals("null")))
                fieldValue = "";

            if (fieldTypeArr[i].toLowerCase().equals("money") || fieldTypeArr[i].toLowerCase().equals("percent")) {
                String[] fieldValueS = fieldValue.split("\\.");
                if (fieldValueS.length == 2) {
                    if (fieldValueS[1].length() >= 2)
                        fieldValue = fieldValueS[0] + "." + fieldValueS[1].substring(0, 2);
                }
            }

            return fieldValue;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // /////////获取数据表格的可编辑cell///////////////////////////////////////////////////////
    private String getEditFieldCell(RecordSet RS, int i, int index) {
        try {
            String cellStr = "";
            String oneCheckStr = "";

            if (visibleArr[i].toLowerCase().equals("true")) {

                if (fieldCheckArr != null) {
                    String[] fieldOneCheckArr = fieldCheckArr[i].split(",", -2);
                    for (int j = 0; j < fieldOneCheckArr.length; j++) {
                        if (fieldOneCheckArr[j].trim().length() > 0) {
                            String[] fieldValueCheckArr = fieldOneCheckArr[j].split("=", -2);
                            oneCheckStr += fieldValueCheckArr[0] + "=\"" + fieldValueCheckArr[1] + "\"  ";
                        }

                    }
                }

                String value = getFieldValue(RS, i);
                // /
                // System.out.println("!!!!!!!!!!!!fieldTypeArr[i].toLowerCase()"+fieldTypeArr[i].toLowerCase());
                if (fieldTypeArr[i].toLowerCase().equals("dropdown") || fieldTypeArr[i].toLowerCase().equals("sql")) {
                    cellStr = "<td  nowrap width=\"" + fieldWidthArr[i + index] + "%\"   class=\"dt_center\"  "
                            + oneCheckStr + "oldvalue =\"" + value + "\"  fieldname=\"" + fieldNameArr[i] + "\" fieldtype=\""
                            + fieldTypeArr[i] + "\" attr=\"" + value + "\">" + enumStr + "</td>";

                } else if (fieldTypeArr[i].toLowerCase().equals("money")) {
                    cellStr = "<td  nowrap width=\"" + fieldWidthArr[i + index] + "%\"    class=\"dt_right_padding\" "
                            + oneCheckStr + "oldvalue =\"" + value + "\"  fieldname=\"" + fieldNameArr[i] + "\" fieldtype=\""
                            + fieldTypeArr[i] + "\" >" + value + "</td>";
                } else if (fieldTypeArr[i].toLowerCase().equals("number")) {
                    cellStr = "<td  nowrap width=\"" + fieldWidthArr[i + index] + "%\"    class=\"dt_right_padding\" "
                            + oneCheckStr + "oldvalue =\"" + value + "\"  fieldname=\"" + fieldNameArr[i] + "\" fieldtype=\""
                            + fieldTypeArr[i] + "\" >" + value + "</td>";
                } else if (fieldTypeArr[i].toLowerCase().equals("center")) {
                    cellStr = "<td  nowrap width=\"" + fieldWidthArr[i + index] + "%\"    class=\"dt_center\" " + oneCheckStr
                            + "oldvalue =\"" + value + "\"  fieldname=\"" + fieldNameArr[i] + "\" fieldtype=\"" + fieldTypeArr[i]
                            + "\" >" + value + "</td>";
                } else {
                    cellStr = "<td  nowrap width=\"" + fieldWidthArr[i + index] + "%\"    class=\"dt_left_padding\" "
                            + oneCheckStr + "oldvalue =\"" + value + "\"  fieldname=\"" + fieldNameArr[i] + "\" fieldtype=\""
                            + fieldTypeArr[i] + "\" >" + value + "</td>";
                }
            }
            return cellStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // /////////获取数据表格的只读cell///////////////////////////////////////////////////////
    private String getReadFieldCell(RecordSet RS, int i, int index) {
        try {
            String cellStr = "";

            if (visibleArr[i].toLowerCase().equals("true")) {
                String value = getFieldValue(RS, i);
                if (fieldTypeArr[i].toLowerCase().equals("dropdown") || fieldTypeArr[i].toLowerCase().equals("sql")) {
                    cellStr = "<td  width=\"" + fieldWidthArr[i + index] + "%\"   align =\"" + align + "\"  oldvalue =\"" + value
                            + "\"  attr=\"" + value + "\">" + enumStr + "</td>";
                } else {
                    cellStr = "<td  width=\"" + fieldWidthArr[i + index] + "%\"   align =\"" + align + "\"  oldvalue =\"" + value
                            + "\">" + value + "</td>";
                }

            }
            return cellStr;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // ///获取数据表格的row//////////////////////////////////////////////////////////////
    private String getFieldROW(RecordSet RS, int RecorderIndex) {
        try {
            String cellStr = "";
            // /////// onDblClick=\"" + ID + "_TRDbclick(this)\"
            StringBuffer StrBuf = new StringBuffer();

            if (RecorderIndex % 2 == 0) {
                StrBuf.append("<tr  class=\"gridOddRow\"  edit=false operate=\"\" whStr=\"" + getKeyStr(RS) + "\" ValueStr=\""
                        + fieldValueStr + "\"  OldValueStr=\"" + fieldValueStr
                        + "\"  height=\"25\"  OnMouseUp=\"Gride_OnMouseUp(this)\"  onclick=\"TR_click(this)\"  >");
            } else {
                StrBuf.append("<tr class=\"gridEvenRow\" edit=false operate=\"\" whStr=\"" + getKeyStr(RS) + "\" ValueStr=\""
                        + fieldValueStr + "\"   OldValueStr=\"" + fieldValueStr
                        + "\"  height=\"25\"  OnMouseUp=\"Gride_OnMouseUp(this)\"    onclick=\"TR_click(this)\" >");
            }

            int index = 0;

            if (checkbl) {
                index = 1;

                cellStr = "<td noWrap align=\"center\"   width=\"" + fieldWidthArr[0]
                        + "%\" >  <input class=\"checkbox\"  type=\"checkbox\" style=\"borderStyle :none\"  name=\"chkchild_" + ID
                        + "\"  onclick=\"checkClick('child_" + ID + "')\"></td>";
                StrBuf.append(cellStr);
            }

            // ///////////////判断数据集是否只读////////////////////////////////////////////////
            for (int i = 0; i < Count; i++) {
                // if(this.gridType.trim().trim().equals("edit"))
                StrBuf.append(getEditFieldCell(RS, i, index));
                // else
                // StrBuf.append(getReadFieldCell(RS, i, index));
            }

            StrBuf.append("</tr>");

            return StrBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    private String getColorFieldROW(RecordSet RS, int RecorderIndex) {
        try {
            String cellStr = "";

            StringBuffer StrBuf = new StringBuffer();

            if (RS.getColumnCount() - Count == 3 && RS.getString(Count + 1) != null)
                StrBuf.append("<tr style=\"color:" + RS.getString(Count + 1) + "\"  bgcolor=\"" + RS.getString(Count)
                        + "\" oldclass=\"" + RS.getString(Count) + "\"   edit=false operate=\"\" whStr=\"" + getKeyStr(RS)
                        + "\" ValueStr=\"" + fieldValueStr + "\" OldValueStr=\"" + fieldValueStr
                        + "\"  height=\"25\" OnMouseUp=\"Gride_OnMouseUp(this)\" onDblClick=\"" + ID
                        + "_TRDbclick(this)\"   onclick=\"TR_click(this)\" >");
            else
                StrBuf.append("<tr   bgcolor=\"" + RS.getString(Count) + "\" oldclass=\"" + RS.getString(Count)
                        + "\"   edit=false operate=\"\" whStr=\"" + getKeyStr(RS) + "\" ValueStr=\"" + fieldValueStr
                        + "\" OldValueStr=\"" + fieldValueStr
                        + "\"  height=\"25\" OnMouseUp=\"Gride_OnMouseUp(this)\" onDblClick=\"" + ID
                        + "_TRDbclick(this)\"   onclick=\"TR_click(this)\" >");

            int index = 0;

            if (checkbl) {
                index = 1;

                cellStr = "<td align=\"center\"   width=\"" + fieldWidthArr[0]
                        + "%\" >  <input class=\"checkbox\"  type=\"checkbox\" style=\"borderStyle :none\"  name=\"chkchild_" + ID
                        + "\"  onclick=\"checkClick('child_" + ID + "')\"></td>";
                StrBuf.append(cellStr);
            }

            // ///////////////判断数据集是否只读////////////////////////////////////////////////
            for (int i = 0; i < Count; i++) {
                StrBuf.append(getEditFieldCell(RS, i, index));
            }

            StrBuf.append("</tr>");

            return StrBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // //获取数据集
    private String getFieldGrid() {
        DatabaseConnection DBCon = manager.getConnection();

        try {

            int RecorderIndex = 0;
            String selectSql = fieldSQL + whereStr;

            // PreparedStatement ps = DBCon.getPreparedStatement(selectSql);
            // rs = DBCon.executeQuery(ps,pageSize * (AbsolutePage - 1) + 1,pageSize);

            // System.out.println("#################"+selectSql);
            RecordSet rs = DBCon.executeQuery(selectSql, pageSize * (AbsolutePage - 1) + 1, pageSize);

            int index = 0;

            if (checkbl)
                index = 1;

            StringBuffer StrBuf = new StringBuffer();

            while (rs.next()) {
                if (this.gridType.trim().trim().equals("color"))
                    StrBuf.append(getColorFieldROW(rs, RecorderIndex));
                else
                    StrBuf.append(getFieldROW(rs, RecorderIndex));

                RecorderIndex++;
                if (RecorderIndex == 1)
                    alignStr = tempalign;
            }

            /*
            * trBuf.append("<tr >");
            *
            * if (checkbl) StrBuf.append("<td width=\"" + fieldWidthArr[0] +
            * "\" ></td>");
            *
            * for(int i=0; i<Count;i++ ){
            *
            * if (visibleArr[i].toLowerCase().equals("true"))
            * StrBuf.append("<td width=\"" + fieldWidthArr[i+index] + "\" ></td>"); }
            *
            *
            * StrBuf.append("</tr>");
            */
            rs.close();

            return StrBuf.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";

        } finally {
            manager.release();
            manager.releaseConnection(DBCon);
        }

    }

    // //// /////获取数据集无表头BOTTOM

    private String getNOTitleGridEnd() {
        try {
            StringBuffer StrBuf = new StringBuffer();
            int count = Count;

            if (checkbl)
                count = Count + 1;

            StrBuf.append("<tr class=\"gridEvenRow\" >");
            StrBuf.append("<td  colspan=\"" + (count) + "\"class=\"borderGrooveT\" >");
            StrBuf.append("<div  class=\"scrollpane\"  id=\"divb_" + ID + "\" style=\"TOP:0px; height:25px \">");
            StrBuf
                    .append("<table id=\"bottom_"
                            + ID
                            + "\" width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr  class=\"gridHead\" ><td id=\"all_recordcount\" class=\"statusBarText\" width=\"30%\">总条目:"
                            + RecordCount + "</td>");

            StrBuf.append("<td width=\"30%\" align =\"right\" > <input class=\"Covert_Button\"  id=\"Covert_Button" + ID
                    + "\" type=button  hideFocus=true style=\"height: 22px;width:30px\"   value=转到  onclick='Covert_Click(\""
                    + ID + "\",\"Covert_Button" + ID + "\",\"Covert_Text" + ID + "\")'>");
            StrBuf.append("<td width=\"10%\" class=\"statusBarText\" valign=\"top\"><input id=\"Covert_Text" + ID
                    + "\" style=\"height: 20px;width:30px\"   value=" + AbsolutePage + " onKeyPress='return newturnPage1_1(\""
                    + ID + "\",\"Covert_Button" + ID + "\",\"Covert_Text" + ID + "\");' >页</td>");
            StrBuf.append("<td id=\"all_page\" class=\"statusBarText\" width=\"30%\" > 总页数:" + TotalPage + "</td>");

            StrBuf.append("</tr></table></div> </td></tr>");

            StrBuf.append("</table></form>");

            return StrBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // //// /////获取数据集无表头BOTTOM

    private String getNOTitleNOEnd() {
        try {
            StringBuffer StrBuf = new StringBuffer();

            StrBuf.append("</table></form>");

            return StrBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // //// /////获取数据集无表头BOTTOM

    private String getTitleNOEnd() {
        try {
            StringBuffer StrBuf = new StringBuffer();

            StrBuf.append("</table></div></td></tr>");

            StrBuf.append("</table></form>");

            return StrBuf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // ///获取数据集BOTTOM
    private String getGridEnd() {

        try {
            StringBuffer StrBuf = new StringBuffer();
            int count = Count;

            if (checkbl)
                count = Count + 1;

            StrBuf.append("</table>");
            StrBuf.append("</div></div>");

            StrBuf.append("<div  id=\"divb_" + ID + "\"  >");

            StrBuf
                    .append("<table width=\"30%\"  id=\"bottom_"
                            + ID
                            + "\" borer=\"1\"  cellspacing=\"1\" cellpadding=\"1\"><tr><td nowrap=\"true\" id=\"all_recordcount\" class=\"statusBarText\" width=\"10%\">总条目:"
                            + RecordCount + "</td>");
            StrBuf.append("<td id=\"all_page\" class=\"statusBarText\" width=\"10%\" nowrap=\"true\"> 总页数:" + TotalPage
                    + "</td>");
            StrBuf.append("<td width=\"6%\" align =\"right\" > <input class=\"Covert_Button\"  id=\"Covert_Button" + ID
                    + "\" type=button hideFocus=true   value=转到  onclick='Covert_Click(\"" + ID + "\",\"Covert_Button" + ID
                    + "\",\"Covert_Text" + ID + "\")'>");
            StrBuf.append("<td valign=\"top\" nowrap=\"true\" width=\"10%\" class=\"statusBarText\" ><input id=\"Covert_Text"
                    + ID + "\" tableid=\"" + ID + "\"  buttonid=\"Covert_Button" + ID
                    + "\"  style=\"height: 20px;width:60px\"   value=" + AbsolutePage + " onKeyPress='return newturnPage1_1(\""
                    + ID + "\",\"Covert_Button" + ID + "\",\"Covert_Text" + ID + "\");\' >页</td>");


/*
            //金额类总计显示  暂不用
            int listsize = sumfieldArr.length / 2;
            int k = 0;
            if (listsize > 0) {
                for (int i = 0; i < listsize; i++) {
                    StrBuf.append("<td>&nbsp");
                    StrBuf.append(sumfieldArr[k]);
                    StrBuf.append(getSumFieldAmt(sumfieldArr[k + 1]));
                    StrBuf.append("</td>");
                }
                k++;
            }

*/

            StrBuf.append("</tr></table> ");
            StrBuf.append("</div>");
            StrBuf.append("</form>");

            return StrBuf.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    // /////////////////////////////////////////////////////////////////////////////////////////////

    // ////数据按钮集ID
    public void setdataPilotID(String dataPilotID) {
        this.dataPilotID = dataPilotID;
    }

    // /////数据按钮
    public void setbuttons(String buttons) {

        if (buttons.toLowerCase().equals("readonly"))
            this.buttons = "moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast";
        else if (buttons.toLowerCase().equals("default"))
            this.buttons = "moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast,appendRecord,editRecord,deleteRecord,cancelRecord,updateRecord";
        else if (!buttons.toLowerCase().equals(""))
            this.buttons = buttons;

        if (!this.buttons.toLowerCase().equals(""))
            buttonsArr = this.buttons.split(",", -2);
    }

    public String getDataPilot() {

        this.dataPilotID = this.ID + "Buttons";

        StringBuffer StrBuf = new StringBuffer();

        StrBuf.append("<table class=\"borderDataPilot\" attrib=datapilot  id=\"" + dataPilotID + "\" tableName=\"" + ID
                + "\"  confirmdelete=true  confirmcancel=true  readonly=true border=0  cellspacing=1 cellpadding=0>");

        StrBuf.append("<tr align=\"center\">");

        for (int i = 0; i < buttonsArr.length; i++) {

            if (buttonsArr[i].equals("moveFirst")) {
                StrBuf
                        .append("<td  > <input class=\"buttonGrooveDisable\" id="
                                + dataPilotID
                                + "_moveFirst   buttontype=\"moveFirst\"   type=button hideFocus=true style=\"width:30px; font-family: Webdings\" value=\"9\"  title=\"移动到第一页记录\"   onclick=\"dataPilotButton_onclick()\"></td>");
            } else if (buttonsArr[i].equals("prevPage")) {
                StrBuf
                        .append("<td > <input class=\"buttonGrooveDisable\" id="
                                + dataPilotID
                                + "_prevPage  buttontype=\"prevPage\"   type=button  hideFocus=true style=\"width:30px; font-family: Webdings\" value=\"7\"  title=\"向前翻页\"    onclick=\"dataPilotButton_onclick()\"></td>");
            } else if (buttonsArr[i].equals("movePrev")) {
                StrBuf
                        .append("<td > <input class=\"buttonGrooveDisable\" id="
                                + dataPilotID
                                + "_movePrev   buttontype=\"movePrev\"   type=button  hideFocus=true style=\"width:30px; font-family: Webdings\" value=\"3\"  title=\"移动到上一条记录\"    onclick=\"dataPilotButton_onclick()\"></td>");
            } else if (buttonsArr[i].equals("moveNext")) {
                StrBuf
                        .append("<td> <input class=\"buttonGrooveDisable\" id="
                                + dataPilotID
                                + "_moveNext   buttontype=\"moveNext\"   type=button hideFocus=true style=\"width:30px; font-family: Webdings\" value=\"4\"  title=\"移动到下一条记录\"    onclick=\"dataPilotButton_onclick()\" ></td>");
            } else if (buttonsArr[i].equals("nextPage")) {
                StrBuf
                        .append("<td > <input class=\"buttonGrooveDisable\" id="
                                + dataPilotID
                                + "_nextPage   buttontype=\"nextPage\"   type=button  hideFocus=true style=\"width:30px; font-family: Webdings\" value=\"8\"  title=\"向后翻页\"    onclick=\"dataPilotButton_onclick()\"></td>");
            } else if (buttonsArr[i].equals("moveLast")) {
                StrBuf
                        .append("<td > <input class=\"buttonGrooveDisable\" id="
                                + dataPilotID
                                + "_moveLast   buttontype=\"moveLast\"  type=button  hideFocus=true style=\"width:30px; font-family: Webdings\" value=\":\"  title=\"移动到最后一页记录\"    onclick=\"dataPilotButton_onclick()\"></td>");
            }
            if (this.gridType.trim().trim().equals("edit")) {
                if (buttonsArr[i].equals("insertRecord")) {
                    StrBuf
                            .append("<td> <input class=\"buttonGrooveDisable\" id="
                                    + dataPilotID
                                    + "_insertRecord   buttontype=\"insertRecord\"  type=button  hideFocus=true style=\"width:45px\" value=\"插入\"  title=\"插入一条新记录\"    onclick=\"dataPilotButton_onclick()\"></td>");
                } else if (buttonsArr[i].equals("appendRecord")) {
                    StrBuf
                            .append("<td> <input class=\"buttonGrooveDisable\" id="
                                    + dataPilotID
                                    + "_appendRecord   buttontype=\"appendRecord\"  type=button  hideFocus=true style=\"width:45px\" value=\"添加\"  title=\"添加一条新记录\"    onclick=\"dataPilotButton_onclick()\"></td>");
                } else if (buttonsArr[i].equals("editRecord")) {
                    StrBuf
                            .append("<td> <input class=\"buttonGrooveDisable\" id="
                                    + dataPilotID
                                    + "_editRecord   buttontype=\"editRecord\"  type=button  hideFocus=true style=\"width:45px\" value=\"修改\"  title=\"修改当前记录\"    onclick=\"dataPilotButton_onclick()\"></td>");
                } else if (buttonsArr[i].equals("deleteRecord")) {
                    StrBuf
                            .append("<td> <input class=\"buttonGrooveDisable\" id="
                                    + dataPilotID
                                    + "_deleteRecord   buttontype=\"deleteRecord\"  type=button  hideFocus=true style=\"width:45px\" value=\"删除\"  title=\"删除当前记录\"   onclick=\"dataPilotButton_onclick()\"></td>");
                } else if (buttonsArr[i].equals("cancelRecord")) {
                    StrBuf
                            .append("<td> <input class=\"buttonGrooveDisable\" id="
                                    + dataPilotID
                                    + "_cancelRecord   buttontype=\"cancelRecord\"  type=button  hideFocus=true style=\"width:45px\" value=\"撤销\"  title=\"撤销对当前记录的修改\"    onclick=\"dataPilotButton_onclick()\"></td>");
                } else if (buttonsArr[i].equals("updateRecord")) {
                    StrBuf
                            .append("<td> <input class=\"buttonGrooveDisable\" id="
                                    + dataPilotID
                                    + "_updateRecord   buttontype=\"updateRecord\"   type=button  hideFocus=true style=\"width:45px\" value=\"确认\"  title=\"确认对当前记录的修改\"    onclick=\"dataPilotButton_onclick()\"></td>");
                }
            }
            String[] buttonArr = Basic.splite(buttonsArr[i], "=");
            if (buttonArr.length > 1) {
                if (buttonArr[1].length() > 5) {
                    StrBuf.append("<td> <input class=\"buttonGrooveDisableExcel\" id=" + dataPilotID + "_" + buttonArr[1]
                            + " buttontype=\"" + buttonArr[1]
                            + "\"   type=button  hideFocus=true style=\" fontFamily: Webdings\" value=\"" + buttonArr[0]
                            + "\"      onclick=\"dataPilotButton_onclick()\"></td>");
                } else {
                    StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_" + buttonArr[1]
                            + " buttontype=\"" + buttonArr[1]
                            + "\"   type=button  hideFocus=true style=\" fontFamily: Webdings\" value=\"" + buttonArr[0]
                            + "\"      onclick=\"dataPilotButton_onclick()\"></td>");
                }
            }

        }
        StrBuf.append("</tr></table>");
        return StrBuf.toString();

    }

    public String getDataPilotID() {
        return dataPilotID;
    }

    public String getGridID() {
        return this.ID;
    }

    public String[] getButtonsArr() {
        return buttonsArr;
    }

}
