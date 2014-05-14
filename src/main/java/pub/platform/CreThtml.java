package pub.platform;


import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import pub.platform.db.ConnectionManager;
import pub.platform.db.DatabaseConnection;


public class CreThtml {
     private int i_cols_num;
     private String s_seqnos;
     private String s_invisibles;
     private String s_disableds;
     private String s_tablename;
     private String s_objname;
     private String s_filename;
     public CreThtml() {
     }

     public int getI_cols_num() {
          return i_cols_num;
     }

     public void setI_cols_num(int i_cols_num) {
          this.i_cols_num = i_cols_num;
     }

     public String getS_seqnos() {
          return s_seqnos;
     }

     public void setS_seqnos(String s_seqnos) {
          this.s_seqnos = s_seqnos;
     }

     public String getS_invisibles() {
          return s_invisibles;
     }

     public void setS_invisibles(String s_invisibles) {
          this.s_invisibles = s_invisibles;
     }

     public String getS_disableds() {
          return s_disableds;
     }

     public void setS_disableds(String s_disableds) {
          this.s_disableds = s_disableds;
     }

     public String getS_tablename() {
          return s_tablename;
     }

     public void setS_tablename(String s_tablename) {
          this.s_tablename = s_tablename;
     }

     public String checkinput() {
          String strBack = "";
          if(i_cols_num < 1) {
               i_cols_num = 1;
          }
          if(s_tablename == null) {
               strBack = "表名必须输入！";
          }
          return strBack;
     }

     public String toString()  {
          String strBack = "";
          strBack += "<%@ page contentType=\"text/html; charset=GBK\" %>";
          strBack += "<html><head>";
          strBack += "<link href=\"/css/catv.css\" type=\"text/css\" rel=\"stylesheet\">";
          strBack += "<script language=\"javascript\" src=\"/js/basic.js\"></script>       "+
               "<script language=\"JavaScript\" src=\"/js/Calendar.js\"></script>    " +
               "<script language=\"JavaScript\" src=\"/js/compack.js\"></script>     " +
               "<script language=\"javascript\" src=\"/js/xmlHttp.js\"></script>     " +
               "<script language=\"javascript\" src=\"/js/getLoad.js\"></script>     " +
               "<script language=\"javascript\" src=\"/js/month.js\"></script>       " +
               "<script language=\"javascript\" src=\"/js/dbgrid.js\"></script>      " +
               "<script language=\"javascript\" src=\"/js/dbutil.js\"></script>      " +
               "<script language=\"javascript\" src=\"/js/dropdownData.js\"></script>" +
               "<script language=\"javascript\" src=\"/js/common.js\"></script>      ";
          strBack += "</head>";
          strBack += "<body bgcolor=\"#d4d0c8\" onClick=\"body_Click();\" onload=\"body_init(winform);\" oncontextmenu=\"event.returnValue=false;\">";

          strBack += "<table>";
          String str = checkinput().trim();
          if(!str.equals("")) {
               return str;
          }
          DatabaseConnection dconn = ConnectionManager.getInstance().get();
          Connection conn = dconn.getConnection();


          try {
               PreparedStatement pstat = conn.prepareStatement("select a.colno,lower(a.cname) cname,b.COMMENTS,"
                                                 + "lower(a.coltype) coltype,a.width from sys.col a,all_col_comments b"
                                                 + " where a.cname=b.COLUMN_NAME and tname=b.table_name "
                                                 + "and owner='LHNJ' and b.table_name=? order by a.colno");

               pstat.setString(1,this.s_tablename.toUpperCase());
               ResultSet rs = pstat.executeQuery();
//               RecordSet rs = dconn.executeQuery("select a.colno,lower(a.cname) cname,b.COMMENTS,"
//                                                 + "lower(a.coltype) coltype,a.width from sys.col a,all_col_comments b"
//                                                 + " where a.cname=b.COLUMN_NAME and tname=b.table_name "
//                                                 + "and owner='CATV' and b.table_name='"+this.s_tablename+"' order by a.colno");

               int i_col_index = 0;
               while(rs.next()) {
                    if(i_col_index==0){
                         strBack +="<tr>";
                    }
                    strBack += "<td>";
                    strBack += rs.getString("COMMENTS");
                    strBack += "</td>";
                    strBack += "<td>";
                    strBack += "<input type=\"text\" id=\""+rs.getString(2)+"\" name=\""+rs.getString(2)+"\" "
                          + ((rs.getString("COLTYPE").equals("number"))?" onkeypress=\"onKeyPressInputInteger(this);\" ":"")
                          + " maxlength=\""+rs.getString("WIDTH")+"\" fieldtype=\"text\" fieldname=\""+rs.getString(2)+"\""+((s_objname==null)?"":" value=\"<%=String.valueOf("+s_objname.trim()+".get"
                          +rs.getString(2).substring(0,1).toUpperCase()
                          +rs.getString(2).substring(1,rs.getString(2).length()).toLowerCase()+"())%>\"")+">";
                    strBack += "</td>";
                    i_col_index++;
                    if(i_col_index==i_cols_num){
                         strBack +="</tr>";
                         i_col_index = 0;
                    }
               }
               strBack +="</tr>";
          } catch(Exception e) {
               e.printStackTrace();
          } finally {
              
          }

          strBack += "</table>";
          strBack += "</body>";
          strBack += "</html>";
          return strBack;
     }
     public void executeUpdate() throws SQLException{
          DatabaseConnection dconn =ConnectionManager.getInstance().get();;
          Connection conn = dconn.getConnection();

     }
     public static void generateJsp(int table_cols_num,String table_name,String tableObjName,String Jspfilename) {
          System.out.println("=====正在产生文件【"+Jspfilename+"】=====");
          CreThtml html = new CreThtml();
          html.setI_cols_num(table_cols_num);
          html.setS_tablename(table_name);
          html.setS_objname(tableObjName);
          try {
               FileOutputStream fos = new FileOutputStream(Jspfilename);
               fos.write(html.toString().getBytes());
          } catch ( Exception e ) {
               e.printStackTrace();
               System.out.println("*****产生文件【"+Jspfilename+"】失败！*****");
               return;
          }
          System.out.println("=====产生文件【"+Jspfilename+"】成功 =====");
     }
     public static void main(String[] args){
//          CreThtml html = new CreThtml();
//          html.setI_cols_num(3);
//          html.setS_disableds("");
//          html.setS_invisibles("");
//          html.setS_seqnos("1,2,3,4,5,6,7,8,9,10");
//          html.setS_tablename("PRJTASKINFO");
//          html.setS_objname("prjtaskinfo");
//generateJsp(3,"PRJTASKINFO","prjtaskinfo","prjtaskinfo.jsp");
//          generateJsp(3,"testtbl","testtbl","defaultroot/testtbl.jsp");
          generateJsp(2,"cp_djqy","ptmenubean","defaultroot/mc_qymc_query.jsp");
     }
     public String getS_objname() {
          return s_objname;
     }
     public void setS_objname(String s_objname) {
          this.s_objname = s_objname;
     }
     public String getS_filename() {
          return s_filename;
     }
     public void setS_filename(String s_filename) {
          this.s_filename = s_filename;
     }
}
