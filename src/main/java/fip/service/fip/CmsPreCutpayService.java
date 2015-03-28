package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.newcms.controllers.BaseCTL;
import fip.gateway.newcms.controllers.T100103CTL;
import fip.gateway.newcms.controllers.T100104CTL;
import fip.gateway.newcms.domain.T100103.T100103ResponseRecord;
import fip.gateway.newcms.domain.T100104.T100104RequestList;
import fip.gateway.newcms.domain.T100104.T100104RequestRecord;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.PtenudetailMapper;
import fip.repository.dao.XfapprepaymentMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 提前还款处理.
 * User: zhanrui
 * Date: 11-8-13
 * Time: 下午3:10
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CmsPreCutpayService {
    private static final Logger logger = LoggerFactory.getLogger(CmsPreCutpayService.class);

    @Autowired
    private BillManagerService billManagerService;
    @Autowired
    private CmsService cmsService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;
    @Autowired
    private XfapprepaymentMapper xfapprepaymentMapper;
    @Autowired
    private PtenudetailMapper enudetailMapper;

    private final BillType billType = BillType.PRECUTPAYMENT;

    /**
     * 查询信贷系统的代扣记录（可供选择性获取）
     *
     * @param bizType
     * @return
     */
    public List<FipCutpaydetl> doQueryCmsBills(BizType bizType) {

        List<T100103ResponseRecord> recvedList = getCmsPreCutpayResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100103ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, this.billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    /**
     * 获取全部信贷系统记录
     *
     * @return
     */
    //@Transactional
    public synchronized int doObtainCmsBills(BizType bizType) {
        //TODO
        //String billType = "0";

        List<T100103ResponseRecord> recvedList = getCmsPreCutpayResponseRecords(bizType);
        if (recvedList.size() > 0) {
            //TODO 检查本地数据状态

            //TODO 存档本地表中既存数据
            //billManagerService.archiveAllBillsByBizID(bizID);
        } else {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100103ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, this.billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            //TODO 判断业务主键是否重复   提前还款特别处理
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4PreCutpay(cutpaydetl.getPaybackdate(), cutpaydetl.getBilltype());
            //进行自动利息锁定
            if (lockOrUnlockIntr4PreCutpay(cutpaydetl, "3")) {
                if (isNotRepeated) {
                    cutpaydetl.setDateCmsGet(new Date());
                    fipCutpaydetlMapper.insert(cutpaydetl);
                    count++;
                } else {
                    logger.info("获取信贷数据时检查出重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                }
            } else {
                logger.info("利息加锁错误：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }

    //@Transactional
    public synchronized int doMultiObtainCmsBills(BizType bizType, FipCutpaydetl[] selectedCutpaydetls) {
        //TODO
        //String billType = "0";

        List<T100103ResponseRecord> recvedList = getCmsPreCutpayResponseRecords(bizType);
        if (recvedList.size() > 0) {
            //TODO 检查本地数据状态

            //TODO 存档本地表中既存数据
            //billManagerService.archiveAllBillsByBizID(bizID);
        } else {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100103ResponseRecord responseBean : recvedList) {
            //判断是否在获取范围内
            for (FipCutpaydetl selectedCutpaydetl : selectedCutpaydetls) {
                if (responseBean.getStdjjh().equals(selectedCutpaydetl.getIouno())
                        && responseBean.getStdqch().equals(selectedCutpaydetl.getPoano())) {
                    iSeqno++;
                    FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, this.billType, responseBean);
                    if (cutpaydetl == null) {
                        continue;
                    }
                    //TODO 判断业务主键是否重复
                    //boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
                    boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4PreCutpay(cutpaydetl.getPaybackdate(), cutpaydetl.getBilltype());
                    //进行自动利息锁定
                    if (lockOrUnlockIntr4PreCutpay(cutpaydetl, "3")) {
                        if (isNotRepeated) {
                            cutpaydetl.setDateCmsGet(new Date());
                            fipCutpaydetlMapper.insert(cutpaydetl);
                            count++;
                        } else {
                            logger.info("获取信贷数据时检查出重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        }
                    } else {
                        logger.info("利息加锁错误：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    }
                }
            }
        }

        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }

    private List<T100103ResponseRecord> getCmsPreCutpayResponseRecords(BizType bizType) {
        //获取新信贷数据LIST
        BaseCTL ctl;
        ctl = new T100103CTL();

        String biztype = "";
        if (bizType.equals(BizType.FD)) {
            biztype = "1";
        } else if (bizType.equals(BizType.XF)) {
            biztype = "2";
        }
        //查询 房贷/消费信贷（1/2） 数据
        return ctl.start(biztype);
    }

    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               BillType billType,
                                               T100103ResponseRecord responseBean) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        cutpaydetl.setXfappPkid(responseBean.getStdsqlsh()); //申请单流水号
        cutpaydetl.setAppno(responseBean.getStdsqdh()); //申请单号

        cutpaydetl.setIouno(responseBean.getStdjjh()); //借据号
        cutpaydetl.setPoano(responseBean.getStdqch());  //期次号
        cutpaydetl.setContractno(responseBean.getStdhth()); //合同号
        cutpaydetl.setPaybackdate(responseBean.getStdjhhkr()); //计划还款日

        //客户信息
        cutpaydetl.setClientno(responseBean.getStdkhh());
        cutpaydetl.setClientname(responseBean.getStdkhmc());

        //SBS开户信息
        cutpaydetl.setClientact(responseBean.getStddkzh());   //贷款帐号

        //还款金额信息
        cutpaydetl.setPaybackamt(new BigDecimal(responseBean.getStdhkje()));  //还款金额
        cutpaydetl.setPrincipalamt(new BigDecimal(responseBean.getStdhkbj())); //还款本金
        cutpaydetl.setInterestamt(new BigDecimal(responseBean.getStdhklx()));  //还款利息
        cutpaydetl.setPunitiveintamt(new BigDecimal("0.00"));//罚息金额
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //冗余金额
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//违约金金额
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//罚息复利金额


        //还款渠道信息
        if (bizType.equals(BizType.XF)) {
            //TODO 遗留数据
            String tmpStr = responseBean.getStddqh();
            String regioncdTmp, bankcdTmp, nameTmp;
            if (StringUtils.isEmpty(tmpStr)) {
                if (StringUtils.isEmpty(cutpaydetl.getAppno())) {
                    String msg = "自信贷获取扣款记录时出错，申请单号不应为空." + cutpaydetl.getClientname();
                    logger.error(msg);
                    throw new RuntimeException(msg);
                }
                //对于不含扣款帐号的记录
                Xfapprepayment xfapprepayment = xfapprepaymentMapper.selectByPrimaryKey(cutpaydetl.getAppno());
                if (xfapprepayment == null) {
                    logger.error("未找到本地申请记录。" + cutpaydetl.getClientname());
                    return null;
                }
                String channel = xfapprepayment.getChannel();
                if (StringUtils.isEmpty(channel)) {
                    logger.error("自信贷获取扣款记录发现渠道号为空." + cutpaydetl.getClientname());
                    channel = CutpayChannel.NONE.getCode();
                }
                cutpaydetl.setBiChannel(channel);
                cutpaydetl.setBiActopeningbank(xfapprepayment.getActopeningbank());
                cutpaydetl.setBiBankactno(xfapprepayment.getBankactno());
                cutpaydetl.setBiBankactname(xfapprepayment.getBankactname());
                cutpaydetl.setBiActopeningbankUd(xfapprepayment.getActopeningbankUd());
                cutpaydetl.setBiCustomerCode(xfapprepayment.getCustomerCode());
                cutpaydetl.setBiSignAccountNo(xfapprepayment.getSignAccountNo());
                cutpaydetl.setBiProvince(xfapprepayment.getProvince());
                cutpaydetl.setBiCity(xfapprepayment.getCity());
            } else {
                String[] code = tmpStr.split("-");
                if (code.length == 2) {
                    regioncdTmp = code[0].trim(); //废弃
                    bankcdTmp = code[1].trim();
                    cutpaydetl.setBiActopeningbank(bankcdTmp);
                }
                cutpaydetl.setBiBankactno(responseBean.getStdhkzh());
                //cutpaydetl.setBiBankactname(responseBean.getStdkhmc());
                cutpaydetl.setBiBankactname(responseBean.getStdckr());
                cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode()); //默认为无渠道
            }
        } else if (bizType.equals(BizType.FD)) {
            String tmpStr = responseBean.getStddqh();
            String regioncdTmp = "";
            String bankcdTmp = "";
            String nameTmp = "";
            String[] code = tmpStr.split("-");
            if (code.length == 2) {
                regioncdTmp = code[0].trim();
                bankcdTmp = code[1].trim();
                cutpaydetl.setBiActopeningbank(bankcdTmp);
            }
            cutpaydetl.setBiBankactno(responseBean.getStdhkzh());
            //cutpaydetl.setBiBankactname(responseBean.getStdkhmc());
            cutpaydetl.setBiBankactname(responseBean.getStdckr());

            //临时方案 如果地区不是青岛 全部通过银联  20120829 修改为 105全部直连建行
            if ("105".equals(bankcdTmp)) {
                cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode()); //默认为无渠道
            } else {
                cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode());
            }

            Ptenudetail enu = selectEnuDetail("CmsProvince", regioncdTmp);
            if (enu == null) {
                String msg = "地区信息有误:" + cutpaydetl.getClientname() + " 区号:" + regioncdTmp;
                logger.error(msg);
                throw new RuntimeException(msg);
            }
            cutpaydetl.setBiProvince(enu.getEnuitemexpand());
            cutpaydetl.setBiCity(enu.getEnuitemlabel());

            /*
            if ("0532".equals(regioncdTmp)) {
                cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode()); //默认为无渠道
                cutpaydetl.setBiProvince("青岛");
            } else {
                cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode());
                if ("0531".equals(regioncdTmp)) {
                    cutpaydetl.setBiProvince("山东");
                } else if ("0351".equals(regioncdTmp)) {
                    cutpaydetl.setBiProvince("山西");
                } else if ("023".equals(regioncdTmp)) {
                    cutpaydetl.setBiProvince("重庆");
                } else {
                    cutpaydetl.setBiProvince("地区不明");
                }
            }
            cutpaydetl.setBiCity(regioncdTmp);
            */
        }

        //其他
        cutpaydetl.setRecversion((long) 0);
        cutpaydetl.setDeletedflag("0");
        cutpaydetl.setArchiveflag("0");
        //帐单状态
        cutpaydetl.setBillstatus(BillStatus.INIT.getCode());
        cutpaydetl.setSendflag("0");

        //帐单类型
        cutpaydetl.setBilltype(billType.getCode());
        cutpaydetl.setDateCmsGet(new Date());
        return cutpaydetl;
    }

    //============================================

    /**
     * 查找具体一条枚举记录
     *
     * @param enuType
     * @param areaCode
     * @return
     */
    private Ptenudetail selectEnuDetail(String enuType, String areaCode) {
        PtenudetailExample example = new PtenudetailExample();
        example.createCriteria().andEnutypeEqualTo(enuType).andEnuitemvalueEqualTo(areaCode);
        if (enudetailMapper.countByExample(example) != 1) {
            return null;
        }
        return enudetailMapper.selectByExample(example).get(0);
    }


    /**
     * 检查本地表中既存记录的状态 不允许有
     * 1、状态不明的记录
     * 2、未发送的记录
     * 3、发送成功的记录（发送成功的必须入帐回写）
     *
     * @return
     */
    private boolean checkLocalBillsStatus() {
        return true;
    }

    //TODO  事务
    private void batchInsertLogByBatchno(String batchno) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBatchSnEqualTo(batchno);
        List<FipCutpaydetl> fipCutpaydetlList = fipCutpaydetlMapper.selectByExample(example);

        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        for (FipCutpaydetl fipCutpaydetl : fipCutpaydetlList) {
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(fipCutpaydetl.getPkid());
            log.setJobname("新建记录");
            log.setJobdesc("新获取信贷系统代扣记录");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }

    //================

    /**
     * 加锁解锁提前还款利息
     */
    public synchronized boolean lockOrUnlockIntr4PreCutpay(FipCutpaydetl detl, String option) {
        if ((!"1".equals(option))
                && (!"2".equals(option))
                && (!"3".equals(option))
                ) {
            throw new RuntimeException("参数错误！");
        }

        T100104CTL t100104ctl = new T100104CTL();
        T100104RequestRecord record = new T100104RequestRecord();
        record.setStdjjh(detl.getIouno());
        record.setStdqch(detl.getPoano());
        record.setStdjhkkr(detl.getPaybackdate());
        //1-成功 2-失败(利息解锁)  3-利息锁定
        record.setStdkkjg(option);
        T100104RequestList list = new T100104RequestList();
        list.add(record);
        //单笔发送处理
        return t100104ctl.start(list);
    }

}
