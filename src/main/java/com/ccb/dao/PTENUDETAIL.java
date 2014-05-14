package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTENUDETAIL extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTENUDETAIL().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTENUDETAIL().findAndLockByWhere(sSqlWhere);      }       public static PTENUDETAIL findFirst(String sSqlWhere) {           return (PTENUDETAIL)new PTENUDETAIL().findFirstByWhere(sSqlWhere);      }       public static PTENUDETAIL findFirstAndLock(String sSqlWhere) {           return (PTENUDETAIL)new PTENUDETAIL().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTENUDETAIL().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTENUDETAIL bean = new PTENUDETAIL();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTENUDETAIL bean = new PTENUDETAIL();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTENUDETAIL findFirst(String sSqlWhere,boolean isAutoRelease) {           PTENUDETAIL bean = new PTENUDETAIL();           bean.setAutoRelease(isAutoRelease);           return (PTENUDETAIL)bean.findFirstByWhere(sSqlWhere);      }       public static PTENUDETAIL findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTENUDETAIL bean = new PTENUDETAIL();           bean.setAutoRelease(isAutoRelease);           return (PTENUDETAIL)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTENUDETAIL bean = new PTENUDETAIL();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTENUDETAIL().findByWhereByRow(sSqlWhere,row);      } String enutype;
String enuitemvalue;
String enuitemlabel;
String enuitemdesc;
int dispno;
String enuitemexpand;
public static final String TABLENAME ="ptenudetail";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTENUDETAIL abb = new PTENUDETAIL();
abb.enutype=rs.getString("enutype");abb.setKeyValue("ENUTYPE",""+abb.getEnutype());
abb.enuitemvalue=rs.getString("enuitemvalue");abb.setKeyValue("ENUITEMVALUE",""+abb.getEnuitemvalue());
abb.enuitemlabel=rs.getString("enuitemlabel");abb.setKeyValue("ENUITEMLABEL",""+abb.getEnuitemlabel());
abb.enuitemdesc=rs.getString("enuitemdesc");abb.setKeyValue("ENUITEMDESC",""+abb.getEnuitemdesc());
abb.dispno=rs.getInt("dispno");abb.setKeyValue("DISPNO",""+abb.getDispno());
abb.enuitemexpand=rs.getString("enuitemexpand");abb.setKeyValue("ENUITEMEXPAND",""+abb.getEnuitemexpand());
list.add(abb);
abb.operate_mode = "edit";
}public String getEnutype() { if ( this.enutype == null ) return ""; return this.enutype;}
public String getEnuitemvalue() { if ( this.enuitemvalue == null ) return ""; return this.enuitemvalue;}
public String getEnuitemlabel() { if ( this.enuitemlabel == null ) return ""; return this.enuitemlabel;}
public String getEnuitemdesc() { if ( this.enuitemdesc == null ) return ""; return this.enuitemdesc;}
public int getDispno() { return this.dispno;}
public String getEnuitemexpand() { if ( this.enuitemexpand == null ) return ""; return this.enuitemexpand;}
public void setEnutype(String enutype) { sqlMaker.setField("enutype",enutype,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEnutype().equals(enutype)) cf.add("enutype",this.enutype,enutype); } this.enutype=enutype;}
public void setEnuitemvalue(String enuitemvalue) { sqlMaker.setField("enuitemvalue",enuitemvalue,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEnuitemvalue().equals(enuitemvalue)) cf.add("enuitemvalue",this.enuitemvalue,enuitemvalue); } this.enuitemvalue=enuitemvalue;}
public void setEnuitemlabel(String enuitemlabel) { sqlMaker.setField("enuitemlabel",enuitemlabel,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEnuitemlabel().equals(enuitemlabel)) cf.add("enuitemlabel",this.enuitemlabel,enuitemlabel); } this.enuitemlabel=enuitemlabel;}
public void setEnuitemdesc(String enuitemdesc) { sqlMaker.setField("enuitemdesc",enuitemdesc,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEnuitemdesc().equals(enuitemdesc)) cf.add("enuitemdesc",this.enuitemdesc,enuitemdesc); } this.enuitemdesc=enuitemdesc;}
public void setDispno(int dispno) { sqlMaker.setField("dispno",""+dispno,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getDispno()!=dispno) cf.add("dispno",this.dispno+"",dispno+""); } this.dispno=dispno;}
public void setEnuitemexpand(String enuitemexpand) { sqlMaker.setField("enuitemexpand",enuitemexpand,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getEnuitemexpand().equals(enuitemexpand)) cf.add("enuitemexpand",this.enuitemexpand,enuitemexpand); } this.enuitemexpand=enuitemexpand;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"enutype") !=null ) {this.setEnutype(actionRequest.getFieldValue(i,"enutype"));}
if ( actionRequest.getFieldValue(i,"enuitemvalue") !=null ) {this.setEnuitemvalue(actionRequest.getFieldValue(i,"enuitemvalue"));}
if ( actionRequest.getFieldValue(i,"enuitemlabel") !=null ) {this.setEnuitemlabel(actionRequest.getFieldValue(i,"enuitemlabel"));}
if ( actionRequest.getFieldValue(i,"enuitemdesc") !=null ) {this.setEnuitemdesc(actionRequest.getFieldValue(i,"enuitemdesc"));}
if ( actionRequest.getFieldValue(i,"dispno") !=null && actionRequest.getFieldValue(i,"dispno").trim().length() > 0 ) {this.setDispno(Integer.parseInt(actionRequest.getFieldValue(i,"dispno")));}
if ( actionRequest.getFieldValue(i,"enuitemexpand") !=null ) {this.setEnuitemexpand(actionRequest.getFieldValue(i,"enuitemexpand"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTENUDETAIL obj = (PTENUDETAIL)super.clone();obj.setEnutype(obj.enutype);
obj.setEnuitemvalue(obj.enuitemvalue);
obj.setEnuitemlabel(obj.enuitemlabel);
obj.setEnuitemdesc(obj.enuitemdesc);
obj.setDispno(obj.dispno);
obj.setEnuitemexpand(obj.enuitemexpand);
return obj;}}