package fip.gateway.ccms.domain.T100102;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fip.gateway.ccms.domain.common.MsgHeader;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-10-10
 * Time: 17:18:39
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("ROOT")
public class T100102Request extends MsgHeader {
    private String stdjjh;
    //    private String stdqch;
    private String stdjyrq;
    private String stdhth;
    private String stddkzh;
    private String stdjhkkr;
    private String stdcgkkje;
    private String stdkkjg;

    public String getStdjjh() {
        return stdjjh;
    }

    public void setStdjjh(String stdjjh) {
        this.stdjjh = stdjjh;
    }

    public String getStdjyrq() {
        return stdjyrq;
    }

    public void setStdjyrq(String stdjyrq) {
        this.stdjyrq = stdjyrq;
    }

    public String getStdhth() {
        return stdhth;
    }

    public void setStdhth(String stdhth) {
        this.stdhth = stdhth;
    }

    public String getStddkzh() {
        return stddkzh;
    }

    public void setStddkzh(String stddkzh) {
        this.stddkzh = stddkzh;
    }

    public String getStdjhkkr() {
        return stdjhkkr;
    }

    public void setStdjhkkr(String stdjhkkr) {
        this.stdjhkkr = stdjhkkr;
    }

    public String getStdcgkkje() {
        return stdcgkkje;
    }

    public void setStdcgkkje(String stdcgkkje) {
        this.stdcgkkje = stdcgkkje;
    }

    public String getStdkkjg() {
        return stdkkjg;
    }

    public void setStdkkjg(String stdkkjg) {
        this.stdkkjg = stdkkjg;
    }
}
