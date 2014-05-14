package pub.platform.system.manage.dao;

import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;

public class PtButtonBean
     extends AbstractBasicBean
     implements Cloneable {
     String buttonid;
     String parentbuttonid;
     String buttondesc;
     String buttonlabel;
     int buttonindex;
     String fillstr50;
     String fillstr100;
     int fillint1;
     double filldbl2;
     String filldate;
     public static final String TABLENAME = "ptbutton";
     public String getTableName() {
          return TABLENAME;
     }

     public void addObject(List list, RecordSet rs) {
          PtButtonBean abb = new PtButtonBean();
          abb.buttonid = rs.getString("buttonid");
          abb.parentbuttonid = rs.getString("parentbuttonid");
          abb.buttondesc = rs.getString("buttondesc");
          abb.buttonlabel = rs.getString("buttonlabel");
          abb.buttonindex = rs.getInt("buttonindex");
          abb.fillstr50 = rs.getString("fillstr50");
          abb.fillstr100 = rs.getString("fillstr100");
          abb.fillint1 = rs.getInt("fillint1");
          abb.filldbl2 = rs.getDouble("filldbl2");
          abb.filldate = rs.getTimeString("filldate");
          list.add(abb);
     }

     public String getButtonid() {
          if(this.buttonid == null) {
               return "";
          }
          return this.buttonid;
     }

     public String getParentbuttonid() {
          if(this.parentbuttonid == null) {
               return "";
          }
          return this.parentbuttonid;
     }

     public String getButtondesc() {
          if(this.buttondesc == null) {
               return "";
          }
          return this.buttondesc;
     }

     public String getButtonlabel() {
          if(this.buttonlabel == null) {
               return "";
          }
          return this.buttonlabel;
     }

     public int getButtonindex() {
          return this.buttonindex;
     }

     public String getFillstr50() {
          if(this.fillstr50 == null) {
               return "";
          }
          return this.fillstr50;
     }

     public String getFillstr100() {
          if(this.fillstr100 == null) {
               return "";
          }
          return this.fillstr100;
     }

     public int getFillint1() {
          return this.fillint1;
     }

     public double getFilldbl2() {
          return this.filldbl2;
     }

     public String getFilldate() {
          if(this.filldate == null) {
               return "";
          } else {
               return this.filldate.trim().split(" ")[0];
          }
     }

     public String getFilldateTime() {
          if(this.filldate == null) {
               return "";
          }
          return this.filldate.split("\\.")[0];
     }

     public void setButtonid(String buttonid) {
          sqlMaker.setField("buttonid", buttonid, Field.TEXT);
          this.buttonid = buttonid;
     }

     public void setParentbuttonid(String parentbuttonid) {
          sqlMaker.setField("parentbuttonid", parentbuttonid, Field.TEXT);
          this.parentbuttonid = parentbuttonid;
     }

     public void setButtondesc(String buttondesc) {
          sqlMaker.setField("buttondesc", buttondesc, Field.TEXT);
          this.buttondesc = buttondesc;
     }

     public void setButtonlabel(String buttonlabel) {
          sqlMaker.setField("buttonlabel", buttonlabel, Field.TEXT);
          this.buttonlabel = buttonlabel;
     }

     public void setButtonindex(int buttonindex) {
          sqlMaker.setField("buttonindex", "" + buttonindex, Field.NUMBER);
          this.buttonindex = buttonindex;
     }

     public void setFillstr50(String fillstr50) {
          sqlMaker.setField("fillstr50", fillstr50, Field.TEXT);
          this.fillstr50 = fillstr50;
     }

     public void setFillstr100(String fillstr100) {
          sqlMaker.setField("fillstr100", fillstr100, Field.TEXT);
          this.fillstr100 = fillstr100;
     }

     public void setFillint1(int fillint1) {
          sqlMaker.setField("fillint1", "" + fillint1, Field.NUMBER);
          this.fillint1 = fillint1;
     }

     public void setFilldbl2(double filldbl2) {
          sqlMaker.setField("filldbl2", "" + filldbl2, Field.NUMBER);
          this.filldbl2 = filldbl2;
     }

     public void setFilldate(String filldate) {
          sqlMaker.setField("filldate", filldate, Field.DATE);
          this.filldate = filldate;
     }

     public void init(int i, ActionRequest actionRequest)
          throws Exception {
          if(actionRequest.getFieldValue(i, "buttonid") != null) {
               this.setButtonid(actionRequest.getFieldValue(i, "buttonid"));
          }
          if(actionRequest.getFieldValue(i, "parentbuttonid") != null) {
               this.setParentbuttonid(actionRequest.getFieldValue(i, "parentbuttonid"));
          }
          if(actionRequest.getFieldValue(i, "buttondesc") != null) {
               this.setButtondesc(actionRequest.getFieldValue(i, "buttondesc"));
          }
          if(actionRequest.getFieldValue(i, "buttonlabel") != null) {
               this.setButtonlabel(actionRequest.getFieldValue(i, "buttonlabel"));
          }
          if(actionRequest.getFieldValue(i, "buttonindex") != null && actionRequest.getFieldValue(i, "buttonindex").trim().length() > 0) {
               this.setButtonindex(Integer.parseInt(actionRequest.getFieldValue(i, "buttonindex")));
          }
          if(actionRequest.getFieldValue(i, "fillstr50") != null) {
               this.setFillstr50(actionRequest.getFieldValue(i, "fillstr50"));
          }
          if(actionRequest.getFieldValue(i, "fillstr100") != null) {
               this.setFillstr100(actionRequest.getFieldValue(i, "fillstr100"));
          }
          if(actionRequest.getFieldValue(i, "fillint1") != null && actionRequest.getFieldValue(i, "fillint1").trim().length() > 0) {
               this.setFillint1(Integer.parseInt(actionRequest.getFieldValue(i, "fillint1")));
          }
          if(actionRequest.getFieldValue(i, "filldbl2") != null && actionRequest.getFieldValue(i, "filldbl2").trim().length() > 0) {
               this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i, "filldbl2")));
          }
          if(actionRequest.getFieldValue(i, "filldate") != null) {
               this.setFilldate(actionRequest.getFieldValue(i, "filldate"));
          }
     }

     public void init(ActionRequest actionRequest)
          throws Exception {
          this.init(0, actionRequest);
     }

     public void initAll(int i, ActionRequest actionRequest)
          throws Exception {
          if(actionRequest.getFieldValue(i, "buttonid") != null) {
               this.setButtonid(actionRequest.getFieldValue(i, "buttonid"));
          } else {
               this.setButtonid(null);
          }
          if(actionRequest.getFieldValue(i, "parentbuttonid") != null) {
               this.setParentbuttonid(actionRequest.getFieldValue(i, "parentbuttonid"));
          } else {
               this.setParentbuttonid(null);
          }
          if(actionRequest.getFieldValue(i, "buttondesc") != null) {
               this.setButtondesc(actionRequest.getFieldValue(i, "buttondesc"));
          } else {
               this.setButtondesc(null);
          }
          if(actionRequest.getFieldValue(i, "buttonlabel") != null) {
               this.setButtonlabel(actionRequest.getFieldValue(i, "buttonlabel"));
          } else {
               this.setButtonlabel(null);
          }
          if(actionRequest.getFieldValue(i, "buttonindex") != null && actionRequest.getFieldValue(i, "buttonindex").trim().length() > 0) {
               this.setButtonindex(Integer.parseInt(actionRequest.getFieldValue(i, "buttonindex")));
          } else {
               this.setButtonindex(0);
          }
          if(actionRequest.getFieldValue(i, "fillstr50") != null) {
               this.setFillstr50(actionRequest.getFieldValue(i, "fillstr50"));
          } else {
               this.setFillstr50(null);
          }
          if(actionRequest.getFieldValue(i, "fillstr100") != null) {
               this.setFillstr100(actionRequest.getFieldValue(i, "fillstr100"));
          } else {
               this.setFillstr100(null);
          }
          if(actionRequest.getFieldValue(i, "fillint1") != null && actionRequest.getFieldValue(i, "fillint1").trim().length() > 0) {
               this.setFillint1(Integer.parseInt(actionRequest.getFieldValue(i, "fillint1")));
          } else {
               this.setFillint1(0);
          }
          if(actionRequest.getFieldValue(i, "filldbl2") != null && actionRequest.getFieldValue(i, "filldbl2").trim().length() > 0) {
               this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i, "filldbl2")));
          } else {
               this.setFilldbl2(0);
          }
          if(actionRequest.getFieldValue(i, "filldate") != null) {
               this.setFilldate(actionRequest.getFieldValue(i, "filldate"));
          } else {
               this.setFilldate(null);
          }
     }

     public void initAll(ActionRequest actionRequest)
          throws Exception {
          this.initAll(0, actionRequest);
     }

     public Object clone()
          throws CloneNotSupportedException {
          PtButtonBean obj = (PtButtonBean)super.clone();
          obj.setButtonid(obj.buttonid);
          obj.setParentbuttonid(obj.parentbuttonid);
          obj.setButtondesc(obj.buttondesc);
          obj.setButtonlabel(obj.buttonlabel);
          obj.setButtonindex(obj.buttonindex);
          obj.setFillstr50(obj.fillstr50);
          obj.setFillstr100(obj.fillstr100);
          obj.setFillint1(obj.fillint1);
          obj.setFilldbl2(obj.filldbl2);
          obj.setFilldate(obj.filldate);
          return obj;
     }
}
