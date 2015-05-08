package fip.view.hccb;

import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.HccbService;
import fip.service.fip.SbsTxnHelper;
import fip.service.fip.ZmdService;
import ibp.repository.model.IbpIfUnionpayTxn;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 小贷系统HCCB SBS 入账
 * User: zhanrui
 * Date: 2015-04-15
 */
@ManagedBean
@ViewScoped
public class HccbSbsAccountAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(HccbSbsAccountAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> successDetlList;
    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl[] selectedRecords;
    private FipCutpaydetl selectedRecord;

    private int totalcount;
    private int totalSuccessCount;
    private String totalamt;
    private String totalSuccessAmt;
    private String startDate;
    private String endDate;
    private String sbsOutActno;
    private String sbsInActno;

    private Map<String, String> statusMap = new HashMap<String, String>();

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{hccbService}")
    private HccbService hccbService;
    @ManagedProperty(value = "#{sbsTxnHelper}")
    private SbsTxnHelper sbsTxnHelper;

    private String bizid;
    private BizType bizType;


    @PostConstruct
    public void init() {
        this.startDate = new DateTime().minusDays(1).toString("yyyy-MM-dd");
        this.endDate = new DateTime().minusDays(1).toString("yyyy-MM-dd");

        FacesContext context = FacesContext.getCurrentInstance();
        this.bizid = context.getExternalContext().getRequestParameterMap().get("bizid");
        if (!StringUtils.isEmpty(this.bizid)) {
            if ("HCCB".equals(this.bizid)) {
                this.bizType = BizType.valueOf(bizid);
                initList();
            } else {
                throw new RuntimeException("业务号错误");
            }
        }
        this.sbsOutActno = sbsTxnHelper.selectSbsActnoFromPtEnuDetail("HCCB_FROM_ACTNO"); //对应建行37101985510051003497
        this.sbsInActno = sbsTxnHelper.selectSbsActnoFromPtEnuDetail("HCCB_TO_ACTNO");
    }

    private synchronized void initList() {
        detlList = billManagerService.selectBillListByObtainDate(this.bizType, BillStatus.CUTPAY_SUCCESS, startDate, endDate);
        detlList.addAll(billManagerService.selectBillListByObtainDate(this.bizType, BillStatus.ACCOUNT_FAILED, startDate, endDate));
        successDetlList = billManagerService.selectBillListByObtainDate(this.bizType, BillStatus.ACCOUNT_SUCCESS, startDate, endDate);
        this.totalamt = sumTotalAmt(detlList);
        this.totalSuccessAmt = sumTotalAmt(successDetlList);
        this.totalcount = detlList.size();
        this.totalSuccessCount = successDetlList.size();
    }

    public  void onQuery(){
        initList();
    }

    public synchronized void onAccountAll() {
        if (this.detlList.isEmpty()) {
            MessageUtil.addWarn("没有需要处理的记录...");
            return;
        }
        try {
            int succ = hccbService.accountCutPayRecord2SBS(this.detlList, this.totalamt, sbsOutActno, sbsInActno);
            MessageUtil.addWarn("入帐结束." );
        } catch (Exception e) {
            logger.error("SBS入帐时出现错误，请查询。", e);
            MessageUtil.addError("SBS入帐时出现错误，请查询。" + e.getMessage());
        }
        initList();
    }

    public synchronized void onAccountMulti() {
        if (this.detlList.isEmpty()) {
            MessageUtil.addWarn("没有需要处理的记录...");
            return;
        }

        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addInfo("请选择至少一项纪录！");
            return;
        }

        try {
            List<FipCutpaydetl> cutpaydetlList = Arrays.asList(selectedRecords);
            int succ = hccbService.accountCutPayRecord2SBS(cutpaydetlList, sumTotalAmt(cutpaydetlList), sbsOutActno, sbsInActno);
            MessageUtil.addWarn("入帐结束." );
        } catch (Exception e) {
            logger.error("SBS入帐时出现错误，请查询。", e);
            MessageUtil.addError("SBS入帐时出现错误，请查询。" + e.getMessage());
        }
        initList();
    }


    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList) {
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }


    //====================================================================================
    public int getTotalcount() {
        return totalcount;
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


    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<FipCutpaydetl> getSuccessDetlList() {
        return successDetlList;
    }

    public void setSuccessDetlList(List<FipCutpaydetl> successDetlList) {
        this.successDetlList = successDetlList;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public Map<String, String> getStatusMap() {
        return statusMap;
    }

    public void setStatusMap(Map<String, String> statusMap) {
        this.statusMap = statusMap;
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

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public HccbService getHccbService() {
        return hccbService;
    }

    public void setHccbService(HccbService hccbService) {
        this.hccbService = hccbService;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getSbsOutActno() {
        return sbsOutActno;
    }

    public void setSbsOutActno(String sbsOutActno) {
        this.sbsOutActno = sbsOutActno;
    }

    public String getSbsInActno() {
        return sbsInActno;
    }

    public void setSbsInActno(String sbsInActno) {
        this.sbsInActno = sbsInActno;
    }

    public SbsTxnHelper getSbsTxnHelper() {
        return sbsTxnHelper;
    }

    public void setSbsTxnHelper(SbsTxnHelper sbsTxnHelper) {
        this.sbsTxnHelper = sbsTxnHelper;
    }
}
