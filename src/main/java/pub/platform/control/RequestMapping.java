package pub.platform.control;

import java.util.*;
import java.io.*;

public class RequestMapping
{
    private static Map maps = new HashMap();
    public static final String ROOT_NODE_NAME = "root";

    public static void init(InputStream is) {
        try {
            Properties props = new Properties();
            props.load(is);
            for ( Enumeration enu = props.propertyNames() ; enu.hasMoreElements() ; ) {
                try {
                    String name = (String) enu.nextElement();
                    String value = props.getProperty(name);
                    if (value != null) {
                        value = new String(value.getBytes("ISO-8859-1"));
                    }
                    maps.put(name,value);
                } catch ( Exception e ) {
                    e.printStackTrace();
                }
            }
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }
    public static String map(String name) {
        return (String)maps.get(ROOT_NODE_NAME)+(String)maps.get(name);
    }
    public static void main(String[] argv) {
        RequestMapping rm = new RequestMapping();
        RequestMapping.init(rm.getClass().getResourceAsStream("maps.properties"));
        System.out.println(rm.map("/login.do"));
    }
}