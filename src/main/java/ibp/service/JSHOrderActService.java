package ibp.service;

import fip.service.fip.JobLogService;
import ibp.repository.dao.IbpJshOrderMapper;
import ibp.repository.model.IbpJshOrder;
import ibp.repository.model.IbpJshOrderExample;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.txn.TiaXml9109001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by lenovo on 2015-04-23.
 */
@Service
public class JSHOrderActService {
    private static final Logger logger = LoggerFactory.getLogger(JSHOrderActService.class);

    @Autowired
    private JobLogService jobLogService;
    @Autowired
    private IbpJshOrderMapper ibpJshOrderMapper;

    // ������̻㶩���ֿ���ϸ
    @Transactional
    public int saveRecords(TiaXml9109001 tia) {

        int cnt = 0;

        for (TiaXml9109001.BodyDetail record : tia.BODY.DETAILS) {
            IbpJshOrder order = new IbpJshOrder();
            order.setTxnCode(tia.INFO.TXN_CODE);
            order.setReqSn(tia.INFO.REQ_SN);
            order.setOrderid(record.ORDERID);
            order.setTxndate(record.TXNDATE);
            order.setSerialno(record.SERIALNO);

            order.setActno(record.ACTNO);
            order.setActname(record.ACTNAME);
            order.setTxnAmt(new BigDecimal(record.TXN_AMT));
            order.setRemark(record.REMARK);
            order.setReserve(record.RESERVE);
            if (ibpJshOrderMapper.insert(order) > 0) {
                cnt++;
            }
        }
        return cnt;
    }

    // �����ϸ��¼�����ش�����Ϣ
    public String checkOrderNo(List<TiaXml9109001.BodyDetail> detailList) {
        /*IbpJshOrderExample example = new IbpJshOrderExample();
        for(TiaXml9109001.BodyDetail record : tia.BODY.DETAILS) {
            example.clear();
            example.createCriteria().and
        }*/
        return null;
    }

    public List<IbpJshOrder> qryOrdersByDate(String txnDate) {
        IbpJshOrderExample example = new IbpJshOrderExample();
        example.createCriteria().andTxndateEqualTo(txnDate);
        return ibpJshOrderMapper.selectByExample(example);
    }

}
