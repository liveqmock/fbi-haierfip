package fip.service.fip;

import fip.gateway.ftp.FtpClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.platform.advance.utils.PropertyManager;

import javax.jms.JMSException;
import java.io.IOException;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-4-1
 * Time: 下午1:15
 * To change this template use File | Settings | File Templates.
 */
@Service
public class FtpSbsFileService {

    private static final Logger logger = LoggerFactory.getLogger(FtpSbsFileService.class);

    @Autowired
    private JobLogService jobLogService;

    //  ftp连接sbs获取清算明细文件内容
    public String[] obtainSbsZfqsMsgs(String date) throws JMSException, IOException {
        logger.info("FTP 连接SBS 执行交易开始----------：");
        // 连接sbs获取数据anonymous ident
        FtpClient ftpClient = new FtpClient(PropertyManager.getProperty("SBS_HOSTIP"), "anonymous", "");
        String lines = ftpClient.readCotent("pub/print/" + date, PropertyManager.getProperty("SBS_ZFQS_FILE_NAME"));
        logger.info("日期：" + date + "  SBS总分账户清算明细获取结束。");
        ftpClient.logout();
        // 记录日志
        appendFtpSbsJoblog(UUID.randomUUID().toString(), date);
        logger.info("读取到sbs文件内容:  \n" + lines);
        return StringUtils.isEmpty(lines) ? null : lines.split("\n");
    }

    public void appendFtpSbsJoblog(String pkid, String bizDate) {
        jobLogService.insertNewJoblog(pkid, "dep_sbs_zfqs", "ftp连接读取sbs文件", "ftp连接读取sbs内部总分账户清算结果文件,业务日期[" + bizDate + "]", "9999", "系统管理员");
    }
}
