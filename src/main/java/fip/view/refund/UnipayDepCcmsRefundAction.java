package fip.view.refund;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipRefunddetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.CcmsService;
import fip.service.fip.JobLogService;
import fip.service.fip.UnipayDepService;
import fip.view.common.JxlsManager;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.security.OperatorManager;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ������������ȫ����DEP APP�ӿڣ� for �������Ŵ�ϵͳccms.
 * User: zhanrui
 * Date: 2012-07-03
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UnipayDepCcmsRefundAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnipayDepCcmsRefundAction.class);

    private List<FipRefunddetl> detlList;
    private FipRefunddetl[] selectedRecords;

    private List<FipRefunddetl> needQueryDetlList;
    private FipRefunddetl[] selectedNeedQryRecords;

    private List<FipRefunddetl> failureDetlList;
    private FipRefunddetl[] selectedFailRecords;

    private List<FipRefunddetl> successDetlList;
    private FipRefunddetl[] selectedAccountRecords;
    private FipRefunddetl[] selectedConfirmAccountRecords;

    private List<FipRefunddetl> actDetlList;


    private FipRefunddetl detlRecord = new FipRefunddetl();
    private FipRefunddetl selectedRecord;


    private int totalcount;
    private String totalamt;
    private int totalFailureCount;
    private String totalFailureAmt;
    private int totalSuccessCount;
    private String totalSuccessAmt;
    private int totalAccountCount;
    private String totalAccountAmt;

    private BigDecimal totalPrincipalAmt;   //����
    private BigDecimal totalInterestAmt;    //��Ϣ
    private BigDecimal totalFxjeAmt;    //��Ϣ

    private Map<String, String> statusMap = new HashMap<String, String>();

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    @ManagedProperty(value = "#{unipayDepService}")
    private UnipayDepService unipayDepService;
    @ManagedProperty(value = "#{ccmsService}")
    private CcmsService ccmsService;


    private String bizid;
    private String sysid = "";   //�ݲ�����
    private String pkid;
    private BizType bizType;


    private String userid, username;

    @PostConstruct
    public void init() {
        try {
            OperatorManager om = SystemService.getOperatorManager();
            this.userid = om.getOperatorId();
            this.username = om.getOperatorName();

            this.sysid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sysid");
            this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(this.bizid)) {
                this.bizType = BizType.valueOf(this.bizid);
                initList();
            }
        } catch (Exception e) {
            logger.error("��ʼ��ʱ���ִ���");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "��ʼ��ʱ���ִ���", "�������ݿ�������⡣"));
        }

    }

    public void initList() {
        detlList = billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.INIT);
        detlList.addAll(billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.RESEND_PEND));
        needQueryDetlList = billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        failureDetlList = billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_FAILED);
        successDetlList = billManagerService.selectRefundRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_SUCCESS);
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalSuccessCount = successDetlList.size();
        this.totalFailureAmt = sumTotalAmt(failureDetlList);
        this.totalFailureCount = failureDetlList.size();
    }

    private String sumTotalAmt(List<FipRefunddetl> qrydetlList) {
        BigDecimal amt = new BigDecimal(0);
        for (FipRefunddetl detl : qrydetlList) {
            amt = amt.add(detl.getPayamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public String onSendRequestAll() {
        if (detlList.isEmpty()) {
            MessageUtil.addWarn("û��Ҫ���͵ļ�¼��");
            return null;
        }
        try {
            for (FipRefunddetl detl : this.detlList) {
                processOneRefundRequestRecord(detl);
            }
            MessageUtil.addInfo("���ݷ��ͽ�������鿴��������ϸ��");
        } catch (Exception e) {
            MessageUtil.addError("���ݷ��ͽ��������쳣" + e.getMessage());
        }
        initList();
        return null;
    }

    public String onSendRequestMulti() {
        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addWarn("δѡ��Ҫ����ļ�¼��");
            return null;
        }
        try {
            for (FipRefunddetl detl : this.selectedRecords) {
                processOneRefundRequestRecord(detl);
            }
            MessageUtil.addInfo("���ݷ��ͽ�������鿴��������ϸ��");
        } catch (Exception e) {
            MessageUtil.addError("���ݷ��ͽ��������쳣" + e.getMessage());
        }
        initList();
        return null;
    }

    public String onQueryAll() {
        if (needQueryDetlList.isEmpty()) {
            MessageUtil.addWarn("û��Ҫ���͵ļ�¼��");
            return null;
        }
        try {
            for (FipRefunddetl detl : this.needQueryDetlList) {
                processOneQueryRecord(detl);
            }
            MessageUtil.addInfo("��ѯ���׷��ͽ�������鿴��������ϸ��");
        } catch (Exception e) {
            MessageUtil.addError("��ѯ���״����쳣" + e.getMessage());
        }
        initList();
        return null;
    }

    public String onQueryMulti() {
        if (selectedNeedQryRecords == null || selectedNeedQryRecords.length <= 0) {
            MessageUtil.addWarn("û�л�δѡ��Ҫ���͵ļ�¼��");
            return null;
        }
        try {
            for (FipRefunddetl detl : this.selectedNeedQryRecords) {
                processOneQueryRecord(detl);
            }
            MessageUtil.addInfo("��ѯ���׷��ͽ�������鿴��������ϸ��");
        } catch (Exception e) {
            MessageUtil.addError("��ѯ���״����쳣" + e.getMessage());
        }
        initList();
        return null;
    }

    /**
     * ��������
     *
     * @param record
     * @return
     */
    private void processOneRefundRequestRecord(FipRefunddetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvT1001002Message(record);
            appendNewJoblog(pkid, "����������������", "��������������������ɡ�");
        } catch (Exception e) {
            appendNewJoblog(pkid, "����������������", "������������������ʧ��." + e.getMessage());
            throw new RuntimeException("���ݷ����쳣������ϵͳ��·���·��ͣ�" + e.getMessage());
        }
    }

    /**
     * ��ѯ����
     *
     * @param record
     * @return
     */
    private void processOneQueryRecord(FipRefunddetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvRefundT1003001Message(record);
            appendNewJoblog(pkid, "���Ͳ�ѯ����", "����������ѯ��������ɡ�");
        } catch (Exception e) {
            appendNewJoblog(pkid, "���Ͳ�ѯ����", "����������ѯ������ʧ��." + e.getMessage());
            throw new RuntimeException("���ݷ����쳣������ϵͳ��·���·��ͣ�" + e.getMessage());
        }
    }


    public String onExportFailureList() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return null;
        } else {
            String excelFilename = "ʵʱ����ʧ�ܼ�¼�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportRefundList(excelFilename, this.failureDetlList);
        }
        return null;
    }

    public String onExportSuccessList() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return null;
        } else {
            String excelFilename = "ʵʱ�����ɹ���¼�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportRefundList(excelFilename, this.successDetlList);
        }
        return null;
    }


    //zhanrui 20110306
    public String onWriteBackAllUncertainlyRecords() {
        if (this.needQueryDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackRefundRecord2CCMS(this.needQueryDetlList, false, this.bizType);
                MessageUtil.addWarn("��д�ɹ���¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackAllSuccessRecords() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackRefundRecord2CCMS(this.successDetlList, true, this.bizType);
                MessageUtil.addWarn("��д�ɹ���¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackAllFailRecords() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackRefundRecord2CCMS(this.failureDetlList, true, this.bizType);
                billManagerService.archiveRefundBills(this.failureDetlList);
                MessageUtil.addWarn("��д��¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedUncertainlyyRecords() {
        if (this.selectedNeedQryRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipRefunddetl> detlList = Arrays.asList(this.selectedNeedQryRecords);
                count = ccmsService.writebackRefundRecord2CCMS(detlList, false, this.bizType);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedSuccessRecords() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipRefunddetl> detlList = Arrays.asList(this.selectedConfirmAccountRecords);
                count = ccmsService.writebackRefundRecord2CCMS(detlList, true, this.bizType);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedFailRecords() {
        if (this.selectedFailRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipRefunddetl> detlList = Arrays.asList(this.selectedFailRecords);
                count = ccmsService.writebackRefundRecord2CCMS(detlList, true, this.bizType);
                //billManagerService.archiveRefundBills(detlList);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String reset() {
        this.detlRecord = new FipRefunddetl();
        return null;
    }


    private void appendNewJoblog(String pkid, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, "fip_refunddetl", jobname, jobdesc, userid, username);
    }
    //====================================================================================


    public FipRefunddetl[] getSelectedConfirmAccountRecords() {
        return selectedConfirmAccountRecords;
    }

    public void setSelectedConfirmAccountRecords(FipRefunddetl[] selectedConfirmAccountRecords) {
        this.selectedConfirmAccountRecords = selectedConfirmAccountRecords;
    }

    public List<FipRefunddetl> getActDetlList() {
        return actDetlList;
    }

    public void setActDetlList(List<FipRefunddetl> actDetlList) {
        this.actDetlList = actDetlList;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public BigDecimal getTotalPrincipalAmt() {
        return totalPrincipalAmt;
    }

    public BigDecimal getTotalInterestAmt() {
        return totalInterestAmt;
    }

    public BigDecimal getTotalFxjeAmt() {
        return totalFxjeAmt;
    }


    public FipRefunddetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipRefunddetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipRefunddetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipRefunddetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public FipRefunddetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipRefunddetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public List<FipRefunddetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipRefunddetl> detlList) {
        this.detlList = detlList;
    }


    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public List<FipRefunddetl> getNeedQueryDetlList() {
        return needQueryDetlList;
    }

    public void setNeedQueryDetlList(List<FipRefunddetl> needQueryDetlList) {
        this.needQueryDetlList = needQueryDetlList;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<FipRefunddetl> getFailureDetlList() {
        return failureDetlList;
    }

    public void setFailureDetlList(List<FipRefunddetl> failureDetlList) {
        this.failureDetlList = failureDetlList;
    }

    public List<FipRefunddetl> getSuccessDetlList() {
        return successDetlList;
    }

    public void setSuccessDetlList(List<FipRefunddetl> successDetlList) {
        this.successDetlList = successDetlList;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public UnipayDepService getUnipayDepService() {
        return unipayDepService;
    }

    public void setUnipayDepService(UnipayDepService unipayDepService) {
        this.unipayDepService = unipayDepService;
    }

    public FipRefunddetl[] getSelectedNeedQryRecords() {
        return selectedNeedQryRecords;
    }

    public void setSelectedNeedQryRecords(FipRefunddetl[] selectedNeedQryRecords) {
        this.selectedNeedQryRecords = selectedNeedQryRecords;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public int getTotalFailureCount() {
        return totalFailureCount;
    }

    public void setTotalFailureCount(int totalFailureCount) {
        this.totalFailureCount = totalFailureCount;
    }

    public String getTotalFailureAmt() {
        return totalFailureAmt;
    }

    public void setTotalFailureAmt(String totalFailureAmt) {
        this.totalFailureAmt = totalFailureAmt;
    }

    public int getTotalSuccessCount() {
        return totalSuccessCount;
    }

    public void setTotalSuccessCount(int totalSuccessCount) {
        this.totalSuccessCount = totalSuccessCount;
    }

    public String getTotalSuccessAmt() {
        return totalSuccessAmt;
    }

    public void setTotalSuccessAmt(String totalSuccessAmt) {
        this.totalSuccessAmt = totalSuccessAmt;
    }

    public int getTotalAccountCount() {
        return totalAccountCount;
    }

    public void setTotalAccountCount(int totalAccountCount) {
        this.totalAccountCount = totalAccountCount;
    }

    public String getTotalAccountAmt() {
        return totalAccountAmt;
    }

    public void setTotalAccountAmt(String totalAccountAmt) {
        this.totalAccountAmt = totalAccountAmt;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public FipRefunddetl[] getSelectedAccountRecords() {
        return selectedAccountRecords;
    }

    public void setSelectedAccountRecords(FipRefunddetl[] selectedAccountRecords) {
        this.selectedAccountRecords = selectedAccountRecords;
    }

    public FipRefunddetl[] getSelectedFailRecords() {
        return selectedFailRecords;
    }

    public void setSelectedFailRecords(FipRefunddetl[] selectedFailRecords) {
        this.selectedFailRecords = selectedFailRecords;
    }

    public CcmsService getCcmsService() {
        return ccmsService;
    }

    public void setCcmsService(CcmsService ccmsService) {
        this.ccmsService = ccmsService;
    }

    public String getSysid() {
        return sysid;
    }

    public void setSysid(String sysid) {
        this.sysid = sysid;
    }
}
