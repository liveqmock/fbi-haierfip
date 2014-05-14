package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PtCmCodeBean extends AbstractBasicBean implements Cloneable {
String codtp;
String langtp;
int codsq;
String valtp;
String valstr;
String coddesc;
public static final String TABLENAME ="ptcmcode";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PtCmCodeBean abb = new PtCmCodeBean();
abb.codtp=rs.getString("codtp");
abb.langtp=rs.getString("langtp");
abb.codsq=rs.getInt("codsq");
abb.valtp=rs.getString("valtp");
abb.valstr=rs.getString("valstr");
abb.coddesc=rs.getString("coddesc");
list.add(abb);
}public String getCodtp() { if ( this.codtp == null ) return ""; return this.codtp;}
public String getLangtp() { if ( this.langtp == null ) return ""; return this.langtp;}
public int getCodsq() { return this.codsq;}
public String getValtp() { if ( this.valtp == null ) return ""; return this.valtp;}
public String getValstr() { if ( this.valstr == null ) return ""; return this.valstr;}
public String getCoddesc() { if ( this.coddesc == null ) return ""; return this.coddesc;}
public void setCodtp(String codtp) { sqlMaker.setField("codtp",codtp,Field.TEXT);this.codtp=codtp;}
public void setLangtp(String langtp) { sqlMaker.setField("langtp",langtp,Field.TEXT);this.langtp=langtp;}
public void setCodsq(int codsq) { sqlMaker.setField("codsq",""+codsq,Field.NUMBER);this.codsq=codsq;}
public void setValtp(String valtp) { sqlMaker.setField("valtp",valtp,Field.TEXT);this.valtp=valtp;}
public void setValstr(String valstr) { sqlMaker.setField("valstr",valstr,Field.TEXT);this.valstr=valstr;}
public void setCoddesc(String coddesc) { sqlMaker.setField("coddesc",coddesc,Field.TEXT);this.coddesc=coddesc;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"codtp") !=null ) {this.setCodtp(actionRequest.getFieldValue(i,"codtp"));}
if ( actionRequest.getFieldValue(i,"langtp") !=null ) {this.setLangtp(actionRequest.getFieldValue(i,"langtp"));}
if ( actionRequest.getFieldValue(i,"codsq") !=null && actionRequest.getFieldValue(i,"codsq").trim().length() > 0 ) {this.setCodsq(Integer.parseInt(actionRequest.getFieldValue(i,"codsq")));}
if ( actionRequest.getFieldValue(i,"valtp") !=null ) {this.setValtp(actionRequest.getFieldValue(i,"valtp"));}
if ( actionRequest.getFieldValue(i,"valstr") !=null ) {this.setValstr(actionRequest.getFieldValue(i,"valstr"));}
if ( actionRequest.getFieldValue(i,"coddesc") !=null ) {this.setCoddesc(actionRequest.getFieldValue(i,"coddesc"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"codtp") !=null ) {this.setCodtp(actionRequest.getFieldValue(i,"codtp"));} else {this.setCodtp(null);}
if ( actionRequest.getFieldValue(i,"langtp") !=null ) {this.setLangtp(actionRequest.getFieldValue(i,"langtp"));} else {this.setLangtp(null);}
if ( actionRequest.getFieldValue(i,"codsq") !=null && actionRequest.getFieldValue(i,"codsq").trim().length() > 0 ) {this.setCodsq(Integer.parseInt(actionRequest.getFieldValue(i,"codsq")));} else {this.setCodsq(0);}
if ( actionRequest.getFieldValue(i,"valtp") !=null ) {this.setValtp(actionRequest.getFieldValue(i,"valtp"));} else {this.setValtp(null);}
if ( actionRequest.getFieldValue(i,"valstr") !=null ) {this.setValstr(actionRequest.getFieldValue(i,"valstr"));} else {this.setValstr(null);}
if ( actionRequest.getFieldValue(i,"coddesc") !=null ) {this.setCoddesc(actionRequest.getFieldValue(i,"coddesc"));} else {this.setCoddesc(null);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PtCmCodeBean obj = (PtCmCodeBean)super.clone();obj.setCodtp(obj.codtp);
obj.setLangtp(obj.langtp);
obj.setCodsq(obj.codsq);
obj.setValtp(obj.valtp);
obj.setValstr(obj.valstr);
obj.setCoddesc(obj.coddesc);
return obj;}}