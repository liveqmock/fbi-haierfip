package com.ccb.dao;
import java.util.*;
import pub.platform.db.*;
import pub.platform.utils.*;
import pub.platform.db.AbstractBasicBean;
public class PTMENU extends AbstractBasicBean implements Cloneable {
     public static List find(String sSqlWhere) {           return new PTMENU().findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere) {           return new PTMENU().findAndLockByWhere(sSqlWhere);      }       public static PTMENU findFirst(String sSqlWhere) {           return (PTMENU)new PTMENU().findFirstByWhere(sSqlWhere);      }       public static PTMENU findFirstAndLock(String sSqlWhere) {           return (PTMENU)new PTMENU().findFirstAndLockByWhere(sSqlWhere);      }            public static RecordSet findRecordSet(String sSqlWhere) {           return new PTMENU().findRecordSetByWhere(sSqlWhere);      }       public static List find(String sSqlWhere,boolean isAutoRelease) {           PTMENU bean = new PTMENU();           bean.setAutoRelease(isAutoRelease);           return bean.findByWhere(sSqlWhere);      }       public static List findAndLock(String sSqlWhere,boolean isAutoRelease) {           PTMENU bean = new PTMENU();           bean.setAutoRelease(isAutoRelease);           return bean.findAndLockByWhere(sSqlWhere);      }       public static PTMENU findFirst(String sSqlWhere,boolean isAutoRelease) {           PTMENU bean = new PTMENU();           bean.setAutoRelease(isAutoRelease);           return (PTMENU)bean.findFirstByWhere(sSqlWhere);      }       public static PTMENU findFirstAndLock(String sSqlWhere,boolean isAutoRelease) {           PTMENU bean = new PTMENU();           bean.setAutoRelease(isAutoRelease);           return (PTMENU)bean.findFirstAndLockByWhere(sSqlWhere);      }       public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) {           PTMENU bean = new PTMENU();           bean.setAutoRelease(isAutoRelease);           return bean.findRecordSetByWhere(sSqlWhere);      }      public static List findByRow(String sSqlWhere,int row) {           return new PTMENU().findByWhereByRow(sSqlWhere,row);      } String menuid;
String parentmenuid;
int menulevel;
int isleaf;
String menudesc;
String menulabel;
String menuicon;
String openicon;
String targetmachine;
String menuaction;
String openwindow;
int windowwidth;
int windowheight;
int levelidx;
String fillstr50;
String fillstr100;
int fillint1;
double filldbl2;
String filldate;
public static final String TABLENAME ="ptmenu";
private String operate_mode = "add";
public ChangeFileds cf = new ChangeFileds();
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PTMENU abb = new PTMENU();
abb.menuid=rs.getString("menuid");abb.setKeyValue("MENUID",""+abb.getMenuid());
abb.parentmenuid=rs.getString("parentmenuid");abb.setKeyValue("PARENTMENUID",""+abb.getParentmenuid());
abb.menulevel=rs.getInt("menulevel");abb.setKeyValue("MENULEVEL",""+abb.getMenulevel());
abb.isleaf=rs.getInt("isleaf");abb.setKeyValue("ISLEAF",""+abb.getIsleaf());
abb.menudesc=rs.getString("menudesc");abb.setKeyValue("MENUDESC",""+abb.getMenudesc());
abb.menulabel=rs.getString("menulabel");abb.setKeyValue("MENULABEL",""+abb.getMenulabel());
abb.menuicon=rs.getString("menuicon");abb.setKeyValue("MENUICON",""+abb.getMenuicon());
abb.openicon=rs.getString("openicon");abb.setKeyValue("OPENICON",""+abb.getOpenicon());
abb.targetmachine=rs.getString("targetmachine");abb.setKeyValue("TARGETMACHINE",""+abb.getTargetmachine());
abb.menuaction=rs.getString("menuaction");abb.setKeyValue("MENUACTION",""+abb.getMenuaction());
abb.openwindow=rs.getString("openwindow");abb.setKeyValue("OPENWINDOW",""+abb.getOpenwindow());
abb.windowwidth=rs.getInt("windowwidth");abb.setKeyValue("WINDOWWIDTH",""+abb.getWindowwidth());
abb.windowheight=rs.getInt("windowheight");abb.setKeyValue("WINDOWHEIGHT",""+abb.getWindowheight());
abb.levelidx=rs.getInt("levelidx");abb.setKeyValue("LEVELIDX",""+abb.getLevelidx());
abb.fillstr50=rs.getString("fillstr50");abb.setKeyValue("FILLSTR50",""+abb.getFillstr50());
abb.fillstr100=rs.getString("fillstr100");abb.setKeyValue("FILLSTR100",""+abb.getFillstr100());
abb.fillint1=rs.getInt("fillint1");abb.setKeyValue("FILLINT1",""+abb.getFillint1());
abb.filldbl2=rs.getDouble("filldbl2");abb.setKeyValue("FILLDBL2",""+abb.getFilldbl2());
abb.filldate=rs.getTimeString("filldate");abb.setKeyValue("FILLDATE",""+abb.getFilldate());
list.add(abb);
abb.operate_mode = "edit";
}public String getMenuid() { if ( this.menuid == null ) return ""; return this.menuid;}
public String getParentmenuid() { if ( this.parentmenuid == null ) return ""; return this.parentmenuid;}
public int getMenulevel() { return this.menulevel;}
public int getIsleaf() { return this.isleaf;}
public String getMenudesc() { if ( this.menudesc == null ) return ""; return this.menudesc;}
public String getMenulabel() { if ( this.menulabel == null ) return ""; return this.menulabel;}
public String getMenuicon() { if ( this.menuicon == null ) return ""; return this.menuicon;}
public String getOpenicon() { if ( this.openicon == null ) return ""; return this.openicon;}
public String getTargetmachine() { if ( this.targetmachine == null ) return ""; return this.targetmachine;}
public String getMenuaction() { if ( this.menuaction == null ) return ""; return this.menuaction;}
public String getOpenwindow() { if ( this.openwindow == null ) return ""; return this.openwindow;}
public int getWindowwidth() { return this.windowwidth;}
public int getWindowheight() { return this.windowheight;}
public int getLevelidx() { return this.levelidx;}
public String getFillstr50() { if ( this.fillstr50 == null ) return ""; return this.fillstr50;}
public String getFillstr100() { if ( this.fillstr100 == null ) return ""; return this.fillstr100;}
public int getFillint1() { return this.fillint1;}
public double getFilldbl2() { return this.filldbl2;}
public String getFilldate() {  if ( this.filldate == null ) { return ""; } else { return this.filldate.trim().split(" ")[0];} }public String getFilldateTime() {  if ( this.filldate == null ) return ""; return this.filldate.split("\\.")[0];}
public void setMenuid(String menuid) { sqlMaker.setField("menuid",menuid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMenuid().equals(menuid)) cf.add("menuid",this.menuid,menuid); } this.menuid=menuid;}
public void setParentmenuid(String parentmenuid) { sqlMaker.setField("parentmenuid",parentmenuid,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getParentmenuid().equals(parentmenuid)) cf.add("parentmenuid",this.parentmenuid,parentmenuid); } this.parentmenuid=parentmenuid;}
public void setMenulevel(int menulevel) { sqlMaker.setField("menulevel",""+menulevel,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getMenulevel()!=menulevel) cf.add("menulevel",this.menulevel+"",menulevel+""); } this.menulevel=menulevel;}
public void setIsleaf(int isleaf) { sqlMaker.setField("isleaf",""+isleaf,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getIsleaf()!=isleaf) cf.add("isleaf",this.isleaf+"",isleaf+""); } this.isleaf=isleaf;}
public void setMenudesc(String menudesc) { sqlMaker.setField("menudesc",menudesc,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMenudesc().equals(menudesc)) cf.add("menudesc",this.menudesc,menudesc); } this.menudesc=menudesc;}
public void setMenulabel(String menulabel) { sqlMaker.setField("menulabel",menulabel,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMenulabel().equals(menulabel)) cf.add("menulabel",this.menulabel,menulabel); } this.menulabel=menulabel;}
public void setMenuicon(String menuicon) { sqlMaker.setField("menuicon",menuicon,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMenuicon().equals(menuicon)) cf.add("menuicon",this.menuicon,menuicon); } this.menuicon=menuicon;}
public void setOpenicon(String openicon) { sqlMaker.setField("openicon",openicon,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOpenicon().equals(openicon)) cf.add("openicon",this.openicon,openicon); } this.openicon=openicon;}
public void setTargetmachine(String targetmachine) { sqlMaker.setField("targetmachine",targetmachine,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getTargetmachine().equals(targetmachine)) cf.add("targetmachine",this.targetmachine,targetmachine); } this.targetmachine=targetmachine;}
public void setMenuaction(String menuaction) { sqlMaker.setField("menuaction",menuaction,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getMenuaction().equals(menuaction)) cf.add("menuaction",this.menuaction,menuaction); } this.menuaction=menuaction;}
public void setOpenwindow(String openwindow) { sqlMaker.setField("openwindow",openwindow,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getOpenwindow().equals(openwindow)) cf.add("openwindow",this.openwindow,openwindow); } this.openwindow=openwindow;}
public void setWindowwidth(int windowwidth) { sqlMaker.setField("windowwidth",""+windowwidth,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getWindowwidth()!=windowwidth) cf.add("windowwidth",this.windowwidth+"",windowwidth+""); } this.windowwidth=windowwidth;}
public void setWindowheight(int windowheight) { sqlMaker.setField("windowheight",""+windowheight,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getWindowheight()!=windowheight) cf.add("windowheight",this.windowheight+"",windowheight+""); } this.windowheight=windowheight;}
public void setLevelidx(int levelidx) { sqlMaker.setField("levelidx",""+levelidx,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getLevelidx()!=levelidx) cf.add("levelidx",this.levelidx+"",levelidx+""); } this.levelidx=levelidx;}
public void setFillstr50(String fillstr50) { sqlMaker.setField("fillstr50",fillstr50,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr50().equals(fillstr50)) cf.add("fillstr50",this.fillstr50,fillstr50); } this.fillstr50=fillstr50;}
public void setFillstr100(String fillstr100) { sqlMaker.setField("fillstr100",fillstr100,Field.TEXT); if (this.operate_mode.equals("edit")) { if (!this.getFillstr100().equals(fillstr100)) cf.add("fillstr100",this.fillstr100,fillstr100); } this.fillstr100=fillstr100;}
public void setFillint1(int fillint1) { sqlMaker.setField("fillint1",""+fillint1,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFillint1()!=fillint1) cf.add("fillint1",this.fillint1+"",fillint1+""); } this.fillint1=fillint1;}
public void setFilldbl2(double filldbl2) { sqlMaker.setField("filldbl2",""+filldbl2,Field.NUMBER); if (this.operate_mode.equals("edit")) { if (this.getFilldbl2()!=filldbl2) cf.add("filldbl2",this.filldbl2+"",filldbl2+""); } this.filldbl2=filldbl2;}
public void setFilldate(String filldate) { sqlMaker.setField("filldate",filldate,Field.DATE); if (this.operate_mode.equals("edit")) { if (!this.getFilldate().equals(filldate)) cf.add("filldate",this.filldate,filldate); } this.filldate=filldate;}
public void init(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"menuid") !=null ) {this.setMenuid(actionRequest.getFieldValue(i,"menuid"));}
if ( actionRequest.getFieldValue(i,"parentmenuid") !=null ) {this.setParentmenuid(actionRequest.getFieldValue(i,"parentmenuid"));}
if ( actionRequest.getFieldValue(i,"menulevel") !=null && actionRequest.getFieldValue(i,"menulevel").trim().length() > 0 ) {this.setMenulevel(Integer.parseInt(actionRequest.getFieldValue(i,"menulevel")));}
if ( actionRequest.getFieldValue(i,"isleaf") !=null && actionRequest.getFieldValue(i,"isleaf").trim().length() > 0 ) {this.setIsleaf(Integer.parseInt(actionRequest.getFieldValue(i,"isleaf")));}
if ( actionRequest.getFieldValue(i,"menudesc") !=null ) {this.setMenudesc(actionRequest.getFieldValue(i,"menudesc"));}
if ( actionRequest.getFieldValue(i,"menulabel") !=null ) {this.setMenulabel(actionRequest.getFieldValue(i,"menulabel"));}
if ( actionRequest.getFieldValue(i,"menuicon") !=null ) {this.setMenuicon(actionRequest.getFieldValue(i,"menuicon"));}
if ( actionRequest.getFieldValue(i,"openicon") !=null ) {this.setOpenicon(actionRequest.getFieldValue(i,"openicon"));}
if ( actionRequest.getFieldValue(i,"targetmachine") !=null ) {this.setTargetmachine(actionRequest.getFieldValue(i,"targetmachine"));}
if ( actionRequest.getFieldValue(i,"menuaction") !=null ) {this.setMenuaction(actionRequest.getFieldValue(i,"menuaction"));}
if ( actionRequest.getFieldValue(i,"openwindow") !=null ) {this.setOpenwindow(actionRequest.getFieldValue(i,"openwindow"));}
if ( actionRequest.getFieldValue(i,"windowwidth") !=null && actionRequest.getFieldValue(i,"windowwidth").trim().length() > 0 ) {this.setWindowwidth(Integer.parseInt(actionRequest.getFieldValue(i,"windowwidth")));}
if ( actionRequest.getFieldValue(i,"windowheight") !=null && actionRequest.getFieldValue(i,"windowheight").trim().length() > 0 ) {this.setWindowheight(Integer.parseInt(actionRequest.getFieldValue(i,"windowheight")));}
if ( actionRequest.getFieldValue(i,"levelidx") !=null && actionRequest.getFieldValue(i,"levelidx").trim().length() > 0 ) {this.setLevelidx(Integer.parseInt(actionRequest.getFieldValue(i,"levelidx")));}
if ( actionRequest.getFieldValue(i,"fillstr50") !=null ) {this.setFillstr50(actionRequest.getFieldValue(i,"fillstr50"));}
if ( actionRequest.getFieldValue(i,"fillstr100") !=null ) {this.setFillstr100(actionRequest.getFieldValue(i,"fillstr100"));}
if ( actionRequest.getFieldValue(i,"fillint1") !=null && actionRequest.getFieldValue(i,"fillint1").trim().length() > 0 ) {this.setFillint1(Integer.parseInt(actionRequest.getFieldValue(i,"fillint1")));}
if ( actionRequest.getFieldValue(i,"filldbl2") !=null && actionRequest.getFieldValue(i,"filldbl2").trim().length() > 0 ) {this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl2")));}
if ( actionRequest.getFieldValue(i,"filldate") !=null ) {this.setFilldate(actionRequest.getFieldValue(i,"filldate"));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { this.init(i,actionRequest);}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PTMENU obj = (PTMENU)super.clone();obj.setMenuid(obj.menuid);
obj.setParentmenuid(obj.parentmenuid);
obj.setMenulevel(obj.menulevel);
obj.setIsleaf(obj.isleaf);
obj.setMenudesc(obj.menudesc);
obj.setMenulabel(obj.menulabel);
obj.setMenuicon(obj.menuicon);
obj.setOpenicon(obj.openicon);
obj.setTargetmachine(obj.targetmachine);
obj.setMenuaction(obj.menuaction);
obj.setOpenwindow(obj.openwindow);
obj.setWindowwidth(obj.windowwidth);
obj.setWindowheight(obj.windowheight);
obj.setLevelidx(obj.levelidx);
obj.setFillstr50(obj.fillstr50);
obj.setFillstr100(obj.fillstr100);
obj.setFillint1(obj.fillint1);
obj.setFilldbl2(obj.filldbl2);
obj.setFilldate(obj.filldate);
return obj;}}