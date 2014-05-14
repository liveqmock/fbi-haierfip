package fip.gateway.newcms.domain.T201003;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fip.gateway.newcms.domain.common.MsgHeader;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-10-10
 * Time: 17:18:39
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ROOT")
public class T201003Response extends MsgHeader {
    String stdshmc; // 商户名称
    String stdkkzh; // 扣款账号
    String stdcwxx;  //错误信息    1-无查询的申请单号； 2-当前申请电话未维护对应扣款账号


    public String getStdkkzh() {
        return stdkkzh;
    }

    public void setStdkkzh(String stdkkzh) {
        this.stdkkzh = stdkkzh;
    }

    public String getStdcwxx() {
        return stdcwxx;
    }

    public void setStdcwxx(String stdcwxx) {
        this.stdcwxx = stdcwxx;
    }

    public String getStdshmc() {
        return stdshmc;
    }

    public void setStdshmc(String stdshmc) {
        this.stdshmc = stdshmc;
    }

    public static void main(String[] args) {
       /* String str = ConstSqlString.TEST_201002_RESPONSE_XML;
        T201002Response res = (T201002Response)T201002Response.toObject(T201002Response.class, str);
        System.out.println(res.getStdsqdzt());*/
    }

}
