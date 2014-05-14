package pub.platform.system.manage.dept;

import pub.platform.db.*;
import pub.platform.form.control.Action;


public class DeptSelectOneAction extends Action {
	public int doBusiness() {

		String SQLStr = "select  deptid,deptname,deptdesc,FILLSTR10,FILLSTR20,FILLSTR100,FILLSTR150,deptlevel from ptdept where (deptid='" +
			this.req.getFieldValue("deptid") + "') order by deptid ";

		this.rs = this.dc.executeQuery(SQLStr);

		this.res.setFieldName("deptid;deptname;deptdesc;FILLSTR10;FILLSTR20;FILLSTR100;FILLSTR150;deptlevel");
		this.res.setFieldType("text;text;text;text;text;text;text;text");
          this.res.setEnumType("0;0;0;0;0;0;0;0");
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
