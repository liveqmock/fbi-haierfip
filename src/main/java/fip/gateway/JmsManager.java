package fip.gateway;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.fbi.dep.model.base.TIA;
import org.fbi.dep.model.base.TIAHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.advance.utils.PropertyManager;

import javax.jms.*;

/**
 * Created by IntelliJ IDEA. User: zhangxiaobo Date: 11-10-2 Time: 下午1:45 To
 * change this template use File | Settings | File Templates.
 */
public final class JmsManager {
    private static final Logger logger = LoggerFactory.getLogger(JmsManager.class);

    private static final String BROKER_URL = PropertyManager.getProperty("jms.brokerURL");
    private static final String USER_NAME = PropertyManager.getProperty("jms.username");
    private static final String PASSWORD = PropertyManager.getProperty("jms.password");
    private static final String requestQueueName = PropertyManager.getProperty("queue.fip.to.dep.object");
    private static final String responseQueueName = PropertyManager.getProperty("queue.fip.from.dep.object");
    private static final long MQ_TIME_OUT = PropertyManager.getLongProperty("jms.realtime_receive_timeout");

    private static Connection connection = null;
    private static final JmsManager messenger = new JmsManager();

    private JmsManager() {
    }

    public static synchronized JmsManager getInstance() {
        if (connection == null) {
            try {
                ConnectionFactory queueConnectionFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
                connection = queueConnectionFactory.createConnection();
                connection.start();
            } catch (JMSException e) {
                logger.error("初始化Jms client错误。", e);
                throw new RuntimeException("初始化Jms client错误。", e);
            }
        }
        return messenger;
    }

    public Object sendAndRecv(TIA tia) throws JMSException {
         return sendAndRecv(tia, MQ_TIME_OUT);
    }

    public Object sendAndRecv(TIA tia,  long timeout) throws JMSException {
        long start = System.currentTimeMillis();

        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            logger.info("MQ连接错误，重新连接中...", e);
            if (connection != null) {
                connection.close();
            }
            ConnectionFactory queueConnectionFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            connection = queueConnectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.info("MQ连接成功...");
        }

        long end1 = System.currentTimeMillis();

        try {
            Destination requestDestination = session.createQueue(requestQueueName);
            MessageProducer sender = session.createProducer(requestDestination);
            sender.setDeliveryMode(DeliveryMode.PERSISTENT);

            ObjectMessage message = session.createObjectMessage(tia);
            initMessage(tia, message);
            sender.send(message);
            long end2 = System.currentTimeMillis();

            String sentMsgID = message.getJMSMessageID();
            String filter = "JMSCorrelationID = '" + sentMsgID + "'";
            Destination responseDestination = session.createQueue(responseQueueName);
            MessageConsumer receiver = session.createConsumer(responseDestination, filter);

            ObjectMessage objectMessage = (ObjectMessage) receiver.receive(timeout);
            if (objectMessage == null) {
                throw new RuntimeException("消息接收超时！");
            } else {
                Object rtnBean = objectMessage.getObject();
                //TODO  logger
                long end3 = System.currentTimeMillis();
                logger.info("JMS connect time:" + (end1 - start) + " send time:" + (end2 - end1) + " recv time:" + (end3 - end2) +   " PID:" + Thread.currentThread().getId());

                return rtnBean;
            }
        } catch (JMSException e) {
            logger.error("消息处理失败！", e);
            throw new RuntimeException("消息处理失败！", e);
        } finally {
            session.close();
        }
    }

    //zhanrui 20120730 直接走core接口 主要用于与sbs直连
    //处理完成后 直接关闭 connection
    public byte[] sendAndRecvForDepCoreInterface(byte[] msgbuf) throws JMSException {
        String requestQueueName = PropertyManager.getProperty("queue.fip.to.dep");
        String responseQueueName = PropertyManager.getProperty("queue.fip.from.dep");

        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            //logger.info("MQ连接错误，重新连接中...", e);
            if (connection != null) {
                connection.close();
            }
            ConnectionFactory queueConnectionFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            connection = queueConnectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.info("MQ连接成功...");
        }

        try {
            Destination requestDestination = session.createQueue(requestQueueName);
            MessageProducer sender = session.createProducer(requestDestination);
            sender.setDeliveryMode(DeliveryMode.PERSISTENT);

            BytesMessage message = session.createBytesMessage();
            message.writeBytes(msgbuf);
            message.setStringProperty("JMSX_CHANNELID", "900");
            message.setStringProperty("JMSX_APPID", "haierfip");
            message.setStringProperty("JMSX_BIZID", "haierfip");
            message.setStringProperty("JMSX_TXCODE", "haierfip");
            message.setStringProperty("JMSX_USERID", "haierfip");
            message.setStringProperty("JMSX_PASSWORD", "haierfip");
            sender.send(message);
            String sentMsgID = message.getJMSMessageID();
            logger.info("MessageID : " + sentMsgID);

            String filter = "JMSCorrelationID = '" + sentMsgID + "'";
            Destination responseDestination = session.createQueue(responseQueueName);
            MessageConsumer receiver = session.createConsumer(responseDestination, filter);

            BytesMessage rtnMessage = (BytesMessage) receiver.receive(MQ_TIME_OUT);
            if (rtnMessage == null) {
                throw new RuntimeException("消息接收超时！");
            } else {
                byte[] rtnBytes = new byte[(int) rtnMessage.getBodyLength()];
                rtnMessage.readBytes(rtnBytes);
                return rtnBytes;
            }
        } catch (JMSException e) {
            logger.error("消息处理失败！", e);
            throw new RuntimeException("消息处理失败！", e);
        } finally {
            session.close();
            connection.close(); //TODO  若不关闭需进行内存泄露检查
            logger.info("" + connection);
        }
    }

    private void initMessage(TIA tia, ObjectMessage message) throws JMSException {
        TIAHeader header = tia.getHeader();
        message.setStringProperty("JMSX_CHANNELID", header.CHANNEL_ID);
        message.setStringProperty("JMSX_APPID", header.APP_ID);
        message.setStringProperty("JMSX_BIZID", header.BIZ_ID == null ? header.APP_ID : header.BIZ_ID);
        message.setStringProperty("JMSX_TXCODE", header.TX_CODE);
        message.setStringProperty("JMSX_USERID", header.USER_ID);
        message.setStringProperty("JMSX_PASSWORD", header.PASSWORD);
        message.setStringProperty("JMSX_REQSN", header.REQ_SN);
    }

    public Object recvByMsgId(String msgId) throws JMSException {
        Session session = null;
        try {
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        } catch (JMSException e) {
            logger.info("MQ连接错误，重新连接中...");
            if (connection != null) {
                connection.close();
            }
            ConnectionFactory queueConnectionFactory = new ActiveMQConnectionFactory(USER_NAME, PASSWORD, BROKER_URL);
            connection = queueConnectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            logger.info("MQ连接成功...");
        }
        Destination responseDestination = session.createQueue(responseQueueName);

        String filter = "JMSCorrelationID = '" + msgId + "'";
        try {
            MessageConsumer receiver = session.createConsumer(responseDestination, filter);
            ObjectMessage objectMessage = (ObjectMessage) receiver.receive(MQ_TIME_OUT);
            if (objectMessage == null) {
                throw new RuntimeException("消息接收超时！");
            } else {
                Object rtnBean = objectMessage.getObject();
                logger.info(rtnBean.toString());
                return rtnBean;
            }
        } catch (JMSException e) {
            logger.error("消息处理失败！", e);
            throw new RuntimeException("消息处理失败！", e);
        } finally {
            session.close();
        }
    }
}
