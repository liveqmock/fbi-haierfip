package fip.gateway.sbs;

import com.ibm.ctg.client.ECIRequest;
import com.ibm.ctg.client.JavaGateway;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4MultiRecord;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.advance.utils.PropertyManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 临时测试 ：connection 优化.
 * User: zhanrui
 * Date: 2009-5-10
 * Time: 20:58:47
 * To change this template use File | Settings | File Templates.
 */
public class SbsCtgManager {

    private static Logger logger = LoggerFactory.getLogger(SbsCtgManager.class);
    private JavaGateway javaGatewayObject;

    private boolean bDataConv = true;
    private String strDataConv = "ASCII";

    private String strProgram = "SCLMPC";
    private String strChosenServer = "haier";

    private String strUrl = PropertyManager.getProperty("SBS_HOSTIP");
    private int iPort = Integer.parseInt(PropertyManager.getProperty("SBS_HOSTPORT"));

    private int iCommareaSize = 32000;

    private static int TXNCODE_OFFSET = 21;
    private static int TXNCODE_LENGTH = 4;

    private static String CICS_USERID = "CICSUSER";
    private static String CICS_PWD = "";


    /*
     消费信贷扣款成功后入帐处理: a541
     房贷扣款成功后入帐处理: a542
     消费信贷系统放款处理：aa54
      */
    public byte[] processAccount(List list, String txncode) throws IOException {

        ECIRequest eciRequestObject = null;
        String buff = "";

        try {
            byte[] abytCommarea = new byte[iCommareaSize];

            javaGatewayObject = new JavaGateway(strUrl, iPort);

            eciRequestObject = ECIRequest.listSystems(20);
            flowRequest(eciRequestObject);
            int no = 1001;

            if (eciRequestObject.SystemList.isEmpty() == true) {
                System.out.println("No CICS servers have been defined.");
                if (javaGatewayObject.isOpen() == true) {
                    javaGatewayObject.close();
                }
            }
            //打包
            buff = "TPEI" + txncode + "  010       XD01XD01"; //包头内容，xxxx交易，010网点，MPC1终端，MPC1柜员，包头定长51个字符
            System.arraycopy(getBytes(buff), 0, abytCommarea, 0, buff.length()); //打包包头

            no = no + 1;

            //打包包体
            setBufferValues(list, abytCommarea);

//            System.out.println("发送包内容:\n" + new String(abytCommarea));
            logger.info("发送包内容:\n" + new String(abytCommarea));

            //发送包
            eciRequestObject = new ECIRequest(ECIRequest.ECI_SYNC, //ECI call type
                    strChosenServer, //CICS server
                    "1", //CICS userid
                    "1", //CICS password
                    strProgram, //CICS program to be run
                    null, //CICS transid to be run
                    abytCommarea, //Byte array containing the
                    // COMMAREA
                    iCommareaSize, //COMMAREA length
                    ECIRequest.ECI_NO_EXTEND, //ECI extend mode
                    0);                       //ECI LUW token


            //获取返回报文

            String rtnStr = new String(abytCommarea);

            if (flowRequest(eciRequestObject) == true) {
                //解sof
//                System.out.println("返回值11为\n" + rtnStr);
                logger.info("返回值11为\n" + rtnStr);
            }
//            System.out.println("返回值22为\n" + rtnStr);
            logger.info("返回值22为\n" + rtnStr);

            if (javaGatewayObject.isOpen() == true) {
                javaGatewayObject.close();
            }

            return abytCommarea;

        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("SBS系统不能连通或连接超时！");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


    /**
     * 处理查询交易多包返回的情况
     * 注意：请求包中的起始笔数栏位应放在最后一个。
     */
    public void processMultiResponsePkg(SBSRequest request, SBSResponse4MultiRecord response) {
        ECIRequest eciRequestObject = null;
        javaGatewayObject = null;

        try {
            javaGatewayObject = new JavaGateway(strUrl, iPort);

            eciRequestObject = ECIRequest.listSystems(20);
            flowRequest(eciRequestObject);

            if (eciRequestObject.SystemList.isEmpty()) {
                System.out.println("No CICS servers have been defined.");
                if (javaGatewayObject.isOpen()) {
                    javaGatewayObject.close();
                }
                throw new Exception("未定义 CICS 服务器，请确认！");
            }

            boolean isEnd = false;
            //已接收的明细记录笔数
            int receivedCount = 0;
            //已接收的报文个数
            int receivedPkgCount = 0;
            //每个包的起始笔数
            int beginNumber = 1;
            //通讯耗时
            long txTotalTime = 0;

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss:SSS");
            String sendTime;

            while (!isEnd) {
                String requestBuffer = "";

                byte[] abytCommarea = new byte[iCommareaSize];

                //包头内容，xxxx交易，010网点，MPC1终端，MPC1柜员，包头定长51个字符
                requestBuffer = "TPEI" + request.getTxncode() + "  010       MT01MT01";
                //打包包头
                System.arraycopy(getBytes(requestBuffer), 0, abytCommarea, 0, requestBuffer.length());

                List paramList = new ArrayList();
                for (String s : request.getParamList()) {
                    paramList.add(s);
                }

                //处理请求包中的起始笔数
                paramList.add(StringUtils.leftPad(String.valueOf(beginNumber), 6, '0'));

                //打包包体
                setBufferValues(paramList, abytCommarea);

                sendTime = sdf.format(new Date());
                logger.info("发送报文: " + sendTime + format16(truncBuffer(abytCommarea)));

                long starttime = System.currentTimeMillis();
                //发送包
                eciRequestObject = new ECIRequest(ECIRequest.ECI_SYNC, //ECI call type
                        strChosenServer, //CICS server
                        CICS_USERID, //CICS userid
                        CICS_PWD, //CICS password
                        strProgram, //CICS program to be run
                        null, //CICS transid to be run
                        abytCommarea, //Byte array containing the
                        // COMMAREA
                        iCommareaSize, //COMMAREA length
                        ECIRequest.ECI_NO_EXTEND, //ECI extend mode
                        0);                       //ECI LUW token

                //获取返回报文
                if (flowRequest(eciRequestObject)) {
                    logger.info("接收报文(发送时间:" + sendTime + "): " + format16(truncBuffer(abytCommarea)));
                }

                long endtime = System.currentTimeMillis();

                receivedPkgCount++;

                response.init(abytCommarea);
                String formcode = response.getFormcode();

                logger.info("===接收报文序号：" + StringUtils.leftPad(String.valueOf(receivedPkgCount), 6, '0') + "   返回FORMCODE:" + formcode + "   本包通讯耗时:" + (endtime - starttime) + "ms.");

                txTotalTime = txTotalTime + (endtime - starttime);

                if (formcode.substring(0, 1).equals("T")) {
                    int totcnt = Integer.parseInt(response.getSofDataHeader().getTotcnt());
                    int curcnt = Integer.parseInt(response.getSofDataHeader().getCurcnt());
                    receivedCount += curcnt;
                    beginNumber = receivedCount + 1;
                    isEnd = receivedCount >= totcnt;
                } else {
                    isEnd = true;
                }
            }
            logger.info("本次通讯报文个数:" + receivedPkgCount + "   通讯总耗时:" + txTotalTime / 1000 + "秒   平均每包耗时:" + txTotalTime / receivedPkgCount + "ms.");
        } catch (Exception e) {
            logger.error("与SBS通讯出现问题：", e);
            throw new RuntimeException(e.getMessage());
        } finally {
            if (javaGatewayObject != null) {
                if (javaGatewayObject.isOpen()) {
                    try {
                        javaGatewayObject.close();
                    } catch (IOException e) {
                        logger.error("与SBS通讯出现问题：", e);
                        throw new RuntimeException(e.getMessage());
                    }
                }
            }
        }
    }

    public void init(){
        ECIRequest eciRequestObject = null;
        javaGatewayObject = null;

        try {
            javaGatewayObject = new JavaGateway(strUrl, iPort);

            eciRequestObject = ECIRequest.listSystems(20);
            flowRequest(eciRequestObject);

            if (eciRequestObject.SystemList.isEmpty()) {
                System.out.println("No CICS servers have been defined.");
                if (javaGatewayObject.isOpen()) {
                    javaGatewayObject.close();
                }
                throw new Exception("未定义 CICS 服务器，请确认！");
            }

        } catch (Exception e) {
            logger.error("与SBS通讯出现问题：", e);
            if (javaGatewayObject != null) {
                if (javaGatewayObject.isOpen()) {
                    try {
                        javaGatewayObject.close();
                    } catch (IOException ex) {
                        logger.error("与SBS通讯出现问题：", ex);
                        throw new RuntimeException(ex.getMessage());
                    }
                }
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            ;
        }
    }

    public void shutdown(){
        if (javaGatewayObject != null) {
            if (javaGatewayObject.isOpen()) {
                try {
                    javaGatewayObject.close();
                } catch (IOException ex) {
                    logger.error("与SBS通讯出现问题：", ex);
                    throw new RuntimeException(ex.getMessage());
                }
            }
        }
    }

    /**
     * 处理单包查询的情况
     *
     * @throws Exception
     */
    public void processSingleResponsePkg(SBSRequest request, SBSResponse4SingleRecord response) {
        ECIRequest eciRequestObject = null;
        //javaGatewayObject = null;

        try {
            //通讯耗时
            long txTotalTime = 0;
            String requestBuffer = "";

            byte[] abytCommarea = new byte[iCommareaSize];

            //包头内容，xxxx交易，010网点，MPC1终端，MPC1柜员，包头定长51个字符
            requestBuffer = "TPEI" + request.getTxncode() + "  010       MT01MT01";
            //打包包头
            System.arraycopy(getBytes(requestBuffer), 0, abytCommarea, 0, requestBuffer.length());

            List paramList = new ArrayList();
            for (String s : request.getParamList()) {
                paramList.add(s);
            }

            //打包包体
            setBufferValues(paramList, abytCommarea);

            String sendTime = new SimpleDateFormat("HH:mm:ss:SSS").format(new Date());
            logger.info("交易" + request.getTxncode() + " 发送报文: " + sendTime + format16(truncBuffer(abytCommarea)));

            long starttime = System.currentTimeMillis();
            //发送包
            eciRequestObject = new ECIRequest(ECIRequest.ECI_SYNC, //ECI call type
                    strChosenServer, //CICS server
                    CICS_USERID, //CICS userid
                    CICS_PWD, //CICS password
                    strProgram, //CICS program to be run
                    null, //CICS transid to be run
                    abytCommarea, //Byte array containing the
                    // COMMAREA
                    iCommareaSize, //COMMAREA length
                    ECIRequest.ECI_NO_EXTEND, //ECI extend mode
                    0);                       //ECI LUW token

            //获取返回报文
            if (flowRequest(eciRequestObject)) {
                logger.info("交易" + request.getTxncode() + " 接收报文(发送时间:" + sendTime + "): " + format16(truncBuffer(abytCommarea)));
            }

            long endtime = System.currentTimeMillis();

            response.init(abytCommarea);
            String formcode = response.getFormcode();

            logger.info("===返回FORMCODE:" + formcode + "   本包通讯耗时:" + (endtime - starttime) + "ms.");

        } catch (Exception e) {
            logger.error("与SBS通讯出现问题：", e);
            if (javaGatewayObject != null) {
                if (javaGatewayObject.isOpen()) {
                    try {
                        javaGatewayObject.close();
                    } catch (IOException ex) {
                        logger.error("与SBS通讯出现问题：", ex);
                        throw new RuntimeException(ex.getMessage());
                    }
                }
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            ;
        }
    }

    //===================================================================================================
    private byte[] getBytes(String source) throws java.io.UnsupportedEncodingException {
        if (bDataConv) {
            return source.getBytes(strDataConv);
        } else {
            return source.getBytes();
        }
    }

    private void setBufferValues(List list, byte[] bb) {
        int start = 51;
        for (int i = 1; i <= list.size(); i++) {
            String value = list.get(i - 1).toString();

            String size = "";
            try {
                size = new String(value.getBytes("GBK"), "ISO-8859-1");
            } catch (Exception e) {
                e.printStackTrace();
            }

            //logger.info(i + " " + start + " " + size.length() + " " + value);
            setVarData(start, value, bb);
            start = start + size.length() + 2;
        }
    }

    private void setVarData(int pos, String data, byte[] aa) {

        String size = "";
        try {
            size = new String(data.getBytes("GBK"), "ISO-8859-1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        short len = (short) size.length();

        byte[] slen = new byte[2];
        slen[0] = (byte) (len >> 8);
        slen[1] = (byte) (len >> 0);
        System.arraycopy(slen, 0, aa, pos, 2);
        System.arraycopy(data.getBytes(), 0, aa, pos + 2, len);
    }


    private boolean flowRequest(ECIRequest requestObject) throws Exception {
        int iRc = javaGatewayObject.flow(requestObject);
        String msg = null;
        switch (requestObject.getCicsRc()) {
            case ECIRequest.ECI_NO_ERROR:
                if (iRc == 0) {
                    return true;
                } else {
                    if (javaGatewayObject.isOpen() == true) {
                        javaGatewayObject.close();
                    }
                    throw new Exception("SBS Gateway 出现错误("
                            + requestObject.getRcString()
                            + "), 请查明原因，重新发起交易");
                }
            case ECIRequest.ECI_ERR_SECURITY_ERROR:
                msg = "SBS CICS: 用户名或密码错误";
                break;
            case ECIRequest.ECI_ERR_TRANSACTION_ABEND:
                msg = "SBS CICS : 没有权限运行此笔CICS交易";
                break;
            default:
                msg = "SBS CICS : 出现错误，请查找原因。" + requestObject.getCicsRcString();
        }
        logger.info("ECI returned: " + requestObject.getCicsRcString());
        logger.info("Abend code was " + requestObject.Abend_Code + " ");
        if (javaGatewayObject.isOpen() == true) {
            javaGatewayObject.close();
        }
        throw new Exception(msg);
    }

    /**
     * 16进制格式化输出
     *
     * @param buffer
     * @return
     */
    private String format16(byte[] buffer) {
        StringBuilder result = new StringBuilder();
        result.append("\n");
        int n = 0;
        byte[] lineBuffer = new byte[16];
        for (byte b : buffer) {
            if (n % 16 == 0) {
                result.append(String.format("%05x: ", n));
                lineBuffer = new byte[16];
            }
            result.append(String.format("%02x ", b));
            lineBuffer[n % 16] = b;
            n++;
            if (n % 16 == 0) {
                result.append(new String(lineBuffer));
                result.append("\n");
            }

            //TODO
            if (n >= 1024) {
                result.append("报文过大，已截断...");
                break;
            }

        }
        for (int k = 0; k < (16 - n % 16); k++) {
            result.append("   ");
        }
        result.append(new String(lineBuffer));
        result.append("\n");
        return result.toString();
    }

    /**
     * @param buffer
     * @return
     */
    private byte[] truncBuffer(byte[] buffer) {
        int count = 0;
        for (int i = 0; i < iCommareaSize; i++) {
            if (buffer[iCommareaSize - 1 - i] == 0x00) {
                count++;
            } else {
                break;
            }
        }
        byte[] outBuffer = new byte[iCommareaSize - count];
        System.arraycopy(buffer, 0, outBuffer, 0, outBuffer.length);
        return outBuffer;
    }

}
