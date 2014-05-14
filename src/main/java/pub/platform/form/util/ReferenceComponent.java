package pub.platform.form.util;


/**
 * @author qingdao tec
 * @version 1.0
 */
public class ReferenceComponent {
    private Object value;
    private String name;

    /**
     * @param value
     * @param name
     * @roseuid 3F73EC1C0103
     */
    public ReferenceComponent(Object p_value, String p_name) {
      this.value=p_value;
      this.name=p_name;
    }

    /**
     * @return Object
     * @roseuid 3F73EC0A020B
     */
    public Object getValue() {
     return this.value;
    }

    /**
     * @return String
     * @roseuid 3F73EC1601EB
     */
    public String getName() {
     return this.name;
    }
}
