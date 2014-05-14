package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T201003.T201003Request;
import fip.gateway.newcms.domain.T201003.T201003Response;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pgw.NewCmsManager;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:22:35
 * To change this template use File | Settings | File Templates.
 */
public class T201003CTL extends BaseCTL {
    private Log logger = LogFactory.getLog(this.getClass());



    public Map<String,String> startQry(String appno) {
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(T201003Request.class);
        xstream.processAnnotations(T201003Response.class);

        T201003Request request = new T201003Request();

        request.initHeader("0100", "201003", "3");

        request.setStdsqdh(appno);

        NewCmsManager ncm = new NewCmsManager();
        String strXml = "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xstream.toXML(request);

        //发送请求
        String responseBody = ncm.doPostXml(strXml);

        if (StringUtils.isEmpty(responseBody)) {
            throw new RuntimeException("信贷系统返回数据为空。");
        }

        T201003Response response = (T201003Response) xstream.fromXML(responseBody);

        Map<String,String> rtnMap = new HashMap();
        rtnMap.put("stdshmc", response.getStdshmc());
        rtnMap.put("stdkkzh", response.getStdkkzh());
        rtnMap.put("stdcwxx", response.getStdcwxx());

        return rtnMap;
    }

}
