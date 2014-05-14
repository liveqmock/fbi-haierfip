package fip.online;

import fip.common.constant.PayoutBatRtnCode;
import fip.common.constant.PayoutBatRtnCode;
import fip.repository.model.FipPayoutbat;
import fip.repository.model.FipPayoutdetl;
import fip.service.fip.PayoutDetlService;
import fip.service.fip.PayoutbatService;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.ToaXml;
import org.fbi.dep.model.base.ToaXmlHeader;
import org.fbi.dep.model.txn.TiaXml1003001;
import org.fbi.dep.model.txn.ToaXml1003001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-4-11
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */
@Component
public class DepTxn1003001Processor extends DepAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DepTxn1003001Processor.class);

    @Autowired
    private PayoutbatService payoutbatService;
    @Autowired
    private PayoutDetlService payoutDetlService;

    @Transactional
    @Override
    public ToaXml process(TiaXml tia) throws Exception {
        TiaXml1003001 tia1003001 = (TiaXml1003001) tia;
        ToaXml1003001 toa = new ToaXml1003001();
        toa.INFO = new ToaXmlHeader();
        toa.INFO.TRX_CODE = "1003001";
        toa.INFO.REQ_SN = tia1003001.INFO.REQ_SN;
        toa.INFO.WSYS_ID = tia1003001.INFO.WSYS_ID;

        toa.BODY = new ToaXml1003001.Body();
        toa.BODY.QUERY_TRANS.QUERY_SN = tia1003001.BODY.QUERY_TRANS.QUERY_SN;
        toa.BODY.QUERY_TRANS.QUERY_REMARK = tia1003001.BODY.QUERY_TRANS.QUERY_REMARK;
        toa.BODY.RET_DETAILS = new ArrayList<ToaXml1003001.Body.BodyDetail>();
        try {
            // 查询交易只需查询fip数据库
            List<FipPayoutbat> batList = payoutbatService.qryRecordBySn(tia1003001.BODY.QUERY_TRANS.QUERY_SN);
            if (batList.isEmpty()) {
                throw new RuntimeException(PayoutBatRtnCode.MSG_QRYSN_NOT_EXIST.toRtnMsg());
            } else {
                FipPayoutbat txn = batList.get(0);
                toa.INFO.RET_CODE = StringUtils.getNullString(txn.getRetCode());
                toa.INFO.ERR_MSG = StringUtils.getNullString(txn.getErrMsg());
            }
            List<FipPayoutdetl> recordList = payoutDetlService.qryRecordsBySn(tia1003001.BODY.QUERY_TRANS.QUERY_SN);
            for (FipPayoutdetl bodyDetail : recordList) {
                ToaXml1003001.Body.BodyDetail record = new ToaXml1003001.Body.BodyDetail();
                record.SN = bodyDetail.getSn();
                record.ACCOUNT_NO = bodyDetail.getAccountNo();
                record.ACCOUNT_NAME = bodyDetail.getAccountName();
                record.AMOUNT = bodyDetail.getAmount();
                record.REMARK = StringUtils.getNullString(bodyDetail.getRemark());
                record.RET_CODE = StringUtils.getNullString(bodyDetail.getRetCode());
                record.ERR_MSG = StringUtils.getNullString(bodyDetail.getErrMsg());
                toa.BODY.RET_DETAILS.add(record);
            }
        } catch (Exception e) {
            logger.error("代付交易结果查询1003001失败," + e.getMessage(), e);
            if (e.getMessage() == null) {
                toa.INFO.RET_CODE = PayoutBatRtnCode.UNKNOWN_EXCEPTION.getCode();
                toa.INFO.ERR_MSG = PayoutBatRtnCode.UNKNOWN_EXCEPTION.getTitle();
            } else {
                String errmsg[] = e.getMessage().split("\\|");
                toa.INFO.RET_CODE = errmsg[0];
                toa.INFO.ERR_MSG = errmsg[1];
            }
        }
        logger.info("fip响应dep报文：" + toa.toString());
        return toa;
    }
}
