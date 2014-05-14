package org.fbi.dep.model.txn;

import org.fbi.dep.model.base.TOA;
import org.fbi.dep.model.base.TOABody;
import org.fbi.dep.model.base.TOAHeader;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * SBS: 对外支付落地审核明细查询
 * User: zhanrui
 * Date: 2012-02-14
 */
public class TOA900n116 extends TOA implements Serializable {
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
        public String TOTCNT;          //总记录数
        public String CURCNT;          //本包内记录数
        public List<BodyDetail> RET_DETAILS = new ArrayList<BodyDetail>();

        public static class BodyDetail implements Serializable{
            public String FBTIDX;      //流水号
            public String CUSACT;      //付款账号
            public String RETNAM;      //付款户名
            public String BENACT;      //收款账号
            public String BENNAM;      //收款户名
            public String BENBNK;      //收款银行
            public BigDecimal TXNAMT;  //交易金额
            public String RETAUX;      //用途
            public String CTLRSN;      //落地原因
            public String TLRNAM;      //审核柜员名
            public String TERMNM;      //交易渠道
        }
    }
}
