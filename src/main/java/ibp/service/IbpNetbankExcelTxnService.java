package ibp.service;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import ibp.repository.dao.IbpIfNetbnkTxnMapper;
import ibp.repository.model.IbpIfNetbnkTxn;
import ibp.repository.model.IbpIfNetbnkTxnExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2014-10-31.
 */
@Service
public class IbpNetbankExcelTxnService {
    private static final Logger logger = LoggerFactory.getLogger(IbpNetbankExcelTxnService.class);

    @Autowired
    private IbpIfNetbnkTxnMapper ibpIfNetbnkTxnMapper;

    public List<IbpIfNetbnkTxn> qryTxnsByNotBookFlag(BillStatus bookFlag) {
        IbpIfNetbnkTxnExample example = new IbpIfNetbnkTxnExample();
        example.createCriteria().andBookflagNotEqualTo(bookFlag.getCode());
        example.setOrderByClause(" BKSERIALNO ");
        return ibpIfNetbnkTxnMapper.selectByExample(example);
    }

    // 已录入转入账户但未入账明细
    public List<IbpIfNetbnkTxn> qryTxnsToBook() {
        IbpIfNetbnkTxnExample example = new IbpIfNetbnkTxnExample();
        example.createCriteria().andSbsactnoIsNotNull().andBookflagEqualTo(BillStatus.ACCOUNT_PEND.getCode());
        example.setOrderByClause(" BKSERIALNO ");
        return ibpIfNetbnkTxnMapper.selectByExample(example);
    }

    // 是否存在未录入转入账户的明细
    public boolean hasInitTxns() {
        IbpIfNetbnkTxnExample example = new IbpIfNetbnkTxnExample();
        example.createCriteria().andSbsactnoIsNull();
        return ibpIfNetbnkTxnMapper.countByExample(example) > 0;
    }

    public boolean isExist(IbpIfNetbnkTxn record) {
        IbpIfNetbnkTxnExample example = new IbpIfNetbnkTxnExample();
        example.createCriteria().andBkserialnoEqualTo(record.getBkserialno());
        return ibpIfNetbnkTxnMapper.countByExample(example) > 0;
    }

    @Transactional
    public void saveTxns(List<IbpIfNetbnkTxn> records) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String operid = SystemService.getOperatorManager().getOperatorId();

        String dateStr = sdf.format(new Date());
        for (IbpIfNetbnkTxn record : records) {
            record.setCreatetime(dateStr);                             // 数据保存日期-接口无关
            record.setOperid(operid);                                  // 操作人-接口无关
            record.setBookflag(BillStatus.INIT.getCode());
            record.setRecversion(0);
            ibpIfNetbnkTxnMapper.insert(record);
        }
    }

    // 并发冲突
    public boolean isConflict(IbpIfNetbnkTxn txn) {
        return txn.getRecversion().compareTo(ibpIfNetbnkTxnMapper.selectByPrimaryKey(txn.getPkid()).getRecversion()) != 0;
    }

    public int update(IbpIfNetbnkTxn txn) {
        txn.setRecversion(txn.getRecversion() + 1);
        return ibpIfNetbnkTxnMapper.updateByPrimaryKey(txn);
    }

}
