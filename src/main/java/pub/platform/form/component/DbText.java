//Source file: e:\\java\\zt\\platform\\form\\component\\DbText.java

package pub.platform.form.component;

import pub.platform.form.config.*;
/**
 *@author     请替换
 *@created    2003年10月11日
 *@version    1.0
 */
public class DbText extends AbstractFormComponent {
    /**
     *  Constructor for the DbText object
     *
     *@param  element  Description of the Parameter
     */
    public DbText(ElementBean element) {
        super(element);
    }


    /**
     *  形成如下字符串
     *  <td class="page_form_title_td">
     *
     *  </td>
     *
     *  <td class="page_form_td">
     *    <input type="text" name="" value="" ……>
     *  </td>
     *
     *
     *@return     String
     *@roseuid    3F73AADA006B
     */

    public String toHTML() {
        String componentStr = "<input type=\"text\" name=\"" + element.getName() + "\" value=\"" + getValues()[0] + "\" class=\"" + CSS_PAGE_FORM_TEXT + "\"" + sizeAndMaxLength() + otherStr() + ""+conditions()+">";
        String text = getHeader() + componentStr + GetFooter();
        return text;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String sizeAndMaxLength() {
        String size = "";
        String maxLength = "";
        if (element.getSize() != 0) {
            size = " size=\"" + element.getSize() + "\"";
        }
        if (element.getMaxLength() != 0) {
            maxLength = " maxlength=\"" + element.getMaxLength() + "\"";
        }
        return size + maxLength;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String conditions() {
        String mayNull = "";
        String minLength = "";
        String dataType = "";
        String errInfo = "";
        String precision = "";
        String decimalDigits = "";
        if (element.isIsnull()) {
            mayNull = " mayNull=\"1\"";
        } else {
            mayNull = " mayNull=\"0\"";
            if (element.getMinLength() != 0) {
                minLength = " minLength=\"" + element.getMinLength() + "\"";
            }
        }

        dataType=" dataType=\""+element.getDataType()+"\"";
        errInfo=" errInfo=\""+element.getCaption()+"\"";

        if(element.getDataType()==FieldBean.DATA_TYPE_DECIMAL){
            precision=" precision=\""+element.getPrecision()+"\"";
            decimalDigits=" decimalDigits=\""+element.getDecimalDigits()+"\"";
        }

        return mayNull+minLength+dataType+errInfo+precision+decimalDigits;
    }
}
