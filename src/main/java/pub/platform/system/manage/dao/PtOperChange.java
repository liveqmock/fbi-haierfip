package pub.platform.system.manage.dao;

import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;

public class PtOperChange extends AbstractBasicBean implements Cloneable {
	public static List find(String sSqlWhere) {
		return new PtOperChange().findByWhere(sSqlWhere);
	}

	public static List findAndLock(String sSqlWhere) {
		return new PtOperChange().findAndLockByWhere(sSqlWhere);
	}

	public static PtOperChange findFirst(String sSqlWhere) {
		return (PtOperChange) new PtOperChange().findFirstByWhere(sSqlWhere);
	}

	public static PtOperChange findFirstAndLock(String sSqlWhere) {
		return (PtOperChange) new PtOperChange()
				.findFirstAndLockByWhere(sSqlWhere);
	}

	public static RecordSet findRecordSet(String sSqlWhere) {
		return new PtOperChange().findRecordSetByWhere(sSqlWhere);
	}

	public static List find(String sSqlWhere, boolean isAutoRelease) {
		PtOperChange bean = new PtOperChange();
		bean.setAutoRelease(isAutoRelease);
		return bean.findByWhere(sSqlWhere);
	}

	public static List findAndLock(String sSqlWhere, boolean isAutoRelease) {
		PtOperChange bean = new PtOperChange();
		bean.setAutoRelease(isAutoRelease);
		return bean.findAndLockByWhere(sSqlWhere);
	}

	public static PtOperChange findFirst(String sSqlWhere, boolean isAutoRelease) {
		PtOperChange bean = new PtOperChange();
		bean.setAutoRelease(isAutoRelease);
		return (PtOperChange) bean.findFirstByWhere(sSqlWhere);
	}

	public static PtOperChange findFirstAndLock(String sSqlWhere,
			boolean isAutoRelease) {
		PtOperChange bean = new PtOperChange();
		bean.setAutoRelease(isAutoRelease);
		return (PtOperChange) bean.findFirstAndLockByWhere(sSqlWhere);
	}

	public static RecordSet findRecordSet(String sSqlWhere,
			boolean isAutoRelease) {
		PtOperChange bean = new PtOperChange();
		bean.setAutoRelease(isAutoRelease);
		return bean.findRecordSetByWhere(sSqlWhere);
	}

	int opersign;

	String deptid;

	String operid;

	String olddeptid;

	String oldoperid;

	public static final String TABLENAME = "ptoperchange";

	private String operate_mode = "add";

	public ChangeFileds cf = new ChangeFileds();

	public String getTableName() {
		return TABLENAME;
	}

	public void addObject(List list, RecordSet rs) {
		PtOperChange abb = new PtOperChange();
		abb.opersign = rs.getInt("opersign");
		abb.setKeyValue("OPERSIGN", "" + abb.getOpersign());
		abb.deptid = rs.getString("deptid");
		abb.setKeyValue("DEPTID", "" + abb.getDeptid());
		abb.operid = rs.getString("operid");
		abb.setKeyValue("OPERID", "" + abb.getOperid());
		abb.olddeptid = rs.getString("olddeptid");
		abb.setKeyValue("OLDDEPTID", "" + abb.getOlddeptid());
		abb.oldoperid = rs.getString("oldoperid");
		abb.setKeyValue("OLDOPERID", "" + abb.getOldoperid());
		list.add(abb);
		abb.operate_mode = "edit";
	}

	public int getOpersign() {
		return this.opersign;
	}

	public String getDeptid() {
		if (this.deptid == null)
			return "";
		return this.deptid;
	}

	public String getOperid() {
		if (this.operid == null)
			return "";
		return this.operid;
	}

	public String getOlddeptid() {
		if (this.olddeptid == null)
			return "";
		return this.olddeptid;
	}

	public String getOldoperid() {
		if (this.oldoperid == null)
			return "";
		return this.oldoperid;
	}

	public void setOpersign(int opersign) {
		sqlMaker.setField("opersign", "" + opersign, Field.NUMBER);
		if (this.operate_mode.equals("edit")) {
			if (this.getOpersign() != opersign)
				cf.add("opersign", this.opersign + "", opersign + "");
		}
		this.opersign = opersign;
	}

	public void setDeptid(String deptid) {
		sqlMaker.setField("deptid", deptid, Field.TEXT);
		if (this.operate_mode.equals("edit")) {
			if (!this.getDeptid().equals(deptid))
				cf.add("deptid", this.deptid, deptid);
		}
		this.deptid = deptid;
	}

	public void setOperid(String operid) {
		sqlMaker.setField("operid", operid, Field.TEXT);
		if (this.operate_mode.equals("edit")) {
			if (!this.getOperid().equals(operid))
				cf.add("operid", this.operid, operid);
		}
		this.operid = operid;
	}

	public void setOlddeptid(String olddeptid) {
		sqlMaker.setField("olddeptid", olddeptid, Field.TEXT);
		if (this.operate_mode.equals("edit")) {
			if (!this.getOlddeptid().equals(olddeptid))
				cf.add("olddeptid", this.olddeptid, olddeptid);
		}
		this.olddeptid = olddeptid;
	}

	public void setOldoperid(String oldoperid) {
		sqlMaker.setField("oldoperid", oldoperid, Field.TEXT);
		if (this.operate_mode.equals("edit")) {
			if (!this.getOldoperid().equals(oldoperid))
				cf.add("oldoperid", this.oldoperid, oldoperid);
		}
		this.oldoperid = oldoperid;
	}

	public void init(int i, ActionRequest actionRequest) throws Exception {
		if (actionRequest.getFieldValue(i, "opersign") != null
				&& actionRequest.getFieldValue(i, "opersign").trim().length() > 0) {
			this.setOpersign(Integer.parseInt(actionRequest.getFieldValue(i,
					"opersign")));
		}
		if (actionRequest.getFieldValue(i, "deptid") != null) {
			this.setDeptid(actionRequest.getFieldValue(i, "deptid"));
		}
		if (actionRequest.getFieldValue(i, "operid") != null) {
			this.setOperid(actionRequest.getFieldValue(i, "operid"));
		}
		if (actionRequest.getFieldValue(i, "olddeptid") != null) {
			this.setOlddeptid(actionRequest.getFieldValue(i, "olddeptid"));
		}
		if (actionRequest.getFieldValue(i, "oldoperid") != null) {
			this.setOldoperid(actionRequest.getFieldValue(i, "oldoperid"));
		}
	}

	public void init(ActionRequest actionRequest) throws Exception {
		this.init(0, actionRequest);
	}

	public void initAll(int i, ActionRequest actionRequest) throws Exception {
		this.init(i, actionRequest);
	}

	public void initAll(ActionRequest actionRequest) throws Exception {
		this.initAll(0, actionRequest);
	}

	public Object clone() throws CloneNotSupportedException {
		PtOperChange obj = (PtOperChange) super.clone();
		obj.setOpersign(obj.opersign);
		obj.setDeptid(obj.deptid);
		obj.setOperid(obj.operid);
		obj.setOlddeptid(obj.olddeptid);
		obj.setOldoperid(obj.oldoperid);
		return obj;
	}
}