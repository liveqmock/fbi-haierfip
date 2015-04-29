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
 * ����
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

            // ��鱣��
            String msg = jshOrderActService.checkOrderSerialNo(tiaXml9109001);
            if (msg != null) {
                toa.INFO.RET_CODE = "1000";
                toa.INFO.RET_MSG = msg + " ��ϸ�����ˣ������ظ����͡�";
                return toa;
            } else {
                int cnt = jshOrderActService.saveRecords(tiaXml9109001);
                if (cnt >= 0) {
                    // ����ɹ�= ȫ���״ν��ջ��ظ����յ�δ���˼�¼.
                    toa.INFO.RET_CODE = "0000";
                    toa.INFO.RET_MSG = "��ɱ��������" + cnt + ",�ظ����ձ�����" + (tiaXml9109001.BODY.DETAILS.size() - cnt);

                } else {
                    toa.INFO.RET_CODE = "1000";
                    toa.INFO.RET_MSG = "����ʧ��";
                }
            }



        } catch (Exception e) {
            logger.error("���վ��̻�ֿ���ϸ91009001����ִ���쳣," + e.getMessage());
            toa.INFO.RET_CODE = "1000";
            if (e.getMessage() == null) {
                toa.INFO.RET_MSG = "�����쳣";
            } else {
                toa.INFO.RET_MSG = "�����쳣��" + e.getMessage();
            }
        }
        logger.info("fip��Ӧdep���ģ�" + toa.toString());
        return toa;
    }
}
