package pub.platform.system.manage.dept;

import pub.platform.form.control.Action;
import pub.platform.security.*;

public class DeptSelectAction extends Action {
	
	
	public int doBusiness() {



          String whereStr=" where parentdeptid='" + this.req.getFieldValue("parentdeptid") + "' order by deptid";

//          if (this.req.getFieldValue("parentdeptid").equals("0")){
//               if (this.getOperator().getOpertype() != null && this.getOperator().getOpertype() .equals("2")){
//                    if (this.getOperator().getIssuper().equals("1"))
//                         whereStr=" where deptid='" + this.getOperator().getDeptid() + "' order by deptid";
//
//                    else
//                        whereStr=" where 1>1 ";
//
//               }
//          }

          String SQLStr = "select  deptid,deptname,deptdesc,(select  count(deptid) as a from ptdept  where parentdeptid=ta.deptid) as countnumer from ptdept ta " + whereStr;
		this.rs = this.dc.executeQuery(SQLStr);

		this.res.setFieldName("deptid;deptname;deptdesc;count");
		this.res.setFieldType("text;text;text;count");
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
	public int getchildmenu(){
		try{
			DatabaseAgent database = new DatabaseAgent();
			
			int menuLevel =  Integer.parseInt(this.req.getFieldValue("level"))+1;
			String menuParent = this.req.getFieldValue("parentdeptid");
			
	         StringBuffer sb = new StringBuffer();
	         sb.append("<tree>");
	         
	         MenuItemBean[] menuItemsLevel = database.getMenuItems(this.operator.getOperid(), menuLevel,menuParent);
	         
	         for(int i = 0; i < menuItemsLevel.length; i++) {
	            sb.append(menuItemsLevel[i].convertToString());
	            sb.append("</tree>");
	        }
	        sb.append("</tree>"); 
	
	        this.setXml(sb.toString());
	        return 0;
		}catch(Exception e ){
			this.setError("操作员超时！");
			return -1;
		}
         
		
	}
}
