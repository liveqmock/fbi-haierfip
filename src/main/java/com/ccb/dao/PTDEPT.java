package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTDEPT extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTDEPT().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTDEPT().findAndLockByWhere(sSqlWhere);      }       public static PTDEPT findFirst(String sSqlWhere) {           return (PTDEPT)new PTDEPT().findFirstByWhere(sSqlWhere);      }       public static PTDEPT findFirstAndLock(String sSqlWhere) {           return (PTDEPT)new PTDEPT().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTDEPT().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTDEPT bean = new PTDEPT();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTDEPT bean = new PTDEPT();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTDEPT findFirst(String sSqlWhere,boolean isAutoRelease) {           PTDEPT bean = new PTDEPT();           bean.setAutoRelease(isAutoRelease);           return (PTDEPT)bean.findFirstByWhere(sSqlWhere);      }       public static PTDEPT findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTDEPT bean = new PTDEPT();           bean.setAutoRelease(isAutoRelease);           return (PTDEPT)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTDEPT bean = new PTDEPT();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTDEPT().findByWhereByRow(sSqlWhere,row);      } String deptid;
String deptname;
String deptdesc;
String parentdeptid;
String deptleaf;
int deptlevel;
String deptstatus;
String isdummy;
String fillstr10;
String fillstr20;
String fillstr100;
String fillstr150;
int fillint4;
int fillint6;
int fillint8;
double filldbl2;
double filldbl22;
double filldbl4;
String filldate1;
String filldate2;
String dqdm;
String mkdm;
int deptindex;
String deptguid;
public static final String TABLENAME ="ptdept";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTDEPT abb = new PTDEPT();
abb.deptid=rs.getString("deptid");abb.setKeyValue("DEPTID",""+abb.getDeptid());
abb.deptname=rs.getString("deptname");abb.setKeyValue("DEPTNAME",""+abb.getDeptname());
abb.deptdesc=rs.getString("deptdesc");abb.setKeyValue("DEPTDESC",""+abb.getDeptdesc());
abb.parentdeptid=rs.getString("parentdeptid");abb.setKeyValue("PARENTDEPTID",""+abb.getParentdeptid());
abb.deptleaf=rs.getString("deptleaf");abb.setKeyValue("DEPTLEAF",""+abb.getDeptleaf());
abb.deptlevel=rs.getInt("deptlevel");abb.setKeyValue("DEPTLEVEL",""+abb.getDeptlevel());
abb.deptstatus=rs.getString("deptstatus");abb.setKeyValue("DEPTSTATUS",""+abb.getDeptstatus());
abb.isdummy=rs.getString("isdummy");abb.setKeyValue("ISDUMMY",""+abb.getIsdummy());
abb.fillstr10=rs.getString("fillstr10");abb.setKeyValue("FILLSTR10",""+abb.getFillstr10());
abb.fillstr20=rs.getString("fillstr20");abb.setKeyValue("FILLSTR20",""+abb.getFillstr20());
abb.fillstr100=rs.getString("fillstr100");abb.setKeyValue("FILLSTR100",""+abb.getFillstr100());
abb.fillstr150=rs.getString("fillstr150");abb.setKeyValue("FILLSTR150",""+abb.getFillstr150());
abb.fillint4=rs.getInt("fillint4");abb.setKeyValue("FILLINT4",""+abb.getFillint4());
abb.fillint6=rs.getInt("fillint6");abb.setKeyValue("FILLINT6",""+abb.getFillint6());
abb.fillint8=rs.getInt("fillint8");abb.setKeyValue("FILLINT8",""+abb.getFillint8());
abb.filldbl2=rs.getDouble("filldbl2");abb.setKeyValue("FILLDBL2",""+abb.getFilldbl2());
abb.filldbl22=rs.getDouble("filldbl22");abb.setKeyValue("FILLDBL22",""+abb.getFilldbl22());
abb.filldbl4=rs.getDouble("filldbl4");abb.setKeyValue("FILLDBL4",""+abb.getFilldbl4());
abb.filldate1=rs.getTimeString("filldate1");abb.setKeyValue("FILLDATE1",""+abb.getFilldate1());
abb.filldate2=rs.getTimeString("filldate2");abb.setKeyValue("FILLDATE2",""+abb.getFilldate2());
abb.dqdm=rs.getString("dqdm");abb.setKeyValue("DQDM",""+abb.getDqdm());
abb.mkdm=rs.getString("mkdm");abb.setKeyValue("MKDM",""+abb.getMkdm());
abb.deptindex=rs.getInt("deptindex");abb.setKeyValue("DEPTINDEX",""+abb.getDeptindex());
abb.deptguid=rs.getString("deptguid");abb.setKeyValue("DEPTGUID",""+abb.getDeptguid());
list.add(abb);
abb.operate_mode = "edit";
}public String getDeptid() { if ( this.deptid == null ) return ""; return this.deptid;}
public String getDeptname() { if ( this.deptname == null ) return ""; return this.deptname;}
public String getDeptdesc() { if ( this.deptdesc == null ) return ""; return this.deptdesc;}
public String getParentdeptid() { if ( this.parentdeptid == null ) return ""; return this.parentdeptid;}
public String getDeptleaf() { if ( this.deptleaf == null ) return ""; return this.deptleaf;}
public int getDeptlevel() { return this.deptlevel;}
public String getDeptstatus() { if ( this.deptstatus == null ) return ""; return this.deptstatus;}
public String getIsdummy() { if ( this.isdummy == null ) return ""; return this.isdummy;}
public String getFillstr10() { if ( this.fillstr10 == null ) return ""; return this.fillstr10;}
public String getFillstr20() { if ( this.fillstr20 == null ) return ""; return this.fillstr20;}
public String getFillstr100() { if ( this.fillstr100 == null ) return ""; return this.fillstr100;}
public String getFillstr150() { if ( this.fillstr150 == null ) return ""; return this.fillstr150;}
public int getFillint4() { return this.fillint4;}
public int getFillint6() { return this.fillint6;}
public int getFillint8() { return this.fillint8;}
public double getFilldbl2() { return this.filldbl2;}
public double getFilldbl22() { return this.filldbl22;}
public double getFilldbl4() { return this.filldbl4;}
public String getFilldate1() {  if ( this.filldate1 == null ) { return ""; } else { return this.filldate1.trim().split(" ")[0];} }public String getFilldate1Time() {  if ( this.filldate1 == null ) return ""; return this.filldate1.split("\\.")[0];}
public String getFilldate2() {  if ( this.filldate2 == null ) { return ""; } else { return this.filldate2.trim().split(" ")[0];} }public String getFilldate2Time() {  if ( this.filldate2 == null ) return ""; return this.filldate2.split("\\.")[0];}
public String getDqdm() { if ( this.dqdm == null ) return ""; return this.dqdm;}
public String getMkdm() { if ( this.mkdm == null ) return ""; return this.mkdm;}
public int getDeptindex() { return this.deptindex;}
public String getDeptguid() { if ( this.deptguid == null ) return ""; return this.deptguid;}
public void setDeptid(String deptid) { sqlMaker.setField("deptid",deptid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getDeptid().equals(deptid)) cf.add("deptid",this.deptid,deptid); } this.deptid=deptid;}
public void setDeptname(String deptname) { sqlMaker.setField("deptname",deptname,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getDeptname().equals(deptname)) cf.add("deptname",this.deptname,deptname); } this.deptname=deptname;}
public void setDeptdesc(String deptdesc) { sqlMaker.setField("deptdesc",deptdesc,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getDeptdesc().equals(deptdesc)) cf.add("deptdesc",this.deptdesc,deptdesc); } this.deptdesc=deptdesc;}
public void setParentdeptid(String parentdeptid) { sqlMaker.setField("parentdeptid",parentdeptid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getParentdeptid().equals(parentdeptid)) cf.add("parentdeptid",this.parentdeptid,parentdeptid); } this.parentdeptid=parentdeptid;}
public void setDeptleaf(String deptleaf) { sqlMaker.setField("deptleaf",deptleaf,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getDeptleaf().equals(deptleaf)) cf.add("deptleaf",this.deptleaf,deptleaf); } this.deptleaf=deptleaf;}
public void setDeptlevel(int deptlevel) { sqlMaker.setField("deptlevel",""+deptlevel,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getDeptlevel()!=deptlevel) cf.add("deptlevel",this.deptlevel+"",deptlevel+""); } this.deptlevel=deptlevel;}
public void setDeptstatus(String deptstatus) { sqlMaker.setField("deptstatus",deptstatus,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getDeptstatus().equals(deptstatus)) cf.add("deptstatus",this.deptstatus,deptstatus); } this.deptstatus=deptstatus;}
public void setIsdummy(String isdummy) { sqlMaker.setField("isdummy",isdummy,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getIsdummy().equals(isdummy)) cf.add("isdummy",this.isdummy,isdummy); } this.isdummy=isdummy;}
public void setFillstr10(String fillstr10) { sqlMaker.setField("fillstr10",fillstr10,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr10().equals(fillstr10)) cf.add("fillstr10",this.fillstr10,fillstr10); } this.fillstr10=fillstr10;}
public void setFillstr20(String fillstr20) { sqlMaker.setField("fillstr20",fillstr20,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr20().equals(fillstr20)) cf.add("fillstr20",this.fillstr20,fillstr20); } this.fillstr20=fillstr20;}
public void setFillstr100(String fillstr100) { sqlMaker.setField("fillstr100",fillstr100,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr100().equals(fillstr100)) cf.add("fillstr100",this.fillstr100,fillstr100); } this.fillstr100=fillstr100;}
public void setFillstr150(String fillstr150) { sqlMaker.setField("fillstr150",fillstr150,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr150().equals(fillstr150)) cf.add("fillstr150",this.fillstr150,fillstr150); } this.fillstr150=fillstr150;}
public void setFillint4(int fillint4) { sqlMaker.setField("fillint4",""+fillint4,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillint4()!=fillint4) cf.add("fillint4",this.fillint4+"",fillint4+""); } this.fillint4=fillint4;}
public void setFillint6(int fillint6) { sqlMaker.setField("fillint6",""+fillint6,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillint6()!=fillint6) cf.add("fillint6",this.fillint6+"",fillint6+""); } this.fillint6=fillint6;}
public void setFillint8(int fillint8) { sqlMaker.setField("fillint8",""+fillint8,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillint8()!=fillint8) cf.add("fillint8",this.fillint8+"",fillint8+""); } this.fillint8=fillint8;}
public void setFilldbl2(double filldbl2) { sqlMaker.setField("filldbl2",""+filldbl2,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFilldbl2()!=filldbl2) cf.add("filldbl2",this.filldbl2+"",filldbl2+""); } this.filldbl2=filldbl2;}
public void setFilldbl22(double filldbl22) { sqlMaker.setField("filldbl22",""+filldbl22,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFilldbl22()!=filldbl22) cf.add("filldbl22",this.filldbl22+"",filldbl22+""); } this.filldbl22=filldbl22;}
public void setFilldbl4(double filldbl4) { sqlMaker.setField("filldbl4",""+filldbl4,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFilldbl4()!=filldbl4) cf.add("filldbl4",this.filldbl4+"",filldbl4+""); } this.filldbl4=filldbl4;}
public void setFilldate1(String filldate1) { sqlMaker.setField("filldate1",filldate1,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getFilldate1().equals(filldate1)) cf.add("filldate1",this.filldate1,filldate1); } this.filldate1=filldate1;}
public void setFilldate2(String filldate2) { sqlMaker.setField("filldate2",filldate2,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getFilldate2().equals(filldate2)) cf.add("filldate2",this.filldate2,filldate2); } this.filldate2=filldate2;}
public void setDqdm(String dqdm) { sqlMaker.setField("dqdm",dqdm,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getDqdm().equals(dqdm)) cf.add("dqdm",this.dqdm,dqdm); } this.dqdm=dqdm;}
public void setMkdm(String mkdm) { sqlMaker.setField("mkdm",mkdm,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMkdm().equals(mkdm)) cf.add("mkdm",this.mkdm,mkdm); } this.mkdm=mkdm;}
public void setDeptindex(int deptindex) { sqlMaker.setField("deptindex",""+deptindex,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getDeptindex()!=deptindex) cf.add("deptindex",this.deptindex+"",deptindex+""); } this.deptindex=deptindex;}
public void setDeptguid(String deptguid) { sqlMaker.setField("deptguid",deptguid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getDeptguid().equals(deptguid)) cf.add("deptguid",this.deptguid,deptguid); } this.deptguid=deptguid;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"deptid") !=null ) {this.setDeptid(actionRequest.getFieldValue(i,"deptid"));}
if ( actionRequest.getFieldValue(i,"deptname") !=null ) {this.setDeptname(actionRequest.getFieldValue(i,"deptname"));}
if ( actionRequest.getFieldValue(i,"deptdesc") !=null ) {this.setDeptdesc(actionRequest.getFieldValue(i,"deptdesc"));}
if ( actionRequest.getFieldValue(i,"parentdeptid") !=null ) {this.setParentdeptid(actionRequest.getFieldValue(i,"parentdeptid"));}
if ( actionRequest.getFieldValue(i,"deptleaf") !=null ) {this.setDeptleaf(actionRequest.getFieldValue(i,"deptleaf"));}
if ( actionRequest.getFieldValue(i,"deptlevel") !=null && actionRequest.getFieldValue(i,"deptlevel").trim().length() > 0 ) {this.setDeptlevel(Integer.parseInt(actionRequest.getFieldValue(i,"deptlevel")));}
if ( actionRequest.getFieldValue(i,"deptstatus") !=null ) {this.setDeptstatus(actionRequest.getFieldValue(i,"deptstatus"));}
if ( actionRequest.getFieldValue(i,"isdummy") !=null ) {this.setIsdummy(actionRequest.getFieldValue(i,"isdummy"));}
if ( actionRequest.getFieldValue(i,"fillstr10") !=null ) {this.setFillstr10(actionRequest.getFieldValue(i,"fillstr10"));}
if ( actionRequest.getFieldValue(i,"fillstr20") !=null ) {this.setFillstr20(actionRequest.getFieldValue(i,"fillstr20"));}
if ( actionRequest.getFieldValue(i,"fillstr100") !=null ) {this.setFillstr100(actionRequest.getFieldValue(i,"fillstr100"));}
if ( actionRequest.getFieldValue(i,"fillstr150") !=null ) {this.setFillstr150(actionRequest.getFieldValue(i,"fillstr150"));}
if ( actionRequest.getFieldValue(i,"fillint4") !=null && actionRequest.getFieldValue(i,"fillint4").trim().length() > 0 ) {this.setFillint4(Integer.parseInt(actionRequest.getFieldValue(i,"fillint4")));}
if ( actionRequest.getFieldValue(i,"fillint6") !=null && actionRequest.getFieldValue(i,"fillint6").trim().length() > 0 ) {this.setFillint6(Integer.parseInt(actionRequest.getFieldValue(i,"fillint6")));}
if ( actionRequest.getFieldValue(i,"fillint8") !=null && actionRequest.getFieldValue(i,"fillint8").trim().length() > 0 ) {this.setFillint8(Integer.parseInt(actionRequest.getFieldValue(i,"fillint8")));}
if ( actionRequest.getFieldValue(i,"filldbl2") !=null && actionRequest.getFieldValue(i,"filldbl2").trim().length() > 0 ) {this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl2")));}
if ( actionRequest.getFieldValue(i,"filldbl22") !=null && actionRequest.getFieldValue(i,"filldbl22").trim().length() > 0 ) {this.setFilldbl22(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl22")));}
if ( actionRequest.getFieldValue(i,"filldbl4") !=null && actionRequest.getFieldValue(i,"filldbl4").trim().length() > 0 ) {this.setFilldbl4(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl4")));}
if ( actionRequest.getFieldValue(i,"filldate1") !=null ) {this.setFilldate1(actionRequest.getFieldValue(i,"filldate1"));}
if ( actionRequest.getFieldValue(i,"filldate2") !=null ) {this.setFilldate2(actionRequest.getFieldValue(i,"filldate2"));}
if ( actionRequest.getFieldValue(i,"dqdm") !=null ) {this.setDqdm(actionRequest.getFieldValue(i,"dqdm"));}
if ( actionRequest.getFieldValue(i,"mkdm") !=null ) {this.setMkdm(actionRequest.getFieldValue(i,"mkdm"));}
if ( actionRequest.getFieldValue(i,"deptindex") !=null && actionRequest.getFieldValue(i,"deptindex").trim().length() > 0 ) {this.setDeptindex(Integer.parseInt(actionRequest.getFieldValue(i,"deptindex")));}
if ( actionRequest.getFieldValue(i,"deptguid") !=null ) {this.setDeptguid(actionRequest.getFieldValue(i,"deptguid"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTDEPT obj = (PTDEPT)super.clone();obj.setDeptid(obj.deptid);
obj.setDeptname(obj.deptname);
obj.setDeptdesc(obj.deptdesc);
obj.setParentdeptid(obj.parentdeptid);
obj.setDeptleaf(obj.deptleaf);
obj.setDeptlevel(obj.deptlevel);
obj.setDeptstatus(obj.deptstatus);
obj.setIsdummy(obj.isdummy);
obj.setFillstr10(obj.fillstr10);
obj.setFillstr20(obj.fillstr20);
obj.setFillstr100(obj.fillstr100);
obj.setFillstr150(obj.fillstr150);
obj.setFillint4(obj.fillint4);
obj.setFillint6(obj.fillint6);
obj.setFillint8(obj.fillint8);
obj.setFilldbl2(obj.filldbl2);
obj.setFilldbl22(obj.filldbl22);
obj.setFilldbl4(obj.filldbl4);
obj.setFilldate1(obj.filldate1);
obj.setFilldate2(obj.filldate2);
obj.setDqdm(obj.dqdm);
obj.setMkdm(obj.mkdm);
obj.setDeptindex(obj.deptindex);
obj.setDeptguid(obj.deptguid);
return obj;}}