package fip.service.fip;

import fip.common.constant.DeletedFlag;
import fip.common.constant.PayoutBatRtnCode;
import fip.common.constant.PayoutBatTxnStep;
import fip.repository.dao.FipPayoutbatMapper;
import fip.repository.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-4-2
 * Time: 上午10:58
 * To change this template use File | Settings | File Templates.
 */
@Service
public class PayoutbatService {

    @Autowired
    private FipPayoutbatMapper fipPayoutbatMapper;
    @Autowired
    private PayoutDetlService payoutDetlService;

    private int qryPayoutCntByReqsn(String reqsn) {
        FipPayoutbatExample example = new FipPayoutbatExample();
        example.createCriteria().andReqSnEqualTo(reqsn);
        return fipPayoutbatMapper.countByExample(example);
    }

    public boolean isExist(String reqsn) {
        return qryPayoutCntByReqsn(reqsn) > 0;
    }

    public List<FipPayoutbat> qryRecordBySn(String reqsn) {
        FipPayoutbatExample example = new FipPayoutbatExample();
        example.createCriteria().andReqSnEqualTo(reqsn);
        return fipPayoutbatMapper.selectByExample(example);
    }

    @Transactional
    public int savePayoutTxns(FipPayoutbat record, List<FipPayoutdetl> details) {
        String createtime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        record.setTrxStep(PayoutBatTxnStep.INIT.getCode());
        record.setRetCode(PayoutBatRtnCode.TXN_HALFWAY.getCode());
        record.setErrMsg(PayoutBatRtnCode.TXN_HALFWAY.getTitle());
        // 非发往银联备注，本地数据库备注
        record.setRemark(PayoutBatTxnStep.INIT.toRtnMsg());
        record.setCreateTime(createtime);
        record.setDeleteFlag(DeletedFlag.UNDELETED.getCode());
        if (fipPayoutbatMapper.insert(record) == 1) {
            return payoutDetlService.saveRecords(details);
        } else {
            throw new RuntimeException(PayoutBatRtnCode.MSG_SAVE_ERROR.toRtnMsg());
        }
    }

    // 查询某状态的代付交易
    public List<FipPayoutbat> qryPayoutbatsByBatSts(PayoutBatRtnCode rtnCode, PayoutBatTxnStep txnStep) {
        FipPayoutbatExample example = new FipPayoutbatExample();
        example.createCriteria().andDeleteFlagEqualTo(DeletedFlag.UNDELETED.getCode())
                .andTrxStepEqualTo(txnStep.getCode()).andRetCodeEqualTo(rtnCode.getCode());
        return fipPayoutbatMapper.selectByExample(example);
    }

    // 检查交易状态冲突
    public boolean isNoTxnStepClash(FipPayoutbat record) {
        FipPayoutbat origRecord = fipPayoutbatMapper.selectByPrimaryKey(record.getPkid());
        if (!origRecord.getTrxStep().equals(record.getTrxStep())) {
            throw new RuntimeException("并发更新冲突。流水号：" + record.getReqSn()
                    + " 原状态：" + record.getTrxStep()
                    + " 已更新为：" + origRecord.getTrxStep());
        } else {
            return true;
        }
    }

    public int updatePayoutbatTxnStep(FipPayoutbat record, PayoutBatTxnStep txnStep) {
        if (isNoTxnStepClash(record)) {
            record.setTrxStep(txnStep.getCode());
            return fipPayoutbatMapper.updateByPrimaryKey(record);
        } else return -1;
    }

    public List<FipPayoutbat> qryAllPayoutbats() {
        FipPayoutbatExample example = new FipPayoutbatExample();
        example.createCriteria().andDeleteFlagEqualTo(DeletedFlag.UNDELETED.getCode());
        return fipPayoutbatMapper.selectByExample(example);
    }
}
