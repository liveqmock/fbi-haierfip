package pub.platform.system.manage.resRole;

import pub.platform.form.control.Action;

public class ResRoleInsertAction extends Action{
    private String  SQLStr;
    private String  RoleID;
    private String  ResID;

     public int doBusiness() throws Exception {

       for (int i=0; i<this.req.getRecorderCount();i++ ){


            RoleID = this.req.getFieldValue(i,"RoleID");
            ResID  =  this.req.getFieldValue(i,"ResID");

           SQLStr = "delete from PTRoleRes where (RoleID='" + RoleID + "')and(ResID='" + ResID + "')";

           int retcount = this.dc.executeUpdate(SQLStr);
            if(retcount < 0) {
                 this.res.setType(0);
                 this.res.setResult(false);
                 this.res.setMessage("添加记录失败");
                 return retcount;
          }

         SQLStr = "insert into  PTRoleRes (RoleID,ResID) values( '"+RoleID+ "' ,'"+ResID+ "')";


        retcount =this.dc.executeUpdate(SQLStr);
        ///////////添加子节点
        //insertChild(RoleID,ResID);

        //////////添加父节点
        insertParent(RoleID,ResID);


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


    private void insertChild(String RoleID , String ResID) throws Exception {
         SQLStr = "  select ResID  from PTResource   where ParentResID='" +ResID + "'";
         this.rs = this.dc.executeQuery(SQLStr);

         while (this.rs.next()){
        	 	SQLStr = "delete from PTRoleRes where (RoleID='" + RoleID + "')and(ResID='" +this.rs.getString("ResID") + "')";
        	 	this.dc.executeUpdate(SQLStr);
        	 	
              SQLStr = "insert into  PTRoleRes (RoleID,ResID) values( '"+RoleID+ "' ,'"+this.rs.getString("ResID")+ "')";

              if (this.dc.executeUpdate(SQLStr) <0)
                   throw new Exception("添加记录失败");

         }
    }

    private void insertParent(String RoleID , String ResID) throws Exception {


        SQLStr = "  select ParentResID from PTResource   where ResID='" +ResID + "'";

        this.rs = this.dc.executeQuery(SQLStr);

        while (this.rs.next()){
             if (!this.rs.getString("ParentResID").equals("null")){

            	 
            	 SQLStr = "delete from PTRoleRes where (RoleID='" + RoleID + "')and(ResID='" +this.rs.getString("ParentResID") + "')";
         	 	this.dc.executeUpdate(SQLStr);
         	 	
         	 	
                  SQLStr = "insert into  PTRoleRes (RoleID,ResID) values( '"+RoleID+ "' ,'"+this.rs.getString("ParentResID")+ "')"; 



                  if (this.dc.executeUpdate(SQLStr) <0)
                       throw new Exception("添加记录失败");
                  else
                      insertParent(RoleID,this.rs.getString("ParentResID")) ;
            }

        }
   }

   ////////////////////删除
   public int delenum(){
        for (int i=0; i<this.req.getRecorderCount();i++ ){
                String  SQLStr = "delete from PTRoleRes where (ResID='" + this.req.getFieldValue(i,"ResID") + "')and(RoleID='" + this.req.getFieldValue(i,"RoleID") + "')";
                 int retcount = this.dc.executeUpdate(SQLStr);

                 if(retcount < 0) {
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
