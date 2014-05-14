package pub.platform.system.manage.dao;
import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;
public class PtMenuBean extends AbstractBasicBean implements Cloneable {
String menuid;
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
String fillstr50;
String fillstr100;
int fillint1;
double filldbl2;
String filldate;
int levelidx;
public static final String TABLENAME ="ptmenu";
public String getTableName() {return TABLENAME;}
public void addObject(List list,RecordSet rs) {
PtMenuBean abb = new PtMenuBean();
abb.menuid=rs.getString("menuid");
abb.parentmenuid=rs.getString("parentmenuid");
abb.menulevel=rs.getInt("menulevel");
abb.isleaf=rs.getInt("isleaf");
abb.menudesc=rs.getString("menudesc");
abb.menulabel=rs.getString("menulabel");
abb.menuicon=rs.getString("menuicon");
abb.openicon=rs.getString("openicon");
abb.targetmachine=rs.getString("targetmachine");
abb.menuaction=rs.getString("menuaction");
abb.openwindow=rs.getString("openwindow");
abb.windowwidth=rs.getInt("windowwidth");
abb.windowheight=rs.getInt("windowheight");
abb.fillstr50=rs.getString("fillstr50");
abb.fillstr100=rs.getString("fillstr100");
abb.fillint1=rs.getInt("fillint1");
abb.filldbl2=rs.getDouble("filldbl2");
abb.filldate=rs.getTimeString("filldate");
abb.levelidx=rs.getInt("levelidx");
list.add(abb);
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
public String getFillstr50() { if ( this.fillstr50 == null ) return ""; return this.fillstr50;}
public String getFillstr100() { if ( this.fillstr100 == null ) return ""; return this.fillstr100;}
public int getFillint1() { return this.fillint1;}
public double getFilldbl2() { return this.filldbl2;}
public String getFilldate() {  if ( this.filldate == null ) { return ""; } else { return this.filldate.trim().split(" ")[0];} }public String getFilldateTime() {  if ( this.filldate == null ) return ""; return this.filldate.split("\\.")[0];}
public int getLevelidx() { return this.levelidx;}
public void setMenuid(String menuid) { sqlMaker.setField("menuid",menuid,Field.TEXT);this.menuid=menuid;}
public void setParentmenuid(String parentmenuid) { sqlMaker.setField("parentmenuid",parentmenuid,Field.TEXT);this.parentmenuid=parentmenuid;}
public void setMenulevel(int menulevel) { sqlMaker.setField("menulevel",""+menulevel,Field.NUMBER);this.menulevel=menulevel;}
public void setIsleaf(int isleaf) { sqlMaker.setField("isleaf",""+isleaf,Field.NUMBER);this.isleaf=isleaf;}
public void setMenudesc(String menudesc) { sqlMaker.setField("menudesc",menudesc,Field.TEXT);this.menudesc=menudesc;}
public void setMenulabel(String menulabel) { sqlMaker.setField("menulabel",menulabel,Field.TEXT);this.menulabel=menulabel;}
public void setMenuicon(String menuicon) { sqlMaker.setField("menuicon",menuicon,Field.TEXT);this.menuicon=menuicon;}
public void setOpenicon(String openicon) { sqlMaker.setField("openicon",openicon,Field.TEXT);this.openicon=openicon;}
public void setTargetmachine(String targetmachine) { sqlMaker.setField("targetmachine",targetmachine,Field.TEXT);this.targetmachine=targetmachine;}
public void setMenuaction(String menuaction) { sqlMaker.setField("menuaction",menuaction,Field.TEXT);this.menuaction=menuaction;}
public void setOpenwindow(String openwindow) { sqlMaker.setField("openwindow",openwindow,Field.TEXT);this.openwindow=openwindow;}
public void setWindowwidth(int windowwidth) { sqlMaker.setField("windowwidth",""+windowwidth,Field.NUMBER);this.windowwidth=windowwidth;}
public void setWindowheight(int windowheight) { sqlMaker.setField("windowheight",""+windowheight,Field.NUMBER);this.windowheight=windowheight;}
public void setFillstr50(String fillstr50) { sqlMaker.setField("fillstr50",fillstr50,Field.TEXT);this.fillstr50=fillstr50;}
public void setFillstr100(String fillstr100) { sqlMaker.setField("fillstr100",fillstr100,Field.TEXT);this.fillstr100=fillstr100;}
public void setFillint1(int fillint1) { sqlMaker.setField("fillint1",""+fillint1,Field.NUMBER);this.fillint1=fillint1;}
public void setFilldbl2(double filldbl2) { sqlMaker.setField("filldbl2",""+filldbl2,Field.NUMBER);this.filldbl2=filldbl2;}
public void setFilldate(String filldate) { sqlMaker.setField("filldate",filldate,Field.DATE);this.filldate=filldate;}
public void setLevelidx(int levelidx) { sqlMaker.setField("levelidx",""+levelidx,Field.NUMBER);this.levelidx=levelidx;}
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
if ( actionRequest.getFieldValue(i,"fillstr50") !=null ) {this.setFillstr50(actionRequest.getFieldValue(i,"fillstr50"));}
if ( actionRequest.getFieldValue(i,"fillstr100") !=null ) {this.setFillstr100(actionRequest.getFieldValue(i,"fillstr100"));}
if ( actionRequest.getFieldValue(i,"fillint1") !=null && actionRequest.getFieldValue(i,"fillint1").trim().length() > 0 ) {this.setFillint1(Integer.parseInt(actionRequest.getFieldValue(i,"fillint1")));}
if ( actionRequest.getFieldValue(i,"filldbl2") !=null && actionRequest.getFieldValue(i,"filldbl2").trim().length() > 0 ) {this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl2")));}
if ( actionRequest.getFieldValue(i,"filldate") !=null ) {this.setFilldate(actionRequest.getFieldValue(i,"filldate"));}
if ( actionRequest.getFieldValue(i,"levelidx") !=null && actionRequest.getFieldValue(i,"levelidx").trim().length() > 0 ) {this.setLevelidx(Integer.parseInt(actionRequest.getFieldValue(i,"levelidx")));}
}public void init(ActionRequest actionRequest) throws Exception { this.init(0,actionRequest);}public void initAll(int i,ActionRequest actionRequest) throws Exception { if ( actionRequest.getFieldValue(i,"menuid") !=null ) {this.setMenuid(actionRequest.getFieldValue(i,"menuid"));} else {this.setMenuid(null);}
if ( actionRequest.getFieldValue(i,"parentmenuid") !=null ) {this.setParentmenuid(actionRequest.getFieldValue(i,"parentmenuid"));} else {this.setParentmenuid(null);}
if ( actionRequest.getFieldValue(i,"menulevel") !=null && actionRequest.getFieldValue(i,"menulevel").trim().length() > 0 ) {this.setMenulevel(Integer.parseInt(actionRequest.getFieldValue(i,"menulevel")));} else {this.setMenulevel(0);}
if ( actionRequest.getFieldValue(i,"isleaf") !=null && actionRequest.getFieldValue(i,"isleaf").trim().length() > 0 ) {this.setIsleaf(Integer.parseInt(actionRequest.getFieldValue(i,"isleaf")));} else {this.setIsleaf(0);}
if ( actionRequest.getFieldValue(i,"menudesc") !=null ) {this.setMenudesc(actionRequest.getFieldValue(i,"menudesc"));} else {this.setMenudesc(null);}
if ( actionRequest.getFieldValue(i,"menulabel") !=null ) {this.setMenulabel(actionRequest.getFieldValue(i,"menulabel"));} else {this.setMenulabel(null);}
if ( actionRequest.getFieldValue(i,"menuicon") !=null ) {this.setMenuicon(actionRequest.getFieldValue(i,"menuicon"));} else {this.setMenuicon(null);}
if ( actionRequest.getFieldValue(i,"openicon") !=null ) {this.setOpenicon(actionRequest.getFieldValue(i,"openicon"));} else {this.setOpenicon(null);}
if ( actionRequest.getFieldValue(i,"targetmachine") !=null ) {this.setTargetmachine(actionRequest.getFieldValue(i,"targetmachine"));} else {this.setTargetmachine(null);}
if ( actionRequest.getFieldValue(i,"menuaction") !=null ) {this.setMenuaction(actionRequest.getFieldValue(i,"menuaction"));} else {this.setMenuaction(null);}
if ( actionRequest.getFieldValue(i,"openwindow") !=null ) {this.setOpenwindow(actionRequest.getFieldValue(i,"openwindow"));} else {this.setOpenwindow(null);}
if ( actionRequest.getFieldValue(i,"windowwidth") !=null && actionRequest.getFieldValue(i,"windowwidth").trim().length() > 0 ) {this.setWindowwidth(Integer.parseInt(actionRequest.getFieldValue(i,"windowwidth")));} else {this.setWindowwidth(0);}
if ( actionRequest.getFieldValue(i,"windowheight") !=null && actionRequest.getFieldValue(i,"windowheight").trim().length() > 0 ) {this.setWindowheight(Integer.parseInt(actionRequest.getFieldValue(i,"windowheight")));} else {this.setWindowheight(0);}
if ( actionRequest.getFieldValue(i,"fillstr50") !=null ) {this.setFillstr50(actionRequest.getFieldValue(i,"fillstr50"));} else {this.setFillstr50(null);}
if ( actionRequest.getFieldValue(i,"fillstr100") !=null ) {this.setFillstr100(actionRequest.getFieldValue(i,"fillstr100"));} else {this.setFillstr100(null);}
if ( actionRequest.getFieldValue(i,"fillint1") !=null && actionRequest.getFieldValue(i,"fillint1").trim().length() > 0 ) {this.setFillint1(Integer.parseInt(actionRequest.getFieldValue(i,"fillint1")));} else {this.setFillint1(0);}
if ( actionRequest.getFieldValue(i,"filldbl2") !=null && actionRequest.getFieldValue(i,"filldbl2").trim().length() > 0 ) {this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i,"filldbl2")));} else {this.setFilldbl2(0);}
if ( actionRequest.getFieldValue(i,"filldate") !=null ) {this.setFilldate(actionRequest.getFieldValue(i,"filldate"));} else {this.setFilldate(null);}
if ( actionRequest.getFieldValue(i,"levelidx") !=null && actionRequest.getFieldValue(i,"levelidx").trim().length() > 0 ) {this.setLevelidx(Integer.parseInt(actionRequest.getFieldValue(i,"levelidx")));} else {this.setLevelidx(0);}
}public void initAll(ActionRequest actionRequest) throws Exception { this.initAll(0,actionRequest);}public Object clone() throws CloneNotSupportedException { PtMenuBean obj = (PtMenuBean)super.clone();obj.setMenuid(obj.menuid);
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
obj.setFillstr50(obj.fillstr50);
obj.setFillstr100(obj.fillstr100);
obj.setFillint1(obj.fillint1);
obj.setFilldbl2(obj.filldbl2);
obj.setFilldate(obj.filldate);
obj.setLevelidx(obj.levelidx);
return obj;}}