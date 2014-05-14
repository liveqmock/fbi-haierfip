package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PterrorBean extends AbstractBasicBean implements Cloneable {
String moudleid;
String errortype;
String errorindex;
String message;
public static final String TABLENAME ="pterror";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PterrorBean abb = new PterrorBean();
abb.moudleid=rs.getString("moudleid");
abb.errortype=rs.getString("errortype");
abb.errorindex=rs.getString("errorindex");
abb.message=rs.getString("message");
list.add(abb);
}public String getMoudleid() { if ( this.moudleid == null ) return ""; return this.moudleid;}
public String getErrortype() { if ( this.errortype == null ) return ""; return this.errortype;}
public String getErrorindex() { if ( this.errorindex == null ) return ""; return this.errorindex;}
public String getMessage() { if ( this.message == null ) return ""; return this.message;}
public void setMoudleid(String moudleid) { sqlMaker.setField("moudleid",moudleid,Field.TEXT);this.moudleid=moudleid;}
public void setErrortype(String errortype) { sqlMaker.setField("errortype",errortype,Field.TEXT);this.errortype=errortype;}
public void setErrorindex(String errorindex) { sqlMaker.setField("errorindex",errorindex,Field.TEXT);this.errorindex=errorindex;}
public void setMessage(String message) { sqlMaker.setField("message",message,Field.TEXT);this.message=message;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"moudleid") !=null ) {this.setMoudleid(actionRequest.getFieldValue(i,"moudleid"));}
if ( actionRequest.getFieldValue(i,"errortype") !=null ) {this.setErrortype(actionRequest.getFieldValue(i,"errortype"));}
if ( actionRequest.getFieldValue(i,"errorindex") !=null ) {this.setErrorindex(actionRequest.getFieldValue(i,"errorindex"));}
if ( actionRequest.getFieldValue(i,"message") !=null ) {this.setMessage(actionRequest.getFieldValue(i,"message"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"moudleid") !=null ) {this.setMoudleid(actionRequest.getFieldValue(i,"moudleid"));} else {this.setMoudleid(null);}
if ( actionRequest.getFieldValue(i,"errortype") !=null ) {this.setErrortype(actionRequest.getFieldValue(i,"errortype"));} else {this.setErrortype(null);}
if ( actionRequest.getFieldValue(i,"errorindex") !=null ) {this.setErrorindex(actionRequest.getFieldValue(i,"errorindex"));} else {this.setErrorindex(null);}
if ( actionRequest.getFieldValue(i,"message") !=null ) {this.setMessage(actionRequest.getFieldValue(i,"message"));} else {this.setMessage(null);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PterrorBean obj = (PterrorBean)super.clone();obj.setMoudleid(obj.moudleid);
obj.setErrortype(obj.errortype);
obj.setErrorindex(obj.errorindex);
obj.setMessage(obj.message);
return obj;}}