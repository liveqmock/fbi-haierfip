package org.fbi.dep.model.txn;

import org.fbi.dep.model.base.TIA;
import org.fbi.dep.model.base.TIABody;
import org.fbi.dep.model.base.TIAHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 银联：代付
 */

public class TIA1002001 extends TIA {
    public Header header = new Header();
    public Body body = new Body();


    @Override
    public TIAHeader getHeader() {
        return header;
    }

    @Override
    public TIABody getBody() {
        return body;
    }

    //====================================================================
    public static class Header extends TIAHeader {
    }

    public static class Body extends TIABody {
        public BodyHeader TRANS_SUM;
        public List<BodyDetail> TRANS_DETAILS = new ArrayList<BodyDetail>();

        public static class BodyHeader implements Serializable {
            public String TOTAL_ITEM = "1";
            public String TOTAL_SUM = "000";
        }

        public static class BodyDetail implements Serializable {
            public String SN = "";
            public String BANK_CODE = "";
            public String ACCOUNT_TYPE = ""; //
            public String ACCOUNT_NO = "";
            public String ACCOUNT_NAME = "";
            public String PROVINCE = "";
            public String CITY = "";
            public String ACCOUNT_PROP = "0";   //0私人，1公司。不填时，默认为私人0。
            public String AMOUNT = "";   //整数，单位分
            public String REMARK = "";
            public String RESERVE1 = "";
        }
    }
}
