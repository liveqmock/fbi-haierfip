package pub.platform.form.control;


import java.sql.*;
import java.util.*;

import pub.platform.advance.utils.PropertyManager;
import pub.platform.db.*;
import pub.platform.form.config.*;
import pub.platform.form.util.*;
import pub.platform.form.util.event.*;
import pub.platform.utils.*;

/**
 * FORM事件处理
 *
 * @author 王德良
 * @version 1.0
 */
public class ControllerAssistor {

    /**
     * 1、获得FORM管理器
     *       FormInstanceManager  insatnceManager=
     *       ctx.getAttribute(SESSION_FORM_INSATNCE_MANAGER_NAME)
     * 2、实例化FORM
     *      instanceid = instanceManager.instanceForm(event.getId())
     *     失败则将错误信息加入msgs中，return否则继续
     * 3、执行定义的FormActions实例的load
     * 4、成功更新event的instanceid,否则要删除该instance
     *
     * 以上任何一步失败都返回小于0的错误代号，成功返回0
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F72721E032E
     */
    public static int load(SessionContext ctx, Event event, ErrorMessages msgs,
                           EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager)ctx.getAttribute(SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = fiManager.instanceForm(event.getId());

        if ( instanceid == null ) {
            msgs.add(""+ErrorCode.FORM_LOAD_FAIL);
            return ErrorCode.FORM_LOAD_FAIL;
        }
        FormBean fb = FormBeanManager.getForm(event.getId());
        event.setInstanceid(instanceid);
        FormInstance fi = fiManager.getFormInstance(instanceid);
        FormActions fActions = fi.getFormAction();
        if ( fActions != null ) {
            int result = fActions.load(ctx, conn, fi,msgs, manager, event.getParameters());
            if (result < 0) {
                msgs.add(""+result);
                fiManager.removeInstance(instanceid);
            } else {
                if ( !manager.hasMoreEvent() ) {
                    if ( fb.getType() == fb.LIST_TYPE || fb.getType() == fb.QUERY_TYPE ) {
                        manager.trigger(instanceid, EventType.FIND_EVENT_TYPE,
                                        Event.BRANCH_CONTINUE_TYPE, true);
                    } else {
                        manager.trigger(instanceid, EventType.INSERT_VIEW_EVENT_TYPE,
                                        Event.BRANCH_CONTINUE_TYPE, true);
                    }
                }
            }

            event.setResult(result);

            return result;
        } else {
            if ( !manager.hasMoreEvent() ) {
                if (fb.getType() == fb.LIST_TYPE || fb.getType() == fb.QUERY_TYPE ) {
                    manager.trigger(instanceid, EventType.FIND_EVENT_TYPE,
                                    Event.BRANCH_CONTINUE_TYPE, true);
                }
                else {
                    manager.trigger(instanceid, EventType.INSERT_VIEW_EVENT_TYPE,
                                    Event.BRANCH_CONTINUE_TYPE, true);
                }
            }

            event.setResult(0);
            return 0;
        }
    }

    /**
     * 1、获得FORM实例管理器
     * 2、获得FORM实例
     * 3、执行formInstance的FormActions的unload()
     * 4、卸载实例formInstance. removeInstance(instanceid)
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7ED0AD017C
     */
    public static int unload(SessionContext ctx, Event event, ErrorMessages msgs, EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager)ctx.getAttribute(SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME);
        FormInstance fi = fiManager.getFormInstance(event.getId());
        FormActions fActions = fi.getFormAction();
        if ( fActions != null ) {
            fActions.unload(ctx,conn,fi,msgs,manager);
        }
        fiManager.removeInstance(event.getId());
        return 0;
    }

    /**
     * 1、获得FORM管理器
     * 2、如果event.isInstance==false,则先加载该FORM
     * 3、获取该FORM实例
     * 4、初始化该实例的值，调用initValues()
     * 5、得到该实例的FormActions实例，执行其beforeInsert()方法
     *
     * 以上任何一步失败都返回小于0的错误代号，成功返回0
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7272980012
     */
    public static int insertview(SessionContext ctx, Event event, ErrorMessages msgs,
                                 EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager)ctx.getAttribute(SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);

        if ( fi != null ) {
            fi.initValues();
            FormActions actions = fi.getFormAction();
            int result = 0;
            if ( actions != null ) {
                result = actions.beforeInsert(ctx, conn, fi, msgs, manager);
                if ( result < 0 ) {
                    msgs.add(""+result);
                }
            }
            event.setResult(result);
            return result;
        } else {
            msgs.add(""+ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }
    }

    /**
     * 1、获得FORM实例管理器
     * 2、获得FORM实例,如果event.isInstance==false,则先加载该FORM
     * 3、调用validate()方法校验输入,失败返回错误代号
     * 4、构造SqlAssistor,参数为formInstance和1(主键)
     * 5、执行formInstance的FormActions的preInsert()
     * 6、String sSql = assistor.toSql(inserttp)
     * 7、int rtn = conn.executeUpdate(sSql)
     * 8、成功,则执行formInsatnce的FormActions的postInsertOk()
     *       失败,则执行formInsatnce的FormActions的postInsertFail()
     * 9、返回结果
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7272CF0333
     */
    public static int insert(SessionContext ctx, Event event, ErrorMessages msgs,
                             EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager)ctx.getAttribute(SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        if ( fi != null ) {
            if ( validate(fi,msgs) ) {
                SqlAssistor sqlAssistor = new SqlAssistor(fi,SqlAssistor.PRIMARY_KEY_TYPE);
                FormActions actions     = fi.getFormAction();
                int result = 0;
                if ( actions != null ) {
                    result = actions.preInsert(ctx,conn,fi,msgs,manager,sqlAssistor);
                    if ( result < 0 ) {
                        msgs.add(""+result);
                    }
                }
                event.setBefore_result(result);
                if ( result < 0 ) {
                    return result;
                }

                String sSql = sqlAssistor.toSql(SqlAssistor.INSERT_SQL_TYPE);
                result = conn.executeUpdate(sSql);

                if ( result == 0 ) {
                    result = -1;
                    msgs.add("增加记录失败");
                }
                event.setResult(result);
                if ( actions != null ) {
                    if ( result < 0 ) {
                        String errorMsg = conn.getErrorMsg();

                        if (errorMsg != null && errorMsg.trim().length() > 0)
                            msgs.add(errorMsg);

                        result = actions.postInsertFail(ctx,conn,fi,msgs,manager);
                        if ( result < 0 )
                            msgs.add(""+result);
                    } else {
                        result = actions.postInsertOk(ctx,conn,fi,msgs,manager);
                        if ( result >= 0 ) {
                            msgs.add("添加信息成功");
                        } else {
                            msgs.add(""+result);
                        }
                    }
                    event.setAfter_result(result);
                } else {
                    if ( result >= 0 ) {
                        msgs.add("添加信息成功");
                    } else {
                        String errorMsg = conn.getErrorMsg();
                        if (errorMsg != null && errorMsg.trim().length() > 0)
                            msgs.add(errorMsg);
                    }
                }

                if ( event.getResult() >= 0 && !manager.hasMoreEvent() ) {
                    manager.trigger(instanceid, EventType.INSERT_VIEW_EVENT_TYPE,
                                    Event.BRANCH_CONTINUE_TYPE, true);
                }

                return result;
            } else {
                event.setResult(ErrorCode.FORM_INSTANCE_VALUE_ERROR);
                return ErrorCode.FORM_INSTANCE_VALUE_ERROR;
            }
        } else {
            msgs.add(""+ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }
    }

    /**
     * 1、获得FORM实例管理器
     * 2、获得FORM实例
     * 3、如果event.isInstance=false,则加载该FORM
     * 4、构造SqlAssistor参数为formInstance和1（主键）
     * 5、执行formInsatnce的FormActions的beforeEdit()
     * 6、String sSql = assistor.toSql(select)
     * 7、 执行conn.executeQuery(sSql)，查找符合条件的记录
     * 8、取第一笔记录并更新实例的值
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7272D70077
     */
    public static int editview(SessionContext ctx, Event event, ErrorMessages msgs,
                               EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(SessionAttributes.
            SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        if (fi != null) {
            SqlAssistor sqlAssistor = new SqlAssistor(fi, SqlAssistor.PRIMARY_KEY_TYPE);
            FormActions actions = fi.getFormAction();
            int result = 0;
            if (actions != null) {
                result = actions.beforeEdit(ctx, conn, fi, msgs, manager, sqlAssistor);
                if ( result < 0 ) {
                    msgs.add(""+result);
                }
            }
            event.setBefore_result(result);
            if (result < 0) {
                return result;
            }

            String sSql = sqlAssistor.toSql(SqlAssistor.SELECT_SQL_TYPE);
            RecordSet rs = conn.executeQuery(sSql, 1, 1);
            if (!rs.next()) {
                result = ErrorCode.RECORD_SET_IS_NULL;
                event.setResult(ErrorCode.RECORD_SET_IS_NULL);
            } else {
                event.setResult(ErrorCode.SUCCESS_NO);

                fi.updateValue(rs);
            }

            return result;
        }
        else {
            msgs.add("" + ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }
    }

    /**
     * 1、获得FORM实例管理器
     * 2、如果event.isInstance()==false,则加载该FORM
     * 3、获得FORM实例
     * 4、执行validate()校验值
     * 5、构造SqlAssistor，参数formInstance和1(主键)
     * 6、执行formInsatnce的FormActions的preEdit()
     * 7、String sSql = assistor.toSql(edit)
     * 8、int rtn = conn.executeUpdate(sSql)
     * 9、成功执行formInsatnce的FormActions的postEditOk()
     *       失败执行formInsatnce的FormActions的postEditFail()
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7272E20195
     */
    public static int edit(SessionContext ctx, Event event, ErrorMessages msgs,
                           EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager)ctx.getAttribute(SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        if ( fi != null ) {
            if ( validate(fi,msgs) ) {
                SqlAssistor sqlAssistor = new SqlAssistor(fi,SqlAssistor.PRIMARY_KEY_TYPE);
                FormActions actions = fi.getFormAction();
                int result = 0;
                if ( actions != null ) {
                    result = actions.preEdit(ctx,conn,fi,msgs,manager,sqlAssistor);
                    if ( result < 0 ) {
                        msgs.add(""+result);
                    }
                }
                event.setBefore_result(result);
                if ( result < 0 ) {
                    return result;
                }

                String sSql = sqlAssistor.toSql(SqlAssistor.UPDATE_SQL_TYPE);
                result = conn.executeUpdate(sSql);
                event.setResult(result);
                if ( result == 0 )
                    result = ErrorCode.NO_RECORD_EDITED;
                event.setResult(result);

                if ( actions != null ) {
                    if ( result < 0 ) {
                        String errorMsg = conn.getErrorMsg();
                        if (errorMsg != null && errorMsg.trim().length() > 0)
                            msgs.add(errorMsg);

                        result = actions.postEditFail(ctx,conn,fi,msgs,manager);
                        if ( result < 0 )
                            msgs.add(""+result);
                    } else {
                        result = actions.postEditOk(ctx,conn,fi,msgs,manager);
                        if ( result >= 0 ) {
                            msgs.add("修改信息成功");
                        } else {
                            msgs.add(""+result);
                        }
                    }
                    event.setAfter_result(result);
                } else {
                    if (result >= 0) {
                        msgs.add("修改信息成功");
                    } else {
                        String errorMsg = conn.getErrorMsg();
                        if (errorMsg != null && errorMsg.trim().length() > 0)
                            msgs.add(errorMsg);
                    }
                }
                if ( event.getResult() >= 0 && !manager.hasMoreEvent() )
                    manager.trigger(instanceid,EventType.INSERT_VIEW_EVENT_TYPE,Event.BRANCH_CONTINUE_TYPE,true);

                return result;
            } else {
                event.setResult(ErrorCode.FORM_INSTANCE_VALUE_ERROR);
                return ErrorCode.FORM_INSTANCE_VALUE_ERROR;
            }
        } else {
            msgs.add(""+ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }

    }

    /**
     * 1、获得FORM实例管理器
     * 2、获得FORM实例
     * 3、如果event.isInstance=false,则加载该FORM
     * 4、构造SqlAssistor参数为formInstance和1（主键）
     * 5、执行formInsatnce的FormActions的beforeDelete()
     * 6、String sSql = assistor.toSql(select)
     * 7、 执行conn.executeQuery(sSql)，查找符合条件的记录
     * 8、取第一笔记录并更新实例的值
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7272E50168
     */
    public static int deleteview(SessionContext ctx, Event event, ErrorMessages msgs,
                                 EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(SessionAttributes.
            SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        if (fi != null) {
            SqlAssistor sqlAssistor = new SqlAssistor(fi, SqlAssistor.PRIMARY_KEY_TYPE);
            FormActions actions = fi.getFormAction();
            int result = 0;
            if (actions != null) {
                result = actions.beforeDelete(ctx, conn, fi, msgs, manager, sqlAssistor);
                if ( result < 0 )
                    msgs.add(""+result);
            }
            event.setBefore_result(result);
            if (result < 0) {
                return result;
            }

            String sSql = sqlAssistor.toSql(SqlAssistor.SELECT_SQL_TYPE);
            RecordSet rs = conn.executeQuery(sSql, 1, 1);
            if (rs.next())
                event.setResult(ErrorCode.RECORD_SET_IS_NULL);
            else {
                event.setResult(ErrorCode.SUCCESS_NO);
                fi.updateValue(rs);
            }

            return result;
        }
        else {
            msgs.add("" + ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }
    }

    /**
     * 1、获得FORM实例管理器
     * 2、如果event.isInstance()==false,则加载该FORM
     * 3、获得FORM实例
     * 4、构造SqlAssistor，参数formInstance和1(主键)
     * 5、执行formInsatnce的FormActions的preDelete()
     * 6、String sSql = assistor.toSql(delete)
     * 7、int rtn = conn.executeUpdate(sSql)
     * 8、成功执行formInsatnce的FormActions的postDeleteOk()
     *       失败执行formInsatnce的FormActions的postDeleteFail()
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7272EC0384
     */
    public static int delete(SessionContext ctx, Event event, ErrorMessages msgs,
                             EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager)ctx.getAttribute(SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        if ( fi != null ) {
            if ( validate(fi,msgs) ) {
                SqlAssistor sqlAssistor = new SqlAssistor(fi,SqlAssistor.PRIMARY_KEY_TYPE);
                FormActions actions = fi.getFormAction();
                int result = 0;
                if ( actions != null ) {
                    result = actions.preDelete(ctx,conn,fi,msgs,manager,sqlAssistor);
                    if ( result < 0 )
                        msgs.add(""+result);
                }
                event.setBefore_result(result);
                if ( result < 0 ) {
                    return result;
                }

                String sSql = sqlAssistor.toSql(SqlAssistor.DELETE_SQL_TYPE);
                result = conn.executeUpdate(sSql);
                event.setResult(result);
                if ( result == 0 ) {
                    result = ErrorCode.NO_RECORD_DELETED;
                }
                event.setResult(result);
                if ( actions != null ) {
                    if ( result < 0 ) {
                        String errorMsg = conn.getErrorMsg();
                        if (errorMsg != null && errorMsg.trim().length() > 0)
                            msgs.add(errorMsg);

                        result = actions.postDeleteFail(ctx,conn,fi,msgs,manager);
                        if ( result < 0 )
                            msgs.add(""+result);
                    } else {
                        result = actions.postDeleteOk(ctx,conn,fi,msgs,manager);
                        if ( result >= 0 ) {
                            msgs.add("删除信息成功");
                        } else {
                            msgs.add(""+result);
                        }
                    }
                    event.setAfter_result(result);
                } else {
                    if ( result >= 0 ) {
                        msgs.add("删除信息成功");
                    } else {
                        String errorMsg = conn.getErrorMsg();
                        if (errorMsg != null && errorMsg.trim().length() > 0)
                            msgs.add(errorMsg);
                    }
                }

                if ( event.getResult() >= 0 && !manager.hasMoreEvent() )
                    manager.trigger(instanceid,EventType.INSERT_VIEW_EVENT_TYPE,Event.BRANCH_CONTINUE_TYPE,true);

                return result;
            } else {
                event.setResult(ErrorCode.FORM_INSTANCE_VALUE_ERROR);
                return ErrorCode.FORM_INSTANCE_VALUE_ERROR;
            }
        } else {
            msgs.add(""+ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }

    }

    /**
     * 1、获得FORM实例管理器
     * 2、如果event.isInstance==false,则加载FORM
     * 3、获得FORM实例
     * 4、调用实例formInstance的initValue(方法初始化FORM实例的值
     * 5、执行formInsatnce的FormActions的beforeFind()
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7272F201C0
     */
    public static int findview(SessionContext ctx, Event event, ErrorMessages msgs, EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(SessionAttributes.
            SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        if (fi != null) {
            fi.initValues();
            FormActions actions = fi.getFormAction();
            int result = 0;
            if (actions != null) {
                result = actions.beforeFind(ctx, conn, fi, msgs, manager);
                if ( result < 0 )
                    msgs.add(""+result);
            }
            event.setResult(result);

            return result;
        }
        else {
            msgs.add("" + ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }
    }

    /**
     * 1、获得FORM实例管理器
     * 2、如果event.isInstance==false,则加载该FORM
     * 3、获得FORM实例
     * 4、if ( List Form ) 则
     *       1）pageno   = ctx.getRequestParameter(REQUEST_LIST_PAGENO_NAME)、 缺省为1
     *       2）pagesize = 从FORM定义中取，失败则从系统配置中取pagesize
     *       3）PreparedStatement ps = conn.getPS(form定义的sql)
     *       4）执行formInsatnce的FormActions的preFind()
     *       5）执行ps，并且设置了开始记录号和要取的记录数
     *       6）成功，则执行postFindOk()
     *             失败，则执行postFindFail()
     *       7）将结果保存在SessionContext中名称是REQUEST_FIND_RESULT_NAME
     *             ,注意调用setRequestAttribute
     * 5、if ( Page Form ) 则
     *      1）构造SqlAssistor,参数为formInstance和0（查询键）
     *      2）String sSql = assistor.toSql(select)
     *      3）conn.executeQuery(sSql)
     *      4）成功，则执行formInstance的FormActions的postFindOk()
     *            失败，则执行formInsatnce的FormActions的postFindFail()
     *      5）定位到第一条记录更新实例的值
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7272F603BB
     */
    public static int find(SessionContext ctx, Event event, ErrorMessages msgs,
                           EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(SessionAttributes.
            SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        int result = 0;
        if ( fi != null ) {
            String formid = fi.getFormid();
            FormBean fb = FormBeanManager.getForm(formid);
            if ( fb.getType() == fb.LIST_TYPE ) {
                String sPageno = ctx.getParameter(SessionAttributes.REQUEST_LIST_PAGENO_NAME);
                String sPageCount = ctx.getParameter(SessionAttributes.REQUEST_LIST_PAGECOUNT_NAME);
                int pageno = 0;
                int pagecount = 0;
                try {
                    pageno = Integer.parseInt(sPageno);
                } catch ( Exception e ) {
                    pageno = 0;
                }
                try {
                    pagecount = Integer.parseInt(sPageCount);
                } catch ( Exception e ) {
                    pagecount = 0;
                }

                int pagesize = fb.getRows();
                if ( pagesize == 0 )
                   pagesize = PropertyManager.getIntProperty(SessionAttributes.REQUEST_LIST_PAGESIZE_NAME);

               String sSql = fb.getListsql();
               String countSql = fb.getCountsql();
               if ( sSql == null || countSql == null ) {
                   msgs.add("" + ErrorCode.LIST_SQL_DEFINED_ERROR);
                   event.setResult(ErrorCode.LIST_SQL_DEFINED_ERROR);
                   return ErrorCode.LIST_SQL_DEFINED_ERROR;
               }

               PreparedStatement ps = conn.getPreparedStatement(sSql);
               PreparedStatement countps = conn.getPreparedStatement(countSql);

               FormActions fa = fi.getFormAction();

               if ( fa != null ) {
                   result = fa.preFind(ctx,conn,fi,msgs,manager,ps,countps);
                   if ( result < 0 )
                       msgs.add(""+result);
               }

               RecordSet rs1 = conn.executeQuery(countps);
               if (rs1.next()) {
                   pagecount = rs1.getInt(0);
                   if (pagesize != 0)
                       pagecount = (pagecount - 1) / pagesize;
               }

               event.setBefore_result(result);
               RecordSet rs = conn.executeQuery(ps,(pageno*pagesize)+1,pagesize);
               if ( rs.getRecordCount() <= 0 ) {
                   event.setResult(ErrorCode.RECORD_SET_IS_NULL);
                   msgs.add(""+ErrorCode.RECORD_SET_IS_NULL);
                   String errorMsg = conn.getErrorMsg();
                   if ( errorMsg != null && errorMsg.trim().length() > 0 )
                       msgs.add(errorMsg);
                   if ( fa != null ) {
                       result = fa.postFindFail(ctx, conn, fi, msgs, manager);
                       if ( result < 0 )
                           msgs.add(""+result);
                   }
               }
               else {
                   event.setResult(0);
                   if ( fa != null ) {
                       result = fa.postFindOk(ctx, conn, fi, msgs, manager, rs);
                       if ( result < 0 )
                           msgs.add(""+result);
                   }
               }
               try {
                ps.close();
                countps.close();
               } catch (Exception e ) {System.out.println(e.getMessage());}

               if ( result >= 0 ) {
                   ctx.setRequestAtrribute(SessionAttributes.REQUEST_LIST_PAGENO_NAME,""+pageno);
                   ctx.setRequestAtrribute(SessionAttributes.REQUEST_LIST_PAGECOUNT_NAME,""+pagecount);
                   ctx.setRequestAtrribute(SessionAttributes.REQUEST_FIND_RESULT_NAME,rs);
               }
               return result;
            } else if ( fb.getType() == fb.QUERY_TYPE ) {
                String sPageno = ctx.getParameter(SessionAttributes.REQUEST_LIST_PAGENO_NAME);
                String sPageCount = ctx.getParameter(SessionAttributes.REQUEST_LIST_PAGECOUNT_NAME);
                int pageno = 0;
                int pagecount = 0;
                try {
                    pageno = Integer.parseInt(sPageno);
                } catch ( Exception e ) {
                    pageno = 0;
                }
                try {
                    pagecount = Integer.parseInt(sPageCount);
                } catch ( Exception e ) {
                    pagecount = 0;
                }

                int pagesize = fb.getRows();
                if ( pagesize == 0 )
                   pagesize = PropertyManager.getIntProperty(SessionAttributes.REQUEST_LIST_PAGESIZE_NAME);

               String sSql = fb.getListsql();
               String countSql = fb.getCountsql();
               if ( sSql == null || countSql == null ) {
                   msgs.add("" + ErrorCode.LIST_SQL_DEFINED_ERROR);
                   event.setResult(ErrorCode.LIST_SQL_DEFINED_ERROR);
                   return ErrorCode.LIST_SQL_DEFINED_ERROR;
               }

               FormActions fa = fi.getFormAction();

               if ( fa != null ) {
                   try {
                       SqlWhereUtil sqlWhereUtil = new SqlWhereUtil();
                       result = fa.preFind(ctx, conn, fi, msgs, manager, sqlWhereUtil);
                       if ( result < 0 )
                           msgs.add(""+result);
                       if ( sSql != null && countSql != null ) {
                        if (sSql.toLowerCase().indexOf(" where ") > 0) {
                            String tmp = sqlWhereUtil.toSqlWhere(false);
                            if ( tmp.length() > 0 && tmp.trim().length() > 0 ) {
//                                sSql  += " and ( " + tmp +" ) ";
//                                countSql += " and ( " + tmp +" ) ";
                                if ( sSql != null )
                                    sSql = sSql.replaceFirst("1=1",tmp);
                                if ( countSql != null )
                                    countSql = countSql.replaceFirst("1=1",tmp);
                            }
                        }
                        else {
                            String tmp = sqlWhereUtil.toSqlWhere(true);
                            if ( tmp.length() > 0 && tmp.trim().length() > 0 ) {
//                                sSql  += tmp ;
//                                countSql += tmp;
                                if ( sSql != null )
                                    sSql = sSql.replaceFirst("1=1",tmp);
                                if ( countSql != null )
                                    countSql = countSql.replaceFirst("1=1",tmp);
                            }
                        }
                    }

                   } catch ( Exception e ) {
                       e.printStackTrace();
                   }
               }
               PreparedStatement ps = conn.getPreparedStatement(sSql);
               PreparedStatement countps = conn.getPreparedStatement(countSql);

               RecordSet rs1 = conn.executeQuery(countps);
               if (rs1.next()) {
                   pagecount = rs1.getInt(0);
                   if (pagesize != 0)
                       pagecount = (pagecount - 1) / pagesize;
               }

               event.setBefore_result(result);
               RecordSet rs = conn.executeQuery(ps,(pageno*pagesize)+1,pagesize);

               if ( rs.getRecordCount() <= 0 ) {
//                   result = ErrorCode.RECORD_SET_IS_NULL;
                   event.setResult(ErrorCode.RECORD_SET_IS_NULL);
                   msgs.add(""+ErrorCode.RECORD_SET_IS_NULL);
                   String errorMsg = conn.getErrorMsg();
                   if ( errorMsg != null && errorMsg.trim().length() > 0 )
                       msgs.add(errorMsg);

                   if ( fa != null ) {
                       result = fa.postFindFail(ctx, conn, fi, msgs, manager);
                       if ( result < 0 )
                           msgs.add(""+result);
                   }
               }
               else {
                   event.setResult(0);
                   if ( fa != null ) {
                       result = fa.postFindOk(ctx, conn, fi, msgs, manager, rs);
                       if ( result < 0 )
                           msgs.add(""+result);
                   }
               }
               try {
                   ps.close();
                   countps.close();
               } catch (Exception e ) {System.out.println(e.getMessage());}

               if ( result >= 0 ) {
                   ctx.setRequestAtrribute(SessionAttributes.REQUEST_LIST_PAGENO_NAME,""+pageno);
                   ctx.setRequestAtrribute(SessionAttributes.REQUEST_LIST_PAGECOUNT_NAME,""+pagecount);
                   ctx.setRequestAtrribute(SessionAttributes.REQUEST_FIND_RESULT_NAME,rs);
               }
               fi.initValues();
               return result;

            }else {
                SqlAssistor sqlAssistor = new SqlAssistor(fi, SqlAssistor.SEARCH_KEY_TYPE);
                FormActions actions = fi.getFormAction();
                if (actions != null) {
                    result = actions.preFind(ctx, conn, fi, msgs, manager, sqlAssistor);
                    if ( result < 0 )
                        msgs.add(""+result);
                }
                event.setBefore_result(result);
                if (result < 0) {
                    return result;
                }

                String sSql  = sqlAssistor.toSql(SqlAssistor.SELECT_SQL_TYPE);
                RecordSet rs = conn.executeQuery(sSql, 1, 1);
                if ( rs.next() ) {
                    if ( actions != null ) {
                        result = actions.postFindOk(ctx, conn, fi, msgs, manager, rs);
                        if ( result < 0 )
                            msgs.add(""+result);
                        event.setAfter_result(result);
                    }
                    event.setResult(ErrorCode.SUCCESS_NO);
                    if ( result >= 0 )
                        fi.updateValue(rs);
                } else {
                    String errorMsg = conn.getErrorMsg();
                   if ( errorMsg != null && errorMsg.trim().length() > 0 )
                       msgs.add(errorMsg);

                    if ( actions != null ) {
                        result = actions.postFindFail(ctx, conn, fi, msgs, manager);
                        if ( result < 0 )
                            msgs.add(""+result);
                        event.setAfter_result(result);
                    }
                    msgs.add("信息不存在,请重新查找");
                    fi.initValues();
                    event.setResult(ErrorCode.RECORD_SET_IS_NULL);
                    if ( !manager.hasMoreEvent() ) {
                        manager.trigger(instanceid,EventType.INSERT_VIEW_EVENT_TYPE,Event.BRANCH_CONTINUE_TYPE,true);
                    }
                }

                return result;
            }
        } else {
            msgs.add("" + ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }
    }

    /**
     * 处理按钮触发事件
     *
     * 设计思想：
     * 点击了某个按钮，提交其类型
     * 约定，在网页中添加隐含标签
     * <input type="hidden" name="Plat_Form_Request_Button_Event" value=""/>
     * 当点击某按钮时,将button元素的值置为相应的标签名称。
     * 缺省事件是Plat_Form_Request_Button_Delete,即删除所有的记录
     * checkbox的名称为recordnos,它们的值是由searchKey组成的串
     *
     * 步骤
     * 1、获得FORM实例管理器
     * 2、如果event.isInstance==false则加载该FORM
     * 3、获得FORM实例
     * 4、button= ctx.getAttribute(REQUEST_BUTTON_EVENT_NAME)
     * 5、if ( button.equals(RQUEST_BUTTON_DELETE_NAME) ) {
     *          删除记录（取recordnos的值组）
     *          然后触发当前实例的Find事件
     *          return
     *     } else {
     *          执行formInsatnce的FormActions的buttonEvent()
     *     }
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F72737701B8
     */
    public static int button(SessionContext ctx, Event event, ErrorMessages msgs,
                             EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(SessionAttributes.
            SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        int result = 0;
        String button = ctx.getParameter(SessionAttributes.REQUEST_BUTTON_EVENT_NAME);
        if ( fi == null ) {
            msgs.add("" + ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }

        FormBean fb = FormBeanManager.getForm(fi.getFormid());
        String tbl = fb.getTbl();
        if ( button != null ) {
            if ( button.equals(SessionAttributes.REQUEST_BUTTON_DELETE_NAME) ) {
                String[] recordnos = ctx.getParameters(SessionAttributes.REQUEST_DELETE_RANGE_NAME);
                List results = new ArrayList();
                if ( recordnos != null ) {
                    for ( int i = 0 ; i < recordnos.length ; i++ ) {
                        try {
                            if ( recordnos[i] != null && recordnos[i].trim().length() > 0 ) {
                                String sSql = "delete from " + tbl + " where " + recordnos[i];
                                int thisresult = conn.executeUpdate(sSql);
                                if ( thisresult <= 0 ) {
                                    results.add(""+i);
                                    msgs.add(""+thisresult);
                                }
                            }
                        } catch ( Exception e ) {
                            msgs.add(e.getMessage());
                            results.add(""+i);
                            e.printStackTrace();
                        }
                    }
                    if ( results.size() > 0 ) {
                        String err = "第";
                        for (int i = 0; i < results.size(); i++) {
                            err += results.get(i);
                            if (results.size() < results.size() - 1) {
                                err += ",";
                            }
                            else {
                                err += "记录删除失败";
                            }
                        }
                        msgs.add(err);
                    }
                }
                manager.trigger(instanceid,EventType.FIND_EVENT_TYPE,Event.BRANCH_CONTINUE_TYPE,true);

                return 0;
            } else {
                if ( fi != null ) {
                    FormActions fa = fi.getFormAction();
                    if ( fa != null ) {
                        result = fa.buttonEvent(ctx, conn, fi, button, msgs, manager);
                        if ( result < 0 )
                            msgs.add(""+result);
                    }
                    if ( !manager.hasMoreEvent() ) {
                        if ( fb.getType() == fb.LIST_TYPE ) {
                            manager.trigger(instanceid, EventType.FIND_EVENT_TYPE,
                                            Event.BRANCH_CONTINUE_TYPE, true);
                        }else {
                            manager.trigger(instanceid, EventType.INSERT_VIEW_EVENT_TYPE,
                                            Event.BRANCH_CONTINUE_TYPE, true);
                        }
                    }
                }
                return result;
            }
        } else {
            msgs.add("" + ErrorCode.BUTTON_EVENT_ERROR);
            event.setResult(ErrorCode.BUTTON_EVENT_ERROR);
            return ErrorCode.BUTTON_EVENT_ERROR;
        }
    }

    /**
     * 参考行文本查询
     *
     * 1、获得FORM实例管理器
     * 2、如果event.isInstance()==false,则加载该FORM
     * 3、获得FORM实例
     * 4、获取参考字段的名称ctx.getParameter(SessionAttributes.REQUEST_REFERENCE_FIELD_
     * NAME)
     * 5、根据定义组成SQL语句，构造PreparedStatement ps
     * 5、执行formInsatnce的FormActions的preDelete()
     * 6、执行ps,得到查询结果,并存放ctx中，setRequestAttribute(SessionAttributes.REQUES
     * T_REFERENCE_RESULT_NAME)
     * 7、成功执行formInsatnce的FormActions的postDeleteOk()
     *       失败执行formInsatnce的FormActions的postDeleteFail()
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F7EB8A303CC
     */
    public static int reference(SessionContext ctx, Event event, ErrorMessages msgs, EventManager manager,DatabaseConnection conn)
    {
        FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(SessionAttributes.
            SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();
        FormInstance fi = fiManager.getFormInstance(instanceid);
        int result = 0;
        if ( fi != null ) {
            String reffldnm = ctx.getParameter(SessionAttributes.REQUEST_REFERENCE_FIELD_NAME);
            //如果是QueryForm的过滤参考性字段，则截掉largelarge(在QueryGenerator中添加的)
            if ( reffldnm != null && reffldnm.endsWith("largelarge") ) {
                reffldnm = reffldnm.substring(0,reffldnm.length()-"largelarge".length());
            }
            String formid = fi.getFormid();
            FormBean fb = FormBeanManager.getForm(formid);
            if (fb == null) {
                msgs.add("" + ErrorCode.REF_GET_FORMBEAN_ERROR);
                event.setResult(ErrorCode.REF_GET_FORMBEAN_ERROR);
                return ErrorCode.REF_GET_FORMBEAN_ERROR;
            }

            String sSql  = fi.getRefSQL(ctx,reffldnm);
            String sSql1 = fi.getRefCountSQL(ctx,reffldnm);

            if ( sSql == null || sSql1 == null ) {
                msgs.add("" + ErrorCode.REF_DEFINED_ERROR);
                event.setResult(ErrorCode.REF_DEFINED_ERROR);
                return ErrorCode.REF_DEFINED_ERROR;
            }

            String sPageno    = ctx.getParameter(SessionAttributes.REQUEST_LIST_PAGENO_NAME);
            String sPageCount = ctx.getParameter(SessionAttributes.REQUEST_LIST_PAGECOUNT_NAME);
            int pageno = 0;
            int pagecount = 0;
            try {
                pageno = Integer.parseInt(sPageno);
            } catch ( Exception e ) {
                pageno = 0;
            }
            try {
                pagecount = Integer.parseInt(sPageCount);
            } catch ( Exception e ) {
                pagecount = 0;
            }
            int pagesize = fb.getRows();
            if (pagesize == 0)
                pagesize = PropertyManager.getIntProperty(SessionAttributes.
                    REQUEST_LIST_PAGESIZE_NAME);

            FormActions fa = fi.getFormAction();

            if ( fa != null ) {
                try {
                    SqlWhereUtil sqlWhereUtil = new SqlWhereUtil();
                    result = fa.preReference(ctx, conn, fi, msgs, manager, reffldnm,sqlWhereUtil);
                    if ( sSql != null && sSql1 != null ) {
                        if (sSql.toLowerCase().indexOf(" where ") > 0) {
                            String tmp = sqlWhereUtil.toSqlWhere(false);
                            if ( tmp!= null && tmp.trim().length() > 0 ) {
                                sSql  += " and ( " + tmp +" ) ";
                                sSql1 += " and ( " + tmp +" ) ";
                            }
                        }
                        else {
                            String tmp = sqlWhereUtil.toSqlWhere(true);
                            if ( tmp != null && tmp.trim().length() > 0 ) {
                                sSql  += tmp ;
                                sSql1 += tmp;
                            }
                        }
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }

            PreparedStatement ps = conn.getPreparedStatement(sSql);
            PreparedStatement countps = conn.getPreparedStatement(sSql1);

            RecordSet rs1 = conn.executeQuery(countps);
            if (rs1.next()) {
                pagecount = rs1.getInt(0);
                if (pagesize != 0)
                    pagecount = (pagecount - 1) / pagesize;
            }

            event.setBefore_result(result);
            if ( result < 0 ) {
                return result;
            }
            RecordSet rs = conn.executeQuery(ps,(pageno*pagesize+1),pagesize);

            if ( rs.getRecordCount() > 0 ) {
                event.setResult(0);
                if ( fa != null )
                    result = fa.postReferenceOk(ctx,conn,fi,msgs,manager,reffldnm,rs);
            } else {
                if ( fa != null )
                    result = fa.postReferenceFail(ctx,conn,fi,msgs,manager,reffldnm,rs);
                msgs.add(""+ ErrorCode.RECORD_SET_IS_NULL);
                event.setResult(ErrorCode.RECORD_SET_IS_NULL);
            }
            try {
                ps.close();
                countps.close();
            } catch (Exception e ) {System.out.println(e.getMessage());}
            event.setAfter_result(result);
            ctx.setRequestAtrribute(SessionAttributes.REQUEST_REF_RESULT_NAME,rs);
            ctx.setRequestAtrribute(SessionAttributes.REQUEST_LIST_PAGENO_NAME,""+pageno);
            ctx.setRequestAtrribute(SessionAttributes.REQUEST_LIST_PAGECOUNT_NAME, "" + pagecount);

            return result;
        } else {
            msgs.add("" + ErrorCode.FORM_INSTANCE_NOT_EXIST);
            event.setResult(ErrorCode.FORM_INSTANCE_NOT_EXIST);
            return ErrorCode.FORM_INSTANCE_NOT_EXIST;
        }
    }

    public static int insertSmallQuery(SessionContext ctx, Event event, ErrorMessages msgs, EventManager manager,DatabaseConnection conn) {
        try {
            FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(
                SessionAttributes.
                SESSION_FORM_INSTANCE_MANAGER_NAME);
            String instanceid = event.getId();
            FormInstance fi = fiManager.getFormInstance(instanceid);
            if ( fi != null ) {
                FormActions fa = fi.getFormAction();
                if ( fa != null ) {
                    fa.insertSmallQueryEvent(ctx,conn,fi,msgs,manager);
                }
            }

        } catch ( Exception e ) {
            msgs.add(e.getMessage());
            e.printStackTrace();
        }
        if ( !manager.hasMoreEvent() ) {
            event.setType(EventType.INSERT_VIEW_EVENT_TYPE);
        }
        return 0;
    }

    public static int editSmallQuery(SessionContext ctx, Event event, ErrorMessages msgs, EventManager manager,DatabaseConnection conn) {
        try {
            FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(
                SessionAttributes.
                SESSION_FORM_INSTANCE_MANAGER_NAME);
            String instanceid = event.getId();
            FormInstance fi = fiManager.getFormInstance(instanceid);
            if ( fi != null ) {
                FormActions fa = fi.getFormAction();
                if ( fa != null ) {
                    fa.editSmallQueryEvent(ctx,conn,fi,msgs,manager);
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
            msgs.add(e.getMessage());
        }
        if ( !manager.hasMoreEvent() ) {
            event.setType(EventType.EDIT_VIEW_EVENT_TYPE);
        }

        return 0;
    }

    /**
     * 处理指定的系统事件
     *
     * 根据event的类型type，选择相应的方法进行处理
     * @param ctx
     * @param event
     * @param msgs
     * @param manager
     * @return int
     * @roseuid 3F72730D0061
     */
    public static int process(SessionContext ctx, Event event, ErrorMessages msgs,
                              EventManager manager,DatabaseConnection conn)
    {
        switch ( event.getType() ) {
            case EventType.BUTTON_EVENT_TYPE :
                return button(ctx,event,msgs,manager,conn);
            case EventType.DELETE_EVENT_TYPE :
                return delete(ctx,event,msgs,manager,conn);
            case EventType.DELETE_VIEW_EVENT_TYPE :
                return deleteview(ctx,event,msgs,manager,conn);
            case EventType.EDIT_EVENT_TYPE :
                return edit(ctx,event,msgs,manager,conn);
            case EventType.EDIT_VIEW_EVENT_TYPE :
                return editview(ctx,event,msgs,manager,conn);
            case EventType.FIND_EVENT_TYPE :
                return find(ctx,event,msgs,manager,conn);
            case EventType.FIND_VIEW_EVENT_TYPE :
                return findview(ctx,event,msgs,manager,conn);
            case EventType.INSERT_EVENT_TYPE :
                return insert(ctx,event,msgs,manager,conn);
            case EventType.INSERT_VIEW_EVENT_TYPE :
                return insertview(ctx,event,msgs,manager,conn);
            case EventType.LOAD_EVENT_TYPE :
                return load(ctx,event,msgs,manager,conn);
            case EventType.REFERENCE_FIELD_EVENT_TYPE :
                return reference(ctx,event,msgs,manager,conn);
            case EventType.UNLOAD_EVENT_TYPE :
                return unload(ctx,event,msgs,manager,conn);
            case EventType.INSERT_SMALL_QUERY_EVENT_TYPE :
                return insertSmallQuery(ctx,event,msgs,manager,conn);
            case EventType.EDIT_SMALL_QUERY_EVENT_TYPE :
                return editSmallQuery(ctx,event,msgs,manager,conn);
            default :
                return 0;
        }
    }

    /**
     * 校验每个栏位值的合法性
     * @param instance
     * @param msgs
     * @return boolean
     * @roseuid 3F728E3B0192
     */
    public static boolean validate(FormInstance instance, ErrorMessages msgs)
    {
        return true;
    }
}
