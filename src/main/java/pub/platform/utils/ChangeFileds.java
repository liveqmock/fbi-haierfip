package pub.platform.utils;

import java.util.*;

public class ChangeFileds  implements java.io.Serializable {
     private List cfs = new ArrayList();

     public ChangeFileds() {
     }

     public void add(String filedName,String oriValue,String nowValue) {
          ChangeFiled cf = new ChangeFiled(filedName,oriValue,nowValue);
          cfs.add(cf);
     }

     public List getChangeField() {
          return cfs;
     }
}
