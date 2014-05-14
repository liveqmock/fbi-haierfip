//Source file: e:\\java\\zt\\platform\\form\\component\\FormJavaScript.java

package pub.platform.form.component;

import pub.platform.form.config.ElementBean;
/**
 *  JavaScript脚本组件
 *
 *@author     请替换
 *@created    2003年10月11日
 *@version    1.0
 */
public class FormJavaScript extends AbstractFormComponent {
    /**
     *  Constructor for the FormJavaScript object
     *
     *@param  element  Description of the Parameter
     */
    private ElementBean element;
    public FormJavaScript(ElementBean element) {
        super(element);
        this.element = element;
    }


    /**
     *  根据value生成如下字符串: <script src="" type="text/javascript" ></script>
     *
     *@return     String
     *@roseuid    3F73AADB00D0
     */
    public String toHTML() {
        return "<script src=\"" + ctx.getUrl(element.getDefaultValue()) + "\" type=\"text/javascript\" ></script>";
    }
}
