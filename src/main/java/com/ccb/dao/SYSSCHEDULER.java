package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class SYSSCHEDULER extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new SYSSCHEDULER().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new SYSSCHEDULER().findAndLockByWhere(sSqlWhere);      }       public static SYSSCHEDULER findFirst(String sSqlWhere) {           return (SYSSCHEDULER)new SYSSCHEDULER().findFirstByWhere(sSqlWhere);      }       public static SYSSCHEDULER findFirstAndLock(String sSqlWhere) {           return (SYSSCHEDULER)new SYSSCHEDULER().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new SYSSCHEDULER().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULER bean = new SYSSCHEDULER();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULER bean = new SYSSCHEDULER();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static SYSSCHEDULER findFirst(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULER bean = new SYSSCHEDULER();           bean.setAutoRelease(isAutoRelease);           return (SYSSCHEDULER)bean.findFirstByWhere(sSqlWhere);      }       public static SYSSCHEDULER findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULER bean = new SYSSCHEDULER();           bean.setAutoRelease(isAutoRelease);           return (SYSSCHEDULER)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           SYSSCHEDULER bean = new SYSSCHEDULER();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new SYSSCHEDULER().findByWhereByRow(sSqlWhere,row);      } int jobid;
String jobname;
String cronexpression;
int successcount;
int failcount;
String lastexecutetime;
String status;
String mailonfail;
String information;
String jobaction;
String jobmethod;
String jobparam;
public static final String TABLENAME ="sys_scheduler";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
SYSSCHEDULER abb = new SYSSCHEDULER();
abb.jobid=rs.getInt("jobid");abb.setKeyValue("JOBID",""+abb.getJobid());
abb.jobname=rs.getString("jobname");abb.setKeyValue("JOBNAME",""+abb.getJobname());
abb.cronexpression=rs.getString("cronexpression");abb.setKeyValue("CRONEXPRESSION",""+abb.getCronexpression());
abb.successcount=rs.getInt("successcount");abb.setKeyValue("SUCCESSCOUNT",""+abb.getSuccesscount());
abb.failcount=rs.getInt("failcount");abb.setKeyValue("FAILCOUNT",""+abb.getFailcount());
abb.lastexecutetime=rs.getTimeString("lastexecutetime");abb.setKeyValue("LASTEXECUTETIME",""+abb.getLastexecutetime());
abb.status=rs.getString("status");abb.setKeyValue("STATUS",""+abb.getStatus());
abb.mailonfail=rs.getString("mailonfail");abb.setKeyValue("MAILONFAIL",""+abb.getMailonfail());
abb.information=rs.getString("information");abb.setKeyValue("INFORMATION",""+abb.getInformation());
abb.jobaction=rs.getString("jobaction");abb.setKeyValue("JOBACTION",""+abb.getJobaction());
abb.jobmethod=rs.getString("jobmethod");abb.setKeyValue("JOBMETHOD",""+abb.getJobmethod());
abb.jobparam=rs.getString("jobparam");abb.setKeyValue("JOBPARAM",""+abb.getJobparam());
list.add(abb);
abb.operate_mode = "edit";
}public int getJobid() { return this.jobid;}
public String getJobname() { if ( this.jobname == null ) return ""; return this.jobname;}
public String getCronexpression() { if ( this.cronexpression == null ) return ""; return this.cronexpression;}
public int getSuccesscount() { return this.successcount;}
public int getFailcount() { return this.failcount;}
public String getLastexecutetime() {  if ( this.lastexecutetime == null ) { return ""; } else { return this.lastexecutetime.trim().split(" ")[0];} }public String getLastexecutetimeTime() {  if ( this.lastexecutetime == null ) return ""; return this.lastexecutetime.split("\\.")[0];}
public String getStatus() { if ( this.status == null ) return ""; return this.status;}
public String getMailonfail() { if ( this.mailonfail == null ) return ""; return this.mailonfail;}
public String getInformation() { if ( this.information == null ) return ""; return this.information;}
public String getJobaction() { if ( this.jobaction == null ) return ""; return this.jobaction;}
public String getJobmethod() { if ( this.jobmethod == null ) return ""; return this.jobmethod;}
public String getJobparam() { if ( this.jobparam == null ) return ""; return this.jobparam;}
public void setJobid(int jobid) { sqlMaker.setField("jobid",""+jobid,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getJobid()!=jobid) cf.add("jobid",this.jobid+"",jobid+""); } this.jobid=jobid;}
public void setJobname(String jobname) { sqlMaker.setField("jobname",jobname,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getJobname().equals(jobname)) cf.add("jobname",this.jobname,jobname); } this.jobname=jobname;}
public void setCronexpression(String cronexpression) { sqlMaker.setField("cronexpression",cronexpression,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getCronexpression().equals(cronexpression)) cf.add("cronexpression",this.cronexpression,cronexpression); } this.cronexpression=cronexpression;}
public void setSuccesscount(int successcount) { sqlMaker.setField("successcount",""+successcount,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getSuccesscount()!=successcount) cf.add("successcount",this.successcount+"",successcount+""); } this.successcount=successcount;}
public void setFailcount(int failcount) { sqlMaker.setField("failcount",""+failcount,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFailcount()!=failcount) cf.add("failcount",this.failcount+"",failcount+""); } this.failcount=failcount;}
public void setLastexecutetime(String lastexecutetime) { sqlMaker.setField("lastexecutetime",lastexecutetime,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getLastexecutetime().equals(lastexecutetime)) cf.add("lastexecutetime",this.lastexecutetime,lastexecutetime); } this.lastexecutetime=lastexecutetime;}
public void setStatus(String status) { sqlMaker.setField("status",status,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getStatus().equals(status)) cf.add("status",this.status,status); } this.status=status;}
public void setMailonfail(String mailonfail) { sqlMaker.setField("mailonfail",mailonfail,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMailonfail().equals(mailonfail)) cf.add("mailonfail",this.mailonfail,mailonfail); } this.mailonfail=mailonfail;}
public void setInformation(String information) { sqlMaker.setField("information",information,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getInformation().equals(information)) cf.add("information",this.information,information); } this.information=information;}
public void setJobaction(String jobaction) { sqlMaker.setField("jobaction",jobaction,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getJobaction().equals(jobaction)) cf.add("jobaction",this.jobaction,jobaction); } this.jobaction=jobaction;}
public void setJobmethod(String jobmethod) { sqlMaker.setField("jobmethod",jobmethod,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getJobmethod().equals(jobmethod)) cf.add("jobmethod",this.jobmethod,jobmethod); } this.jobmethod=jobmethod;}
public void setJobparam(String jobparam) { sqlMaker.setField("jobparam",jobparam,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getJobparam().equals(jobparam)) cf.add("jobparam",this.jobparam,jobparam); } this.jobparam=jobparam;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"jobid") !=null && actionRequest.getFieldValue(i,"jobid").trim().length() > 0 ) {this.setJobid(Integer.parseInt(actionRequest.getFieldValue(i,"jobid")));}
if ( actionRequest.getFieldValue(i,"jobname") !=null ) {this.setJobname(actionRequest.getFieldValue(i,"jobname"));}
if ( actionRequest.getFieldValue(i,"cronexpression") !=null ) {this.setCronexpression(actionRequest.getFieldValue(i,"cronexpression"));}
if ( actionRequest.getFieldValue(i,"successcount") !=null && actionRequest.getFieldValue(i,"successcount").trim().length() > 0 ) {this.setSuccesscount(Integer.parseInt(actionRequest.getFieldValue(i,"successcount")));}
if ( actionRequest.getFieldValue(i,"failcount") !=null && actionRequest.getFieldValue(i,"failcount").trim().length() > 0 ) {this.setFailcount(Integer.parseInt(actionRequest.getFieldValue(i,"failcount")));}
if ( actionRequest.getFieldValue(i,"lastexecutetime") !=null ) {this.setLastexecutetime(actionRequest.getFieldValue(i,"lastexecutetime"));}
if ( actionRequest.getFieldValue(i,"status") !=null ) {this.setStatus(actionRequest.getFieldValue(i,"status"));}
if ( actionRequest.getFieldValue(i,"mailonfail") !=null ) {this.setMailonfail(actionRequest.getFieldValue(i,"mailonfail"));}
if ( actionRequest.getFieldValue(i,"information") !=null ) {this.setInformation(actionRequest.getFieldValue(i,"information"));}
if ( actionRequest.getFieldValue(i,"jobaction") !=null ) {this.setJobaction(actionRequest.getFieldValue(i,"jobaction"));}
if ( actionRequest.getFieldValue(i,"jobmethod") !=null ) {this.setJobmethod(actionRequest.getFieldValue(i,"jobmethod"));}
if ( actionRequest.getFieldValue(i,"jobparam") !=null ) {this.setJobparam(actionRequest.getFieldValue(i,"jobparam"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { SYSSCHEDULER obj = (SYSSCHEDULER)super.clone();obj.setJobid(obj.jobid);
obj.setJobname(obj.jobname);
obj.setCronexpression(obj.cronexpression);
obj.setSuccesscount(obj.successcount);
obj.setFailcount(obj.failcount);
obj.setLastexecutetime(obj.lastexecutetime);
obj.setStatus(obj.status);
obj.setMailonfail(obj.mailonfail);
obj.setInformation(obj.information);
obj.setJobaction(obj.jobaction);
obj.setJobmethod(obj.jobmethod);
obj.setJobparam(obj.jobparam);
return obj;}}