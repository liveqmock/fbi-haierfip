package pub.platform.system.manage.action;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class ActionUpdateAction extends Action
{
    public int doBusiness() {

          PtLogicAct  ptlogic = new PtLogicAct();

		for (int i=0; i<this.req.getRecorderCount();i++ ){

               ptlogic.setLogiccode(this.req.getFieldValue(i,"LogicCode"));
               ptlogic.setLogicclass(this.req.getFieldValue(i,"LogicClass"));
               ptlogic.setLogicmethod(this.req.getFieldValue(i,"LogicMethod"));
               ptlogic.setLogicdesc(this.req.getFieldValue(i,"LogicDesc"));


		   if (ptlogic.updateByWhere("where (LogicCode='"+this.req.getFieldValue(i,"keycode")+"')") <0){
			   this.res.setType(0);
			   this.res.setResult(false);
			   this.res.setMessage("更新记录失败");
			   return -1;
		   }
       }

       //重新载入Action数据
       pub.platform.form.control.ActionConfig.getInstance().reload();

       this.res.setType(0);
       this.res.setResult(true);
       this.res.setMessage("更新记录成功");

       return 0;
   }


}
