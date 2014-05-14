package pub.platform.utils;

public class ReturnNoString {
     public ReturnNoString() {
     }
     public static String getString(int i){
          return (i==0)?"":String.valueOf(i);
     }
     public static String getString(String str){
          return (str==null)?"":str.trim();
     }
}
