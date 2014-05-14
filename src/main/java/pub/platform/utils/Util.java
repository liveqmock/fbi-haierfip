package pub.platform.utils;


import org.jdom.Element;
import pub.platform.db.ConnectionManager;
import pub.platform.db.DBUtil;
import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

public class Util
{
    public Util()
    {
    }

    public static String operID="";
    //取出某表某字段的最大值
    public static String  getFieldMax(DatabaseConnection dc,String Keyfield,String tableName,String whereStr){

       String maxcount ="1";
       try{
            String SQLStr = "select max(to_number(" + Keyfield + ")) as maxcount from  " + tableName + " " +whereStr ;
            System.out.println("SQLStr="+SQLStr);
            RecordSet rs = dc.executeQuery(SQLStr);
            if(rs.next())
                 maxcount = rs.getString(0);

            if(maxcount == null )
                 maxcount = "1";
            else
            	maxcount = "" + (Integer.parseInt(maxcount) +1);
       }catch(Exception e){
           // e.printStackTrace();
       }

       return maxcount;


    }
    //取出某表某字段的最大值
    public static String  getFieldMax(DatabaseConnection dc,String Keyfield,String tableName){

        return getFieldMax(dc,Keyfield,tableName,"");
    }
    
    public static String getUserSerial(String operID){
    	
    	ConnectionManager cm  = ConnectionManager.getInstance();
        DatabaseConnection dc = cm.get();
    	String serial ="1";
         try{
              String SQLStr = "select FILLINT6 as serial from PTOPER  where OPERID='"+operID+"'";

              RecordSet rs = dc.executeQuery(SQLStr);
              if(rs.next())
              	serial = rs.getString(0);

              if(serial == null)
              	serial = "null";
         }catch(Exception e){
             // e.printStackTrace();
         }
        // cm.release();
    	return serial;
    }
    
    
    public static String getUserSerial(DatabaseConnection dc,String operID){
    	
    	 String serial ="1";
         try{
              String SQLStr = "select FILLINT6 as serial from PTOPER  where OPERID='"+operID+"'";

              RecordSet rs = dc.executeQuery(SQLStr);
              if(rs.next())
              	serial = rs.getString(0);

              if(serial == null)
              	serial = "null";
         }catch(Exception e){
             // e.printStackTrace();
         }

    	return serial;
    }
    
    public static String getUserName(String operID){
    	 ConnectionManager cm  = ConnectionManager.getInstance();
         DatabaseConnection dc = cm.get();
         
    	 String username ="";
         try{
              String SQLStr = "select OPERNAME as username from PTOPER  where OPERID='"+operID+"'";

              RecordSet rs = dc.executeQuery(SQLStr);
              if(rs.next())
              	username = rs.getString(0);

              if(username == null)
              	username = "";
         }catch(Exception e){
             // e.printStackTrace();
         }
        // cm.release();
         
    	return username;
    }
    
    public static String getUserName(DatabaseConnection dc,String operID){
    	
    	 String username ="";
         try{
              String SQLStr = "select OPERNAME as username from PTOPER  where OPERID='"+operID+"'";

              RecordSet rs = dc.executeQuery(SQLStr);
              if(rs.next())
              	username = rs.getString(0);

              if(username == null)
              	username = "";
         }catch(Exception e){
             // e.printStackTrace();
         }

    	return username;
    }
    
    public static String getUserNamefromSerial(String SerialID){
    	 ConnectionManager cm  = ConnectionManager.getInstance();
         DatabaseConnection dc = cm.get();
         
    	 String username ="";
         try{
              String SQLStr = "select OPERNAME as username from PTOPER  where FILLINT6="+SerialID;

              RecordSet rs = dc.executeQuery(SQLStr);
              if(rs.next())
              	username = rs.getString(0);

              if(username == null)
              	username = "";
         }catch(Exception e){
             // e.printStackTrace();
         }
         //cm.release();
    	return username;
    }
    
    public static String getUserNamefromSerial(DatabaseConnection dc,String SerialID){
    	
    	 String username ="";
         try{
              String SQLStr = "select OPERNAME as username from PTOPER  where FILLINT6="+SerialID;

              RecordSet rs = dc.executeQuery(SQLStr);
              if(rs.next())
              	username = rs.getString(0);

              if(username == null)
              	username = "";
         }catch(Exception e){
             // e.printStackTrace();
         }

    	return username;
    }
    // 生成添加语句
    public String createInsertSQL(String fieldStr,Element insertElement,String tablename){
        String sqlstr = "insert into  "+tablename +"(";
        String fieldname ="";
        String fieldvalue ="";

        List listchild = insertElement.getChildren();

        for (int j=0; j< listchild.size();j++){
            Element childnode = (Element)listchild.get(j);

            if (!fieldname.equals(""))
                fieldname +="," ;
            if (!fieldvalue.equals(""))
                fieldvalue +="," ;


            if (childnode.getAttributeValue("type").equals("text")){
                fieldname += childnode.getAttributeValue("name");
                fieldvalue+="'" + childnode.getAttributeValue("value")+"'";
            }

            if (childnode.getAttributeValue("type").equals("date")){
                fieldname += childnode.getAttributeValue("name");
                fieldvalue+=DBUtil.formatDateTime(childnode.getAttributeValue("value"));
            }

            if (childnode.getAttributeValue("type").equals("datetime")){
                fieldname += childnode.getAttributeValue("name");
                Calendar rightNow = Calendar.getInstance();
                String timeStr = rightNow.get(rightNow.HOUR_OF_DAY)+":"+rightNow.get(rightNow.MINUTE)+":"+rightNow.get(rightNow.SECOND);

                fieldvalue += DBUtil.formatDateTime(childnode.getAttributeValue("value")+" "+timeStr);
            }
            if ((childnode.getAttributeValue("type").equals("int"))||(childnode.getAttributeValue("type").equals("checkbox"))){
                fieldname  += childnode.getAttributeValue("name");
                fieldvalue += childnode.getAttributeValue("value");
            }

        }

        String[] recoderS = fieldStr.split(";");

        for (int i=0; i< recoderS.length;i++){
            if (!fieldname.equals(""))
                fieldname +="," ;
            if (!fieldvalue.equals(""))
                fieldvalue +="," ;

            String[] fields = recoderS[i].split("&");

            if (fields[2].equals("text")){
                fieldname += fields[0];
                fieldvalue+="'" + fields[1]+"'";
            }
            if ((fields[2].equals("date"))||(fields[2].equals("datetime"))){
                fieldname += fields[0];
                fieldvalue+=DBUtil.formatDateTime(fields[1]);
            }

            if (fields[2].equals("int")){
                fieldname += fields[0];
                fieldvalue+= fields[1];
            }

        }
        sqlstr += fieldname +" ) " + "values( "+ fieldvalue+" )";
        System.out.print(sqlstr);
        return sqlstr;

    }

    ///生成更新语句

    public String createupdateSQL(String fieldStr,Element updateElement,String tablename,String whereStr){
       String sqlstr = "update  "+tablename +"  set  ";
       String updatevalue ="";

       System.out.print(sqlstr);

       List listchild = updateElement.getChildren();

       for (int j=0; j< listchild.size()-1;j++){
           Element childnode = (Element)listchild.get(j);

           if (!updatevalue.equals(""))
               updatevalue +="," ;


           if (childnode.getAttributeValue("type").equals("text")){
               updatevalue += childnode.getAttributeValue("name")+" = '" + childnode.getAttributeValue("value")+"'";
            }

            if (childnode.getAttributeValue("type").equals("date")){
                updatevalue += childnode.getAttributeValue("name")+ "=" + DBUtil.formatDateTime(childnode.getAttributeValue("value"));
            }

            if (childnode.getAttributeValue("type").equals("datetime")){
                Calendar rightNow = Calendar.getInstance();
                String timeStr = rightNow.get(rightNow.HOUR_OF_DAY)+":"+rightNow.get(rightNow.MINUTE)+":"+rightNow.get(rightNow.SECOND);

                updatevalue +=childnode.getAttributeValue("name")+"="+DBUtil.formatDateTime(childnode.getAttributeValue("value")+" "+timeStr);
            }
            if ((childnode.getAttributeValue("type").equals("int"))||(childnode.getAttributeValue("type").equals("checkbox"))){
                   updatevalue += childnode.getAttributeValue("name")+" = " + childnode.getAttributeValue("value")+"";

            }

       }

       if (!fieldStr.equals("")){
           String[] recoderS = fieldStr.split(";");

           for (int i=0; i< recoderS.length;i++){
               if (!updatevalue.equals(""))
                   updatevalue +="," ;

               String[] fields = recoderS[i].split("&");

               if (fields[2].equals("text")){
                   updatevalue += fields[0] + "='" + fields[1]+"'";
               }
               if ((fields[2].equals("date"))||(fields[2].equals("datetime"))){
                   updatevalue += fields[0] + "=" + DBUtil.formatDateTime(fields[1]);
               }

               if (fields[2].equals("int")){
                   updatevalue += fields[0] + "=" + fields[1]+"";
               }
           }
       }

       Element childRoot = (Element) listchild.get(listchild.size()-1);

       //添加更新条件
       sqlstr += updatevalue +" where (1=1)";

       if (!childRoot.getText().equals("no"))
       {
           sqlstr += childRoot.getText();
       }
       sqlstr += whereStr;

       return sqlstr;

   }


    ///生成删除语句
    public String createdeleteSQL(Element deleteElement,String tablename,String whereStr){
        String sqlstr = "delete  from   " + tablename +" where (1=1) ";

        List listchild = deleteElement.getChildren();
        Element childnode = (Element)listchild.get(0);


        if (childnode.getText().length() > 0)
            sqlstr +=  childnode.getText();


       sqlstr +=  whereStr;
       System.out.print(sqlstr);
       return sqlstr;
    }

    //生成查询语句
    public String createSelectSQl(Element selectElement,String tablename,String whereStr){
        String sqlstr = "select  ";
        String selectValue ="";

        List listchild = selectElement.getChildren();

        for (int j=0; j< listchild.size()-1;j++){
            Element childnode = (Element)listchild.get(j);

            if (!selectValue.equals(""))
                selectValue +="," ;

                selectValue += childnode.getAttributeValue("name");

         }

         sqlstr += selectValue + "  from   " + tablename +"  where (1=1)";

         Element childRoot = (Element) listchild.get(listchild.size()-1);


         if (!childRoot.getText().equals("no"))
               sqlstr += childRoot.getText();

         sqlstr += whereStr;

         return sqlstr;

     }


     public String createSelectValue(Element selectElement,RecordSet rs){
         String SelectValue = "";

         SelectValue ="<root success=\"true\" retValue=\"true\">";
         while (rs.next())
         {
             SelectValue +="<recoder>";


              List listchild = selectElement.getChildren();
              for (int j=0; j< listchild.size()-1;j++){

                  Element childnode = (Element)listchild.get(j);

                  if (childnode.getAttributeValue("type")=="datetime")
                      SelectValue +="<field name=\""+childnode.getAttributeValue("name")+"\" value=\""+rs.getCalendar(j)+"\" type=\""+childnode.getAttributeValue("type")+"\"/>";
                  else
                      SelectValue +="<field name=\""+childnode.getAttributeValue("name")+"\" value=\""+rs.getString(j)+"\" type=\""+childnode.getAttributeValue("type")+"\"/>";

              }

               SelectValue +="</recoder>";
         }
          SelectValue +="</root>";


         return SelectValue;
     }


     public String retValue(String retBol,String Value){

         String retStr ="<root success=\""+retBol+"\"  retValue=\"false\">"+Value+"</root>";
         return retStr;

     }

     public static boolean isOperHasMenu(String menuID,String operID){
         ConnectionManager cm  = ConnectionManager.getInstance();
         DatabaseConnection dc = cm.get();
         
    	 boolean ishas =false;
         try{
              String SQLStr = "select PTOPERRole.roleid from PTOPERRole,ptroleres  where (PTOPERRole.roleid=ptroleres.roleid)and (ptroleres.resid='m"+menuID+"')and PTOPERRole.OPERID='"+operID+"'";

              RecordSet rs = dc.executeQuery(SQLStr);
              if(rs.next())
            	  ishas = true;

            
         }catch(Exception e){
        	 return ishas;
         }
         return ishas;
     }
     //////取出重音字
     public String getSyhz(){
          String PolyChWord ="<root>";

           String SQLStr = "select PolyChWord maxcount from PtWord";

           ConnectionManager cms = ConnectionManager.getInstance();
           DatabaseConnection dc = cms.getConnection();

           RecordSet rs = dc.executeQuery(SQLStr);

           while (rs.next()){
             PolyChWord +="<field>"+ rs.getString(0)+"</field>";

          }

           PolyChWord +="</root>";

          return PolyChWord;

    }
    public static Calendar getDate(String sDate) {
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
               return null;
          }
          return new GregorianCalendar(year,month,day);
     }
     public static String getDateString(Calendar sDate) {
          if ( sDate == null )
               return "";
          return (new java.text.SimpleDateFormat("yyyy-MM-dd")).format(sDate.getTime());
     }
     public static String getYearMonth(Calendar sDate) {
          if ( sDate == null )
               return "";
          return (new java.text.SimpleDateFormat("yyyy-MM")).format(sDate.getTime());
     }

     public static String getMoneyString(double money) {

          NumberFormat  nf =NumberFormat.getInstance();
          ((DecimalFormat) nf).applyPattern("#,000.00");
          String moneyStr=nf.format(money);
          return ""+moneyStr;
     }









     public static String getDateString(String date) {
          if ( date == null )
               return date;

          String[] dates = date.split("-");
          if ( dates.length >= 2 ) {
               return dates[0] + "年" + dates[1] + "月";
          } else {
               return date;
          }
     }
     public static String getChnDate(String date) {
          if ( date == null )
               return date;

          String[] dates = date.split("-");

          return dates[0]+"年"+dates[1]+"月"+dates[2]+"日";
     }
     public static String inttostr(String intStr){
    	 String returnStr ="";
    	 for (int i=0; i <intStr.length() ;i++ ){
    		 if ( String.valueOf(intStr.charAt(i)).equals("1"))
    			 returnStr +="a";
    		 if ( String.valueOf(intStr.charAt(i)).equals("2"))
    			 returnStr +="b";

    		 if ( String.valueOf(intStr.charAt(i)).equals("3"))
    			 returnStr +="c";
    		 if ( String.valueOf(intStr.charAt(i)).equals("4"))
    			 returnStr +="d";	
    		
    		 if ( String.valueOf(intStr.charAt(i)).equals("5"))
    			 returnStr +="e";
    		 if ( String.valueOf(intStr.charAt(i)).equals("6"))
    			 returnStr +="f";

    		 if ( String.valueOf(intStr.charAt(i)).equals("7"))
    			 returnStr +="g";
    		 if ( String.valueOf(intStr.charAt(i)).equals("8"))
    			 returnStr +="h";		 
    		 
    		 if ( String.valueOf(intStr.charAt(i)).equals("9"))
    			 returnStr +="i";
    		 if ( String.valueOf(intStr.charAt(i)).equals("0"))
    			 returnStr +="j";	
    		 
    		 
    		 
    	 }
    	 return returnStr;
    	 
     }
     
 public static String strtoint(String intStr){
    	 String returnStr ="";
    	 for (int i=0; i <intStr.length() ;i++ ){
    		 if ( String.valueOf(intStr.charAt(i)).equals("a"))
    			 returnStr +="1";
    		 if ( String.valueOf(intStr.charAt(i)).equals("b"))
    			 returnStr +="2";

    		 if ( String.valueOf(intStr.charAt(i)).equals("c"))
    			 returnStr +="3";
    		 if ( String.valueOf(intStr.charAt(i)).equals("d"))
    			 returnStr +="4";	
    		
    		 if ( String.valueOf(intStr.charAt(i)).equals("e"))
    			 returnStr +="5";
    		 if ( String.valueOf(intStr.charAt(i)).equals("f"))
    			 returnStr +="6";

    		 if ( String.valueOf(intStr.charAt(i)).equals("g"))
    			 returnStr +="7";
    		 if ( String.valueOf(intStr.charAt(i)).equals("h"))
    			 returnStr +="8";		 
    		 
    		 if ( String.valueOf(intStr.charAt(i)).equals("i"))
    			 returnStr +="9";
    		 if ( String.valueOf(intStr.charAt(i)).equals("j"))
    			 returnStr +="0";	
    		 
    		 
    		 
    	 }
    	 return returnStr;
    	 
     }

     public static void main(String[] argv) {
          System.out.println(getDateString("2004-10-20"));
     }
     
}
