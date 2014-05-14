//Source file: D:\\zt\\platform\\form\\control\\SessionContext.java

package pub.platform.form.control;

import java.util.Enumeration;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;

import pub.platform.security.OperatorManager;

/**
 * 会话上下文环境接口
 *
 * @author 请替换
 * @version 1.0
 */
public interface SessionContext {

    /**
     * 设置并保存会话周期（多次请求，从用户建立会话到终止会话）内属性name的值value
     * @param name
     * @param value
     * @roseuid 3F721CA90242
     */
    public void setAttribute(String name, Object value);

    /**
     * 获取会话周期（多次请求，从用户建立会话到终止会话）内属性name的值，不存在返回null
     * @param name
     * @return Object
     * @roseuid 3F721CB10013
     */
    public Object getAttribute(String name);

    /**
     * 删除会话周期（多次请求，从用户建立会话到终止会话）内属性name
     * @param name
     * @return Object
     * @roseuid 3F721CCD012C
     */
    public void removeAttribute(String name);

    /**
     * 获得会话周期内所有属性的名称
     * @return String[]
     * @roseuid 3F721CDF009B
     */
    public Enumeration getAttributeNames();

    /**
     * 获得会话周期内所有属性的值
     * @return Object[]
     * @roseuid 3F721CEB0139
     */
    public Object[] getAttributes();

    /**
     * 获取名字name的请求参数的值
     * @param name
     * @return String
     * @roseuid 3F7220D102F7
     */
    public String getParameter(String name);

    /**
     * 设置请求参数name值为value
     * @param name
     * @param value
     * @roseuid 3F7DF45C0077
     */
    public void setParameter(String name, String value);

    /**
     * 获取请求参数name值的values值数组
     * @param name
     * @return String[]
     * @roseuid 3F7220DC01BC
     */
    public String[] getParameters(String name);

    /**
     * 设置请求参数name值为values
     * @param name
     * @param values
     * @roseuid 3F7DF474034C
     */
    public void setParameter(String name, String[] values);

    /**
     * 获得所有请求参数的名称
     * @return String[]
     * @roseuid 3F7220E90215
     */
    public Enumeration getParameterNames();

    /**
     * 删除请求参数name
     * @param name
     * @return String
     * @roseuid 3F722100036C
     */
    public String removeParameter(String name);

    /**
     * 清空所有的请求参数
     * @roseuid 3F7221140393
     */
    public void clearParameters();

    /**
     * 设置要生成FORM的头，生命周期为本次请求
     * @param head
     * @roseuid 3F722BF602E5
     */
    public void setHead(String head);

    /**
     * 获取FORM的头部信息
     * @return String
     * @roseuid 3F722C26029E
     */
    public String getHead();

    /**
     * 设置要生成FORM的主体，生命周期为本次请求
     * @param body
     * @roseuid 3F722C040209
     */
    public void setBody(String body);

    /**
     * 获取FORM的主体
     * @return String
     * @roseuid 3F722C2E0105
     */
    public String getBody();

    /**
     * 设置要生成FORM的尾部，生命周期为本次请求
     * @param tail
     * @roseuid 3F722C15019F
     */
    public void setTail(String tail);

    /**
     * 获得FORM的尾部
     * @return String
     * @roseuid 3F722C3502D2
     */
    public String getTail();

    /**
     * 设置请求属性，生命周期为本次请求
     * @param name
     * @param value
     * @roseuid 3F73B1DE0280
     */
    public void setRequestAtrribute(String name, Object value);

    /**
     * 获得生命周期为本次请求的属性的值
     * @param name
     * @return Object
     * @roseuid 3F73B1F402AA
     */
    public Object getRequestAttribute(String name);

    /**
     * 删除生命周期为本次请求的属性
     * @param name
     * @return Object
     * @roseuid 3F73B2050091
     */
    public void removeRequestAttribute(String name);

    /**
     * 获得生命周期为本次请求的所有属性的名称数组
     * @return String[]
     * @roseuid 3F73B21802F1
     */
    public Enumeration getRequestAttributeNames();

    public void update(Object request);

    public String getUrl(String path);

    public void setSession(Object session);

    public void setRequest(Object request);

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

    public OperatorManager getUserManager();

    public boolean forward();
    public boolean needForward();
    public void setTarget(String target);
    public void setResponse(HttpServletResponse response);
}
