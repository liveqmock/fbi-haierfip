package fip.view.hccb.gateway;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanrui on 2014/12/10.
 * 批量返回结果
 */
@XStreamAlias("root")
public class HccbT1003Request extends HccbMsg {
    public Body body = new Body();

    public static class Body {
        public String totalitems = "0";
        public String totalamt = "0.00";
        public BodyRecords records = new BodyRecords();

        @Override
        public String toString() {
            return "Body{" +
                    "totalitems='" + totalitems + '\'' +
                    ", totalamt='" + totalamt + '\'' +
                    ", records=" + records +
                    '}';
        }
    }

    public static class BodyRecords {
        @XStreamImplicit(itemFieldName = "record")
        public List<HccbResultVO> cutpayResults = new ArrayList<HccbResultVO>();

        @Override
        public String toString() {
            return "BodyRecords{" +
                    "cutpayResults=" + cutpayResults +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "HccbT1003Request{" +
                "head=" + head +
                "body=" + body +
                '}';
    }

}
