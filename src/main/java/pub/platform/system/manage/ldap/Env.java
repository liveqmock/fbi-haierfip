package pub.platform.system.manage.ldap;

public interface Env {

          /*
      * Initial context implementation to use.
      */
     public static String INITCTX = "com.sun.jndi.ldap.LdapCtxFactory";

     /*
      * Host name and port number of LDAP server
      */
     //public static String MY_SERVICE = "ldap://192.9.100.205:389";
     public static String MY_SERVICE = "ldap://192.9.100.32:389";
     /*
      * DN of directory manager entry.  This entry should have write access to
      * the entire directory.
      */
     //public static String MGR_DN = "cn=orcladmin,cn=Users,dc=sohipc,dc=com";

     //public static String MGR_DN = "cn=administrator,cn=users,dc=sohidomain,dc=sohipc,dc=com";
     public static String MGR_DN = "administrator@zhongtian.com";
     /*
      * Password for manager DN.
      */
     //public static String MGR_PW = "manager0";
     public static String MGR_PW = "123456";

     /*
      * Subtree to search
      */
     //public static String MY_SEARCHBASE = "cn=Users,dc=sohipc,dc=com";
     public static String MY_SEARCHBASE = "DC=zhongtian,DC=com";

     /*
      * Subtree to modify
      */
     public static String MY_MODBASE = "*";

     /*
      * Filter to use when searching.  This one searches for all entries with the
      * surname (last name) of "Jensen".
      */
     public static String MY_FILTER = "cn=yujjcreate";

     /*
      * Entry to retrieve
      */
     public static String ENTRYDN = "cn=orcladmin,cn=Users,dc=sohipc,dc=com";







}
