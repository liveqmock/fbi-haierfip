package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.*;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.fip.FipCommonMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.advance.utils.PropertyManager;
import pub.platform.security.OperatorManager;
import skyline.service.common.ToolsService;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-23
 * Time: 上午8:10
 * To change this template use File | Settings | File Templates.
 */
@Service
public class BatchPkgService {
    private static final Logger logger = LoggerFactory.getLogger(BatchPkgService.class);


    @Autowired
    private ToolsService toolsService;

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipCutpaybatMapper fipCutpaybatMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;

    @Autowired
    private FipCommonMapper fipCommonMapper;

    private static SimpleDateFormat sdf8 = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat sdf14 = new SimpleDateFormat("yyyyMMddHHmmss");

    public FipCutpaybat selectRecord(String pkid) {
        return fipCutpaybatMapper.selectByPrimaryKey(pkid);
    }

    public List<FipCutpaydetl> selectRecordByLastPoano(BizType bizType, List<FipCutpaydetl> detlList) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        //example.createCriteria();
        List<FipCutpaydetl> resultList = new ArrayList<FipCutpaydetl>();
        for (FipCutpaydetl detl : detlList) {
            example.clear();
            int poano = Integer.parseInt(detl.getPoano()) - 1;
            example.createCriteria().andIounoEqualTo(detl.getIouno())
                    .andPoanoEqualTo(String.valueOf(poano))
                    .andBiBankactnoEqualTo(detl.getBiBankactno())
                    .andBiChannelEqualTo(detl.getBiChannel())
                    .andOriginBizidEqualTo(bizType.getCode())
                    .andBillstatusEqualTo(BillStatus.CMS_SUCCESS.getCode());
            if (fipCutpaydetlMapper.selectByExample(example).size() > 0) {
                resultList.add(detl);
            }else{
                example.clear();
                poano = Integer.parseInt(detl.getPoano()) - 2;
                example.createCriteria().andIounoEqualTo(detl.getIouno())
                        .andPoanoEqualTo(String.valueOf(poano))
                        .andBiBankactnoEqualTo(detl.getBiBankactno())
                        .andBiChannelEqualTo(detl.getBiChannel())
                        .andOriginBizidEqualTo(bizType.getCode())
                        .andBillstatusEqualTo(BillStatus.CMS_SUCCESS.getCode());
                if (fipCutpaydetlMapper.selectByExample(example).size() > 0) {
                    resultList.add(detl);
                }
            }
        }
        return resultList;
    }


    @Transactional
    public void packCcbBatchPkg(BizType bizType, List<FipCutpaydetl> cutpaydetlList) {
        long firstBatchSn = generateTxpkgSn();

        int batPkgCount = 2000;

        List<FipCutpaybat> cutpaybatList = new ArrayList<FipCutpaybat>();
        processBatchPkg(firstBatchSn, 0, bizType, CutpayChannel.NONE, cutpaydetlList, cutpaybatList, batPkgCount, "BAW");
        insertFipCutpaybatList(cutpaybatList);
    }

    /**
     * 打包银联批量报文
     *
     * @param bizType
     * @param cutpaydetlList
     */
    @Transactional
    public void packUnipayBatchPkg(BizType bizType, List<FipCutpaydetl> cutpaydetlList) {
        List<String> syncBankcodes = toolsService.selectEnuItemValue("UnipayRealTxnBank");
        List<FipCutpaydetl> syncTxnList = new ArrayList<FipCutpaydetl>();
        List<FipCutpaydetl> asyncTxnList = new ArrayList<FipCutpaydetl>();
        for (FipCutpaydetl fipCutpaydetl : cutpaydetlList) {
            if (isSyncTxnType(syncBankcodes, fipCutpaydetl)) {
                syncTxnList.add(fipCutpaydetl);
            } else {
                asyncTxnList.add(fipCutpaydetl);
            }
        }
        doPackUnipayBatchPkg(bizType, asyncTxnList, "ASYNC");
        doPackUnipayBatchPkg(bizType, syncTxnList, "SYNC");
    }

    private boolean isSyncTxnType(List<String> syncBankcodes, FipCutpaydetl fipCutpaydetl) {
        for (String bank : syncBankcodes) {
            if (fipCutpaydetl.getBiActopeningbank().equals(bank)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param bizType
     * @param cutpaydetlList
     * @param txnType        同步100004交易：“SYNC”  异步同步100001交易：“ASYNC”
     */
    private void doPackUnipayBatchPkg(BizType bizType, List<FipCutpaydetl> cutpaydetlList, String txnType) {
        long firstBatchSn = generateTxpkgSn();

        int batPkgCount = 1; //同步交易默认每包为一笔
        if ("ASYNC".equals(txnType)) {
            batPkgCount = Integer.parseInt(PropertyManager.getProperty("unionpay_batch_max_num"));
        }

        List<FipCutpaybat> cutpaybatList = new ArrayList<FipCutpaybat>();
        processBatchPkg(firstBatchSn, 0, bizType, CutpayChannel.UNIPAY, cutpaydetlList, cutpaybatList, batPkgCount, txnType);
        insertFipCutpaybatList(cutpaybatList);
    }

    /**
     * 循环更新单笔帐单的序号以及生成批量报文BEAN
     *
     * @param txpkgSn
     * @param txpkgDetlSn
     * @param bizType
     * @param cutpayChannel
     * @param cutpaydetlList 待处理的明细记录
     * @param cutpaybatList  存放生成的批量包
     * @param batPkgCount    自定义的批量报文最大明细笔数
     */
    private void processBatchPkg(long txpkgSn, int txpkgDetlSn,
                                 BizType bizType, CutpayChannel cutpayChannel,
                                 final List<FipCutpaydetl> cutpaydetlList,
                                 List<FipCutpaybat> cutpaybatList, int batPkgCount,
                                 String txnType) {
        int listSize = cutpaydetlList.size();
        if (txpkgDetlSn >= listSize) return;
        int count = 0;
        BigDecimal totalamt = new BigDecimal(0);
        do {
            FipCutpaydetl cutpaydetl = cutpaydetlList.get(txpkgDetlSn);
            updateBillBatchSn(cutpaydetl, txpkgSn, txpkgDetlSn % batPkgCount + 1);
            txpkgDetlSn++;
            count++;
            totalamt = totalamt.add(cutpaydetl.getPaybackamt());
        } while ((txpkgDetlSn < listSize) && (txpkgDetlSn % batPkgCount != 0));

        cutpaybatList.add(initNewBaseCutpaybatBean(bizType, txpkgSn, count, totalamt, cutpayChannel, txnType));
        txpkgSn++;
        processBatchPkg(txpkgSn, txpkgDetlSn, bizType, cutpayChannel, cutpaydetlList, cutpaybatList, batPkgCount, txnType);
    }

    /**
     * 生成批量报文序号： 8位日期 + 6位序号
     *
     * @return
     */
    private Long generateTxpkgSn() {
        String strdate8 = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String txpkgsn = fipCommonMapper.selectMaxTxPkgSnByDate(strdate8);
        int iSeqno = 1;
        if (!StringUtils.isEmpty(txpkgsn)) {
            iSeqno = Integer.parseInt(txpkgsn.substring(8, 14));
            iSeqno++;
        }
        String sSeqno = "" + iSeqno;
        sSeqno = StringUtils.leftPad(sSeqno, 6, "0");
        return Long.parseLong(strdate8 + sSeqno);
    }

    /**
     * 更新  单笔帐单的 批量报文序号和明细序号
     */
    private void updateBillBatchSn(FipCutpaydetl cutpaydetl, long txpkgSn, int txpkgDetlSn) {
        FipCutpaydetl cutpaydetl_db = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid());
        Long recversion = cutpaydetl.getRecversion();
        if (!cutpaydetl_db.getRecversion().equals(recversion)) {
            logger.error("记录版本冲突:" + cutpaydetl.getClientname());
            throw new RuntimeException("记录版本冲突:" + cutpaydetl.getClientname());
        }

        recversion++;
        cutpaydetl.setRecversion(recversion);

        cutpaydetl.setTxpkgSn(String.valueOf(txpkgSn));
        cutpaydetl.setTxpkgDetlSn(StringUtils.leftPad(String.valueOf(txpkgDetlSn), 6, "0"));
        cutpaydetl.setBillstatus(BillStatus.PACKED.getCode());
        fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);

        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog log = new FipJoblog();
        log.setTablename("fip_cutpaydetl");
        log.setRowpkid(cutpaydetl.getPkid());
        log.setJobname("批量打包");
        log.setJobdesc("准备批量扣款的报文");
        log.setJobtime(date);
        log.setJobuserid(userid);
        log.setJobusername(username);
        fipJoblogMapper.insert(log);
    }

    private FipCutpaybat initNewBaseCutpaybatBean(BizType bizType, long txPkgSn,
                                                  int batPkgCount, BigDecimal totalamt,
                                                  CutpayChannel channel, String txnType) {
        FipCutpaybat fipCutpaybat = new FipCutpaybat();
        fipCutpaybat.setTxpkgSn(String.valueOf(txPkgSn));
        String bizTypeCode = bizType.getCode();

        String tmpBizTypeCode = bizTypeCode;
        if (bizTypeCode.length() > 4) {
            tmpBizTypeCode = bizTypeCode.substring(0, 4);
        }
        fipCutpaybat.setBizSn(tmpBizTypeCode + txPkgSn);

        fipCutpaybat.setTxndate(sdf8.format(new Date()));
        fipCutpaybat.setTxntype(txnType);

        fipCutpaybat.setTxntype(txnType);
        BigDecimal count = new BigDecimal(batPkgCount);

        //不区分后续包
        fipCutpaybat.setTotalcount(count); //总笔数
        fipCutpaybat.setCurrcount(count); // 本包总笔数
        fipCutpaybat.setMultiflag("0"); //无后续包
        fipCutpaybat.setTransferact("801000003012011001    "); //转出帐户   TODO 目前是建行账号

        fipCutpaybat.setTotalamt(totalamt);
        fipCutpaybat.setFailamt(new BigDecimal(0));
        fipCutpaybat.setFailcount(new BigDecimal(0));

        fipCutpaybat.setUsage("99999999    "); //用途
        fipCutpaybat.setRemark("");
        fipCutpaybat.setRemark1("");
        fipCutpaybat.setRemark2("");

        fipCutpaybat.setStartdate(new Date());

        fipCutpaybat.setBankid("105");// 银行代码  TODO 目前是建行

        fipCutpaybat.setTxpkgStatus(TxpkgStatus.SEND_PEND.getCode());

        fipCutpaybat.setOriginBizid(bizTypeCode);
        fipCutpaybat.setArchiveflag("0");
        fipCutpaybat.setDeletedflag("0");
        fipCutpaybat.setRecversion(1L);
        fipCutpaybat.setSendflag(TxSendFlag.UNSEND.getCode());

        fipCutpaybat.setChannel(channel.getCode());
        return fipCutpaybat;
    }

    /**
     * 插入多笔记录到批量表
     */
    private void insertFipCutpaybatList(List<FipCutpaybat> fipCutpaybatList) {
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        for (FipCutpaybat fipCutpaybat : fipCutpaybatList) {
            fipCutpaybatMapper.insert(fipCutpaybat);
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaybat");
            log.setRowpkid(fipCutpaybat.getTxpkgSn());
            log.setJobname("新建报文");
            log.setJobdesc("准备批量扣款的报文");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }


    public List<FipCutpaybat> selectSendableBatchs(BizType bizType, CutpayChannel channel, TxSendFlag sendFlag) {
        FipCutpaybatExample example = new FipCutpaybatExample();
        example.createCriteria().andDeletedflagEqualTo("0").andArchiveflagEqualTo("0")
                .andChannelEqualTo(channel.getCode())
                .andOriginBizidEqualTo(bizType.getCode()).andSendflagEqualTo(sendFlag.getCode());
        example.setOrderByClause("txpkg_sn");
        return fipCutpaybatMapper.selectByExample(example);
    }

    //获取批量报文
    public List<FipCutpaybat> selectBatchRecordList(BizType bizType, CutpayChannel channel, TxpkgStatus status) {
        FipCutpaybatExample example = new FipCutpaybatExample();
        example.createCriteria().andDeletedflagEqualTo("0").andArchiveflagEqualTo("0")
                .andChannelEqualTo(channel.getCode())
                .andOriginBizidEqualTo(bizType.getCode()).andTxpkgStatusEqualTo(status.getCode());
        example.setOrderByClause("txpkg_sn");
        return fipCutpaybatMapper.selectByExample(example);
    }

    //获取批量报文 历史报文
    public List<FipCutpaybat> selectHistoryBatchRecordList(BizType bizType, CutpayChannel channel, TxpkgStatus status) {
        FipCutpaybatExample example = new FipCutpaybatExample();
        example.createCriteria().andDeletedflagEqualTo("0").andArchiveflagEqualTo("0")
                .andChannelEqualTo(channel.getCode())
                .andOriginBizidEqualTo(bizType.getCode()).andTxpkgStatusEqualTo(status.getCode());
        example.setOrderByClause("txpkg_sn desc");
        return fipCutpaybatMapper.selectByExample(example);
    }

    /**
     * 查询需再次查询确认的批量报文
     *
     * @param bizType
     * @param channel
     * @return
     */
    public List<FipCutpaybat> selectNeedConfirmBatchRecords(BizType bizType, CutpayChannel channel) {
        FipCutpaybatExample example = new FipCutpaybatExample();
        example.or().andChannelEqualTo(channel.getCode())
                .andSendflagEqualTo(TxSendFlag.SENT.getCode())
                .andTxpkgStatusEqualTo(TxpkgStatus.QRY_PEND.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizType.getCode());
        example.or().andChannelEqualTo(channel.getCode())
                .andSendflagEqualTo(TxSendFlag.SENT.getCode())
                .andTxpkgStatusEqualTo(TxpkgStatus.DEAL_FAIL.getCode())
                .andArchiveflagEqualTo("0").andDeletedflagEqualTo("0")
                .andOriginBizidEqualTo(bizType.getCode());
        example.setOrderByClause("TXPKG_SN desc");
        return fipCutpaybatMapper.selectByExample(example);
    }

    @Transactional
    public void unpackOneBatchPkg(FipCutpaybat cutpaybat) {
        //if (cutpaybat.getSendflag().equals(TxSendFlag.UNSEND.getCode())) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andTxpkgSnEqualTo(cutpaybat.getTxpkgSn()).andArchiveflagEqualTo("0").andDeletedflagEqualTo("0");
        List<FipCutpaydetl> detlList = fipCutpaydetlMapper.selectByExample(example);
        for (FipCutpaydetl fipCutpaydetl : detlList) {
            fipCutpaydetl.setBillstatus(BillStatus.INIT.getCode());
            fipCutpaydetl.setRecversion(fipCutpaydetl.getRecversion() + 1);
            fipCutpaydetlMapper.updateByPrimaryKey(fipCutpaydetl);
            String log = "重新解包，批量报文号：" + cutpaybat.getTxpkgSn();
            appendNewJoblog(fipCutpaydetl.getPkid(), "fip_cutpaydetl", "重新解包", log);
        }
        FipCutpaybatExample batExample = new FipCutpaybatExample();
        batExample.createCriteria().andTxpkgSnEqualTo(cutpaybat.getTxpkgSn());
        FipCutpaybat batRecord = fipCutpaybatMapper.selectByPrimaryKey(cutpaybat.getTxpkgSn());
        if (!batRecord.getRecversion().equals(cutpaybat.getRecversion())) {
            throw new RuntimeException("批量报文版本错误！");
        }
        batRecord.setRecversion(cutpaybat.getRecversion() + 1);
        batRecord.setArchiveflag("1");
        batRecord.setSendflag(TxSendFlag.SENT.getCode());
        fipCutpaybatMapper.updateByPrimaryKey(batRecord);
        appendNewJoblog(batRecord.getTxpkgSn(), "fip_cutpaybat", "重新解包", "...");
        //}
    }

    private void appendNewJoblog(String pkid, String tblname, String jobname, String jobdesc) {
        OperatorManager om = SystemService.getOperatorManager();
        String userid = om.getOperatorId();
        String username = om.getOperatorName();

        jobLogService.insertNewJoblog(pkid, tblname, jobname, jobdesc, "数据交换平台", "数据交换平台");
    }

}
