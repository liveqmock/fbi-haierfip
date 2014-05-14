//Source file: e:\\java\\zt\\platform\\form\\util\\FormInstanceManager.java

package pub.platform.form.util;

import java.util.*;
import java.io.Serializable;

/**
 * 管理FORM的实例化,一个会话（session）一个实例
 *
 *
 * @author qingdao tec
 * @version 1.0
 */
public class FormInstanceManager
    implements Serializable {

    /**
     * FormInstance缓冲池
     *
     * 采用前插模式，如果缓冲池满了，删除最后一个实例，第一个元素为当前元素
     * 请注意元素在添加时的互斥访问问题
     */
    private LinkedList manager = new LinkedList();

    /**
     * 实例缓冲池大小
     */
    private int maxSize;

    /**
     * 实例序号产生器，每个实例对应一个唯一的序号
     */
    private InstanceSequencer sequencer;

    /**
     * 实例化FormInstanceManager管理器
     *
     * maxSize:实例池的大小
     * @param maxSize
     * @roseuid 3F7215D90371
     */
    public FormInstanceManager(int p_maxSize)
    {
        this.maxSize = p_maxSize;
        sequencer = new InstanceSequencer();
    }

    /**
     * 根据formid实例化FORM,并返回实例的序号
     * @param formid
     * @return String
     * @roseuid 3F7215F002FC
     */
    public String instanceForm(String formid)
    {
        //判断有没有超出最大值
        if (manager.size() >= maxSize)
            manager.removeFirst();
            //产生新的instanceForm，并返回FormInstanceId
        String sequence = sequencer.getSequence();
        FormInstance fi = new FormInstance(formid, sequence);
        manager.add(fi);
        return sequence;
    }

    /**
     * 实例化FORM,并初始化
     * @param formid
     * @param values
     * @return String
     * @roseuid 3F72B50D01E3
     */
    public String instanceForm(String formid, Map values)
    {
        if (manager.size() >= maxSize)
            manager.removeFirst();
        String sequence = sequencer.getSequence();
        FormInstance fi = new FormInstance(formid, sequence);
        fi.setValue(values);
        manager.add(fi);
        return sequence;
    }

    /**
     * 获取实例instanceid
     * @param instanceid
     * @return zt.platform.form.util.FormInstance
     * @roseuid 3F721605023E
     */
    public FormInstance getFormInstance(String instanceid)
    {
        //遍历manager找出instanceid相同的，删除，同时返回句柄。
        FormInstance fi;
        for (int i = manager.size() - 1; i >= 0; i--) {
            fi = (FormInstance) manager.get(i);
            if ( (fi.getInstanceid()).equals(instanceid))
                return (FormInstance) manager.get(i);
        }
        return null;
    }

    /**
     * 得到当前实例
     * @return zt.platform.form.util.FormInstance
     * @roseuid 3F7E1D3D0316
     */
    public FormInstance getFormInstance()
    {
        return (FormInstance) manager.getLast();
    }

    /**
     * 取得实例instanceid的上一个FORM实例
     * @param instanceid
     * @return zt.platform.form.util.FormInstance
     * @roseuid 3F7284700101
     */
    public FormInstance getPreviousInstance(String instanceid)
    {
        //遍历manager找出instanceid相同的，取其前一个（序号来说是小一个序号的）实例返回，没有，则返回null。
        FormInstance fi;
        for (int i = manager.size() - 1; i > 0; i--) {
            fi = (FormInstance) manager.get(i);
            if ( (fi.getInstanceid()).equals(instanceid))
                return (FormInstance) manager.get(i - 1);
        }
        return null;
    }

    /**
     * 取得指定FORM实例的上一个实例
     * @param instance
     * @return zt.platform.form.util.FormInstance
     * @roseuid 3F72848D01C1
     */
    public FormInstance getPreviousInstance(FormInstance instance)
    {
        //遍历manager找出instance相同的，取其前一个（序号来说是小一个序号的）实例返回，没有，则返回null。
        FormInstance fi;
        for (int i = manager.size() - 1; i > 0; i--) {
            fi = (FormInstance) manager.get(i);
            if (fi.equals(instance))
                return (FormInstance) manager.get(i - 1);
        }
        return null;
    }

    /**
     * 获取当前实例的上一个实例
     * @return zt.platform.form.util.FormInstance
     * @roseuid 3F7E1C960077
     */
    public FormInstance getPreviousInstance()
    {
        return getPreviousInstance(getFormInstance());
    }

    /**
     * 卸载实例instanceid
     * @param instanceid
     * @return FormInsatnce
     * @roseuid 3F72CD4A0325
     */
    public FormInstance removeInstance(String instanceid)
    {
        //遍历manager找出instanceid相同的，删除，同时返回句柄。
        FormInstance fi;
        for (int i = manager.size() - 1; i >= 0; i--) {
            fi = (FormInstance) manager.get(i);
            if ( (fi.getInstanceid()).equals(instanceid))
                return (FormInstance) manager.remove(i);
        }
        return null;
    }

    /**
     * 卸载实例instance
     * @param instance
     * @return zt.platform.form.util.FormInstance
     * @roseuid 3F72CE53004B
     */
    public FormInstance removeInstance(FormInstance instance)
    {
        //遍历manager找出instanceid相同的，删除，同时返回句柄。
        FormInstance fi;
        for (int i = manager.size() - 1; i >= 0; i--) {
            fi = (FormInstance) manager.get(i);
            if (fi.equals(instance))
                return (FormInstance) manager.remove(i);
        }
        return null;
    }

    public void print()
    {
        System.out.println("FormInstanceManager Pool size = " + manager.size());
        for (int i = 0; i < manager.size(); i++) {
            FormInstance fi = (FormInstance) manager.get(i);
            System.out.println(fi.getFormid());
        }
    }

    public static void main(String[] args)
    {
        LinkedList ll = new LinkedList();
        ArrayList al = new ArrayList();
        System.out.println(ll.size());
        ll.add("asdf1");
        ll.add("asdf2");
        ll.add("asdf3");
        al.add("asdf1");
        al.add("asdf2");
        al.add("asdf3");
        ll.remove(1);
        al.remove(1);
        System.out.println(ll.get(1));
    }
}