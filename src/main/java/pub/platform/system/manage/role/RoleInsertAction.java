package pub.platform.system.manage.role;

import pub.platform.form.control.Action;

public class RoleInsertAction extends Action
{
    public int doBusiness() {

       for (int i=0; i<this.req.getRecorderCount();i++ ){

	    String SQLStr = "insert into  PTRole (RoleID,RoleDesc)";
	           SQLStr += "values('"+this.req.getFieldValue(i,"RoleID")+"','"+this.req.getFieldValue(i,"RoleDesc")+"')";

	   int retcount =this.dc.executeUpdate(SQLStr);
	   if (retcount <0){
	       this.res.setType(0);
	       this.res.setResult(false);
	       this.res.setMessage("添加记录失败");
	       return retcount;
	   }
       }

       this.res.setType(0);
       this.res.setResult(true);
       this.res.setMessage("添加记录成功");

       return 0;
   }



   public int editenum(){
        for (int i=0; i<this.req.getRecorderCount();i++ ){

               if (this.req.getFieldValue(i,"RoleID").trim().toLowerCase().equals(this.req.getFieldValue(i,"keycode").trim().toLowerCase()))
               {
                    String SQLStr = "update PTRole set RoleDesc='"+this.req.getFieldValue(i,"RoleDesc")+"'  where (RoleID='"+this.req.getFieldValue(i,"keycode")+"')";

                    System.out.print(SQLStr);
                    int retcount = this.dc.executeUpdate(SQLStr);

                    if (retcount <0){
                       this.res.setType(0);
                       this.res.setResult(false);
                       this.res.setMessage("更新记录失败");
                       return retcount;
                  }
             }else{
                  String SQLStr = "select  * from  PTOperRole where (RoleID='"+this.req.getFieldValue(i,"keycode")+"') ";
                  System.out.print(SQLStr);

                  this.rs = this.dc.executeQuery(SQLStr);

                  if (this.rs.next()){
                       this.res.setType(0);
                       this.res.setResult(false);
                       this.res.setMessage("该角色已分配人员无法更新主健！");
                       return -1;

                  }

                  SQLStr = "select  * from  PTRoleRes where (RoleID='"+this.req.getFieldValue(i,"keycode")+"') ";
                 System.out.print(SQLStr);

                 this.rs = this.dc.executeQuery(SQLStr);

                 if (this.rs.next()){
                      this.res.setType(0);
                      this.res.setResult(false);
                      this.res.setMessage("该角色已分配资源无法更新主健！");
                      return -1;

                 }
                 SQLStr = "update PTRole set RoleID='"+this.req.getFieldValue(i,"RoleID")+"' , RoleDesc='"+this.req.getFieldValue(i,"RoleDesc")+"'  where (RoleID='"+this.req.getFieldValue(i,"keycode")+"')";

                   System.out.print(SQLStr);
                   int retcount = this.dc.executeUpdate(SQLStr);

                   if (retcount <0){
                      this.res.setType(0);
                      this.res.setResult(false);
                      this.res.setMessage("更新记录失败");
                      return retcount;
                 }
             }
        }

       this.res.setType(0);
       this.res.setResult(true);
       this.res.setMessage("更新记录成功");

       return 0;


   }

    public int delenum(){
         for (int i=0; i<this.req.getRecorderCount();i++ ){

              this.dc.executeUpdate("delete from  PTOperRole where (RoleID='"+this.req.getFieldValue(i,"keycode")+"')" );

              this.dc.executeUpdate("delete from  PTRoleRes where (RoleID='"+this.req.getFieldValue(i,"keycode")+"')" );

              String SQLStr = "select  * from  PTOperRole where (RoleID='"+this.req.getFieldValue(i,"keycode")+"') ";


              this.rs = this.dc.executeQuery(SQLStr);

              if (!this.rs.next()){

                   SQLStr = "select  * from  PTRoleRes where (RoleID='"+this.req.getFieldValue(i,"keycode")+"') ";


                  this.rs = this.dc.executeQuery(SQLStr);
                  if (!this.rs.next()){
                       SQLStr = "delete from PTRole where (RoleID='" + this.req.getFieldValue(i,"keycode") + "')";
                       int retcount = this.dc.executeUpdate(SQLStr);
                       if(retcount < 0) {
                            this.res.setType(0);
                            this.res.setResult(false);
                            this.res.setMessage("删除记录失败");
                            return retcount;
                       }

                  }else{
                       this.res.setType(0);
                       this.res.setResult(false);
                       this.res.setMessage("角色资源删除失败");
                       return -1;

                  }

             }else{
                  this.res.setType(0);
                  this.res.setResult(false);
                  this.res.setMessage("人员角色删除失败");
                  return -1;

             }

         }

         this.res.setType(0);
         this.res.setResult(true);
         this.res.setMessage("删除记录成功");
         return 0;

    }



}
