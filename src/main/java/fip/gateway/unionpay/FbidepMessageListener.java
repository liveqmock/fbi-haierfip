package fip.gateway.unionpay;

import com.ccb.util.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

@Deprecated
public class FbidepMessageListener implements MessageListener {
    private static final Logger logger = LoggerFactory.getLogger(FbidepMessageListener.class);

    @Autowired
    private RtnMessageHandler rtnMessageHandler;

    @Override
    public void onMessage(Message message) {
        if (message != null && message instanceof TextMessage) {
            String msgContent = null;
            String rtnCode = "";
            String rtnMsg = "";
            try {
                rtnCode = message.getStringProperty("depReturnCode");
                rtnMsg = message.getStringProperty("depReturnMsg");
                msgContent = ((TextMessage) message).getText();
                logger.info("FIP收到MQ消息的文本内容：\n" + msgContent);
            } catch (JMSException e) {
                logger.error("FIP收到消息时出错！", e);
            }

            if ("200".equals(rtnCode)) {
                if (!StringUtils.isEmpty(msgContent)) {
                    int txCode = Integer.parseInt(StringUtil.getSubstrBetweenStrs(msgContent, "<TRX_CODE>", "</TRX_CODE>"));
                    // TODO 是否有必要将接口代号写入properties文件
                    switch (txCode) {
                        case 100001:  // TODO 硬编码
                            rtnMessageHandler.handle100001Message(msgContent);
                            break;
                        case 100004:  // TODO 硬编码
                            rtnMessageHandler.handle100004Message(msgContent);
                            break;
                        case 200001:  // TODO 硬编码
                            rtnMessageHandler.handle200001BatchMessage(msgContent);
                            break;
                    }
                } else {
                    //未收到银联的返回结果，只处理DEP的返回结果
                    logger.error("FIP收到消息时出错，消息为空或类型错误！");
                }
            }else if ("400".equals(rtnCode)) {
                logger.error("消息错误，请求报文出错:" + rtnMsg);
                //TODO
            }else if ("504".equals(rtnCode)) {
                logger.error("处理超时:" + rtnMsg);
                //TODO
            }else{
                logger.error("处理错误:" + rtnMsg);
                //TODO
            }
        } else {
            logger.error("FIP收到消息时出错，消息为空或类型错误！");
        }
    }

}
