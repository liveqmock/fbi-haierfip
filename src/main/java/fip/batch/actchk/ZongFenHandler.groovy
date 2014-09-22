package fip.batch.actchk
import fip.common.utils.sms.SmsTool
import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pub.platform.advance.utils.PropertyManager
/**
 * 总分账户对帐（MBP 建行）.
 * User: zhanrui
 * Date: 12-7-26
 * Time: 下午5:06
 * To change this template use File | Settings | File Templates.
 */
class ZongFenHandler {
    private static final Logger logger = LoggerFactory.getLogger(ZongFenHandler.class)

    //String txn_date = "19991231"
    String txn_date = "20140731"
    def dbparam
    def db
    def eventId = "ZF001001"
    def ftpmain = "D:/ftpmain/mbp"

    static void main(args) {

        ZongFenHandler handler = new ZongFenHandler()
//        handler.processCCBMsg_test()
        handler.startActChk(false, "CCB")
        //handler.notifyResult()
    }

    //WEB层调用接口
    public void startActChk4Web(String yyyymmdd) {
/*
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }
*/

        this.txn_date = yyyymmdd;
        startActChk(false, "CCB")
    }
    //定时任务调用接口
    public void startActChk4Cron() {
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }

        txn_date = (new Date() - 1).format('yyyyMMdd');
        startActChk(true, "CCB")
    }

    // isCheckSuccessFlag  true:进行对账前先检查已成功标志 false:进行对账前不检查已成功标志
    private void startActChk(boolean isCheckSuccessFlag, bankCode) {
        //1.初始化环境
        try {
            println "=============" + txn_date
            initDB()
        } catch (Exception e) {
            logger.error("对账时出现系统错误！", e)
            return
        }

        def sbsCode = 'SBS_' + bankCode
        def delSql = "delete from chk_zongfen_txn where txn_date = ${txn_date} and send_sys_id in ('${bankCode}' ,'${sbsCode}')"
        logger.info(delSql)

        try {
            def db_txn_date = db.firstRow("select txn_date  from EVT_MAININFO where evt_id = ${this.eventId}").txn_date
            def succ_flag = db.firstRow("select succ_flag  from EVT_MAININFO where evt_id = ${this.eventId}").succ_flag
            if (db_txn_date == txn_date) {
                if (isCheckSuccessFlag) {
                    if (succ_flag == '1') { //已操作成功（包括获取数据和校验数据）
                        logger.info("总分账户对账已成功。")
                        return
                    }
                }
//                db.execute("delete from chk_zongfen_txn where txn_date = ${txn_date} and send_sys_id in ('SBS','CCB')")
                db.execute(delSql)
            } else {
                updateOperationEventMainInfo(this.eventId, false, "开始处理", "开始处理")
                db.execute("update EVT_MAININFO set evt_msg_code= '1000' where evt_id = ${eventId}")
                //db.execute("delete from chk_zongfen_txn where txn_date = ${txn_date} and send_sys_id in ('SBS','CCB')")
            }
        } catch (Exception e) {
            logger.error("对账时出现系统错误！", e)
            return
        }

        def start = System.currentTimeMillis()

        //2.获取建行对账数据
        try {
            processCCBMsg()
        } catch (Exception e) {
            logger.error("建行相关处理错误", e)
            def msg = "总分账户对账异常, 业务日期:${txn_date}, 错误信息:" + e.getMessage()
            msg = msg.size() <= 70 ? msg : msg.substring(0, 70)
            updateOperationEventMainInfo(this.eventId, false, msg, msg)
            return
        }

        //3.获取SBS对账数据
        try {
            processSBSMsg(bankCode)
        } catch (Exception sbsex) {
            logger.error("获取SBS对账数据错误。", sbsex)
            def msg = "总分账户对账异常, 业务日期:${txn_date}, 错误信息:" + sbsex.getMessage()
            msg = msg.size() <= 70 ? msg : msg.substring(0, 70)
            updateOperationEventMainInfo(this.eventId, false, msg, msg)
            return
        }

        updateOperationEventMainInfo(this.eventId, true, "获取对账数据完成.", "获取对账数据完成.")

        //4.开始校验
        verifyData(bankCode)

        def end = System.currentTimeMillis()

        println "---end---" + (end - start) / 1000
//        db.close()
    }

    public void verifyData(bankCode) {
        //查看事件处理状态，
        def succ_flag = db.firstRow("select succ_flag  from EVT_MAININFO where evt_id = ${this.eventId}").succ_flag
        if (succ_flag == '0') {  //操作失败
            return
        }

        //VerifyDataHelper.verify(db, txn_date, "SBS", "CCB")

        def sbs_bank = "SBS_" + bankCode;
        //校验对账结果
        VerifyDataHelper.verify(db, txn_date, sbs_bank, bankCode)

        def ccbCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${this.txn_date} and send_sys_id = '${bankCode}'").cnt
        def sbsCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${this.txn_date} and send_sys_id = '${sbs_bank}'").cnt
        def totalCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${this.txn_date} and send_sys_id in ('${sbs_bank}','${bankCode}')").cnt
        def totalFailCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${this.txn_date} and (chksts is null or chksts != '0') and send_sys_id in ('${sbs_bank}','${bankCode}')").cnt

        if (totalFailCount == 0) {
            def msg = "总分账户对账结果:平帐,业务日期:${txn_date},CCB:${ccbCount}笔,SBS:${sbsCount}笔."
            updateTxnEventMainInfo(this.eventId, "0000", msg, msg)
        } else {
            def errmsg = "总分账户对账结果:不平,业务日期:${txn_date},CCB:${ccbCount}笔,SBS:${sbsCount}笔,不平:${totalFailCount}笔."
            updateTxnEventMainInfo(this.eventId, "1000", errmsg, errmsg)
        }
    }

    //=============
    //WEB层调用接口
    public void notifyResult4Web(String yyyymmdd) {
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }

        this.txn_date = yyyymmdd;
        notifyResult()
    }
    //定时任务调用接口
    public void notifyResult4Cron() {
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }

        txn_date = (new Date() - 1).format('yyyyMMdd');
        notifyResult()
    }

    private void notifyResult() {
        initDB()
        //查看事件处理状态，
        notifyInfo()
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
    private void updateOperationEventMainInfo(String evt_id, boolean isSuccess, String sms_info, String mail_info) {
        def evt_date = new Date().format('yyyyMMdd')
        def evt_time = new Date().format('HH:mm:ss')
        def evt_succ_flag
        if (isSuccess) evt_succ_flag = "1"
        else evt_succ_flag = "0"
        db.execute("""update EVT_MAININFO set
                        evt_date = ${evt_date},
                        evt_time = ${evt_time},
                        txn_date = ${this.txn_date},
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


    private void notifyInfo() {
        def row = db.firstRow("select * from EVT_MAININFO where evt_id = ${this.eventId}")
        def smsList = row.sms_list
        def smsInfo = row.sms_info

        boolean isTxnSucc = false
        if (row.evt_msg_code == '0000') {
            isTxnSucc = true
        }
        switch (row.notify_type) {
            case "1":  //失败时通知
                if (!isTxnSucc) {
                    notifySms(smsList, smsInfo)
                }
                break
            case "2":  //成功时通知
                if (isTxnSucc) {
                    notifySms(smsList, smsInfo)
                }
                break
            case "3":  //全部通知
                notifySms(smsList, smsInfo)
                break
            case "0":  //不通知
            default:
                break
        }
    }

    private void notifySms(String receivers, String msg) {
        try {
            receivers.split(";").each {
                SmsTool.sendMessage(it, msg)
            }
        } catch (Exception e) {
            db.execute("update EVT_MAININFO set sms_err_msg = '与海尔短信网关连接失败.' where evt_id = ${eventId}")
            logger.error("与海尔短信网关连接失败.", e)
        }

    }


    def processCCBMsg() {
        def file
        def Root
        try {
            file = new File(ftpmain + "/${txn_date}.xml")
        } catch (Exception e) {
            def errmsg = "建行对账文件:${txn_date}.xml不存在。"
            throw new RuntimeException(errmsg, e)
        }

        //20130819 zr
        println("====" + file.length())
        boolean isNullFile = false;
        if (file.length() < 100) {
            file.eachLine {
                if (it.contains("MADE BY MBP")) {  //空文件 表示建行发来的对账文件为空
                    logger.info("建行发来的对账文件为空.")
                    isNullFile = true
                }
            }
        }
        if (isNullFile) return;

        //20130507 zr  建行文件中 部分汉字处理有问题
        StringBuffer sb = new StringBuffer();
        file.eachLine {
            if (!it.contains("AbstractStr")) {
                sb.append(it)
            }
        }

        try {
            Root = new XmlSlurper().parseText(sb.toString())
        } catch (Exception e) {
            def errmsg = "解析处理建行对账文件:${txn_date}.xml时出现异常。"
            throw new RuntimeException(errmsg, e)
        }

        db.withBatch(500, """insert into chk_zongfen_txn
                (pkid, txn_date, send_sys_id, actno_in, actno_out, txnamt, dc_flag, msg_sn, chksts)
                values
                (:pkid, :txn_date, :send_sys_id, :actno_in,:actno_out, :txnamt, :dc_flag, :msg_sn, :chksts) """) { ps ->
            Root.Body.Record.each() {
                BigDecimal amt = new BigDecimal(((String) it.TxAmount));
                //String txdate = it.TxDate
                String txdate = this.txn_date
                String dcflag = it.DCFlag
                String msgsn = it.BankVoucherId
                //20121029 账号为空时默认为 40个‘0'
                String outAcctId = it.OutAcctId
                outAcctId = outAcctId.trim() ?: '00000000000000000000000000000000'
                String memo = it.Memo
                memo = memo.trim() ?: '00000000000000000000000000000000'
                if (dcflag == "C") {
                    ps.addBatch(pkid: UUID.randomUUID().toString(),
                            txn_date: txdate,
                            send_sys_id: "CCB",
                            actno_in: memo,
                            actno_out: outAcctId,
                            txnamt: amt,
                            dc_flag: dcflag,
                            msg_sn: msgsn.trim(),
                            chksts: "-")
                }
            }
        }
    }

    void processSBSMsg(bankCode) {
        def lines = []

        //processOneSbsMsg(lines, 1)
        SbsHelper.processOneSbsMsg(lines, 1, "MBP1", "   ", this.txn_date)

        db.withBatch(500, """insert into chk_zongfen_txn
                (pkid, txn_date, send_sys_id, actno_in, actno_out, txnamt, dc_flag, msg_sn, chksts)
                values
                (:pkid, :txn_date, :send_sys_id, :actno_in,:actno_out, :txnamt, :dc_flag, :msg_sn, :chksts) """) { ps ->

            lines.each {
                println it
                def fields = it.split('\\|')

                BigDecimal amt = new BigDecimal((String) fields[4].trim());
                //String txdate = it.TxDate
                String txdate = this.txn_date
                String dcflag = fields[3].trim()
                String msgsn = fields[5].trim()
                String inAcctId = fields[1].trim()
                String outAcctId = fields[8].trim()
                ps.addBatch(pkid: UUID.randomUUID().toString(),
                        txn_date: txdate,
                        send_sys_id: "SBS_" + bankCode,
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
    def processCCBMsg_test() {
        def file
        def Root
        try {
            file = new File(ftpmain + "/${txn_date}.xml")
        } catch (Exception e) {
            def errmsg = "建行对账文件:${txn_date}.xml不存在。"
            throw new RuntimeException(errmsg, e)
        }

        //20130507 zr  建行文件中 部分汉字处理有问题
        StringBuffer sb = new StringBuffer();
        file.eachLine {
            sb.append(it)
        }

        try {
            Root = new XmlSlurper().parseText(sb.toString())
        } catch (Exception e) {
            def errmsg = "解析处理建行对账文件:${txn_date}.xml时出现异常。"
            throw new RuntimeException(errmsg, e)
        }

    }

}

