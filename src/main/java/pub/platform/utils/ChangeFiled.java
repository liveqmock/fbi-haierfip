package pub.platform.utils;

public class ChangeFiled implements java.io.Serializable{
     private String filedName;
     private String oriValue;
     private String nowValue;

     public ChangeFiled(String filedName,String oriValue,String nowValue) {
          this.filedName = filedName;
          this.oriValue = oriValue;
          this.nowValue = nowValue;
     }

     public String getFiledName() {
          return filedName;
     }
     public void setFiledName(String filedName) {
          this.filedName = filedName;
     }
     public String getOriValue() {
          return oriValue;
     }
     public void setOriValue(String oriValue) {
          this.oriValue = oriValue;
     }
     public String getNowValue() {
          return nowValue;
     }
     public void setNowValue(String nowValue) {
          this.nowValue = nowValue;
     }
}
