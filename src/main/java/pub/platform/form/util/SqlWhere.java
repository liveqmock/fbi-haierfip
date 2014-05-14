package pub.platform.form.util;

import pub.platform.form.util.datatype.*;
/**
 * where条件类
 *
 * @author qingdao tec
 * @version 1.0
 */
public class SqlWhere implements Comparable {
    private String tblname;
    private String fldname;
    private int operator;
    private Object value;
    private int type;//与zt.platform.form.util.datatype.DataType对应，扩展值7对应与其它表的字段关联。
    private String relation;

    /**
     *构造函数
     */
    public SqlWhere(String p_tblname,String p_fldname,int p_operator,Object p_value,int p_type,String relation){
        this.tblname=p_tblname;
        this.fldname=p_fldname;
        this.operator=p_operator;
        this.value=p_value;
        this.type=p_type;
        this.relation = relation;
    }

    public SqlWhere(String p_tblname,String p_fldname,int p_operator,Object p_value,int p_type){
        this.tblname=p_tblname;
        this.fldname=p_fldname;
        this.operator=p_operator;
        this.value=p_value;
        this.type=p_type;
    }
    public SqlWhere(String p_fldname,int p_operator,Object p_value,int p_type){
        this.tblname=null;
        this.fldname=p_fldname;
        this.operator=p_operator;
        this.value=p_value;
        this.type=p_type;
    }
    public SqlWhere(String p_fldname,Object p_value,int p_type){
        this.tblname=null;
        this.fldname=p_fldname;
        this.operator=OperatorType.NOOPERATOR_TYPE;
        this.value=p_value;
        this.type=p_type;
    }
    public SqlWhere(){
        this.tblname=null;
        this.fldname=null;
        this.operator=OperatorType.NOOPERATOR_TYPE;
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
     * @param operator
     * @roseuid 3F7EB6140081
     */
    public void setOperator(int p_operator) {
      this.operator=p_operator;
    }
    /**
     * @return int
     * @roseuid 3F7EB62502A2
     */
    public int getOperator() {
     return this.operator;
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

    public boolean equals(Object o) {
        if ( o instanceof SqlWhere ) {
            SqlWhere tmp = (SqlWhere)o;
//            System.out.println("================="+tmp.getFldname()+"="+tmp.getTblname()+"="+this.fldname+"="+this.tblname);
            if ( tmp.getTblname() != null && tmp.getTblname().equals(this.tblname) &&
                 tmp.getFldname() != null && tmp.getFldname().equals(this.fldname) )
                return true;
        }
        return false;
    }
    public String getRelation()
    {
        return relation;
    }
    public void setRelation(String relation)
    {
        this.relation = relation;
    }
    public int compareTo(Object o)
    {
        if ( o instanceof SqlWhere ) {
            SqlWhere tmp = (SqlWhere)o;
            if ( tmp.getTblname() != null && tmp.getTblname().equals(this.tblname) &&
                 tmp.getFldname() != null && tmp.getFldname().equals(this.fldname) )
                return 0;
        }
        return -1;

    }
}
