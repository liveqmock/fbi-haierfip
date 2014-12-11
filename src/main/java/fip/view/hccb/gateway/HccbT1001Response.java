package fip.view.hccb.gateway;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanrui on 2014/12/10.
 */
@XStreamAlias("root")
public class HccbT1001Response extends HccbMsg {
    public Body body = new Body();

    public static class Body {
        public String pagesum = "0";
        public BodyRecords records = new BodyRecords();

        @Override
        public String toString() {
            return "Body{" +
                    "pagesum='" + pagesum + '\'' +
                    ", records=" + records +
                    '}';
        }
    }

    public static class BodyRecords {
        @XStreamImplicit(itemFieldName = "record")
        public List<HccbBillVO> bills = new ArrayList<HccbBillVO>();

        @Override
        public String toString() {
            return "BodyRecords{" +
                    "bills=" + bills +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HccbT1001Response{" +
                "head=" + head +
                "body=" + body +
                '}';
    }

    public static void main(String argv[]) {
        HccbT1001Response req = new HccbT1001Response();
        HccbBillVO vo = new HccbBillVO();
        req.body.records.bills.add(vo);
        vo = new HccbBillVO();
        req.body.records.bills.add(vo);

        String xml = req.toXml(req);
        System.out.println(xml);
    }
}
