package fip.service.fip;

import fip.repository.dao.FipCutpaybatMapper;
import fip.repository.dao.FipCutpaydetlMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.ProducerCallback;
import org.springframework.stereotype.Service;
import pub.platform.advance.utils.PropertyManager;

import javax.annotation.Resource;
import javax.jms.*;

/**
 * DEP数据交换平台接口
 * 银联：走通用交易接口
 * User: zhanrui
 * Date: 11-8-24
 * Time: 上午7:17
 * To change this template use File | Settings | File Templates.
 */
@Service
public class DepService {
    private static final Logger logger = LoggerFactory.getLogger(DepService.class);

    private static String DEP_USERNAME = PropertyManager.getProperty("jms.username");
    private static String DEP_PWD = PropertyManager.getProperty("jms.password");

    @Resource
    private JmsTemplate jmsSendTemplate;

    @Resource
    private JmsTemplate jmsRecvTemplate;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    FipCutpaydetlMapper cutpaydetlMapper;

    @Autowired
    FipCutpaybatMapper cutpaybatMapper;


    /**
     * 通过MQ 向 DEP发送消息 (通用交易接口)
     * @param channelId
     * @param msgtxt
     * @param bizId
     * @return
     * @throws JMSException
     */
    public String sendDepMessage(final String channelId, final String msgtxt, final String bizId) throws JMSException {
        TextMessage msg = (TextMessage) jmsSendTemplate.execute(new ProducerCallback<Object>() {
            public Object doInJms(Session session, MessageProducer producer) throws JMSException {
                TextMessage msg = session.createTextMessage(msgtxt);
                msg.setStringProperty("JMSX_CHANNELID", channelId);
                msg.setStringProperty("JMSX_APPID", "HAIERFIP");
                msg.setStringProperty("JMSX_BIZID", bizId.toUpperCase());
                msg.setStringProperty("JMSX_USERID", DEP_USERNAME);
                msg.setStringProperty("JMSX_PASSWORD", DEP_PWD);
                producer.send(msg);
                return msg;
            }
        });
        String msgid = msg.getJMSMessageID();
        logger.info("MQ消息发送, MSGID=" + msgid);
        logger.debug("MQ消息发送, MSGID=" + msgid + "\n  报文内容: \n" + msg.getText());
        return msgid;
    }

    public String recvDepMessage(String msgid) {
        Message msg = null;
        try {
            String filter = "JMSCorrelationID = '" + msgid + "'";
            msg = jmsRecvTemplate.receiveSelected(filter);

            //TODO 超时处理
            if (msg != null) {
                if (msg instanceof TextMessage) {
                    logger.info("接收消息:" + ((TextMessage) msg).getText());
                    return ((TextMessage) msg).getText();
                } else {
                    throw new RuntimeException("消息类型错误。");
                }
            } else {
                logger.info("超时");
                throw new RuntimeException("接收报文超时.");
            }
        } catch (Exception e) {
            logger.error("接收消息失败。", e);
            throw new RuntimeException("接收消息失败。" + e.getMessage());
        }
    }

}
