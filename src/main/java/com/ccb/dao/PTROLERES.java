package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTROLERES extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTROLERES().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTROLERES().findAndLockByWhere(sSqlWhere);      }       public static PTROLERES findFirst(String sSqlWhere) {           return (PTROLERES)new PTROLERES().findFirstByWhere(sSqlWhere);      }       public static PTROLERES findFirstAndLock(String sSqlWhere) {           return (PTROLERES)new PTROLERES().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTROLERES().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTROLERES bean = new PTROLERES();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTROLERES bean = new PTROLERES();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTROLERES findFirst(String sSqlWhere,boolean isAutoRelease) {           PTROLERES bean = new PTROLERES();           bean.setAutoRelease(isAutoRelease);           return (PTROLERES)bean.findFirstByWhere(sSqlWhere);      }       public static PTROLERES findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTROLERES bean = new PTROLERES();           bean.setAutoRelease(isAutoRelease);           return (PTROLERES)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTROLERES bean = new PTROLERES();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTROLERES().findByWhereByRow(sSqlWhere,row);      } String resid;
String roleid;
public static final String TABLENAME ="ptroleres";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTROLERES abb = new PTROLERES();
abb.resid=rs.getString("resid");abb.setKeyValue("RESID",""+abb.getResid());
abb.roleid=rs.getString("roleid");abb.setKeyValue("ROLEID",""+abb.getRoleid());
list.add(abb);
abb.operate_mode = "edit";
}public String getResid() { if ( this.resid == null ) return ""; return this.resid;}
public String getRoleid() { if ( this.roleid == null ) return ""; return this.roleid;}
public void setResid(String resid) { sqlMaker.setField("resid",resid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getResid().equals(resid)) cf.add("resid",this.resid,resid); } this.resid=resid;}
public void setRoleid(String roleid) { sqlMaker.setField("roleid",roleid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getRoleid().equals(roleid)) cf.add("roleid",this.roleid,roleid); } this.roleid=roleid;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"resid") !=null ) {this.setResid(actionRequest.getFieldValue(i,"resid"));}
if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTROLERES obj = (PTROLERES)super.clone();obj.setResid(obj.resid);
obj.setRoleid(obj.roleid);
return obj;}}