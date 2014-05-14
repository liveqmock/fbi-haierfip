//Source file: e:\\java\\zt\\platform\\form\\config\\FieldBean.java

package pub.platform.form.config;
import java.util.*;

/**
 *  该类描述了数据库表字段的定义 每个属性的含义请参考Word Document:贷款平台档案设计.doc Notes:
 *  请添加每个属性的getter和setter方法
 *
 *@author     编程人员请替换
 *@created    2003年10月8日
 *@version    1.0
 */
public class FieldBean implements Comparable {

    /**
     *  字段名称
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String name;

    /**
     *  字段的顺序
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private int seqno;

    /**
     *  所属表的名称
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String tblname;

    /**
     *  是否表主键
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private boolean isPrimary;

    /**
     *  字段的类型,具体值请参照：DataType类，当是枚举类型时， 属性enutpname标识了具体的枚举名称
     */

    /**
     *  当字段类型是枚举类型时，定义了枚举类型名称
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String enutpname;

    /**
     *  是否Search字段，用于形成Search输入界面
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private boolean isSearch;

    /**
     *  字段长度
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private int length;

    /**
     *  是否允许为空
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private boolean isnull;

    /**
     *  是否允许重复
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private boolean isrepeat;

    /**
     *  用于显示界面，title
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String comment;

    /**
     *  字段的描述
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String description;

    /**
     *  缺省值
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String defaultValue;

    /**
     *  精度
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private int precision;

    /**
     *  小数位数，不包含小数点
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private int decimals;

    /**
     *  外键参考的表名
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String reftbl;

    /**
     *  参考注释字段名称
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String refnamefld;

    /**
     *  参考值字段名称
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String refvaluefld;

    /**
     *  参考的可选择Where串
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String refWhere;
    private String caption;
    private int datatype;

    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATA_TYPE_STRING = 1;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATA_TYPE_INTEGER = 2;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATA_TYPE_DECIMAL = 3;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATA_TYPE_BOOLEAN = 4;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATA_TYPE_DATE = 5;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月11日
     */
    public final static int DATA_TYPE_ENUMERATION = 6;
  private boolean needEncode;


    /**
     *  按字段的序号进行比较 <0 小于 =0 等于 >0 大于
     *
     *@param  o
     *@return     int
     *@roseuid    3F7160100166
     */
    public int compareTo(Object o) {
        int no = this.seqno - ((FieldBean) o).seqno;
        return this.seqno - ((FieldBean) o).seqno;
    }


    /**
     *  校验该字段的值是否合法 1。是否允许为空（isnull） 2。类型校验(datatype) 3。长度校验(length)
     *  4。小数位数校验(precision、decimals) 5。取值范围校验(枚举类型的取值范围)
     *
     *@param  value
     *@return        boolean
     *@roseuid       3F7262DB0254
     */
    public boolean validate(Object value) {
        return true;
    }


    /**
     *  Gets the comment attribute of the FieldBean object
     *
     *@return    The comment value
     */
    public String getComment() {
        return comment;
    }


    /**
     *  Sets the comment attribute of the FieldBean object
     *
     *@param  comment  The new comment value
     */
    public void setComment(String comment) {
        this.comment = checkString(comment);
    }


    /**
     *  Gets the datatype attribute of the FieldBean object
     *
     *@return    The datatype value
     */
    public int getDatatype() {
        return datatype;
    }


    /**
     *  Sets the datatype attribute of the FieldBean object
     *
     *@param  datatype  The new datatype value
     */
    public void setDatatype(int datatype) {
        this.datatype = datatype;
    }


    /**
     *  Gets the decimals attribute of the FieldBean object
     *
     *@return    The decimals value
     */
    public int getDecimals() {
        return decimals;
    }


    /**
     *  Gets the defaultValue attribute of the FieldBean object
     *
     *@return    The defaultValue value
     */
    public String getDefaultValue() {
        return checkString(defaultValue);
    }


    /**
     *  Gets the description attribute of the FieldBean object
     *
     *@return    The description value
     */
    public String getDescription() {
        return checkString(description);
    }


    /**
     *  Gets the enutpname attribute of the FieldBean object
     *
     *@return    The enutpname value
     */
    public String getEnutpname() {
        return checkString(enutpname);
    }


    /**
     *  Gets the isnull attribute of the FieldBean object
     *
     *@return    The isnull value
     */
    public boolean isIsnull() {
        return isnull;
    }


    /**
     *  Gets the isPrimary attribute of the FieldBean object
     *
     *@return    The isPrimary value
     */
    public boolean isIsPrimary() {
        return isPrimary;
    }


    /**
     *  Gets the isrepeat attribute of the FieldBean object
     *
     *@return    The isrepeat value
     */
    public boolean isIsrepeat() {
        return isrepeat;
    }


    /**
     *  Gets the isSearch attribute of the FieldBean object
     *
     *@return    The isSearch value
     */
    public boolean isIsSearch() {
        return isSearch;
    }


    /**
     *  Gets the length attribute of the FieldBean object
     *
     *@return    The length value
     */
    public int getLength() {
        return length;
    }


    /**
     *  Gets the name attribute of the FieldBean object
     *
     *@return    The name value
     */
    public String getName() {
        return checkString(name);
    }


    /**
     *  Gets the precision attribute of the FieldBean object
     *
     *@return    The precision value
     */
    public int getPrecision() {
        return precision;
    }


    /**
     *  Gets the refnamefld attribute of the FieldBean object
     *
     *@return    The refnamefld value
     */
    public String getRefnamefld() {
        return checkString(refnamefld);
    }


    /**
     *  Gets the refvaluefld attribute of the FieldBean object
     *
     *@return    The refvaluefld value
     */
    public String getRefvaluefld() {
        return checkString(refvaluefld);
    }


    /**
     *  Gets the reftbl attribute of the FieldBean object
     *
     *@return    The reftbl value
     */
    public String getReftbl() {
        return checkString(reftbl);
    }


    /**
     *  Gets the refWhere attribute of the FieldBean object
     *
     *@return    The refWhere value
     */
    public String getRefWhere() {
        return checkString(refWhere);
    }


    /**
     *  Gets the seqno attribute of the FieldBean object
     *
     *@return    The seqno value
     */
    public int getSeqno() {
        return seqno;
    }


    /**
     *  Gets the tblname attribute of the FieldBean object
     *
     *@return    The tblname value
     */
    public String getTblname() {
        return checkString(tblname);
    }


    /**
     *  Sets the decimals attribute of the FieldBean object
     *
     *@param  decimals  The new decimals value
     */
    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }


    /**
     *  Sets the defaultValue attribute of the FieldBean object
     *
     *@param  defaultValue  The new defaultValue value
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = checkString(defaultValue);
    }


    /**
     *  Sets the description attribute of the FieldBean object
     *
     *@param  description  The new description value
     */
    public void setDescription(String description) {
        this.description = checkString(description);
    }


    /**
     *  Sets the enutpname attribute of the FieldBean object
     *
     *@param  enutpname  The new enutpname value
     */
    public void setEnutpname(String enutpname) {
        this.enutpname = checkString(enutpname);
    }


    /**
     *  Sets the isnull attribute of the FieldBean object
     *
     *@param  isnull  The new isnull value
     */
    public void setIsnull(boolean isnull) {
        this.isnull = isnull;
    }


    /**
     *  Sets the isPrimary attribute of the FieldBean object
     *
     *@param  isPrimary  The new isPrimary value
     */
    public void setIsPrimary(boolean isPrimary) {
        this.isPrimary = isPrimary;
    }


    /**
     *  Sets the isrepeat attribute of the FieldBean object
     *
     *@param  isrepeat  The new isrepeat value
     */
    public void setIsrepeat(boolean isrepeat) {
        this.isrepeat = isrepeat;
    }


    /**
     *  Sets the isSearch attribute of the FieldBean object
     *
     *@param  isSearch  The new isSearch value
     */
    public void setIsSearch(boolean isSearch) {
        this.isSearch = isSearch;
    }


    /**
     *  Sets the length attribute of the FieldBean object
     *
     *@param  length  The new length value
     */
    public void setLength(int length) {
        this.length = length;
    }


    /**
     *  Sets the name attribute of the FieldBean object
     *
     *@param  name  The new name value
     */
    public void setName(String name) {
        this.name = checkString(name);
    }


    /**
     *  Sets the precision attribute of the FieldBean object
     *
     *@param  precision  The new precision value
     */
    public void setPrecision(int precision) {
        this.precision = precision;
    }


    /**
     *  Sets the refnamefld attribute of the FieldBean object
     *
     *@param  refnamefld  The new refnamefld value
     */
    public void setRefnamefld(String refnamefld) {
        this.refnamefld = checkString(refnamefld);
    }


    /**
     *  Sets the reftbl attribute of the FieldBean object
     *
     *@param  reftbl  The new reftbl value
     */
    public void setReftbl(String reftbl) {
        this.reftbl = checkString(reftbl);
    }


    /**
     *  Sets the refvaluefld attribute of the FieldBean object
     *
     *@param  refvaluefld  The new refvaluefld value
     */
    public void setRefvaluefld(String refvaluefld) {
        this.refvaluefld = checkString(refvaluefld);
    }


    /**
     *  Sets the refWhere attribute of the FieldBean object
     *
     *@param  refWhere  The new refWhere value
     */
    public void setRefWhere(String refWhere) {
        this.refWhere = checkString(refWhere);
    }


    /**
     *  Sets the seqno attribute of the FieldBean object
     *
     *@param  seqno  The new seqno value
     */
    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }


    /**
     *  Sets the tblname attribute of the FieldBean object
     *
     *@param  tblname  The new tblname value
     */
    public void setTblname(String tblname) {
        this.tblname = checkString(tblname);
    }


    /**
     *  Gets the caption attribute of the FieldBean object
     *
     *@return    The caption value
     */
    public String getCaption() {
        return checkString(caption);
    }


    /**
     *  Sets the caption attribute of the FieldBean object
     *
     *@param  caption  The new caption value
     */
    public void setCaption(String caption) {
        this.caption = checkString(caption);
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        String text = ":" + seqno + ":" + name + ":" + this.isPrimary + ":" + datatype
                + ":" + this.isSearch + ":" + length + ":" + isnull + ":" + isrepeat + ":" + caption
                + ":" + description + ":" + this.defaultValue + ":" + precision + ":" + this.decimals + ":" +
                reftbl + ":" + this.refnamefld + ":" + this.refvaluefld + ":" + this.refWhere+"#####enuname:"+getEnutpname();
        return text;
    }
  public boolean isNeedEncode() {
    return needEncode;
  }
  public void setNeedEncode(boolean needEncode) {
    this.needEncode = needEncode;
  }
  public String checkString(String key){
      if(key==null){
          return "";
      }else{
          return key.trim();
      }
  }
}
