package pub.platform.system.manage.dao;

import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;

public class PtDeptBean
     extends AbstractBasicBean
     implements Cloneable {
     String deptid;
     String deptname;
     String deptdesc;
     String parentdeptid;
     String deptleaf;
     int deptlevel;
     String deptstatus;
     String isdummy;
     String fillstr10;
     String fillstr20;
     String fillstr100;
     String fillstr150;
     int fillint4;
     int fillint6;
     int fillint8;
     double filldbl2;
     double filldbl22;
     double filldbl4;
     String filldate1;
     String filldate2;
     String dqdm;
     String mkdm;
     int deptindex;

     String superdqdm;
     String superdqmc;


     public static final String TABLENAME = "ptdept";
     public String getTableName() {
          return TABLENAME;
     }

     public void addObject(List list, RecordSet rs) {
          PtDeptBean abb = new PtDeptBean();
          abb.deptid = rs.getString("deptid");
          abb.deptname = rs.getString("deptname");
          abb.deptdesc = rs.getString("deptdesc");
          abb.parentdeptid = rs.getString("parentdeptid");
          abb.deptleaf = rs.getString("deptleaf");
          abb.deptlevel = rs.getInt("deptlevel");
          abb.deptstatus = rs.getString("deptstatus");
          abb.isdummy = rs.getString("isdummy");
          abb.fillstr10 = rs.getString("fillstr10");
          abb.fillstr20 = rs.getString("fillstr20");
          abb.fillstr100 = rs.getString("fillstr100");
          abb.fillstr150 = rs.getString("fillstr150");
          abb.fillint4 = rs.getInt("fillint4");
          abb.fillint6 = rs.getInt("fillint6");
          abb.fillint8 = rs.getInt("fillint8");
          abb.filldbl2 = rs.getDouble("filldbl2");
          abb.filldbl22 = rs.getDouble("filldbl22");
          abb.filldbl4 = rs.getDouble("filldbl4");
          abb.filldate1 = rs.getTimeString("filldate1");
          abb.filldate2 = rs.getTimeString("filldate2");
          abb.dqdm = rs.getString("dqdm");
          abb.mkdm = rs.getString("mkdm");
          abb.deptindex = rs.getInt("deptindex");

          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();

          try {
               RecordSet chrs = dc.executeQuery("select dqdm,deptname from " + TABLENAME +" where fillstr10='"+abb.fillstr10+"' and parentdeptid='0'" );
               while ( chrs != null && chrs.next() ) {
                  abb.superdqdm = rs.getString("dqdm");
                  abb.superdqmc = rs.getString("deptname");

               }
          } catch ( Exception e ) {
               e.printStackTrace();
          } finally {
               if ( isAutoRelease )
                    cm.release();

          }



          list.add(abb);
     }


     public String getSuperdqdm() {
         if(this.superdqdm == null) {
              return "";
         }
         return this.superdqdm;
    }

    public String getSuperdqmc() {
         if(this.superdqmc == null) {
              return "";
         }
         return this.superdqmc;
    }




     public String getDeptid() {
          if(this.deptid == null) {
               return "";
          }
          return this.deptid;
     }

     public String getDeptname() {
          if(this.deptname == null) {
               return "";
          }
          return this.deptname;
     }

     public String getDeptdesc() {
          if(this.deptdesc == null) {
               return "";
          }
          return this.deptdesc;
     }

     public String getParentdeptid() {
          if(this.parentdeptid == null) {
               return "";
          }
          return this.parentdeptid;
     }

     public String getDeptleaf() {
          if(this.deptleaf == null) {
               return "";
          }
          return this.deptleaf;
     }

     public int getDeptlevel() {
          return this.deptlevel;
     }

     public String getDeptstatus() {
          if(this.deptstatus == null) {
               return "";
          }
          return this.deptstatus;
     }

     public String getIsdummy() {
          if(this.isdummy == null) {
               return "";
          }
          return this.isdummy;
     }

     public String getFillstr10() {
          if(this.fillstr10 == null) {
               return "";
          }
          return this.fillstr10;
     }

     public String getFillstr20() {
          if(this.fillstr20 == null) {
               return "";
          }
          return this.fillstr20;
     }

     public String getFillstr100() {
          if(this.fillstr100 == null) {
               return "";
          }
          return this.fillstr100;
     }

     public String getFillstr150() {
          if(this.fillstr150 == null) {
               return "";
          }
          return this.fillstr150;
     }

     public int getFillint4() {
          return this.fillint4;
     }

     public int getFillint6() {
          return this.fillint6;
     }

     public int getFillint8() {
          return this.fillint8;
     }

     public double getFilldbl2() {
          return this.filldbl2;
     }

     public double getFilldbl22() {
          return this.filldbl22;
     }

     public double getFilldbl4() {
          return this.filldbl4;
     }

     public String getFilldate1() {
          if(this.filldate1 == null) {
               return "";
          } else {
               return this.filldate1.trim().split(" ")[0];
          }
     }

     public String getFilldate1Time() {
          if(this.filldate1 == null) {
               return "";
          }
          return this.filldate1.split("\\.")[0];
     }

     public String getFilldate2() {
          if(this.filldate2 == null) {
               return "";
          } else {
               return this.filldate2.trim().split(" ")[0];
          }
     }

     public String getFilldate2Time() {
          if(this.filldate2 == null) {
               return "";
          }
          return this.filldate2.split("\\.")[0];
     }

     public String getDqdm() {
          if(this.dqdm == null) {
               return "";
          }
          return this.dqdm;
     }

     public String getMkdm() {
          if(this.mkdm == null) {
               return "";
          }
          return this.mkdm;
     }

     public int getDeptindex() {
          return this.deptindex;
     }

     public void setDeptid(String deptid) {
          sqlMaker.setField("deptid", deptid, Field.TEXT);
          this.deptid = deptid;
     }

     public void setDeptname(String deptname) {
          sqlMaker.setField("deptname", deptname, Field.TEXT);
          this.deptname = deptname;
     }

     public void setDeptdesc(String deptdesc) {
          sqlMaker.setField("deptdesc", deptdesc, Field.TEXT);
          this.deptdesc = deptdesc;
     }

     public void setParentdeptid(String parentdeptid) {
          sqlMaker.setField("parentdeptid", parentdeptid, Field.TEXT);
          this.parentdeptid = parentdeptid;
     }

     public void setDeptleaf(String deptleaf) {
          sqlMaker.setField("deptleaf", deptleaf, Field.TEXT);
          this.deptleaf = deptleaf;
     }

     public void setDeptlevel(int deptlevel) {
          sqlMaker.setField("deptlevel", "" + deptlevel, Field.NUMBER);
          this.deptlevel = deptlevel;
     }

     public void setDeptstatus(String deptstatus) {
          sqlMaker.setField("deptstatus", deptstatus, Field.TEXT);
          this.deptstatus = deptstatus;
     }

     public void setIsdummy(String isdummy) {
          sqlMaker.setField("isdummy", isdummy, Field.TEXT);
          this.isdummy = isdummy;
     }

     public void setFillstr10(String fillstr10) {
          sqlMaker.setField("fillstr10", fillstr10, Field.TEXT);
          this.fillstr10 = fillstr10;
     }

     public void setFillstr20(String fillstr20) {
          sqlMaker.setField("fillstr20", fillstr20, Field.TEXT);
          this.fillstr20 = fillstr20;
     }

     public void setFillstr100(String fillstr100) {
          sqlMaker.setField("fillstr100", fillstr100, Field.TEXT);
          this.fillstr100 = fillstr100;
     }

     public void setFillstr150(String fillstr150) {
          sqlMaker.setField("fillstr150", fillstr150, Field.TEXT);
          this.fillstr150 = fillstr150;
     }

     public void setFillint4(int fillint4) {
          sqlMaker.setField("fillint4", "" + fillint4, Field.NUMBER);
          this.fillint4 = fillint4;
     }

     public void setFillint6(int fillint6) {
          sqlMaker.setField("fillint6", "" + fillint6, Field.NUMBER);
          this.fillint6 = fillint6;
     }

     public void setFillint8(int fillint8) {
          sqlMaker.setField("fillint8", "" + fillint8, Field.NUMBER);
          this.fillint8 = fillint8;
     }

     public void setFilldbl2(double filldbl2) {
          sqlMaker.setField("filldbl2", "" + filldbl2, Field.NUMBER);
          this.filldbl2 = filldbl2;
     }

     public void setFilldbl22(double filldbl22) {
          sqlMaker.setField("filldbl22", "" + filldbl22, Field.NUMBER);
          this.filldbl22 = filldbl22;
     }

     public void setFilldbl4(double filldbl4) {
          sqlMaker.setField("filldbl4", "" + filldbl4, Field.NUMBER);
          this.filldbl4 = filldbl4;
     }

     public void setFilldate1(String filldate1) {
          sqlMaker.setField("filldate1", filldate1, Field.DATE);
          this.filldate1 = filldate1;
     }

     public void setFilldate2(String filldate2) {
          sqlMaker.setField("filldate2", filldate2, Field.DATE);
          this.filldate2 = filldate2;
     }

     public void setDqdm(String dqdm) {
          sqlMaker.setField("dqdm", dqdm, Field.TEXT);
          this.dqdm = dqdm;
     }

     public void setMkdm(String mkdm) {
          sqlMaker.setField("mkdm", mkdm, Field.TEXT);
          this.mkdm = mkdm;
     }

     public void setDeptindex(int deptindex) {
          sqlMaker.setField("deptindex", "" + deptindex, Field.NUMBER);
          this.deptindex = deptindex;
     }

     public void init(int i, ActionRequest actionRequest)
          throws Exception {
          if(actionRequest.getFieldValue(i, "deptid") != null) {
               this.setDeptid(actionRequest.getFieldValue(i, "deptid"));
          }
          if(actionRequest.getFieldValue(i, "deptname") != null) {
               this.setDeptname(actionRequest.getFieldValue(i, "deptname"));
          }
          if(actionRequest.getFieldValue(i, "deptdesc") != null) {
               this.setDeptdesc(actionRequest.getFieldValue(i, "deptdesc"));
          }
          if(actionRequest.getFieldValue(i, "parentdeptid") != null) {
               this.setParentdeptid(actionRequest.getFieldValue(i, "parentdeptid"));
          }
          if(actionRequest.getFieldValue(i, "deptleaf") != null) {
               this.setDeptleaf(actionRequest.getFieldValue(i, "deptleaf"));
          }
          if(actionRequest.getFieldValue(i, "deptlevel") != null && actionRequest.getFieldValue(i, "deptlevel").trim().length() > 0) {
               this.setDeptlevel(Integer.parseInt(actionRequest.getFieldValue(i, "deptlevel")));
          }
          if(actionRequest.getFieldValue(i, "deptstatus") != null) {
               this.setDeptstatus(actionRequest.getFieldValue(i, "deptstatus"));
          }
          if(actionRequest.getFieldValue(i, "isdummy") != null) {
               this.setIsdummy(actionRequest.getFieldValue(i, "isdummy"));
          }
          if(actionRequest.getFieldValue(i, "fillstr10") != null) {
               this.setFillstr10(actionRequest.getFieldValue(i, "fillstr10"));
          }
          if(actionRequest.getFieldValue(i, "fillstr20") != null) {
               this.setFillstr20(actionRequest.getFieldValue(i, "fillstr20"));
          }
          if(actionRequest.getFieldValue(i, "fillstr100") != null) {
               this.setFillstr100(actionRequest.getFieldValue(i, "fillstr100"));
          }
          if(actionRequest.getFieldValue(i, "fillstr150") != null) {
               this.setFillstr150(actionRequest.getFieldValue(i, "fillstr150"));
          }
          if(actionRequest.getFieldValue(i, "fillint4") != null && actionRequest.getFieldValue(i, "fillint4").trim().length() > 0) {
               this.setFillint4(Integer.parseInt(actionRequest.getFieldValue(i, "fillint4")));
          }
          if(actionRequest.getFieldValue(i, "fillint6") != null && actionRequest.getFieldValue(i, "fillint6").trim().length() > 0) {
               this.setFillint6(Integer.parseInt(actionRequest.getFieldValue(i, "fillint6")));
          }
          if(actionRequest.getFieldValue(i, "fillint8") != null && actionRequest.getFieldValue(i, "fillint8").trim().length() > 0) {
               this.setFillint8(Integer.parseInt(actionRequest.getFieldValue(i, "fillint8")));
          }
          if(actionRequest.getFieldValue(i, "filldbl2") != null && actionRequest.getFieldValue(i, "filldbl2").trim().length() > 0) {
               this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i, "filldbl2")));
          }
          if(actionRequest.getFieldValue(i, "filldbl22") != null && actionRequest.getFieldValue(i, "filldbl22").trim().length() > 0) {
               this.setFilldbl22(Double.parseDouble(actionRequest.getFieldValue(i, "filldbl22")));
          }
          if(actionRequest.getFieldValue(i, "filldbl4") != null && actionRequest.getFieldValue(i, "filldbl4").trim().length() > 0) {
               this.setFilldbl4(Double.parseDouble(actionRequest.getFieldValue(i, "filldbl4")));
          }
          if(actionRequest.getFieldValue(i, "filldate1") != null) {
               this.setFilldate1(actionRequest.getFieldValue(i, "filldate1"));
          }
          if(actionRequest.getFieldValue(i, "filldate2") != null) {
               this.setFilldate2(actionRequest.getFieldValue(i, "filldate2"));
          }
          if(actionRequest.getFieldValue(i, "dqdm") != null) {
               this.setDqdm(actionRequest.getFieldValue(i, "dqdm"));
          }
          if(actionRequest.getFieldValue(i, "mkdm") != null) {
               this.setMkdm(actionRequest.getFieldValue(i, "mkdm"));
          }
          if(actionRequest.getFieldValue(i, "deptindex") != null && actionRequest.getFieldValue(i, "deptindex").trim().length() > 0) {
               this.setDeptindex(Integer.parseInt(actionRequest.getFieldValue(i, "deptindex")));
          }
     }

     public void init(ActionRequest actionRequest)
          throws Exception {
          this.init(0, actionRequest);
     }

     public void initAll(int i, ActionRequest actionRequest)
          throws Exception {
          if(actionRequest.getFieldValue(i, "deptid") != null) {
               this.setDeptid(actionRequest.getFieldValue(i, "deptid"));
          } else {
               this.setDeptid(null);
          }
          if(actionRequest.getFieldValue(i, "deptname") != null) {
               this.setDeptname(actionRequest.getFieldValue(i, "deptname"));
          } else {
               this.setDeptname(null);
          }
          if(actionRequest.getFieldValue(i, "deptdesc") != null) {
               this.setDeptdesc(actionRequest.getFieldValue(i, "deptdesc"));
          } else {
               this.setDeptdesc(null);
          }
          if(actionRequest.getFieldValue(i, "parentdeptid") != null) {
               this.setParentdeptid(actionRequest.getFieldValue(i, "parentdeptid"));
          } else {
               this.setParentdeptid(null);
          }
          if(actionRequest.getFieldValue(i, "deptleaf") != null) {
               this.setDeptleaf(actionRequest.getFieldValue(i, "deptleaf"));
          } else {
               this.setDeptleaf(null);
          }
          if(actionRequest.getFieldValue(i, "deptlevel") != null && actionRequest.getFieldValue(i, "deptlevel").trim().length() > 0) {
               this.setDeptlevel(Integer.parseInt(actionRequest.getFieldValue(i, "deptlevel")));
          } else {
               this.setDeptlevel(0);
          }
          if(actionRequest.getFieldValue(i, "deptstatus") != null) {
               this.setDeptstatus(actionRequest.getFieldValue(i, "deptstatus"));
          } else {
               this.setDeptstatus(null);
          }
          if(actionRequest.getFieldValue(i, "isdummy") != null) {
               this.setIsdummy(actionRequest.getFieldValue(i, "isdummy"));
          } else {
               this.setIsdummy(null);
          }
          if(actionRequest.getFieldValue(i, "fillstr10") != null) {
               this.setFillstr10(actionRequest.getFieldValue(i, "fillstr10"));
          } else {
               this.setFillstr10(null);
          }
          if(actionRequest.getFieldValue(i, "fillstr20") != null) {
               this.setFillstr20(actionRequest.getFieldValue(i, "fillstr20"));
          } else {
               this.setFillstr20(null);
          }
          if(actionRequest.getFieldValue(i, "fillstr100") != null) {
               this.setFillstr100(actionRequest.getFieldValue(i, "fillstr100"));
          } else {
               this.setFillstr100(null);
          }
          if(actionRequest.getFieldValue(i, "fillstr150") != null) {
               this.setFillstr150(actionRequest.getFieldValue(i, "fillstr150"));
          } else {
               this.setFillstr150(null);
          }
          if(actionRequest.getFieldValue(i, "fillint4") != null && actionRequest.getFieldValue(i, "fillint4").trim().length() > 0) {
               this.setFillint4(Integer.parseInt(actionRequest.getFieldValue(i, "fillint4")));
          } else {
               this.setFillint4(0);
          }
          if(actionRequest.getFieldValue(i, "fillint6") != null && actionRequest.getFieldValue(i, "fillint6").trim().length() > 0) {
               this.setFillint6(Integer.parseInt(actionRequest.getFieldValue(i, "fillint6")));
          } else {
               this.setFillint6(0);
          }
          if(actionRequest.getFieldValue(i, "fillint8") != null && actionRequest.getFieldValue(i, "fillint8").trim().length() > 0) {
               this.setFillint8(Integer.parseInt(actionRequest.getFieldValue(i, "fillint8")));
          } else {
               this.setFillint8(0);
          }
          if(actionRequest.getFieldValue(i, "filldbl2") != null && actionRequest.getFieldValue(i, "filldbl2").trim().length() > 0) {
               this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i, "filldbl2")));
          } else {
               this.setFilldbl2(0);
          }
          if(actionRequest.getFieldValue(i, "filldbl22") != null && actionRequest.getFieldValue(i, "filldbl22").trim().length() > 0) {
               this.setFilldbl22(Double.parseDouble(actionRequest.getFieldValue(i, "filldbl22")));
          } else {
               this.setFilldbl22(0);
          }
          if(actionRequest.getFieldValue(i, "filldbl4") != null && actionRequest.getFieldValue(i, "filldbl4").trim().length() > 0) {
               this.setFilldbl4(Double.parseDouble(actionRequest.getFieldValue(i, "filldbl4")));
          } else {
               this.setFilldbl4(0);
          }
          if(actionRequest.getFieldValue(i, "filldate1") != null) {
               this.setFilldate1(actionRequest.getFieldValue(i, "filldate1"));
          } else {
               this.setFilldate1(null);
          }
          if(actionRequest.getFieldValue(i, "filldate2") != null) {
               this.setFilldate2(actionRequest.getFieldValue(i, "filldate2"));
          } else {
               this.setFilldate2(null);
          }
          if(actionRequest.getFieldValue(i, "dqdm") != null) {
               this.setDqdm(actionRequest.getFieldValue(i, "dqdm"));
          } else {
               this.setDqdm(null);
          }
          if(actionRequest.getFieldValue(i, "mkdm") != null) {
               this.setMkdm(actionRequest.getFieldValue(i, "mkdm"));
          } else {
               this.setMkdm(null);
          }
          if(actionRequest.getFieldValue(i, "deptindex") != null && actionRequest.getFieldValue(i, "deptindex").trim().length() > 0) {
               this.setDeptindex(Integer.parseInt(actionRequest.getFieldValue(i, "deptindex")));
          } else {
               this.setDeptindex(0);
          }
     }

     public void initAll(ActionRequest actionRequest)
          throws Exception {
          this.initAll(0, actionRequest);
     }

     public Object clone()
          throws CloneNotSupportedException {
          PtDeptBean obj = (PtDeptBean)super.clone();
          obj.setDeptid(obj.deptid);
          obj.setDeptname(obj.deptname);
          obj.setDeptdesc(obj.deptdesc);
          obj.setParentdeptid(obj.parentdeptid);
          obj.setDeptleaf(obj.deptleaf);
          obj.setDeptlevel(obj.deptlevel);
          obj.setDeptstatus(obj.deptstatus);
          obj.setIsdummy(obj.isdummy);
          obj.setFillstr10(obj.fillstr10);
          obj.setFillstr20(obj.fillstr20);
          obj.setFillstr100(obj.fillstr100);
          obj.setFillstr150(obj.fillstr150);
          obj.setFillint4(obj.fillint4);
          obj.setFillint6(obj.fillint6);
          obj.setFillint8(obj.fillint8);
          obj.setFilldbl2(obj.filldbl2);
          obj.setFilldbl22(obj.filldbl22);
          obj.setFilldbl4(obj.filldbl4);
          obj.setFilldate1(obj.filldate1);
          obj.setFilldate2(obj.filldate2);
          obj.setDqdm(obj.dqdm);
          obj.setMkdm(obj.mkdm);
          obj.setDeptindex(obj.deptindex);
          return obj;
     }
}
