package fip.utils
import com.ibm.ctg.client.ECIRequest
import com.ibm.ctg.client.JavaGateway

import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-12-26
 * Time: 下午12:56
 * To change this template use File | Settings | File Templates.
 */
class SBSTest {
    def private static final AtomicInteger threadcount = new AtomicInteger(0)
    def private static final AtomicInteger failcount = new AtomicInteger(0)
    def private static final AtomicInteger txnerr = new AtomicInteger(0)
    def private static final AtomicInteger txnsucc = new AtomicInteger(0)
    def private static ConcurrentHashMap<Long, Long> elapsedMap = new ConcurrentHashMap<Long, Long>();

    def txn_date = "20121224"

    static void main(args) {
        int threadsNum = 10, repeats = 5, separateTime = 1

        SBSTest tester = new SBSTest()
        if (args.size() > 0) {
            tester.txn_date = args[0]
            threadsNum = args[1].toInteger()
            repeats = args[2].toInteger()
        }else{
            println "Usage: java -jar sbstest.jar txndate threads repeats"
            return
        }

        def suminfo = {
            def i = 0, total = 0
            elapsedMap.each {
                i++
                total += it.value
            }
            return "成功交易：${i}, 耗时：${Math.round(total / 1000)}秒, 平均每笔耗时: ${Math.round(total / i)} ms"
        }

        def begin = System.currentTimeMillis();
        repeats.times {
            def threads = []
            threadsNum.times {
                threads.add(Thread.start { tester.process("n022", suminfo) })
                threads.add(Thread.start { tester.process("n024", suminfo) })
            }
            threads.each { it.join() }
            Thread.sleep(separateTime * 1000)
        }

        def end = System.currentTimeMillis();
        def time = Math.round((end - begin) / 1000)

        threadsNum = threadsNum * 2 * repeats;
        println("总交易数：${threadsNum}笔, 总耗时：${time}秒, 每秒交易数(成功)：${(threadsNum - failcount) / time}")
        println("系统失败交易数：${failcount.get()}笔")
        println("交易业务处理成功数：${txnsucc.get()}笔, 流水号不符交易数：${txnerr.get()}笔")

        println suminfo()
    }

    def process(String txn_code, suminfo) {
        int txn_sn = threadcount.incrementAndGet();
        def begin = System.currentTimeMillis();
        try {
            def  beginTime = new Date().format("HH:mm:ss:SSS")
            def rtnmsg = processOneSbsMsg(txn_code, txn_sn, "getTxn_${txn_code}_TIA"(txn_sn))
            def end = System.currentTimeMillis();
            elapsedMap.put(Thread.currentThread().getId(), end - begin);
            println "[${txn_code}:${end-begin}]${beginTime}->${rtnmsg}  ${suminfo()}"
        } catch (Exception e) {
            failcount.incrementAndGet();
            e.printStackTrace();
            println("处理异常:" + e.stackTrace);
        }
    }

    def processOneSbsMsg(String txn_code, int txn_sn, List reqList) {
        def rtnmsg = ""
        int iCommareaSize = 32000;
        byte[] tiabuf = new byte[iCommareaSize];

        String buff = ""
        buff = "TPEI" + txn_code + "  010       MT01MT01" //包头内容定长51个字符
        System.arraycopy(buff.getBytes(), 0, tiabuf, 0, buff.length()) //打包包头

        //打包包体
        setBufferValues(reqList, tiabuf)

        //logger.info("===TIA:" + new String(tiabuf))
        def toabuf = processTxn(tiabuf)

        //logger.info("===TOA:" + new String(toabuf))
        short msgLen = byteToShort(toabuf[28], toabuf[27])
        def formcode = new String((byte[]) toabuf[21..24])
        def req_txnsn = 'MPC' + ('' + txn_sn).padLeft(15, '0')
        if (msgLen > 0) {
            if (formcode == 'T543' || formcode == 'T531' || formcode == 'T999') {
                txnsucc.incrementAndGet()
                def txnsnOffset = getTxnsnOffset(txn_code);
                def FBTIDX = new String((byte[]) toabuf[txnsnOffset..(txnsnOffset + 17)]).trim()
                rtnmsg =  "[${formcode}]${new Date().format("HH:mm:ss:SSS")} 流水号:${req_txnsn}. 流水号不匹配：${txnerr}笔. 总线程${threadcount},失败:${failcount}"
                if (FBTIDX != req_txnsn) {
                    txnerr.incrementAndGet()
                }
            }else{
                rtnmsg =  "[${formcode}]${new Date().format("HH:mm:ss:SSS")} 请求流水号：${req_txnsn}"
            }
        }else{
            //rtnmsg = "返回报文长度${msgLen}"
            rtnmsg =  "[${formcode}]${new Date().format("HH:mm:ss:SSS")} 请求流水号：${req_txnsn}"
        }
        rtnmsg
    }

    def getTxnsnOffset = { txn_code ->
        switch (txn_code) {
            case 'n022':
                return 71
            case 'n024':
                return 72
        }
    }

    private short byteToShort(byte high, byte low) {
        byte[] msglenBuf = [high, low]
        short tmp1 = msglenBuf[0] & 0xFF
        short tmp2 = (msglenBuf[1] << 8) & 0xFF00
        short msgLen = tmp2 | tmp1
        msgLen
    }

    //java code  for sbs msg
    private void setBufferValues(List list, byte[] bb) throws UnsupportedEncodingException {
        int start = 51;
        for (int i = 1; i <= list.size(); i++) {
            String value = list.get(i - 1).toString();
            setVarData(start, value, bb);
            start = start + value.getBytes("GBK").length + 2;
        }
    }

    private void setVarData(int pos, String data, byte[] aa) throws UnsupportedEncodingException {
        short len = (short) data.getBytes("GBK").length;

        byte[] slen = new byte[2];
        slen[0] = (byte) (len >> 8);
        slen[1] = (byte) (len);
        System.arraycopy(slen, 0, aa, pos, 2);
        System.arraycopy(data.getBytes(), 0, aa, pos + 2, len);
    }

    //===
    public static byte[] processTxn(byte[] tiaBytes) {
        String strUrl = "10.143.20.130";
        int iPort = 2006;

        int iCommareaSize = 32000;
        String strChosenServer = "haier";
        String strProgram = "SCLMPC";
        String CICS_USERID = "CICSUSER";
        String CICS_PWD = "";

        ECIRequest eciRequestObject = null;
        JavaGateway javaGatewayObject = null;

        javaGatewayObject = new JavaGateway(strUrl, iPort);

        eciRequestObject = ECIRequest.listSystems(20);
        flowRequest(javaGatewayObject, eciRequestObject);

        if (eciRequestObject.SystemList.isEmpty()) {
            System.out.println("No CICS servers have been defined.");
            if (javaGatewayObject.isOpen()) {
                javaGatewayObject.close();
            }
            throw new Exception("未定义 CICS 服务器，请确认！");
        }

        try {
            byte[] abytCommarea = new byte[iCommareaSize];

            //javaGatewayObject = getGateWay(strUrl, iPort)
            //打包
            byte[] headBytes = tiaBytes;
            System.arraycopy(headBytes, 0, abytCommarea, 0, headBytes.length); //打包包头

            //logger.info("发送包内容:\n" + new String(tiaBytes));

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
            flowRequest(javaGatewayObject, eciRequestObject);

            //logger.info("【CtgManager】返回包内容:" + format16(abytCommarea));
            return abytCommarea;
        } catch (Exception e) {
            println("与SBS通讯出现问题：" + e.stackTrace);
            throw new RuntimeException(e);
        } finally {
            if (javaGatewayObject != null) {
                if (javaGatewayObject.isOpen()) {
                    try {
                        javaGatewayObject.close();
                    } catch (IOException e) {
                        println("与SBS通讯出现问题：" + + e.message);
                    }
                }
            }
        }
    }

    private static JavaGateway getGateWay(String strUrl, int iPort) {
        JavaGateway javaGatewayObject
        ECIRequest eciRequestObject
        javaGatewayObject = new JavaGateway(strUrl, iPort);
        eciRequestObject = ECIRequest.listSystems(20);
        flowRequest(javaGatewayObject, eciRequestObject);

        if (eciRequestObject.SystemList.isEmpty()) {
            System.out.println("No CICS servers have been defined.");
            if (javaGatewayObject.isOpen()) {
                javaGatewayObject.close();
            }
        }
        javaGatewayObject
    }

    private static boolean flowRequest(JavaGateway javaGatewayObject, ECIRequest requestObject) throws Exception {
        int iRc = javaGatewayObject.flow(requestObject);
        String msg = null;
        switch (requestObject.getCicsRc()) {
            case ECIRequest.ECI_NO_ERROR:
                if (iRc == 0) {
                    return true;
                } else {
                    if (javaGatewayObject.isOpen()) {
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
        //logger.info("ECI returned: " + requestObject.getCicsRcString());
        //logger.info("Abend code was " + requestObject.Abend_Code + " ");
        if (javaGatewayObject.isOpen()) {
            javaGatewayObject.close();
        }
        throw new Exception(msg);
    }

    //==
    List getTxn_n024_TIA(int txn_sn) {
        List list = new ArrayList();
        //n020 CTY 汇出汇款
        //list.add("MPC1000147231420  ");     //MPC流水号===================>每次发生变化
        list.add("MPC" + ('0' + txn_sn).padLeft(15, '0'));                       //  MPC流水号         18位
        list.add("010");                    //交易机构
        list.add(txn_date);                                //  交易日期      8位
        //list.add("20130121");               //委托日期
        list.add("010104");                 //客户号
        list.add("CTY");                    //交易类型
        list.add("001");                    //交易货币
        list.add("+0000000000000.14");      //交易金额
        list.add("T");                      //汇款类型
        list.add("01");                     //汇款帐户类型
        list.add("801000010002011001    ");//汇款帐户
        list.add("1");                     //费用帐户类型
        list.add("                      ");//费用帐户
        list.add("37101985510051005301-9999    ");  //收款人帐号
        list.add("重庆海尔投资发展有限公司      ");//收款人名称
        list.add("中国建设银行股份有限公司泉州分行                                                    ");//收款行行名
        list.add("103452006018                                                                    ");//保留项
        list.add("                                        ");                                        //保留项
        list.add("1                                                                               ");//汇款人名称150
        list.add("GE冰箱展台制作维修费                                                            ");//汇款用途150
        list.add(" ");                                            //保留项
        list.add("109452006018"); //收款行机构号、行号
        list.add("保留项    ");   //保留项
        list.add(" ");           //保留项
        list.add("FSPP293062000001");   //FS流水号
        list.add("2100000147259198");   //交易流水号
        return list;
    }

    List getTxn_n022_TIA(int txn_sn) {
        List list = new ArrayList();
        //n022       对公代理支付结果查询
        //list.add("MPC" + (threadcount.get().toString()).padLeft(15, '0'));                       //  MPC流水号         18位
        list.add("MPC" + ('0' + txn_sn).padLeft(15, '0'));                       //  MPC流水号         18位
        list.add("2100000147259198");                         //  交易流水号    16位
        list.add(txn_date);                                //  交易日期      8位
        list.add("105452006018");                             //  收款行行号   12位
        return list;
    }
}