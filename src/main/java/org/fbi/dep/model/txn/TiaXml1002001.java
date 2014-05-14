package org.fbi.dep.model.txn;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.TiaXmlHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 银联：代付
 * User: zhangxiaobo
 * Date: 2013-04-01
 */

@XStreamAlias("GZELINK")
public class TiaXml1002001 extends TiaXml {
    public TiaXmlHeader INFO;
    public Body BODY;

    public static class Body implements Serializable {
        public BodyHeader TRANS_SUM;
        public List<BodyDetail> TRANS_DETAILS = new ArrayList<BodyDetail>();

        public static class BodyHeader implements Serializable {
            public String TOTAL_ITEM = "1";
            public String TOTAL_SUM = "0";
        }

        @XStreamAlias("TRANS_DETAIL")
        public static class BodyDetail implements Serializable {
            public String SN = "0001";
            public String BANK_CODE = "";
            public String ACCOUNT_TYPE = ""; //
            public String ACCOUNT_NO = "";
            public String ACCOUNT_NAME = "";
            public String PROVINCE = "";
            public String CITY = "";
            public String ACCOUNT_PROP = "0";   //0私人，1公司。不填时，默认为私人0。
            public String AMOUNT = "";   //整数，单位分
            public String SBS_ACCOUNT_NO = "";   // SBS结算账户
            public String REMARK = "";
            public String RESERVE1 = "";

            public String getSN() {
                return SN;
            }

            public void setSN(String SN) {
                this.SN = SN;
            }

            public String getBANK_CODE() {
                return BANK_CODE;
            }

            public void setBANK_CODE(String BANK_CODE) {
                this.BANK_CODE = BANK_CODE;
            }

            public String getACCOUNT_TYPE() {
                return ACCOUNT_TYPE;
            }

            public void setACCOUNT_TYPE(String ACCOUNT_TYPE) {
                this.ACCOUNT_TYPE = ACCOUNT_TYPE;
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

            public String getPROVINCE() {
                return PROVINCE;
            }

            public void setPROVINCE(String PROVINCE) {
                this.PROVINCE = PROVINCE;
            }

            public String getCITY() {
                return CITY;
            }

            public void setCITY(String CITY) {
                this.CITY = CITY;
            }

            public String getACCOUNT_PROP() {
                return ACCOUNT_PROP;
            }

            public void setACCOUNT_PROP(String ACCOUNT_PROP) {
                this.ACCOUNT_PROP = ACCOUNT_PROP;
            }

            public String getAMOUNT() {
                return AMOUNT;
            }

            public void setAMOUNT(String AMOUNT) {
                this.AMOUNT = AMOUNT;
            }

            public String getSBS_ACCOUNT_NO() {
                return SBS_ACCOUNT_NO;
            }

            public void setSBS_ACCOUNT_NO(String SBS_ACCOUNT_NO) {
                this.SBS_ACCOUNT_NO = SBS_ACCOUNT_NO;
            }

            public String getREMARK() {
                return REMARK;
            }

            public void setREMARK(String REMARK) {
                this.REMARK = REMARK;
            }

            public String getRESERVE1() {
                return RESERVE1;
            }

            public void setRESERVE1(String RESERVE1) {
                this.RESERVE1 = RESERVE1;
            }
        }
    }

    @Override
    public String toString() {
        this.INFO.TRX_CODE = "1002001";
        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("$", "_");
        HierarchicalStreamDriver hierarchicalStreamDriver = new XppDriver(replacer);
        XStream xs = new XStream(hierarchicalStreamDriver);
        xs.processAnnotations(TiaXml1002001.class);
        return "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xs.toXML(this);
    }

    @Override
    public TiaXml getTia(String xml) {
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(TiaXml1002001.class);
        return (TiaXml1002001) xs.fromXML(xml);
    }
}
