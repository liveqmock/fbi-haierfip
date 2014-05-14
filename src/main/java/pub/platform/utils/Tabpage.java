package pub.platform.utils;

public class Tabpage {

     public static String creTabpage ( String id , String tabs , String imgPath ) {

          if ( tabs == "" )return "" ;
          String[] Tabs = Basic.splite ( tabs , "," ) ;

          String prop = " id=" + id + " tabs=\"" + tabs + "\"" ;

          String HTMLStr = " <table style=\"TOP:0px ;width:100% \"  attrib=tabpage cellspacing=0 cellpadding=0 " + prop + "><TBODY>" ;

          HTMLStr += " <TR> " ;

          HTMLStr += " <TD firstcell=\"true\"><IMG src=\"" + imgPath + "images/start_tab.gif\"></TD> " ;

          String caption , tabcode ;
          int index ;

          for ( int i = 0 ; i < Tabs.length ; i++ ) {
               index = Tabs[i].indexOf ( '=' ) ;

               if ( index > 0 ) {
                    caption = Tabs[i].substring ( 0 , index ) ;
                    tabcode = Tabs[i].substring ( index + 1 ) ;
               } else {
                    caption = Tabs[i] ;
                    tabcode = Tabs[i] ;
               }

               HTMLStr += " <TD class=\"tabTD\" tab_index=\"" + i + "\" tab_code=\"" + tabcode
                    + "\">" ;



               HTMLStr += " <INPUT   id=\"" + tabcode + "\" hideFocus  class=\"tabButton\"  " ;
               HTMLStr += " onclick=tabpage_onclick(this) tabIndex=-1 type=button value=" + caption + "></TD>" ;

               if ( i != Tabs.length - 1 ) {
                    HTMLStr += " <TD><IMG src=\"" + imgPath + "images/tab.gif\"></TD> " ;
               } else {
                    HTMLStr += " <TD lastcell=\"true\"><IMG src=\"" + imgPath + "images/end_tab.gif\"></TD>" ;
               }
          }

          HTMLStr += " <TD class=\"endTD\" ></TD></TR></TBODY>" ;

          HTMLStr += " </table>" ;

          return HTMLStr ;

     }

}
