package pub.platform.system.pubpack.changeSelect;


import pub.platform.form.control.Action;
import pub.platform.utils.Basic;
import pub.platform.utils.Util;


public class ChangeSelect extends Action
{



     public int doBusiness() {


          String SQLStr = Basic.decode(this.req.getFieldValue("selectsql"));
          System.out.println(SQLStr);

          this.res.setType(1);

          if (SQLStr != null && !SQLStr.equals("")){

               this.rs = this.dc.executeQuery(SQLStr);
               String fieldname="";
               String fieldtype="";
               String enumtype="";


               for (int i=1 ;i <= this.rs.getfieldCount(); i++){




                    if (fieldname.equals("")){
                         fieldname = this.rs.getMetaData(i).getName().toLowerCase();
                         fieldtype="text";
                         enumtype ="0";
                    }else{
                         fieldname = fieldname+";"+this.rs.getMetaData(i).getName().toLowerCase();
                         fieldtype=fieldtype+";text";
                         enumtype =enumtype+";0";

                    }
               }


               this.res.setFieldName(fieldname);
               this.res.setFieldType(fieldtype);
               this.res.setEnumType(enumtype);


               if (this.rs == null) {
                    this.res.setResult(false);
                    this.res.setMessage("无返回信息记录");
                    return -1;

               } else {
                    this.res.setResult(true);
                    this.res.setRecordset(this.rs);
               }
          }else{
               this.res.setResult(false);
               this.res.setMessage("请检查你的输入项");
               return -1;

          }


          return 0;

    }
     
     
     
	 
	   public int gettUserSerial(){
    	
    	
    	String operID = this.req.getFieldValue("userid");
    	
    	String userSerial = Util.getUserSerial(this.dc,operID);
    	
    	this.setHtml(userSerial);
         
        
    	 return 0;
    	
    }

    ///////////////////////选出模块信息



 }
