package pub.platform.utils;

/**
 * 流水号管理器
 *
 * 没有考虑并发的情况，如果出现问题可以加同步标志
 *
 * 1.采用数据库的Sequence实现。
 * 2.单独获得\关闭数据库（不采用线程方式），因为有可能单独使用。
 *
 * @author zhouwei
 * $Date: 2006/05/17 09:19:45 $
 * @version 1.0
 *
 * 版权：leonwoo
 */

import pub.platform.db.*;

public class SerialNumber {

    /**
     * 获得当前的流水号
     * 失败时返回null
     * @param serialName
     * @return
     */
    public static String getNextSerialNo(String serialName) {
        DatabaseConnection dc = null;
        String rtn = null;
        try {
            dc = ConnectionManager.getInstance().getConnection();
            dc.begin();
            RecordSet rs = dc.executeQuery("select " + serialName + ".nextval from dual");
            while (rs.next())
                rtn = rs.getString(0);
            dc.rollback();
        } catch (Exception e) {
            System.out.println("得到序列号失败:"+e);
        } finally {
            if (dc != null)
                dc.close();
        }
        return rtn;
    }

    /**
     * 获得下一个流水号
     * 失败时返回null
     * @param serialName
     * @return
     */
    public static String getCurrSerialNo(String serialName) {
        DatabaseConnection dc = null;
        String rtn = null;
        try {
            dc = ConnectionManager.getInstance().getConnection();
            RecordSet rs = dc.executeQuery("select " + serialName + ".currval from dual");
            while(rs.next())
                rtn = rs.getString(0);
        } catch (Exception e) {
            System.out.println("得到序列号失败:"+e);
        } finally {
            if (dc != null)
                dc.close();
        }
        return rtn;
    }

    /**
     * 生成一个序列号生成器
     * @param serialName
     * @return
     */
    public static int createSerial(String serialName) {
        DatabaseConnection dc = null;
        int rtn = -1;
        try {
            dc = ConnectionManager.getInstance().getConnection();
            rtn = dc.executeUpdate(serialName);
        } catch (Exception e) {
            System.out.println("生成序列失败:"+e);
        } finally {
            if (dc != null)
                dc.close();
        }
        return rtn;
    }

    /**
     * 删除一个序列号生成器
     * @param serialName
     * @return
     */
    public static int removeSerial(String serialName) {
        DatabaseConnection dc = null;
        int rtn = -1;
        try {
            dc = ConnectionManager.getInstance().getConnection();
            rtn = dc.executeUpdate(serialName);
        } catch (Exception e) {
            System.out.println("删除序列失败:"+e);
        } finally {
            if (dc != null)
                dc.close();
        }
        return rtn;
    }

  public static void main(String[] args) {
      //System.out.println(SerialNumber.createSerial("create sequence aa minvalue 1 maxvalue 9999 start with 1 increment by 1 cache 15 cycle order"));
      //System.out.println(SerialNumber.getNextSerialNo("aa"));
      //System.out.println(SerialNumber.getCurrSerialNo("aa"));
      //System.out.println(SerialNumber.removeSerial("drop sequence aa"));
      System.out.println(getNextSerialNo("usercredentialseq"));
  }
}
