//Source file: D:\\zt\\platform\\form\\control\\ViewController.java

package pub.platform.form.control;

import pub.platform.form.config.*;
import pub.platform.form.util.*;
import pub.platform.form.util.event.*;

/**
 * 根据FORM实例产生FORM的HTML编码
 *
 * @author 请替换
 * @version 1.0
 */
public class ViewController {

    /**
     * 根据处理产生FORM的脚本字符串
     *
     * 流程如下：
     * 1、如果是参考事件，则产生参考输出
     *       return ReferenceGenerator.run()
     * 2、取实例IDinstanceid=event.getInstanceid()
     * 3、获得FORM实例管理器
     * 4、获得实例标识为instanceid的实例formInstance
     * 5、根据formid获得Form实例
     * 6、如果该form是List类型，并且是Find事件则
     *       if ( 查询出错 ) {
     *           根据msg组织出错信息
     *           return 出错信息
     *       } else
     *            return ListGenerator.run()
     * 7、其他的调用PageGenerator.run()产生脚本
     * @param ctx
     * @param event
     * @param msg
     * @param result
     * @return String
     * @roseuid 3F722D6E0372
     */
    public static String[] process(SessionContext ctx, Event event, ErrorMessages msgs, int result)
    {
        String[] rtnMsg = new String[2];
        if (event.getType() == EventType.REFERENCE_FIELD_EVENT_TYPE) {
            return ReferenceGenerator.run(ctx, event, msgs, result);
        }
        FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(SessionAttributes.
            SESSION_FORM_INSTANCE_MANAGER_NAME);
        String instanceid = event.getId();

        FormInstance fi = fiManager.getFormInstance(instanceid);

        FormBean fb = FormBeanManager.getForm(fi.getFormid());
        if (fb.getType() == fb.LIST_TYPE && event.getType() == EventType.FIND_EVENT_TYPE) {
            if (result < 0) {
                rtnMsg[0] = printErrorMsgs(msgs,result);
                return rtnMsg;
            }
            else {
                return ListGenerator.run(ctx, event,msgs,result);
            }
        } else if ( fb.getType() == fb.QUERY_TYPE ) {
            return QueryGenerator.run(ctx, event,msgs,result);
        } else {
            return PageGenerator.run(ctx, event, msgs, result);
        }
    }

    public static String printErrorMsgs(ErrorMessages msgs,int result)
    {
        return PageGenerator.getErrorString(msgs,result);
    }
}