package fip.common;

import pub.platform.form.config.SystemAttributeNames;
import pub.platform.security.OperatorManager;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-16
 * Time: 20:58:24
 * To change this template use File | Settings | File Templates.
 */
public class SystemService {

    //static private SqlSessionFactory sessionFactory = IbatisFactory.ORACLE.getInstance();

    public static OperatorManager getOperatorManager(){
        FacesContext currentInstance = FacesContext.getCurrentInstance();
        //判断后台批量处理的情况
        if (currentInstance == null) {
            return null;
        }
        ExternalContext extContext = currentInstance.getExternalContext();
        HttpSession session = (HttpSession) extContext.getSession(true);
        OperatorManager om = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
        if (om == null) {
            throw new RuntimeException("用户未登录！");
        }
        return om;
    }
    
/*
    public static <T> T getMapperObjFromFactory(Class<T> clazz) {
    	SqlSession session = sessionFactory.openSession();
        T mapper = session.getMapper(clazz);
        return mapper;
    }
    
    public static SqlSession getSqlSession() {
    	return sessionFactory.openSession();
    }
*/

    public static String getDatetime14() {
        SimpleDateFormat sdfdatetime14 = new SimpleDateFormat("yyyyMMddHHmmss");
    	return sdfdatetime14.format(new Date());
    }
}
