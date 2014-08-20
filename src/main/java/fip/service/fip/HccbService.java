package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipCutpaydetlExample;
import fip.repository.model.FipJoblog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.security.OperatorManager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * HCCB 小额信贷.
 * User: zhanrui
 * Date: 2014-08-01
 */
@Service
public class HccbService {
    private static final Logger logger = LoggerFactory.getLogger(HccbService.class);

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipRefunddetlMapper fipRefunddetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;

    @Transactional
    public int importDataFromXls(BizType bizType, List<FipCutpaydetl> cutpaydetls, List<String> returnMsgs) {

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (FipCutpaydetl cutpaydetl : cutpaydetls) {
            iSeqno++;
            assembleCutpayRecord(bizType, batchno, iSeqno, cutpaydetl);

            //TODO 判断业务主键是否重复   注意 修改IOUNO长度时需要同步修改commonmapper中的SQL
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Hccb(cutpaydetl.getIouno(), cutpaydetl.getPoano());
            if (isNotRepeated) {
                fipCutpaydetlMapper.insert(cutpaydetl);
                count++;
            } else {
                returnMsgs.add("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.error("重复记录：" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //日志
        batchInsertLogByBatchno(batchno);
        return count;
    }

    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               FipCutpaydetl cutpaydetl) {
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        if (StringUtils.isEmpty(cutpaydetl.getPoano())) {
           cutpaydetl.setPoano("0");
        }

        //还款金额信息
        cutpaydetl.setPrincipalamt(new BigDecimal("0.00")); //还款本金
        cutpaydetl.setInterestamt(new BigDecimal("0.00"));  //还款利息
        cutpaydetl.setPunitiveintamt(new BigDecimal("0.00"));//罚息金额
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//违约金金额
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//罚息复利金额
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //冗余金额

        //还款渠道信息
        if (bizType.equals(BizType.HCCB)) {
            cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //默认为银联
        } else {
            throw new RuntimeException("非HCCB数据，不能处理");
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

        //zhanrui 20120305  标识消费信贷数据来源自信贷系统 便与回写时区分来源系统
        cutpaydetl.setRemark3("HCCB");
        cutpaydetl.setDateCmsGet(new Date());

        //其它
        cutpaydetl.setBilltype(BillType.NORMAL.getCode());
        cutpaydetl.setClientact("123456"); //不能为空
        return cutpaydetl;
    }

    //============================================


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

        OperatorManager operatorManager = SystemService.getOperatorManager();
        String userid;
        String username;
        if (operatorManager == null) {
            userid = "9999";
            username = "BATCH";
        }else{
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }

        for (FipCutpaydetl fipCutpaydetl : fipCutpaydetlList) {
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(fipCutpaydetl.getPkid());
            log.setJobname("新建记录");
            log.setJobdesc("新获取新消费信贷系统代扣记录");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }

}
