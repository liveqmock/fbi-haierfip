/****************************************************
 *
 *      文件名：  JavaBeanGenerator.java
 *
 *      作  用：  数据层类的生成
 *
 *      作  者：  王德良
 *
 *      时  间：  2004-3-14
 *
 *      版  权：  leonwoo
 *
 ****************************************************/

package pub.platform.utils;

import java.io.*;

import pub.platform.db.*;

public class JavaBeanGenerator {
     private FileOutputStream fos;

     public JavaBeanGenerator() {

     }

     private void generateHead(String packageStr) {
          try {
               fos.write(("package "+packageStr+";\n").getBytes());
          } catch ( Exception e ) {

          }
     }

     private void generateImport() {
          try {
               String importStr = "import java.util.*;\nimport pub.platform.db.*;\nimport pub.platform.utils.*;\nimport pub.platform.db.AbstractBasicBean;\n";
               fos.write(importStr.getBytes());
          } catch ( Exception e ) {

          }
     }

     private void generateUtilMethod(String className) {
          String utilMethod =
                 "     public static List find(String sSqlWhere) { "
               + "          return new " + className + "().findByWhere(sSqlWhere); "
               + "     } "
               + " "
               + "     public static List findAndLock(String sSqlWhere) { "
               + "          return new " + className + "().findAndLockByWhere(sSqlWhere); "
               + "     } "
               + " "
               + "     public static " + className + " findFirst(String sSqlWhere) { "
               + "          return (" + className + ")new " + className + "().findFirstByWhere(sSqlWhere); "
               + "     } "
               + " "
               + "     public static " + className + " findFirstAndLock(String sSqlWhere) { "
               + "          return (" + className + ")new " + className + "().findFirstAndLockByWhere(sSqlWhere); "
               + "     } "
               + "      "
               + "     public static RecordSet findRecordSet(String sSqlWhere) { "
               + "          return new " + className + "().findRecordSetByWhere(sSqlWhere); "
               + "     } "
               + " "
               + "     public static List find(String sSqlWhere,boolean isAutoRelease) { "
               + "          " + className + " bean = new " + className + "(); "
               + "          bean.setAutoRelease(isAutoRelease); "
               + "          return bean.findByWhere(sSqlWhere); "
               + "     } "
               + " "
               + "     public static List findAndLock(String sSqlWhere,boolean isAutoRelease) { "
               + "          " + className + " bean = new " + className + "(); "
               + "          bean.setAutoRelease(isAutoRelease); "
               + "          return bean.findAndLockByWhere(sSqlWhere); "
               + "     } "
               + " "
               + "     public static " + className + " findFirst(String sSqlWhere,boolean isAutoRelease) { "
               + "          " + className + " bean = new " + className + "(); "
               + "          bean.setAutoRelease(isAutoRelease); "
               + "          return (" + className + ")bean.findFirstByWhere(sSqlWhere); "
               + "     } "
               + " "
               + "     public static " + className + " findFirstAndLock(String sSqlWhere,boolean isAutoRelease) { "
               + "          " + className + " bean = new " + className + "(); "
               + "          bean.setAutoRelease(isAutoRelease); "
               + "          return (" + className + ")bean.findFirstAndLockByWhere(sSqlWhere); "
               + "     } "
               + " "
               + "     public static RecordSet findRecordSet(String sSqlWhere,boolean isAutoRelease) { "
               + "          " + className + " bean = new " + className + "(); "
               + "          bean.setAutoRelease(isAutoRelease); "
               + "          return bean.findRecordSetByWhere(sSqlWhere); "
               + "     } "
               
               ///add by leonwoo 增加一个接口
               +"     public static List findByRow(String sSqlWhere,int row) { "
               + "          return new " + className + "().findByWhereByRow(sSqlWhere,row); "
               + "     } ";
          
          

          try {
               fos.write(utilMethod.getBytes());
          } catch(IOException ex) {

          }

     }

     public void generate(String classPath,String className,String tablename) {
          if ( className == null || tablename == null || classPath == null ) {
               System.out.println("Error!");
               return;
          }
          String filepath = "src/"+classPath.replace('.','/')+"/";
          tablename = tablename.toUpperCase();
          System.out.println("======正在产生类【"+filepath+className+".java】======");
          try {
               fos = new FileOutputStream(filepath+className + ".java");
               generateHead(classPath);
               generateImport();
               fos.write(("public class "+className+" extends AbstractBasicBean implements Cloneable {\n").getBytes());
               generateUtilMethod(className); //zw changed 20050127
               ConnectionManager cm = ConnectionManager.getInstance();
               DatabaseConnection dc = cm.get();
               //fos.write(("private SQLMaker sqlMaker = new SQLMaker();\n").getBytes());
               String sql = " select case coltype " +
                               " when 'NUMBER' then " +
                                    " case when scale > 0 then " +
                                    " 'double '||lower(cname)||';' "+
                                    " else "+
                                    " 'int '||lower(cname)||';' " +
                                    " end " +
                                    " when 'DATE' then  " +
                                    " 'String '||lower(cname)||';' " +
                                    " else " +
                                    " 'String '||lower(cname)||';' " +
                                    " end " +
                                    " from sys.col where tname='"+ tablename+"' order by colno ";
//               System.out.println(sql);
               RecordSet rs = dc.executeQuery(sql);
               while ( rs != null && rs.next() ) {
                    fos.write((rs.getString(0)+"\n").getBytes());
               }

               fos.write(("public static final String TABLENAME =\""+tablename.toLowerCase()+"\";\n").getBytes());
               fos.write(("private String operate_mode = \"add\";\n").getBytes()); //dd
               fos.write(("public ChangeFileds cf = new ChangeFileds();\n").getBytes());//dd
               fos.write("public String getTableName() {return TABLENAME;}\n".getBytes());

               fos.write("public void addObject(List list,RecordSet rs) {\n".getBytes());
               fos.write((className+" abb = new "+className+"();\n").getBytes());
//               String setSql =
//                    " select case coltype " +
//                    "   when 'NUMBER' then "+
//                    "     case when scale > 0 then "+
//                    "       'abb.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(rs.getDouble(\"'|| lower(cname) ||'\"));' "+
//                    "     else " +
//                    "       'abb.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(rs.getInt(\"'|| lower(cname) ||'\"));' "+
//                    "   end " +
//                    "   when 'DATE' then "+
//                    "       'abb.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(rs.getString(\"'|| lower(cname) ||'\"));' "+
//                    "  else " +
//                    "       'abb.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(rs.getString(\"'|| lower(cname) ||'\"));' "+
//                    "  end " +
//                    " from sys.col where tname='"+tablename+"' order by colno";
               String setSql =
                    " select case coltype " +
                    "   when 'NUMBER' then "+
                    "     case when scale > 0 then "+
                    "       'abb.'||lower(cname)||'=rs.getDouble(\"'|| lower(cname) ||'\");'||'abb.setKeyValue(\"'||upper(cname)||'\",\"\"+abb.get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'());' "+
                    "     else " +
                    "       'abb.'||lower(cname)||'=rs.getInt(\"'|| lower(cname) ||'\");'||'abb.setKeyValue(\"'||upper(cname)||'\",\"\"+abb.get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'());' "+
                    "   end " +
                    "   when 'DATE' then "+
                    "       'abb.'||lower(cname)||'=rs.getTimeString(\"'|| lower(cname) ||'\");'||'abb.setKeyValue(\"'||upper(cname)||'\",\"\"+abb.get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'());' "+
                    "  else " +
                    "       'abb.'||lower(cname)||'=rs.getString(\"'|| lower(cname) ||'\");'||'abb.setKeyValue(\"'||upper(cname)||'\",\"\"+abb.get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'());' "+
                    "  end " +
                    " from sys.col where tname='"+tablename+"' order by colno";

//               System.out.println(setSql);
               rs = dc.executeQuery(setSql);

               while ( rs != null && rs.next() ) {
                    fos.write((rs.getString(0)+"\n").getBytes());
               }
               fos.write("list.add(abb);\n".getBytes());
               fos.write("abb.operate_mode = \"edit\";\n".getBytes());
               fos.write("}".getBytes());

               String getterSql =
                    " select case coltype " +
                    " when 'NUMBER' then " +
                    "   case when scale > 0 then "+
                    "     'public double get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'() { return this.'||lower(cname)||';}'  " +
                    "   else " +
                    "     'public int get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'() { return this.'||lower(cname)||';}'  " +
                    "   end " +
                    " when 'DATE' then  "+
                    "   'public String get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'() {  if ( this.'||lower(cname)||' == null ) { return \"\"; } else { return this.'||lower(cname)||'.trim().split(\" \")[0];} }'||" +
                    "   'public String get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'Time() {  if ( this.'||lower(cname)||' == null ) return \"\"; return this.'||lower(cname)||'.split(\"\\\\.\")[0];}'  " +
                    " else " +
                    "   'public String get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'() { if ( this.'||lower(cname)||' == null ) return \"\"; return this.'||lower(cname)||';}'       " +
                    " end " +
                    " from sys.col where tname='"+tablename+"' order by colno";
//               System.out.println(getterSql);
               rs = dc.executeQuery(getterSql);
               while ( rs != null && rs.next() ) {
                    fos.write((rs.getString(0)+"\n").getBytes());
               }

               String setterSql =
                    " select case coltype "+
                    "        when 'NUMBER' then "+
                    "            case when scale > 0 then"+
                    "              'public void set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(double '||lower(cname)||') { sqlMaker.setField(\"'||lower(cname)||'\",\"\"+'||lower(cname)||',Field.NUMBER); if (this.operate_mode.equals(\"edit\")) { if (this.get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'()!='||lower(cname)||') cf.add(\"'||lower(cname)||'\",this.'||lower(cname)||'+\"\",'||lower(cname)||'+\"\"); } this.'||lower(cname)||'='||lower(cname)||';}'"+
                    "            else "+
                    "              'public void set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(int '||lower(cname)||') { sqlMaker.setField(\"'||lower(cname)||'\",\"\"+'||lower(cname)||',Field.NUMBER); if (this.operate_mode.equals(\"edit\")) { if (this.get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'()!='||lower(cname)||') cf.add(\"'||lower(cname)||'\",this.'||lower(cname)||'+\"\",'||lower(cname)||'+\"\"); } this.'||lower(cname)||'='||lower(cname)||';}' "+
                    "            end "+
                    "        when 'DATE' then "+
                    "           'public void set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(String '||lower(cname)||') { sqlMaker.setField(\"'||lower(cname)||'\",'||lower(cname)||',Field.DATE); if (this.operate_mode.equals(\"edit\")) { if (!this.get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'().equals('||lower(cname)||')) cf.add(\"'||lower(cname)||'\",this.'||lower(cname)||','||lower(cname)||'); } this.'||lower(cname)||'='||lower(cname)||';}' "+
                    "        else "+
                    "           'public void set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(String '||lower(cname)||') { sqlMaker.setField(\"'||lower(cname)||'\",'||lower(cname)||',Field.TEXT); if (this.operate_mode.equals(\"edit\")) { if (!this.get'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'().equals('||lower(cname)||')) cf.add(\"'||lower(cname)||'\",this.'||lower(cname)||','||lower(cname)||'); } this.'||lower(cname)||'='||lower(cname)||';}' "+
                    "        end "+
                    " from sys.col where tname='"+tablename+"' order by colno";
//               System.out.println(setterSql);
               rs = dc.executeQuery(setterSql);
               while ( rs != null && rs.next() ) {
                    fos.write((rs.getString(0)+"\n").getBytes());
               }
               String initSql =
                    " select  case coltype " +
                    " when 'NUMBER' then " +
                    " case when scale > 0 then " +
                    " 'if ( actionRequest.getFieldValue(i,\"'||lower(cname)||'\") !=null && actionRequest.getFieldValue(i,\"'||lower(cname)||'\").trim().length() > 0 ) {' ||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(Double.parseDouble(actionRequest.getFieldValue(i,\"'||lower(cname)||'\")));}' " +
                    " else " +
                    " 'if ( actionRequest.getFieldValue(i,\"'||lower(cname)||'\") !=null && actionRequest.getFieldValue(i,\"'||lower(cname)||'\").trim().length() > 0 ) {' ||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(Integer.parseInt(actionRequest.getFieldValue(i,\"'||lower(cname)||'\")));}' " +
                    " end " +
                    " when 'DATE' then " +
                    " 'if ( actionRequest.getFieldValue(i,\"'||lower(cname)||'\") !=null ) {' ||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(actionRequest.getFieldValue(i,\"'||lower(cname)||'\"));}' " +
                    " else " +
                    " 'if ( actionRequest.getFieldValue(i,\"'||lower(cname)||'\") !=null ) {' ||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(actionRequest.getFieldValue(i,\"'||lower(cname)||'\"));}' " +
                    " end " +
                    " from sys.col where tname='"+tablename+"' order by colno";

//               System.out.println(initSql);
               fos.write("public void init(int i,ActionRequest actionRequest) throws Exception { ".getBytes());
               rs = dc.executeQuery(initSql);
               while ( rs != null && rs.next() ) {
                    fos.write((rs.getString(0)+"\n").getBytes());
               }
               fos.write("}".getBytes());


               fos.write("public void init(ActionRequest actionRequest) throws Exception { ".getBytes());
               fos.write("this.init(0,actionRequest);".getBytes());
               fos.write("}".getBytes());

//==========initAll
               String initAllSql =
                    " select  case coltype " +
                    " when 'NUMBER' then " +
                    " case when scale > 0 then " +
                    " 'if ( actionRequest.getFieldValue(i,\"'||lower(cname)||'\") !=null && actionRequest.getFieldValue(i,\"'||lower(cname)||'\").trim().length() > 0 ) {' ||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(Double.parseDouble(actionRequest.getFieldValue(i,\"'||lower(cname)||'\")));} else {'||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(0);}' " +
                    " else " +
                    " 'if ( actionRequest.getFieldValue(i,\"'||lower(cname)||'\") !=null && actionRequest.getFieldValue(i,\"'||lower(cname)||'\").trim().length() > 0 ) {' ||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(Integer.parseInt(actionRequest.getFieldValue(i,\"'||lower(cname)||'\")));} else {'||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(0);}' " +
                    " end " +
                    " when 'DATE' then " +
                    " 'if ( actionRequest.getFieldValue(i,\"'||lower(cname)||'\") !=null ) {' ||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(actionRequest.getFieldValue(i,\"'||lower(cname)||'\"));} else {'||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(null);}' " +
                    " else " +
                    " 'if ( actionRequest.getFieldValue(i,\"'||lower(cname)||'\") !=null ) {' ||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(actionRequest.getFieldValue(i,\"'||lower(cname)||'\"));} else {'||'this.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(null);}' " +
                    " end " +
                    " from sys.col where tname='"+tablename+"' order by colno";


//               System.out.println(initAllSql);
               fos.write("public void initAll(int i,ActionRequest actionRequest) throws Exception { ".getBytes());
//               rs = dc.executeQuery(initAllSql);
//               while ( rs != null && rs.next() ) {
//                    fos.write((rs.getString(0)+"\n").getBytes());
//               }
               fos.write("this.init(i,actionRequest);".getBytes());
               fos.write("}".getBytes());


               fos.write("public void initAll(ActionRequest actionRequest) throws Exception { ".getBytes());
               fos.write("this.initAll(0,actionRequest);".getBytes());
               fos.write("}".getBytes());

               //==========initAll


               String cloneSql =
                    " select  case coltype " +
                    " when 'NUMBER' then " +
                    " case when scale > 0 then " +
                    " 'obj.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(obj.'||lower(cname)||');' " +
                    " else " +
                    " 'obj.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(obj.'||lower(cname)||');' " +
                    " end " +
                    " when 'DATE' then " +
                    " 'obj.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(obj.'||lower(cname)||');' " +
                    " else " +
                    " 'obj.set'||substr(cname,1,1)||lower(substr(cname,2,length(cname)-1))||'(obj.'||lower(cname)||');' " +
                    " end " +
                    " from sys.col where tname='"+tablename+"' order by colno";

//               System.out.println(cloneSql);

               fos.write("public Object clone() throws CloneNotSupportedException { ".getBytes());
               fos.write((className+" obj = ("+className+")super.clone();").getBytes());
               rs = dc.executeQuery(cloneSql);
               while ( rs != null && rs.next() ) {
                    fos.write((rs.getString(0)+"\n").getBytes());
               }

               fos.write("return obj;".getBytes());
               fos.write("}".getBytes());

               cm.release();
               fos.write("}".getBytes());

               fos.flush();
               fos.close();
          } catch ( Exception e ) {
               e.printStackTrace();
               System.out.println("*******产生类【"+filepath+className+".java】失败！*******");
          }
          System.out.println("======产生类【"+filepath+className+".java】成功！======");
     }
}
