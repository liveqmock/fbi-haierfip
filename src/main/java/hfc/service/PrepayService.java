package hfc.service;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.XfappMapper;
import fip.repository.dao.XfapprepaymentMapper;
import fip.repository.model.*;
import fip.service.fip.BillManagerService;
import hfc.common.AppPrepayStatus;
import hfc.common.AppStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-27
 * Time: 下午4:31
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PrepayService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private BillManagerService billManagerService;
    @Autowired
    private XfappMapper xfappmapper;

    @Autowired
    private FipJoblogMapper fipJoblogMapper;
    @Autowired
    private XfapprepaymentMapper xfapprepaymentMapper;
    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;


    public List<Xfapp> selectPendCutpayList(AppStatus appStatus, AppPrepayStatus prepayStatus) {
        XfappExample example = new XfappExample();
        example.createCriteria()
                .andAppstatusEqualTo(appStatus.getCode())
                .andPrepayCutpayTypeEqualTo("1")
                .andPrepayStatusEqualTo(prepayStatus.getCode());
        return xfappmapper.selectByExample(example);
    }
    public List<Xfapp> selectPendCutpayList(AppPrepayStatus status) {
        XfappExample example = new XfappExample();
        example.createCriteria()
                .andPrepayStatusEqualTo(status.getCode())
                .andPrepayCutpayTypeEqualTo("1");
        return xfappmapper.selectByExample(example);
    }

    public void updateXfappPrepayStatus(String pkid, AppPrepayStatus status){
        Xfapp record = xfappmapper.selectByPrimaryKey(pkid);
        record.setPrepayStatus(status.getCode());

        xfappmapper.updateByPrimaryKey(record);
    }

    /**
     * 根据代扣的结果更新 申请单中 首付的状态
     *
     * @param appList
     */
    @Transactional
    public void updateXfappPrepayCutpayStatusToSuccess(List<Xfapp> appList) {
        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();

        for (Xfapp xfapp : appList) {
            xfapp.setPrepayStatus("1");
            xfappmapper.updateByPrimaryKey(xfapp);
            FipJoblog log = new FipJoblog();
            log.setTablename("xfapp");
            log.setRowpkid(xfapp.getPkid());
            log.setJobname("首付代扣");
            log.setJobdesc("首付代扣成功");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }

    }

    public void generateCutpayRecords(List<Xfapp> appList) {
        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;

        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();

        for (Xfapp xfapp : appList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(BizType.XFSF.getCode(),
                    batchno, iSeqno, BillType.NORMAL.getCode(), xfapp);
            if (cutpaydetl == null) {
                continue;
            }
            fipCutpaydetlMapper.insert(cutpaydetl);
            //更新XFAPP表状态
            updateXfappPrepayStatus(xfapp.getPkid(), AppPrepayStatus.CUTPAY_RECORD_GENERATED);

            FipJoblog log = new FipJoblog();
            log.setTablename("xfapp");
            log.setRowpkid(xfapp.getPkid());
            log.setJobname("首付代扣");
            log.setJobdesc("生成首付代扣记录");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }

    private FipCutpaydetl assembleCutpayRecord(String bizID,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               String billType,
                                               Xfapp appRecord) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setOriginBizid(bizID);
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        cutpaydetl.setXfappPkid(appRecord.getPkid()); //申请单流水号
        cutpaydetl.setAppno(appRecord.getAppno()); //申请单号

        cutpaydetl.setIouno("000000"); //借据号
        cutpaydetl.setPoano("00000");  //期次号
        cutpaydetl.setContractno("000000"); //合同号
        cutpaydetl.setPaybackdate(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); //计划还款日

        //客户信息
        cutpaydetl.setClientno(appRecord.getClientno().toString());
        cutpaydetl.setClientname(appRecord.getName());

        //SBS开户信息
        cutpaydetl.setClientact("111111");   //贷款帐号

        //还款金额信息
        cutpaydetl.setPaybackamt(appRecord.getReceiveamt());  //还款金额
        cutpaydetl.setPrincipalamt(new BigDecimal(0)); //还款本金
        cutpaydetl.setInterestamt(new BigDecimal(0));  //还款利息
        cutpaydetl.setPunitiveintamt(new BigDecimal(0));//罚息金额
        cutpaydetl.setReserveamt(new BigDecimal(0));  //冗余金额

        //还款渠道信息
        Xfapprepayment xfapprepayment = xfapprepaymentMapper.selectByPrimaryKey(cutpaydetl.getAppno());

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

        //其他
        cutpaydetl.setRecversion((long) 0);
        cutpaydetl.setDeletedflag("0");
        cutpaydetl.setArchiveflag("0");
        //帐单状态
        cutpaydetl.setBillstatus(BillStatus.INIT.getCode());
        cutpaydetl.setSendflag("0");

        //帐单类型
        cutpaydetl.setBilltype(billType);

        return cutpaydetl;
    }


}
