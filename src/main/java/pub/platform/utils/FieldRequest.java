package pub.platform.utils;

import java.io.Serializable;

public class FieldRequest {

     private String name; ///字段名称
     private String type; ////字段类型
     private String value; ////字段值
     private String oldValue; ///旧的字段值
     private String fieldLable;////字段中文解释

     public FieldRequest() {
     }

     public void setName(String name) {
          this.name = name;
     }

     public String getName() {
          return name;
     }

     public void setType(String type) {
          this.type = type;
     }

     public String geType() {
          return type;

     }

     public void setValue(String value) {
          this.value = value;
     }

     public String getValue() {
          return value;

     }

     public String getoldValue() {
          return oldValue;
     }

     public void setoldValue(String oldValue) {
          this.oldValue = oldValue;
     }

     public String getfieldLable() {
         return fieldLable;
    }

    public void setfieldLable(String fieldLable) {
         this.fieldLable = fieldLable;
    }

}
