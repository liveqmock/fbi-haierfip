package pub.platform.control.autorun;

import java.util.*;

import pub.AutorunInterface;
import pub.platform.control.autorun.SchedulerTask;
/**
 *
 * <p>Title: </p>
 * <p>随着系统的启动，启动定时任务 </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: leonwoo</p>
 * @author yujj
 * @version 1.0
 */
public class AutoRunMain implements AutorunInterface{

     public AutoRunMain() {
     }

     public boolean stopServer() {

          return true;
     }

     public boolean startServer() {
          try {
               System.out.println("autorunmain startServer is run");
               Timer timer = new Timer();
               Calendar calendar = Calendar.getInstance();
               calendar.set(Calendar.HOUR_OF_DAY, 22);
               calendar.set(Calendar.MINUTE, 00);
               calendar.set(Calendar.SECOND, 00);
               SchedulerTask schedulerTask = new SchedulerTask();
               timer.scheduleAtFixedRate(schedulerTask, calendar.getTime(), 60 * 60 * 1000 * 24);
               //timer.scheduleAtFixedRate(schedulerTask, calendar.getTime(),10 * 60 * 1000 );

               return true;
          } catch(Exception ex) {
               ex.printStackTrace();

               return false;
          }

     }

     public static void main(String[] arg) {

          AutoRunMain autoRun=new AutoRunMain();
          autoRun.startServer();
//          SchedulerTask st = new SchedulerTask();
//          st.run();
//          DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//           String CONFIG_FILE_NAME="autorunconfig.xml";
//        String txnCode="";
//        try
//        {
//             DocumentBuilder db = dbf.newDocumentBuilder();
//
//             Document dom = db.parse(new File(CONFIG_FILE_NAME));
//             if(dom==null)
//             {
//                  System.out.println(" is null ");
//             }
//             Element root = dom.getDocumentElement();
//
//             NodeList nodes = root.getElementsByTagName("transaction");
//             HashMap hourMap = new HashMap();
//             HashMap miniteMap = new HashMap();
//             HashMap classNameMap = new HashMap();
//             int length = nodes.getLength();
//             for(int i = 0; i < length; i++) {
//                  Node node = nodes.item(i); //<transaction>
//                  if(node.getNodeType() != Node.ELEMENT_NODE)
//                       continue;
//                  Element el = (Element)node;
//                  txnCode = el.getAttribute("txncode");
//                  System.out.print(txnCode);
//                  //1.取出输入字段<inputfields>
//                  NodeList nodeList = el.getElementsByTagName("runtime");
//                  if(nodeList.getLength() > 0) {
//                       Element elrun = (Element)nodeList.item(0);
//                       hourMap.put(txnCode, elrun.getAttribute("itemhour"));
//                       miniteMap.put(txnCode, elrun.getAttribute("itemminite"));
//                  }
//
//                  //3.取业务类
//                  NodeList procNode = el.getElementsByTagName("processclass");
//                  if(procNode.getLength() > 0) {
//                       Element procEl = (Element)procNode.item(0);
//                       classNameMap.put(txnCode, procEl.getAttribute("classname"));
//                  }
//             }
//
//             System.out.println(hourMap.get("9001").toString()+miniteMap.get("9001").toString()+classNameMap.get("9001").toString());
//             System.out.println(hourMap.get("9002").toString()+miniteMap.get("9002").toString()+classNameMap.get("9002").toString());
//        }
//        catch(Exception ex)
//        {
//         ex.printStackTrace();
//        }
//

     }
}
