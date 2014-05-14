package fip.view.sbs;

import fip.common.constant.TxSendFlag;
import fip.repository.model.DepSbsZfqs;
import fip.service.fip.DepSbsZfqsService;
import fip.common.utils.MessageUtil;
import fip.service.fip.FtpSbsFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-3-27
 * Time: 下午4:04
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class DepSbsZfqsAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(DepSbsZfqsAction.class);

    private String startDate;
    private String endDate;
    private String obtainDate;
    @ManagedProperty(value = "#{depSbsZfqsService}")
    private DepSbsZfqsService depSbsZfqsService;
    @ManagedProperty(value = "#{ftpSbsFileService}")
    private FtpSbsFileService ftpSbsFileService;

    private List<DepSbsZfqs> recordList;
    private DepSbsZfqs[] selectedRecords;
    private TxSendFlag sendFlag = TxSendFlag.UNSEND;

    @PostConstruct
    public void init() {
        obtainDate = getDateAfter(new Date(), -1, "yyyy-MM-dd");
        startDate = obtainDate;
        endDate = obtainDate;
        recordList = depSbsZfqsService.qryUnsendRecords();
    }

    public String onObtain() {
        try {
            // 获取总分账户清算数据
            String[] lines = ftpSbsFileService.obtainSbsZfqsMsgs(obtainDate);
            if (lines == null || lines.length == 0) {
                MessageUtil.addInfo("本次未获取到SBS数据。[" + obtainDate + "]没有总分账户清算明细。");
            } else {
                MessageUtil.addInfo("本次获取清算明细笔数：" + lines.length);
                // 保存
                int cnt = depSbsZfqsService.saveZfqsRecords(lines);
                MessageUtil.addInfo("保存明细笔数：" + cnt + ", 重复获取数：" + (lines.length - cnt));
            }
            //查询所有未发送记录
            recordList = depSbsZfqsService.qryUnsendRecords();

        } catch (Exception e) {
            MessageUtil.addError("连接SBS读取文件失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("连接SBS读取文件失败。", e);
        }
        return null;
    }

    public String onSend() {
        int cnt = 0;
        int allCnt = 0;
        try {
            if (recordList == null || recordList.isEmpty()) {
                MessageUtil.addWarn("没有未发送数据。");
                return null;
            } else {
                /*
                String[] sns = depJdeZfqsService.sendSbsZfqsRecords(recordList);
                int cnt = depSbsZfqsService.updateToSendStsBySns(sns);*/
                allCnt = recordList.size();
                String operTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                for (DepSbsZfqs record : recordList) {
                    String snMsg = depSbsZfqsService.sendSbsZfqs(record);
                    cnt += depSbsZfqsService.updateToSentSts(snMsg, operTime) == 1 ? 1 : 0;
                }
                MessageUtil.addInfo("发送成功笔数：" + cnt + " 发送失败笔数：" + (allCnt - cnt));
                //查询所有未发送记录
                recordList = depSbsZfqsService.qryUnsendRecords();
                if (recordList == null || recordList.isEmpty()) {
                    MessageUtil.addInfo("所有已获取清算明细均发送成功。");
                }
            }
        } catch (Exception e) {
            MessageUtil.addWarn("发送成功笔数：" + cnt + " 发送失败笔数：" + (allCnt - cnt));
            MessageUtil.addError("发送数据至JDE过程出现异常。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("发送数据至JDE过程出现异常。", e);
        }
        return null;
    }

    public String onMultiSend() {
        int cnt = 0;
        int allCnt = 0;
        try {
            if (selectedRecords == null || selectedRecords.length == 0) {
                MessageUtil.addWarn("请至少选择一笔数据记录。");
                return null;
            } else {
                /*
                String[] sns = depJdeZfqsService.sendSbsZfqsRecords(recordList);
                int cnt = depSbsZfqsService.updateToSendStsBySns(sns);*/
                allCnt = selectedRecords.length;
                String operTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                for (DepSbsZfqs record : selectedRecords) {
                    String snMsg = depSbsZfqsService.sendSbsZfqs(record);
                    cnt += depSbsZfqsService.updateToSentSts(snMsg, operTime) == 1 ? 1 : 0;
                }
                MessageUtil.addInfo("发送成功笔数：" + cnt + " 发送失败笔数：" + (allCnt - cnt));
                //查询所有未发送记录
                recordList = depSbsZfqsService.qryUnsendRecords();
                if (recordList == null || recordList.isEmpty()) {
                    MessageUtil.addInfo("所有已获取清算明细均发送成功。");
                }
            }
        } catch (Exception e) {
            MessageUtil.addWarn("发送成功笔数：" + cnt + " 发送失败笔数：" + (allCnt - cnt));
            MessageUtil.addError("发送数据至JDE过程出现异常。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("发送数据至JDE过程出现异常。", e);
        }
        return null;
    }

    public String onQuery() {
        try {
            startDate = startDate.substring(0, 4) + startDate.substring(5, 7) + startDate.substring(8, 10);
            endDate = endDate.substring(0, 4) + endDate.substring(5, 7) + endDate.substring(8, 10);
            //查询所有未发送记录
            recordList = depSbsZfqsService.qryUnSendRecordsByDate(startDate, endDate);
            if (recordList == null || recordList.isEmpty()) {
                MessageUtil.addInfo("没有查询到该日期数据。");
            }
        } catch (Exception e) {
            MessageUtil.addError("查询数据失败。" + (e.getMessage() == null ? "" : e.getMessage()));
            logger.error("查询数据失败。", e);
        }
        return null;
    }

    private String getDateAfter(Date date, int days, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return sdf.format(calendar.getTime());
    }

    // ---------------------------

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public List<DepSbsZfqs> getRecordList() {
        return recordList;
    }

    public void setRecordList(List<DepSbsZfqs> recordList) {
        this.recordList = recordList;
    }

    public String getObtainDate() {
        return obtainDate;
    }

    public void setObtainDate(String obtainDate) {
        this.obtainDate = obtainDate;
    }

    public DepSbsZfqsService getDepSbsZfqsService() {
        return depSbsZfqsService;
    }

    public void setDepSbsZfqsService(DepSbsZfqsService depSbsZfqsService) {
        this.depSbsZfqsService = depSbsZfqsService;
    }

    public FtpSbsFileService getFtpSbsFileService() {
        return ftpSbsFileService;
    }

    public void setFtpSbsFileService(FtpSbsFileService ftpSbsFileService) {
        this.ftpSbsFileService = ftpSbsFileService;
    }

    public TxSendFlag getSendFlag() {
        return sendFlag;
    }

    public void setSendFlag(TxSendFlag sendFlag) {
        this.sendFlag = sendFlag;
    }

    public DepSbsZfqs[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(DepSbsZfqs[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }
}
