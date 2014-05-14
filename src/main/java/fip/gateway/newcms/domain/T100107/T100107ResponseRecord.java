package fip.gateway.newcms.domain.T100107;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2012-8-27
 * Time: 16:40:10
 * To change this template use File | Settings | File Templates.
 */
public class T100107ResponseRecord {
    String   stdjjh;  //借据号
    String   stdhkzh; //还款帐号
    String   stddqh;  //地区号
    String   stdyhh;  //银行号

    String   stdhth;    //合同号
    String   stdkhh;    //客户号
    String   stdkhmc;   //客户名称
    String   stddkzh;   //贷款账号

    String   stdhkje;   //还款金额(合计)

    String   stdfxje;   //罚息金额(合计)
    String   stdhkbj;   //还款本金(合计)
    String   stdhklx;   //还款利息(合计)
    String   stdryje;   //冗余金额-利息复利(合计)

    String   stdjjzt;   //借据状态 0-已加锁1- 未加锁

    String   stdckr;  //持卡人名称  20121204

    public String getStdjjh() {
        return stdjjh;
    }

    public void setStdjjh(String stdjjh) {
        this.stdjjh = stdjjh;
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

    public String getStdhth() {
        return stdhth;
    }

    public void setStdhth(String stdhth) {
        this.stdhth = stdhth;
    }

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

    public String getStdfxje() {
        return stdfxje;
    }

    public void setStdfxje(String stdfxje) {
        this.stdfxje = stdfxje;
    }

    public String getStdhkbj() {
        return stdhkbj;
    }

    public void setStdhkbj(String stdhkbj) {
        this.stdhkbj = stdhkbj;
    }

    public String getStdhklx() {
        return stdhklx;
    }

    public void setStdhklx(String stdhklx) {
        this.stdhklx = stdhklx;
    }

    public String getStdryje() {
        return stdryje;
    }

    public void setStdryje(String stdryje) {
        this.stdryje = stdryje;
    }

    public String getStdjjzt() {
        return stdjjzt;
    }

    public void setStdjjzt(String stdjjzt) {
        this.stdjjzt = stdjjzt;
    }

    public String getStdckr() {
        return stdckr;
    }

    public void setStdckr(String stdckr) {
        this.stdckr = stdckr;
    }
}
