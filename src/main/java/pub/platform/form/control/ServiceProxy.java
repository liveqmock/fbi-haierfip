//Source file: D:\\zt\\platform\\form\\control\\ServiceProxy.java

package pub.platform.form.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 * FORM Engine 服务接口
 *
 * @author 请替换
 * @version 1.0
 */
public interface ServiceProxy {

    /**
     * 获得FORM头部
     * @return String
     * @roseuid 3F722159020C
     */
    public String getHead();

    /**
     * 设置FORM头部
     * @param head
     * @roseuid 3F7222F6024C
     */
    public void setHead(String head);

    /**
     * 获取FORM主体
     * @return String
     * @roseuid 3F722161033A
     */
    public String getBody();

    /**
     * 设置FORM主体
     * @param body
     * @roseuid 3F72230003CD
     */
    public void setBody(String body);

    /**
     * 获取FORM尾部
     * @return String
     * @roseuid 3F72216D02DD
     */
    public String getTail();

    /**
     * 设置FORM尾部
     * @param tail
     * @roseuid 3F72230E01EC
     */
    public void setTail(String tail);

    public String getSysButton();

    public void setSysButton(String sysButton);

    public String getBeforeHead();

    public void setBeforeHead(String beforeHead);

    public String getAfterHead();

    public void setAfterHead(String afterHead);

    public String getAfterBody();

    public void setAfterBody(String afterBody);

    public String getAfterSysButton();

    public void setAfterSysButton(String afterSysButton);


    /**
     * 提供所有的FORM请求服务
     * @param request
     * @roseuid 3F72217F0116
     */
    public void service(HttpServletRequest request, HttpServletResponse response);
    public void proxyService(HttpServletRequest request, HttpServletResponse response);

}
