package fip.gateway.unionpay.core;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:24:32
 * To change this template use File | Settings | File Templates.
 */

public class TOAHeader implements Serializable {
    public String TRX_CODE = "";
    public String VERSION = "04";
    public String DATA_TYPE = "2";
    public String REQ_SN = "";
    public String RET_CODE = "";
    public String ERR_MSG = "";
    public String SIGNED_MSG = "";

}