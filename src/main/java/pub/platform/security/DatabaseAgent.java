package pub.platform.security;

import pub.platform.db.ConnectionManager;
import pub.platform.db.DBUtil;
import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: The persistent state (ie. database).</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: ZhongTian</p>
 *
 * @author WangHaiLei
 * @version 1.4
 *          $ UpdateDate: Y-M-D-H-M:
 *          2003-11-25-10-55
 *          2004-02-28-11-05
 *          2003-03-01-22-15
 *          $
 */
public class DatabaseAgent {

    DBUtil dbut = new DBUtil();

    /**
     * Retrieve all the basic information of a Operator, Except password.
     *
     * @param
     * @return Map which includes all the basic info in a HashMap with Key & Value pare.
     * @throws Exception
     */
    public Map getBasicOfOperator(String operatorId)
            throws Exception {

        // 如果操作员根本不存在，则返回null。
        if (operatorId == null) {
            return null;
        }

        String SQL_getBasicOfOperator = "" +
                "SELECT * " +
                "FROM ptoper " +
                "WHERE operid = '" + operatorId + "'";

        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;
        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_getBasicOfOperator);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getBasicOfOperator(String operid).    [" + ex + "] ");
        }

        String nameOfOperator = null;
        String idOfOperator = null;
        String departmentId = null;
        String isSuper = null;
        String operatorType = null;
        String sexOfOperator = null;
        String emailOfOperator = null;
        String mobilePhone = null;
        String operatorPhone = null;
        String otherPhone = null;
        String enabledOfOperator = null;
        String combinedSequence = null;
        String isCombined = null;

        int statusOfOperator = 0;

        if (rs == null)
            rs = new RecordSet();

        if (!rs.next()) {
            return null;
        } else {
            try {
                if (rs.getString("opername") != null) {
                    nameOfOperator = rs.getString("opername").trim();
                } else {
                    nameOfOperator = "";
                }
                if (rs.getString("operid") != null) {
                    idOfOperator = rs.getString("operid").trim();
                } else {
                    idOfOperator = "";
                }
                if (rs.getString("deptid") != null) {
                    departmentId = rs.getString("deptid").trim();
                } else {
                    departmentId = "";
                }
                if (rs.getString("issuper") != null) {
                    isSuper = rs.getString("issuper").trim();
                } else {
                    isSuper = "";
                }
                if (rs.getString("opertype") != null) {
                    operatorType = rs.getString("opertype").trim();
                } else {
                    operatorType = "";
                }
                if (rs.getString("mobilephone") != null) {
                    mobilePhone = rs.getString("mobilephone").trim();
                } else {
                    mobilePhone = "";
                }
                if (rs.getString("operphone") != null) {
                    operatorPhone = rs.getString("operphone").trim();
                } else {
                    operatorPhone = "";
                }
                if (rs.getString("otherphone") != null) {
                    otherPhone = rs.getString("otherphone").trim();
                } else {
                    otherPhone = "";
                }
                if (rs.getString("cmbtxnseq") != null) {
                    combinedSequence = rs.getString("cmbtxnseq").trim();
                } else {
                    combinedSequence = "";
                }
                if (rs.getString("iscmbend") != null) {
                    isCombined = rs.getString("iscmbend").trim();
                } else {
                    isCombined = "";
                }
                if (rs.getString("sex") != null) {
                    sexOfOperator = rs.getString("sex").trim();
                } else {
                    sexOfOperator = "";
                }
                if (rs.getString("email") != null) {
                    emailOfOperator = rs.getString("email").trim();
                } else {
                    emailOfOperator = "";
                }
                if (rs.getString("operenabled") != null) {
                    enabledOfOperator = rs.getString("operenabled").trim();
                } else {
                    enabledOfOperator = "";
                }

                cm.releaseConnection(dc);
            } catch (Exception ex1) {
                System.err.println("Wrong, when getting data from RecordSet.   Place zt.platform.security.DatabaseAgent.getBasicOfOperator(String operid).    [" + ex1 + "] ");
            }

            Map basicInfo = new HashMap();

            basicInfo.put("opnm", nameOfOperator);
            basicInfo.put("opid", idOfOperator);
            basicInfo.put("sexo", sexOfOperator);
            basicInfo.put("emai", emailOfOperator);
            basicInfo.put("enab", enabledOfOperator);
            basicInfo.put("issu", isSuper);
            basicInfo.put("depi", departmentId);
            basicInfo.put("otyp", operatorType);
            basicInfo.put("mobp", mobilePhone);
            basicInfo.put("opep", operatorPhone);
            basicInfo.put("othp", otherPhone);
            basicInfo.put("comb", combinedSequence);
            basicInfo.put("isco", isCombined);

            return basicInfo;
        }
    }

    /**
     * Looks up a operator's password.
     *
     * @return A String containing the operator's password.
     */
    public String getPasswordOfOperator(String operatorId)
            throws Exception {

        // 如果操作员根本不存在，则返回false。
        if (operatorId == null) {
            return null;
        }

        String SQL_GetPasswordOfOperator = "" +
                "SELECT operpasswd " +
                "FROM ptoper " +
                "WHERE operid = '" + operatorId + "'";

        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;
        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_GetPasswordOfOperator);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getPasswordOfOperator(String operid).    [" + ex + "] ");
        }

        // 如果密码不存在，返回null。如果密码存在，则返回密码。
        String passwordOfOperator = null;
        if (rs == null)
            rs = new RecordSet();

        if (!rs.next()) {
            return null;
        } else {
            try {
                passwordOfOperator = rs.getString("operpasswd").trim();
                cm.releaseConnection(dc);
            } catch (Exception ex1) {
                System.err.println("Wrong, when getting data from RecordSet.   Place zt.platform.security.DatabaseAgent.getPasswordOfOperator(String operid).    [" + ex1 + "] ");
            }
            return passwordOfOperator;
        }
    }

    /**
     * 通过operid得到该操作员的operid。
     *
     * @return
     * @throws Exception
     */
    public String getOperatorIdOfOperator(String operatorId)
            throws Exception {

        String SQL_GetOperatorIdOfOperator = "" +
                "SELECT operid " +
                "FROM ptoper u " +
                "WHERE operid='" + operatorId + "'";

        String operatorIdOfOperator = null;
        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_GetOperatorIdOfOperator);
            if (rs == null)
                rs = new RecordSet();


            while (rs.next()) {
                if (rs.getString("operid") != null) {
                    operatorIdOfOperator = rs.getString("operid").trim();
                } else {
                    operatorIdOfOperator = "";
                }
            }
            cm.releaseConnection(dc);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getUserIdOfOperator(String operid)    [" + ex + "] ");
        }

        return operatorIdOfOperator;
    }

    /**
     * @return
     * @throws Exception
     */
    public String[] getRoleIdsOfOperator(String operatorId)
            throws Exception {

        String SQL_GetRolesOfOperator = "" +
                "SELECT roleid " +
                "FROM ptoperrole r " +
                "WHERE operid='" + operatorId + "'";

        String[] roleIdsOfOperator = null;
        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_GetRolesOfOperator);
            if (rs == null)
                rs = new RecordSet();

            List listTemp = new ArrayList();
            while (rs.next()) {
                String roleId = null;
                if (rs.getString("roleid") != null) {
                    roleId = rs.getString("roleid").trim();
                } else {
                    roleId = "";
                }

                listTemp.add(roleId);
            }
            roleIdsOfOperator = new String[listTemp.size()];
            for (int i = 0; i < listTemp.size(); i++) {
                roleIdsOfOperator[i] = listTemp.get(i).toString();
            }
            cm.releaseConnection(dc);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getRoleIdsOfOperator(String operid)    [" + ex + "] ");
        }

        return roleIdsOfOperator;
    }

//     /**
//      * The method is not enabled in this edition.
//      * @param roleId
//      */
//     public String getResourcesOfRole(String roleId)
//          throws Exception {
//
//          String SQL_GetResourcesOfRole = null;
//
//          return new String();
//     }

    /**
     * 输入operid得到该操作员可以使用的Resource Object的Map
     *
     * @return
     * @throws Exception
     */
    public Map getResourcesOfOperator(String operatorId)
            throws Exception {

        String SQL_GetResourcesOfOperator = "" +
                "SELECT distinct " +
                "rs.resid AS resid, " +
                "rs.resname AS res, " +
                "rs.restype AS tp " +
                "FROM ptoperrole ur, " +
                "ptroleres rr, " +
                "ptresource rs " +
                "WHERE ur.operid='" + operatorId + "' " +
                "and rr.roleid=ur.roleid " +
                "and rs.resid=rr.resid " +
                "and rs.resenabled='1'";


        Map resources = null;
        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_GetResourcesOfOperator);

            if (rs == null)
                rs = new RecordSet();

            resources = new HashMap();
            while (rs.next()) {
                String resourceId = null;
                if (rs.getString("resid") != null) {
                    resourceId = rs.getString("resid").trim();
                } else {
                    resourceId = "";
                }

                String resource = null;
                if (rs.getString("res") != null) {
                    resource = rs.getString("res").trim();
                } else {
                    resource = "";
                }

                int restype = rs.getInt("tp");

                Resource r = new Resource(resourceId, resource, restype);
                resources.put(resourceId, r);
            }
            cm.releaseConnection(dc);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getResourcesOfOperator(String operid)    [" + ex + "] ");
        }

        return resources;
    }

    /**
     * @return
     * @throws Exception
     */
    public List getResourceIdsOfOperator(String operatorId)
            throws Exception {

        String SQL_GetResourcesOfOperator = "" +
                "SELECT distinct rs.resid " +
                "FROM ptoperrole rl, " +
                "ptroleres rr, " +
                "ptresource rs, " +
                "WHERE rl.operid='" + operatorId + "' " +
                "and rr.roleid=rl.roleid " +
                "and rs.resid=rl.resid " +
                "and rs.resenabled='1'";

        List resourceIdsOfOperator = null;
        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_GetResourcesOfOperator);

            if (rs == null)
                rs = new RecordSet();

            resourceIdsOfOperator = new ArrayList();
            while (rs.next()) {
                String resourceId = null;
                if (rs.getString("resid") != null) {
                    resourceId = rs.getString("resid").trim();
                } else {
                    resourceId = "";
                }

                resourceIdsOfOperator.add(resourceId);
            }
            cm.releaseConnection(dc);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getResourceIdsOfOperator(String operid).    [" + ex + "] ");
        }

        return resourceIdsOfOperator;
    }

    /**
     * Determines if a operator exists.
     *
     * @param operatorId A String containing the operator's name.
     * @return A boolean.  true if the operator exists, false otherwise.
     */
    public boolean isOperatorExistent(String operatorId) {

        if (operatorId == null) {
            return false;
        }

        String SQL_GetAnOperatorId = "" +
                "SELECT operid " +
                "FROM ptoper " +
                "WHERE operid='" + operatorId + "'";

        String operatorIdGetFromDB = null;
        ConnectionManager cm;
        DatabaseConnection dc;
        RecordSet rs = null;

        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_GetAnOperatorId);
            if (rs == null)
                rs = new RecordSet();
            if (!rs.next()) {
                return false;
            }
            operatorIdGetFromDB = rs.getString("operid").trim();
            cm.releaseConnection(dc);

        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.isOperatorExistent(String operid).    [" + ex + "] ");
        }

        if (operatorId.equals(operatorIdGetFromDB)) {
            return true;
        } else {
            return false;
        }
    }

//     /**
//      * Determines if a operator's password exists.
//      *
//      * @param operator A String containing the operator's name.
//      * @return A boolean.  true if the operator exists, false otherwise.
//      */
//     public boolean isPasswordExistent(String operid) {
//
//          // 如果操作员根本不存在，则返回false。
//          if(operid == null) {
//               return false;
//          }
//
//          String SQL_GetPassworOfOperator = "" +
//               "SELECT operpasswd " +
//               "FROM ptoper " +
//               "WHERE operid = '"+operid+"'";
//
//          ConnectionManager cm = ConnectionManager.getInstance();
//          DatabaseConnection dc = cm.getConnection();
//          RecordSet rs = dc.executeQuery(SQL_GetPassworOfOperator);
//          if(!rs.next()) {
//               return false;
//          } else {
//               return true;
//          }
//
//          String passwordOfOperator = rs.getString("password").trim();
//          if(operid.equals(operidGetFromDB)) {
//               return true;
//          } else {
//               return false;
//          }
//     }

    /**
     * @param operatorId
     * @param roleId
     * @return
     * @throws Exception
     */
    public boolean isOperatorInRole(String operatorId, String roleId)
            throws Exception {

        String SQL_GetRolesOfOperator = "" +
                "SELECT roleid AS roleid " +
                "FROM ptoperrole, " +
                "WHERE operid='" + operatorId + "' ";

        List roleIdsOfOperator = null;
        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_GetRolesOfOperator);

            if (rs == null)
                rs = new RecordSet();
            roleIdsOfOperator = new ArrayList();
            while (rs.next()) {
                String ri = null;
                if (rs.getString("roleid") != null) {
                    ri = rs.getString("roleid").trim();
                } else {
                    ri = "";
                }

                roleIdsOfOperator.add(ri);
            }
            cm.releaseConnection(dc);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.isOperatorInRole(String operid, String roleId).    [" + ex + "] ");
        }

        if (roleIdsOfOperator.contains(roleId)) {
            return true;
        } else {
            return false;
        }
    }

/////////////////////////////////////////////////////////////////////////////////////////////// Public Methods

    /**
     * 输入operid和nodesLevel之后，就得到这个操作员在某一级上的所有的Node Object Array。
     * <p/>
     * 1，得到该操作员的所有角色id。
     * 2，根据roleid，从ptroleresource中得到所有"where restype=4"的resid，即得到了该操作员的所有的menuid
     * 3，从ptresource中取出restype=4并且resid的范围就在allResourcesForThisOperator中的resid。
     * 4，根据上面的resid，从ptmenu通过where level='"+nodesLevel+"'得到在一个指定level上的所有menuid。
     * 5，依次根据每一个menuid从ptmenu表中取出相应的label和url和isleaf，将值分别赋给Node object中的String label, String url, String isleaf，构建每个Node Object。
     * 6，构建一个Node Object的Array。把该操作员所拥有的所有的Node都放进去。返回。大功告成。
     *
     * @param operatorId     操作员ID，就是Loged In的那个操作员。
     * @param menuItemsLevel 想要得到的Menu Item的层级。
     * @return Node] 返回一个该层级上该操作员所看到的Node Oject的一个Array。
     */
    public MenuItemBean[] getMenuItems(String operatorId, int menuItemsLevel)
            throws Exception {

        String SQL_GetMenuItemsForAnOperator = "" +
                "SELECT distinct " +
                "m.menuid AS menuItemId, " +
                "m.menulabel AS menuItemLabel, " +
                "m.isleaf AS menuItemIsLeaf, " +
                "m.menuaction AS menuItemUrl, " +
                "m.menudesc AS menuItemDescription, " +
                "m.OpenWindow AS menuItemOpenWindow, " +
                "m.WindowWidth AS menuItemWindowWidth, " +
                "m.WindowHeight AS menuItemWindowHeight, m.Levelidx,m.targetmachine " +
                ",(select count(ptmenu.MENULABEL) FROM ptoperrole, ptroleres , ptresource , ptmenu " +
                "WHERE ptoperrole.operid = '" + operatorId + "'  and ptroleres.roleid =ptoperrole.roleid and " +
                "ptresource.resid = ptroleres.resid and ptresource.restype = '4' and " +
                "rtrim(ptmenu.menuid) = ptresource.resname and ptmenu.menulevel = " + (menuItemsLevel + 1) + " and ptmenu.PARENTMENUID=m.MENUID) as childcount" +

                " FROM ptoperrole r, " +
                "ptroleres rs, " +
                "ptresource s, " +
                "ptmenu m " +
                "WHERE r.operid = '" + operatorId + "' " +
                "and rs.roleid = r.roleid " +
                "and s.resid = rs.resid " +
                "and s.restype = '4' " +
                "and rtrim(m.menuid) = s.resname " +
                "and m.menulevel = " + menuItemsLevel + "  order by m.Levelidx";

        MenuItemBean[] menuItemsForThisOperator = null;
        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        try {
            cm = ConnectionManager.getInstance();
//               dc = cm.getConnection();
            dc = cm.get();
            rs = dc.executeQuery(SQL_GetMenuItemsForAnOperator);
            if (rs == null)
                rs = new RecordSet();
            List listTemp = new ArrayList();
            while (rs.next()) {
                String menuItemId = null;
                if (rs.getString("menuItemId") != null) {
                    menuItemId = rs.getString("menuItemId").trim();
                } else {
                    menuItemId = "";
                }

                String menuItemLabel = null;
                if (rs.getString("menuItemLabel") != null) {
                    menuItemLabel = rs.getString("menuItemLabel").trim();
                } else {
                    menuItemLabel = "";
                }

                String menuItemIsLeaf = null;
                if (rs.getString("menuItemIsLeaf") != null) {
                    menuItemIsLeaf = rs.getString("menuItemIsLeaf").trim();
                } else {
                    menuItemIsLeaf = "";
                }

                String menuItemUrl = null;
                if (rs.getString("menuItemUrl") != null) {
                    menuItemUrl = rs.getString("menuItemUrl").trim();
                } else {
                    menuItemUrl = "#";
                }

                String menuItemDescription = null;
                if (rs.getString("menuItemDescription") != null) {
                    menuItemDescription = rs.getString("menuItemDescription").trim();
                } else {
                    menuItemDescription = "#";
                }
                String menuItemOpenWindow = "0";
                if (rs.getString("menuItemOpenWindow") != null) {
                    menuItemOpenWindow = rs.getString("menuItemOpenWindow").trim();
                } else {
                    menuItemOpenWindow = "0";
                }

                String menuItemWindowWidth = "0";
                if (rs.getString("menuItemWindowWidth") != null) {
                    menuItemWindowWidth = rs.getString("menuItemWindowWidth").trim();
                } else {
                    menuItemWindowWidth = "0";
                }

                String menuItemWindowHeight = "0";
                if (rs.getString("menuItemWindowHeight") != null) {
                    menuItemWindowHeight = rs.getString("menuItemWindowHeight").trim();
                } else {
                    menuItemWindowHeight = "0";
                }
                String targetmachine = "";
                if (rs.getString("targetmachine") != null) {
                    targetmachine = rs.getString("targetmachine").trim();
                } else {
                    targetmachine = "";
                }
                String childcount = "";
                if (rs.getString("childcount") != null) {
                    childcount = rs.getString("childcount").trim();
                } else {
                    childcount = "";
                }


                listTemp.add(new MenuItemBean(menuItemId, menuItemLabel, menuItemIsLeaf, menuItemUrl, menuItemDescription, menuItemOpenWindow, menuItemWindowWidth, menuItemWindowHeight, targetmachine, childcount));
            }

            menuItemsForThisOperator = new MenuItemBean[listTemp.size()];
            for (int i = 0; i < listTemp.size(); i++) {
                menuItemsForThisOperator[i] = (MenuItemBean) listTemp.get(i);
            }
//               cm.releaseConnection(dc);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getMenuItems(String operid, int menuItemsLevel).    [" + ex + "] ");
        }

        return menuItemsForThisOperator;
    }

    /**
     * getMenuItems()的另一种形式。
     * 多了一个parentId参数，用于非第一级MenuItem。
     *
     * @return
     */
    public MenuItemBean[] getMenuItems(String operatorId, int menuItemsLevel, String parentId) {

        String SQL_GetMenuItemsForAnOperator = "" +
                "SELECT distinct " +
                "m.menuid AS menuItemId, " +
                "m.menulabel AS menuItemLabel, " +
                "m.isleaf AS menuItemIsLeaf, " +
                "m.menuaction AS menuItemUrl, " +
                "m.menudesc AS menuItemDescription, " +
                "m.OpenWindow AS menuItemOpenWindow, " +
                "m.WindowWidth AS menuItemWindowWidth, " +
                "m.WindowHeight AS menuItemWindowHeight, m.Levelidx,m.targetmachine " +
                ",(select count(ptmenu.MENULABEL) FROM ptoperrole, ptroleres , ptresource , ptmenu " +
                "WHERE ptoperrole.operid = '" + operatorId + "'  and ptroleres.roleid =ptoperrole.roleid and " +
                "ptresource.resid = ptroleres.resid and ptresource.restype = '4' and " +
                "rtrim(ptmenu.menuid) = ptresource.resname and ptmenu.menulevel = " + (menuItemsLevel + 1) + " and ptmenu.PARENTMENUID=m.MENUID) as childcount" +

                " FROM ptoperrole r, " +
                "ptroleres rs, " +
                "ptresource s, " +
                "ptmenu m " +
                "WHERE r.operid = '" + operatorId + "' " +
                "and rs.roleid = r.roleid " +
                "and s.resid = rs.resid " +
                "and s.restype = '4' " +
                "and rtrim(m.menuid) = s.resname " +
                "and m.menulevel = " + menuItemsLevel + " " +
                "and m.parentmenuid = '" + parentId + "'  order by m.Levelidx";

        MenuItemBean[] menuItemsForThisOperator = null;
        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        try {
            cm = ConnectionManager.getInstance();
//               dc = cm.getConnection();
            dc = cm.get();
            rs = dc.executeQuery(SQL_GetMenuItemsForAnOperator);
            if (rs == null)
                rs = new RecordSet();
            List listTemp = new ArrayList();
            while (rs.next()) {
                String menuItemId = null;
                if (rs.getString("menuItemId") != null) {
                    menuItemId = rs.getString("menuItemId").trim();
                } else {
                    menuItemId = "";
                }

                String menuItemLabel = null;
                if (rs.getString("menuItemLabel") != null) {
                    menuItemLabel = rs.getString("menuItemLabel").trim();
                } else {
                    menuItemLabel = "";
                }

                String menuItemIsLeaf = null;
                if (rs.getString("menuItemIsLeaf") != null) {
                    menuItemIsLeaf = rs.getString("menuItemIsLeaf").trim();
                } else {
                    menuItemIsLeaf = "";
                }

                String menuItemUrl = null;
                if (rs.getString("menuItemUrl") != null) {
                    menuItemUrl = rs.getString("menuItemUrl").trim();
                } else {
                    menuItemUrl = "#";
                }

                String menuItemDescription = null;
                if (rs.getString("menuItemDescription") != null) {
                    menuItemDescription = rs.getString("menuItemDescription").trim();
                } else {
                    menuItemDescription = "#";
                }

                String menuItemOpenWindow = "0";
                if (rs.getString("menuItemOpenWindow") != null) {
                    menuItemOpenWindow = rs.getString("menuItemOpenWindow").trim();
                } else {
                    menuItemOpenWindow = "0";
                }

                String menuItemWindowWidth = "0";
                if (rs.getString("menuItemWindowWidth") != null) {
                    menuItemWindowWidth = rs.getString("menuItemWindowWidth").trim();
                } else {
                    menuItemWindowWidth = "0";
                }

                String menuItemWindowHeight = "0";
                if (rs.getString("menuItemWindowHeight") != null) {
                    menuItemWindowHeight = rs.getString("menuItemWindowHeight").trim();
                } else {
                    menuItemWindowHeight = "0";
                }

                String targetmachine = "";
                if (rs.getString("targetmachine") != null) {
                    targetmachine = rs.getString("targetmachine").trim();
                } else {
                    targetmachine = "";
                }

                String childcount = "";
                if (rs.getString("childcount") != null) {
                    childcount = rs.getString("childcount").trim();
                } else {
                    childcount = "";
                }

                MenuItemBean aMenuItem = new MenuItemBean(menuItemId, menuItemLabel, menuItemIsLeaf, menuItemUrl, menuItemDescription, menuItemOpenWindow, menuItemWindowWidth, menuItemWindowHeight, targetmachine, childcount);
                listTemp.add(aMenuItem);
            }

            menuItemsForThisOperator = new MenuItemBean[listTemp.size()];
            for (int i = 0; i < listTemp.size(); i++) {
                menuItemsForThisOperator[i] = (MenuItemBean) listTemp.get(i);
            }
//               cm.releaseConnection(dc);
        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getMenuItems(String operid, int menuItemsLevel, String parentId).    [" + ex + "] ");
        }

        return menuItemsForThisOperator;
    }

    /**
     * 20100820 zhanrui   取得某操作员所有菜单项
     *
     * @param operatorId
     * @return
     * @throws Exception
     */
    public List<MenuItemBean> getMenuItems(String operatorId, String targetMachine) throws Exception {

        String whereStr = null;
        if (targetMachine == null) {
            //TODO log error
            throw new RuntimeException("参数错误。");
        }

        if (targetMachine.equals("default")) {
             whereStr = " and (m.targetmachine is null or m.targetmachine = 'default') ";
        }else{
             whereStr = " and (m.targetmachine = '" + targetMachine + "') ";
        }

        String SQL_GetMenuItemsForAnOperator = "" +
                "SELECT distinct " +
                "m.menuid AS menuItemId, " +
                "m.parentmenuid AS menuItemPId, " +
                "m.menuLevel AS menuLevel, " +
                "m.menulabel AS menuItemLabel, " +
                "m.isleaf AS menuItemIsLeaf, " +
                "m.menuaction AS menuItemUrl, " +
                "m.menudesc AS menuItemDescription, " +
                "m.OpenWindow AS menuItemOpenWindow, " +
                "m.WindowWidth AS menuItemWindowWidth, " +
                "m.WindowHeight AS menuItemWindowHeight, m.Levelidx,m.targetmachine " +
                ",(select count(ptmenu.MENULABEL) FROM ptoperrole, ptroleres , ptresource , ptmenu " +
                "WHERE ptoperrole.operid = '" + operatorId + "'  and ptroleres.roleid =ptoperrole.roleid and " +
                "ptresource.resid = ptroleres.resid and ptresource.restype = '4' and " +
                "rtrim(ptmenu.menuid) = ptresource.resname  and ptmenu.PARENTMENUID=m.MENUID) as childcount" +

                " FROM ptoperrole r, " +
                "ptroleres rs, " +
                "ptresource s, " +
                "ptmenu m " +
                "WHERE r.operid = '" + operatorId + "' " +
                "and rs.roleid = r.roleid " +
                "and s.resid = rs.resid " +
                "and s.restype = '4' " +
                "and rtrim(m.menuid) = s.resname " + whereStr +
                " order by m.menuLevel,m.Levelidx";

        MenuItemBean[] menuItemsForThisOperator = null;
        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        List<MenuItemBean> listTemp = new ArrayList();
        try {
            cm = ConnectionManager.getInstance();
            dc = cm.get();
            rs = dc.executeQuery(SQL_GetMenuItemsForAnOperator);
            if (rs == null)
                rs = new RecordSet();
            while (rs.next()) {
                String menuItemId = null;
                if (rs.getString("menuItemId") != null) {
                    menuItemId = rs.getString("menuItemId").trim();
                } else {
                    menuItemId = "";
                }

                String menuItemPId = null;
                if (rs.getString("menuItemPId") != null) {
                    menuItemPId = rs.getString("menuItemPId").trim();
                } else {
                    menuItemId = "";
                }

                int menuLevel = rs.getInt("menuLevel");

                String menuItemLabel = null;
                if (rs.getString("menuItemLabel") != null) {
                    menuItemLabel = rs.getString("menuItemLabel").trim();
                } else {
                    menuItemLabel = "";
                }

                String menuItemIsLeaf = null;
                if (rs.getString("menuItemIsLeaf") != null) {
                    menuItemIsLeaf = rs.getString("menuItemIsLeaf").trim();
                } else {
                    menuItemIsLeaf = "";
                }

                String menuItemUrl = null;
                if (rs.getString("menuItemUrl") != null) {
                    menuItemUrl = rs.getString("menuItemUrl").trim();
                } else {
                    menuItemUrl = "#";
                }

                String menuItemDescription = null;
                if (rs.getString("menuItemDescription") != null) {
                    menuItemDescription = rs.getString("menuItemDescription").trim();
                } else {
                    menuItemDescription = "#";
                }
                String menuItemOpenWindow = "0";
                if (rs.getString("menuItemOpenWindow") != null) {
                    menuItemOpenWindow = rs.getString("menuItemOpenWindow").trim();
                } else {
                    menuItemOpenWindow = "0";
                }

                String menuItemWindowWidth = "0";
                if (rs.getString("menuItemWindowWidth") != null) {
                    menuItemWindowWidth = rs.getString("menuItemWindowWidth").trim();
                } else {
                    menuItemWindowWidth = "0";
                }

                String menuItemWindowHeight = "0";
                if (rs.getString("menuItemWindowHeight") != null) {
                    menuItemWindowHeight = rs.getString("menuItemWindowHeight").trim();
                } else {
                    menuItemWindowHeight = "0";
                }
                String targetmachine = "";
                if (rs.getString("targetmachine") != null) {
                    targetmachine = rs.getString("targetmachine").trim();
                } else {
                    targetmachine = "";
                }
                String childcount = "";
                if (rs.getString("childcount") != null) {
                    childcount = rs.getString("childcount").trim();
                } else {
                    childcount = "";
                }


                listTemp.add(new MenuItemBean(menuItemId, menuItemPId, menuLevel, menuItemLabel, menuItemIsLeaf, menuItemUrl, menuItemDescription, menuItemOpenWindow, menuItemWindowWidth, menuItemWindowHeight, targetmachine, childcount));
            }

        } catch (Exception ex) {
            System.err.println("Wrong, when data retrieving.   Place zt.platform.security.DatabaseAgent.getMenuItems(String operid, int menuItemsLevel).    [" + ex + "] ");
        }

        return listTemp;
    }


    /**
     * @return
     * @throws Exception
     */
    public boolean checkUrl(String operatorId, String url)
            throws Exception {

        String SQL_GetMenuItemsForAnOperator = "" +
                "SELECT " +
                "m.menuaction AS action " +
                "FROM ptoperrole r, " +
                "ptroleres rs, " +
                "ptresource s, " +
                "ptmenu m " +
                "WHERE r.operid = '" + operatorId + "' " +
                "and rs.roleid = r.roleid " +
                "and s.resid = rs.resid " +
                "and s.restype = '4' " +
                "and m.menuid = s.resname " +
                "and m.menuaction like '%" + url + "%' ";

        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        boolean checkpass = false;

        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_GetMenuItemsForAnOperator);
            if (rs == null)
                rs = new RecordSet();
            if (rs.next()) {
                checkpass = true;
            }

            cm.releaseConnection(dc);
        } catch (Exception ex) {
            System.err.println("Wrong, when data check. Place zt.platform.security.DatabaseAgent.checkUrl(String operid, String url).    [" + ex + "] ");
        }

        return checkpass;
    }
    /* 保存所有jsp类型的资源的URL信息
    *
    *
    */

    public ArrayList getAllUrlInfoOfOperator(String operatorId) throws Exception {
        ArrayList al = null;
        String SQL_getAllUrlInfoOfOperator = "" +
                "select distinct a.menuaction " +
                "from ptmenu a," +
                "ptoperrole r," +
                "ptroleres rs," +
                "ptresource s " +
                "where rtrim(a.menuid) = rtrim(s.RESNAME) " +
                "and rtrim(s.RESID)=rtrim(rs.RESID) " +
                "and rtrim(rs.ROLEID)=rtrim(r.ROLEID) " +
                "and rtrim(r.OPERID) = '" + operatorId + "' " +
                "and a.ISLEAF = 1 " +
                "union all " +
                "select s.RESNAME " +
                "from ptoperrole r," +
                "ptroleres rs," +
                "ptresource s " +
                "where rtrim(s.RESID)=rtrim(rs.RESID) " +
                "and rtrim(rs.ROLEID)=rtrim(r.ROLEID) " +
                "and rtrim(r.OPERID) = '" + operatorId + "' " +
                "and s.restype = '2' ";

        ConnectionManager cm = null;
        DatabaseConnection dc = null;
        RecordSet rs = null;

        System.out.println("SQL_getAllUrlInfoOfOperator" + SQL_getAllUrlInfoOfOperator);
        try {
            cm = ConnectionManager.getInstance();
            dc = cm.getConnection();
            rs = dc.executeQuery(SQL_getAllUrlInfoOfOperator);
            if (rs == null)
                rs = new RecordSet();
            al = new ArrayList();
            while (rs.next()) {
                al.add(rs.getString(0));
            }


        } catch (Exception ex) {
            ex.printStackTrace();
            throw new Exception("错误(DatabaseAgent:getAllUrlInfoOfOperator):" + ex.getMessage());
        }
        finally {
            cm.releaseConnection(dc);
        }
        return al;
    }
}
