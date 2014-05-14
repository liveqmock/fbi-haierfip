package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class SYSLOCK extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new SYSLOCK().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new SYSLOCK().findAndLockByWhere(sSqlWhere);      }       public static SYSLOCK findFirst(String sSqlWhere) {           return (SYSLOCK)new SYSLOCK().findFirstByWhere(sSqlWhere);      }       public static SYSLOCK findFirstAndLock(String sSqlWhere) {           return (SYSLOCK)new SYSLOCK().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new SYSLOCK().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           SYSLOCK bean = new SYSLOCK();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           SYSLOCK bean = new SYSLOCK();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static SYSLOCK findFirst(String sSqlWhere,boolean isAutoRelease) {           SYSLOCK bean = new SYSLOCK();           bean.setAutoRelease(isAutoRelease);           return (SYSLOCK)bean.findFirstByWhere(sSqlWhere);      }       public static SYSLOCK findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           SYSLOCK bean = new SYSLOCK();           bean.setAutoRelease(isAutoRelease);           return (SYSLOCK)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           SYSLOCK bean = new SYSLOCK();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new SYSLOCK().findByWhereByRow(sSqlWhere,row);      } String syslockstatus;
public static final String TABLENAME ="sys_lock";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
SYSLOCK abb = new SYSLOCK();
abb.syslockstatus=rs.getString("syslockstatus");abb.setKeyValue("SYSLOCKSTATUS",""+abb.getSyslockstatus());
list.add(abb);
abb.operate_mode = "edit";
}public String getSyslockstatus() { if ( this.syslockstatus == null ) return ""; return this.syslockstatus;}
public void setSyslockstatus(String syslockstatus) { sqlMaker.setField("syslockstatus",syslockstatus,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getSyslockstatus().equals(syslockstatus)) cf.add("syslockstatus",this.syslockstatus,syslockstatus); } this.syslockstatus=syslockstatus;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"syslockstatus") !=null ) {this.setSyslockstatus(actionRequest.getFieldValue(i,"syslockstatus"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { SYSLOCK obj = (SYSLOCK)super.clone();obj.setSyslockstatus(obj.syslockstatus);
return obj;}}