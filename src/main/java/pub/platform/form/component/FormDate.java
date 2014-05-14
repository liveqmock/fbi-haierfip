package pub.platform.form.component;
import pub.platform.form.config.*;
/**
 *  <p>
 *
 *  Title: </p> <p>
 *
 *  Description: </p> <p>
 *
 *  Copyright: Copyright (c) 2003</p> <p>
 *
 *  Company: </p>
 *
 *@author     not attributable
 *@created    2003年11月28日
 *@version    1.0
 */

public class FormDate extends AbstractFormComponent {

    private static String javascriptsrc = "/js/meizzDate.js";
    /**
     *  Constructor for the FormLabel object
     *
     *@param  element  Description of the Parameter
     */
    public FormDate(ElementBean element) {
        super(element);
    }


    /**
     *  形成如下的字符串 <label>value</label>
     *
     *@return     String
     *@roseuid    3F73AADB003A
     */
    public String toHTML() {
        String jsUrl = ctx.getUrl(javascriptsrc);
        String componentStr = "";
        if ( isReadonly() ) {
            componentStr += "<input type=\"text\" name=\"" + element.getName() +
                "\" value=\"" + getValues()[0] + "\" class=\"" + CSS_PAGE_FORM_TEXT +  "\" " + sizeAndMaxLength() + otherStr() + " readonly=true>";
        } else {
            componentStr += "<input type=\"text\" name=\"" + element.getName() +
                           "\" value=\"" + getValues()[0] + "\" class=\"" + CSS_PAGE_FORM_TEXT +
                           "\" "+ sizeAndMaxLength() + conditions() + otherStr() + " ><input type=\"button\" value=\"…\" class=\"" +
                           AbstractFormComponent.CSS_PAGE_FORM_REFBUTTON +
                           "\" onclick=\"setday(this,winform." + element.getName() + ")\""+disabled()+">";
        }
        String text = getHeader() + componentStr + GetFooter();
        return text;
    }

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

        dataType = " dataType=\"" + element.getDataType() + "\"";
        errInfo = " errInfo=\"" + element.getCaption() + "\"";

        if (element.getDataType() == FieldBean.DATA_TYPE_DECIMAL) {
            precision = " element=\"" + element.getPrecision() + "\"";
            decimalDigits = " decimalDigits=\"" + element.getDecimalDigits() + "\"";
        }

        return mayNull + minLength + dataType + errInfo + precision + decimalDigits;
    }

    private String disabled(){
    if(element.isReadonly()){
        return " disabled";
    }
    return "";
}



}
