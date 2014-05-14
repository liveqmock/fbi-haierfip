//Source file: e:\\java\\zt\\platform\\form\\util\\datatype\\OperatorType.java

package pub.platform.form.util.datatype;


/**
 * SQL语句的关系操作符类
 *
 * @author qingdao tec
 * @version 1.0
 */
public class OperatorType {
    public static final int NOOPERATOR_TYPE = -1;
    public static final int EQUALS_OPERATOR_TYPE = 0;
    public static final int LOWER_OPERATOR_TYPE = 1;
    public static final int GREATER_OPERATOR_TYPE = 2;
    public static final int LIKE_OPERATOR_TYPE = 3;
    public static final int LOWER_EQUALS_OPERATOR_TYPE = 4;
    public static final int GREATER_EQUALS_OPERATOR_TYPE = 5;
    public static final int NOT_EQUALS_OPERATOR_TYPE = 6;
    public static final int NOT_LIKE_OPERATOR_TYPE = 7;

    public static final String EQUALS_OPERATOR = "=";
    public static final String LOWER_OPERATOR = "<";
    public static final String GREATER_OPERATOR = ">";
    public static final String LIKE_OPERATOR = "like";
    public static final String LOWER_EQUALS_OPERATOR = "<=";
    public static final String GREATER_EQUALS_OPERATOR = ">=";
    public static final String NOT_EQUALS_OPERATOR = "!=";
    public static final String NOT_LIKE_OPERATOR = "not like";

    /**
     * 根据操作类型返回操作符
     * @param type
     * @return String
     * @roseuid 3F729CFB02F0
     */
    public String getOperator(int type) {
     if(type==this.EQUALS_OPERATOR_TYPE){
       return this.EQUALS_OPERATOR;
     }
     else if(type==this.LOWER_OPERATOR_TYPE){
       return this.LOWER_EQUALS_OPERATOR;
     }
     else if(type==this.GREATER_OPERATOR_TYPE){
       return this.GREATER_OPERATOR;
     }
     else if(type==this.LIKE_OPERATOR_TYPE){
       return this.LIKE_OPERATOR;
     }
     else if(type==this.LOWER_EQUALS_OPERATOR_TYPE){
       return this.LOWER_EQUALS_OPERATOR;
     }
     else if(type==this.GREATER_EQUALS_OPERATOR_TYPE){
       return this.GREATER_EQUALS_OPERATOR;
     }
     else if(type==this.NOT_EQUALS_OPERATOR_TYPE){
       return this.NOT_EQUALS_OPERATOR;
     }
     else if(type==this.NOT_LIKE_OPERATOR_TYPE){
       return this.NOT_LIKE_OPERATOR;
     }
     else{
       //NOOPERATOR_TYPE
       return "=";
     }
    }
}
