package skyline.config.scope;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import javax.faces.context.FacesContext;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-14
 * Time: 上午9:40
 * To change this template use File | Settings | File Templates.
 */
public class ViewScope implements Scope {
    public Object get(String name, ObjectFactory objectFactory) {
        Map<String, Object> viewMap = FacesContext.getCurrentInstance().getViewRoot().getViewMap();

        if (viewMap.containsKey(name)) {
            return viewMap.get(name);
        } else {
            Object object = objectFactory.getObject();
            viewMap.put(name, object);
            return object;
        }
    }

    public Object remove(String name) {
        return FacesContext.getCurrentInstance().getViewRoot().getViewMap().remove(name);
    }

    public String getConversationId() {
        return null;
    }

    public void registerDestructionCallback(String name, Runnable callback) {
        //不支持
    }

    public Object resolveContextualObject(String key) {
        return null;
    }
}
