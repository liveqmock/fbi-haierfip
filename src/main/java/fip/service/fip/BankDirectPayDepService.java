package fip.service.fip;

import fip.common.constant.BillStatus;
import fip.common.constant.TxpkgStatus;
import fip.gateway.sbs.DepCtgManager;
import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import fip.repository.dao.FipRefunddetlMapper;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;
import fip.repository.model.FipCutpaydetlExample;
import org.apache.commons.lang.StringUtils;
import org.fbi.dep.model.txn.TOA900n052;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pub.platform.advance.utils.PropertyManager;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 银行直连代扣  通过DEP转SBS.
 * 重点是多包收发的处理。
 * User: zhanrui
 * Date: 12-8-30
 * Time: 上午11:56
 * To change this template use File | Settings | File Templates.
 */
@Service
public class BankDirectPayDepService {
    private static final Logger logger = LoggerFactory.getLogger(BankDirectPayDepService.class);
    private static String DEP_CHANNEL_ID_UNIPAY = "100";
    private static String APP_ID = PropertyManager.getProperty("app_id");
    private static String DEP_USERNAME = PropertyManager.getProperty("jms.username");
    private static String DEP_PWD = PropertyManager.getProperty("jms.password");

    @Resource
    private JmsTemplate jmsSendTemplate;

    @Resource
    private JmsTemplate jmsRecvTemplate;

    @Resource
    private DepService depService;

    @Autowired
    private BillManagerService billManagerService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    FipCutpaydetlMapper cutpaydetlMapper;

    @Autowired
    FipRefunddetlMapper refunddetlMapper;

    @Autowired
    FipCutpaybatMapper cutpaybatMapper;


    //发起代扣代付处理请求批量报文
    @Transactional
    public synchronized void performRequestHandleBatchPkg(String txnDate, FipCutpaybat cutpaybat, String userid, String username) {
        if (!checkBatchTableRecord(cutpaybat)) {
            throw new RuntimeException("版本或状态错误。" + cutpaybat.getTxpkgSn());
        }

        List<FipCutpaydetl> cutpaydetlList = queryCutpaydetlList(cutpaybat);
        //TODO 发送报文前更新bat和detl的版本号， 并提交事务

        //首先更新批量报文表状态（独立事务） 再发送
        billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.QRY_PEND, "1");
        //TODO 改为sql批处理
        billManagerService.updateCutpaydetlListStatus4NewTransactional(cutpaydetlList, BillStatus.CUTPAY_QRY_PEND);

        jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "银行处理请求报文", "开始发送", userid, username);
        //TODO 批量增加detl表的日志

        //大包拆多包发送
        try {
            processOneRequestHandleBatchPkg(txnDate, cutpaybat, cutpaydetlList);
        } catch (Exception e) {
            jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "银行处理请求报文", "发送错误" + e.getMessage(), userid, username);
            throw new RuntimeException(e);
        }

        jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "银行处理请求报文", "发送完成", userid, username);
        //TODO 批量增加detl表的日志
    }

    //发起结果查询批量报文
    @Transactional
    public synchronized void performResultQueryBatchPkg(String txnDate, FipCutpaybat cutpaybat, String userid, String username) {
        if (!checkBatchTableRecord(cutpaybat)) {
            throw new RuntimeException("版本或状态错误。" + cutpaybat.getTxpkgSn());
        }

        byte[] response = processOneResultQueryBatchPkg(txnDate, cutpaybat);
        String formcode = new String(response, 21, 4);
        logger.info(new String(response));

        if (!formcode.equals("T541")) {             //异常情况处理
            String errmsg = "[SBS返回错误信息: " + formcode + " " + getErrMsgFromResponse(response) + " ]";
            jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "银行结果查询报文", errmsg, userid, username);
            if ("WB02".equals(formcode)) {          //该笔业务不存在或已被银行拒绝    需查询原因，状态不变
                //更新批量报文表状态（独立事务）
                billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.SEND_PEND, "1");
                throw new RuntimeException("该笔业务不存在或已被银行拒绝。请重新组包。" + errmsg);
            } else {                                //其它错误需要进行结果查询再次确认
                billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.QRY_PEND, "1");
                throw new RuntimeException("银行处理结果不明，请发起结果查询交易进行确认。" + errmsg);
            }
        } else {                                    //报文发送成功
            //解包处理
            TOA900n052 toa = unmarshall(response);

            if ("1".equals(toa.body.FLOFLG)) {
                //TODO
                throw new RuntimeException("SBS返回报文中存在后续包，系统暂不支持，请联系系统管理员。");
            }

            String succmsg = "银行返回成功笔数：" + toa.body.SUCCNT + " 失败笔数：" + toa.body.FALCNT +
                    " 成功金额：" + toa.body.SUCAMT + " 失败金额：" + toa.body.FALAMT;
            jobLogService.insertNewJoblog(cutpaybat.getTxpkgSn(), "fip_cutpaybat", "银行结果查询报文", succmsg, userid, username);

            //处理本地明细记录
            updateLocalDBRecordStatusByResponse(cutpaybat, toa, userid, username);
            //设定批处理完成
            billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.DEAL_SUCCESS, "1");
        }
    }

    //处理本地明细记录
    private void updateLocalDBRecordStatusByResponse(FipCutpaybat cutpaybat, TOA900n052 toa, String userid, String username) {
        List<FipCutpaydetl> cutpaydetlList = queryCutpaydetlList(cutpaybat);
        if (Integer.parseInt(toa.body.FALCNT) == 0) {
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                cutpaydetl.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                cutpaydetl.setDateBankCutpay(new Date());
                cutpaydetl.setRecversion(cutpaydetl.getRecversion() + 1);
                cutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
                jobLogService.insertNewJoblog(cutpaydetl.getPkid(), "fip_cutpaydetl", "结果查询交易", "代扣处理成功", userid, username);
            }
        } else {
            TOA900n052.Body.BodyDetail[] details = new TOA900n052.Body.BodyDetail[toa.body.RET_DETAILS.size()];
            int step = 0;
            for (TOA900n052.Body.BodyDetail bodyDetail : toa.body.RET_DETAILS) {
                details[step++] = bodyDetail;
            }

            boolean[] detailFlags = new boolean[details.length];
            for (int i = 0; i < detailFlags.length; i++) {
                System.out.println(detailFlags[i]);
                detailFlags[i] = false;
            }
            for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
                String actno = cutpaydetl.getBiBankactno().trim();
                BigDecimal payamt = cutpaydetl.getPaybackamt();
                boolean isFound = false;
                for (int i = 0; i < detailFlags.length; i++) {
                    if (!detailFlags[i]) {
                        String rtnActno = details[i].ACTNUM.trim();
                        BigDecimal rtnAmt = new BigDecimal(details[i].TXNAMT.trim());
                        if (actno.equals(rtnActno) && payamt.compareTo(rtnAmt) == 0) {
                            isFound = true;
                            detailFlags[i] = true;  //已处理过本记录，以后不再处理
                            break;
                        }
                    }
                }
                if (isFound) {
                    //设置为扣款失败
                    cutpaydetl.setBillstatus(BillStatus.CUTPAY_FAILED.getCode());
                    String reason = details[0].REASON;

                    //20140123  zr  判断余额不足情况
                    cutpaydetl.setTxRetcode("XXXX");
                    cutpaydetl.setTxRetmsg(reason);
                    if (reason.contains("余额不足")) {
                        cutpaydetl.setTxRetcode("3008");
                    }

                    jobLogService.insertNewJoblog(cutpaydetl.getPkid(), "fip_cutpaydetl", "结果查询交易", "代扣处理失败:" + reason, userid, username);
                } else {
                    //设置为扣款成功
                    cutpaydetl.setBillstatus(BillStatus.CUTPAY_SUCCESS.getCode());
                    cutpaydetl.setTxRetcode("0000");
                    //日期
                    cutpaydetl.setDateBankCutpay(new Date());
                    jobLogService.insertNewJoblog(cutpaydetl.getPkid(), "fip_cutpaydetl", "结果查询交易", "代扣处理成功", userid, username);
                }
                cutpaydetl.setRecversion(cutpaydetl.getRecversion() + 1);
                cutpaydetlMapper.updateByPrimaryKey(cutpaydetl);
            }
        }

        checkResultOfToaAndLocalDbAfterUpdateStatus(cutpaybat, toa);
    }

    //核对修改状态后的汇总金额是否与TOA一致
    private void  checkResultOfToaAndLocalDbAfterUpdateStatus(FipCutpaybat cutpaybat, TOA900n052 toa){
        List<FipCutpaydetl> cutpaydetlList = queryCutpaydetlList(cutpaybat);
        BigDecimal succDbAmt = new BigDecimal(0.00);
        BigDecimal succToaAmt = new BigDecimal(toa.body.SUCAMT.trim());
        BigDecimal failDbAmt = new BigDecimal(0.00);
        BigDecimal failToaAmt = new BigDecimal(toa.body.FALAMT.trim());

        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            if (cutpaydetl.getBillstatus().equals(BillStatus.CUTPAY_SUCCESS.getCode())) {
                succDbAmt = succDbAmt.add(cutpaydetl.getPaybackamt());
            }else if (cutpaydetl.getBillstatus().equals(BillStatus.CUTPAY_FAILED.getCode())) {
                failDbAmt = failDbAmt.add(cutpaydetl.getPaybackamt());
            }
        }
        if (succToaAmt.compareTo(succDbAmt) != 0 || failToaAmt.compareTo(failDbAmt) != 0) {
            throw new RuntimeException("结果查询返回扣款金额与数据库汇总不符。");
        }
    }

    //更新批量报文表状态
    private void updateCutpaybatRecordStatus(FipCutpaybat cutpaybat) {
        long recversion = cutpaybat.getRecversion() + 1;
        cutpaybat.setTxpkgStatus(TxpkgStatus.QRY_PEND.getCode());
        cutpaybat.setRecversion(recversion);
        cutpaybatMapper.updateByPrimaryKey(cutpaybat);
    }


    private List<FipCutpaydetl> queryCutpaydetlList(FipCutpaybat cutpaybat) {
        FipCutpaydetlExample example = new FipCutpaydetlExample();
        example.createCriteria().andTxpkgSnEqualTo(cutpaybat.getTxpkgSn())
                //.andBillstatusEqualTo(BillStatus.PACKED.getCode())
                .andArchiveflagEqualTo("0")
                .andDeletedflagEqualTo("0");
        return cutpaydetlMapper.selectByExample(example);
    }


    //检查并发版本，总分核对、状态检查
    private boolean checkBatchTableRecord(FipCutpaybat cutpaybat) {

        return true;
    }


    //==========================代扣请求处理==============================================================

    /**
     * 发送批量代扣处理请求报文   拆多包 发送
     */
    private void processOneRequestHandleBatchPkg(String txnDate, FipCutpaybat cutpaybat, List<FipCutpaydetl> cutpaydetlList) {
        int recordNumPerMsg = 130; //每个报文中代扣记录笔数

        List<FipCutpaydetl> cutpaydetlsOneMsg = new ArrayList<FipCutpaydetl>();

        int onemsgStep = 0;
        for (FipCutpaydetl cutpaydetl : cutpaydetlList) {
            cutpaydetlsOneMsg.add(cutpaydetl);
            onemsgStep++;
            if (onemsgStep == recordNumPerMsg) {
                byte[] response = processOneRequestHandleMsg(txnDate, cutpaybat, cutpaydetlsOneMsg, false);
                logger.info(new String(response));

                String formcode = new String(response, 21, 4);
                if (!"W105".equals(formcode)) {
                    String errmsg = "[SBS返回错误信息: " + formcode + " " + getErrMsgFromResponse(response) + " ]";
                    throw new RuntimeException("SBS通讯或报文解析错误。" + errmsg);
                }
                cutpaydetlsOneMsg = new ArrayList<FipCutpaydetl>();
                onemsgStep = 0;
            }
        }

        byte[] response = processOneRequestHandleMsg(txnDate, cutpaybat, cutpaydetlsOneMsg, true);
        logger.info(new String(response));
        String formcode = new String(response, 21, 4);
        if (!"T531".equals(formcode)) {
            String errmsg = "[SBS返回错误信息: " + formcode + " " + getErrMsgFromResponse(response) + " ]";
            if ("MB01".equals(formcode)) {   //连接银行超时,结果不明需要MPC查询确认
                //更新批量报文表状态（独立事务）
                billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.QRY_PEND, "1");
                throw new RuntimeException("SBS连接银行超时或发生不明确错误，请进行结果查询交易进行确认。" + errmsg);
            } else {
                billManagerService.updateCutpaybatRecordStatus4NewTransactional(cutpaybat, TxpkgStatus.SEND_PEND, "1");
                throw new RuntimeException("SBS处理失败，请重新组包。" + errmsg);
            }
        }
    }

    //单个代扣处理请求报文
    private byte[] processOneRequestHandleMsg(String txnDate, FipCutpaybat cutpaybat, List<FipCutpaydetl> cutpaydetls, boolean isLastMsg) {
        StringBuffer data = new StringBuffer();
        DecimalFormat amtdf = new DecimalFormat("#############0.00");

        for (FipCutpaydetl cutpaydetl : cutpaydetls) {
            String amt = amtdf.format(cutpaydetl.getPaybackamt());
            data = data.append(amt); //金额
            data = data.append("|");
            data = data.append("|"); //明细备注，一般为空
            data = data.append(cutpaydetl.getBiBankactno().trim()); //帐号
            data = data.append("|");
            data = data.append(cutpaydetl.getBiBankactname().trim()); //户名
            data = data.append("|");
            data = data.append("|"); //证件号 为空
        }
        List<String> paramList = getRequestHandleReqParamList(txnDate, cutpaybat, cutpaydetls.size(), isLastMsg);
        paramList.add(data.toString());  //  代发代扣文件内容  29000

        return DepCtgManager.processSingleResponsePkg("n050", paramList);
    }


    //组代扣请求报文参数
    private List<String> getRequestHandleReqParamList(String txnDate, FipCutpaybat cutpaybat, int currcount, boolean isLastMsg) {
        List<String> paramList = new ArrayList<String>();

        paramList.add(txnDate);                       //交易日期
        paramList.add(StringUtils.rightPad(cutpaybat.getBizSn(), 18, " "));        //业务编号（消费信贷系统：XF+8位日期+6位顺序号       房贷系统：FD+8位日期+6位顺序号）
        paramList.add(StringUtils.rightPad(cutpaybat.getTxpkgSn(), 16, " "));      //报文包号（批量发送时使用）

        DecimalFormat df = new DecimalFormat("#############0.00");

        String totalamt = df.format(cutpaybat.getTotalamt());
        paramList.add("+" + StringUtils.leftPad(totalamt, 16, '0'));                  //总金额  17

        String totalcount = String.valueOf(cutpaybat.getTotalcount());
        paramList.add(StringUtils.leftPad(totalcount, 7, '0'));                         // 总笔数        7

        paramList.add(StringUtils.leftPad("" + currcount, 7, '0'));                          // 本包总笔数        7

        paramList.add(isLastMsg ? "0" : "1");                                         //  是否有后续包  0-否，1-是
        paramList.add(StringUtils.rightPad(cutpaybat.getTransferact(), 22, ' '));      //  转出帐户  22

        if (cutpaybat.getUsage() != null) {
            paramList.add(StringUtils.rightPad(cutpaybat.getUsage(), 12, ' '));         //  用途 12
        } else {
            paramList.add(StringUtils.rightPad("99999998", 12, ' '));                   //  用途 12
        }

        if (cutpaybat.getRemark() != null) {
            paramList.add(StringUtils.rightPad(cutpaybat.getRemark(), 30, ' '));         //  备注,   30
        } else {
            paramList.add(StringUtils.rightPad("REMARK:", 30, ' '));                     //  备注,   30
        }

        if (cutpaybat.getRemark1() != null) {
            paramList.add(StringUtils.rightPad(cutpaybat.getRemark1(), 32, ' '));        //  备注1,  32
        } else {
            paramList.add(StringUtils.rightPad("REMARK1", 32, ' '));                    //  备注1,  32
        }

        if (cutpaybat.getRemark2() != null) {
            paramList.add(StringUtils.rightPad(cutpaybat.getRemark2(), 32, ' '));        //  备注2,  32
        } else {
            paramList.add(StringUtils.rightPad("REMARK2", 32, ' '));                     //  备注2,  32
        }

        paramList.add(cutpaybat.getBankid());                     //  银行代码,  3
        paramList.add("+0000000000000.00");                       //  失败金额       17
        paramList.add("0000000");                                 //  失败笔数        7
        paramList.add("BAW");                                     //  交易类别 BAP-批量报销,BAS-批量代发工资

        return paramList;
    }

    //==========================结果查询处理==============================================================

    /**
     * 发送结果查询报文
     */
    private byte[] processOneResultQueryBatchPkg(String txnDate, FipCutpaybat cutpaybat) {
        List<FipCutpaydetl> cutpaydetlList = queryCutpaydetlList(cutpaybat);
        List<String> paramList = new ArrayList<String>();

        paramList.add(StringUtils.rightPad(cutpaybat.getBizSn(), 18, " "));        //业务编号（消费信贷系统：XF+8位日期+6位顺序号       房贷系统：FD+8位日期+6位顺序号）
        paramList.add(StringUtils.rightPad(cutpaybat.getTxpkgSn(), 16, " "));      //报文包号（批量发送时使用）
        paramList.add(txnDate);                                                    //交易日期
        paramList.add("000001");                                                   //起始笔数 6

        return DepCtgManager.processSingleResponsePkg("n052", paramList);
    }

    private TOA900n052 unmarshall(byte[] buffer) {
        TOA900n052 toa = new TOA900n052();

        int k = 0;
        try {
            int pos = 29;
            byte[] bSuccnt = new byte[6];
            byte[] bFalcnt = new byte[6];
            byte[] bSucamt = new byte[17];
            byte[] bFalamt = new byte[17];
            byte[] bFloflg = new byte[1];
            byte[] bCurcnt = new byte[6];
            byte[] bRemark1 = new byte[99];
            byte[] bRemark2 = new byte[99];

            System.arraycopy(buffer, pos, bSuccnt, 0, bSuccnt.length);
            pos += bSuccnt.length;
            System.arraycopy(buffer, pos, bFalcnt, 0, bFalcnt.length);
            pos += bFalcnt.length;
            System.arraycopy(buffer, pos, bSucamt, 0, bSucamt.length);
            pos += bSucamt.length;
            System.arraycopy(buffer, pos, bFalamt, 0, bFalamt.length);
            pos += bFalamt.length;

            System.arraycopy(buffer, pos, bFloflg, 0, bFloflg.length);
            pos += bFloflg.length;
            System.arraycopy(buffer, pos, bCurcnt, 0, bCurcnt.length);
            pos += bCurcnt.length;
            System.arraycopy(buffer, pos, bRemark1, 0, bRemark1.length);
            pos += bRemark1.length;
            System.arraycopy(buffer, pos, bRemark2, 0, bRemark2.length);
            pos += bRemark2.length;


            toa.body.SUCCNT = new String(bSuccnt);
            toa.body.FALCNT = new String(bFalcnt);
            toa.body.SUCAMT = new String(bSucamt);
            toa.body.FALAMT = new String(bFalamt);

            toa.body.FLOFLG = new String(bFloflg);
            toa.body.CURCNT = new String(bCurcnt);
            toa.body.REMARK1 = new String(bRemark1);
            toa.body.REMARK2 = new String(bRemark2);

            int curcnt = Integer.parseInt(toa.body.CURCNT);

            byte[] bActnum = new byte[32];
            byte[] bActnam = new byte[60];
            byte[] bReason = new byte[40];
            byte[] bTxnamt = new byte[17];


            for (k = 0; k < curcnt; k++) {
                TOA900n052.Body.BodyDetail record = new TOA900n052.Body.BodyDetail();
                System.arraycopy(buffer, pos, bActnum, 0, bActnum.length);
                pos += bActnum.length;
                System.arraycopy(buffer, pos, bActnam, 0, bActnam.length);
                pos += bActnam.length;
                System.arraycopy(buffer, pos, bReason, 0, bReason.length);
                pos += bReason.length;
                System.arraycopy(buffer, pos, bTxnamt, 0, bTxnamt.length);
                pos += bTxnamt.length;
                record.ACTNUM = new String(bActnum);
                record.ACTNAM = new String(bActnam);
                record.REASON = new String(bReason);
                record.TXNAMT = new String(bTxnamt);
                toa.body.RET_DETAILS.add(record);
            }
            return toa;
        } catch (Exception e) {
            System.out.println("报文解包时出现问题：" + k);
            logger.error("报文解包时出现问题：" + k);
            throw new RuntimeException(e);
        }
    }


    //===========================================================================================
    /*
   获取报错应答报文中的错误信息
    */
    private String getErrMsgFromResponse(byte[] buffer) {

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