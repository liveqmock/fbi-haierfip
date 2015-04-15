package ibp.service;

import fip.common.constant.BillStatus;
import fip.gateway.sbs.DepCtgManager;
import ibp.repository.dao.IbpIfCcbTxnMapper;
import ibp.repository.model.IbpIfCcbTxn;
import ibp.repository.model.IbpIfCcbTxnExample;
import ibp.repository.model.IbpSbsTranstxn;
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
 * Created by lenovo on 2014-10-31.
 */
@Service
public class IbpIfCcbTxnService {
    private static final Logger logger = LoggerFactory.getLogger(IbpIfCcbTxnService.class);

    @Autowired
    private IbpIfCcbTxnMapper ibpIfCcbTxnMapper;

    public List<IbpIfCcbTxn> qryCcbTxnsByBookFlag(BillStatus bookFlag) {
        IbpIfCcbTxnExample example = new IbpIfCcbTxnExample();
        example.createCriteria().andBookflagEqualTo(bookFlag.getCode());
        return ibpIfCcbTxnMapper.selectByExample(example);
    }

    // ²¢·¢³åÍ»
    public boolean isConflict(IbpIfCcbTxn txn) {
        return txn.getRecversion().compareTo(ibpIfCcbTxnMapper.selectByPrimaryKey(txn.getPkid()).getRecversion()) != 0;
    }

    public int update(IbpIfCcbTxn txn) {
        txn.setRecversion(txn.getRecversion() + 1);
        return ibpIfCcbTxnMapper.updateByPrimaryKey(txn);
    }

}
