package fip.view.fangdai;

import fip.gateway.newcms.controllers.T100101CTL;
import fip.gateway.newcms.domain.T100101.T100101ResponseRecord;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.primefaces.event.SelectEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-10-21
 * Time: 11:28:16
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "T100101")
@ViewScoped
//@RequestScoped
public class T100101Bean implements Serializable {
//    private DataModel model;
//    private LazyDataModel<T100101ResponseRecord> lazyModel;
    private T100101ResponseRecord selectedRecord;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private T100101ResponseRecord responseRecord = new T100101ResponseRecord();
    List<T100101ResponseRecord> responseFDList;
    List<T100101ResponseRecord> responseXFList;

    T100101CTL t100101ctl = new T100101CTL();

    private int totalcount;
    private BigDecimal totalamt;
    private BigDecimal totalPrincipalAmt;   //本金
    private BigDecimal totalInterestAmt;    //利息
    private BigDecimal totalFxjeAmt;    //罚息

    private String[] regionCodes = {"0532", "0531", "023", "0351"};
    private String[] regionNames = {"青岛", "济南", "重庆", "太原"};
    private SelectItem[] regionOptions;

    private String field;

    public T100101Bean() {
        FacesContext context = FacesContext.getCurrentInstance();
        field = context.getExternalContext().getRequestParameterMap().get("field");
        initFilters();
    }

    private void initList() {
        FacesContext context = FacesContext.getCurrentInstance();
        List<T100101ResponseRecord> fdList = null;
        try {
            fdList = t100101ctl.getAllFDRecords();
            if (fdList.size()==0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "记录为空...", ""));
            }
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "与新信贷的接口出现错误。", "请咨询系统管理人员。"));
            throw new RuntimeException("与新信贷的接口出现错误.");
        }
        totalcount = 0;
        initAmt();
        responseFDList = new ArrayList<T100101ResponseRecord>();

        boolean result = true;
        for (T100101ResponseRecord record : fdList) {
            String tmpStr = record.getStddqh();
            String regioncdTmp, bankcdTmp, nameTmp;
            if (tmpStr == null || tmpStr.equals("null")) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "地区及代扣银行未设置！", "姓名：" + record.getStdkhmc() + " 合同编号：" + record.getStdhth()));
                result = false;
                continue;
            } else {
                String[] code = tmpStr.split("-");
                if (code.length != 2) {
                    FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "地区或代扣银行设置错误！", "姓名：" + record.getStdkhmc() + " 合同编号：" + record.getStdhth() + " 备注：" + tmpStr));

                    result = false;
                    continue;
                } else {
                    record.setStddqh(code[0].trim());
                    record.setStdyhh(code[1].trim());
                }
            }
            responseFDList.add(record);
            totalcount++;
            countAmt(record);
        }
        if (!result) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "地区代号转换有误", ""));
            throw new RuntimeException("地区代号转换有误");
        }
    }

    private void initFilters() {
        regionOptions = createRegionOptions(regionNames, regionCodes);
    }

    private SelectItem[] createRegionOptions(String[] names, String[] codes) {
        SelectItem[] options = new SelectItem[codes.length + 1];

        options[0] = new SelectItem("", "全部地区");
        for (int i = 0; i < codes.length; i++) {
//            options[i + 1] = new SelectItem(codes[i],codes[i]);
            options[i + 1] = new SelectItem(codes[i], names[i]);
        }

        return options;
    }

    public String query() {
        try {
            initList();

            String hth, dqh, khmc;
            dqh = responseRecord.getStddqh();
            hth = responseRecord.getStdhth();
            khmc = responseRecord.getStdkhmc();
            totalcount = 0;
            initAmt();
            List<T100101ResponseRecord> newResponseFDList = new ArrayList<T100101ResponseRecord>();
            for (T100101ResponseRecord record : responseFDList) {
                if (StringUtils.isNotEmpty(hth)) {
                    if (!record.getStdhth().equals(hth)) {
                        continue;
                    }
                }
                if (StringUtils.isNotEmpty(khmc)) {
                    if (!record.getStdkhmc().equals(khmc)) {
                        continue;
                    }
                }
                if (StringUtils.isNotEmpty(dqh)) {
                    if (!record.getStddqh().equals(dqh)) {
                        continue;
                    }
                }
                newResponseFDList.add(record);
                totalcount++;
                countAmt(record);
            }
            responseFDList = newResponseFDList;
        } catch (Exception e) {
            logger.error("查询", e);
            this.responseFDList = new ArrayList();
            initAmt();
            //FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "查询错误！", ""));
        }
        return null;
    }

    private void initAmt() {
        totalamt = new BigDecimal(0);
        totalPrincipalAmt = new BigDecimal(0);
        totalInterestAmt = new BigDecimal(0);
        totalFxjeAmt = new BigDecimal(0);
    }

    private void countAmt(T100101ResponseRecord record) {
        totalamt = totalamt.add(new BigDecimal(record.getStdhkje()));
        if (StringUtils.isNotEmpty(record.getStdhkbj())) {
            totalPrincipalAmt = totalPrincipalAmt.add(new BigDecimal(record.getStdhkbj()));
        }
        if (StringUtils.isNotEmpty(record.getStdhklx())) {
            totalInterestAmt = totalInterestAmt.add(new BigDecimal(record.getStdhklx()));
        }
        if (StringUtils.isNotEmpty(record.getStdfxje())) {
            totalFxjeAmt = totalFxjeAmt.add(new BigDecimal(record.getStdfxje()));
        }
    }

    public String onRowSelectNavigate(SelectEvent event) {
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("selectedRecord", event.getObject());

        return "FDCutpayDetail?faces-redirect=true";
    }


    public void postProcessXLS(Object document) {
/*
        FacesContext context = FacesContext.getCurrentInstance();

        if (this.totalcount == 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "无查询结果数据。", "请先执行查询功能。"));
            return;
        }
*/
        HSSFWorkbook wb = (HSSFWorkbook) document;
        HSSFSheet sheet = wb.getSheetAt(0);
        HSSFRow header = sheet.getRow(0);

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        for (int i = 0; i < header.getPhysicalNumberOfCells(); i++) {
            HSSFCell cell = header.getCell(i);

            cell.setCellStyle(cellStyle);
        }
    }

    //=========================================





    //==============================================================

    public T100101ResponseRecord getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(T100101ResponseRecord selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public List<T100101ResponseRecord> getResponseFDList() {
        return responseFDList;
    }

    public void setResponseFDList(List<T100101ResponseRecord> responseFDList) {
        this.responseFDList = responseFDList;
    }

    public List<T100101ResponseRecord> getResponseXFList() {
        return responseXFList;
    }

    public void setResponseXFList(List<T100101ResponseRecord> responseXFList) {
        this.responseXFList = responseXFList;
    }

    public T100101ResponseRecord getResponseRecord() {
        return responseRecord;
    }

    public void setResponseRecord(T100101ResponseRecord responseRecord) {
        this.responseRecord = responseRecord;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public BigDecimal getTotalamt() {
        return totalamt;
    }

    public SelectItem[] getRegionOptions() {
        return regionOptions;
    }

    public BigDecimal getTotalPrincipalAmt() {
        return totalPrincipalAmt;
    }

    public BigDecimal getTotalInterestAmt() {
        return totalInterestAmt;
    }

    public String[] getRegionCodes() {
        return regionCodes;
    }

    public void setRegionCodes(String[] regionCodes) {
        this.regionCodes = regionCodes;
    }

    public String[] getRegionNames() {
        return regionNames;
    }

    public void setRegionNames(String[] regionNames) {
        this.regionNames = regionNames;
    }

    public BigDecimal getTotalFxjeAmt() {
        return totalFxjeAmt;
    }

    public void setTotalFxjeAmt(BigDecimal totalFxjeAmt) {
        this.totalFxjeAmt = totalFxjeAmt;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
