package pub.platform.system.manage.dept;

import pub.platform.db.*;
import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class DeptUpdateAction extends Action {
    public int doBusiness() {


              PtDeptBean ptDeptBean  = new PtDeptBean();

              ptDeptBean.setDeptname(this.req.getFieldValue("deptname"));
              ptDeptBean.setDeptdesc(this.req.getFieldValue("deptdesc"));

              ptDeptBean.setFillstr10(this.req.getFieldValue("FILLSTR10"));
              ptDeptBean.setFillstr20(this.req.getFieldValue("FILLSTR20"));
              ptDeptBean.setFillstr100(this.req.getFieldValue("FILLSTR100"));
              ptDeptBean.setFillstr150(this.req.getFieldValue("FILLSTR150"));


               if (ptDeptBean.updateByWhere(" where (deptid='" + this.req.getFieldValue("deptid") + "')") < 0) {
                   this.res.setType(0);
                   this.res.setResult(false);
                   this.res.setMessage("修改记录失败");
                   return -1;
               }

               this.res.setType(0);
               this.res.setResult(true);
               this.res.setMessage("修改记录成功");

               return 0;

    }

}
