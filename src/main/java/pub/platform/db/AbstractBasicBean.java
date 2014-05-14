package pub.platform.db;

import java.io.Serializable;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pub.platform.utils.*;



public abstract class AbstractBasicBean implements Cloneable,Serializable {
	private static final Log logger = LogFactory.getLog(AbstractBasicBean.class);
	
     protected SQLMaker sqlMaker = new SQLMaker();
     protected Hashtable keyValues = new Hashtable();
     protected Exception errorException;

     protected boolean isAutoRelease = false;//默认释放Connection

     public int insert() {
          int insertCount = -1;
          if ( sqlMaker == null ) {
               return insertCount;
          }
          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();
          dc.begin();
          try {
        	  	sqlMaker.setTableStr("insert into "+getTableName()+" ");
        	  	String sSql = sqlMaker.getInsertSQL();
               insertCount = dc.executeUpdate(sSql);
               errorException = dc.getErrorException();
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( insertCount > 0 ) {
                    dc.commit();
               } else {
                    Debug.debug(Debug.TYPE_WARNING,"＝＝＝" + this.getClass().getName() + "＝＝产生失败，事务回滚！");
                    dc.rollback();
               }
               if ( isAutoRelease )
                    cm.release();
               initSqlMaker();
               return insertCount;
          }
     }

     public int deleteByWhere(String sSqlWhere) {
          String sSql = "delete from " + getTableName() + " ";
          int deleteCount = -1;
          if ( sSqlWhere != null && sSqlWhere.length() > 0 ) {
               sSql += sSqlWhere;
          }
          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();
          dc.begin();
          try {
               deleteCount = dc.executeUpdate(sSql);
               errorException = dc.getErrorException();
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( deleteCount >= 0 ) {
                    dc.commit();
               } else {
                    dc.rollback();
               }
               if ( isAutoRelease )
                    cm.release();

               return deleteCount;
          }
     }

     public int updateByWhere(String sSqlWhere) {
          int uptCount = -1;
          if ( sSqlWhere == null ) {
               return uptCount;
          }

          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();
          dc.begin();
          try {
              sqlMaker.setTableStr("update " + getTableName() + " ");
              sqlMaker.setWhereStr(sSqlWhere);
              String sSql = sqlMaker.getUpdateSQL();

              uptCount = dc.executeUpdate(sSql);
               errorException = dc.getErrorException();
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( uptCount >= 0 ) {
                    dc.commit();
               } else {
                    dc.rollback();
               }

               if ( isAutoRelease )
                    cm.release();

               initSqlMaker();
               return uptCount;
          }
     }

     public boolean isExist(String sSqlWhere) {
          String sSql = "select * from " + getTableName() + " ";
          if ( sSqlWhere != null && sSqlWhere.length() > 0 ) {
               sSql += sSqlWhere;
          }
          boolean isExist = false;
          ConnectionManager cm = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();
          try {
               RecordSet rs = dc.executeQuery(sSql);
               errorException = dc.getErrorException();
               if ( rs != null && rs.next() ) {
                    isExist = true;
               }
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( isAutoRelease )
                    cm.release();
               return isExist;
          }
     }

     public List findByWhere(String sSqlWhere) {
          String sSql = "select * from " + getTableName() + " ";
          if ( sSqlWhere != null && sSqlWhere.length() > 0 ) {
               sSql += sSqlWhere;
          }

          List list = new ArrayList();
          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();

          try {
               RecordSet rs = dc.executeQuery(sSql);
               errorException = dc.getErrorException();
               while ( rs != null && rs.next() ) {
                   addObject(list,rs);
               }
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( isAutoRelease )
                    cm.release();
               return list;
          }
     }
     
     
     /*
      * add by leonwoo
      * 取出前n个dao对象
      * 
      */
     public List findByWhereByRow(String sSqlWhere,int num) {
         //String sSql = "select * from (select * from " + getTableName() + ") where rownum<="+num;
    	 String sSql = "select * from " + getTableName()+"  " ;
         if ( sSqlWhere != null && sSqlWhere.length() > 0 ) {
              sSql += sSqlWhere;
         }
         sSql="select * from ("+sSql+") where rownum<="+num;
         List list = new ArrayList();
         ConnectionManager cm  = ConnectionManager.getInstance();
         DatabaseConnection dc = cm.get();

         try {
              RecordSet rs = dc.executeQuery(sSql);
              errorException = dc.getErrorException();
              while ( rs != null && rs.next() ) {
                  addObject(list,rs);
              }
         } catch ( Exception e ) {
              errorException =e;
              e.printStackTrace();
         } finally {
              if ( isAutoRelease )
                   cm.release();
              return list;
         }
    }
     
     
     public Object findFirstByWhere(String sSqlWhere) {
          String sSql = "select * from " + getTableName() + " ";
          if ( sSqlWhere != null && sSqlWhere.length() > 0 ) {
               sSql += sSqlWhere;
          }

          List list = new ArrayList();
          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();

          try {
               RecordSet rs = dc.executeQuery(sSql);
               errorException = dc.getErrorException();
               if ( rs != null && rs.next() ) {
                   addObject(list,rs);
               }
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( isAutoRelease )
                    cm.release();
               if ( list.size() > 0 )
                    return list.get(0);
               else
                    return null;
          }
     }

     public RecordSet findRecordSetByWhere(String sSqlWhere) {
          String sSql = "select * from " + getTableName() + " ";
          if ( sSqlWhere != null && sSqlWhere.length() > 0 ) {
               sSql += sSqlWhere;
          }

          List list = new ArrayList();
          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();
          RecordSet rs = null;
          try {
               rs = dc.executeQuery(sSql);
               errorException = dc.getErrorException();
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( isAutoRelease )
                    cm.release();
               return rs;
          }
     }

     public List findAndLockByWhere(String sSqlWhere) {
          String sSql = "select * from " + getTableName() + " ";
          if ( sSqlWhere != null && sSqlWhere.length() > 0 ) {
               sSql += sSqlWhere;
          }
          sSql += " for update ";

          List list = new ArrayList();
          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();

          try {
               RecordSet rs = dc.executeQuery(sSql);
               errorException = dc.getErrorException();
               while ( rs != null && rs.next() ) {
                   addObject(list,rs);
               }
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( isAutoRelease )
                    cm.release();
               return list;
          }
     }

     public Object findFirstAndLockByWhere(String sSqlWhere) {
          String sSql = "select * from " + getTableName() + " ";
          if ( sSqlWhere != null && sSqlWhere.length() > 0 ) {
               sSql += sSqlWhere;
          }
          sSql += " for update ";

          List list = new ArrayList();
          ConnectionManager cm  = ConnectionManager.getInstance();
          DatabaseConnection dc = cm.get();

          try {
               RecordSet rs = dc.executeQuery(sSql);
               errorException = dc.getErrorException();
               if ( rs != null && rs.next() ) {
                   addObject(list,rs);
               }
          } catch ( Exception e ) {
               errorException =e;
               e.printStackTrace();
          } finally {
               if ( isAutoRelease )
                    cm.release();
               if ( list.size() > 0 )
                    return list.get(0);
               else
                    return null;
          }
     }

     public void setAutoRelease(boolean autoRelease) {
          this.isAutoRelease = autoRelease;
     }

     public boolean isAutoRelease() {
          return this.isAutoRelease;
     }


     public void initSqlMaker() {
          sqlMaker.init();
     }

     public void setSQLField(String name,String value) {
          sqlMaker.setField(name,value,Field.NUMBER);
     }

     public Object clone()
          throws CloneNotSupportedException {
          AbstractBasicBean obj = (AbstractBasicBean)super.clone();
          obj.sqlMaker = new SQLMaker();

          return obj;
     }

     public void setKeyValue(String key,String value) {
         keyValues.put(key,value);
     }

     public String getKeyValue(String key) {
         return (String)keyValues.get(key);
     }

     public Hashtable getKeyValues() {
         return keyValues;
     }
     public Exception getErrorException() {
         return errorException;
     }

    public SQLMaker getSqlMaker() {
        return sqlMaker;
    }


    public abstract String getTableName() ;
     public abstract void addObject(List list,RecordSet rs);
}
