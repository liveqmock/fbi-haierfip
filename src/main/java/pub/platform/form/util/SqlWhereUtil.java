package pub.platform.form.util;

import java.util.*;
import java.util.logging.*;

import pub.platform.form.util.datatype.*;

/**
 * 构造Where串语句
 *
 * 字段类型包括：字符-0、数字-1 日期-2
 * 操作符:=、<、>、like、<=、>=、!=、no like
 * 关系运算符:and 、or
 */

public class SqlWhereUtil {
    private Logger log = Logger.getLogger("zt.platform.form.util.SqlWhereUtil");
    public static final int DataType_Character = 0;
    public static final int DataType_Number    = 1;
    public static final int DataType_Date      = 2;
    public static final int DataType_Sql       = 3;


    private static final String Operator_No     = "";
    private static final String Operator_Equals = "=";
    private static final String Operator_Lower  = "<";
    private static final String Operator_Greater = ">";
    private static final String Operator_Like = " like ";
    private static final String Operator_Lower_Equals = "<=";
    private static final String Operator_Greater_Equals = ">=";
    private static final String Operator_Not_Equals = "!=";
    private static final String Operator_Not_Like = " not like ";
    private static final String Operator_In = " in ";

    public static final int OperatorType_No = -1;
    public static final int OperatorType_Equals = 0;
    public static final int OperatorType_Lower = 1;
    public static final int OperatorType_Greater = 2;
    public static final int OperatorType_Like = 3;
    public static final int OperatorType_Lower_Equals = 4;
    public static final int OperatorType_Greater_Equals = 5;
    public static final int OperatorType_Not_Equals = 6;
    public static final int OperatorType_Not_Like = 7;
    public static final int OperatorType_In       = 8;

    public static final String RelationOperator_And  = " and ";
    public static final String RelationOperator_Or   = " or ";
    public static final String RelationOperator_NONE = " ";

    private List sqlWheres = new ArrayList();
    /**
     * 增加Where字段
     * @param tblname
     * @param fldname
     * @param value
     * @param type - 类型(0-字符 1-数字 2-日期)
     * @param operator － 操作符 (-1-无符号,0-等号1-小于2-大于3-like4－小于等于5-大于等于6不等于7-not like)
     * @param relationOpr - 关系符(and、or)
     */
    public void addWhereField(String tblname, String fldname, String value, int datatype, int operatorType,String relationType)
    {
        if (datatype == DataType_Character) {
            if ( operatorType == SqlWhereUtil.OperatorType_Like ||
                 operatorType == SqlWhereUtil.OperatorType_Not_Like ) {
                value = "'%"+value+"%'";
            } else
                value = "'" + value + "'";
        } else if ( datatype == DataType_Date ) {
            value = "'" + toDate(value) + "'";
        } else if ( datatype == DataType_Sql ) {
            value = "("+value+")";
        }

        sqlWheres.add(new SqlWhere(tblname, fldname, operatorType, value, DataType.STRING_TYPE,relationType));
    }
    /**
     * 增加Where字段,默认字符类型
     * @param tblname
     * @param fldname
     * @param value
     * @param operator
     * @param relationOpr
     */
//    public void addWhereField(String tblname, String fldname, String value, int operatorType,String relationOpr)
//    {
//        value = "'" + value + "'";
//        sqlWheres.add(new SqlWhere(tblname, fldname, operatorType, value, DataType.STRING_TYPE,relationOpr));
//    }
    /**
     * 增加Where字段,默认字符类型
     * @param tblname
     * @param fldname
     * @param value
     * @param operator
     */
//    public void addWhereField(String tblname, String fldname, String value, int operatorType)
//    {
//        value = "'" + value + "'";
//        sqlWheres.add(new SqlWhere(tblname, fldname, operatorType, value, DataType.STRING_TYPE,RelationOperator_And));
//    }
    /**
     * 增加Where字段
     * @param tblname
     * @param fldname
     * @param value
     * @param type
     * @param operator
     */
    public void addWhereField(String tblname, String fldname, String value, int datatype, int operatorType)
    {
        if (datatype == DataType_Character) {
            if ( operatorType == SqlWhereUtil.OperatorType_Like ||
                 operatorType == SqlWhereUtil.OperatorType_Not_Like ) {
                value = "'%"+value+"%'";
            } else
                value = "'" + value + "'";
        } else if ( datatype == DataType_Date ) {
            value = "'" + toDate(value) + "'";
        } else if ( datatype == DataType_Sql ) {
            value = "("+value+")";
        }


        sqlWheres.add(new SqlWhere(tblname, fldname, operatorType, value, DataType.STRING_TYPE,RelationOperator_And));
    }
    /**
     * 增加Where字段,默认字符类型
     * @param tblname
     * @param fldname
     * @param value
     * @param relationOpr
     */
//    public void addWhereField(String tblname, String fldname, String value, String relationType)
//    {
//        value = "'" + value + "'";
//        sqlWheres.add(new SqlWhere(tblname, fldname, OperatorType_Equals, value,DataType.STRING_TYPE, relationType));
//    }
    /**
     * 增加Where字段
     * @param tblname
     * @param fldname
     * @param value
     * @param relationOpr
     * @param type
     */
//    public void addWhereField(String tblname, String fldname, String value, String relationType,int datatype)
//    {
//        if (datatype == DataType_Character) {
//            value = "'" + value + "'";
//        } else if ( datatype == DataType_Date ) {
//            value = toDate(value);
//        }
//
//
//        sqlWheres.add(new SqlWhere(tblname, fldname, OperatorType_Equals, value,DataType.STRING_TYPE, relationType));
//    }

    /**
     * 增加Where字段
     * @param tblname
     * @param fldname
     * @param type - 数字/字符/日期
     * @param value
     */
    public void addWhereField(String tblname, String fldname, String value, int datatype)
    {
        if (datatype == DataType_Character) {
            value = "'" + value + "'";
        } else if ( datatype == DataType_Date ) {
            value = "'" + toDate(value) + "'";
        } else if ( datatype == DataType_Sql ) {
            value = "("+value+")";
        }


        sqlWheres.add(new SqlWhere(tblname, fldname, OperatorType_Equals, value,
                                   DataType.STRING_TYPE, RelationOperator_And));
    }

    /**
     * 增加Where字段,默认字符类型
     * @param tblname
     * @param fldname
     * @param value
     */
    public void addWhereField(String tblname, String fldname, String value)
    {
        value = "'" + value + "'";
        sqlWheres.add(new SqlWhere(tblname, fldname, OperatorType_Equals, value,
                                   DataType.STRING_TYPE, RelationOperator_And));
    }


    public void clear() {
        sqlWheres.clear();
    }

    /**
     * 删除指定字段
     * @param tblname
     * @param fldname
     */
    public void removeWhereField(String tblname, String fldname)
    {
        SqlWhere tmpWhere = new SqlWhere(tblname, fldname, DataType.STRING_TYPE, "",
                                         DataType.STRING_TYPE);
        sqlWheres.remove(tmpWhere);
    }
    /**
     * 修改指定字段的值
     *
     * @param tblname
     * @param fldname
     * @param value
     */
    public void setWhereFld(String tblname, String fldname, String value)
    {
        SqlWhere tmpWhere = new SqlWhere(tblname, fldname, DataType.STRING_TYPE, value,
                                         DataType.STRING_TYPE);
        int index = sqlWheres.indexOf(tmpWhere);
        if (index >= 0) {
            tmpWhere = (SqlWhere) sqlWheres.get(index);
            tmpWhere.setValue(value);
        }
    }

    public String toSqlWhere(boolean hasWhere)
    {

        String sql = " " ;
        if ( hasWhere )
            sql += " where ";
        for (int i = 0; i < sqlWheres.size(); i++) {
            SqlWhere sqlWhere = (SqlWhere) sqlWheres.get(i);
            sql += sqlWhere.getTblname() + "." + sqlWhere.getFldname() ;
            sql += getOperator(sqlWhere.getOperator());
            sql += sqlWhere.getValue();
            if (i < sqlWheres.size() - 1) {
                sql += " " + sqlWhere.getRelation() + " ";
            }
        }
        log.log(Level.INFO,sql);


        if (sql.trim().endsWith("where"))
            return "";

        return sql;
    }

    private String getOperator(int operator)
    {
        switch (operator) {
            case OperatorType_No:
                return Operator_No;
            case OperatorType_Equals:
                return Operator_Equals;
            case OperatorType_Lower:
                return Operator_Lower;
            case OperatorType_Greater:
                return Operator_Greater;
            case OperatorType_Like:
                return Operator_Like;
            case OperatorType_Lower_Equals:
                return Operator_Lower_Equals;
            case OperatorType_Greater_Equals:
                return Operator_Greater_Equals;
            case OperatorType_Not_Equals:
                return Operator_Not_Equals;
            case OperatorType_Not_Like:
                return Operator_Not_Like;
            case OperatorType_In:
                return Operator_In;
            default:
                return Operator_Equals;
        }
    }
    public String toDate(String date) {
        if ( date == null || date.length() != 8 ) {
            return date;
        }
        return date.substring(0,4)+"-"+date.substring(4,6)+"-"+date.substring(6,8);
    }

    public static void main(String[] args)
    {
        SqlWhereUtil swu = new SqlWhereUtil();
//        swu.addWhereField("t1","id",SqlWhereUtil.Number_Type,"00001");
        swu.addWhereField("t1","name","wwwww",SqlWhereUtil.DataType_Character,SqlWhereUtil.OperatorType_Like,SqlWhereUtil.RelationOperator_And);
        swu.addWhereField("t2","date","20030405",SqlWhereUtil.DataType_Date,SqlWhereUtil.OperatorType_Like,SqlWhereUtil.RelationOperator_And);
        swu.addWhereField("t2","date","20030406",SqlWhereUtil.DataType_Date,SqlWhereUtil.OperatorType_Like,SqlWhereUtil.RelationOperator_And);
        swu.addWhereField("t2","ccc","select * from td",SqlWhereUtil.DataType_Sql,SqlWhereUtil.OperatorType_In,SqlWhereUtil.RelationOperator_NONE);
        swu.toSqlWhere(false);

    }
}