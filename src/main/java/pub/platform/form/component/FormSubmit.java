//Source file: e:\\java\\zt\\platform\\form\\component\\FormSubmit.java

package pub.platform.form.component;

import pub.platform.form.config.ElementBean;
/**
 * 提交按钮组件
 *
 * @author 请替换
 * @version 1.0
 */
public class FormSubmit
    extends AbstractFormComponent {
    public FormSubmit(ElementBean element) {
        super(element);
    }

    /**
     * 生成如下字符串:
     *
     * <input type="submit" name="" value="" ……>
     * @return String
     * @roseuid 3F73AADB0211
     */
    public String toHTML() {
        if (isReadonly() || element.isDisabled()) {
            return "<input type=\"button\" name=\"" + element.getName() +
                "\" value=\"" + element.getCaption() + "\" onclick=\""+element.getOnclick()+"\" class=\"" +
                AbstractFormComponent.CSS_PAGE_FORM_SUBMIT + "\" disabled>";
        }
        else {
            return "<input type=\"button\" name=\"" + element.getName() +
                "\" value=\"" + element.getCaption() + "\" onclick=\""+element.getOnclick()+"\" class=\"" +
                AbstractFormComponent.CSS_PAGE_FORM_SUBMIT + "\">";
        }
    }
}