package pub.platform.system.manage.action;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;


public class ActionDeleteAction extends Action
{
    public int doBusiness() {

         PtLogicAct  ptlogic = new PtLogicAct();

         for (int i=0; i<this.req.getRecorderCount();i++ ){

              if ( ptlogic.deleteByWhere("where (LogicCode='"+this.req.getFieldValue(i,"keycode")+"')") <0){
                       this.res.setType(0);
                       this.res.setResult(false);
                       this.res.setMessage("删除记录失败");
                       return -1;
              }


         }

         //重新载入Action数据
         pub.platform.form.control.ActionConfig.getInstance().reload();

         this.res.setType(0);
         this.res.setResult(true);
         this.res.setMessage("删除记录成功");
         return 0;

    }

}
