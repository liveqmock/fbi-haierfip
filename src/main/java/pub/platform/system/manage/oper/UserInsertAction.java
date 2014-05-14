package pub.platform.system.manage.oper;

import jxl.*;
import java.io.*;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;
import pub.platform.utils.Util;

public class UserInsertAction extends Action
{
    PtOperBean operbean = new PtOperBean();


    public int doBusiness() {

       String  maxCoun="1";
        for (int i=0; i<this.req.getRecorderCount();i++ ){

            maxCoun = Util.getFieldMax(this.dc,"FILLINT6","ptoper");

            if (Integer.parseInt(maxCoun)==1)
               maxCoun ="100000";
            operbean.setFillint6(Integer.parseInt(maxCoun));


            operbean.setDeptid(this.req.getFieldValue(i,"deptid"));
            operbean.setOperid(this.req.getFieldValue(i,"operid"));
            operbean.setOpername(this.req.getFieldValue(i,"opername"));
            //operbean.setOpertype(this.req.getFieldValue(i,"opertype"));
            operbean.setOpertype("2");
            operbean.setOperpasswd(this.req.getFieldValue(i,"operpasswd"));
            operbean.setIssuper(this.req.getFieldValue(i,"issuper"));
            operbean.setSex(this.req.getFieldValue(i,"sex"));
            operbean.setEmail(this.req.getFieldValue(i,"email"));

            operbean.setMobilephone(this.req.getFieldValue(i,"mobilephone"));

            operbean.setOperphone(this.req.getFieldValue(i,"operphone"));

            operbean.setOtherphone(this.req.getFieldValue(i,"otherphone"));
            operbean.setOperenabled(this.req.getFieldValue(i,"operenabled"));

            if (this.req.getFieldValue(i,"fillint12") != null && !this.req.getFieldValue(i,"fillint12").equals(""))
                 operbean.setFillint12(Integer.parseInt(this.req.getFieldValue(i,"fillint12")));


             operbean.setFillstr150(this.req.getFieldValue(i,"fillstr150"));
             operbean.setFillstr600(this.req.getFieldValue(i,"fillstr600"));
             if (operbean.insert() <0){
                 this.res.setType(0);
                 this.res.setResult(false);
                 this.res.setMessage("添加记录失败!");
                 return -1;
             }
       }

        this.res.setType(0);
        this.res.setResult(true);
        this.res.setMessage("添加人员成功");

       return 0;
   }

   public int piliangadd(){
      try{
            Workbook   workbook = Workbook.getWorkbook(new File("e:\\pel.xls"));

            Sheet sheet = workbook.getSheet(0);
            String username="";
            int    userindex=0;
            String userid="";
            String maxCoun="1";
            String deptname="";

             String parentid=this.req.getFieldValue("parentdeptid");


              PtDeptBean ptDeptBean  = new PtDeptBean();


            for (int i=0; i< sheet.getRows(); i++){


                 username= sheet.getCell(0,i).getContents();
                 userid = sheet.getCell(1,i).getContents();

                 userindex = Integer.parseInt(sheet.getCell(2,i).getContents());
                 deptname = sheet.getCell(3,i).getContents();

                 if (ptDeptBean == null)
                      ptDeptBean  = new PtDeptBean();

                 ptDeptBean = (PtDeptBean)ptDeptBean.findFirstAndLockByWhere("where deptid like '"+parentid.substring(0,6)+"%' and DEPTNAME='"+deptname+"'");


                    if (ptDeptBean == null){
                        this.res.setType(0);
                        this.res.setResult(false);
                        this.res.setMessage("部门为空!");
                        return -1;

                    }

                 System.out.println("index"+i+"userid="+userid + "username"+username);



                 operbean.setDeptid(ptDeptBean.getDeptid());
                 operbean.setOperid(userid);

                  maxCoun = Util.getFieldMax(this.dc,"FILLINT6","ptoper");

                  if (Integer.parseInt(maxCoun)==1)
                    maxCoun ="100000";
                  operbean.setFillint6(Integer.parseInt(maxCoun));



                  operbean.setFillint12(userindex);

                  operbean.setOpername(username);
                  operbean.setIssuper("0");

                  operbean.setOperenabled("1");

                  operbean.setOperpasswd("123");


                  if (operbean.insert() <0){
                      this.res.setType(0);
                      this.res.setResult(false);
                      this.res.setMessage("添加记录失败!");
                      return -1;
                  }


            }


      }catch(Exception e){
      }

        this.res.setType(0);
        this.res.setResult(true);
        this.res.setMessage("添加记录成功");

        return 0;

   }



   public int roleadd(){
      try{
            Workbook   workbook = Workbook.getWorkbook(new File("e:\\role.xls"));

            Sheet sheet = workbook.getSheet(0);
            String roleid="";

            PtOperRoleBean ptoperrolebean = new PtOperRoleBean();

            String userid="";


            for (int i=0; i< sheet.getRows(); i++){

                 userid= sheet.getCell(0,i).getContents();
                 roleid = sheet.getCell(1,i).getContents();

                if  (userid.trim().equals(""))
                     break;


               ptoperrolebean.setOperid(userid);
               ptoperrolebean.setRoleid(roleid);

               if (ptoperrolebean.insert() <0){
                   this.res.setType(0);
                   this.res.setResult(false);
                   this.res.setMessage("添加记录失败");
                   return -1;
               }



            }


      }catch(Exception e){
      }

        this.res.setType(0);
        this.res.setResult(true);
        this.res.setMessage("添加记录成功");

        return 0;

   }



   public int deptadd(){
   try{
         Workbook   workbook = Workbook.getWorkbook(new File("e:\\dept.xls"));

         Sheet sheet = workbook.getSheet(0);


         String dpdm="";
         String mkdm="";
         String deptname="";


         PtDeptBean ptDeptBean  = new PtDeptBean();

         String maxCount ="0";

          for (int i=0; i< sheet.getRows(); i++){
              dpdm= sheet.getCell(0,i).getContents();
              mkdm = sheet.getCell(1,i).getContents();
              deptname = sheet.getCell(2,i).getContents();


              if (i==0)
                   maxCount = Util.getFieldMax(this.dc,"DEPTINDEX","ptdept"," where dqdm='"+dpdm.trim()+"' and mkdm='"+mkdm.trim()+"'");
               else
                    maxCount = String.valueOf(Integer.parseInt(maxCount)+1);

              ptDeptBean.setDeptindex(Integer.parseInt(maxCount));

              if (maxCount.length()==2)
                   maxCount ="0"+maxCount;

              if (maxCount.length()==1)
                   maxCount ="00"+maxCount;


                   System.out.println("dept");
                   System.out.println(dpdm.trim()+mkdm.trim()+maxCount);

              ptDeptBean.setDeptid(dpdm.trim()+mkdm.trim()+maxCount);

              ptDeptBean.setDqdm(dpdm.trim());
              ptDeptBean.setMkdm(mkdm.trim());


              ptDeptBean.setDeptname(deptname.trim());

              ptDeptBean.setParentdeptid(this.req.getFieldValue("parentdeptid"));

              if (ptDeptBean.insert() < 0){
                   this.res.setType(0);
                   this.res.setResult(false);
                   this.res.setMessage("添加记录失败");
                   return -1;

              }

         }

   }catch(Exception e){
   }

     this.res.setType(0);
     this.res.setResult(true);
     this.res.setMessage("添加记录成功");

     return 0;

}

   public int birthdayadd(){
    try{
          Workbook   workbook = Workbook.getWorkbook(new File("e:\\birthday.xls"));

          Sheet sheet = workbook.getSheet(0);


          String operid="";
          String birthday="";
          

          PtOperBean operbean = new PtOperBean();

           for (int i=0; i< sheet.getRows(); i++){
               operid= sheet.getCell(0,i).getContents();
               birthday = sheet.getCell(2,i).getContents();
  System.out.println(birthday);
               operbean.setFilldate1("1965-"+birthday);

              

               if (operbean.updateByWhere("where OPERID= '"+operid+"'")<0){
                    this.res.setType(0);
                    this.res.setResult(false);
                    this.res.setMessage("更新生日记录失败");
                    return -1;

               }

          }

    }catch(Exception e){
    }

      this.res.setType(0);
      this.res.setResult(true);
      this.res.setMessage("更新生日记录成功");

      return 0;

 }



}
