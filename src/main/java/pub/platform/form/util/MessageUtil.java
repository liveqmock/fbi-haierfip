package pub.platform.form.util;

import pub.platform.advance.utils.PropertyManager;
import pub.platform.form.util.event.ErrorMessage;
/**
 * 消息处理工具
 *
 * @author qingdao tec
 * @version 1.0
 */
public class MessageUtil {

    /**
     * 将msg转换成消息串
     *
     * 根据ErrorMessage的实例，访问PropertyMessages得到转换后的消息串
     * @param msg
     * @return String
     * @roseuid 3F7E3FD90063
     */
    public static String getMessage(ErrorMessage msg) {
      String temp;
      if(msg.getType()==msg.CONSTANT_TYPE){
        temp= PropertyManager.getProperty(msg.getMessage());
        if(temp==null) temp=msg.getMessage();
      }
      else{
        temp= PropertyManager.getProperty(msg.getMessage(),msg.getArguments());
        if(temp==null) temp=msg.getMessage()+"<"+msg.getArguments()+">";
      }
      return temp;
    }
}
