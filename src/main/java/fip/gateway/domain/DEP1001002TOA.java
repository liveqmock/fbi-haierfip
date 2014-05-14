package fip.gateway.domain;

/**
 * 实时代付交易 TOA (只支持单笔模式).
 * User: zhanrui
 * Date: 11-12-13
 * Time: 上午10:10
 * To change this template use File | Settings | File Templates.
 */
public class DEP1001002TOA {
    public String REQUEST_SN;
    public String TX_CODE;
    public String HEAD_RTN_CODE;
    public String HEAD_RTN_MSG = "";

    public String ACCOUNT_NO = "";
    public String ACCOUNT_NAME = "";
    public String AMOUNT = "";
    public String REMARK = "";
    public String RECORD_RET_CODE = "";
    public String RECORD_RTN_MSG = "";
    public String RESERVE1 = "";
    public String RESERVE2 = "";
    public String RESERVE3 = "";
}
