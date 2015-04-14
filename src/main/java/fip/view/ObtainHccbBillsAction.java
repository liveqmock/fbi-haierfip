package fip.view;

import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.EnumApp;
import fip.common.utils.MessageUtil;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.BillManagerService;
import fip.service.fip.HccbService;
import fip.service.fip.JobLogService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * HCCB小额贷款系统 数据导入.
 * User: zhanrui
 * Date: 8-8-2014
 */
@ManagedBean
@ViewScoped
public class ObtainHccbBillsAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(ObtainHccbBillsAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> filteredDetlList;
    private List<FipCutpaydetl> qrydetlList;
    private List<FipCutpaydetl> filteredQrydetlList;
    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl[] selectedRecords;
    private FipCutpaydetl[] selectedQryRecords;
    private FipCutpaydetl selectedRecord;

    private int totalcount;
    private int totalqrycount;
    private String totalamt;
    private String totalqryamt;
    private BigDecimal totalPrincipalAmt;   //本金
    private BigDecimal totalInterestAmt;    //利息
    private BigDecimal totalFxjeAmt;    //罚息

    private Map<String, String> statusMap = new HashMap<String, String>();

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{hccbService}")
    private HccbService hccbService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    private BizType bizType;

    private List<SelectItem> billStatusOptions;

    //===
    private UploadedFile file;

    @PostConstruct
    public void init() {
        try {
            //String bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            String bizid = "HCCB";

            if (!StringUtils.isEmpty(bizid)) {
                this.bizType = BizType.valueOf(bizid);
                if (!"HCCB".equals(bizid)) {
                    throw new RuntimeException("只支持BIZID=HCCB");
                }
                qrydetlList = new ArrayList<FipCutpaydetl>();
                initDetlList();
            }
        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }
    }

    private synchronized void initDetlList() {
        detlList = billManagerService.selectBillList(this.bizType, BillType.NORMAL, BillStatus.INIT);
        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
    }

    private void initSelectItem(EnumApp e){
        List<SelectItem> items = new ArrayList<SelectItem>();
        SelectItem item;


    }

    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList){
        BigDecimal amt = new BigDecimal(0);
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(amt);
    }

    public synchronized String onQryHccb() {
        try {
            qrydetlList = hccbService.doQueryHccbBills(this.bizType);
            if (qrydetlList.size() == 0) {
                MessageUtil.addWarn("未获取到信贷系统的代扣记录。");
            }
            this.totalqryamt = sumTotalAmt(qrydetlList);
            this.totalqrycount = qrydetlList.size();
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
            MessageUtil.addError("获取记录时出错。" + e.getMessage());
        }
        return null;
    }


    public String onObtain() {
        //TODO 检查本地记录状态
        try {
            List<String> returnMsgs = new ArrayList<String>();
            int count = hccbService.doObtainHccbBills(this.bizType, returnMsgs);
            MessageUtil.addWarn("本次获取记录数：" + count + " 条.");
            for (String returnMsg : returnMsgs) {
                MessageUtil.addWarn(returnMsg);
            }
            initDetlList();
        } catch (Exception e) {
            logger.error("获取记录时出错", e);
            MessageUtil.addError("获取记录时出错。" + e.getMessage());
        }
        return null;
    }

    public synchronized String onDeleteAll() {
        if (detlList.size() == 0) {
            MessageUtil.addWarn("记录为空。");
            return null;
        }
        billManagerService.deleteBillsByKey(detlList);
        initDetlList();
        return null;
    }

    public synchronized String onDeleteMulti() {
        if (selectedRecords.length == 0) {
            MessageUtil.addWarn("请先选择记录。");
            return null;
        }

        billManagerService.deleteBillsByKey(Arrays.asList(selectedRecords));
        initDetlList();
        MessageUtil.addInfo("删除记录成功。");
        return null;
    }


    //=============
    public synchronized void onUpload() {
        long start = System.currentTimeMillis();
        int xlsRowImpCount = 0;
        int rowcount = 0;
        int cellcount = 0;
        InputStream is = null;
        try {
            is = file.getInputstream();

            XSSFWorkbook wb = new XSSFWorkbook(is);
            XSSFSheet sheet = wb.getSheetAt(0);

            rowcount = sheet.getLastRowNum();

            //根据第一列的字段数 设置列数
            cellcount = sheet.getRow(0).getLastCellNum();

            String[] fields = new String[cellcount];

/*
            //设置数据库表字段信息
            getOneRow(sheet, 0, cellcount, fields);
            userDefRptService.insertColumnDefInfo(rptno, fields);
            rowcount--;
*/

            List<FipCutpaydetl> cutpaydetls = new ArrayList<FipCutpaydetl>();
            List<String> returnMsgs = new ArrayList<String>();
            //数据导入
            for (int i = 1; i <= rowcount; i++) {
                getOneRow(sheet, i, cellcount, fields);
                if (StringUtils.isEmpty(fields[0])) { //第一列为空
                    break;
                } else {
                    FipCutpaydetl cutpaydetl = new FipCutpaydetl();
                    cutpaydetl.setIouno(fields[0]);
                    cutpaydetl.setPoano(fields[1]);
                    cutpaydetl.setClientno(fields[2]);

                    cutpaydetl.setClientname(fields[3]);
                    cutpaydetl.setBiBankactname(fields[3]);

                    cutpaydetl.setClientid(fields[4]);
                    cutpaydetl.setPaybackamt(new BigDecimal(fields[5]));
                    cutpaydetl.setBiBankactno(fields[6]);
                    cutpaydetl.setBiActopeningbank(fields[7]);
                    cutpaydetl.setBiProvince(fields[8]);
                    cutpaydetl.setBiCity(fields[9]);

                    cutpaydetls.add(cutpaydetl);
                    xlsRowImpCount++;
                }
            }
            int dbImpCount = hccbService.importDataFromXls(this.bizType, cutpaydetls, returnMsgs);

            for (String returnMsg : returnMsgs) {
                MessageUtil.addWarn(returnMsg);
            }

            MessageUtil.addInfo("XLS中记录总行数:[" + xlsRowImpCount + "]  成功导入:[" + dbImpCount + "]");

        } catch (Exception ex) {
            logger.error(" 导入失败。", ex);
            MessageUtil.addError("导入失败." + ex.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            long end = System.currentTimeMillis();
            MessageUtil.addInfo("共耗时:" + (end - start) / 1000 + "秒...");
            logger.info("导入记录数:" + xlsRowImpCount);
        }
    }

    private void getOneRow(XSSFSheet sheet, int row, int cellCnt, String[] fields) {
        for (int j = 0; j < cellCnt; j++) {
            XSSFCell cell = sheet.getRow(row).getCell(j);
            String cellValue = "";
            if (cell != null) {
                if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                    cellValue = cell.getStringCellValue();
                } else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                    if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                        cellValue = new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
                    } else {
                        cellValue = NumberFormat.getNumberInstance().format(cell.getNumericCellValue()).replaceAll(",", "");
                    }
                } else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
                    cellValue = "";
                } else {
                    cellValue = "格式有误";
                }
                fields[j] = cellValue;
            } else {
                fields[j] = "";
            }
        }
    }



    //============================================================================================

    public List<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipCutpaydetl> detlList) {
        this.detlList = detlList;
    }

    public FipCutpaydetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipCutpaydetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public FipCutpaydetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaydetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public int getTotalcount() {
        return this.totalcount;
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

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public HccbService getHccbService() {
        return hccbService;
    }

    public void setHccbService(HccbService hccbService) {
        this.hccbService = hccbService;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public List<FipCutpaydetl> getQrydetlList() {
        return qrydetlList;
    }

    public void setQrydetlList(List<FipCutpaydetl> qrydetlList) {
        this.qrydetlList = qrydetlList;
    }

    public int getTotalqrycount() {
        return totalqrycount;
    }

    public void setTotalqrycount(int totalqrycount) {
        this.totalqrycount = totalqrycount;
    }

    public String getTotalqryamt() {
        return totalqryamt;
    }

    public void setTotalqryamt(String totalqryamt) {
        this.totalqryamt = totalqryamt;
    }

    public FipCutpaydetl[] getSelectedQryRecords() {
        return selectedQryRecords;
    }

    public void setSelectedQryRecords(FipCutpaydetl[] selectedQryRecords) {
        this.selectedQryRecords = selectedQryRecords;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public List<SelectItem> getBillStatusOptions() {
        return billStatusOptions;
    }

    public void setBillStatusOptions(List<SelectItem> billStatusOptions) {
        this.billStatusOptions = billStatusOptions;
    }

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public List<FipCutpaydetl> getFilteredQrydetlList() {
        return filteredQrydetlList;
    }

    public void setFilteredQrydetlList(List<FipCutpaydetl> filteredQrydetlList) {
        this.filteredQrydetlList = filteredQrydetlList;
    }
}
