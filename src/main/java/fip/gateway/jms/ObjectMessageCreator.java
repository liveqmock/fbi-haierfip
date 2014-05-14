package fip.gateway.jms;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.MessageCreator;

import javax.jms.*;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ObjectMessageCreator implements MessageCreator {

    private static final Logger logger = LoggerFactory.getLogger(ObjectMessageCreator.class);
    private String corelationMessageId;
    private Message message;
    private Serializable obj;
    private HashMap<String, String> propertyMap;

    public ObjectMessageCreator(Serializable obj, String corelationMessageId, HashMap<String, String> propertyMap) {
        this.obj = obj;
        this.corelationMessageId = corelationMessageId;
        this.propertyMap = propertyMap;
    }

    @Override
    public Message createMessage(Session session) {

        ObjectMessage objMessage = null;
        try {
            objMessage = session.createObjectMessage(obj);
            if (!StringUtils.isEmpty(corelationMessageId)) {
                objMessage.setJMSCorrelationID(corelationMessageId);
            }
            if(propertyMap != null) {
                for(Map.Entry<String, String> entry : propertyMap.entrySet()) {
                    objMessage.setStringProperty(entry.getKey(), entry.getValue());
                }
            }
            message = objMessage;
        } catch (Exception e) {
            logger.error("消息生成异常！", e);
            throw new RuntimeException(e);
        }
        return objMessage;
    }

    public String getMessageID() throws JMSException {
        return message == null ? null : message.getJMSMessageID();
    }
}
