package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PtRoleBean extends AbstractBasicBean implements Cloneable {
String roleid;
String roledesc;
String status;
public static final String TABLENAME ="ptrole";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PtRoleBean abb = new PtRoleBean();
abb.roleid=rs.getString("roleid");
abb.roledesc=rs.getString("roledesc");
abb.status=rs.getString("status");
list.add(abb);
}public String getRoleid() { if ( this.roleid == null ) return ""; return this.roleid;}
public String getRoledesc() { if ( this.roledesc == null ) return ""; return this.roledesc;}
public String getStatus() { if ( this.status == null ) return ""; return this.status;}
public void setRoleid(String roleid) { sqlMaker.setField("roleid",roleid,Field.TEXT);this.roleid=roleid;}
public void setRoledesc(String roledesc) { sqlMaker.setField("roledesc",roledesc,Field.TEXT);this.roledesc=roledesc;}
public void setStatus(String status) { sqlMaker.setField("status",status,Field.TEXT);this.status=status;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));}
if ( actionRequest.getFieldValue(i,"roledesc") !=null ) {this.setRoledesc(actionRequest.getFieldValue(i,"roledesc"));}
if ( actionRequest.getFieldValue(i,"status") !=null ) {this.setStatus(actionRequest.getFieldValue(i,"status"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));} else {this.setRoleid(null);}
if ( actionRequest.getFieldValue(i,"roledesc") !=null ) {this.setRoledesc(actionRequest.getFieldValue(i,"roledesc"));} else {this.setRoledesc(null);}
if ( actionRequest.getFieldValue(i,"status") !=null ) {this.setStatus(actionRequest.getFieldValue(i,"status"));} else {this.setStatus(null);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PtRoleBean obj = (PtRoleBean)super.clone();obj.setRoleid(obj.roleid);
obj.setRoledesc(obj.roledesc);
obj.setStatus(obj.status);
return obj;}}