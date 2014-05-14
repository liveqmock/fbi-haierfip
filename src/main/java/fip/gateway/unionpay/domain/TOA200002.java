package fip.gateway.unionpay.domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import fip.gateway.unionpay.core.TOA;
import fip.gateway.unionpay.core.TOABody;
import fip.gateway.unionpay.core.TOAHeader;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2011-11-14
 * Time: 上午9:27
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("GZELINK")
public class TOA200002 extends TOA {
    public Header INFO = new Header();
    public Body BODY  = new Body();

    @Override
    public TOAHeader getHeader() {
        return INFO;
    }

    @Override
    public TOABody getBody() {
        return BODY;
    }

    public static class Header extends TOAHeader {
    }

    public static class Body extends TOABody{
        public BodyHeader QUERY_TRANS;
        public static class BodyHeader {
            public String QUERY_SN = "";
            public String QUERY_REMARK = "";
            public String PAGE_SUM = "0";  //总页数
        }
        public List<BodyDetail> RET_DETAILS;
        @XStreamAlias("RET_DETAIL")
        public static class BodyDetail {
            public String ORAFILE_ID = "";
            public String SN = "";
            public String ACCOUNT = "";
            public String ACCOUNT_NAME = "";
            public String AMOUNT = "";
            public String CUST_USERID = "";
            public String COMPLETE_TIME = "";        //YYYYMMDDHHMMSS  完成时间 C（14）
            public String REMARK = "";
            public String RET_CODE = "";
            public String ERR_MSG = "";
        }
    }

    @Override
    public String toString() {
        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("$", "_");
        HierarchicalStreamDriver hierarchicalStreamDriver = new XppDriver(replacer);
        XStream xs = new XStream(hierarchicalStreamDriver);
        xs.processAnnotations(TOA200002.class);
        return "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xs.toXML(this);
    }
    public static TOA200002 getToa(String xml){
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(TOA200002.class);
        return (TOA200002)xs.fromXML(xml);
    }
}
