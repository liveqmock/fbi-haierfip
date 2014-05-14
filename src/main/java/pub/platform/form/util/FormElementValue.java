//Source file: e:\\java\\zt\\platform\\form\\util\\FormElementValue.java

package pub.platform.form.util;

import java.util.*;

import pub.platform.form.util.datatype.*;


/**
 * Form元素值的载体
 *
 * 负责存放每个元素的值
 *
 * @author qingdao tec
 * @version 1.0
 */
public class FormElementValue {
  private List values = new ArrayList();
  private boolean readonly=false;
  private boolean isnull=true;
  private boolean visible=true;
  private boolean disabled=false;
  private int type;
  /**
   * 1. String 在List中是java.lang.String
   * 2. integer在List中是java.lang.Integer
   * 3. decimal在List中是java.lang.Double
   * 4. boolean在List中是java.lang.String (0-false 1-true)
   * 5. Date   在List中是java.lang.String (格式MM/DD/YYYY)
   * 6. Enumeration在List中是java.lang.Integer
   */

  public FormElementValue(int p_type) {
    this.type = p_type;
  }

  /**
   * @param values
   * @roseuid 3F7E20E003DD
   */
  public FormElementValue(List values, int p_type) {
    this.values = values;
    this.type = p_type;
  }

  /**
   * @param value
   * @roseuid 3F7E20EB0099
   */
  public void add(Object value) {
    this.values.add(value);
  }

  /**
   * @roseuid 3F7E229A02F2
   */
  public void clear() {
    this.values.clear();
  }

  /**
   * @param idx
   * @roseuid 3F7E22A002DD
   */
  public void remove(int idx) {
    this.values.remove(idx);
  }

  /**
   * @param value
   * @roseuid 3F7E22AB0062
   */
  public void remove(Object value) {
    this.values.remove(value);
  }

  public void setValue(List p_value) {
    this.values = p_value;
  }

  public List getValue() {
    return values;
  }

  public void setType(int p_type) {
    this.type = p_type;
  }

  public int getType() {
    return this.type;
  }

  public String[] getValueArray() {
    if (this.values.size() < 1) {
      return null;
    }
    String[] tmpStr = new String[this.values.size()];
    for (int i = 0; i < tmpStr.length; i++) {
      //null值都返回""
      if (values.get(i) == null) {
        tmpStr[i] = "";
        continue;
      }
      //根据数据类型(type)，将数据作相应的转化再转到String
      //INTEGER_TYPE,DECIAML_TYPE类型在最大值时转成""
      String value;
      if (this.type == DataType.INTEGER_TYPE || this.type == DataType.ENUMERATION_TYPE) {
        int temp=((Integer) values.get(i)).intValue();
        if(temp==Integer.MAX_VALUE){
          value="";
        }
        else{
          value = temp + "";
        }
      }
      else if (this.type == DataType.DECIAML_TYPE) {
        double temp=((Double)values.get(i)).doubleValue();
        if(temp==Double.MAX_VALUE){
          value="";
        }
        else{
          value = pub.platform.db.DBUtil.doubleToStr(temp);
        }
      }
      else {
        value = values.get(i).toString();
      }
      tmpStr[i] = value;
    }
    return tmpStr;
  }

  public boolean isReadonly() {
    return readonly;
  }
  public void setReadonly(boolean readonly) {
    this.readonly = readonly;
  }

  public boolean isIsNull() {
    return isnull;
  }
  public void setIsNull(boolean p_isnull) {
    this.isnull = p_isnull;
  }

  public boolean isVisible() {
    return visible;
  }
  public void setVisible(boolean p_visible) {
    this.visible = p_visible;
  }

  public boolean isDisabled() {
    return disabled;
  }
  public void setDisabled(boolean p_disabled) {
    this.disabled = p_disabled;
  }

  //单元测试用
  public static void main(String[] args) {
    FormElementValue fev = new FormElementValue(0);
    fev.add("asdf1");
    fev.add("sadf2");
    String[] ddd = fev.getValueArray();
    System.out.println(ddd[0]);
    System.out.println(ddd[1]);
  }
}