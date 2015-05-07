package fip.gateway.sbs.txn.T8123;


import fip.gateway.sbs.core.SOFDataDetail;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-15
 * Time: 上午10:59
 * To change this template use File | Settings | File Templates.
 */
public class T8123SOFDataDetail extends SOFDataDetail {
    /*
    TXXX-ACTNUM	账号	X(14) 	左对齐，右补空格
    TXXX-ACTNAM	户名	X(60) 	左对齐，右补空格
    TXXX-ACTBAL	余额	X(21) 	左对齐，右补空格
    TXXX-ACTTYP	账户种类	X(1)	左对齐，右补空格	1-定存,2-贷款,3-活存,9-其他
    TXXX-ASSTYP	是否保证金	X(1)	左对齐，右补空格	1-保证金
     */
    private String actnum;
    private String actnam;
    private String actbal;
    private String acttyp;
    private String asstyp;

    {
        offset = 0;
        fieldTypes = new int[]{1, 1, 1, 1, 1};
        fieldLengths = new int[]{14, 60, 21, 1, 1};
    }

    public String getActnum() {
        return actnum;
    }

    public void setActnum(String actnum) {
        this.actnum = actnum;
    }

    public String getActbal() {
        return actbal;
    }

    public void setActbal(String actbal) {
        this.actbal = actbal;
    }

    public String getActtyp() {
        return acttyp;
    }

    public void setActtyp(String acttyp) {
        this.acttyp = acttyp;
    }

    public String getAsstyp() {
        return asstyp;
    }

    public void setAsstyp(String asstyp) {
        this.asstyp = asstyp;
    }

    public String getActnam() {
        return actnam;
    }

    public void setActnam(String actnam) {
        this.actnam = actnam;
    }
}
