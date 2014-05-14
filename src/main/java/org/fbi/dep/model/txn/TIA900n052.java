package org.fbi.dep.model.txn;

import org.fbi.dep.model.base.TIA;
import org.fbi.dep.model.base.TIABody;
import org.fbi.dep.model.base.TIAHeader;

import java.io.Serializable;

/**
 * SBS 批量代扣结果查询-请求报文
 * User: zhanrui
 * Date: 2012-09-01
 */

public class TIA900n052 extends TIA implements Serializable {
    public  Header header = new Header();
    public  Body body = new Body();

    @Override
    public TIAHeader getHeader() {
        return  header;
    }

    @Override
    public TIABody getBody() {
        return  body;
    }

    //====================================================================
    public  static class Header extends TIAHeader {
    }

    public static class Body extends TIABody {
        /*
        CTF-FBTIDX	外围系统流水号	X(18)	左对齐，不足时右补空格
        CTF-ADVNUM	原请求序列号	9(16)
        CTF-ORDDAT	交易日期	X(8)
        CTF-BEGNUM	起始笔数	9(6)	右对齐，左补0
        */
        public String FBTIDX;
        public String ADVNUM;
        public String ORDDAT;
        public String BEGNUM = "000001";
    }

}
