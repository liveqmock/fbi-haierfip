package fip.view.common;

import fip.common.constant.BillStatus;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipRefunddetl;
import net.sf.jxls.transformer.XLSTransformer;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.advance.utils.PropertyManager;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EXCEL输出.
 * User: zhanrui
 * Date: 11-9-29
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class JxlsManager {
    private static final Logger logger = LoggerFactory.getLogger(JxlsManager.class);

    public String exportCutpayList(String filename, List<FipCutpaydetl> records) {
        try {
            List<FipCutpaydetl> rptRecords = new ArrayList<FipCutpaydetl>();
            for (FipCutpaydetl r : records) {
                r.setBillstatus(BillStatus.valueOfAlias(r.getBillstatus()).getTitle());
                rptRecords.add(r);
            }
            Map beansMap = new HashMap();
            beansMap.put("records", rptRecords);

            String reportPath = PropertyManager.getProperty("REPORT_ROOTPATH");
            String templateFileName = reportPath + "fdCutpayList.xls";

            outputExcel(beansMap, templateFileName, filename);
        } catch (Exception e) {
            logger.error("报表处理错误！", e);
            throw new RuntimeException("报表处理错误！", e);
        }
        return null;
    }

    //设定模板文件名
    public String exportCutpayList(String filename, String template, List<FipCutpaydetl> records) {
        try {
            List<FipCutpaydetl> rptRecords = new ArrayList<FipCutpaydetl>();
            for (FipCutpaydetl r : records) {
                r.setBillstatus(BillStatus.valueOfAlias(r.getBillstatus()).getTitle());
                rptRecords.add(r);
            }
            Map beansMap = new HashMap();
            beansMap.put("records", rptRecords);

            String reportPath = PropertyManager.getProperty("REPORT_ROOTPATH");
            //String templateFileName = reportPath + "fdCutpayList.xls";
            String templateFileName = reportPath + template;

            outputExcel(beansMap, templateFileName, filename);
        } catch (Exception e) {
            logger.error("报表处理错误！", e);
            throw new RuntimeException("报表处理错误！", e);
        }
        return null;
    }
    public String exportRefundList(String filename, List<FipRefunddetl> records) {
        try {
            List<FipRefunddetl> rptRecords = new ArrayList<FipRefunddetl>();
            for (FipRefunddetl r : records) {
                r.setBillstatus(BillStatus.valueOfAlias(r.getBillstatus()).getTitle());
                rptRecords.add(r);
            }
            Map beansMap = new HashMap();
            beansMap.put("records", rptRecords);

            String reportPath = PropertyManager.getProperty("REPORT_ROOTPATH");
            String templateFileName = reportPath + "fdRefundList.xls";

            outputExcel(beansMap, templateFileName, filename);
        } catch (Exception e) {
            logger.error("报表处理错误！", e);
            throw new RuntimeException("报表处理错误！", e);
        }
        return null;
    }

    private void outputExcel(Map beansMap, String templateFileName, String excelFilename) throws IOException, InvalidFormatException {
        ServletOutputStream os = null;
        InputStream is = null;
        try {
            XLSTransformer transformer = new XLSTransformer();
            is = new BufferedInputStream(new FileInputStream(templateFileName));
            Workbook wb = transformer.transformXLS(is, beansMap);
            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            os = response.getOutputStream();
            response.reset();
            response.setHeader("Content-disposition", "attachment; filename=" + java.net.URLEncoder.encode(excelFilename, "UTF-8"));
            response.setContentType("application/msexcel");
            wb.write(os);
        } finally {
            if (os != null) {
                os.flush();
                os.close();
            }
            if (is != null) {
                is.close();
            }
        }
    }

}
