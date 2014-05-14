/**
 *  Copyright 2003 ZhongTian, Inc. All rights reserved. qingdao tec
 *  PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. $Id: ConnectionManager.java,v 1.1 2006/05/17 09:19:30 wiserd Exp $
 *  File:ConnectionManager.java Date Author Changes March 5 2003 wangdeliang
 *  Created
 */

package pub.platform.db;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import zt.concurrent.concurrent.ConcurrentHashMap;

import pub.platform.advance.utils.PropertyManager;

import javax.sql.rowset.CachedRowSet;

/**
 * 数据库管理器
 */

public class ConnectionManager {
    private static final Log logger = LogFactory.getLog(ConnectionManager.class);

    /**
     * 线程标识<--->数据库连接(Key-Value)
     */
    private static HashMap conns = null;

    // 数据库资源检测容器,用连接的句柄作主键
    public static Map badconn = new ConcurrentHashMap();

    private static ConnectionManager manager = null;

    String sDBDriver = "";

    String sConnStr = "";

    String user = "";

    String passwd = "";

    /**
     * @roseuid 3E5EB14400C7 /**
     * @roseuid 3E5EB14400C7
     */
    private ConnectionManager() {
        if (conns == null)
            conns = new HashMap();

        String s = null;
        if ((s = PropertyManager.getProperty("pub.platform.db.ConnectionManager.sDBDriver")) != null) {
            this.sDBDriver = s;
        }
        if ((s = PropertyManager.getProperty("pub.platform.db.ConnectionManager.sConnStr")) != null) {
            this.sConnStr = s;
        }
        if ((s = PropertyManager.getProperty("pub.platform.db.ConnectionManager.user")) != null) {
            this.user = s;
        }
        if ((s = PropertyManager.getProperty("pub.platform.db.ConnectionManager.passwd")) != null) {
            this.passwd = s;
        }
    }

    /**
     * /** 得到管理实例
     *
     * @return ConnectionManager
     * @roseuid 3E62D37A011D
     */
    public static ConnectionManager getInstance() {
        if (manager == null) {
            manager = new ConnectionManager();
        }
        return manager;
    }

    /**
     * 得到缺省的数据库连接,以线程ID保存
     *
     * @return zt.power.front.db.DatabaseConnection
     * @roseuid 3E5EB1AE011A
     */
    public DatabaseConnection get() {
        return getConnection(null, null);
    }

    /**
     * 得到指定数据源URL的数据库连接,以线程ID保存
     *
     * @param url
     * @return
     */
    public DatabaseConnection get(String url) {
        return getConnection(url, null);
    }

    /**
     * 得到指定数据源URL的数据库连接,并以指定的id保存
     *
     * @param url
     * @param id
     * @return
     */
    public DatabaseConnection get(String url, String id) {
        return getConnection(url, id);
    }

    /**
     * 释放数据库连接
     *
     * @roseuid 3E5EB1CE030B
     */
    public void release() {
        releaseConnection1(null);
    }

    /**
     * 释放指定的数据库连接
     *
     * @param id
     */
    public void release(String id) {
        releaseConnection1(id);
    }

    /**
     * 判断该线程的数据库连接是否已经存在
     *
     * @return
     */
    public boolean exist() {
        return isExist(null);
    }

    /**
     * 判断指定id的数据库连接是否已经存在
     *
     * @param id
     * @return
     */
    public boolean exist(String id) {
        return isExist(id);
    }

    /**
     * Description of the Method
     *
     * @param con Description of the Parameter
     */
    public void releaseConnection(DatabaseConnection con) {
        try {
            con.close();
        } catch (Exception e) {

        }
    }

    private boolean isExist(String id) {
        if (id == null) {
            id = "" + Thread.currentThread().hashCode();
        }
        return conns.containsKey(id);
    }

    private void releaseConnection1(String id) {
        try {
            if (id == null)
                id = "" + Thread.currentThread().hashCode();
            if (conns.containsKey(id)) {
                DatabaseConnection dc = (DatabaseConnection) conns.get(id);
                dc.close();
                conns.remove(id);

                logger.debug("关闭数据库链接：" + dc);

            }

        } catch (Exception e) {
            logger.error(e);
        }
    }

    private DatabaseConnection getConnection(String url, String id) {
        if (url == null) {
            url = DatabaseConnection.DEFAULT_DATASOURCE_URL;
        }
        if (id == null) {
            id = "" + Thread.currentThread().hashCode();
        }
        if (conns.containsKey(id)) {
            return (DatabaseConnection) conns.get(id);
        }

        DatabaseConnection dc = getConnection();
        conns.put(id, dc);
        logger.debug("获得数据库链接：" + dc);
        return dc;
    }

    /**
     * Gets the connection attribute of the ConnectionManager object
     *
     * @return The connection value
     */
    public DatabaseConnection getConnection() {
        DatabaseConnection con = null;
        try {
            con = new DatabaseConnection();
        } catch (NoAvailableResourceException ex) {
            try {
                con = new DatabaseConnection(sDBDriver, sConnStr, user, passwd);
            } catch (NoAvailableResourceException ex1) {
                ex1.printStackTrace();
            }
        }
        if (con.getConnection() == null) {
            return null;
        } else {
            return con;
        }
    }

    public CachedRowSet getRs(String p_sql) throws Exception {
        return DB2_81.getRs(p_sql);
    }

    public String getCellValue(String p_field, String p_table, String p_where) throws
            Exception {
        return DB2_81.getCellValue(p_field, p_table, p_where);
    }

    public boolean execBatch(String[] p_sql) throws Exception {
        return DB2_81.execBatch(p_sql);
    }


    public static void main(String[] argv) {
        ConnectionManager cm = ConnectionManager.getInstance();
        DatabaseConnection dc = cm.getConnection();
        Date date = new Date(104, 03, 1);

        // String dateStr = "insert into
        // testtype(chty,ch2ty,lgty,flty,intty,daty)values('ddddddd','dsafsdfsda',33333,33.444,3333,'"+DBUtil.formatWithSql92Date(date)+"')";

        // System.out.print(dateStr);
        // dc.executeUpdate("insert into
        // testtype(chty,ch2ty,lgty,flty,intty,daty)values('ddddddd','dsafsdfsda',33333,33.444,3333,"+DBUtil.formatWithSql92Date(date)+")");
        RecordSet rs = dc.executeQuery("select * from testtype");
        while (rs.next()) {
            System.out.println(rs.getString("test"));
        }
        rs.close();
        cm.releaseConnection(dc);
    }
}
