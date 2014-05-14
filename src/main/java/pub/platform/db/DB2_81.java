package pub.platform.db;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-14
 * Time: 下午4:47
 * To change this template use File | Settings | File Templates.
 */
public class DB2_81 {
    /**
         * 构造
         */
        public DB2_81() {

        }

        /**
         * 获取结果集
         */
        public static CachedRowSet getRs(String p_sql) throws Exception {
            ConnectionManager dbcm = ConnectionManager.getInstance();
            DatabaseConnection dbcn = dbcm.getConnection();
            java.sql.Connection conn = dbcn.getConnection();
            if (conn == null) throw new Exception("数据库连接错误！");

            try {
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(p_sql);

                CachedRowSet crs = DB2_81.createCachedRowSet();
                crs.populate(rs);
                rs.close();
                stmt.close();
                return crs;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new Exception(ex.getMessage());
            } finally {

                if (conn != null) {
                    conn.close();
                }
            }
        }
        public static String getCellValue(String p_field, String p_table, String p_where) throws Exception {
            ConnectionManager dbcm = ConnectionManager.getInstance();
            DatabaseConnection dbcn = dbcm.getConnection();
            java.sql.Connection conn = dbcn.getConnection();
            if (conn == null) throw new Exception("数据库连接错误！");
            String sql = "select " + p_field;
            sql += " from " + p_table;
            sql += " where " + p_where;
            try {
                java.sql.Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(sql);
                while (rs.next()) {
                    return rs.getString(1);
                }
                rs.close();
                stmt.close();
                return "";
            } catch (Exception ex) {
                throw new Exception(ex.getMessage());
            } finally {
                if (conn != null) {
                    conn.close();
                }

            }
        }
        /**
         * 执行Batch
         */
        public static boolean execBatch(String[] p_sql) throws Exception {
            ConnectionManager dbcm = ConnectionManager.getInstance();
            DatabaseConnection dbcn = dbcm.getConnection();
            java.sql.Connection conn = dbcn.getConnection();
            Statement stmt = null;
            if (conn == null) throw new Exception("数据库连接错误！");
            try {
                conn.setAutoCommit(false);
                stmt = conn.createStatement();
                for (int i = 0; i < p_sql.length; i++) {
                    if (p_sql[i] != null) {
                        if (p_sql[i].length() > 0) {
                            stmt.addBatch(p_sql[i]);
                        }
                    }
                }
                stmt.executeBatch();
                conn.commit();
                stmt.close();
            } catch (Exception ex) {
                conn.rollback();
                throw new Exception(ex.getMessage());
            } finally {
                if (conn != null) {
                    conn.close();
                }

            }
            return true;
        }
        public static CachedRowSet createCachedRowSet() throws SQLException {
            Locale locale = Locale.getDefault();
            try {
                Locale.setDefault(Locale.US);
                return new CachedRowSetImpl();
            }
            finally {
                Locale.setDefault(locale);
            }
        }

}
