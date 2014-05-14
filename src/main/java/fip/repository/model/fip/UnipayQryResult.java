package fip.repository.model.fip;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-11-14
 * Time: 下午4:18
 * To change this template use File | Settings | File Templates.
 */
public class UnipayQryResult implements Serializable {
    private String QUERY_SN = "";
    private String ORAFILE_ID = "";
    private String SN = "";
    private String ACCOUNT = "";
    private String ACCOUNT_NAME = "";
    private String AMOUNT = "";
    private String CUST_USERID = "";
    private String COMPLETE_TIME = "";        //YYYYMMDDHHMMSS  完成时间 C（14）
    private String REMARK = "";
    private String RET_CODE = "";
    private String ERR_MSG = "";

    public String getQUERY_SN() {
        return QUERY_SN;
    }

    public void setQUERY_SN(String QUERY_SN) {
        this.QUERY_SN = QUERY_SN;
    }

    public String getORAFILE_ID() {
        return ORAFILE_ID;
    }

    public void setORAFILE_ID(String ORAFILE_ID) {
        this.ORAFILE_ID = ORAFILE_ID;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getACCOUNT() {
        return ACCOUNT;
    }

    public void setACCOUNT(String ACCOUNT) {
        this.ACCOUNT = ACCOUNT;
    }

    public String getACCOUNT_NAME() {
        return ACCOUNT_NAME;
    }

    public void setACCOUNT_NAME(String ACCOUNT_NAME) {
        this.ACCOUNT_NAME = ACCOUNT_NAME;
    }

    public String getAMOUNT() {
        return AMOUNT;
    }

    public void setAMOUNT(String AMOUNT) {
        this.AMOUNT = AMOUNT;
    }

    public String getCUST_USERID() {
        return CUST_USERID;
    }

    public void setCUST_USERID(String CUST_USERID) {
        this.CUST_USERID = CUST_USERID;
    }

    public String getCOMPLETE_TIME() {
        return COMPLETE_TIME;
    }

    public void setCOMPLETE_TIME(String COMPLETE_TIME) {
        this.COMPLETE_TIME = COMPLETE_TIME;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getRET_CODE() {
        return RET_CODE;
    }

    public void setRET_CODE(String RET_CODE) {
        this.RET_CODE = RET_CODE;
    }

    public String getERR_MSG() {
        return ERR_MSG;
    }

    public void setERR_MSG(String ERR_MSG) {
        this.ERR_MSG = ERR_MSG;
    }
}
