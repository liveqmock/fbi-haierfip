package fip.online;

import fip.common.constant.PayoutBatRtnCode;
import fip.common.constant.PayoutBatRtnCode;
import fip.repository.model.FipPayoutbat;
import fip.repository.model.FipPayoutdetl;
import fip.service.fip.PayoutSbsactsService;
import fip.service.fip.PayoutbatService;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.ToaXml;
import org.fbi.dep.model.base.ToaXmlHeader;
import org.fbi.dep.model.txn.TiaXml1002001;
import org.fbi.dep.model.txn.ToaXml1002001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-4-11
 * Time: 上午10:13
 * To change this template use File | Settings | File Templates.
 */
@Component
public class DepTxn1002001Processor extends DepAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DepTxn1002001Processor.class);

    @Autowired
    private PayoutbatService payoutbatService;
    @Autowired
    private PayoutSbsactsService payoutSbsactsService;

    @Override
    public ToaXml process(TiaXml tia) throws Exception {
        ToaXml1002001 toa = new ToaXml1002001();
        toa.INFO = new ToaXmlHeader();
        toa.BODY = new ToaXml1002001.Body();
        toa.BODY.TRANS_DETAILS = new ArrayList<ToaXml1002001.Body.BodyDetail>();
        try {
            TiaXml1002001 tia1002001 = (TiaXml1002001) tia;
            toa.INFO.TRX_CODE = "1002001";
            toa.INFO.REQ_SN = tia1002001.INFO.REQ_SN;
            toa.INFO.WSYS_ID = tia1002001.INFO.WSYS_ID;

            // 检查交易序号是否重复，重复则直接返回失败
            if (payoutbatService.isExist(tia1002001.INFO.REQ_SN)) {
                throw new RuntimeException(PayoutBatRtnCode.MSG_REQSN_EXIST.toRtnMsg());
            } else {
                // 保存该交易信息和代付明细, 全部保存成功则为成功，否则全部失败
                FipPayoutbat payout = new FipPayoutbat();
                payout.setReqSn(tia1002001.INFO.REQ_SN);
                payout.setTrxCode(tia1002001.INFO.TRX_CODE);
                payout.setWsysId(tia1002001.INFO.WSYS_ID);
                payout.setTotalItem(tia1002001.BODY.TRANS_SUM.TOTAL_ITEM);
                payout.setTotalSum(tia1002001.BODY.TRANS_SUM.TOTAL_SUM);
                String createtime = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
                List<FipPayoutdetl> detailsList = new ArrayList<FipPayoutdetl>();
                BigDecimal totalAmt = new BigDecimal(0.0);
                for (TiaXml1002001.Body.BodyDetail detail : tia1002001.BODY.TRANS_DETAILS) {
                    if (!payoutSbsactsService.isExist(tia1002001.INFO.WSYS_ID, tia1002001.INFO.TRX_CODE, detail.SBS_ACCOUNT_NO)) {
                        logger.error(PayoutBatRtnCode.MSG_SBS_ACT_NOT_ALLOWED.toRtnMsg()
                                + "[系统ID：" + tia1002001.INFO.WSYS_ID + "交易码：" + tia1002001.INFO.TRX_CODE + "SBS账号" + detail.SBS_ACCOUNT_NO + "].");
                        throw new RuntimeException(PayoutBatRtnCode.MSG_SBS_ACT_NOT_ALLOWED.toRtnMsg());
                    }
                    FipPayoutdetl record = new FipPayoutdetl();
                    record.setReqSn(tia1002001.INFO.REQ_SN);
                    record.setSn(detail.SN);
                    record.setBankCode(detail.BANK_CODE);
                    record.setAccountType(detail.ACCOUNT_TYPE);
                    record.setAccountNo(detail.ACCOUNT_NO);
                    record.setAccountName(detail.ACCOUNT_NAME);
                    record.setProvince(detail.PROVINCE);
                    record.setCity(detail.CITY);
                    record.setAccountProp(detail.ACCOUNT_PROP);
                    record.setAmount(detail.AMOUNT);
                    record.setSbsAccountNo(detail.SBS_ACCOUNT_NO);
                    record.setRemark(detail.REMARK);
                    record.setReserve1(detail.RESERVE1);
                    record.setCreateTime(createtime);
                    detailsList.add(record);
                    totalAmt = totalAmt.add(new BigDecimal(detail.AMOUNT));
                }
                // 判断金额和总笔数
                if (!tia1002001.BODY.TRANS_SUM.TOTAL_ITEM.equals("" + detailsList.size())
                        || totalAmt.compareTo(new BigDecimal(tia1002001.BODY.TRANS_SUM.TOTAL_SUM)) != 0) {
                    throw new RuntimeException(PayoutBatRtnCode.MSG_CONTENT_FAILED.toRtnMsg());
                }
                if (payoutbatService.savePayoutTxns(payout, detailsList) == detailsList.size()) {
                    toa.INFO.RET_CODE = PayoutBatRtnCode.MSG_SAVE_SUCCESS.getCode();
                    toa.INFO.ERR_MSG = PayoutBatRtnCode.MSG_SAVE_SUCCESS.getTitle();
                    logger.info("成功保存代付明细笔数：" + detailsList.size() + " 交易流水号：" + tia1002001.INFO.REQ_SN);
                    for (TiaXml1002001.Body.BodyDetail detail : tia1002001.BODY.TRANS_DETAILS) {
                        ToaXml1002001.Body.BodyDetail rtnRecord = new ToaXml1002001.Body.BodyDetail();
                        rtnRecord.SN = detail.SN;
                        rtnRecord.RET_CODE = PayoutBatRtnCode.MSG_SAVE_SUCCESS.getCode();
                        rtnRecord.ERR_MSG = PayoutBatRtnCode.MSG_SAVE_SUCCESS.getTitle();
                        toa.BODY.TRANS_DETAILS.add(rtnRecord);
                    }
                } else {
                    throw new RuntimeException(PayoutBatRtnCode.MSG_SAVE_ERROR.toRtnMsg());
                }
            }
        } catch (Exception e) {
            logger.error("代付1002001交易保存失败," + e.getMessage());
            if (e.getMessage() == null) {
                toa.INFO.RET_CODE = PayoutBatRtnCode.MSG_SAVE_ERROR.getCode();
                toa.INFO.ERR_MSG = PayoutBatRtnCode.MSG_SAVE_ERROR.getTitle();
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
