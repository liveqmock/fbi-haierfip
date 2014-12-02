package ibp.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.utils.MessageUtil;
import ibp.repository.model.*;
import ibp.service.IbpIfUnionpayTxnService;
import ibp.service.IbpSbsActService;
import ibp.service.IbpSbsTransTxnService;
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
 * ����������ϸ-SBS����-��������
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

    private BillStatus status = BillStatus.INIT;
    private String sbsInAct = "801000043902012001";
    private String sbsInActName = "ʯ��ׯ";

    private String sbsOutAct = "801000026131041001";
    private String sbsOutActName = "801000026131041001";

    @ManagedProperty(value = "#{ibpIfUnionpayTxnService}")
    private IbpIfUnionpayTxnService ibpIfUnionpayTxnService;
    @ManagedProperty(value = "#{ibpSbsTransTxnService}")
    private IbpSbsTransTxnService ibpSbsTransTxnService;
    @ManagedProperty(value = "#{ibpSbsActService}")
    private IbpSbsActService ibpSbsActService;
    private Map<String, String> actMap = new HashMap<String, String>();
    DecimalFormat df = new DecimalFormat("0.00");

    @PostConstruct
    public void init() {
        try {
            detlList = ibpIfUnionpayTxnService.qryUnionpayTxnsByBookFlag(BillStatus.INIT);
            sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();
        } catch (Exception e) {
            logger.error("��ʼ��ʱ���ִ���", e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "��ʼ��ʱ���ִ���", "�������ݿ�������⡣"));
        }

    }


    // ȫ��
    public void onBookAll() {


        bookList(detlList);
        detlList = ibpIfUnionpayTxnService.qryUnionpayTxnsByBookFlag(BillStatus.INIT);
        sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();

    }

    // ���
    public void onBookMulti() {
        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addInfo("��ѡ������һ���¼��");
            return;
        } else {
            List<IbpIfUnionpayTxn> txnList = Arrays.asList(selectedRecords);
            bookList(txnList);
        }
        detlList = ibpIfUnionpayTxnService.qryUnionpayTxnsByBookFlag(BillStatus.INIT);
        sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();

    }

    private void bookList(List<IbpIfUnionpayTxn> txnList) {
        for (IbpIfUnionpayTxn record : txnList) {

            if (ibpIfUnionpayTxnService.isConflict(record)) {
                MessageUtil.addError("������ͻ��ˢ��ҳ������²�������ţ�" + record.getSn());
                return;
            }
            try {
                IbpSbsTranstxn txn = new IbpSbsTranstxn();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
                txn.setSerialno(record.getQuerySn() + record.getSn());
                txn.setOutAct(sbsOutAct);
                txn.setInAct(sbsInAct);
                txn.setInActnam(sbsInActName);
                txn.setTxnamt(record.getAmount());
                txn.setTxntime(sdf.format(new Date()));
                txn.setTxncode("aa41");
                txn.setOperid(SystemService.getOperatorManager().getOperatorId());
                String formCode = ibpSbsTransTxnService.executeSBSTxn(txn, "����������������");
                logger.info(txn.getSerialno() + " SBS������:" + formCode);
                if ("T531".equals(formCode) || "T999".equals(formCode)) {
                    txn.setFormcode(formCode);
                    // ���潻�׼�¼����ʾ���׽�������´����˼�¼�汾�š������˻��ÿ�
                    ibpSbsTransTxnService.insertTxn(txn);
                    record.setBookflag(BillStatus.ACCOUNT_SUCCESS.getCode());
                    ibpIfUnionpayTxnService.update(record);
                    sbsActList.clear();
                } else {
                    MessageUtil.addError("����ʧ�ܣ�SBS������ " + formCode);
                    return;
                }
                //
            } catch (Exception e) {
                logger.error("���׳����쳣��", e);
                MessageUtil.addError("���׳����쳣!");
                detlList = ibpIfUnionpayTxnService.qryUnionpayTxnsByBookFlag(BillStatus.INIT);
                sbsTxnList = ibpSbsTransTxnService.qryTodayTrans();
                return;
            }
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

    public List<IbpIfUnionpayTxn> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<IbpIfUnionpayTxn> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
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
}
