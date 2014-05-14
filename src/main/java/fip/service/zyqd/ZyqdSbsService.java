package fip.service.zyqd;

import fip.common.SystemService;
import fip.gateway.sbs.txn.Taa56.Taa56Handler;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.model.FipJoblog;
import fip.repository.model.fip.UnipayQryResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanrui on 14-1-17.
 */

@Service
public class ZyqdSbsService {
    private static final Logger logger = LoggerFactory.getLogger(ZyqdSbsService.class);

    @Autowired
    private FipJoblogMapper fipJoblogMapper;


    /**
     * 入帐  aa56交易  (用于同业帐户与商户帐户之间的转账)
     * 一笔一提交， 不做数据库事务处理
     */
    public void account_aa56(List<UnipayQryResult> detlList, String outActno, String inActno) {
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog joblog = new FipJoblog();

        //处理入帐
        Taa56Handler handler = new Taa56Handler();

/*
        for (UnipayQryResult detl : detlList) {
            List<String> txnparamList = assemble_aa56(detl, outActno, inActno);

            SBSRequest request = new SBSRequest("aa56", txnparamList);
            SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
            Taa56SOFDataDetail sofDataDetail = new Taa56SOFDataDetail();
            response.setSofDataDetail(sofDataDetail);

            handler.run(request, response);

            String formcode = response.getFormcode();
            logger.debug("formcode:" + formcode);
            if (!formcode.equals("T531")) {     //异常情况处理
                detl.setBillstatus(BillStatus.ACCOUNT_FAILED.getCode());
                joblog.setJobdesc("SBS入帐失败：FORMCODE=" + formcode);
            } else {
                if (sofDataDetail.getSECNUM().trim().equals(detl.getBatchSn() + detl.getBatchDetlSn())) {
                    detl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                    detl.setDateSbsAct(new Date());
                    joblog.setJobdesc("SBS入帐成功：FORMCODE=" + formcode + " 同业帐号:" + detl.getSbsInterbankActno());
                } else {
                    logger.error("SBS入帐成功,但返回的流水号出错，请查询。" + detl.getBatchSn() + detl.getBatchDetlSn());
                    joblog.setJobdesc("SBS入帐成功,但返回的流水号出错，请查询。" + sofDataDetail.getSECNUM());
                    detl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                    detl.setDateSbsAct(new Date());
                }
            }
            joblog.setTablename("fip_cutpaydetl");
            joblog.setRowpkid(detl.getPkid());
            joblog.setJobname("SBS记帐");
            joblog.setJobtime(new Date());
            joblog.setJobuserid(userid);
            joblog.setJobusername(username);
            fipJoblogMapper.insert(joblog);
            fipCutpaydetlMapper.updateByPrimaryKey(detl);
        }
*/
    }

    private List<String> assemble_aa56(UnipayQryResult cutpaydetl, String outActno, String inActno) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();

        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //转出帐户类型
        txnparamList.add("01");

        //转出帐户
        txnparamList.add(outActno); //同业账号

        //取款方式
        txnparamList.add("3");
        //转出帐户户名
        txnparamList.add(StringUtils.leftPad("", 72, ' '));
        //取款密码
        txnparamList.add(StringUtils.leftPad("", 6, ' '));
        //证件类型
        txnparamList.add("N");

        //外围系统流水号
        txnparamList.add(cutpaydetl.getSN().substring(4));      //交易流水号, 去掉前四位！！

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
        String amt = df.format(cutpaydetl.getAMOUNT());
        txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));   //金额

        //转入帐户类型
        txnparamList.add("01");

        //转入帐户 (商户帐号)
        String account = StringUtils.rightPad(inActno, 22, ' ');
        txnparamList.add(account);

        //转入帐户户名
        txnparamList.add(StringUtils.leftPad("", 72, ' '));
        //无折标识
        txnparamList.add(" ");
        //交易日期
        txnparamList.add(txndate);

        //摘要(流水号)
        txnparamList.add(StringUtils.leftPad(cutpaydetl.getSN(), 30, ' '));
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

}
