//Source file: e:\\java\\zt\\platform\\form\\config\\PropertyMessages.java

package pub.platform.form.config;

import java.util.*;
/**
 * 系统错误信息载体类
 * 用来存储开发人员定义的错误信息
 *
 * 错误信息分为两大类：
 * 0－常量，在程序中写死返回信息，即返回错误代号
 * 1－变量，在程序中以信息代号的方式编写，信息内容通过代号可以动态获得，并且可以定
 * 义参数，参数按顺序定义，在信息内容定义中只要包含%s的就替换成信息内容
 *
 * 文件格式是标准JAVA的properties文件
 * 请在static语句块中初始化
 *
 * @author 请替换
 * @version 1.0
 */
public class PropertyMessages {
    private static Map messages;
    public static final String PROPERTIES_FILE_NAME = "systemmessage.properties";


    /**
     * 取代号为key的消息
     *
     * 如果该代号不存在则返回该代号
     * @param key
     * @return String
     * @roseuid 3F72699B0078
     */
    public static String getMessage(String key) {
     return null;
    }

    /**
     * 取代号为key的消息，并用参数替换消息体中的%s
     * @param key
     * @param argus
     * @return String
     * @roseuid 3F7269AF0302
     */
    public static String getMessage(String key, String[] argus) {
     return null;
    }
}
