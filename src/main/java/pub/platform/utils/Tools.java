package pub.platform.utils;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pub.platform.form.config.SystemAttributeNames;
import pub.platform.form.control.SessionContext;
import pub.platform.form.control.impl.SessionContextHttpImpl;
import pub.platform.form.util.SessionAttributes;
import pub.platform.security.Operator;
import pub.platform.security.OperatorManager;
import pub.platform.system.manage.dao.*;

import java.util.*;


public class Tools {
     public static SessionContext getSc(HttpServletRequest req,HttpServletResponse res) {
          HttpSession session = req.getSession ( true ) ;

          SessionContext ctx = ( SessionContext )session.getAttribute ( SessionAttributes.SESSION_CONTEXT_NAME ) ;
          if ( ctx == null ) {
               ctx = new SessionContextHttpImpl ( req , res ) ;
               session.setAttribute ( SessionAttributes.SESSION_CONTEXT_NAME , ctx ) ;
          }

          return ctx;
     }
     public static PtOperBean getOper(HttpSession session) {
          OperatorManager om = (OperatorManager)session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
          if ( om == null )
               return null;
          else
               return om.getOperator();
     }
     public static String getOperid(HttpSession session) throws Exception {
          OperatorManager om = (OperatorManager)session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
          if ( om == null ) {
               throw new Exception("操作员签到超时，请重新签到！");
          } else {
               PtOperBean oper = om.getOperator();
               if ( oper == null ) {
                    throw new Exception("操作员签到超时，请重新签到！");
               }
               return oper.getOperid();
          }
     }
}
