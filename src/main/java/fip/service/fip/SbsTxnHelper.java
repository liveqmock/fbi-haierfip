package fip.service.fip;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanrui on 2015/5/6.
 */
public class SbsTxnHelper {
    //author:zhangxiaobo
    //201504 zr 注: 字段可不做pad处理
    public static List<String> assembleTaa41Param(String sn, String fromAcct, String toAcct, BigDecimal txnAmt, String productCode, String remark) {
        // 转出账户
        String outAct = StringUtils.rightPad(fromAcct, 22, ' ');
        // 转入账户
        String inAct = StringUtils.rightPad(toAcct, 22, ' ');

        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();
        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //转出帐户类型
        txnparamList.add("01");
        //转出帐户
        txnparamList.add(outAct);
        //取款方式
        txnparamList.add("3");
        //转出帐户户名
        txnparamList.add(" ");
        //取款密码
        txnparamList.add(StringUtils.leftPad("", 6, ' '));
        //证件类型
        txnparamList.add("N");

        //外围系统流水号
        txnparamList.add(StringUtils.rightPad(sn, 18, ' '));      //交易流水号

        //支票种类
        txnparamList.add(" ");
        //支票号
        txnparamList.add(StringUtils.leftPad("", 10, ' '));
        //支票密码
        txnparamList.add(StringUtils.leftPad("", 12, ' '));
        //签发日期
        txnparamList.add(StringUtils.leftPad("", 8, ' '));
        //无折标识
        txnparamList.add("3");
        //备用字段
        txnparamList.add(StringUtils.leftPad("", 8, ' '));
        //备用字段
        txnparamList.add(StringUtils.leftPad("", 4, ' '));

        //交易金额
        txnparamList.add(df.format(txnAmt));   //金额

        //转入帐户类型
        txnparamList.add("01");
        //转入帐户 (商户帐号)
        String account = StringUtils.rightPad(inAct, 22, ' ');
        txnparamList.add(account);

        //转入帐户户名
        txnparamList.add(" ");
        //无折标识
        txnparamList.add(" ");
        //交易日期
        txnparamList.add(txndate);

        //摘要
        txnparamList.add(remark == null ? "" : remark);

        //产品码
        txnparamList.add(productCode);
        //MAGFL1
        txnparamList.add(" ");
        //MAGFL2
        txnparamList.add(" ");

        return txnparamList;
    }

}
