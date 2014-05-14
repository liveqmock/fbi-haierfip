package pub.platform.system.manage.resource;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class ResourceUpdateAction extends Action
{

     PtResourceBean  prresbean = new PtResourceBean();

     public int doBusiness() {


		for (int i=0; i<this.req.getRecorderCount();i++ ){

               if (this.req.getFieldValue(i,"ResID").trim().toLowerCase().equals(this.req.getFieldValue(i,"keycode").trim().toLowerCase()))
               {



                     prresbean.setParentresid(this.req.getFieldValue(i,"ParentResID"));
                     prresbean.setRestype(this.req.getFieldValue(i,"ResType"));
                     prresbean.setResname(this.req.getFieldValue(i,"ResName"));
                     prresbean.setResdesc(this.req.getFieldValue(i,"ResDesc"));

                    if (prresbean.updateByWhere(" where (ResID='"+this.req.getFieldValue(i,"keycode")+"')") <0){
                       this.res.setType(0);
                       this.res.setResult(false);
                       this.res.setMessage("更新记录失败");
                       return -1;
                    }
             }else{


                 String SQLStr = "select  * from  PTRoleRes where (ResID='"+this.req.getFieldValue(i,"keycode")+"') ";


                 this.rs = this.dc.executeQuery(SQLStr);

                 if (this.rs.next()){
                      this.res.setType(0);
                      this.res.setResult(false);
                      this.res.setMessage("该角色已分配资源无法更新主健！");
                      return -1;

                 }

                    prresbean.setResid(this.req.getFieldValue(i,"ResID"));
                    prresbean.setParentresid(this.req.getFieldValue(i,"ParentResID"));
                    prresbean.setRestype(this.req.getFieldValue(i,"ResType"));
                    prresbean.setResname(this.req.getFieldValue(i,"ResName"));
                    prresbean.setResdesc(this.req.getFieldValue(i,"ResDesc"));

                   if (prresbean.updateByWhere(" where (ResID='"+this.req.getFieldValue(i,"keycode")+"')") <0){
                      this.res.setType(0);
                      this.res.setResult(false);
                      this.res.setMessage("更新记录失败");
                      return -1;
                   }


             }
        }

       this.res.setType(0);
       this.res.setResult(true);
       this.res.setMessage("更新记录成功");

       return 0;
   }


}
