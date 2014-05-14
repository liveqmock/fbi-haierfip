package pub.platform.system.manage.dept;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.PtDeptBean;
import pub.platform.utils.Util;

public class DeptInsertAction extends Action {
  public int doBusiness() {

    PtDeptBean ptDeptBean = new PtDeptBean();

    
      String maxCount = Util.getFieldMax(this.dc,"DEPTINDEX","ptdept","");
          
      ptDeptBean.setDeptindex(Integer.parseInt(maxCount));
      
      if (maxCount.length()==2) maxCount ="0"+maxCount;
      
      if (maxCount.length()==1) maxCount ="00"+maxCount;
     
    //ptDeptBean.setDeptid(maxCount);  
    ptDeptBean.setDeptid(this.req.getFieldValue("deptid"));

    ptDeptBean.setDeptname(this.req.getFieldValue("deptname"));
    ptDeptBean.setDeptdesc(this.req.getFieldValue("deptdesc"));
    ptDeptBean.setParentdeptid(this.req.getFieldValue("parentdeptid"));

    ptDeptBean.setFillstr10(this.req.getFieldValue("FILLSTR10"));
    ptDeptBean.setFillstr20(this.req.getFieldValue("FILLSTR20"));
    ptDeptBean.setFillstr100(this.req.getFieldValue("FILLSTR100"));
    ptDeptBean.setFillstr150(this.req.getFieldValue("FILLSTR150"));

    if (ptDeptBean.insert() < 0) {
      this.res.setType(0);
      this.res.setResult(false);
      this.res.setMessage("添加记录失败");
      return -1;
    }
    this.res.setType(0);
    this.res.setResult(true);
    this.res.setMessage("添加部门成功");
    return 0;
  }

}
