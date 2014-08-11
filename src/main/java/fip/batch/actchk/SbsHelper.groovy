package fip.batch.actchk

import fip.gateway.JmsManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Created by zhanrui on 2014/8/4.
 */
class SbsHelper {
    private static final Logger logger = LoggerFactory.getLogger(SbsHelper.class)

    static void processOneSbsMsg(lines, int startNum, String channelId, String bankCode, String txnDate) {
        int iCommareaSize = 32000;
        byte[] tiabuf = new byte[iCommareaSize];

        String buff = ""
        String txncode = "n117"
        buff = "TPEI" + txncode + "  010       MT01MT01" //包头内容定长51个字符
        System.arraycopy(buff.getBytes(), 0, tiabuf, 0, buff.length()) //打包包头

        def tiaFieldsList = [txnDate, startNum.toString().padLeft(6, '0'), channelId,  bankCode.padRight(12, " ")]
        setBufferValues(tiaFieldsList, tiabuf)
        logger.info("===TIA:" + new String(tiabuf))

        byte[] toabuf = []
        int bufp = 0
        def instance = null
        try {
            instance = JmsManager.getInstance()
            toabuf = instance.sendAndRecvForDepCoreInterface(tiabuf)
        } catch (Exception e) {
            logger.error("MQ处理异常，连接错误或超时。", e)
            throw new RuntimeException("DEP数据交换平台接口异常" + e.getMessage(), e)
        } finally {
            instance = null
        }

        logger.info("===TOA:" + new String(toabuf))

        short msgLen = byteToShort(toabuf[28], toabuf[27])


        int totcnt = 0, curcnt = 0

        bufp = 29 //SOF-DATA-LEN	包长度	X(2)
        if (msgLen > 0) {
            //检查返回交易码
            txncode = new String((byte[]) toabuf[21..24])
            if (txncode != 'T821') {
                def errmsg = "SBS返回异常报文：${txncode} " + new String((byte[]) toabuf[29..29 + msgLen - 1]).trim()
                if (txncode == 'M104') { //本日无数据
                    logger.info(errmsg)
                    return
                }
                throw new RuntimeException(errmsg)
            }

            byte[] b1 = toabuf[bufp..bufp + 5]
            bufp += 6
            byte[] b2 = toabuf[bufp..bufp + 5]
            bufp += 6
            totcnt = new String(b1).toInteger()
            curcnt = new String(b2).toInteger()
            println "===TOA:   ${totcnt}  ${curcnt}"

            curcnt.times {
                short recordlen = byteToShort(toabuf[bufp + 1], toabuf[bufp])
                bufp += 2
                byte[] recordbuf = toabuf[bufp..bufp + recordlen - 1]
                def record = new String(recordbuf)
                lines.add(record)
                bufp += recordlen
            }

            int recvcnt = startNum - 1
            recvcnt += curcnt
            if (recvcnt < totcnt) {
                processOneSbsMsg(lines, recvcnt + 1, channelId, bankCode, txnDate)
            }
        }
    }
    private static short byteToShort(byte high, byte low) {
        byte[] msglenBuf = [high, low]
        short tmp1 = msglenBuf[0] & 0xFF
        short tmp2 = (msglenBuf[1] << 8) & 0xFF00
        short msgLen = tmp2 | tmp1
        msgLen
    }

    //java code  for sbs msg
    private static void setBufferValues(List list, byte[] bb) throws UnsupportedEncodingException {
        int start = 51;
        for (int i = 1; i <= list.size(); i++) {
            String value = list.get(i - 1).toString();
            setVarData(start, value, bb);
            start = start + value.getBytes("GBK").length + 2;
        }
    }

    private static void setVarData(int pos, String data, byte[] aa) throws UnsupportedEncodingException {
        short len = (short) data.getBytes("GBK").length;

        byte[] slen = new byte[2];
        slen[0] = (byte) (len >> 8);
        slen[1] = (byte) (len);
        System.arraycopy(slen, 0, aa, pos, 2);
        System.arraycopy(data.getBytes(), 0, aa, pos + 2, len);
    }


}
