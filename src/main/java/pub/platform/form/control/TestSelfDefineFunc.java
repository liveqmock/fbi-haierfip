package pub.platform.form.control;

import java.sql.PreparedStatement;

import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;
import pub.platform.form.util.FormInstance;
import pub.platform.form.util.SqlAssistor;
import pub.platform.form.util.SqlWhereUtil;
import pub.platform.form.util.event.*;


public class TestSelfDefineFunc extends FormActions
{
    public int load(SessionContext ctx, DatabaseConnection conn, FormInstance instance,ErrorMessages msgs,
            EventManager manager, String parameter) {
System.out.println("================i am in load==========================");
//instance.setReadonly(true);

        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721D3503CA
     */
    public int unload(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in unload==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721D4A017C
     */
    public int beforeInsert(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in before insert==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@param  assistor
     *@return           int
     *@roseuid          3F721DC40091
     */
    public int preInsert(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, SqlAssistor assistor) {
//System.out.println("================i am in pre Insert==========================");
//        msgs.add("学历不允许为空!");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721DCE03D4
     */
    public int postInsertOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in post insert ok==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721DDD0386
     */
    public int postInsertFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in post insert fail==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@param  assistor
     *@return           int
     *@roseuid          3F721DE4030E
     */
    public int beforeEdit(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, SqlAssistor assistor) {
System.out.println("================i am in before edit==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@param  assistor
     *@return           int
     *@roseuid          3F721E020267
     */
    public int preEdit(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, SqlAssistor assistor) {
System.out.println("================i am in pre edit==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721E0D0371
     */
    public int postEditOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in post edit ok==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721E1603CE
     */
    public int postEditFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in post edit fail==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@param  assistor
     *@return           int
     *@roseuid          3F721E2C01F9
     */
    public int beforeDelete(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, SqlAssistor assistor) {
System.out.println("================i am in before delete==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@param  assistor
     *@return           int
     *@roseuid          3F721E340146
     */
    public int preDelete(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, SqlAssistor assistor) {
System.out.println("================i am in pre delete==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721E3903C4
     */
    public int postDeleteOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in post delete ok==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721E42020E
     */
    public int postDeleteFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in post delete fail==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721E4800E1
     */
    public int beforeFind(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager) {
System.out.println("================i am in before find==========================");
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
    public int preFind(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, PreparedStatement ps, PreparedStatement countps) {
System.out.println("================i am in pre find==========================");
        return 0;
    }


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
System.out.println("================i am in pre find==========================");
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
System.out.println("================i am in post find ok==========================");
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
System.out.println("================i am in post find fail==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@param  ps
     *@return           int
     *@roseuid          3F7EC940015A
     */
    public int preReference(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, String reffldnm,SqlWhereUtil sqlWhereUtil) {
String name = ctx.getParameter("name");
        if ( name != null && name.trim().length() > 0 )
        sqlWhereUtil.addWhereField("testref","name",name,SqlWhereUtil.DataType_Character,SqlWhereUtil.OperatorType_Like);
System.out.println("================i am in pre reference==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@param  rs        Description of the Parameter
     *@return           int
     *@roseuid          3F7EC950010D
     */
    public int postReferenceOk(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, RecordSet rs) {
System.out.println("================i am in post reference ok==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  msgs
     *@param  manager
     *@param  rs        Description of the Parameter
     *@return           int
     *@roseuid          3F7EC95C0255
     */
    public int postReferenceFail(SessionContext ctx, DatabaseConnection conn, FormInstance instance, ErrorMessages msgs,
            EventManager manager, RecordSet rs) {
System.out.println("================i am in post reference fail==========================");
        return 0;
    }


    /**
     *@param  ctx
     *@param  conn
     *@param  instance  Description of the Parameter
     *@param  button
     *@param  msgs
     *@param  manager
     *@return           int
     *@roseuid          3F721E6C001A
     */
    public int buttonEvent(SessionContext ctx, DatabaseConnection conn, FormInstance instance, String button,
            ErrorMessages msgs, EventManager manager) {
System.out.println("================i am in button "+button+" event==========================");
        msgs.add("i love this game");
        trigger(manager, instance, EventType.INSERT_VIEW_EVENT_TYPE,Event.BRANCH_BREAK_TYPE);
        return 0;
    }
    public int insertSmallQueryEvent(SessionContext ctx, DatabaseConnection conn, FormInstance instance,
            ErrorMessages msgs, EventManager manager) {
        instance.setValue("ref","44");
System.out.println("================i am in insert small query ==========================");
        return 0;
    }
    public int editSmallQueryEvent(SessionContext ctx, DatabaseConnection conn, FormInstance instance,
            ErrorMessages msgs, EventManager manager) {
        instance.setValue("ref","88");
System.out.println("================i am in edit small query==========================");
        return 0;
    }



}