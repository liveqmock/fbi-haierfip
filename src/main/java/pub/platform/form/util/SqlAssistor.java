package pub.platform.form.util;

import java.util.*;

import java.util.logging.Logger;
import java.util.logging.Level;

import pub.platform.db.*;
import pub.platform.form.config.*;
import pub.platform.form.util.datatype.*;

/**
 * Sql语句生成器
 *
 * @author qingdao tec
 * @version 1.0
 */
public class SqlAssistor {
  private Logger logger = Logger.getLogger("zt.platform.form.util.SqlAssistor");
  private List sqlFields = new ArrayList();
  private List sqlWheres = new ArrayList();
  private String defaultTbl;
  private List tbls = new ArrayList();
  private FormInstance formInstance;
  public static final int INSERT_SQL_TYPE = 0;
  public static final int UPDATE_SQL_TYPE = 1;
  public static final int DELETE_SQL_TYPE = 2;
  public static final int SELECT_SQL_TYPE = 3;

  public static final int SEARCH_KEY_TYPE = 0;
  public static final int PRIMARY_KEY_TYPE = 1;

  /**
   * 生成SQL构造器，取FORM中的DB类型的字段，加入到sqlField中，将表名加入到tbls中，
   * 将defaultTbl置为该FormInstance的表，根据type的值，
   * type=0时，将SearchKey的字段加入到sqlWhere中，
   * type=1时，将primaryKey的字段加入到sqlWhere中
   * @param formInstance
   * @param type
   * @roseuid 3F729B920175
   */
  public SqlAssistor(FormInstance formInstance, int type) {
    //必需检测组合的有效性，如果必输字段则直接取
    //FormInstance中对应的value，否则，取字段的defaultvalue代替null值
    //一个FormBean只对应一个表（需求中的定义）
    this.formInstance = formInstance;
    String formid = formInstance.getFormid();
    FormBean formBean = FormBeanManager.getForm(formid);
    String[] elementKeys = formBean.getElementKeys();
    //
    this.defaultTbl = formBean.getTbl().trim();
    //
    tbls.add(this.defaultTbl);
    for (int i = 0; i < elementKeys.length; i++) {
      ElementBean elementBean = formBean.getElement(elementKeys[i]);
      //如果是内存变量，不作处理。
      if (elementBean.getType() == elementBean.MEMORY_FIELD) {
        continue;
      }
      //取FORM中的DB类型的字段，加入到sqlField中
      SqlField sf = new SqlField();
      sf.setTblname(this.defaultTbl);
      sf.setFldname(elementKeys[i]);
      sf.setType(elementBean.getDataType());
      //处理value
      FormElementValue fev = (FormElementValue) formInstance.getValue(
          elementKeys[i]);
      Object value = null;
      if ( ( (ArrayList) fev.getValue()).size() > 0) {
        value = ( (ArrayList) fev.getValue()).get(0);
      }
      //对TEXTAREA_TYPE组件的数据做toSql和toHtml转换
      if ( (elementBean.getComponetTp() == ComponentType.TEXTAREA_TYPE || elementBean.getComponetTp() == ComponentType.TEXT_TYPE ) && value != null) {
        value = DBUtil.toSql(value.toString());
        //value = zt.platform.db.DBUtil.toHtml(value.toString());
      }

      //如果是字符串，value值需要转码的在此转码
      if (sf.getType() == DataType.STRING_TYPE) {
        if (elementBean.isNeedEncode() & value != null) {
          value = DBUtil.toDB(value.toString());
        }
      }
      //如果是日期，需要加上"-"
      if (sf.getType() == DataType.DATE_TYPE) {
        if (value != null) {
          String temp = value.toString();
          if(temp.length()==8){
            temp=temp.substring(0,4)+"-"+temp.substring(4,6)+"-"+temp.substring(6,8);
            value=temp;
          }
          else{
            value=null;
          }
        }
      }
      sf.setValue(value);
      //System.out.println(sf.getTblname()+"."+sf.getFldname()+"="+sf.getValue().toString()+"("+sf.getType()+")");
      addSqlField( (sf));
      //type=0
      if (type == 0) {
        if (elementBean.isIsSearchKey()) {
          SqlWhere sw = new SqlWhere();
          sw.setTblname(sf.getTblname());
          sw.setFldname(sf.getFldname());
          sw.setType(sf.getType());
          sw.setOperator(OperatorType.NOOPERATOR_TYPE);
          sw.setValue(sf.getValue());
          addSqlWhere(sw);
        }
      }
      else {
        if (elementBean.isIsPrimaryKey()) {
          SqlWhere sw = new SqlWhere();
          sw.setTblname(sf.getTblname());
          sw.setFldname(sf.getFldname());
          sw.setType(sf.getType());
          sw.setOperator(OperatorType.NOOPERATOR_TYPE);
          sw.setValue(sf.getValue());
          addSqlWhere(sw);
        }
      }
    }
  }

  /**
   * 数据检测
   * @param p_value：数据
   * @param p_type：数据类型
   * @roseuid 3F7EB6140081
   */
  private Object checkValue(Object p_value, int p_type) {
    if (p_type == DataType.STRING_TYPE || p_type == DataType.DATE_TYPE) {
      if (p_value == null) {
        return "";
      }
    }
    if (p_type == DataType.INTEGER_TYPE || p_type == DataType.ENUMERATION_TYPE) {
      if (p_value == null || p_value.equals("")) {
        return new Integer(0);
      }
    }

    if (p_type == DataType.BOOLEAN_TYPE) {
      if (p_value == null || p_value.equals("")) {
        return "0";
      }
    }
    return p_value;
  }

  /**
   * @param defaultTbl
   * @roseuid 3F7EB6140081
   */
  public void setDefaultTbl(String p_defaultTbl) {
    this.defaultTbl = p_defaultTbl;
  }

  /**
   * @return String
   * @roseuid 3F7EB62502A2
   */
  public String getDefaultTbl() {
    return this.defaultTbl;
  }

  /**
   * @param sqlField
   * 此方法给forminstance用
   */
  public void addSqlField(SqlField sqlField) {
    //有效性检测
    String tblname;
    //Object value;
    tblname = sqlField.getTblname();
    if (tblname == null || tblname.length() < 1) {
      tblname = this.defaultTbl;
    }
    sqlField.setTblname(tblname);
    //value=sqlField.getValue();
    //if(value==null) return;
    this.sqlFields.add(sqlField);
    //将sqlWheres中所有的tblname合并同类项
    int j = tbls.size();
    for (int i = 0; i < j; i++) {
      if (! ( (String) tbls.get(i)).equals(tblname)) {
        tbls.add(tblname);
      }
    }
  }

  /**
   * 设置table中某一字段的值
   * 此方法给应用人员用
   */
  public void setSqlFieldValue(String p_tblName, String p_fldName,
                               String p_value) {
    //有效性检测
    SqlField sf = getSqlField(p_tblName, p_fldName);
    if (sf != null) {
      int type = sf.getType();
      sf.setValue(FormInstance.convertValueSingle(DBUtil.toSql(p_value), type));
    }
  }

  /**
   * 根据idx返回SqlField
   */
  public SqlField getSqlField(int idx) {
    return (SqlField)this.sqlFields.get(idx);
  }

  /**
   * 根据tblName和fldName返回SqlField
   */
  public SqlField getSqlField(String p_tblName, String p_fldName) {
    //检测处理
    if (p_tblName == null || p_fldName == null) {
      return null;
    }
    String tblName1 = p_tblName.trim().toLowerCase();
    String fldName1 = p_fldName.trim().toLowerCase();
    //遍历查找
    for (int i = 0; i < sqlFields.size(); i++) {
      SqlField sf = (SqlField) sqlFields.get(i);
      String tblName2 = sf.getTblname().trim().toLowerCase();
      String fldName2 = sf.getFldname().trim().toLowerCase();
      if (tblName2.equals(tblName1) && fldName2.equals(fldName1)) {
        return sf;
      }
    }
    return null;
  }

  /**
   * @return List
   * @roseuid 3F72987202AB
   */
  public List getSqlFields() {
    return (List)this.sqlFields;
  }

  /**
   * @param sqlField
   * @roseuid 3F7298500125
   */
  public void removeSqlField(SqlField sqlField) {
    this.sqlFields.remove(sqlField);
  }

  /**
   * @param idx
   * @roseuid 3F7298F4012B
   */
  public void removeSqlField(int idx) {
    this.sqlFields.remove(idx);
  }

  /**
   * @param sqlWhere
   * @roseuid 3F7E0F4F0123
   */
  public void addSqlWhere(SqlWhere sqlWhere) {
    //有效性检测
    String tblname;
    Object value;
    tblname = sqlWhere.getTblname();
    if (tblname == null || tblname.length() < 1) {
      tblname = this.defaultTbl;
    }
    sqlWhere.setTblname(tblname);
    value = sqlWhere.getValue();
    if (value == null) {
      return;
    }
    this.sqlWheres.add(sqlWhere);
    //将sqlWheres中所有的tblname合并同类项
    int j = tbls.size();
    for (int i = 0; i < j; i++) {
      if (! ( (String) tbls.get(i)).equals(sqlWhere.getTblname())) {
        tbls.add(tblname);
      }
    }
  }

  /**
   * @param idx
   * @return zt.platform.form.util.SqlWhere
   * @roseuid 3F7298D302B4
   */
  public SqlWhere getSqlWhere(int idx) {
    return (SqlWhere)this.sqlWheres.get(idx);
  }

  /**
   * @return List
   * @roseuid 3F7298E6003B
   */
  public List getSqlWheres() {
    return (List)this.sqlWheres;
  }

  /**
   * @param sqlWhere
   * @roseuid 3F7298B003E0
   */
  public void removeSqlWhere(SqlWhere sqlWhere) {
    sqlWheres.remove(sqlWhere);
  }

  /**
   * @param idx
   * @roseuid 3F7298CB02D1
   */
  public void removeSqlWhere(int idx) {
    sqlWheres.remove(idx);
  }

  /**
   * 生成SQL语句串
   * type:
   * 0-生成insert语句
   * 1-生成update语句
   * 2-生成delete语句
   * 3-生成select语句
   *
   * @param type
   * @return String
   * @roseuid 3F72990C0266
   */
  public String toSql(int type) {
    //如果sqlFields没有内容或defaultTbl为空，返回
    int j = this.sqlFields.size();
    if (j < 1 || defaultTbl == null || defaultTbl.length() < 1) {
      return null;
    }

    //借用临时字符串temp组合INSERT_SQL_TYPE类型的sql，这里假设所有经过构建器形成的
    //sqlFields都是合法的,同时假设所有用户自己接触过的sqlFields也都是合法的。
    if (type == this.INSERT_SQL_TYPE) {
      String temp = "insert into " + defaultTbl + " (";
      //字段组合（只组合tblname与defaultTbl相等的字段）
      for (int i = 0; i < j; i++) {
        SqlField sf = (SqlField) sqlFields.get(i);
        if (sf.getTblname().equals(defaultTbl) && sf.getValue() != null) {
          temp += sf.getFldname() + ",";
        }
      }
      if (temp.endsWith(" (")) {
        return null;
      }
      temp = temp.substring(0, temp.length() - 1);
      temp += ") values(";
      //赋值组合（只组合tblname与defaultTbl相等的字段）
      for (int i = 0; i < j; i++) {
        SqlField sf = (SqlField) sqlFields.get(i);
        if (sf.getTblname().equals(defaultTbl) && sf.getValue() != null) {
          temp += crtFieldValue(sf.getValue().toString(), sf.getType()) + ",";
        }
      }
      temp = temp.substring(0, temp.length() - 1);
      temp += ")";
//      System.out.println(temp);
      return temp;
    }

    //下面是针对update,delete,select三种sql都要做的合法性检测
    //没有where条件，则退出
    int k = this.sqlWheres.size();
    if (k < 1) {
      return null;
    }
    //where中没有defaultTbl，也退出
    boolean flg = false;
    for (int i = 0; i < k; i++) {
      if ( ( (SqlWhere) sqlWheres.get(i)).getTblname().equals(defaultTbl)) {
        flg = true;
        break;
      }
    }
    if (!flg) {
      return null;
    }

    //借用临时字符串temp组合UPDATE_SQL_TYPE类型的sql
    if (type == this.UPDATE_SQL_TYPE) {
      //段值组合（只组合tblname与defaultTbl相等的字段，且关键字段不更新）
      String temp = "update " + defaultTbl + " set ";
      for (int i = 0; i < j; i++) {
        SqlField sf = (SqlField) sqlFields.get(i);
//        System.out.print(sf.getFldname() + "=");
//        System.out.println(sf.getValue());
        if (sf.getTblname().equals(defaultTbl) && sf.getValue() != null &&
            !formInstance.isFk(sf.getFldname())) {
          temp += sf.getFldname();
          temp += "=";
          temp += crtFieldValue(sf.getValue().toString(), sf.getType());
          temp += ",";
        }
      }
      if (temp.endsWith(" set ")) {
        return null;
      }
      temp = temp.substring(0, temp.length() - 1);
      //条件组合（只组合tblname与defaultTbl相等的字段）,所有条件关系都是and
      temp += " where ";
      for (int i = 0; i < k; i++) {
        SqlWhere sw = (SqlWhere) sqlWheres.get(i);
        if (sw.getTblname().equals(defaultTbl)) {
          if (sw.getValue() == null) {
            temp += sw.getFldname() + " is null and ";
          }
          else if(isMaxValue(sw.getValue().toString(),sw.getType())){
            continue;
          }
          else {
            temp += sw.getFldname() + " ";
            temp += (new OperatorType()).getOperator(sw.getOperator()) + " ";
            temp +=
                crtWhereValue(sw.getValue().toString(), sw.getType(), sw.getOperator());
            temp += " and ";
          }
        }
      }
      temp = temp.substring(0, temp.length() - 5);
//      System.out.println(temp);
      return temp;
    }

    //借用临时字符串temp组合DELETE_SQL_TYPE类型的sql
    if (type == this.DELETE_SQL_TYPE) {
      //条件组合（只组合tblname与defaultTbl相等的字段）
      String temp = "delete from " + defaultTbl + " where ";
      for (int i = 0; i < k; i++) {
        SqlWhere sw = (SqlWhere) sqlWheres.get(i);
        if (sw.getTblname().equals(defaultTbl)) {
          if (sw.getValue() == null) {
            temp += sw.getFldname() + " is null and ";
          }
          else if(isMaxValue(sw.getValue().toString(),sw.getType())){
            continue;
          }
          else {
            temp += sw.getFldname() + " ";
            temp += (new OperatorType()).getOperator(sw.getOperator()) + " ";
            temp +=
                crtWhereValue(sw.getValue().toString(), sw.getType(), sw.getOperator());
            temp += " and ";
          }
        }
      }
      temp = temp.substring(0, temp.length() - 5);
//      System.out.println(temp);
      return temp;
    }

    //借用临时字符串temp组合SELECT_SQL_TYPE类型的sql
    if (type == this.SELECT_SQL_TYPE) {
      //字段组合
      String temp = "select ";
      for (int i = 0; i < j; i++) {
        SqlField sf = (SqlField) sqlFields.get(i);
        temp += sf.getTblname() + "." + sf.getFldname();
        if (sf.getAliaseName() != null && sf.getAliaseName().length() > 0) {
          temp += " as ";
          temp += sf.getAliaseName();
        }
        temp += ",";
      }
      temp = temp.substring(0, temp.length() - 1);
      //from组合
      temp += " from ";
      for (int i = 0; i < tbls.size(); i++) {
        temp += tbls.get(i) + ",";
      }
      temp = temp.substring(0, temp.length() - 1);
      //条件组合
      temp += " where ";
      for (int i = 0; i < k; i++) {
        SqlWhere sw = (SqlWhere) sqlWheres.get(i);
        if (sw.getValue() == null || (sw.getValue().toString()).length() < 1) {
          continue;
        }
        if(isMaxValue(sw.getValue().toString(),sw.getType())){
          continue;
        }
        temp += sw.getTblname() + "." + sw.getFldname() + " ";
        //对没有设置操作符的sw，如果数据类型是String，那么操作符设为LIKE_OPERATOR_TYPE类型
        if (sw.getType() == DataType.STRING_TYPE &&
            sw.getOperator() == OperatorType.NOOPERATOR_TYPE) {
//          sw.setOperator(OperatorType.LIKE_OPERATOR_TYPE);
          sw.setOperator(OperatorType.EQUALS_OPERATOR_TYPE);
        }
        temp += (new OperatorType()).getOperator(sw.getOperator()) + " ";
        temp +=
            crtWhereValue(sw.getValue().toString(), sw.getType(), sw.getOperator());
        temp += " and ";
      }
      temp = temp.substring(0, temp.length() - 5);
      logger.log(Level.INFO,temp);
      return temp;
    }
    else {
      logger.log(Level.WARNING,"平台形成SQL语句为NULL");
      return null;
    }
  }

  /**
   * 检查p_value是不是其类型的最大值，因为平台用最大值来表示IE中
   * 未做输入的情况，（用null来表示IE的Form中没有相应的组件）
   */
  public static boolean isMaxValue(String p_value, int p_tp){
    //INTEGER_TYPE,DECIAML_TYPE将MAX_VALUE转化为NULL
    if (p_tp == DataType.INTEGER_TYPE || p_tp == DataType.ENUMERATION_TYPE) {
      if (Integer.parseInt(p_value) == Integer.MAX_VALUE) {
        return true;
      }
    }
    else if (p_tp == DataType.DECIAML_TYPE) {
      if (Double.parseDouble(p_value) == Double.MAX_VALUE) {
        return true;
      }
    }
    return false;
  }
  /**
   * 生成（insert update delete时）SQL语句串中"set field=value"中的value部分，因为有的需要加"'"
   * @param type
   * @return String
   */
  public static String crtFieldValue(String p_value, int p_tp) {
    //null或''值处理
    if (p_value == null || p_value.length() < 1) {
      return "NULL";
    }
    //需要加'的值组合
    if (p_tp == DataType.STRING_TYPE || p_tp == DataType.DATE_TYPE ||
        p_tp == DataType.BOOLEAN_TYPE) {
      return "'" + p_value + "'";
    }
    //INTEGER_TYPE,DECIAML_TYPE将MAX_VALUE转化为NULL
    else if (isMaxValue(p_value,p_tp)) {
      return "NULL";
    }
    //不需加'的值组合（除了DataType中定义的上面三种外，还有类型为数值7）
    else {
      return p_value;
    }
  }

  /**
   * 生成（insert update delete时）SQL语句串中"set field=value"中的value部分，因为有的需要加"'"
   * @param type
   * @return String
   */
  public static String crtWhereValue(String p_value, int p_tp, int p_opt) {
    //需要加'的值组合
    String operator = new OperatorType().getOperator(p_opt);
    if (p_tp == DataType.STRING_TYPE &&
        (p_opt == OperatorType.LIKE_OPERATOR_TYPE ||
         p_opt == OperatorType.NOT_LIKE_OPERATOR_TYPE)) {
      return "'%" + p_value + "%'";
    }
    else if (p_tp == DataType.STRING_TYPE || p_tp == DataType.DATE_TYPE ||
             p_tp == DataType.BOOLEAN_TYPE) {
      return "'" + p_value + "'";
    }
    //不需加'的值组合（除了DataType中定义的上面三种外，还有类型为数值7）
    else {
      return p_value;
    }
  }
}
