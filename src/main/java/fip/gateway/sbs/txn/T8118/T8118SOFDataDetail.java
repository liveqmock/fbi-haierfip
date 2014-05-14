package fip.gateway.sbs.txn.T8118;

import fip.gateway.sbs.core.SOFDataDetail;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-9-22
 * Time: ÏÂÎç4:53
 * To change this template use File | Settings | File Templates.
 */
public class T8118SOFDataDetail extends SOFDataDetail {
    private String ORGIDT;
    private String ACTNUM;
    private String ACTNAM;
    private String BOKBAL;
    private String AVABAL;
    private String DIFBAL;
    private String CIFBAL;
    private String MAVBAL;
    private String YAVBAL;
    private String DDRAMT;
    private String DCRAMT;
    private String DDRCNT;
    private String DCRCNT;
    private String MDRAMT;
    private String MCRAMT;
    private String MDRCNT;
    private String MCRCNT;
    private String YDRAMT;
    private String YCRAMT;
    private String YDRCNT;
    private String YCRCNT;
    private String DINRAT;
    private String CINRAT;
    private String DRATSF;
    private String CRATSF;
    private String DACINT;
    private String CACINT;
    private String DAMINT;
    private String CAMINT;
    private String INTFLG;
    private String INTCYC;
    private String INTTRA;
    private String LINTDT;
    private String LINDTH;
    private String STMCYC;
    private String STMCDT;
    private String STMFMT;
    private String STMSHT;
    private String STMDEP;
    private String STMADD;
    private String STMZIP;
    private String LEGCYC;
    private String LEGCDT;
    private String LEGFMT;
    private String LEGSHT;
    private String LEGDEP;
    private String LEGADD;
    private String LEGZIP;
    private String ACTTYP;
    private String ACTGLC;
    private String ACTPLC;
    private String DEPNUM;
    private String LENTDT;
    private String DRACCM;
    private String CRACCM;
    private String CQEFLG;
    private String BALLIM;
    private String OVELIM;
    private String OVEEXP;
    private String OPNDAT;
    private String CLSDAT;
    private String REGSTS;
    private String FRZSTS;
    private String ACTSTS;
    private String AMDTLR;
    private String UPDDAT;
    private String RECSTS;

    {

        offset = 0;
        fieldTypes = new int[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1,
                1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1};
        fieldLengths = new int[]{3, 14, 72, 21, 21, 21, 21, 21, 21, 21,
                21, 9, 9, 21, 21, 9, 9, 21, 21, 9, 9, 3, 3,
                10, 10, 21, 21, 21, 21, 1, 1, 18, 8, 8,
                1, 4, 1, 9, 5, 42, 6, 1, 4, 1,
                9, 5, 42, 6, 1, 4, 4, 2, 8, 24,
                24, 1, 21, 21, 8, 8, 8, 1, 1, 1,
                4, 8, 1};
    }

    public String getORGIDT() {
        return ORGIDT;
    }

    public void setORGIDT(String ORGIDT) {
        this.ORGIDT = ORGIDT;
    }

    public String getACTNUM() {
        return ACTNUM;
    }

    public void setACTNUM(String ACTNUM) {
        this.ACTNUM = ACTNUM;
    }

    public String getACTNAM() {
        return ACTNAM;
    }

    public void setACTNAM(String ACTNAM) {
        this.ACTNAM = ACTNAM;
    }

    public String getBOKBAL() {
        return BOKBAL;
    }

    public void setBOKBAL(String BOKBAL) {
        this.BOKBAL = BOKBAL;
    }

    public String getAVABAL() {
        return AVABAL;
    }

    public void setAVABAL(String AVABAL) {
        this.AVABAL = AVABAL;
    }

    public String getDIFBAL() {
        return DIFBAL;
    }

    public void setDIFBAL(String DIFBAL) {
        this.DIFBAL = DIFBAL;
    }

    public String getCIFBAL() {
        return CIFBAL;
    }

    public void setCIFBAL(String CIFBAL) {
        this.CIFBAL = CIFBAL;
    }

    public String getMAVBAL() {
        return MAVBAL;
    }

    public void setMAVBAL(String MAVBAL) {
        this.MAVBAL = MAVBAL;
    }

    public String getYAVBAL() {
        return YAVBAL;
    }

    public void setYAVBAL(String YAVBAL) {
        this.YAVBAL = YAVBAL;
    }

    public String getDDRAMT() {
        return DDRAMT;
    }

    public void setDDRAMT(String DDRAMT) {
        this.DDRAMT = DDRAMT;
    }

    public String getDCRAMT() {
        return DCRAMT;
    }

    public void setDCRAMT(String DCRAMT) {
        this.DCRAMT = DCRAMT;
    }

    public String getDDRCNT() {
        return DDRCNT;
    }

    public void setDDRCNT(String DDRCNT) {
        this.DDRCNT = DDRCNT;
    }

    public String getDCRCNT() {
        return DCRCNT;
    }

    public void setDCRCNT(String DCRCNT) {
        this.DCRCNT = DCRCNT;
    }

    public String getMDRAMT() {
        return MDRAMT;
    }

    public void setMDRAMT(String MDRAMT) {
        this.MDRAMT = MDRAMT;
    }

    public String getMCRAMT() {
        return MCRAMT;
    }

    public void setMCRAMT(String MCRAMT) {
        this.MCRAMT = MCRAMT;
    }

    public String getMDRCNT() {
        return MDRCNT;
    }

    public void setMDRCNT(String MDRCNT) {
        this.MDRCNT = MDRCNT;
    }

    public String getMCRCNT() {
        return MCRCNT;
    }

    public void setMCRCNT(String MCRCNT) {
        this.MCRCNT = MCRCNT;
    }

    public String getYDRAMT() {
        return YDRAMT;
    }

    public void setYDRAMT(String YDRAMT) {
        this.YDRAMT = YDRAMT;
    }

    public String getYCRAMT() {
        return YCRAMT;
    }

    public void setYCRAMT(String YCRAMT) {
        this.YCRAMT = YCRAMT;
    }

    public String getYDRCNT() {
        return YDRCNT;
    }

    public void setYDRCNT(String YDRCNT) {
        this.YDRCNT = YDRCNT;
    }

    public String getYCRCNT() {
        return YCRCNT;
    }

    public void setYCRCNT(String YCRCNT) {
        this.YCRCNT = YCRCNT;
    }

    public String getDINRAT() {
        return DINRAT;
    }

    public void setDINRAT(String DINRAT) {
        this.DINRAT = DINRAT;
    }

    public String getCINRAT() {
        return CINRAT;
    }

    public void setCINRAT(String CINRAT) {
        this.CINRAT = CINRAT;
    }

    public String getDRATSF() {
        return DRATSF;
    }

    public void setDRATSF(String DRATSF) {
        this.DRATSF = DRATSF;
    }

    public String getCRATSF() {
        return CRATSF;
    }

    public void setCRATSF(String CRATSF) {
        this.CRATSF = CRATSF;
    }

    public String getDACINT() {
        return DACINT;
    }

    public void setDACINT(String DACINT) {
        this.DACINT = DACINT;
    }

    public String getCACINT() {
        return CACINT;
    }

    public void setCACINT(String CACINT) {
        this.CACINT = CACINT;
    }

    public String getDAMINT() {
        return DAMINT;
    }

    public void setDAMINT(String DAMINT) {
        this.DAMINT = DAMINT;
    }

    public String getCAMINT() {
        return CAMINT;
    }

    public void setCAMINT(String CAMINT) {
        this.CAMINT = CAMINT;
    }

    public String getINTFLG() {
        return INTFLG;
    }

    public void setINTFLG(String INTFLG) {
        this.INTFLG = INTFLG;
    }

    public String getINTCYC() {
        return INTCYC;
    }

    public void setINTCYC(String INTCYC) {
        this.INTCYC = INTCYC;
    }

    public String getINTTRA() {
        return INTTRA;
    }

    public void setINTTRA(String INTTRA) {
        this.INTTRA = INTTRA;
    }

    public String getLINTDT() {
        return LINTDT;
    }

    public void setLINTDT(String LINTDT) {
        this.LINTDT = LINTDT;
    }

    public String getLINDTH() {
        return LINDTH;
    }

    public void setLINDTH(String LINDTH) {
        this.LINDTH = LINDTH;
    }

    public String getSTMCYC() {
        return STMCYC;
    }

    public void setSTMCYC(String STMCYC) {
        this.STMCYC = STMCYC;
    }

    public String getSTMCDT() {
        return STMCDT;
    }

    public void setSTMCDT(String STMCDT) {
        this.STMCDT = STMCDT;
    }

    public String getSTMFMT() {
        return STMFMT;
    }

    public void setSTMFMT(String STMFMT) {
        this.STMFMT = STMFMT;
    }

    public String getSTMSHT() {
        return STMSHT;
    }

    public void setSTMSHT(String STMSHT) {
        this.STMSHT = STMSHT;
    }

    public String getSTMDEP() {
        return STMDEP;
    }

    public void setSTMDEP(String STMDEP) {
        this.STMDEP = STMDEP;
    }

    public String getSTMADD() {
        return STMADD;
    }

    public void setSTMADD(String STMADD) {
        this.STMADD = STMADD;
    }

    public String getSTMZIP() {
        return STMZIP;
    }

    public void setSTMZIP(String STMZIP) {
        this.STMZIP = STMZIP;
    }

    public String getLEGCYC() {
        return LEGCYC;
    }

    public void setLEGCYC(String LEGCYC) {
        this.LEGCYC = LEGCYC;
    }

    public String getLEGCDT() {
        return LEGCDT;
    }

    public void setLEGCDT(String LEGCDT) {
        this.LEGCDT = LEGCDT;
    }

    public String getLEGFMT() {
        return LEGFMT;
    }

    public void setLEGFMT(String LEGFMT) {
        this.LEGFMT = LEGFMT;
    }

    public String getLEGSHT() {
        return LEGSHT;
    }

    public void setLEGSHT(String LEGSHT) {
        this.LEGSHT = LEGSHT;
    }

    public String getLEGDEP() {
        return LEGDEP;
    }

    public void setLEGDEP(String LEGDEP) {
        this.LEGDEP = LEGDEP;
    }

    public String getLEGADD() {
        return LEGADD;
    }

    public void setLEGADD(String LEGADD) {
        this.LEGADD = LEGADD;
    }

    public String getLEGZIP() {
        return LEGZIP;
    }

    public void setLEGZIP(String LEGZIP) {
        this.LEGZIP = LEGZIP;
    }

    public String getACTTYP() {
        return ACTTYP;
    }

    public void setACTTYP(String ACTTYP) {
        this.ACTTYP = ACTTYP;
    }

    public String getACTGLC() {
        return ACTGLC;
    }

    public void setACTGLC(String ACTGLC) {
        this.ACTGLC = ACTGLC;
    }

    public String getACTPLC() {
        return ACTPLC;
    }

    public void setACTPLC(String ACTPLC) {
        this.ACTPLC = ACTPLC;
    }

    public String getDEPNUM() {
        return DEPNUM;
    }

    public void setDEPNUM(String DEPNUM) {
        this.DEPNUM = DEPNUM;
    }

    public String getLENTDT() {
        return LENTDT;
    }

    public void setLENTDT(String LENTDT) {
        this.LENTDT = LENTDT;
    }

    public String getDRACCM() {
        return DRACCM;
    }

    public void setDRACCM(String DRACCM) {
        this.DRACCM = DRACCM;
    }

    public String getCRACCM() {
        return CRACCM;
    }

    public void setCRACCM(String CRACCM) {
        this.CRACCM = CRACCM;
    }

    public String getCQEFLG() {
        return CQEFLG;
    }

    public void setCQEFLG(String CQEFLG) {
        this.CQEFLG = CQEFLG;
    }

    public String getBALLIM() {
        return BALLIM;
    }

    public void setBALLIM(String BALLIM) {
        this.BALLIM = BALLIM;
    }

    public String getOVELIM() {
        return OVELIM;
    }

    public void setOVELIM(String OVELIM) {
        this.OVELIM = OVELIM;
    }

    public String getOVEEXP() {
        return OVEEXP;
    }

    public void setOVEEXP(String OVEEXP) {
        this.OVEEXP = OVEEXP;
    }

    public String getOPNDAT() {
        return OPNDAT;
    }

    public void setOPNDAT(String OPNDAT) {
        this.OPNDAT = OPNDAT;
    }

    public String getCLSDAT() {
        return CLSDAT;
    }

    public void setCLSDAT(String CLSDAT) {
        this.CLSDAT = CLSDAT;
    }

    public String getREGSTS() {
        return REGSTS;
    }

    public void setREGSTS(String REGSTS) {
        this.REGSTS = REGSTS;
    }

    public String getFRZSTS() {
        return FRZSTS;
    }

    public void setFRZSTS(String FRZSTS) {
        this.FRZSTS = FRZSTS;
    }

    public String getACTSTS() {
        return ACTSTS;
    }

    public void setACTSTS(String ACTSTS) {
        this.ACTSTS = ACTSTS;
    }

    public String getAMDTLR() {
        return AMDTLR;
    }

    public void setAMDTLR(String AMDTLR) {
        this.AMDTLR = AMDTLR;
    }

    public String getUPDDAT() {
        return UPDDAT;
    }

    public void setUPDDAT(String UPDDAT) {
        this.UPDDAT = UPDDAT;
    }

    public String getRECSTS() {
        return RECSTS;
    }

    public void setRECSTS(String RECSTS) {
        this.RECSTS = RECSTS;
    }
}
