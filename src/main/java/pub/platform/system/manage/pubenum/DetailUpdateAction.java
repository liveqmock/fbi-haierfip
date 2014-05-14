package pub.platform.system.manage.pubenum;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class DetailUpdateAction extends Action
{
    public int doBusiness() {

          PtEnuDetailBean ptdetai = new PtEnuDetailBean();


		for (int i=0; i<this.req.getRecorderCount();i++ ){


               ptdetai.setEnutype(this.req.getFieldValue(i,"EnuType"));
               ptdetai.setEnuitemvalue(this.req.getFieldValue(i,"EnuItemValue"));
               ptdetai.setEnuitemlabel(this.req.getFieldValue(i,"Enuitemlabel"));
               ptdetai.setEnuitemdesc(this.req.getFieldValue(i,"EnuItemDesc"));
               try{
                     if (this.req.getFieldValue(i,"dispno") != null ){
                          ptdetai.setDispno(Integer.parseInt(this.req.getFieldValue(i,"dispno")));
                     }
                }catch(Exception e){

                }


		   if (ptdetai.updateByWhere("where (EnuType='"+this.req.getFieldValue(i,"EnuType")+"')and(EnuItemValue='"+this.req.getFieldValue(i,"keycode")+"')") <0){
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


}
