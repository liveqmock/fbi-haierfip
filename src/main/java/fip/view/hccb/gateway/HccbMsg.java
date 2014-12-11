package fip.view.hccb.gateway;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import java.io.Serializable;

abstract  public  class HccbMsg implements Serializable {
    public HccbMsgHead head = new HccbMsgHead();
    public HccbMsgBody body;

    public HccbMsg toBean(String xml) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(this.getClass());
        return (HccbMsg) xs.fromXML(xml);
    }
    public String toXml(HccbMsg bean) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(this.getClass());
        return  xs.toXML(bean);
    }

    //=======

}
