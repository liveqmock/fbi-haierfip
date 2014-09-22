package fip.batch.actchk
import fip.common.utils.sms.SmsTool
import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pub.platform.advance.utils.PropertyManager
/**
 * �ܷ��˻����ʣ�MBP ���У�.
 * User: zhanrui
 * Date: 12-7-26
 * Time: ����5:06
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

    //WEB����ýӿ�
    public void startActChk4Web(String yyyymmdd) {
/*
        if (!isCronTaskOpen()) {
            logger.info("�Զ������������ѹرա�");
            return;
        }
*/

        this.txn_date = yyyymmdd;
        startActChk(false, "CCB")
    }
    //��ʱ������ýӿ�
    public void startActChk4Cron() {
        if (!isCronTaskOpen()) {
            logger.info("�Զ������������ѹرա�");
            return;
        }

        txn_date = (new Date() - 1).format('yyyyMMdd');
        startActChk(true, "CCB")
    }

    // isCheckSuccessFlag  true:���ж���ǰ�ȼ���ѳɹ���־ false:���ж���ǰ������ѳɹ���־
    private void startActChk(boolean isCheckSuccessFlag, bankCode) {
        //1.��ʼ������
        try {
            println "=============" + txn_date
            initDB()
        } catch (Exception e) {
            logger.error("����ʱ����ϵͳ����", e)
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
                    if (succ_flag == '1') { //�Ѳ����ɹ���������ȡ���ݺ�У�����ݣ�
                        logger.info("�ܷ��˻������ѳɹ���")
                        return
                    }
                }
//                db.execute("delete from chk_zongfen_txn where txn_date = ${txn_date} and send_sys_id in ('SBS','CCB')")
                db.execute(delSql)
            } else {
                updateOperationEventMainInfo(this.eventId, false, "��ʼ����", "��ʼ����")
                db.execute("update EVT_MAININFO set evt_msg_code= '1000' where evt_id = ${eventId}")
                //db.execute("delete from chk_zongfen_txn where txn_date = ${txn_date} and send_sys_id in ('SBS','CCB')")
            }
        } catch (Exception e) {
            logger.error("����ʱ����ϵͳ����", e)
            return
        }

        def start = System.currentTimeMillis()

        //2.��ȡ���ж�������
        try {
            processCCBMsg()
        } catch (Exception e) {
            logger.error("������ش������", e)
            def msg = "�ܷ��˻������쳣, ҵ������:${txn_date}, ������Ϣ:" + e.getMessage()
            msg = msg.size() <= 70 ? msg : msg.substring(0, 70)
            updateOperationEventMainInfo(this.eventId, false, msg, msg)
            return
        }

        //3.��ȡSBS��������
        try {
            processSBSMsg(bankCode)
        } catch (Exception sbsex) {
            logger.error("��ȡSBS�������ݴ���", sbsex)
            def msg = "�ܷ��˻������쳣, ҵ������:${txn_date}, ������Ϣ:" + sbsex.getMessage()
            msg = msg.size() <= 70 ? msg : msg.substring(0, 70)
            updateOperationEventMainInfo(this.eventId, false, msg, msg)
            return
        }

        updateOperationEventMainInfo(this.eventId, true, "��ȡ�����������.", "��ȡ�����������.")

        //4.��ʼУ��
        verifyData(bankCode)

        def end = System.currentTimeMillis()

        println "---end---" + (end - start) / 1000
//        db.close()
    }

    public void verifyData(bankCode) {
        //�鿴�¼�����״̬��
        def succ_flag = db.firstRow("select succ_flag  from EVT_MAININFO where evt_id = ${this.eventId}").succ_flag
        if (succ_flag == '0') {  //����ʧ��
            return
        }

        //VerifyDataHelper.verify(db, txn_date, "SBS", "CCB")

        def sbs_bank = "SBS_" + bankCode;
        //У����˽��
        VerifyDataHelper.verify(db, txn_date, sbs_bank, bankCode)

        def ccbCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${this.txn_date} and send_sys_id = '${bankCode}'").cnt
        def sbsCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${this.txn_date} and send_sys_id = '${sbs_bank}'").cnt
        def totalCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${this.txn_date} and send_sys_id in ('${sbs_bank}','${bankCode}')").cnt
        def totalFailCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${this.txn_date} and (chksts is null or chksts != '0') and send_sys_id in ('${sbs_bank}','${bankCode}')").cnt

        if (totalFailCount == 0) {
            def msg = "�ܷ��˻����˽��:ƽ��,ҵ������:${txn_date},CCB:${ccbCount}��,SBS:${sbsCount}��."
            updateTxnEventMainInfo(this.eventId, "0000", msg, msg)
        } else {
            def errmsg = "�ܷ��˻����˽��:��ƽ,ҵ������:${txn_date},CCB:${ccbCount}��,SBS:${sbsCount}��,��ƽ:${totalFailCount}��."
            updateTxnEventMainInfo(this.eventId, "1000", errmsg, errmsg)
        }
    }

    //=============
    //WEB����ýӿ�
    public void notifyResult4Web(String yyyymmdd) {
        if (!isCronTaskOpen()) {
            logger.info("�Զ������������ѹرա�");
            return;
        }

        this.txn_date = yyyymmdd;
        notifyResult()
    }
    //��ʱ������ýӿ�
    public void notifyResult4Cron() {
        if (!isCronTaskOpen()) {
            logger.info("�Զ������������ѹرա�");
            return;
        }

        txn_date = (new Date() - 1).format('yyyyMMdd');
        notifyResult()
    }

    private void notifyResult() {
        initDB()
        //�鿴�¼�����״̬��
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
            logger.error("���ݿ����Ӵ���", e)
            throw new RuntimeException("���ݿ����Ӵ���" + e.getMessage(), e)
        }
    }

    //���²����¼���Ϣ
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
    //����ҵ���¼���Ϣ
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
            case "1":  //ʧ��ʱ֪ͨ
                if (!isTxnSucc) {
                    notifySms(smsList, smsInfo)
                }
                break
            case "2":  //�ɹ�ʱ֪ͨ
                if (isTxnSucc) {
                    notifySms(smsList, smsInfo)
                }
                break
            case "3":  //ȫ��֪ͨ
                notifySms(smsList, smsInfo)
                break
            case "0":  //��֪ͨ
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
            db.execute("update EVT_MAININFO set sms_err_msg = '�뺣��������������ʧ��.' where evt_id = ${eventId}")
            logger.error("�뺣��������������ʧ��.", e)
        }

    }


    def processCCBMsg() {
        def file
        def Root
        try {
            file = new File(ftpmain + "/${txn_date}.xml")
        } catch (Exception e) {
            def errmsg = "���ж����ļ�:${txn_date}.xml�����ڡ�"
            throw new RuntimeException(errmsg, e)
        }

        //20130819 zr
        println("====" + file.length())
        boolean isNullFile = false;
        if (file.length() < 100) {
            file.eachLine {
                if (it.contains("MADE BY MBP")) {  //���ļ� ��ʾ���з����Ķ����ļ�Ϊ��
                    logger.info("���з����Ķ����ļ�Ϊ��.")
                    isNullFile = true
                }
            }
        }
        if (isNullFile) return;

        //20130507 zr  �����ļ��� ���ֺ��ִ���������
        StringBuffer sb = new StringBuffer();
        file.eachLine {
            if (!it.contains("AbstractStr")) {
                sb.append(it)
            }
        }

        try {
            Root = new XmlSlurper().parseText(sb.toString())
        } catch (Exception e) {
            def errmsg = "���������ж����ļ�:${txn_date}.xmlʱ�����쳣��"
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
                //20121029 �˺�Ϊ��ʱĬ��Ϊ 40����0'
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
            logger.error("��ȡ�����ļ�����", e);
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
            def errmsg = "���ж����ļ�:${txn_date}.xml�����ڡ�"
            throw new RuntimeException(errmsg, e)
        }

        //20130507 zr  �����ļ��� ���ֺ��ִ���������
        StringBuffer sb = new StringBuffer();
        file.eachLine {
            sb.append(it)
        }

        try {
            Root = new XmlSlurper().parseText(sb.toString())
        } catch (Exception e) {
            def errmsg = "���������ж����ļ�:${txn_date}.xmlʱ�����쳣��"
            throw new RuntimeException(errmsg, e)
        }

    }

}

