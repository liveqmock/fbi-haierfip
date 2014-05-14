package pub.auth;

import pub.platform.security.OnLineOpersManager;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-4-10
 * Time: 下午11:46
 * To change this template use File | Settings | File Templates.
 */
public class OperOnLineSessionListener implements HttpSessionListener {

    //private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh24:mm:ss");
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
      //  HttpSession session = httpSessionEvent.getSession();
       // System.out.println(sdf.format(new Date())+"  session created : " + session.getId()+"  creationTime: "+session.getCreationTime());
    }

    /**
     * session销毁的操作
     * @param httpSessionEvent
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        HttpSession session = httpSessionEvent.getSession();
        OnLineOpersManager.removeSessionFromServer(session.getId(), session.getServletContext());
        //System.out.println(sdf.format(new Date())+"  session Destroyed : "+session.getId() +"  creationTime: "+session.getCreationTime());
    }
}