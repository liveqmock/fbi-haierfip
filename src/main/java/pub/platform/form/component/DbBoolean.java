//Source file: e:\\java\\zt\\platform\\form\\component\\DbBoolean.java

package pub.platform.form.component;

import pub.platform.form.config.ElementBean;

/**
 *  Boolean类型的抽象组件，实际上是RadioButton组件，取值是 0-否 1-是
 *
 *@author     请替换
 *@created    2003年10月10日
 *@version    1.0
 */
public class DbBoolean extends AbstractFormComponent {

    /**
     *  Constructor for the DbBoolean object
     *
     *@param  element  Description of the Parameter
     */
    public DbBoolean(ElementBean element) {
        super(element);
    }
    /**
     *  形成
     *  <td class="page_form_title_td">
     *
     *  </td>
     *
     *  <td class="page_form_td">
     *    <input type="radio" name="" value="1" ……>是</input> <input type="radio"
     *    name="" value="0" ……>否</input>
     *  </td>
     *  的字符串
     *
     *@return     String
     *@roseuid    3F73AAD902D6
     */
    public String toHTML() {
        String value = getValues()[0];
        String truestr = " ";
        String falsestr = " ";
        if ( value.equals("0") ) {
            falsestr = " checked ";
        } else {
            truestr = " checked ";
        }
        String componentStr = "<input type=\"radio\" name=\"" + element.getName()
                + "\" value=\"1\" class=\""+CSS_PAGE_FORM_RADIO+"\""+truestr+otherStr()+">是</input>"+element.getMiddleStr()+"<input type=\"radio\" name=\"" +
                element.getName()
                + "\" value=\"0\" class=\""+CSS_PAGE_FORM_RADIO+"\""+falsestr+otherStr()+">否</input>";
        return getHeader()+componentStr+GetFooter();
    }

}
