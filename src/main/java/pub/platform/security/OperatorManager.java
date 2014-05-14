package pub.platform.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.advance.utils.PropertyManager;
import pub.platform.db.ConnectionManager;
import pub.platform.db.DatabaseConnection;
import pub.platform.system.manage.dao.PtDeptBean;
import pub.platform.system.manage.dao.PtOperBean;
import pub.platform.utils.BusinessDate;
import pub.platform.utils.ImgSign;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * <p>
 * Title: OperatorManager.java
 * </p>
 * <p>
 * Description: This class includes the basic behaviors of operator.
 * </p>
 * <p>
 * Copyright: Copyright (c) 2003
 * </p>
 * <p>
 * Company:
 * </p>
 * 
 * @author WangHaiLei
 * @version 1.6 $ UpdateDate: Y-M-D-H-M: 2003-12-02-09-50 2004-03-01-20-35 $
 */
public class OperatorManager implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(OperatorManager.class);
	/**
	 * operatorid是从login(operatorid, password)中得到的。
	 */
	private String fimgSign = "";

	private String operatorname = null;

	private String operatorid = null;

    //当前权限下的菜单项（不包含子菜单）
	private String xmlString = null;

    //当前权限下的全部菜单
	private String jsonString = null;

    /*
    20100820 zhanrui
    当前权限下的按照targetmachine分类的菜单项
    目前只分为两大类
    1、default：主要是业务菜单（targetmachine 为空或 为default）
    2、system：主要是系统管理相关菜单
     */
    private Map jsonMap = new HashMap();

	private Resources resources;

	private String[] roles = new String[] {};

	private MenuBean mb;

	private List jsplist = null;

	private PtOperBean operator;

	private String remoteAddr = null;

	private String remoteHost = null;

	private String loginTime = BusinessDate.getTodaytime();

	private boolean isLogin = false;

	private String filePath = "";

	private String safySign = "";

	public OperatorManager() {

		// //创建图片标示
		createImgSign();
	}

	/**
	 * 
	 * @return
	 */
	public String getXmlString() {
		return (this.xmlString);
	}

	// ////////////////////////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * 返回一个Operator Object。这个Ojbect中包含该操作员的基本信息，包括：operid, email, enabled, sex,
	 * status, opername。
	 * 
	 * @return Operator Ojbect.
	 */
	public PtOperBean getOperator() {
		return operator;
	}

	public String filePath() {
		return filePath;
	}

	/**
	 * 得到当前操作员的operatorname。
	 * 
	 * @return
	 */
	public String getOperatorName() {
		return operator.getOpername();
	}

	/**
	 * 
	 * @return
	 */
	public String getOperatorId() {
		return operatorid;
	}

	/**
	 * 操作员签到，验证operid+passwd是否正确 签到成功后 1.isLogin=true 2.取得该操作员相关的所有角色 3.初始化资源列表
	 * 4.取得操作员的菜单
	 * 
	 * @param operid
	 * @param password
	 * @return boolean
	 * @roseuid 3F80B6360281
	 */
	public boolean login(String operid, String password) {

		ConnectionManager cm = ConnectionManager.getInstance();
		DatabaseConnection dc = cm.get();
        //SqlSession session = IbatisFactory.ORACLE.getInstance().openSession();
		try {
			String loginWhere = "where operid='" + operid
					+ "' and operpasswd ='" + password + "'and operenabled='1'";
            this.operatorid = operid;
            operator = new PtOperBean();
            operator = (PtOperBean) operator.findFirstByWhere(loginWhere);
            if (operator == null) {
                isLogin = false;
                return false;
            }

            String sss = "登录时间 :" + loginTime + " IP: " + remoteAddr
                    + " 机器名称 : " + remoteHost;

            operator.setFillstr600(sss);

            PtDeptBean ptpdet = new PtDeptBean();

            operator.setPtDeptBean((PtDeptBean) ptpdet
                    .findFirstByWhere("where deptid='" + operator.getDeptid()
                            + "'"));

            this.operatorname = operator.getOpername();
            isLogin = true;
            // 取得该操作员的所有角色。dep
            // PtOperRoleBean porb = new PtOperRoleBean();
            // List porbs = porb.findByWhere("where operid='"+operid+"'");
            // roles = new String[porbs.size()];
            // for ( int i = 0 ; i < porbs.size() ; i++ ) {
            // roles[i] = ((PtOperRoleBean)porbs.get(i)).getRoleid();
            // }
            // 初始化资源列表。
            resources = new Resources(operid);
            // 初始化菜单。
            try {
                mb = new MenuBean();

                //this.xmlString = mb.generateStream(operid);
                //this.jsonString = mb.generateJsonStream(operid);
                this.jsonMap.put("default", mb.generateJsonStream(operid, "default"));
                this.jsonMap.put("system", mb.generateJsonStream(operid, "system"));

            } catch (Exception ex3) {
                ex3.printStackTrace();
                System.err.println("Wrong when getting menus of operator: [ "
                        + ex3 + "]");
            }

            return isLogin;
        } catch (Exception e) {
            logger.error("获取登录信息错误！", e);
			return false;
		} finally {
			cm.release();
            //session.close();
		}

	}

    /**
     * 单点登录入口 不检查pwd
     * @param operid
     * @return
     */
	public boolean login4sso(String operid) {

		ConnectionManager cm = ConnectionManager.getInstance();
		DatabaseConnection dc = cm.get();
		try {
			String loginWhere = "where operid='" + operid
					+ "' and operenabled='1'";
            this.operatorid = operid;
            operator = new PtOperBean();
            operator = (PtOperBean) operator.findFirstByWhere(loginWhere);
            if (operator == null) {
                isLogin = false;
                return false;
            }

            String sss = "登录时间 :" + loginTime + " IP: " + remoteAddr
                    + " 机器名称 : " + remoteHost;

            operator.setFillstr600(sss);

            PtDeptBean ptpdet = new PtDeptBean();

            operator.setPtDeptBean((PtDeptBean) ptpdet
                    .findFirstByWhere("where deptid='" + operator.getDeptid()
                            + "'"));

            this.operatorname = operator.getOpername();
            isLogin = true;

            // 初始化资源列表。
            resources = new Resources(operid);
            // 初始化菜单。
            try {
                mb = new MenuBean();

                //this.xmlString = mb.generateStream(operid);
                //this.jsonString = mb.generateJsonStream(operid);
                String aDefault = mb.generateJsonStream(operid, "default");
                this.jsonMap.put("default", aDefault);
                String system = mb.generateJsonStream(operid, "system");
                this.jsonMap.put("system", system);

                if ((aDefault+system).length() <=100) {
                    return false;
                }

            } catch (Exception ex3) {
                ex3.printStackTrace();
                System.err.println("Wrong when getting menus of operator: [ "
                        + ex3 + "]");
            }

            return isLogin;
        } catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			cm.release();
		}

	}



	/**
	 * @return ArrayList
	 * @roseuid 3F80B71A01BC
	 */
	public List getJspList() {
		return jsplist;
	}

	/**
	 * @return boolean
	 * @roseuid 3F80B71A00BC
	 */
	public boolean isLogin() {
		return isLogin;
	}

	/**
	 * 检验权限 1。取到合法资源标识 2。使用Resources的checkPermission方法校验
	 * 
	 * @param resource
	 * @return boolean
	 * @roseuid 3F80B8590151
	 */
	public boolean checkPermission(String resource, int type, String url) {
		boolean permit = resources.checkPermission(resource, type, url);
		return permit;
	}

	/**
	 * 签退
	 */
	public void logout() {

		isLogin = false;
		resources = null;
		operator = null;
		operatorname = null;
		operatorid = null;
		roles = null;
		mb = null;
		xmlString = null;
		jsonString = null;
		remoteHost = null;
		remoteAddr = null;
		loginTime = null;
	}

	public void setRemoteAddr(String remoteAddr) {
		this.remoteAddr = remoteAddr;
	}

	public void setRemoteHost(String remoteHost) {
		this.remoteHost = remoteHost;
	}

	public void createImgSign2() {

		try {
			String deptfillstr100 = PropertyManager.getProperty("cims");
			deptfillstr100 = new String(deptfillstr100.getBytes(), "GBK");
			String lastFile = System.currentTimeMillis() + "";

			ImgSign imgSign = new ImgSign();
			filePath = "/images/" + lastFile + ".jpg";
			safySign = imgSign.creatImgSign(deptfillstr100 + filePath);

		} catch (Exception e) {
		}

		/*
		 * try { String deptfillstr100 = PropertyManager.getProperty("cims");
		 * deptfillstr100 = new String(deptfillstr100.getBytes(), "GBK"); String
		 * lastFile = System.currentTimeMillis() + "";
		 * 
		 * File file = new File(deptfillstr100 + "/images"); if (!file.exists()) {
		 * file.mkdir(); }
		 * 
		 * File f = new File(deptfillstr100 + "/images"); File[] files =
		 * f.listFiles(); for (int i = 0; i < files.length; i++) { if
		 * (files[i].isFile() && files[i].exists()) {
		 * 
		 * if (Integer.parseInt((System.currentTimeMillis() + "") .substring(0,
		 * 5)) - Integer.parseInt(Util.strtoint(files[i].getName() .substring(0,
		 * 5))) > 0) files[i].delete();
		 *  } }
		 * 
		 * ImgSign imgSign = new ImgSign(); filePath = "/images/" + lastFile +
		 * ".jpg"; safySign = imgSign.creatImgSign(deptfillstr100 + filePath);
		 *  } catch (Exception e) {
		 *  }
		 */

	}

	public boolean ImgSign(String sign) {
		boolean retbool = false;

		try {
			ImgSignDel();

			if (sign.equals(safySign))
				retbool = true;

			return retbool;
		} catch (Exception e) {
			return retbool;
		}

	}

	public void ImgSignDel() {
		try {
			String deptfillstr100 = PropertyManager.getProperty("cims");
			deptfillstr100 = new String(deptfillstr100.getBytes(), "GBK");
			File f = new File(deptfillstr100 + filePath);
			if (f.exists())
				f.delete();

		} catch (Exception e) {

		}
	}

	private void createImgSign() {
		fimgSign = "";

		try {

			int rad = (int) Math.round(Math.random() * 10);
			if (rad == 10)
				rad = 9;
			fimgSign += rad;

			rad = (int) Math.round(Math.random() * 10);
			if (rad == 10)
				rad = 9;
			fimgSign += rad;

			rad = (int) Math.round(Math.random() * 10);
			if (rad == 10)
				rad = 9;
			fimgSign += rad;

			rad = (int) Math.round(Math.random() * 10);
			if (rad == 10)
				rad = 9;
			fimgSign += rad;

		} catch (Exception e) {

		}
	}

	public String getImgSign() {

		return fimgSign;
	}

    public String getJsonString(){
        return jsonString;
    }

    public String getJsonString(String target){
        return (String)this.jsonMap.get(target);
    }

}
