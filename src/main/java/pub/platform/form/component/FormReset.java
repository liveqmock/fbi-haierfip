//Source file: e:\\java\\zt\\platform\\form\\component\\FormReset.java

package pub.platform.form.component;

import pub.platform.form.config.ElementBean;
/**
 * 重置按钮组件
 *
 * @author 请替换
 * @version 1.0
 */
public class FormReset
    extends AbstractFormComponent {

    public FormReset(ElementBean element) {
        super(element);
    }

    /**
     * 形成如下字符串：
     * <input type="reset" name="" value="" ……>
     * @return String
     * @roseuid 3F73AADB02CF
     */
    public String toHTML() {
     return "<input type=\"reset\" name=\""+element.getName()+"\" value=\""+element.getCaption()+"\" class=\""+AbstractFormComponent.CSS_PAGE_FORM_RESET+"\">";
    }
}
