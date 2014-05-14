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
 *@created    2003Äê10ÔÂ11ÈÕ
 *@version    1.0
 */

public class FormPassword extends AbstractFormComponent {
    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toHTML() {
        return getHeader() + "<input type=\"password\" name=\"" + element.getName() + "\" value=\"" + getValues()[0] + "\" class=\"" + CSS_PAGE_FORM_PASSWORD + "\"" + sizeAndMaxLength() + otherStr() + conditions() + ">" + GetFooter();
    }


    /**
     *  Constructor for the FormPassword object
     *
     *@param  element  Description of the Parameter
     */
    public FormPassword(ElementBean element) {
        super(element);
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

        dataType = " dataType=\"" + element.getDataType() + "\"";
        errInfo = " errInfo=\"" + element.getCaption() + "\"";

        if (element.getDataType() == FieldBean.DATA_TYPE_DECIMAL) {
            precision = " precision=\"" + element.getPrecision() + "\"";
            decimalDigits = " decimalDigits=\"" + element.getDecimalDigits() + "\"";
        }
        return mayNull + minLength + dataType + errInfo + precision + decimalDigits;
    }

}
