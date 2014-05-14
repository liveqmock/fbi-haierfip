package fip.gateway.sbs.txn.Taa56;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
public class Taa56TiaRecord {
    /*
    CTF-ACTTY1	转出帐户类型	X(2)	固定值	01
    CTF-IPTAC1	转出帐户	X(22)	同业账号	22		801000026131041001
    CTF-DRAMD1	取款方式	X(1)	固定值	3
    CTF-ACTNM1	转出帐户户名	X(72)	固定值	空格
    CTF-CUSPW1	取款密码	X(6)	固定值	空格

    CTF-PASTYP	证件类型	X(1)	固定值	N
    CTF-PASSNO	外围系统流水号	X(18)	固定值
    CTF-PAPTYP	支票种类	X(1)	固定值	空格
    CTF-PAPCDE	支票号	X(10)	固定值	空格
    CTF-PAPMAC	支票密码	X(12)	固定值	空格

    CTF-SGNDAT	签发日期	X(8)	固定值	空格
    CTF-NBKFL1	无折标识	X(1)	固定值	3
    CTF-AUTSEQ	备用字段	X(8)	固定值	空格
    CTF-AUTDAT	备用字段	X(4)	固定值	空格
    CTF-TXNAMT	交易金额	S9(13).99	右对齐，左补0				S表示正负号，占一位

    CTF-ACTTY2	转入帐户类型	X(2)	固定值	01
    CTF-IPTAC2	转入帐户	X(22)	左对齐右补空格
    CTF-ACTNM2	转入帐户户名	X(72)	固定值	空格
    CTF-NBKFL2	无折标识	X(1)	固定值	空格
    CTF-TXNDAT	交易日期	X(8)	YYYYMMDD

    CTF-REMARK	摘要	X(30)	左对齐右补空格
    CTF-ANACDE	产品码	X(4)	固定值	空格
    CTF-MAGFL1		X(1)	固定值	空格
    CTF-MAGFL2		X(1)	固定值	空格
    CTF-DEVTYP	交易种类	X(3)	固定值	空格
     */
    private String  ACTTY1;
    private String  IPTAC1;
    private String  DRAMD1;
    private String  ACTNM1;
    private String  CUSPW1;

    private String  PASTYP;
    private String  PASSNO;
    private String  PAPTYP;
    private String  PAPCDE;
    private String  PAPMAC;

    private String  SGNDAT;
    private String  NBKFL1;
    private String  AUTSEQ;
    private String  AUTDAT;
    private String  TXNAMT;

    private String  ACTTY2;
    private String  IPTAC2;
    private String  ACTNM2;
    private String  NBKFL2;
    private String  TXNDAT;

    private String  REMARK;
    private String  ANACDE;
    private String  MAGFL1;
    private String  MAGFL2;
    private String  DEVTYP;


    public String getACTTY1() {
        return ACTTY1;
    }

    public void setACTTY1(String ACTTY1) {
        this.ACTTY1 = ACTTY1;
    }

    public String getIPTAC1() {
        return IPTAC1;
    }

    public void setIPTAC1(String IPTAC1) {
        this.IPTAC1 = IPTAC1;
    }

    public String getDRAMD1() {
        return DRAMD1;
    }

    public void setDRAMD1(String DRAMD1) {
        this.DRAMD1 = DRAMD1;
    }

    public String getACTNM1() {
        return ACTNM1;
    }

    public void setACTNM1(String ACTNM1) {
        this.ACTNM1 = ACTNM1;
    }

    public String getCUSPW1() {
        return CUSPW1;
    }

    public void setCUSPW1(String CUSPW1) {
        this.CUSPW1 = CUSPW1;
    }

    public String getPASTYP() {
        return PASTYP;
    }

    public void setPASTYP(String PASTYP) {
        this.PASTYP = PASTYP;
    }

    public String getPASSNO() {
        return PASSNO;
    }

    public void setPASSNO(String PASSNO) {
        this.PASSNO = PASSNO;
    }

    public String getPAPTYP() {
        return PAPTYP;
    }

    public void setPAPTYP(String PAPTYP) {
        this.PAPTYP = PAPTYP;
    }

    public String getPAPCDE() {
        return PAPCDE;
    }

    public void setPAPCDE(String PAPCDE) {
        this.PAPCDE = PAPCDE;
    }

    public String getPAPMAC() {
        return PAPMAC;
    }

    public void setPAPMAC(String PAPMAC) {
        this.PAPMAC = PAPMAC;
    }

    public String getSGNDAT() {
        return SGNDAT;
    }

    public void setSGNDAT(String SGNDAT) {
        this.SGNDAT = SGNDAT;
    }

    public String getNBKFL1() {
        return NBKFL1;
    }

    public void setNBKFL1(String NBKFL1) {
        this.NBKFL1 = NBKFL1;
    }

    public String getAUTSEQ() {
        return AUTSEQ;
    }

    public void setAUTSEQ(String AUTSEQ) {
        this.AUTSEQ = AUTSEQ;
    }

    public String getAUTDAT() {
        return AUTDAT;
    }

    public void setAUTDAT(String AUTDAT) {
        this.AUTDAT = AUTDAT;
    }

    public String getTXNAMT() {
        return TXNAMT;
    }

    public void setTXNAMT(String TXNAMT) {
        this.TXNAMT = TXNAMT;
    }

    public String getACTTY2() {
        return ACTTY2;
    }

    public void setACTTY2(String ACTTY2) {
        this.ACTTY2 = ACTTY2;
    }

    public String getIPTAC2() {
        return IPTAC2;
    }

    public void setIPTAC2(String IPTAC2) {
        this.IPTAC2 = IPTAC2;
    }

    public String getACTNM2() {
        return ACTNM2;
    }

    public void setACTNM2(String ACTNM2) {
        this.ACTNM2 = ACTNM2;
    }

    public String getNBKFL2() {
        return NBKFL2;
    }

    public void setNBKFL2(String NBKFL2) {
        this.NBKFL2 = NBKFL2;
    }

    public String getTXNDAT() {
        return TXNDAT;
    }

    public void setTXNDAT(String TXNDAT) {
        this.TXNDAT = TXNDAT;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getANACDE() {
        return ANACDE;
    }

    public void setANACDE(String ANACDE) {
        this.ANACDE = ANACDE;
    }

    public String getMAGFL1() {
        return MAGFL1;
    }

    public void setMAGFL1(String MAGFL1) {
        this.MAGFL1 = MAGFL1;
    }

    public String getMAGFL2() {
        return MAGFL2;
    }

    public void setMAGFL2(String MAGFL2) {
        this.MAGFL2 = MAGFL2;
    }

    public String getDEVTYP() {
        return DEVTYP;
    }

    public void setDEVTYP(String DEVTYP) {
        this.DEVTYP = DEVTYP;
    }
}
