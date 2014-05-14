package pub.platform.utils;
import java.util.*;

import pub.platform.db.*;
import pub.platform.form.config.EnumerationBean;
import pub.platform.form.config.EnumerationType;
import pub.platform.form.util.event.LogAssistor;
import pub.platform.utils.Basic;

public class SQLResponse {
    private Vector  VectMessage;   ///信息
    private Vector  VectResult;   ///结果信息  正确返回true;错误返回false
    private Vector  VectType;     ///结果信息  查询返回1;其它返回0
    private Vector  VectRecordset ;//结果数据信息
    private Vector  VectFieldType ;//字段类型每个类型用；隔开
    private Vector  VectFieldName ;//字段名称每个名称用；隔开
    private  Vector VectEnumType  ;//字段类型每个名称用；隔开
    private Vector  vectFieldValue;//字段值每个值用;隔开

    public static final int MESSAGE_TYPE     = 0;
    public static final int DATA_TYPE        = 1;
    public static final int HTML_TYPE        = 2;
    public static final int SELF_XML_TYPE    = 3;
    public static final int VALUE_TYPE       = 4;

    public static final boolean SUCCESS  = true;
    public static final boolean FAIL     = false;

    public SQLResponse() {

        VectMessage    = new Vector();   ///信息
        VectResult     = new Vector();   ///结果信息  正确返回true;错误返回false
        VectType       = new Vector();   ///结果信息  查询返回1;其它返回0
        VectRecordset  = new Vector();   //结果数据信息
        VectFieldType  = new Vector();   //字段类型每个类型用；隔开
        VectFieldName  = new Vector();
        VectEnumType   = new Vector();
        vectFieldValue = new Vector();

    }
    ///////添加字段值
    public void setFieldValue(String FieldValue) {
         vectFieldValue.add(FieldValue);
     }

    public void setEnumType(String EnumType) {
         VectEnumType.add(EnumType);
     }

    public void setMessage(String message){
        VectMessage.add(message);
    }

   public void setResult(boolean Result){
         VectResult.add(new Boolean(Result));

    }

    public void setType(int type){
        VectType.add(new Integer(type));
    }

     public void setRecordset(RecordSet recordSet) {
        VectRecordset.add(recordSet);
    }

    public void setFieldType(String fieldType) {
        VectFieldType.add(fieldType);
    }
    public void setFieldName(String fieldName) {
       VectFieldName.add(fieldName);
   }
   public void addField(String name,String fieldType,String value) {
        VectFieldName.add(name);
        VectFieldType.add(fieldType);
        vectFieldValue.add(value);
   }
   public void addField(String name,String value) {
        VectFieldName.add(name);
        vectFieldValue.add(value);
   }


    public String getXmlStr(){
        StringBuffer  StrBuf = new StringBuffer();

        try{
             StrBuf.append("<root>");

            for (int i=0; i<VectResult.size();i++){



                Integer type = (Integer)VectType.get(i);
                Boolean result =(Boolean)VectResult.get(i) ;

                if (type.intValue()==1){
                     if (result.booleanValue()) {
                          if ((VectRecordset.get(i)!=null)&&(VectFieldType.get(i)!=null)&&(VectFieldName.get(i)!=null)){

                              StrBuf.append("<action type=\"1\" result=\"true\">");

                               RecordSet recordSet = (RecordSet) VectRecordset.get(i);
                               String[] fieldTypeArr = ((String) VectFieldType.get(i)).split(";");
                                String[] fieldNameArr = ((String) VectFieldName.get(i)).split(";");
                               String[] enumTypeArr = ((String) VectEnumType.get(i)).split(";");

                               recordSet.beforeFirst(); //多次取出响应结果
                               
                               while (recordSet.next()){
                                    StrBuf.append("<record> ");

                                    for (int j=0; j<fieldNameArr.length; j++){
                                        StrBuf.append("<field ");
                                        StrBuf.append(" name =\"" + fieldNameArr[j] + "\"");
                                        StrBuf.append(" type =\"" + fieldTypeArr[j] + "\"");

                                        if (fieldTypeArr[j].toLowerCase().equals("datetime"))
                                             StrBuf.append("  value =\"" + Basic.encode(recordSet.getCalendar(j).toString()) + "\"");

                                        else  if (fieldTypeArr[j].toLowerCase().equals("dropdown")){
                                              String enumStr ="";
                                             if ( (!enumTypeArr[j].equals("-1")) && (!enumTypeArr[j].equals("0"))) {

                                                   String fieldValue = recordSet.getString(j);

                                                   if (fieldValue !=null){
                                                       EnumerationBean enumBean = EnumerationType.getEnu(enumTypeArr[j]);
                                                       enumStr = ((String) enumBean.getValue(fieldValue.trim())).split(";")[0];
                                                  }

                                                  if (enumStr==null)
                                                       enumStr ="";
                                             }
                                              StrBuf.append(" attr =\"" +Basic.encode(recordSet.getString(j)) + "\"");
                                              StrBuf.append("  value =\"" + Basic.encode(enumStr) + "\"");
                                        } else
                                            StrBuf.append("  value =\"" + Basic.encode(recordSet.getString(j)) + "\"");


                                         StrBuf.append(" />");

                                      }
                                     StrBuf.append("</record>");

                                 }
                                StrBuf.append("</action>");

                             }else{
                                StrBuf.append("<action type=\"1\" result=\"false\">"+Basic.encode("VectRecordset、VectFieldType、VectFieldName出现空值")+"</action>");
                             }
                       }else{
                           StrBuf.append("<action type=\"1\" result=\"false\">"+Basic.encode((String)VectMessage.get(i))+"</action>");
                        }
                    } else if ( type.intValue() == HTML_TYPE ) {
                        StrBuf.append("<action type=\""+type.intValue()+"\" result=\""+ result.toString()+"\">"+(String)VectMessage.get(i)+"</action>");
                   } else if ( type.intValue() == SELF_XML_TYPE ) {
                        StrBuf.append("<action type=\""+type.intValue()+"\" result=\""+ result.toString()+"\">"+(String)VectMessage.get(i)+"</action>");
                   }else if ( type.intValue() == VALUE_TYPE ) {
                       StrBuf.append(getValueType(result,i));
                  }else {
                        StrBuf.append("<action type=\""+type.intValue()+"\" result=\""+ result.toString()+"\">"+Basic.encode((String)VectMessage.get(i))+"</action>");
                   }
               }
	       StrBuf.append("</root>");
	       //System.out.println(StrBuf.toString());
        } catch ( Exception e ) {
               e.printStackTrace();
              return "<root><action type=\"0\" result=\"false\">"+Basic.encode("构造XML出错")+"</action></root>";
         }


        if (LogAssistor.log()) {
              return StrBuf.toString() ;
        } else {
              return "" ;
        }
    }



    private String getValueType(Boolean result, int i){
          StringBuffer  StrBuf = new StringBuffer();


          if (result.booleanValue()) {
               if ((vectFieldValue.get(i)!=null)&&(VectFieldType.get(i)!=null)&&(VectFieldName.get(i)!=null)&&(VectEnumType.get(i)!=null)){

                    StrBuf.append("<action type=\"1\" result=\"true\">");
                    StrBuf.append("<record> ");

                    String[] fieldTypeArr = ((String) VectFieldType.get(i)).split(";");
                    String[] fieldNameArr = ((String) VectFieldName.get(i)).split(";");
                    String[] enumTypeArr = ((String) VectEnumType.get(i)).split(";");
                    String[] fieldValueArr = ((String) vectFieldValue.get(i)).split(";",fieldNameArr.length);


                     for (int j=0; j<fieldNameArr.length; j++){
                         StrBuf.append("<field ");
                         StrBuf.append(" name =\"" + fieldNameArr[j] + "\"");
                         StrBuf.append(" type =\"" + fieldTypeArr[j] + "\"");


                         if (fieldTypeArr[j].toLowerCase().equals("dropdown")){
                              String enumStr ="";
                              if ( (!enumTypeArr[j].equals("-1")) && (!enumTypeArr[j].equals("0"))) {

                                    String fieldValue = fieldValueArr[j];

                                    if (fieldValue !=null){
                                             EnumerationBean enumBean = EnumerationType.getEnu(enumTypeArr[j]);
                                             enumStr = ((String) enumBean.getValue(fieldValue.trim())).split(";")[0];

                                   }

                                   if (enumStr==null)
                                   enumStr ="";
                              }
                              StrBuf.append(" attr =\"" +Basic.encode(fieldValueArr[j]) + "\"");
                              StrBuf.append("  value =\"" + Basic.encode(enumStr) + "\"");
                         } else
                              StrBuf.append("  value =\"" + Basic.encode(fieldValueArr[j]) + "\"");


                               StrBuf.append(" />");

                         }
                           StrBuf.append("</record>");

                       }
                      StrBuf.append("</action>");
          }
          return StrBuf.toString();
    }
}
