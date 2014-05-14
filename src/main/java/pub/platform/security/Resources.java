package pub.platform.security;

import java.util.*;
import java.io.Serializable;

import pub.platform.system.manage.dao.*;



/**
 * 资源类集
 * @author WangHaiLei
 * @version 1.2
 * $ UpdateDate: Y-M-D-H-M:
 *               2003-12-09-14-00
 *               2004-03-01-20-54
 * $
 */

public class Resources implements Serializable  {

     /**
      *
      */
     private String operatorId = null;

     /**
      * 资源列表
      *
      * 资源（resource）- 资源实体(Privilege)
      */
     private Map resources = new HashMap();

     /**
      * 构造Privileges
      * 在内部调用init()方法，获得
      * @param operatorid
      * @throws Exception
      * @roseuid 3F80BABE0076
      */
     public Resources(String operatorId) {
          init(operatorId);
     }

     /**
      * 验证资源的权限
      * @param resource
      * @return boolean
      * @roseuid 3F80BA9F0176
      */
     public boolean checkPermission(String resource, int type, String url) {

          boolean permitted = false;
//          print();
          try {
               /*
               Resource rscToBeCheck = new Resource();
               rscToBeCheck.setResource(resource);
               rscToBeCheck.setType(type);

               if(resources.containsValue(rscToBeCheck)) {
                    permitted = true;
               } else {
                    permitted = false;
               }
//______________________________

               if ( permitted && url != null && url.trim().length() > 0 ) {
                   url = url.trim();
                   database = new DatabaseAgent();
                   permitted = database.checkUrl(operatorId,url);
               }
               */
//-------------------
          } catch(Exception ex2) {
               System.out.println("Wrong, when checking permission : ["+ex2+" ]");
          }

          return permitted;
     }

     /**
      * 初始化，将属于该操作员所有资源取出
      * @param operatorid
      * @roseuid 3F80BB050281
      */
     private void init(String operid) {
          PtResourceBean prb = new PtResourceBean();
          List prbs = prb.findByWhere("where resid in (select resid from ptroleres where roleid in (select roleid from ptoperrole where operid='"+operid+"'))");
          for ( int i = 0 ; i < prbs.size() ; i++ ) {
               prb = (PtResourceBean)prbs.get(i);
               resources.put(prb.getResid(),prb);
          }
     }

     public void print() {
         Collection cl = resources.values();
         System.out.println("==========================");
         for ( Iterator its = cl.iterator(); its.hasNext() ; ) {
             Resource it = (Resource)its.next();
             System.out.println(it.toString());
         }
         System.out.println("==========================");
     }
}
