package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PtEnuMainBean extends AbstractBasicBean implements Cloneable {
String enutype;
String enuname;
String valuetype;
String enudesc;
public static final String TABLENAME ="ptenumain";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PtEnuMainBean abb = new PtEnuMainBean();
abb.enutype=rs.getString("enutype");
abb.enuname=rs.getString("enuname");
abb.valuetype=rs.getString("valuetype");
abb.enudesc=rs.getString("enudesc");
list.add(abb);
}public String getEnutype() { if ( this.enutype == null ) return ""; return this.enutype;}
public String getEnuname() { if ( this.enuname == null ) return ""; return this.enuname;}
public String getValuetype() { if ( this.valuetype == null ) return ""; return this.valuetype;}
public String getEnudesc() { if ( this.enudesc == null ) return ""; return this.enudesc;}
public void setEnutype(String enutype) { sqlMaker.setField("enutype",enutype,Field.TEXT);this.enutype=enutype;}
public void setEnuname(String enuname) { sqlMaker.setField("enuname",enuname,Field.TEXT);this.enuname=enuname;}
public void setValuetype(String valuetype) { sqlMaker.setField("valuetype",valuetype,Field.TEXT);this.valuetype=valuetype;}
public void setEnudesc(String enudesc) { sqlMaker.setField("enudesc",enudesc,Field.TEXT);this.enudesc=enudesc;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"enutype") !=null ) {this.setEnutype(actionRequest.getFieldValue(i,"enutype"));}
if ( actionRequest.getFieldValue(i,"enuname") !=null ) {this.setEnuname(actionRequest.getFieldValue(i,"enuname"));}
if ( actionRequest.getFieldValue(i,"valuetype") !=null ) {this.setValuetype(actionRequest.getFieldValue(i,"valuetype"));}
if ( actionRequest.getFieldValue(i,"enudesc") !=null ) {this.setEnudesc(actionRequest.getFieldValue(i,"enudesc"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"enutype") !=null ) {this.setEnutype(actionRequest.getFieldValue(i,"enutype"));} else {this.setEnutype(null);}
if ( actionRequest.getFieldValue(i,"enuname") !=null ) {this.setEnuname(actionRequest.getFieldValue(i,"enuname"));} else {this.setEnuname(null);}
if ( actionRequest.getFieldValue(i,"valuetype") !=null ) {this.setValuetype(actionRequest.getFieldValue(i,"valuetype"));} else {this.setValuetype(null);}
if ( actionRequest.getFieldValue(i,"enudesc") !=null ) {this.setEnudesc(actionRequest.getFieldValue(i,"enudesc"));} else {this.setEnudesc(null);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PtEnuMainBean obj = (PtEnuMainBean)super.clone();obj.setEnutype(obj.enutype);
obj.setEnuname(obj.enuname);
obj.setValuetype(obj.valuetype);
obj.setEnudesc(obj.enudesc);
return obj;}}