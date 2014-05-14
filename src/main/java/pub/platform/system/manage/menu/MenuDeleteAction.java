package pub.platform.system.manage.menu;

import pub.platform.form.control.Action;


public class MenuDeleteAction extends Action
{
    public int doBusiness() {
         for (int i=0; i<this.req.getRecorderCount();i++ ){

              //////////////删除角色资源关系
            this.dc.executeUpdate(" delete from  PTRoleRes where (ResID='m"+this.req.getFieldValue(i,"keycode")+"') ");

               ///////////////删除资源
            this.dc.executeUpdate(" delete from  ptresource where (ResID='m"+this.req.getFieldValue(i,"keycode")+"') ");


              //////////////////删除菜单
             String SQLStr = "delete from PTMenu where (MenuID='"+this.req.getFieldValue(i,"keycode")+"')";
             int retcount =this.dc.executeUpdate(SQLStr);
             if (retcount <0){
                  this.res.setType(0);
                  this.res.setResult(false);
                  this.res.setMessage("删除记录失败");
                  return retcount;
             }

         }
         this.res.setType(0);
         this.res.setResult(true);
         this.res.setMessage("删除记录成功");
         return 0;

    }

}
