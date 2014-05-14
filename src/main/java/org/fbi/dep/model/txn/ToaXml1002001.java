package org.fbi.dep.model.txn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.fbi.dep.model.base.ToaXml;
import org.fbi.dep.model.base.ToaXmlHeader;

import java.io.Serializable;
import java.util.List;

/**
 * ÒøÁª£º´ú¸¶
 * User: zhangxiaobo
 * Date: 2013-04-01
 */

@XStreamAlias("GZELINK")
public class ToaXml1002001 extends ToaXml {
    public ToaXmlHeader INFO;
    public Body BODY;

    public static class Body implements Serializable {
        public List<BodyDetail> TRANS_DETAILS;

        @XStreamAlias("TRANS_DETAIL")
        public static class BodyDetail implements Serializable {
            public String SN = "0001";
            public String RET_CODE = "";
            public String ERR_MSG = ""; //

            public String getSN() {
                return SN;
            }

            public void setSN(String SN) {
                this.SN = SN;
            }

            public String getRET_CODE() {
                return RET_CODE;
            }

            public void setRET_CODE(String RET_CODE) {
                this.RET_CODE = RET_CODE;
            }

            public String getERR_MSG() {
                return ERR_MSG;
            }

            public void setERR_MSG(String ERR_MSG) {
                this.ERR_MSG = ERR_MSG;
            }
        }
    }

    @Override
    public String toString() {
        this.INFO.TRX_CODE = "1002001";
        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("$", "_");
        HierarchicalStreamDriver hierarchicalStreamDriver = new XppDriver(replacer);
        XStream xs = new XStream(hierarchicalStreamDriver);
        xs.processAnnotations(ToaXml1002001.class);
        return "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xs.toXML(this);
    }

    public static ToaXml1002001 getToa(String xml) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(ToaXml1002001.class);
        return (ToaXml1002001) xs.fromXML(xml);
    }
}
