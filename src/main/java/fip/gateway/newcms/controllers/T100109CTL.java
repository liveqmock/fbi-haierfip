package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T100109.T100109Request;
import fip.gateway.newcms.domain.T100109.T100109Response;
import fip.gateway.newcms.domain.T100109.T100109ResponseRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pgw.HttpManager;
import pgw.NewCmsManager;

import java.util.ArrayList;
import java.util.List;


/**
 * 专卖店 提取 还款记录
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:22:35
 */

public class T100109CTL extends BaseCTL implements java.io.Serializable {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private T100109ResponseRecord responseRecord = new T100109ResponseRecord();
    List<T100109ResponseRecord> responseFDList = new ArrayList();


    public T100109ResponseRecord getResponseRecord() {
        return responseRecord;
    }

    public void setResponseRecord(T100109ResponseRecord responseRecord) {
        this.responseRecord = responseRecord;
    }

    public List<T100109ResponseRecord> getResponseFDList() {
        return responseFDList;
    }

    public void setResponseFDList(List<T100109ResponseRecord> responseFDList) {
        this.responseFDList = responseFDList;
    }

    //==============================================================
    public static void main(String[] args) throws Exception {

        T100109CTL ctl = new T100109CTL();
        ctl.start("0");
    }


    public String query() {
        setResponseFDList(start("0"));
        return null;
    }

    /**
     * @param qryType 0-未扣款 1-已扣款
     */
    public List<T100109ResponseRecord> start(String qryType) {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T100109Request.class);
        xstream.processAnnotations(T100109Response.class);

        T100109Request request = new T100109Request();

        request.initHeader("0100", "100109", "3");

        request.setStdcxlx(qryType);
        int pkgcnt = 1000;
        int startnum = 1;
        request.setStdymjls(String.valueOf(pkgcnt));

        NewCmsManager ncm = new NewCmsManager();
        //HttpManager ncm = new HttpManager("http://10.143.19.124:10003/PLoanSysWeb/FipProcess.dispatcher");

        List<T100109ResponseRecord> responseList = new ArrayList<T100109ResponseRecord>();
        int totalcount = processTxn(responseList, ncm, xstream, request, pkgcnt, startnum);
        if (totalcount == 0) {
            return responseList;
        }
        logger.info("received list zise:" + responseList.size());
        if (totalcount != responseList.size()) {
            logger.error("获取还款数据笔数有误！应收笔数：" + responseList.size() + "实收笔数：" + totalcount);
            throw new RuntimeException("获取还款数据笔数有误.");
        }
        return responseList;
    }

    public int processTxn(List<T100109ResponseRecord> responseList,
                          NewCmsManager ncm, XStream xstream, T100109Request request,
                          int pkgcnt, int startnum) {
        request.setStdqsjls(String.valueOf(startnum));

        String strXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xstream.toXML(request);
        //发送请求
        String responseBody = ncm.doPostXml(strXml);
        if (StringUtils.isEmpty(responseBody)) {
            return 0;
        }
        T100109Response response = (T100109Response) xstream.fromXML(responseBody);

        //头部总记录数
        String std400acur = response.getStd400acur();
        if (std400acur == null || std400acur.equals("")) {
            std400acur = "0";
        }
        int totalcount = Integer.parseInt(std400acur);

        if (totalcount == 0) {
            //
        } else {
            List<T100109ResponseRecord> tmpList = response.getBody().getContent();

            int currCnt = tmpList.size();
            logger.info("totalcount:" + totalcount + " currCnt:" + currCnt + " startnum:" + startnum);

            //打包到返回list中
            for (T100109ResponseRecord record : tmpList) {
                responseList.add(record);
            }

            //一个包不可以处理完
            if (totalcount > pkgcnt) {
                startnum += pkgcnt;
                if (startnum <= totalcount) {
                    processTxn(responseList, ncm, xstream, request, pkgcnt, startnum);
                }
            }
        }

        return totalcount;
    }
}
