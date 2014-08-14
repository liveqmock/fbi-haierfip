package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.service.fip.CmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UnionpayBatchDepCMSAction extends UnionpayBatchAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayBatchDepCMSAction.class);

    @ManagedProperty(value = "#{cmsService}")
    private CmsService cmsService;

    @PostConstruct
    public void init() {
        super.init();
    }

    protected void initDataList(){
        super.initBaseDataList();
    }

    public String onConfirmAccountAll() {
        if (successDetlList.isEmpty()) {
            MessageUtil.addWarn("记录为空！");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                cmsService.obtainMerchantActnoFromCms(this.successDetlList);
            }
            billManagerService.updateCutpaydetlBillStatus(this.successDetlList, BillStatus.ACCOUNT_PEND);
            initDataList();
            MessageUtil.addInfo("确认完成！");
        } catch (Exception e) {
            MessageUtil.addError("数据确认出现错误！" + e.getMessage());
        }

        return null;
    }

    public String onConfirmAccountMulti() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("请选择记录.");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                cmsService.obtainMerchantActnoFromCms(Arrays.asList(this.selectedConfirmAccountRecords));
            }
            billManagerService.updateCutpaydetlBillStatus(Arrays.asList(this.selectedConfirmAccountRecords), BillStatus.ACCOUNT_PEND);
            initDataList();
            MessageUtil.addInfo("确认完成！");
        } catch (Exception e) {
            MessageUtil.addError("数据确认出现错误！" + e.getMessage());
        }
        return null;
    }


    public CmsService getCmsService() {
        return cmsService;
    }

    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }
}

