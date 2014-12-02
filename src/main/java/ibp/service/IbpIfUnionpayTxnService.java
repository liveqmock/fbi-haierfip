package ibp.service;

import fip.common.constant.BillStatus;
import ibp.repository.dao.IbpIfCcbTxnMapper;
import ibp.repository.dao.IbpIfUnionpayTxnMapper;
import ibp.repository.model.IbpIfCcbTxn;
import ibp.repository.model.IbpIfCcbTxnExample;
import ibp.repository.model.IbpIfUnionpayTxn;
import ibp.repository.model.IbpIfUnionpayTxnExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lenovo on 2014-10-31.
 */
@Service
public class IbpIfUnionpayTxnService {
    private static final Logger logger = LoggerFactory.getLogger(IbpIfUnionpayTxnService.class);

    @Autowired
    private IbpIfUnionpayTxnMapper ibpIfUnionpayTxnMapper;

    public List<IbpIfUnionpayTxn> qryUnionpayTxnsByBookFlag(BillStatus bookFlag) {
        IbpIfUnionpayTxnExample example = new IbpIfUnionpayTxnExample();
        example.createCriteria().andBookflagEqualTo(bookFlag.getCode());
        example.setOrderByClause(" sn desc ");
        return ibpIfUnionpayTxnMapper.selectByExample(example);
    }

    // ²¢·¢³åÍ»
    public boolean isConflict(IbpIfUnionpayTxn txn) {
        return txn.getRecversion() != ibpIfUnionpayTxnMapper.selectByPrimaryKey(txn.getPkid()).getRecversion();
    }

    public int update(IbpIfUnionpayTxn txn) {
        txn.setRecversion(txn.getRecversion() + 1);
        return ibpIfUnionpayTxnMapper.updateByPrimaryKey(txn);
    }

}
