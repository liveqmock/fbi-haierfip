package pub.platform.system.manage.action;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class ActionInsertAction extends Action
{

     PtLogicAct  ptlogic = new PtLogicAct();

     public int doBusiness() {
          return 0;

         

     }



   public int addenum(){
        for (int i=0; i<this.req.getRecorderCount();i++ ){
                 ptlogic.setLogiccode(this.req.getFieldValue(i,"LogicCode"));
                 ptlogic.setLogicclass(this.req.getFieldValue(i,"LogicClass"));
                 ptlogic.setLogicmethod(this.req.getFieldValue(i,"LogicMethod"));
                 ptlogic.setLogicdesc(this.req.getFieldValue(i,"LogicDesc"));


                 if (ptlogic.insert() <0){
                     this.res.setType(0);
                     this.res.setResult(false);
                     this.res.setMessage("添加记录失败");
                     return -1;
                 }
           }

           //重新载入Action数据
           pub.platform.form.control.ActionConfig.getInstance().reload();

           this.res.setType(0);
           this.res.setResult(true);
           this.res.setMessage("添加记录成功");

           return 0;

   }

   public int editenum(){
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

     public int delenum(){

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
