package pub.platform.utils;

import java.util.*;

public class BusinessDate {
     private static String toDay = "2004-3-11";
     public BusinessDate() {
     }

     static {
          toDay = (new Date()).toString();
     }

     public static String getNowDay() {
          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
          Calendar cl = new GregorianCalendar();
          try {
//               return sdf.format(CMCodeManager.getDate("KEY_D1_1_1")) ;
               return sdf.format(cl.getTime());
          } catch(Exception ex) {
               return null;
          }

     }

     public static String getToday() {
          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
          Calendar cl = new GregorianCalendar();
          try {
//               return sdf.format(CMCodeManager.getDate("KEY_D1_1_1")) ;
               return sdf.format(cl.getTime());
          } catch(Exception ex) {
               return null;
          }
     }

     public static String getTodaytime() {
          Calendar cl = new GregorianCalendar();
          return getToday() + " " + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) + ":" + cl.get(Calendar.SECOND) + " ";
     }

     public static String getTodaytime(String bussDate) {
          Calendar cl = new GregorianCalendar();
          return bussDate + " " + cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) + ":" + cl.get(Calendar.SECOND) + " ";
     }

     public static String getTime() {
          Calendar cl = new GregorianCalendar();
          return cl.get(Calendar.HOUR_OF_DAY) + ":" + cl.get(Calendar.MINUTE) + ":" + cl.get(Calendar.SECOND) + " ";
     }

     public static int gethour() {
          Calendar cl = new GregorianCalendar();
          return cl.get(Calendar.HOUR_OF_DAY);
     }

     public static String getNoFormatToday() {
          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyyMMdd");
          Calendar cl = new GregorianCalendar();
          try {
//               return sdf.format(CMCodeManager.getDate("KEY_D1_1_1")) ;
               return sdf.format(cl.getTime());
          } catch(Exception ex) {
               return null;
          }
     }

     public static String getNoFormatTime() {
          java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("HHmmss");
          Calendar cl = new GregorianCalendar();
          try {

                return sdf.format(cl.getTime());
         } catch (Exception ex) {
              return null ;
         }


          //Calendar cl = new GregorianCalendar();
          //return cl.get(Calendar.HOUR_OF_DAY)+""+cl.get(Calendar.MINUTE)+""+cl.get(Calendar.SECOND)+"";

     }

     public static String getYear() {
//          return CMCodeManager.getString("KEY_D1_1_1").substring(0,4);
          return BusinessDate.getNowDay().substring(0, 4);
     }

     public static String getMonth() {
          return BusinessDate.getNowDay().substring(5, 7);
     }

     public static String getDay() {
          return BusinessDate.getNowDay().substring(8, 10);
     }

     public static String getYesterday()
          throws Exception {
          String strYesterday = "";
          Calendar cale = null;
          try {
//               cale = CMCodeManager.getCalendar("KEY_D1_1_1");
               cale = new GregorianCalendar();
          } catch(Exception ex) {
               throw new Exception(ex.getMessage());
          }
          cale.add(Calendar.DATE, -1);
          strYesterday = BusinessDate.getStrByCalendar(cale);
          return strYesterday;
     }

     public static String getStrByCalendar(Calendar cale) {
          return(new java.text.SimpleDateFormat("yyyy-MM-dd")).format(cale.getTime());
     }

     public static String[] getDateBound()
          throws Exception {

          String[] retarray = new String[2];
          String datetime = CMCodeManager.getString("KEY_dt_1_1");
          String[] array = datetime.split(":");
          Calendar caletoday = null;
          Calendar calenow = null;
          Calendar caleyest = null;
          Calendar caletomm = null;

          try {
               calenow = new GregorianCalendar();
               caletoday = (Calendar) calenow.clone();
          } catch(Exception ex) {
               throw new Exception(ex.getMessage());
          }
          try {

               caletoday.set(Integer.parseInt(BusinessDate.getStrByCalendar(calenow).substring(0, 4)),
                             Integer.parseInt(BusinessDate.getStrByCalendar(calenow).substring(5, 7)) - 1,
                             Integer.parseInt(BusinessDate.getStrByCalendar(calenow).substring(8, 10)),
                             Integer.parseInt(array[0]),
                             Integer.parseInt(array[1]), 0);
          } catch(Exception e) {
               e.printStackTrace();
          }

          caleyest = (Calendar)caletoday.clone();

          caletomm = (Calendar)caletoday.clone();

          caleyest.add(Calendar.DATE, -1);
          caletomm.add(Calendar.DATE, 1);

          if(calenow.before(caletoday) && calenow.after(caleyest)) {
               retarray[0] =  BusinessDate.getStrByCalendar(caleyest) + " " + CMCodeManager.getString("KEY_dt_1_1")+":00";
               retarray[1] =  BusinessDate.getStrByCalendar(caletoday) + " " + CMCodeManager.getString("KEY_dt_1_1")+":00";
          } else if(calenow.after(caletoday) && calenow.before(caletomm)) {

               retarray[0] =  BusinessDate.getStrByCalendar(caletoday) + " " + CMCodeManager.getString("KEY_dt_1_1")+":00";
               retarray[1] =  BusinessDate.getStrByCalendar(caletomm) + " " + CMCodeManager.getString("KEY_dt_1_1")+":00";
          }


          return retarray;
     }

     public static void main(String[] args) {
          try {
               String[] stra = BusinessDate.getDateBound();
               System.out.println("array[0]:"+stra[0]+"\narray[1]:"+stra[1]);
          } catch(Exception ex) {
               ex.printStackTrace();
          }
     }
     public static String getChnDateString(String sDate) {
          if ( sDate == null )
               return null;
          sDate = sDate.trim();
          if ( sDate.length() == 7 ) {
               sDate += "-01";
          }
          StringTokenizer st = new StringTokenizer(sDate,"-");
          int year=2100;
          int month=0;
          int day=1;
          try {
               year  = Integer.parseInt(st.nextToken());
               month = Integer.parseInt(st.nextToken())-1;
               day   = Integer.parseInt(st.nextToken());
          } catch ( Exception e ) {
               return "";
          }
          Calendar cl = new GregorianCalendar(year,month,day);
          return cl.get(Calendar.YEAR)+"年"+cl.get(Calendar.MONTH)+"月"+cl.get(Calendar.DATE)+"日";
     }
     public static String getChnDateString1(String sDate) {
          if ( sDate == null )
               return null;
          sDate = sDate.trim();
          if ( sDate.length() == 7 ) {
               sDate += "-01";
          }
          StringTokenizer st = new StringTokenizer(sDate,"-");
          int year=2100;
          int month=0;
          int day=1;
          try {
               year  = Integer.parseInt(st.nextToken());
               month = Integer.parseInt(st.nextToken())-1;
               day   = Integer.parseInt(st.nextToken());
          } catch ( Exception e ) {
               return "";
          }
          Calendar cl = new GregorianCalendar(year,month,day);
          return cl.get(Calendar.YEAR)+"年"+(cl.get(Calendar.MONTH)+1)+"月"+cl.get(Calendar.DATE)+"日";
     }


}
