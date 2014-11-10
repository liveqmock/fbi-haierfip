package fip.gateway.sbs.txn.Taa41;


import fip.gateway.sbs.core.SOFDataDetail;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-15
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class Taa41SOFDataDetail extends SOFDataDetail {
    /*
    T531-NBKMSG		X(4)
    T531-SEQNUM.	交易流水号	X(39).
    T531-SECNUM	外围系统流水号	X(18)	从请求报文取值
    T531-TXNCDE		X(4)
    T531-TLRNUM	柜员号	X(4)

    T531-VCHSET	传票套号	X(4)	左补零，右对齐
    T531-SETSEQ	传票套顺序号	X(2)	左补零，右对齐
    T531-TXNDAT	交易日期	X(8)	YYYYMMDD
    T531-INTCUR		X(3)
    T531-TXNAMT	交易金额	S9(13).99	左补空格右对齐				S表示正负号，占一位

    T531-ACTTY1	帐户类型1	X(2)	固定值	01
    T531-IPTAC1	帐号1	X(22)	左对齐，右补空格
    T531-CUSID1	客户号1	X(7)
    T531-ACTNM1	帐户名称1	X(30)
    T531-SHTSEQ		X(2)

    T531-ACTTY2	帐户类型2	X(2)	固定值	01
    T531-IPTAC2	帐号2	X(22)	左补空格右对齐
    T531-CUSID2	客户号2	X(7)
    T531-ACTNM2	帐户名称2	X(30)
    T531-DPTPRD		X(2)

    T531-VALDAT	起息日	X(8)	YYYYMMDD
    T531-AUTTLR	授权柜员	X(4)
    T531-ACTTYP1	帐户类型1	X(2)	固定值	01
    T531-ACTTYP2	帐户类型2	X(2)	固定值	01
     */
    private String NBKMSG;
    private String SEQNUM;
    private String SECNUM;
    private String TXNCDE;
    private String TLRNUM;

    private String VCHSET;
    private String SETSEQ;
    private String TXNDAT;
    private String INTCUR;
    private String TXNAMT;

    private String ACTTY1;
    private String IPTAC1;
    private String CUSID1;
    private String ACTNM1;
    private String SHTSEQ;

    private String ACTTY2;
    private String IPTAC2;
    private String CUSID2;
    private String ACTNM2;
    private String DPTPRD;

    private String VALDAT;
    private String AUTTLR;
    private String ACTTYP1;
    private String ACTTYP2;

    {
        offset = 0;
        fieldTypes = new int[]{1, 1, 1, 1, 1,  1, 1, 1, 1, 1,  1, 1, 1, 1, 1,  1, 1, 1, 1, 1,   1,1, 1, 1};
        fieldLengths = new int[]{4, 39, 18, 4, 4,   4,2,8,3,17,  2,22,7,30,2,  2,22,7,30,2,  8,4,2,2};
    }

    public String getNBKMSG() {
        return NBKMSG;
    }

    public void setNBKMSG(String NBKMSG) {
        this.NBKMSG = NBKMSG;
    }

    public String getSEQNUM() {
        return SEQNUM;
    }

    public void setSEQNUM(String SEQNUM) {
        this.SEQNUM = SEQNUM;
    }

    public String getSECNUM() {
        return SECNUM;
    }

    public void setSECNUM(String SECNUM) {
        this.SECNUM = SECNUM;
    }

    public String getTXNCDE() {
        return TXNCDE;
    }

    public void setTXNCDE(String TXNCDE) {
        this.TXNCDE = TXNCDE;
    }

    public String getTLRNUM() {
        return TLRNUM;
    }

    public void setTLRNUM(String TLRNUM) {
        this.TLRNUM = TLRNUM;
    }

    public String getVCHSET() {
        return VCHSET;
    }

    public void setVCHSET(String VCHSET) {
        this.VCHSET = VCHSET;
    }

    public String getSETSEQ() {
        return SETSEQ;
    }

    public void setSETSEQ(String SETSEQ) {
        this.SETSEQ = SETSEQ;
    }

    public String getTXNDAT() {
        return TXNDAT;
    }

    public void setTXNDAT(String TXNDAT) {
        this.TXNDAT = TXNDAT;
    }

    public String getINTCUR() {
        return INTCUR;
    }

    public void setINTCUR(String INTCUR) {
        this.INTCUR = INTCUR;
    }

    public String getTXNAMT() {
        return TXNAMT;
    }

    public void setTXNAMT(String TXNAMT) {
        this.TXNAMT = TXNAMT;
    }

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

    public String getCUSID1() {
        return CUSID1;
    }

    public void setCUSID1(String CUSID1) {
        this.CUSID1 = CUSID1;
    }

    public String getACTNM1() {
        return ACTNM1;
    }

    public void setACTNM1(String ACTNM1) {
        this.ACTNM1 = ACTNM1;
    }

    public String getSHTSEQ() {
        return SHTSEQ;
    }

    public void setSHTSEQ(String SHTSEQ) {
        this.SHTSEQ = SHTSEQ;
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

    public String getCUSID2() {
        return CUSID2;
    }

    public void setCUSID2(String CUSID2) {
        this.CUSID2 = CUSID2;
    }

    public String getACTNM2() {
        return ACTNM2;
    }

    public void setACTNM2(String ACTNM2) {
        this.ACTNM2 = ACTNM2;
    }

    public String getDPTPRD() {
        return DPTPRD;
    }

    public void setDPTPRD(String DPTPRD) {
        this.DPTPRD = DPTPRD;
    }

    public String getVALDAT() {
        return VALDAT;
    }

    public void setVALDAT(String VALDAT) {
        this.VALDAT = VALDAT;
    }

    public String getAUTTLR() {
        return AUTTLR;
    }

    public void setAUTTLR(String AUTTLR) {
        this.AUTTLR = AUTTLR;
    }

    public String getACTTYP1() {
        return ACTTYP1;
    }

    public void setACTTYP1(String ACTTYP1) {
        this.ACTTYP1 = ACTTYP1;
    }

    public String getACTTYP2() {
        return ACTTYP2;
    }

    public void setACTTYP2(String ACTTYP2) {
        this.ACTTYP2 = ACTTYP2;
    }
}
