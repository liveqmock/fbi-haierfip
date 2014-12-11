package mock.hccbserver;

import fip.view.hccb.gateway.HccbBillVO;
import fip.view.hccb.gateway.HccbT1001Request;
import fip.view.hccb.gateway.HccbT1001Response;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhanrui on 2014/11/13.
 * 房地产资金管理系统 对账 mock
 */
@WebServlet(name = "hccbserver", urlPatterns = "/hccbserver")
public class HccbServerMock extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HccbServerMock.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("GBK");
        response.setContentType("text/html;charset=GBK");
        PrintWriter out = response.getWriter();

        String reqXml = null;
        try {
            reqXml = getRequestXmlMsg(request);
            logger.info(">>>>XML:" + reqXml);

            int startIndex = reqXml.indexOf("<txncode>") + "<txncode>".length();
            int endIndex = reqXml.indexOf("</txncode>");
            String txnCode = reqXml.substring(startIndex, endIndex);

            String respXml = "";
            if ("1001".equals(txnCode)) {
                respXml = process1001(reqXml);
            } else if ("1003".equals(txnCode)) {
                Thread.sleep(25 * 1000);
                respXml = process1003(reqXml);
            } else {
                logger.error("交易号错误");
                throw new RuntimeException("交易号错误");
            }
            logger.info("<<<<TOA:" + respXml);
            out.println(respXml);
        } catch (Exception e) {
            logger.error("txn error.", e);
            out.println("error" + e.getMessage());
        } finally {
            out.flush();
            out.close();
        }
    }

    private String getRequestXmlMsg(HttpServletRequest request) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "GBK"));
        StringBuilder sb = new StringBuilder();
        String tmp = "";
        while (true) {
            tmp = br.readLine();
            if (tmp == null)
                break;
            else
                sb.append(tmp);
        }
        return sb.toString();
    }

    private String process1001(String reqXml) {
        HccbT1001Request request = new HccbT1001Request();
        request = (HccbT1001Request) request.toBean(reqXml);
        logger.info(">>>>REQUEST:" + request);

        //init data
        int billnum = 2001;
        List<HccbBillVO> bills = new ArrayList<HccbBillVO>();
        for (int i = 0; i < billnum; i++) {
            HccbBillVO bill = new HccbBillVO();
            bill.setActno("actno-" + i);
            bills.add(bill);
        }

        //txn
        int pagesize = 0;
        if (StringUtils.isNotEmpty(request.body.pagesize)) {
            pagesize = Integer.parseInt(request.body.pagesize);
        }
        if (pagesize == 0) {
            pagesize = 1000;
        }
        int pagesum = billnum % pagesize == 0 ? billnum / pagesize : billnum / pagesize + 1;

        int pagenum = 1;
        if (StringUtils.isNotEmpty(request.body.pagenum)) {
            pagenum = Integer.parseInt(request.body.pagenum);
        }

        int step = 0;
        int framenum = pagenum * pagesize;

        List<HccbBillVO> billsForMsg = new ArrayList<HccbBillVO>();
        for (HccbBillVO bill : bills) {
            if (step >= framenum - pagesize && step < framenum) {
                billsForMsg.add(bill);
            }
            if (step == framenum) {
                break;
            }
            step++;
        }


        HccbT1001Response response = new HccbT1001Response();
        response.body.pagesum = "" + pagesum;
        response.body.records.bills = billsForMsg;

        return response.toXml(response);
    }

    private String process1003(String reqXml) {

        return "";
    }
}
