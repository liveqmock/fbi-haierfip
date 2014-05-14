package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTRESOURCE extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTRESOURCE().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTRESOURCE().findAndLockByWhere(sSqlWhere);      }       public static PTRESOURCE findFirst(String sSqlWhere) {           return (PTRESOURCE)new PTRESOURCE().findFirstByWhere(sSqlWhere);      }       public static PTRESOURCE findFirstAndLock(String sSqlWhere) {           return (PTRESOURCE)new PTRESOURCE().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTRESOURCE().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTRESOURCE bean = new PTRESOURCE();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTRESOURCE bean = new PTRESOURCE();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTRESOURCE findFirst(String sSqlWhere,boolean isAutoRelease) {           PTRESOURCE bean = new PTRESOURCE();           bean.setAutoRelease(isAutoRelease);           return (PTRESOURCE)bean.findFirstByWhere(sSqlWhere);      }       public static PTRESOURCE findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTRESOURCE bean = new PTRESOURCE();           bean.setAutoRelease(isAutoRelease);           return (PTRESOURCE)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTRESOURCE bean = new PTRESOURCE();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTRESOURCE().findByWhereByRow(sSqlWhere,row);      } String resid;
String parentresid;
String resname;
String restype;
String resenabled;
String resdesc;
public static final String TABLENAME ="ptresource";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTRESOURCE abb = new PTRESOURCE();
abb.resid=rs.getString("resid");abb.setKeyValue("RESID",""+abb.getResid());
abb.parentresid=rs.getString("parentresid");abb.setKeyValue("PARENTRESID",""+abb.getParentresid());
abb.resname=rs.getString("resname");abb.setKeyValue("RESNAME",""+abb.getResname());
abb.restype=rs.getString("restype");abb.setKeyValue("RESTYPE",""+abb.getRestype());
abb.resenabled=rs.getString("resenabled");abb.setKeyValue("RESENABLED",""+abb.getResenabled());
abb.resdesc=rs.getString("resdesc");abb.setKeyValue("RESDESC",""+abb.getResdesc());
list.add(abb);
abb.operate_mode = "edit";
}public String getResid() { if ( this.resid == null ) return ""; return this.resid;}
public String getParentresid() { if ( this.parentresid == null ) return ""; return this.parentresid;}
public String getResname() { if ( this.resname == null ) return ""; return this.resname;}
public String getRestype() { if ( this.restype == null ) return ""; return this.restype;}
public String getResenabled() { if ( this.resenabled == null ) return ""; return this.resenabled;}
public String getResdesc() { if ( this.resdesc == null ) return ""; return this.resdesc;}
public void setResid(String resid) { sqlMaker.setField("resid",resid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getResid().equals(resid)) cf.add("resid",this.resid,resid); } this.resid=resid;}
public void setParentresid(String parentresid) { sqlMaker.setField("parentresid",parentresid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getParentresid().equals(parentresid)) cf.add("parentresid",this.parentresid,parentresid); } this.parentresid=parentresid;}
public void setResname(String resname) { sqlMaker.setField("resname",resname,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getResname().equals(resname)) cf.add("resname",this.resname,resname); } this.resname=resname;}
public void setRestype(String restype) { sqlMaker.setField("restype",restype,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getRestype().equals(restype)) cf.add("restype",this.restype,restype); } this.restype=restype;}
public void setResenabled(String resenabled) { sqlMaker.setField("resenabled",resenabled,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getResenabled().equals(resenabled)) cf.add("resenabled",this.resenabled,resenabled); } this.resenabled=resenabled;}
public void setResdesc(String resdesc) { sqlMaker.setField("resdesc",resdesc,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getResdesc().equals(resdesc)) cf.add("resdesc",this.resdesc,resdesc); } this.resdesc=resdesc;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"resid") !=null ) {this.setResid(actionRequest.getFieldValue(i,"resid"));}
if ( actionRequest.getFieldValue(i,"parentresid") !=null ) {this.setParentresid(actionRequest.getFieldValue(i,"parentresid"));}
if ( actionRequest.getFieldValue(i,"resname") !=null ) {this.setResname(actionRequest.getFieldValue(i,"resname"));}
if ( actionRequest.getFieldValue(i,"restype") !=null ) {this.setRestype(actionRequest.getFieldValue(i,"restype"));}
if ( actionRequest.getFieldValue(i,"resenabled") !=null ) {this.setResenabled(actionRequest.getFieldValue(i,"resenabled"));}
if ( actionRequest.getFieldValue(i,"resdesc") !=null ) {this.setResdesc(actionRequest.getFieldValue(i,"resdesc"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTRESOURCE obj = (PTRESOURCE)super.clone();obj.setResid(obj.resid);
obj.setParentresid(obj.parentresid);
obj.setResname(obj.resname);
obj.setRestype(obj.restype);
obj.setResenabled(obj.resenabled);
obj.setResdesc(obj.resdesc);
return obj;}}