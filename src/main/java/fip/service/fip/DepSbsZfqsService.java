package fip.service.fip;

import fip.common.constant.TxSendFlag;
import fip.repository.dao.DepSbsZfqsMapper;
import fip.repository.model.DepSbsZfqs;
import fip.repository.model.DepSbsZfqsExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 13-4-1
 * Time: 下午1:15
 * To change this template use File | Settings | File Templates.
 */
@Service
public class DepSbsZfqsService {

    private static final Logger logger = LoggerFactory.getLogger(DepSbsZfqsService.class);

    @Autowired
    private DepService depService;
    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private DepSbsZfqsMapper depSbsZfqsMapper;

    //  发送清算明细到JDE，返回发送成功记录流水号
    public String sendSbsZfqs(DepSbsZfqs record) throws JMSException {

        // 发送
        String msgid = depService.sendDepMessage("300", record.toFormatString(), "transCreditInfoFromSBStoJDE");
        String rtnmsg = depService.recvDepMessage(msgid);
        // 记录日志
        appendDepJdeJoblog(record.getPkid(), record.getSerialNo());
        return rtnmsg;
    }

    public void appendDepJdeJoblog(String pkid, String serialNo) {
        jobLogService.insertNewJoblog(pkid, "dep_sbs_zfqs", "transCreditInfoFromSBStoJDE", "发送SBS内部总分账户清算数据到JDE,[" + serialNo + "]", "9999", "系统管理员");
    }

    // -----------------------------------------
    // 根据DEP返回的字符串列表，生成明细数据
    public int saveZfqsRecords(String[] originRecords) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int existCnt = 0;
        for (String line : originRecords) {
            DepSbsZfqs record = new DepSbsZfqs();
            record.setSerialNo(line.substring(0, 14));
            // 若数据库已存在该流水号，则视为已获取该笔明细
            if (isExist(record.getSerialNo())) {
                existCnt++;
                continue;
            }

            record.setTxnDate(line.substring(14, 22));
            record.setFenAccountNo(line.substring(22, 40));
            record.setZongAccountNo(line.substring(40, 58));
            record.setTxnAmt(new BigDecimal(line.substring(58, 74)));
            record.setTxnDirection(line.substring(74, 75));
            record.setCreateTime(record.getTxnDate());
            record.setSendFlag(TxSendFlag.UNSEND.getCode());
            addZfqsRecord(record);
        }
        logger.info("【本次共获取记录数】：" + originRecords.length + "，【重复获取数】：" + existCnt);
        return originRecords.length - existCnt;
    }

    public int addZfqsRecord(DepSbsZfqs record) {
        return depSbsZfqsMapper.insert(record);
    }

    public boolean isExist(String serialNo) {
        DepSbsZfqsExample example = new DepSbsZfqsExample();
        example.createCriteria().andSerialNoEqualTo(serialNo);
        return depSbsZfqsMapper.countByExample(example) > 0;
    }

    public List<DepSbsZfqs> qryUnsendRecords() {
        DepSbsZfqsExample example = new DepSbsZfqsExample();
        example.createCriteria().andSendFlagNotEqualTo(TxSendFlag.SENT.getCode());
        return depSbsZfqsMapper.selectByExample(example);
    }

    public List<DepSbsZfqs> qryUnSendRecordsByDate(String startDate, String endDate) {
        DepSbsZfqsExample example = new DepSbsZfqsExample();
        example.createCriteria().andSendFlagNotEqualTo(TxSendFlag.SENT.getCode()).andCreateTimeBetween(startDate, endDate);
        return depSbsZfqsMapper.selectByExample(example);
    }

    @Deprecated
    public int updateToSendStsBySns(String[] snList) {
        int cnt = 0;
        String operTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        for (String sn : snList) {
          /* 1、F  插入失败，记账失败
            2、S  插入成功
            3、D  此号已存在 */
            String[] record = sn.split("\\|");
            if ("S".equalsIgnoreCase(record[1]) || "D".equalsIgnoreCase(record[1])) {
                cnt += (updateToSendStsBySn(record[0], operTime, TxSendFlag.SENT, record[1]) == -1 ? 0 : 1);
            }
        }
        return cnt;
    }

    public int updateToSentSts(String snMsg, String operTime) {
        String[] record = snMsg.split("\\|");
        if ("S".equalsIgnoreCase(record[1]) || "D".equalsIgnoreCase(record[1])) {
            return updateToSendStsBySn(record[0], operTime, TxSendFlag.SENT, record[1]);
        } else {
            return updateToSendStsBySn(record[0], operTime, TxSendFlag.UNSEND, record[1]);
        }
    }

    public int updateToSendStsBySn(String sn, String operTime, TxSendFlag sendFlag, String remark) {

        DepSbsZfqsExample example = new DepSbsZfqsExample();
        example.createCriteria().andSerialNoEqualTo(sn);
        DepSbsZfqs originRecord = depSbsZfqsMapper.selectByExample(example).get(0);
        originRecord.setSendFlag(sendFlag.getCode());
        originRecord.setOperTime(operTime);
        originRecord.setRemark(remark);
        return depSbsZfqsMapper.updateByPrimaryKey(originRecord);
    }
}
