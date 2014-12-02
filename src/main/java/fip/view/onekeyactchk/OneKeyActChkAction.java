package fip.view.onekeyactchk;

import fip.common.utils.MessageUtil;
import fip.repository.model.Ptenudetail;
import fip.utils.SmsHelper;
import fip.view.onekeyactchk.wsclient.spc1.SBSSysServiceServiceLocator;
import fip.view.onekeyactchk.wsclient.spc1.SBSSysServiceSoapBindingStub;
import fip.view.onekeyactchk.wsclient.spc1.ScfDzInfoVO;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import skyline.service.common.ToolsService;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * SBS 一键对账处理
 * User: zhanrui
 * Date: 2014-11-27
 * Time: 下午3:40
 */
@ManagedBean
//@ViewScoped
@SessionScoped
public class OneKeyActChkAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(OneKeyActChkAction.class);
    private static final long serialVersionUID = 1366227629931959859L;

    private String txnDate;

    private List<Ptenudetail> ptenudetails;

    private List<PeripheralAppInfo> apps = new ArrayList<PeripheralAppInfo>();

    private PeripheralAppInfo[] selectedRecords;
    private TxnStatus txnStatus = TxnStatus.INIT;

    @ManagedProperty(value = "#{toolsService}")
    private ToolsService toolsService;
    private static final Executor executor = Executors.newCachedThreadPool();


    @PostConstruct
    public void postConstruct() {
        DateTime dt = new DateTime();
        //this.txnDate = dt.minusMonths(1).dayOfMonth().withMaximumValue().toString("yyyyMMdd");
        this.txnDate = dt.toString("yyyyMMdd");

        ptenudetails = toolsService.selectEnuDetail("ONEKEY_ACCT_CHK");
        for (Ptenudetail enu : ptenudetails) {
            PeripheralAppInfo appInfo = new PeripheralAppInfo();
            appInfo.setAppId(enu.getDispno().toString());
            appInfo.setAppName(enu.getEnuitemlabel());
            appInfo.setAppChnCode(enu.getEnuitemvalue());
            appInfo.setUrl(enu.getEnuitemexpand());
            appInfo.setStatus(TxnStatus.INIT.getCode());
            appInfo.setInformTime("");
            appInfo.setResultQryTime("");
            appInfo.setSmsDesc(enu.getEnuitemdesc());
            apps.add(appInfo);
        }
    }


    public String onStartAcctChk() {
        if (selectedRecords.length == 0) {
            MessageUtil.addError("请选择需对账的系统...");
            return null;
        }
        try {
            for (final PeripheralAppInfo app : selectedRecords) {
                if (StringUtils.isEmpty(app.getUrl())) {
                    app.setRtnMsg("此系统的对账服务未开启");
                    //短信通知
                    if (!StringUtils.isEmpty(app.getSmsDesc())) {
                        processSMS(app);
                    }
                    continue;
                }

                //检查是否正在对账中 或 已平帐
                TxnStatus status = TxnStatus.valueOfAlias(app.getStatus());
                switch (status) {
                    case ACCT_UNDERWAY:
                    case ACCT_SUCC_BANLANCE:
                        continue;
                }

                //初始化状态 准备发起对账处理任务
                app.setStatus(TxnStatus.INIT.getCode());
                app.setInformTime("");
                app.setResultQryTime("");

                //发起对账处理任务
                Runnable task = new Runnable() {
                    @Override
                    public void run() {
                        try {
                            app.setRtnCode("");
                            app.setRtnMsg("");
                            processInformTxn(app);
                            TxnStatus status = TxnStatus.valueOfAlias(app.getStatus());
                            switch (status) {
                                case INFORM_FAIL:
                                    return;
                            }

                            boolean loop;
                            do {
                                app.setRtnCode("");
                                app.setRtnMsg("");
                                processResultQryTxn(app);
                                status = TxnStatus.valueOfAlias(app.getStatus());
                                switch (status) {
                                    case ACCT_UNDERWAY:
                                        loop = true;
                                        Thread.sleep(5 * 1000);
                                        break;
                                    case ACCT_SUCC_BANLANCE:
                                    case ACCT_SUCC_NOTBANLANCE:
                                    case ACCT_FAIL_EXCEPTION:
                                    default:
                                        loop = false;
                                }
                            } while (loop);
                        } catch (Exception e) {
                            app.setRtnCode("ERR1");
                            app.setRtnMsg(e.getMessage());
                            logger.error("一键对账出错.", e);
                        }
                    }
                };
                executor.execute(task);
            }
        } catch (Exception ex) {
            logger.error("对账的发起交易处理错误。", ex);
            MessageUtil.addError("对账的发起交易处理错误。" + ex.getMessage());
        }
        return null;
    }


    public String onResetStatus() {
        if (selectedRecords.length == 0) {
            MessageUtil.addError("请选择需处理的系统...");
            return null;
        }
        try {
            for (PeripheralAppInfo app : selectedRecords) {
                app.setStatus(TxnStatus.INIT.getCode());
                app.setRtnCode("");
                app.setRtnMsg("");
                app.setInformTime("");
                app.setResultQryTime("");
                ptenudetails = toolsService.selectEnuDetail("ONEKEY_ACCT_CHK");
                for (Ptenudetail enu : ptenudetails) {
                    if (enu.getEnuitemvalue().equals(app.getAppChnCode())) {
                        app.setUrl(enu.getEnuitemexpand());
                        app.setAppName(enu.getEnuitemlabel());
                        app.setSmsDesc(enu.getEnuitemdesc());
                    }
                }
                logger.info("一键对账重置状态:" + app.getAppName() + "  URL=" + app.getUrl());
            }
        } catch (Exception ex) {
            logger.error("状态设置处理错误。", ex);
            MessageUtil.addError("状态设置处理错误。" + ex.getMessage());
        }
        return null;
    }

    private void processInformTxn(PeripheralAppInfo app) {
        //短信通知
        if (!StringUtils.isEmpty(app.getSmsDesc())) {
            processSMS(app);
        }
        if ("SPC1".equals(app.getAppChnCode())) {
            processInformTxn_webservice(app);
        } else {
            processInformTxn_Http(app);
        }
    }

    private void processInformTxn_Http(PeripheralAppInfo app) {
        T1001Request request = new T1001Request();
        request.getINFO().setTXNCODE("1001");
        request.getINFO().setVERSION("01");
        request.getINFO().setREQSN(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

        request.getBODY().setTXNDATE(txnDate);
        request.getBODY().setTXNTIME(new SimpleDateFormat("HHmmss").format(new Date()));

        request.getBODY().setCHNCODE(app.getAppChnCode());
        request.getBODY().setACTION("1");
        request.getBODY().setREMARK("start txn : 1001");

        String reqXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n" + request.toXml(request);
        logger.info("一键对账T1001请求报文" + reqXml);

        if (StringUtils.isEmpty(app.getUrl())) {
            throw new RuntimeException("系统渠道未定义服务URL");
        }
        String respXml = doPost(app.getUrl(), reqXml, "GBK");
        logger.info("一键对账T1001响应报文" + respXml);

        T1001Response response = new T1001Response();
        response = (T1001Response) response.toBean(respXml);

        String rtncode = response.getINFO().getRTNCODE();
        if ("0000".equals(rtncode)) {
            app.setStatus(TxnStatus.INFORM_SUCC.getCode());
        } else {
            app.setStatus(TxnStatus.INFORM_FAIL.getCode());
        }
        app.setRtnCode(rtncode);
        app.setRtnMsg(response.getINFO().getRTNMSG());
        app.setInformTime(new DateTime().toString("HH:mm:ss"));
    }

    private void processInformTxn_webservice(PeripheralAppInfo app) {
        if (StringUtils.isEmpty(app.getUrl())) {
            throw new RuntimeException("系统渠道未定义服务URL");
        }

        URL wsdlUrl = null;
        SBSSysServiceSoapBindingStub service = null;
        ScfDzInfoVO vo = new ScfDzInfoVO();
        vo.setTxnCode("1001");
        vo.setVersion("01");
        vo.setReqSn(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        vo.setTxnDate(txnDate);
        vo.setTxnTime(new SimpleDateFormat("HHmmss").format(new Date()));
        vo.setAction("1");
        vo.setChnCode("SPC1");

        ScfDzInfoVO respVo = null;
        try {
            wsdlUrl = new URL(app.getUrl());
            service = (SBSSysServiceSoapBindingStub) new SBSSysServiceServiceLocator().getSBSSysService(wsdlUrl);
            respVo = service.acceptB2BDzInfo(vo);
        } catch (Exception e) {
            throw new RuntimeException("WebService处理错误", e);
        }

        String rtncode = respVo.getRtnCode();
        if ("0000".equals(rtncode)) {
            app.setStatus(TxnStatus.INFORM_SUCC.getCode());
        } else {
            app.setStatus(TxnStatus.INFORM_FAIL.getCode());
        }
        app.setRtnCode(rtncode);
        app.setRtnMsg(respVo.getRtnMsg());
        app.setInformTime(new DateTime().toString("HH:mm:ss"));
    }

    //---
    private void processResultQryTxn(PeripheralAppInfo app) {
        if ("SPC1".equals(app.getAppChnCode())) {
            processResultQryTxn_webservice(app);
        } else {
            processResultQryTxn_http(app);
        }
    }

    private void processResultQryTxn_http(PeripheralAppInfo app) {
        T1002Request request = new T1002Request();
        request.getINFO().setTXNCODE("1002");
        request.getINFO().setVERSION("01");
        request.getINFO().setREQSN(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));

        request.getBODY().setCHANNEL(app.getAppChnCode());

        String reqXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>\n" + request.toXml(request);
        logger.info("一键对账T1002请求报文" + reqXml);
        String respXml = doPost(app.getUrl(), reqXml, "GBK");
        logger.info("一键对账T1002响应报文" + respXml);

        T1002Response response = new T1002Response();
        response = (T1002Response) response.toBean(respXml);

        String rtncode = response.getINFO().getRTNCODE();
        if ("0000".equals(rtncode)) {
            app.setStatus(TxnStatus.ACCT_SUCC_BANLANCE.getCode());
        } else if ("1000".equals(rtncode)) {
            app.setStatus(TxnStatus.ACCT_SUCC_NOTBANLANCE.getCode());
        } else if ("0001".equals(rtncode)) {
            app.setStatus(TxnStatus.ACCT_SUCC_NOTBANLANCE.getCode());
        }
        app.setRtnCode(rtncode);
        app.setRtnMsg(response.getINFO().getRTNMSG());
        app.setResultQryTime(new DateTime().toString("HH:mm:ss"));
    }

    private void processResultQryTxn_webservice(PeripheralAppInfo app) {
        if (StringUtils.isEmpty(app.getUrl())) {
            throw new RuntimeException("系统渠道未定义服务URL");
        }

        URL wsdlUrl = null;
        SBSSysServiceSoapBindingStub service = null;
        ScfDzInfoVO vo = new ScfDzInfoVO();
        vo.setTxnCode("1002");
        vo.setVersion("01");
        vo.setReqSn(new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()));
        vo.setTxnDate(txnDate);
        vo.setTxnTime(new SimpleDateFormat("HHmmss").format(new Date()));
        vo.setAction("1");
        vo.setChannel("SPC1");

        ScfDzInfoVO respVo = null;
        try {
            wsdlUrl = new URL(app.getUrl());
            service = (SBSSysServiceSoapBindingStub) new SBSSysServiceServiceLocator().getSBSSysService(wsdlUrl);
            respVo = service.acceptB2BDzInfo(vo);
        } catch (Exception e) {
            throw new RuntimeException("WebService处理错误", e);
        }

        String rtncode = respVo.getRtnCode();
        if ("0000".equals(rtncode)) {
            app.setStatus(TxnStatus.ACCT_SUCC_BANLANCE.getCode());
        } else if ("1000".equals(rtncode)) {
            app.setStatus(TxnStatus.ACCT_SUCC_NOTBANLANCE.getCode());
        } else if ("0001".equals(rtncode)) {
            app.setStatus(TxnStatus.ACCT_SUCC_NOTBANLANCE.getCode());
        }
        app.setRtnCode(rtncode);
        app.setRtnMsg(respVo.getRtnMsg());
        app.setResultQryTime(new DateTime().toString("HH:mm:ss"));
    }

    //=====
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

    private void processSMS(PeripheralAppInfo app) {
        SmsHelper.asyncSendSms(app.getSmsDesc(), app.getAppName() + "开始对账:" + txnDate);
    }

    public String getTxnDate() {
        return txnDate;
    }

    public void setTxnDate(String txnDate) {
        this.txnDate = txnDate;
    }


    public ToolsService getToolsService() {
        return toolsService;
    }

    public void setToolsService(ToolsService toolsService) {
        this.toolsService = toolsService;
    }

    public List<PeripheralAppInfo> getApps() {
        return apps;
    }

    public void setApps(List<PeripheralAppInfo> apps) {
        this.apps = apps;
    }


    public PeripheralAppInfo[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(PeripheralAppInfo[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public TxnStatus getTxnStatus() {
        return txnStatus;
    }

    public void setTxnStatus(TxnStatus txnStatus) {
        this.txnStatus = txnStatus;
    }
}
