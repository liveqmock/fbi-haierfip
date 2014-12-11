package ibp.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.utils.MessageUtil;
import fip.repository.model.fip.UnipayQryParam;
import fip.repository.model.fip.UnipayQryResult;
import fip.service.fip.UnipayHistoryQryService;
import ibp.repository.model.*;
import ibp.service.IbpIfUnionpayTxnService;
import ibp.service.IbpSbsActService;
import ibp.service.IbpSbsTransTxnService;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 银联代扣明细-SBS入账-自有渠道
 */
@ManagedBean
@ViewScoped
public class IfUnionpayTxnAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(IfUnionpayTxnAction.class);

    private List<IbpIfUnionpayTxn> detlList;
    private IbpIfUnionpayTxn[] selectedRecords;
    private List<IbpSbsTranstxn> sbsTxnList = new ArrayList<IbpSbsTranstxn>();
    private List<IbpIfUnionpayTxn> filteredDetlList = new ArrayList<IbpIfUnionpayTxn>();
    private List<SelectItem> sbsActList = new ArrayList<SelectItem>();
    private UnipayQryParam qryParam = new UnipayQryParam();

    private BillStatus status = BillStatus.INIT;
    private String sbsInAct = "801000026113021001";
    private String sbsInActName = "建行海尔路支行-ZYQD";

    private String sbsOutAct = "801000026111041001";
    private String sbsOutActName = "存放同业-建行海尔路支行";
    private BigDecimal toActAmt = new BigDecimal("0.00");
    private BigDecimal overActAmt = new BigDecimal("0.00");

    @ManagedProperty(value = "#{ibpIfUnionpayTxnService}")
    private IbpIfUnionpayTxnService ibpIfUnionpayTxnService;
    @ManagedProperty(value = "#{ibpSbsTransTxnService}")
    private IbpSbsTransTxnService ibpSbsTransTxnService;
    @ManagedProperty(value = "#{ibpSbsActService}")
    private IbpSbsActService ibpSbsActService;
    @ManagedProperty(value = "#{unipayHistoryQryService}")
    private UnipayHistoryQryService unipayHistoryQryService;

    @PostConstruct
    public void init() {
        try {
            sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();
            DateTime yesterday = new DateTime().minusDays(1);
            String yesterdayStr = yesterday.toString("yyyy-MM-dd");
            qryParam.setBEGIN_DATE(yesterdayStr);
            qryParam.setEND_DATE(yesterdayStr);
            qryParam.setBIZ_ID("ZYQD");
            initDetList();
        } catch (Exception e) {
            logger.error("初始化时出现错误。", e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public void onQuery() {

        try {

            DateTime begin = new DateTime(qryParam.getBEGIN_DATE());
            DateTime end = new DateTime(qryParam.getEND_DATE());
            DateTime today = new DateTime(new DateTime().toString("yyyy-MM-dd"));
            if (!begin.isBefore(today) || !end.isBefore(today)) {
                MessageUtil.addError("今日资金可能未到账，日期请选择昨日或之前。");
                return;
            } else if (begin.isAfter(end)) {
                MessageUtil.addError("起始日期不能大于截止日期。");
                return;
            }

            if (StringUtils.isEmpty(qryParam.getBIZ_ID())) {
                qryParam.setBIZ_ID("ZYQD");
            }
            List<UnipayQryResult> upaylist = new ArrayList<UnipayQryResult>();

            Map<String, String> rtnMainMsgMap = unipayHistoryQryService.queryCurrentData(qryParam, upaylist);

            String rtn_code = rtnMainMsgMap.get("RTN_CODE");
            if (!rtn_code.equals("0000")) {
                MessageUtil.addWarn("银联返回信息：[" + rtn_code + "] " + rtnMainMsgMap.get("ERR_MSG"));
                return;
            } else {
                // 保存银联明细
                logger.info("本次查询到自有渠道银联代扣记录笔数：" + upaylist.size());
                int cnt = ibpIfUnionpayTxnService.insert(upaylist);
                logger.info("本次保存自有渠道银联代扣记录笔数：" + cnt);

                if (upaylist.isEmpty()) {
                    MessageUtil.addWarn("银联返回代扣记录为空！");
                    return;
                } else {
                    initDetList();
                }
            }

        } catch (Exception e) {
            logger.error("获取代扣记录时出错", e);
            MessageUtil.addError("获取代扣记录时出错。" + e.getMessage() + e.getCause());
        }
        return;

    }


    // 全部
    public void onBookAll() {

        if (detlList.isEmpty()) {
            MessageUtil.addWarn("没有待入账记录！");
            return;
        }

        bookList(detlList);
        initDetList();
        sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();

    }

    private void initDetList() {
        detlList = ibpIfUnionpayTxnService.qryUnionpayTxnsByBookFlag(BillStatus.INIT, qryParam.getBEGIN_DATE(), qryParam.getEND_DATE());
        toActAmt = new BigDecimal("0.00");
        for (IbpIfUnionpayTxn record : detlList) {
            toActAmt = toActAmt.add(record.getAmount());
        }
    }

    // 多笔
    public void onBookMulti() {

        if (detlList.isEmpty()) {
            MessageUtil.addWarn("没有待入账记录！");
            return;
        }

        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addInfo("请选择至少一项纪录！");
            return;
        } else {
            List<IbpIfUnionpayTxn> txnList = Arrays.asList(selectedRecords);
            bookList(txnList);
        }
        initDetList();
        sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();

    }

    private void bookList(List<IbpIfUnionpayTxn> txnList) {
        try {

            overActAmt = new BigDecimal("0.00");
            for (IbpIfUnionpayTxn record : txnList) {

                if (ibpIfUnionpayTxnService.isConflict(record)) {
                    MessageUtil.addError("并发冲突，刷新页面后重新操作！序号：" + record.getSn());
                    return;
                }
                IbpSbsTranstxn txn = new IbpSbsTranstxn();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//                txn.setSerialno(record.getCompleteTime().substring(0, 8) + record.getSn().substring(record.getSn().length() - 10));
                txn.setSerialno(ibpSbsTransTxnService.qryMaxSerialNo());
                txn.setOutAct(sbsOutAct);
                txn.setInAct(sbsInAct);
                txn.setInActnam(sbsInActName);
                txn.setTxnamt(record.getAmount());
                txn.setTxntime(sdf.format(new Date()));
                txn.setTxncode("aa41");
                txn.setOperid(SystemService.getOperatorManager().getOperatorId());
                String formCode = ibpSbsTransTxnService.executeSBSTxn(txn, "自有渠道银联代扣");
                logger.info(txn.getSerialno() + " SBS返回码:" + formCode);
                if ("T531".equals(formCode) || "T999".equals(formCode)) {
                    txn.setFormcode(formCode);
                    // 保存交易记录、提示交易结果、更新待入账记录版本号、入账账户置空
                    ibpSbsTransTxnService.insertTxn(txn);
                    record.setBookflag(BillStatus.ACCOUNT_SUCCESS.getCode());
                    ibpIfUnionpayTxnService.update(record);
                    overActAmt = overActAmt.add(txn.getTxnamt());
                    sbsActList.clear();
                } else {
                    MessageUtil.addError("入账失败：SBS返回码 " + formCode + " sn:" + record.getSn());
                    return;
                }
                //
            }
            MessageUtil.addInfo("SBS入账完成，本次入账金额：" + overActAmt.toString());
        } catch (Exception e) {
            logger.error("交易出现异常。", e);
            MessageUtil.addError("交易出现异常!");
            initDetList();
            sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();
            return;
        }
    }


    //====================================================================================


    public List<IbpIfUnionpayTxn> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<IbpIfUnionpayTxn> detlList) {
        this.detlList = detlList;
    }

    public List<IbpSbsTranstxn> getSbsTxnList() {
        return sbsTxnList;
    }

    public void setSbsTxnList(List<IbpSbsTranstxn> sbsTxnList) {
        this.sbsTxnList = sbsTxnList;
    }

    public List<SelectItem> getSbsActList() {
        return sbsActList;
    }

    public void setSbsActList(List<SelectItem> sbsActList) {
        this.sbsActList = sbsActList;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public String getSbsOutAct() {
        return sbsOutAct;
    }

    public void setSbsOutAct(String sbsOutAct) {
        this.sbsOutAct = sbsOutAct;
    }

    public IbpIfUnionpayTxnService getIbpIfUnionpayTxnService() {
        return ibpIfUnionpayTxnService;
    }

    public void setIbpIfUnionpayTxnService(IbpIfUnionpayTxnService ibpIfUnionpayTxnService) {
        this.ibpIfUnionpayTxnService = ibpIfUnionpayTxnService;
    }

    public IbpSbsTransTxnService getIbpSbsTransTxnService() {
        return ibpSbsTransTxnService;
    }

    public void setIbpSbsTransTxnService(IbpSbsTransTxnService ibpSbsTransTxnService) {
        this.ibpSbsTransTxnService = ibpSbsTransTxnService;
    }

    public IbpSbsActService getIbpSbsActService() {
        return ibpSbsActService;
    }

    public void setIbpSbsActService(IbpSbsActService ibpSbsActService) {
        this.ibpSbsActService = ibpSbsActService;
    }

    public UnipayHistoryQryService getUnipayHistoryQryService() {
        return unipayHistoryQryService;
    }

    public void setUnipayHistoryQryService(UnipayHistoryQryService unipayHistoryQryService) {
        this.unipayHistoryQryService = unipayHistoryQryService;
    }

    public List<IbpIfUnionpayTxn> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<IbpIfUnionpayTxn> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public UnipayQryParam getQryParam() {
        return qryParam;
    }

    public void setQryParam(UnipayQryParam qryParam) {
        this.qryParam = qryParam;
    }

    public String getSbsInAct() {
        return sbsInAct;
    }

    public void setSbsInAct(String sbsInAct) {
        this.sbsInAct = sbsInAct;
    }

    public IbpIfUnionpayTxn[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(IbpIfUnionpayTxn[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public String getSbsInActName() {
        return sbsInActName;
    }

    public void setSbsInActName(String sbsInActName) {
        this.sbsInActName = sbsInActName;
    }

    public String getSbsOutActName() {
        return sbsOutActName;
    }

    public void setSbsOutActName(String sbsOutActName) {
        this.sbsOutActName = sbsOutActName;
    }

    public BigDecimal getToActAmt() {
        return toActAmt;
    }

    public void setToActAmt(BigDecimal toActAmt) {
        this.toActAmt = toActAmt;
    }

    public BigDecimal getOverActAmt() {
        return overActAmt;
    }

    public void setOverActAmt(BigDecimal overActAmt) {
        this.overActAmt = overActAmt;
    }
}
