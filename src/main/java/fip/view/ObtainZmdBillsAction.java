package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.EnumApp;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.JobLogService;
import fip.service.fip.ZmdService;
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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-13
 * Time: ����2:07
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class ObtainZmdBillsAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ObtainZmdBillsAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> filteredDetlList;
    private List<FipCutpaydetl> qrydetlList;
    private List<FipCutpaydetl> filteredQrydetlList;
    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl[] selectedRecords;
    private FipCutpaydetl[] selectedQryRecords;
    private FipCutpaydetl selectedRecord;

    private int totalcount;
    private int totalqrycount;
    private String totalamt;
    private String totalqryamt;
    private BigDecimal totalPrincipalAmt;   //����
    private BigDecimal totalInterestAmt;    //��Ϣ
    private BigDecimal totalFxjeAmt;    //��Ϣ

    private Map<String, String> statusMap = new HashMap<String, String>();

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{zmdService}")
    private ZmdService zmdService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    private BizType bizType;

    private List<SelectItem> billStatusOptions;
    private String title="";

    @PostConstruct
    public void init() {
        try {
            String bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(bizid)) {
                this.bizType = BizType.valueOf(bizid);
                qrydetlList = new ArrayList<FipCutpaydetl>();
                initDetlList();
                if (this.bizType.equals(BizType.ZMD)) {
                    this.title = "ר�������";
                } else {
                    this.title = "====ϵͳ����=======";
                    throw new RuntimeException("ϵͳ����");
                }
            }
        } catch (Exception e) {
            logger.error("��ʼ��ʱ���ִ���");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "��ʼ��ʱ���ִ���", "�������ݿ�������⡣"));
        }
    }

    private synchronized void initDetlList() {
        detlList = billManagerService.selectBillList(this.bizType, BillType.NORMAL, BillStatus.INIT);
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
    }

    private void initSelectItem(EnumApp e){
        List<SelectItem> items = new ArrayList<SelectItem>();
        SelectItem item;
    }

    public synchronized String onQryCms() {
        try {
            qrydetlList = zmdService.doQueryZmdBills(this.bizType, BillType.NORMAL);
            if (qrydetlList.size() == 0) {
                MessageUtil.addWarn("δ��ȡ���Ŵ�ϵͳר����Ĵ��ۼ�¼��");
            }
            this.totalqryamt = sumTotalAmt(qrydetlList);
            this.totalqrycount = qrydetlList.size();
        } catch (Exception e) {
            logger.error("��ȡ��¼ʱ����", e);
            MessageUtil.addError("��ȡ��¼ʱ������" + e.getMessage());
        }
        return null;
    }
    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList){
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public synchronized String onObtain() {
        //TODO ��鱾�ؼ�¼״̬
        try {
            List<String> returnMsgs = new ArrayList<String>();
            int count = zmdService.doObtainZmdBills(this.bizType, BillType.NORMAL, returnMsgs);
            MessageUtil.addWarn("���λ�ȡ��¼����" + count + " ��.");
            for (String returnMsg : returnMsgs) {
                MessageUtil.addWarn(returnMsg);
            }
        } catch (Exception e) {
            logger.error("��ȡ��¼ʱ����", e);
            MessageUtil.addError("��ȡ��¼ʱ������" + e.getMessage());
        }
        initDetlList();
        return null;
    }

    public synchronized String onDeleteAll() {
        if (detlList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ�ա�");
            return null;
        }
        billManagerService.deleteBillsByKey(detlList);
        initDetlList();
        return null;
    }

    public synchronized String onDeleteMulti() {
        if (selectedRecords.length == 0) {
            MessageUtil.addWarn("����ѡ���¼��");
            return null;
        }

        billManagerService.deleteBillsByKey(Arrays.asList(selectedRecords));
        initDetlList();
        MessageUtil.addInfo("ɾ����¼�ɹ���");
        return null;
    }

    //============================================================================================

    public List<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipCutpaydetl> detlList) {
        this.detlList = detlList;
    }

    public FipCutpaydetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipCutpaydetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public FipCutpaydetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaydetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public int getTotalcount() {
        return this.totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public BigDecimal getTotalPrincipalAmt() {
        return totalPrincipalAmt;
    }

    public void setTotalPrincipalAmt(BigDecimal totalPrincipalAmt) {
        this.totalPrincipalAmt = totalPrincipalAmt;
    }

    public BigDecimal getTotalInterestAmt() {
        return totalInterestAmt;
    }

    public void setTotalInterestAmt(BigDecimal totalInterestAmt) {
        this.totalInterestAmt = totalInterestAmt;
    }

    public BigDecimal getTotalFxjeAmt() {
        return totalFxjeAmt;
    }

    public void setTotalFxjeAmt(BigDecimal totalFxjeAmt) {
        this.totalFxjeAmt = totalFxjeAmt;
    }

    public Map<String, String> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(Map<String, String> statusMap) {
        this.statusMap = statusMap;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public ZmdService getZmdService() {
        return zmdService;
    }

    public void setZmdService(ZmdService zmdService) {
        this.zmdService = zmdService;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public List<FipCutpaydetl> getQrydetlList() {
        return qrydetlList;
    }

    public void setQrydetlList(List<FipCutpaydetl> qrydetlList) {
        this.qrydetlList = qrydetlList;
    }

    public int getTotalqrycount() {
        return totalqrycount;
    }

    public void setTotalqrycount(int totalqrycount) {
        this.totalqrycount = totalqrycount;
    }

    public String getTotalqryamt() {
        return totalqryamt;
    }

    public void setTotalqryamt(String totalqryamt) {
        this.totalqryamt = totalqryamt;
    }

    public FipCutpaydetl[] getSelectedQryRecords() {
        return selectedQryRecords;
    }

    public void setSelectedQryRecords(FipCutpaydetl[] selectedQryRecords) {
        this.selectedQryRecords = selectedQryRecords;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public List<SelectItem> getBillStatusOptions() {
        return billStatusOptions;
    }

    public void setBillStatusOptions(List<SelectItem> billStatusOptions) {
        this.billStatusOptions = billStatusOptions;
    }

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public List<FipCutpaydetl> getFilteredQrydetlList() {
        return filteredQrydetlList;
    }

    public void setFilteredQrydetlList(List<FipCutpaydetl> filteredQrydetlList) {
        this.filteredQrydetlList = filteredQrydetlList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}