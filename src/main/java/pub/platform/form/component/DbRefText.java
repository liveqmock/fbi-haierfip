//Source file: e:\\java\\zt\\platform\\form\\component\\DbRefText.java

package pub.platform.form.component;

import pub.platform.form.config.*;
import pub.platform.form.util.SessionAttributes;
import pub.platform.form.util.event.*;
/**
 *  用来表示带有外键参考的字段
 *
 *@author     请替换
 *@created    2003年11月21日
 *@version    1.0
 */
public class DbRefText
         extends AbstractFormComponent {
    /**
     *  Description of the Field
     */
    public final static String REF_FILE_NAME = "/templates/ref.jsp";
    private String ref;


    /**
     *  Constructor for the DbRefText object
     *
     *@param  element  Description of the Parameter
     */
    public DbRefText(ElementBean element) {
        super(element);
    }


    /**
     *  形成如下的字符串：
     *  <td class="page_form_title_td">
     *
     *  </td>
     *
     *  <td class="page_form_td">
     *    <input type="text" name="" value="" ……>&nbsp;&nbsp;&nbsp;&nbsp;<input
     *    type="button" name="nameref" onclick="window.open('/template/ref.jsp?instanceid=''&eventid=11&reference_field
     *    ='$name$'');">
     *  </td>
     *
     *
     *@return     String
     *@roseuid    3F73AAD903B2
     */
    public String toHTML() {
        StringBuffer sb = new StringBuffer();
        sb.append(getHeader());
        sb.append("<input type=\"text\" name=\"" + element.getName() + "\" value=\"" + getValues()[0] + "\" class=\"" + AbstractFormComponent.CSS_PAGE_FORM_TEXT + "\"" + sizeAndMaxLength() + otherStr() + "" + conditions() + ">");
        sb.append(element.getMiddleStr());
        sb.append("<input type=\"button\" name=\"nameref\" value=\"…\" onclick=\"window.open(&quot;" +
                getCtx().getUrl(REF_FILE_NAME) + "?" + SessionAttributes.REQUEST_INSATNCE_ID_NAME +
                "=" + getInstanceId() + "&" + SessionAttributes.REQUEST_EVENT_ID_NAME +
                "=" + EventType.REFERENCE_FIELD_EVENT_TYPE +
                "&reference_field=" + element.getName() + "&quot;,&quot;FIREF"+element.getName()+getInstanceId()+"&quot;,&quot;height=350,width=460,toolbar=no,scrollbars=yes&quot;);\" class=\"" +
                AbstractFormComponent.CSS_PAGE_FORM_REFBUTTON + "\""+disabled()+">");
//        sb.append("<input type=\"button\" name=\"nameref\" value=\"…\" onclick=\"window.showModalDialog('" +
//                getCtx().getUrl(REF_FILE_NAME) + "?" + SessionAttributes.REQUEST_INSATNCE_ID_NAME +
//                "=" + getInstanceId() + "&" + SessionAttributes.REQUEST_EVENT_ID_NAME +
//                "=" + EventType.REFERENCE_FIELD_EVENT_TYPE +
//                "&reference_field=" + element.getName() + "','dialogHeight=350pt;dialogWidth=460pt,toolbar=no,scrollbars=yes');\" class=\"" +
//                AbstractFormComponent.CSS_PAGE_FORM_REFBUTTON + "\">");

        sb.append(GetFooter());
        return sb.toString();
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

    private String disabled(){
        if(isReadonly() || element.isReadonly()){
            return " disabled";
        }
        return "";
    }

}
