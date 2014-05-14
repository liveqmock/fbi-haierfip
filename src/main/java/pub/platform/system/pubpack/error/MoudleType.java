package pub.platform.system.pubpack.error;
import pub.platform.form.control.Action;
public class MoudleType extends Action{
     public int doBusiness() {


          String SQLStr = "select  ENUITEMVALUE,ENUITEMLABEL,ENUITEMDESC  from PTENUDETAIL  where ENUTYPE='CM_YWLX' " ;
          this.rs = this.dc.executeQuery(SQLStr);

          this.res.setFieldName("id;name;desc");
          this.res.setFieldType("text;text;text");
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
