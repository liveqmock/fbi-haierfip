package pub.platform.system.manage.oper;

import pub.platform.db.*;
import pub.platform.form.control.Action;


public class UserDeleteAction extends Action
{
    public int doBusiness() {

	for (int i=0; i<this.req.getRecorderCount();i++ ){

          this.dc.executeUpdate( " delete from  PTOperRole where (OperID='"+this.req.getFieldValue(i,"keycode")+"')" );

          String SQLStr = "select  * from  PTOperRole where (OperID='"+this.req.getFieldValue(i,"keycode")+"') ";


          this.rs = this.dc.executeQuery(SQLStr);
          if (!this.rs.next()){
              SQLStr = "delete from ptoper where (deptid='"+this.req.getFieldValue(i,"deptid")+"')and(operid='"+this.req.getFieldValue(i,"keycode")+"')";
              int retcount =this.dc.executeUpdate(SQLStr);
              if (retcount <0){
                   this.res.setType(0);
                   this.res.setResult(false);
                   this.res.setMessage("删除记录失败");
                   return retcount;
              }
         }else{
              this.res.setType(0);
              this.res.setResult(false);
              this.res.setMessage("用户角色删除失败!");
              return -1;

          }

	}
	 this.res.setType(0);
     this.res.setResult(true);
     this.res.setMessage("删除人员成功");

	return 0;
    }


   public void delete(){
        System.out.println("WWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
   }


}
