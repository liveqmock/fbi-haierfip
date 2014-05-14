package pub.platform.form.control;

import java.sql.PreparedStatement;
import java.util.Map;
import java.util.HashMap;

import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;
import pub.platform.form.util.*;
import pub.platform.form.util.event.*;


public class TestQuery extends FormActions
{

    public int load(SessionContext ctx, DatabaseConnection conn, FormInstance instance,ErrorMessages msgs,
        EventManager manager, String parameter) {
        System.out.println("********************in Load ==========");
        if ( instance.getFormid().equals("000007") ) {
            instance.setValue("name", ctx.getParameter("name"));
            trigger(manager, instance, EventType.EDIT_VIEW_EVENT_TYPE, Event.BRANCH_CONTINUE_TYPE);
        }
//        instance.setReadonly(true);
        System.out.println("********************in Load ==========");
        return 0;
    }

    public int buttonEvent(SessionContext ctx, DatabaseConnection conn, FormInstance instance, String button,
            ErrorMessages msgs, EventManager manager) {
        System.out.println("********************in Test List Edit Button=========="+button);
        if ( button != null && button.equals(SessionAttributes.REQUEST_EDIT_BUTTON_VALUE) ) {
            trigger(manager,"000007",null);
        }

        System.out.println("********************in Test List Edit Button=========="+SessionAttributes.REQUEST_EDIT_BUTTON_VALUE);
        return 0;
    }

    public int beforeFind(SessionContext ctx, DatabaseConnection conn, FormInstance instance,
                          ErrorMessages msgs,
                          EventManager manager)
    {
        return 0;
    }


        /**
         *@param  ctx
         *@param  conn
         *@param  instance  Description of the Parameter
         *@param  msgs
         *@param  manager
         *@param  ps
         *@param  countps   Description of the Parameter
         *@return           int
         *@roseuid          3F721E5200D1
         */
//        public int preFind(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
//                EventManager manager,SqlWhereUtil sqlUtil) {
//          super.preFind(ctx,conn,instance,msgs,manager,sqlUtil);
//          sqlUtil.clear();
//           String pid = ctx.getParameter("qid");
//           String pname = ctx.getParameter("qname");
//
//           if ( pid != null && pid.trim().length() > 0 ) {
//               sqlUtil.addWhereField("testquery","id",pid.trim(),SqlWhereUtil.DataType_Character,SqlWhereUtil.OperatorType_Like);
//           }
//           if ( pname != null && pname.trim().length() > 0 ) {
//               sqlUtil.addWhereField("testquery","name",pname.trim(),SqlWhereUtil.DataType_Character,SqlWhereUtil.OperatorType_Like);
//           }
//
//           return 0;
//       }


        /**
         *  Description of the Method
         *
         *@param  ctx       Description of the Parameter
         *@param  conn      Description of the Parameter
         *@param  instance  Description of the Parameter
         *@param  msgs      Description of the Parameter
         *@param  manager   Description of the Parameter
         *@param  assistor  Description of the Parameter
         *@return           Description of the Return Value
         */
        public int preFind(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                EventManager manager, SqlAssistor assistor) {
            return 0;
        }


        /**
         *@param  ctx
         *@param  conn
         *@param  instance  Description of the Parameter
         *@param  msgs
         *@param  manager
         *@param  result
         *@return           int
         *@roseuid          3F721E5603D0
         */
        public int postFindOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                EventManager manager, RecordSet result) {
            return 0;
        }


        /**
         *@param  ctx
         *@param  conn
         *@param  instance  Description of the Parameter
         *@param  msgs
         *@param  manager
         *@return           int
         *@roseuid          3F721E6500D8
         */
        public int postFindFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                EventManager manager) {
            return 0;
        }


}
