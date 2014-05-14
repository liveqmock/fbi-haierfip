//Source file: e:\\java\\zt\\platform\\form\\config\\FormBeanManager.java

package pub.platform.form.config;

import java.util.*;
import java.sql.*;
import java.util.logging.*;

import pub.platform.db.*;
import pub.platform.form.component.AbstractFormComponent;
import pub.platform.form.util.datatype.ComponentType;
import pub.platform.form.util.datatype.DataType;
import pub.platform.utils.*;
/**
 *  管理所有的FORM定义信息 在static语句块中负责加载enabled=1的FORM定义信息到内存,数据来源
 *  (ptforminfomain、ptforminfodetl) 注意： 1。将表字段的信息从pttblinfodetl中复制到ElementBean中
 *
 *@author     Rock
 *@created    2003年10月10日
 *@version    1.0
 */
public class FormBeanManager {

    /**
     *  FORM管理器，管理所有的FORM实例 管理方式：FORMID : FORM实例值对的模式
     *
     *@author    sun
     *@since     2003年10月10日
     */
    private static Map formBeans;
    private static Logger logger = Logger.getLogger("zt.platform.form.config.FormBeanManager");
    static {
        init();
    }


    /**
     *  Description of the Method
     *
     *@param  eb  Description of the Parameter
     *@param  fb  Description of the Parameter
     */
    private static void check(ElementBean eb, FieldBean fb) {
        if (fb == null) {
            logger.info("FieldBean is null could not check");
        } else {
            if (eb.getDefaultValue() == null || eb.getDefaultValue().equals("")) {
                String value = DBUtil.fromDB(fb.getDefaultValue());
                eb.setDefaultValue(value);
            }

            if (eb.getCaption() == null || eb.getCaption().equals("")) {
                eb.setCaption(fb.getCaption());
            }
            if (eb.getDescription() == null || eb.getDescription().equals("")) {
                eb.setDescription(fb.getDescription());
            }
            if (eb.getDataType() == 0) {
                eb.setDataType(fb.getDatatype());
            }
            if (eb.getComponetTp() == 0) {
                eb.setDataType(fb.getDatatype());
                if (eb.getDataType() == FieldBean.DATA_TYPE_BOOLEAN) {
                    eb.setComponetTp(ComponentType.BOOLEAN_TYPE);
                }
                if (eb.getDataType() == FieldBean.DATA_TYPE_DECIMAL ||
                        eb.getDataType() == FieldBean.DATA_TYPE_STRING) {
                    eb.setComponetTp(ComponentType.TEXT_TYPE);
                }
                if ( eb.getDataType() == FieldBean.DATA_TYPE_INTEGER ) {
                    if ( eb.getRefTbl()!= null && eb.getRefName()!= null && eb.getRefValue() != null ) {
                        eb.setComponetTp(ComponentType.REFERENCE_TEXT_TYPE);
                    } else {
                        eb.setComponetTp(ComponentType.TEXT_TYPE);
                    }
                }
                if (eb.getDataType() == FieldBean.DATA_TYPE_ENUMERATION) {
                    eb.setComponetTp(ComponentType.ENUMERATION_TYPE);
                }
                if (eb.getDataType() == FieldBean.DATA_TYPE_DATE) {
                    eb.setComponetTp(ComponentType.TEXT_TYPE);
                }
            }
        }
    }


    /**
     *  Description of the Method
     *
     *@param  form  Description of the Parameter
     *@param  keys  Description of the Parameter
     *@return       Description of the Return Value
     */
    private static FormBean initFormWithFields(FormBean form, Collection keys) {
        TableBean table = TableBeanManager.getTable(form.getTbl());
        if (table == null) {
            logger.warning("***********TableBean with name " + form.getTbl() + "is null,could not init*************");
        } else {
            FieldBean fields[] = table.getFields();

            int x = 1;
            for (int i = 0; i < fields.length; i++) {
                logger.info("Add field into " + form.getId() + " of " + fields[i].getName());
                ElementBean element = new ElementBean();
                element.setCaption(fields[i].getCaption());
                element.setComment(fields[i].getComment());
                element.setDataType(fields[i].getDatatype());
                element.setDefaultValue(fields[i].getDefaultValue());
                element.setName(fields[i].getName());
                element.setIsnull(fields[i].isIsnull());
                element.setIsPrimaryKey(fields[i].isIsPrimary());
                element.setIsSearchKey(fields[i].isIsSearch());
                element.setEnutpname(fields[i].getEnutpname());
                element.setIsrepeate(fields[i].isIsrepeat());
                element.setFormatcls(null, form.getType());
                element.setSize(fields[i].getLength());
                element.setMaxLength(fields[i].getLength());
                element.setDecimalDigits(fields[i].getDecimals());
                element.setPrecision(fields[i].getPrecision());
                element.setRefName(fields[i].getRefnamefld());
                element.setRefValue(fields[i].getRefvaluefld());
                element.setRefTbl(fields[i].getReftbl());
                element.setRefWhere(fields[i].getRefWhere());

                element.setXposition(x);
                element.setYposition(1);
                element.setNeedEncode(fields[i].isNeedEncode());
                x++;
                check(element, fields[i]);
                form.addElement(element.getName(), element);
                keys.add(element.getName());
            }
        }
        return form;
    }


    /**
     *  根据FORMID访问FORM实例
     *
     *@param  id
     *@return     zt.platform.form.config.FormBean
     *@roseuid    3F716FAA02A9
     */
    public static FormBean getForm(String id) {
        return (FormBean) formBeans.get(id);
    }


    /**
     *  Gets the trim attribute of the FormBeanManager class
     *
     *@param  value  Description of the Parameter
     *@return        The trim value
     */
    private static String getTrim(String value) {
        if (value == null) {
            return "";
        } else {
            return value.trim();
        }
    }


    /**
     *  Description of the Method
     */
    public static void init() {
        logger.info("@@@@@@@@@@@@@@@@@@@@@@@@@@@@FromBeanManager static init@@@@@@@@@@@@@@@@@@@@@@@@@@@");

        formBeans = new HashMap();
        ConnectionManager manager = ConnectionManager.getInstance();
        DatabaseConnection con = manager.getConnection();
        RecordSet rs = con.executeQuery("select * from PTFORMINFOMAIN where enabled='1'");

        //formid,urlocate,formproc,formstyle,formtbl
        //title,formtype,readonly,rows,cols,enabled,listsql,description
        //useAdd,usedelete,usesave,useedit,usereset,usesearch
        while (rs.next()) {
            String formId = rs.getString("formid").trim();
            logger.info("Find form which formid is '" + formId + "'");
            FormBean fb = new FormBean();
            formBeans.put(formId, fb);
            fb.setId(formId);
            fb.setUrl(rs.getString("urlocate"));

            fb.setProccls(getTrim(rs.getString("formproc")));
            fb.setStyle(getTrim(rs.getString("formstyle")));
            fb.setTbl(getTrim(rs.getString("formtbl")));

            fb.setTitle(DBUtil.fromDB(getTrim(rs.getString("title"))));
            fb.setType(rs.getInt("formtype"));
            fb.setReadonly(rs.getBoolean("readonly"));
            fb.setRows(rs.getInt("rows"));
            fb.setCols(rs.getInt("cols"));
            fb.setListsql(getTrim(rs.getString("listsql")));
            fb.setDescription(DBUtil.fromDB(getTrim(getTrim(rs.getString("description")))));
            fb.setUseAdd(rs.getBoolean("useadd"));
            fb.setUseDelete(rs.getBoolean("usedelete"));
            fb.setUseEdit(rs.getBoolean("useedit"));
            fb.setUseReset(rs.getBoolean("usereset"));
            fb.setUseSave(rs.getBoolean("usesave"));
            fb.setUseSearch(rs.getBoolean("usesearch"));
            fb.setCountsql(rs.getString("countsql"));

            fb.setWidth(rs.getInt("width"));
            fb.setScriptFile(DBUtil.fromDB(getTrim(rs.getString("SCRIPTFILE"))));

        }

        String elementStr = "select * from PTFORMINFODETL where formid='";
        String elementStr2 = "' order by xposition asc,yposition,seqno asc";
        for (Iterator iter = formBeans.values().iterator(); iter.hasNext(); ) {
            FormBean form = (FormBean) iter.next();
            logger.info("------------------------------Handle the form of " + form.getId() + "-------------------------------------------------");
            //formid,seqno,type,name,defaultvalue,
            //caption,description,readonly,minlength,maxlength,
            //size,visible,isnull,compnenttype(char(2)),datatype,
            //xposition,yposition,rows,cols,multiple,
            //disabled,valuesettype,valueset,headstr,middlestr
            //afterstr,formatcls,onclick,onchange,onsubmit
            //others
            boolean hasElementRecord = false;
            Collection keys = new Vector();
            TableBean tb = TableBeanManager.getTable(form.getTbl());
            ElementBean eb = null;

            RecordSet elementRs = con.executeQuery(elementStr + form.getId() + elementStr2);
            //对于每一个element保存到form中去
            while (elementRs.next()) {
                hasElementRecord = true;
                eb = new ElementBean();
                eb.setSeqno(elementRs.getInt("seqno"));
                eb.setType(elementRs.getInt("type"));
                eb.setName(getTrim(elementRs.getString("name")));
                eb.setMaxLength(elementRs.getInt("maxlength"));

                keys.add(eb.getName());
                FieldBean fb = null;
                if (tb != null) {
                    fb = tb.getField(eb.getName());
                    logger.info("Handle the field with name:" + eb.getName());
                    if(eb.getType()==0){
                        if (fb == null) {
                            logger.severe("The "+tb.getName()+" field with name " +
                                          eb.getName() +
                                          " is null,could not init primary and other attribute!");
                        }
                        else {
                            eb.setNeedEncode(fb.isNeedEncode());
                            eb.setIsPrimaryKey(fb.isIsPrimary());
                            eb.setIsSearchKey(fb.isIsSearch());
                            eb.setEnutpname(fb.getEnutpname());
                            eb.setRefTbl(fb.getReftbl());
                            eb.setRefName(fb.getRefnamefld());
                            eb.setRefWhere(fb.getRefWhere());
                            eb.setRefValue(fb.getRefvaluefld());

                            eb.setDecimalDigits(fb.getDecimals());
                            eb.setPrecision(fb.getPrecision());

                            if ( eb.getMaxLength() == 0 &&
                                 (fb.getDatatype() == DataType.STRING_TYPE || fb.getDatatype() == DataType.INTEGER_TYPE)) {
                                eb.setMaxLength(fb.getPrecision());
                            }

                            logger.info("enuname is" + fb.getEnutpname());
                        }
                    }
                } else {
                    logger.warning("The tablebean with name " + form.getTbl() +
                            " is null");
                }

                eb.setDefaultValue(DBUtil.fromDB(getTrim(elementRs.getString(
                        "defaultvalue"))));

                eb.setCaption(DBUtil.fromDB(getTrim(elementRs.getString("caption"))));
                eb.setDescription(DBUtil.fromDB(getTrim(getTrim(elementRs.getString(
                        "description")))));
                eb.setReadonly(elementRs.getBoolean("readonly"));
                eb.setMinLength(elementRs.getInt("minlength"));


                eb.setSize(elementRs.getInt("size"));

                if(elementRs.getString("visible") != null && elementRs.getString("visible").equals("0")){
                    eb.setVisible(false);
                }

                eb.setIsnull(elementRs.getBoolean("isnull"));
                //The under two ziduan is not complete!
                eb.setComponetTp(elementRs.getInt("componenttp"));
                eb.setDataType(elementRs.getInt("datatype"));

                eb.setXposition(elementRs.getInt("xposition"));
                eb.setYposition(elementRs.getInt("yposition"));
                eb.setRows(elementRs.getInt("rows"));
                eb.setCols(elementRs.getInt("cols"));
                eb.setMultiple(elementRs.getBoolean("multiple"));

                eb.setDisabled(elementRs.getBoolean("disabled"));
                eb.setValuesettype(elementRs.getInt("valuesettype"));
                eb.setValueset(DBUtil.fromDB(getTrim(elementRs.getString("valueset"))));
                eb.setHeadstr(getTrim(elementRs.getString("headstr")));
                eb.setMiddleStr(getTrim(elementRs.getString("middlestr")));

                eb.setAfterstr(getTrim(elementRs.getString("afterstr")));

                eb.setFormatcls(getTrim(elementRs.getString("formatcls")),
                        form.getType());
                eb.setOnclick(getTrim(elementRs.getString("onclick")));
                eb.setOnchange(getTrim(elementRs.getString("onchange")));
                eb.setOnsubmit(getTrim(elementRs.getString("onsubmit")));

                eb.setOthers(getTrim(elementRs.getString("others")));

                eb.setWidth(elementRs.getInt("width"));
                eb.setExpression(getTrim(elementRs.getString("expression")));
                eb.setDisplayType(elementRs.getInt("displaytype"));
                eb.setChangeEvent(elementRs.getBoolean("changeEvent"));


                check(eb, fb);
                form.addElement(eb.getName(), eb);
            }
            //如果没有element元素，通过field生成
            if (!hasElementRecord) {
                form = initFormWithFields(form, keys);
            }

            Object[] o = keys.toArray();
            String[] theKeys = new String[o.length];
            for (int i = 0; i < o.length; i++) {
                theKeys[i] = (String) o[i];
            }
            form.setElementKeys(theKeys);
            logger.info("------------------------------end Handle the form of " + form.getId() + " end-------------------------------------------------");
        }
        con.commit();
        manager.releaseConnection(con);
        logger.info(
                "@@@@@@@@@@@@@@@@@@@@@@@@@@@@ FromBeanManager static init end @@@@@@@@@@@@@@@@@@@@@@@@@");

    }
}
