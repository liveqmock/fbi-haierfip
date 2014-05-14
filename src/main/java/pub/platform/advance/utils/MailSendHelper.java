package pub.platform.advance.utils;

import java.util.*;
import java.io.*;
import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;

import pub.platform.form.config.*;

public class MailSendHelper {

	public String host;
	public String sendMailAddress;
	public String passwd;

	public String subject;
	public String message;
	public List receiverList;
	public List appendlist;

	/**
	 * 初始化参数
	 * @param subject 邮件主题
	 * @param message 邮件文本内容
	 * @param receiverList 接受者邮件地址列表
	 * @param appendlist 附件文件名列表
	 */
	public MailSendHelper(String subject, String message, List receiverList, List appendlist){
		this.subject = subject;
		this.message = message;
		this.receiverList = receiverList;
		this.appendlist = appendlist;
	}

	public void send()throws Exception{
		if(this.host == null){
			this.host = EnumerationType.getEnumName("MAIL", "mail.smtp.host");
		}
		if(this.sendMailAddress == null){
			this.sendMailAddress = EnumerationType.getEnumName("MAIL", "mail.sender");
		}
		if(this.passwd == null){
			this.passwd = EnumerationType.getEnumName("MAIL", "mail.password");
		}

        // 创建认证实例
        AuthenticatorImpl auth
                = new AuthenticatorImpl(this.sendMailAddress, this.passwd);

        // 创建属性
        Properties props = System.getProperties();
        props.put("mail.smtp.host", this.host);
        props.put("mail.smtp.auth", "true");

        // 创建会话
        Session session = Session.getInstance(props, auth);
        session.setDebug(false);

        // 创建消息
        MimeMessage msg = new MimeMessage(session);
        msg.setFrom(new InternetAddress(this.sendMailAddress));

        // 设置接受方
        int count = receiverList.size();
        InternetAddress[] address = new InternetAddress[count];
        for (int i = 0; i < count; i++) {
            address[i] = new InternetAddress((String)receiverList.get(i));
        }
        msg.setRecipients(Message.RecipientType.TO, address);

        // 设置主题
        msg.setSubject(this.subject);

        // 设置标题
        MimeBodyPart mbp = new MimeBodyPart();
        mbp.setText(this.message);

        // 添加所有消息体
        Multipart mp = new MimeMultipart();
        // 添加标题
        mp.addBodyPart(mbp);

        // 添加所有附件
        if(appendlist != null){
        	for (int i = 0; i < appendlist.size(); i++) {
        		// 创建附件消息体
        		MimeBodyPart append = new MimeBodyPart();
        		String fileName = (String)this.appendlist.get(i);
        		File file = new File(fileName);
        		FileDataSource fds = new FileDataSource(file);
        		append.setDataHandler(new DataHandler(fds));
        		append.setFileName(MimeUtility.encodeWord(fileName));
        		mp.addBodyPart(append);
        	}
        }
        // 设置消息内容
        msg.setContent(mp);

        // 设置时间
        msg.setSentDate(new Date());

        // 发送邮件
        Transport.send(msg);

    }

}
