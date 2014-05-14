package pub.platform.system.manage.resRole;

import pub.platform.db.*;
import pub.platform.form.control.Action;
import pub.platform.utils.*;

public class ResSelectAction extends Action {
     public int doBusiness() {
         String SQLStr ="";

         SQLStr = "delete from ptroleres t where roleid='"+this.req.getFieldValue("roleid")+"'  and resid is null ";
         int retcount = this.dc.executeUpdate(SQLStr);


         if (this.req.getFieldValue("resid").equals("0"))
              SQLStr = "  select r.ResID ,m.MenuLabel,(select  count(ResID) as a from PTResource  where ParentResID=r.ResID) as countnumer  from PTResource r ,PTMenu m where r.resname=rtrim(m.MenuID) and r.ParentResID = 'm0'   order by  Levelidx";
        else{
             if (Basic.getDbType().equals("DB2"))
             {
            	 SQLStr = " select r.ResID ,m.MenuLabel,(select  count(ResID) as a from PTResource  where ParentResID=r.ResID ) as countnumer   from PTResource r ,PTMenu m where r.resname=rtrim(m.MenuID) and r.ParentResID='" +this.req.getFieldValue("resid") + "' and  r.ResID not in (  select ResID from ptroleres t where roleid='"+this.req.getFieldValue("roleid")+"' EXCEPT ";
            	 SQLStr += "  select distinct ParentResID  from   PTResource where ParentResID in(   select ResID  from ptroleres t where roleid='"+this.req.getFieldValue("roleid")+"' )  )  order by  Levelidx ";
             }else
             {
            	 SQLStr = " select r.ResID ,m.MenuLabel,(select  count(ResID) as a from PTResource  where ParentResID=r.ResID ) as countnumer   from PTResource r ,PTMenu m where r.resname=trim(m.MenuID) and r.ParentResID='" +this.req.getFieldValue("resid") + "' and  r.ResID not in (  select ResID from ptroleres t where roleid='"+this.req.getFieldValue("roleid")+"' minus ";
            	 SQLStr += "  select distinct ParentResID  from   PTResource where ParentResID in(   select ResID  from ptroleres t where roleid='"+this.req.getFieldValue("roleid")+"' )  )  order by  Levelidx ";
             }
        
        }



            // SQLStr = "  select r.ResID ,m.MenuLabel,(select  count(ResID) as a from PTResource  where ParentResID=r.ResID) as countnumer   from PTResource r ,PTMenu m where r.resname=m.MenuID and r.ParentResID='" +this.req.getFieldValue("resid") + "' and  r.ResID not in (select ResID from ptroleres t where roleid='"+this.req.getFieldValue("roleid")+"' )  order by  MenuID";
        //  System.out.println(SQLStr);
          this.rs = this.dc.executeQuery(SQLStr);

          this.res.setFieldName("ResID;ResName;count");
          this.res.setFieldType("text;text;text");
          this.res.setEnumType("0;0;0");
          this.res.setType(1);
          //System.out.println("this.rs.next()="+this.rs.next());
          if (this.rs== null) {

            //  SQLStr = "  select r.ResID ,m.MenuLabel,(select  count(ResID) as a from PTResource  where ParentResID=r.ResID) as countnumer   from PTResource r ,PTMenu m where r.ResID=m.MenuID and  r.ResID not in (select ResID from ptroleres t where roleid='"+this.req.getFieldValue("roleid")+"' )  order by  ResID";

             // System.out.println(SQLStr);
              // this.rs = this.dc.executeQuery(SQLStr);
              //   if (!this.rs.next()) {
                      this.res.setResult(false);
                      this.res.setMessage("无取出记录信息");
                      return -1;
               //  }
          }
          this.rs.beforeFirst();
          this.res.setResult(true);
          this.res.setRecordset(this.rs);


          return 0;
     }


}
