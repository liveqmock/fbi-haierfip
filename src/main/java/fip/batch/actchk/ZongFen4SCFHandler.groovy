package fip.batch.actchk
import fip.common.utils.sms.SmsTool
import groovy.sql.Sql
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import pub.platform.advance.utils.PropertyManager
/**
 * �ܷ��˻����� ����Ӧ������ƽ̨SCF��.
 * User: zhanrui
 * Date: 12-7-26
 * Time: ����5:06
 * To change this template use File | Settings | File Templates.
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

    //WEB����ýӿ�
    public void startActChk4Web(String yyyymmdd) {
        if (!isCronTaskOpen()) {
            logger.info("�Զ������������ѹرա�");
            return;
        }

        this.txn_date = yyyymmdd;
        startActChk(false)
    }
    //��ʱ������ýӿ�
    public void startActChk4Cron() {
        if (!isCronTaskOpen()) {
            logger.info("�Զ������������ѹرա�");
            return;
        }

        def txnDate = (new Date() - 1).format('yyyyMMdd');

        //ƽ�� 313
        startActChk(true, "SCF1", "313", txnDate, "ZF002001", "scf")
    }

    // isCheckSuccessFlag  true:���ж���ǰ�ȼ���ѳɹ���־ false:���ж���ǰ������ѳɹ���־
    private void startActChk(boolean isCheckSuccessFlag, String channelId, String bankCode, String txnDate, String eventId, String ftpUserName) {
        //1.��ʼ������
        try {
            println "=============" + txnDate
            initDB()
        } catch (Exception e) {
            logger.error("����ʱ����ϵͳ����", e)
            return
        }

        try {
            def db_txn_date = db.firstRow("select txn_date  from EVT_MAININFO where evt_id = ${eventId}").txn_date
            def succ_flag = db.firstRow("select succ_flag  from EVT_MAININFO where evt_id = ${eventId}").succ_flag

            def sbsCode = 'SBS_' + bankCode
            def delSql = "delete from chk_zongfen_txn where txn_date = ${txnDate} and send_sys_id in ('${bankCode}' ,'${sbsCode}')"
            logger.info(delSql)
            if (db_txn_date == txnDate) {
                if (isCheckSuccessFlag) {
                    if (succ_flag == '1') { //�Ѳ����ɹ���������ȡ���ݺ�У�����ݣ�
                        logger.info("�ܷ��˻������ѳɹ���")
                        return
                    }
                }
                db.execute(delSql)
            } else {
                updateOperationEventMainInfo(eventId, txnDate, false, "��ʼ����", "��ʼ����")
                db.execute("update EVT_MAININFO set evt_msg_code= '1000' where evt_id = ${eventId}")
                db.execute(delSql)
            }
        } catch (Exception e) {
            logger.error("����ʱ����ϵͳ����", e)
            return
        }

        def start = System.currentTimeMillis()

        //2.��ȡ���ж�������
        def   txnFileName = ""
        try {
            if ('105' == bankCode) {
                txnFileName = "${ftpmain}/${ftpUserName}/${txnDate}.xml"
            } else {
                txnFileName = "${ftpmain}/${ftpUserName}/${txnDate}_${bankCode}.xml"
            }
            processBankMsg(bankCode, txnDate, txnFileName)
        } catch (Exception e) {
            logger.error("������ش������", e)
            def msg = "�ܷ��˻������쳣, ҵ������:${txnDate}, ������Ϣ:" + e.getMessage()
            msg = msg.size() <= 70 ? msg : msg.substring(0, 70)
            updateOperationEventMainInfo(eventId,  txnDate, false, msg, msg)
            return
        }

        //3.��ȡSBS��������
        try {
            processSBSMsg(channelId, bankCode, txnDate)
        } catch (Exception sbsex) {
            logger.error("��ȡSBS�������ݴ���", sbsex)
            def msg = "�ܷ��˻������쳣, ҵ������:${txnDate}, ������Ϣ:" + sbsex.getMessage()
            msg = msg.size() <= 70 ? msg : msg.substring(0, 70)
            updateOperationEventMainInfo(eventId,  txnDate, false, msg, msg)
            return
        }

        updateOperationEventMainInfo(eventId,  txnDate, true, "��ȡ�����������.", "��ȡ�����������.")

        //4.��ʼУ��
        verifyData()

        def end = System.currentTimeMillis()

        println "---end---" + (end - start) / 1000
//        db.close()
    }

    public void verifyData(String bankCode, String txnDate, String eventId) {
        //�鿴�¼�����״̬��
        def succ_flag = db.firstRow("select succ_flag  from EVT_MAININFO where evt_id = ${eventId}").succ_flag
        if (succ_flag == '0') {  //����ʧ��
            return
        }

        def sbs_bank = "SBS_" + bankCode;

        //У����˽��
        VerifyDataHelper.verify(db, txnDate, sbs_bank, bankCode)

        def bankCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${txnDate} and send_sys_id = '${bankCode}'").cnt
        def sbsCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${txnDate} and send_sys_id = '${sbs_bank}'").cnt
        def totalCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${txnDate}").cnt
        def totalFailCount = db.firstRow("select count(*) as cnt from CHK_ZONGFEN_TXN where txn_date = ${txnDate} and (chksts is null or chksts != '0')").cnt

        if (totalFailCount == 0) {
            def msg = "����[${bankCode}]�ܷ��˻����˽��:ƽ��, ҵ������:${txnDate}, ${bankCode}:${bankCount}��,SBS:${sbsCount}��."
            updateTxnEventMainInfo(eventId, "0000", msg, msg)
        } else {
            def errmsg = "����[${bankCode}]�ܷ��˻����˽��:��ƽ, ҵ������:${txnDate}, ${bankCode}:${bankCount}��,SBS:${sbsCount}��, ��ƽ����:${totalFailCount}��."
            updateTxnEventMainInfo(eventId, "1000", errmsg, errmsg)
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
    private void updateOperationEventMainInfo(String txnDate, String evt_id, boolean isSuccess, String sms_info, String mail_info) {
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


    def processBankMsg(String bankCode, String txnDate, String txnFileName) {
        def file
        def Root
        try {
            file = new File(txnFileName)
        } catch (Exception e) {
            def errmsg = "���ж����ļ�:${txnFileName}�����ڡ�"
            throw new RuntimeException(errmsg, e)
        }

        //20130819 zr
        println("====" + file.length())
        boolean isNullFile = false;

        //TODO
        if (file.length() < 100) {
            file.eachLine {
                if (it.contains("MADE BY MBP")) {  //���ļ� ��ʾ���з����Ķ����ļ�Ϊ��
                    logger.info("���з����Ķ����ļ�Ϊ��.")
                    isNullFile = true
                }
            }
        }
        if (isNullFile) return;

        StringBuffer sb = new StringBuffer();
        file.eachLine {
            sb.append(it)
        }

        try {
            Root = new XmlSlurper().parseText(sb.toString())
        } catch (Exception e) {
            def errmsg = "�����������ж����ļ�:${txnFileName}ʱ�����쳣��"
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
                //20121029 �˺�Ϊ��ʱĬ��Ϊ 40����0'
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
                ps.addBatch(pkid: UUID.randomUUID().toString(),
                        txn_date: txnDate,
                        send_sys_id: "SBS_${bankCode}",
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
    def processBankMsg_test() {
        def file
        def Root
        try {
            file = new File(ftpmain + "/${txn_date}_313.xml")
        } catch (Exception e) {
            def errmsg = "���ж����ļ�:${txn_date}_313.xml�����ڡ�"
            throw new RuntimeException(errmsg, e)
        }

        StringBuffer sb = new StringBuffer();
        file.eachLine {
            sb.append(it)
        }

        try {
            Root = new XmlSlurper().parseText(sb.toString())
        } catch (Exception e) {
            def errmsg = "�����������ж����ļ�:${txn_date}.xmlʱ�����쳣��"
            throw new RuntimeException(errmsg, e)
        }

    }

}

