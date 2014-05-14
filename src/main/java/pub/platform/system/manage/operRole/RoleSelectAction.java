package pub.platform.system.manage.operRole;

import pub.platform.db.*;
import pub.platform.form.control.Action;

public class RoleSelectAction extends Action {
     public int doBusiness() {

    	 String operID = this.req.getFieldValue("operid");
    	 
         String SQLStr = "  select RoleID ,RoleDesc  from PTRole where roleid not in (select roleid from ptoperrole where operid='"+operID+"') order by RoleDesc ";

//         if (this.getOperator().getOpertype() != null && this.getOperator().getOpertype() .equals("2")){
//                    if (this.getOperator().getIssuper().equals("1"))
//                         SQLStr =" select PTRole.RoleID ,PTRole.RoleDesc  from PTRole,ptoperrole where PTRole.roleid = ptoperrole.roleid and ptoperrole.operid='"+this.getOperator().getOperid()+"' and PTRole.roleid not in (select roleid from ptoperrole where operid='"+operID+"') order by PTRole.RoleDesc";
//                    else
//                         SQLStr =" select PTRole.RoleID ,PTRole.RoleDesc  from PTRole,ptoperrole where 1>1 ";
//
//         }



          this.rs = this.dc.executeQuery(SQLStr);

          this.res.setFieldName("RoleID;RoleDesc");
          this.res.setFieldType("text;text");
            this.res.setEnumType("0;0");
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
