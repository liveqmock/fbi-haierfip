package fip.gateway.hccb.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Created by zhanrui on 2015/3/31.
 */
@XStreamAlias("root")
public class T1001Request {
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
        return "T1001Request{" +
                "head=" + head +
                ", body=" + body +
                '}';
    }

    //=======
    public static class Body{
        private String qrytype;
        private String pagenum;
        private String pagesize;

        public String getQrytype() {
            return qrytype;
        }

        public void setQrytype(String qrytype) {
            this.qrytype = qrytype;
        }

        public String getPagenum() {
            return pagenum;
        }

        public void setPagenum(String pagenum) {
            this.pagenum = pagenum;
        }

        public String getPagesize() {
            return pagesize;
        }

        public void setPagesize(String pagesize) {
            this.pagesize = pagesize;
        }

        @Override
        public String toString() {
            return "Body{" +
                    "qrytype='" + qrytype + '\'' +
                    ", pagenum='" + pagenum + '\'' +
                    ", pagesize='" + pagesize + '\'' +
                    '}';
        }
    }
}
