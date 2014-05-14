package pub.platform.system.manage.pubenum;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class EnumInsertAction extends Action
{
    PtEnuMainBean ptmainbean = new PtEnuMainBean();
     public int doBusiness() {
       return 0;
     }

   //////////////添加一个枚举
   public int addenum(){


          for (int i=0; i<this.req.getRecorderCount();i++ ){

                  ptmainbean.setEnutype(this.req.getFieldValue(i,"EnuType"));
                  ptmainbean.setEnuname(this.req.getFieldValue(i,"EnuName"));
                  ptmainbean.setValuetype(this.req.getFieldValue(i,"ValueType"));
                  ptmainbean.setEnudesc(this.req.getFieldValue(i,"EnuDesc"));


                if (ptmainbean.insert() <0){
                    this.res.setType(0);
                    this.res.setResult(false);
                    this.res.setMessage("添加记录失败");
                    return -1;
                }
          }

          //重新载入枚举数据
          pub.platform.form.config.EnumerationType.reload();

          this.res.setType(0);
          this.res.setResult(true);
          this.res.setMessage("添加记录成功");

          return 0;

   }

   /////////////修改一个枚举
   public int editenum(){
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
   ///////////////////删除一个枚举
   public int delenum(){
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
