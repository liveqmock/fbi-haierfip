package pub.platform.security;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Comparator;

import pub.platform.utils.BusinessDate;

/**
 * <p>Title: Operator.java</p>
 * <p>Description: This class is used to represent the Operator object and its properties.</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author WangHaiLei
 * @version 1.4
 * $ UpdateDate: Y-M-D-H-M:
 *               2003-12-02-09-50
 * $
 */
public class Operator implements Serializable,Comparator  {

     /**
      * The following are the properties.
      */
     private String operatorId;
     private String operatorName;
     private String sex;
     private String email;
     private String operatorEnabled;
     private String departmentId;
     private String isSuper;
     private String operatorType;
     private String mobilePhone;
     private String operatorPhone;
     private String otherPhone;
     private String combinedSequence;
     private String isCombined;
     private String remoteAddr;
     private String remoteHost;
     private String loginTime = BusinessDate.getTodaytime();

     /**
      * 操作员数据库代表，数据库操作都在其中进行。
      */
     private transient DatabaseAgent database;

     /**
      * Default Constructors.
      */
     public Operator() {
     }

     /**
      * This constructor is good to use for initialization.
      * @param username
      */
     public Operator(String operatorId) {
          super();
          this.operatorId = operatorId;
          init(operatorId);
     }

     /**
      * Getter methods
      */

     public String getOperatorId() {
          return operatorId;
     }

     public String getOperatorName() {
          return operatorName;
     }

     public String getSex() {
          return sex;
     }

     public String getEmail() {
          return email;
     }

     public String getOperatorEnabled() {
          return operatorEnabled;
     }

     public String getDepartmentId() {
          return departmentId;
     }

     public String getIsSuper() {
          return isSuper;
     }

     public String getOperatorType() {
          return operatorType;
     }

     public String getMobilePhone() {
          return mobilePhone;
     }

     public String getOperatorPhone() {
          return operatorPhone;
     }

     public String getOtherPhone() {
          return otherPhone;
     }

     public String getCombinedSequence() {
          return combinedSequence;
     }

     public String getIsCombined() {
          return isCombined;
     }

     /**
      * 初始化，将属于该操作员的所有基本信息取出。
      * @param username
      * @roseuid 3F80BB050281
      */
     private void init(String operatorId) {
          try {
               database = new DatabaseAgent();
               Map basic = database.getBasicOfOperator(operatorId);

               this.operatorName = (String)basic.get("opnm");
               this.operatorId   = (String)basic.get("opid");
               sex               = (String)basic.get("sexo");
               email             = (String)basic.get("emai");
               operatorEnabled   = (String)basic.get("enab");
               isSuper           = (String)basic.get("issu");
               departmentId      = (String)basic.get("depi");
               operatorType      = (String)basic.get("otyp");
               mobilePhone       = (String)basic.get("mobp");
               operatorPhone     = (String)basic.get("opep");
               otherPhone        = (String)basic.get("othp");
               combinedSequence  = (String)basic.get("comb");
               isCombined        = (String)basic.get("isco");

          } catch(Exception ex3) {
               System.out.println("Wrong, when initializing User: ["+ex3+"]. Place: zt.platform.security.Operator. "+ex3.getStackTrace());
          }
     }
     public String getLoginTime() {
          return loginTime;
     }
     public void setLoginTime(String loginTime) {
          this.loginTime = loginTime;
     }
     public String getRemoteHost() {
          return remoteHost;
     }
     public String getRemoteAddr() {
          return remoteAddr;
     }
     public void setRemoteHost(String remoteHost) {
          this.remoteHost = remoteHost;
     }
     public void setRemoteAddr(String remoteAddr) {
          this.remoteAddr = remoteAddr;
     }
     public int compare(Object obj1,Object obj2) {
          if ( obj1 == null && obj2 == null )
               return 0;
          if ( obj1 == null ) {
               return -1;
          }
          if ( obj2 == null ) {
               return 1;
          }

          if ( obj1 instanceof Operator && obj2 instanceof Operator ) {
               byte[] bt1 = ((Operator)obj1).getOperatorId().getBytes();
               byte[] bt2 = ((Operator)obj2).getOperatorId().getBytes();
               if ( bt1 == null && bt2 == null )
                    return 0;
               if ( bt1 == null )
                    return -1;
               if ( bt2 == null )
                    return 1;
               int i = 0;
               for (  ; i < bt1.length && i < bt2.length ; i++ ) {
                    if ( bt1[i] < bt2[i] ) {
                         return -1;
                    }
                    if ( bt1[i] > bt2[i] ) {
                         return 1;
                    }
               }
               if ( i == bt1.length ) {
                    return -1;
               }
               if ( i == bt2.length ) {
                    return 1;
               }
          }

          return 0;
     }
}
