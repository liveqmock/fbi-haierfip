package fip.gateway.newcms.domain.T100109;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2015-4
 * 华腾孙晓建：客户号+计划还款日做主键
 */

public class T100109ResponseRecord {
    String   stdkhh;    //客户号
    String   stdkhmc;  //客户名称
    String   stddkzh;   //结算户账号
    String   stdhkje;   //还款金额
    String   stdjhhkr;  //计划还款日 YYYY-MM-DD
    String   stdhkzh; //还款帐号 (银行账号)
    String   stddqh;  //地区号
    String   stdyhh;  //银行号
    String   stdzjh;  //证件号

    public String getStdkhh() {
        return stdkhh;
    }

    public void setStdkhh(String stdkhh) {
        this.stdkhh = stdkhh;
    }

    public String getStdkhmc() {
        return stdkhmc;
    }

    public void setStdkhmc(String stdkhmc) {
        this.stdkhmc = stdkhmc;
    }

    public String getStddkzh() {
        return stddkzh;
    }

    public void setStddkzh(String stddkzh) {
        this.stddkzh = stddkzh;
    }

    public String getStdhkje() {
        return stdhkje;
    }

    public void setStdhkje(String stdhkje) {
        this.stdhkje = stdhkje;
    }

    public String getStdjhhkr() {
        return stdjhhkr;
    }

    public void setStdjhhkr(String stdjhhkr) {
        this.stdjhhkr = stdjhhkr;
    }

    public String getStdhkzh() {
        return stdhkzh;
    }

    public void setStdhkzh(String stdhkzh) {
        this.stdhkzh = stdhkzh;
    }

    public String getStddqh() {
        return stddqh;
    }

    public void setStddqh(String stddqh) {
        this.stddqh = stddqh;
    }

    public String getStdyhh() {
        return stdyhh;
    }

    public void setStdyhh(String stdyhh) {
        this.stdyhh = stdyhh;
    }

    public String getStdzjh() {
        return stdzjh;
    }

    public void setStdzjh(String stdzjh) {
        this.stdzjh = stdzjh;
    }
}
