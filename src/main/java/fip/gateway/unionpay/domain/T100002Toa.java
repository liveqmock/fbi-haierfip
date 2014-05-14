package fip.gateway.unionpay.domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import fip.gateway.unionpay.core.TOAHeader;

import java.util.List;

/**
 * 批量代付.
 * User: zhanrui
 * Date: 11-7-25
 * Time: 上午9:27
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("GZELINK")
public class T100002Toa {
    public static class ToaHeader extends TOAHeader {
    }

    public static class Body {
        public List<BodyDetail> RET_DETAILS;

        @XStreamAlias("RET_DETAIL")
        public static class BodyDetail {
            public String SN = "";
            public String RET_CODE = "";
            public String ERR_MSG = "";
        }
    }

    public ToaHeader INFO;
    public Body BODY;

    @Override
    public String toString() {
        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("$", "_");
        HierarchicalStreamDriver hierarchicalStreamDriver = new XppDriver(replacer);
        XStream xs = new XStream(hierarchicalStreamDriver);
        xs.processAnnotations(T100002Toa.class);
        return "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xs.toXML(this);
    }
    public static T100002Toa getToa(String xml){
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(T100002Toa.class);
        return (T100002Toa)xs.fromXML(xml);
    }
}
