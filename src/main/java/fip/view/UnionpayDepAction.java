package fip.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.constant.BillType;
import fip.common.constant.BizType;
import fip.common.constant.CutpayChannel;
import fip.common.utils.MessageUtil;
import fip.gateway.JmsManager;
import fip.repository.model.FipCutpaydetl;
import fip.service.fip.*;
import fip.view.common.JxlsManager;
import org.apache.commons.lang.StringUtils;
import org.fbi.dep.model.txn.TOA1003001;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.security.OperatorManager;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.jms.JMSException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 银联代扣处理（全部走DEP APP接口）.
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UnionpayDepAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayDepAction.class);

    private List<FipCutpaydetl> detlList;
    private List<FipCutpaydetl> filteredDetlList;
    private FipCutpaydetl[] selectedRecords;

    private List<FipCutpaydetl> needQueryDetlList;
    private List<FipCutpaydetl> filteredNeedQueryDetlList;
    private FipCutpaydetl[] selectedNeedQryRecords;

    private List<FipCutpaydetl> failureDetlList;
    private List<FipCutpaydetl> filteredFailureDetlList;
    private FipCutpaydetl[] selectedFailRecords;

    private List<FipCutpaydetl> successDetlList;
    //    private List<FipCutpaydetl> filteredSuccessDetlList = new ArrayList<FipCutpaydetl>();
    private List<FipCutpaydetl> filteredSuccessDetlList;
    private FipCutpaydetl[] selectedAccountRecords;
    private FipCutpaydetl[] selectedConfirmAccountRecords;

    private List<FipCutpaydetl> actDetlList;


    private FipCutpaydetl detlRecord = new FipCutpaydetl();
    private FipCutpaydetl selectedRecord;


    private int totalcount;
    private String totalamt;
    private int totalFailureCount;
    private String totalFailureAmt;
    private int totalSuccessCount;
    private String totalSuccessAmt;
    private int totalAccountCount;
    private String totalAccountAmt;

    private BigDecimal totalPrincipalAmt;   //本金
    private BigDecimal totalInterestAmt;    //利息
    private BigDecimal totalFxjeAmt;    //罚息

    private Map<String, String> statusMap = new HashMap<String, String>();

    private BillStatus status = BillStatus.CUTPAY_FAILED;

    @ManagedProperty(value = "#{billManagerService}")
    private BillManagerService billManagerService;
    @ManagedProperty(value = "#{jobLogService}")
    private JobLogService jobLogService;

    @ManagedProperty(value = "#{unipayDepService}")
    private UnipayDepService unipayDepService;
    @ManagedProperty(value = "#{cmsService}")
    private CmsService cmsService;
    @ManagedProperty(value = "#{ccmsService}")
    private CcmsService ccmsService;


    private String bizid;
    private String sysid = "";   //暂不适用
    private String pkid;
    private BizType bizType;


    private String userid, username;

    @PostConstruct
    public void init() {
        try {
            OperatorManager om = SystemService.getOperatorManager();
            this.userid = om.getOperatorId();
            this.username = om.getOperatorName();

            this.sysid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("sysid");
            this.bizid = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bizid");
            if (!StringUtils.isEmpty(this.bizid)) {
                this.bizType = BizType.valueOf(this.bizid);
                initList();
            }
        } catch (Exception e) {
            logger.error("初始化时出现错误。");
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "初始化时出现错误。", "检索数据库出现问题。"));
        }

    }

    public void initList() {
        detlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.INIT);
        detlList.addAll(billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.RESEND_PEND));
        filteredDetlList = detlList;

        needQueryDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_QRY_PEND);
        filteredNeedQueryDetlList = needQueryDetlList;

        failureDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_FAILED);
        filteredFailureDetlList = failureDetlList;

        successDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.CUTPAY_SUCCESS);
        filteredSuccessDetlList = successDetlList;

        actDetlList = billManagerService.selectRecords4UnipayOnline(this.bizType, BillStatus.ACCOUNT_PEND);

        this.totalamt = sumTotalAmt(detlList);
        this.totalcount = detlList.size();
        this.totalSuccessAmt = sumTotalAmt(filteredSuccessDetlList);
        this.totalSuccessCount = filteredSuccessDetlList.size();
        this.totalFailureAmt = sumTotalAmt(failureDetlList);
        this.totalFailureCount = failureDetlList.size();
        this.totalAccountAmt = sumTotalAmt(actDetlList);
        this.totalAccountCount = actDetlList.size();
    }

    private String sumTotalAmt(List<FipCutpaydetl> qrydetlList) {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        BigDecimal amt = new BigDecimal(0);
        if (qrydetlList == null || qrydetlList.size() == 0) {
            return df.format(amt);
        }
        for (FipCutpaydetl cutpaydetl : qrydetlList) {
            amt = amt.add(cutpaydetl.getPaybackamt());
        }
        return df.format(amt);
    }

    public String onSendRequestAll() {
        if (filteredDetlList.isEmpty()) {
            MessageUtil.addWarn("没有要发送的记录！");
            return null;
        }

        long start = System.currentTimeMillis();
        try {
            int retrytimes = 0;
            for (FipCutpaydetl cutpaydetl : this.filteredDetlList) {
                processOneCutpayRequestRecord(cutpaydetl);
/*
                try {
                    processOneCutpayRequestRecord(cutpaydetl);
                } catch (Exception e) {
                    retrytimes ++;
                    if (retrytimes <= 10 ) {
                        Thread.sleep(10000);
                        processOneCutpayRequestRecord(cutpaydetl);
                    }else{
                        throw new RuntimeException(e);
                    }
                }
*/

            }

            long end = System.currentTimeMillis();
            MessageUtil.addInfo("数据发送结束！请查看处理结果明细。" + "耗时:" + (end - start));
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            MessageUtil.addError("数据发送结束处理异常" + e.getMessage() + "耗时:" + (end - start));
        }
        initList();
        return null;
    }

    public String onSendRequestMulti() {
        if (selectedRecords == null || selectedRecords.length <= 0) {
            MessageUtil.addWarn("未选中要处理的记录！");
            return null;
        }

        long start = System.currentTimeMillis();
        try {
            for (FipCutpaydetl cutpaydetl : this.selectedRecords) {
                processOneCutpayRequestRecord(cutpaydetl);
            }

            long end = System.currentTimeMillis();
            MessageUtil.addInfo("数据发送结束！请查看处理结果明细。" + "耗时:" + (end - start));
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            MessageUtil.addError("数据发送结束处理异常" + e.getMessage() + "耗时:" + (end - start));
        }
        initList();
        return null;
    }

    public String onChangeChannel() {
        if (selectedRecords.length <= 0) {
            MessageUtil.addWarn("未选中记录！");
            return null;
        }
        try {
            billManagerService.chgChannel(Arrays.asList(selectedRecords), CutpayChannel.NONE);
            MessageUtil.addInfo("变更代扣渠道完成！");
        } catch (Exception e) {
            logger.error("变更代扣渠道出现错误！", e);
            MessageUtil.addError("变更代扣渠道出现错误！" + e.getMessage());
        }
        initList();
        return null;
    }


    public String onQueryAll() {
        if (filteredNeedQueryDetlList.isEmpty()) {
            MessageUtil.addWarn("没有要发送的记录！");
            return null;
        }

        long start = System.currentTimeMillis();
        try {
/*
            int retrytimes = 0;
            for (FipCutpaydetl cutpaydetl : this.filteredNeedQueryDetlList) {
                try {
                    processOneQueryRecord(cutpaydetl);
                } catch (Exception e) {
                    retrytimes++;
                    if (retrytimes <= 10) {
                        Thread.sleep(10000);
                        processOneQueryRecord(cutpaydetl);
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
*/

            concurrentQuery(this.filteredNeedQueryDetlList);

            long end = System.currentTimeMillis();
            MessageUtil.addInfo("查询交易发送结束！请查看处理结果明细。 " + "耗时:" + (end - start));
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            MessageUtil.addError("查询交易处理异常" + e.getMessage() + "耗时:" + (end - start));
        }

        initList();
        return null;
    }

    public String onQueryMulti() {
        if (selectedNeedQryRecords == null || selectedNeedQryRecords.length <= 0) {
            MessageUtil.addWarn("没有或未选中要发送的记录！");
            return null;
        }

        long start = System.currentTimeMillis();
        try {
            for (FipCutpaydetl cutpaydetl : this.selectedNeedQryRecords) {
                processOneQueryRecord(cutpaydetl);
            }
/*
            List<FipCutpaydetl> qryList = Arrays.asList(this.selectedNeedQryRecords);
            concurrentQuery(qryList);
*/

            long end = System.currentTimeMillis();
            MessageUtil.addInfo("查询交易发送结束！请查看处理结果明细。" + "耗时:" + (end - start));
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            MessageUtil.addError("查询交易处理异常" + e.getMessage() + "耗时:" + (end - start));
        }
        initList();
        return null;
    }


    /**
     * 并发处理请求  查询
     */
    private void concurrentQuery(List<FipCutpaydetl> qryList) throws InterruptedException {
        int threadNumber = 5;
        final ExecutorService executor = Executors.newFixedThreadPool(threadNumber);

        for (final FipCutpaydetl cutpaydetl : qryList) {
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        processOneQueryRecord(cutpaydetl);
                    } catch (Exception e) {
                        MessageUtil.addInfo("流水号:" + cutpaydetl.getBatchSn() +
                                cutpaydetl.getBatchDetlSn() + "姓名:" +
                                cutpaydetl.getClientname() + " 错误信息:" +
                                e.getMessage());
                    }
                }
            };
            executor.execute(task);
        }

        executor.shutdown();
        //等待处理结束
        while (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
//            logger.info("线程池没有关闭");
        }
        logger.info("线程池已经关闭");
    }

    @Deprecated
    private void parallelProcess4Query_old(List<FipCutpaydetl> qryList) throws InterruptedException {
        int threadNumber = 10;
        int qryListSize = qryList.size();
        if (qryListSize <= 50) {
            threadNumber = 1;
        }

        // 开始的倒计数锁
        final CountDownLatch begin = new CountDownLatch(1);
        // 结束的倒计数锁
        final CountDownLatch end = new CountDownLatch(threadNumber);

        final ExecutorService executor = Executors.newFixedThreadPool(threadNumber);

        for (int index = 0; index < threadNumber; index++) {
            final int NO = index;
            int beginPos = index * threadNumber;
            int endPos = index * threadNumber + threadNumber;
            boolean isEnd = false;
            if (endPos > qryListSize) {
                isEnd = true;
                endPos = qryListSize;
            }
            final List<FipCutpaydetl> qryListThread;
            if (index == threadNumber - 1) {
                qryListThread = qryList.subList(beginPos, qryListSize);
            } else {
                qryListThread = qryList.subList(beginPos, endPos);
            }
            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("No." + NO + " " + Thread.currentThread().getName());
                        begin.await();//阻塞
                        for (FipCutpaydetl fipCutpaydetl : qryListThread) {
                            processOneQueryRecord(fipCutpaydetl);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        end.countDown();
                    }
                }
            };
            executor.submit(task);
            if (isEnd) {
                break;
            }
        }
        System.out.println("并发线程开始...");
        begin.countDown();
        end.await();
        System.out.println("并发线程结束...");
        executor.shutdown();
    }

    @Deprecated
    private void parallelProcessRecv(List<String> recvMsgIdList) throws InterruptedException {
        int threadNumber = 10;
        int recvListSize = recvMsgIdList.size();
        if (recvListSize <= 20) {
            threadNumber = 1;
        }

        // 开始的倒计数锁
        final CountDownLatch begin = new CountDownLatch(1);
        // 结束的倒计数锁
        final CountDownLatch end = new CountDownLatch(threadNumber);

        final ExecutorService executor = Executors.newFixedThreadPool(threadNumber);

        for (int index = 0; index < threadNumber; index++) {
            final int NO = index;
            int beginPos = index * threadNumber;
            int endPos = index * threadNumber + threadNumber;
            boolean isEnd = false;
            if (endPos > recvListSize) {
                isEnd = true;
                endPos = recvListSize;
            }
            final List<String> recvListThread;
            if (index == threadNumber - 1) {
                recvListThread = recvMsgIdList.subList(beginPos, recvListSize);
            } else {
                recvListThread = recvMsgIdList.subList(beginPos, endPos);
            }

            Runnable task = new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println("No." + NO + " " + Thread.currentThread().getName());
                        begin.await();//阻塞
                        for (String msgid : recvListThread) {
                            //processOneQueryRecord(msgid);
                            TOA1003001 toa = (TOA1003001) JmsManager.getInstance().recvByMsgId(msgid);
                            //TODO
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (JMSException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                    } finally {
                        end.countDown();
                    }
                }
            };
            executor.submit(task);
            if (isEnd) {
                break;
            }
        }
        System.out.println("并发线程开始...");
        begin.countDown();
        end.await();
        System.out.println("并发线程结束...");
        executor.shutdown();
    }

    /**
     * 扣款请求
     *
     * @param record
     * @return
     */
    private void processOneCutpayRequestRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        long start = System.currentTimeMillis();
        try {
            unipayDepService.sendAndRecvT1001001Message(record);
            long end = System.currentTimeMillis();
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文完成。 " + " time:" + (end - start));
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            appendNewJoblog(pkid, "发送扣款请求", "发送银联扣款请求报文失败." + e.getMessage() + " time:" + (end - start));
            throw new RuntimeException("数据发送异常，请检查系统线路重新发送！" + e.getMessage());
        }
    }

    /**
     * 查询请求
     *
     * @param record
     * @return
     */
    private void processOneQueryRecord(FipCutpaydetl record) {
        String pkid = record.getPkid();
        long start = System.currentTimeMillis();
        try {
//            Thread.sleep(1000);
            unipayDepService.sendAndRecvCutpayT1003001Message(record);
            long end = System.currentTimeMillis();
//            logger.info("发送银联查询请求报文完成。 time:" + (end - start) + " PID:" + Thread.currentThread().getId());
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文完成。 time:" + (end - start) + " PID:" + Thread.currentThread().getId());
        } catch (Exception e) {
            long end = System.currentTimeMillis();
            appendNewJoblog(pkid, "发送查询请求", "发送银联查询请求报文失败." + e.getMessage() + " time:" + (end - start) + " PID:" + Thread.currentThread().getId());
            throw new RuntimeException("数据发送异常，请检查系统线路重新发送！" + e.getMessage());
        }
    }

    public String onConfirmAccountAll() {
        if (filteredSuccessDetlList.isEmpty()) {
            MessageUtil.addWarn("记录为空！");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                cmsService.obtainMerchantActnoFromCms(this.filteredSuccessDetlList);
            }
            billManagerService.updateCutpaydetlBillStatus(this.filteredSuccessDetlList, BillStatus.ACCOUNT_PEND);
            MessageUtil.addInfo("确认入账完成！笔数:" + filteredSuccessDetlList.size());
            initList();
        } catch (Exception e) {
            MessageUtil.addError("数据确认出现错误！" + e.getMessage());
        }

        return null;
    }

    public String onConfirmAccountMulti() {
        if (this.selectedConfirmAccountRecords.length == 0) {
            MessageUtil.addWarn("请选择记录.");
            return null;
        }
        try {
            if (this.bizType.equals(BizType.XFSF)) {
                cmsService.obtainMerchantActnoFromCms(Arrays.asList(this.selectedConfirmAccountRecords));
            }
            billManagerService.updateCutpaydetlBillStatus(Arrays.asList(this.selectedConfirmAccountRecords), BillStatus.ACCOUNT_PEND);
            initList();
            MessageUtil.addInfo("确认完成！");
        } catch (Exception e) {
            MessageUtil.addError("数据确认出现错误！" + e.getMessage());
        }
        return null;
    }

    public String onExportFailureList() {
        if (this.failureDetlList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            String excelFilename = "代扣失败记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, this.failureDetlList);
        }
        return null;
    }

    public String onExportSuccessList() {
        if (this.successDetlList.size() == 0) {
            MessageUtil.addWarn("记录为空...");
            return null;
        } else {
            String excelFilename = "代扣成功记录清单-" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xls";
            JxlsManager jxls = new JxlsManager();
            jxls.exportCutpayList(excelFilename, this.successDetlList);
        }
        return null;
    }


    /**
     * 存档失败记录
     *
     * @return
     */
    public String onArchiveAllFailureRecord() {
        List<String> rtnMsgs = new ArrayList<String>();

        if (this.filteredFailureDetlList.size() == 0) {
            MessageUtil.addWarn("失败记录集为空。");
            return null;
        } else {
            try {
                //20130412 zr 在失败记录的存档处理时自动进行信贷系统回写
                //old-> int count = billManagerService.archiveBills(this.filteredFailureDetlList);
                cmsService.writebackCutPayRecord2CMS_ForFailureReord(filteredFailureDetlList, rtnMsgs);
                if (rtnMsgs.size() == 0) {
                    MessageUtil.addInfo("归档与回写处理完成。");
                }
                unlockIntr(this.filteredFailureDetlList);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        for (String rtnMsg : rtnMsgs) {
            MessageUtil.addError(rtnMsg);
        }
        initList();
        return null;
    }

    public String onArchiveMultiFailureRecord() {
        List<String> rtnMsgs = new ArrayList<String>();

        if (this.selectedFailRecords.length == 0) {
            MessageUtil.addWarn("请选择记录...");
            return null;
        } else {
            try {
                List<FipCutpaydetl> cutpaydetlList = Arrays.asList(this.selectedFailRecords);
                //20130412 zr 在失败记录的存档处理时自动进行信贷系统回写
                //old-> int count = billManagerService.archiveBills(cutpaydetlList);
                cmsService.writebackCutPayRecord2CMS_ForFailureReord(cutpaydetlList, rtnMsgs);
                if (rtnMsgs.size() == 0) {
                    MessageUtil.addInfo("归档与回写处理完成。");
                }
                unlockIntr(cutpaydetlList);
            } catch (Exception e) {
                MessageUtil.addError("数据处理错误！" + e.getMessage());
            }
        }
        for (String rtnMsg : rtnMsgs) {
            MessageUtil.addError(rtnMsg);
        }
        initList();
        return null;
    }

    private void unlockIntr(List<FipCutpaydetl> failureDetlList) {
        //20130109 zr 对于逾期的记录 做自动解锁处理
        for (FipCutpaydetl fipCutpaydetl : failureDetlList) {
            if (fipCutpaydetl.getBilltype().equals(BillType.OVERDUE.getCode())) {
                if (cmsService.unlockIntr4Overdue(fipCutpaydetl)) {
                    MessageUtil.addInfo("利息解锁成功：" + fipCutpaydetl.getIouno() + fipCutpaydetl.getClientname());
                } else {
                    MessageUtil.addError("利息解锁出错：" + fipCutpaydetl.getIouno() + fipCutpaydetl.getClientname());
                }
            }
        }
    }

    public String reset() {
        this.detlRecord = new FipCutpaydetl();
        return null;
    }


    private void appendNewJoblog(String pkid, String jobname, String jobdesc) {
        jobLogService.insertNewJoblog(pkid, "fip_cutpaydetl", jobname, jobdesc, userid, username);
    }
    //====================================================================================


    public FipCutpaydetl[] getSelectedConfirmAccountRecords() {
        return selectedConfirmAccountRecords;
    }

    public void setSelectedConfirmAccountRecords(FipCutpaydetl[] selectedConfirmAccountRecords) {
        this.selectedConfirmAccountRecords = selectedConfirmAccountRecords;
    }

    public List<FipCutpaydetl> getActDetlList() {
        return actDetlList;
    }

    public void setActDetlList(List<FipCutpaydetl> actDetlList) {
        this.actDetlList = actDetlList;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public BigDecimal getTotalPrincipalAmt() {
        return totalPrincipalAmt;
    }

    public BigDecimal getTotalInterestAmt() {
        return totalInterestAmt;
    }

    public BigDecimal getTotalFxjeAmt() {
        return totalFxjeAmt;
    }


    public FipCutpaydetl getDetlRecord() {
        return detlRecord;
    }

    public void setDetlRecord(FipCutpaydetl detlRecord) {
        this.detlRecord = detlRecord;
    }

    public FipCutpaydetl getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(FipCutpaydetl selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public FipCutpaydetl[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(FipCutpaydetl[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public List<FipCutpaydetl> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<FipCutpaydetl> detlList) {
        this.detlList = detlList;
    }


    public BillManagerService getBillManagerService() {
        return billManagerService;
    }

    public void setBillManagerService(BillManagerService billManagerService) {
        this.billManagerService = billManagerService;
    }

    public List<FipCutpaydetl> getNeedQueryDetlList() {
        return needQueryDetlList;
    }

    public void setNeedQueryDetlList(List<FipCutpaydetl> needQueryDetlList) {
        this.needQueryDetlList = needQueryDetlList;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }

    public List<FipCutpaydetl> getFailureDetlList() {
        return failureDetlList;
    }

    public void setFailureDetlList(List<FipCutpaydetl> failureDetlList) {
        this.failureDetlList = failureDetlList;
    }

    public List<FipCutpaydetl> getSuccessDetlList() {
        return successDetlList;
    }

    public void setSuccessDetlList(List<FipCutpaydetl> successDetlList) {
        this.successDetlList = successDetlList;
    }

    public JobLogService getJobLogService() {
        return jobLogService;
    }

    public void setJobLogService(JobLogService jobLogService) {
        this.jobLogService = jobLogService;
    }

    public UnipayDepService getUnipayDepService() {
        return unipayDepService;
    }

    public void setUnipayDepService(UnipayDepService unipayDepService) {
        this.unipayDepService = unipayDepService;
    }

    public FipCutpaydetl[] getSelectedNeedQryRecords() {
        return selectedNeedQryRecords;
    }

    public void setSelectedNeedQryRecords(FipCutpaydetl[] selectedNeedQryRecords) {
        this.selectedNeedQryRecords = selectedNeedQryRecords;
    }

    public CmsService getCmsService() {
        return cmsService;
    }

    public void setCmsService(CmsService cmsService) {
        this.cmsService = cmsService;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public int getTotalFailureCount() {
        return totalFailureCount;
    }

    public void setTotalFailureCount(int totalFailureCount) {
        this.totalFailureCount = totalFailureCount;
    }

    public String getTotalFailureAmt() {
        return totalFailureAmt;
    }

    public void setTotalFailureAmt(String totalFailureAmt) {
        this.totalFailureAmt = totalFailureAmt;
    }

    public int getTotalSuccessCount() {
        return totalSuccessCount;
    }

    public void setTotalSuccessCount(int totalSuccessCount) {
        this.totalSuccessCount = totalSuccessCount;
    }

    public String getTotalSuccessAmt() {
        return totalSuccessAmt;
    }

    public void setTotalSuccessAmt(String totalSuccessAmt) {
        this.totalSuccessAmt = totalSuccessAmt;
    }

    public int getTotalAccountCount() {
        return totalAccountCount;
    }

    public void setTotalAccountCount(int totalAccountCount) {
        this.totalAccountCount = totalAccountCount;
    }

    public String getTotalAccountAmt() {
        return totalAccountAmt;
    }

    public void setTotalAccountAmt(String totalAccountAmt) {
        this.totalAccountAmt = totalAccountAmt;
    }

    public BizType getBizType() {
        return bizType;
    }

    public void setBizType(BizType bizType) {
        this.bizType = bizType;
    }

    public FipCutpaydetl[] getSelectedAccountRecords() {
        return selectedAccountRecords;
    }

    public void setSelectedAccountRecords(FipCutpaydetl[] selectedAccountRecords) {
        this.selectedAccountRecords = selectedAccountRecords;
    }

    public FipCutpaydetl[] getSelectedFailRecords() {
        return selectedFailRecords;
    }

    public void setSelectedFailRecords(FipCutpaydetl[] selectedFailRecords) {
        this.selectedFailRecords = selectedFailRecords;
    }

    public CcmsService getCcmsService() {
        return ccmsService;
    }

    public void setCcmsService(CcmsService ccmsService) {
        this.ccmsService = ccmsService;
    }

    public String getSysid() {
        return sysid;
    }

    public void setSysid(String sysid) {
        this.sysid = sysid;
    }

    public List<FipCutpaydetl> getFilteredSuccessDetlList() {
        return filteredSuccessDetlList;
    }

    public void setFilteredSuccessDetlList(List<FipCutpaydetl> filteredSuccessDetlList) {
        this.filteredSuccessDetlList = filteredSuccessDetlList;
        this.totalSuccessAmt = sumTotalAmt(filteredSuccessDetlList);
        if (this.filteredSuccessDetlList == null) {
            this.totalSuccessCount = 0;
        } else
            this.totalSuccessCount = filteredSuccessDetlList.size();
    }

    public List<FipCutpaydetl> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<FipCutpaydetl> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public List<FipCutpaydetl> getFilteredNeedQueryDetlList() {
        return filteredNeedQueryDetlList;
    }

    public void setFilteredNeedQueryDetlList(List<FipCutpaydetl> filteredNeedQueryDetlList) {
        this.filteredNeedQueryDetlList = filteredNeedQueryDetlList;
    }

    public List<FipCutpaydetl> getFilteredFailureDetlList() {
        return filteredFailureDetlList;
    }

    public void setFilteredFailureDetlList(List<FipCutpaydetl> filteredFailureDetlList) {
        this.filteredFailureDetlList = filteredFailureDetlList;
    }
}
