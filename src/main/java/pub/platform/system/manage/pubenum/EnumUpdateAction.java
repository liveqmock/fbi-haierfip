package pub.platform.system.manage.pubenum;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class EnumUpdateAction extends Action
{

    PtEnuMainBean ptmainbean = new PtEnuMainBean();

     public int doBusiness() {


		for (int i=0; i<this.req.getRecorderCount();i++ ){


              ptmainbean.setEnutype(this.req.getFieldValue(i,"EnuType"));
              ptmainbean.setEnuname(this.req.getFieldValue(i,"EnuName"));
              ptmainbean.setValuetype(this.req.getFieldValue(i,"ValueType"));
              ptmainbean.setEnudesc(this.req.getFieldValue(i,"EnuDesc"));



             if (ptmainbean.updateByWhere(" where (EnuType='"+this.req.getFieldValue(i,"keycode")+"')") <0){
                  this.res.setType(0);
                  this.res.setResult(false);
                  this.res.setMessage("更新记录失败");
                  return -1;
             }
       }

       //重新载入枚举数据
       pub.platform.form.config.EnumerationType.reload();

       this.res.setType(0);
       this.res.setResult(true);
       this.res.setMessage("更新记录成功");

       return 0;
   }


}
