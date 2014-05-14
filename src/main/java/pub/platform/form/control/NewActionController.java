//Source file: D:\\zt\\platform\\form\\control\\ActionController.java

package pub.platform.form.control;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pub.platform.form.control.ActionConfig.LogicAct;
import pub.platform.security.OperatorManager;
import pub.platform.utils.SQLRequest;
import pub.platform.utils.SQLResponse;

/**
 * For WZNJ
 *
 * @author 请替换
 * @version 1.0
 */
public class NewActionController {
	private static final Log logger = LogFactory.getLog(NewActionController.class);
	
    private SessionContext ctx;
    public static String REQUEST_XML_NAME  = "sys_request_xml";
    public static String RESPONSE_XML_NAME = "sys_response_xml";

    /**
     * @param ctx
     * @roseuid 3F724F850093
     */
    public NewActionController(SessionContext ctx) {
        this.ctx = ctx;
    }

    public int run() {

        String reqXml = ctx.getParameter(REQUEST_XML_NAME);

        SQLRequest sqlReq = new SQLRequest();
        SQLResponse sqlRes = new SQLResponse();

        boolean result = sqlReq.setXMLStr(reqXml);
        logger.debug("请求的XML:"+reqXml);
        ctx.setRequestAtrribute(this.RESPONSE_XML_NAME,sqlRes);

        if ( !result ) {
            sqlRes.setResult(true);
            sqlRes.setType(0);
            sqlRes.setMessage("解析请求XML文件失败，xml=["+reqXml+"]");
            return -1;
        }

        int actionCount  = sqlReq.getActionCount();

        for (int i = 0 ; i < actionCount; i++) {

            try {
                String actionNo = sqlReq.getActionName(i);

                LogicAct lact = ActionConfig.getInstance().getActionConfig(actionNo);
                if (lact==null) {
                	logger.error("传递的Action编号【"+actionNo+"】不存在，请确认！");
                	break;
                }
                
                Class cc = Class.forName(lact.logicClass);
                Action act = (Action) cc.newInstance();
                act.setSc(this.ctx);
                OperatorManager om = ctx.getUserManager();
                
//                if (om==null || om.getOperator() == null){
//                     sqlRes.setResult(false);
//                     sqlRes.setType(0);
//                     sqlRes.setMessage("操作员超时，请重新签到！");
//                     return -1;
//                }


                if ( om != null )
                	if(om.getOperator()!=null)
                		act.setOperator(om.getOperator());
                             
                act.setReqXml(reqXml);
                
                if(om!=null){
                	if(om.getOperator()!=null)
                		act.setDept(om.getOperator().getPtDeptBean());
                }
                act.execute(sqlReq.getActionRequest(i), sqlRes);

            } catch (Exception e) {
                logger.error("",e);
                sqlRes.setResult(false);
                sqlRes.setType(0);
                sqlRes.setMessage("调用类对象出错："+e.getMessage());
            }
        }
        //logger.debug("结果XML:"+sqlRes.getXmlStr());

        return 0;
    }

}
