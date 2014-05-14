package pub.platform.form.control;

import pub.platform.advance.utils.PropertyManager;
import pub.platform.db.ConnectionManager;
import pub.platform.db.DatabaseConnection;
import pub.platform.form.config.FormBean;
import pub.platform.form.config.FormBeanManager;
import pub.platform.form.config.SystemAttributeNames;
import pub.platform.form.util.FormInstance;
import pub.platform.form.util.FormInstanceManager;
import pub.platform.form.util.SessionAttributes;
import pub.platform.form.util.event.ErrorMessages;
import pub.platform.form.util.event.Event;
import pub.platform.form.util.event.EventManager;
import pub.platform.form.util.event.EventType;
import pub.platform.utils.ErrorCode;

/**
 * FORM请求处理的中央控制器
 *
 * 负责处理所有的FORM请求，根据请求的ID和事件ID调度不同工作对进行处理，它的具体工作
 * 由ControllerAssistor完成
 *
 * @author 请替换
 * @version 1.0
 */
public class ActionController {
    private SessionContext ctx;
    public static final int POOL_MIN_SIZE = 20;

    /**
     * @param ctx
     * @roseuid 3F724F850093
     */
    public ActionController(SessionContext ctx)
    {
        this.ctx = ctx;
    }

    /**
     * FORM请求中控处理
     *
     * 成功返回 0
     * 失败返回 错误代号(<0)
     *
     * 执行逻辑如下：
     * 1、获得FORM实例管理器（FormInstanceManager）formInstanceManager
     *
     * formInstanceManager=ctx.getAttribute(SessionAttribute.SESSION_FORM_INSTANCE_MANA
     * GER_NAME);
     *      if ( formInstanceManager == null ) {
     *          formInstanceManager = new FormInstanceManager();
     *          ctx.setAttribute();
     *      }
     * 2、获取请求的Formid、Instanceid、eventId
     *     instanceid=ctx.getParameter(SessionAtribute.REQUEST_INSTANCE_ID_NAME)
     *     formid=ctx.getParameter(SessionAtribute.REQUEST_FORM_ID_NAME)
     *     eventtp=ctx.getParameter(SessionAttribute.REQUEST_EVENT_ID_NAME)
     * 3、创建事件管理器，并触发第一个请求事件
     *     if ( instanceid!=null ) {
     *         if ( instanceid not exist ) {
     *              ctx.setHead("请求错误")
     *              ctx.setBody("实例ID="+instanceid+"不存在,非法的FORM请求")
     *              ctx.setTail("")
     *              return;
     *         }
     *         if ( eventid 不合法) {
     *              ctx.setHead("请求错误")
     *              ctx.setBody("事件ID="+eventid+"不存在,非法的EVENT请求")
     *              ctx.setTail("")
     *              return;
     *          }
     *          id = instanceid;
     *          isInstance = true;
     *      } else if ( formid!=null) {
     *          if ( formid not exist ) {
     *               ctx.setHead("请求错误")
     *               ctx.setBody("FORM ID="+formid+"不存在,非法的FORM请求")
     *               ctx.setTail("")
     *               return;
     *          }
     *          if ( eventid 不合法 ) {
     *               ctx.setHead("请求错误")
     *               ctx.setBody("事件ID="+eventid+"不存在,非法的EVENT请求")
     *               ctx.setTail("")
     *               return;
     *          }
     *          id = formid;
     *          isInstance = false;
     *      } else {
     *          ctx.setHead("请求错误")
     *          ctx.setBody("实例ID和窗体ID为空，非法的FORM请求")
     *          ctx.setTail("")
     *          return;
     *      }
     *
     *      manager = New EventManager()
     *      Event firstEvent = new Event(id, eventtp,isInstance)
     *      manager.trigger(firstEvent)
     *      errorMessages = new EventMessages()
     *      Event event
     *      int  result=0;
     *      如果是实例请求则将请求参数值保存到实例当中
     * 4、申请数据库连接
     * 5、处理事件
     *       while ( manager.hasMoreEvent() ) {
     *
     *           5、开始事务
     *           event = manager.nextEvent();
     *           if ( event == null )
     *               break;
     *           result = ControllerAssistor.process(ctx,event,errorMessages,manager) ;
     *           if ( result >= 0 ) 提交事务 sysEvent=event，else
     * 回滚数据库事务sysEvent=firstEvent
     *           if (result < 0 || 是break属性的事件)
     *               break;
     *       }
     *
     * 6、如果ctx.getHead==null,则ctx.setHead(FORM定义的Title)
     * 7、生成Form主体
     *       String body=ViewController.process(ctx,sysEvent,errorMessages,result)
     * 8、将FORM主体保存
     *       ctx.setBody(body);
     * @return int
     * @roseuid 3F7226D502D3
     */
    public int run()
    {
        try {
            //1.获得FormInstance管理器
            FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(
                SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME);
            if (fiManager == null) {
                int size = PropertyManager.getIntProperty(SystemAttributeNames.
                    FORM_INSTANCE_POOL_SIZE);
                if (size < POOL_MIN_SIZE) {
                    size = POOL_MIN_SIZE;
                }
                fiManager = new FormInstanceManager(size);
                ctx.setAttribute(SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME, fiManager);
            }
            //2.构造触发事件
            Event firstEvent = findEvent(fiManager);
            if (firstEvent == null)
                return ErrorCode.ERROR_NOT_EVENT;

            EventManager eManager = new EventManager();
            eManager.trigger(firstEvent);

            ErrorMessages errMsgs = new ErrorMessages();
            Event event = null ;
            int result = 0;
            if ( firstEvent.isInstance() ) {
                FormInstance fi = fiManager.getFormInstance(firstEvent.getInstanceid());
                if ( fi != null ) {
                    fi.updateValue(ctx);
                }
            }
            //3.申请数据库资源
            ConnectionManager manager = ConnectionManager.getInstance();
            DatabaseConnection con = manager.getConnection();
            try {
                //4.处理事件
                while (eManager.hasMoreEvent() && result >= 0) {
                    event = eManager.nextEvent();
                    con.begin();
                    result = ControllerAssistor.process(ctx, event, errMsgs, eManager, con);
                    if (result < 0) {
                        con.rollback();
                    }
                    else {
                        con.commit();
                    }

                    if (event.getBranchType() == event.BRANCH_BREAK_TYPE)
                        break;
                }
            } catch ( Exception e ) {
                e.printStackTrace();
                con.rollback();
            } catch ( Throwable t ) {
                System.out.println("=====================发生致命错误==========================");
                t.printStackTrace();
                System.out.println("=====================发生致命错误==========================");
                con.rollback();
            }

            manager.releaseConnection(con);

            if ( ctx.needForward() ) {
                return 0;
            }

            if ( event.getType() == EventType.LOAD_EVENT_TYPE && result < 0 ) {
                ctx.setHead("加载失败");
                if ( errMsgs.size() > 0 ) {
                    ctx.setBody(PageGenerator.getErrorString(errMsgs,result));
                }
//                ctx.setTarget();
                return -1;
            }

            //5.设置FORM的Title
            if ( ctx.getHead() == null && event != null ) {
                 FormBean formBean = FormBeanManager.getForm(fiManager.getFormInstance(event.getInstanceid()).getFormid());
                 ctx.setHead(formBean.getTitle());
            }
            //6.形成Form的主体
            String[] body = ViewController.process(ctx,event,errMsgs,result);
            if ( body != null ) {
                if ( body.length >= 1 ) {
                    ctx.setBody(body[0]);
                }
                if ( body.length >= 2 ) {
                    ctx.setSysButton(body[1]);
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            ctx.setHead("发生例外");
            ctx.setBody("系统资源忙，稍后继续");
            ctx.setTail("");
            return ErrorCode.EXCEPTION_THROWN;
        }
    }

    private Event findEvent(FormInstanceManager manager)
    {
        String formid = ctx.getParameter(SessionAttributes.REQUEST_FORM_ID_NAME);
        String instanceid = ctx.getParameter(SessionAttributes.REQUEST_INSATNCE_ID_NAME);
        String eventid = ctx.getParameter(SessionAttributes.REQUEST_EVENT_ID_NAME);
        String initParameters = ctx.getParameter(SessionAttributes.
                                                 REQUEST_FORM_INIT_PARAMETERS_NAME);

        String id = "";
        int eventType = EventType.LOAD_EVENT_TYPE;
        boolean isInstance = false;
        try {
            if (instanceid != null && instanceid.trim().length() > 0) {
                instanceid = instanceid.trim();
                if (manager.getFormInstance(instanceid) == null) {
                    ctx.setHead("请求错误");
                    ctx.setBody("实例ID=" + instanceid + "不存在,非法的FORM请求");
                    ctx.setTail("");
                    return null;
                }
                if (eventid == null || !EventType.validate(eventid)) {
                    ctx.setHead("请求错误");
                    ctx.setBody("事件ID=" + eventid + "不存在,非法的EVENT请求");
                    ctx.setTail("");
                    return null;
                }
                id = instanceid;
                isInstance = true;
            }
            else if (formid != null) {
                formid = formid.trim();
                if (FormBeanManager.getForm(formid) == null) {
                    ctx.setHead("请求错误");
                    ctx.setBody("FORM ID=" + formid + "不存在,非法的FORM请求");
                    ctx.setTail("");
                    return null;
                }
                if (eventid == null || !eventid.equals("" + EventType.LOAD_EVENT_TYPE)) {
                    ctx.setHead("请求错误");
                    ctx.setBody("事件ID=" + eventid + "不存在,非法的EVENT请求");
                    ctx.setTail("");
                    return null;
                }
                id = formid;
                isInstance = false;
            }
            else {
                ctx.setHead("请求错误");
                ctx.setBody("实例ID和窗体ID为空，非法的FORM请求");
                ctx.setTail("");
                return null;
            }

            eventType = Integer.parseInt(eventid);
        } catch (Exception e) {
            return null;
        }

        return new Event(id,eventType,Event.BRANCH_CONTINUE_TYPE, isInstance, initParameters);
    }
}