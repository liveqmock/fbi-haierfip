/**
 *  Copyright 2003 ZhongTian, Inc. All rights reserved. qingdao tec
 *  PROPRIETARY/CONFIDENTIAL. Use is subject to license terms. $Id: DatabaseConnection.java,v 1.2 2006/05/18 08:20:22 wiserd Exp $
 *  File:DatabaseConnection.java Date Author Changes March 5 2003 wangdeliang
 *  Created
 */

package pub.platform.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pub.platform.debug.BussDebug;
import pub.platform.debug.DebugInfo;
import pub.platform.utils.*;

/**
 * 负责访问数据库
 * 
 */

public class DatabaseConnection {
  private static final Log logger = LogFactory.getLog(DatabaseConnection.class);

  // 获得出错时的例外
  protected Exception errorException;

  /**
   * 数据源默认的名字
   */
  public final static String DEFAULT_ENV_CONTEXT_URL = "java:comp/env";

  /**
   * Description of the Field
   */
  public final static String DEFAULT_DATASOURCE_URL = "ccb_pool";

  private Connection connect;

  /**
   * 事务是否自动提交(true-自动提交,false-手工提交)
   * 
   */
  private boolean isAuto = true;

  /**
   * 事务的层次,初始为0
   */
  private int layers = 0;

  /**
   * 申请连接，失败则抛出例外NoAvailableResourceException
   * 
   * @exception NoAvailableResourceException
   *              Description of the Exception
   * @roseuid 3E5B223E0372
   */
  public DatabaseConnection() throws NoAvailableResourceException {
    try {
      Hashtable prop = new Hashtable();
      // prop.put(Context.PROVIDER_URL,"t3://172.1.1.16:7001");
      Context initCtx = new InitialContext(prop);
      // Context envCtx = (Context) initCtx.lookup(DEFAULT_ENV_CONTEXT_URL);
      DataSource ds = (DataSource) initCtx.lookup(DEFAULT_DATASOURCE_URL);

      connect = ds.getConnection();
      // 记录申请连接
      this.register();

    } catch (Exception e) {
      errorException = e;
      throw new NoAvailableResourceException(e.getMessage());
    }
  }

  /**
   * Constructor for the DatabaseConnection object
   * 
   * @param sDBDriver
   *          Description of the Parameter
   * @param sConnStr
   *          Description of the Parameter
   * @param user
   *          Description of the Parameter
   * @param passwd
   *          Description of the Parameter
   * @exception NoAvailableResourceException
   *              Description of the Exception
   */

  public DatabaseConnection(String sDBDriver, String sConnStr, String user, String passwd)
      throws NoAvailableResourceException {
    try {
      Class.forName(sDBDriver).newInstance();

    } catch (Exception ex) {
      errorException = ex;
      ex.printStackTrace();
    }

    try {
      connect = DriverManager.getConnection(sConnStr, user, passwd);
      // 记录申请连接
      this.register();

    } catch (SQLException ex) {
      errorException = ex;
      ex.printStackTrace();
    }
  }

  /**
   * 开始事务
   */
  public void begin() {
    /*
     * isAuto:自动提交为true,否则为false.当调用该方法时， isAuto属性置为false且layers加1 @roseuid
     * 3E5B22540070
     */
    isAuto = false;
    try {
      if (connect.getAutoCommit())
        connect.setAutoCommit(false);
    } catch (SQLException sqle) {
    }
    layers++;
  }

  /**
   * 提交事务
   */
  public void commit() {
    /*
     * layers--,如果layers等于0,则提交事务 @roseuid 3E5B22D7024F
     */
    if (!isAuto) {
      layers--;
      if (layers == 0) {
        try {
          connect.commit();
          connect.setAutoCommit(true);
          isAuto = true;
        } catch (SQLException sqle) {
        }
      } // if
    }
  }

  /**
   * 回滚事务
   */
  public void rollback() {
    /*
     * 执行事务回滚，清理环境,layers = 0 @roseuid 3E5B22E40258
     */
    if (!isAuto) {
      layers = 0;
      try {
        connect.rollback();
        isAuto = true;
      } catch (SQLException sqle) {
      }
    } // if
  }

  /**
   * /** 执行数据库更新语句
   * 
   * @param sql
   *          sql语句
   * @return int 操作是否成功
   * @roseuid 3E5B22F70179
   */
  public int executeUpdate(String sql) {
    if (sql == null || sql.trim().length() == 0) {
      logger.error("DatabaseConnection.executeUpdate's sql parameter is null!!!");
      return -1000;
    }

    // 打印sql
    printsql(sql);

    try {
      Statement st = connect.createStatement();
      int result = st.executeUpdate(sql);
      st.close();
      return result;
    } catch (SQLException sqle) {
      errorException = sqle;

      logger.error("更新错误：", sqle);
      if (sqle.getErrorCode() < 0) {
        return sqle.getErrorCode();
      } else {
        return sqle.getErrorCode() * (-1);
      }
    }
  }

  /**
   * 执行sql查询语句
   * 
   * @param sql
   * @return com.zt.db.RecordSet
   * @roseuid 3E5B230E0281
   */
  public RecordSet executeQuery(String sql) {
    if (sql == null || sql.trim().length() == 0) {
      logger.error("DatabaseConnection.executeQuery's sql parameter is null!!!");
      return null;
    }

    // ////////打印sql
    printsql(sql);

    try {
      Statement st = connect.createStatement();

      ResultSet rs = st.executeQuery(sql);
      RecordSet records = new RecordSet(rs);

      rs.close();
      st.close();
      return records;
    } catch (SQLException sqle) {
      errorException = sqle;
      logger.error("查询错误：", sqle);
      return null;
    }
  }

  public ResultSet executeResultSetQuery(String sql) {
    if (sql == null || sql.trim().length() == 0) {
      logger.error("DatabaseConnection.executeQuery's sql parameter is null!!!");
      return null;
    }

    // ////////打印sql
    printsql(sql);

    try {
      Statement st = connect.createStatement();
      ResultSet rs = st.executeQuery(sql);
      return rs;
    } catch (SQLException sqle) {
      errorException = sqle;
      logger.error("查询错误：", sqle);
      return null;
    }
  }

  /**
   * Description of the Method
   * 
   * @param sql
   *          Description of the Parameter
   * @param beginIndex
   *          Description of the Parameter
   * @param resultNo
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public RecordSet executeQuery(String sql, int beginIndex, int resultNo) {

    if (sql == null || sql.trim().length() == 0) {
      logger.error("DatabaseConnection.executeQuery's sql parameter is null!!!");
      return new RecordSet();
    }

    // ////////打印sql
    printsql(sql);

    try {
      String pageSql = "";

      Statement st = connect.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      if (Basic.getDbType().equals("DB2")) {
        pageSql = " select * from (SELECT ta.*, ROWNUMBER() OVER ()  rn FROM ( " + sql + ") ta FETCH FIRST "
            + (beginIndex - 1 + resultNo) + " ROW ONLY) tb where tb.rn>" + (beginIndex - 1);

      } else
        pageSql = " select * from ( select t1.*, rownum rnum from ( " + sql + " ) t1 where rownum<= "
            + (beginIndex - 1 + resultNo) + " ) t2 where t2.rnum> " + (beginIndex - 1);

      // //////// 打印sql
      printsql("page sql:" + pageSql);
      ResultSet rs = st.executeQuery(pageSql);
      RecordSet records = new RecordSet(rs);

      rs.close();
      st.close();
      return records;
    } catch (SQLException sqle) {
      errorException = sqle;
      logger.error("查询错误：", sqle);
      return new RecordSet();
    }
  }

  /**
   * 判断记录是否存在
   * 
   * @param sql
   * @return
   */
  public boolean isExist(String sql) {
    try {
      Statement st = connect.createStatement();
      ResultSet rs = st.executeQuery(sql);
      boolean exist = rs.next();
      st.close();
      return exist;
    } catch (SQLException sqle) {
      errorException = sqle;
      logger.error("查询错误：", sqle);
      return false;
    }
  }

  /**
   * 判断事务是否自动提交
   * 
   * @return boolean
   * @roseuid 3E5B244903E2
   */
  public boolean isAuto() {
    return isAuto;
  }

  /**
   * 设置事务的提交方式
   * 
   * @param isAuto
   *          boolean类型
   * @roseuid 3E5EA680009C
   */
  public void setAuto(boolean isAuto) {
    try {
      connect.setAutoCommit(isAuto);
    } catch (SQLException sqle) {
      errorException = sqle;
    }
    this.isAuto = isAuto;
  }

  /**
   * 关闭数据库连接,使用完之后一定调用该方法释放数据库连接,否则会造成资源的浪费
   * 
   * @roseuid 3E5B3532018E
   */
  public void close() {
    try {
      connect.close();
      // 记录关闭连接
      this.remove();

    } catch (SQLException ex) {
      errorException = ex;
      ex.printStackTrace();
    }
  }

  /**
   * Gets the preparedStatement attribute of the DatabaseConnection object
   * 
   * @param str
   *          Description of the Parameter
   * @return The preparedStatement value
   */
  public PreparedStatement getPreparedStatement(String str) {
    try {

      PreparedStatement st = connect.prepareStatement(str, ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY);
      return st;
    } catch (SQLException ex) {
      errorException = ex;
      return null;
    }
  }

  /**
   * Description of the Method
   * 
   * @param pst
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public RecordSet executeQuery(PreparedStatement pst) {
    if (pst == null) {
      return new RecordSet();
    }
    try {
      ResultSet rs = pst.executeQuery();
      ConnectionHealth.end(this);
      RecordSet records = new RecordSet(rs);

      pst.close();
      return records;
    } catch (SQLException sqle) {
      errorException = sqle;
      logger.error("查询错误：", sqle);
      return new RecordSet();
    }
  }

  /**
   * Description of the Method
   * 
   * @param pst
   *          Description of the Parameter
   * @param beginIndex
   *          Description of the Parameter
   * @param resultNo
   *          Description of the Parameter
   * @return Description of the Return Value
   */
  public RecordSet executeQuery(PreparedStatement pst, int beginIndex, int resultNo) {
    if (pst == null) {
      System.out.println("DatabaseConnection.executeQuery's pst parameter is null!!!");
      return new RecordSet();
    }

    try {

      pst.setMaxRows(beginIndex - 1 + resultNo);

      ResultSet rs = pst.executeQuery();
      if (beginIndex != 1) {
        rs.absolute(beginIndex - 1);
      }

      RecordSet records = new RecordSet(rs, resultNo);

      rs.close();
      pst.close();
      return records;
    } catch (SQLException sqle) {
      errorException = sqle;
      logger.error("查询错误：", sqle);
      return new RecordSet();
    }
  }

  /**
   * Gets the connection attribute of the DatabaseConnection object
   * 
   * @return The connection value
   */
  public Connection getConnection() {
    return connect;
  }

  public String getErrorMsg() {
    return "";
  }

  public void setErrorMsg(String errorMsg) {
    if (errorMsg != null) {
      if (errorMsg.indexOf("SQL") > 0) {
        errorMsg = errorMsg.substring(errorMsg.indexOf("SQL"), errorMsg.length());
        errorMsg = DBUtil.fromDB(errorMsg);
      }
    }
  }

  public Exception getErrorException() {
    return errorException;
  }

  private void printsql(String sql) {
    logger.debug(sql);
  }

  // 是否检测死链接
  private boolean debug = false;

  private void register() {
    if (debug)
      ConnectionManager.badconn.put(this.hashCode() + "", new DebugInfo(BussDebug.inferCaller()));
  }

  private void remove() {
    if (debug)
      ConnectionManager.badconn.remove(this.hashCode() + "");
  }
}
