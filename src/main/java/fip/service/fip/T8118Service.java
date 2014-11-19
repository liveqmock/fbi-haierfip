package fip.service.fip;

import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.txn.T8118.T8118Handler;
import fip.gateway.sbs.txn.T8118.T8118SOFDataDetail;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-9-26
 * Time: 下午4:14
 * To change this template use File | Settings | File Templates.
 */
@Deprecated

public class T8118Service {
    private static final Logger logger = LoggerFactory.getLogger(T8118Service.class);
    private static DecimalFormat df = new DecimalFormat("0.00");

    /**
     * 获取账户余额*/

    public double qryActBal(String strActno) {
        T8118Handler handler = new T8118Handler();

        List list = new ArrayList();
        list.add("111111");
        list.add("010");    //柜员机构号
        list.add("60");     //柜员部门号
        list.add("010");     //帐户机构号
        String actno = StringUtils.rightPad(strActno, 22, ' ');
        list.add(actno);        //账户号码 22位
        SBSRequest request = new SBSRequest("8118",list);
        SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();
        T8118SOFDataDetail sofDataDetail = new T8118SOFDataDetail();

        response.setSofDataDetail(sofDataDetail);
        handler.run(request, response);
        String strbalamt = sofDataDetail.getBOKBAL().replace(",","");
        double balamt = -Double.parseDouble(strbalamt);
        logger.debug("formcode:"+ response.getFormcode());
        return balamt;
    }
 }
