package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTENUMAIN extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTENUMAIN().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTENUMAIN().findAndLockByWhere(sSqlWhere);      }       public static PTENUMAIN findFirst(String sSqlWhere) {           return (PTENUMAIN)new PTENUMAIN().findFirstByWhere(sSqlWhere);      }       public static PTENUMAIN findFirstAndLock(String sSqlWhere) {           return (PTENUMAIN)new PTENUMAIN().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTENUMAIN().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTENUMAIN bean = new PTENUMAIN();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTENUMAIN bean = new PTENUMAIN();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTENUMAIN findFirst(String sSqlWhere,boolean isAutoRelease) {           PTENUMAIN bean = new PTENUMAIN();           bean.setAutoRelease(isAutoRelease);           return (PTENUMAIN)bean.findFirstByWhere(sSqlWhere);      }       public static PTENUMAIN findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTENUMAIN bean = new PTENUMAIN();           bean.setAutoRelease(isAutoRelease);           return (PTENUMAIN)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTENUMAIN bean = new PTENUMAIN();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTENUMAIN().findByWhereByRow(sSqlWhere,row);      } String enutype;
String enuname;
String valuetype;
String enudesc;
public static final String TABLENAME ="ptenumain";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTENUMAIN abb = new PTENUMAIN();
abb.enutype=rs.getString("enutype");abb.setKeyValue("ENUTYPE",""+abb.getEnutype());
abb.enuname=rs.getString("enuname");abb.setKeyValue("ENUNAME",""+abb.getEnuname());
abb.valuetype=rs.getString("valuetype");abb.setKeyValue("VALUETYPE",""+abb.getValuetype());
abb.enudesc=rs.getString("enudesc");abb.setKeyValue("ENUDESC",""+abb.getEnudesc());
list.add(abb);
abb.operate_mode = "edit";
}public String getEnutype() { if ( this.enutype == null ) return ""; return this.enutype;}
public String getEnuname() { if ( this.enuname == null ) return ""; return this.enuname;}
public String getValuetype() { if ( this.valuetype == null ) return ""; return this.valuetype;}
public String getEnudesc() { if ( this.enudesc == null ) return ""; return this.enudesc;}
public void setEnutype(String enutype) { sqlMaker.setField("enutype",enutype,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEnutype().equals(enutype)) cf.add("enutype",this.enutype,enutype); } this.enutype=enutype;}
public void setEnuname(String enuname) { sqlMaker.setField("enuname",enuname,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEnuname().equals(enuname)) cf.add("enuname",this.enuname,enuname); } this.enuname=enuname;}
public void setValuetype(String valuetype) { sqlMaker.setField("valuetype",valuetype,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getValuetype().equals(valuetype)) cf.add("valuetype",this.valuetype,valuetype); } this.valuetype=valuetype;}
public void setEnudesc(String enudesc) { sqlMaker.setField("enudesc",enudesc,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEnudesc().equals(enudesc)) cf.add("enudesc",this.enudesc,enudesc); } this.enudesc=enudesc;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"enutype") !=null ) {this.setEnutype(actionRequest.getFieldValue(i,"enutype"));}
if ( actionRequest.getFieldValue(i,"enuname") !=null ) {this.setEnuname(actionRequest.getFieldValue(i,"enuname"));}
if ( actionRequest.getFieldValue(i,"valuetype") !=null ) {this.setValuetype(actionRequest.getFieldValue(i,"valuetype"));}
if ( actionRequest.getFieldValue(i,"enudesc") !=null ) {this.setEnudesc(actionRequest.getFieldValue(i,"enudesc"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTENUMAIN obj = (PTENUMAIN)super.clone();obj.setEnutype(obj.enutype);
obj.setEnuname(obj.enuname);
obj.setValuetype(obj.valuetype);
obj.setEnudesc(obj.enudesc);
return obj;}}