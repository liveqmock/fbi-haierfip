package fip.gateway.ccms.domain.common;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class BaseBean {

    public String toXml() {
        String xmlHead = "<?xml version=\"1.0\" encoding=\"GBK\"?>";
        XStream xStream = new XStream();
        xStream.processAnnotations(this.getClass());
        String xmlContent = xStream.toXML(this);
        return xmlHead+xmlContent;
    }

    public static Object toObject(Class clazz, String xml){
        XStream xStream = new XStream(new DomDriver());
        xStream.processAnnotations(clazz);
        return xStream.fromXML(xml);
    }
}
