package fip.view;

import fip.common.utils.MessageUtil;
import fip.service.fip.CcmsService;
import fip.service.fip.HccbService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2015-04-18
 * Time: 12:52:46
 */
@ManagedBean
@ViewScoped
public class UnionpayBatchDepHCCBAction extends UnionpayBatchAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayBatchDepHCCBAction.class);

    @ManagedProperty(value = "#{hccbService}")
    protected HccbService hccbService;

    @PostConstruct
    public void init() {
        super.init();
    }
    protected void initDataList(){
        super.initBaseDataList();
    }


    //��дȫ���ɹ���¼ ���浵����
    public synchronized String onWriteBackAllSuccessCutpayRecords() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = hccbService.writebackCutPayRecord2Hccb(this.successDetlList, true);
                MessageUtil.addWarn("��д�ɹ���¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initDataList();
        return null;
    }

    public synchronized String onWriteBackAllFailCutpayRecords() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("��¼��Ϊ�ա�");
            return null;
        } else {
            int count = 0;
            try {
                count = hccbService.writebackCutPayRecord2Hccb(this.failureDetlList, true);
                MessageUtil.addWarn("��д��¼������" + count);
            } catch (Exception e) {
                MessageUtil.addError("���ݴ������" + e.getMessage());
            }
        }
        initDataList();
        return null;
    }

    public HccbService getHccbService() {
        return hccbService;
    }

    public void setHccbService(HccbService hccbService) {
        this.hccbService = hccbService;
    }
}

