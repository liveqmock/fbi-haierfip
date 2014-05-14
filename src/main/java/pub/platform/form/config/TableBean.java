//Source file: e:\\java\\zt\\platform\\form\\config\\TableBean.java

package pub.platform.form.config;

import java.util.*;
/**
 *  表信息 包含了表的名称、类型、字段、主键、查询字段信息
 *
 *@author     请替换
 *@created    2003年10月8日
 *@version    1.0
 */
public class TableBean {

    /**
     *  表名称
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String name;

    /**
     *  表类型 0－静态表 1－动态表
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private int type;

    /**
     *  存放动态表记录的表名称
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String dynamictbl;

    /**
     *  字段集合 以字段名称－字段实例值对的方式存放
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private Map fields;

    /**
     *  主键名称数组 类初始化时，根据字段的定义信息，isPrimary的值来初始化该数组
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String[] primaryKey = new String[0];

    /**
     *  查询字段名称数组 类初始化时，根据字段的定义信息，isSearch的值来初始化该数组
     *
     *@author    sun
     *@since     2003年10月8日
     */
    private String[] searchKey = new String[0];

    private String description;

    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月8日
     */
    public final static int STATIC_TABLE = 0;
    /**
     *  Description of the Field
     *
     *@author    sun
     *@since     2003年10月8日
     */
    public final static int DYNAMIC_TYPE = 1;


    /**
     *@param  name
     *@roseuid      3F7162780021
     */
    public TableBean(String name) {
        fields = new HashMap();
        this.name = name;
    }


    /**
     *@roseuid        3F7162560285
     */
//    public TableBean(String name, Map fields) {
//
//    }

    /**
     *  取得查询键字段 返回值Field[],必须是按照字段序号排序的
     *
     *@return     zt.platform.form.config.FieldBean[]
     *@roseuid    3F7161E20224
     */
    public FieldBean[] getSearchKey() {
        FieldBean[] fs = new FieldBean[searchKey.length];
        for (int i = 0; i < this.searchKey.length; i++) {
            fs[i] = (FieldBean) this.fields.get(searchKey[i]);
        }
        return fs;
    }


    /**
     *  取得主键字段 返回值Field[],必须是按照字段序号排序的
     *
     *@return     zt.platform.form.config.FieldBean[]
     *@roseuid    3F716206004F
     */
    public FieldBean[] getPrimaryKey() {
        FieldBean[] fs = new FieldBean[primaryKey.length];
        for (int i = 0; i < this.primaryKey.length; i++) {
            fs[i] = (FieldBean) this.fields.get(primaryKey[i]);
        }
        return fs;
    }


    /**
     *  取得指定名称的字段
     *
     *@param  name
     *@return       zt.platform.form.config.FieldBean
     *@roseuid      3F7162230277
     */
    public FieldBean getField(String name) {
        return (FieldBean) this.fields.get(name);
    }


    /**
     *  取得所有的字段 返回值Field[],必须是按照字段序号排序的
     *
     *@return     zt.platform.form.config.FieldBean[]
     *@roseuid    3F71623101AF
     */
    public FieldBean[] getFields() {
        Object[] values = this.fields.values().toArray();
        Arrays.sort(values);
        FieldBean fs[] = new FieldBean[values.length];
        for (int i = 0; i < values.length; i++) {
            fs[i] = (FieldBean) values[i];
        }
        return fs;
    }


    /**
     *@return     String
     *@roseuid    3F71625001DC
     */
    public String getName() {
        return this.name;
    }


    /**
     *@return     boolean
     *@roseuid    3F78FC9F0005
     */
    public boolean isDynamic() {
        if (this.getType() == this.DYNAMIC_TYPE) {
            return true;
        } else {
            return false;
        }
    }


    /**
     *@return     boolean
     *@roseuid    3F78FCAC016C
     */
    public boolean isStatic() {
        if (this.getType() == this.DYNAMIC_TYPE) {
            return false;
        } else {
            return true;
        }
    }


    /**
     *@return     String
     *@roseuid    3F7CDBA002BB
     */
    public String getDynamicTbl() {
        return this.dynamictbl;
    }


    /**
     *@param  name
     *@roseuid      3F7CDBAC02FE
     */
    public void setDynamicTbl(String name) {
        this.dynamictbl = name;
    }


    /**
     *@param  field
     *@roseuid       3F78FCC8020D
     */
    public void addField(FieldBean field) {
        this.fields.put(field.getName(), field);
        if (field.isIsPrimary()) {
            this.makePrimary(field);
        }
        if (field.isIsSearch()) {
            this.makeSearch(field);
        }
    }


    /**
     *  Description of the Method
     *
     *@param  field  Description of the Parameter
     */
    private void makePrimary(FieldBean field) {
        Collection keys = new Vector();
        for (int i = 0; i < this.primaryKey.length; i++) {
            keys.add(primaryKey[i]);
        }
        keys.add(field.getName());
        this.primaryKey = this.convertToStrings(keys.toArray());
    }


    /**
     *  Description of the Method
     *
     *@param  field  Description of the Parameter
     */
    private void makeSearch(FieldBean field) {
        Collection keys = new Vector();
        for (int i = 0; i < this.searchKey.length; i++) {
            keys.add(searchKey[i]);
        }
        keys.add(field.getName());
        this.searchKey = this.convertToStrings(keys.toArray());
    }


    /**
     *  Gets the description attribute of the TableBean object
     *
     *@return    The description value
     */
    public String getDescription() {
        return description;
    }


    /**
     *  Sets the description attribute of the TableBean object
     *
     *@param  description  The new description value
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     *  Gets the type attribute of the TableBean object
     *
     *@return    The type value
     */
    public int getType() {
        return type;
    }


    /**
     *  Sets the type attribute of the TableBean object
     *
     *@param  type  The new type value
     */
    public void setType(int type) {
        this.type = type;
    }


    /**
     *  Description of the Method
     *
     *@param  os  Description of the Parameter
     *@return     Description of the Return Value
     */
    private String[] convertToStrings(Object[] os) {
        String[] keys = new String[os.length];
        for (int i = 0; i < os.length; i++) {
            keys[i] = (String) os[i];
        }
        return keys;
    }
}
