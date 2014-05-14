package pub;

public class RestartServer {
     public static boolean start() {
          try {
               Process proc = Runtime.getRuntime().exec("sh /bea/user_projects/domains/mydomain/restartSh.sh &");

               return true;
          } catch ( Exception e ) {
               return false;
          }
     }
}
