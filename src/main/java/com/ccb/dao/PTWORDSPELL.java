package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTWORDSPELL extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTWORDSPELL().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTWORDSPELL().findAndLockByWhere(sSqlWhere);      }       public static PTWORDSPELL findFirst(String sSqlWhere) {           return (PTWORDSPELL)new PTWORDSPELL().findFirstByWhere(sSqlWhere);      }       public static PTWORDSPELL findFirstAndLock(String sSqlWhere) {           return (PTWORDSPELL)new PTWORDSPELL().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTWORDSPELL().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTWORDSPELL bean = new PTWORDSPELL();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTWORDSPELL bean = new PTWORDSPELL();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTWORDSPELL findFirst(String sSqlWhere,boolean isAutoRelease) {           PTWORDSPELL bean = new PTWORDSPELL();           bean.setAutoRelease(isAutoRelease);           return (PTWORDSPELL)bean.findFirstByWhere(sSqlWhere);      }       public static PTWORDSPELL findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTWORDSPELL bean = new PTWORDSPELL();           bean.setAutoRelease(isAutoRelease);           return (PTWORDSPELL)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTWORDSPELL bean = new PTWORDSPELL();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTWORDSPELL().findByWhereByRow(sSqlWhere,row);      } String chword;
String chwordspell;
public static final String TABLENAME ="ptwordspell";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTWORDSPELL abb = new PTWORDSPELL();
abb.chword=rs.getString("chword");abb.setKeyValue("CHWORD",""+abb.getChword());
abb.chwordspell=rs.getString("chwordspell");abb.setKeyValue("CHWORDSPELL",""+abb.getChwordspell());
list.add(abb);
abb.operate_mode = "edit";
}public String getChword() { if ( this.chword == null ) return ""; return this.chword;}
public String getChwordspell() { if ( this.chwordspell == null ) return ""; return this.chwordspell;}
public void setChword(String chword) { sqlMaker.setField("chword",chword,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getChword().equals(chword)) cf.add("chword",this.chword,chword); } this.chword=chword;}
public void setChwordspell(String chwordspell) { sqlMaker.setField("chwordspell",chwordspell,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getChwordspell().equals(chwordspell)) cf.add("chwordspell",this.chwordspell,chwordspell); } this.chwordspell=chwordspell;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"chword") !=null ) {this.setChword(actionRequest.getFieldValue(i,"chword"));}
if ( actionRequest.getFieldValue(i,"chwordspell") !=null ) {this.setChwordspell(actionRequest.getFieldValue(i,"chwordspell"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTWORDSPELL obj = (PTWORDSPELL)super.clone();obj.setChword(obj.chword);
obj.setChwordspell(obj.chwordspell);
return obj;}}