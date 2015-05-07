package ibp.service;

import fip.gateway.sbs.DepCtgManager;
import fip.gateway.sbs.core.SBSResponse4MultiRecord;
import fip.gateway.sbs.core.SOFDataDetail;
import fip.gateway.sbs.txn.T8123.T8123SOFDataDetail;
import ibp.repository.dao.IbpSbsActMapper;
import ibp.repository.model.IbpSbsAct;
import ibp.repository.model.IbpSbsActExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lenovo on 2014-11-05.
 */
@Service
public class IbpSbsActService {
    private static final Logger logger = LoggerFactory.getLogger(IbpSbsActService.class);

    @Autowired
    private IbpSbsActMapper ibpSbsActMapper;

    public List<IbpSbsAct> qrySbsActByName(String actnam) {
        return ibpSbsActMapper.qrySbsActToTrans("%" + actnam + "%");
    }

    private int delAllRecords() {
        IbpSbsActExample example = new IbpSbsActExample();
        example.createCriteria().andStatusIsNotNull();
        return ibpSbsActMapper.deleteByExample(example);
    }

    // ִ��SBS���׻�ȡSBS�˻�
    private List<IbpSbsAct> getSbsActRecords() {

        String date8 = new SimpleDateFormat("yyyyMMdd").format(new Date());

        List<String> paramList = new ArrayList<String>();
        paramList.add("       ");//7λ�ͻ���
        paramList.add("    ");//4λ������
        paramList.add("   ");//3λ�ұ�
        paramList.add("3");//1λ�ʻ�����
        paramList.add("1");//1����λ 2������
        paramList.add("0"); // ��ʼ����
        logger.info(paramList.toString());
        int pkid = 0;
        SBSResponse4MultiRecord response = excuteSbsTxn8123(paramList);
        int totcnt = Integer.parseInt(response.getSofDataHeader().getTotcnt());
        logger.info("TOTCNT:" + totcnt);
        List<SOFDataDetail> details = null;
        List<IbpSbsAct> acts = new ArrayList<IbpSbsAct>();
        while (pkid < totcnt) {
            paramList.set(5, String.valueOf(pkid + 1));
            response = excuteSbsTxn8123(paramList);
            details = response.getSofDataDetailList();
            for (SOFDataDetail record : details) {
                T8123SOFDataDetail detail = (T8123SOFDataDetail) record;
                IbpSbsAct act = new IbpSbsAct();
                act.setPkid(String.valueOf(pkid));
                act.setActnum(detail.getActnum());
                act.setActnam(detail.getActnam());
                act.setOpndat(date8);
                act.setStatus("O");
                acts.add(act);
                pkid++;
            }
            logger.info("PKID:" + pkid);
        }

        return acts;
    }

    private SBSResponse4MultiRecord excuteSbsTxn8123(List<String> paramList) {
        byte[] recvBuf = DepCtgManager.processSingleResponsePkg("8123", paramList);
        SBSResponse4MultiRecord response = new SBSResponse4MultiRecord();
        T8123SOFDataDetail sofDataDetail = new T8123SOFDataDetail();
        response.setSofDataDetail(sofDataDetail);
        response.init(recvBuf);
        return response;
    }

    // ����SBS�˻�
    @Transactional
    public int syncSbsActRecords() {
        List<IbpSbsAct> acts = getSbsActRecords();
        int cnt = delAllRecords();
        logger.info(" ɾ��ԭ��SBS�˻�����" + cnt);
        for (IbpSbsAct act : acts) {
            ibpSbsActMapper.insert(act);
        }
        return acts.size();
    }

}
