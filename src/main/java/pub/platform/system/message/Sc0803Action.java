package pub.platform.system.message;


import pub.platform.form.control.Action;


public class Sc0803Action extends Action{
	public int doBusiness() {
		 try {

			 MessageBean msgBean = new MessageBean();


			  for (int i=0; i<this.req.getRecorderCount();i++ ){
				  String WhereStr=" where seqno='"+this.req.getFieldValue(i,"seqno")+"'";

				 if(msgBean.deleteByWhere(WhereStr) < 0) {
					 this.res.setType(0);
					 this.res.setResult(false);
					 this.res.setMessage("信息删除失败");
					 return -1;
				 }
			 }
		 } catch(Exception e) {
			 e.printStackTrace();
			 this.res.setType(0);
			 this.res.setResult(false);
			 this.res.setMessage("信息删除失败");
			 return -1;
		 }
		 this.res.setType(0);
		 this.res.setResult(true);
		 this.res.setMessage("信息删除成功");
		 return 0;

	}


}
