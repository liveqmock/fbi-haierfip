//Source file: D:\\zt\\platform\\form\\util\\event\\EventManager.java

package pub.platform.form.util.event;

import java.util.LinkedList;
/**
 * 管理一次FORM请求触发的事件
 * 机制：
 * 串行触发和串行处理事件
 * @author qingdao tec
 * @version 1.0
 */
public class EventManager {
    private LinkedList events=new LinkedList();

    /**
     * @roseuid 3F725CEA0207
     */
    public EventManager() {

    }

    /**
     * 触发事件
     *
     * 添加事件到事件管理器
     * @param event
     * @roseuid 3F725CF203AD
     */
    public void trigger(Event event) {
      this.events.add(event);
    }

    /**
     * @param id
     * @param type
     * @param isInstance
     * @roseuid 3F7E3B7E00BA
     */
    public void trigger(String id, int type, int branchType,boolean isInstance) {
      this.events.add(new Event(id,type,branchType,isInstance,null));
    }

    /**
     * 触发FORM加载事件
     *
     * formid - FORM的ID
     * type - 0
     * isInstance - false
     * parameters - 初始化参数
     * @param formid
     * @param parameters
     * @roseuid 3F84B87D0168
     */
    public void trigger(String formid, String parameters) {
      this.events.add(new Event(formid,EventType.LOAD_EVENT_TYPE,Event.BRANCH_CONTINUE_TYPE,false,null));
    }

    /**
     * 是否存在已发生的事件
     * @return boolean
     * @roseuid 3F725D0000C8
     */
    public boolean hasMoreEvent() {
      if(this.events.size()>0) return true;
      return false;
    }

    /**
     * 接收下一事件并处理（events中删除并返回）
     * @return zt.platform.form.util.event.Event
     * @roseuid 3F725E740145
     */
    public Event nextEvent() {
      if(hasMoreEvent()){
        Event ev = (Event)this.events.getFirst();
        this.events.removeFirst();
        return ev;
      }
      return null;
    }
}
