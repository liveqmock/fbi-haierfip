package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.newcms.controllers.*;
import fip.gateway.newcms.domain.T100101.T100101ResponseRecord;
import fip.gateway.newcms.domain.T100102.T100102RequestList;
import fip.gateway.newcms.domain.T100102.T100102RequestRecord;
import fip.gateway.newcms.domain.T100104.T100104RequestList;
import fip.gateway.newcms.domain.T100104.T100104RequestRecord;
import fip.gateway.newcms.domain.T100108.T100108RequestList;
import fip.gateway.newcms.domain.T100108.T100108RequestRecord;
import fip.repository.dao.*;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * 正常还款处理.
 * User: zhanrui
 * Date: 11-8-13
 * Time: 下午3:10
 */
@Service
public class CmsService {
    private static final Logger logger = LoggerFactory.getLogger(CmsService.class);

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;

    @Autowired
    private FipJoblogMapper fipJoblogMapper;

    @Autowired
    private XfappMapper xfappMapper;

    @Autowired
    private XfapprepaymentMapper xfapprepaymentMapper;

    @Autowired
    private PtenudetailMapper enudetailMapper;

    /**
     * 查询信贷系统的代扣记录（可供选择性获取）    (不包括罚息帐单)
     */
    public List<FipCutpaydetl> doQueryCmsBills(BizType bizType, BillType billType) {

        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //前过滤 只获取贷款状态为正常的记录
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                logger.error("贷款状态字段为空:" + responseBean.getStdkhmc());
                throw new RuntimeException("贷款状态字段为空:" + responseBean.getStdkhmc());
            }
            if (!"0".equals(stddkzt.trim())) { //贷款状态：非正常的不处理
                continue;
            }

            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, billType, responseBean);

            //后过滤
            if (cutpaydetl == null) {
                continue;
            }

            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    /**
     * 获取全部信贷系统记录  (不包括罚息帐单)
     */
    //@Transactional
    public synchronized int doObtainCmsBills(BizType bizType, BillType billType, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //前过滤 只获取贷款状态为正常的记录
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                returnMsgs.add("贷款状态字段为空:" + responseBean.getStdkhmc());
                logger.error("贷款状态字段为空:" + responseBean.getStdkhmc());
                continue;
            }
            if (!"0".equals(stddkzt.trim())) { //贷款状态：非正常的不处理
                continue;
            }

            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);

            //后过滤
            if (cutpaydetl == null) {
                continue;
            }

            //TODO 判断业务主键是否重复
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
            if (isNotRepeated) {
                cutpaydetl.setDateCmsGet(new Date());
                fipCutpaydetlMapper.insert(cutpaydetl);
                count++;
            } else {
                returnMsgs.add("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.info("获取信贷数据时检查出重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }

    //@Transactional
    public synchronized int doMultiObtainCmsBills(BizType bizType, BillType billType, FipCutpaydetl[] selectedCutpaydetls, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //前过滤 只获取贷款状态为正常的记录
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                returnMsgs.add("贷款状态字段为空:" + responseBean.getStdkhmc());
                logger.error("贷款状态字段为空:" + responseBean.getStdkhmc());
                continue;
            }
            if (!"0".equals(stddkzt.trim())) { //贷款状态：非正常的不处理
                continue;
            }

            //判断是否在获取范围内
            for (FipCutpaydetl selectedCutpaydetl : selectedCutpaydetls) {
                if (responseBean.getStdjjh().equals(selectedCutpaydetl.getIouno())
                        && responseBean.getStdqch().equals(selectedCutpaydetl.getPoano())) {
                    iSeqno++;
                    FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);
                    if (cutpaydetl == null) {
                        continue;
                    }
                    //TODO 判断业务主键是否重复
                    boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
                    if (isNotRepeated) {
                        cutpaydetl.setDateCmsGet(new Date());
                        fipCutpaydetlMapper.insert(cutpaydetl);
                        count++;
                    } else {
                        returnMsgs.add("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        logger.info("获取信贷数据时检查出重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    }

                }
            }
        }

        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }

    //=======================================================================================

    /**
     * 查询信贷系统的代扣记录（逾期记录）
     */
    public List<FipCutpaydetl> doQueryCmsOverdueBills(BizType bizType, BillType billType) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //前过滤 只获取贷款状态为逾期的记录
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                logger.error("贷款状态字段为空:" + responseBean.getStdkhmc());
                throw new RuntimeException("贷款状态字段为空:" + responseBean.getStdkhmc());
            }
            if (!"1".equals(stddkzt.trim())) { //贷款状态：非正常的不处理
                continue;
            }

            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    /**
     * 获取全部信贷系统记录  (不包括罚息帐单)
     *
     * @return
     */
    @Transactional
    public synchronized int doObtainCmsOverdueBills(BizType bizType, BillType billType, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //前过滤 只获取贷款状态为逾期的记录
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                returnMsgs.add("贷款状态字段为空:" + responseBean.getStdkhmc());
                logger.error("贷款状态字段为空:" + responseBean.getStdkhmc());
            }
            if (!"1".equals(stddkzt.trim())) { //贷款状态：非正常的不处理
                continue;
            }

            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            //TODO 判断业务主键是否重复
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());

            //进行自动利息锁定
            if (lockOrUnlockIntr4Overdue(cutpaydetl.getIouno(), "1")) {
                if (isNotRepeated) {
                    cutpaydetl.setDateCmsGet(new Date());
                    fipCutpaydetlMapper.insert(cutpaydetl);
                    count++;
                } else {
                    returnMsgs.add("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    logger.info("获取信贷数据时检查出重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                }
            } else {
                returnMsgs.add("利息加锁错误：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.info("利息加锁错误：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }

    @Transactional
    public synchronized int doMultiObtainCmsOverdueBills(BizType bizType, BillType billType, FipCutpaydetl[] selectedCutpaydetls, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //前过滤 只获取贷款状态为逾期的记录
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                returnMsgs.add("贷款状态字段为空:" + responseBean.getStdkhmc());
                logger.error("贷款状态字段为空:" + responseBean.getStdkhmc());
            }
            if (!"1".equals(stddkzt.trim())) { //贷款状态：非正常的不处理
                continue;
            }

            //判断是否在获取范围内
            for (FipCutpaydetl selectedCutpaydetl : selectedCutpaydetls) {
                if (responseBean.getStdjjh().equals(selectedCutpaydetl.getIouno())
                        && responseBean.getStdqch().equals(selectedCutpaydetl.getPoano())) {
                    iSeqno++;
                    FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);
                    if (cutpaydetl == null) {
                        continue;
                    }
                    //TODO 判断业务主键是否重复
                    boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
                    //进行自动利息锁定
                    if (lockOrUnlockIntr4Overdue(cutpaydetl.getIouno(), "1")) {
                        if (isNotRepeated) {
                            cutpaydetl.setDateCmsGet(new Date());
                            fipCutpaydetlMapper.insert(cutpaydetl);
                            count++;
                        } else {
                            returnMsgs.add("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                            logger.info("获取信贷数据时检查出重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        }
                    } else {
                        returnMsgs.add("利息加锁错误：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        logger.info("利息加锁错误：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    }
                }
            }
        }

        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }


    //=======================================================================================
    private List<T100101ResponseRecord> getCmsResponseRecords(BizType bizType) {
        //获取新信贷数据LIST
        BaseCTL ctl;
        ctl = new T100101CTL();

        String biztype = "";
        if (bizType.equals(BizType.FD)) {
            biztype = "1";
        } else if (bizType.equals(BizType.XF)) {
            biztype = "2";
        }
        //查询 房贷/消费信贷（1/2） 数据
        return ctl.start(biztype);
    }


    /**
     * 不包括 罚息帐单处理 ！！！
     */
    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               BillType billType,
                                               T100101ResponseRecord responseBean) {
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
        cutpaydetl.setPunitiveintamt(new BigDecimal(responseBean.getStdfxje()));//罚息金额

        //字段初始
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//违约金金额
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//罚息复利金额
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //冗余金额

        //20120503 zhanrui 房贷业务的罚息复利 存放在报文的冗余字段中
        cutpaydetl.setCompoundintamt(new BigDecimal(responseBean.getStdryje()));  //罚息复利金额

        //帐单类型
        cutpaydetl.setBilltype(billType.getCode());

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

                //cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode()); //默认为无渠道
                //20120313 zhanrui 消费信贷老数据改为银联代扣
                cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //默认为无渠道
            }
            //zhanrui 20120305  标识消费信贷数据来源自信贷系统 便与回写时区分来源系统
            cutpaydetl.setRemark3("XF-CMS");
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

        }

        //其他
        cutpaydetl.setRecversion((long) 0);
        cutpaydetl.setDeletedflag("0");
        cutpaydetl.setArchiveflag("0");
        cutpaydetl.setWritebackflag("0");
        cutpaydetl.setAccountflag("0");
        //帐单状态
        cutpaydetl.setBillstatus(BillStatus.INIT.getCode());
        cutpaydetl.setSendflag("0");

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

    /**
     * 回写信贷系统
     * 2013-4-11之前只使用本方法回写代扣成功记录
     */
    //@Transactional
    public int writebackCutPayRecord2CMS(List<FipCutpaydetl> cutpaydetlList, List<String> returnMsgs) {

        int count = 0;

        T100102CTL t100102ctl = new T100102CTL();
        T100104CTL t100104ctl = new T100104CTL();
        T100106CTL t100106ctl = new T100106CTL();

        for (FipCutpaydetl detl : cutpaydetlList) {
            boolean writebackResult = false;
            if (detl.getBilltype().equals(BillType.NORMAL.getCode())) { //正常还款
                T100102RequestRecord recordT102 = new T100102RequestRecord();
                recordT102.setStdjjh(detl.getIouno());
                recordT102.setStdqch(detl.getPoano());
                recordT102.setStdjhkkr(detl.getPaybackdate());
                //1-成功 2-失败
                recordT102.setStdkkjg("1");
                T100102RequestList t100102list = new T100102RequestList();
                t100102list.add(recordT102);
                //单笔发送处理
                writebackResult = t100102ctl.start(t100102list);
            } else if (detl.getBilltype().equals(BillType.PRECUTPAYMENT.getCode())) { //提前还款
                T100104RequestRecord recordT104 = new T100104RequestRecord();
                recordT104.setStdjjh(detl.getIouno());
                recordT104.setStdqch(detl.getPoano());
                recordT104.setStdjhkkr(detl.getPaybackdate());
                //1-成功 2-失败
                recordT104.setStdkkjg("1");
                T100104RequestList t100104list = new T100104RequestList();
                t100104list.add(recordT104);
                //单笔发送处理
                writebackResult = t100104ctl.start(t100104list);
            } else if (detl.getBilltype().equals(BillType.OVERDUE.getCode())) { //逾期
                /*逾期回写暂时使用正常还款回写接口
                T100106RequestRecord recordT106 = new T100106RequestRecord();
                recordT106.setStdjjh(detl.getIouno());
                recordT106.setStdqch(detl.getPoano());
                recordT106.setStdjhkkr(detl.getPaybackdate());
                //1-成功 2-失败
                recordT106.setStdkkjg("1");
                T100106RequestList t100106list = new T100106RequestList();
                t100106list.add(recordT106);
                */
                T100102RequestRecord recordT102 = new T100102RequestRecord();
                recordT102.setStdjjh(detl.getIouno());
                recordT102.setStdqch(detl.getPoano());
                recordT102.setStdjhkkr(detl.getPaybackdate());
                //1-成功 2-失败
                recordT102.setStdkkjg("1");
                T100102RequestList t100102list = new T100102RequestList();
                t100102list.add(recordT102);

                //20130109 zr 逾期类自动解锁 (先解锁，处理成功后再回写)
                if (unlockIntr4Overdue(detl)) {
                    //单笔发送处理
                    writebackResult = t100102ctl.start(t100102list);
                    //单笔发送处理
                    //writebackResult = t100106ctl.start(t100106list);
                } else {
                    returnMsgs.add("利息解锁错误：" + detl.getIouno() + detl.getClientname());
                }
            } else {
                returnMsgs.add("回写信贷系统出错，帐单类型不支持：" + detl.getIouno() + detl.getClientname());
                throw new RuntimeException("回写信贷系统出错，帐单类型不支持");
            }

            Date date = new Date();
            FipJoblog log = new FipJoblog();
            if (writebackResult) {
                detl.setBillstatus(BillStatus.CMS_SUCCESS.getCode());
                detl.setArchiveflag("1");
                log.setJobdesc("信贷回写处理成功");
                count++;
            } else {
                detl.setBillstatus(BillStatus.CMS_FAILED.getCode());
                log.setJobdesc("信贷回写处理失败");
            }
            detl.setDateCmsPut(date);
            fipCutpaydetlMapper.updateByPrimaryKey(detl);

            //工作日志处理
            String userid = SystemService.getOperatorManager().getOperatorId();
            String username = SystemService.getOperatorManager().getOperatorName();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(detl.getPkid());
            log.setJobname("代扣成功记录信贷回写处理");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
        return count;
    }

    /**
     * zr
     * 回写信贷系统
     * 2013-4-11之后新增本方法，用于回写代扣失败记录
     */
    public int writebackCutPayRecord2CMS_ForFailureReord(List<FipCutpaydetl> cutpaydetlList, List<String> returnMsgs) {

        int count = 0;

        T100102CTL t100102ctl = new T100102CTL();
        T100104CTL t100104ctl = new T100104CTL();
        T100106CTL t100106ctl = new T100106CTL();

        FipJoblog log = new FipJoblog();
        log.setJobcode("writebackCutPayRecord2CMS_ForFailureReord");

        for (FipCutpaydetl detl : cutpaydetlList) {
            //20140122 zr  只对银联的返回3008余额不足的进行回写
            if (!"3008".equals(detl.getTxRetcode())) {
                detl.setArchiveflag("1");
                log.setJobdesc("代扣失败记录存档处理成功(未回写信贷)");
            } else {
                boolean writebackResult = false;
                if (detl.getBilltype().equals(BillType.NORMAL.getCode())) { //正常还款
                    T100102RequestRecord recordT102 = new T100102RequestRecord();
                    recordT102.setStdjjh(detl.getIouno());
                    recordT102.setStdqch(detl.getPoano());
                    recordT102.setStdjhkkr(detl.getPaybackdate());
                    //1-成功 2-失败
                    recordT102.setStdkkjg("2");
                    T100102RequestList t100102list = new T100102RequestList();
                    t100102list.add(recordT102);
                    //单笔发送处理
                    writebackResult = t100102ctl.start(t100102list);


                    Date date = new Date();
                    if (writebackResult) {
                        detl.setWritebackflag("1"); //已回写
                        detl.setArchiveflag("1");
                        log.setJobdesc("代扣失败记录信贷回写处理及存档处理成功");
                        count++;
                    } else {
                        //detl.setWritebackflag("0"); //未回写
                        returnMsgs.add("未进行存档处理：" + detl.getIouno() + detl.getClientname());
                        log.setJobdesc("代扣失败记录信贷回写处理失败");
                        //throw new RuntimeException("信贷回写处理失败");
                    }
                    detl.setDateCmsPut(date);

                } else if (detl.getBilltype().equals(BillType.PRECUTPAYMENT.getCode())) { //提前还款 暂不做回写处理
                    //T100104RequestRecord recordT104 = new T100104RequestRecord();
                    //recordT104.setStdjjh(detl.getIouno());
                    //recordT104.setStdqch(detl.getPoano());
                    //recordT104.setStdjhkkr(detl.getPaybackdate());
                    ////1-成功 2-失败
                    //recordT104.setStdkkjg("2");
                    //T100104RequestList t100104list = new T100104RequestList();
                    //t100104list.add(recordT104);
                    ////单笔发送处理
                    //writebackResult = t100104ctl.start(t100104list);

                    //暂不做回写处理 默认成功
                    detl.setArchiveflag("1");
                    log.setJobdesc("代扣失败记录存档处理成功");
                    count++;
                } else if (detl.getBilltype().equals(BillType.OVERDUE.getCode())) { //逾期   暂不做回写处理
                    //T100102RequestRecord recordT102 = new T100102RequestRecord();
                    //recordT102.setStdjjh(detl.getIouno());
                    //recordT102.setStdqch(detl.getPoano());
                    //recordT102.setStdjhkkr(detl.getPaybackdate());
                    ////1-成功 2-失败
                    //recordT102.setStdkkjg("2");
                    //T100102RequestList t100102list = new T100102RequestList();
                    //t100102list.add(recordT102);

                    //暂不做回写处理 默认成功
                    detl.setArchiveflag("1");
                    log.setJobdesc("代扣失败记录存档处理成功");
                    count++;
                } else {
                    returnMsgs.add("回写信贷系统出错，帐单类型不支持：" + detl.getIouno() + detl.getClientname());
                    throw new RuntimeException("回写信贷系统出错，帐单类型不支持");
                }
            }


            fipCutpaydetlMapper.updateByPrimaryKey(detl);

            //工作日志处理
            String userid = SystemService.getOperatorManager().getOperatorId();
            String username = SystemService.getOperatorManager().getOperatorName();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(detl.getPkid());
            log.setJobname("代扣失败记录信贷回写及归档处理");
            log.setJobtime(new Date());
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
        return count;
    }

    /**
     * 根据申请单号 获取 CMS系统中维护的 商户信息
     *
     * @return
     */
    @Transactional
    public void obtainMerchantActnoFromCms(List<FipCutpaydetl> cutpaydetlList) {
        T201003CTL ctl = new T201003CTL();

        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog log = new FipJoblog();

        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            cutpaydetl.getPkid();
            Map<String, String> rtnMap = ctl.startQry(cutpaydetl.getAppno());
            if (StringUtils.isEmpty(rtnMap.get("stdkkzh"))) {
                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(cutpaydetl.getPkid());
                log.setJobname("获取商户信息");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                log.setJobdesc("获取商户信息错误" + rtnMap.get("stdcwxx"));
                fipJoblogMapper.insert(log);
                continue;
                //throw new RuntimeException(rtnMap.get("stdcwxx"));
            }

            FipCutpaydetl cutpaydetlDB = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid());
            Long recversion = cutpaydetl.getRecversion();
            if (cutpaydetlDB.getRecversion().equals(recversion)) {
                cutpaydetl.setMerchantName(rtnMap.get("stdshmc"));
                cutpaydetl.setMerchantActno(rtnMap.get("stdkkzh"));
                cutpaydetl.setRecversion(++recversion);
                fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);

                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(cutpaydetl.getPkid());
                log.setJobname("获取商户信息");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                log.setJobdesc("获取商户信息成功" + cutpaydetl.getMerchantName() + cutpaydetl.getMerchantActno());
                fipJoblogMapper.insert(log);
            } else {
                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(cutpaydetl.getPkid());
                log.setJobname("获取商户信息");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                log.setJobdesc("获取商户信息失败，并发冲突." + cutpaydetl.getMerchantName() + cutpaydetl.getMerchantActno());
                fipJoblogMapper.insert(log);
            }
        }
    }

    //逾期解锁并记录操作日志
    //事务独立（在本service外部调用时才起作用）
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean unlockIntr4Overdue(FipCutpaydetl detl) {
        boolean unlockResult = lockOrUnlockIntr4Overdue(detl.getIouno(), "2");

        Date date = new Date();
        FipJoblog log = new FipJoblog();
        if (unlockResult) {
            log.setJobdesc("信贷利息解锁处理成功.");
        } else {
            detl.setBillstatus(BillStatus.CMS_FAILED.getCode());
            log.setJobdesc("信贷利息解锁处理失败.");
            logger.error("利息解锁错误：" + detl.getIouno() + detl.getClientname());
        }

        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        log.setTablename("fip_cutpaydetl");
        log.setRowpkid(detl.getPkid());
        log.setJobname("信贷利息解锁处理");
        log.setJobtime(date);
        log.setJobuserid(userid);
        log.setJobusername(username);
        fipJoblogMapper.insert(log);
        return unlockResult;
    }
    //==================================20121120  罚息自动加解锁处理==================================


    /**
     * 加锁解锁逾期罚息
     *
     * @param iouno  借据号
     * @param option 1-加锁 2-解锁
     * @return 成功笔数
     */
    private boolean lockOrUnlockIntr4Overdue(String iouno, String option) {
        if ((!"1".equals(option))
                && (!"2".equals(option))
                ) {
            throw new RuntimeException("参数错误！");
        }

        T100108CTL t100108ctl = new T100108CTL();
        T100108RequestRecord record = new T100108RequestRecord();
        record.setStdjjh(iouno);
        //1-加锁 2-解锁
        record.setStdkkjg(option);
        T100108RequestList list = new T100108RequestList();
        list.add(record);
        //单笔发送处理
        return t100108ctl.start(list);
    }
}
