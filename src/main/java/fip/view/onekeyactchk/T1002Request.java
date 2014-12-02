package fip.view.onekeyactchk;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by zhanrui on 2014/11/27.
 */
@XStreamAlias("ROOT")
public class T1002Request extends HttpXmlMsg{
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
        private String CHANNEL;

        public String getCHANNEL() {
            return CHANNEL;
        }

        public void setCHANNEL(String CHANNEL) {
            this.CHANNEL = CHANNEL;
        }
    }
}