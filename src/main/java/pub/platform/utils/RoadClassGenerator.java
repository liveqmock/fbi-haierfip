package pub.platform.utils;

public class RoadClassGenerator {
     public RoadClassGenerator() {
     }

     /**
      * main
      *
      * @param argv String[]
      */
     public static void main(String[] argv) {
          JavaBeanGenerator jbg = new JavaBeanGenerator();
          jbg.generate("zt.catv.sys.business.dao","PTRoadBean","ptroad");
          jbg.generate("zt.catv.sys.business.dao","PTRoadHisBean","ptroadhis");
          jbg.generate("zt.catv.sys.business.dao","PTRoadChgHisBean","ptroadchghis");
     }
}
