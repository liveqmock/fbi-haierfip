package pub.platform.utils;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

import pub.platform.utils.*;

import java.io.*;
import java.util.*;

public class SQLRequest {
     private Map ActionRequestNames; /////按名称存放所有行为的信息
     private Vector ActionRequestInts; /////存放所有行为的信息
     private int ActionCount; ///操作行为个数
     private Vector ActionNames; /////存放所有行为的名称

     public  String ReqXML; /////从客户端获取的XML数据

     public SQLRequest() {
          ActionRequestNames = new HashMap();
          ActionRequestInts = new Vector();
          ActionNames = new Vector();

     }

     ////初始化信息 参数 XML字符串

     public boolean setXMLStr(String XMLStr) {
          boolean retbool = false;

          ReqXML = XMLStr;

          try {
               Reader reader = new StringReader(XMLStr);
               SAXBuilder saxbd = new SAXBuilder();
               Document doc = saxbd.build(reader);

               Element rootNode = doc.getRootElement();

               List ActionChildList = rootNode.getChildren();

               ////取出action 的个数
               ActionCount = ActionChildList.size();

               for(int i = 0; i < ActionChildList.size(); i++) {

                    ActionRequest actionRequest = new ActionRequest();

                    Element ActionCildNode = (Element)ActionChildList.get(i);

                    List recorderChildList = ActionCildNode.getChildren();

                    for(int j = 0; j < recorderChildList.size(); j++) {
                         Element recorderChildNode = (Element)recorderChildList.get(j);

                         List fieldChildList = recorderChildNode.getChildren();

                         Map recorderTamp = new HashMap();

                         for(int k = 0; k < fieldChildList.size(); k++) {
                              Element fieldChildNode = (Element)fieldChildList.get(k);

                              FieldRequest fieldRequest = new FieldRequest();
                              fieldRequest.setName(fieldChildNode.getAttributeValue("name").trim().toLowerCase());
                              fieldRequest.setType(fieldChildNode.getAttributeValue("type").trim().toLowerCase());

                              if(fieldChildNode.getAttribute("oldValue") != null)
                                   fieldRequest.setoldValue(Basic.decode(fieldChildNode.getAttributeValue("oldValue")).trim());
                              else
                                   fieldRequest.setoldValue("");





                              if(fieldChildNode.getAttribute("fieldLable") != null){

                                   fieldRequest.setfieldLable(Basic.decode(fieldChildNode.getAttributeValue("fieldLable")));
                              }
                             else
                                  fieldRequest.setfieldLable("");



                              fieldRequest.setValue(Basic.decode(fieldChildNode.getAttributeValue("value")).trim());
                              //填充数据
                              recorderTamp.put(fieldChildNode.getAttributeValue("name").trim().toLowerCase(), fieldRequest);
                         }
                         actionRequest.setRecorderInt(recorderTamp);

                         actionRequest.setTypeS(recorderChildNode.getAttributeValue("type").trim().toLowerCase());

                         if(j == 0) {
                              actionRequest.setRecorder(recorderTamp);
                              actionRequest.setType(recorderChildNode.getAttributeValue("type").trim().toLowerCase());
                         }

                    }
                    actionRequest.setRecorderCount(recorderChildList.size());
                    ActionRequestNames.put(ActionCildNode.getAttributeValue("actionname").trim().toLowerCase(), actionRequest);
                    ActionRequestInts.add(actionRequest);
                    ActionNames.add(ActionCildNode.getAttributeValue("actionname").trim().toLowerCase());


                    //////////////添加方法名称
                    if (ActionCildNode.getAttribute("methodname") != null)
                        actionRequest.setmethodName(ActionCildNode.getAttributeValue("methodname").trim());

                    else
                         actionRequest.setmethodName("");


               }
               reader.close();
               retbool = true;
          } catch(Exception ex) {
               ex.printStackTrace();

               retbool = false;
          }
          return retbool;
     }

     ///取出行为的个数
     public int getActionCount() {
          return ActionCount;
     }

     ///取出行为的数据
     public ActionRequest getActionRequest(String actionName) {
          return(ActionRequest)ActionRequestNames.get(actionName.toLowerCase().trim());
     }

     ///取出行为的数据
     public ActionRequest getActionRequest(int index) {
          return(ActionRequest)ActionRequestInts.get(index);
     }

     ///取出行为的名称
     public String getActionName(int index) {
          return(String)ActionNames.get(index);
     }

}
