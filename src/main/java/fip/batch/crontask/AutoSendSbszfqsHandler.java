package fip.batch.crontask;

import fip.batch.common.tool.OperationValve;
import fip.repository.model.DepSbsZfqs;
import fip.service.fip.DepSbsZfqsService;
import fip.service.fip.FtpSbsFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-4-2
 * Time: 上午10:32
 * To change this template use File | Settings | File Templates.
 */

@Component
public class AutoSendSbszfqsHandler {
    private static final Logger logger = LoggerFactory.getLogger(AutoSendSbszfqsHandler.class);

    @Autowired
    private DepSbsZfqsService depSbsZfqsService;
    @Autowired
    private FtpSbsFileService ftpSbsFileService;

    @Autowired @Qualifier("propertyFileOperationValve")
    private OperationValve operationValve;

    // 自动获取并发送 昨日 SBS内部总分账户清算明细
    public void obtainAndSendSbsZfqsRecords() {
        if (!isCronTaskOpen()) {
            logger.info("自动批量处理开关已关闭。");
            return;
        }
        String date10 = getDateAfter(new Date(), -1, "yyyy-MM-dd");
        try {
            // 获取总分账户清算数据
            String[] lines = ftpSbsFileService.obtainSbsZfqsMsgs(date10);
            int obtainCnt = 0;
            if (lines == null || lines.length == 0) {
                logger.info("本次未获取到SBS数据。[" + date10 + "]没有总分账户清算明细。");
                return;
            } else {
                // 保存
                obtainCnt = depSbsZfqsService.saveZfqsRecords(lines);
            }
            String date8 = getDateAfter(new Date(), -1, "yyyyMMdd");
            List<DepSbsZfqs> recordList = depSbsZfqsService.qryUnSendRecordsByDate(date8, date8);
            String operTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            int sendCnt = 0;
            for (DepSbsZfqs record : recordList) {
                String snMsg = depSbsZfqsService.sendSbsZfqs(record);
                sendCnt += depSbsZfqsService.updateToSentSts(snMsg, operTime) == 1 ? 1 : 0;
            }
           /* List<DepSmsCfg> smsCfgList = smsCfgService.qrySmsCfgsByTxnCode("sendSbsZfqs");
            String msg = date8 + "获取明细笔数：" + lines.length + ",保存笔数：" + obtainCnt + ",重复数："
                    + (lines.length - obtainCnt) + ",JDE接收处理成功数：" + sendCnt + " JDE返回失败数：" + (recordList.size() - sendCnt);
            for(DepSmsCfg sms : smsCfgList) {
                SmsTool.sendMessage(sms.getUserPhone(), "[" + sms.getTxnName() + "]" + msg );
            }*/
            logger.info(date10 + "自动发送SBS内部总分账户清算明细数：" + sendCnt);
            logger.info(date10 + " obtainAndSendSbsZfqsRecords：success.");
        } catch (Exception e) {
            logger.error(date10 + "自动获取并发送SBS内部总分账户清算明细失败。", e);
        }
    }

    private String getDateAfter(Date date, int days, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return sdf.format(calendar.getTime());
    }

    private boolean isCronTaskOpen(){
        try {
            return operationValve.isOpen("cron_task_mode");
        } catch (Exception e) {
            logger.error("读取参数文件错误", e);
            return false;
        }
    }
}
