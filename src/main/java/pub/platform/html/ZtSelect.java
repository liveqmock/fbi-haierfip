package pub.platform.html;

import org.apache.ecs.html.*;

import pub.platform.db.*;
import pub.platform.form.config.*;
import pub.platform.utils.DbUtil;

import java.util.*;

/**
 * 根据枚举类型变量生成下拉菜单程序.
 * 示例：
 *     ZtSelect zs = new ZtSelect("test","02","03");
 *     zs.addAttr("onClick","javascript:alert('dddd')");
 *     zs.addOption("无","1234");
 *     System.out.println(zs);
 *
 * 返回结果：
 *     <select name="test" onClick="javascript:alert('dddd')">
 *          <option value="1234">无
 *          <option value="01">有线装机施工单
 *          <option value="02">有线开通施工单
 *          <option selected value="03">有线报停施工单
 *          ....
 *     </select>
 *
 */

public class ZtSelect {
  String name = "";         //下拉菜单名称
  String enuType = "";      //使用的枚举类型
  String defValue;          //默认值
  Set nousedValue;           //枚举值中的选中值

  boolean displayAll;       //是否显示（值-名称）
  HashMap attr = new HashMap();  //自定义的属性集合
  HashMap options = new HashMap(); //自定义的选项
  RecordSet rs ;            //生成菜单使用的结果集，为值，名称两列数据
  String sqlString;         //传入的SQL语句，通过调用执行产生结果集生成菜单
  boolean isSelectNull = true;
  /**
   * 构造方法
   * @param name 下拉菜单名称
   * @param enuType 下拉菜单对应的枚举类型
   * @param defValue 下拉菜单的默认值
   */
  public ZtSelect(String name,String enuType,String defValue) {
    this.name = name;
    this.enuType = enuType;
    this.defValue = defValue;
    attr.put("fieldname",name);
    attr.put("fieldtype","select");
  }

  /**
   * 构造方法
   * @param name 下拉菜单名称
   * @param enuType 下拉菜单对应的枚举类型
   * @param defValue 下拉菜单的默认值
   */
  public ZtSelect(String name,String enuType) {
    this.name = name;
    this.enuType = enuType;
    attr.put("fieldname",name);
    attr.put("fieldtype","select");
  }

  /**
   * 构造方法
   *
   * @param name 下拉菜单名称
   * @param rs 下拉菜单对应的结果集(包括值和名称两列)
   * @param defValue 下拉菜单的默认值
   */
  public ZtSelect(String name,RecordSet rs,String defValue) {
    this.name = name;
    this.rs = rs;
    this.defValue = defValue;
    attr.put("fieldname",name);
    attr.put("fieldtype","select");
  }

  public static void main(String[] args) {

    ZtSelect zs = new ZtSelect("busscode","citycode","03");
    zs.setSqlString("select itemcode,itemname from coreitemdefine where rownum<=5");
    zs.addAttr("onClick","javascript:alert('dddd')");
    zs.addOption("无","1234");
    zs.setDisplayAll(true);
    System.out.println(zs);
  }

  /**
   * 增加Select的属性
   * @param attrName 属性名称
   * @param attrCont 属性的内容字符串
   */
  public void addAttr(String attrName,String attrCont) {
	if (attrCont == null)
		attrCont = "";
    attr.put(attrName,attrCont);
  }

  /**
   * 手工添加选择项
   * @param optName 选择项的名称
   * @param optValue 选择项的值
   */
  public void addOption(String optName,String optValue) {
    options.put(optName,optValue);
  }

  /**
   * 是否显示值
   * @param displayAll
   */
  public void setDisplayAll(boolean displayAll) {
    this.displayAll = displayAll;
  }

  private void setAttr(Select se) {
    se.addAttribute("id",name);
    Iterator attrit = attr.keySet().iterator();
    while(attrit.hasNext()) {
      Object object = attrit.next();
      se.addAttribute((String)object,(String)attr.get(object));
    }
  }

  private void setOption(Select se) {
    Iterator optit = options.keySet().iterator();
    while(optit.hasNext()) {
      Object object = optit.next();
      String[] enumArr=((String) object).split(";");

      Option option = new Option();
      if (displayAll)
        option.addElement((String) options.get(object) +" - "+enumArr[0]);
      else
        option.addElement(enumArr[0]);
      option.setValue( (String) options.get(object));

      if (enumArr.length >1)
           option.addAttribute("expand",enumArr[1]);

      if (object.equals(defValue))
        option.setSelected(true);
      se.addElement(option);
    }
  }

  private String selectNull() {
       Script script = new Script();
       script.setLanguage("javascript");
       script.addElement("document.all(\""+name+"\").value='"+defValue+"';");

       if (this.defValue!=null)
            return script.toString();
       else
            return "";
  }

  /**
   * 通用的生成下拉菜单的方法
   * 需要兼顾传SQL语句的方式
   *          结果集的方式
   * @return String
   */
  public String toString() {
    //传SQL语句
    if (sqlString != null){
    	 String rtn="";
    	 rtn=this.getRsSelect();
    	 //System.out.println("ddd:"+rtn);    	 
         return this.getRsSelect();
    }
    //结果集的方式
    if (rs!=null)
         return this.getRsSelect();
    //普通的通过枚举变量方式
    Select se = new Select(name);
    setAttr(se);     //加入自定义属性
    setOption(se);   //加入自定义选项

    //根据枚举变量构造选项
    EnumerationBean eb = EnumerationType.getEnu(enuType);
    if (eb == null)
         return se.toString();
    Collection tmpKey = eb.getKeys();
    Object[] keys = tmpKey.toArray();
    for ( int i = 0 ; i < keys.length ; i++ ) {
      Object object = keys[i];
      //如果传入了选定值，则只有选定的值才显示
      if (nousedValue!=null && nousedValue.contains(object))
           continue;
      Option option = new Option();

       String[] enumArr=((String)eb.getValue(object)).split(";");

      if (displayAll)
        option.addElement((String)object +" - " +enumArr[0]);
      else
        option.addElement(enumArr[0]);
      option.setValue((String)object);

      if (enumArr.length >1)
           option.addAttribute("expand",enumArr[1]);

      if (object.equals(defValue))
        option.setSelected(true);
      se.addElement(option);
    }
    if(this.isSelectNull){    	 
         return se.toString() + selectNull();
      
    }else{
         return se.toString();
    }
  }


  public String optionToString() {
       String opts = "";
       EnumerationBean eb = EnumerationType.getEnu(enuType);
       Iterator it = eb.getKeys().iterator();
       while(it.hasNext()) {
            Object object = it.next();
            Option option = new Option();

             String[] enumArr=((String)eb.getValue(object)).split(";");
            if(displayAll)
                 option.addElement((String)object + " - " +enumArr[0]);
            else
                 option.addElement(enumArr[0]);
            option.setValue((String)object);

            if (enumArr.length >1)
                 option.addAttribute("expand",enumArr[1]);

            if(object.equals(defValue))
                 option.setSelected(true);
            opts += option.toString();
       }

       return opts;
  }

  /**
   *  根据SQL语句查询生成下拉菜单
   *  如果传入SQL语句，则执行生成结果集
   *  否则，直接使用结果集
   * @return String
   */
  public String getRsSelect() {
    Select se = new Select(name);
    se.addAttribute("id",name);

    setAttr(se);
    setOption(se);

    if(sqlString != null)
         rs = DbUtil.getRecord(sqlString);

    while (rs!=null && rs.next()) {
      String value = rs.getString(0);
      String name = rs.getString(1);

        Option option = new Option();
        if (displayAll)
          option.addElement(  value+ " - " + name);
        else
          option.addElement( name);
        option.setValue( value);
        String retStr ="";
       if ( rs.getfieldCount()>2){
            for (int i=2; i< rs.getfieldCount() ; i++ ){
                 retStr = rs.getString(i);
                 if (retStr == null)
                      retStr ="";
               option.addAttribute(rs.getFieldName(i),retStr);
            }


       }

//        if (value.equals(defValue))
//          option.setSelected(true);
        se.addElement(option);
    }

    if(this.isSelectNull){
         return se.toString() + selectNull();
    }else{
         return se.toString();
    }

  }

  public void setDefValue(String defValue) {
    this.defValue = defValue;
  }
  public String getDefValue() {
    return defValue;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public void setSelectNull(boolean isselectnull){
       this.isSelectNull = isselectnull;
  }
     public void setSqlString(String sqlString) {
          this.sqlString = sqlString;
     }
     public void setNosedValue(Set nousedValue) {
          this.nousedValue = nousedValue;
     }

}
