package fip.service.fip;

import fip.common.constant.*;
import fip.gateway.sbs.DepCtgManager;
import fip.repository.model.FipPayoutbat;
import fip.repository.model.FipPayoutdetl;
import org.apache.commons.lang.StringUtils;
import org.fbi.dep.model.txn.TOA1002001;
import org.fbi.dep.model.txn.TOA1003001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/*
* sbs-n057 -> unionpay 1002001 -> sbs-n058/n059
 */
@Service
@Deprecated

public class PayoutTxnService {
    private static final Logger logger = LoggerFactory.getLogger(PayoutTxnService.class);

    @Autowired
    private PayoutbatService payoutbatService;
    @Autowired
    private PayoutDetlService payoutDetlService;
    @Autowired
    private UnipayDepService unipayDepService;
    @Autowired
    private JobLogService jobLogService;

    // ��ROUND-1����sbs���� N057
    public int processN057(List<FipPayoutbat> payoutbatList) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        int failCnt = 0;
        int sucCnt = 0;
        for (FipPayoutbat bat : payoutbatList) {
            List<FipPayoutdetl> detlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.INIT);
            if (payoutbatService.isNoTxnStepClash(bat)) {
                for (FipPayoutdetl detl : detlList) {
                    // sbs n057 ����
                    List<String> paramlist = assembleTn057Param(detl);
                    // sbs����,;��dep

                    byte[] sbsResBytes = DepCtgManager.processSingleResponsePkg("n057", paramlist);
                    logger.info(new String(sbsResBytes));

                    String formcode = new String(sbsResBytes, 21, 4);
                    String rtnWsysSn = new String(sbsResBytes, 72, 18).trim();
                    jobLogService.insertNewJoblog(detl.getPkid(), "fip_payoutdetl", detl.getReqSn() + detl.getSn() + "SBSN057", "SBS������:" + formcode, "Haierfip", "�ʽ𽻻�ƽ̨");

                    if (!"T531".equals(formcode)) {
                        // ���׽�����ʧ��
                        detl.setRetCode(PayoutDetlRtnCode.FAIL.getCode());
                        detl.setErrMsg(PayoutDetlRtnCode.FAIL.getTitle());
                        failCnt++;
                        String errmsg = "[SBS���ش�����Ϣ: " + formcode + " " + getSBSErrMsgFromResponse(sbsResBytes) + " ]";
                        logger.error("SBSͨѶ���Ľ�������" + errmsg);
                    } else
                    // �ж���ˮ���Ƿ�һ��
                    if (!rtnWsysSn.equals(detl.getReqSn() + detl.getSn())) {
                        String errmsg = "[������Ϣ: ��ˮ�Ų�һ��," + "]";
                        throw new RuntimeException("SBS���Ľ�������" + errmsg);
                    }
                    sucCnt++;
                    detl.setSbsTxnTime(sdf);
                    payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.SBSN057);
                }
                // ���д��� n057 ��ʧ�ܣ���ñʴ�����ʧ�ܣ����跢������
                if ((failCnt != 0) && (failCnt == detlList.size())) {
                    bat.setRetCode(PayoutBatRtnCode.TXN_OVER.getCode());
                    bat.setErrMsg(PayoutBatRtnCode.TXN_OVER.getTitle());
                }
                // ״̬����
                bat.setSbsTxnTime(sdf);
                bat.setRemark(PayoutDetlTxnStep.SBSN057.toRtnMsg());
                payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.SBSN057);
            } else {
                throw new RuntimeException("sbs-n057�������׳�ͻ��������ˮ�ţ�" + bat.getReqSn());
            }
        }
        return sucCnt;
    }

    // ��ROUND-2����ͨ��dep������������
    public int processUnionpayPayout(List<FipPayoutbat> payoutbatList) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        int sucCnt = 0;
        for (FipPayoutbat bat : payoutbatList) {
            List<FipPayoutdetl> detlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.SBSN057);
            int failCnt = 0;
            if (payoutbatService.isNoTxnStepClash(bat)) {
                for (FipPayoutdetl detl : detlList) {

                    TOA1002001 toa = unipayDepService.sendAndRecvT1002001Message(bat, detl);
                    jobLogService.insertNewJoblog(detl.getPkid(), "fip_payoutdetl", detl.getReqSn() + detl.getSn() + "[Dep1002001]",
                            "��������:" + toa.header.RETURN_CODE + toa.header.RETURN_MSG, "Haierfip", "�ʽ𽻻�ƽ̨");

                    if (!DepUnipayTxnStatus.TXN_SUCCESS.getCode().equals(toa.header.RETURN_CODE)) {
                        // ���׽�����ʧ�ܣ���ִ�� N059
                        failCnt++;
                        String errmsg = "[���ش�����Ϣ: " + toa.header.RETURN_CODE + toa.header.RETURN_MSG + "]";
                        detl.setUnionpayTxnTime(sdf);
                        payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.UNIONPAY_PAYOUT_FAIL);
                        logger.error("�������ش���" + errmsg);
                    } else {
                        detl.setUnionpayTxnTime(sdf);
                        sucCnt += payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.SENT_UNIONPAY);
                    }
                }
                // ���д�����ʧ�ܣ���ñʴ�����ʧ�ܣ�batʧ��
                if ((failCnt != 0) && (failCnt == detlList.size())) {
                    // ״̬����
                    bat.setUnionpayTxnTime(sdf);
                    bat.setRemark(PayoutBatTxnStep.UNIONPAY_TXN_OVER.toRtnMsg());
                    payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.UNIONPAY_TXN_OVER);
                } else {
                    // ״̬����
                    bat.setUnionpayTxnTime(sdf);
//                bat.setUnionpayErrMsg();
                    bat.setRemark(PayoutDetlTxnStep.SENT_UNIONPAY.toRtnMsg());
                    payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.UNIONPAY_TXN_PAYOUT);
                }
            } else {
                throw new RuntimeException("�������������������׳�ͻ��������ˮ�ţ�" + bat.getReqSn());
            }
        }
        return sucCnt;
    }

    // ��ROUND-3����ͨ��dep����������ѯ
    @Transactional
    public void processUnionpayQry(List<FipPayoutbat> payoutbatList) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        for (FipPayoutbat bat : payoutbatList) {
            List<FipPayoutdetl> detlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.SENT_UNIONPAY);
            int unknownCnt = 0;
            if (payoutbatService.isNoTxnStepClash(bat)) {
                for (FipPayoutdetl detl : detlList) {

                    TOA1003001 toa = unipayDepService.sendAndRecvPayoutT1003001Message(bat, detl);
                    jobLogService.insertNewJoblog(detl.getPkid(), "fip_payoutdetl", detl.getReqSn() + detl.getSn() + "[Dep1003001]",
                            "��������:" + toa.header.RETURN_CODE + toa.header.RETURN_MSG, "Haierfip", "�ʽ𽻻�ƽ̨");
                    detl.setQryrtnTime(sdf);
                    // �������ɹ�
                    if (DepUnipayTxnStatus.TXN_SUCCESS.getCode().equals(toa.header.RETURN_CODE)) {
                        detl.setQryrtnAccountNo(toa.body.ACCOUNT_NO);
                        detl.setQryrtnAccountName(toa.body.ACCOUNT_NAME);
                        detl.setQryrtnAmount(toa.body.AMOUNT.toString());
                        detl.setQryrtnRetCode(toa.header.RETURN_CODE);
                        detl.setQryrtnErrMsg(toa.header.RETURN_MSG);
                        detl.setQryrtnRemark(toa.body.REMARK);
                        payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.UNIONPAY_PAYOUT_SUCCESS);
                    } else if (DepUnipayTxnStatus.TXN_FAILED.getCode().equals(toa.header.RETURN_CODE)) { // ʧ��
                        detl.setQryrtnAccountNo(toa.body.ACCOUNT_NO);
                        detl.setQryrtnAccountName(toa.body.ACCOUNT_NAME);
                        detl.setQryrtnAmount(toa.body.AMOUNT.toString());
                        detl.setQryrtnRetCode(toa.header.RETURN_CODE);
                        detl.setQryrtnErrMsg(toa.header.RETURN_MSG);
                        detl.setQryrtnRemark(toa.body.REMARK);
                        String errmsg = "[���ش�����Ϣ: " + toa.header.RETURN_CODE + toa.header.RETURN_MSG + "]";
                        logger.error("�������ش���" + errmsg);
                        payoutDetlService.updatePayoutDetlTxnStep(detl, PayoutDetlTxnStep.UNIONPAY_PAYOUT_FAIL);

                    } else if (DepUnipayTxnStatus.TXN_QRY_PEND.getCode().equals(toa.header.RETURN_CODE)) { // ����
                        // �������������ҵ�������ٴβ�ѯ
                        unknownCnt++;
                    }
                }

                // û�в�������Ľ���
                if (unknownCnt == 0) {
                    bat.setRemark(PayoutBatTxnStep.UNIONPAY_TXN_OVER.toRtnMsg());
                    payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.UNIONPAY_TXN_OVER);
                } else {
                    // ���ٴβ�ѯ
                }
            } else {
                throw new RuntimeException("��ѯ���������������׳�ͻ��������ˮ�ţ�" + bat.getReqSn());
            }
        }
    }

    // ��ROUND-4����ͨ��dep����n058��n059����
    public int processSbsPayoutConfirm(List<FipPayoutbat> payoutbatList) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        int sucCnt = 0;
        for (FipPayoutbat bat : payoutbatList) {
            List<FipPayoutdetl> sucDetlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.UNIONPAY_PAYOUT_SUCCESS);
            int cnt = 0;
            for (FipPayoutdetl record : sucDetlList) {
                // n058
                cnt += processN058(record);
                sucCnt++;
            }
            List<FipPayoutdetl> failDetlList = payoutDetlService.qryRecords(bat.getReqSn(), PayoutDetlRtnCode.HALFWAY, PayoutDetlTxnStep.UNIONPAY_PAYOUT_FAIL);
            for (FipPayoutdetl record : failDetlList) {
                // n059
                cnt += processN059(record);
            }
            // ȷ��batȫ�����׽���
            if (cnt != 0 && cnt == (sucDetlList.size() + failDetlList.size())) {
                bat.setRetCode(PayoutBatRtnCode.TXN_OVER.getCode());
                bat.setErrMsg(PayoutBatRtnCode.TXN_OVER.getTitle());
                bat.setRemark(PayoutBatTxnStep.ALL_TXN_OVER.toRtnMsg());
                bat.setSbsTxnTime(sdf);
                payoutbatService.updatePayoutbatTxnStep(bat, PayoutBatTxnStep.ALL_TXN_OVER);
            }
        }
        return sucCnt;
    }

    @Transactional
    private int processN058(FipPayoutdetl record) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        if (payoutDetlService.isNoTxnStepClash(record)) {
            // sbs n058 ����
            List<String> paramlist = assembleTn058Param(record);
            // sbs����,;��dep
            byte[] sbsResBytes = DepCtgManager.processSingleResponsePkg("n058", paramlist);
            logger.info(new String(sbsResBytes));

            String formcode = new String(sbsResBytes, 21, 4);
            String rtnWsysSn = new String(sbsResBytes, 72, 18).trim();
            jobLogService.insertNewJoblog(record.getPkid(), "fip_payoutdetl", record.getReqSn() + record.getSn() + "SBSN058",
                    "SBS����:" + formcode, "Haierfip", "�ʽ𽻻�ƽ̨");

            if (!"T531".equals(formcode)) {
                // ����ʧ�ܣ�����¼��־�⣬�����κδ������ٴη���n058���������ȷ��
                String errmsg = "[SBS���ش�����Ϣ: " + formcode + " " + getSBSErrMsgFromResponse(sbsResBytes) + " ]";
                logger.error("SBSͨѶ���Ľ�������" + errmsg);
                return 0;
            } else {
                // �ж���ˮ���Ƿ�һ��
                if (!rtnWsysSn.equals(record.getReqSn() + record.getSn())) {
                    String errmsg = "[������Ϣ: ��ˮ�Ų�һ��,sbs:" + rtnWsysSn + "]";
                    throw new RuntimeException("SBS���Ľ�������" + errmsg);
                }
                record.setSbsTxnTime(sdf);
                record.setRetCode(PayoutDetlRtnCode.SUCCESS.getCode());
                record.setErrMsg(PayoutDetlRtnCode.SUCCESS.getTitle());
                return payoutDetlService.updatePayoutDetlTxnStep(record, PayoutDetlTxnStep.SBSN058);
            }
        } else {
            throw new RuntimeException("sbs-n058�������׳�ͻ����ˮ�ţ�" + record.getReqSn() + record.getSn());
        }
    }

    @Transactional
    private int processN059(FipPayoutdetl record) {
        String sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss").format(new Date());
        if (payoutDetlService.isNoTxnStepClash(record)) {
            // sbs n059 ����
            List<String> paramlist = assembleTn059Param(record);
            // sbs����,;��dep
            byte[] sbsResBytes = DepCtgManager.processSingleResponsePkg("n059", paramlist);
            logger.info(new String(sbsResBytes));

            String formcode = new String(sbsResBytes, 21, 4);
            String rtnWsysSn = new String(sbsResBytes, 72, 18).trim();
            jobLogService.insertNewJoblog(record.getPkid(), "fip_payoutdetl", record.getReqSn() + record.getSn() + "SBSN059",
                    "SBS����:" + formcode, "Haierfip", "�ʽ𽻻�ƽ̨");
            if (!"T531".equals(formcode)) {
                // ����ʧ�ܣ�����¼��־�⣬�����κδ������ٴη���n059���������ȷ��
                String errmsg = "[SBS���ش�����Ϣ: " + formcode + " " + getSBSErrMsgFromResponse(sbsResBytes) + " ]";
                logger.error("SBSͨѶ���Ľ�������" + errmsg);
                return 0;
            } else {
                // �ж���ˮ���Ƿ�һ��
                if (!rtnWsysSn.equals(record.getReqSn() + record.getSn())) {
                    String errmsg = "[������Ϣ: ��ˮ�Ų�һ��,sbs:" + rtnWsysSn + "]";
                    throw new RuntimeException("SBS���Ľ�������" + errmsg);
                }
                record.setSbsTxnTime(sdf);
                record.setRetCode(PayoutDetlRtnCode.FAIL.getCode());
                record.setErrMsg(PayoutDetlRtnCode.FAIL.getTitle());
                return payoutDetlService.updatePayoutDetlTxnStep(record, PayoutDetlTxnStep.SBSN059);
            }
        } else {
            throw new RuntimeException("sbs-n059�������׳�ͻ����ˮ�ţ�" + record.getReqSn() + record.getSn());
        }
    }

    // ��װn058���� ͬn057
    private List<String> assembleTn058Param(FipPayoutdetl detail) {
        return assembleTn057Param(detail);
    }

    // ��װn059����  ͬn057
    private List<String> assembleTn059Param(FipPayoutdetl detail) {
        return assembleTn057Param(detail);
    }

    // ��װn057����
    private List<String> assembleTn057Param(FipPayoutdetl detail) {
        List<String> txnparamList = new ArrayList<String>();
        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        DecimalFormat df = new DecimalFormat("#############0.00");
        String sbsActno = detail.getSbsAccountNo();

        // 1 ��Χϵͳ��ˮ��  18 ����ˮ��+��¼���
        String sn = detail.getReqSn() + detail.getSn();
        if (sn.length() > 18) {
            sn = sn.substring(sn.length() - 18, sn.length());
        } else {
            sn = StringUtils.rightPad(sn, 18, " ");
        }
        txnparamList.add(sn);
        // 2 ���� 010
        txnparamList.add("010");
        // 3 ί������ 8
        txnparamList.add(txndate);
        // 4 �ͻ��� 7λ
        String cusidt = sbsActno.startsWith("8010") ? sbsActno.substring(4, 11) : sbsActno.substring(0, 7);
        txnparamList.add(cusidt);
        // 5 �������� CPY����������
        txnparamList.add("CPY");
        // 6 ���׻��� 001-�����
        txnparamList.add("001");
        // 7 ���׽�� �����ӿ��н�λ�Ƿ֣�sbs���׽�λ��Ԫ
        txnparamList.add(df.format(new BigDecimal(detail.getAmount()).divide(new BigDecimal(100.0))));
        // 8 ������� M���Ż� T�����
        txnparamList.add("T");
        // 9 ����˻����� 01
        txnparamList.add("01");
        // 10 �����ʻ� 8010
        txnparamList.add(sbsActno.startsWith("8010") ? sbsActno : "8010" + sbsActno);
        // 11 �����ʻ����� 1
        txnparamList.add("1");
        // 12 �����˻�
        txnparamList.add(" ");
        // 13 �տ����˺�
        txnparamList.add(detail.getAccountNo());
        // 14 �տ�������
        txnparamList.add(detail.getAccountName());
        // 15 �տ�������
        txnparamList.add(" ");
        // 16 ����������
        txnparamList.add(" ");
        // 17 �����˺�
        txnparamList.add(" ");
        // 18 ���������
        txnparamList.add(" ");
        // 19 �����;
        txnparamList.add(StringUtils.isEmpty(detail.getRemark()) ? detail.getReqSn() + detail.getSn() + "��������" : detail.getRemark());
        // 20 ֧Ʊ����
        txnparamList.add(" ");
        // 21 �տ����к� 12λ
        txnparamList.add("            ");
        // 22 ֧Ʊ����	X(10)	�̶�ֵ	�ո�
        txnparamList.add("          ");
        // 23 ������	X(1)	�̶�ֵ	�ո�
        txnparamList.add(" ");
        // 24 FS��ˮ�� ����
        txnparamList.add(" ");
        // 25 ������ˮ�� ����
        txnparamList.add(" ");
        return txnparamList;
    }

    /*
      ��ȡsbs����Ӧ�����еĴ�����Ϣ
    */
    private String getSBSErrMsgFromResponse(byte[] buffer) {

        byte[] bLen = new byte[2];
        System.arraycopy(buffer, 27, bLen, 0, 2);
        short iLen = (short) (((bLen[0] << 8) | bLen[1] & 0xff));
        byte[] bLog = new byte[iLen];
        System.arraycopy(buffer, 29, bLog, 0, iLen);
        String log = new String(bLog);
        log = StringUtils.trimToEmpty(log);

        return log;
    }
}
