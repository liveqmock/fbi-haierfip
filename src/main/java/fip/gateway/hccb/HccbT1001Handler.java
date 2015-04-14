package fip.gateway.hccb;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.hccb.model.T1001Request;
import fip.gateway.hccb.model.T1001Response;
import fip.gateway.hccb.model.T1003Response;
import fip.gateway.hccb.model.TxnHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pgw.HttpManager;

import java.util.ArrayList;
import java.util.List;

/**
 * ��ȡС�� ���ۼ�¼
 * Created by zhanrui on 2015/4/13.
 */

public class HccbT1001Handler implements HccbTxnHandler {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private XStream stream = new XStream(new DomDriver("GBK"));

    @Override
    public void process(HccbContext context) {
        List<T1001Response.Record> records = new ArrayList<T1001Response.Record>();
        int pageSize = 10;
        processT1001(1, pageSize, records);

        context.setResponse(records);
    }

    private void processT1001(int pageNum, int pageSize, List<T1001Response.Record> recordList) {
        T1001Request request = new T1001Request();
        request.setHead(new TxnHead().txncode("1001"));
        request.getBody().setQrytype("01");  //01-��������
        request.getBody().setPagenum("" + pageNum);    //Ϊ��ʱĬ��Ϊ��һҳ
        request.getBody().setPagesize("" + pageSize);  //ÿ�������¼��  ��Ϊ�� Ϊ��ʱĬ��Ϊ1000�ʼ�¼

        stream.processAnnotations(T1001Request.class);
        String reqXml = stream.toXML(request);

        //�������ͨѶ
        HttpManager manager = new HttpManager(hccbServerUri);
        String respXml = manager.doPostXml(reqXml);

        stream.processAnnotations(T1001Response.class);
        T1001Response response = (T1001Response) stream.fromXML(respXml);

        //TODO
        String rtnCode = response.getHead().getRtncode();
        String rtnMsg = response.getHead().getRtnmsg();
        if ("0000".equals(rtnCode)) {
            recordList.addAll(response.getBody().getRecords());

            int pageSum = Integer.parseInt(response.getBody().getPagesum());
            pageNum++;
            if (pageNum <= pageSum) {
                processT1001(pageNum, pageSize, recordList);
            }
        } else {
            //TODO
            logger.error("===T1001 response: �쳣����-" + rtnCode + rtnMsg);
        }
    }
}
