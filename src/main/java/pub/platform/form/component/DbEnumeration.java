//Source file: e:\\java\\zt\\platform\\form\\component\\DbEnumeration.java

package pub.platform.form.component;
import java.util.*;
import java.util.logging.*;

import pub.platform.form.config.*;
import pub.platform.utils.*;
/**
 * Enumeration类是List的变种，它的所有值都是根据enuname从EnumerationType中根据enuna
 * me获取
 *
 * @author 请替换
 * @version 1.0
 */
public class DbEnumeration extends AbstractFormComponent {
    Logger logger=Logger.getLogger("zt.platform.form.component.DbEnumeration");

    private String enuname;

    public DbEnumeration(ElementBean element) {
        super(element);
    }




    /**
     * 根据enutpname从EnumerationType中获得枚举实例，形成
     * <td class="page_form_title_td"></td>
     * <td class="page_form_td">
     * <select……>
     *     ……
     *    <option value="">name</option>
     *    <option value="" selected >name</option>
     *     ……
     * </select>
     * <td>
     * 的字符串
     * @return String
     * @roseuid 3F73AADA0101
     */
    public String toHTML() {
     EnumerationBean enu=EnumerationType.getEnu(element.getEnutpname()) ;
     StringBuffer sb=new StringBuffer();
//
     sb.append(getHeader());
     sb.append("<select name=\"" + element.getName() + "\" class=\"" + CSS_PAGE_FORM_SELECT +
               "\"" + otherStr() + ">");
     if(enu!=null){
         for (Iterator iter = enu.getKeys().iterator(); iter.hasNext(); ) {
             Object key = (Object) iter.next();
             Object value = enu.getValue(key);
             if ( isReadonly() ) {
                 if ( isSelected(key+"") ) {
                     sb.append("<option value=\"" + key + "\" selected >" + value +
                           "</option>");
                 }
             } else
                 sb.append("<option value=\"" + key + "\"" + this.selected(key + "") + ">" + value +
                           "</option>");
         }
     }else{
         logger.severe("The enumeration type with name "+element.getEnutpname()+" is null");
     }
     sb.append("</select>");
     sb.append(GetFooter());
     return sb.toString();
    }
    private String selected(String value){
        String[] v=getValues();
        if(v!=null){
            for (int i = 0; i < v.length; i++) {
                if (v[i] != null) {
                    if (v[i].equals(value)) {
                        return " selected";
                    }
                }
            }
            return "";
        }else{
            return "";
        }
    }
    private boolean isSelected(String value){
        String[] v=getValues();
        if(v!=null){
            for (int i = 0; i < v.length; i++) {
                if (v[i] != null) {
                    if (v[i].equals(value)) {
                        return true;
                    }
                }
            }
            return false;
        }else{
            return false;
        }
    }

}
