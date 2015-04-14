package fip.gateway.hccb.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanrui on 2015/3/31.
 */
@XStreamAlias("root")
public class T1003Request {
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
        return "T1003Request{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }

    //=======

    public static class Body{
        private String totalitems;
        private String totalamt;

        List<Record> records = new ArrayList<Record>();

        public String getTotalitems() {
            return totalitems;
        }

        public void setTotalitems(String totalitems) {
            this.totalitems = totalitems;
        }

        public String getTotalamt() {
            return totalamt;
        }

        public void setTotalamt(String totalamt) {
            this.totalamt = totalamt;
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
                    "totalitems='" + totalitems + '\'' +
                    ", totalamt='" + totalamt + '\'' +
                    ", records=" + records +
                    '}';
        }
    }

    @XStreamAlias("record")
    public static class Record{
        private String iouno = "";
        private String poano = "";
        private String txnamt = "";
        private String schpaydate = "";
        private String resultcode = ""; //1-成功 2-失败  2-不明

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

        public String getResultcode() {
            return resultcode;
        }

        public void setResultcode(String resultcode) {
            this.resultcode = resultcode;
        }

        @Override
        public String toString() {
            return "\n\t\tRecord{" +
                    "iouno='" + iouno + '\'' +
                    ", poano='" + poano + '\'' +
                    ", txnamt='" + txnamt + '\'' +
                    ", schpaydate='" + schpaydate + '\'' +
                    ", resultcode='" + resultcode + '\'' +
                    '}';
        }
    }

}
