package fip.batch.actchk

import org.slf4j.Logger
import org.slf4j.LoggerFactory
/**
 * Created by zhanrui on 2014/8/4.
 */
class VerifyDataHelper {
    private static final Logger logger = LoggerFactory.getLogger(VerifyDataHelper.class)

    //校验对账数据，设置标志
    static void verify(db, txnDate, sendSysId1, sendSysId2) {
        //双方帐户都存在 核对 平账
        def sql = """
                  update CHK_ZONGFEN_TXN
                    set chksts = '0'
                  where txn_date = ${txnDate}
                    and msg_sn in (select t1.msg_sn
                                    from (select *
                                            from CHK_ZONGFEN_TXN
                                           where send_sys_id = ${sendSysId1}
                                             and txn_date = ${txnDate}) t1,
                                         (select *
                                            from CHK_ZONGFEN_TXN
                                           where send_sys_id = ${sendSysId2}
                                             and txn_date = ${txnDate}) t2
                                   where t1.msg_sn = t2.msg_sn
                                     and t1.actno_in = t2.actno_in
                                     and t1.actno_out = t2.actno_out
                                     and t1.txnamt = t2.txnamt
                                     and t1.dc_flag = t2.dc_flag)

        """
        db.execute(sql)

        //核对我有他无
        sql = """
              update CHK_ZONGFEN_TXN
                set chksts = '1'
              where txn_date = ${txnDate}
                and send_sys_id = ${sendSysId1}
                and msg_sn not in (select msg_sn
                                    from CHK_ZONGFEN_TXN
                                   where send_sys_id = ${sendSysId2}
                                     and txn_date = ${txnDate})
               """
        db.execute(sql)

        //核对我无他有
        sql = """
              update CHK_ZONGFEN_TXN
                set chksts = '1'
              where txn_date = ${txnDate}
                and send_sys_id = ${sendSysId2}
                and msg_sn not in (select msg_sn
                                    from CHK_ZONGFEN_TXN
                                   where send_sys_id = ${sendSysId1}
                                     and txn_date = ${txnDate})
               """
        db.execute(sql)
        //核对我无他有
        sql = """
              update CHK_ZONGFEN_TXN
                set chksts = '2'
              where txn_date = ${txnDate}
                and msg_sn in (select t1.msg_sn
                                from (select *
                                        from CHK_ZONGFEN_TXN
                                       where send_sys_id = ${sendSysId1}
                                         and txn_date = ${txnDate}) t1,
                                     (select *
                                        from CHK_ZONGFEN_TXN
                                       where send_sys_id = ${sendSysId2}
                                         and txn_date = ${txnDate}) t2
                               where t1.msg_sn = t2.msg_sn
                                 and (t1.txnamt != t2.txnamt
                                 or t1.actno_in != t2.actno_in
                                 or t1.actno_out != t2.actno_out
                                 or t1.dc_flag != t2.dc_flag))
               """
        db.execute(sql)

    }
}
