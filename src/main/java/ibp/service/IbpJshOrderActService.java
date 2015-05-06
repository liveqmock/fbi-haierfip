package ibp.service;

import fip.common.constant.BillStatus;
import fip.service.fip.JobLogService;
import ibp.repository.dao.IbpJshOrderMapper;
import ibp.repository.model.IbpIfNetbnkTxn;
import ibp.repository.model.IbpJshOrder;
import ibp.repository.model.IbpJshOrderExample;
import org.fbi.dep.model.txn.TiaXml9109001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2015-04-23.
 */
@Service
public class IbpJshOrderActService {
    private static final Logger logger = LoggerFactory.getLogger(IbpJshOrderActService.class);

    @Autowired
    private JobLogService jobLogService;
    @Autowired
    private IbpJshOrderMapper ibpJshOrderMapper;
    private static final String INIT_STS = BillStatus.INIT.getCode();

    // ������̻㶩���ֿ���ϸ
    @Transactional
    public int saveRecords(TiaXml9109001 tia) {

        int cnt = 0;

        String time = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        IbpJshOrderExample example = new IbpJshOrderExample();
        List<IbpJshOrder> orders = null;
        IbpJshOrder order = null;

        for (TiaXml9109001.BodyDetail record : tia.BODY.DETAILS) {

            example.clear();
            example.createCriteria().andReqSnEqualTo(tia.INFO.REQ_SN).andSerialnoEqualTo(record.SERIALNO).andFormcodeEqualTo(INIT_STS);
            orders = ibpJshOrderMapper.selectByExample(example);

            // �Ѵ���δ����ͬ���кŶ���������
            if (orders != null && !orders.isEmpty()) {
                order = orders.get(0);
                order.setRecversion(order.getRecversion() + 1);

            } else {
                order = new IbpJshOrder();
                order.setRecversion(0L);
            }
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
            order.setFormcode(INIT_STS);
            order.setCreateTime(time);

            if (orders != null && !orders.isEmpty()) {
                ibpJshOrderMapper.updateByExample(order, example);
            } else {
                if (ibpJshOrderMapper.insert(order) > 0) {
                    cnt++;
                }
            }
        }
        return cnt;
    }

    // �����ϸ��¼�����ش�����Ϣ
    public String checkOrderSerialNo(TiaXml9109001 tia) {
        IbpJshOrderExample example = new IbpJshOrderExample();

        for (TiaXml9109001.BodyDetail record : tia.BODY.DETAILS) {
            example.clear();
            example.createCriteria().andReqSnEqualTo(tia.INFO.REQ_SN).andSerialnoEqualTo(record.SERIALNO).andFormcodeNotEqualTo(INIT_STS);
            List<IbpJshOrder> orders = ibpJshOrderMapper.selectByExample(example);
            if (orders != null && !orders.isEmpty()) {
                return "��ˮ�룺" + tia.INFO.REQ_SN + "  ���:" + record.SERIALNO;
            }
        }
        return null;
    }


    // ���������ڲ�ѯ
    public List<IbpJshOrder> qryOrdersByDate(String txnDate) {
        IbpJshOrderExample example = new IbpJshOrderExample();
        example.createCriteria().andTxndateEqualTo(txnDate);
        return ibpJshOrderMapper.selectByExample(example);
    }

    // ���������ڲ�ѯ�����˼�¼
    public List<IbpJshOrder> qryActOrdersByDate(String txnDate) {
        IbpJshOrderExample example = new IbpJshOrderExample();
        example.createCriteria().andSbsTxndateEqualTo(txnDate).andFormcodeNotEqualTo(INIT_STS);
        return ibpJshOrderMapper.selectByExample(example);
    }

    // ��ѯδ���˼�¼
    public List<IbpJshOrder> qryInitOrders() {
        IbpJshOrderExample example = new IbpJshOrderExample();
//        example.createCriteria().andTxndateEqualTo(txnDate).andFormcodeEqualTo(INIT_STS);
        example.createCriteria().andFormcodeEqualTo(INIT_STS);
        return ibpJshOrderMapper.selectByExample(example);
    }

    // ������ͻ
    public boolean isConflict(IbpJshOrder txn) {
        return txn.getRecversion().compareTo(ibpJshOrderMapper.selectByPrimaryKey(txn.getPkid()).getRecversion()) != 0;
    }

    public int update(IbpJshOrder txn) {
        txn.setRecversion(txn.getRecversion() + 1);
        return ibpJshOrderMapper.updateByPrimaryKey(txn);
    }
}
