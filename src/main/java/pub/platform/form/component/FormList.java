//Source file: e:\\java\\zt\\platform\\form\\component\\FormList.java

package pub.platform.form.component;

import pub.platform.form.config.ElementBean;

/**
 *@author     请替换
 *@created    2003年10月11日
 *@version    1.0
 */
public class FormList extends AbstractFormComponent {


    /**
     *  Constructor for the FormList object
     *
     *@param  element  Description of the Parameter
     */
    public FormList(ElementBean element) {
        super(element);
    }


    /**
     *  产生FormList的HTML脚本 根据value、nameset、valueste生成如下脚本：
     *  <td class="page_form_title_td">
     *
     *  </td>
     *
     *  <td class="page_form_td">
     *    $headstr <select name="" mutltiple> <option value="111" selected>111
     *    ……  $middlestr
     *  </td>
     *
     *
     *@return     String
     *@roseuid    3F74FE1D02A7
     */
    public String toHTML() {
        StringBuffer sb = new StringBuffer();
        sb.append(getHeader());

        String multiple="";
        if(element.isMultiple()==true){
            multiple=" size=\""+element.getSize()+"\" multiple";
        }
        sb.append("<select name=\""+element.getName()+"\" class=\""+CSS_PAGE_FORM_SELECT+"\""+multiple+""+otherStr()+">");
        for (int i = 0; i < nameset.length; i++) {
            sb.append("<option value=\""+valueset[i]+"\""+selected(valueset[i])+">"+nameset[i]+"</option>");
        }
        sb.append("</select>");
        sb.append(GetFooter());
        return sb.toString();
    }

    public String selected(String value){
        String v[]=getValues();
        if(element.isMultiple()){
            for (int i = 0; i < v.length; i++) {
                if (v[i].equals(value)) {
                    return " selected";
                }
            }
        }else{
            if(v[0].equals(value)){
                return " selected";
            }
        }
        return "";
    }


    /**
     *  初始化 算法如下： 1.super(e) 2.根据valuetype和valueset初始化数组nameset和valueset
     *
     *@param  e
     *@roseuid    3F7EA13302CA
     */
    protected void init(ElementBean e) { }
}
