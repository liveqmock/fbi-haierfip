<!--
/*********************************************************************
* 功能描述:
* 作 者:
* 开发日期:
* 修 改 人:
* 修改日期:
* 版 权:
***********************************************************************/
-->
<%@page contentType="text/html; charset=GBK" %>
<%@page import="net.sf.jxls.report.ReportManager" %>
<%@page import="net.sf.jxls.report.ReportManagerImpl" %>
<%@page import="net.sf.jxls.transformer.XLSTransformer" %>
<%@page import="org.apache.commons.logging.Log" %>
<%@page import="org.apache.commons.logging.LogFactory" %>
<%@page import="org.apache.poi.hssf.usermodel.HSSFWorkbook" %>
<%@page import="pub.platform.advance.utils.PropertyManager" %>
<%@page import="pub.platform.db.ConnectionManager" %>
<%@page import="pub.platform.db.DatabaseConnection" %>
<%@page import="pub.platform.form.config.SystemAttributeNames" %>
<%@page import="pub.platform.security.OperatorManager" %>
<%@page import="java.io.*" %>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="java.util.*" %>
<%
    Log logger = LogFactory.getLog("ExcelRptTemplate.jsp");

    OperatorManager omgr = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
    //采用do while结构便于在主流程中监测错误发生后退出主程序；
    try {
        do {
            //输出报表
            response.reset();
            response.setContentType("application/vnd.ms-excel");
            response.addHeader("Content-Disposition", "attachment; filename=" + CcbLoanConst.MISC_RPT_MODEL_04 + ".xls");

            //检查输入参数

            /*
              检查查询权限
              新旧帐号转换  核对机构号

             */

            //--根据模板创建输出流--
            //得到报表模板
            String rptModelPath = PropertyManager.getProperty("REPORT_ROOTPATH");
            String rptName = rptModelPath + CcbLoanConst.MISC_RPT_MODEL_04 + ".xls";
            File file = new File(rptName);
            // 判断模板是否存在,不存在则退出
            if (!file.exists()) {
                out.println(rptName + PropertyManager.getProperty("304"));
                break;
            }

            Map beans = new HashMap();

            String startdate = request.getParameter("MORTEXPIREDATE");
            String enddate = request.getParameter("MORTEXPIREDATE2");

            int startdateflag = 0;
            if (startdate != null && !startdate.equals("")) {
                beans.put("startdate", startdate);
            } else {
                beans.put("startdate", "1000-01-01");
                startdateflag = 1;  //未输入起始日期
            }
            int enddateflag = 0;
            if (enddate != null && !enddate.equals("")) {
                beans.put("enddate", enddate);
            } else {
                beans.put("enddate", "9000-01-01");
                enddateflag = 1;   //未输入截止日期
            }

            if (startdateflag == 1 && enddateflag == 0) {
                beans.put("condidate", " 抵押接收截至日期:" + enddate);
            } else if (startdateflag == 0 && enddateflag == 1) {
                beans.put("condidate", " 抵押接收起始日期:" + startdate);
            } else if (startdateflag == 0 && enddateflag == 0) {
                beans.put("condidate", " 抵押接收日期:" + startdate + "至" + enddate);
            } else {
                beans.put("condidate", " ");
            }

            Date date = new Date();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            String reportdate = df.format(date);
            //reportdate = reportdate.substring(0,4)+"年"+reportdate.substring(4,6)+"月"+reportdate.substring(6,8)+"日";
            beans.put("currdate", reportdate);
            beans.put("reportdate", "出表日期:" + reportdate); //报表TITLE使用

            //生成统计表数据

            DatabaseConnection conn = ConnectionManager.getInstance().get();

            ReportManager reportManager = new ReportManagerImpl(conn.getConnection(), beans);
            beans.put("rm", reportManager);
            beans.put("deptid", omgr.getOperator().getDeptid());


            XLSTransformer transformer = new XLSTransformer();
            InputStream is = new BufferedInputStream(new FileInputStream(rptName));
            HSSFWorkbook workbook = transformer.transformXLS(is, beans);
            OutputStream os = response.getOutputStream();
            workbook.write(os);
            is.close();
            out.flush();
            out.close();

            //TODO 工作日志处理
        } while (false);
    } catch (Exception e) {
        logger.error("生成签约放款未回证统计表四时出现错误。");
        e.printStackTrace();
    } finally {
        //todo:
        ConnectionManager.getInstance().release();
    }
%>
