package fip.service.fip;

import fip.common.constant.DeletedFlag;
import fip.repository.dao.FipPayoutSbsactsMapper;
import fip.repository.model.FipPayoutSbsactsExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-5-22
 * Time: ÏÂÎç5:23
 * To change this template use File | Settings | File Templates.
 */
@Service
@Deprecated

public class PayoutSbsactsService {

    @Autowired
    private FipPayoutSbsactsMapper fipPayoutSbsactsMapper;

    public boolean isExist(String wsysid, String txnCode, String wsysUserid, String sbsActno) {
        return qrySbsActCnt(wsysid, txnCode, wsysUserid, sbsActno) >= 1;
    }

    public boolean isExist(String wsysid, String txnCode, String sbsActno) {
        return qrySbsActCnt(wsysid, txnCode, sbsActno) >= 1;
    }

    public int qrySbsActCnt(String wsysid, String txnCode, String wsysUserid, String sbsActno) {
        FipPayoutSbsactsExample example = new FipPayoutSbsactsExample();
        example.createCriteria().andDeleteFlagEqualTo(DeletedFlag.UNDELETED.getCode())
                .andWsysIdEqualTo(wsysid).andTxnCodeEqualTo(txnCode).andWsysUseridEqualTo(wsysUserid)
                .andSbsActnoEqualTo(sbsActno);
        return fipPayoutSbsactsMapper.countByExample(example);
    }

    public int qrySbsActCnt(String wsysid, String txnCode, String sbsActno) {
        FipPayoutSbsactsExample example = new FipPayoutSbsactsExample();
        example.createCriteria().andDeleteFlagEqualTo(DeletedFlag.UNDELETED.getCode())
                .andWsysIdEqualTo(wsysid).andTxnCodeEqualTo(txnCode).andSbsActnoEqualTo(sbsActno);
        return fipPayoutSbsactsMapper.countByExample(example);
    }
}
