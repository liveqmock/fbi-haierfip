//Source file: e:\\java\\zt\\platform\\form\\config\\EnumerationBean.java

package pub.platform.form.config;
import java.util.*;

/**
 * 枚举
 *
 * @author 请替换
 * @version 1.0
 */
public class EnumerationBean {

    /**
     * 枚举子元集
     * Map是key-value结构
     * key  －对应枚举的值
     * value－对应枚举的注释
     */
    private Map enums;

    private Vector keys=new Vector();

    /**
     * 枚举的名称
     */
    private String name;
    private String enudesc;

    /**
     * @param name
     * @param enu
     * @roseuid 3F7CF5DA020B
     */
//    public EnumerationBean(String name, Map enu) {
//
//    }

    /**
     * @param name
     * @roseuid 3F71680F0368
     */
    public EnumerationBean(String name) {
        enums=new HashMap();
        this.name=name;
    }

    /**
     * 设置枚举子元集
     * @param enu
     * @roseuid 3F7167FF0094
     */
//    public void setEnu(Map enu) {
//
//    }

    /**
     * 得到枚举的值集
     * @return Map
     * @roseuid 3F716723026E
     */
    public Map getEnu() {
     return enums;
    }

    /**
     * 增加一个枚举子元
     * @param key
     * @param value
     * @roseuid 3F7167C80135
     */
    public void add(Object key, Object value) {
        this.keys.add(key);
        this.enums.put(key,value);
    }

    /**
     * 得到枚举的名称
     * @return String
     * @roseuid 3F71686A0065
     */
    public String getName() {
     return this.name;
    }
    public String getEnudesc() {
        return enudesc;
    }
    public void setEnudesc(String enudesc) {
        this.enudesc = enudesc;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Object getValue(Object key){
        return enums.get(key) == null ? "": enums.get(key);
    }

    public String toString(){
        String text=":"+this.getName()+":"+this.getEnudesc()+":?";
        Iterator keys=this.enums.keySet().iterator();
        while(keys.hasNext()){
            Object key=keys.next();
            text=text+"?"+key+"?"+this.enums.get(key)+"?";
        }
        return text;
    }

    public Collection getKeys(){
        return keys;
    }

}
