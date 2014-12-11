package fip.view.hccb.gateway;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zhanrui on 2014/12/10.
 */
@XStreamAlias("root")
public class HccbT1001Request extends HccbMsg {
    public Body body = new Body();

    public static class Body {
        public String qrytype = "";
        public String pagenum = "";
        public String pagesize = "";

        @Override
        public String toString() {
            return "Body{" +
                    "qrytype='" + qrytype + '\'' +
                    ", pagenum='" + pagenum + '\'' +
                    ", pagesize='" + pagesize + '\'' +
                    '}';
        }
    }

    public HccbT1001Request() {
        head.setMsgtype("0101");
        head.setTxncode("1001");
        head.setTxnsn(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
    }


    @Override
    public String toString() {
        return "HccbT1001Request{" +
                "head=" + head +
                "body=" + body +
                '}';
    }


    //===============
    public static void main(String argv[]) {
        HccbT1001Request req = new HccbT1001Request();
        req.body.qrytype="1";
        String xml = req.toXml(req);
        System.out.println(xml);

        req = (HccbT1001Request)req.toBean(xml);
        System.out.println(req);
    }
}
