package fip.gateway.sbs.txn.Taa56;

import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.core.SOFDataDetail;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
public class Taa56Test {
    private static Logger logger = LoggerFactory.getLogger(Taa56Test.class);

    public static void main(String[] argv) {
        Taa56Handler handler = new Taa56Handler();

        DecimalFormat df = new DecimalFormat("#############0.00");

        List list = new ArrayList();
        list.add("01");
        list.add("801000026131041001"); //同业账号
        list.add("3");
        list.add(StringUtils.leftPad("", 72, ' '));
        list.add(StringUtils.leftPad("", 6, ' '));

        list.add("N");
        list.add("123456789012345678");  //外围系统流水号
        list.add(" ");
        list.add(StringUtils.leftPad("", 10, ' '));
        list.add(StringUtils.leftPad("", 12, ' '));

        list.add(StringUtils.leftPad("", 8, ' '));
        list.add("3");
        list.add(StringUtils.leftPad("", 8, ' '));
        list.add(StringUtils.leftPad("", 4, ' '));
        list.add(df.format(new BigDecimal(0.01)));

        list.add("01");
        list.add("801000010002011001"); //转入帐户
        list.add(StringUtils.leftPad("", 72, ' '));
        list.add(" ");
        list.add("20110829"); //交易日期

        list.add(StringUtils.leftPad("", 30, ' '));
        list.add(StringUtils.leftPad("", 4, ' '));
        list.add(" ");
        list.add(" ");
        list.add("   ");

        SBSRequest request = new SBSRequest("aa56", list);

        SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();

        SOFDataDetail sofDataDetail = new Taa56SOFDataDetail();

        response.setSofDataDetail(sofDataDetail);

        handler.run(request, response);
        logger.debug("formcode:"+ response.getFormcode());
    }
}
