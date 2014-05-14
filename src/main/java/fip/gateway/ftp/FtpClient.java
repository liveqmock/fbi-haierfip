package fip.gateway.ftp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-2-4
 * Time: 下午4:22
 * To change this template use File | Settings | File Templates.
 */
public class FtpClient {
    private static Logger logger = LoggerFactory.getLogger(FtpClient.class);
    private FTPClient ftp;

    public FtpClient(String ip, String username, String pwd) {
        ftp = new FTPClient();
        //config.setXXX(YYY); // change required options
        String systemKey = FTPClientConfig.SYST_UNIX;
        String serverLanguageCode = "zh";
        FTPClientConfig config = new FTPClientConfig(systemKey);
        config.setServerLanguageCode(serverLanguageCode);
        config.setDefaultDateFormatStr("yyyy-MM-dd");
//        ftp.setControlKeepAliveTimeout(300);
        ftp.configure(config);
        try {
            ftp.connect(ip);
            logger.info("Connected to " + ip);
            logger.info("ftp reply string: " + ftp.getReplyString());

            // After connection attempt, you should check the reply code to verify
            // success.
            int reply = ftp.getReplyCode();
            logger.info("Ftp reply code : " + reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                logger.error("FTP server refused connection.");
            }
            ftp.login(username, pwd);
            // 设置被动模式
            ftp.enterLocalPassiveMode();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public String readCotent(String remotePath, String fileName) throws IOException {
        StringBuilder msg = new StringBuilder();
        logger.info("change directory to remote path: " + remotePath + "/" + fileName);
        // 改变工作路径
        if (!ftp.changeWorkingDirectory(remotePath)) {
            logger.error("changeDirectory to remote path failed.目录不存在");
            return msg.toString();
        }
        // 列出当前工作路径下的文件列表
        List<FTPFile> fileList = Arrays.asList(ftp.listFiles());
        if (fileList == null || fileList.size() == 0) {
            logger.error("no files in ftp server.");
            return msg.toString();
        }
        for (FTPFile ftpfile : fileList) {
            if (ftpfile.getName().equals(fileName)) {
                logger.info("----  start read file : " + fileName + "------");
                InputStream inputStream = ftp.retrieveFileStream(ftpfile.getName());
                InputStreamReader inputsr = new InputStreamReader(inputStream);
                BufferedReader br = new BufferedReader(inputsr);
                String line = null;
                while ((line = br.readLine()) != null) {
                    if (!StringUtils.isEmpty(line)) {
                        msg.append(line).append("\n");
                    }
                }
                br.close();
                inputsr.close();
                inputStream.close();
                break;
            }
        }
        return msg.length() > 1 ? msg.substring(0, msg.length() - 1) : "";
    }

    public void logout() throws IOException {
        ftp.logout();
        if (ftp.isConnected()) {
            ftp.disconnect();
        }
    }

    public static void main(String[] args) {
        // ftp://192.168.91.5/pub/print/2013-01-17/
        try {
            FtpClient ftpClient = new FtpClient("192.168.91.5", "anonymous", "");
            String msg = ftpClient.readCotent("pub/print/2013-02-17", "rptmpc51_1.010");
            String[] lines = msg.split("\n");
            for (String line : lines) {
                System.out.println(line);
            }
            ftpClient.logout();
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}