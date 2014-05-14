package pub.platform.system.message;


import pub.platform.form.control.Action;




public class Sc0802Action extends Action{
	public int doBusiness() {
		 try {

                System.out.println("@@@@@@@@@@@@@@@@@@@");
                System.out.println("@@@@@@@@@@@@@@@@@@@");
			MessageBean msgBean = new MessageBean();

			  this.rs = this.dc.executeQuery(" select (select opername from ptoper where operid=sendoper) sendname ,message from message where operid='"+this.operator.getOperid()+"' and type='1'");
 System.out.println("################################");
			  this.res.setFieldName("sendoper;message");
			  this.res.setFieldType("text;text");
			  this.res.setEnumType("0;0");
			  this.res.setType(1);

			  if (this.rs == null) {
				  this.res.setResult(false);
				  this.res.setMessage("无返回信息记录");
				  return -1;

			  } else {
				  if (this.rs.next()){
					  msgBean.setType("0");
					  msgBean.updateByWhere("where operid='"+this.operator.getOperid()+"' and type='1'");

					  this.rs.beforeFirst();
				  }


				  this.res.setResult(true);
				  this.res.setRecordset(this.rs);
			  }
			  return 0;


		 } catch(Exception e) {
			 e.printStackTrace();
                 System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%");
			 this.res.setType(0);
			 this.res.setResult(false);
			 this.res.setMessage("无返回信息记录");
			 return -1;
		 }



	}


}
