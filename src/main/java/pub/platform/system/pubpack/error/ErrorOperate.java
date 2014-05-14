package pub.platform.system.pubpack.error;

import pub.platform.form.control.*;
import pub.platform.system.manage.dao.*;

public class ErrorOperate extends Action{
   PterrorBean pterrorbean = new PterrorBean();

     public int adderror(){


          for (int i=0; i<this.req.getRecorderCount();i++ ){
               pterrorbean.setMoudleid(this.req.getFieldValue(i, "moudleid"));
               pterrorbean.setErrortype(this.req.getFieldValue(i, "errortype"));
               pterrorbean.setErrorindex(this.req.getFieldValue(i, "errorindex"));
               pterrorbean.setMessage(this.req.getFieldValue(i, "message"));

               if (pterrorbean.insert()<0){
                    this.setMessage("GGMK",this.logMamager.ERR_TYPE_BAD,"001");

                    return -1;

               }

          }

          this.setMessage("GGMK","0","001");

          return 0;
    }


    public int editerror(){

          for (int i=0; i<this.req.getRecorderCount();i++ ){
               String wehreStr="where Moudleid='"+this.req.getFieldValue(i, "moudleid")+"' and Errortype='"+this.req.getFieldValue(i, "errortype")+"' and Errorindex='"+this.req.getFieldValue(i, "errorindex")+"'";
               pterrorbean.setMessage(this.req.getFieldValue(i, "message"));
               if (pterrorbean.updateByWhere(wehreStr) <0 ){
                    this.setMessage("GGMK","1","002");
                    return -1;




               }
          }

          this.setMessage("GGMK","0","002");
          return 0;
    }


    public int delerror(){
     
           for (int i=0; i<this.req.getRecorderCount();i++ ){

                String wehreStr="where Moudleid='"+this.req.getFieldValue(i, "moudleid")+"' and Errortype='"+this.req.getFieldValue(i, "errortype")+"' and Errorindex='"+this.req.getFieldValue(i, "errorindex")+"'";
              
                if (pterrorbean.deleteByWhere(wehreStr) <0 ){
                     this.setMessage("GGMK","1","003");
                     return -1;
//
                }

           }

           this.setMessage("GGMK","0","003");
           return 0;
     }




}
