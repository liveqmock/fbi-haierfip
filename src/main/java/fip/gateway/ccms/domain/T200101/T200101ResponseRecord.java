package fip.gateway.ccms.domain.T200101;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 16:40:10
 * To change this template use File | Settings | File Templates.
 */
public class T200101ResponseRecord {
    String   stdjjh;   //借据号
    String   stdjyrq;  //交易日期
    String   stdqch;   //期次号
    String   stdkhh;   //客户号（ID）
    String   stdkhmc;  //客户名称（户名）
//    String   stdkhsfz; //客户身份证号
    String   stddkzh;  //贷款帐号（财务公司SBS帐号）
    String   stdhkje;  //还款金额（还款总金额）
    String   stdhkbj;  //本金
    String   stdhklx;  //利息
    String   stdjhhkr; //计划还款日
    String   stdhth;   //合同号
    String   stdhkzh;  //还款帐号（银行帐号）
    String   stdyhsf;  //开户行所在省（汉字，不含‘省’）
//    String   stdyhcs;  //开户行所在市（汉字，不含‘市’）
    String   stdyhh;   //开户行（3位代号）
    String   stdfxje;  //罚息金额
    String   stdryje;  //冗余金额
    String   stdsqdh;  //申请单号
    String   stdsqlsh; //申请流水号

    public String getStdjjh() {
        return stdjjh;
    }

    public void setStdjjh(String stdjjh) {
        this.stdjjh = stdjjh;
    }

    public String getStdjyrq() {
        return stdjyrq;
    }

    public void setStdjyrq(String stdjyrq) {
        this.stdjyrq = stdjyrq;
    }

    public String getStdqch() {
        return stdqch;
    }

    public void setStdqch(String stdqch) {
        this.stdqch = stdqch;
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

    public String getStdjhhkr() {
        return stdjhhkr;
    }

    public void setStdjhhkr(String stdjhhkr) {
        this.stdjhhkr = stdjhhkr;
    }

    public String getStdhth() {
        return stdhth;
    }

    public void setStdhth(String stdhth) {
        this.stdhth = stdhth;
    }

    public String getStdhkzh() {
        return stdhkzh;
    }

    public void setStdhkzh(String stdhkzh) {
        this.stdhkzh = stdhkzh;
    }

    public String getStdyhsf() {
        return stdyhsf;
    }

    public void setStdyhsf(String stdyhsf) {
        this.stdyhsf = stdyhsf;
    }

    public String getStdyhh() {
        return stdyhh;
    }

    public void setStdyhh(String stdyhh) {
        this.stdyhh = stdyhh;
    }

    public String getStdfxje() {
        return stdfxje;
    }

    public void setStdfxje(String stdfxje) {
        this.stdfxje = stdfxje;
    }

    public String getStdryje() {
        return stdryje;
    }

    public void setStdryje(String stdryje) {
        this.stdryje = stdryje;
    }

    public String getStdsqdh() {
        return stdsqdh;
    }

    public void setStdsqdh(String stdsqdh) {
        this.stdsqdh = stdsqdh;
    }

    public String getStdsqlsh() {
        return stdsqlsh;
    }

    public void setStdsqlsh(String stdsqlsh) {
        this.stdsqlsh = stdsqlsh;
    }
}