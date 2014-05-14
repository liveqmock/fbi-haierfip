package pub.platform.system.manage.button;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;
import pub.platform.utils.*;

public class ParentButtonAction extends Action
{

      PtButtonBean ptbut = new PtButtonBean();
     PtResourceBean ptresBean = new PtResourceBean();

     public int doBusiness() {

          String maxCoun ="1";


             for (int i=0; i<this.req.getRecorderCount();i++ ){


                  ////////////////求出菜单主健
                  maxCoun = Util.getFieldMax(this.dc,"BUTTONINDEX","ptbutton");

                  ptbut.setButtonindex(Integer.parseInt(maxCoun));
                  ptbut.setButtonid("modulebuttonstype");
                  ptbut.setButtondesc(this.req.getFieldValue(i,"buttondesc"));
                  ptbut.setButtonlabel(this.req.getFieldValue(i,"buttonlab"));
                  ptbut.setParentbuttonid(this.req.getFieldValue(i,"parentbuttonid"));



                   if (ptbut.insert() <0){
                       this.res.setType(0);
                       this.res.setResult(false);
                       this.res.setMessage("添加Button失败");
                       return -1;
                   }


                    /////////////////添加资源信息
                     ptresBean.setResid("b"+maxCoun);
                     ptresBean.setResdesc(this.req.getFieldValue("buttonlab"));
                     ptresBean.setParentresid("b"+this.req.getFieldValue("parentbuttonid"));
                     ptresBean.setResname(maxCoun);
                     ptresBean.setRestype("5");

                     if (ptresBean.insert() <0){
                         this.res.setType(0);
                         this.res.setResult(false);
                         this.res.setMessage("添加资源失败");
                         return -1;
                    }

             }

             this.res.setType(0);
             this.res.setResult(true);
             this.res.setMessage("添加记录成功");

             return 0;
    }

    ///////////////修改
    public int editenum(){

           for (int i=0; i<this.req.getRecorderCount();i++ ){
                ptbut.setButtondesc(this.req.getFieldValue(i,"buttondesc"));
                ptbut.setButtonlabel(this.req.getFieldValue(i,"buttonlab"));


                   if (ptbut.updateByWhere(" where (buttonindex='"+this.req.getFieldValue(i,"keycode")+"')") <0){
                        this.res.setType(0);
                        this.res.setResult(false);
                        this.res.setMessage("更新记录失败");
                        return -1;
                   }

                    /////////////////更新资源信息
                     ptresBean.setResdesc(this.req.getFieldValue(i,"menulabel"));
                     ptresBean.setParentresid("b"+this.req.getFieldValue(i,"parentmenuid"));

                     if (ptresBean.updateByWhere(" where (ResID='b"+this.req.getFieldValue(i,"keycode")+"')") <0){
                          this.res.setType(0);
                          this.res.setResult(false);
                          this.res.setMessage("更新资源失败");
                          return -1;
                     }

        }

        this.res.setType(0);
        this.res.setResult(true);
        this.res.setMessage("更新记录成功");

        return 0;
    }

    //////////////删除
    public int delenum(){
         for (int i=0; i<this.req.getRecorderCount();i++ ){

               //////////////删除角色资源关系
             this.dc.executeUpdate(" delete from  PTRoleRes where (ResID='b"+this.req.getFieldValue(i,"keycode")+"') ");

                ///////////////删除资源
             this.dc.executeUpdate(" delete from  ptresource where (ResID='b"+this.req.getFieldValue(i,"keycode")+"') ");


               //////////////////删除菜单
              String SQLStr = "delete from PTButton where (buttonindex='"+this.req.getFieldValue(i,"keycode")+"')";
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
    ///////////////////////选出模块信息
    public int getbuttons(){




          String SQLStr = "select  buttonindex,Buttonlabel,Buttondesc,(select  count(buttonindex) as a from ptbutton   where (buttonid ='modulebuttonstype' ) and Parentbuttonid=me.buttonid) as menucount from ptbutton  me where (buttonid='modulebuttonstype') and Parentbuttonid='" +
                      this.req.getFieldValue("parentid") + "'";

          this.rs = this.dc.executeQuery(SQLStr);

          this.res.setFieldName("buttonindex;Buttonlabel;buttondesc;menucount");
          this.res.setFieldType("text;text;text;text");
          this.res.setEnumType("0;0;0;0");
          this.res.setType(1);

          if (this.rs == null) {
               this.res.setResult(false);
               this.res.setMessage("无返回信息记录");
               return -1;

          } else {
               this.res.setResult(true);
               this.res.setRecordset(this.rs);
          }

          return 0;
     }



 }
