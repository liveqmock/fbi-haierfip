package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipCutpaydetlExample;
import fip.repository.model.FipJoblog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.security.OperatorManager;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * HCCB С���Ŵ�.
 * User: zhanrui
 * Date: 2014-08-01
 */
@Service
public class HccbService {
    private static final Logger logger = LoggerFactory.getLogger(HccbService.class);

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipRefunddetlMapper fipRefunddetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;

    @Transactional
    public int importDataFromXls(BizType bizType, List<FipCutpaydetl> cutpaydetls, List<String> returnMsgs) {

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (FipCutpaydetl cutpaydetl : cutpaydetls) {
            iSeqno++;
            assembleCutpayRecord(bizType, batchno, iSeqno, cutpaydetl);

            //TODO �ж�ҵ�������Ƿ��ظ�   ע�� �޸�IOUNO����ʱ��Ҫͬ���޸�commonmapper�е�SQL
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4Hccb(cutpaydetl.getIouno(), cutpaydetl.getPoano());
            if (isNotRepeated) {
                fipCutpaydetlMapper.insert(cutpaydetl);
                count++;
            } else {
                returnMsgs.add("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.error("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //��־
        batchInsertLogByBatchno(batchno);
        return count;
    }

    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               FipCutpaydetl cutpaydetl) {
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        if (StringUtils.isEmpty(cutpaydetl.getPoano())) {
           cutpaydetl.setPoano("0");
        }

        //��������Ϣ
        cutpaydetl.setPrincipalamt(new BigDecimal("0.00")); //�����
        cutpaydetl.setInterestamt(new BigDecimal("0.00"));  //������Ϣ
        cutpaydetl.setPunitiveintamt(new BigDecimal("0.00"));//��Ϣ���
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//ΥԼ����
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//��Ϣ�������
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //������

        //����������Ϣ
        if (bizType.equals(BizType.HCCB)) {
            cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //Ĭ��Ϊ����
        } else {
            throw new RuntimeException("��HCCB���ݣ����ܴ���");
        }

        //����
        cutpaydetl.setRecversion((long) 0);
        cutpaydetl.setDeletedflag("0");
        cutpaydetl.setArchiveflag("0");
        cutpaydetl.setWritebackflag("0");
        cutpaydetl.setAccountflag("0");
        //�ʵ�״̬
        cutpaydetl.setBillstatus(BillStatus.INIT.getCode());
        cutpaydetl.setSendflag("0");

        //zhanrui 20120305  ��ʶ�����Ŵ�������Դ���Ŵ�ϵͳ �����дʱ������Դϵͳ
        cutpaydetl.setRemark3("HCCB");
        cutpaydetl.setDateCmsGet(new Date());

        //����
        cutpaydetl.setBilltype(BillType.NORMAL.getCode());
        cutpaydetl.setClientact("123456"); //����Ϊ��
        return cutpaydetl;
    }

    //============================================


    /**
     * ��鱾�ر��мȴ��¼��״̬ ��������
     * 1��״̬�����ļ�¼
     * 2��δ���͵ļ�¼
     * 3�����ͳɹ��ļ�¼�����ͳɹ��ı������ʻ�д��
     *
     * @return
     */
    private boolean checkLocalBillsStatus() {
        return true;
    }

    //TODO  ����
    private void batchInsertLogByBatchno(String batchno) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andBatchSnEqualTo(batchno);
        List<FipCutpaydetl> fipCutpaydetlList = fipCutpaydetlMapper.selectByExample(example);

        Date date = new Date();

        OperatorManager operatorManager = SystemService.getOperatorManager();
        String userid;
        String username;
        if (operatorManager == null) {
            userid = "9999";
            username = "BATCH";
        }else{
            userid = operatorManager.getOperatorId();
            username = operatorManager.getOperatorName();
        }

        for (FipCutpaydetl fipCutpaydetl : fipCutpaydetlList) {
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(fipCutpaydetl.getPkid());
            log.setJobname("�½���¼");
            log.setJobdesc("�»�ȡ�������Ŵ�ϵͳ���ۼ�¼");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }

}
