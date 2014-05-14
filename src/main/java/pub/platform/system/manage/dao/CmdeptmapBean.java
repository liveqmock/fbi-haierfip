package pub.platform.system.manage.dao;

import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;

public class CmdeptmapBean extends AbstractBasicBean implements Cloneable {
	String highdept;

	String lowerdept;

	public static final String TABLENAME = "cmdeptmap";

	public String getTableName() {
		return TABLENAME;
	}

	public void addObject(List list, RecordSet rs) {
		CmdeptmapBean abb = new CmdeptmapBean();
		abb.highdept = rs.getString("highdept");
		abb.lowerdept = rs.getString("lowerdept");
		list.add(abb);
	}

	public String getHighdept() {
		if (this.highdept == null)
			return "";
		return this.highdept;
	}

	public String getLowerdept() {
		if (this.lowerdept == null)
			return "";
		return this.lowerdept;
	}

	public void setHighdept(String highdept) {
		sqlMaker.setField("highdept", highdept, Field.TEXT);
		this.highdept = highdept;
	}

	public void setLowerdept(String lowerdept) {
		sqlMaker.setField("lowerdept", lowerdept, Field.TEXT);
		this.lowerdept = lowerdept;
	}

	public void init(int i, ActionRequest actionRequest) throws Exception {
		if (actionRequest.getFieldValue(i, "highdept") != null) {
			this.setHighdept(actionRequest.getFieldValue(i, "highdept"));
		}
		if (actionRequest.getFieldValue(i, "lowerdept") != null) {
			this.setLowerdept(actionRequest.getFieldValue(i, "lowerdept"));
		}
	}

	public void init(ActionRequest actionRequest) throws Exception {
		this.init(0, actionRequest);
	}

	public void initAll(int i, ActionRequest actionRequest) throws Exception {
		if (actionRequest.getFieldValue(i, "highdept") != null) {
			this.setHighdept(actionRequest.getFieldValue(i, "highdept"));
		} else {
			this.setHighdept(null);
		}
		if (actionRequest.getFieldValue(i, "lowerdept") != null) {
			this.setLowerdept(actionRequest.getFieldValue(i, "lowerdept"));
		} else {
			this.setLowerdept(null);
		}
	}

	public void initAll(ActionRequest actionRequest) throws Exception {
		this.initAll(0, actionRequest);
	}

	public Object clone() throws CloneNotSupportedException {
		CmdeptmapBean obj = (CmdeptmapBean) super.clone();
		obj.setHighdept(obj.highdept);
		obj.setLowerdept(obj.lowerdept);
		return obj;
	}
}