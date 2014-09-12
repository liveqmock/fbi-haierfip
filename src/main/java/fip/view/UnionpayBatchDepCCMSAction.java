package fip.view;

import fip.common.constant.BillStatus;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.CcmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * ������������.
 * User: zhanrui
 * Date: 2014-08-13
 */
@ManagedBean
@ViewScoped
public class UnionpayBatchDepCCMSAction extends UnionpayBatchAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayBatchDepCCMSAction.class);

    private List<FipCutpaydetl> needQueryDetlList;
    private FipCutpaydetl[] selectedNeedQryRecords;
    private List<FipCutpaydetl> filteredNeedQueryDetlList;

    @ManagedProperty(value = "#{ccmsService}")
    protected CcmsService ccmsService;


    @PostConstruct
    public void init() {
        super.init();
        //needQueryDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        needQueryDetlList = billManagerService.selectRecords4UnipayBatchDetail(this.bizType, BillStatus.CUTPAY_QRY_PEND);
    }

    protected void initDataList(){
        //needQueryDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        needQueryDetlList = billManagerService.selectRecords4UnipayBatchDetail(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        super.initBaseDataList();
    }
    //��дȫ��������¼�����浵
    public String onWriteBackAllUncertainlyRecords() {
        if (this.needQueryDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackCutPayRecord2CCMS(this.needQueryDetlList, false);
                MessageUtil.addWarn("��д�ɹ���¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initDataList();
        return null;
    }
    public String onWriteBackSelectedUncertainlyRecords() {
        if (this.selectedNeedQryRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼...");
            return null;
        } else {
            try {
                int count = 0;
                List<FipCutpaydetl> cutpaydetlList = Arrays.asList(this.selectedNeedQryRecords);
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, false);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initDataList();
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
                count = ccmsService.writebackCutPayRecord2CCMS(this.successDetlList, true);
                MessageUtil.addWarn("��д�ɹ���¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initDataList();
        return null;
    }

    public String onWriteBackAllFailCutpayRecords() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = ccmsService.writebackCutPayRecord2CCMS(this.failureDetlList, true);
                MessageUtil.addWarn("��д��¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initDataList();
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
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, true);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initDataList();
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
                count = ccmsService.writebackCutPayRecord2CCMS(cutpaydetlList, true);
                MessageUtil.addWarn("���¼�¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initDataList();
        return null;
    }

    public List<FipCutpaydetl> getNeedQueryDetlList() {
        return needQueryDetlList;
    }

    public void setNeedQueryDetlList(List<FipCutpaydetl> needQueryDetlList) {
        this.needQueryDetlList = needQueryDetlList;
    }


    public CcmsService getCcmsService() {
        return ccmsService;
    }

    public void setCcmsService(CcmsService ccmsService) {
        this.ccmsService = ccmsService;
    }

    public FipCutpaydetl[] getSelectedNeedQryRecords() {
        return selectedNeedQryRecords;
    }

    public void setSelectedNeedQryRecords(FipCutpaydetl[] selectedNeedQryRecords) {
        this.selectedNeedQryRecords = selectedNeedQryRecords;
    }

    public List<FipCutpaydetl> getFilteredNeedQueryDetlList() {
        return filteredNeedQueryDetlList;
    }

    public void setFilteredNeedQueryDetlList(List<FipCutpaydetl> filteredNeedQueryDetlList) {
        this.filteredNeedQueryDetlList = filteredNeedQueryDetlList;
    }
}

