package pub.platform.system.manage.dao;

import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;

public class PtResourceBean
     extends AbstractBasicBean
     implements Cloneable {
     String resid;
     String parentresid;
     String resname;
     String restype;
     String resenabled;
     String resdesc;
     public static final String TABLENAME = "ptresource";
     public String getTableName() {
          return TABLENAME;
     }

     public void addObject(List list, RecordSet rs) {
          PtResourceBean abb = new PtResourceBean();
          abb.resid = rs.getString("resid");
          abb.parentresid = rs.getString("parentresid");
          abb.resname = rs.getString("resname");
          abb.restype = rs.getString("restype");
          abb.resenabled = rs.getString("resenabled");
          abb.resdesc = rs.getString("resdesc");
          list.add(abb);
     }

     public String getResid() {
          if(this.resid == null)return "";
          return this.resid;
     }

     public String getParentresid() {
          if(this.parentresid == null)return "";
          return this.parentresid;
     }

     public String getResname() {
          if(this.resname == null)return "";
          return this.resname;
     }

     public String getRestype() {
          if(this.restype == null)return "";
          return this.restype;
     }

     public String getResenabled() {
          if(this.resenabled == null)return "";
          return this.resenabled;
     }

     public String getResdesc() {
          if(this.resdesc == null)return "";
          return this.resdesc;
     }

     public void setResid(String resid) {
          sqlMaker.setField("resid", resid, Field.TEXT);
          this.resid = resid;
     }

     public void setParentresid(String parentresid) {
          sqlMaker.setField("parentresid", parentresid, Field.TEXT);
          this.parentresid = parentresid;
     }

     public void setResname(String resname) {
          sqlMaker.setField("resname", resname, Field.TEXT);
          this.resname = resname;
     }

     public void setRestype(String restype) {
          sqlMaker.setField("restype", restype, Field.TEXT);
          this.restype = restype;
     }

     public void setResenabled(String resenabled) {
          sqlMaker.setField("resenabled", resenabled, Field.TEXT);
          this.resenabled = resenabled;
     }

     public void setResdesc(String resdesc) {
          sqlMaker.setField("resdesc", resdesc, Field.TEXT);
          this.resdesc = resdesc;
     }

     public void init(int i, ActionRequest actionRequest)
          throws Exception {
          if(actionRequest.getFieldValue(i, "resid") != null) {
               this.setResid(actionRequest.getFieldValue(i, "resid"));
          }
          if(actionRequest.getFieldValue(i, "parentresid") != null) {
               this.setParentresid(actionRequest.getFieldValue(i, "parentresid"));
          }
          if(actionRequest.getFieldValue(i, "resname") != null) {
               this.setResname(actionRequest.getFieldValue(i, "resname"));
          }
          if(actionRequest.getFieldValue(i, "restype") != null) {
               this.setRestype(actionRequest.getFieldValue(i, "restype"));
          }
          if(actionRequest.getFieldValue(i, "resenabled") != null) {
               this.setResenabled(actionRequest.getFieldValue(i, "resenabled"));
          }
          if(actionRequest.getFieldValue(i, "resdesc") != null) {
               this.setResdesc(actionRequest.getFieldValue(i, "resdesc"));
          }
     }

     public void init(ActionRequest actionRequest)
          throws Exception {
          this.init(0, actionRequest);
     }

     public void initAll(int i, ActionRequest actionRequest)
          throws Exception {
          if(actionRequest.getFieldValue(i, "resid") != null) {
               this.setResid(actionRequest.getFieldValue(i, "resid"));
          } else {
               this.setResid(null);
          }
          if(actionRequest.getFieldValue(i, "parentresid") != null) {
               this.setParentresid(actionRequest.getFieldValue(i, "parentresid"));
          } else {
               this.setParentresid(null);
          }
          if(actionRequest.getFieldValue(i, "resname") != null) {
               this.setResname(actionRequest.getFieldValue(i, "resname"));
          } else {
               this.setResname(null);
          }
          if(actionRequest.getFieldValue(i, "restype") != null) {
               this.setRestype(actionRequest.getFieldValue(i, "restype"));
          } else {
               this.setRestype(null);
          }
          if(actionRequest.getFieldValue(i, "resenabled") != null) {
               this.setResenabled(actionRequest.getFieldValue(i, "resenabled"));
          } else {
               this.setResenabled(null);
          }
          if(actionRequest.getFieldValue(i, "resdesc") != null) {
               this.setResdesc(actionRequest.getFieldValue(i, "resdesc"));
          } else {
               this.setResdesc(null);
          }
     }

     public void initAll(ActionRequest actionRequest)
          throws Exception {
          this.initAll(0, actionRequest);
     }

     public Object clone()
          throws CloneNotSupportedException {
          PtResourceBean obj = (PtResourceBean)super.clone();
          obj.setResid(obj.resid);
          obj.setParentresid(obj.parentresid);
          obj.setResname(obj.resname);
          obj.setRestype(obj.restype);
          obj.setResenabled(obj.resenabled);
          obj.setResdesc(obj.resdesc);
          return obj;
     }
}
