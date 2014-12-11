package mock.onekeyactchk.domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.Serializable;

abstract  public  class HttpXmlMsg implements Serializable {

    public HttpXmlMsg toBean(String xml) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(this.getClass());
        return (HttpXmlMsg) xs.fromXML(xml);
    }
    public String toXml(HttpXmlMsg bean) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(this.getClass());
        return  xs.toXML(bean);
    }

    //=======
}
