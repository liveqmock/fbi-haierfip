package fip.repository.dao.fip;

import fip.repository.model.FipCutpaydetl;
import fip.repository.model.Xfapp;
import hfc.parambean.AppQryParam;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface FipCommonMapper {

    //代扣-----------------------
    @Select("select max(batch_sn) from fip_cutpaydetl where substr(batch_sn,1,8) = #{strdate8}")
    String selectMaxBatchSnByDate(@Param("strdate8") String strdate8);

    @Select("select max(batch_detl_sn) from fip_cutpaydetl where batch_sn = #{batch_sn}")
    String selectMaxBatchDetlSnByBatchSn(@Param("batch_sn") String batch_sn);

    @Update("update fip_cutpaydetl set archiveflag='1' where batch_sn = #{batch_sn}")
    int archiveBillsByBatchSn(@Param("batch_sn") String batch_sn);
    //---------------------------


    //代付(退款处理)-------------
    @Select("select max(batch_sn) from fip_refunddetl where substr(batch_sn,1,8) = #{strdate8}")
    String selectMaxBatchSnByDate4Refund(@Param("strdate8") String strdate8);

    @Select("select max(batch_detl_sn) from fip_refunddetl where batch_sn = #{batch_sn}")
    String selectMaxBatchDetlSnByBatchSn4Refund(@Param("batch_sn") String batch_sn);

    @Update("update fip_refunddetl set archiveflag='1' where batch_sn = #{batch_sn}")
    int archiveBillsByBatchSn4Refund(@Param("batch_sn") String batch_sn);
    //---------------------------

    /**
     * 批量设置帐单状态
     *
     * @param bizID
     * @return
     */
    @Update("update fip_cutpaydetl set archiveflag='1' where origin_bizid = #{bizID} and archiveflag = '0' and deletedflag = '0'")
    int archiveAllBillsByBizID(@Param("bizID") String bizID);

    @Update("update fip_cutpaydetl set deletedflag='1' where pkid = #{pkid}")
    int deleteBillsByKey(@Param("pkid") String pkid);

    // TODO 应改为example.or....
    @Select("select PKID, BATCH_SN as batchSn , BATCH_DETL_SN as batchDetlSn, " +
            "      TXPKG_SN as txpkgSn, TXPKG_DETL_SN txpkgDetl, APPNO, " +
            "      IOUNO, POANO, CONTRACTNO, " +
            "      CLIENTNAME, CLIENTACT, PAYBACKAMT, " +
            "      PAYBACKDATE, RECVACT, RECVBANKID, " +
            "      RECVBANKNO, STARTDATE, BILLSTATUS, " +
            "      PAIDUPAMT, PAIDUPDATE, PRINCIPALAMT, " +
            "      INTERESTAMT, PUNITIVEINTAMT, RESERVEAMT, " +
            "      BILLTYPE, CLIENTNO, CLIENTIDTYPE, " +
            "      CLIENTID, BI_ACTOPENINGBANK as BIACTOPENINGBANK, BI_BANKACTNO as BIBANKACTNO, " +
            "      BI_ACTOPENINGBANK_UD as BIACTOPENINGBANKUD, BI_BANKACTNAME as BIBANKACTNAME, " +
            "      BI_CUSTOMER_CODE as BICUSTOMERCODE, bi_province as biProvince, bi_city as biCity," +
            "      BI_SIGN_ACCOUNT_NO as BISIGNACCOUNTNO, BI_CHANNEL as BICHANNEL, TX_RETCODE as TXRETCODE, " +
            "      TX_RETMSG as TXRETMSG, ORIGIN_BIZID as ORIGINBIZID, SENDFLAG, " +
            "      ARCHIVEFLAG, DELETEDFLAG, RECVERSION, XFAPP_PKID as XFAPPPKID" +
            " from fip_cutpaydetl where origin_bizid = #{bizID} and bi_channel = #{channel} and sendflag = #{sendflag} " +
            " and  (txpkg_sn is  null or trim(txpkg_sn) = '')   order by batch_sn,batch_detl_sn")
    List<FipCutpaydetl> selectPkgableRecords(@Param("bizID") String bizID, @Param("channel") String channel,
                                             @Param("sendflag") String sendflag);


    /**
     * 判断帐单是否重复
     *
     * @return
     */
    @Select("select count(*) from fip_cutpaydetl " +
            "where iouno=#{iouno} and poano=#{poano} and billtype=#{billtype}" +
            " and billstatus != #{billstatus} " +
            " and deletedflag='0' ")
    int countRepeatedBizkeyRecordsNumber(@Param("iouno") String iouno
            , @Param("poano") String poano
            , @Param("billtype") String billtype
            , @Param("billstatus") String billstatus);

    //检查代扣记录重复 新消费信贷 以及消费金融
    @Select("select count(*) from fip_cutpaydetl " +
            "where substr(iouno, 1, 20)=#{iouno} and poano=#{poano} and billtype=#{billtype}" +
            " and billstatus != #{billstatus} " +
            " and origin_bizid = #{bizType} " +
            " and deletedflag='0' ")
    int countRepeatedBizkeyRecordsNumber4Ccms(
            @Param("iouno") String iouno
            , @Param("poano") String poano
            , @Param("billtype") String billtype
            , @Param("billstatus") String billstatus
            , @Param("bizType") String bizType
            );


    //检查代扣记录重复 HCCB
    @Select("select count(*) from fip_cutpaydetl " +
            "where iouno=#{iouno} and poano=#{poano} " +
            " and billstatus != #{billstatus} " +
            " and deletedflag='0' ")
    int countRepeatedBizkeyRecordsNumber4Hccb(@Param("iouno") String iouno
            , @Param("poano") String poano
            , @Param("billstatus") String billstatus);


    //检查代付记录重复
    @Select("select count(*) from fip_refunddetl " +
            "where substr(iouno,1,20)=#{iouno} and poano=#{poano}" +
            " and billstatus != #{billstatus} " +
            " and deletedflag='0' ")
    int countRepeatedBizkeyRecordsNumber4CcmsRefund(@Param("iouno") String iouno
            , @Param("poano") String poano
            , @Param("billstatus") String billstatus);

    /**
     * 判断提前还款帐单是否重复  (主键是贷款审批号，放在还款日字段中)
     *
     * @return
     */
    @Select("select count(*) from fip_cutpaydetl " +
            "where paybackdate=#{paybackdate} and billtype=#{billtype}" +
            " and billstatus != #{billstatus} " +
            " and deletedflag='0' ")
    int countRepeatedBizkeyRecordsNumber4PreCutpay(@Param("paybackdate") String paybackdate
            , @Param("billtype") String billtype
            , @Param("billstatus") String billstatus);

    @Select("select max(txpkg_sn) from fip_cutpaybat where substr(txpkg_sn,1,8) = #{strdate8}")
    String selectMaxTxPkgSnByDate(@Param("strdate8") String strdate8);

    List<Xfapp> selectAppByCondition(AppQryParam bean);

    /**
     * 查询已扣款成功的记录 for 退款处理  2011-12-12
     *
     * @param billstatus
     * @param startdate
     * @param enddate
     * @param clientname
     * @param iouno
     * @param poano
     * @return
     */
    List<FipCutpaydetl> selectCutpaysByCondition(@Param("bizid") String bizid,
                                                 @Param("billstatus") String billstatus,
                                                 @Param("startdate") String startdate,
                                                 @Param("enddate") String enddate,
                                                 @Param("clientname") String clientname,
                                                 @Param("iouno") String iouno,
                                                 @Param("poano") String poano);

}