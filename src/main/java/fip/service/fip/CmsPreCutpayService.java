package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.newcms.controllers.BaseCTL;
import fip.gateway.newcms.controllers.T100103CTL;
import fip.gateway.newcms.controllers.T100104CTL;
import fip.gateway.newcms.domain.T100103.T100103ResponseRecord;
import fip.gateway.newcms.domain.T100104.T100104RequestList;
import fip.gateway.newcms.domain.T100104.T100104RequestRecord;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.PtenudetailMapper;
import fip.repository.dao.XfapprepaymentMapper;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * ��ǰ�����.
 * User: zhanrui
 * Date: 11-8-13
 * Time: ����3:10
 * To change this template use File | Settings | File Templates.
 */
@Service
public class CmsPreCutpayService {
    private static final Logger logger = LoggerFactory.getLogger(CmsPreCutpayService.class);

    @Autowired
    private BillManagerService billManagerService;
    @Autowired
    private CmsService cmsService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;
    @Autowired
    private XfapprepaymentMapper xfapprepaymentMapper;
    @Autowired
    private PtenudetailMapper enudetailMapper;

    private final BillType billType = BillType.PRECUTPAYMENT;

    /**
     * ��ѯ�Ŵ�ϵͳ�Ĵ��ۼ�¼���ɹ�ѡ���Ի�ȡ��
     *
     * @param bizType
     * @return
     */
    public List<FipCutpaydetl> doQueryCmsBills(BizType bizType) {

        List<T100103ResponseRecord> recvedList = getCmsPreCutpayResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100103ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, this.billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    /**
     * ��ȡȫ���Ŵ�ϵͳ��¼
     *
     * @return
     */
    //@Transactional
    public synchronized int doObtainCmsBills(BizType bizType) {
        //TODO
        //String billType = "0";

        List<T100103ResponseRecord> recvedList = getCmsPreCutpayResponseRecords(bizType);
        if (recvedList.size() > 0) {
            //TODO ��鱾������״̬

            //TODO �浵���ر��мȴ�����
            //billManagerService.archiveAllBillsByBizID(bizID);
        } else {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100103ResponseRecord responseBean : recvedList) {
            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, this.billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            //TODO �ж�ҵ�������Ƿ��ظ�   ��ǰ�����ر���
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4PreCutpay(cutpaydetl.getPaybackdate(), cutpaydetl.getBilltype());
            //�����Զ���Ϣ����
            if (lockOrUnlockIntr4PreCutpay(cutpaydetl, "3")) {
                if (isNotRepeated) {
                    cutpaydetl.setDateCmsGet(new Date());
                    fipCutpaydetlMapper.insert(cutpaydetl);
                    count++;
                } else {
                    logger.info("��ȡ�Ŵ�����ʱ�����ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                }
            } else {
                logger.info("��Ϣ��������" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //��־
        batchInsertLogByBatchno(batchno);
        return count;
    }

    //@Transactional
    public synchronized int doMultiObtainCmsBills(BizType bizType, FipCutpaydetl[] selectedCutpaydetls) {
        //TODO
        //String billType = "0";

        List<T100103ResponseRecord> recvedList = getCmsPreCutpayResponseRecords(bizType);
        if (recvedList.size() > 0) {
            //TODO ��鱾������״̬

            //TODO �浵���ر��мȴ�����
            //billManagerService.archiveAllBillsByBizID(bizID);
        } else {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100103ResponseRecord responseBean : recvedList) {
            //�ж��Ƿ��ڻ�ȡ��Χ��
            for (FipCutpaydetl selectedCutpaydetl : selectedCutpaydetls) {
                if (responseBean.getStdjjh().equals(selectedCutpaydetl.getIouno())
                        && responseBean.getStdqch().equals(selectedCutpaydetl.getPoano())) {
                    iSeqno++;
                    FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, this.billType, responseBean);
                    if (cutpaydetl == null) {
                        continue;
                    }
                    //TODO �ж�ҵ�������Ƿ��ظ�
                    //boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
                    boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords4PreCutpay(cutpaydetl.getPaybackdate(), cutpaydetl.getBilltype());
                    //�����Զ���Ϣ����
                    if (lockOrUnlockIntr4PreCutpay(cutpaydetl, "3")) {
                        if (isNotRepeated) {
                            cutpaydetl.setDateCmsGet(new Date());
                            fipCutpaydetlMapper.insert(cutpaydetl);
                            count++;
                        } else {
                            logger.info("��ȡ�Ŵ�����ʱ�����ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        }
                    } else {
                        logger.info("��Ϣ��������" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    }
                }
            }
        }

        //��־
        batchInsertLogByBatchno(batchno);
        return count;
    }

    private List<T100103ResponseRecord> getCmsPreCutpayResponseRecords(BizType bizType) {
        //��ȡ���Ŵ�����LIST
        BaseCTL ctl;
        ctl = new T100103CTL();

        String biztype = "";
        if (bizType.equals(BizType.FD)) {
            biztype = "1";
        } else if (bizType.equals(BizType.XF)) {
            biztype = "2";
        }
        //��ѯ ����/�����Ŵ���1/2�� ����
        return ctl.start(biztype);
    }

    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               BillType billType,
                                               T100103ResponseRecord responseBean) {
        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        cutpaydetl.setOriginBizid(bizType.getCode());
        cutpaydetl.setBatchSn(batchSn);
        String seqno = "" + iBatchDetlSn;
        cutpaydetl.setBatchDetlSn(StringUtils.leftPad(seqno, 7, "0"));

        cutpaydetl.setXfappPkid(responseBean.getStdsqlsh()); //���뵥��ˮ��
        cutpaydetl.setAppno(responseBean.getStdsqdh()); //���뵥��

        cutpaydetl.setIouno(responseBean.getStdjjh()); //��ݺ�
        cutpaydetl.setPoano(responseBean.getStdqch());  //�ڴκ�
        cutpaydetl.setContractno(responseBean.getStdhth()); //��ͬ��
        cutpaydetl.setPaybackdate(responseBean.getStdjhhkr()); //�ƻ�������

        //�ͻ���Ϣ
        cutpaydetl.setClientno(responseBean.getStdkhh());
        cutpaydetl.setClientname(responseBean.getStdkhmc());

        //SBS������Ϣ
        cutpaydetl.setClientact(responseBean.getStddkzh());   //�����ʺ�

        //��������Ϣ
        cutpaydetl.setPaybackamt(new BigDecimal(responseBean.getStdhkje()));  //������
        cutpaydetl.setPrincipalamt(new BigDecimal(responseBean.getStdhkbj())); //�����
        cutpaydetl.setInterestamt(new BigDecimal(responseBean.getStdhklx()));  //������Ϣ
        cutpaydetl.setPunitiveintamt(new BigDecimal("0.00"));//��Ϣ���
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //������
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//ΥԼ����
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//��Ϣ�������


        //����������Ϣ
        if (bizType.equals(BizType.XF)) {
            //TODO ��������
            String tmpStr = responseBean.getStddqh();
            String regioncdTmp, bankcdTmp, nameTmp;
            if (StringUtils.isEmpty(tmpStr)) {
                if (StringUtils.isEmpty(cutpaydetl.getAppno())) {
                    String msg = "���Ŵ���ȡ�ۿ��¼ʱ�������뵥�Ų�ӦΪ��." + cutpaydetl.getClientname();
                    logger.error(msg);
                    throw new RuntimeException(msg);
                }
                //���ڲ����ۿ��ʺŵļ�¼
                Xfapprepayment xfapprepayment = xfapprepaymentMapper.selectByPrimaryKey(cutpaydetl.getAppno());
                if (xfapprepayment == null) {
                    logger.error("δ�ҵ����������¼��" + cutpaydetl.getClientname());
                    return null;
                }
                String channel = xfapprepayment.getChannel();
                if (StringUtils.isEmpty(channel)) {
                    logger.error("���Ŵ���ȡ�ۿ��¼����������Ϊ��." + cutpaydetl.getClientname());
                    channel = CutpayChannel.NONE.getCode();
                }
                cutpaydetl.setBiChannel(channel);
                cutpaydetl.setBiActopeningbank(xfapprepayment.getActopeningbank());
                cutpaydetl.setBiBankactno(xfapprepayment.getBankactno());
                cutpaydetl.setBiBankactname(xfapprepayment.getBankactname());
                cutpaydetl.setBiActopeningbankUd(xfapprepayment.getActopeningbankUd());
                cutpaydetl.setBiCustomerCode(xfapprepayment.getCustomerCode());
                cutpaydetl.setBiSignAccountNo(xfapprepayment.getSignAccountNo());
                cutpaydetl.setBiProvince(xfapprepayment.getProvince());
                cutpaydetl.setBiCity(xfapprepayment.getCity());
            } else {
                String[] code = tmpStr.split("-");
                if (code.length == 2) {
                    regioncdTmp = code[0].trim(); //����
                    bankcdTmp = code[1].trim();
                    cutpaydetl.setBiActopeningbank(bankcdTmp);
                }
                cutpaydetl.setBiBankactno(responseBean.getStdhkzh());
                //cutpaydetl.setBiBankactname(responseBean.getStdkhmc());
                cutpaydetl.setBiBankactname(responseBean.getStdckr());
                cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode()); //Ĭ��Ϊ������
            }
        } else if (bizType.equals(BizType.FD)) {
            String tmpStr = responseBean.getStddqh();
            String regioncdTmp = "";
            String bankcdTmp = "";
            String nameTmp = "";
            String[] code = tmpStr.split("-");
            if (code.length == 2) {
                regioncdTmp = code[0].trim();
                bankcdTmp = code[1].trim();
                cutpaydetl.setBiActopeningbank(bankcdTmp);
            }
            cutpaydetl.setBiBankactno(responseBean.getStdhkzh());
            //cutpaydetl.setBiBankactname(responseBean.getStdkhmc());
            cutpaydetl.setBiBankactname(responseBean.getStdckr());

            //��ʱ���� ������������ൺ ȫ��ͨ������  20120829 �޸�Ϊ 105ȫ��ֱ������
            if ("105".equals(bankcdTmp)) {
                cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode()); //Ĭ��Ϊ������
            } else {
                cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode());
            }

            Ptenudetail enu = selectEnuDetail("CmsProvince", regioncdTmp);
            if (enu == null) {
                String msg = "������Ϣ����:" + cutpaydetl.getClientname() + " ����:" + regioncdTmp;
                logger.error(msg);
                throw new RuntimeException(msg);
            }
            cutpaydetl.setBiProvince(enu.getEnuitemexpand());
            cutpaydetl.setBiCity(enu.getEnuitemlabel());

            /*
            if ("0532".equals(regioncdTmp)) {
                cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode()); //Ĭ��Ϊ������
                cutpaydetl.setBiProvince("�ൺ");
            } else {
                cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode());
                if ("0531".equals(regioncdTmp)) {
                    cutpaydetl.setBiProvince("ɽ��");
                } else if ("0351".equals(regioncdTmp)) {
                    cutpaydetl.setBiProvince("ɽ��");
                } else if ("023".equals(regioncdTmp)) {
                    cutpaydetl.setBiProvince("����");
                } else {
                    cutpaydetl.setBiProvince("��������");
                }
            }
            cutpaydetl.setBiCity(regioncdTmp);
            */
        }

        //����
        cutpaydetl.setRecversion((long) 0);
        cutpaydetl.setDeletedflag("0");
        cutpaydetl.setArchiveflag("0");
        //�ʵ�״̬
        cutpaydetl.setBillstatus(BillStatus.INIT.getCode());
        cutpaydetl.setSendflag("0");

        //�ʵ�����
        cutpaydetl.setBilltype(billType.getCode());
        cutpaydetl.setDateCmsGet(new Date());
        return cutpaydetl;
    }

    //============================================

    /**
     * ���Ҿ���һ��ö�ټ�¼
     *
     * @param enuType
     * @param areaCode
     * @return
     */
    private Ptenudetail selectEnuDetail(String enuType, String areaCode) {
        PtenudetailExample example = new PtenudetailExample();
        example.createCriteria().andEnutypeEqualTo(enuType).andEnuitemvalueEqualTo(areaCode);
        if (enudetailMapper.countByExample(example) != 1) {
            return null;
        }
        return enudetailMapper.selectByExample(example).get(0);
    }


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
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        for (FipCutpaydetl fipCutpaydetl : fipCutpaydetlList) {
            FipJoblog log = new FipJoblog();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(fipCutpaydetl.getPkid());
            log.setJobname("�½���¼");
            log.setJobdesc("�»�ȡ�Ŵ�ϵͳ���ۼ�¼");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
    }

    //================

    /**
     * ����������ǰ������Ϣ
     */
    public synchronized boolean lockOrUnlockIntr4PreCutpay(FipCutpaydetl detl, String option) {
        if ((!"1".equals(option))
                && (!"2".equals(option))
                && (!"3".equals(option))
                ) {
            throw new RuntimeException("��������");
        }

        T100104CTL t100104ctl = new T100104CTL();
        T100104RequestRecord record = new T100104RequestRecord();
        record.setStdjjh(detl.getIouno());
        record.setStdqch(detl.getPoano());
        record.setStdjhkkr(detl.getPaybackdate());
        //1-�ɹ� 2-ʧ��(��Ϣ����)  3-��Ϣ����
        record.setStdkkjg(option);
        T100104RequestList list = new T100104RequestList();
        list.add(record);
        //���ʷ��ʹ���
        return t100104ctl.start(list);
    }

}
