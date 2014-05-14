package org.fbi.dep.model.base;

import java.io.Serializable;

public class TiaXmlHeader implements Serializable {
    public String REQ_SN;                    // 流水号 客户端应用系统内唯一
    public String TRX_CODE;                   // 交易请求码
    public String WSYS_ID;                // 外联系统标识
}
