package pub.platform.form.control;

import java.sql.PreparedStatement;

import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;
import pub.platform.form.util.*;
import pub.platform.form.util.event.*;


public class TestListEditFormActions extends FormActions
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
        ctx.setTarget("/aa.jsp");
        System.out.println("********************in Test List Edit Button=========="+SessionAttributes.REQUEST_EDIT_BUTTON_VALUE);
        return 0;
    }


}