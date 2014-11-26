package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.repository.dao.fip.LazyDataCutpaydetlMapper;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.fip.LazyDataCutpaydetlParam;
import fip.service.fip.BillManagerService;
import fip.service.fip.JobLogService;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 根据是否归档查询记录
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
//@RequestScoped
public class CutpayArchiveQryAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(CutpayArchiveQryAction.class);

    private FipCutpaydetl selectedRecord;
    private FipCutpaydetl[] selectedRecords;
    private LazyDataModel<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> filteredDetlList;

    private CutpayChannel channelEnum = CutpayChannel.NONE;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    private String bizid;

    private Map<String, String> channelMap = new HashMap<String, String>();
    private BillStatus status = BillStatus.CUTPAY_FAILED;
    private BizType bizType;

    private int totalcount;
    private String totalamt;
    private LazyDataCutpaydetlParam paramBean;

    @PostConstruct
    public void init() {
        try {
            for (CutpayChannel cutpayChannelEnum : CutpayChannel.values()) {
                channelMap.put(cutpayChannelEnum.getCode(), cutpayChannelEnum.getTitle());
            }

            this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");

            if (!StringUtils.isEmpty(this.bizid)) {
                this.bizType = BizType.valueOf(bizid);
            }
            this.paramBean = new LazyDataCutpaydetlParam();
            this.paramBean.setStartDate(new DateTime().dayOfMonth().withMinimumValue().toString("yyyyMMdd"));
            this.paramBean.setEndDate(new DateTime().toString("yyyyMMdd"));
        } catch (Exception e) {
            logger.error("初始化时出现错误。", e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public void onQuery() {
        paramBean.setBizId(this.bizid);
        paramBean.setArchiveflag("1"); //已存档
        paramBean.setDeletedflag("0"); //未删除

        String endDate = new DateTime(this.paramBean.getEndDate()).plusDays(1).toString("yyyyMMdd");
        paramBean.setEndDate(endDate); //截止日期加一 便于查询
        detlList = new LazyDataCutpaydetlModel(billManagerService.getLazyDataCutpaydetlMapper(), paramBean);
        if (detlList.getWrappedData() == null) {
            this.totalcount = 0;
        } else
            this.totalcount = ((List) detlList.getWrappedData()).size();
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
    class LazyDataCutpaydetlModel extends LazyDataModel<FipCutpaydetl> {
        private LazyDataCutpaydetlParam paramBean;
        private LazyDataCutpaydetlMapper lazyDataMapper;

        public LazyDataCutpaydetlModel(LazyDataCutpaydetlMapper lazyDataMapper, LazyDataCutpaydetlParam paramBean) {
            this.lazyDataMapper = lazyDataMapper;
            this.paramBean = paramBean;
        }

        @Override
        public List<FipCutpaydetl> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, String> filters) {
            List<FipCutpaydetl> dataList;
            try {
                LazyDataCutpaydetlParam vo = new LazyDataCutpaydetlParam();
                PropertyUtils.copyProperties(vo, paramBean);
                vo.setOffset(first);
                vo.setPagesize(first + pageSize);
                if (sortField != null) {
                    vo.setSortField(changeBeanPropertyName2DBTableFieldName(sortField));
                    if (sortOrder != null) {
                        if (sortOrder.compareTo(SortOrder.DESCENDING) == 0) {
                            vo.setSortOrder(" DESC ");
                        }
                    }
                }else{ //默认排序字段
                    vo.setSortField("batch_sn, batch_detl_sn");
                    //vo.setSortField("1");
                }
                dataList = this.lazyDataMapper.selectPagedRecords(vo);
            } catch (Exception e) {
                logger.error("查询数据出现错误.", e);
                throw new RuntimeException(e);
            }

            if (super.getRowCount() <= 0) {
                int total = lazyDataMapper.countRecords(paramBean);
                this.setRowCount(total);
            }
            this.setPageSize(pageSize);
            return dataList;
        }

        private String changeBeanPropertyName2DBTableFieldName(String propertyName) {
            char[] ch = propertyName.toCharArray();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < propertyName.length(); i++) {
                if ('A' <= ch[i] && ch[i] <= 'Z') {
                    sb.append("_");
                    sb.append(String.valueOf(ch[i]).toLowerCase());
                }else{
                    sb.append(String.valueOf(ch[i]).toLowerCase());
                }
            }
            return sb.toString();
        }
    }




    //====================================================================================

    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public FipCutpaydetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaydetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public CutpayChannel getChannelEnum() {
        return channelEnum;
    }

    public void setChannelEnum(CutpayChannel channelEnum) {
        this.channelEnum = channelEnum;
    }

    public Map<String, String> getChannelMap() {
        return channelMap;
    }

    public void setChannelMap(Map<String, String> channelMap) {
        this.channelMap = channelMap;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public String getBizid() {
        return bizid;
    }

    public void setBizid(String bizid) {
        this.bizid = bizid;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public LazyDataModel<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(LazyDataModel<FipCutpaydetl> detlList) {
        this.detlList = detlList;
    }

    public LazyDataCutpaydetlParam getParamBean() {
        return paramBean;
    }

    public void setParamBean(LazyDataCutpaydetlParam paramBean) {
        this.paramBean = paramBean;
    }
}
