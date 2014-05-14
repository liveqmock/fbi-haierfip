package pub.platform.system.manage.operRole;

import pub.platform.db.*;
import pub.platform.form.control.Action;


public class OperRoleSelectAction extends Action {
     public int doBusiness() {

          String SQLStr = " select selid,slename,seltype,countnumer from ( ";
           SQLStr += "  select operid as selid ,opername as slename ,1 as seltype,0 as countnumer from ptoper where deptid='"+ this.req.getFieldValue("parentdeptid")+ "'  union";
           SQLStr += " select  deptid as selid ,deptname as slename,0 as seltype ,(select  count(deptid) as a from ptdept  where parentdeptid=ta.deptid) as countnumer from ptdept ta   where parentdeptid='"+ this.req.getFieldValue("parentdeptid")+ "') ss order by seltype ";



//           if (this.req.getFieldValue("parentdeptid").equals("0")){
//                if (this.getOperator().getOpertype() != null && this.getOperator().getOpertype() .equals("2")){
//                     if (this.getOperator().getIssuper().equals("1")){
//                          SQLStr = " select selid,slename,seltype,countnumer from ( ";
//                                SQLStr += "  select operid as selid ,opername as slename ,1 as seltype,0 as countnumer from ptoper where deptid='"+  this.getOperator().getDeptid() + "'  union";
//                                SQLStr += " select  deptid as selid ,deptname as slename,0 as seltype ,(select  count(deptid) as a from ptdept  where parentdeptid=ta.deptid) as countnumer from ptdept ta   where parentdeptid='"+  this.getOperator().getDeptid() + "') ss order by seltype ";
//
//
//                     }else
//                         SQLStr= "select operid as selid ,opername as slename ,1 as seltype,0 as countnumer from ptoper where 1>1";
//
//                }
//           }
//
//




          this.rs = this.dc.executeQuery(SQLStr);

          this.res.setFieldName("selid;slename;seltype;count");
          this.res.setFieldType("text;text;text;text");
            this.res.setEnumType("0;0;0;0");
          this.res.setType(1);

          if (this.rs == null) {
               this.res.setResult(false);
               this.res.setMessage("无返回信息记录");
               return -1;

          } else {
               this.res.setResult(true);
               this.res.setRecordset(this.rs);
          }

          return 0;
     }


}
