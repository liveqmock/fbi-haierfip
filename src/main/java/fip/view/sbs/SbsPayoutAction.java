package fip.view.sbs;

import fip.common.constant.PayoutBatRtnCode;
import fip.common.constant.PayoutBatTxnStep;
import fip.common.constant.PayoutDetlRtnCode;
import fip.common.constant.PayoutDetlTxnStep;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipJoblog;
import fip.repository.model.FipPayoutbat;
import fip.repository.model.FipPayoutdetl;
import fip.service.fip.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.*;

/**
 * sbs银联代付
 */
@ManagedBean
@ViewScoped
public class SbsPayoutAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(SbsPayoutAction.class);
    @ManagedProperty(value = "#{payoutbatService}")
    private PayoutbatService payoutbatService;
    @ManagedProperty(value = "#{payoutDetlService}")
    private PayoutDetlService payoutDetlService;
    @ManagedProperty(value = "#{payoutTxnService}")
    private PayoutTxnService payoutTxnService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    private List<FipPayoutbat> n057List;
    private List<FipPayoutbat> unipayList;
    private List<FipPayoutbat> qryList;
    private List<FipPayoutbat> sbsConfirmList;
    private List<FipPayoutbat> allList;

    private List<FipPayoutdetl> detlList;
    private List<FipJoblog> joblogList;

    private FipPayoutbat[] selectedBats;
    private FipPayoutdetl selectedDetl;

    private PayoutBatRtnCode batRetCode = PayoutBatRtnCode.TXN_HALFWAY;
    private PayoutDetlRtnCode detlRetCode = PayoutDetlRtnCode.HALFWAY;
    private PayoutDetlTxnStep detlTxnStep = PayoutDetlTxnStep.INIT;
    private PayoutBatTxnStep batTxnStep = PayoutBatTxnStep.INIT;

    @PostConstruct
    public void init() {
        String reqsn = (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("reqsn");
        if (StringUtils.isEmpty(reqsn)) {
            onQuery();
        } else {
            detlList = payoutDetlService.qryRecordsBySn(reqsn);
            joblogList = new ArrayList<FipJoblog>();
            for (FipPayoutdetl detl : detlList) {
                joblogList.addAll(jobLogService.selectJobLogsByOriginPkid("fip_payoutdetl", detl.getPkid()));
            }
        }
    }

    public String onQuery() {
        try {
            n057List = payoutbatService.qryPayoutbatsByBatSts(PayoutBatRtnCode.TXN_HALFWAY, PayoutBatTxnStep.INIT);
            unipayList = payoutbatService.qryPayoutbatsByBatSts(PayoutBatRtnCode.TXN_HALFWAY, PayoutBatTxnStep.SBSN057);
            qryList = payoutbatService.qryPayoutbatsByBatSts(PayoutBatRtnCode.TXN_HALFWAY, PayoutBatTxnStep.UNIONPAY_TXN_PAYOUT);
            sbsConfirmList = payoutbatService.qryPayoutbatsByBatSts(PayoutBatRtnCode.TXN_HALFWAY, PayoutBatTxnStep.UNIONPAY_TXN_OVER);
        } catch (Exception e) {
            MessageUtil.addError("查询失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("查询失败。", e);
        }
        return null;
    }

    public String onQryALl() {
        try {
            allList = payoutbatService.qryAllPayoutbats();
            if (allList.isEmpty()) {
                MessageUtil.addWarn("没有数据记录。");
            }
        } catch (Exception e) {
            MessageUtil.addError("查询失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("查询失败。", e);
        }
        return null;
    }

    // SBS代付入账
    public String onAllN057() {
        try {
            // sbs代付
            int cnt = payoutTxnService.processN057(n057List);
            if (cnt > 0) {
                MessageUtil.addInfo("SBS支付入账成功笔数:" + cnt);
            } else {
                MessageUtil.addInfo("SBS支付入账失败。");
            }
            // 再次查询
            onQuery();
        } catch (Exception e) {
            MessageUtil.addError("支付[N057]失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("支付[N057]失败。", e);
        }
        return null;
    }

    public String onN057() {
        try {
            // sbs代付
            int cnt = payoutTxnService.processN057(Arrays.asList(selectedBats));
            if (cnt > 0) {
                MessageUtil.addInfo("SBS支付入账成功笔数:" + cnt);
            } else {
                MessageUtil.addInfo("SBS支付入账失败。");
            }
            // 再次查询
            onQuery();
        } catch (Exception e) {
            MessageUtil.addError("支付[N057]失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("支付[N057]失败。", e);
        }
        return null;
    }

    // 银联代付
    public String onUnipayout() {
        try {
            // unionpay 代付
            int cnt = payoutTxnService.processUnionpayPayout(unipayList);
            if (cnt > 0) {
                MessageUtil.addInfo("发送银联代付完成，结果待查询。笔数:" + cnt);
            } else {
                MessageUtil.addInfo("发送银联代付失败。");
            }
            // 再次查询
            onQuery();
        } catch (Exception e) {
            MessageUtil.addError("银联代付失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("银联代付失败。", e);
        }
        return null;
    }

    // 银联查询
    public String onUnionpayQry() {
        try {
            // unionpay query
            payoutTxnService.processUnionpayQry(qryList);
            MessageUtil.addInfo("代付结果查询，若仍有不明结果交易，可稍后再次查询。");
            // 再次查询
            onQuery();
        } catch (Exception e) {
            MessageUtil.addError("查询失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("查询失败。", e);
        }
        return null;
    }

    // sbs 代付入账 n058或n059
    public String onSbsConfirm() {
        try {
            // unionpay query
            int cnt = payoutTxnService.processSbsPayoutConfirm(sbsConfirmList);
            if (cnt > 0) {
                MessageUtil.addInfo("SBS入账确认笔数:" + cnt);
            } else {
                MessageUtil.addInfo("SBS入账撤销笔数：" + (sbsConfirmList.size() - cnt));
            }
            // 再次查询
            onQuery();
        } catch (Exception e) {
            MessageUtil.addError("查询失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("查询失败。", e);
        }
        return null;
    }
    // ---------------------------

    public PayoutbatService getPayoutbatService() {
        return payoutbatService;
    }

    public void setPayoutbatService(PayoutbatService payoutbatService) {
        this.payoutbatService = payoutbatService;
    }

    public PayoutDetlService getPayoutDetlService() {
        return payoutDetlService;
    }

    public void setPayoutDetlService(PayoutDetlService payoutDetlService) {
        this.payoutDetlService = payoutDetlService;
    }

    public PayoutTxnService getPayoutTxnService() {
        return payoutTxnService;
    }

    public void setPayoutTxnService(PayoutTxnService payoutTxnService) {
        this.payoutTxnService = payoutTxnService;
    }

    public List<FipPayoutbat> getN057List() {
        return n057List;
    }

    public void setN057List(List<FipPayoutbat> n057List) {
        this.n057List = n057List;
    }

    public List<FipPayoutbat> getSbsConfirmList() {
        return sbsConfirmList;
    }

    public void setSbsConfirmList(List<FipPayoutbat> sbsConfirmList) {
        this.sbsConfirmList = sbsConfirmList;
    }

    public List<FipPayoutbat> getQryList() {
        return qryList;
    }

    public void setQryList(List<FipPayoutbat> qryList) {
        this.qryList = qryList;
    }

    public List<FipPayoutbat> getUnipayList() {
        return unipayList;
    }

    public void setUnipayList(List<FipPayoutbat> unipayList) {
        this.unipayList = unipayList;
    }

    public FipPayoutbat[] getSelectedBats() {
        return selectedBats;
    }

    public void setSelectedBats(FipPayoutbat[] selectedBats) {
        this.selectedBats = selectedBats;
    }

    public PayoutBatRtnCode getBatRetCode() {
        return batRetCode;
    }

    public void setBatRetCode(PayoutBatRtnCode batRetCode) {
        this.batRetCode = batRetCode;
    }

    public PayoutDetlRtnCode getDetlRetCode() {
        return detlRetCode;
    }

    public void setDetlRetCode(PayoutDetlRtnCode detlRetCode) {
        this.detlRetCode = detlRetCode;
    }

    public PayoutDetlTxnStep getDetlTxnStep() {
        return detlTxnStep;
    }

    public void setDetlTxnStep(PayoutDetlTxnStep detlTxnStep) {
        this.detlTxnStep = detlTxnStep;
    }

    public PayoutBatTxnStep getBatTxnStep() {
        return batTxnStep;
    }

    public void setBatTxnStep(PayoutBatTxnStep batTxnStep) {
        this.batTxnStep = batTxnStep;
    }

    public List<FipPayoutbat> getAllList() {
        return allList;
    }

    public void setAllList(List<FipPayoutbat> allList) {
        this.allList = allList;
    }

    public List<FipPayoutdetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipPayoutdetl> detlList) {
        this.detlList = detlList;
    }

    public List<FipJoblog> getJoblogList() {
        return joblogList;
    }

    public void setJoblogList(List<FipJoblog> joblogList) {
        this.joblogList = joblogList;
    }

    public FipPayoutdetl getSelectedDetl() {
        return selectedDetl;
    }

    public void setSelectedDetl(FipPayoutdetl selectedDetl) {
        this.selectedDetl = selectedDetl;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }
}
