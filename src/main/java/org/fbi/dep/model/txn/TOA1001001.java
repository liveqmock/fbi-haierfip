package org.fbi.dep.model.txn;

import org.fbi.dep.model.base.TOA;
import org.fbi.dep.model.base.TOABody;
import org.fbi.dep.model.base.TOAHeader;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * ÒøÁª£º´ú¿Û
 * User: zhanrui
 * Date: 2012-01-31
 */
public class TOA1001001 extends TOA implements Serializable {
    public Header header = new Header();
    public Body body = new Body();

    @Override
    public TOAHeader getHeader() {
        return header;
    }

    @Override
    public TOABody getBody() {
        return body;
    }

    //====================================================================
    public static class Header extends TOAHeader {
    }

    public static class Body extends TOABody {
        public String ACCOUNT_NO;
        public String ACCOUNT_NAME;
        public BigDecimal AMOUNT;
        public String REMARK;
    }
}
