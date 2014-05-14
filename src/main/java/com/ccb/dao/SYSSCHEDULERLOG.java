package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class SYSSCHEDULERLOG extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new SYSSCHEDULERLOG().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new SYSSCHEDULERLOG().findAndLockByWhere(sSqlWhere);      }       public static SYSSCHEDULERLOG findFirst(String sSqlWhere) {           return (SYSSCHEDULERLOG)new SYSSCHEDULERLOG().findFirstByWhere(sSqlWhere);      }       public static SYSSCHEDULERLOG findFirstAndLock(String sSqlWhere) {           return (SYSSCHEDULERLOG)new SYSSCHEDULERLOG().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new SYSSCHEDULERLOG().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULERLOG bean = new SYSSCHEDULERLOG();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULERLOG bean = new SYSSCHEDULERLOG();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static SYSSCHEDULERLOG findFirst(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULERLOG bean = new SYSSCHEDULERLOG();           bean.setAutoRelease(isAutoRelease);           return (SYSSCHEDULERLOG)bean.findFirstByWhere(sSqlWhere);      }       public static SYSSCHEDULERLOG findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULERLOG bean = new SYSSCHEDULERLOG();           bean.setAutoRelease(isAutoRelease);           return (SYSSCHEDULERLOG)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULERLOG bean = new SYSSCHEDULERLOG();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new SYSSCHEDULERLOG().findByWhereByRow(sSqlWhere,row);      } int jobid;
String jobname;
String time;
String info;
public static final String TABLENAME ="sys_scheduler_log";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
SYSSCHEDULERLOG abb = new SYSSCHEDULERLOG();
abb.jobid=rs.getInt("jobid");abb.setKeyValue("JOBID",""+abb.getJobid());
abb.jobname=rs.getString("jobname");abb.setKeyValue("JOBNAME",""+abb.getJobname());
abb.time=rs.getTimeString("time");abb.setKeyValue("TIME",""+abb.getTime());
abb.info=rs.getString("info");abb.setKeyValue("INFO",""+abb.getInfo());
list.add(abb);
abb.operate_mode = "edit";
}public int getJobid() { return this.jobid;}
public String getJobname() { if ( this.jobname == null ) return ""; return this.jobname;}
public String getTime() {  if ( this.time == null ) { return ""; } else { return this.time.trim().split(" ")[0];} }public String getTimeTime() {  if ( this.time == null ) return ""; return this.time.split("\\.")[0];}
public String getInfo() { if ( this.info == null ) return ""; return this.info;}
public void setJobid(int jobid) { sqlMaker.setField("jobid",""+jobid,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getJobid()!=jobid) cf.add("jobid",this.jobid+"",jobid+""); } this.jobid=jobid;}
public void setJobname(String jobname) { sqlMaker.setField("jobname",jobname,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getJobname().equals(jobname)) cf.add("jobname",this.jobname,jobname); } this.jobname=jobname;}
public void setTime(String time) { sqlMaker.setField("time",time,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getTime().equals(time)) cf.add("time",this.time,time); } this.time=time;}
public void setInfo(String info) { sqlMaker.setField("info",info,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getInfo().equals(info)) cf.add("info",this.info,info); } this.info=info;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"jobid") !=null && actionRequest.getFieldValue(i,"jobid").trim().length() > 0 ) {this.setJobid(Integer.parseInt(actionRequest.getFieldValue(i,"jobid")));}
if ( actionRequest.getFieldValue(i,"jobname") !=null ) {this.setJobname(actionRequest.getFieldValue(i,"jobname"));}
if ( actionRequest.getFieldValue(i,"time") !=null ) {this.setTime(actionRequest.getFieldValue(i,"time"));}
if ( actionRequest.getFieldValue(i,"info") !=null ) {this.setInfo(actionRequest.getFieldValue(i,"info"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { SYSSCHEDULERLOG obj = (SYSSCHEDULERLOG)super.clone();obj.setJobid(obj.jobid);
obj.setJobname(obj.jobname);
obj.setTime(obj.time);
obj.setInfo(obj.info);
return obj;}}