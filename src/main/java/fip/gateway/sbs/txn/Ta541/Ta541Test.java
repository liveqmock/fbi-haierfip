package fip.gateway.sbs.txn.Ta541;

import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.core.SOFDataDetail;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public class Ta541Test {
    private static Logger logger = LoggerFactory.getLogger(Ta541Test.class);

    public static void main(String[] argv) {
        Ta541Handler handler = new Ta541Handler();

        DecimalFormat df = new DecimalFormat("#############0.00");

        List list = new ArrayList();

        list.add("20110101");          //交易日期
        list.add(StringUtils.rightPad("123", 18, ' '));      //交易流水号

        String account = StringUtils.rightPad("12312", 22, ' ');
        list.add(account);//贷款帐户                 22位，不足补空格


        String principal = df.format(123.12);
        String servicecharge = df.format(122);
        String breachfee = df.format(122);
        String latefee = df.format(122);

        if ("0".equals("0")) { //正常帐单
            list.add("+" + StringUtils.leftPad(principal, 16, '0'));     //本金金额
            list.add("+0000000000000.00");     //违约金金额
            list.add("+0000000000000.00");     //滞纳金金额
            list.add("+" + StringUtils.leftPad(servicecharge, 16, '0'));     //手续费金额
        } else {
            list.add("+0000000000000.00");     //本金金额
            list.add("+" + StringUtils.leftPad(breachfee, 16, '0'));     //违约金金额
            list.add("+" + StringUtils.leftPad(latefee, 16, '0'));     //滞纳金金额
            list.add("+0000000000000.00");     //手续费金额
        }
        //TODO: 参数化
        list.add("105                           ");//摘要


        SBSRequest request = new SBSRequest("a541", list);

        SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();

        SOFDataDetail sofDataDetail = new Ta541SOFDataDetail();

        response.setSofDataDetail(sofDataDetail);

        handler.run(request, response);
        logger.debug("formcode:"+ response.getFormcode());
    }
}
