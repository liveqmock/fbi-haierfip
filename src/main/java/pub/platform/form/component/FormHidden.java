//Source file: e:\\java\\zt\\platform\\form\\component\\FormHidden.java

package pub.platform.form.component;

import pub.platform.form.config.ElementBean;
/**
 * Hidden组件
 *
 * @author 请替换
 * @version 1.0
 */
public class FormHidden extends AbstractFormComponent {

    public FormHidden(ElementBean element){
        super(element);
    }

    /**
     * 生成如下字符串：
     *
     * @return String
     * @roseuid 3F73AADA024B
     */
    public String toHTML() {
     return "<input type=\"hidden\" name=\""+element.getName()+"\" value=\""+getValues()[0]+"\" />";
    }
}
