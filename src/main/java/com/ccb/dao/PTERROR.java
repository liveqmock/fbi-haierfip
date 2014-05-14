package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTERROR extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTERROR().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTERROR().findAndLockByWhere(sSqlWhere);      }       public static PTERROR findFirst(String sSqlWhere) {           return (PTERROR)new PTERROR().findFirstByWhere(sSqlWhere);      }       public static PTERROR findFirstAndLock(String sSqlWhere) {           return (PTERROR)new PTERROR().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTERROR().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTERROR bean = new PTERROR();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTERROR bean = new PTERROR();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTERROR findFirst(String sSqlWhere,boolean isAutoRelease) {           PTERROR bean = new PTERROR();           bean.setAutoRelease(isAutoRelease);           return (PTERROR)bean.findFirstByWhere(sSqlWhere);      }       public static PTERROR findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTERROR bean = new PTERROR();           bean.setAutoRelease(isAutoRelease);           return (PTERROR)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTERROR bean = new PTERROR();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTERROR().findByWhereByRow(sSqlWhere,row);      } String moudleid;
String errortype;
String errorindex;
String message;
String fill1;
public static final String TABLENAME ="pterror";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTERROR abb = new PTERROR();
abb.moudleid=rs.getString("moudleid");abb.setKeyValue("MOUDLEID",""+abb.getMoudleid());
abb.errortype=rs.getString("errortype");abb.setKeyValue("ERRORTYPE",""+abb.getErrortype());
abb.errorindex=rs.getString("errorindex");abb.setKeyValue("ERRORINDEX",""+abb.getErrorindex());
abb.message=rs.getString("message");abb.setKeyValue("MESSAGE",""+abb.getMessage());
abb.fill1=rs.getString("fill1");abb.setKeyValue("FILL1",""+abb.getFill1());
list.add(abb);
abb.operate_mode = "edit";
}public String getMoudleid() { if ( this.moudleid == null ) return ""; return this.moudleid;}
public String getErrortype() { if ( this.errortype == null ) return ""; return this.errortype;}
public String getErrorindex() { if ( this.errorindex == null ) return ""; return this.errorindex;}
public String getMessage() { if ( this.message == null ) return ""; return this.message;}
public String getFill1() { if ( this.fill1 == null ) return ""; return this.fill1;}
public void setMoudleid(String moudleid) { sqlMaker.setField("moudleid",moudleid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMoudleid().equals(moudleid)) cf.add("moudleid",this.moudleid,moudleid); } this.moudleid=moudleid;}
public void setErrortype(String errortype) { sqlMaker.setField("errortype",errortype,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getErrortype().equals(errortype)) cf.add("errortype",this.errortype,errortype); } this.errortype=errortype;}
public void setErrorindex(String errorindex) { sqlMaker.setField("errorindex",errorindex,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getErrorindex().equals(errorindex)) cf.add("errorindex",this.errorindex,errorindex); } this.errorindex=errorindex;}
public void setMessage(String message) { sqlMaker.setField("message",message,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMessage().equals(message)) cf.add("message",this.message,message); } this.message=message;}
public void setFill1(String fill1) { sqlMaker.setField("fill1",fill1,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFill1().equals(fill1)) cf.add("fill1",this.fill1,fill1); } this.fill1=fill1;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"moudleid") !=null ) {this.setMoudleid(actionRequest.getFieldValue(i,"moudleid"));}
if ( actionRequest.getFieldValue(i,"errortype") !=null ) {this.setErrortype(actionRequest.getFieldValue(i,"errortype"));}
if ( actionRequest.getFieldValue(i,"errorindex") !=null ) {this.setErrorindex(actionRequest.getFieldValue(i,"errorindex"));}
if ( actionRequest.getFieldValue(i,"message") !=null ) {this.setMessage(actionRequest.getFieldValue(i,"message"));}
if ( actionRequest.getFieldValue(i,"fill1") !=null ) {this.setFill1(actionRequest.getFieldValue(i,"fill1"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTERROR obj = (PTERROR)super.clone();obj.setMoudleid(obj.moudleid);
obj.setErrortype(obj.errortype);
obj.setErrorindex(obj.errorindex);
obj.setMessage(obj.message);
obj.setFill1(obj.fill1);
return obj;}}