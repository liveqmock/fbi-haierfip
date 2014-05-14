package org.fbi.dep.model.base;

import java.io.Serializable;

public class ToaXmlHeader implements Serializable {
    public String TRX_CODE;                   // 交易请求码
    public String REQ_SN;                    // 流水号 客户端应用系统内唯一
    public String WSYS_ID;                // 外联系统ID
    public String RET_CODE;               // 交易响应码
    public String ERR_MSG;                // 交易响应信息
}
