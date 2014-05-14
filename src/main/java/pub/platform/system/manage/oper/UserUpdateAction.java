package pub.platform.system.manage.oper;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class UserUpdateAction extends Action {
  PtOperBean operbean = new PtOperBean();
  PtOperChange operChane = new PtOperChange();

  public int doBusiness() {

    for (int i = 0; i < this.req.getRecorderCount(); i++) {

      // operbean =
      // (PtOperBean)operbean.findFirstByWhere("where (operid='"+this.req.getFieldValue(i,"keycode")+"')");
      // if (operbean != null){
      // operChane.setOpersign(operbean.getFillint6());
      // operChane.setOlddeptid(operbean.getDeptid());
      // operChane.setDeptid(this.req.getFieldValue(i,"deptid"));
      // operChane.setOperid(this.req.getFieldValue(i,"operid"));
      // operChane.setOldoperid(operbean.getOperid());
      // if (operChane.insert() <0){
      // this.res.setType(0);
      // this.res.setResult(false);
      // this.res.setMessage("人员备份失败");
      // return -1;
      // }
      // }
      //				
      //				
      // operbean = new PtOperBean();

      if (this.req.getFieldValue(i, "operid").trim().toLowerCase().equals(
          this.req.getFieldValue(i, "keycode").trim().toLowerCase())) {

        operbean.setDeptid(this.req.getFieldValue(i, "deptid"));
        operbean.setOpername(this.req.getFieldValue(i, "opername"));
        operbean.setOpertype("2");
        // operbean.setOpertype(this.req.getFieldValue(i,"opertype"));
        operbean.setOperpasswd(this.req.getFieldValue(i, "operpasswd"));
        operbean.setIssuper(this.req.getFieldValue(i, "issuper"));
        operbean.setSex(this.req.getFieldValue(i, "sex"));
        operbean.setEmail(this.req.getFieldValue(i, "email"));
        operbean.setMobilephone(this.req.getFieldValue(i, "mobilephone"));
        operbean.setOperphone(this.req.getFieldValue(i, "operphone"));
        operbean.setOtherphone(this.req.getFieldValue(i, "otherphone"));
        operbean.setOperenabled(this.req.getFieldValue(i, "operenabled"));

        if (this.req.getFieldValue(i, "fillint12") != null && !this.req.getFieldValue(i, "fillint12").equals(""))
          operbean.setFillint12(Integer.parseInt(this.req.getFieldValue(i, "fillint12")));

        operbean.setFillstr150(this.req.getFieldValue(i, "fillstr150"));
        operbean.setFillstr600(this.req.getFieldValue(i, "fillstr600"));

        if (operbean.updateByWhere("where (operid='" + this.req.getFieldValue(i, "keycode") + "')") < 0) {
          this.res.setType(0);
          this.res.setResult(false);
          this.res.setMessage("更新记录失败");
          return -1;
        }
      } else {
        String SQLStr = "delete  from  PTOperRole where (OperID='" + this.req.getFieldValue(i, "keycode") + "') ";

        if (this.dc.executeUpdate(SQLStr) < 0) {
          this.res.setType(0);
          this.res.setResult(false);
          this.res.setMessage("该用户删除分配角色失败!");
          return -1;

        }

        operbean.setDeptid(this.req.getFieldValue(i, "deptid"));
        operbean.setOperid(this.req.getFieldValue(i, "operid"));
        operbean.setOpername(this.req.getFieldValue(i, "opername"));
        operbean.setOpertype(this.req.getFieldValue(i, "opertype"));
        operbean.setOperpasswd(this.req.getFieldValue(i, "operpasswd"));
        operbean.setIssuper(this.req.getFieldValue(i, "issuper"));
        operbean.setSex(this.req.getFieldValue(i, "sex"));
        operbean.setEmail(this.req.getFieldValue(i, "email"));
        operbean.setMobilephone(this.req.getFieldValue(i, "mobilephone"));
        operbean.setOperphone(this.req.getFieldValue(i, "operphone"));
        operbean.setOtherphone(this.req.getFieldValue(i, "otherphone"));
        operbean.setOperenabled(this.req.getFieldValue(i, "operenabled"));

        if (operbean.updateByWhere("where (operid='" + this.req.getFieldValue(i, "keycode") + "')") < 0) {
          this.res.setType(0);
          this.res.setResult(false);
          this.res.setMessage("更新记录失败");
          return -1;
        }

      }
    }

    this.res.setType(0);
    this.res.setResult(true);
    this.res.setMessage("修改人员成功");

    return 0;
  }

}
