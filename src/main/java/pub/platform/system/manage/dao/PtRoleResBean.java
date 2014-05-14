package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PtRoleResBean extends AbstractBasicBean implements Cloneable {
String resid;
String roleid;
public static final String TABLENAME ="ptroleres";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PtRoleResBean abb = new PtRoleResBean();
abb.resid=rs.getString("resid");
abb.roleid=rs.getString("roleid");
list.add(abb);
}public String getResid() { if ( this.resid == null ) return ""; return this.resid;}
public String getRoleid() { if ( this.roleid == null ) return ""; return this.roleid;}
public void setResid(String resid) { sqlMaker.setField("resid",resid,Field.TEXT);this.resid=resid;}
public void setRoleid(String roleid) { sqlMaker.setField("roleid",roleid,Field.TEXT);this.roleid=roleid;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"resid") !=null ) {this.setResid(actionRequest.getFieldValue(i,"resid"));}
if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"resid") !=null ) {this.setResid(actionRequest.getFieldValue(i,"resid"));} else {this.setResid(null);}
if ( actionRequest.getFieldValue(i,"roleid") !=null ) {this.setRoleid(actionRequest.getFieldValue(i,"roleid"));} else {this.setRoleid(null);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PtRoleResBean obj = (PtRoleResBean)super.clone();obj.setResid(obj.resid);
obj.setRoleid(obj.roleid);
return obj;}}