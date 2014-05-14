package org.fbi.dep.model.txn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.TiaXmlHeader;

import java.io.Serializable;

/**
 * 银联：查询交易结果
 * User: zhangxiaobo
 * Date: 2013-04-01
 */

@XStreamAlias("GZELINK")
public class TiaXml1003001 extends TiaXml {
    public TiaXmlHeader INFO;
    public Body BODY;

    public static class Body implements Serializable {
        public BodyHeader QUERY_TRANS;

        public static class BodyHeader implements Serializable {
            public String QUERY_SN = "";
            public String QUERY_REMARK = "";
        }
    }

    @Override
    public TiaXml getTia(String xml) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(TiaXml1003001.class);
        return (TiaXml1003001) xs.fromXML(xml);
    }
}
