package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.*;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.dao.fip.FipCommonMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.security.OperatorManager;
import skyline.service.common.ToolsService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-13
 * Time: 下午2:16
 * To change this template use File | Settings | File Templates.
 */
@Service
public class BillManagerService {
    private static final Logger logger = LoggerFactory.getLogger(BillManagerService.class);

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;

    @Autowired
    private FipRefunddetlMapper fipRefunddetlMapper;

    @Autowired
    private FipCommonMapper fipCommonMapper;

    @Autowired
    private FipJoblogMapper fipJoblogMapper;

    @Autowired
    private FipCutpaybatMapper fipCutpaybatMapper;

    @Autowired
    private ToolsService toolsService;

    @Autowired
    private JobLogService jobLogService;
    /**
     * ===============================================
     */
    /**
     * 判断记录是否可打包，防止并发
     *
     * @param cutpaydetl
     * @return
     */
    public boolean isPkgable(FipCutpaydetl cutpaydetl) {
        String txPkgSn = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid()).getTxpkgSn();
        return StringUtils.isEmpty(txPkgSn);   // 为空则可打包
    }

    // 系统ID、银联、未发送、未归档、未删除、批量包号
    public List<FipCutpaydetl> selectBillList(String bizID, String channel, String sendflag, String txPkgSn) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andOriginBizidEqualTo(bizID)
                .andBiChannelEqualTo(channel).andSendflagEqualTo(sendflag)
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0").andTxpkgSnEqualTo(txPkgSn);
        return fipCutpaydetlMapper.selectByExample(example);
    }

    /**
     * 更改帐单状态
     * @param cutpaydetls
     * @param status
     */
    @Transactional
    public void updateCutpaydetlBillStatus(List<FipCutpaydetl> cutpaydetls, BillStatus status){
        for (FipCutpaydetl cutpaydetl : cutpaydetls) {
             updateCutpaydetlBillStatus(cutpaydetl, status);
        }
    }
    @Transactional
    public void updateCutpaydetlBillStatus(FipCutpaydetl cutpaydetl, BillStatus status){
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog log = new FipJoblog();

        FipCutpaydetl  dbrecord = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid());
        Long recversion = cutpaydetl.getRecversion();

        if (dbrecord.getRecversion().equals(recversion)) {
            String oldstatus = cutpaydetl.getBillstatus();
            cutpaydetl.setBillstatus(status.getCode());
            cutpaydetl.setRecversion(++recversion);
            fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);

            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(cutpaydetl.getPkid());
            log.setJobname("更改状态");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            log.setJobdesc("更改状态 " + BillStatus.valueOfAlias(oldstatus).getTitle() +"为：" + BillStatus.valueOfAlias(cutpaydetl.getBillstatus()).getTitle());
            fipJoblogMapper.insert(log);
        }
    }
    // 批量内各记录发送状态修改
    @Transactional
    public void updateCutpaydetlListToSendflag(List<FipCutpaydetl> records, String sendflag) {
        for (FipCutpaydetl record : records) {
            record.setSendflag(sendflag);
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            checkAndUpdateCutpaydetlRecordVersion(record);
        }
    }

    // 批量包发送状态修改
    @Transactional
    public void updateCutpaybatToSendflag(String txpkgSn, String sendflag) {
        FipCutpaybat fipCutpaybat = selectFipCutpaybatByTxpkgSn(txpkgSn);
        fipCutpaybat.setSendflag(sendflag);
        fipCutpaybat.setTxpkgStatus(TxpkgStatus.QRY_PEND.getCode());
        fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
        fipCutpaybatMapper.updateByPrimaryKey(fipCutpaybat);
    }

    public FipCutpaybat selectFipCutpaybatByTxpkgSn(String txpkgSn) {
        return fipCutpaybatMapper.selectByPrimaryKey(txpkgSn);
    }

    //更新批量报文表状态 独立事务
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCutpaybatRecordStatus4NewTransactional(FipCutpaybat cutpaybat, TxpkgStatus status, String sendFlag) {
        cutpaybat.setTxpkgStatus(status.getCode());
        cutpaybat.setRecversion(cutpaybat.getRecversion() + 1);
        cutpaybat.setSendflag(sendFlag);
        fipCutpaybatMapper.updateByPrimaryKey(cutpaybat);
    }

    // 批量内各记录发送状态修改
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateCutpaydetlListStatus4NewTransactional(List<FipCutpaydetl> records, BillStatus status) {
        for (FipCutpaydetl record : records) {
            //record.setSendflag(sendflag);
            record.setBillstatus(status.getCode());
            checkAndUpdateCutpaydetlRecordVersion(record);
        }
    }


    /**
     * ===============================================
     */
    public List<FipCutpaydetl> selectBillList(BizType bizType, BillType billType) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria()
                .andOriginBizidEqualTo(bizType.getCode()).andBilltypeEqualTo(billType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0");
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    public List<FipCutpaydetl> selectBillList(BizType bizType, BillStatus status) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status.getCode());
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    public List<FipCutpaydetl> selectBillList(BizType bizType, BillType billType, BillStatus status) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria()
                .andOriginBizidEqualTo(bizType.getCode()).andBilltypeEqualTo(billType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status.getCode());
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    //代发记录清单
    public List<FipRefunddetl> selectRefundBillList(BizType bizType, BillStatus status) {
        FipRefunddetlExample example = new FipRefunddetlExample();
        example.createCriteria()
                .andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status.getCode());
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        return fipRefunddetlMapper.selectByExample(example);
    }

    /**
     * 区分 消费信贷的 新旧系统
     * @param bizType
     * @param billType
     * @param status
     * @param cmsFlag
     * @return
     */
    public List<FipCutpaydetl> selectBillList(BizType bizType, BillType billType, BillStatus status, String cmsFlag) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria()
                .andOriginBizidEqualTo(bizType.getCode()).andBilltypeEqualTo(billType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andRemark3EqualTo(cmsFlag)
                .andBillstatusEqualTo(status.getCode());
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    public List<FipCutpaydetl> selectBillList(BizType bizType, BillStatus status1, BillStatus status2) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status1.getCode());
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        List<FipCutpaydetl> fipCutpaydetlList1 = fipCutpaydetlMapper.selectByExample(example);
        example.clear();
        example.createCriteria().andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status2.getCode());
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        List<FipCutpaydetl> fipCutpaydetlList2 = fipCutpaydetlMapper.selectByExample(example);
        fipCutpaydetlList1.addAll(fipCutpaydetlList2);
        return fipCutpaydetlList1;
    }
    public List<FipCutpaydetl> selectBillList(BizType bizType, BillStatus status1, BillStatus status2, BillType billType) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status1.getCode())
                .andBilltypeEqualTo(billType.getCode());
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        List<FipCutpaydetl> fipCutpaydetlList1 = fipCutpaydetlMapper.selectByExample(example);
        example.clear();
        example.createCriteria().andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status2.getCode())
                .andBilltypeEqualTo(billType.getCode());
        example.setOrderByClause(" batch_sn desc, batch_detl_sn ");
        List<FipCutpaydetl> fipCutpaydetlList2 = fipCutpaydetlMapper.selectByExample(example);
        fipCutpaydetlList1.addAll(fipCutpaydetlList2);
        return fipCutpaydetlList1;
    }

    /**
     * 根据业务类型获取非逾期帐单清单
     * @param bizType
     * @param status1
     * @param status2
     * @return
     */
    public List<FipCutpaydetl> selectNotOverdueBillList(BizType bizType, BillStatus status1, BillStatus status2) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status1.getCode())
                .andBilltypeNotEqualTo(BillType.OVERDUE.getCode());
        List<FipCutpaydetl> fipCutpaydetlList1 = fipCutpaydetlMapper.selectByExample(example);
        example.clear();
        example.createCriteria().andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andBillstatusEqualTo(status2.getCode())
                .andBilltypeNotEqualTo(BillType.OVERDUE.getCode());
        List<FipCutpaydetl> fipCutpaydetlList2 = fipCutpaydetlMapper.selectByExample(example);
        fipCutpaydetlList1.addAll(fipCutpaydetlList2);
        return fipCutpaydetlList1;
    }

    public List<FipCutpaydetl> selectBillList(String bizID, String channel, String bankcode) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andOriginBizidEqualTo(bizID)
                .andBiChannelEqualTo(channel).andBiActopeningbankEqualTo(bankcode)
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    //本月已归档数据查询
    public List<FipCutpaydetl> selectBillListForCurrMonth(BizType bizType, boolean isArchived) {
        String yyyymm = new SimpleDateFormat("yyyyMM").format(new Date());
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andOriginBizidEqualTo(bizType.getCode())
                .andArchiveflagEqualTo(isArchived ? "1" : "0").andDeletedflagEqualTo("0")
                .andBatchSnLike(yyyymm + "%");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    public FipCutpaydetl selectBillByKey(String pkid) {
        return fipCutpaydetlMapper.selectByPrimaryKey(pkid);
    }
    public FipRefunddetl selectRefundBillByKey(String pkid) {
        return fipRefunddetlMapper.selectByPrimaryKey(pkid);
    }

    /**
     * 根据业务主键判断是否有未存档（未回写）的重复记录
     * 查询借据号、期次号、帐单类型相同、未删除的，帐单类型不为扣款失败的记录数
     * @return
     */
    public boolean checkNoRepeatedBizkeyRecords(String iouno, String poano, String billtype) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber(iouno, poano, billtype,
                BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }
    public boolean checkNoRepeatedBizkeyRecords4Ccms(String iouno, String poano, String billtype) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber4Ccms(iouno, poano, billtype,
                BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }

    //检查代付记录是否重复
    public boolean checkNoRepeatedBizkeyRecords4CcmsRefund(String iouno, String poano) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber4CcmsRefund(iouno, poano,
                BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }

    /**
     * 提前还款重复判断 (主键是贷款审批号，放在还款日字段中)
     * @param paybackdate
     * @param billtype
     * @return
     */
    public boolean checkNoRepeatedBizkeyRecords4PreCutpay(String paybackdate,  String billtype) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber4PreCutpay(paybackdate, billtype,
                BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }

    /**
     * 根据批次号更新该批次存档状态
     *
     * @param batchno
     */
    @Transactional
    public void archiveBillsByBatchno(String batchno) {
        fipCommonMapper.archiveBillsByBatchSn(batchno);
    }

    @Transactional
    public void archiveAllBillsByBizID(String bizID) {
        fipCommonMapper.archiveAllBillsByBizID(bizID);
    }

    /**
     * 批量设置存档状态
     * @param fipCutpaydetlList
     * @return
     */
    @Transactional
    public int  archiveBills(List<FipCutpaydetl> fipCutpaydetlList){
        int count = 0;
        for (FipCutpaydetl cutpaydetl : fipCutpaydetlList) {
            FipCutpaydetl record = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid());
            if (record.getRecversion().equals(cutpaydetl.getRecversion())) {
                cutpaydetl.setRecversion(cutpaydetl.getRecversion()+1);
                cutpaydetl.setArchiveflag("1");
                count += fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }
        }
        return count;
    }
    @Transactional
    public int  archiveBillsNoCheckRecvision(List<FipCutpaydetl> fipCutpaydetlList){
        int count = 0;
        for (FipCutpaydetl cutpaydetl : fipCutpaydetlList) {
            cutpaydetl.setArchiveflag("1");
            count += fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
        }
        return count;
    }
    public int  archiveRefundBills(List<FipRefunddetl> detlList){
        int count = 0;
        for (FipRefunddetl detl : detlList) {
            FipCutpaydetl record = fipCutpaydetlMapper.selectByPrimaryKey(detl.getPkid());
            if (record.getRecversion().equals(detl.getRecversion())) {
                detl.setRecversion(detl.getRecversion()+1);
                detl.setArchiveflag("1");
                fipRefunddetlMapper.updateByPrimaryKey(detl);
                count++;
            }
        }
        return count;
    }

    @Transactional
    public void deleteBillsByKey(List<FipCutpaydetl> fipCutpaydetlList) {
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        for (FipCutpaydetl fipCutpaydetl : fipCutpaydetlList) {
            if (fipCutpaydetl.getBillstatus().equals(BillStatus.INIT.getCode())) {
                //只是初始化状态的数据可以删除
                fipCutpaydetl.setDeletedflag("1");
                checkAndUpdateCutpaydetlRecordVersion(fipCutpaydetl);

                FipJoblog log = new FipJoblog();
                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(fipCutpaydetl.getPkid());
                log.setJobname("删除记录");
                log.setJobdesc("删除获取的信贷系统代扣记录");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                fipJoblogMapper.insert(log);
            }
        }
    }

    //删除代付记录
    @Transactional
    public void deleteRefundBillsByKey(List<FipRefunddetl> detlList) {
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        for (FipRefunddetl detl : detlList) {
            if (detl.getBillstatus().equals(BillStatus.INIT.getCode())) {
                //只是初始化状态的数据可以删除
                detl.setDeletedflag("1");
                checkAndUpdateRefunddetlRecordVersion(detl);

                FipJoblog log = new FipJoblog();
                log.setTablename("fip_refunddetl");
                log.setRowpkid(detl.getPkid());
                log.setJobname("删除记录");
                log.setJobdesc("删除获取的信贷系统代扣记录");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                fipJoblogMapper.insert(log);
            }
        }
    }

    //===

    /**
     * 需要发起查询交易的记录集。（已发送、交易结果不等于失败和成功两项最终值 ）
     *
     * @param channel
     * @return
     */
    public List<FipCutpaydetl> selectNeedConfirmRecords4Online(String bizID, CutpayChannel channel) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(channel.getCode())
                .andSendflagEqualTo(TxSendFlag.SENT.getCode())
                .andBillstatusNotEqualTo(BillStatus.CUTPAY_FAILED.getCode())
                .andBillstatusNotEqualTo(BillStatus.CUTPAY_SUCCESS.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizID)
                .andBiActopeningbankNotEqualTo(BankCode.ZHONGGUO.getCode());
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    public List<FipCutpaydetl> selectNeedConfirmRecords4Batch(String bizID, CutpayChannel channel) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(channel.getCode())
                .andSendflagEqualTo(TxSendFlag.SENT.getCode())
                .andBillstatusNotEqualTo(BillStatus.CUTPAY_FAILED.getCode())
                .andBillstatusNotEqualTo(BillStatus.CUTPAY_SUCCESS.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizID)
                .andBiActopeningbankEqualTo(BankCode.ZHONGGUO.getCode());
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    /**
     * 查询某发送状态的记录集
     *
     * @param channel
     * @param sendflag
     * @return
     */
    public List<FipCutpaydetl> selectRecords4Online(String bizID, CutpayChannel channel, String sendflag) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(channel.getCode()).andSendflagEqualTo(sendflag)
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0").andOriginBizidEqualTo(bizID)
                .andBiActopeningbankNotEqualTo(BankCode.ZHONGGUO.getCode());
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }


    /**
     * 查询某交易状态，某发送状态的记录集
     *
     * @param channel
     * @param billstatus
     * @param sendflag
     * @return
     */
    public List<FipCutpaydetl> selectRecords4Online(String bizID, CutpayChannel channel, String billstatus, String sendflag) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(channel.getCode()).andBillstatusEqualTo(billstatus)
                .andSendflagEqualTo(sendflag).andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizID)
                .andBiActopeningbankNotEqualTo(BankCode.ZHONGGUO.getCode());
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    /**
     *  重要  (无代扣渠道包括SBS:根据状态选择批量交易的银行的代扣记录 )
     * @param bizType
     * @param billstatus
     * @param bankCode
     * @return
     */
    public List<FipCutpaydetl> selectRecords4NoChannelBatch(BizType bizType, BillStatus billstatus, BankCode bankCode) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(CutpayChannel.NONE.getCode())
                .andBillstatusEqualTo(billstatus.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizType.getCode())
                .andBiActopeningbankEqualTo(bankCode.getCode());
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    /**
     * 重要  (银联渠道：根据状态选择实时交易的银行的代扣记录)  不处理send_flag
     * @param bizType
     * @param billstatus
     * @return
     */
    public List<FipCutpaydetl> selectRecords4UnipayOnline(BizType bizType, BillStatus billstatus) {
        //List<String> bankcodes = toolsService.selectEnuItemValue("UnipayRealTxnBank");

        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(CutpayChannel.UNIPAY.getCode())
                .andBillstatusEqualTo(billstatus.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizType.getCode());
                //.andBiActopeningbankIn(bankcodes);
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }
    //代付 20120703 zhanrui
    public List<FipRefunddetl> selectRefundRecords4UnipayOnline(BizType bizType, BillStatus billstatus) {
        FipRefunddetlExample example = new FipRefunddetlExample();
        example.createCriteria().andBiChannelEqualTo(CutpayChannel.UNIPAY.getCode())
                .andBillstatusEqualTo(billstatus.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizType.getCode());
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipRefunddetlMapper.selectByExample(example);
    }

    /**
     * 重要  (银联渠道：根据状态选择非实时交易的银行的代扣记录)
     * @param bizType
     * @param billstatus
     * @return
     */
    public List<FipCutpaydetl> selectRecords4UnipayBatch(BizType bizType, BillStatus billstatus) {
        //List<String> bankcodes = toolsService.selectEnuItemValue("UnipayRealTxnBank");

        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(CutpayChannel.UNIPAY.getCode())
                .andBillstatusEqualTo(billstatus.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizType.getCode());
                //.andBiActopeningbankNotIn(bankcodes);
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }


    public List<FipCutpaydetl> selectRecordsByTxpkgSn(String txpkgSn) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andTxpkgSnEqualTo(txpkgSn);
        example.setOrderByClause(" txpkg_detl_sn ");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    /**
     * 主要用于区分银联的实时报文和批量报文
     *
     * @param paramMap
     * @return
     */
    @Deprecated
    public List<FipCutpaydetl> selectRecordsByParamMap(Map paramMap) {
        String bizID = (String) paramMap.get("bizID");
        String channel = (String) paramMap.get("channel");
        String billstatus = (String) paramMap.get("billstatus");
        String sendflag = (String) paramMap.get("sendflag");
        String pkgtype = (String) paramMap.get("pkgtype");

        FipCutpaydetlExample example = new FipCutpaydetlExample();
        if ("online".equals(pkgtype)) {
            example.createCriteria().andBiChannelEqualTo(channel).andBillstatusEqualTo(billstatus)
                    .andSendflagEqualTo(sendflag).andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                    .andOriginBizidEqualTo(bizID).andBiActopeningbankNotEqualTo("104");
        } else {
            example.createCriteria().andBiChannelEqualTo(channel).andBillstatusEqualTo(billstatus)
                    .andSendflagEqualTo(sendflag).andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                    .andOriginBizidEqualTo(bizID).andBiActopeningbankEqualTo("104");
        }
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    /**
     * 发送批量报文前，检查明细记录的版本号，更新版本号， 组成新LIST 返回
     * @param txPkgSn
     * @return
     */
    @Transactional
    public List<FipCutpaydetl> checkToMakeSendableRecords(String txPkgSn) {
        List<FipCutpaydetl> batchRecords = selectRecordsByTxpkgSn(txPkgSn);
        List<FipCutpaydetl> sendRecords = new ArrayList<FipCutpaydetl>();
        for (FipCutpaydetl record : batchRecords) {
            if ((record.getBillstatus().equals(BillStatus.PACKED.getCode())) && isSameVersion(record)) {
                checkAndUpdateCutpaydetlRecordVersion(record);   // 更新版本号
                sendRecords.add(record);
            }
        }
        return sendRecords;
    }



    /**
     * 检查某记录是否有过更新
     *
     * @param record
     * @return
     */
    public boolean isSameVersion(FipCutpaydetl record) {
        FipCutpaydetl originRecord = fipCutpaydetlMapper.selectByPrimaryKey(record.getPkid());
        if (!originRecord.getRecversion().equals(record.getRecversion())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 更新sendflag
     *
     * @param record
     * @param sendflag
     */
    @Transactional
    public void updateCutpaydetlToSendflag(FipCutpaydetl record, String sendflag) {
        record.setSendflag(sendflag);
        record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
        fipCutpaydetlMapper.updateByPrimaryKey(record);
    }


    /**
     * 更新记录到+1版
     *
     * @param record
     * @return
     */
    @Transactional
    public FipCutpaydetl checkAndUpdateCutpaydetlRecordVersion(FipCutpaydetl record) {
        FipCutpaydetl originRecord = fipCutpaydetlMapper.selectByPrimaryKey(record.getPkid());
        if (!originRecord.getRecversion().equals(record.getRecversion())) {
            throw new RuntimeException("并发更新冲突,UUID=" + record.getPkid());
        } else {
            record.setRecversion(record.getRecversion() + 1);
            fipCutpaydetlMapper.updateByPrimaryKey(record);
            return record;
        }
    }
    @Transactional
    public FipRefunddetl checkAndUpdateRefunddetlRecordVersion(FipRefunddetl record) {
        FipRefunddetl originRecord = fipRefunddetlMapper.selectByPrimaryKey(record.getPkid());
        if (!originRecord.getRecversion().equals(record.getRecversion())) {
            throw new RuntimeException("并发更新冲突,UUID=" + record.getPkid());
        } else {
            record.setRecversion(record.getRecversion() + 1);
            fipRefunddetlMapper.updateByPrimaryKey(record);
            return record;
        }
    }

        /**
     * 根据当前日期生成批次号 每日批次最多1000次
     *
     * @return
     */
    public synchronized String generateBatchno() {
        String strdate8 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String batchno = getMaxBatchno(strdate8);

        int iSeqno = 1;
        if (!StringUtils.isEmpty(batchno)) {
            iSeqno = Integer.parseInt(batchno.substring(8, 11));
            iSeqno++;
        }
        String sSeqno = "" + iSeqno;
        sSeqno = StringUtils.leftPad(sSeqno, 3, "0");
        return strdate8 + sSeqno;
    }
    private String getMaxBatchno(String strdate8) {
        return fipCommonMapper.selectMaxBatchSnByDate(strdate8);
    }

    private String getMaxSeqno(String batchno) {
        return fipCommonMapper.selectMaxBatchDetlSnByBatchSn(batchno);
    }

        /**
     * 根据当前日期生成批次号 每日批次最多1000次    for 代付
     *
     * @return
     */
    public synchronized  String generateBatchno4Refund() {
        String strdate8 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String batchno = getMaxBatchno4Refund(strdate8);

        int iSeqno = 1;
        if (!StringUtils.isEmpty(batchno)) {
            iSeqno = Integer.parseInt(batchno.substring(8, 11));
            iSeqno++;
        }
        String sSeqno = "" + iSeqno;
        sSeqno = StringUtils.leftPad(sSeqno, 3, "0");
        return strdate8 + sSeqno;
    }
    private String getMaxBatchno4Refund(String strdate8) {
        return fipCommonMapper.selectMaxBatchSnByDate4Refund(strdate8);
    }

    private String getMaxSeqno4Refund(String batchno) {
        return fipCommonMapper.selectMaxBatchDetlSnByBatchSn4Refund(batchno);
    }


    //扣款前变更代扣渠道   zhanrui 20121024
    @Transactional
    public void chgChannel(List<FipCutpaydetl> cutpaydetlList, CutpayChannel channel) {
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        for (FipCutpaydetl detl : cutpaydetlList) {
            FipCutpaydetl record = fipCutpaydetlMapper.selectByPrimaryKey(detl.getPkid());
            if ("0".equals(record.getDeletedflag()) && "0".equals(record.getArchiveflag())
                    && detl.getRecversion().compareTo(record.getRecversion()) == 0
                    && record.getBillstatus().equals(BillStatus.INIT.getCode())) {
                record.setBiChannel(channel.getCode());
                fipCutpaydetlMapper.updateByPrimaryKey(record);
                FipJoblog log = new FipJoblog();
                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(detl.getPkid());
                log.setJobname("修改代扣渠道");
                log.setJobdesc("修改代扣渠道为：" + channel.getTitle() + " 成功。");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                fipJoblogMapper.insert(log);
            }else{
                String jobdesc = "修改代扣渠道为：" + channel.getTitle() + " 失败。";
                appendNewJoblog(detl.getPkid(), "fip_cutpaydetl", "修改代扣渠道", jobdesc);
                throw new RuntimeException(jobdesc + " 记录状态错误或并发冲突。");
            }
        }
    }

    private void appendNewJoblog(String pkid, String tblname, String jobname, String jobdesc) {
        OperatorManager om = SystemService.getOperatorManager();
        String userid = om.getOperatorId();
        String username = om.getOperatorName();

        jobLogService.insertNewJoblog(pkid, tblname, jobname, jobdesc, "数据交换平台", "数据交换平台");
    }
}
