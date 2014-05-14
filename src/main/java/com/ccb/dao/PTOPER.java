package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTOPER extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTOPER().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTOPER().findAndLockByWhere(sSqlWhere);      }       public static PTOPER findFirst(String sSqlWhere) {           return (PTOPER)new PTOPER().findFirstByWhere(sSqlWhere);      }       public static PTOPER findFirstAndLock(String sSqlWhere) {           return (PTOPER)new PTOPER().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTOPER().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTOPER bean = new PTOPER();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTOPER bean = new PTOPER();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTOPER findFirst(String sSqlWhere,boolean isAutoRelease) {           PTOPER bean = new PTOPER();           bean.setAutoRelease(isAutoRelease);           return (PTOPER)bean.findFirstByWhere(sSqlWhere);      }       public static PTOPER findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTOPER bean = new PTOPER();           bean.setAutoRelease(isAutoRelease);           return (PTOPER)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTOPER bean = new PTOPER();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTOPER().findByWhereByRow(sSqlWhere,row);      } String deptid;
String operid;
String operpasswd;
String issuper;
String operduty;
String opertype;
String opername;
String sex;
String email;
String mobilephone;
String operphone;
String otherphone;
String operenabled;
String fillstr50;
String fillstr80;
String fillstr100;
String fillstr150;
String fillstr600;
String filldate1;
String filldate2;
String filldate3;
int fillint6;
int fillint8;
int fillint10;
int fillint12;
int fillintvar;
double filldbl1;
double filldbl2;
double filldbl3;
String userguid;
public static final String TABLENAME ="ptoper";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTOPER abb = new PTOPER();
abb.deptid=rs.getString("deptid");abb.setKeyValue("DEPTID",""+abb.getDeptid());
abb.operid=rs.getString("operid");abb.setKeyValue("OPERID",""+abb.getOperid());
abb.operpasswd=rs.getString("operpasswd");abb.setKeyValue("OPERPASSWD",""+abb.getOperpasswd());
abb.issuper=rs.getString("issuper");abb.setKeyValue("ISSUPER",""+abb.getIssuper());
abb.operduty=rs.getString("operduty");abb.setKeyValue("OPERDUTY",""+abb.getOperduty());
abb.opertype=rs.getString("opertype");abb.setKeyValue("OPERTYPE",""+abb.getOpertype());
abb.opername=rs.getString("opername");abb.setKeyValue("OPERNAME",""+abb.getOpername());
abb.sex=rs.getString("sex");abb.setKeyValue("SEX",""+abb.getSex());
abb.email=rs.getString("email");abb.setKeyValue("EMAIL",""+abb.getEmail());
abb.mobilephone=rs.getString("mobilephone");abb.setKeyValue("MOBILEPHONE",""+abb.getMobilephone());
abb.operphone=rs.getString("operphone");abb.setKeyValue("OPERPHONE",""+abb.getOperphone());
abb.otherphone=rs.getString("otherphone");abb.setKeyValue("OTHERPHONE",""+abb.getOtherphone());
abb.operenabled=rs.getString("operenabled");abb.setKeyValue("OPERENABLED",""+abb.getOperenabled());
abb.fillstr50=rs.getString("fillstr50");abb.setKeyValue("FILLSTR50",""+abb.getFillstr50());
abb.fillstr80=rs.getString("fillstr80");abb.setKeyValue("FILLSTR80",""+abb.getFillstr80());
abb.fillstr100=rs.getString("fillstr100");abb.setKeyValue("FILLSTR100",""+abb.getFillstr100());
abb.fillstr150=rs.getString("fillstr150");abb.setKeyValue("FILLSTR150",""+abb.getFillstr150());
abb.fillstr600=rs.getString("fillstr600");abb.setKeyValue("FILLSTR600",""+abb.getFillstr600());
abb.filldate1=rs.getTimeString("filldate1");abb.setKeyValue("FILLDATE1",""+abb.getFilldate1());
abb.filldate2=rs.getTimeString("filldate2");abb.setKeyValue("FILLDATE2",""+abb.getFilldate2());
abb.filldate3=rs.getTimeString("filldate3");abb.setKeyValue("FILLDATE3",""+abb.getFilldate3());
abb.fillint6=rs.getInt("fillint6");abb.setKeyValue("FILLINT6",""+abb.getFillint6());
abb.fillint8=rs.getInt("fillint8");abb.setKeyValue("FILLINT8",""+abb.getFillint8());
abb.fillint10=rs.getInt("fillint10");abb.setKeyValue("FILLINT10",""+abb.getFillint10());
abb.fillint12=rs.getInt("fillint12");abb.setKeyValue("FILLINT12",""+abb.getFillint12());
abb.fillintvar=rs.getInt("fillintvar");abb.setKeyValue("FILLINTVAR",""+abb.getFillintvar());
abb.filldbl1=rs.getDouble("filldbl1");abb.setKeyValue("FILLDBL1",""+abb.getFilldbl1());
abb.filldbl2=rs.getDouble("filldbl2");abb.setKeyValue("FILLDBL2",""+abb.getFilldbl2());
abb.filldbl3=rs.getDouble("filldbl3");abb.setKeyValue("FILLDBL3",""+abb.getFilldbl3());
abb.userguid=rs.getString("userguid");abb.setKeyValue("USERGUID",""+abb.getUserguid());
list.add(abb);
abb.operate_mode = "edit";
}public String getDeptid() { if ( this.deptid == null ) return ""; return this.deptid;}
public String getOperid() { if ( this.operid == null ) return ""; return this.operid;}
public String getOperpasswd() { if ( this.operpasswd == null ) return ""; return this.operpasswd;}
public String getIssuper() { if ( this.issuper == null ) return ""; return this.issuper;}
public String getOperduty() { if ( this.operduty == null ) return ""; return this.operduty;}
public String getOpertype() { if ( this.opertype == null ) return ""; return this.opertype;}
public String getOpername() { if ( this.opername == null ) return ""; return this.opername;}
public String getSex() { if ( this.sex == null ) return ""; return this.sex;}
public String getEmail() { if ( this.email == null ) return ""; return this.email;}
public String getMobilephone() { if ( this.mobilephone == null ) return ""; return this.mobilephone;}
public String getOperphone() { if ( this.operphone == null ) return ""; return this.operphone;}
public String getOtherphone() { if ( this.otherphone == null ) return ""; return this.otherphone;}
public String getOperenabled() { if ( this.operenabled == null ) return ""; return this.operenabled;}
public String getFillstr50() { if ( this.fillstr50 == null ) return ""; return this.fillstr50;}
public String getFillstr80() { if ( this.fillstr80 == null ) return ""; return this.fillstr80;}
public String getFillstr100() { if ( this.fillstr100 == null ) return ""; return this.fillstr100;}
public String getFillstr150() { if ( this.fillstr150 == null ) return ""; return this.fillstr150;}
public String getFillstr600() { if ( this.fillstr600 == null ) return ""; return this.fillstr600;}
public String getFilldate1() {  if ( this.filldate1 == null ) { return ""; } else { return this.filldate1.trim().split(" ")[0];} }public String getFilldate1Time() {  if ( this.filldate1 == null ) return ""; return this.filldate1.split("\\.")[0];}
public String getFilldate2() {  if ( this.filldate2 == null ) { return ""; } else { return this.filldate2.trim().split(" ")[0];} }public String getFilldate2Time() {  if ( this.filldate2 == null ) return ""; return this.filldate2.split("\\.")[0];}
public String getFilldate3() {  if ( this.filldate3 == null ) { return ""; } else { return this.filldate3.trim().split(" ")[0];} }public String getFilldate3Time() {  if ( this.filldate3 == null ) return ""; return this.filldate3.split("\\.")[0];}
public int getFillint6() { return this.fillint6;}
public int getFillint8() { return this.fillint8;}
public int getFillint10() { return this.fillint10;}
public int getFillint12() { return this.fillint12;}
public int getFillintvar() { return this.fillintvar;}
public double getFilldbl1() { return this.filldbl1;}
public double getFilldbl2() { return this.filldbl2;}
public double getFilldbl3() { return this.filldbl3;}
public String getUserguid() { if ( this.userguid == null ) return ""; return this.userguid;}
public void setDeptid(String deptid) { sqlMaker.setField("deptid",deptid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getDeptid().equals(deptid)) cf.add("deptid",this.deptid,deptid); } this.deptid=deptid;}
public void setOperid(String operid) { sqlMaker.setField("operid",operid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOperid().equals(operid)) cf.add("operid",this.operid,operid); } this.operid=operid;}
public void setOperpasswd(String operpasswd) { sqlMaker.setField("operpasswd",operpasswd,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOperpasswd().equals(operpasswd)) cf.add("operpasswd",this.operpasswd,operpasswd); } this.operpasswd=operpasswd;}
public void setIssuper(String issuper) { sqlMaker.setField("issuper",issuper,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getIssuper().equals(issuper)) cf.add("issuper",this.issuper,issuper); } this.issuper=issuper;}
public void setOperduty(String operduty) { sqlMaker.setField("operduty",operduty,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOperduty().equals(operduty)) cf.add("operduty",this.operduty,operduty); } this.operduty=operduty;}
public void setOpertype(String opertype) { sqlMaker.setField("opertype",opertype,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOpertype().equals(opertype)) cf.add("opertype",this.opertype,opertype); } this.opertype=opertype;}
public void setOpername(String opername) { sqlMaker.setField("opername",opername,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOpername().equals(opername)) cf.add("opername",this.opername,opername); } this.opername=opername;}
public void setSex(String sex) { sqlMaker.setField("sex",sex,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getSex().equals(sex)) cf.add("sex",this.sex,sex); } this.sex=sex;}
public void setEmail(String email) { sqlMaker.setField("email",email,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEmail().equals(email)) cf.add("email",this.email,email); } this.email=email;}
public void setMobilephone(String mobilephone) { sqlMaker.setField("mobilephone",mobilephone,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMobilephone().equals(mobilephone)) cf.add("mobilephone",this.mobilephone,mobilephone); } this.mobilephone=mobilephone;}
public void setOperphone(String operphone) { sqlMaker.setField("operphone",operphone,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOperphone().equals(operphone)) cf.add("operphone",this.operphone,operphone); } this.operphone=operphone;}
public void setOtherphone(String otherphone) { sqlMaker.setField("otherphone",otherphone,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOtherphone().equals(otherphone)) cf.add("otherphone",this.otherphone,otherphone); } this.otherphone=otherphone;}
public void setOperenabled(String operenabled) { sqlMaker.setField("operenabled",operenabled,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOperenabled().equals(operenabled)) cf.add("operenabled",this.operenabled,operenabled); } this.operenabled=operenabled;}
public void setFillstr50(String fillstr50) { sqlMaker.setField("fillstr50",fillstr50,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr50().equals(fillstr50)) cf.add("fillstr50",this.fillstr50,fillstr50); } this.fillstr50=fillstr50;}
public void setFillstr80(String fillstr80) { sqlMaker.setField("fillstr80",fillstr80,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr80().equals(fillstr80)) cf.add("fillstr80",this.fillstr80,fillstr80); } this.fillstr80=fillstr80;}
public void setFillstr100(String fillstr100) { sqlMaker.setField("fillstr100",fillstr100,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr100().equals(fillstr100)) cf.add("fillstr100",this.fillstr100,fillstr100); } this.fillstr100=fillstr100;}
public void setFillstr150(String fillstr150) { sqlMaker.setField("fillstr150",fillstr150,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr150().equals(fillstr150)) cf.add("fillstr150",this.fillstr150,fillstr150); } this.fillstr150=fillstr150;}
public void setFillstr600(String fillstr600) { sqlMaker.setField("fillstr600",fillstr600,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr600().equals(fillstr600)) cf.add("fillstr600",this.fillstr600,fillstr600); } this.fillstr600=fillstr600;}
public void setFilldate1(String filldate1) { sqlMaker.setField("filldate1",filldate1,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getFilldate1().equals(filldate1)) cf.add("filldate1",this.filldate1,filldate1); } this.filldate1=filldate1;}
public void setFilldate2(String filldate2) { sqlMaker.setField("filldate2",filldate2,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getFilldate2().equals(filldate2)) cf.add("filldate2",this.filldate2,filldate2); } this.filldate2=filldate2;}
public void setFilldate3(String filldate3) { sqlMaker.setField("filldate3",filldate3,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getFilldate3().equals(filldate3)) cf.add("filldate3",this.filldate3,filldate3); } this.filldate3=filldate3;}
public void setFillint6(int fillint6) { sqlMaker.setField("fillint6",""+fillint6,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillint6()!=fillint6) cf.add("fillint6",this.fillint6+"",fillint6+""); } this.fillint6=fillint6;}
public void setFillint8(int fillint8) { sqlMaker.setField("fillint8",""+fillint8,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillint8()!=fillint8) cf.add("fillint8",this.fillint8+"",fillint8+""); } this.fillint8=fillint8;}
public void setFillint10(int fillint10) { sqlMaker.setField("fillint10",""+fillint10,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillint10()!=fillint10) cf.add("fillint10",this.fillint10+"",fillint10+""); } this.fillint10=fillint10;}
public void setFillint12(int fillint12) { sqlMaker.setField("fillint12",""+fillint12,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillint12()!=fillint12) cf.add("fillint12",this.fillint12+"",fillint12+""); } this.fillint12=fillint12;}
public void setFillintvar(int fillintvar) { sqlMaker.setField("fillintvar",""+fillintvar,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillintvar()!=fillintvar) cf.add("fillintvar",this.fillintvar+"",fillintvar+""); } this.fillintvar=fillintvar;}
public void setFilldbl1(double filldbl1) { sqlMaker.setField("filldbl1",""+filldbl1,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFilldbl1()!=filldbl1) cf.add("filldbl1",this.filldbl1+"",filldbl1+""); } this.filldbl1=filldbl1;}
public void setFilldbl2(double filldbl2) { sqlMaker.setField("filldbl2",""+filldbl2,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFilldbl2()!=filldbl2) cf.add("filldbl2",this.filldbl2+"",filldbl2+""); } this.filldbl2=filldbl2;}
public void setFilldbl3(double filldbl3) { sqlMaker.setField("filldbl3",""+filldbl3,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFilldbl3()!=filldbl3) cf.add("filldbl3",this.filldbl3+"",filldbl3+""); } this.filldbl3=filldbl3;}
public void setUserguid(String userguid) { sqlMaker.setField("userguid",userguid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getUserguid().equals(userguid)) cf.add("userguid",this.userguid,userguid); } this.userguid=userguid;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"deptid") !=null ) {this.setDeptid(actionRequest.getFieldValue(i,"deptid"));}
if ( actionRequest.getFieldValue(i,"operid") !=null ) {this.setOperid(actionRequest.getFieldValue(i,"operid"));}
if ( actionRequest.getFieldValue(i,"operpasswd") !=null ) {this.setOperpasswd(actionRequest.getFieldValue(i,"operpasswd"));}
if ( actionRequest.getFieldValue(i,"issuper") !=null ) {this.setIssuper(actionRequest.getFieldValue(i,"issuper"));}
if ( actionRequest.getFieldValue(i,"operduty") !=null ) {this.setOperduty(actionRequest.getFieldValue(i,"operduty"));}
if ( actionRequest.getFieldValue(i,"opertype") !=null ) {this.setOpertype(actionRequest.getFieldValue(i,"opertype"));}
if ( actionRequest.getFieldValue(i,"opername") !=null ) {this.setOpername(actionRequest.getFieldValue(i,"opername"));}
if ( actionRequest.getFieldValue(i,"sex") !=null ) {this.setSex(actionRequest.getFieldValue(i,"sex"));}
if ( actionRequest.getFieldValue(i,"email") !=null ) {this.setEmail(actionRequest.getFieldValue(i,"email"));}
if ( actionRequest.getFieldValue(i,"mobilephone") !=null ) {this.setMobilephone(actionRequest.getFieldValue(i,"mobilephone"));}
if ( actionRequest.getFieldValue(i,"operphone") !=null ) {this.setOperphone(actionRequest.getFieldValue(i,"operphone"));}
if ( actionRequest.getFieldValue(i,"otherphone") !=null ) {this.setOtherphone(actionRequest.getFieldValue(i,"otherphone"));}
if ( actionRequest.getFieldValue(i,"operenabled") !=null ) {this.setOperenabled(actionRequest.getFieldValue(i,"operenabled"));}
if ( actionRequest.getFieldValue(i,"fillstr50") !=null ) {this.setFillstr50(actionRequest.getFieldValue(i,"fillstr50"));}
if ( actionRequest.getFieldValue(i,"fillstr80") !=null ) {this.setFillstr80(actionRequest.getFieldValue(i,"fillstr80"));}
if ( actionRequest.getFieldValue(i,"fillstr100") !=null ) {this.setFillstr100(actionRequest.getFieldValue(i,"fillstr100"));}
if ( actionRequest.getFieldValue(i,"fillstr150") !=null ) {this.setFillstr150(actionRequest.getFieldValue(i,"fillstr150"));}
if ( actionRequest.getFieldValue(i,"fillstr600") !=null ) {this.setFillstr600(actionRequest.getFieldValue(i,"fillstr600"));}
if ( actionRequest.getFieldValue(i,"filldate1") !=null ) {this.setFilldate1(actionRequest.getFieldValue(i,"filldate1"));}
if ( actionRequest.getFieldValue(i,"filldate2") !=null ) {this.setFilldate2(actionRequest.getFieldValue(i,"filldate2"));}
if ( actionRequest.getFieldValue(i,"filldate3") !=null ) {this.setFilldate3(actionRequest.getFieldValue(i,"filldate3"));}
if ( actionRequest.getFieldValue(i,"fillint6") !=null && actionRequest.getFieldValue(i,"fillint6").trim().length() > 0 ) {this.setFillint6(Integer.parseInt(actionRequest.getFieldValue(i,"fillint6")));}
if ( actionRequest.getFieldValue(i,"fillint8") !=null && actionRequest.getFieldValue(i,"fillint8").trim().length() > 0 ) {this.setFillint8(Integer.parseInt(actionRequest.getFieldValue(i,"fillint8")));}
if ( actionRequest.getFieldValue(i,"fillint10") !=null && actionRequest.getFieldValue(i,"fillint10").trim().length() > 0 ) {this.setFillint10(Integer.parseInt(actionRequest.getFieldValue(i,"fillint10")));}
if ( actionRequest.getFieldValue(i,"fillint12") !=null && actionRequest.getFieldValue(i,"fillint12").trim().length() > 0 ) {this.setFillint12(Integer.parseInt(actionRequest.getFieldValue(i,"fillint12")));}
if ( actionRequest.getFieldValue(i,"fillintvar") !=null && actionRequest.getFieldValue(i,"fillintvar").trim().length() > 0 ) {this.setFillintvar(Integer.parseInt(actionRequest.getFieldValue(i,"fillintvar")));}
if ( actionRequest.getFieldValue(i,"filldbl1") !=null && actionRequest.getFieldValue(i,"filldbl1").trim().length() > 0 ) {this.setFilldbl1(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl1")));}
if ( actionRequest.getFieldValue(i,"filldbl2") !=null && actionRequest.getFieldValue(i,"filldbl2").trim().length() > 0 ) {this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl2")));}
if ( actionRequest.getFieldValue(i,"filldbl3") !=null && actionRequest.getFieldValue(i,"filldbl3").trim().length() > 0 ) {this.setFilldbl3(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl3")));}
if ( actionRequest.getFieldValue(i,"userguid") !=null ) {this.setUserguid(actionRequest.getFieldValue(i,"userguid"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTOPER obj = (PTOPER)super.clone();obj.setDeptid(obj.deptid);
obj.setOperid(obj.operid);
obj.setOperpasswd(obj.operpasswd);
obj.setIssuper(obj.issuper);
obj.setOperduty(obj.operduty);
obj.setOpertype(obj.opertype);
obj.setOpername(obj.opername);
obj.setSex(obj.sex);
obj.setEmail(obj.email);
obj.setMobilephone(obj.mobilephone);
obj.setOperphone(obj.operphone);
obj.setOtherphone(obj.otherphone);
obj.setOperenabled(obj.operenabled);
obj.setFillstr50(obj.fillstr50);
obj.setFillstr80(obj.fillstr80);
obj.setFillstr100(obj.fillstr100);
obj.setFillstr150(obj.fillstr150);
obj.setFillstr600(obj.fillstr600);
obj.setFilldate1(obj.filldate1);
obj.setFilldate2(obj.filldate2);
obj.setFilldate3(obj.filldate3);
obj.setFillint6(obj.fillint6);
obj.setFillint8(obj.fillint8);
obj.setFillint10(obj.fillint10);
obj.setFillint12(obj.fillint12);
obj.setFillintvar(obj.fillintvar);
obj.setFilldbl1(obj.filldbl1);
obj.setFilldbl2(obj.filldbl2);
obj.setFilldbl3(obj.filldbl3);
obj.setUserguid(obj.userguid);
return obj;}}