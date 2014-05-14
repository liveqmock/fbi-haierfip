package pub.platform.utils;

import org.apache.log4j.*;
import java.io.InputStream;
import java.io.*;
import java.util.*;

/**
 * <p>Title: 基础框架</p>
 * <p>Description: 日志</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: 青岛中天科技</p>
 * @author 林意炜
 * @version 1.0
 */

public  class Log {

    public static final int OFF = 0;
    public static final int FATAL = 1;
    public static final int ERROR = 2;
    public static final int WARN = 3;
    public static final int INFO = 4;
    public static final int DEBUG = 5;
    public static final int ALL = 6;

    private static Logger logger = null;

    private static final String  fileName="/log4j.properties";



    private void getlog(){
        //初始化Log4j配置信息
        try {
           //构建配置文件输入流
           InputStream input = getClass().getResourceAsStream(fileName);

           Properties props = new Properties();

            props.load(input);
            input.close();

            PropertyConfigurator.configure(props);
            logger = Logger.getRootLogger();


       } catch (Exception ex) {
           // 加载日志文件出错时不能用日志类进行日志输出
           if (fileName.indexOf("log") > 0) {
               System.out.println("读取" + fileName + "配置文件出错！");
           } else {
               getLogger().error("读取" + fileName + "配置文件出错！", ex);
           }
       }

    }

    /**
     * 获取日志
     * @return
     */
    public  Logger getLogger() {
         if (logger ==null)
              getlog();

        return logger;
    }

    /**
     * 获取日志级别
     * @return
     */
    public static String getLogLevel() {
        return logger.getLevel().toString();
    }


    /**
     * 设置日志级别
     * @param level
     */
    public static void setLevel(int level) {
        switch (level) {

        case OFF:
            logger.setLevel(Level.OFF);
            break;

        case FATAL:
            logger.setLevel(Level.FATAL);
            break;

        case ERROR:
            logger.setLevel(Level.ERROR);
            break;

        case WARN:
            logger.setLevel(Level.WARN);
            break;

        case INFO:
            logger.setLevel(Level.INFO);
            break;

        case DEBUG:
            logger.setLevel(Level.DEBUG);
            break;

        case ALL:
            logger.setLevel(Level.ALL);
            break;

        default:
            break;
        }
    }


}
