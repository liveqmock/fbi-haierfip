package pub.platform.system.manage.dept;

import pub.platform.db.*;
import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;




public class DeptDeleteAction extends Action
{
    public int doBusiness() {

     PtDeptBean ptDeptBean  = new PtDeptBean();
     PtOperBean ptOperBean =  new PtOperBean();
     
    
     
     
     

	this.rs = ptDeptBean.findRecordSetByWhere("where (parentdeptid='"+this.req.getFieldValue("deptid")+"') order by deptid ");

	if (!this.rs.next()){

           this.rs = ptOperBean.findRecordSetByWhere(" where (deptid='"+this.req.getFieldValue("deptid")+"') ");

           if (!this.rs.next()){
                if(ptDeptBean.deleteByWhere("where (deptid='" + this.req.getFieldValue("deptid") + "')") < 0) {
                     this.res.setType(0);
                     this.res.setResult(false);
                     this.res.setMessage("删除记录失败");
                     return -1;
                }
           }else {
                this.res.setType(0);
                this.res.setResult(false);
                this.res.setMessage("该部门存在人员");
                return -1;
           }

	}else{
	    this.res.setType(0);
	    this.res.setResult(false);
	    this.res.setMessage("存在下级部门");
	    return -1;
	}
	 this.res.setType(0);
	 this.res.setResult(true);
	 this.res.setMessage("删除部门成功");

	return 0;
    }

}
