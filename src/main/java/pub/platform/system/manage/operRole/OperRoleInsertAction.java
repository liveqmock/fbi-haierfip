package pub.platform.system.manage.operRole;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class OperRoleInsertAction extends Action{

     PtOperRoleBean ptoperrolebean = new PtOperRoleBean();

     public int doBusiness() {

       for (int i=0; i<this.req.getRecorderCount();i++ ){
           String  SQLStr = "delete from PTOperRole where (RoleID='" + this.req.getFieldValue(i,"RoleID") + "')and(OperID='" + this.req.getFieldValue(i,"OperID") + "')";
            int retcount = this.dc.executeUpdate(SQLStr);
            if(retcount < 0) {
                 this.res.setType(0);
                 this.res.setResult(false);
                 this.res.setMessage("添加记录失败");
                 return retcount;
          }


             ptoperrolebean.setOperid(this.req.getFieldValue(i,"OperID"));
             ptoperrolebean.setRoleid(this.req.getFieldValue(i,"RoleID"));

             if (ptoperrolebean.insert() <0){
                 this.res.setType(0);
                 this.res.setResult(false);
                 this.res.setMessage("添加记录失败");
                 return -1;
             }
     }

     this.res.setType(0);
     this.res.setResult(true);
     this.res.setMessage("添加记录成功");

     return 0;
   }

    public int delenum(){
         for (int i=0; i<this.req.getRecorderCount();i++ ){
                String  SQLStr = "delete from PTOperRole where (RoleID='" + this.req.getFieldValue(i,"RoleID") + "')and(OperID='" + this.req.getFieldValue(i,"OperID") + "')";
                 int retcount = this.dc.executeUpdate(SQLStr);

                 if(retcount < 0) {
                      this.res.setType(0);
                      this.res.setResult(false);
                      this.res.setMessage("删除记录失败");
                      return retcount;
               }

          }

          this.res.setType(0);
          this.res.setResult(true);
          this.res.setMessage("删除记录成功");

          return 0;

    }
}
