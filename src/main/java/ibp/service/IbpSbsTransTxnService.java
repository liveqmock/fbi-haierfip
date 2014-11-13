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

import java.io.UnsupportedEncodingException;
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

    // ִ��SBS���� ����Form��
    public String executeSBSTxn(IbpSbsTranstxn txn, String remark) {
        List<String> paramList = assembleTaa41Param(txn.getSerialno(), txn.getOutAct(), txn.getInAct(), txn.getTxnamt(), remark);
        logger.info(paramList.toString());
        byte[] sbsResBytes = DepCtgManager.processSingleResponsePkg("aa41", paramList);
        return new String(sbsResBytes, 21, 4);
    }

    private static List<String> assembleTaa41Param(String sn, String fromAcct, String toAcct, BigDecimal txnAmt, String remark) {

        // ת���˻�
        String outAct = StringUtils.rightPad(fromAcct, 22, ' ');
        // ת���˻�
        String inAct = StringUtils.rightPad(toAcct, 22, ' ');

        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();
        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());
//        String txndate = "20141106";

        //ת���ʻ�����
        txnparamList.add("01");
        //ת���ʻ�
        txnparamList.add(outAct);
        //ȡ�ʽ
        txnparamList.add("3");
        //ת���ʻ�����
//        txnparamList.add(StringUtils.leftPad("", 72, ' '));
        txnparamList.add(" ");
        //ȡ������
        txnparamList.add(StringUtils.leftPad("", 6, ' '));
        //֤������
        txnparamList.add("N");
        //��Χϵͳ��ˮ��
        txnparamList.add(StringUtils.rightPad(sn, 18, ' '));      //������ˮ��
        //֧Ʊ����
        txnparamList.add(" ");
        //֧Ʊ��
        txnparamList.add(StringUtils.leftPad("", 10, ' '));
        //֧Ʊ����
        txnparamList.add(StringUtils.leftPad("", 12, ' '));
        //ǩ������
        txnparamList.add(StringUtils.leftPad("", 8, ' '));
        //���۱�ʶ
        txnparamList.add("3");
        //�����ֶ�
        txnparamList.add(StringUtils.leftPad("", 8, ' '));
        //�����ֶ�
        txnparamList.add(StringUtils.leftPad("", 4, ' '));

        //���׽��
        txnparamList.add(df.format(txnAmt));   //���

        //ת���ʻ�����
        txnparamList.add("01");
        //ת���ʻ� (�̻��ʺ�)
        String account = StringUtils.rightPad(inAct, 22, ' ');
        txnparamList.add(account);

        //ת���ʻ�����
        txnparamList.add(" ");
        //���۱�ʶ
        txnparamList.add(" ");
        //��������
        txnparamList.add(txndate);
        //ժҪ
        txnparamList.add(remark == null ? "" : rightPad4ChineseToByteLength(remark, 25, " "));
        //��Ʒ��
        txnparamList.add("N101");
        //MAGFL1
        txnparamList.add(" ");
        //MAGFL2
        txnparamList.add(" ");

        return txnparamList;
    }

    public static String rightPad4ChineseToByteLength(String srcStr, int totalByteLength, String padStr) {
        if (srcStr == null) {
            return null;
        }
        int srcByteLength = srcStr.getBytes().length;

        if (padStr == null || "".equals(padStr)) {
            padStr = " ";
        } else if (padStr.getBytes().length > 1 || totalByteLength <= 0) {
            throw new RuntimeException("��������");
        }
        StringBuilder rtnStrBuilder = new StringBuilder();
        if (totalByteLength >= srcByteLength) {
            rtnStrBuilder.append(srcStr);
            for (int i = 0; i < totalByteLength - srcByteLength; i++) {
                rtnStrBuilder.append(padStr);
            }
        } else {
            byte[] rtnBytes = new byte[totalByteLength];
            try {
                System.arraycopy(srcStr.getBytes("GBK"), 0, rtnBytes, 0, totalByteLength);
                rtnStrBuilder.append(new String(rtnBytes, "GBK"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return rtnStrBuilder.toString();
    }

}
