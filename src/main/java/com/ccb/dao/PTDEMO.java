package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTDEMO extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTDEMO().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTDEMO().findAndLockByWhere(sSqlWhere);      }       public static PTDEMO findFirst(String sSqlWhere) {           return (PTDEMO)new PTDEMO().findFirstByWhere(sSqlWhere);      }       public static PTDEMO findFirstAndLock(String sSqlWhere) {           return (PTDEMO)new PTDEMO().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTDEMO().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTDEMO bean = new PTDEMO();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTDEMO bean = new PTDEMO();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTDEMO findFirst(String sSqlWhere,boolean isAutoRelease) {           PTDEMO bean = new PTDEMO();           bean.setAutoRelease(isAutoRelease);           return (PTDEMO)bean.findFirstByWhere(sSqlWhere);      }       public static PTDEMO findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTDEMO bean = new PTDEMO();           bean.setAutoRelease(isAutoRelease);           return (PTDEMO)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTDEMO bean = new PTDEMO();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTDEMO().findByWhereByRow(sSqlWhere,row);      } String file_id;
int file_namelength;
String file_name;
String file_type;
String file_childtype;
String file_detailtype;
String file_extend;
int file_userid;
String file_deptid;
String file_date;
String file_lastdate;
String isvisibled;
String impawnnoreason;
public static final String TABLENAME ="ptdemo";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTDEMO abb = new PTDEMO();
abb.file_id=rs.getString("file_id");abb.setKeyValue("FILE_ID",""+abb.getFile_id());
abb.file_namelength=rs.getInt("file_namelength");abb.setKeyValue("FILE_NAMELENGTH",""+abb.getFile_namelength());
abb.file_name=rs.getString("file_name");abb.setKeyValue("FILE_NAME",""+abb.getFile_name());
abb.file_type=rs.getString("file_type");abb.setKeyValue("FILE_TYPE",""+abb.getFile_type());
abb.file_childtype=rs.getString("file_childtype");abb.setKeyValue("FILE_CHILDTYPE",""+abb.getFile_childtype());
abb.file_detailtype=rs.getString("file_detailtype");abb.setKeyValue("FILE_DETAILTYPE",""+abb.getFile_detailtype());
abb.file_extend=rs.getString("file_extend");abb.setKeyValue("FILE_EXTEND",""+abb.getFile_extend());
abb.file_userid=rs.getInt("file_userid");abb.setKeyValue("FILE_USERID",""+abb.getFile_userid());
abb.file_deptid=rs.getString("file_deptid");abb.setKeyValue("FILE_DEPTID",""+abb.getFile_deptid());
abb.file_date=rs.getTimeString("file_date");abb.setKeyValue("FILE_DATE",""+abb.getFile_date());
abb.file_lastdate=rs.getTimeString("file_lastdate");abb.setKeyValue("FILE_LASTDATE",""+abb.getFile_lastdate());
abb.isvisibled=rs.getString("isvisibled");abb.setKeyValue("ISVISIBLED",""+abb.getIsvisibled());
abb.impawnnoreason=rs.getString("impawnnoreason");abb.setKeyValue("IMPAWNNOREASON",""+abb.getImpawnnoreason());
list.add(abb);
abb.operate_mode = "edit";
}public String getFile_id() { if ( this.file_id == null ) return ""; return this.file_id;}
public int getFile_namelength() { return this.file_namelength;}
public String getFile_name() { if ( this.file_name == null ) return ""; return this.file_name;}
public String getFile_type() { if ( this.file_type == null ) return ""; return this.file_type;}
public String getFile_childtype() { if ( this.file_childtype == null ) return ""; return this.file_childtype;}
public String getFile_detailtype() { if ( this.file_detailtype == null ) return ""; return this.file_detailtype;}
public String getFile_extend() { if ( this.file_extend == null ) return ""; return this.file_extend;}
public int getFile_userid() { return this.file_userid;}
public String getFile_deptid() { if ( this.file_deptid == null ) return ""; return this.file_deptid;}
public String getFile_date() {  if ( this.file_date == null ) { return ""; } else { return this.file_date.trim().split(" ")[0];} }public String getFile_dateTime() {  if ( this.file_date == null ) return ""; return this.file_date.split("\\.")[0];}
public String getFile_lastdate() {  if ( this.file_lastdate == null ) { return ""; } else { return this.file_lastdate.trim().split(" ")[0];} }public String getFile_lastdateTime() {  if ( this.file_lastdate == null ) return ""; return this.file_lastdate.split("\\.")[0];}
public String getIsvisibled() { if ( this.isvisibled == null ) return ""; return this.isvisibled;}
public String getImpawnnoreason() { if ( this.impawnnoreason == null ) return ""; return this.impawnnoreason;}
public void setFile_id(String file_id) { sqlMaker.setField("file_id",file_id,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFile_id().equals(file_id)) cf.add("file_id",this.file_id,file_id); } this.file_id=file_id;}
public void setFile_namelength(int file_namelength) { sqlMaker.setField("file_namelength",""+file_namelength,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFile_namelength()!=file_namelength) cf.add("file_namelength",this.file_namelength+"",file_namelength+""); } this.file_namelength=file_namelength;}
public void setFile_name(String file_name) { sqlMaker.setField("file_name",file_name,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFile_name().equals(file_name)) cf.add("file_name",this.file_name,file_name); } this.file_name=file_name;}
public void setFile_type(String file_type) { sqlMaker.setField("file_type",file_type,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFile_type().equals(file_type)) cf.add("file_type",this.file_type,file_type); } this.file_type=file_type;}
public void setFile_childtype(String file_childtype) { sqlMaker.setField("file_childtype",file_childtype,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFile_childtype().equals(file_childtype)) cf.add("file_childtype",this.file_childtype,file_childtype); } this.file_childtype=file_childtype;}
public void setFile_detailtype(String file_detailtype) { sqlMaker.setField("file_detailtype",file_detailtype,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFile_detailtype().equals(file_detailtype)) cf.add("file_detailtype",this.file_detailtype,file_detailtype); } this.file_detailtype=file_detailtype;}
public void setFile_extend(String file_extend) { sqlMaker.setField("file_extend",file_extend,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFile_extend().equals(file_extend)) cf.add("file_extend",this.file_extend,file_extend); } this.file_extend=file_extend;}
public void setFile_userid(int file_userid) { sqlMaker.setField("file_userid",""+file_userid,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFile_userid()!=file_userid) cf.add("file_userid",this.file_userid+"",file_userid+""); } this.file_userid=file_userid;}
public void setFile_deptid(String file_deptid) { sqlMaker.setField("file_deptid",file_deptid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFile_deptid().equals(file_deptid)) cf.add("file_deptid",this.file_deptid,file_deptid); } this.file_deptid=file_deptid;}
public void setFile_date(String file_date) { sqlMaker.setField("file_date",file_date,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getFile_date().equals(file_date)) cf.add("file_date",this.file_date,file_date); } this.file_date=file_date;}
public void setFile_lastdate(String file_lastdate) { sqlMaker.setField("file_lastdate",file_lastdate,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getFile_lastdate().equals(file_lastdate)) cf.add("file_lastdate",this.file_lastdate,file_lastdate); } this.file_lastdate=file_lastdate;}
public void setIsvisibled(String isvisibled) { sqlMaker.setField("isvisibled",isvisibled,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getIsvisibled().equals(isvisibled)) cf.add("isvisibled",this.isvisibled,isvisibled); } this.isvisibled=isvisibled;}
public void setImpawnnoreason(String impawnnoreason) { sqlMaker.setField("impawnnoreason",impawnnoreason,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getImpawnnoreason().equals(impawnnoreason)) cf.add("impawnnoreason",this.impawnnoreason,impawnnoreason); } this.impawnnoreason=impawnnoreason;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"file_id") !=null ) {this.setFile_id(actionRequest.getFieldValue(i,"file_id"));}
if ( actionRequest.getFieldValue(i,"file_namelength") !=null && actionRequest.getFieldValue(i,"file_namelength").trim().length() > 0 ) {this.setFile_namelength(Integer.parseInt(actionRequest.getFieldValue(i,"file_namelength")));}
if ( actionRequest.getFieldValue(i,"file_name") !=null ) {this.setFile_name(actionRequest.getFieldValue(i,"file_name"));}
if ( actionRequest.getFieldValue(i,"file_type") !=null ) {this.setFile_type(actionRequest.getFieldValue(i,"file_type"));}
if ( actionRequest.getFieldValue(i,"file_childtype") !=null ) {this.setFile_childtype(actionRequest.getFieldValue(i,"file_childtype"));}
if ( actionRequest.getFieldValue(i,"file_detailtype") !=null ) {this.setFile_detailtype(actionRequest.getFieldValue(i,"file_detailtype"));}
if ( actionRequest.getFieldValue(i,"file_extend") !=null ) {this.setFile_extend(actionRequest.getFieldValue(i,"file_extend"));}
if ( actionRequest.getFieldValue(i,"file_userid") !=null && actionRequest.getFieldValue(i,"file_userid").trim().length() > 0 ) {this.setFile_userid(Integer.parseInt(actionRequest.getFieldValue(i,"file_userid")));}
if ( actionRequest.getFieldValue(i,"file_deptid") !=null ) {this.setFile_deptid(actionRequest.getFieldValue(i,"file_deptid"));}
if ( actionRequest.getFieldValue(i,"file_date") !=null ) {this.setFile_date(actionRequest.getFieldValue(i,"file_date"));}
if ( actionRequest.getFieldValue(i,"file_lastdate") !=null ) {this.setFile_lastdate(actionRequest.getFieldValue(i,"file_lastdate"));}
if ( actionRequest.getFieldValue(i,"isvisibled") !=null ) {this.setIsvisibled(actionRequest.getFieldValue(i,"isvisibled"));}
if ( actionRequest.getFieldValue(i,"impawnnoreason") !=null ) {this.setImpawnnoreason(actionRequest.getFieldValue(i,"impawnnoreason"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTDEMO obj = (PTDEMO)super.clone();obj.setFile_id(obj.file_id);
obj.setFile_namelength(obj.file_namelength);
obj.setFile_name(obj.file_name);
obj.setFile_type(obj.file_type);
obj.setFile_childtype(obj.file_childtype);
obj.setFile_detailtype(obj.file_detailtype);
obj.setFile_extend(obj.file_extend);
obj.setFile_userid(obj.file_userid);
obj.setFile_deptid(obj.file_deptid);
obj.setFile_date(obj.file_date);
obj.setFile_lastdate(obj.file_lastdate);
obj.setIsvisibled(obj.isvisibled);
obj.setImpawnnoreason(obj.impawnnoreason);
return obj;}}