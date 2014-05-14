package org.fbi.dep.model.txn;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.fbi.dep.model.base.TOA;
import org.fbi.dep.model.base.TOABody;
import org.fbi.dep.model.base.TOAHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ÒøÁª£º´ú¸¶
 */
public class TOA1002001 extends TOA implements Serializable {
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
        public List<BodyDetail> RET_DETAILS = new ArrayList<BodyDetail>();

        @XStreamAlias("RET_DETAIL")
        public static class BodyDetail implements Serializable {
            public String SN = "";
            public String RET_CODE = "";
            public String ERR_MSG = "";
        }
    }
}
