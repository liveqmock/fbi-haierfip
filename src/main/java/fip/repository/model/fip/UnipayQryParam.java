package fip.repository.model.fip;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-11-14
 * Time: 下午4:12
 * To change this template use File | Settings | File Templates.
 */
public class UnipayQryParam {
    private String BIZ_ID = "";
    private String MERCHANT_ID = "";
    private String QUERY_SN = "";
    private String BEGIN_DATE = "";     //YYYYMMDD 空时查询当天记录
    private String END_DATE = "";
    private String PAGE_NUM = "10";   //为空默认第一页
    private String PAGE_SIZE = "20";   //为空默认大小（1000）
    private String RESULT_TYPE = "1";   //1、全部；2、成功；3、失败（默认全部）
    private String NEED_DETAIL = "1";   //1、返回；2、不返回（默认返回）
    private String QUERY_REMARK = "HAIER TXN QRY";

    public String getBIZ_ID() {
        return BIZ_ID;
    }

    public void setBIZ_ID(String BIZ_ID) {
        this.BIZ_ID = BIZ_ID;
    }

    public String getMERCHANT_ID() {
        return MERCHANT_ID;
    }

    public void setMERCHANT_ID(String MERCHANT_ID) {
        this.MERCHANT_ID = MERCHANT_ID;
    }

    public String getQUERY_SN() {
        return QUERY_SN;
    }

    public void setQUERY_SN(String QUERY_SN) {
        this.QUERY_SN = QUERY_SN;
    }

    public String getBEGIN_DATE() {
        return BEGIN_DATE;
    }

    public void setBEGIN_DATE(String BEGIN_DATE) {
        this.BEGIN_DATE = BEGIN_DATE;
    }

    public String getEND_DATE() {
        return END_DATE;
    }

    public void setEND_DATE(String END_DATE) {
        this.END_DATE = END_DATE;
    }

    public String getPAGE_NUM() {
        return PAGE_NUM;
    }

    public void setPAGE_NUM(String PAGE_NUM) {
        this.PAGE_NUM = PAGE_NUM;
    }

    public String getPAGE_SIZE() {
        return PAGE_SIZE;
    }

    public void setPAGE_SIZE(String PAGE_SIZE) {
        this.PAGE_SIZE = PAGE_SIZE;
    }

    public String getRESULT_TYPE() {
        return RESULT_TYPE;
    }

    public void setRESULT_TYPE(String RESULT_TYPE) {
        this.RESULT_TYPE = RESULT_TYPE;
    }

    public String getNEED_DETAIL() {
        return NEED_DETAIL;
    }

    public void setNEED_DETAIL(String NEED_DETAIL) {
        this.NEED_DETAIL = NEED_DETAIL;
    }

    public String getQUERY_REMARK() {
        return QUERY_REMARK;
    }

    public void setQUERY_REMARK(String QUERY_REMARK) {
        this.QUERY_REMARK = QUERY_REMARK;
    }
}
