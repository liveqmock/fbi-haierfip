package fip.gateway.sbs.txn.Ta543;

/**
 * 代扣还款接口.
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
public class Ta543TiaRecord {
    /*
        CTF-TXNDAT	交易日期	X(8)	固定值
        CTF-PASSNO	外围系统流水号	X(18)
        CTF-IPTAC1	贷款账号	X(22)	左对齐，不足时右补空格
        CTF-TXNAMT	本金金额	S9(13).99	右对齐，不足时左补零
        CTF-ADVAMT	违约金金额（利息/应收息）	S9(13).99	右对齐，不足时左补零

        CTF-STNAMT	滞纳金金额（罚息）	S9(13).99	右对齐，不足时左补零
        CTF-DEB-LIMNUM	手续费金额（复利）	S9(13).99	右对齐，不足时左补零
        CTF-REMARK	摘要	X(30)	左对齐右补空格
        CTF-IPTAC2	本金还款账号	X(22)	输入还款结算账号或银行同业账号
        CTF-DPTTYP	贷款种类	X(2)	左对齐右补空格	"01对公非房地产贷款
                                                    02对公房地产开发贷款
                                                    03转贷款
                                                    04对私消费贷款
                                                    05对私按揭贷款
                                                    06委托贷款
                                                    07贴现
                                                    08信贷资产转让"
        CTF-DSUUSE-IPTACT	利息还款账号	X(22)	输入还款结算账号或银行同业账号
     */
    private String  TXNDAT;
    private String  PASSNO;
    private String  IPTAC1;
    private String  TXNAMT;
    private String  ADVAMT;

    private String  STNAMT;
    private String  DEBLIMNUM;
    private String  REMARK;
    private String  IPTAC2;
    private String  DPTTYP;

    private String  DSUUSEIPTACT;

    public String getTXNDAT() {
        return TXNDAT;
    }

    public void setTXNDAT(String TXNDAT) {
        this.TXNDAT = TXNDAT;
    }

    public String getPASSNO() {
        return PASSNO;
    }

    public void setPASSNO(String PASSNO) {
        this.PASSNO = PASSNO;
    }

    public String getIPTAC1() {
        return IPTAC1;
    }

    public void setIPTAC1(String IPTAC1) {
        this.IPTAC1 = IPTAC1;
    }

    public String getTXNAMT() {
        return TXNAMT;
    }

    public void setTXNAMT(String TXNAMT) {
        this.TXNAMT = TXNAMT;
    }

    public String getADVAMT() {
        return ADVAMT;
    }

    public void setADVAMT(String ADVAMT) {
        this.ADVAMT = ADVAMT;
    }

    public String getSTNAMT() {
        return STNAMT;
    }

    public void setSTNAMT(String STNAMT) {
        this.STNAMT = STNAMT;
    }

    public String getDEBLIMNUM() {
        return DEBLIMNUM;
    }

    public void setDEBLIMNUM(String DEBLIMNUM) {
        this.DEBLIMNUM = DEBLIMNUM;
    }

    public String getREMARK() {
        return REMARK;
    }

    public void setREMARK(String REMARK) {
        this.REMARK = REMARK;
    }

    public String getIPTAC2() {
        return IPTAC2;
    }

    public void setIPTAC2(String IPTAC2) {
        this.IPTAC2 = IPTAC2;
    }

    public String getDPTTYP() {
        return DPTTYP;
    }

    public void setDPTTYP(String DPTTYP) {
        this.DPTTYP = DPTTYP;
    }

    public String getDSUUSEIPTACT() {
        return DSUUSEIPTACT;
    }

    public void setDSUUSEIPTACT(String DSUUSEIPTACT) {
        this.DSUUSEIPTACT = DSUUSEIPTACT;
    }
}
