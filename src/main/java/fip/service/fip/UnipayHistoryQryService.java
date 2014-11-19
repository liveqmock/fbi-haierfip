package fip.service.fip;

import fip.gateway.unionpay.domain.TIA200002;
import fip.gateway.unionpay.txn.TOA200002Handler;
import fip.repository.model.fip.UnipayQryParam;
import fip.repository.model.fip.UnipayQryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-11-14
 * Time: 下午4:24
 */
@Service
public class UnipayHistoryQryService {
    private static final Logger logger = LoggerFactory.getLogger(UnipayHistoryQryService.class);
    private static  String  DEP_CHANNEL_ID_UNIPAY = "100";

    @Resource
    private JmsTemplate jmsSendTemplate;

    @Resource
    private JmsTemplate jmsRecvTemplate;

    @Resource
    private DepService depService;

    /**
     * 同步查询数据
     * @param record
     * @param resultList
     * @return
     */
    public Map<String, String> queryCurrentData(UnipayQryParam record, List<UnipayQryResult> resultList) {
        record.setPAGE_NUM(String.valueOf("1"));
        String msgID = sendQueryMessage(record);
        String msgtxt = depService.recvDepMessage(msgID);

        if (msgtxt != null) {
            TOA200002Handler handler = new TOA200002Handler(msgtxt);
            Map<String, String> rtnMainMsgMap = handler.getRtnMainMsgMap();
            String rtn_code = rtnMainMsgMap.get("RTN_CODE");
            if (rtn_code.equals("0000")){
                resultList.addAll(handler.getToaDetailList());
                int pageNum = handler.getPageNum();
                if (pageNum > 1) {
                    for (int i = 2; i<= pageNum; i++){
                        record.setPAGE_NUM(String.valueOf(i));
                        msgID = sendQueryMessage(record);
                        msgtxt = depService.recvDepMessage(msgID);
                        handler = new TOA200002Handler(msgtxt);
                        resultList.addAll(handler.getToaDetailList());
                    }
                }
            }
            return rtnMainMsgMap;
        } else {
            throw new RuntimeException("返回报文为空");
        }
    }
    public String sendQueryMessage(UnipayQryParam record) {
        try {
            Map<String,UnipayQryParam> paramMap = new HashMap<String, UnipayQryParam>();
            paramMap.put("paramBean", record);

            TIA200002 tia = new TIA200002(paramMap);
            String msgtxt = tia.toXml(record.getBIZ_ID());
            return depService.sendDepMessage(DEP_CHANNEL_ID_UNIPAY, msgtxt, record.getBIZ_ID());
        } catch (Exception e) {
            logger.error("MQ消息发送失败", e);
            throw new RuntimeException("MQ消息发送失败", e);
        }
    }


}
