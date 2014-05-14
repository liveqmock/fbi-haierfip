package fip.view;

import fip.common.constant.BizType;
import fip.common.utils.MessageUtil;
import fip.repository.model.fip.UnipayQryParam;
import fip.repository.model.fip.UnipayQryResult;
import fip.service.fip.UnipayHistoryQryService;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 银联历史交易查询.
 * User: zhanrui
 * Date: 11-11-01
 * Time: 下午2:07
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UnipayTxnHisQryAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnipayTxnHisQryAction.class);

    private UnipayQryParam qryParam = new UnipayQryParam();

    private List<UnipayQryResult> detlList;
    private List<UnipayQryResult> filteredDetlList;
    private UnipayQryResult detlRecord = new UnipayQryResult();
    private UnipayQryResult[] selectedRecords;
    private UnipayQryResult[] selectedQryRecords;
    private UnipayQryResult selectedRecord;

    private int totalcount;
    private String totalamt;
    private BigDecimal totalPrincipalAmt;   //本金
    private BigDecimal totalInterestAmt;    //利息
    private BigDecimal totalFxjeAmt;    //罚息

    private Map<String, String> statusMap = new HashMap<String, String>();
    private static SelectItem[] qryTypeList = new SelectItem[]{
            new SelectItem("1", "全部记录"),
            new SelectItem("2", "成功记录"),
            new SelectItem("3", "失败记录")
    };

    @ManagedProperty(value = "#{unipayHistoryQryService}")
    private UnipayHistoryQryService unipayQryService;

    private BizType bizType;

    @PostConstruct
    public void init() {
        try {
            String bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(bizid)) {
                this.bizType = BizType.valueOf(bizid);
                qryParam.setBIZ_ID(bizid);
            }
            qryParam.setBEGIN_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            qryParam.setEND_DATE(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }
    }


    public String onQuery() {
        try {
            detlList = new ArrayList<UnipayQryResult>();
            Map<String, String> rtnMainMsgMap = unipayQryService.queryCurrentData(qryParam, detlList);
            String rtn_code = rtnMainMsgMap.get("RTN_CODE");
            if (!rtn_code.equals("0000")) {
                MessageUtil.addWarn("银联返回信息：[" + rtn_code + "] " + rtnMainMsgMap.get("ERR_MSG"));
                return null;
            }
            totalcount = detlList.size();
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
            MessageUtil.addError("获取记录时出错。" + e.getMessage() + e.getCause());
        }
        return null;
    }

    //============================================================================================

    public UnipayQryParam getQryParam() {
        return qryParam;
    }

    public void setQryParam(UnipayQryParam qryParam) {
        this.qryParam = qryParam;
    }

    public List<UnipayQryResult> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<UnipayQryResult> detlList) {
        this.detlList = detlList;
    }

    public UnipayQryResult getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(UnipayQryResult detlRecord) {
        this.detlRecord = detlRecord;
    }

    public UnipayQryResult[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(UnipayQryResult[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public UnipayQryResult[] getSelectedQryRecords() {
        return selectedQryRecords;
    }

    public void setSelectedQryRecords(UnipayQryResult[] selectedQryRecords) {
        this.selectedQryRecords = selectedQryRecords;
    }

    public UnipayQryResult getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(UnipayQryResult selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public int getTotalcount() {
        return totalcount;
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

    public UnipayHistoryQryService getUnipayQryService() {
        return unipayQryService;
    }

    public void setUnipayQryService(UnipayHistoryQryService unipayQryService) {
        this.unipayQryService = unipayQryService;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public SelectItem[] getQryTypeList() {
        return qryTypeList;
    }

    public List<UnipayQryResult> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<UnipayQryResult> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }
}
