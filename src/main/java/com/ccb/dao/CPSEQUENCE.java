package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class CPSEQUENCE extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new CPSEQUENCE().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new CPSEQUENCE().findAndLockByWhere(sSqlWhere);      }       public static CPSEQUENCE findFirst(String sSqlWhere) {           return (CPSEQUENCE)new CPSEQUENCE().findFirstByWhere(sSqlWhere);      }       public static CPSEQUENCE findFirstAndLock(String sSqlWhere) {           return (CPSEQUENCE)new CPSEQUENCE().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new CPSEQUENCE().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           CPSEQUENCE bean = new CPSEQUENCE();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           CPSEQUENCE bean = new CPSEQUENCE();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static CPSEQUENCE findFirst(String sSqlWhere,boolean isAutoRelease) {           CPSEQUENCE bean = new CPSEQUENCE();           bean.setAutoRelease(isAutoRelease);           return (CPSEQUENCE)bean.findFirstByWhere(sSqlWhere);      }       public static CPSEQUENCE findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           CPSEQUENCE bean = new CPSEQUENCE();           bean.setAutoRelease(isAutoRelease);           return (CPSEQUENCE)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           CPSEQUENCE bean = new CPSEQUENCE();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new CPSEQUENCE().findByWhereByRow(sSqlWhere,row);      } String seqname;
int minvalue;
int maxvalue;
String cycle;
int stepvalue;
int cache;
String year;
int curvalue;
public static final String TABLENAME ="cp_sequence";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
CPSEQUENCE abb = new CPSEQUENCE();
abb.seqname=rs.getString("seqname");abb.setKeyValue("SEQNAME",""+abb.getSeqname());
abb.minvalue=rs.getInt("minvalue");abb.setKeyValue("MINVALUE",""+abb.getMinvalue());
abb.maxvalue=rs.getInt("maxvalue");abb.setKeyValue("MAXVALUE",""+abb.getMaxvalue());
abb.cycle=rs.getString("cycle");abb.setKeyValue("CYCLE",""+abb.getCycle());
abb.stepvalue=rs.getInt("stepvalue");abb.setKeyValue("STEPVALUE",""+abb.getStepvalue());
abb.cache=rs.getInt("cache");abb.setKeyValue("CACHE",""+abb.getCache());
abb.year=rs.getString("year");abb.setKeyValue("YEAR",""+abb.getYear());
abb.curvalue=rs.getInt("curvalue");abb.setKeyValue("CURVALUE",""+abb.getCurvalue());
list.add(abb);
abb.operate_mode = "edit";
}public String getSeqname() { if ( this.seqname == null ) return ""; return this.seqname;}
public int getMinvalue() { return this.minvalue;}
public int getMaxvalue() { return this.maxvalue;}
public String getCycle() { if ( this.cycle == null ) return ""; return this.cycle;}
public int getStepvalue() { return this.stepvalue;}
public int getCache() { return this.cache;}
public String getYear() { if ( this.year == null ) return ""; return this.year;}
public int getCurvalue() { return this.curvalue;}
public void setSeqname(String seqname) { sqlMaker.setField("seqname",seqname,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getSeqname().equals(seqname)) cf.add("seqname",this.seqname,seqname); } this.seqname=seqname;}
public void setMinvalue(int minvalue) { sqlMaker.setField("minvalue",""+minvalue,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getMinvalue()!=minvalue) cf.add("minvalue",this.minvalue+"",minvalue+""); } this.minvalue=minvalue;}
public void setMaxvalue(int maxvalue) { sqlMaker.setField("maxvalue",""+maxvalue,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getMaxvalue()!=maxvalue) cf.add("maxvalue",this.maxvalue+"",maxvalue+""); } this.maxvalue=maxvalue;}
public void setCycle(String cycle) { sqlMaker.setField("cycle",cycle,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getCycle().equals(cycle)) cf.add("cycle",this.cycle,cycle); } this.cycle=cycle;}
public void setStepvalue(int stepvalue) { sqlMaker.setField("stepvalue",""+stepvalue,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getStepvalue()!=stepvalue) cf.add("stepvalue",this.stepvalue+"",stepvalue+""); } this.stepvalue=stepvalue;}
public void setCache(int cache) { sqlMaker.setField("cache",""+cache,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getCache()!=cache) cf.add("cache",this.cache+"",cache+""); } this.cache=cache;}
public void setYear(String year) { sqlMaker.setField("year",year,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getYear().equals(year)) cf.add("year",this.year,year); } this.year=year;}
public void setCurvalue(int curvalue) { sqlMaker.setField("curvalue",""+curvalue,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getCurvalue()!=curvalue) cf.add("curvalue",this.curvalue+"",curvalue+""); } this.curvalue=curvalue;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"seqname") !=null ) {this.setSeqname(actionRequest.getFieldValue(i,"seqname"));}
if ( actionRequest.getFieldValue(i,"minvalue") !=null && actionRequest.getFieldValue(i,"minvalue").trim().length() > 0 ) {this.setMinvalue(Integer.parseInt(actionRequest.getFieldValue(i,"minvalue")));}
if ( actionRequest.getFieldValue(i,"maxvalue") !=null && actionRequest.getFieldValue(i,"maxvalue").trim().length() > 0 ) {this.setMaxvalue(Integer.parseInt(actionRequest.getFieldValue(i,"maxvalue")));}
if ( actionRequest.getFieldValue(i,"cycle") !=null ) {this.setCycle(actionRequest.getFieldValue(i,"cycle"));}
if ( actionRequest.getFieldValue(i,"stepvalue") !=null && actionRequest.getFieldValue(i,"stepvalue").trim().length() > 0 ) {this.setStepvalue(Integer.parseInt(actionRequest.getFieldValue(i,"stepvalue")));}
if ( actionRequest.getFieldValue(i,"cache") !=null && actionRequest.getFieldValue(i,"cache").trim().length() > 0 ) {this.setCache(Integer.parseInt(actionRequest.getFieldValue(i,"cache")));}
if ( actionRequest.getFieldValue(i,"year") !=null ) {this.setYear(actionRequest.getFieldValue(i,"year"));}
if ( actionRequest.getFieldValue(i,"curvalue") !=null && actionRequest.getFieldValue(i,"curvalue").trim().length() > 0 ) {this.setCurvalue(Integer.parseInt(actionRequest.getFieldValue(i,"curvalue")));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { CPSEQUENCE obj = (CPSEQUENCE)super.clone();obj.setSeqname(obj.seqname);
obj.setMinvalue(obj.minvalue);
obj.setMaxvalue(obj.maxvalue);
obj.setCycle(obj.cycle);
obj.setStepvalue(obj.stepvalue);
obj.setCache(obj.cache);
obj.setYear(obj.year);
obj.setCurvalue(obj.curvalue);
return obj;}}