package ibp.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.utils.Message;
import fip.common.utils.MessageUtil;
import fip.service.fip.JobLogService;
import ibp.repository.model.IbpIfCcbTxn;
import ibp.repository.model.IbpSbsTranstxn;
import ibp.repository.model.IbpSbsAct;
import ibp.service.IbpIfCcbTxnService;
import ibp.service.IbpSbsActService;
import ibp.service.IbpSbsTransTxnService;
import org.apache.commons.lang.StringUtils;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 建行到账明细-SBS入账
 */
@ManagedBean
@ViewScoped
public class IfCcbTxnAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(IfCcbTxnAction.class);

    private IbpIfCcbTxn selectedRecord;
    private List<IbpIfCcbTxn> detlList;
    private List<IbpSbsTranstxn> sbsTxnList = new ArrayList<IbpSbsTranstxn>();
    private List<IbpIfCcbTxn> filteredDetlList;
    private List<SelectItem> sbsActList = new ArrayList<SelectItem>();

    private BillStatus status = BillStatus.INIT;
    private String sbsAct = "";
    private String sbsActName = "";
    private String sbsOutAct = "801000026131041001";
    // 2017 2301 2033

    @ManagedProperty(value = "#{ibpIfCcbTxnService}")
    private IbpIfCcbTxnService ibpIfCcbTxnService;
    @ManagedProperty(value = "#{ibpSbsTransTxnService}")
    private IbpSbsTransTxnService ibpSbsTransTxnService;
    @ManagedProperty(value = "#{ibpSbsActService}")
    private IbpSbsActService ibpSbsActService;
    private Map<String, String> actMap = new HashMap<String, String>();
    DecimalFormat df = new DecimalFormat("0.00");

    @PostConstruct
    public void init() {
        try {
            detlList = ibpIfCcbTxnService.qryCcbTxnsByBookFlag(BillStatus.INIT);
            sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();
        } catch (Exception e) {
            logger.error("初始化时出现错误。", e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public void onBook() {
        if (selectedRecord == null) {
            MessageUtil.addError("请先选择一笔待入账记录!");
            return;
        }
       /* if (StringUtils.isEmpty(sbsAct)) {
            MessageUtil.addError("必须输入转入账号!");
            return;
        }
        sbsAct = sbsAct.trim();
        if (!sbsAct.startsWith("8010")) sbsAct = "8010" + sbsAct;
        int actLength = sbsAct.length();
        if (actLength != 18) {
            MessageUtil.addError("入账账号长度错误!");
            return;
        } else if (!sbsAct.endsWith("001")) {
            MessageUtil.addError("账号币别错误!");
            return;
        } else {
            String apcode = sbsAct.substring(actLength - 7, actLength - 3);
            if (!(apcode.equals("2017") || apcode.equals("2301") || apcode.equals("2033"))) {
                MessageUtil.addError("账号核算码错误!");
                return;
            }
        }*/

        if (ibpIfCcbTxnService.isConflict(selectedRecord)) {
            MessageUtil.addError("并发冲突，刷新页面后重新操作！");
            return;
        }

        try {
            IbpSbsTranstxn txn = new IbpSbsTranstxn();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
//            txn.setSerialno(selectedRecord.getTxdate() + selectedRecord.getTxseqid());
            txn.setSerialno(ibpSbsTransTxnService.qryMaxSerialNo());
            txn.setOutAct(sbsOutAct);
            txn.setInAct(sbsAct);
            String inActName = actMap.get(sbsAct);
            txn.setInActnam(inActName == null ? (sbsActName + sbsAct) : inActName);
            txn.setTxnamt(selectedRecord.getTxamount());
            txn.setTxntime(sdf.format(new Date()));
            txn.setTxncode("aa41");
            txn.setOperid(SystemService.getOperatorManager().getOperatorId());
            String formCode = ibpSbsTransTxnService.executeSBSTxn(txn, "N102", selectedRecord.getOutacctname() + "&" + selectedRecord.getAbstractstr());
            logger.info("SBS返回码:" + formCode);
            if ("T531".equals(formCode) || "T999".equals(formCode)) {
                txn.setFormcode(formCode);
                // 保存交易记录、提示交易结果、更新待入账记录版本号、入账账户置空
                ibpSbsTransTxnService.insertTxn(txn);
                selectedRecord.setBookflag(BillStatus.ACCOUNT_SUCCESS.getCode());
                ibpIfCcbTxnService.update(selectedRecord);
                MessageUtil.addInfo("入账成功，金额:" + df.format(txn.getTxnamt()));
                sbsAct = "";
                sbsActName = " ";
                detlList = ibpIfCcbTxnService.qryCcbTxnsByBookFlag(BillStatus.INIT);
                sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();
                selectedRecord = null;
                actMap.clear();
                sbsActList.clear();
            } else {
                MessageUtil.addError("入账失败：SBS返回码 " + formCode);
            }
            //
        } catch (Exception e) {
            logger.error("交易出现异常。", e);
            MessageUtil.addError("交易出现异常!");
            return;
        }

    }

    public void qryActs() {


        if (StringUtils.isEmpty(sbsActName)) {
            MessageUtil.addWarn("必须输入SBS转入账户名！");
            return;
        }

        if (sbsActName.trim().length() < 2) {
            return;
        }

        sbsActName = sbsActName.trim();
        List<IbpSbsAct> acts = ibpSbsActService.qrySbsActByName(sbsActName);
        actMap.clear();
        sbsActList.clear();

        for (IbpSbsAct act : acts) {
            SelectItem item = new SelectItem(act.getActnum(), act.getActnam() + act.getActnum());
            sbsActList.add(item);
            actMap.put(act.getActnum(), act.getActnam());
        }
        if (sbsActList.isEmpty()) {
            MessageUtil.addError("没有查询到账户：" + sbsActName);
            return;
        }

    }


    //====================================================================================

    public IbpIfCcbTxn getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(IbpIfCcbTxn selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public List<IbpIfCcbTxn> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<IbpIfCcbTxn> detlList) {
        this.detlList = detlList;
    }

    public List<IbpIfCcbTxn> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<IbpIfCcbTxn> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }


    public IbpIfCcbTxnService getIbpIfCcbTxnService() {
        return ibpIfCcbTxnService;
    }

    public void setIbpIfCcbTxnService(IbpIfCcbTxnService ibpIfCcbTxnService) {
        this.ibpIfCcbTxnService = ibpIfCcbTxnService;
    }

    public IbpSbsTransTxnService getIbpSbsTransTxnService() {
        return ibpSbsTransTxnService;
    }

    public void setIbpSbsTransTxnService(IbpSbsTransTxnService ibpSbsTransTxnService) {
        this.ibpSbsTransTxnService = ibpSbsTransTxnService;
    }

    public List<SelectItem> getSbsActList() {
        return sbsActList;
    }

    public void setSbsActList(List<SelectItem> sbsActList) {
        this.sbsActList = sbsActList;
    }

    public IbpSbsActService getIbpSbsActService() {
        return ibpSbsActService;
    }

    public void setIbpSbsActService(IbpSbsActService IbpSbsActService) {
        this.ibpSbsActService = IbpSbsActService;
    }

    public String getSbsAct() {
        return sbsAct;
    }

    public void setSbsAct(String sbsAct) {
        this.sbsAct = sbsAct;
    }

    public String getSbsActName() {
        return sbsActName;
    }

    public void setSbsActName(String sbsActName) {
        this.sbsActName = sbsActName;
    }

    public String getSbsOutAct() {
        return sbsOutAct;
    }

    public void setSbsOutAct(String sbsOutAct) {
        this.sbsOutAct = sbsOutAct;
    }

    public List<IbpSbsTranstxn> getSbsTxnList() {
        return sbsTxnList;
    }

    public void setSbsTxnList(List<IbpSbsTranstxn> sbsTxnList) {
        this.sbsTxnList = sbsTxnList;
    }
}
