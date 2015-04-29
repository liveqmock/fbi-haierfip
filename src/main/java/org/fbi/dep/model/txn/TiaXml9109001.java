package org.fbi.dep.model.txn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.TiaXmlInfo;

import java.io.Serializable;
import java.util.List;

/**
 * 巨商汇待入账明细
 */

@XStreamAlias("ROOT")
public class TiaXml9109001 extends TiaXml {
    public TiaXmlInfo INFO;
    public Body BODY;

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
        public String REMARK = "";
        public String RESERVE = "";

    }

    @Override
    public TiaXml getTia(String xml) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(TiaXml9109001.class);
        return (TiaXml9109001) xs.fromXML(xml);
    }
}
