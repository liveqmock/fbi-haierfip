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


    //回写全部成功记录 做存档处理
    public synchronized String onWriteBackAllSuccessCutpayRecords() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("记录集为空。");
            return null;
        } else {
            int count = 0;
            try {
                count = hccbService.writebackCutPayRecord2Hccb(this.successDetlList, true);
                MessageUtil.addWarn("回写成功记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        initDataList();
        return null;
    }

    public synchronized String onWriteBackAllFailCutpayRecords() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("记录集为空。");
            return null;
        } else {
            int count = 0;
            try {
                count = hccbService.writebackCutPayRecord2Hccb(this.failureDetlList, true);
                MessageUtil.addWarn("回写记录条数：" + count);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
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

