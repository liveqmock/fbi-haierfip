package fip.service.fip;

import fip.repository.dao.PtenudetailMapper;
import fip.repository.model.Ptenudetail;
import fip.repository.model.PtenudetailExample;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanrui on 2015/5/6.
 */

@Service
public class SbsTxnHelper {
    @Autowired
    private PtenudetailMapper ptenudetailMapper;

    //ת�� author:zhangxiaobo
    //201504 zr ע: �ֶοɲ���pad����
    public  List<String> assembleTaa41Param(String sn, String fromAcct, String toAcct, BigDecimal txnAmt, String productCode, String remark) {
        // ת���˻�
        String outAct = StringUtils.rightPad(fromAcct, 22, ' ');
        // ת���˻�
        String inAct = StringUtils.rightPad(toAcct, 22, ' ');

        DecimalFormat df = new DecimalFormat("#############0.00");
        List<String> txnparamList = new ArrayList<String>();
        String txndate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        //ת���ʻ�����
        txnparamList.add("01");
        //ת���ʻ�
        txnparamList.add(outAct);
        //ȡ�ʽ
        txnparamList.add("3");
        //ת���ʻ�����
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
        txnparamList.add(remark == null ? "" : remark);

        //��Ʒ��
        txnparamList.add(productCode);
        //MAGFL1
        txnparamList.add(" ");
        //MAGFL2
        txnparamList.add(" ");

        return txnparamList;
    }

    public String selectSbsActnoFromPtEnuDetail(String type) {
        PtenudetailExample example = new PtenudetailExample();
        example.createCriteria()
                .andEnutypeEqualTo("ActnoForSBS").andEnuitemvalueEqualTo(type);
        example.setOrderByClause(" dispno ");
        List<Ptenudetail> ptenudetails = ptenudetailMapper.selectByExample(example);
        if (ptenudetails.size() != 1) {
            throw new RuntimeException("ö���ֵ����ô���");
        }
        return ptenudetails.get(0).getEnuitemexpand();
    }

}