package pub.platform.system.manage.dao;

import java.util.*;

import pub.platform.db.*;
import pub.platform.utils.*;


public class PtOperBean extends AbstractBasicBean implements Cloneable {
	String deptid;

	String operid;

	String operpasswd;

	String issuper;

	String operduty;

	String opertype;

	String opername;

	String sex;

	String email;

	String mobilephone;

	String operphone;

	String otherphone;

	String operenabled;

	String fillstr50;

	String fillstr80;

	String fillstr100;

	String fillstr150;

	String fillstr600;

	String filldate1;

	String filldate2;

	String filldate3;

	PtDeptBean ptdeptBean;

	int fillint6;

	int fillint8;

	int fillint10;

	int fillint12;

	int fillintvar;

	double filldbl1;

	double filldbl2;

	double filldbl3;

	public static final String TABLENAME = "ptoper";

	public String getTableName() {
		return TABLENAME;
	}

	public void addObject(List list, RecordSet rs) {
		PtOperBean abb = new PtOperBean();
		abb.deptid = rs.getString("deptid");
		abb.operid = rs.getString("operid");
		abb.operpasswd = rs.getString("operpasswd");
		abb.issuper = rs.getString("issuper");
		abb.operduty = rs.getString("operduty");
		abb.opertype = rs.getString("opertype");
		abb.opername = rs.getString("opername");
		abb.sex = rs.getString("sex");
		abb.email = rs.getString("email");
		abb.mobilephone = rs.getString("mobilephone");
		abb.operphone = rs.getString("operphone");
		abb.otherphone = rs.getString("otherphone");
		abb.operenabled = rs.getString("operenabled");
		abb.fillstr50 = rs.getString("fillstr50");
		abb.fillstr80 = rs.getString("fillstr80");
		abb.fillstr100 = rs.getString("fillstr100");
		abb.fillstr150 = rs.getString("fillstr150");
		abb.fillstr600 = rs.getString("fillstr600");
		abb.filldate1 = rs.getTimeString("filldate1");
		abb.filldate2 = rs.getTimeString("filldate2");
		abb.filldate3 = rs.getTimeString("filldate3");
		abb.fillint6 = rs.getInt("fillint6");
		abb.fillint8 = rs.getInt("fillint8");
		abb.fillint10 = rs.getInt("fillint10");
		abb.fillint12 = rs.getInt("fillint12");
		abb.fillintvar = rs.getInt("fillintvar");
		abb.filldbl1 = rs.getDouble("filldbl1");
		abb.filldbl2 = rs.getDouble("filldbl2");
		abb.filldbl3 = rs.getDouble("filldbl3");
		list.add(abb);
	}

	public String getDeptid() {
		if (this.deptid == null)
			return "";
		return this.deptid;
	}

	public PtDeptBean getPtDeptBean() {
		return this.ptdeptBean;
	}

	public void setPtDeptBean(PtDeptBean deptBane) {
		ptdeptBean = deptBane;
	}

	public String getOperid() {
		if (this.operid == null)
			return "";
		return this.operid;
	}

	public String getOperpasswd() {
		if (this.operpasswd == null)
			return "";
		return this.operpasswd;
	}

	public String getIssuper() {
		if (this.issuper == null)
			return "";
		return this.issuper;
	}

	public String getOperduty() {
		if (this.operduty == null)
			return "";
		return this.operduty;
	}

	public String getOpertype() {
		if (this.opertype == null)
			return "";
		return this.opertype;
	}

	public String getOpername() {
		if (this.opername == null)
			return "";
		return this.opername;
	}

	public String getSex() {
		if (this.sex == null)
			return "";
		return this.sex;
	}

	public String getEmail() {
		if (this.email == null)
			return "";
		return this.email;
	}

	public String getMobilephone() {
		if (this.mobilephone == null)
			return "";
		return this.mobilephone;
	}

	public String getOperphone() {
		if (this.operphone == null)
			return "";
		return this.operphone;
	}

	public String getOtherphone() {
		if (this.otherphone == null)
			return "";
		return this.otherphone;
	}

	public String getOperenabled() {
		if (this.operenabled == null)
			return "";
		return this.operenabled;
	}

	public String getFillstr50() {
		if (this.fillstr50 == null)
			return "";
		return this.fillstr50;
	}

	public String getFillstr80() {
		if (this.fillstr80 == null)
			return "";
		return this.fillstr80;
	}

	public String getFillstr100() {
		if (this.fillstr100 == null)
			return "";
		return this.fillstr100;
	}

	public String getFillstr150() {
		if (this.fillstr150 == null)
			return "";
		return this.fillstr150;
	}

	public String getFillstr600() {
		if (this.fillstr600 == null)
			return "";
		return this.fillstr600;
	}

	public String getFilldate1() {
		if (this.filldate1 == null) {
			return "";
		} else {
			return this.filldate1.trim().split(" ")[0];
		}
	}

	public String getFilldate1Time() {
		if (this.filldate1 == null)
			return "";
		return this.filldate1.split("\\.")[0];
	}

	public String getFilldate2() {
		if (this.filldate2 == null) {
			return "";
		} else {
			return this.filldate2.trim().split(" ")[0];
		}
	}

	public String getFilldate2Time() {
		if (this.filldate2 == null)
			return "";
		return this.filldate2.split("\\.")[0];
	}

	public String getFilldate3() {
		if (this.filldate3 == null) {
			return "";
		} else {
			return this.filldate3.trim().split(" ")[0];
		}
	}

	public String getFilldate3Time() {
		if (this.filldate3 == null)
			return "";
		return this.filldate3.split("\\.")[0];
	}

	public int getFillint6() {
		return this.fillint6;
	}

	public int getFillint8() {
		return this.fillint8;
	}

	public int getFillint10() {
		return this.fillint10;
	}

	public int getFillint12() {
		return this.fillint12;
	}

	public int getFillintvar() {
		return this.fillintvar;
	}

	public double getFilldbl1() {
		return this.filldbl1;
	}

	public double getFilldbl2() {
		return this.filldbl2;
	}

	public double getFilldbl3() {
		return this.filldbl3;
	}

	public void setDeptid(String deptid) {
		sqlMaker.setField("deptid", deptid, Field.TEXT);
		this.deptid = deptid;
	}

	public void setOperid(String operid) {
		sqlMaker.setField("operid", operid, Field.TEXT);
		this.operid = operid;
	}

	public void setOperpasswd(String operpasswd) {
		sqlMaker.setField("operpasswd", operpasswd, Field.TEXT);
		this.operpasswd = operpasswd;
	}

	public void setIssuper(String issuper) {
		sqlMaker.setField("issuper", issuper, Field.TEXT);
		this.issuper = issuper;
	}

	public void setOperduty(String operduty) {
		sqlMaker.setField("operduty", operduty, Field.TEXT);
		this.operduty = operduty;
	}

	public void setOpertype(String opertype) {
		sqlMaker.setField("opertype", opertype, Field.TEXT);
		this.opertype = opertype;
	}

	public void setOpername(String opername) {
		sqlMaker.setField("opername", opername, Field.TEXT);
		this.opername = opername;
	}

	public void setSex(String sex) {
		sqlMaker.setField("sex", sex, Field.TEXT);
		this.sex = sex;
	}

	public void setEmail(String email) {
		sqlMaker.setField("email", email, Field.TEXT);
		this.email = email;
	}

	public void setMobilephone(String mobilephone) {
		sqlMaker.setField("mobilephone", mobilephone, Field.TEXT);
		this.mobilephone = mobilephone;
	}

	public void setOperphone(String operphone) {
		sqlMaker.setField("operphone", operphone, Field.TEXT);
		this.operphone = operphone;
	}

	public void setOtherphone(String otherphone) {
		sqlMaker.setField("otherphone", otherphone, Field.TEXT);
		this.otherphone = otherphone;
	}

	public void setOperenabled(String operenabled) {
		sqlMaker.setField("operenabled", operenabled, Field.TEXT);
		this.operenabled = operenabled;
	}

	public void setFillstr50(String fillstr50) {
		sqlMaker.setField("fillstr50", fillstr50, Field.TEXT);
		this.fillstr50 = fillstr50;
	}

	public void setFillstr80(String fillstr80) {
		sqlMaker.setField("fillstr80", fillstr80, Field.TEXT);
		this.fillstr80 = fillstr80;
	}

	public void setFillstr100(String fillstr100) {
		sqlMaker.setField("fillstr100", fillstr100, Field.TEXT);
		this.fillstr100 = fillstr100;
	}

	public void setFillstr150(String fillstr150) {
		sqlMaker.setField("fillstr150", fillstr150, Field.TEXT);
		this.fillstr150 = fillstr150;
	}

	public void setFillstr600(String fillstr600) {
		sqlMaker.setField("fillstr600", fillstr600, Field.TEXT);
		this.fillstr600 = fillstr600;
	}

	public void setFilldate1(String filldate1) {
		sqlMaker.setField("filldate1", filldate1, Field.DATE);
		this.filldate1 = filldate1;
	}

	public void setFilldate2(String filldate2) {
		sqlMaker.setField("filldate2", filldate2, Field.DATE);
		this.filldate2 = filldate2;
	}

	public void setFilldate3(String filldate3) {
		sqlMaker.setField("filldate3", filldate3, Field.DATE);
		this.filldate3 = filldate3;
	}

	public void setFillint6(int fillint6) {
		sqlMaker.setField("fillint6", "" + fillint6, Field.NUMBER);
		this.fillint6 = fillint6;
	}

	public void setFillint8(int fillint8) {
		sqlMaker.setField("fillint8", "" + fillint8, Field.NUMBER);
		this.fillint8 = fillint8;
	}

	public void setFillint10(int fillint10) {
		sqlMaker.setField("fillint10", "" + fillint10, Field.NUMBER);
		this.fillint10 = fillint10;
	}

	public void setFillint12(int fillint12) {
		sqlMaker.setField("fillint12", "" + fillint12, Field.NUMBER);
		this.fillint12 = fillint12;
	}

	public void setFillintvar(int fillintvar) {
		sqlMaker.setField("fillintvar", "" + fillintvar, Field.NUMBER);
		this.fillintvar = fillintvar;
	}

	public void setFilldbl1(double filldbl1) {
		sqlMaker.setField("filldbl1", "" + filldbl1, Field.NUMBER);
		this.filldbl1 = filldbl1;
	}

	public void setFilldbl2(double filldbl2) {
		sqlMaker.setField("filldbl2", "" + filldbl2, Field.NUMBER);
		this.filldbl2 = filldbl2;
	}

	public void setFilldbl3(double filldbl3) {
		sqlMaker.setField("filldbl3", "" + filldbl3, Field.NUMBER);
		this.filldbl3 = filldbl3;
	}

	public void init(int i, ActionRequest actionRequest) throws Exception {
		if (actionRequest.getFieldValue(i, "deptid") != null) {
			this.setDeptid(actionRequest.getFieldValue(i, "deptid"));
		}
		if (actionRequest.getFieldValue(i, "operid") != null) {
			this.setOperid(actionRequest.getFieldValue(i, "operid"));
		}
		if (actionRequest.getFieldValue(i, "operpasswd") != null) {
			this.setOperpasswd(actionRequest.getFieldValue(i, "operpasswd"));
		}
		if (actionRequest.getFieldValue(i, "issuper") != null) {
			this.setIssuper(actionRequest.getFieldValue(i, "issuper"));
		}
		if (actionRequest.getFieldValue(i, "operduty") != null) {
			this.setOperduty(actionRequest.getFieldValue(i, "operduty"));
		}
		if (actionRequest.getFieldValue(i, "opertype") != null) {
			this.setOpertype(actionRequest.getFieldValue(i, "opertype"));
		}
		if (actionRequest.getFieldValue(i, "opername") != null) {
			this.setOpername(actionRequest.getFieldValue(i, "opername"));
		}
		if (actionRequest.getFieldValue(i, "sex") != null) {
			this.setSex(actionRequest.getFieldValue(i, "sex"));
		}
		if (actionRequest.getFieldValue(i, "email") != null) {
			this.setEmail(actionRequest.getFieldValue(i, "email"));
		}
		if (actionRequest.getFieldValue(i, "mobilephone") != null) {
			this.setMobilephone(actionRequest.getFieldValue(i, "mobilephone"));
		}
		if (actionRequest.getFieldValue(i, "operphone") != null) {
			this.setOperphone(actionRequest.getFieldValue(i, "operphone"));
		}
		if (actionRequest.getFieldValue(i, "otherphone") != null) {
			this.setOtherphone(actionRequest.getFieldValue(i, "otherphone"));
		}
		if (actionRequest.getFieldValue(i, "operenabled") != null) {
			this.setOperenabled(actionRequest.getFieldValue(i, "operenabled"));
		}
		if (actionRequest.getFieldValue(i, "fillstr50") != null) {
			this.setFillstr50(actionRequest.getFieldValue(i, "fillstr50"));
		}
		if (actionRequest.getFieldValue(i, "fillstr80") != null) {
			this.setFillstr80(actionRequest.getFieldValue(i, "fillstr80"));
		}
		if (actionRequest.getFieldValue(i, "fillstr100") != null) {
			this.setFillstr100(actionRequest.getFieldValue(i, "fillstr100"));
		}
		if (actionRequest.getFieldValue(i, "fillstr150") != null) {
			this.setFillstr150(actionRequest.getFieldValue(i, "fillstr150"));
		}
		if (actionRequest.getFieldValue(i, "fillstr600") != null) {
			this.setFillstr600(actionRequest.getFieldValue(i, "fillstr600"));
		}
		if (actionRequest.getFieldValue(i, "filldate1") != null) {
			this.setFilldate1(actionRequest.getFieldValue(i, "filldate1"));
		}
		if (actionRequest.getFieldValue(i, "filldate2") != null) {
			this.setFilldate2(actionRequest.getFieldValue(i, "filldate2"));
		}
		if (actionRequest.getFieldValue(i, "filldate3") != null) {
			this.setFilldate3(actionRequest.getFieldValue(i, "filldate3"));
		}
		if (actionRequest.getFieldValue(i, "fillint6") != null
				&& actionRequest.getFieldValue(i, "fillint6").trim().length() > 0) {
			this.setFillint6(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillint6")));
		}
		if (actionRequest.getFieldValue(i, "fillint8") != null
				&& actionRequest.getFieldValue(i, "fillint8").trim().length() > 0) {
			this.setFillint8(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillint8")));
		}
		if (actionRequest.getFieldValue(i, "fillint10") != null
				&& actionRequest.getFieldValue(i, "fillint10").trim().length() > 0) {
			this.setFillint10(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillint10")));
		}
		if (actionRequest.getFieldValue(i, "fillint12") != null
				&& actionRequest.getFieldValue(i, "fillint12").trim().length() > 0) {
			this.setFillint12(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillint12")));
		}
		if (actionRequest.getFieldValue(i, "fillintvar") != null
				&& actionRequest.getFieldValue(i, "fillintvar").trim().length() > 0) {
			this.setFillintvar(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillintvar")));
		}
		if (actionRequest.getFieldValue(i, "filldbl1") != null
				&& actionRequest.getFieldValue(i, "filldbl1").trim().length() > 0) {
			this.setFilldbl1(Double.parseDouble(actionRequest.getFieldValue(i,
					"filldbl1")));
		}
		if (actionRequest.getFieldValue(i, "filldbl2") != null
				&& actionRequest.getFieldValue(i, "filldbl2").trim().length() > 0) {
			this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i,
					"filldbl2")));
		}
		if (actionRequest.getFieldValue(i, "filldbl3") != null
				&& actionRequest.getFieldValue(i, "filldbl3").trim().length() > 0) {
			this.setFilldbl3(Double.parseDouble(actionRequest.getFieldValue(i,
					"filldbl3")));
		}
	}

	public void init(ActionRequest actionRequest) throws Exception {
		this.init(0, actionRequest);
	}

	public void initAll(int i, ActionRequest actionRequest) throws Exception {
		if (actionRequest.getFieldValue(i, "deptid") != null) {
			this.setDeptid(actionRequest.getFieldValue(i, "deptid"));
		} else {
			this.setDeptid(null);
		}
		if (actionRequest.getFieldValue(i, "operid") != null) {
			this.setOperid(actionRequest.getFieldValue(i, "operid"));
		} else {
			this.setOperid(null);
		}
		if (actionRequest.getFieldValue(i, "operpasswd") != null) {
			this.setOperpasswd(actionRequest.getFieldValue(i, "operpasswd"));
		} else {
			this.setOperpasswd(null);
		}
		if (actionRequest.getFieldValue(i, "issuper") != null) {
			this.setIssuper(actionRequest.getFieldValue(i, "issuper"));
		} else {
			this.setIssuper(null);
		}
		if (actionRequest.getFieldValue(i, "operduty") != null) {
			this.setOperduty(actionRequest.getFieldValue(i, "operduty"));
		} else {
			this.setOperduty(null);
		}
		if (actionRequest.getFieldValue(i, "opertype") != null) {
			this.setOpertype(actionRequest.getFieldValue(i, "opertype"));
		} else {
			this.setOpertype(null);
		}
		if (actionRequest.getFieldValue(i, "opername") != null) {
			this.setOpername(actionRequest.getFieldValue(i, "opername"));
		} else {
			this.setOpername(null);
		}
		if (actionRequest.getFieldValue(i, "sex") != null) {
			this.setSex(actionRequest.getFieldValue(i, "sex"));
		} else {
			this.setSex(null);
		}
		if (actionRequest.getFieldValue(i, "email") != null) {
			this.setEmail(actionRequest.getFieldValue(i, "email"));
		} else {
			this.setEmail(null);
		}
		if (actionRequest.getFieldValue(i, "mobilephone") != null) {
			this.setMobilephone(actionRequest.getFieldValue(i, "mobilephone"));
		} else {
			this.setMobilephone(null);
		}
		if (actionRequest.getFieldValue(i, "operphone") != null) {
			this.setOperphone(actionRequest.getFieldValue(i, "operphone"));
		} else {
			this.setOperphone(null);
		}
		if (actionRequest.getFieldValue(i, "otherphone") != null) {
			this.setOtherphone(actionRequest.getFieldValue(i, "otherphone"));
		} else {
			this.setOtherphone(null);
		}
		if (actionRequest.getFieldValue(i, "operenabled") != null) {
			this.setOperenabled(actionRequest.getFieldValue(i, "operenabled"));
		} else {
			this.setOperenabled(null);
		}
		if (actionRequest.getFieldValue(i, "fillstr50") != null) {
			this.setFillstr50(actionRequest.getFieldValue(i, "fillstr50"));
		} else {
			this.setFillstr50(null);
		}
		if (actionRequest.getFieldValue(i, "fillstr80") != null) {
			this.setFillstr80(actionRequest.getFieldValue(i, "fillstr80"));
		} else {
			this.setFillstr80(null);
		}
		if (actionRequest.getFieldValue(i, "fillstr100") != null) {
			this.setFillstr100(actionRequest.getFieldValue(i, "fillstr100"));
		} else {
			this.setFillstr100(null);
		}
		if (actionRequest.getFieldValue(i, "fillstr150") != null) {
			this.setFillstr150(actionRequest.getFieldValue(i, "fillstr150"));
		} else {
			this.setFillstr150(null);
		}
		if (actionRequest.getFieldValue(i, "fillstr600") != null) {
			this.setFillstr600(actionRequest.getFieldValue(i, "fillstr600"));
		} else {
			this.setFillstr600(null);
		}
		if (actionRequest.getFieldValue(i, "filldate1") != null) {
			this.setFilldate1(actionRequest.getFieldValue(i, "filldate1"));
		} else {
			this.setFilldate1(null);
		}
		if (actionRequest.getFieldValue(i, "filldate2") != null) {
			this.setFilldate2(actionRequest.getFieldValue(i, "filldate2"));
		} else {
			this.setFilldate2(null);
		}
		if (actionRequest.getFieldValue(i, "filldate3") != null) {
			this.setFilldate3(actionRequest.getFieldValue(i, "filldate3"));
		} else {
			this.setFilldate3(null);
		}
		if (actionRequest.getFieldValue(i, "fillint6") != null
				&& actionRequest.getFieldValue(i, "fillint6").trim().length() > 0) {
			this.setFillint6(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillint6")));
		} else {
			this.setFillint6(0);
		}
		if (actionRequest.getFieldValue(i, "fillint8") != null
				&& actionRequest.getFieldValue(i, "fillint8").trim().length() > 0) {
			this.setFillint8(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillint8")));
		} else {
			this.setFillint8(0);
		}
		if (actionRequest.getFieldValue(i, "fillint10") != null
				&& actionRequest.getFieldValue(i, "fillint10").trim().length() > 0) {
			this.setFillint10(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillint10")));
		} else {
			this.setFillint10(0);
		}
		if (actionRequest.getFieldValue(i, "fillint12") != null
				&& actionRequest.getFieldValue(i, "fillint12").trim().length() > 0) {
			this.setFillint12(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillint12")));
		} else {
			this.setFillint12(0);
		}
		if (actionRequest.getFieldValue(i, "fillintvar") != null
				&& actionRequest.getFieldValue(i, "fillintvar").trim().length() > 0) {
			this.setFillintvar(Integer.parseInt(actionRequest.getFieldValue(i,
					"fillintvar")));
		} else {
			this.setFillintvar(0);
		}
		if (actionRequest.getFieldValue(i, "filldbl1") != null
				&& actionRequest.getFieldValue(i, "filldbl1").trim().length() > 0) {
			this.setFilldbl1(Double.parseDouble(actionRequest.getFieldValue(i,
					"filldbl1")));
		} else {
			this.setFilldbl1(0);
		}
		if (actionRequest.getFieldValue(i, "filldbl2") != null
				&& actionRequest.getFieldValue(i, "filldbl2").trim().length() > 0) {
			this.setFilldbl2(Double.parseDouble(actionRequest.getFieldValue(i,
					"filldbl2")));
		} else {
			this.setFilldbl2(0);
		}
		if (actionRequest.getFieldValue(i, "filldbl3") != null
				&& actionRequest.getFieldValue(i, "filldbl3").trim().length() > 0) {
			this.setFilldbl3(Double.parseDouble(actionRequest.getFieldValue(i,
					"filldbl3")));
		} else {
			this.setFilldbl3(0);
		}
	}

	public void initAll(ActionRequest actionRequest) throws Exception {
		this.initAll(0, actionRequest);
	}

	public Object clone() throws CloneNotSupportedException {
		PtOperBean obj = (PtOperBean) super.clone();
		obj.setDeptid(obj.deptid);
		obj.setOperid(obj.operid);
		obj.setOperpasswd(obj.operpasswd);
		obj.setIssuper(obj.issuper);
		obj.setOperduty(obj.operduty);
		obj.setOpertype(obj.opertype);
		obj.setOpername(obj.opername);
		obj.setSex(obj.sex);
		obj.setEmail(obj.email);
		obj.setMobilephone(obj.mobilephone);
		obj.setOperphone(obj.operphone);
		obj.setOtherphone(obj.otherphone);
		obj.setOperenabled(obj.operenabled);
		obj.setFillstr50(obj.fillstr50);
		obj.setFillstr80(obj.fillstr80);
		obj.setFillstr100(obj.fillstr100);
		obj.setFillstr150(obj.fillstr150);
		obj.setFillstr600(obj.fillstr600);
		obj.setFilldate1(obj.filldate1);
		obj.setFilldate2(obj.filldate2);
		obj.setFilldate3(obj.filldate3);
		obj.setFillint6(obj.fillint6);
		obj.setFillint8(obj.fillint8);
		obj.setFillint10(obj.fillint10);
		obj.setFillint12(obj.fillint12);
		obj.setFillintvar(obj.fillintvar);
		obj.setFilldbl1(obj.filldbl1);
		obj.setFilldbl2(obj.filldbl2);
		obj.setFilldbl3(obj.filldbl3);
		return obj;
	}

	//wiserd add on 2005-04-06
	/**
	 * 判断当前用户是否是市局用户
	 */
	public boolean isShiJuUser() {
		//判断是否工商所用户
		if (isGongShangSuoUser() || isGuanPianUser()) {
			return false;
		}

//		根据 dqdm的后2字节 判断是否为 分/市局
		String sDjjg = this.getPtDeptBean().getDqdm();
		//地区代码是否合法，代码后2位是否为 '00'
		if (sDjjg == null || sDjjg.length() != 6 || ! sDjjg.substring(4,6).equals("00")) {
			return false;
		}
		//地区代码和管辖代码是否相同，口岸站不同
		String sGxjg = this.getPtDeptBean().getFillstr10();
		if (sGxjg == null || sGxjg != null && ! sGxjg.equals(sDjjg)) {
			return false;
		}

		return true;
	}

	/**
	 * 判断当前用户是否是工商所用户
	 * @return
	 */
	public boolean isGongShangSuoUser() {
		String sMkdm = this.getPtDeptBean().getMkdm();
		if (sMkdm == null || ! sMkdm.equals("99")) {
			return false;
		}

		return true;
	}

	/**
	 * 判断当前用户是否是普通分局用户
	 * @return
	 */
	public boolean isFenJuUser() {
//		判断是否工商所用户
		if (isGongShangSuoUser() || isGuanPianUser()) {
			return false;
		}

		//根据 dqdm的后2字节 判断是否为 分/市局
		String sDjjg = this.getPtDeptBean().getDqdm();
		//地区代码是否合法
		if (sDjjg == null || sDjjg.length() != 6) {
			return false;
		}
		//地区代码和管辖代码是否相同，口岸站不同
		String sGxjg = this.getPtDeptBean().getFillstr10();
		if (sGxjg == null) {
			return false;
		}
		if (sDjjg.substring(4,6).equals("00") && sGxjg.equals(sDjjg)) {
			return false;
		}

		return true;
	}

	/**
	 * 判断当前用户是否是管片用户
	 * @return
	 */
	public boolean isGuanPianUser() {
		String sMkdm = this.getPtDeptBean().getMkdm();
		if (sMkdm == null || ! sMkdm.equals("90")) {
			return false;
		}

		return true;
	}
}
