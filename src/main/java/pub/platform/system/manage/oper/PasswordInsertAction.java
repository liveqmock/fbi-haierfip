package pub.platform.system.manage.oper;

import pub.platform.form.control.*;
import pub.platform.system.manage.dao.*;

public class PasswordInsertAction extends Action {
     PtOperBean operbean = new PtOperBean();

     public int doBusiness() {
           
           operbean.setOperpasswd(this.req.getFieldValue("newpwd"));
           
           this.res.setType(0);
           
           if (operbean.updateByWhere(" where (deptid='"+this.req.getFieldValue("deptid")+"')and(operid='"+this.req.getFieldValue("operid")+"')") <0){

             this.res.setResult(false);
             this.res.setMessage("更新密码失败！");
             this.dc.close();
             return -1;
           }

         

          this.res.setResult(true);
          this.res.setMessage("更新密码成功！");
          this.dc.close();
          return 0;
     }


}
