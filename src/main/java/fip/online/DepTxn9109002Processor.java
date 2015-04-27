package fip.online;

import ibp.repository.model.IbpJshOrder;
import ibp.service.JSHOrderActService;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.ToaXml;
import org.fbi.dep.model.txn.TiaXml9109001;
import org.fbi.dep.model.txn.TiaXml9109002;
import org.fbi.dep.model.txn.ToaXml9109001;
import org.fbi.dep.model.txn.ToaXml9109002;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Component
public class DepTxn9109002Processor extends DepAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DepTxn9109002Processor.class);


    @Autowired
    private JSHOrderActService jshOrderActService;

    @Override
    public ToaXml process(TiaXml tia) throws Exception {
        ToaXml9109002 toa = new ToaXml9109002();
        try {
            TiaXml9109002 tiaXml9109002 = (TiaXml9109002) tia;
            toa.INFO.REQ_SN = tiaXml9109002.INFO.REQ_SN;

            List<IbpJshOrder> orderList = jshOrderActService.qryOrdersByDate(tiaXml9109002.BODY.TXNDATE);
            toa.BODY.DETAILNUM = String.valueOf(orderList.size());
            if (!orderList.isEmpty()) {
                toa.BODY.DETAILS = new ArrayList<ToaXml9109002.BodyDetail>();
                for (IbpJshOrder record : orderList) {
                    ToaXml9109002.BodyDetail bean = new ToaXml9109002.BodyDetail();
                    bean.ORDERID = record.getOrderid();
                    bean.TXNDATE = record.getTxndate();
                    bean.SERIALNO = record.getSerialno();
                    bean.ACTNO = record.getActno();
                    bean.ACTNAME = record.getActname();
                    bean.TXN_AMT = record.getTxnAmt().toString();
                    bean.ACTDATE = record.getSbsTxndate();
                    bean.FORMCODE = record.getFormcode();
                    bean.FORMMSG = record.getFormmsg();
                    bean.REMARK = record.getRemark();
                    bean.RESERVE = record.getReserve();
                    toa.BODY.DETAILS.add(bean);
                }
            }


        } catch (Exception e) {
            logger.error("接收巨商汇分款明细91009001交易执行异常," + e.getMessage());
            toa.INFO.RET_CODE = "1000";
            if (e.getMessage() == null) {
                toa.INFO.RET_MSG = "交易异常";
            } else {
                toa.INFO.RET_MSG = "交易异常，" + e.getMessage();
            }
        }
        logger.info("fip响应dep报文：" + toa.toString());
        return toa;
    }
}
