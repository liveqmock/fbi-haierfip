package fip.gateway.unionpay.domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import fip.gateway.unionpay.core.TIA;
import fip.gateway.unionpay.core.TIABody;
import fip.gateway.unionpay.core.TIAHeader;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 */
@XStreamAlias("GZELINK")
public class TIA200001 extends TIA {
    public static class Header extends TIAHeader {
    }

    public static class Body extends TIABody{
        public BodyHeader QUERY_TRANS = new BodyHeader();
        public static class BodyHeader {
            public String QUERY_SN = "";
            public String QUERY_REMARK = "";
        }
    }

    public Header INFO = new Header();
    public Body BODY = new Body();

    @Override
    public TIAHeader getHeader() {
        return INFO;
    }

    @Override
    public TIABody getBody() {
        return BODY;
    }

    public TIA200001(Map paramMap){
        String reqSN = (String)paramMap.get("reqSN");
        assembleTIA(reqSN);
    }

    @Override
    public String toString() {
        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("$", "_");
        HierarchicalStreamDriver hierarchicalStreamDriver = new XppDriver(replacer);
        XStream xs = new XStream(hierarchicalStreamDriver);
        xs.processAnnotations(TIA200001.class);
        return "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xs.toXML(this);
    }

    public  String assembleTIA(String reqSN) {
        this.INFO.TRX_CODE = "200001";
        this.INFO.REQ_SN = System.currentTimeMillis() + "";

        this.INFO.VERSION = "03";

        // TODO
        this.BODY.QUERY_TRANS.QUERY_REMARK = "HAIER_FIP" + this.INFO.REQ_SN;
        this.BODY.QUERY_TRANS.QUERY_SN = reqSN;

        return this.toString();
    }
}
