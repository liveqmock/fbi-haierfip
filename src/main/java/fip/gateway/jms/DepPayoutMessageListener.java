package fip.gateway.jms;

import fip.online.DepAbstractTxnProcessor;
import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.ToaXml;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.util.HashMap;

@Component
public class DepPayoutMessageListener implements MessageListener {

    private static Logger logger = LoggerFactory.getLogger(DepPayoutMessageListener.class);
    @Autowired
    @Qualifier(value = "jmsFipOutTemplate")
    private JmsTemplate jmsFipOutTemplate;

    @Override
    public void onMessage(Message message) {
        String txnCode = null;
        String correlationID = null;
        try {
            correlationID = message.getJMSCorrelationID();

            txnCode = message.getStringProperty("JMSX_BIZID");
            HashMap<String, String> propertyMap = new HashMap<String, String>();
            propertyMap.put("JMSX_CHANNELID", message.getStringProperty("JMSX_CHANNELID"));
            propertyMap.put("JMSX_APPID", message.getStringProperty("JMSX_APPID"));
            propertyMap.put("JMSX_BIZID", txnCode);
            propertyMap.put("JMSX_SRCMSGFLAG", message.getStringProperty("JMSX_SRCMSGFLAG"));
            ObjectMessage objMsg = (ObjectMessage) message;
            TiaXml tiaXml = (TiaXml)objMsg.getObject();
            logger.info("【fip接收dep交易】correlationID：" + correlationID + ",交易码:" + txnCode);
            WebApplicationContext springContext = ContextLoader.getCurrentWebApplicationContext();
            DepAbstractTxnProcessor cbsTxnProcessor = (DepAbstractTxnProcessor) springContext.getBean("depTxn" + txnCode + "Processor");
            ToaXml toa = cbsTxnProcessor.run(tiaXml);
            jmsFipOutTemplate.send(new ObjectMessageCreator(toa, correlationID, propertyMap));
        } catch (Exception e) {
            logger.error("[fip]消息处理异常!", e);
        }
    }
}
