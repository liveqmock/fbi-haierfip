package pub.platform.system.manage.oper;

import pub.platform.form.control.*;

public class GetUserAction  extends Action {
    private String isall="0";
    private String isdistsup="0";
    private String isdept="0";
    private String supper="0";
    private String depid="";
    private String SQLStr= null;


     public int doBusiness() {


            if (this.req.getFieldValue("isall") != null)
                 isall =this.req.getFieldValue("isall");

            if (this.req.getFieldValue("isdistsup") != null)
                 isdistsup =this.req.getFieldValue("isdistsup");

            if (this.req.getFieldValue("isdept") != null)
                 isdept =this.req.getFieldValue("isdept");

            if (this.req.getFieldValue("depid") != null)
                 depid =this.req.getFieldValue("depid");

             String[] deptidarr =null;

            if (!depid.trim().equals(""))
                deptidarr= depid.trim().split(";",-2);

           supper = this.operator.getIssuper();


            if (isall.equals("1")){
                 SQLStr = " select selid,slename,seltype,countnumer,EMAIL, '"+supper+"' as supper from ( ";
                 SQLStr += "  select operid as selid ,opername as slename ,1 as seltype,0 as countnumer, EMAIL  from ptoper  where deptid='"+ this.req.getFieldValue("parentdeptid")+ "'  union";
                 SQLStr += " select  deptid as selid ,deptname as slename,0 as seltype ,(select  count(deptid) as a from ptdept  where parentdeptid=ta.deptid) as countnumer,'' as EMAIL from ptdept ta   where parentdeptid='"+ this.req.getFieldValue("parentdeptid")+ "') ss order by seltype ";

          }else{

               if (deptidarr != null){
                    for(int i=0; i<deptidarr.length ;i++ ){
                         if (SQLStr ==null)
                              SQLStr = "  select  deptid as selid ,deptname as slename,0 as seltype ,0 as countnumer,'"+supper+"' as supper from ptdept where deptid='"+deptidarr[i]+ "'";
                         else
                              SQLStr += "  union select  deptid as selid ,deptname as slename,0 as seltype ,0 as countnumer,'"+supper+"' as supper from ptdept where deptid='"+deptidarr[i]+ "'";
                    }
               }else{

                    if (isdistsup.equals("0")){
                         if(this.req.getFieldValue("parentdeptid").equals("0")){
                               SQLStr = "  select  deptid as selid ,deptname as slename,0 as seltype ,0 as countnumer,'"+supper+"' as supper from ptdept where deptid='"+this.operator.getDeptid()+ "'";

                         }else{

                              if (isdept.equals("0")){
                                   SQLStr = " select selid,slename,seltype,countnumer,'"+supper+"' as supper from ( ";

                                   SQLStr += " select  deptid as selid ,deptname as slename,0 as seltype ,(select  count(deptid) as a from ptdept  where parentdeptid=ta.deptid) as countnumer from ptdept ta   where parentdeptid='"+ this.req.getFieldValue("parentdeptid")+ "') ss order by seltype ";
                              }else{
                                   SQLStr = " select  deptid as selid ,deptname as slename,0 as seltype ,(select  count(deptid) as a from ptdept  where parentdeptid=ta.deptid) as countnumer,'"+supper+"' as supper from ptdept ta   where parentdeptid='"+ this.req.getFieldValue("parentdeptid")+ "' order by seltype ";
                              }
                         }
                    }else{
                         if (supper.equals("1")){
                              if(this.req.getFieldValue("parentdeptid").equals("0")){
                                    SQLStr = "  select  deptid as selid ,deptname as slename,0 as seltype ,0 as countnumer,'"+supper+"' as supper from ptdept where deptid='"+this.operator.getDeptid()+ "'";

                             }else{
                                  if (isdept.equals("0")){
                                       SQLStr = " select selid,slename,seltype,countnumer,'"+supper+"' as supper from ( ";

                                       SQLStr += " select  deptid as selid ,deptname as slename,0 as seltype ,(select  count(deptid) as a from ptdept  where parentdeptid=ta.deptid) as countnumer from ptdept ta   where parentdeptid='"+ this.req.getFieldValue("parentdeptid")+ "') ss order by seltype ";
                                  }else{
                                       SQLStr = " select  deptid as selid ,deptname as slename,0 as seltype ,(select  count(deptid) as a from ptdept  where parentdeptid=ta.deptid) as countnumer,'"+supper+"' as supper from ptdept ta   where parentdeptid='"+ this.req.getFieldValue("parentdeptid")+ "' order by seltype ";
                                 }

                             }

                         }else{
                              if(this.req.getFieldValue("parentdeptid").equals("0")){
                                   SQLStr = "  select  deptid as selid ,deptname as slename,0 as seltype ,0 as countnumer,'"+supper+"' as supper from ptdept where deptid='"+this.operator.getDeptid()+ "'";

                             }else{
                                  SQLStr = "  select operid as selid ,opername as slename ,1 as seltype,0 as countnumer,'"+supper+"' as supper from v_workman   where (deptid='"+ this.req.getFieldValue("parentdeptid")+ "') and(OperID='"+this.operator.getOperid()+"')";
                              }

                         }
                    }
               }

          }


         //  System.out.println(SQLStr);

          this.rs = this.dc.executeQuery(SQLStr);

          this.res.setFieldName("selid;slename;seltype;count;EMAIL");
          this.res.setFieldType("text;text;text;text;text");
            this.res.setEnumType("0;0;0;0;0");
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
