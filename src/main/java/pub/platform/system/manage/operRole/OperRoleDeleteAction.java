package pub.platform.system.manage.operRole;

import pub.platform.form.control.Action;

public class OperRoleDeleteAction extends Action{
    public int doBusiness() {

       for (int i=0; i<this.req.getRecorderCount();i++ ){
           String  SQLStr = "delete from PTOperRole where (RoleID='" + this.req.getFieldValue(i,"RoleID") + "')and(OperID='" + this.req.getFieldValue(i,"OperID") + "')";
            int retcount = this.dc.executeUpdate(SQLStr);

            if(retcount < 0) {
                 this.res.setType(0);
                 this.res.setResult(false);
                 this.res.setMessage("É¾³ý¼ÇÂ¼Ê§°Ü");
                 return retcount;
          }

     }

     this.res.setType(0);
     this.res.setResult(true);
     this.res.setMessage("É¾³ý¼ÇÂ¼³É¹¦");

     return 0;
   }
}
