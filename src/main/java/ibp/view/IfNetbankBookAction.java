package ibp.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.utils.MessageUtil;
import ibp.repository.model.IbpIfNetbnkTxn;
import ibp.repository.model.IbpSbsTranstxn;
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
 * 建行到账明细-EXCEL导入，SBS入账
 */
@ManagedBean
@ViewScoped
public class IfNetbankBookAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(IfNetbankBookAction.class);

    private List<IbpIfNetbnkTxn> detlList = new ArrayList<IbpIfNetbnkTxn>();
    private List<IbpSbsTranstxn> sbsTxnList = new ArrayList<IbpSbsTranstxn>();
    private List<IbpIfNetbnkTxn> filteredDetlList;
    private List<SelectItem> sbsActList = new ArrayList<SelectItem>();

    private BillStatus status = BillStatus.INIT;
    private String sbsOutAct = "801000026131041001";
    private UploadedFile file;
    private String totalamt = "0.00";
    private int cnt = 0;
    // 2017 2301 2033

    @ManagedProperty(value = "#{ibpNetbankExcelTxnService}")
    private IbpNetbankExcelTxnService ibpNetbankExcelTxnService;
    @ManagedProperty(value = "#{ibpSbsTransTxnService}")
    private IbpSbsTransTxnService ibpSbsTransTxnService;
    @ManagedProperty(value = "#{ibpSbsActService}")
    private IbpSbsActService ibpSbsActService;
    private Map<String, String> actMap = new HashMap<String, String>();
    DecimalFormat df = new DecimalFormat("0.00");

    @PostConstruct
    public void init() {
        try {

            // 若存在未录入转入账户的明细，则等待录入完成
            boolean hasInitTxns = ibpNetbankExcelTxnService.hasInitTxns();
            if (hasInitTxns) {
                MessageUtil.addWarn("有未录入转入账户的明细，暂时不能入账！");
                return;
            } else {
                initList();
            }
        } catch (Exception e) {
            logger.error("初始化时出现错误。", e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public void initList() {
        detlList = ibpNetbankExcelTxnService.qryTxnsToBook();
        cnt = detlList.size();
        BigDecimal bd = new BigDecimal(0.00);
        for (IbpIfNetbnkTxn record : detlList) {
            bd = bd.add(new BigDecimal(record.getCramount().replace(",", "")));
        }
        totalamt = bd.toString();
        sbsTxnList = ibpSbsTransTxnService.qryTodayTrans("N102");

    }


    public void onBook() {


        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String txnTime = sdf.format(new Date());
        String operId = SystemService.getOperatorManager().getOperatorId();
        try {
            for (IbpIfNetbnkTxn record : detlList) {
                if (ibpNetbankExcelTxnService.isConflict(record)) {
                    MessageUtil.addError("并发入账冲突，刷新页面后重新操作！");
                    break;
                }
                IbpSbsTranstxn txn = new IbpSbsTranstxn();
//            txn.setSerialno(selectedRecord.getTxdate() + selectedRecord.getTxseqid());
                txn.setSerialno(ibpSbsTransTxnService.qryMaxSerialNo());
                txn.setOutAct(sbsOutAct);
                txn.setInAct(record.getSbsactno());
                txn.setInActnam(record.getSbsactname());
                txn.setTxnamt(new BigDecimal(record.getCramount().replace(",", "")));
                txn.setTxntime(txnTime);
                txn.setTxncode("N102");
                txn.setOperid(operId);
                String formCode = ibpSbsTransTxnService.executeSBSTxn(txn, "N102", record.getOutacctname() + "&" + record.getAbstractstr());
                logger.info(txn.getSerialno() + "  SBS返回码:" + formCode);
                if ("T531".equals(formCode) || "T999".equals(formCode)) {
                    txn.setFormcode(formCode);
                    // 保存交易记录、提示交易结果、更新待入账记录版本号、入账账户置空
                    ibpSbsTransTxnService.insertTxn(txn);
                    record.setBookflag(BillStatus.ACCOUNT_SUCCESS.getCode());
                    ibpNetbankExcelTxnService.update(record);

                } else {
                    MessageUtil.addError("转入 " + record.getSbsactname() + "：" + record.getSbsactno() + " 入账失败：SBS返回码 " + formCode);
                    break;
                }
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

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public List<IbpIfNetbnkTxn> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<IbpIfNetbnkTxn> detlList) {
        this.detlList = detlList;
    }

    public List<IbpIfNetbnkTxn> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<IbpIfNetbnkTxn> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public IbpNetbankExcelTxnService getIbpNetbankExcelTxnService() {
        return ibpNetbankExcelTxnService;
    }

    public void setIbpNetbankExcelTxnService(IbpNetbankExcelTxnService ibpNetbankExcelTxnService) {
        this.ibpNetbankExcelTxnService = ibpNetbankExcelTxnService;
    }

    public Map<String, String> getActMap() {
        return actMap;
    }

    public void setActMap(Map<String, String> actMap) {
        this.actMap = actMap;
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

    public IbpSbsActService getIbpSbsActService() {
        return ibpSbsActService;
    }

    public void setIbpSbsActService(IbpSbsActService ibpSbsActService) {
        this.ibpSbsActService = ibpSbsActService;
    }
}
