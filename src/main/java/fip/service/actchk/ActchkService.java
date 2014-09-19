package fip.service.actchk;

import fip.repository.dao.ChkZongfenTxnMapper;
import fip.repository.dao.actchk.ActchkCmnMapper;
import fip.repository.model.ChkZongfenTxnExample;
import fip.repository.model.actchk.ActchkVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-8-7
 * Time: ����4:18
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ActchkService {
    @Resource
    private ChkZongfenTxnMapper chkZongfenTxnMapper;
    @Resource
    private ActchkCmnMapper actchkCmnMapper;

    //������ͳ����ˮ���ʱ�����
    public int countActchkRecord(String txnDate, String bankCode){
        ChkZongfenTxnExample example = new ChkZongfenTxnExample();

        List<String> paras = new ArrayList<String>();
        paras.add(bankCode);
        paras.add("SBS_"+bankCode);

        example.createCriteria().andTxnDateEqualTo(txnDate).andSendSysIdIn(paras);
        return  chkZongfenTxnMapper.countByExample(example);
    }

    //��ˮ���ʽ����ѯ
    public List<ActchkVO> selectChkTxnFailResult(String sendSysId1, String sendSysId2, String txnDate){
        return  actchkCmnMapper.selectChkTxnFailResult(sendSysId1,sendSysId2, txnDate);
    }
    public List<ActchkVO> selectChkTxnSuccResult(String sendSysId1, String sendSysId2, String txnDate){
        return  actchkCmnMapper.selectChkTxnSuccResult(sendSysId1,sendSysId2, txnDate);
    }

}
