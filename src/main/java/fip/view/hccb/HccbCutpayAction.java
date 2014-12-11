package fip.view.hccb;

import fip.view.hccb.gateway.HccbBillVO;
import fip.view.hccb.gateway.HccbT1001Request;
import fip.view.hccb.gateway.HccbT1001Response;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanrui on 2014/12/11.
 */
public class HccbCutpayAction {
    private static final Logger logger = LoggerFactory.getLogger(HccbCutpayAction.class);

    public void doQueryBills() {
        List<HccbBillVO> bills = new ArrayList<HccbBillVO>();
        int txcount = 1;
        int pagesize = 10;
        try {
            HccbT1001Response response = sendAndRecv(pagesize, txcount);

            assembleBills(bills, response);

            int pagesum = Integer.parseInt(response.body.pagesum);
            while (txcount <= pagesum) {
                txcount++;
                response = sendAndRecv(pagesize, txcount);
                assembleBills(bills, response);
            }
            logger.info("" + bills.size());
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    private void assembleBills(List<HccbBillVO> bills, HccbT1001Response response) {
        for (HccbBillVO bill : response.body.records.bills) {
            bills.add(bill);
        }
    }

    private HccbT1001Response sendAndRecv(int pagesize, int pagenum) {
        HccbT1001Request req = new HccbT1001Request();
        req.head.setTxnsn(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        req.head.setTxndate(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        req.head.setTxntime(new SimpleDateFormat("HHmmss").format(new Date()));

        req.body.pagesize = "" + pagesize;
        req.body.pagenum = "" + pagenum;

        String reqXml = req.toXml(req);
        logger.info("HCCB request:" + reqXml);

        String respXml = doPost("http://localhost:8080/haierfip/hccbserver", reqXml, "GBK");
        logger.info("HCCB response:" + respXml);

        if (StringUtils.isEmpty(respXml)) {
            throw new RuntimeException("HCCB 响应报文为空");
        }
        HccbT1001Response response = null;
        response = new HccbT1001Response();
        response = (HccbT1001Response) response.toBean(respXml);
        return response;
    }


    private String doPost(String serverUrl, String datagram, String charsetName) {
        HttpClient httpclient = new DefaultHttpClient();
        try {
            //请求超时
            httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000 * 20);
            //读取超时
            httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000 * 30);

            HttpPost httppost = new HttpPost(serverUrl);
            httppost.getURI();
            StringEntity xmlSE = new StringEntity(datagram, charsetName);
            httppost.setEntity(xmlSE);

            HttpResponse httpResponse = httpclient.execute(httppost);

            //HttpStatus.SC_OK)表示连接成功
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(httpResponse.getEntity(), charsetName);
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("Http 通讯错误", e);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
    }

    public static void main(String argv[]) {
        HccbCutpayAction action = new HccbCutpayAction();
        action.doQueryBills();
    }
}
