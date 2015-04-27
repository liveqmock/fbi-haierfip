package org.fbi.dep.model.base;

import java.io.Serializable;

public class ToaXmlInfo implements Serializable {
    public String TXN_CODE;                   // 交易请求码
    public String REQ_SN;                    // 流水号 客户端应用系统内唯一
    public String RET_CODE;               // 交易响应码
    public String RET_MSG;                // 交易响应信息
}
