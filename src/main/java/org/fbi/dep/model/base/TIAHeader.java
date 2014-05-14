package org.fbi.dep.model.base;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-1-31
 * Time: 下午2:10
 */
public class TIAHeader implements Serializable {
    public String CHANNEL_ID;                // 渠道
    public String APP_ID;                    // 应用ID
    public String BIZ_ID;                    // 业务ID
    public String REQ_SN;                    // 流水号 客户端应用系统内唯一
    public String USER_ID;                   // 操作员ID
    public String PASSWORD;                  // 操作员密码
    public String TX_CODE;                   // 交易请求码
    public String SIGNED_MSG;                // 交易报文签名信息
}
