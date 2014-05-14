package org.fbi.dep.model.txn;

import org.fbi.dep.model.base.TOA;
import org.fbi.dep.model.base.TOABody;
import org.fbi.dep.model.base.TOAHeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * SBS: 批量代扣结果查询-应答报文
 * User: zhanrui
 * Date: 2012-09-01
 */
public class TOA900n052 extends TOA implements Serializable {
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
        public String SUCCNT;
        public String FALCNT;
        public String SUCAMT;
        public String FALAMT;
        public String FLOFLG; //后续包标志
        public String CURCNT; //当前包笔数
        public String REMARK1;
        public String REMARK2;

        public List<BodyDetail> RET_DETAILS = new ArrayList<BodyDetail>();

        public static class BodyDetail implements Serializable{
            /*
                T541-ACTNUM	账号	X(32)
                T541-ACTNAM	姓名	X(60)
                T541-REASON	失败原因	X(40)
                T541-TXNAMT	交易金额	9(14).99
            */
            public String ACTNUM;
            public String ACTNAM;
            public String REASON;
            public String TXNAMT;
        }
    }
}
