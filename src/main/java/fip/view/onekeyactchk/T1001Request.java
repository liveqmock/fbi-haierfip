package fip.view.onekeyactchk;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by zhanrui on 2014/11/27.
 */
@XStreamAlias("ROOT")
public class T1001Request extends HttpXmlMsg{
    private Info INFO = new Info();
    private Body BODY = new Body();

    public Info getINFO() {
        return INFO;
    }

    public void setINFO(Info INFO) {
        this.INFO = INFO;
    }

    public Body getBODY() {
        return BODY;
    }

    public void setBODY(Body BODY) {
        this.BODY = BODY;
    }

    public static class Info {
        private String TXNCODE;
        private String VERSION;
        private String REQSN;

        public String getTXNCODE() {
            return TXNCODE;
        }

        public void setTXNCODE(String TXNCODE) {
            this.TXNCODE = TXNCODE;
        }

        public String getVERSION() {
            return VERSION;
        }

        public void setVERSION(String VERSION) {
            this.VERSION = VERSION;
        }

        public String getREQSN() {
            return REQSN;
        }

        public void setREQSN(String REQSN) {
            this.REQSN = REQSN;
        }
    }

    public static class Body {
        private String TXNDATE;
        private String TXNTIME;
        private String CHNCODE;
        private String ACTION;
        private String REMARK;

        public String getTXNDATE() {
            return TXNDATE;
        }

        public void setTXNDATE(String TXNDATE) {
            this.TXNDATE = TXNDATE;
        }

        public String getTXNTIME() {
            return TXNTIME;
        }

        public void setTXNTIME(String TXNTIME) {
            this.TXNTIME = TXNTIME;
        }

        public String getCHNCODE() {
            return CHNCODE;
        }

        public void setCHNCODE(String CHNCODE) {
            this.CHNCODE = CHNCODE;
        }

        public String getACTION() {
            return ACTION;
        }

        public void setACTION(String ACTION) {
            this.ACTION = ACTION;
        }

        public String getREMARK() {
            return REMARK;
        }

        public void setREMARK(String REMARK) {
            this.REMARK = REMARK;
        }
    }
}