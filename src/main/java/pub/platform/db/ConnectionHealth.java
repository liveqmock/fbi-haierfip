package pub.platform.db;

import java.util.*;

public class ConnectionHealth extends Thread {
     public static List list = Collections.synchronizedList(new ArrayList());

     public synchronized static void begin(DatabaseConnection dc,String sql) {
//          try {
//               dc.sqlBgnTime = System.currentTimeMillis();
//               dc.currSql = sql;
//               synchronized (ConnectionHealth.list) {
//                    ConnectionHealth.list.add(dc);
//               }
//          } catch ( Throwable e ) {
//               e.printStackTrace();
//          }
     }
     public  static void end(DatabaseConnection dc) {
//          try {
//               synchronized (ConnectionHealth.list) {
//                   ConnectionHealth.list.remove(dc);
//               }
//          } catch ( Throwable e ) {
//               e.printStackTrace();
//          }
     }

     public void run() {
          System.out.println(System.currentTimeMillis()+" ConnectionHealth Thread Start!");
          while ( true ) {
               try {
                    Thread.sleep(60000); //–›œ¢60√Î
               } catch ( Exception e ) {

               }
//               try {
//                    for(int i = 0; i < ConnectionHealth.list.size(); i++) {
//                         DatabaseConnection dc = (DatabaseConnection)ConnectionHealth.list.get(i);
//                         if((System.currentTimeMillis() - dc.sqlBgnTime) / 1000 > 20) {//≥¨π˝20√Î
//                              try {
//                                   dc.rollback();
//                              } catch ( Throwable e ) {
//
//                              }
//                              try {
//                                   dc.close();
//                              } catch ( Throwable e ) {
//
//                              }
//                              try {
//                                   end(dc);
//                              } catch ( Throwable e ) {
//
//                              }
//                              System.out.println("overtime sql:" + dc.currSql);
//                         }
//                    }
//                    Thread.sleep(1000);//–›œ¢1√Î
//               } catch ( Throwable e ) {
//
//               }
          }
     }

}
