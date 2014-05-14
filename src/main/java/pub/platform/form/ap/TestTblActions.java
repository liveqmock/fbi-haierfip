package pub.platform.form.ap;

import java.sql.PreparedStatement;

import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;
import pub.platform.form.control.*;
import pub.platform.form.util.FormInstance;
import pub.platform.form.util.SqlAssistor;
import pub.platform.form.util.event.ErrorMessages;
import pub.platform.form.util.event.EventManager;


public class TestTblActions extends FormActions
{
    public int load(SessionContext ctx, DatabaseConnection conn, ErrorMessages msgs,
                     EventManager manager, String parameter)
     {
         System.out.println("********************i am in load!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721D3503CA
      */
     public int unload(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                       EventManager manager)
     {
         System.out.println("********************i am in unload!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721D4A017C
      */
     public int beforeInsert(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                             EventManager manager)
     {
         System.out.println("********************i am in beforeInsert!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @param assistor
      * @return int
      * @roseuid 3F721DC40091
      */
     public int preInsert(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                          EventManager manager, SqlAssistor assistor)
     {
         System.out.println("********************i am in preInsert!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721DCE03D4
      */
     public int postInsertOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                             EventManager manager)
     {
         System.out.println("********************i am in postInsertOk!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721DDD0386
      */
     public int postInsertFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                               EventManager manager)
     {
         System.out.println("********************i am in postInsertFail!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @param assistor
      * @return int
      * @roseuid 3F721DE4030E
      */
     public int beforeEdit(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                           EventManager manager, SqlAssistor assistor)
     {
         System.out.println("********************i am in beforeEdit!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @param assistor
      * @return int
      * @roseuid 3F721E020267
      */
     public int preEdit(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                        EventManager manager, SqlAssistor assistor)
     {
         System.out.println("********************i am in preEdit!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721E0D0371
      */
     public int postEditOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                           EventManager manager)
     {
         System.out.println("********************i am in postEditOk!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721E1603CE
      */
     public int postEditFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                             EventManager manager)
     {
         System.out.println("********************i am in postEditFail!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @param assistor
      * @return int
      * @roseuid 3F721E2C01F9
      */
     public int beforeDelete(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                             EventManager manager, SqlAssistor assistor)
     {
         System.out.println("********************i am in beforeDelete!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @param assistor
      * @return int
      * @roseuid 3F721E340146
      */
     public int preDelete(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                          EventManager manager, SqlAssistor assistor)
     {
         System.out.println("********************i am in preDelete!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721E3903C4
      */
     public int postDeleteOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                             EventManager manager)
     {
         System.out.println("********************i am in postDeleteOk!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721E42020E
      */
     public int postDeleteFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                               EventManager manager)
     {
         System.out.println("********************i am in postDeleteFail!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721E4800E1
      */
     public int beforeFind(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                           EventManager manager)
     {
         System.out.println("********************i am in beforeFind!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @param assistor
      * @param ps
      * @return int
      * @roseuid 3F721E5200D1
      */
     public int preFind(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                        EventManager manager, PreparedStatement ps,PreparedStatement countps)
     {
         System.out.println("********************i am in preFind!");
         return 0;
     }
     public int preFind(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                        EventManager manager, SqlAssistor assistor)
     {
         System.out.println("********************i am in preFind!");
         return 0;
     }


     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @param result
      * @return int
      * @roseuid 3F721E5603D0
      */
     public int postFindOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                           EventManager manager, RecordSet result)
     {
         System.out.println("********************i am in postFindOk!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721E6500D8
      */
     public int postFindFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                             EventManager manager)
     {
         System.out.println("********************i am in postFindFail!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @param ps
      * @return int
      * @roseuid 3F7EC940015A
      */
     public int preReference(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                             EventManager manager, PreparedStatement ps)
     {
         System.out.println("********************i am in preReference!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F7EC950010D
      */
     public int postReferenceOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                                EventManager manager,RecordSet rs)
     {
         System.out.println("********************i am in postReferenceOk!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F7EC95C0255
      */
     public int postReferenceFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
                                  EventManager manager,RecordSet rs)
     {
         System.out.println("********************i am in postReferenceFail!");
         return 0;
     }

     /**
      * @param ctx
      * @param conn
      * @param button
      * @param msgs
      * @param manager
      * @return int
      * @roseuid 3F721E6C001A
      */
     public int buttonEvent(SessionContext ctx, DatabaseConnection conn, FormInstance instance, String button,
                            ErrorMessages msgs, EventManager manager)
     {
         System.out.println("********************i am in buttonEvent!");
         return 0;
     }


}