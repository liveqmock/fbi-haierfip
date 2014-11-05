package ibp.service;

import fip.gateway.sbs.DepCtgManager;
import ibp.repository.dao.IbpSbsTranstxnMapper;
import ibp.repository.model.IbpSbsTranstxn;
import ibp.repository.model.IbpSbsTranstxnExample;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by lenovo on 2014-11-03.
 */
@Service
public class IbpSbsTransTxnService {
    private static final Logger logger = LoggerFactory.getLogger(IbpSbsTransTxnService.class);
    @Autowired
    private IbpSbsTranstxnMapper ibpSbsTranstxnMapper;

    public int insertTxn(IbpSbsTranstxn txn) {
        txn.setPkid(UUID.randomUUID().toString());
        return ibpSbsTranstxnMapper.insert(txn);
    }

    public List<IbpSbsTranstxn> qryTodayTrans() {
        IbpSbsTranstxnExample example = new IbpSbsTranstxnExample();
        example.createCriteria().andTxntimeLike(new SimpleDateFormat("yyyyMMdd").format(new Date()) + "%");
        return ibpSbsTranstxnMapper.selectByExample(example);
    }

    // 执行SBS交易 返回Form号
    public String executeSBSTxn(IbpSbsTranstxn txn) {
        List<String> paramList = assembleTaa41Param(txn.getSerialno(), txn.getOutAct(), txn.getInAct(), txn.getTxnamt(), txn.getOperid());
        byte[] sbsResBytes = DepCtgManager.processSingleResponsePkg("aa41", paramList);
        return new String(sbsResBytes, 21, 4);
    }

    private static List<String> assembleTaa41Param(String sn, String fromAcct, String toAcct, BigDecimal txnAmt, String remark) {

        // 转出账户
        String outAct = StringUtils.rightPad(fromAcct, 22, ' ');
        // 转入账户
        String inAct = StringUtils.rightPad(toAcct, 22, ' ');

        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();
        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        String txndate = "20141103";

        //转出帐户类型
        txnparamList.add("01");
        //转出帐户
        txnparamList.add(outAct);
        //取款方式
        txnparamList.add("3");
        //转出帐户户名
//        txnparamList.add(StringUtils.leftPad("", 72, ' '));
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
        txnparamList.add(StringUtils.leftPad("", 4, ' '));
        //MAGFL1
        txnparamList.add(" ");
        //MAGFL2
        txnparamList.add(" ");

        return txnparamList;
    }

}
