package fip.view;

import fip.common.constant.BillStatus;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipCutpaydetlExample;
import fip.service.fip.ZmdService;
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
 * ר�������
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 */
@ManagedBean
@ViewScoped
public class UnipayBatchDepZMDAction extends UnionpayBatchAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnipayBatchDepZMDAction.class);

    @ManagedProperty(value = "#{zmdService}")
    private ZmdService zmdService;

    @PostConstruct
    public void init() {
        super.init();
    }

    protected void initDataList(){
        super.initBaseDataList();
    }

    public synchronized String onConfirmAccountAll() {
        if (successDetlList.isEmpty()) {
            MessageUtil.addWarn("��¼Ϊ�գ�");
            return null;
        }
        try {
            billManagerService.updateCutpaydetlBillStatus(this.successDetlList, BillStatus.ACCOUNT_PEND);
            initDataList();
            MessageUtil.addInfo("ȷ����ɣ�");
        } catch (Exception e) {
            MessageUtil.addError("����ȷ�ϳ��ִ���" + e.getMessage());
        }

        return null;
    }

    public synchronized String onConfirmAccountMulti() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("��ѡ���¼.");
            return null;
        }
        try {
            billManagerService.updateCutpaydetlBillStatus(Arrays.asList(this.selectedConfirmAccountRecords), BillStatus.ACCOUNT_PEND);
            initDataList();
            MessageUtil.addInfo("ȷ����ɣ�");
        } catch (Exception e) {
            MessageUtil.addError("����ȷ�ϳ��ִ���" + e.getMessage());
        }
        return null;
    }

    protected void processOneQueryRecord(FipCutpaybat record) {
        super.processOneQueryRecord(record);

        //��д����ʧ�ܼ�¼(ȫ��δ�鵵��)�����鵵����
        zmdService.writebackCutPayRecord2Zmd(zmdService.selectDetlsByBatRecord(record, BillStatus.CUTPAY_FAILED), true);
        //��дSBS���˳ɹ���¼ ���� �鵵����
        zmdService.writebackCutPayRecord2Zmd(zmdService.selectDetlsByBatRecord(record, BillStatus.CUTPAY_SUCCESS), false);
    }


    public ZmdService getZmdService() {
        return zmdService;
    }

    public void setZmdService(ZmdService zmdService) {
        this.zmdService = zmdService;
    }
}

