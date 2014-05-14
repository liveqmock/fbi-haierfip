package pub.platform.utils;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import pub.platform.utils.*;

import java.io.*;
import java.util.*;

public class ActionRequest {
   private Map recorder;          /////存放第一类信息
   private String SQLType;        ////操作类型

   private Map recorderNames;     /////按名称存放所有类型的信息
   private Vector recorderInts;   /////存放所有类型的信息
   private Map SQLTypeNames;     /////按名称存放所有类型的信息的类型
   private Vector SQLTypeInts;   /////存放所有类型的信息的类型
   private int recorderCount;    ///操作类型个数
   private String MethodName;   //////////操作方法名称


    public ActionRequest() {

        recorder= new HashMap();
        recorderNames= new HashMap();
        SQLTypeNames= new HashMap();
        recorderInts = new Vector();
        SQLTypeInts =  new  Vector();

        MethodName ="";
    }

    /////给方法赋值
    public void setmethodName(String methodname){
         MethodName =methodname;
    }

    ///////返回方法名称
    public String getmethodName(){
       return MethodName;
   }

    ///返回操作类型
    public String getType(){
        return SQLType;
    }
    ///给操作类型负值
   public void setType(String SQLType){
       this.SQLType = SQLType;
   }

  ////添加字段类
   public void setRecorder(Map recorder)
   {
       this.recorder =recorder;

   }

    ////返回字段类型
    public String getFieldType(String fieldName)
    {
         FieldRequest fieldRequest =  (FieldRequest)recorder.get(fieldName.trim().toLowerCase());
         return fieldRequest.geType();
    }


    ///返回字段值
    public String getFieldValue(String fieldName)
   {
        FieldRequest fieldRequest =  (FieldRequest)recorder.get(fieldName.trim().toLowerCase());
        if ( fieldRequest != null )
             return fieldRequest.getValue();
        else
             return null;
   }



   ///返回旧字段值
   public String getFieldOldValue(String fieldName)
  {
       FieldRequest fieldRequest =  (FieldRequest)recorder.get(fieldName.trim().toLowerCase());
       if ( fieldRequest != null )
            return fieldRequest.getoldValue();
       else
            return null;
  }

  ///返回字段表示
  public String getFieldLable(String fieldName)
 {
      FieldRequest fieldRequest =  (FieldRequest)recorder.get(fieldName.trim().toLowerCase());
      if ( fieldRequest != null )
           return fieldRequest.getfieldLable();
      else
           return null;
 }




  /* ////根据操作名称添加操作类型
  public void setTypeS(String name,String SQLType){
     SQLTypeNames.put(name.trim().toLowerCase(),SQLType);
  }

   ////根据操作名称返回操作类型
   public String getTypeS(String name){
      return  (String)SQLTypeNames.get(name.trim().toLowerCase());
   }
*/
   /////根据操作顺序返回操作类型
   public void setTypeS(String SQLType){
      SQLTypeInts.add(SQLType);
   }

   /////根据操作顺序返回操作类型
   public String getTypeS(int index){
      return  (String)SQLTypeInts.get(index);
   }

   /////根据操作类型返回操作顺序
   public int getIndexOfTypeS(String SQLType){
      return  SQLTypeInts.indexOf(SQLType);
   }

 /*  /////根据操作名称添加操作
   public void setRecorderName(String name,Map recorder)
    {
       recorderNames.put(name.trim().toLowerCase(),recorder);

    }*/
    /////根据添加操作
   public void setRecorderInt(Map recorder)
    {
       recorderInts.add(recorder);

    }
/*
   /////根据操作名称和字段名称返回字段类型
   public String getFieldType(String name,String fieldName)
    {
        Map recorderName  = (Map)recorderNames.get(name.trim().toLowerCase());

        FieldRequest fieldRequest =  (FieldRequest)recorderName.get(fieldName.trim().toLowerCase());
        return fieldRequest.geType();
    }

    /////根据操作名称和字段名称返回字段值
   public String getFieldValue(String name,String fieldName)
    {
        Map recorderName  = (Map)recorderNames.get(name.trim().toLowerCase());

        FieldRequest fieldRequest =  (FieldRequest)recorderName.get(fieldName.trim().toLowerCase());
        return fieldRequest.getValue();
    }

*/
    /////根据操作顺序和字段名称返回字段值
    public String getFieldValue(int index,String fieldName) {
        Map recorderInt  = (Map)recorderInts.get(index);
        FieldRequest fieldRequest =  (FieldRequest)recorderInt.get(fieldName.trim().toLowerCase());
        if ( fieldRequest != null ) {
             return fieldRequest.getValue();
        } else {
             return null;
        }
   }

   /**
    * 获取整形域值
    * @param index int
    * @param fieldName String
    * @return int
    *
    * linyw add
    */
   public int getFieldIntValue(int index,String fieldName){
        String value = getFieldValue(index, fieldName);
        if(value == null || value.trim().length() == 0){
             return 0;
        }else{
             return Integer.parseInt(value);
        }
   }



   ///取出字段名称数组
   public Object[] getFieldNameArr(int index){
         Map recorderInt  = (Map)recorderInts.get(index);
         return recorderInt.keySet().toArray();

   }


   /////根据操作顺序和字段名称返回字段旧值
   public String getFieldOldValue(int index,String fieldName) {
       Map recorderInt  = (Map)recorderInts.get(index);
       FieldRequest fieldRequest =  (FieldRequest)recorderInt.get(fieldName.trim().toLowerCase());
       if ( fieldRequest != null ) {
            return fieldRequest.getoldValue();
       } else {
            return null;
       }
  }

  /////根据操作顺序和字段名称返回字段表示
  public String getFieldLable(int index,String fieldName) {
      Map recorderInt  = (Map)recorderInts.get(index);
      FieldRequest fieldRequest =  (FieldRequest)recorderInt.get(fieldName.trim().toLowerCase());
      if ( fieldRequest != null ) {
           return fieldRequest.getfieldLable();
      } else {
           return null;
      }
 }


   /////根据操作顺序和字段名称返回字段类型
   public String getFieldType(int index,String fieldName)
  {

       Map recorderInt  = (Map)recorderInts.get(index);
       FieldRequest fieldRequest =  (FieldRequest)recorderInt.get(fieldName.trim().toLowerCase());
       return fieldRequest.geType();
  }


    ///返回操作类型总数
    public int getRecorderCount(){
        return recorderInts.size();
    }

    ///添加操作类型总数
   public void setRecorderCount(int recorderCount){
       this.recorderCount = recorderCount;
   }

}
