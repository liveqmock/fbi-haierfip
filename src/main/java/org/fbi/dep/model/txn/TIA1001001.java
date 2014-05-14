package org.fbi.dep.model.txn;

import org.fbi.dep.model.base.TIA;
import org.fbi.dep.model.base.TIABody;
import org.fbi.dep.model.base.TIAHeader;

/**
 * 银联：代扣
 * User: zhanrui
 * Date: 2012-01-31
 */

public class TIA1001001 extends TIA {
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
        public String BANK_CODE;                 // 银行代号
        public String ACCOUNT_TYPE;              // 账号类型 00-银行卡，01-存折
        public String ACCOUNT_NO;                // 账号
        public String ACCOUNT_NAME;              // 账户名

        public String AMOUNT;                    // 交易金额(小数点后保留两位)
        public String PROVINCE;                  // 开户行所在省
        public String CITY;                      // 开户行所在市
        public String ACCOUNT_PROP;              // 账号属性  0-个人 1-公司
        public String REMARK;                    // 备注
    }
}
