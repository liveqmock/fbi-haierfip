package fip.service.fip;

import fip.common.constant.BillStatus;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipJoblog;
import fip.repository.model.FipJoblogExample;
import fip.repository.model.FipRefunddetl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-14
 * Time: 下午3:40
 * To change this template use File | Settings | File Templates.
 */
@Service
public class JobLogService {
    @Autowired
    private FipJoblogMapper fipJoblogMapper;

    @Autowired
    FipCutpaydetlMapper cutpaydetlMapper;

    @Autowired
    FipRefunddetlMapper refunddetlMapper;

    @Autowired
    FipCutpaybatMapper cutpaybatMapper;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void insertNewJoblog(String pkid, String tableName, String jobname, String jobdesc, String userid, String username) {
        FipJoblog log = new FipJoblog();
        log.setRowpkid(pkid);
        log.setTablename(tableName);
        log.setJobname(jobname);
        log.setJobdesc(jobdesc);
        log.setJobtime(new Date());
        log.setJobuserid(userid);
        log.setJobusername(username);
        fipJoblogMapper.insert(log);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkAndUpdateRecversion(FipCutpaydetl record) {
        FipCutpaydetl originRecord = cutpaydetlMapper.selectByPrimaryKey(record.getPkid());
        if (originRecord.getRecversion().compareTo(record.getRecversion()) != 0) {
            throw new RuntimeException("并发更新冲突,UUID=" + record.getPkid());
        } else {
            record.setRecversion(record.getRecversion() + 1);
            //重要！
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            cutpaydetlMapper.updateByPrimaryKey(record);
        }
    }
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkAndUpdateRecversion4Refund(FipRefunddetl record) {
        FipRefunddetl originRecord = refunddetlMapper.selectByPrimaryKey(record.getPkid());
        if (originRecord.getRecversion().compareTo(record.getRecversion()) != 0) {
            throw new RuntimeException("并发更新冲突,UUID=" + record.getPkid());
        } else {
            record.setRecversion(record.getRecversion() + 1);
            //重要！
            record.setBillstatus(BillStatus.CUTPAY_QRY_PEND.getCode());
            refunddetlMapper.updateByPrimaryKey(record);
        }
    }

    public List<FipJoblog> selectJobLogsByOriginPkid(String tablename, String rowpkid) {
        FipJoblogExample example = new FipJoblogExample();
        example.createCriteria().andTablenameEqualTo(tablename).andRowpkidEqualTo(rowpkid);
        example.setOrderByClause("jobtime desc");
        return fipJoblogMapper.selectByExample(example);
    }

}
