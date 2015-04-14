package fip.gateway.hccb.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanrui on 2015/3/31.
 */
@XStreamAlias("root")
public class T1001Response {
    private TxnHead head = new TxnHead();
    private Body body = new Body();

    public TxnHead getHead() {
        return head;
    }

    public void setHead(TxnHead head) {
        this.head = head;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    @Override
    public String toString() {
        return "T1001Response{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }

    //=======

    public static class Body{
        private String pagesum;

        List<Record> records = new ArrayList<Record>();

        public String getPagesum() {
            return pagesum;
        }

        public void setPagesum(String pagesum) {
            this.pagesum = pagesum;
        }

        public List<Record> getRecords() {
            return records;
        }

        public void setRecords(List<Record> records) {
            this.records = records;
        }

        @Override
        public String toString() {
            return "\n\tBody{" +
                    "pagesum='" + pagesum + '\'' +
                    ", records=" + records +
                    '}';
        }
    }
    @XStreamAlias("record")
    public static class Record{
        private String txntype = "";
        private String iouno = "";
        private String poano = "";
        private String cttno = "";
        private String bankcode = "";
        private String actno = "";
        private String actname = "";
        private String custid = "";
        private String certtype = "";
        private String certid = "";
        private String province = "";
        private String city = "";
        private String txnamt = "";
        private String schpaydate = "";
        private String channel = "";

        public String getTxntype() {
            return txntype;
        }

        public void setTxntype(String txntype) {
            this.txntype = txntype;
        }

        public String getIouno() {
            return iouno;
        }

        public void setIouno(String iouno) {
            this.iouno = iouno;
        }

        public String getPoano() {
            return poano;
        }

        public void setPoano(String poano) {
            this.poano = poano;
        }

        public String getCttno() {
            return cttno;
        }

        public void setCttno(String cttno) {
            this.cttno = cttno;
        }

        public String getBankcode() {
            return bankcode;
        }

        public void setBankcode(String bankcode) {
            this.bankcode = bankcode;
        }

        public String getActno() {
            return actno;
        }

        public void setActno(String actno) {
            this.actno = actno;
        }

        public String getActname() {
            return actname;
        }

        public void setActname(String actname) {
            this.actname = actname;
        }

        public String getCustid() {
            return custid;
        }

        public void setCustid(String custid) {
            this.custid = custid;
        }

        public String getCerttype() {
            return certtype;
        }

        public void setCerttype(String certtype) {
            this.certtype = certtype;
        }

        public String getCertid() {
            return certid;
        }

        public void setCertid(String certid) {
            this.certid = certid;
        }

        public String getProvince() {
            return province;
        }

        public void setProvince(String province) {
            this.province = province;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getTxnamt() {
            return txnamt;
        }

        public void setTxnamt(String txnamt) {
            this.txnamt = txnamt;
        }

        public String getSchpaydate() {
            return schpaydate;
        }

        public void setSchpaydate(String schpaydate) {
            this.schpaydate = schpaydate;
        }

        public String getChannel() {
            return channel;
        }

        public void setChannel(String channel) {
            this.channel = channel;
        }

        @Override
        public String toString() {
            return "\n\t\tRecord{" +
                    "txntype='" + txntype + '\'' +
                    ", iouno='" + iouno + '\'' +
                    ", poano='" + poano + '\'' +
                    ", cttno='" + cttno + '\'' +
                    ", bankcode='" + bankcode + '\'' +
                    ", actno='" + actno + '\'' +
                    ", actname='" + actname + '\'' +
                    ", custid='" + custid + '\'' +
                    ", certtype='" + certtype + '\'' +
                    ", certid='" + certid + '\'' +
                    ", province='" + province + '\'' +
                    ", city='" + city + '\'' +
                    ", txnamt='" + txnamt + '\'' +
                    ", schpaydate='" + schpaydate + '\'' +
                    ", channel='" + channel + '\'' +
                    '}';
        }
    }

}
