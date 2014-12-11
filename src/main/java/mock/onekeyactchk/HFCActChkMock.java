package mock.onekeyactchk;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import mock.onekeyactchk.domain.T1001Request;
import mock.onekeyactchk.domain.T1001Response;
import mock.onekeyactchk.domain.T1002Request;
import mock.onekeyactchk.domain.T1002Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

/**
 * Created by zhanrui on 2014/11/13.
 * 房地产资金管理系统 对账 mock
 */
@WebServlet(name = "hfcactchk", urlPatterns = "/hfcactchk")
public class HFCActChkMock extends HaierCardActChkMock {
    private static final Logger logger = LoggerFactory.getLogger(HFCActChkMock.class);

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("GBK");
        response.setContentType("text/html;charset=GBK");
        PrintWriter out = response.getWriter();

        String reqXml = null;
        try {
            reqXml = getRequestXmlMsg(request);
            logger.info(">>>>XML:" + reqXml);

            int startIndex = reqXml.indexOf("<TXNCODE>")+"<TXNCODE>".length();
            int endIndex = reqXml.indexOf("</TXNCODE>");
            String txnCode = reqXml.substring(startIndex,endIndex);

            String respXml = "";
            if ("1001".equals(txnCode)) {
                respXml = process1001(reqXml);
            } else if ("1002".equals(txnCode)) {
                Thread.sleep(25*1000);
                respXml = process1002(reqXml);
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

    private  String process1001(String reqXml){
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(T1001Request.class);
        T1001Request tia = (T1001Request) xs.fromXML(reqXml);
        logger.info(">>>>TIA:" + tia);

        T1001Response response = new T1001Response();
        response.getINFO().setTXNCODE(tia.getINFO().getTXNCODE());
        response.getINFO().setREQSN(tia.getINFO().getREQSN());
        response.getINFO().setVERSION(tia.getINFO().getVERSION());
        response.getINFO().setRTNCODE("0000");
        response.getINFO().setRTNMSG("开始对账");

        return  response.toXml(response);
    }
    private  String process1002(String reqXml){
        XStream xs = new XStream(new DomDriver());
        xs.processAnnotations(T1002Request.class);
        T1002Request tia = (T1002Request) xs.fromXML(reqXml);
        logger.info(">>>>TIA:" + tia);

        T1002Response response = new T1002Response();
        response.getINFO().setTXNCODE(tia.getINFO().getTXNCODE());
        response.getINFO().setREQSN(tia.getINFO().getREQSN());
        response.getINFO().setVERSION(tia.getINFO().getVERSION());
        response.getINFO().setRTNCODE("0000");
        response.getINFO().setRTNMSG("平帐");

        return  response.toXml(response);
    }
}
