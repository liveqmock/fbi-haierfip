package pub.platform.system.manage.resRole;

import pub.platform.db.*;
import pub.platform.form.control.Action;


public class RoleSelectAction extends Action {
     public int doBusiness() {

         String SQLStr = " select RoleID,RoleDesc from PTRole order by  RoleDesc";

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
