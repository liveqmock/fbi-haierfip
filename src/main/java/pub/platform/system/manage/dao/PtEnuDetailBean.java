package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PtEnuDetailBean extends AbstractBasicBean implements Cloneable {
String enutype;
String enuitemvalue;
String enuitemlabel;
String enuitemdesc;
int dispno;
String enuitemexpand;
public static final String TABLENAME ="ptenudetail";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PtEnuDetailBean abb = new PtEnuDetailBean();
abb.enutype=rs.getString("enutype");
abb.enuitemvalue=rs.getString("enuitemvalue");
abb.enuitemlabel=rs.getString("enuitemlabel");
abb.enuitemdesc=rs.getString("enuitemdesc");
abb.dispno=rs.getInt("dispno");
abb.enuitemexpand=rs.getString("enuitemexpand");
list.add(abb);
}public String getEnutype() { if ( this.enutype == null ) return ""; return this.enutype;}
public String getEnuitemvalue() { if ( this.enuitemvalue == null ) return ""; return this.enuitemvalue;}
public String getEnuitemlabel() { if ( this.enuitemlabel == null ) return ""; return this.enuitemlabel;}
public String getEnuitemdesc() { if ( this.enuitemdesc == null ) return ""; return this.enuitemdesc;}
public int getDispno() { return this.dispno;}
public String getEnuitemexpand() { if ( this.enuitemexpand == null ) return ""; return this.enuitemexpand;}
public void setEnutype(String enutype) { sqlMaker.setField("enutype",enutype,Field.TEXT);this.enutype=enutype;}
public void setEnuitemvalue(String enuitemvalue) { sqlMaker.setField("enuitemvalue",enuitemvalue,Field.TEXT);this.enuitemvalue=enuitemvalue;}
public void setEnuitemlabel(String enuitemlabel) { sqlMaker.setField("enuitemlabel",enuitemlabel,Field.TEXT);this.enuitemlabel=enuitemlabel;}
public void setEnuitemdesc(String enuitemdesc) { sqlMaker.setField("enuitemdesc",enuitemdesc,Field.TEXT);this.enuitemdesc=enuitemdesc;}
public void setDispno(int dispno) { sqlMaker.setField("dispno",""+dispno,Field.NUMBER);this.dispno=dispno;}
public void setEnuitemexpand(String enuitemexpand) { sqlMaker.setField("enuitemexpand",enuitemexpand,Field.TEXT);this.enuitemexpand=enuitemexpand;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"enutype") !=null ) {this.setEnutype(actionRequest.getFieldValue(i,"enutype"));}
if ( actionRequest.getFieldValue(i,"enuitemvalue") !=null ) {this.setEnuitemvalue(actionRequest.getFieldValue(i,"enuitemvalue"));}
if ( actionRequest.getFieldValue(i,"enuitemlabel") !=null ) {this.setEnuitemlabel(actionRequest.getFieldValue(i,"enuitemlabel"));}
if ( actionRequest.getFieldValue(i,"enuitemdesc") !=null ) {this.setEnuitemdesc(actionRequest.getFieldValue(i,"enuitemdesc"));}
if ( actionRequest.getFieldValue(i,"dispno") !=null && actionRequest.getFieldValue(i,"dispno").trim().length() > 0 ) {this.setDispno(Integer.parseInt(actionRequest.getFieldValue(i,"dispno")));}
if ( actionRequest.getFieldValue(i,"enuitemexpand") !=null ) {this.setEnuitemexpand(actionRequest.getFieldValue(i,"enuitemexpand"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"enutype") !=null ) {this.setEnutype(actionRequest.getFieldValue(i,"enutype"));} else {this.setEnutype(null);}
if ( actionRequest.getFieldValue(i,"enuitemvalue") !=null ) {this.setEnuitemvalue(actionRequest.getFieldValue(i,"enuitemvalue"));} else {this.setEnuitemvalue(null);}
if ( actionRequest.getFieldValue(i,"enuitemlabel") !=null ) {this.setEnuitemlabel(actionRequest.getFieldValue(i,"enuitemlabel"));} else {this.setEnuitemlabel(null);}
if ( actionRequest.getFieldValue(i,"enuitemdesc") !=null ) {this.setEnuitemdesc(actionRequest.getFieldValue(i,"enuitemdesc"));} else {this.setEnuitemdesc(null);}
if ( actionRequest.getFieldValue(i,"dispno") !=null && actionRequest.getFieldValue(i,"dispno").trim().length() > 0 ) {this.setDispno(Integer.parseInt(actionRequest.getFieldValue(i,"dispno")));} else {this.setDispno(0);}
if ( actionRequest.getFieldValue(i,"enuitemexpand") !=null ) {this.setEnuitemexpand(actionRequest.getFieldValue(i,"enuitemexpand"));} else {this.setEnuitemexpand(null);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PtEnuDetailBean obj = (PtEnuDetailBean)super.clone();obj.setEnutype(obj.enutype);
obj.setEnuitemvalue(obj.enuitemvalue);
obj.setEnuitemlabel(obj.enuitemlabel);
obj.setEnuitemdesc(obj.enuitemdesc);
obj.setDispno(obj.dispno);
obj.setEnuitemexpand(obj.enuitemexpand);
return obj;}}