package fip.online;

import fip.common.constant.PayoutBatRtnCode;
import fip.repository.model.FipPayoutbat;
import fip.repository.model.FipPayoutdetl;
import fip.service.fip.PayoutSbsactsService;
import fip.service.fip.PayoutbatService;
import ibp.service.JSHOrderActService;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.ToaXml;
import org.fbi.dep.model.base.ToaXmlHeader;
import org.fbi.dep.model.txn.TiaXml1002001;
import org.fbi.dep.model.txn.TiaXml9109001;
import org.fbi.dep.model.txn.ToaXml1002001;
import org.fbi.dep.model.txn.ToaXml9109001;
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
 * 接收
 */
@Component
public class DepTxn9109001Processor extends DepAbstractTxnProcessor {

    private static final Logger logger = LoggerFactory.getLogger(DepTxn9109001Processor.class);


    @Autowired
    private JSHOrderActService jshOrderActService;

    @Override
    public ToaXml process(TiaXml tia) throws Exception {
        ToaXml9109001 toa = new ToaXml9109001();
        try {
            TiaXml9109001 tiaXml9109001 = (TiaXml9109001) tia;
            toa.INFO.REQ_SN = tiaXml9109001.INFO.REQ_SN;

            // 检查保存
            String msg = jshOrderActService.checkOrderSerialNo(tiaXml9109001);
            if (msg != null) {
                toa.INFO.RET_CODE = "1000";
                toa.INFO.RET_MSG = msg + " 明细已入账，不可重复发送。";
                return toa;
            } else {
                int cnt = jshOrderActService.saveRecords(tiaXml9109001);
                if (cnt >= 0) {
                    // 保存成功= 全部首次接收或重复接收到未记账记录.
                    toa.INFO.RET_CODE = "0000";
                    toa.INFO.RET_MSG = "完成保存笔数：" + cnt + ",重复接收笔数：" + (tiaXml9109001.BODY.DETAILS.size() - cnt);

                } else {
                    toa.INFO.RET_CODE = "1000";
                    toa.INFO.RET_MSG = "保存失败";
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
