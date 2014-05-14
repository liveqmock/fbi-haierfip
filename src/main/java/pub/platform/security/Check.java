package pub.platform.security;

import javax.servlet.http.*;

import pub.platform.form.config.*;

import java.util.*;

public class Check {
     private HttpSession session = null;

     public Check(){
     }
     public void addSession(HttpSession session){
          this.session = session;
     }
     public boolean hasRight(String strUrl) throws Exception{
          if (this.session == null ){
               throw new Exception ("编码错误：  没有传入session对象!");
          }

          OperatorManager om = (OperatorManager)session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
          if(om == null){
               throw new Exception ("操作员超时，请重新签到！");
          }
          List list = om.getJspList();
          int length = list.size();
          for (int i = 0; i < length ; i ++ ){
               String tmpStr = (String)list.get(i);
               if ( tmpStr.indexOf(strUrl) >= 0 ) {
                    return true;
               }
          }
          return false; 
     }
}
