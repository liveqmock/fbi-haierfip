package fip.gateway.sbs.txn.Ta543;

import fip.common.constant.BizType;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;
import fip.gateway.sbs.core.SOFDataDetail;
import fip.repository.model.FipCutpaydetl;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 贷款还款
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午3:30
 * To change this template use File | Settings | File Templates.
 */
public class Ta543Test {
    private static Logger logger = LoggerFactory.getLogger(Ta543Test.class);

    public static void main(String[] argv) {
        Ta543Handler handler = new Ta543Handler();

        FipCutpaydetl cutpaydetl = new FipCutpaydetl();
        SBSRequest request = new SBSRequest("a543", assembleTa543Param(cutpaydetl));

        SBSResponse4SingleRecord response = new SBSResponse4SingleRecord();

        SOFDataDetail sofDataDetail = new Ta543SOFDataDetail();

        response.setSofDataDetail(sofDataDetail);

        handler.run(request, response);
        logger.debug("formcode:"+ response.getFormcode());
    }

    private static List<String> assembleTa543Param(FipCutpaydetl cutpaydetl) {
        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();

        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());
        //String interbankAccount = getInterbankAccount(cutpaydetl);
        String interbankAccount = "801011112222333344";
        cutpaydetl.setSbsInterbankActno(interbankAccount);

        //1 交易日期
        txnparamList.add(txndate);

        //2 外围系统流水号
        txnparamList.add(cutpaydetl.getBatchSn() + cutpaydetl.getBatchDetlSn());

        //3 贷款账号
        txnparamList.add(cutpaydetl.getClientact());

        //4 本金金额   S9(13).99
        String amt = df.format(cutpaydetl.getPrincipalamt());
        txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));

        //5 违约金金额（利息/应收息）
        amt = df.format(cutpaydetl.getInterestamt());
        txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));

        //6 滞纳金金额（罚息）
        amt = df.format(cutpaydetl.getPunitiveintamt());
        txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));

        //7 手续费金额（复利）
        amt = "0.00";
        txnparamList.add("+" + StringUtils.leftPad(amt, 16, '0'));

        //8 摘要
        txnparamList.add(StringUtils.leftPad("", 30, ' '));

        //9 本金还款账号  输入还款结算账号或银行同业账号
        txnparamList.add(interbankAccount); //同业账号

        //10 贷款种类 04对私消费贷款 05对私按揭贷款
        if (cutpaydetl.getOriginBizid().equals(BizType.FD.getCode())) {
            txnparamList.add("05");
        }else if (cutpaydetl.getOriginBizid().equals(BizType.XF.getCode())){
            txnparamList.add("04");
        }

        //11 利息还款账号  输入还款结算账号或银行同业账号
        txnparamList.add(interbankAccount); //同业账号

        return txnparamList;
    }

}
