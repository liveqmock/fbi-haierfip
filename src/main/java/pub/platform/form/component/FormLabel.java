//Source file: e:\\java\\zt\\platform\\form\\component\\FormLabel.java

package pub.platform.form.component;

import pub.platform.form.config.ElementBean;
/**
 * Label组件
 *
 * @author 请替换
 * @version 1.0
 */
public class FormLabel
    extends AbstractFormComponent {
    public FormLabel(ElementBean element) {
        super(element);
    }

    /**
     * 形成如下的字符串
     *
     * <label>value</label>
     * @return String
     * @roseuid 3F73AADB003A
     */
    public String toHTML() {
     return "<label class=\""+AbstractFormComponent.CSS_PAGE_FORM_LABEL+"\">"+element.getCaption()+"</label>";
    }
}
