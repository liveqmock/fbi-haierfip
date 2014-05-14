package pub.platform.system.message;


import pub.platform.form.control.Action;
import pub.platform.utils.*;



public class Sc0801Action extends Action{

	private String operid="";
	private String msg="";

	public int doBusiness() {
		try {

			MessageBean msgBean = new MessageBean();

			operid = this.req.getFieldValue("operid");
			msg    = this.req.getFieldValue("msg");



			String[] operArr= operid.split(";" ,-2);

			for (int i=0 ; i < operArr.length ;i++){

				msgBean.setOperid(operArr[i]);
				msgBean.setMessage(msg);
				msgBean.setSendoper(this.operator.getOpername());
				msgBean.setSendtime(BusinessDate.getTodaytime());
				msgBean.setType("1");
				msgBean.setSeqno(Util.getFieldMax(this.dc,"seqno","message"));

				if(msgBean.insert() < 0) {
					this.res.setType(0);
					this.res.setResult(false);
					this.res.setMessage("信息发送失败");
					return -1;
				}
			}

		} catch(Exception e) {
			e.printStackTrace();
			this.res.setType(0);
			this.res.setResult(false);
			this.res.setMessage("信息发送失败");
			return -1;
		}
		this.res.setType(0);
		this.res.setResult(true);
		this.res.setMessage("信息发送成功");

		return 0;

    }

}
