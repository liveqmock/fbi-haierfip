package fip.batch.actchk
import fip.common.utils.sms.SmsTool
import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pub.platform.advance.utils.PropertyManager
/**
 * 到账通知对帐 （供应链融资平台SCF）.
 * User: zhanrui
 * Date: 2014-8-26
 */
class ZongFen4SCFHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZongFen4SCFHandler.class)

    //String txn_date = "19991231"
//    String txn_date = "20140802"
    def dbparam
    def db
//    def eventId = "ZF001001"
    def ftpmain = "D:/ftpmain"

    static void main(args) {

        ZongFen4SCFHandler handler = new ZongFen4SCFHandler()
//        handler.processBankMsg_test()
//        handler.startActChk(false, "MBP1", "105", "20140603", "ZF001001", "mbp")
        handler.startActChk(false, "SCF1", "313", "20140804", "ZF002001", "scf")

        //handler.notifyResult()
    }

    //WEB层调用接口
    public void startActChk4Web(String yyyymmdd, String bankcode) {
/*
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }
*/

        startActChk(false, "SCF1", bankcode, yyyymmdd, "scf")
    }
    //定时任务调用接口
    public void startActChk4Cron() {
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }

        def txnDate = (new Date() - 1).format('yyyyMMdd');

        //平安
        startActChk(true, "SCF1", "307452004818", txnDate, "scf")
        //中信
        startActChk(true, "SCF1", "302452036174", txnDate, "scf")
    }

    /**
     * @param isCheckSuccessFlag   true:进行对账前先检查已成功标志 false:进行对账前不检查已成功标志
     * @param channelId    用来填写 SBS交易的报文头
     * @param bankCode
     * @param txnDate
     * @param ftpUserName
     */
    private void startActChk(boolean isCheckSuccessFlag, String channelId, String bankCode, String txnDate, String ftpUserName) {
        //1.初始化环境
        try {
            println "=============" + txnDate
            initDB()
        } catch (Exception e) {
            logger.error("对账时出现系统错误！", e)
            return
        }

        String eventId = "ZF" + bankCode;

        try {
            def db_txn_date = db.firstRow("select txn_date  from EVT_MAININFO where evt_id = ${eventId}").txn_date
            def succ_flag = db.firstRow("select succ_flag  from EVT_MAININFO where evt_id = ${eventId}").succ_flag

            def sbsCode = 'SBS_' + bankCode
            def delSql = "delete from chk_zongfen_txn where txn_date = ${txnDate} and send_sys_id in ('${bankCode}' ,'${sbsCode}')"
            logger.info(delSql)
            if (db_txn_date == txnDate) {
                if (isCheckSuccessFlag) {
                    if (succ_flag == '1') { //已操作成功（包括获取数据和校验数据）
                        logger.info("到账通知对账已成功。")
                        return
                    }
                }
                db.execute(delSql)
            } else {
                updateOperationEventMainInfo(eventId, txnDate, false, "开始处理", "开始处理")
                db.execute("update EVT_MAININFO set evt_msg_code= '1000' where evt_id = ${eventId}")
                db.execute(delSql)
            }
        } catch (Exception e) {
            logger.error("对账时出现系统错误！", e)
            return
        }

        def start = System.currentTimeMillis()

        //2.获取银行对账数据
        def   txnFileName = ""
        try {
            if ('105' == bankCode) {
                txnFileName = "${ftpmain}/${ftpUserName}/${txnDate}.xml"
            } else {
                txnFileName = "${ftpmain}/${ftpUserName}/${txnDate}_${bankCode}.xml"
            }
            processBankMsg(bankCode, txnDate, txnFileName)
        } catch (Exception e) {
            logger.error("银行相关处理错误", e)
            def msg = "到账通知对账异常:日期[${txnDate}] " + e.getMessage()
            def smsmsg = msg.size() <= 200 ? msg : msg.substring(0, 200)
            def mailmsg = msg.size() <= 2000 ? msg : msg.substring(0, 2000)
            updateOperationEventMainInfo(eventId,  txnDate, false, smsmsg, mailmsg)
            return
        }

        //3.获取SBS对账数据
        try {
            processSBSMsg(channelId, bankCode, txnDate)
        } catch (Exception sbsex) {
            logger.error("获取SBS对账数据错误。", sbsex)
            def msg = "到账通知对账异常:日期[${txnDate}] 错误信息:" + sbsex.getMessage()
            def smsmsg = msg.size() <= 200 ? msg : msg.substring(0, 200)
            def mailmsg = msg.size() <= 2000 ? msg : msg.substring(0, 2000)
            updateOperationEventMainInfo(eventId,  txnDate, false, smsmsg, mailmsg)
            return
        }

        updateOperationEventMainInfo(eventId,  txnDate, true, "获取对账数据完成.", "获取对账数据完成.")

        //4.开始校验
        verifyData(bankCode, txnDate)

        def end = System.currentTimeMillis()

        println "---end---" + (end - start) / 1000
//        db.close()
    }

    public void verifyData(String bankCode, String txnDate) {
        String eventId = "ZF" + bankCode;

        //查看事件处理状态，
        def succ_flag = db.firstRow("select succ_flag  from EVT_MAININFO where evt_id = ${eventId}").succ_flag
        if (succ_flag == '0') {  //操作失败
            return
        }

        def sbs_bank = "SBS_" + bankCode;

        //校验对账结果
        VerifyDataHelper.verify(db, txnDate, sbs_bank, bankCode)

        def bankCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${txnDate} and send_sys_id = '${bankCode}'").cnt
        def sbsCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${txnDate} and send_sys_id = '${sbs_bank}'").cnt
        def totalCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${txnDate} and send_sys_id in ('${sbs_bank}','${bankCode}')").cnt
        def totalFailCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${txnDate} and (chksts is null or chksts != '0') and send_sys_id in ('${sbs_bank}','${bankCode}') ").cnt

        if (totalFailCount == 0) {
            def msg = "银行[${bankCode}]到账通知对账:平帐,日期:${txnDate},银行:${bankCount}笔,SBS:${sbsCount}笔"
            updateTxnEventMainInfo(eventId, "0000", msg, msg)
        } else {
            def errmsg = "银行[${bankCode}]到账通知对账:不平,日期:${txnDate},银行:${bankCount}笔,SBS:${sbsCount}笔,不平:${totalFailCount}笔"
            updateTxnEventMainInfo(eventId, "1000", errmsg, errmsg)
        }
    }

    //=============
    //WEB层调用接口
    public void notifyResult4Web(String yyyymmdd, String bankcode) {
/*
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }
*/

        initDB()
        notifyInfo("ZF" + bankcode)
    }
    //定时任务调用接口
    public void notifyResult4Cron() {
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }
        initDB()
        notifyInfo("ZF307452004818")
        notifyInfo("ZF302452036174")
    }


    //================================================================================================
    private void initDB() {
        try {
            def dbdrv = PropertyManager.getProperty("pub.platform.db.ConnectionManager.sDBDriver")
            def dburl = PropertyManager.getProperty("pub.platform.db.ConnectionManager.sConnStr")
            def dbuser = PropertyManager.getProperty("pub.platform.db.ConnectionManager.user")
            def dbpwd = PropertyManager.getProperty("pub.platform.db.ConnectionManager.passwd")

            dbparam = [url: dburl, user: dbuser, password: dbpwd, driver: dbdrv]
            db = Sql.newInstance(dbparam.url, dbparam.user, dbparam.password, dbparam.driver)
        } catch (Exception e) {
            logger.error("数据库连接错误。", e)
            throw new RuntimeException("数据库连接错误。" + e.getMessage(), e)
        }
    }

    //更新操作事件信息
    private void updateOperationEventMainInfo(String evt_id, String txnDate,  boolean isSuccess, String sms_info, String mail_info) {
        def evt_date = new Date().format('yyyyMMdd')
        def evt_time = new Date().format('HH:mm:ss')
        def evt_succ_flag
        if (isSuccess) evt_succ_flag = "1"
        else evt_succ_flag = "0"
        db.execute("""update EVT_MAININFO set
                        evt_date = ${evt_date},
                        evt_time = ${evt_time},
                        txn_date = ${txnDate},
                        txn_time = '00:00:00',
                        succ_flag = ${evt_succ_flag},
                        succ_count = 0,
                        fail_count = 0,
                        sms_info=${sms_info},
                        mail_info=${mail_info}
                        where evt_id = ${evt_id}""")
    }
    //更新业务事件信息
    private void updateTxnEventMainInfo(String evt_id, String evt_msg_code, String sms_info, String mail_info) {
        db.execute("""update EVT_MAININFO set
                        evt_msg_code = ${evt_msg_code},
                        sms_info=${sms_info},
                        mail_info=${mail_info}
                        where evt_id = ${evt_id}""")
    }


    private void notifyInfo(String eventId) {
        def row = db.firstRow("select * from EVT_MAININFO where evt_id = ${eventId}")
        def smsList = row.sms_list
        def smsInfo = row.sms_info

        boolean isTxnSucc = false
        if (row.evt_msg_code == '0000') {
            isTxnSucc = true
        }
        switch (row.notify_type) {
            case "1":  //失败时通知
                if (!isTxnSucc) {
                    notifySms(smsList, smsInfo, eventId)
                }
                break
            case "2":  //成功时通知
                if (isTxnSucc) {
                    notifySms(smsList, smsInfo, eventId)
                }
                break
            case "3":  //全部通知
                notifySms(smsList, smsInfo, eventId)
                break
            case "0":  //不通知
            default:
                break
        }
    }

    private void notifySms(String receivers, String msg, String eventId) {
        try {
            receivers.split(";").each {
                def fields = it.split(":")
                if (fields.length == 2) {
                    SmsTool.sendMessage(fields[1], msg)
                } else {
                    SmsTool.sendMessage(fields[0], msg)
                }
            }
        } catch (Exception e) {
            db.execute("update EVT_MAININFO set sms_err_msg = '与海尔短信网关连接失败.' where evt_id = ${eventId}")
            logger.error("与海尔短信网关连接失败.", e)
        }

    }


    def processBankMsg(String bankCode, String txnDate, String txnFileName) {
        def file
        def Root
        try {
            file = new File(txnFileName)
        } catch (Exception e) {
            def errmsg = "银行对账文件:${txnFileName}处理错误。"
            throw new RuntimeException(errmsg, e)
        }

        if (file.length() == 0) {
            def errmsg = "银行[${bankCode}]对账文件不存在。"
            throw new RuntimeException(errmsg)
        }

        StringBuffer sb = new StringBuffer();
        file.eachLine {
            sb.append(it)
        }

        try {
            Root = new XmlSlurper().parseText(sb.toString())
        } catch (Exception e) {
            def errmsg = "解析处理银行对账文件:${txnFileName}时出现异常。"
            throw new RuntimeException(errmsg, e)
        }

        db.withBatch(500, """insert into chk_zongfen_txn
                (pkid, txn_date, send_sys_id, actno_in, actno_out, txnamt, dc_flag, msg_sn, chksts)
                values
                (:pkid, :txn_date, :send_sys_id, :actno_in,:actno_out, :txnamt, :dc_flag, :msg_sn, :chksts) """) { ps ->
            Root.Body.Record.each() {
                BigDecimal amt = new BigDecimal(((String) it.TxAmount));
                String dcflag = it.DCFlag
                String msgsn = it.TxSeq
                //20121029 账号为空时默认为 40个‘0'
                String payact = it.PayAct
                payact = payact.trim() ?: '00000000000000000000000000000000'
                String recact = it.RecAct
                recact = recact.trim() ?: '00000000000000000000000000000000'
                if (dcflag == "C") {
                    ps.addBatch(pkid: UUID.randomUUID().toString(),
                            txn_date: txnDate,
                            send_sys_id: bankCode,
                            actno_in: recact,
                            actno_out: payact,
                            txnamt: amt,
                            dc_flag: dcflag,
                            msg_sn: msgsn.trim(),
                            chksts: "-")
                }
            }
        }
    }

    void processSBSMsg(String channelId, String bankCode, String txnDate) {
        def lines = []

        SbsHelper.processOneSbsMsg(lines, 1, channelId, bankCode, txnDate)

        db.withBatch(500, """insert into chk_zongfen_txn
                (pkid, txn_date, send_sys_id, actno_in, actno_out, txnamt, dc_flag, msg_sn, chksts)
                values
                (:pkid, :txn_date, :send_sys_id, :actno_in,:actno_out, :txnamt, :dc_flag, :msg_sn, :chksts) """) { ps ->

            lines.each {
                println it
                def fields = it.split('\\|')

                BigDecimal amt = new BigDecimal((String) fields[4].trim());
                //String txdate = it.TxDate
                //String txdate = txnDate
                String dcflag = fields[3].trim()
                String msgsn = fields[5].trim()
                String inAcctId = fields[1].trim()
                String outAcctId = fields[8].trim()
                String sendSysId = "SBS_" + bankCode
                ps.addBatch(pkid: UUID.randomUUID().toString(),
                        txn_date: txnDate,
                        send_sys_id: sendSysId,
                        actno_in: inAcctId,
                        actno_out: outAcctId,
                        txnamt: amt,
                        dc_flag: dcflag,
                        msg_sn: msgsn,
                        chksts: "-")

            }
        }
    }

    //=============================================
    private boolean isCronTaskOpen() {
        try {
            String debug_mode = PropertyManager.getProperty("cron_task_mode");
            return debug_mode != null && !"".equals(debug_mode) && "open".equals(debug_mode);
        } catch (Exception e) {
            logger.error("读取参数文件错误", e);
            return false;
        }
    }

    //===================================================
    def processBankMsg_test() {
        def file
        def Root
        try {
            file = new File(ftpmain + "/${txn_date}_313.xml")
        } catch (Exception e) {
            def errmsg = "银行对账文件:${txn_date}_313.xml不存在。"
            throw new RuntimeException(errmsg, e)
        }

        StringBuffer sb = new StringBuffer();
        file.eachLine {
            sb.append(it)
        }

        try {
            Root = new XmlSlurper().parseText(sb.toString())
        } catch (Exception e) {
            def errmsg = "解析处理银行对账文件:${txn_date}.xml时出现异常。"
            throw new RuntimeException(errmsg, e)
        }

    }

}

