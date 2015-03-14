package fip.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.*;
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
 * �������۴���ȫ����DEP APP�ӿڣ� for �������Ŵ�ϵͳccms.
 * User: zhanrui
 * Date: 2012-03-18
 * Time: 12:52:46
 */
@ManagedBean
@ViewScoped
public class UnionpayDepCcmsAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayDepCcmsAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> filteredDetlList;
    private FipCutpaydetl[] selectedRecords;

    private List<FipCutpaydetl> needQueryDetlList;
    private List<FipCutpaydetl> filteredNeedQueryDetlList;
    private FipCutpaydetl[] selectedNeedQryRecords;

    private List<FipCutpaydetl> failureDetlList;
    private List<FipCutpaydetl> filteredFailureDetlList;
    private FipCutpaydetl[] selectedFailRecords;

    private List<FipCutpaydetl> successDetlList;
    private List<FipCutpaydetl> filteredSuccessDetlList;
    private FipCutpaydetl[] selectedAccountRecords;
    private FipCutpaydetl[] selectedConfirmAccountRecords;

    private List<FipCutpaydetl> actDetlList;


    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl selectedRecord;


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

    private String title="";

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
                if (this.bizType.equals(BizType.XFNEW)) {
                    this.title = "�����Ŵ�ϵͳ";
                } else if (this.bizType.equals(BizType.XFJR)) {
                    this.title = "���ѽ���ϵͳ";
                } else {
                    this.title = "====ϵͳ����=======";
                }
            }
        } catch (Exception e) {
            logger.error("��ʼ��ʱ���ִ���");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "��ʼ��ʱ���ִ���", "�������ݿ�������⡣"));
        }

    }

    public synchronized void initList() {
        detlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.INIT);
        logger.info("Step1");
        detlList.addAll(billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.RESEND_PEND));
        logger.info("Step2");
        needQueryDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        logger.info("Step3");
        failureDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_FAILED);
        logger.info("Step4");
        successDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_SUCCESS);
        logger.info("Step5");
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalSuccessCount = successDetlList.size();
        this.totalFailureAmt = sumTotalAmt(failureDetlList);
        this.totalFailureCount = failureDetlList.size();
        logger.info("Step end");
    }

    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList) {
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public synchronized String onSendRequestAll() {
        if (detlList.isEmpty()) {
            MessageUtil.addWarn("û��Ҫ���͵ļ�¼��");
            return null;
        }
        try {
            for (FipCutpaydetl cutpaydetl : this.detlList) {
                processOneCutpayRequestRecord(cutpaydetl);
            }
            MessageUtil.addInfo("���ݷ��ͽ�������鿴��������ϸ��");
        } catch (Exception e) {
            MessageUtil.addError("���ݷ��ͽ��������쳣" + e.getMessage());
        }
        initList();
        return null;
    }

    public synchronized String onSendRequestMulti() {
        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addWarn("δѡ��Ҫ����ļ�¼��");
            return null;
        }
        try {
            for (FipCutpaydetl cutpaydetl : this.selectedRecords) {
                processOneCutpayRequestRecord(cutpaydetl);
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
            for (FipCutpaydetl cutpaydetl : this.needQueryDetlList) {
                processOneQueryRecord(cutpaydetl);
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
            for (FipCutpaydetl cutpaydetl : this.selectedNeedQryRecords) {
                processOneQueryRecord(cutpaydetl);
            }
            MessageUtil.addInfo("��ѯ���׷��ͽ�������鿴��������ϸ��");
        } catch (Exception e) {
            MessageUtil.addError("��ѯ���״����쳣" + e.getMessage());
        }
        initList();
        return null;
    }

    /**
     * �ۿ�����
     *
     * @param record
     * @return
     */
    private void processOneCutpayRequestRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvT1001001Message(record);
            appendNewJoblog(pkid, "���Ϳۿ�����", "���������ۿ���������ɡ�");
        } catch (Exception e) {
            appendNewJoblog(pkid, "���Ϳۿ�����", "���������ۿ�������ʧ��." + e.getMessage());
            throw new RuntimeException("���ݷ����쳣������ϵͳ��·���·��ͣ�" + e.getMessage());
        }
    }

    /**
     * ��ѯ����
     *
     * @param record
     * @return
     */
    private void processOneQueryRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        try {
            unipayDepService.sendAndRecvCutpayT1003001Message(record);
            appendNewJoblog(pkid, "���Ͳ�ѯ����", "����������ѯ��������ɡ�");
        } catch (Exception e) {
            appendNewJoblog(pkid, "���Ͳ�ѯ����", "����������ѯ������ʧ��." + e.getMessage());
            throw new RuntimeException("���ݷ����쳣������ϵͳ��·���·��ͣ�" + e.getMessage());
        }
    }


    //TODO  ��ʱ���÷���ģ�壬��ģ����clientno�ֶ�������
    public String onExportFailureList() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return null;
        } else {
            String excelFilename = "ʵʱ����ʧ�ܼ�¼�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, "xfCutpayList.xls", this.failureDetlList);
        }
        return null;
    }

    public String onExportSuccessList() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("��¼Ϊ��...");
            return null;
        } else {
            String excelFilename = "ʵʱ���۳ɹ���¼�嵥-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, "xfCutpayList.xls", this.successDetlList);
        }
        return null;
    }

    //��дȫ��������¼�����浵
    public String onWriteBackAllUncertainlyRecords() {
        if (this.needQueryDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackCutPayRecord2CCMS(this.needQueryDetlList, false, this.bizType);
                MessageUtil.addWarn("��д�ɹ���¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    //��дȫ���ɹ���¼ ���浵����
    public String onWriteBackAllSuccessCutpayRecords() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackCutPayRecord2CCMS(this.successDetlList, true, this.bizType);
                MessageUtil.addWarn("��д�ɹ���¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackAllFailCutpayRecords() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackCutPayRecord2CCMS(this.failureDetlList, true, this.bizType);
                //count = billManagerService.archiveBillsNoCheckRecvision(this.failureDetlList);
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
                List<FipCutpaydetl> cutpaydetlList = Arrays.asList(this.selectedNeedQryRecords);
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, false, this.bizType);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedSuccessCutpayRecords() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipCutpaydetl> cutpaydetlList = Arrays.asList(this.selectedConfirmAccountRecords);
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, true, this.bizType);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String onWriteBackSelectedFailCutpayRecords() {
        if (this.selectedFailRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipCutpaydetl> cutpaydetlList = Arrays.asList(this.selectedFailRecords);
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, true, this.bizType);
//                billManagerService.archiveBills(cutpaydetlList);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initList();
        return null;
    }

    public String reset() {
        this.detlRecord = new FipCutpaydetl();
        return null;
    }


    private void appendNewJoblog(String pkid, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, "fip_cutpaydetl", jobname, jobdesc, userid, username);
    }
    //====================================================================================


    public FipCutpaydetl[] getSelectedConfirmAccountRecords() {
        return selectedConfirmAccountRecords;
    }

    public void setSelectedConfirmAccountRecords(FipCutpaydetl[] selectedConfirmAccountRecords) {
        this.selectedConfirmAccountRecords = selectedConfirmAccountRecords;
    }

    public List<FipCutpaydetl> getActDetlList() {
        return actDetlList;
    }

    public void setActDetlList(List<FipCutpaydetl> actDetlList) {
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


    public FipCutpaydetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipCutpaydetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipCutpaydetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaydetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public List<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipCutpaydetl> detlList) {
        this.detlList = detlList;
    }


    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public List<FipCutpaydetl> getNeedQueryDetlList() {
        return needQueryDetlList;
    }

    public void setNeedQueryDetlList(List<FipCutpaydetl> needQueryDetlList) {
        this.needQueryDetlList = needQueryDetlList;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<FipCutpaydetl> getFailureDetlList() {
        return failureDetlList;
    }

    public void setFailureDetlList(List<FipCutpaydetl> failureDetlList) {
        this.failureDetlList = failureDetlList;
    }

    public List<FipCutpaydetl> getSuccessDetlList() {
        return successDetlList;
    }

    public void setSuccessDetlList(List<FipCutpaydetl> successDetlList) {
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

    public FipCutpaydetl[] getSelectedNeedQryRecords() {
        return selectedNeedQryRecords;
    }

    public void setSelectedNeedQryRecords(FipCutpaydetl[] selectedNeedQryRecords) {
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

    public FipCutpaydetl[] getSelectedAccountRecords() {
        return selectedAccountRecords;
    }

    public void setSelectedAccountRecords(FipCutpaydetl[] selectedAccountRecords) {
        this.selectedAccountRecords = selectedAccountRecords;
    }

    public FipCutpaydetl[] getSelectedFailRecords() {
        return selectedFailRecords;
    }

    public void setSelectedFailRecords(FipCutpaydetl[] selectedFailRecords) {
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

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public List<FipCutpaydetl> getFilteredNeedQueryDetlList() {
        return filteredNeedQueryDetlList;
    }

    public void setFilteredNeedQueryDetlList(List<FipCutpaydetl> filteredNeedQueryDetlList) {
        this.filteredNeedQueryDetlList = filteredNeedQueryDetlList;
    }

    public List<FipCutpaydetl> getFilteredFailureDetlList() {
        return filteredFailureDetlList;
    }

    public void setFilteredFailureDetlList(List<FipCutpaydetl> filteredFailureDetlList) {
        this.filteredFailureDetlList = filteredFailureDetlList;
    }

    public List<FipCutpaydetl> getFilteredSuccessDetlList() {
        return filteredSuccessDetlList;
    }

    public void setFilteredSuccessDetlList(List<FipCutpaydetl> filteredSuccessDetlList) {
        this.filteredSuccessDetlList = filteredSuccessDetlList;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
