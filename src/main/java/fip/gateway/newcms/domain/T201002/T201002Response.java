package fip.gateway.newcms.domain.T201002;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fip.gateway.newcms.domain.common.MsgHeader;
import hfc.common.ConstSqlString;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-10-10
 * Time: 17:18:39
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ROOT")
public class T201002Response extends MsgHeader {
    String stdsqdh; // 申请单号
    String stdsqdzt;  //申请单状态
    String stdkhh;  //客户号
    String stdkhxm;  //客户姓名
    String stdzjlx;  //证件类型
    String stdzjhm;  //证件号码
    String stdhth;  //合同号
    String stdjjh;  //借据号
    String stdfkje;  //放款金额
    String stdfkrq;  //放款日期
    String stddqrq;  //到期日期
    String stddkje;  //贷款金额
    String stdye;  //贷款余额
    String stddkxt;  //贷款形态

    public String getStdsqdh() {
        return stdsqdh;
    }

    public void setStdsqdh(String stdsqdh) {
        this.stdsqdh = stdsqdh;
    }

    public String getStdsqdzt() {
        return stdsqdzt;
    }

    public void setStdsqdzt(String stdsqdzt) {
        this.stdsqdzt = stdsqdzt;
    }

    public String getStdkhh() {
        return stdkhh;
    }

    public void setStdkhh(String stdkhh) {
        this.stdkhh = stdkhh;
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

    public String getStdhth() {
        return stdhth;
    }

    public void setStdhth(String stdhth) {
        this.stdhth = stdhth;
    }

    public String getStdjjh() {
        return stdjjh;
    }

    public void setStdjjh(String stdjjh) {
        this.stdjjh = stdjjh;
    }

    public String getStdfkje() {
        return stdfkje;
    }

    public void setStdfkje(String stdfkje) {
        this.stdfkje = stdfkje;
    }

    public String getStdfkrq() {
        return stdfkrq;
    }

    public void setStdfkrq(String stdfkrq) {
        this.stdfkrq = stdfkrq;
    }

    public String getStddqrq() {
        return stddqrq;
    }

    public void setStddqrq(String stddqrq) {
        this.stddqrq = stddqrq;
    }

    public String getStddkje() {
        return stddkje;
    }

    public void setStddkje(String stddkje) {
        this.stddkje = stddkje;
    }

    public String getStdye() {
        return stdye;
    }

    public void setStdye(String stdye) {
        this.stdye = stdye;
    }

    public String getStddkxt() {
        return stddkxt;
    }

    public void setStddkxt(String stddkxt) {
        this.stddkxt = stddkxt;
    }

    public static void main(String[] args) {
       /* String str = ConstSqlString.TEST_201002_RESPONSE_XML;
        T201002Response res = (T201002Response)T201002Response.toObject(T201002Response.class, str);
        System.out.println(res.getStdsqdzt());*/
    }

}
