package pub.platform.system.message;

import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;

public class MessageBean extends AbstractBasicBean implements Cloneable {
	String operid;

	String message;

	String type;

	String sendoper;

	String sendtime;

	String seqno;

	public static final String TABLENAME = "message";

	public String getTableName() {
		return TABLENAME;
	}

	public void addObject(List list, RecordSet rs) {
		MessageBean abb = new MessageBean();
		abb.operid = rs.getString("operid");
		abb.message = rs.getString("message");
		abb.type = rs.getString("type");
		abb.sendoper = rs.getString("sendoper");
		abb.sendtime = rs.getTimeString("sendtime");
		abb.seqno = rs.getString("seqno");
		list.add(abb);
	}

	public String getOperid() {
		if (this.operid == null)
			return "";
		return this.operid;
	}

	public String getMessage() {
		if (this.message == null)
			return "";
		return this.message;
	}

	public String getType() {
		if (this.type == null)
			return "";
		return this.type;
	}

	public String getSendoper() {
		if (this.sendoper == null)
			return "";
		return this.sendoper;
	}

	public String getSendtime() {
		if (this.sendtime == null) {
			return "";
		} else {
			return this.sendtime.trim();
		}
	}

	public String getSendtimeTime() {
		if (this.sendtime == null)
			return "";
		return this.sendtime.split("\\.")[0];
	}

	public String getSeqno() {
		if (this.seqno == null)
			return "";
		return this.seqno;
	}

	public void setOperid(String operid) {
		sqlMaker.setField("operid", operid, Field.TEXT);
		this.operid = operid;
	}

	public void setMessage(String message) {
		sqlMaker.setField("message", message, Field.TEXT);
		this.message = message;
	}

	public void setType(String type) {
		sqlMaker.setField("type", type, Field.TEXT);
		this.type = type;
	}

	public void setSendoper(String sendoper) {
		sqlMaker.setField("sendoper", sendoper, Field.TEXT);
		this.sendoper = sendoper;
	}

	public void setSendtime(String sendtime) {
		sqlMaker.setField("sendtime", sendtime, Field.TEXT);
		this.sendtime = sendtime;
	}

	public void setSeqno(String seqno) {
		sqlMaker.setField("seqno", seqno, Field.TEXT);
		this.seqno = seqno;
	}

	public void init(int i, ActionRequest actionRequest) throws Exception {
		if (actionRequest.getFieldValue(i, "operid") != null) {
			this.setOperid(actionRequest.getFieldValue(i, "operid"));
		}
		if (actionRequest.getFieldValue(i, "message") != null) {
			this.setMessage(actionRequest.getFieldValue(i, "message"));
		}
		if (actionRequest.getFieldValue(i, "type") != null) {
			this.setType(actionRequest.getFieldValue(i, "type"));
		}
		if (actionRequest.getFieldValue(i, "sendoper") != null) {
			this.setSendoper(actionRequest.getFieldValue(i, "sendoper"));
		}
		if (actionRequest.getFieldValue(i, "sendtime") != null) {
			this.setSendtime(actionRequest.getFieldValue(i, "sendtime"));
		}
		if (actionRequest.getFieldValue(i, "seqno") != null) {
			this.setSeqno(actionRequest.getFieldValue(i, "seqno"));
		}
	}

	public void init(ActionRequest actionRequest) throws Exception {
		this.init(0, actionRequest);
	}

	public Object clone() throws CloneNotSupportedException {
		MessageBean obj = (MessageBean) super.clone();
		obj.setOperid(obj.operid);
		obj.setMessage(obj.message);
		obj.setType(obj.type);
		obj.setSendoper(obj.sendoper);
		obj.setSendtime(obj.sendtime);
		obj.setSeqno(obj.seqno);
		return obj;
	}
}