package pub.platform.system.manage.pubenum;

import pub.platform.db.*;
import pub.platform.form.control.Action;


public class EnumDeleteAction extends Action
{
    public int doBusiness() {

        String SQLStr = "select  EnuType from  ptEnuDetail where (EnuType='"+this.req.getFieldValue("keycode")+"') ";


        this.rs = this.dc.executeQuery(SQLStr);

	   if (!this.rs.next()){
             SQLStr = "delete from ptEnuMain where (EnuType='"+this.req.getFieldValue("keycode")+"')";
             int retcount =this.dc.executeUpdate(SQLStr);
             if (retcount <0){
                  this.res.setType(0);
                  this.res.setResult(false);
                  this.res.setMessage("删除记录失败");
                  return retcount;
             } else {

                  //重新载入枚举数据
                  pub.platform.form.config.EnumerationType.reload();

                  this.res.setType(0);
                  this.res.setResult(true);
                  this.res.setMessage("删除记录成功");
                  return 0;
             }
	   }else{
             this.res.setType(0);
             this.res.setResult(false);
             this.res.setMessage("存在下级枚举信息");
             return -1;

        }

    }

}
