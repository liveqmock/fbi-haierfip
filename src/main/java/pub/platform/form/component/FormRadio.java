//Source file: e:\\java\\zt\\platform\\form\\component\\FormRadio.java

package pub.platform.form.component;

import pub.platform.form.config.ElementBean;

/**
 * @author 请替换
 * @version 1.0
 */
public class FormRadio extends AbstractFormComponent {

    public FormRadio(ElementBean element) {
super(element);
}

    /**
     * 产生FormRadio的HTML脚本
     *
     * 根据value、nameset、valueste生成如下脚本：
     * <td class="page_form_title_td">
     * </td>
     * <td class="page_form_td">
     * $headstr
     * <input type="radio" name="" value="" checked>$middlestr
     * <input type="radio" name="" value="" checked>$middlestr
     * <input type="radio" name="" value="" checked>$afterstr
     * </td>
     * @return String
     * @roseuid 3F73AADA0382
     */
    public String toHTML() {
        StringBuffer sb=new StringBuffer();
        sb.append(getHeader());
        for (int i = 0; i < nameset.length; i++) {
            if(i!=0){
                sb.append(element.getMiddleStr());
            }
            sb.append(nameset[i]+"<input type=\"radio\" name=\""+element.getName()+"\" value=\""+valueset[i]+"\" class=\""+CSS_PAGE_FORM_RADIO+"\""+checked(valueset[i])+""+otherStr()+">");
        }
        sb.append(GetFooter());
        return sb.toString();
    }

    public String checked(String value){
        String v[]=getValues();
        if(v[0].equals(value.trim())){
            return " checked";
        }
        return "";
    }

    /**
     * 初始化
     *
     * 算法如下：
     * 1.super(e)
     * 2.根据valuetype和valueset初始化数组nameset和valueset
     * @param e
     * @roseuid 3F7EA1480143
     */
    protected void init(ElementBean e) {

    }
}
