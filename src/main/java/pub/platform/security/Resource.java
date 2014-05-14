package pub.platform.security;

import java.io.Serializable;


/**
 * ×ÊÔ´Àà
 *
 * @author WangHaiLei
 * @version 1.2
 * $ UpdateDate: Y-M-D-H-M:
 *               2003-12-09-14-00
 *               2004-02-28-11-27
 * $
 */
public class Resource  implements Serializable, Comparable {

     public static final int JSP_TYPE = 1;
     public static final int FORM_TYPE = 2;
     public static final int FORM_METHOD_TYPE = 3;
     public static final int MENU_TYPE = 4;

////////////////////////////////////////////////////////////////////////////////////////////////////

     public Resource() {
     }

     public Resource(String resourceid, String resource, int type) {

          super();
          this.resourceid = resourceid;
          this.resource = resource;
          this.type = type;
     }

////////////////////////////////////////////////////////////////////////////////////////////////////

     /**
      *
      */
     private String resourceid  = null;
     public String getResourceid() {
          return resourceid;
     }
     public void setResourceid(String resourceid) {
          this.resourceid = resourceid;
     }

     /**
      *
      */
     private String parentid    = null;
     public String getParentid() {
          return parentid;
     }
     public void setParentid(String parentid) {
          this.parentid = parentid;
     }

     /**
      *
      */
     private String resource    = null;
     public String getResource() {
          return resource;
     }
     public void setResource(String resource) {
          this.resource = resource;
     }

     /**
      *
      */
     private String description = null;
     public String getDescription() {
          return description;
     }
     public void setDescription(String description) {
          this.description = description;
     }

     /**
      *
      */
     private int type = 0;
     public int getType() {
          return type;
     }
     public void setType(int type) {
          this.type = type;
     }

// added by wuyeyuan ///////////////////////////////////////////////////////

     public int compareTo(Object o) {
         if ( o == null )
             return -1;
         if ( !(o instanceof Resource) )
             return -1;
         Resource r = (Resource)o;
         if ( r != null && resource != null && resource.equals(r.getResource()) && r.getType() == this.type ) {
             return 0;
         }
         return 1;
     }

     public boolean equals(Object o) {
         if ( compareTo(o) == 0 )
             return true;
         return false;
     }

     public String toString() {
         return "resourceid="+resourceid+",parentid="+parentid+",resource="+resource+",type="+type;
     }
}