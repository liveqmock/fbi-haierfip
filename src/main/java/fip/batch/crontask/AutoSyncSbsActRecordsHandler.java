package fip.batch.crontask;

import fip.batch.common.tool.OperationValve;
import fip.repository.model.DepSbsZfqs;
import fip.service.fip.DepSbsZfqsService;
import fip.service.fip.FtpSbsFileService;
import ibp.service.IbpSbsActService;
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
 * Time: ����10:32
 * To change this template use File | Settings | File Templates.
 */

@Component
public class AutoSyncSbsActRecordsHandler {
    private static final Logger logger = LoggerFactory.getLogger(AutoSyncSbsActRecordsHandler.class);

    @Autowired
    private IbpSbsActService ibpSbsActService;

    @Autowired
    @Qualifier("propertyFileOperationValve")
    private OperationValve operationValve;

    // �Զ���ȡSBS�˻���Ϣ 8123����
    public void getSbsActRecords() {
        if (!isCronTaskOpen()) {
            logger.info("�Զ������������ѹرա�");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String date8 = sdf.format(new Date());
        try {
            // �Զ���ȡSBS�˻���Ϣ
            int cnt = ibpSbsActService.syncSbsActRecords();
            logger.info(date8 + "�Զ���ȡSBS�˻���Ϣ��ϸ����" + cnt);
        } catch (Exception e) {
            logger.error(date8 + "�Զ���ȡSBS�˻���Ϣʧ�ܡ�", e);
        }
    }

    private String getDateAfter(Date date, int days, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return sdf.format(calendar.getTime());
    }

    private boolean isCronTaskOpen() {
        try {
            return operationValve.isOpen("cron_task_mode");
        } catch (Exception e) {
            logger.error("��ȡ�����ļ�����", e);
            return false;
        }
    }
}
