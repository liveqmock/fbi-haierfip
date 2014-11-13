package ibp.processor;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.io.xml.DomDriver;
import ibp.repository.dao.IbpIfCcbTxnMapper;
import ibp.repository.model.IbpIfCcbTxn;
import ibp.repository.model.IbpIfCcbTxnExample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by zhanrui on 2014/11/13.
 */
@WebServlet(name = "CcbVip4879", urlPatterns = "/CcbVip4879")
public class CcbVip4879Processor extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CcbVip4879Processor.class);

    @Autowired
    private IbpIfCcbTxnMapper txnMapper;

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding("GBK");
        response.setContentType("text/html;charset=GBK");
        PrintWriter out = response.getWriter();

        String reqXml = null;
        try {
            reqXml = getRequestXmlMsg(request);
            logger.info(">>>>XML:" + reqXml);

            XStream xs = new XStream(new DomDriver());
            xs.processAnnotations(T4879Bean.class);
            T4879Bean tia = (T4879Bean) xs.fromXML(reqXml);
            logger.info(">>>>TIA:" + tia);

            //check
            if (!"37101985510051003497".equals(tia.Body.AcctId)) {
                out.println("[1001]acctno != 37101985510051003497");
                return;
            }

            if (isDuplicateMsg(tia)) {
                logger.info(">>>>duplicate msg:" + tia);
                out.println("[1002]duplicate msg");
                return;
            }

            processRecord(tia);
            out.println("[0000] successed.");
        } catch (IOException e) {
            out.println("error" + e.getMessage());
        } finally {
            out.flush();
            out.close();
        }
    }

    private String getRequestXmlMsg(HttpServletRequest request) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream(), "GBK"));
        StringBuilder sb = new StringBuilder();
        String tmp = "";
        while (true) {
            tmp = br.readLine();
            if (tmp == null)
                break;
            else
                sb.append(tmp);
        }
        return sb.toString();
    }

    //
    private boolean isDuplicateMsg(T4879Bean tia) {
        String txnDate = tia.Head.TxDate;
        String txnSn = tia.Head.TxSeqId;
        IbpIfCcbTxnExample example = new IbpIfCcbTxnExample();
        example.createCriteria().andMsgtxdateEqualTo(tia.Head.TxDate).andTxseqidEqualTo(tia.Head.TxSeqId);
        List<IbpIfCcbTxn> txnList = txnMapper.selectByExample(example);
        if (txnList.size() == 0) {
            return false;
        } else {
            return true;
        }

    }

    private void processRecord(T4879Bean tia) {
        IbpIfCcbTxnExample example = new IbpIfCcbTxnExample();
        for (T4879Bean.Body.BodyRecord record : tia.Body.Records) {
            example.clear();
            example.createCriteria().andAccthostseqidEqualTo(record.AcctHostSeqId);
            List<IbpIfCcbTxn> txnList = txnMapper.selectByExample(example);
            if (txnList.size() == 0) {
                IbpIfCcbTxn txn = new IbpIfCcbTxn();
                txn.setOutacctid(record.OutAcctId);
                txn.setInacctid(record.InAcctId);
                txn.setOutacctname(record.OutAcctName);
                txn.setInacctname(record.InAcctName);
                txn.setAbstractstr(record.AbstractStr);
                txn.setVoucherid(record.VoucherId);
                txn.setVouchertype(record.VoucherType);
                txn.setOutbranchname(record.OutBranchName);
                txn.setInbranchname(record.InBranchName);
                txn.setCurcode(record.CurCode);
                txn.setDcflag(record.DCFlag);
                txn.setAbstractCode(record.Abstract);
                txn.setTxdate(record.TxDate);
                txn.setTxtime(record.TxTime);

                txn.setAccthostseqid(record.AcctHostSeqId);

                txn.setCoseqid(record.CoSeqId);
                txn.setBanknodeid(record.BankNodeId);
                txn.setCcbstellerid(record.CCBSTellerId);
                txn.setOutcomacctid(record.OutComAcctId);
                txn.setOutcomname(record.OutComName);
                txn.setReserve1(record.Reserve1);
                txn.setReserve2(record.Reserve2);
                txn.setBankindex(record.BankIndex);
                txn.setAcctbal(new BigDecimal(record.AcctBal));
                txn.setAvbal(new BigDecimal(record.AvBal));
                txn.setCacctbal(new BigDecimal(record.CAcctBal));
                txn.setTxamount(new BigDecimal(record.TxAmount));
                txn.setCtxamount(new BigDecimal(record.CTxAmount));


                txn.setAcctid(tia.Body.AcctId);
                txn.setOperatoruserid(tia.Body.OperatorUserId);
                txn.setReccount(tia.Body.RecCount);

                txn.setCreatetime(new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()));
                txn.setOperid("MBP");

                txn.setBusinessType("CCB_MBP_0001");
                txn.setTxcode(tia.Head.TxCode);
                txn.setBookflag("00");
                txn.setRecversion(0);
                txn.setTxseqid(tia.Head.TxSeqId);
                txn.setMsgtxdate(tia.Head.TxDate);

                txnMapper.insert(txn);
            } else {
                logger.error("建行订单融资到账入账记录重复:" + tia);
            }
        }
    }

    //=======================================
    @XStreamAlias("Root")
    public static class T4879Bean {
        public Head Head = new Head();
        public Body Body = new Body();

        public static class Head {
            public String Version;
            public String TxCode;
            public String FuncCode;
            public String Channel;
            public String SubCenterId;
            public String UserId;
            public String TellerId;
            public String TxSeqId;
            public String TxDate;
            public String TxTime;
            public String NodeId;

            @Override
            public String toString() {
                return "Head{" +
                        "Version='" + Version + '\'' +
                        ", TxCode='" + TxCode + '\'' +
                        ", FuncCode='" + FuncCode + '\'' +
                        ", Channel='" + Channel + '\'' +
                        ", SubCenterId='" + SubCenterId + '\'' +
                        ", UserId='" + UserId + '\'' +
                        ", TellerId='" + TellerId + '\'' +
                        ", TxSeqId='" + TxSeqId + '\'' +
                        ", TxDate='" + TxDate + '\'' +
                        ", TxTime='" + TxTime + '\'' +
                        ", NodeId='" + NodeId + '\'' +
                        '}';
            }
        }

        @XStreamAlias("Body")
        public static class Body {
            @XStreamImplicit(itemFieldName = "Record")
            public List<BodyRecord> Records = new ArrayList<BodyRecord>();
            public String AcctId;
            public String OperatorUserId;
            public String RecCount;

            public static class BodyRecord {
                public String OutAcctId;
                public String InAcctId;
                public String OutAcctName;
                public String InAcctName;
                public String AbstractStr;
                public String VoucherId;
                public String VoucherType;
                public String OutBranchName;
                public String InBranchName;
                public String CurCode;
                public String DCFlag;
                public String Abstract;
                public String TxTime;
                public String AcctHostSeqId;
                public String CoSeqId;
                public String BankNodeId;
                public String CCBSTellerId;
                public String OutComAcctId;
                public String OutComName;
                public String Reserve1;
                public String Reserve2;
                public String BankIndex;
                public String TxDate;
                public String AcctBal;
                public String AvBal;
                public String CAcctBal;
                public String TxAmount;
                public String CTxAmount;

                @Override
                public String toString() {
                    return "BodyRecord{" +
                            "OutAcctId='" + OutAcctId + '\'' +
                            ", InAcctId='" + InAcctId + '\'' +
                            ", OutAcctName='" + OutAcctName + '\'' +
                            ", InAcctName='" + InAcctName + '\'' +
                            ", AbstractStr='" + AbstractStr + '\'' +
                            ", VoucherId='" + VoucherId + '\'' +
                            ", VoucherType='" + VoucherType + '\'' +
                            ", OutBranchName='" + OutBranchName + '\'' +
                            ", InBranchName='" + InBranchName + '\'' +
                            ", CurCode='" + CurCode + '\'' +
                            ", DCFlag='" + DCFlag + '\'' +
                            ", Abstract='" + Abstract + '\'' +
                            ", TxTime='" + TxTime + '\'' +
                            ", AcctHostSeqId='" + AcctHostSeqId + '\'' +
                            ", CoSeqId='" + CoSeqId + '\'' +
                            ", BankNodeId='" + BankNodeId + '\'' +
                            ", CCBSTellerId='" + CCBSTellerId + '\'' +
                            ", OutComAcctId='" + OutComAcctId + '\'' +
                            ", OutComName='" + OutComName + '\'' +
                            ", Reserve1='" + Reserve1 + '\'' +
                            ", Reserve2='" + Reserve2 + '\'' +
                            ", BankIndex='" + BankIndex + '\'' +
                            ", TxDate='" + TxDate + '\'' +
                            ", AcctBal='" + AcctBal + '\'' +
                            ", AvBal='" + AvBal + '\'' +
                            ", CAcctBal='" + CAcctBal + '\'' +
                            ", TxAmount='" + TxAmount + '\'' +
                            ", CTxAmount='" + CTxAmount + '\'' +
                            '}';
                }
            }
        }

        @Override
        public String toString() {
            return "T4879Bean{" +
                    "Head=" + Head +
                    ", Body=" + Body +
                    '}';
        }
    }

}
