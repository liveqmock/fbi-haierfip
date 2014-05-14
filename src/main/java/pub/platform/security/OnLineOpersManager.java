package pub.platform.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.util.HashMap;
import java.util.Iterator;


/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 2011-3-30
 * Time: 11:20:35
 * To change this template use File | Settings | File Templates.
 */
public class OnLineOpersManager {

    private static final Logger logger = LoggerFactory.getLogger(OnLineOpersManager.class);
    private static final String ONLINE_USERS = "onlineoperinformations";

    public static synchronized boolean isHasUserList(ServletContext application) {
        return ((HashMap<String, OperatorManager>) application.getAttribute(ONLINE_USERS)) != null;
    }

    public static synchronized void setUserListToServer(ServletContext application) {
        application.setAttribute(ONLINE_USERS, new HashMap<String, OperatorManager>());
    }

    /*public static synchronized boolean isUserOnline(String oper, ServletContext application) {
        boolean isOn = false;
        HashMap<String, OperatorManager> userMaps = ((HashMap<String, OperatorManager>) application.getAttribute(ONLINE_USERS));
        if (userMaps != null) {
            Iterator it = userMaps.values().iterator();
            while (it.hasNext()) {
                if (((OperatorManager) it.next()).getOperator().getOperid().equals(oper)) {
                    isOn = true;
                    break;
                }
            }
        }
        return isOn;
    }*/

   /* public static synchronized boolean isSessionOnline(String sessionId, ServletContext application) {
        HashMap<String, OperatorManager> userMaps = ((HashMap<String, OperatorManager>) application.getAttribute(ONLINE_USERS));
        return (userMaps != null) ? userMaps.containsKey(sessionId) : false;
    }*/

    public static synchronized void addOperToServer(String sessionKey, OperatorManager oper, ServletContext application) {
        ((HashMap<String, OperatorManager>) application.getAttribute(ONLINE_USERS)).put(sessionKey, oper);
    }

    public static synchronized void removeOperFromServer(String sessionKey, ServletContext application) {
        ((HashMap<String, OperatorManager>) application.getAttribute(ONLINE_USERS)).remove(sessionKey);
    }

    public static synchronized void removeSessionFromServer(String sessionId, ServletContext application) {
        HashMap<String, OperatorManager> userMaps = ((HashMap<String, OperatorManager>) application.getAttribute(ONLINE_USERS));
        if (userMaps != null) {
            Iterator it = userMaps.keySet().iterator();
            String sessionKey = "";
            while (it.hasNext()) {
                 sessionKey = (String)it.next();
                if ((sessionKey).contains(sessionId)) {
                    userMaps.remove(sessionKey);
                }
            }
        }
    }

    public static HashMap<String, OperatorManager> getAllOperMaps(ServletContext application) {
        return (HashMap<String, OperatorManager>) application.getAttribute(ONLINE_USERS);
    }

}