package ibp.service;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.repository.model.fip.UnipayQryResult;
import ibp.repository.dao.IbpIfCcbTxnMapper;
import ibp.repository.dao.IbpIfUnionpayTxnMapper;
import ibp.repository.model.IbpIfCcbTxn;
import ibp.repository.model.IbpIfCcbTxnExample;
import ibp.repository.model.IbpIfUnionpayTxn;
import ibp.repository.model.IbpIfUnionpayTxnExample;
import ibp.view.IfUnionpayTxnAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.platform.security.OperatorManager;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by lenovo on 2014-10-31.
 */
@Service
public class IbpIfUnionpayTxnService {
    private static final Logger logger = LoggerFactory.getLogger(IbpIfUnionpayTxnService.class);

    @Autowired
    private IbpIfUnionpayTxnMapper ibpIfUnionpayTxnMapper;

    public List<IbpIfUnionpayTxn> qryUnionpayTxnsByBookFlag(BillStatus bookFlag, String beginDate, String endDate) {
        if (beginDate.contains("-")) {
            beginDate = beginDate.replace("-", "");
        }
        if (endDate.contains("-")) {
            endDate = endDate.replace("-", "");
        }
        IbpIfUnionpayTxnExample example = new IbpIfUnionpayTxnExample();
        if (beginDate.equals(endDate)) {
            example.createCriteria().andBookflagEqualTo(bookFlag.getCode()).andCompleteTimeLike(beginDate + "%");
        } else {
            example.createCriteria().andBookflagEqualTo(bookFlag.getCode()).andCompleteTimeBetween(beginDate, endDate + " 240000");
        }
        example.setOrderByClause(" createtime,sn desc ");
        return ibpIfUnionpayTxnMapper.selectByExample(example);
    }

    // 保存自有渠道银联历史代扣记录
    public int insert(List<UnipayQryResult> upaylist) {
        IbpIfUnionpayTxnExample example = new IbpIfUnionpayTxnExample();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        String datetime = sdf.format(new Date());
        OperatorManager om = SystemService.getOperatorManager();
        String operid = "9999";
        if (om != null) {
            operid = om.getOperatorId();
        }
        int cnt = 0;
        for (UnipayQryResult result : upaylist) {
            example.clear();
            example.createCriteria().andAccountEqualTo(result.getACCOUNT()).andSnEqualTo(result.getSN());
            List<IbpIfUnionpayTxn> txns = ibpIfUnionpayTxnMapper.selectByExample(example);
            // 不存在则做保存处理
            if ((txns == null || txns.isEmpty()) && "0000".equals(result.getRET_CODE())) {
                //
                IbpIfUnionpayTxn txn = new IbpIfUnionpayTxn();
                txn.setPkid(UUID.randomUUID().toString());
                txn.setRecversion(0);
                txn.setQuerySn(result.getQUERY_SN());
                txn.setSn(result.getSN());
                txn.setOrafileId(result.getORAFILE_ID());
                txn.setAccount(result.getACCOUNT());
                txn.setAccountName(result.getACCOUNT_NAME());
                logger.info("银联返回 户名:" + result.getACCOUNT_NAME() + " SN:" + result.getSN() + "  AMT:" + result.getAMOUNT());

                txn.setAmount((new BigDecimal(result.getAMOUNT()).divide(new BigDecimal("100.00"))).subtract(new BigDecimal("1.50")));
                logger.info("系统保存 户名:" + result.getACCOUNT_NAME() + " SN:" + result.getSN() + "  AMT:" + txn.getAmount());
                txn.setCustUserid(result.getCUST_USERID());
                txn.setCompleteTime(result.getCOMPLETE_TIME());
                txn.setRemark(result.getREMARK());
                txn.setRetCode(result.getRET_CODE());
                txn.setErrMsg(result.getERR_MSG());
                txn.setBookflag(BillStatus.INIT.getCode());
                txn.setCreatetime(datetime);
                txn.setOperid(operid);
                cnt += ibpIfUnionpayTxnMapper.insert(txn);
            }
            // 已存在则不做处理
        }
        return cnt;
    }

    // 并发冲突
    public boolean isConflict(IbpIfUnionpayTxn txn) {
        return txn.getRecversion().compareTo(ibpIfUnionpayTxnMapper.selectByPrimaryKey(txn.getPkid()).getRecversion()) != 0;
    }

    public int update(IbpIfUnionpayTxn txn) {
        txn.setRecversion(txn.getRecversion() + 1);
        return ibpIfUnionpayTxnMapper.updateByPrimaryKey(txn);
    }

}
