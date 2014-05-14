package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTOPERROLE extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTOPERROLE().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTOPERROLE().findAndLockByWhere(sSqlWhere);      }       public static PTOPERROLE findFirst(String sSqlWhere) {           return (PTOPERROLE)new PTOPERROLE().findFirstByWhere(sSqlWhere);      }       public static PTOPERROLE findFirstAndLock(String sSqlWhere) {           return (PTOPERROLE)new PTOPERROLE().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTOPERROLE().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTOPERROLE bean = new PTOPERROLE();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTOPERROLE bean = new PTOPERROLE();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTOPERROLE findFirst(String sSqlWhere,boolean isAutoRelease) {           PTOPERROLE bean = new PTOPERROLE();           bean.setAutoRelease(isAutoRelease);           return (PTOPERROLE)bean.findFirstByWhere(sSqlWhere);      }       public static PTOPERROLE findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTOPERROLE bean = new PTOPERROLE();           bean.setAutoRelease(isAutoRelease);           return (PTOPERROLE)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTOPERROLE bean = new PTOPERROLE();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTOPERROLE().findByWhereByRow(sSqlWhere,row);      } String roleid;
String operid;
public static final String TABLENAME ="ptoperrole";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTOPERROLE abb = new PTOPERROLE();
abb.roleid=rs.getString("roleid");abb.setKeyValue("ROLEID",""+abb.getRoleid());
abb.operid=rs.getString("operid");abb.setKeyValue("OPERID",""+abb.getOperid());
list.add(abb);
abb.operate_mode = "edit";
}public String getRoleid() { if ( this.roleid == null ) return ""; return this.roleid;}
public String getOperid() { if ( this.operid == null ) return ""; return this.operid;}
public void setRoleid(String roleid) { sqlMaker.setField("roleid",roleid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getRoleid().equals(roleid)) cf.add("roleid",this.roleid,roleid); } this.roleid=roleid;}
public void setOperid(String operid) { sqlMaker.setField("operid",operid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOperid().equals(operid)) cf.add("operid",this.operid,operid); } this.operid=operid;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));}
if ( actionRequest.getFieldValue(i,"operid") !=null ) {this.setOperid(actionRequest.getFieldValue(i,"operid"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTOPERROLE obj = (PTOPERROLE)super.clone();obj.setRoleid(obj.roleid);
obj.setOperid(obj.operid);
return obj;}}