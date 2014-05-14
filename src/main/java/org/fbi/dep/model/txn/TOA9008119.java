package org.fbi.dep.model.txn;

import org.fbi.dep.model.base.TOA;
import org.fbi.dep.model.base.TOABody;
import org.fbi.dep.model.base.TOAHeader;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * SBS: 批量多账户余额查询
 * User: zhanrui
 * Date: 2012-01-31
 */
public class TOA9008119 extends TOA implements Serializable {
    public  Header header = new Header();
    public  Body body = new Body();

    @Override
    public TOAHeader getHeader() {
        return  header;
    }

    @Override
    public TOABody getBody() {
        return  body;
    }

    //====================================================================
    public  static class Header extends TOAHeader {
    }

    public static class Body extends TOABody {
        public List<BodyDetail> RET_DETAILS = new ArrayList<BodyDetail>();

        public static class BodyDetail implements Serializable{
            public String ACTNUM;
            public String ACTNAM;
            public BigDecimal BOKBAL;
            public BigDecimal AVABAL;
            public String FRZSTS;
            public String ACTSTS;
        }
    }
}
