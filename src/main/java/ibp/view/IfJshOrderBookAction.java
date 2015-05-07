package ibp.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.utils.MessageUtil;
import ibp.repository.model.IbpJshOrder;
import ibp.repository.model.IbpSbsTranstxn;
import ibp.service.IbpJshOrderActService;
import ibp.service.IbpNetbankExcelTxnService;
import ibp.service.IbpSbsActService;
import ibp.service.IbpSbsTransTxnService;
import org.primefaces.model.UploadedFile;
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
 * JSH订单单据分款SBS入账
 */
@ManagedBean
@ViewScoped
public class IfJshOrderBookAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(IfJshOrderBookAction.class);

    private List<IbpJshOrder> detlList = new ArrayList<IbpJshOrder>();
    private List<IbpJshOrder> sbsTxnList = new ArrayList<IbpJshOrder>();
    private List<IbpJshOrder> filteredDetlList;
    private IbpJshOrder[] selectedRecords;
    private List<SelectItem> sbsActList = new ArrayList<SelectItem>();

    private BillStatus status = BillStatus.INIT;
    //    private String sbsOutAct = "801002115702013001";
    private String sbsOutAct = "801000026123021001";
    private String totalamt = "0.00";
    private String sbsTotalAmt = "0.00";
    private String sbsTxnDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
    private String orderTxnDate = new SimpleDateFormat("yyyyMMdd").format(new Date());

    private int cnt = 0;
    private int sbsCnt = 0;
    // 2017 2301 2033

    @ManagedProperty(value = "#{ibpJshOrderActService}")
    private IbpJshOrderActService ibpJshOrderActService;
    @ManagedProperty(value = "#{ibpSbsTransTxnService}")
    private IbpSbsTransTxnService ibpSbsTransTxnService;
    DecimalFormat df = new DecimalFormat("0.00");

    @PostConstruct
    public void init() {
        try {

            initList();
        } catch (Exception e) {
            logger.error("初始化时出现错误。", e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public void initList() {
        detlList = ibpJshOrderActService.qryInitOrders();
        if (detlList != null) {
            cnt = detlList.size();
        }
        BigDecimal bd = new BigDecimal(0.00);
        for (IbpJshOrder record : detlList) {
            bd = bd.add(record.getTxnAmt());
        }
        totalamt = bd.toString();
        sbsTxnList = ibpJshOrderActService.qryActOrdersByDate(sbsTxnDate);
        if (sbsTxnList != null) {
            sbsCnt = sbsTxnList.size();
            bd = new BigDecimal(0.00);
            for (IbpJshOrder sbs : sbsTxnList) {
                bd = bd.add(sbs.getTxnAmt());
            }
            sbsTotalAmt = bd.toString();
        }
    }

    public void onQry() {
        sbsCnt = 0;
        sbsTotalAmt = "0.00";
        sbsTxnList = ibpJshOrderActService.qryActOrdersByDate(sbsTxnDate);
        if (sbsTxnList == null || sbsTxnList.isEmpty()) {
            MessageUtil.addWarn("没有查询到数据！");
            return;
        }
        sbsCnt = sbsTxnList.size();
        BigDecimal bd = new BigDecimal(0.00);
        for (IbpJshOrder sbs : sbsTxnList) {
            bd = bd.add(sbs.getTxnAmt());
        }
        sbsTotalAmt = bd.toString();
    }




    public void onBook() {
        bookList(detlList);

    }

    public void onMultiBook() {
        if (selectedRecords == null || selectedRecords.length < 1) {
            MessageUtil.addError("至少选择一笔待入账记录!");
        }
        bookList(Arrays.asList(selectedRecords));
    }

    public void bookList(List<IbpJshOrder> bookList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String txnTime = sdf.format(new Date());
        String sbsActDate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String operId = SystemService.getOperatorManager().getOperatorId();
        try {
            int cnt = 0;
            for (IbpJshOrder record : bookList) {
                if (ibpJshOrderActService.isConflict(record)) {
                    MessageUtil.addError("并发入账冲突，刷新页面后重新操作！");
                    break;
                }
                IbpSbsTranstxn txn = new IbpSbsTranstxn();
//            txn.setSerialno(selectedRecord.getTxdate() + selectedRecord.getTxseqid());
                txn.setSerialno(ibpSbsTransTxnService.qryMaxSerialNo());
                txn.setOutAct(sbsOutAct);
                txn.setInAct(record.getActno());
                txn.setInActnam(record.getActname());
                txn.setTxnamt(record.getTxnAmt());
                txn.setTxntime(txnTime);
                txn.setTxncode("N104");
                txn.setOperid(operId);
                String formCode = ibpSbsTransTxnService.executeSBSTxn(txn, "N104", "巨商汇9109001");
                logger.info(txn.getSerialno() + "  SBS返回码:" + formCode);
                if ("T531".equals(formCode) || "T999".equals(formCode)) {
                    txn.setFormcode(formCode);
                    // 保存交易记录、提示交易结果、更新待入账记录版本号、入账账户置空
                    ibpSbsTransTxnService.insertTxn(txn);
                    record.setFormcode(formCode);
                    record.setFormmsg("交易成功");
                    record.setSbsSerialno(txn.getSerialno());
                    record.setSbsTxncode("aa41");
                    record.setSbsTxndate(sbsActDate);
                    record.setOperid(operId);
                    ibpJshOrderActService.update(record);
                    cnt++;
                } else {
                    MessageUtil.addError("转入 " + record.getActname() + record.getActno() + " 入账失败：SBS返回码 " + formCode);
                    break;
                }
            }
            if (cnt > 0) {
                MessageUtil.addInfo("入账成功笔数：" + cnt);
            }
            initList();

            //
        } catch (Exception e) {
            logger.error("交易出现异常。", e);
            MessageUtil.addError("交易出现异常!");
            return;
        }
    }


    //====================================================================================


    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public List<IbpJshOrder> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<IbpJshOrder> detlList) {
        this.detlList = detlList;
    }

    public List<IbpJshOrder> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<IbpJshOrder> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public DecimalFormat getDf() {
        return df;
    }

    public void setDf(DecimalFormat df) {
        this.df = df;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }


    public List<SelectItem> getSbsActList() {
        return sbsActList;
    }

    public void setSbsActList(List<SelectItem> sbsActList) {
        this.sbsActList = sbsActList;
    }

    public String getSbsOutAct() {
        return sbsOutAct;
    }

    public void setSbsOutAct(String sbsOutAct) {
        this.sbsOutAct = sbsOutAct;
    }

    public IbpSbsTransTxnService getIbpSbsTransTxnService() {
        return ibpSbsTransTxnService;
    }

    public void setIbpSbsTransTxnService(IbpSbsTransTxnService ibpSbsTransTxnService) {
        this.ibpSbsTransTxnService = ibpSbsTransTxnService;
    }

    public IbpJshOrder[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(IbpJshOrder[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public String getSbsTxnDate() {
        return sbsTxnDate;
    }

    public void setSbsTxnDate(String sbsTxnDate) {
        this.sbsTxnDate = sbsTxnDate;
    }

    public String getSbsTotalAmt() {
        return sbsTotalAmt;
    }

    public void setSbsTotalAmt(String sbsTotalAmt) {
        this.sbsTotalAmt = sbsTotalAmt;
    }

    public int getSbsCnt() {
        return sbsCnt;
    }

    public void setSbsCnt(int sbsCnt) {
        this.sbsCnt = sbsCnt;
    }

    public List<IbpJshOrder> getSbsTxnList() {
        return sbsTxnList;
    }

    public void setSbsTxnList(List<IbpJshOrder> sbsTxnList) {
        this.sbsTxnList = sbsTxnList;
    }

    public String getOrderTxnDate() {
        return orderTxnDate;
    }

    public void setOrderTxnDate(String orderTxnDate) {
        this.orderTxnDate = orderTxnDate;
    }

    public IbpJshOrderActService getIbpJshOrderActService() {
        return ibpJshOrderActService;
    }

    public void setIbpJshOrderActService(IbpJshOrderActService ibpJshOrderActService) {
        this.ibpJshOrderActService = ibpJshOrderActService;
    }


}
