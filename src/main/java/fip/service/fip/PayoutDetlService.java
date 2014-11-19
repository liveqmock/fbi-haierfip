package fip.service.fip;

import fip.common.constant.DeletedFlag;
import fip.common.constant.PayoutDetlRtnCode;
import fip.common.constant.PayoutDetlTxnStep;
import fip.repository.dao.FipPayoutdetlMapper;
import fip.repository.model.FipPayoutdetl;
import fip.repository.model.FipPayoutdetlExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-4-2
 * Time: ����10:58
 * To change this template use File | Settings | File Templates.
 */
@Service
@Deprecated

public class PayoutDetlService {

    @Autowired
    private FipPayoutdetlMapper fipPayoutdetlMapper;

    public int saveRecord(FipPayoutdetl record) {
        record.setRetCode(PayoutDetlRtnCode.HALFWAY.getCode());
        record.setErrMsg(PayoutDetlRtnCode.HALFWAY.getTitle());
        record.setTrxStep(PayoutDetlTxnStep.INIT.getCode());
        record.setCreateTime(new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date()));
        record.setDeleteFlag(DeletedFlag.UNDELETED.getCode());
        return fipPayoutdetlMapper.insert(record);
    }

    public int saveRecords(List<FipPayoutdetl> records) {
        int cnt = 0;
        for (FipPayoutdetl record : records) {
            cnt += saveRecord(record);
        }
        return cnt;
    }

    public List<FipPayoutdetl> qryRecordsBySn(String reqsn) {
        FipPayoutdetlExample example = new FipPayoutdetlExample();
        example.createCriteria().andReqSnEqualTo(reqsn).andDeleteFlagEqualTo(DeletedFlag.UNDELETED.getCode());
        return fipPayoutdetlMapper.selectByExample(example);
    }

    public List<FipPayoutdetl> qryRecords(String reqsn, PayoutDetlRtnCode retCode, PayoutDetlTxnStep txnStep) {
        FipPayoutdetlExample example = new FipPayoutdetlExample();
        example.createCriteria().andReqSnEqualTo(reqsn).andDeleteFlagEqualTo(DeletedFlag.UNDELETED.getCode())
        .andRetCodeEqualTo(retCode.getCode()).andTrxStepEqualTo(txnStep.getCode());
        return fipPayoutdetlMapper.selectByExample(example);
    }

    // ��齻��״̬��ͻ
    public boolean isNoTxnStepClash(FipPayoutdetl record) {
        FipPayoutdetl origRecord = fipPayoutdetlMapper.selectByPrimaryKey(record.getPkid());
        if (!origRecord.getTrxStep().equals(record.getTrxStep())) {
            throw new RuntimeException("�������³�ͻ����ˮ�ţ�" + record.getReqSn() + record.getSn()
                    + " ԭ״̬��" + record.getTrxStep()
                    + " �Ѹ���Ϊ��" + origRecord.getTrxStep());
        } else {
            return true;
        }
    }

    public int updatePayoutDetlTxnStep(FipPayoutdetl record, PayoutDetlTxnStep txnStep) {
        if (isNoTxnStepClash(record)) {
            record.setTrxStep(txnStep.getCode());
            return fipPayoutdetlMapper.updateByPrimaryKey(record);
        } else return -1;
    }
}
