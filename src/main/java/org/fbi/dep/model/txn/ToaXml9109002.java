package org.fbi.dep.model.txn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.fbi.dep.model.base.ToaXml;
import org.fbi.dep.model.base.ToaXmlInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 巨商汇查询入账结果
 */

@XStreamAlias("ROOT")
public class ToaXml9109002 extends ToaXml {
    public ToaXmlInfo INFO = new ToaXmlInfo();
    public Body BODY = new Body();


    public static class Body implements Serializable {

        public String DETAILNUM;        // 明细数
        @XStreamImplicit
        public List<BodyDetail> DETAILS;

    }

    @XStreamAlias("DETAIL")
    public static class BodyDetail implements Serializable {

        public String ORDERID = "";
        public String TXNDATE = "";
        public String SERIALNO = "";
        public String ACTNO = "";
        public String ACTNAME = "";
        public String TXN_AMT = "";
        public String ACTDATE = "";
        public String FORMCODE = "";
        public String FORMMSG = "";
        public String REMARK = "";
        public String RESERVE = "";


    }
    @Override
    public String toString() {
        this.INFO.TXN_CODE = "9109002";
        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("$", "_");
        HierarchicalStreamDriver hierarchicalStreamDriver = new XppDriver(replacer);
        XStream xs = new XStream(hierarchicalStreamDriver);
        xs.processAnnotations(ToaXml9109002.class);
        return "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xs.toXML(this);
    }

    public static ToaXml9109002 getToa(String xml) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(ToaXml9109002.class);
        return (ToaXml9109002) xs.fromXML(xml);
    }
}
