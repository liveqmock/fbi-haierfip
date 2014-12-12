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
 * ���е�����ϸ-SBS����
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
            logger.error("��ʼ��ʱ���ִ���", e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "��ʼ��ʱ���ִ���", "�������ݿ�������⡣"));
        }

    }

    public void onBook() {
        if (selectedRecord == null) {
            MessageUtil.addError("����ѡ��һ�ʴ����˼�¼!");
            return;
        }
       /* if (StringUtils.isEmpty(sbsAct)) {
            MessageUtil.addError("��������ת���˺�!");
            return;
        }
        sbsAct = sbsAct.trim();
        if (!sbsAct.startsWith("8010")) sbsAct = "8010" + sbsAct;
        int actLength = sbsAct.length();
        if (actLength != 18) {
            MessageUtil.addError("�����˺ų��ȴ���!");
            return;
        } else if (!sbsAct.endsWith("001")) {
            MessageUtil.addError("�˺űұ����!");
            return;
        } else {
            String apcode = sbsAct.substring(actLength - 7, actLength - 3);
            if (!(apcode.equals("2017") || apcode.equals("2301") || apcode.equals("2033"))) {
                MessageUtil.addError("�˺ź��������!");
                return;
            }
        }*/

        if (ibpIfCcbTxnService.isConflict(selectedRecord)) {
            MessageUtil.addError("������ͻ��ˢ��ҳ������²�����");
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
            logger.info("SBS������:" + formCode);
            if ("T531".equals(formCode) || "T999".equals(formCode)) {
                txn.setFormcode(formCode);
                // ���潻�׼�¼����ʾ���׽�������´����˼�¼�汾�š������˻��ÿ�
                ibpSbsTransTxnService.insertTxn(txn);
                selectedRecord.setBookflag(BillStatus.ACCOUNT_SUCCESS.getCode());
                ibpIfCcbTxnService.update(selectedRecord);
                MessageUtil.addInfo("���˳ɹ������:" + df.format(txn.getTxnamt()));
                sbsAct = "";
                sbsActName = " ";
                detlList = ibpIfCcbTxnService.qryCcbTxnsByBookFlag(BillStatus.INIT);
                sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();
                selectedRecord = null;
                actMap.clear();
                sbsActList.clear();
            } else {
                MessageUtil.addError("����ʧ�ܣ�SBS������ " + formCode);
            }
            //
        } catch (Exception e) {
            logger.error("���׳����쳣��", e);
            MessageUtil.addError("���׳����쳣!");
            return;
        }

    }

    public void qryActs() {


        if (StringUtils.isEmpty(sbsActName)) {
            MessageUtil.addWarn("��������SBSת���˻�����");
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
            MessageUtil.addError("û�в�ѯ���˻���" + sbsActName);
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
