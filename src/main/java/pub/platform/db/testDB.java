package pub.platform.db;
import java.sql.*;
import java.util.*;
import sun.misc.*;
/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author not attributable
 * @version 1.0
 */

public class testDB
{
    public testDB()
    {
    }

    public void testContCount(int count)
    {
        ConnectionManager[] cms =new ConnectionManager[count];
        DatabaseConnection[] dcs = new DatabaseConnection[count];
        for (int i=0 ;i<count;i++ ){
            cms[i] = ConnectionManager.getInstance();
            dcs[i] = cms[i].getConnection();
        }
        for (int i=0 ;i<count;i++ ){
            RecordSet rs = dcs[i].executeQuery("select * from test");
            while ( rs.next() ) {
                System.out.println(rs.getString("test"));
            }
            System.out.println(i);
            //cms[i].releaseConnection(dcs[i]);

        }
    }

    public void testcomit(){
        ConnectionManager cms = ConnectionManager.getInstance();
        DatabaseConnection dcs= cms.getConnection();
        dcs.begin();
        //dcs.executeUpdate("delete from test");
        //for (int i=0 ;i< 100;i++){
          //  dcs.executeUpdate("insert into test(test) values('test"+Integer.toString(i)+"')");
        //}
       // dcs.executeUpdate("update test set test='1111111' where test='2222222'");
        //dcs.executeUpdate("delete from testtype");

        dcs.executeUpdate("insert into testtype(chty,ch2ty,nchty,nch2ty,daty,lgty,flty,intty) values('222222','dssddsd','11111','333333',"+DBUtil.formatDateTime("2004-2-1")+",3333,44.55,6666)");
        dcs.commit();
        System.out.println("testcomit");


    }
    public void testroback(){
        ConnectionManager cms = ConnectionManager.getInstance();
        DatabaseConnection dcs= cms.getConnection();
        dcs.begin();
        dcs.executeUpdate("delete from test");
        dcs.executeUpdate("insert into test(test) values('11111')");
        dcs.executeUpdate("delete from testtype");
        dcs.executeUpdate("insert into testtype(chty,ch2ty) values('1111','1111')");
        dcs.rollback();
        System.out.println("rollback");


    }

    public void testparperStatement(){
        ConnectionManager cms = ConnectionManager.getInstance();
        DatabaseConnection dcs= cms.getConnection();
        /*PreparedStatement pstmt =dcs.getPreparedStatement("update  test  set test=? where test=?");

        try{

          pstmt.setString(1,"dddddd");
          pstmt.setString(2,"test0");
           pstmt.execute();
        System.out.println("testparperStatement");
        }catch(java.sql.SQLException e){
            System.out.print(e.getMessage());
        }
*/
    //  RecordSet rs= dcs.executeQuery("select userid,name,passwd,issuper,sex,usertype,email from PTUser where (1=1),1,10);
    RecordSet rs= dcs.executeQuery("Select userid,name,passwd,issuper,sex,usertype,email from PTUser where (1=1) ",2,3);
        while (rs.next()){
            System.out.print(rs.getString(0)+"\n");
        }

    }
    public void testgetRecord(){
        ConnectionManager cms = ConnectionManager.getInstance();
        DatabaseConnection dcs= cms.getConnection();

        RecordSet rs = dcs.executeQuery("select chty,ch2ty,nchty,nch2ty,daty,lgty,flty,intty from testtype");
            while ( rs.next() ) {
                System.out.println(rs.getString("chty")+";");
                System.out.println(rs.getString("ch2ty")+";");
                System.out.println(rs.getString("nchty")+";");
                System.out.println(rs.getString("nch2ty")+";");
                System.out.println(rs.getString("daty")+";");
                System.out.println(rs.getCalendar("daty")+";");
                System.out.println(rs.getString("lgty")+";");
                System.out.println(rs.getLong("lgty")+";");
                System.out.println(rs.getString("flty")+";");
                System.out.println(rs.getFloat("flty")+";");
                System.out.println(rs.getString("intty")+";");
                System.out.println(rs.getDouble("intty")+";");

                System.out.println("\n");

            }


    }

    public void testcal(){
        Calendar rightNow = Calendar.getInstance();
        System.out.print(rightNow.get(rightNow.HOUR_OF_DAY)+"\n");
         System.out.print(rightNow.get(rightNow.MINUTE)+"\n");
          System.out.print(rightNow.get(rightNow.SECOND)+"\n");


    }
    public static void main(String[] args)
    {
         String ttt="ÄãºÃ,>dsdfdffdfdfd2121223323223wewecfsfsdfd¹þ¹þ¹þ¹þ¡££¬325454£¨£©£©£¨£©£¨0-()())()()";
        BASE64Decoder decode1 = new BASE64Decoder();
        try
        {

              System.out.println(java.net.URLEncoder.encode(ttt,"ISO-8859-1"));
        }catch (Exception e){

        }


         //System.out.println(DBUtil.toDB(ttt,"GBK","ISO-8859-1"));

    }

}
