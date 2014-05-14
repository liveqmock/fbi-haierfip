package pub.platform.system.manage.oper;

import pub.platform.form.control.*;

public class UserSelectAction extends Action {
     public int doBusiness() {

          String SQLStr = "select  operid,opername,operpasswd,issuper,sex,opertype,email,mobilephone,operphone,otherphone,operenabled,fillint12,fillstr150,fillstr600 from ptoper where(deptid='"+this.req.getFieldValue("deptid")+"')and(operid='"+this.req.getFieldValue("keycode")+"')";


          this.rs = this.dc.executeQuery(SQLStr);

          this.res.setFieldName("operid;opername;operpasswd;issuper;sex;opertype;email;mobilephone;operphone;otherphone;operenabled;fillint12;fillstr150;fillstr600");
          this.res.setFieldType("text;text;text;text;text;text;text;text;text;text;text;text;text;text");
          this.res.setEnumType("0;0;0;0;0;0;0;0;0;0;0;0;0;0");
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
     ///根据部门id（上级部门）获取一个人员id
     public int getOnlyUserID(){
    	 String parentid="";
    	 
    	 String SqlStr = "select parentdeptid from ptdept where  (deptid='"+this.req.getFieldValue("deptid")+"')and (ptdept.mkdm = '90')";
    	 this.rs = this.dc.executeQuery(SqlStr);
    	 
    	 if (this.rs.next()){
			 parentid = this.rs.getString(0);    			 
		 }else
			 parentid = this.req.getFieldValue("deptid");  	 
    	 
    	 
    	 SqlStr = "select operid from ptoper where  rownum <=1 and  (operid <>'"+this.req.getFieldValue("operid")+"')and(deptid='"+parentid+"')";    	 
    	 
    	 this.rs = this.dc.executeQuery(SqlStr);
    	 
    	
    	 
    	 this.res.setType(this.res.HTML_TYPE);
    	 this.res.setResult(true);
    	 if (this.rs.next())
    		 this.res.setMessage(this.rs.getString(0));
    	 else
    		 this.res.setMessage("false");
    	 
    	 return 0;
    	 
    	 
     }
}
