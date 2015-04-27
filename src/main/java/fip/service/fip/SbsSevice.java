package fip.service.fip;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.txn.Ta543.Ta543Handler;
import fip.gateway.sbs.txn.Ta543.Ta543SOFDataDetail;
import fip.gateway.sbs.txn.Taa56.Taa56Handler;
import fip.gateway.sbs.txn.Taa56.Taa56SOFDataDetail;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipInterbankinfoMapper;
import fip.repository.dao.FipJoblogMapper;
import fip.repository.dao.XfapprepaymentMapper;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipInterbankinfo;
import fip.repository.model.FipInterbankinfoExample;
import fip.repository.model.FipJoblog;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-8-17
 * Time: ����11:23
 * To change this template use File | Settings | File Templates.
 */
@Service
public class SbsSevice {
    private static final Logger logger = LoggerFactory.getLogger(SbsSevice.class);

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private FipCutpaydetlMapper fipCutpaydetlMapper;
    @Autowired
    private FipJoblogMapper fipJoblogMapper;
    @Autowired
    private XfapprepaymentMapper xfapprepaymentMapper;
    @Autowired
    private FipInterbankinfoMapper fipInterbankinfoMapper;


    /**
     * ����ǰ���н��˶�
     * @param cutpaydetlList
     */
    public synchronized  void checkAmt4PendAccountRecord(List<FipCutpaydetl> cutpaydetlList) {
        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            BigDecimal totalamt = cutpaydetl.getPaybackamt();
            //TODO ��ʱ���˶Է�Ϣ���� (��Ҫ���������ʵĽ���ֶμ���)
            BigDecimal accountAmt = cutpaydetl.getPrincipalamt().add(cutpaydetl.getInterestamt())
                    .add(cutpaydetl.getPunitiveintamt()).add(cutpaydetl.getCompoundintamt());
            //BigDecimal accountAmt = cutpaydetl.getPrincipalamt().add(cutpaydetl.getInterestamt());
            if (totalamt.subtract(accountAmt).floatValue() != 0 ) {
                logger.error("���ʵ��ܽ������ϸ����������+��Ϣ+��Ϣ+������" + cutpaydetl.getClientname());
                throw new RuntimeException("���ʵ��ܽ������ϸ����������+��Ϣ+��Ϣ+������" + cutpaydetl.getClientname());
            }
        }

    }

    //@Transactional  ����ʱ��������������
    public synchronized void accountCutPayRecord2SBS(List<FipCutpaydetl> cutpaydetlList) {

        //TODO �����
        checkAmt4PendAccountRecord(cutpaydetlList);

        //TODO ���״̬

        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog joblog = new FipJoblog();

        //��������
        Ta543Handler handler = new Ta543Handler();

        try {
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                List<String> txnparamList = assembleTa543Param(cutpaydetl);

                SBSRequest request = new SBSRequest("a543", txnparamList);
                SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
                Ta543SOFDataDetail sofDataDetail = new Ta543SOFDataDetail();
                response.setSofDataDetail(sofDataDetail);

                handler.run(request, response);

                String formcode = response.getFormcode();
                logger.debug("formcode:" + formcode);

                if (!formcode.equals("T531")) {     //�쳣�������
                    cutpaydetl.setBillstatus(BillStatus.ACCOUNT_FAILED.getCode());
                    joblog.setJobdesc("SBS����ʧ�ܣ�FORMCODE=" + formcode);
                } else {
                    if (sofDataDetail.getSECNUM().trim().equals(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn())) {
                        joblog.setJobdesc("SBS������ɣ�FORMCODE=" + formcode + " ͬҵ�ʺ�:" + cutpaydetl.getSbsInterbankActno());
                        cutpaydetl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                        cutpaydetl.setDateSbsAct(new Date());
                    } else {
                        logger.error("SBS�������,�����ص���ˮ�ų������ѯ��" + cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());
                        joblog.setJobdesc("SBS�������,�����ص���ˮ�ų������ѯ��" + sofDataDetail.getSECNUM());
                        cutpaydetl.setBillstatus(BillStatus.ACCOUNT_PEND.getCode());
                        cutpaydetl.setDateSbsAct(new Date());
                    }
                }
                joblog.setTablename("fip_cutpaydetl");
                joblog.setRowpkid(cutpaydetl.getPkid());
                joblog.setJobname("SBS����");
                joblog.setJobtime(new Date());
                joblog.setJobuserid(userid);
                joblog.setJobusername(username);
                fipJoblogMapper.insert(joblog);
                fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }
        } catch (Exception e) {
            logger.error("����ʱ���ִ���", e);
            throw new RuntimeException("����ʱ���ִ���", e);
        } finally {
            handler.shoudown();
        }
    }

    /**
     * �Ŵ��ۿ�SBS���ʽ���TIA����  a543
     *
     * @param cutpaydetl
     * @return
     */
    private List<String> assembleTa543Param(FipCutpaydetl cutpaydetl) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();

        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String interbankAccount = getInterbankAccount(cutpaydetl);
        cutpaydetl.setSbsInterbankActno(interbankAccount);

        //1 ��������
        txnparamList.add(txndate);

        //2 ��Χϵͳ��ˮ��
        txnparamList.add(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());

        //3 �����˺�
        txnparamList.add(cutpaydetl.getClientact());

        //20120503 zhanrui  �������ڴ��� ��Ϣ������ʱ�鵽��Ϣ��
        if (cutpaydetl.getOriginBizid().equals(BizType.FD.getCode())) {   //ס������
            //4 ������   S9(13).99
            String amt = df.format(cutpaydetl.getPrincipalamt());
            txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));
            //5 ΥԼ�����Ϣ/Ӧ��Ϣ��  �������ڴ��� ��Ϣ������ʱ�鵽��Ϣ��
            String interestamt = df.format(cutpaydetl.getInterestamt()
                    .add(cutpaydetl.getPunitiveintamt())
                    .add(cutpaydetl.getCompoundintamt()));
            txnparamList.add("+" + StringUtils.leftPad(interestamt, 16, '0'));
            //6 ���ɽ����Ϣ��
            txnparamList.add("+" + StringUtils.leftPad("0.00", 16, '0'));
            //7 �����ѽ�������
            txnparamList.add("+" + StringUtils.leftPad("0.00", 16, '0'));
            //8 ժҪ
            txnparamList.add(StringUtils.leftPad("FangDai", 30, ' '));
            //9 ���𻹿��˺�  ���뻹������˺Ż�����ͬҵ�˺�
            txnparamList.add(interbankAccount); //ͬҵ�˺�
            //10 �������� (04��˽���Ѵ��� 05��˽���Ҵ���)
            txnparamList.add("05");
        } else if (cutpaydetl.getOriginBizid().equals(BizType.XF.getCode())) {   //�������Ŵ�
            //4 ������   S9(13).99
            String amt = df.format(cutpaydetl.getPrincipalamt());
            txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));
            //5 ΥԼ�����Ϣ/Ӧ��Ϣ��
            txnparamList.add("+" + StringUtils.leftPad("0.00", 16, '0'));
            //6 ���ɽ����Ϣ��
            txnparamList.add("+" + StringUtils.leftPad("0.00", 16, '0'));
            //7 �����ѽ�������
            String interestamt = df.format(cutpaydetl.getInterestamt()
                    .add(cutpaydetl.getPunitiveintamt())
                    .add(cutpaydetl.getCompoundintamt()));
            txnparamList.add("+" + StringUtils.leftPad(interestamt, 16, '0'));
            //8 ժҪ
            txnparamList.add(StringUtils.leftPad("XiaoFei", 30, ' '));
            //9 ���𻹿��˺�  ���뻹������˺Ż�����ͬҵ�˺�
            txnparamList.add(interbankAccount); //ͬҵ�˺�
            //10 �������� 04��˽���Ѵ��� 05��˽���Ҵ���
            txnparamList.add("04");
        } else {
            throw new RuntimeException("��֧�ֵ�ҵ��Ʒ�֡�");
        }

        //11 ��Ϣ�����˺�  ���뻹������˺Ż�����ͬҵ�˺�
        txnparamList.add(interbankAccount); //ͬҵ�˺�

        return txnparamList;
    }


    @Deprecated
    private List<String> assembleTa541Param(FipCutpaydetl cutpaydetl) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();

        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        txnparamList.add(txndate);          //��������
        txnparamList.add(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());      //������ˮ��

        String account = StringUtils.rightPad(cutpaydetl.getClientact(), 22, ' ');
        txnparamList.add(account);//�����ʻ�                 22λ�����㲹�ո�


        String principal = df.format(cutpaydetl.getPrincipalamt());
        String interest = df.format(cutpaydetl.getInterestamt());
        //String punitiveint = df.format(cutpaydetl.getPunitiveintamt());  //���ɽ� = ��Ϣ

        if (cutpaydetl.getBilltype().equals("0")) { //�����ʵ�
            txnparamList.add("+" + StringUtils.leftPad(principal, 16, '0'));     //������
            txnparamList.add("+0000000000000.00");     //ΥԼ����
            txnparamList.add("+0000000000000.00");     //���ɽ���
            txnparamList.add("+" + StringUtils.leftPad(interest, 16, '0'));     //�����ѽ��
        } else {
            //TODO
        }
        //TODO: ������
        String digest = "   ";

        CutpayChannel channel = CutpayChannel.valueOfAlias(cutpaydetl.getBiChannel());
        //����ҵ��
        if (BizType.FD.getCode().equals(cutpaydetl.getOriginBizid())) {
            switch (channel) {
                case NONE:
                    digest = cutpaydetl.getBiActopeningbank();
                    break;
                case UNIPAY:
                    digest = "905"; //��������
                    break;
                default:
            }
        }
        if (BizType.XF.getCode().equals(cutpaydetl.getOriginBizid())) {
            switch (channel) {
                case NONE:
                    digest = cutpaydetl.getBiActopeningbank();
                    break;
                case UNIPAY:
                    digest = "905"; //��������
                    break;
                default:
            }
        }

        txnparamList.add(digest + "                           ");//ժҪ
        return txnparamList;
    }


    /**
     * ���ѷ��� �׸�����  aa56����  (����ͬҵ�ʻ����̻��ʻ�֮���ת��)
     *
     * @param cutpaydetlList
     */
    //@Transactional
    public synchronized void accountPrepayRecord2SBS(List<FipCutpaydetl> cutpaydetlList) {
        //TODO �����
        checkAmt4PendAccountRecord(cutpaydetlList);

        String userid = SystemService.getOperatorManager().getOperatorId();
        String username = SystemService.getOperatorManager().getOperatorName();
        FipJoblog joblog = new FipJoblog();

        //��������
        Taa56Handler handler = new Taa56Handler();

        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            List<String> txnparamList = assembleTaa56Param(cutpaydetl);

            SBSRequest request = new SBSRequest("aa56", txnparamList);
            SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
            Taa56SOFDataDetail sofDataDetail = new Taa56SOFDataDetail();
            response.setSofDataDetail(sofDataDetail);

            handler.run(request, response);

            String formcode = response.getFormcode();
            logger.debug("formcode:" + formcode);
            if (!formcode.equals("T531")) {     //�쳣�������
                cutpaydetl.setBillstatus(BillStatus.ACCOUNT_FAILED.getCode());
                joblog.setJobdesc("SBS����ʧ�ܣ�FORMCODE=" + formcode);
            } else {
                if (sofDataDetail.getSECNUM().trim().equals(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn())) {
                    cutpaydetl.setBillstatus(BillStatus.ACCOUNT_SUCCESS.getCode());
                    cutpaydetl.setDateSbsAct(new Date());
                    joblog.setJobdesc("SBS���ʳɹ���FORMCODE=" + formcode + " ͬҵ�ʺ�:" + cutpaydetl.getSbsInterbankActno());
                } else {
                    logger.error("SBS���ʳɹ�,�����ص���ˮ�ų������ѯ��" + cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());
                    joblog.setJobdesc("SBS���ʳɹ�,�����ص���ˮ�ų������ѯ��" + sofDataDetail.getSECNUM());
                    cutpaydetl.setBillstatus(BillStatus.ACCOUNT_PEND.getCode());
                    cutpaydetl.setDateSbsAct(new Date());
                }
            }
            joblog.setTablename("fip_cutpaydetl");
            joblog.setRowpkid(cutpaydetl.getPkid());
            joblog.setJobname("SBS����");
            joblog.setJobtime(new Date());
            joblog.setJobuserid(userid);
            joblog.setJobusername(username);
            fipJoblogMapper.insert(joblog);
            fipCutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
        }
    }

    private List<String> assembleTaa56Param(FipCutpaydetl cutpaydetl) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();

        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());


        //ת���ʻ�����
        txnparamList.add("01");

        String interbankAccount = getInterbankAccount(cutpaydetl);
        cutpaydetl.setSbsInterbankActno(interbankAccount);

        //ת���ʻ�
        txnparamList.add(interbankAccount); //ͬҵ�˺�
        //ȡ�ʽ
        txnparamList.add("3");
        //ת���ʻ�����
        txnparamList.add(StringUtils.leftPad("", 72, ' '));
        //ȡ������
        txnparamList.add(StringUtils.leftPad("", 6, ' '));

        //֤������
        txnparamList.add("N");
        //��Χϵͳ��ˮ��
        txnparamList.add(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());      //������ˮ��
        //֧Ʊ����
        txnparamList.add(" ");
        //֧Ʊ��
        txnparamList.add(StringUtils.leftPad("", 10, ' '));
        //֧Ʊ����
        txnparamList.add(StringUtils.leftPad("", 12, ' '));

        //ǩ������
        txnparamList.add(StringUtils.leftPad("", 8, ' '));
        //���۱�ʶ
        txnparamList.add("3");
        //�����ֶ�
        txnparamList.add(StringUtils.leftPad("", 8, ' '));
        //�����ֶ�
        txnparamList.add(StringUtils.leftPad("", 4, ' '));

        //���׽��
        String amt = df.format(cutpaydetl.getPaybackamt());
        txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));   //���

        //ת���ʻ�����
        txnparamList.add("01");

        //ת���ʻ� (�̻��ʺ�)
        String account = StringUtils.rightPad(cutpaydetl.getMerchantActno(), 22, ' ');
        txnparamList.add(account);

        //ת���ʻ�����
        txnparamList.add(StringUtils.leftPad("", 72, ' '));
        //���۱�ʶ
        txnparamList.add(" ");
        //��������
        txnparamList.add(txndate);

        //ժҪ
        txnparamList.add(StringUtils.leftPad("", 30, ' '));
        //��Ʒ��
        txnparamList.add(StringUtils.leftPad("", 4, ' '));
        //MAGFL1
        txnparamList.add(" ");
        //MAGFL2
        txnparamList.add(" ");
        //��������
        txnparamList.add("   ");
        return txnparamList;
    }

    /**
     * ����ÿ�ʴ��ۼ�¼�еĴ�����Ϣ ��ȡͬҵ�ʺ���Ϣ  ��Ҫ����
     *
     * @param cutpaydetl
     * @return
     */
    private String getInterbankAccount(final FipCutpaydetl cutpaydetl) {
        FipInterbankinfoExample interbankinfoExample = new FipInterbankinfoExample();
        if (cutpaydetl.getBiChannel().equals(CutpayChannel.NONE.getCode())) {//��ͨ��������֧����������ʱ
            interbankinfoExample.createCriteria()
                    .andBizidEqualTo(cutpaydetl.getOriginBizid())
                    .andChannelidEqualTo(cutpaydetl.getBiChannel())
                    .andBankidEqualTo(cutpaydetl.getBiActopeningbank());
            List<FipInterbankinfo> infos = fipInterbankinfoMapper.selectByExample(interbankinfoExample);
            if (infos.size() != 1) {
                throw new RuntimeException("ͬҵ�ʺŲ��Ҵ���!");
            }
            return infos.get(0).getActno();
        } else {//������Ϊ00ʱ����ͨ������֧�����Ƚ��д��ۣ� ������Ϣ����������޹�
            interbankinfoExample.createCriteria()
                    .andBizidEqualTo(cutpaydetl.getOriginBizid())
                    .andChannelidEqualTo(cutpaydetl.getBiChannel());
            List<FipInterbankinfo> infos = fipInterbankinfoMapper.selectByExample(interbankinfoExample);
            if (infos.size() != 1) {
                throw new RuntimeException("ͬҵ�ʺŲ��Ҵ���!");
            }
            return infos.get(0).getActno();
        }
    }
}
