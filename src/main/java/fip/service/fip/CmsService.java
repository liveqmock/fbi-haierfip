package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.newcms.controllers.*;
import fip.gateway.newcms.domain.T100101.T100101ResponseRecord;
import fip.gateway.newcms.domain.T100102.T100102RequestList;
import fip.gateway.newcms.domain.T100102.T100102RequestRecord;
import fip.gateway.newcms.domain.T100104.T100104RequestList;
import fip.gateway.newcms.domain.T100104.T100104RequestRecord;
import fip.gateway.newcms.domain.T100108.T100108RequestList;
import fip.gateway.newcms.domain.T100108.T100108RequestRecord;
import fip.repository.dao.*;
import fip.repository.model.*;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * ���������.
 * User: zhanrui
 * Date: 11-8-13
 * Time: ����3:10
 */
@Service
public class CmsService {
    private static final Logger logger = LoggerFactory.getLogger(CmsService.class);

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;

    @Autowired
    private FipJoblogMapper fipJoblogMapper;

    @Autowired
    private XfappMapper xfappMapper;

    @Autowired
    private XfapprepaymentMapper xfapprepaymentMapper;

    @Autowired
    private PtenudetailMapper enudetailMapper;

    /**
     * ��ѯ�Ŵ�ϵͳ�Ĵ��ۼ�¼���ɹ�ѡ���Ի�ȡ��    (��������Ϣ�ʵ�)
     */
    public List<FipCutpaydetl> doQueryCmsBills(BizType bizType, BillType billType) {

        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //ǰ���� ֻ��ȡ����״̬Ϊ�����ļ�¼
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                logger.error("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
                throw new RuntimeException("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
            }
            if (!"0".equals(stddkzt.trim())) { //����״̬���������Ĳ�����
                continue;
            }

            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, billType, responseBean);

            //�����
            if (cutpaydetl == null) {
                continue;
            }

            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    /**
     * ��ȡȫ���Ŵ�ϵͳ��¼  (��������Ϣ�ʵ�)
     */
    //@Transactional
    public synchronized int doObtainCmsBills(BizType bizType, BillType billType, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //ǰ���� ֻ��ȡ����״̬Ϊ�����ļ�¼
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                returnMsgs.add("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
                logger.error("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
                continue;
            }
            if (!"0".equals(stddkzt.trim())) { //����״̬���������Ĳ�����
                continue;
            }

            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);

            //�����
            if (cutpaydetl == null) {
                continue;
            }

            //TODO �ж�ҵ�������Ƿ��ظ�
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
            if (isNotRepeated) {
                cutpaydetl.setDateCmsGet(new Date());
                fipCutpaydetlMapper.insert(cutpaydetl);
                count++;
            } else {
                returnMsgs.add("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.info("��ȡ�Ŵ�����ʱ�����ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //��־
        batchInsertLogByBatchno(batchno);
        return count;
    }

    //@Transactional
    public synchronized int doMultiObtainCmsBills(BizType bizType, BillType billType, FipCutpaydetl[] selectedCutpaydetls, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //ǰ���� ֻ��ȡ����״̬Ϊ�����ļ�¼
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                returnMsgs.add("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
                logger.error("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
                continue;
            }
            if (!"0".equals(stddkzt.trim())) { //����״̬���������Ĳ�����
                continue;
            }

            //�ж��Ƿ��ڻ�ȡ��Χ��
            for (FipCutpaydetl selectedCutpaydetl : selectedCutpaydetls) {
                if (responseBean.getStdjjh().equals(selectedCutpaydetl.getIouno())
                        && responseBean.getStdqch().equals(selectedCutpaydetl.getPoano())) {
                    iSeqno++;
                    FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);
                    if (cutpaydetl == null) {
                        continue;
                    }
                    //TODO �ж�ҵ�������Ƿ��ظ�
                    boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
                    if (isNotRepeated) {
                        cutpaydetl.setDateCmsGet(new Date());
                        fipCutpaydetlMapper.insert(cutpaydetl);
                        count++;
                    } else {
                        returnMsgs.add("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        logger.info("��ȡ�Ŵ�����ʱ�����ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    }

                }
            }
        }

        //��־
        batchInsertLogByBatchno(batchno);
        return count;
    }

    //=======================================================================================

    /**
     * ��ѯ�Ŵ�ϵͳ�Ĵ��ۼ�¼�����ڼ�¼��
     */
    public List<FipCutpaydetl> doQueryCmsOverdueBills(BizType bizType, BillType billType) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        List<FipCutpaydetl> cutpaydetlList = new ArrayList<FipCutpaydetl>();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //ǰ���� ֻ��ȡ����״̬Ϊ���ڵļ�¼
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                logger.error("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
                throw new RuntimeException("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
            }
            if (!"1".equals(stddkzt.trim())) { //����״̬���������Ĳ�����
                continue;
            }

            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, "000000", iSeqno, billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            cutpaydetl.setPkid(UUID.randomUUID().toString());
            cutpaydetlList.add(cutpaydetl);
        }
        return cutpaydetlList;
    }

    /**
     * ��ȡȫ���Ŵ�ϵͳ��¼  (��������Ϣ�ʵ�)
     *
     * @return
     */
    @Transactional
    public synchronized int doObtainCmsOverdueBills(BizType bizType, BillType billType, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //ǰ���� ֻ��ȡ����״̬Ϊ���ڵļ�¼
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                returnMsgs.add("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
                logger.error("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
            }
            if (!"1".equals(stddkzt.trim())) { //����״̬���������Ĳ�����
                continue;
            }

            iSeqno++;
            FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);
            if (cutpaydetl == null) {
                continue;
            }
            //TODO �ж�ҵ�������Ƿ��ظ�
            boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());

            //�����Զ���Ϣ����
            if (lockOrUnlockIntr4Overdue(cutpaydetl.getIouno(), "1")) {
                if (isNotRepeated) {
                    cutpaydetl.setDateCmsGet(new Date());
                    fipCutpaydetlMapper.insert(cutpaydetl);
                    count++;
                } else {
                    returnMsgs.add("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    logger.info("��ȡ�Ŵ�����ʱ�����ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                }
            } else {
                returnMsgs.add("��Ϣ��������" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                logger.info("��Ϣ��������" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
            }
        }

        //��־
        batchInsertLogByBatchno(batchno);
        return count;
    }

    @Transactional
    public synchronized int doMultiObtainCmsOverdueBills(BizType bizType, BillType billType, FipCutpaydetl[] selectedCutpaydetls, List<String> returnMsgs) {
        List<T100101ResponseRecord> recvedList = getCmsResponseRecords(bizType);
        if (recvedList.size() == 0) {
            return 0;
        }

        int count = 0;
        String batchno = billManagerService.generateBatchno();
        int iSeqno = 0;
        for (T100101ResponseRecord responseBean : recvedList) {
            //ǰ���� ֻ��ȡ����״̬Ϊ���ڵļ�¼
            String stddkzt = responseBean.getStddkzt();
            if (StringUtils.isEmpty(stddkzt)) {
                returnMsgs.add("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
                logger.error("����״̬�ֶ�Ϊ��:" + responseBean.getStdkhmc());
            }
            if (!"1".equals(stddkzt.trim())) { //����״̬���������Ĳ�����
                continue;
            }

            //�ж��Ƿ��ڻ�ȡ��Χ��
            for (FipCutpaydetl selectedCutpaydetl : selectedCutpaydetls) {
                if (responseBean.getStdjjh().equals(selectedCutpaydetl.getIouno())
                        && responseBean.getStdqch().equals(selectedCutpaydetl.getPoano())) {
                    iSeqno++;
                    FipCutpaydetl cutpaydetl = assembleCutpayRecord(bizType, batchno, iSeqno, billType, responseBean);
                    if (cutpaydetl == null) {
                        continue;
                    }
                    //TODO �ж�ҵ�������Ƿ��ظ�
                    boolean isNotRepeated = billManagerService.checkNoRepeatedBizkeyRecords(cutpaydetl.getIouno(), cutpaydetl.getPoano(), cutpaydetl.getBilltype());
                    //�����Զ���Ϣ����
                    if (lockOrUnlockIntr4Overdue(cutpaydetl.getIouno(), "1")) {
                        if (isNotRepeated) {
                            cutpaydetl.setDateCmsGet(new Date());
                            fipCutpaydetlMapper.insert(cutpaydetl);
                            count++;
                        } else {
                            returnMsgs.add("�ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                            logger.info("��ȡ�Ŵ�����ʱ�����ظ���¼��" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        }
                    } else {
                        returnMsgs.add("��Ϣ��������" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                        logger.info("��Ϣ��������" + cutpaydetl.getIouno() + cutpaydetl.getClientname());
                    }
                }
            }
        }

        //��־
        batchInsertLogByBatchno(batchno);
        return count;
    }


    //=======================================================================================
    private List<T100101ResponseRecord> getCmsResponseRecords(BizType bizType) {
        //��ȡ���Ŵ�����LIST
        BaseCTL ctl;
        ctl = new T100101CTL();

        String biztype = "";
        if (bizType.equals(BizType.FD)) {
            biztype = "1";
        } else if (bizType.equals(BizType.XF)) {
            biztype = "2";
        }
        //��ѯ ����/�����Ŵ���1/2�� ����
        return ctl.start(biztype);
    }


    /**
     * ������ ��Ϣ�ʵ����� ������
     */
    private FipCutpaydetl assembleCutpayRecord(BizType bizType,
                                               String batchSn,
                                               int iBatchDetlSn,
                                               BillType billType,
                                               T100101ResponseRecord responseBean) {
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
        cutpaydetl.setPunitiveintamt(new BigDecimal(responseBean.getStdfxje()));//��Ϣ���

        //�ֶγ�ʼ
        cutpaydetl.setBreakamt(new BigDecimal("0.00"));//ΥԼ����
        cutpaydetl.setCompoundintamt(new BigDecimal("0.00"));//��Ϣ�������
        cutpaydetl.setReserveamt(new BigDecimal("0.00"));  //������

        //20120503 zhanrui ����ҵ��ķ�Ϣ���� ����ڱ��ĵ������ֶ���
        cutpaydetl.setCompoundintamt(new BigDecimal(responseBean.getStdryje()));  //��Ϣ�������

        //�ʵ�����
        cutpaydetl.setBilltype(billType.getCode());

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

                //cutpaydetl.setBiChannel(CutpayChannel.NONE.getCode()); //Ĭ��Ϊ������
                //20120313 zhanrui �����Ŵ������ݸ�Ϊ��������
                cutpaydetl.setBiChannel(CutpayChannel.UNIPAY.getCode()); //Ĭ��Ϊ������
            }
            //zhanrui 20120305  ��ʶ�����Ŵ�������Դ���Ŵ�ϵͳ �����дʱ������Դϵͳ
            cutpaydetl.setRemark3("XF-CMS");
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

    /**
     * ��д�Ŵ�ϵͳ
     * 2013-4-11֮ǰֻʹ�ñ�������д���۳ɹ���¼
     */
    //@Transactional
    public int writebackCutPayRecord2CMS(List<FipCutpaydetl> cutpaydetlList, List<String> returnMsgs) {

        int count = 0;

        T100102CTL t100102ctl = new T100102CTL();
        T100104CTL t100104ctl = new T100104CTL();
        T100106CTL t100106ctl = new T100106CTL();

        for (FipCutpaydetl detl : cutpaydetlList) {
            boolean writebackResult = false;
            if (detl.getBilltype().equals(BillType.NORMAL.getCode())) { //��������
                T100102RequestRecord recordT102 = new T100102RequestRecord();
                recordT102.setStdjjh(detl.getIouno());
                recordT102.setStdqch(detl.getPoano());
                recordT102.setStdjhkkr(detl.getPaybackdate());
                //1-�ɹ� 2-ʧ��
                recordT102.setStdkkjg("1");
                T100102RequestList t100102list = new T100102RequestList();
                t100102list.add(recordT102);
                //���ʷ��ʹ���
                writebackResult = t100102ctl.start(t100102list);
            } else if (detl.getBilltype().equals(BillType.PRECUTPAYMENT.getCode())) { //��ǰ����
                T100104RequestRecord recordT104 = new T100104RequestRecord();
                recordT104.setStdjjh(detl.getIouno());
                recordT104.setStdqch(detl.getPoano());
                recordT104.setStdjhkkr(detl.getPaybackdate());
                //1-�ɹ� 2-ʧ��
                recordT104.setStdkkjg("1");
                T100104RequestList t100104list = new T100104RequestList();
                t100104list.add(recordT104);
                //���ʷ��ʹ���
                writebackResult = t100104ctl.start(t100104list);
            } else if (detl.getBilltype().equals(BillType.OVERDUE.getCode())) { //����
                /*���ڻ�д��ʱʹ�����������д�ӿ�
                T100106RequestRecord recordT106 = new T100106RequestRecord();
                recordT106.setStdjjh(detl.getIouno());
                recordT106.setStdqch(detl.getPoano());
                recordT106.setStdjhkkr(detl.getPaybackdate());
                //1-�ɹ� 2-ʧ��
                recordT106.setStdkkjg("1");
                T100106RequestList t100106list = new T100106RequestList();
                t100106list.add(recordT106);
                */
                T100102RequestRecord recordT102 = new T100102RequestRecord();
                recordT102.setStdjjh(detl.getIouno());
                recordT102.setStdqch(detl.getPoano());
                recordT102.setStdjhkkr(detl.getPaybackdate());
                //1-�ɹ� 2-ʧ��
                recordT102.setStdkkjg("1");
                T100102RequestList t100102list = new T100102RequestList();
                t100102list.add(recordT102);

                //20130109 zr �������Զ����� (�Ƚ���������ɹ����ٻ�д)
                if (unlockIntr4Overdue(detl)) {
                    //���ʷ��ʹ���
                    writebackResult = t100102ctl.start(t100102list);
                    //���ʷ��ʹ���
                    //writebackResult = t100106ctl.start(t100106list);
                } else {
                    returnMsgs.add("��Ϣ��������" + detl.getIouno() + detl.getClientname());
                }
            } else {
                returnMsgs.add("��д�Ŵ�ϵͳ�����ʵ����Ͳ�֧�֣�" + detl.getIouno() + detl.getClientname());
                throw new RuntimeException("��д�Ŵ�ϵͳ�����ʵ����Ͳ�֧��");
            }

            Date date = new Date();
            FipJoblog log = new FipJoblog();
            if (writebackResult) {
                detl.setBillstatus(BillStatus.CMS_SUCCESS.getCode());
                detl.setArchiveflag("1");
                log.setJobdesc("�Ŵ���д����ɹ�");
                count++;
            } else {
                detl.setBillstatus(BillStatus.CMS_FAILED.getCode());
                log.setJobdesc("�Ŵ���д����ʧ��");
            }
            detl.setDateCmsPut(date);
            fipCutpaydetlMapper.updateByPrimaryKey(detl);

            //������־����
            String userid = SystemService.getOperatorManager().getOperatorId();
            String username = SystemService.getOperatorManager().getOperatorName();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(detl.getPkid());
            log.setJobname("���۳ɹ���¼�Ŵ���д����");
            log.setJobtime(date);
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
        return count;
    }

    /**
     * zr
     * ��д�Ŵ�ϵͳ
     * 2013-4-11֮�����������������ڻ�д����ʧ�ܼ�¼
     */
    public int writebackCutPayRecord2CMS_ForFailureReord(List<FipCutpaydetl> cutpaydetlList, List<String> returnMsgs) {

        int count = 0;

        T100102CTL t100102ctl = new T100102CTL();
        T100104CTL t100104ctl = new T100104CTL();
        T100106CTL t100106ctl = new T100106CTL();

        FipJoblog log = new FipJoblog();
        log.setJobcode("writebackCutPayRecord2CMS_ForFailureReord");

        for (FipCutpaydetl detl : cutpaydetlList) {
            //20140122 zr  ֻ�������ķ���3008����Ľ��л�д
            if (!"3008".equals(detl.getTxRetcode())) {
                detl.setArchiveflag("1");
                log.setJobdesc("����ʧ�ܼ�¼�浵����ɹ�(δ��д�Ŵ�)");
            } else {
                boolean writebackResult = false;
                if (detl.getBilltype().equals(BillType.NORMAL.getCode())) { //��������
                    T100102RequestRecord recordT102 = new T100102RequestRecord();
                    recordT102.setStdjjh(detl.getIouno());
                    recordT102.setStdqch(detl.getPoano());
                    recordT102.setStdjhkkr(detl.getPaybackdate());
                    //1-�ɹ� 2-ʧ��
                    recordT102.setStdkkjg("2");
                    T100102RequestList t100102list = new T100102RequestList();
                    t100102list.add(recordT102);
                    //���ʷ��ʹ���
                    writebackResult = t100102ctl.start(t100102list);


                    Date date = new Date();
                    if (writebackResult) {
                        detl.setWritebackflag("1"); //�ѻ�д
                        detl.setArchiveflag("1");
                        log.setJobdesc("����ʧ�ܼ�¼�Ŵ���д�����浵����ɹ�");
                        count++;
                    } else {
                        //detl.setWritebackflag("0"); //δ��д
                        returnMsgs.add("δ���д浵����" + detl.getIouno() + detl.getClientname());
                        log.setJobdesc("����ʧ�ܼ�¼�Ŵ���д����ʧ��");
                        //throw new RuntimeException("�Ŵ���д����ʧ��");
                    }
                    detl.setDateCmsPut(date);

                } else if (detl.getBilltype().equals(BillType.PRECUTPAYMENT.getCode())) { //��ǰ���� �ݲ�����д����
                    //T100104RequestRecord recordT104 = new T100104RequestRecord();
                    //recordT104.setStdjjh(detl.getIouno());
                    //recordT104.setStdqch(detl.getPoano());
                    //recordT104.setStdjhkkr(detl.getPaybackdate());
                    ////1-�ɹ� 2-ʧ��
                    //recordT104.setStdkkjg("2");
                    //T100104RequestList t100104list = new T100104RequestList();
                    //t100104list.add(recordT104);
                    ////���ʷ��ʹ���
                    //writebackResult = t100104ctl.start(t100104list);

                    //�ݲ�����д���� Ĭ�ϳɹ�
                    detl.setArchiveflag("1");
                    log.setJobdesc("����ʧ�ܼ�¼�浵����ɹ�");
                    count++;
                } else if (detl.getBilltype().equals(BillType.OVERDUE.getCode())) { //����   �ݲ�����д����
                    //T100102RequestRecord recordT102 = new T100102RequestRecord();
                    //recordT102.setStdjjh(detl.getIouno());
                    //recordT102.setStdqch(detl.getPoano());
                    //recordT102.setStdjhkkr(detl.getPaybackdate());
                    ////1-�ɹ� 2-ʧ��
                    //recordT102.setStdkkjg("2");
                    //T100102RequestList t100102list = new T100102RequestList();
                    //t100102list.add(recordT102);

                    //�ݲ�����д���� Ĭ�ϳɹ�
                    detl.setArchiveflag("1");
                    log.setJobdesc("����ʧ�ܼ�¼�浵����ɹ�");
                    count++;
                } else {
                    returnMsgs.add("��д�Ŵ�ϵͳ�����ʵ����Ͳ�֧�֣�" + detl.getIouno() + detl.getClientname());
                    throw new RuntimeException("��д�Ŵ�ϵͳ�����ʵ����Ͳ�֧��");
                }
            }


            fipCutpaydetlMapper.updateByPrimaryKey(detl);

            //������־����
            String userid = SystemService.getOperatorManager().getOperatorId();
            String username = SystemService.getOperatorManager().getOperatorName();
            log.setTablename("fip_cutpaydetl");
            log.setRowpkid(detl.getPkid());
            log.setJobname("����ʧ�ܼ�¼�Ŵ���д���鵵����");
            log.setJobtime(new Date());
            log.setJobuserid(userid);
            log.setJobusername(username);
            fipJoblogMapper.insert(log);
        }
        return count;
    }

    /**
     * �������뵥�� ��ȡ CMSϵͳ��ά���� �̻���Ϣ
     *
     * @return
     */
    @Transactional
    public void obtainMerchantActnoFromCms(List<FipCutpaydetl> cutpaydetlList) {
        T201003CTL ctl = new T201003CTL();

        Date date = new Date();
        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog log = new FipJoblog();

        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            cutpaydetl.getPkid();
            Map<String, String> rtnMap = ctl.startQry(cutpaydetl.getAppno());
            if (StringUtils.isEmpty(rtnMap.get("stdkkzh"))) {
                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(cutpaydetl.getPkid());
                log.setJobname("��ȡ�̻���Ϣ");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                log.setJobdesc("��ȡ�̻���Ϣ����" + rtnMap.get("stdcwxx"));
                fipJoblogMapper.insert(log);
                continue;
                //throw new RuntimeException(rtnMap.get("stdcwxx"));
            }

            FipCutpaydetl cutpaydetlDB = fipCutpaydetlMapper.selectByPrimaryKey(cutpaydetl.getPkid());
            Long recversion = cutpaydetl.getRecversion();
            if (cutpaydetlDB.getRecversion().equals(recversion)) {
                cutpaydetl.setMerchantName(rtnMap.get("stdshmc"));
                cutpaydetl.setMerchantActno(rtnMap.get("stdkkzh"));
                cutpaydetl.setRecversion(++recversion);
                fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);

                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(cutpaydetl.getPkid());
                log.setJobname("��ȡ�̻���Ϣ");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                log.setJobdesc("��ȡ�̻���Ϣ�ɹ�" + cutpaydetl.getMerchantName() + cutpaydetl.getMerchantActno());
                fipJoblogMapper.insert(log);
            } else {
                log.setTablename("fip_cutpaydetl");
                log.setRowpkid(cutpaydetl.getPkid());
                log.setJobname("��ȡ�̻���Ϣ");
                log.setJobtime(date);
                log.setJobuserid(userid);
                log.setJobusername(username);
                log.setJobdesc("��ȡ�̻���Ϣʧ�ܣ�������ͻ." + cutpaydetl.getMerchantName() + cutpaydetl.getMerchantActno());
                fipJoblogMapper.insert(log);
            }
        }
    }

    //���ڽ�������¼������־
    //����������ڱ�service�ⲿ����ʱ�������ã�
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean unlockIntr4Overdue(FipCutpaydetl detl) {
        boolean unlockResult = lockOrUnlockIntr4Overdue(detl.getIouno(), "2");

        Date date = new Date();
        FipJoblog log = new FipJoblog();
        if (unlockResult) {
            log.setJobdesc("�Ŵ���Ϣ��������ɹ�.");
        } else {
            detl.setBillstatus(BillStatus.CMS_FAILED.getCode());
            log.setJobdesc("�Ŵ���Ϣ��������ʧ��.");
            logger.error("��Ϣ��������" + detl.getIouno() + detl.getClientname());
        }

        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        log.setTablename("fip_cutpaydetl");
        log.setRowpkid(detl.getPkid());
        log.setJobname("�Ŵ���Ϣ��������");
        log.setJobtime(date);
        log.setJobuserid(userid);
        log.setJobusername(username);
        fipJoblogMapper.insert(log);
        return unlockResult;
    }
    //==================================20121120  ��Ϣ�Զ��ӽ�������==================================


    /**
     * �����������ڷ�Ϣ
     *
     * @param iouno  ��ݺ�
     * @param option 1-���� 2-����
     * @return �ɹ�����
     */
    private boolean lockOrUnlockIntr4Overdue(String iouno, String option) {
        if ((!"1".equals(option))
                && (!"2".equals(option))
                ) {
            throw new RuntimeException("��������");
        }

        T100108CTL t100108ctl = new T100108CTL();
        T100108RequestRecord record = new T100108RequestRecord();
        record.setStdjjh(iouno);
        //1-���� 2-����
        record.setStdkkjg(option);
        T100108RequestList list = new T100108RequestList();
        list.add(record);
        //���ʷ��ʹ���
        return t100108ctl.start(list);
    }
}
