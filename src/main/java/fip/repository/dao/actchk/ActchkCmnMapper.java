package fip.repository.dao.actchk;

import fip.repository.model.actchk.ActchkVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-8-7
 * Time: 下午12:52
 * To change this template use File | Settings | File Templates.
 */
@Repository
public interface ActchkCmnMapper {
    //双方帐户都存在 核对 平账
    @Update("update CHK_ZONGFEN_TXN" +
            "   set chksts = '0'" +
            " where txn_date = #{txnDate}" +
            "   and msg_sn in (select t1.msg_sn" +
            "                   from (select * " +
            "                           from CHK_ZONGFEN_TXN" +
            "                          where send_sys_id = #{sendSysId1}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select * " +
            "                           from CHK_ZONGFEN_TXN" +
            "                          where send_sys_id = #{sendSysId2}" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.msg_sn = t2.msg_sn" +
            "                    and t1.actno_in = t2.actno_in" +
            "                    and t1.actno_out = t2.actno_out" +
            "                    and t1.txnamt = t2.txnamt" +
            "                    and t1.dc_flag = t2.dc_flag)")
    public int verifyChkTxnResult_0(@Param("txnDate") String txnDate,
                                    @Param("sendSysId1") String sendSysId1,
                                    @Param("sendSysId2") String sendSysId2);

    //核对我有他无
    @Update("update CHK_ZONGFEN_TXN" +
            "   set chksts = '1'" +
            " where txn_date = #{txnDate}" +
            "   and send_sys_id = #{sendSysId1}" +
            "   and msg_sn not in (select msg_sn" +
            "                       from CHK_ZONGFEN_TXN" +
            "                      where send_sys_id = #{sendSysId2}" +
            "                        and txn_date = #{txnDate})")
    public int verifyChkTxnResult_11(@Param("txnDate") String txnDate,
                                     @Param("sendSysId1") String sendSysId1,
                                     @Param("sendSysId2") String sendSysId2);

    //核对我无他有
    @Update("update CHK_ZONGFEN_TXN" +
            "   set chksts = '1'" +
            " where txn_date = #{txnDate}" +
            "   and send_sys_id = #{sendSysId2}" +
            "   and msg_sn not in (select msg_sn" +
            "                       from CHK_ZONGFEN_TXN" +
            "                      where send_sys_id = #{sendSysId1}" +
            "                        and txn_date = #{txnDate})")
    public int verifyChkTxnResult_12(@Param("txnDate") String txnDate,
                                     @Param("sendSysId1") String sendSysId1,
                                     @Param("sendSysId2") String sendSysId2);

    //双方帐户都存在 核对不平的情况
    @Update("update CHK_ZONGFEN_TXN" +
            "   set chksts = '2'" +
            " where txn_date = #{txnDate}" +
            "   and msg_sn in (select t1.msg_sn" +
            "                   from (select * " +
            "                           from CHK_ZONGFEN_TXN" +
            "                          where send_sys_id = #{sendSysId1}" +
            "                            and txn_date = #{txnDate}) t1," +
            "                        (select * " +
            "                           from CHK_ZONGFEN_TXN" +
            "                          where send_sys_id = #{sendSysId2}" +
            "                            and txn_date = #{txnDate}) t2" +
            "                  where t1.msg_sn = t2.msg_sn" +
            "                    and (t1.txnamt != t2.txnamt" +
            "                    or t1.actno_in != t2.actno_in" +
            "                    or t1.actno_out != t2.actno_out" +
            "                    or t1.dc_flag != t2.dc_flag))")
    public int verifyChkTxnResult_2(@Param("txnDate") String txnDate,
                                    @Param("sendSysId1") String sendSysId1,
                                    @Param("sendSysId2") String sendSysId2);

    //流水对帐结果查询
    public List<ActchkVO> selectChkTxnFailResult(@Param("sendSysId1") String sendSysId1,@Param("sendSysId2") String sendSysId2, @Param("txnDate") String txnDate);
    public List<ActchkVO> selectChkTxnSuccResult(@Param("sendSysId1") String sendSysId1,@Param("sendSysId2") String sendSysId2, @Param("txnDate") String txnDate);

}
