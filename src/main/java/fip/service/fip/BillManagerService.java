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
 * Time: ����2:16
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
     * �жϼ�¼�Ƿ�ɴ������ֹ����
     *
     * @param cutpaydetl
     * @return
     */
    public boolean isPkgable(FipCutpaydetl cutpaydetl) {
        String txPkgSn = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid()).getTxpkgSn();
        return StringUtils.isEmpty(txPkgSn);   // Ϊ����ɴ��
    }

    // ϵͳID��������δ���͡�δ�鵵��δɾ������������
    public List<FipCutpaydetl> selectBillList(String bizID, String channel, String sendflag, String txPkgSn) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andOriginBizidEqualTo(bizID)
                .andBiChannelEqualTo(channel).andSendflagEqualTo(sendflag)
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0").andTxpkgSnEqualTo(txPkgSn);
        return fipCutpaydetlMapper.selectByExample(example);
    }

    /**
     * �����ʵ�״̬
     * @param cutpaydetls
     * @param status
     */
    @Transactional
    public synchronized void updateCutpaydetlBillStatus(List<FipCutpaydetl> cutpaydetls, BillStatus status){
        for (FipCutpaydetl cutpaydetl : cutpaydetls) {
             updateCutpaydetlBillStatus(cutpaydetl, status);
        }
    }
    @Transactional
    public synchronized void updateCutpaydetlBillStatus(FipCutpaydetl cutpaydetl, BillStatus status){
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog log = new FipJoblog();

        FipCutpaydetl  dbrecord = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid());
        Long recversion = cutpaydetl.getRecversion();

        if (dbrecord.getRecversion().compareTo(recversion) == 0) {
            String oldstatus = cutpaydetl.getBillstatus();
            cutpaydetl.setBillstatus(status.getCode());
            cutpaydetl.setRecversion(++recversion);
            fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);

            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(cutpaydetl.getPkid());
            log.setJobname("����״̬");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            log.setJobdesc("����״̬ " + BillStatus.valueOfAlias(oldstatus).getTitle() +"Ϊ��" + BillStatus.valueOfAlias(cutpaydetl.getBillstatus()).getTitle());
            fipJoblogMapper.insert(log);
        }
    }
    // �����ڸ���¼����״̬�޸�
    @Transactional
    public synchronized void updateCutpaydetlListToSendflag(List<FipCutpaydetl> records, String sendflag) {
        for (FipCutpaydetl record : records) {
            record.setSendflag(sendflag);
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            checkAndUpdateCutpaydetlRecordVersion(record);
        }
    }

    // ����������״̬�޸�
    @Transactional
    public synchronized void updateCutpaybatToSendflag(String txpkgSn, String sendflag) {
        FipCutpaybat fipCutpaybat = selectFipCutpaybatByTxpkgSn(txpkgSn);
        fipCutpaybat.setSendflag(sendflag);
        fipCutpaybat.setTxpkgStatus(TxpkgStatus.QRY_PEND.getCode());
        fipCutpaybat.setRecversion(fipCutpaybat.getRecversion() + 1);
        fipCutpaybatMapper.updateByPrimaryKey(fipCutpaybat);
    }

    public FipCutpaybat selectFipCutpaybatByTxpkgSn(String txpkgSn) {
        return fipCutpaybatMapper.selectByPrimaryKey(txpkgSn);
    }

    //�����������ı�״̬ ��������
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void updateCutpaybatRecordStatus4NewTransactional(FipCutpaybat cutpaybat, TxpkgStatus status, String sendFlag) {
        cutpaybat.setTxpkgStatus(status.getCode());
        cutpaybat.setRecversion(cutpaybat.getRecversion() + 1);
        cutpaybat.setSendflag(sendFlag);
        fipCutpaybatMapper.updateByPrimaryKey(cutpaybat);
    }

    // �����ڸ���¼����״̬�޸�
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void updateCutpaydetlListStatus4NewTransactional(List<FipCutpaydetl> records, BillStatus status) {
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

    //������¼�嵥
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
     * ���� �����Ŵ��� �¾�ϵͳ
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
     * ����ҵ�����ͻ�ȡ�������ʵ��嵥
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

    //�����ѹ鵵���ݲ�ѯ
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
     * ����ҵ�������ж��Ƿ���δ�浵��δ��д�����ظ���¼
     * ��ѯ��ݺš��ڴκš��ʵ�������ͬ��δɾ���ģ��ʵ����Ͳ�Ϊ�ۿ�ʧ�ܵļ�¼��
     * @return
     */
    public synchronized boolean checkNoRepeatedBizkeyRecords(String iouno, String poano, String billtype) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber(iouno, poano, billtype,
                BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }
    public synchronized boolean checkNoRepeatedBizkeyRecords4Ccms(String iouno, String poano, String billtype) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber4Ccms(iouno, poano, billtype,
                BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }
    public synchronized boolean checkNoRepeatedBizkeyRecords4Hccb(String iouno, String poano) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber4Hccb(iouno, poano, BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }

    //��������¼�Ƿ��ظ�
    public synchronized boolean checkNoRepeatedBizkeyRecords4CcmsRefund(String iouno, String poano) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber4CcmsRefund(iouno, poano,
                BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }

    /**
     * ��ǰ�����ظ��ж� (�����Ǵ��������ţ����ڻ������ֶ���)
     * @param paybackdate
     * @param billtype
     * @return
     */
    public synchronized boolean checkNoRepeatedBizkeyRecords4PreCutpay(String paybackdate,  String billtype) {
        int count = fipCommonMapper.countRepeatedBizkeyRecordsNumber4PreCutpay(paybackdate, billtype,
                BillStatus.CUTPAY_FAILED.getCode());
        if (count > 0) {
            return false;
        }
        return true;
    }

    /**
     * �������κŸ��¸����δ浵״̬
     *
     * @param batchno
     */
    @Transactional
    public synchronized void archiveBillsByBatchno(String batchno) {
        fipCommonMapper.archiveBillsByBatchSn(batchno);
    }

    @Transactional
    public synchronized void archiveAllBillsByBizID(String bizID) {
        fipCommonMapper.archiveAllBillsByBizID(bizID);
    }

    /**
     * �������ô浵״̬
     * @param fipCutpaydetlList
     * @return
     */
    @Transactional
    public synchronized int  archiveBills(List<FipCutpaydetl> fipCutpaydetlList){
        int count = 0;
        for (FipCutpaydetl cutpaydetl : fipCutpaydetlList) {
            FipCutpaydetl record = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid());
            if (record.getRecversion().compareTo(cutpaydetl.getRecversion()) == 0) {
                cutpaydetl.setRecversion(cutpaydetl.getRecversion()+1);
                cutpaydetl.setArchiveflag("1");
                count += fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }
        }
        return count;
    }
    @Transactional
    public synchronized int  archiveBillsNoCheckRecvision(List<FipCutpaydetl> fipCutpaydetlList){
        int count = 0;
        for (FipCutpaydetl cutpaydetl : fipCutpaydetlList) {
            cutpaydetl.setArchiveflag("1");
            count += fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
        }
        return count;
    }
    public synchronized int  archiveRefundBills(List<FipRefunddetl> detlList){
        int count = 0;
        for (FipRefunddetl detl : detlList) {
            FipCutpaydetl record = fipCutpaydetlMapper.selectByPrimaryKey(detl.getPkid());
            if (record.getRecversion().compareTo(detl.getRecversion()) == 0) {
                detl.setRecversion(detl.getRecversion()+1);
                detl.setArchiveflag("1");
                fipRefunddetlMapper.updateByPrimaryKey(detl);
                count++;
            }
        }
        return count;
    }

    @Transactional
    public synchronized void deleteBillsByKey(List<FipCutpaydetl> fipCutpaydetlList) {
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        for (FipCutpaydetl fipCutpaydetl : fipCutpaydetlList) {
            if (fipCutpaydetl.getBillstatus().compareTo(BillStatus.INIT.getCode()) == 0) {
                //ֻ�ǳ�ʼ��״̬�����ݿ���ɾ��
                fipCutpaydetl.setDeletedflag("1");
                checkAndUpdateCutpaydetlRecordVersion(fipCutpaydetl);

                FipJoblog log = new FipJoblog();
                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(fipCutpaydetl.getPkid());
                log.setJobname("ɾ����¼");
                log.setJobdesc("ɾ����ȡ���Ŵ�ϵͳ���ۼ�¼");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                fipJoblogMapper.insert(log);
            }
        }
    }

    //ɾ��������¼
    @Transactional
    public synchronized void deleteRefundBillsByKey(List<FipRefunddetl> detlList) {
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        for (FipRefunddetl detl : detlList) {
            if (detl.getBillstatus().compareTo(BillStatus.INIT.getCode()) == 0) {
                //ֻ�ǳ�ʼ��״̬�����ݿ���ɾ��
                detl.setDeletedflag("1");
                checkAndUpdateRefunddetlRecordVersion(detl);

                FipJoblog log = new FipJoblog();
                log.setTablename("fip_refunddetl");
                log.setRowpkid(detl.getPkid());
                log.setJobname("ɾ����¼");
                log.setJobdesc("ɾ����ȡ���Ŵ�ϵͳ���ۼ�¼");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                fipJoblogMapper.insert(log);
            }
        }
    }

    //===

    /**
     * ��Ҫ�����ѯ���׵ļ�¼�������ѷ��͡����׽��������ʧ�ܺͳɹ���������ֵ ��
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
     * ��ѯĳ����״̬�ļ�¼��
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
     * ��ѯĳ����״̬��ĳ����״̬�ļ�¼��
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
     *  ��Ҫ  (�޴�����������SBS:����״̬ѡ���������׵����еĴ��ۼ�¼ )
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
     * ��Ҫ  (��������������״̬ѡ��ʵʱ���׵����еĴ��ۼ�¼)  ������send_flag
     */
    public List<FipCutpaydetl> selectRecords4UnipayOnline(BizType bizType, BillStatus billstatus) {
        //List<String> bankcodes = toolsService.selectEnuItemValue("UnipayRealTxnBank");

        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(CutpayChannel.UNIPAY.getCode())
                .andBillstatusEqualTo(billstatus.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                //.andTxpkgSnIsNull() //�����������е���ϸ��¼����
                .andSendflagEqualTo("0") //�����������е���ϸ��¼����
                .andOriginBizidEqualTo(bizType.getCode());
                //.andBiActopeningbankIn(bankcodes);
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }

    /**
     * ��Ҫ  (��������������״̬ѡ���첽���ף������������еĴ�����ϸ��¼)  ������send_flag
     */
    public List<FipCutpaydetl> selectRecords4UnipayBatchDetail(BizType bizType, BillStatus billstatus) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBiChannelEqualTo(CutpayChannel.UNIPAY.getCode())
                .andBillstatusEqualTo(billstatus.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                //.andTxpkgSnIsNotNull() //���������е���ϸ��¼����
                .andSendflagEqualTo("1") //���������е���ϸ��¼����
                .andOriginBizidEqualTo(bizType.getCode());
        example.setOrderByClause("batch_sn,batch_detl_sn");
        return fipCutpaydetlMapper.selectByExample(example);
    }
    //���� 20120703 zhanrui
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
     * ��Ҫ  (��������������״̬ѡ���ʵʱ���׵����еĴ��ۼ�¼)
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
     * ��Ҫ��������������ʵʱ���ĺ���������
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
     * ������������ǰ�������ϸ��¼�İ汾�ţ����°汾�ţ� �����LIST ����
     * @param txPkgSn
     * @return
     */
    @Transactional
    public synchronized List<FipCutpaydetl> checkToMakeSendableRecords(String txPkgSn) {
        List<FipCutpaydetl> batchRecords = selectRecordsByTxpkgSn(txPkgSn);
        List<FipCutpaydetl> sendRecords = new ArrayList<FipCutpaydetl>();
        for (FipCutpaydetl record : batchRecords) {
            if ((record.getBillstatus().equals(BillStatus.PACKED.getCode())) && isSameVersion(record)) {
                checkAndUpdateCutpaydetlRecordVersion(record);   // ���°汾��
                sendRecords.add(record);
            }
        }
        return sendRecords;
    }



    /**
     * ���ĳ��¼�Ƿ��й�����
     *
     * @param record
     * @return
     */
    public synchronized boolean isSameVersion(FipCutpaydetl record) {
        FipCutpaydetl originRecord = fipCutpaydetlMapper.selectByPrimaryKey(record.getPkid());
        if (originRecord.getRecversion().compareTo(record.getRecversion()) != 0) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * ����sendflag
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
     * ���¼�¼��+1��
     *
     * @param record
     * @return
     */
    @Transactional
    public synchronized FipCutpaydetl checkAndUpdateCutpaydetlRecordVersion(FipCutpaydetl record) {
        FipCutpaydetl originRecord = fipCutpaydetlMapper.selectByPrimaryKey(record.getPkid());
        if (originRecord.getRecversion().compareTo(record.getRecversion()) != 0) {
            throw new RuntimeException("�������³�ͻ,UUID=" + record.getPkid());
        } else {
            record.setRecversion(record.getRecversion() + 1);
            fipCutpaydetlMapper.updateByPrimaryKey(record);
            return record;
        }
    }
    @Transactional
    public synchronized FipRefunddetl checkAndUpdateRefunddetlRecordVersion(FipRefunddetl record) {
        FipRefunddetl originRecord = fipRefunddetlMapper.selectByPrimaryKey(record.getPkid());
        if (originRecord.getRecversion().compareTo(record.getRecversion()) != 0) {
            throw new RuntimeException("�������³�ͻ,UUID=" + record.getPkid());
        } else {
            record.setRecversion(record.getRecversion() + 1);
            fipRefunddetlMapper.updateByPrimaryKey(record);
            return record;
        }
    }

        /**
     * ���ݵ�ǰ�����������κ� ÿ���������1000��
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
     * ���ݵ�ǰ�����������κ� ÿ���������1000��    for ����
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


    //�ۿ�ǰ�����������   zhanrui 20121024
    @Transactional
    public synchronized void chgChannel(List<FipCutpaydetl> cutpaydetlList, CutpayChannel channel) {
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
                log.setJobname("�޸Ĵ�������");
                log.setJobdesc("�޸Ĵ�������Ϊ��" + channel.getTitle() + " �ɹ���");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                fipJoblogMapper.insert(log);
            }else{
                String jobdesc = "�޸Ĵ�������Ϊ��" + channel.getTitle() + " ʧ�ܡ�";
                appendNewJoblog(detl.getPkid(), "fip_cutpaydetl", "�޸Ĵ�������", jobdesc);
                throw new RuntimeException(jobdesc + " ��¼״̬����򲢷���ͻ��");
            }
        }
    }

    private void appendNewJoblog(String pkid, String tblname, String jobname, String jobdesc) {
        OperatorManager om = SystemService.getOperatorManager();
        String userid = om.getOperatorId();
        String username = om.getOperatorName();

        jobLogService.insertNewJoblog(pkid, tblname, jobname, jobdesc, "���ݽ���ƽ̨", "���ݽ���ƽ̨");
    }
}
