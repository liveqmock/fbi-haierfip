package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.txn.Ta543.Ta543Handler;
import fip.gateway.sbs.txn.Ta543.Ta543SOFDataDetail;
import fip.gateway.sbs.txn.Taa56.Taa56Handler;
import fip.gateway.sbs.txn.Taa56.Taa56SOFDataDetail;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipInterbankinfoMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.XfapprepaymentMapper;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipInterbankinfo;
import fip.repository.model.FipInterbankinfoExample;
import fip.repository.model.FipJoblog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-17
 * Time: 上午11:23
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SbsSevice {
    private static final Logger logger = LoggerFactory.getLogger(SbsSevice.class);

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;
    @Autowired
    private XfapprepaymentMapper xfapprepaymentMapper;
    @Autowired
    private FipInterbankinfoMapper fipInterbankinfoMapper;


    /**
     * 入帐前进行金额核对
     * @param cutpaydetlList
     */
    public void checkAmt4PendAccountRecord(List<FipCutpaydetl> cutpaydetlList) {
        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            BigDecimal totalamt = cutpaydetl.getPaybackamt();
            //TODO 暂时不核对罚息数据 (还要把所有入帐的金额字段加上)
            BigDecimal accountAmt = cutpaydetl.getPrincipalamt().add(cutpaydetl.getInterestamt())
                    .add(cutpaydetl.getPunitiveintamt()).add(cutpaydetl.getCompoundintamt());
            //BigDecimal accountAmt = cutpaydetl.getPrincipalamt().add(cutpaydetl.getInterestamt());
            if (totalamt.subtract(accountAmt).floatValue() != 0 ) {
                logger.error("入帐的总金额与明细不符（本金+利息+罚息+复利）" + cutpaydetl.getClientname());
                throw new RuntimeException("入帐的总金额与明细不符（本金+利息+罚息+复利）" + cutpaydetl.getClientname());
            }
        }

    }

    //@Transactional  入帐时不做整体事务处理
    public void accountCutPayRecord2SBS(List<FipCutpaydetl> cutpaydetlList) {

        //TODO 检查金额
        checkAmt4PendAccountRecord(cutpaydetlList);

        //TODO 检查状态

        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog joblog = new FipJoblog();

        //处理入帐
        Ta543Handler handler = new Ta543Handler();

        try {
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                List<String> txnparamList = assembleTa543Param(cutpaydetl);

                SBSRequest request = new SBSRequest("a543", txnparamList);
                SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
                Ta543SOFDataDetail sofDataDetail = new Ta543SOFDataDetail();
                response.setSofDataDetail(sofDataDetail);

                handler.run(request, response);

                String formcode = response.getFormcode();
                logger.debug("formcode:" + formcode);

                if (!formcode.equals("T531")) {     //异常情况处理
                    cutpaydetl.setBillstatus(BillStatus.ACCOUNT_FAILED.getCode());
                    joblog.setJobdesc("SBS入帐失败：FORMCODE=" + formcode);
                } else {
                    if (sofDataDetail.getSECNUM().trim().equals(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn())) {
                        joblog.setJobdesc("SBS入帐成功：FORMCODE=" + formcode + " 同业帐号:" + cutpaydetl.getSbsInterbankActno());
                        cutpaydetl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                        cutpaydetl.setDateSbsAct(new Date());
                    } else {
                        logger.error("SBS入帐成功,但返回的流水号出错，请查询。" + cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());
                        joblog.setJobdesc("SBS入帐成功,但返回的流水号出错，请查询。" + sofDataDetail.getSECNUM());
                        cutpaydetl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                        cutpaydetl.setDateSbsAct(new Date());
                    }
                }
                joblog.setTablename("fip_cutpaydetl");
                joblog.setRowpkid(cutpaydetl.getPkid());
                joblog.setJobname("SBS记帐");
                joblog.setJobtime(new Date());
                joblog.setJobuserid(userid);
                joblog.setJobusername(username);
                fipJoblogMapper.insert(joblog);
                fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }
        } catch (Exception e) {
            logger.error("入帐时出现错误。", e);
            throw new RuntimeException("入帐时出现错误。", e);
        } finally {
            handler.shoudown();
        }
    }

    /**
     * 信贷扣款SBS入帐交易TIA报文  a543
     *
     * @param cutpaydetl
     * @return
     */
    private List<String> assembleTa543Param(FipCutpaydetl cutpaydetl) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();

        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String interbankAccount = getInterbankAccount(cutpaydetl);
        cutpaydetl.setSbsInterbankActno(interbankAccount);

        //1 交易日期
        txnparamList.add(txndate);

        //2 外围系统流水号
        txnparamList.add(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());

        //3 贷款账号
        txnparamList.add(cutpaydetl.getClientact());

        //20120503 zhanrui  对于逾期贷款 罚息复利暂时归到利息中
        if (cutpaydetl.getOriginBizid().equals(BizType.FD.getCode())) {   //住房按揭
            //4 本金金额   S9(13).99
            String amt = df.format(cutpaydetl.getPrincipalamt());
            txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));
            //5 违约金金额（利息/应收息）  对于逾期贷款 罚息复利暂时归到利息中
            String interestamt = df.format(cutpaydetl.getInterestamt()
                    .add(cutpaydetl.getPunitiveintamt())
                    .add(cutpaydetl.getCompoundintamt()));
            txnparamList.add("+" + StringUtils.leftPad(interestamt, 16, '0'));
            //6 滞纳金金额（罚息）
            txnparamList.add("+" + StringUtils.leftPad("0.00", 16, '0'));
            //7 手续费金额（复利）
            txnparamList.add("+" + StringUtils.leftPad("0.00", 16, '0'));
            //8 摘要
            txnparamList.add(StringUtils.leftPad("FangDai", 30, ' '));
            //9 本金还款账号  输入还款结算账号或银行同业账号
            txnparamList.add(interbankAccount); //同业账号
            //10 贷款种类 (04对私消费贷款 05对私按揭贷款)
            txnparamList.add("05");
        } else if (cutpaydetl.getOriginBizid().equals(BizType.XF.getCode())) {   //旧消费信贷
            //4 本金金额   S9(13).99
            String amt = df.format(cutpaydetl.getPrincipalamt());
            txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));
            //5 违约金金额（利息/应收息）
            txnparamList.add("+" + StringUtils.leftPad("0.00", 16, '0'));
            //6 滞纳金金额（罚息）
            txnparamList.add("+" + StringUtils.leftPad("0.00", 16, '0'));
            //7 手续费金额（复利）
            String interestamt = df.format(cutpaydetl.getInterestamt()
                    .add(cutpaydetl.getPunitiveintamt())
                    .add(cutpaydetl.getCompoundintamt()));
            txnparamList.add("+" + StringUtils.leftPad(interestamt, 16, '0'));
            //8 摘要
            txnparamList.add(StringUtils.leftPad("XiaoFei", 30, ' '));
            //9 本金还款账号  输入还款结算账号或银行同业账号
            txnparamList.add(interbankAccount); //同业账号
            //10 贷款种类 04对私消费贷款 05对私按揭贷款
            txnparamList.add("04");
        } else {
            throw new RuntimeException("不支持的业务品种。");
        }

        //11 利息还款账号  输入还款结算账号或银行同业账号
        txnparamList.add(interbankAccount); //同业账号

        return txnparamList;
    }


    @Deprecated
    private List<String> assembleTa541Param(FipCutpaydetl cutpaydetl) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();

        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        txnparamList.add(txndate);          //交易日期
        txnparamList.add(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());      //交易流水号

        String account = StringUtils.rightPad(cutpaydetl.getClientact(), 22, ' ');
        txnparamList.add(account);//贷款帐户                 22位，不足补空格


        String principal = df.format(cutpaydetl.getPrincipalamt());
        String interest = df.format(cutpaydetl.getInterestamt());
        //String punitiveint = df.format(cutpaydetl.getPunitiveintamt());  //滞纳金 = 罚息

        if (cutpaydetl.getBilltype().equals("0")) { //正常帐单
            txnparamList.add("+" + StringUtils.leftPad(principal, 16, '0'));     //本金金额
            txnparamList.add("+0000000000000.00");     //违约金金额
            txnparamList.add("+0000000000000.00");     //滞纳金金额
            txnparamList.add("+" + StringUtils.leftPad(interest, 16, '0'));     //手续费金额
        } else {
            //TODO
        }
        //TODO: 参数化
        String digest = "   ";

        CutpayChannel channel = CutpayChannel.valueOfAlias(cutpaydetl.getBiChannel());
        //房贷业务
        if (BizType.FD.getCode().equals(cutpaydetl.getOriginBizid())) {
            switch (channel) {
                case NONE:
                    digest = cutpaydetl.getBiActopeningbank();
                    break;
                case UNIPAY:
                    digest = "905"; //银联代扣
                    break;
                default:
            }
        }
        if (BizType.XF.getCode().equals(cutpaydetl.getOriginBizid())) {
            switch (channel) {
                case NONE:
                    digest = cutpaydetl.getBiActopeningbank();
                    break;
                case UNIPAY:
                    digest = "905"; //银联代扣
                    break;
                default:
            }
        }

        txnparamList.add(digest + "                           ");//摘要
        return txnparamList;
    }


    /**
     * 消费分期 首付入帐  aa56交易  (用于同业帐户与商户帐户之间的转账)
     *
     * @param cutpaydetlList
     */
    //@Transactional
    public void accountPrepayRecord2SBS(List<FipCutpaydetl> cutpaydetlList) {
        //TODO 检查金额
        checkAmt4PendAccountRecord(cutpaydetlList);

        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog joblog = new FipJoblog();

        //处理入帐
        Taa56Handler handler = new Taa56Handler();

        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            List<String> txnparamList = assembleTaa56Param(cutpaydetl);

            SBSRequest request = new SBSRequest("aa56", txnparamList);
            SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
            Taa56SOFDataDetail sofDataDetail = new Taa56SOFDataDetail();
            response.setSofDataDetail(sofDataDetail);

            handler.run(request, response);

            String formcode = response.getFormcode();
            logger.debug("formcode:" + formcode);
            if (!formcode.equals("T531")) {     //异常情况处理
                cutpaydetl.setBillstatus(BillStatus.ACCOUNT_FAILED.getCode());
                joblog.setJobdesc("SBS入帐失败：FORMCODE=" + formcode);
            } else {
                if (sofDataDetail.getSECNUM().trim().equals(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn())) {
                    cutpaydetl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                    cutpaydetl.setDateSbsAct(new Date());
                    joblog.setJobdesc("SBS入帐成功：FORMCODE=" + formcode + " 同业帐号:" + cutpaydetl.getSbsInterbankActno());
                } else {
                    logger.error("SBS入帐成功,但返回的流水号出错，请查询。" + cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());
                    joblog.setJobdesc("SBS入帐成功,但返回的流水号出错，请查询。" + sofDataDetail.getSECNUM());
                    cutpaydetl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                    cutpaydetl.setDateSbsAct(new Date());
                }
            }
            joblog.setTablename("fip_cutpaydetl");
            joblog.setRowpkid(cutpaydetl.getPkid());
            joblog.setJobname("SBS记帐");
            joblog.setJobtime(new Date());
            joblog.setJobuserid(userid);
            joblog.setJobusername(username);
            fipJoblogMapper.insert(joblog);
            fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
        }
    }

    private List<String> assembleTaa56Param(FipCutpaydetl cutpaydetl) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();

        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());


        //转出帐户类型
        txnparamList.add("01");

        String interbankAccount = getInterbankAccount(cutpaydetl);
        cutpaydetl.setSbsInterbankActno(interbankAccount);

        //转出帐户
        txnparamList.add(interbankAccount); //同业账号
        //取款方式
        txnparamList.add("3");
        //转出帐户户名
        txnparamList.add(StringUtils.leftPad("", 72, ' '));
        //取款密码
        txnparamList.add(StringUtils.leftPad("", 6, ' '));

        //证件类型
        txnparamList.add("N");
        //外围系统流水号
        txnparamList.add(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());      //交易流水号
        //支票种类
        txnparamList.add(" ");
        //支票号
        txnparamList.add(StringUtils.leftPad("", 10, ' '));
        //支票密码
        txnparamList.add(StringUtils.leftPad("", 12, ' '));

        //签发日期
        txnparamList.add(StringUtils.leftPad("", 8, ' '));
        //无折标识
        txnparamList.add("3");
        //备用字段
        txnparamList.add(StringUtils.leftPad("", 8, ' '));
        //备用字段
        txnparamList.add(StringUtils.leftPad("", 4, ' '));

        //交易金额
        String amt = df.format(cutpaydetl.getPaybackamt());
        txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));   //金额

        //转入帐户类型
        txnparamList.add("01");

        //转入帐户 (商户帐号)
        String account = StringUtils.rightPad(cutpaydetl.getMerchantActno(), 22, ' ');
        txnparamList.add(account);

        //转入帐户户名
        txnparamList.add(StringUtils.leftPad("", 72, ' '));
        //无折标识
        txnparamList.add(" ");
        //交易日期
        txnparamList.add(txndate);

        //摘要
        txnparamList.add(StringUtils.leftPad("", 30, ' '));
        //产品码
        txnparamList.add(StringUtils.leftPad("", 4, ' '));
        //MAGFL1
        txnparamList.add(" ");
        //MAGFL2
        txnparamList.add(" ");
        //交易种类
        txnparamList.add("   ");
        return txnparamList;
    }

    /**
     * 根据每笔代扣记录中的代扣信息 获取同业帐号信息  重要！！
     *
     * @param cutpaydetl
     * @return
     */
    private String getInterbankAccount(final FipCutpaydetl cutpaydetl) {
        FipInterbankinfoExample interbankinfoExample = new FipInterbankinfoExample();
        if (cutpaydetl.getBiChannel().equals(CutpayChannel.NONE.getCode())) {//不通过银联等支付渠道处理时
            interbankinfoExample.createCriteria()
                    .andBizidEqualTo(cutpaydetl.getOriginBizid())
                    .andChannelidEqualTo(cutpaydetl.getBiChannel())
                    .andBankidEqualTo(cutpaydetl.getBiActopeningbank());
            List<FipInterbankinfo> infos = fipInterbankinfoMapper.selectByExample(interbankinfoExample);
            if (infos.size() != 1) {
                throw new RuntimeException("同业帐号查找错误!");
            }
            return infos.get(0).getActno();
        } else {//渠道不为00时（如通过银联支付宝等进行代扣） 入帐信息与具体银行无关
            interbankinfoExample.createCriteria()
                    .andBizidEqualTo(cutpaydetl.getOriginBizid())
                    .andChannelidEqualTo(cutpaydetl.getBiChannel());
            List<FipInterbankinfo> infos = fipInterbankinfoMapper.selectByExample(interbankinfoExample);
            if (infos.size() != 1) {
                throw new RuntimeException("同业帐号查找错误!");
            }
            return infos.get(0).getActno();
        }
    }
}
