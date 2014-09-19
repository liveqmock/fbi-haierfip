package fip.view.actchk;

import fip.batch.actchk.ZongFen4SCFHandler;
import fip.batch.actchk.ZongFenHandler;
import fip.common.utils.MessageUtil;
import fip.repository.model.actchk.ActchkVO;
import fip.service.actchk.ActchkService;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 余额对帐处理
 * zhanrui
 * 2012/3/24
 */
@ManagedBean
@ViewScoped
public class ActChkAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ActChkAction.class);

    private String txnDate;


    private List<ActchkVO> detlList = new ArrayList<ActchkVO>();
    private ActchkVO selectedRecord;
    private ActchkVO[] selectedRecords;

    private int totalCount;
    private int totalErrorCount;
    private int totalSuccessCount;

    private BigDecimal totalAmt;

    private String bankId;

    @ManagedProperty(value = "#{actchkService}")
    private ActchkService actchkService;

    @PostConstruct
    public void init() {
        DateTime dt = new DateTime();
        this.txnDate = dt.minusDays(1).toString("yyyyMMdd");
//        this.txnDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        bankId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bankid");
        if (bankId == null || bankId.equals("")) {
            throw new RuntimeException("银行代码不能为空!");
        }
    }

    public String onQueryFail() {
        try {
            this.totalCount = actchkService.countActchkRecord(this.txnDate, this.bankId);
            if (totalCount == 0) {
                MessageUtil.addError("本日无对帐数据。");
                this.detlList = new ArrayList<ActchkVO>();
                return null;
            }
            this.detlList = actchkService.selectChkTxnFailResult("SBS_" + this.bankId, this.bankId, this.txnDate);
            this.totalErrorCount = this.detlList.size();
            if (this.totalErrorCount == 0) {
                MessageUtil.addError("无不平账数据。");
                return null;
            }
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onQuerySucc() {
        try {
            this.totalCount = actchkService.countActchkRecord(this.txnDate, this.bankId);
            if (totalCount == 0) {
                MessageUtil.addError("本日无对帐数据。");
                this.detlList = new ArrayList<ActchkVO>();
                return null;
            }
            this.detlList = actchkService.selectChkTxnSuccResult("SBS_" + this.bankId, this.bankId, this.txnDate);
            this.totalSuccessCount = this.detlList.size();
            if (this.totalSuccessCount == 0) {
                MessageUtil.addError("无平账数据。");
                return null;
            }
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onStartActchk() {
        try {
            if ("CCB".equals(this.bankId)) {
                ZongFenHandler handler = new ZongFenHandler();
                handler.startActChk4Web(this.txnDate);
            } else {
                ZongFen4SCFHandler handler = new ZongFen4SCFHandler();
                handler.startActChk4Web(this.txnDate, this.bankId);
            }
            this.detlList = new ArrayList<ActchkVO>();
            MessageUtil.addInfo("对账处理已开始。");
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String onStartNotify() {
        try {
            if ("CCB".equals(this.bankId)) {
                ZongFenHandler handler = new ZongFenHandler();
                handler.notifyResult4Web(this.txnDate);
            } else {
                ZongFen4SCFHandler handler = new ZongFen4SCFHandler();
                handler.notifyResult4Web(this.txnDate, this.bankId);
            }
            MessageUtil.addInfo("结果通知已完成。");
        } catch (Exception e) {
            logger.error("处理失败。", e);
            MessageUtil.addError("处理失败。" + e.getMessage());
        }
        return null;
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }

    public List<ActchkVO> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<ActchkVO> detlList) {
        this.detlList = detlList;
    }

    public ActchkVO getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(ActchkVO selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public ActchkVO[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(ActchkVO[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalErrorCount() {
        return totalErrorCount;
    }

    public void setTotalErrorCount(int totalErrorCount) {
        this.totalErrorCount = totalErrorCount;
    }

    public int getTotalSuccessCount() {
        return totalSuccessCount;
    }

    public void setTotalSuccessCount(int totalSuccessCount) {
        this.totalSuccessCount = totalSuccessCount;
    }

    public BigDecimal getTotalAmt() {
        return totalAmt;
    }

    public void setTotalAmt(BigDecimal totalAmt) {
        this.totalAmt = totalAmt;
    }

    public String getBankId() {
        return bankId;
    }

    public void setBankId(String bankId) {
        this.bankId = bankId;
    }

    public ActchkService getActchkService() {
        return actchkService;
    }

    public void setActchkService(ActchkService actchkService) {
        this.actchkService = actchkService;
    }
}
