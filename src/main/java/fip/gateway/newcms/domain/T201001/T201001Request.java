package fip.gateway.newcms.domain.T201001;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import fip.gateway.newcms.domain.common.BaseBean;
import fip.gateway.newcms.domain.common.MsgHeader;

/**
 * 消费信贷申请资料上传
 * User: zhanrui
 * Date: 2010-10-10
 * Time: 17:18:39
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ROOT")
public class T201001Request extends MsgHeader {
    String stdsqdh;  //申请单号
    String stdsqlsh; // 申请流水号
    String stdurl;  //文件URL
    String stdkhxm;  //客户姓名
    String stdzjlx;  //证件类型
    String stdzjhm;  //证件号码
    String stdlxfs;  //联系方式
    String stdxb;  //性别
    String stdcsrq;  //出生日期
    String stdgj;  //国籍
    String stdkhxz;  //客户性质
    String stdhyzk;  //婚姻状况
    String stdjycd;  //教育程度
    String stdkhly;  //客户来源
    String stdhjszd;  //户籍所在地
    String stdhkxz;  //户口性质
    String stdrhzxqk;  //人行征信情况
    String stdfdyqqs;  //征信记录中零售房贷最高逾期期数
    String stdffdyqqs;  //征信记录中零售非房贷最高逾期期数
    String stdjtdz;  //家庭地址
    String stdjtdzdh;  //家庭地址电话
    String stdzzxz;  //住宅性质
    String stdzy;  //职业
    String stdzw;  //职务
    String stdzc;  //职称
    String stdgzdw;  //工作单位
    String stdsshy;  //所属行业
    String stdszqyrs;  //所在企业人数
    String stdgznx;  //目前工作持续年限
    String stdlxr;  //联系人
    // 2011-8-12 stdlxdh  --> stdlxrdh
    String stdlxrdh;  //联系电话
    String stdgrysr;  //个人月收入
    String stdjtwdsr;  //家庭稳定收入
    String stdzwzc;  //每月其他债务支出
    //2011-8-12 新增
    String stddkzje; // 贷款总金额
    String stddkqx;//贷款期限
    String  stdftfs;// 分摊方式
    String stdkhsxfl;//客户手续费率(月利率带千分号)
    String stdshsxfl;// 商户手续费率(月利率带百分号)
    String stdhkr;// 还款日
    String stdkhjl;// 客户经理
    String stdspmc;// 商品名称
    String stdspxh;// 商品型号
    String stdspdj; // 商品单价
    String stdspsl;// 商品数量
    String stdsfk; // 首付款
    String stdhkzh; // 还款账号

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

    public String getStdurl() {
        return stdurl;
    }

    public void setStdurl(String stdurl) {
        this.stdurl = stdurl;
    }

    public String getStdkhxm() {
        return stdkhxm;
    }

    public void setStdkhxm(String stdkhxm) {
        this.stdkhxm = stdkhxm;
    }

    public String getStdzjlx() {
        return stdzjlx;
    }

    public void setStdzjlx(String stdzjlx) {
        this.stdzjlx = stdzjlx;
    }

    public String getStdzjhm() {
        return stdzjhm;
    }

    public void setStdzjhm(String stdzjhm) {
        this.stdzjhm = stdzjhm;
    }

    public String getStdlxfs() {
        return stdlxfs;
    }

    public void setStdlxfs(String stdlxfs) {
        this.stdlxfs = stdlxfs;
    }

    public String getStdxb() {
        return stdxb;
    }

    public void setStdxb(String stdxb) {
        this.stdxb = stdxb;
    }

    public String getStdcsrq() {
        return stdcsrq;
    }

    public void setStdcsrq(String stdcsrq) {
        this.stdcsrq = stdcsrq;
    }

    public String getStdgj() {
        return stdgj;
    }

    public void setStdgj(String stdgj) {
        this.stdgj = stdgj;
    }

    public String getStdkhxz() {
        return stdkhxz;
    }

    public void setStdkhxz(String stdkhxz) {
        this.stdkhxz = stdkhxz;
    }

    public String getStdhyzk() {
        return stdhyzk;
    }

    public void setStdhyzk(String stdhyzk) {
        this.stdhyzk = stdhyzk;
    }

    public String getStdjycd() {
        return stdjycd;
    }

    public void setStdjycd(String stdjycd) {
        this.stdjycd = stdjycd;
    }

    public String getStdkhly() {
        return stdkhly;
    }

    public void setStdkhly(String stdkhly) {
        this.stdkhly = stdkhly;
    }

    public String getStdhjszd() {
        return stdhjszd;
    }

    public void setStdhjszd(String stdhjszd) {
        this.stdhjszd = stdhjszd;
    }

    public String getStdhkxz() {
        return stdhkxz;
    }

    public void setStdhkxz(String stdhkxz) {
        this.stdhkxz = stdhkxz;
    }

    public String getStdrhzxqk() {
        return stdrhzxqk;
    }

    public void setStdrhzxqk(String stdrhzxqk) {
        this.stdrhzxqk = stdrhzxqk;
    }

    public String getStdfdyqqs() {
        return stdfdyqqs;
    }

    public void setStdfdyqqs(String stdfdyqqs) {
        this.stdfdyqqs = stdfdyqqs;
    }

    public String getStdffdyqqs() {
        return stdffdyqqs;
    }

    public void setStdffdyqqs(String stdffdyqqs) {
        this.stdffdyqqs = stdffdyqqs;
    }

    public String getStdjtdz() {
        return stdjtdz;
    }

    public void setStdjtdz(String stdjtdz) {
        this.stdjtdz = stdjtdz;
    }

    public String getStdjtdzdh() {
        return stdjtdzdh;
    }

    public void setStdjtdzdh(String stdjtdzdh) {
        this.stdjtdzdh = stdjtdzdh;
    }

    public String getStdzzxz() {
        return stdzzxz;
    }

    public void setStdzzxz(String stdzzxz) {
        this.stdzzxz = stdzzxz;
    }

    public String getStdzy() {
        return stdzy;
    }

    public void setStdzy(String stdzy) {
        this.stdzy = stdzy;
    }

    public String getStdzw() {
        return stdzw;
    }

    public void setStdzw(String stdzw) {
        this.stdzw = stdzw;
    }

    public String getStdzc() {
        return stdzc;
    }

    public void setStdzc(String stdzc) {
        this.stdzc = stdzc;
    }

    public String getStdgzdw() {
        return stdgzdw;
    }

    public void setStdgzdw(String stdgzdw) {
        this.stdgzdw = stdgzdw;
    }

    public String getStdsshy() {
        return stdsshy;
    }

    public void setStdsshy(String stdsshy) {
        this.stdsshy = stdsshy;
    }

    public String getStdszqyrs() {
        return stdszqyrs;
    }

    public void setStdszqyrs(String stdszqyrs) {
        this.stdszqyrs = stdszqyrs;
    }

    public String getStdgznx() {
        return stdgznx;
    }

    public void setStdgznx(String stdgznx) {
        this.stdgznx = stdgznx;
    }

    public String getStdlxr() {
        return stdlxr;
    }

    public void setStdlxr(String stdlxr) {
        this.stdlxr = stdlxr;
    }

    public String getStdgrysr() {
        return stdgrysr;
    }

    public void setStdgrysr(String stdgrysr) {
        this.stdgrysr = stdgrysr;
    }

    public String getStdjtwdsr() {
        return stdjtwdsr;
    }

    public void setStdjtwdsr(String stdjtwdsr) {
        this.stdjtwdsr = stdjtwdsr;
    }

    public String getStdzwzc() {
        return stdzwzc;
    }

    public void setStdzwzc(String stdzwzc) {
        this.stdzwzc = stdzwzc;
    }

    public String getStdlxrdh() {
        return stdlxrdh;
    }

    public void setStdlxrdh(String stdlxrdh) {
        this.stdlxrdh = stdlxrdh;
    }

    public String getStddkzje() {
        return stddkzje;
    }

    public void setStddkzje(String stddkzje) {
        this.stddkzje = stddkzje;
    }

    public String getStddkqx() {
        return stddkqx;
    }

    public void setStddkqx(String stddkqx) {
        this.stddkqx = stddkqx;
    }

    public String getStdftfs() {
        return stdftfs;
    }

    public void setStdftfs(String stdftfs) {
        this.stdftfs = stdftfs;
    }

    public String getStdkhsxfl() {
        return stdkhsxfl;
    }

    public void setStdkhsxfl(String stdkhsxfl) {
        this.stdkhsxfl = stdkhsxfl;
    }

    public String getStdshsxfl() {
        return stdshsxfl;
    }

    public void setStdshsxfl(String stdshsxfl) {
        this.stdshsxfl = stdshsxfl;
    }

    public String getStdhkr() {
        return stdhkr;
    }

    public void setStdhkr(String stdhkr) {
        this.stdhkr = stdhkr;
    }

    public String getStdkhjl() {
        return stdkhjl;
    }

    public void setStdkhjl(String stdkhjl) {
        this.stdkhjl = stdkhjl;
    }

    public String getStdspmc() {
        return stdspmc;
    }

    public void setStdspmc(String stdspmc) {
        this.stdspmc = stdspmc;
    }

    public String getStdspxh() {
        return stdspxh;
    }

    public void setStdspxh(String stdspxh) {
        this.stdspxh = stdspxh;
    }

    public String getStdspdj() {
        return stdspdj;
    }

    public void setStdspdj(String stdspdj) {
        this.stdspdj = stdspdj;
    }

    public String getStdspsl() {
        return stdspsl;
    }

    public void setStdspsl(String stdspsl) {
        this.stdspsl = stdspsl;
    }

    public String getStdsfk() {
        return stdsfk;
    }

    public void setStdsfk(String stdsfk) {
        this.stdsfk = stdsfk;
    }

    public String getStdhkzh() {
        return stdhkzh;
    }

    public void setStdhkzh(String stdhkzh) {
        this.stdhkzh = stdhkzh;
    }
}
