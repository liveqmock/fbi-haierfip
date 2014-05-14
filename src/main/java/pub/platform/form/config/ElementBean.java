//Source file: e:\\java\\zt\\platform\\form\\config\\ElementBean.java

package pub.platform.form.config;

import java.util.logging.*;
import java.util.*;

import pub.platform.db.DBUtil;
import pub.platform.form.component.AbstractFormComponent;
import pub.platform.form.util.datatype.ComponentType;
/**
 *  每个FORM Component对应的类 详细信息请参照信贷平台档案设计.doc文件的信息，进行详细设计 请添加每个属性的getter和setter方法
 *
 *@author     请替换
 *@created    2003年10月11日
 *@version    1.0
 */
public class ElementBean implements Comparable,Cloneable {

    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public static Logger logger = Logger.getLogger("zt.platform.form.config.ElementBean");

    /**
     *  序号
     *
     *@author    sun
     *@since     2003年10月11日
     */
    private int seqno;

    /**
     *  名称
     *
     *@author    sun
     *@since     2003年10月11日
     */
    private String name;

    /**
     *  是否关键字
     *
     *@author    sun
     *@since     2003年10月11日
     */
    private boolean isPrimaryKey;
    private boolean isSearchKey;
    private String enutpname;

    /**
     *  组件类型
     *
     *@author    sun
     *@since     2003年10月11日
     */
    private int componetTp;

    /**
     *  数据类型，适用于Text和RefText组件
     *
     *@author    sun
     *@since     2003年10月11日
     */
    private int dataType;
    private int type=0;
    private boolean isnull;
    private String comment = "";
    private String description = "";
    private String defaultValue = "";
    private boolean readonly;
    private int minLength;
    private int maxLength;
    private int size;
    private boolean visible=true;
    private boolean isrepeate;
    private int xposition;
    private int yposition;
    private int rows;
    private int cols;
    private boolean multiple;
    private boolean disabled;
    private int valuesettype;
    private String valueset = "";
    private String headstr = "";
    private String afterstr = "";
    private String onclick = "";
    private String onchange = "";
    private String onsubmit = "";
    private String others = "";
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DB_FIELD = 0;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int MEMORY_FIELD = 1;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int VALUESET_EXPRESSION_TYPE = 1;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int VALUESET_TABLE_TYPE = 2;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int VALUESET_SQL_TYPE = 3;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int VALUESET_MEMORY_TYPE = 4;

    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATATYPE_STIRNG = 1;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATATYPE_INTEGER = 2;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATATYPE_DECIMAL = 3;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATATYPE_BOOLEAN = 4;

    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATATYPE_DATE = 5;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATATYPE_ENUMERATION = 6;

    private String middleStr = "";
    private String caption = "";
    private pub.platform.form.util.FieldFormat formatcls;
    private boolean needEncode;
    private String refTbl;
    private String refName;
    private String refValue;
    private String refWhere;
    private int precision;
    private int decimalDigits;
    private int width;
    private String expression="";
    private int displayType;
    private boolean changeEvent;


    /**
     *  Constructor for the ElementBean object
     */
    public ElementBean() { }


    /**
     *  Constructor for the ElementBean object
     *
     *@param  field  Description of the Parameter
     */
    public ElementBean(FieldBean field) {
        logger.info("new element bean with field!");
        this.setCaption(field.getCaption());
        this.setComment(field.getComment());
        this.setDataType(field.getDatatype());
        this.setDefaultValue(field.getDefaultValue());
        this.setName(field.getName());
        this.setIsnull(field.isIsnull());
        this.setIsPrimaryKey(field.isIsPrimary());
        this.setIsSearchKey(field.isIsSearch());
        this.setEnutpname(field.getEnutpname());
        this.setIsrepeate(field.isIsrepeat());
        this.setSize(field.getLength());
        this.setMaxLength(field.getLength());

        if (this.getDefaultValue() == null || this.getDefaultValue().equals("")) {
            this.setDefaultValue(field.getDefaultValue());
        }
        if (this.getCaption() == null || this.getCaption().equals("")) {
            this.setCaption(field.getCaption());
        }
        if (this.getDescription() == null || this.getDescription().equals("")) {
            this.setDescription(field.getDescription());
        }
        if (this.getDataType() == 0) {
            this.setDataType(field.getDatatype());
        }
        if (this.getComponetTp() == 0) {
            this.setDataType(field.getDatatype());
            if (this.getDataType() == FieldBean.DATA_TYPE_BOOLEAN) {
                this.setComponetTp(ComponentType.BOOLEAN_TYPE);
            }
            if (this.getDataType() == FieldBean.DATA_TYPE_DECIMAL ||
                this.getDataType() == FieldBean.DATA_TYPE_INTEGER ||
                this.getDataType() == FieldBean.DATA_TYPE_STRING) {
                this.setComponetTp(ComponentType.TEXT_TYPE);
            }
            if (this.getDataType() == FieldBean.DATA_TYPE_ENUMERATION) {
                this.setComponetTp(ComponentType.ENUMERATION_TYPE);
            }
            if (this.getDataType() == FieldBean.DATA_TYPE_DATE) {
                this.setComponetTp(ComponentType.TEXT_TYPE);
            }
        }
    }


    /**
     *  校验值的合法性，参考FieldBean的validate方法
     *
     *@param  value
     *@return        boolean
     *@roseuid       3F72638101EE
     */
    public boolean validate(Object value) {
        Collection vs = (Collection) value;
        for (Iterator iter = vs.iterator(); iter.hasNext(); ) {
            boolean isValid = this.validateValue(iter.next());
            if (!isValid) {
                return false;
            }
        }
        return true;
    }



    /**
     *  Description of the Method
     *
     *@param  value  Description of the Parameter
     *@return        Description of the Return Value
     */
    private boolean validateValue(Object value) {
        //String type
        if (getDataType() == DATATYPE_STIRNG) {
            try {
                if (value == null) {
                    return false;
                }

                String v = ((String) value);
                if (v.length() >= getMinLength() && v.length() <= getMaxLength()) {
                    return true;
                } else {
                    logger.info("minlength is " + getMinLength() + ",and max length is " + getMaxLength() + ",and value's length is " + v.length() + "");
                    return false;
                }
            } catch (Exception ex) {
                logger.warning("Invalid value " + value);
                return false;
            }
        }

        //Integer type
        if (getDataType() == DATATYPE_INTEGER) {
            try {
                if (value == null) {
                    return false;
                }
                Integer v = ((Integer) value);
                return true;
            } catch (Exception ex) {
                logger.warning("Invalid value " + value);
                return false;
            }
        }

        //Decimal type
        if (getDataType() == DATATYPE_DECIMAL) {
            try {
                if (value == null) {
                    return false;
                }
                Double v = ((Double) value);
                return true;
            } catch (Exception ex) {
                logger.warning("Invalid value " + value);
                return false;
            }

        }

        //Boolean type
        if (getDataType() == DATATYPE_BOOLEAN) {
            try {
                if (value == null) {
                    return false;
                }

                String v = ((String) value);
                if (v.equals("0") || v.equals("1")) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception ex) {
                logger.warning("Invalid value " + value);
                return false;
            }
        }

        //Date Type
        if (getDataType() == DATATYPE_DATE) {
            try {
                if (value == null) {
                    return false;
                }
                String v = ((String) value);

                String[] theDate = v.split("/");
                Calendar c = Calendar.getInstance();
                c.set(Integer.parseInt(theDate[2]),
                        Integer.parseInt(theDate[0]) - 1,
                        Integer.parseInt(theDate[1]));
                return true;
            } catch (Exception ex) {
                logger.warning("Invalid value " + value);
                return false;
            }
        }


        //Enumeration type
        if (getDataType() == DATATYPE_ENUMERATION) {
            try {
                if (value == null) {
                    return false;
                }
                //String v = ((String) value);

                logger.info("Handle the enu name of " + getEnutpname() + " value is " + value.toString());
                return EnumerationType.validate(getEnutpname(), (String)value);
            } catch (Exception ex) {
                logger.warning("Invalid value " + value + " " + ex.getMessage());
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }


    /**
     *  Description of the Method
     *
     *@param  o  Description of the Parameter
     *@return    Description of the Return Value
     */
    public int compareTo(Object o) {
        ElementBean bean = (ElementBean) o;
        return this.seqno - bean.seqno;
    }


    /**
     *  Gets the afterstr attribute of the ElementBean object
     *
     *@return    The afterstr value
     */
    public String getAfterstr() {
        return afterstr;
    }


    /**
     *  Gets the cols attribute of the ElementBean object
     *
     *@return    The cols value
     */
    public int getCols() {
        return cols;
    }


    /**
     *  Gets the comment attribute of the ElementBean object
     *
     *@return    The comment value
     */
    public String getComment() {
        return comment;
    }


    /**
     *  Gets the componetTp attribute of the ElementBean object
     *
     *@return    The componetTp value
     */
    public int getComponetTp() {
        return componetTp;
    }


    /**
     *  Gets the dataType attribute of the ElementBean object
     *
     *@return    The dataType value
     */
    public int getDataType() {
        return dataType;
    }


    /**
     *  Gets the defaultValue attribute of the ElementBean object
     *
     *@return    The defaultValue value
     */
    public String getDefaultValue() {
        return defaultValue;
    }


    /**
     *  Gets the description attribute of the ElementBean object
     *
     *@return    The description value
     */
    public String getDescription() {
        return description;
    }


    /**
     *  Gets the disabled attribute of the ElementBean object
     *
     *@return    The disabled value
     */
    public boolean isDisabled() {
        return disabled;
    }


    /**
     *  Gets the enutpname attribute of the ElementBean object
     *
     *@return    The enutpname value
     */
    public String getEnutpname() {
        return enutpname;
    }


    /**
     *  Gets the formatcls attribute of the ElementBean object
     *
     *@return    The formatcls value
     */
    public pub.platform.form.util.FieldFormat getFormatcls() {
        return formatcls;
    }


    /**
     *  Gets the headstr attribute of the ElementBean object
     *
     *@return    The headstr value
     */
    public String getHeadstr() {
        return headstr;
    }


    /**
     *  Gets the isnull attribute of the ElementBean object
     *
     *@return    The isnull value
     */
    public boolean isIsnull() {
        return isnull;
    }


    /**
     *  Gets the isPrimaryKey attribute of the ElementBean object
     *
     *@return    The isPrimaryKey value
     */
    public boolean isIsPrimaryKey() {
        return isPrimaryKey;
    }


    /**
     *  Gets the isrepeate attribute of the ElementBean object
     *
     *@return    The isrepeate value
     */
    public boolean isIsrepeate() {
        return isrepeate;
    }


    /**
     *  Gets the isSearchKey attribute of the ElementBean object
     *
     *@return    The isSearchKey value
     */
    public boolean isIsSearchKey() {
        return isSearchKey;
    }


    /**
     *  Gets the maxLength attribute of the ElementBean object
     *
     *@return    The maxLength value
     */
    public int getMaxLength() {
        return maxLength;
    }


    /**
     *  Gets the minLength attribute of the ElementBean object
     *
     *@return    The minLength value
     */
    public int getMinLength() {
        return minLength;
    }


    /**
     *  Gets the multiple attribute of the ElementBean object
     *
     *@return    The multiple value
     */
    public boolean isMultiple() {
        return multiple;
    }


    /**
     *  Gets the name attribute of the ElementBean object
     *
     *@return    The name value
     */
    public String getName() {
        return name;
    }


    /**
     *  Gets the onclick attribute of the ElementBean object
     *
     *@return    The onclick value
     */
    public String getOnclick() {
        return onclick;
    }


    /**
     *  Gets the onchange attribute of the ElementBean object
     *
     *@return    The onchange value
     */
    public String getOnchange() {
        return onchange;
    }


    /**
     *  Gets the onsubmit attribute of the ElementBean object
     *
     *@return    The onsubmit value
     */
    public String getOnsubmit() {
        return onsubmit;
    }


    /**
     *  Gets the others attribute of the ElementBean object
     *
     *@return    The others value
     */
    public String getOthers() {
        return others;
    }


    /**
     *  Gets the readonly attribute of the ElementBean object
     *
     *@return    The readonly value
     */
    public boolean isReadonly() {
        return readonly;
    }


    /**
     *  Gets the refnamefld attribute of the ElementBean object
     *
     */

    /**
     *  Gets the refvaluefld attribute of the ElementBean object
     *
     */

    /**
     *  Gets the reftbl attribute of the ElementBean object
     *
     */

    /**
     *  Gets the refwhere attribute of the ElementBean object
     *
     */

    /**
     *  Gets the rows attribute of the ElementBean object
     *
     *@return    The rows value
     */
    public int getRows() {
        return rows;
    }


    /**
     *  Gets the seqno attribute of the ElementBean object
     *
     *@return    The seqno value
     */
    public int getSeqno() {
        return seqno;
    }


    /**
     *  Gets the size attribute of the ElementBean object
     *
     *@return    The size value
     */
    public int getSize() {
        return size;
    }


    /**
     *  Gets the type attribute of the ElementBean object
     *
     *@return    The type value
     */
    public int getType() {
        return type;
    }


    /**
     *  Gets the valueset attribute of the ElementBean object
     *
     *@return    The valueset value
     */
    public String getValueset() {
        return valueset;
    }


    /**
     *  Gets the valuesettype attribute of the ElementBean object
     *
     *@return    The valuesettype value
     */
    public int getValuesettype() {
        return valuesettype;
    }


    /**
     *  Gets the visible attribute of the ElementBean object
     *
     *@return    The visible value
     */
    public boolean isVisible() {
        return visible;
    }


    /**
     *  Gets the xposition attribute of the ElementBean object
     *
     *@return    The xposition value
     */
    public int getXposition() {
        return xposition;
    }


    /**
     *  Gets the yposition attribute of the ElementBean object
     *
     *@return    The yposition value
     */
    public int getYposition() {
        return yposition;
    }


    /**
     *  Sets the afterstr attribute of the ElementBean object
     *
     *@param  afterstr  The new afterstr value
     */
    public void setAfterstr(String afterstr) {
        this.afterstr = afterstr;
    }


    /**
     *  Sets the cols attribute of the ElementBean object
     *
     *@param  cols  The new cols value
     */
    public void setCols(int cols) {
        this.cols = cols;
    }


    /**
     *  Sets the comment attribute of the ElementBean object
     *
     *@param  comment  The new comment value
     */
    public void setComment(String comment) {
        this.comment = comment;
    }


    /**
     *  Sets the componetTp attribute of the ElementBean object
     *
     *@param  componetTp  The new componetTp value
     */
    public void setComponetTp(int componetTp) {
        this.componetTp = componetTp;
    }


    /**
     *  Sets the defaultValue attribute of the ElementBean object
     *
     *@param  defaultValue  The new defaultValue value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }


    /**
     *  Sets the dataType attribute of the ElementBean object
     *
     *@param  dataType  The new dataType value
     */
    public void setDataType(int dataType) {
        this.dataType = dataType;
    }


    /**
     *  Sets the description attribute of the ElementBean object
     *
     *@param  description  The new description value
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     *  Sets the disabled attribute of the ElementBean object
     *
     *@param  disabled  The new disabled value
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }


    /**
     *  Sets the formatcls attribute of the ElementBean object
     *
     *@param  formatcls  The new formatcls value
     *@param  type       The new formatcls value
     */
    public void setFormatcls(String formatcls, int type) {
        //1是list,这时处理
        if (type == FormBean.LIST_TYPE || type == FormBean.QUERY_TYPE ) {
            try {
                pub.platform.form.util.FieldFormat ff = (pub.platform.form.util.FieldFormat) Class.forName(formatcls).newInstance();
                this.formatcls = ff;
            } catch (Exception ex) {
                this.formatcls = new pub.platform.form.util.FieldFormat();
            }
        }
    }


    /**
     *  Sets the enutpname attribute of the ElementBean object
     *
     *@param  enutpname  The new enutpname value
     */
    public void setEnutpname(String enutpname) {
        this.enutpname = enutpname;
    }


    /**
     *  Sets the headstr attribute of the ElementBean object
     *
     *@param  headstr  The new headstr value
     */
    public void setHeadstr(String headstr) {
        this.headstr = headstr;
    }


    /**
     *  Sets the isnull attribute of the ElementBean object
     *
     *@param  isnull  The new isnull value
     */
    public void setIsnull(boolean isnull) {
        this.isnull = isnull;
    }


    /**
     *  Sets the isPrimaryKey attribute of the ElementBean object
     *
     *@param  isPrimaryKey  The new isPrimaryKey value
     */
    public void setIsPrimaryKey(boolean isPrimaryKey) {
        this.isPrimaryKey = isPrimaryKey;
    }


    /**
     *  Sets the isrepeate attribute of the ElementBean object
     *
     *@param  isrepeate  The new isrepeate value
     */
    public void setIsrepeate(boolean isrepeate) {
        this.isrepeate = isrepeate;
    }


    /**
     *  Sets the isSearchKey attribute of the ElementBean object
     *
     *@param  isSearchKey  The new isSearchKey value
     */
    public void setIsSearchKey(boolean isSearchKey) {
        this.isSearchKey = isSearchKey;
    }


    /**
     *  Sets the maxLength attribute of the ElementBean object
     *
     *@param  maxLength  The new maxLength value
     */
    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }


    /**
     *  Sets the minLength attribute of the ElementBean object
     *
     *@param  minLength  The new minLength value
     */
    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }


    /**
     *  Sets the multiple attribute of the ElementBean object
     *
     *@param  multiple  The new multiple value
     */
    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }


    /**
     *  Sets the name attribute of the ElementBean object
     *
     *@param  name  The new name value
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     *  Sets the onchange attribute of the ElementBean object
     *
     *@param  onchange  The new onchange value
     */
    public void setOnchange(String onchange) {
        this.onchange = onchange;
    }


    /**
     *  Sets the onclick attribute of the ElementBean object
     *
     *@param  onclick  The new onclick value
     */
    public void setOnclick(String onclick) {
        this.onclick = onclick;
    }


    /**
     *  Sets the onsubmit attribute of the ElementBean object
     *
     *@param  onsubmit  The new onsubmit value
     */
    public void setOnsubmit(String onsubmit) {
        this.onsubmit = onsubmit;
    }


    /**
     *  Sets the others attribute of the ElementBean object
     *
     *@param  others  The new others value
     */
    public void setOthers(String others) {
        this.others = others;
    }


    /**
     *  Sets the readonly attribute of the ElementBean object
     *
     *@param  readonly  The new readonly value
     */
    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
    }


    /**
     *  Sets the refnamefld attribute of the ElementBean object
     *
     */

    /**
     *  Sets the reftbl attribute of the ElementBean object
     *
     */

    /**
     *  Sets the refvaluefld attribute of the ElementBean object
     *
     */

    /**
     *  Sets the rows attribute of the ElementBean object
     *
     *@param  rows  The new rows value
     */
    public void setRows(int rows) {
        this.rows = rows;
    }


    /**
     *  Sets the refwhere attribute of the ElementBean object
     *
     */

    /**
     *  Sets the seqno attribute of the ElementBean object
     *
     *@param  seqno  The new seqno value
     */
    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }


    /**
     *  Sets the type attribute of the ElementBean object
     *
     *@param  type  The new type value
     */
    public void setType(int type) {
        this.type = type;
    }


    /**
     *  Sets the size attribute of the ElementBean object
     *
     *@param  size  The new size value
     */
    public void setSize(int size) {
        this.size = size;
    }


    /**
     *  Sets the valueset attribute of the ElementBean object
     *
     *@param  valueset  The new valueset value
     */
    public void setValueset(String valueset) {
        this.valueset = valueset;
    }


    /**
     *  Sets the visible attribute of the ElementBean object
     *
     *@param  visible  The new visible value
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    /**
     *  Sets the valuesettype attribute of the ElementBean object
     *
     *@param  valuesettype  The new valuesettype value
     */
    public void setValuesettype(int valuesettype) {
        this.valuesettype = valuesettype;
    }


    /**
     *  Sets the xposition attribute of the ElementBean object
     *
     *@param  xposition  The new xposition value
     */
    public void setXposition(int xposition) {
        this.xposition = xposition;
    }


    /**
     *  Sets the yposition attribute of the ElementBean object
     *
     *@param  yposition  The new yposition value
     */
    public void setYposition(int yposition) {
        this.yposition = yposition;
    }


    /**
     *  Gets the middleStr attribute of the ElementBean object
     *
     *@return    The middleStr value
     */
    public String getMiddleStr() {
        return middleStr;
    }


    /**
     *  Sets the middleStr attribute of the ElementBean object
     *
     *@param  middleStr  The new middleStr value
     */
    public void setMiddleStr(String middleStr) {
        this.middleStr = middleStr;
    }


    /**
     *  Gets the caption attribute of the ElementBean object
     *
     *@return    The caption value
     */
    public String getCaption() {
        return caption;
    }


    /**
     *  Sets the caption attribute of the ElementBean object
     *
     *@param  caption  The new caption value
     */
    public void setCaption(String caption) {
        this.caption = caption;
    }


    //formid,seqno,type,name,defaultvalue,
//caption,description,readonly,minlength,maxlength,
//size,visible,isnull,compnenttype(char(2)),datatype,
//xposition,yposition,rows,cols,multiple,
//disabled,valuesettype,valueset,headstr,middlestr
//afterstr,formatcls,onclick,onchange,onsubmit
//others

    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        String text = "";
        text = getSeqno() + ":" + getType() + ":" + getName() + ":" + getDefaultValue() + ":" + "|" +
                getCaption() + ":" + getDescription() + ":" + isReadonly() + ":" + getMinLength() + ":" + getMaxLength() + "|" +
                getSize() + ":" + isVisible() + ":" + isIsnull() + ":" + getComponetTp() + ":" + getDataType() + "|" +
                getXposition() + ":" + getYposition() + ":" + getRows() + ":" + getCols() + ":" + isMultiple() + "|" +
                isDisabled() + ":" + getValuesettype() + ":" + getValueset() + ":" + getHeadstr() + ":" + getMiddleStr() + "|" +
                getAfterstr() + ":" + getFormatcls() + ":" + getOnclick() + ":" + getOnchange() + ":" + getOnsubmit() + "|" +
                getOthers() + "########" + "enuname==" + getEnutpname();
        return text;
    }


    /**
     *  Gets the needEncode attribute of the ElementBean object
     *
     *@return    The needEncode value
     */
    public boolean isNeedEncode() {
        return needEncode;
    }


    /**
     *  Sets the needEncode attribute of the ElementBean object
     *
     *@param  needEncode  The new needEncode value
     */
    public void setNeedEncode(boolean needEncode) {
        this.needEncode = needEncode;
    }


    /**
     *  Gets the refTbl attribute of the ElementBean object
     *
     *@return    The refTbl value
     */
    public String getRefTbl() {
        return refTbl;
    }


    /**
     *  Sets the refTbl attribute of the ElementBean object
     *
     *@param  refTbl  The new refTbl value
     */
    public void setRefTbl(String refTbl) {
        this.refTbl = refTbl;
    }


    /**
     *  Gets the refName attribute of the ElementBean object
     *
     *@return    The refName value
     */
    public String getRefName() {
        return refName;
    }


    /**
     *  Sets the refName attribute of the ElementBean object
     *
     *@param  refName  The new refName value
     */
    public void setRefName(String refName) {
        this.refName = refName;
    }


    /**
     *  Gets the refValue attribute of the ElementBean object
     *
     *@return    The refValue value
     */
    public String getRefValue() {
        return refValue;
    }


    /**
     *  Sets the refValue attribute of the ElementBean object
     *
     *@param  refValue  The new refValue value
     */
    public void setRefValue(String refValue) {
        this.refValue = refValue;
    }


    /**
     *  Gets the refWhere attribute of the ElementBean object
     *
     *@return    The refWhere value
     */
    public String getRefWhere() {
        return refWhere;
    }


    /**
     *  Sets the refWhere attribute of the ElementBean object
     *
     *@param  refWhere  The new refWhere value
     */
    public void setRefWhere(String refWhere) {
        this.refWhere = refWhere;
    }
    public int getPrecision()
    {
        return precision;
    }
    public void setPrecision(int precision)
    {
        this.precision = precision;
    }
    public int getDecimalDigits()
    {
        return decimalDigits;
    }
    public void setDecimalDigits(int decimalDigits)
    {
        this.decimalDigits = decimalDigits;
    }
    public int getWidth()
    {
        return width;
    }
    public void setWidth(int width)
    {
        this.width = width;
    }
    public String getExpression()
    {
        return expression;
    }
    public void setExpression(String expression)
    {
        this.expression = expression;
    }
    public int getDisplayType()
    {
        return displayType;
    }
    public void setDisplayType(int displayType)
    {
        this.displayType = displayType;
    }
    public boolean isChangeEvent()
    {
        return changeEvent;
    }
    public void setChangeEvent(boolean changeEvent)
    {
        this.changeEvent = changeEvent;
    }

    public Object clone() {
        Object object = null;
        try {
            object = super.clone();
        } catch (CloneNotSupportedException exception) {
            System.err.println("AbstractSpoon is not Cloneable");
        }
        return object;
    }

}
