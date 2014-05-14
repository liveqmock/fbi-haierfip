package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTROLE extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTROLE().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTROLE().findAndLockByWhere(sSqlWhere);      }       public static PTROLE findFirst(String sSqlWhere) {           return (PTROLE)new PTROLE().findFirstByWhere(sSqlWhere);      }       public static PTROLE findFirstAndLock(String sSqlWhere) {           return (PTROLE)new PTROLE().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTROLE().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTROLE bean = new PTROLE();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTROLE bean = new PTROLE();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTROLE findFirst(String sSqlWhere,boolean isAutoRelease) {           PTROLE bean = new PTROLE();           bean.setAutoRelease(isAutoRelease);           return (PTROLE)bean.findFirstByWhere(sSqlWhere);      }       public static PTROLE findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTROLE bean = new PTROLE();           bean.setAutoRelease(isAutoRelease);           return (PTROLE)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTROLE bean = new PTROLE();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTROLE().findByWhereByRow(sSqlWhere,row);      } String roleid;
String roledesc;
String status;
public static final String TABLENAME ="ptrole";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTROLE abb = new PTROLE();
abb.roleid=rs.getString("roleid");abb.setKeyValue("ROLEID",""+abb.getRoleid());
abb.roledesc=rs.getString("roledesc");abb.setKeyValue("ROLEDESC",""+abb.getRoledesc());
abb.status=rs.getString("status");abb.setKeyValue("STATUS",""+abb.getStatus());
list.add(abb);
abb.operate_mode = "edit";
}public String getRoleid() { if ( this.roleid == null ) return ""; return this.roleid;}
public String getRoledesc() { if ( this.roledesc == null ) return ""; return this.roledesc;}
public String getStatus() { if ( this.status == null ) return ""; return this.status;}
public void setRoleid(String roleid) { sqlMaker.setField("roleid",roleid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getRoleid().equals(roleid)) cf.add("roleid",this.roleid,roleid); } this.roleid=roleid;}
public void setRoledesc(String roledesc) { sqlMaker.setField("roledesc",roledesc,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getRoledesc().equals(roledesc)) cf.add("roledesc",this.roledesc,roledesc); } this.roledesc=roledesc;}
public void setStatus(String status) { sqlMaker.setField("status",status,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getStatus().equals(status)) cf.add("status",this.status,status); } this.status=status;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));}
if ( actionRequest.getFieldValue(i,"roledesc") !=null ) {this.setRoledesc(actionRequest.getFieldValue(i,"roledesc"));}
if ( actionRequest.getFieldValue(i,"status") !=null ) {this.setStatus(actionRequest.getFieldValue(i,"status"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTROLE obj = (PTROLE)super.clone();obj.setRoleid(obj.roleid);
obj.setRoledesc(obj.roledesc);
obj.setStatus(obj.status);
return obj;}}