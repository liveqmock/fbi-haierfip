package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PtOperRoleBean extends AbstractBasicBean implements Cloneable {
String roleid;
String operid;
public static final String TABLENAME ="ptoperrole";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PtOperRoleBean abb = new PtOperRoleBean();
abb.roleid=rs.getString("roleid");
abb.operid=rs.getString("operid");
list.add(abb);
}public String getRoleid() { if ( this.roleid == null ) return ""; return this.roleid;}
public String getOperid() { if ( this.operid == null ) return ""; return this.operid;}
public void setRoleid(String roleid) { sqlMaker.setField("roleid",roleid,Field.TEXT);this.roleid=roleid;}
public void setOperid(String operid) { sqlMaker.setField("operid",operid,Field.TEXT);this.operid=operid;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));}
if ( actionRequest.getFieldValue(i,"operid") !=null ) {this.setOperid(actionRequest.getFieldValue(i,"operid"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));} else {this.setRoleid(null);}
if ( actionRequest.getFieldValue(i,"operid") !=null ) {this.setOperid(actionRequest.getFieldValue(i,"operid"));} else {this.setOperid(null);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PtOperRoleBean obj = (PtOperRoleBean)super.clone();obj.setRoleid(obj.roleid);
obj.setOperid(obj.operid);
return obj;}}