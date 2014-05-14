package fip.gateway.unionpay.core;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:24:32
 * To change this template use File | Settings | File Templates.
 */

public class TIAHeader implements Serializable {
    public String TRX_CODE = "";
    public String VERSION = "04";
    public String DATA_TYPE = "2";
    public String LEVEL = "0";
    //测试
    //public String USER_NAME = "YSCS002";
    //public String USER_PASS = "111111";

    //生产
    //住房按揭
    //用户号   	用户名	用户密码	证书密码	商户编号	          权限	商户名称
    //DSF01954	HAIER3	123456	123456	000191400100880	管理员	海尔集团财务有限责任公司个人按揭（代收）
    //DSF01955	HAIER4	123456	123456	000191400100880	浏览者	海尔集团财务有限责任公司个人按揭（代收）
    //public String USER_NAME = "DSF01955";

    //消费信贷
    //DSF01956	HAIER5	123456	123456	000191400100881	管理员	海尔集团财务有限责任公司贷款（代收）
    //DSF01957	HAIER6	123456	123456	000191400100881	浏览者	海尔集团财务有限责任公司贷款（代收）
    //public String USER_NAME = "DSF01957";

    public String USER_NAME = "";
    public String USER_PASS = "";


    public String REQ_SN = "";
    public String SIGNED_MSG = "";

}