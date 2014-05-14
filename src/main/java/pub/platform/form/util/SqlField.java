package pub.platform.form.util;

import pub.platform.form.util.datatype.*;
/**
 * Sql语句的查询字段类
 *
 * @author qingdao tec
 * @version 1.0
 */
public class SqlField {
    private String tblname;
    private String fldname;
    private String aliaseName;
    private Object value;
    private int type;//与zt.platform.form.util.datatype.DataType对应

    /**
     *构造函数
     */
    public SqlField(String p_tblname,String p_fldname,String p_aliaseName,Object p_value,int p_type){
        this.tblname=p_tblname;
        this.fldname=p_fldname;
        this.aliaseName=p_aliaseName;
        this.value=p_value;
        this.type=p_type;
    }
    public SqlField(String p_fldname,String p_aliaseName,Object p_value,int p_type){
        this.tblname=null;
        this.fldname=p_fldname;
        this.aliaseName=p_aliaseName;
        this.value=p_value;
        this.type=p_type;
    }
    public SqlField(String p_fldname,Object p_value,int p_type){
        this.tblname=null;
        this.fldname=p_fldname;
        this.aliaseName=null;
        this.value=p_value;
        this.type=p_type;
    }
    public SqlField(){
        this.tblname=null;
        this.fldname=null;
        this.aliaseName=null;
        this.value=null;
        this.type=DataType.STRING_TYPE;
    }
    /**
     * @param tblname
     * @roseuid 3F7EB6140081
     */
    public void setTblname(String p_tblname) {
      this.tblname=p_tblname;
    }
    /**
     * @return String
     * @roseuid 3F7EB62502A2
     */
    public String getTblname() {
     return this.tblname;
    }
    /**
     * @param fldname
     * @roseuid 3F7EB6140081
     */
    public void setFldname(String p_fldname) {
      this.fldname=p_fldname;
    }
    /**
     * @return String
     * @roseuid 3F7EB62502A2
     */
    public String getFldname() {
     return this.fldname;
    }
    /**
     * @param aliaseName
     * @roseuid 3F7EB6140081
     */
    public void setAliaseName(String p_aliaseName) {
      this.aliaseName=p_aliaseName;
    }
    /**
     * @return String
     * @roseuid 3F7EB62502A2
     */
    public String getAliaseName() {
     return this.aliaseName;
    }
    /**
     * @param value
     * @roseuid 3F7EB6140081
     */
    public void setValue(Object p_value) {
      this.value=p_value;
    }
    /**
     * @return Object
     * @roseuid 3F7EB62502A2
     */
    public Object getValue() {
     return this.value;
    }
    /**
     * @param type
     * @roseuid 3F7EB6140081
     */
    public void setType(int p_type) {
      this.type=p_type;
    }
    /**
     * @return int
     * @roseuid 3F7EB62502A2
     */
    public int getType() {
     return this.type;
    }

}
