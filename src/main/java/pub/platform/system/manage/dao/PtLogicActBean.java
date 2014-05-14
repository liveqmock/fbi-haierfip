package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PtLogicActBean extends AbstractBasicBean implements Cloneable {
String logiccode;
String logicclass;
String logicmethod;
String logicdesc;
String logicenabled;
public static final String TABLENAME ="ptlogicact";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PtLogicActBean abb = new PtLogicActBean();
abb.logiccode=rs.getString("logiccode");
abb.logicclass=rs.getString("logicclass");
abb.logicmethod=rs.getString("logicmethod");
abb.logicdesc=rs.getString("logicdesc");
abb.logicenabled=rs.getString("logicenabled");
list.add(abb);
}public String getLogiccode() { if ( this.logiccode == null ) return ""; return this.logiccode;}
public String getLogicclass() { if ( this.logicclass == null ) return ""; return this.logicclass;}
public String getLogicmethod() { if ( this.logicmethod == null ) return ""; return this.logicmethod;}
public String getLogicdesc() { if ( this.logicdesc == null ) return ""; return this.logicdesc;}
public String getLogicenabled() { if ( this.logicenabled == null ) return ""; return this.logicenabled;}
public void setLogiccode(String logiccode) { sqlMaker.setField("logiccode",logiccode,Field.TEXT);this.logiccode=logiccode;}
public void setLogicclass(String logicclass) { sqlMaker.setField("logicclass",logicclass,Field.TEXT);this.logicclass=logicclass;}
public void setLogicmethod(String logicmethod) { sqlMaker.setField("logicmethod",logicmethod,Field.TEXT);this.logicmethod=logicmethod;}
public void setLogicdesc(String logicdesc) { sqlMaker.setField("logicdesc",logicdesc,Field.TEXT);this.logicdesc=logicdesc;}
public void setLogicenabled(String logicenabled) { sqlMaker.setField("logicenabled",logicenabled,Field.TEXT);this.logicenabled=logicenabled;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"logiccode") !=null ) {this.setLogiccode(actionRequest.getFieldValue(i,"logiccode"));}
if ( actionRequest.getFieldValue(i,"logicclass") !=null ) {this.setLogicclass(actionRequest.getFieldValue(i,"logicclass"));}
if ( actionRequest.getFieldValue(i,"logicmethod") !=null ) {this.setLogicmethod(actionRequest.getFieldValue(i,"logicmethod"));}
if ( actionRequest.getFieldValue(i,"logicdesc") !=null ) {this.setLogicdesc(actionRequest.getFieldValue(i,"logicdesc"));}
if ( actionRequest.getFieldValue(i,"logicenabled") !=null ) {this.setLogicenabled(actionRequest.getFieldValue(i,"logicenabled"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"logiccode") !=null ) {this.setLogiccode(actionRequest.getFieldValue(i,"logiccode"));} else {this.setLogiccode(null);}
if ( actionRequest.getFieldValue(i,"logicclass") !=null ) {this.setLogicclass(actionRequest.getFieldValue(i,"logicclass"));} else {this.setLogicclass(null);}
if ( actionRequest.getFieldValue(i,"logicmethod") !=null ) {this.setLogicmethod(actionRequest.getFieldValue(i,"logicmethod"));} else {this.setLogicmethod(null);}
if ( actionRequest.getFieldValue(i,"logicdesc") !=null ) {this.setLogicdesc(actionRequest.getFieldValue(i,"logicdesc"));} else {this.setLogicdesc(null);}
if ( actionRequest.getFieldValue(i,"logicenabled") !=null ) {this.setLogicenabled(actionRequest.getFieldValue(i,"logicenabled"));} else {this.setLogicenabled(null);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PtLogicActBean obj = (PtLogicActBean)super.clone();obj.setLogiccode(obj.logiccode);
obj.setLogicclass(obj.logicclass);
obj.setLogicmethod(obj.logicmethod);
obj.setLogicdesc(obj.logicdesc);
obj.setLogicenabled(obj.logicenabled);
return obj;}}