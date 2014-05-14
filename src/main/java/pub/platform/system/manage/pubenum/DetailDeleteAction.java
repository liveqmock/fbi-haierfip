package pub.platform.system.manage.pubenum;

import pub.platform.db.*;
import pub.platform.form.control.Action;


public class DetailDeleteAction extends Action
{
    public int doBusiness() {

	for (int i=0; i<this.req.getRecorderCount();i++ ){
	    String SQLStr = "delete from ptEnuDetail where (EnuType='"+this.req.getFieldValue(i,"EnuType")+"')and(EnuItemValue='"+this.req.getFieldValue(i,"keycode")+"')";
	    int retcount =this.dc.executeUpdate(SQLStr);
	    if (retcount <0){
		this.res.setType(0);
		this.res.setResult(false);
		this.res.setMessage("删除记录失败");
		return retcount;
	    }
    }

    //重新载入枚举数据
    pub.platform.form.config.EnumerationType.reload();

    this.res.setType(0);
    this.res.setResult(true);
	this.res.setMessage("删除记录成功");

	return 0;
    }


}
