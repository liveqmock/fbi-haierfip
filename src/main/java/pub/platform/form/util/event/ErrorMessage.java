//Source file: e:\\java\\zt\\platform\\form\\util\\event\\ErrorMessage.java

package pub.platform.form.util.event;


/**
 * 错误信息
 * 错误信息分为两大类：
 * 0－常量，在程序中写死返回信息
 * 1－变量，在程序中以信息代号的方式编写，信息内容通过代号可以动态获得并且可以定义参数
 *
 * @author qingdao tec
 * @version 1.0
 */
public class ErrorMessage {

    /**
     * 信息类型，0-无参信息 1-有参信息
     */
    private int type;

    /**
     * 消息代号
     */
    private String message;

    /**
     * 参数数组
     */
    private String[] arguments;
    public static final int CONSTANT_TYPE = 0;
    public static final int VARIABLE_TYPE = 1;

    /**
     * 错误信息构造器（带arguments）
     * @param type
     * @param message
     * @param arguments
     * @roseuid 3F7266AD0228
     */
    public ErrorMessage(int p_type, String p_message, String[] p_arguments) {
      this.type=p_type;
      this.message=p_message;
      this.arguments=p_arguments;
    }

    /**
     * 错误信息构造器
     * @param type
     * @param message
     * @roseuid 3F7266880153
     */
    public ErrorMessage(int p_type, String p_message) {
      this.type=p_type;
      this.message=p_message;
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
    /**
     * @param arguments
     * @roseuid 3F7EB6140081
     */
    public void setMessage(String p_message) {
      this.message=p_message;
    }
    /**
     * @return String
     * @roseuid 3F72683A007A
     */
    public String getMessage() {
     return this.message;
    }
    /**
     * @param arguments
     * @roseuid 3F7EB6140081
     */
    public void setArguments(String[] p_arguments) {
      this.arguments=p_arguments;
    }
    /**
     * @return String[]
     * @roseuid 3F7E3CD50294
     */
    public String[] getArguments() {
     return this.arguments;
    }
}
