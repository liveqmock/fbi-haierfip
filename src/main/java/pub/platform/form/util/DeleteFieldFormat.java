package pub.platform.form.util;

import pub.platform.db.RecordSet;
import pub.platform.form.config.*;
import pub.platform.form.control.SessionContext;
import pub.platform.form.util.datatype.DataType;


public class DeleteFieldFormat extends FieldFormat
{
    public String format(SessionContext ctx, ElementBean eb1, FormInstance fi, RecordSet values) {
        String name = eb1.getName();
        FormBean fb = FormBeanManager.getForm(fi.getFormid());
        String[] names = fb.getElementKeys();
        String value  = "";
        if ( names != null ) {
            for ( int i = 0 ; i < names.length ; i++ ) {
                ElementBean eb = fb.getElement(names[i]);
                if ( eb.isIsPrimaryKey() ) {
                    if ( value.length() != 0 && !value.endsWith("and ") ) {
                        value += " and ";
                    }
                    int type = eb.getDataType();
                    if ( type == DataType.STRING_TYPE || type == DataType.BOOLEAN_TYPE || type == DataType.DATE_TYPE ) {
                        String fldvalue = values.getString(eb.getName());
                        if ( fldvalue != null ) {
                            fldvalue = fldvalue.trim();
                        }
                        value += eb.getName() + "='" + fldvalue + "'";
                    } else {
                        String fldvalue = values.getString(eb.getName());
                        if ( fldvalue != null ) {
                            fldvalue = fldvalue.trim();
                        }
                        value += eb.getName() + "=" + fldvalue;
                    }
                }
            }
        }
        String body = "<center><input type='checkbox' class='delete_checkbox' name='"+SessionAttributes.REQUEST_DELETE_RANGE_NAME+
                      "' value=\""+value+"\"></center>";

        if (value.trim().length() == 0) {
            body += "<center>使用删除功能，请定义主键</center>";
        }

        return body;
    }
}