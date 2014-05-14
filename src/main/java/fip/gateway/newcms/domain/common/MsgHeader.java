package fip.gateway.newcms.domain.common;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:24:32
 * To change this template use File | Settings | File Templates.
 */

/*
    名称	标签名	长度	说明
    报文类型	stdmsgtype             	4
        0100 查询类请求
        0200 交易类请求
    交易码	std400trcd	6	发起方交易码
        发起方必填
        100101-还款记录查询
        100102—还款结果返回
    处理代码	stdprocode	6	不填写
    发起方标志	std400aqid	1	1-网银
    2-消费信贷
    3-分期扣款
    商户编号	stdmercno	15	不填写
    终端类型	stdtermtyp	1	不填写
    终端号 	stdtermid	8	不填写
    交易柜员	std400tlno	10	发起方柜员号
    帐号1	stdpriacno	19	不填写
    密码	stdpindata	16	不填写
    发起方日期	stdlocdate	8	交易日期
    发起方时间	stdloctime	6	交易时间
    前台流水号	stdtermtrc	6	发送发填写，当天不重复
    授权柜员	std400autl	10	不填写
    授权号	stdauthid	6	不填写
    授权柜员密码	std400aups	16	不填写
    交易日期	std400trdt	8	不填写
    交换中心流水号	stdrefnum	12	0
    结算日期                	stdsetdate	8	不填写
    柜员交易号                	std400trno	14	不填写
    信息标识	std400mgid	7	应答填写
    交易成功—“AAAAAAA”
    重复次数	std400acur	2	00

 */
public class MsgHeader extends BaseBean{
    String stdmsgtype = "";
    String std400trcd = "";
    String stdprocode = "";
    String std400aqid = "";
    String stdmercno = "";
    String stdtermtyp = "";
    String stdtermid = "";
    String std400tlno = "";
    String stdpriacno = "";
    String stdpindata = "";
    String stdlocdate = "";
    String stdloctime = "";
    String stdtermtrc = "";
    String std400autl = "";
    String stdauthid = "";
    String std400aups = "";
    String std400trdt = "";
    String stdrefnum = "0";
    String stdsetdate = "";
    String std400trno = "";
    String std400mgid = "";
    String std400acur = "00";

    /**
     * 初始化报文头信息
     * @param txntype  交易类型
     * @param txncode  交易代码
     * @param txnsrc  发起方
     */
    public void initHeader(String txntype,String txncode,String txnsrc){
        this.stdmsgtype = txntype;
        this.std400trcd = txncode;
        this.std400aqid = txnsrc;
        this.std400tlno = "";
        this.stdlocdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        this.stdloctime = new SimpleDateFormat("hhmmss").format(new Date());

        //TODO 前台流水号
        this.stdtermtrc = stdloctime;
    }
    public String getStdmsgtype() {
        return stdmsgtype;
    }

    public void setStdmsgtype(String stdmsgtype) {
        this.stdmsgtype = stdmsgtype;
    }

    public String getStd400trcd() {
        return std400trcd;
    }

    public void setStd400trcd(String std400trcd) {
        this.std400trcd = std400trcd;
    }

    public String getStdprocode() {
        return stdprocode;
    }

    public void setStdprocode(String stdprocode) {
        this.stdprocode = stdprocode;
    }

    public String getStd400aqid() {
        return std400aqid;
    }

    public void setStd400aqid(String std400aqid) {
        this.std400aqid = std400aqid;
    }

    public String getStdmercno() {
        return stdmercno;
    }

    public void setStdmercno(String stdmercno) {
        this.stdmercno = stdmercno;
    }

    public String getStdtermtyp() {
        return stdtermtyp;
    }

    public void setStdtermtyp(String stdtermtyp) {
        this.stdtermtyp = stdtermtyp;
    }

    public String getStdtermid() {
        return stdtermid;
    }

    public void setStdtermid(String stdtermid) {
        this.stdtermid = stdtermid;
    }

    public String getStd400tlno() {
        return std400tlno;
    }

    public void setStd400tlno(String std400tlno) {
        this.std400tlno = std400tlno;
    }

    public String getStdpriacno() {
        return stdpriacno;
    }

    public void setStdpriacno(String stdpriacno) {
        this.stdpriacno = stdpriacno;
    }

    public String getStdpindata() {
        return stdpindata;
    }

    public void setStdpindata(String stdpindata) {
        this.stdpindata = stdpindata;
    }

    public String getStdlocdate() {
        return stdlocdate;
    }

    public void setStdlocdate(String stdlocdate) {
        this.stdlocdate = stdlocdate;
    }

    public String getStdloctime() {
        return stdloctime;
    }

    public void setStdloctime(String stdloctime) {
        this.stdloctime = stdloctime;
    }

    public String getStdtermtrc() {
        return stdtermtrc;
    }

    public void setStdtermtrc(String stdtermtrc) {
        this.stdtermtrc = stdtermtrc;
    }

    public String getStd400autl() {
        return std400autl;
    }

    public void setStd400autl(String std400autl) {
        this.std400autl = std400autl;
    }

    public String getStdauthid() {
        return stdauthid;
    }

    public void setStdauthid(String stdauthid) {
        this.stdauthid = stdauthid;
    }

    public String getStd400aups() {
        return std400aups;
    }

    public void setStd400aups(String std400aups) {
        this.std400aups = std400aups;
    }

    public String getStd400trdt() {
        return std400trdt;
    }

    public void setStd400trdt(String std400trdt) {
        this.std400trdt = std400trdt;
    }

    public String getStdrefnum() {
        return stdrefnum;
    }

    public void setStdrefnum(String stdrefnum) {
        this.stdrefnum = stdrefnum;
    }

    public String getStdsetdate() {
        return stdsetdate;
    }

    public void setStdsetdate(String stdsetdate) {
        this.stdsetdate = stdsetdate;
    }

    public String getStd400trno() {
        return std400trno;
    }

    public void setStd400trno(String std400trno) {
        this.std400trno = std400trno;
    }

    public String getStd400mgid() {
        return std400mgid;
    }

    public void setStd400mgid(String std400mgid) {
        this.std400mgid = std400mgid;
    }

    public String getStd400acur() {
        return std400acur;
    }

    public void setStd400acur(String std400acur) {
        this.std400acur = std400acur;
    }
}
