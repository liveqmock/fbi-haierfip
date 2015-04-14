package fip.gateway.hccb;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.hccb.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pgw.HttpManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 批量回写
 * Created by zhanrui on 2015/4/13.
 */

public class HccbT1003Handler implements HccbTxnHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private XStream stream = new XStream(new DomDriver("GBK"));

    @Override
    public void process(HccbContext context) {
        T1003Request request = (T1003Request)context.getRequest();
        request.setHead(new TxnHead().txncode("1003"));

        stream.processAnnotations(T1003Request.class);
        String reqXml = stream.toXML(request);

        //与服务器通讯
        HttpManager manager = new HttpManager(hccbServerUri);
        String respXml = manager.doPostXml(reqXml);

        stream.processAnnotations(T1003Response.class);
        T1003Response response = (T1003Response) stream.fromXML(respXml);

        String rtnCode = response.getHead().getRtncode();
        String rtnMsg = response.getHead().getRtnmsg();

        Map<String, String> paraMap = context.getParaMap();
        paraMap.put("rtnCode", rtnCode);
        paraMap.put("rtnMsg", rtnMsg);
    }
}
