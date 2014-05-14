package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTLOGICACT extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTLOGICACT().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTLOGICACT().findAndLockByWhere(sSqlWhere);      }       public static PTLOGICACT findFirst(String sSqlWhere) {           return (PTLOGICACT)new PTLOGICACT().findFirstByWhere(sSqlWhere);      }       public static PTLOGICACT findFirstAndLock(String sSqlWhere) {           return (PTLOGICACT)new PTLOGICACT().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTLOGICACT().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTLOGICACT bean = new PTLOGICACT();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTLOGICACT bean = new PTLOGICACT();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTLOGICACT findFirst(String sSqlWhere,boolean isAutoRelease) {           PTLOGICACT bean = new PTLOGICACT();           bean.setAutoRelease(isAutoRelease);           return (PTLOGICACT)bean.findFirstByWhere(sSqlWhere);      }       public static PTLOGICACT findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTLOGICACT bean = new PTLOGICACT();           bean.setAutoRelease(isAutoRelease);           return (PTLOGICACT)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTLOGICACT bean = new PTLOGICACT();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTLOGICACT().findByWhereByRow(sSqlWhere,row);      } String logiccode;
String logicclass;
String logicmethod;
String logicdesc;
String logicenabled;
public static final String TABLENAME ="ptlogicact";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTLOGICACT abb = new PTLOGICACT();
abb.logiccode=rs.getString("logiccode");abb.setKeyValue("LOGICCODE",""+abb.getLogiccode());
abb.logicclass=rs.getString("logicclass");abb.setKeyValue("LOGICCLASS",""+abb.getLogicclass());
abb.logicmethod=rs.getString("logicmethod");abb.setKeyValue("LOGICMETHOD",""+abb.getLogicmethod());
abb.logicdesc=rs.getString("logicdesc");abb.setKeyValue("LOGICDESC",""+abb.getLogicdesc());
abb.logicenabled=rs.getString("logicenabled");abb.setKeyValue("LOGICENABLED",""+abb.getLogicenabled());
list.add(abb);
abb.operate_mode = "edit";
}public String getLogiccode() { if ( this.logiccode == null ) return ""; return this.logiccode;}
public String getLogicclass() { if ( this.logicclass == null ) return ""; return this.logicclass;}
public String getLogicmethod() { if ( this.logicmethod == null ) return ""; return this.logicmethod;}
public String getLogicdesc() { if ( this.logicdesc == null ) return ""; return this.logicdesc;}
public String getLogicenabled() { if ( this.logicenabled == null ) return ""; return this.logicenabled;}
public void setLogiccode(String logiccode) { sqlMaker.setField("logiccode",logiccode,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getLogiccode().equals(logiccode)) cf.add("logiccode",this.logiccode,logiccode); } this.logiccode=logiccode;}
public void setLogicclass(String logicclass) { sqlMaker.setField("logicclass",logicclass,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getLogicclass().equals(logicclass)) cf.add("logicclass",this.logicclass,logicclass); } this.logicclass=logicclass;}
public void setLogicmethod(String logicmethod) { sqlMaker.setField("logicmethod",logicmethod,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getLogicmethod().equals(logicmethod)) cf.add("logicmethod",this.logicmethod,logicmethod); } this.logicmethod=logicmethod;}
public void setLogicdesc(String logicdesc) { sqlMaker.setField("logicdesc",logicdesc,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getLogicdesc().equals(logicdesc)) cf.add("logicdesc",this.logicdesc,logicdesc); } this.logicdesc=logicdesc;}
public void setLogicenabled(String logicenabled) { sqlMaker.setField("logicenabled",logicenabled,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getLogicenabled().equals(logicenabled)) cf.add("logicenabled",this.logicenabled,logicenabled); } this.logicenabled=logicenabled;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"logiccode") !=null ) {this.setLogiccode(actionRequest.getFieldValue(i,"logiccode"));}
if ( actionRequest.getFieldValue(i,"logicclass") !=null ) {this.setLogicclass(actionRequest.getFieldValue(i,"logicclass"));}
if ( actionRequest.getFieldValue(i,"logicmethod") !=null ) {this.setLogicmethod(actionRequest.getFieldValue(i,"logicmethod"));}
if ( actionRequest.getFieldValue(i,"logicdesc") !=null ) {this.setLogicdesc(actionRequest.getFieldValue(i,"logicdesc"));}
if ( actionRequest.getFieldValue(i,"logicenabled") !=null ) {this.setLogicenabled(actionRequest.getFieldValue(i,"logicenabled"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTLOGICACT obj = (PTLOGICACT)super.clone();obj.setLogiccode(obj.logiccode);
obj.setLogicclass(obj.logicclass);
obj.setLogicmethod(obj.logicmethod);
obj.setLogicdesc(obj.logicdesc);
obj.setLogicenabled(obj.logicenabled);
return obj;}}