package org.fbi.dep.model.txn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.fbi.dep.model.base.ToaXml;
import org.fbi.dep.model.base.ToaXmlHeader;

import java.io.Serializable;
import java.util.List;

/*
银联：查询交易结果
 */
@XStreamAlias("GZELINK")
public class ToaXml1003001 extends ToaXml {
    public static class Body  implements Serializable {
        public BodyHeader QUERY_TRANS = new BodyHeader();

        public static class BodyHeader  implements Serializable {
            public String QUERY_SN = "";
            public String QUERY_REMARK = "";
        }

        public List<BodyDetail> RET_DETAILS;

        @XStreamAlias("RET_DETAIL")
        public static class BodyDetail implements Serializable {
            public String SN = "";
            public String ACCOUNT_NO = "";
            public String ACCOUNT_NAME = "";
            public String AMOUNT = "";
            public String REMARK = "";
            public String RET_CODE = "";
            public String ERR_MSG = "";

            public String getSN() {
                return SN;
            }

            public void setSN(String SN) {
                this.SN = SN;
            }

            public String getACCOUNT_NO() {
                return ACCOUNT_NO;
            }

            public void setACCOUNT_NO(String ACCOUNT_NO) {
                this.ACCOUNT_NO = ACCOUNT_NO;
            }

            public String getACCOUNT_NAME() {
                return ACCOUNT_NAME;
            }

            public void setACCOUNT_NAME(String ACCOUNT_NAME) {
                this.ACCOUNT_NAME = ACCOUNT_NAME;
            }

            public String getAMOUNT() {
                return AMOUNT;
            }

            public void setAMOUNT(String AMOUNT) {
                this.AMOUNT = AMOUNT;
            }

            public String getREMARK() {
                return REMARK;
            }

            public void setREMARK(String REMARK) {
                this.REMARK = REMARK;
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

    public ToaXmlHeader INFO;
    public Body BODY;

    @Override
    public String toString() {
        this.INFO.TRX_CODE = "1003001";
        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("$", "_");
        HierarchicalStreamDriver hierarchicalStreamDriver = new XppDriver(replacer);
        XStream xs = new XStream(hierarchicalStreamDriver);
        xs.processAnnotations(ToaXml1003001.class);
        return "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xs.toXML(this);
    }
}
