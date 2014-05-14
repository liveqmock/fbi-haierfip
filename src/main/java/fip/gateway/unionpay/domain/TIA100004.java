package fip.gateway.unionpay.domain;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.io.HierarchicalStreamDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyReplacer;
import com.thoughtworks.xstream.io.xml.XppDriver;
import fip.common.SystemService;
import fip.gateway.unionpay.core.TIA;
import fip.gateway.unionpay.core.TIABody;
import fip.gateway.unionpay.core.TIAHeader;
import fip.repository.model.FipCutpaybat;
import fip.repository.model.FipCutpaydetl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-7-25
 * Time: 上午9:27
 * To change this template use File | Settings | File Templates.
 */
@XStreamAlias("GZELINK")
public class TIA100004 extends TIA {
    public static class Header extends TIAHeader {
    }

    public static class Body extends TIABody {
        public BodyHeader TRANS_SUM = new BodyHeader();
        public List<BodyDetail> TRANS_DETAILS = new ArrayList<BodyDetail>();

        public static class BodyHeader {
            public String BUSINESS_CODE = "";
            public String MERCHANT_ID = "";
            public String SUBMIT_TIME = "";
            public String TOTAL_ITEM = "1";
            public String TOTAL_SUM = "1";
        }

        @XStreamAlias("TRANS_DETAIL")
        public static class BodyDetail {
            public String SN = "001";
            public String BANK_CODE = "";
            public String ACCOUNT_NO = "";
            public String ACCOUNT_NAME = "";
            public String ACCOUNT_PROP = "0";   //0私人，1公司。不填时，默认为私人0。
            public String AMOUNT = "";   //整数，单位分
            public String REMARK = "";
            public String RESERVE1 = "";
            public String RESERVE2 = "";
        }
    }

    public Header INFO = new Header();
    public Body BODY = new Body();

    @Override
    public String toString() {
        XmlFriendlyReplacer replacer = new XmlFriendlyReplacer("$", "_");
        HierarchicalStreamDriver hierarchicalStreamDriver = new XppDriver(replacer);
        XStream xs = new XStream(hierarchicalStreamDriver);
        xs.processAnnotations(TIA100004.class);
        return "<?xml version=\"1.0\" encoding=\"GBK\"?>" + "\n" + xs.toXML(this);
    }

    @Override
    public TIAHeader getHeader() {
        return INFO;
    }

    @Override
    public TIABody getBody() {
        return BODY;
    }

    public TIA100004(Map paramMap){
        assembleTIA(paramMap);
    }
    /**
     * 实时代扣报文  同步  多笔
     * @return
     */
    public  String assembleTIA(Map paramMap) {
        FipCutpaybat cutpaybat = (FipCutpaybat)paramMap.get("batBean");
        List<FipCutpaydetl> cutpaydetlList =  (List<FipCutpaydetl>)paramMap.get("detlList");
        this.INFO.TRX_CODE = "100004";
        this.INFO.LEVEL = "5";

        this.INFO.REQ_SN = cutpaybat.getTxpkgSn();// 批量包号即为交易流水号

        this.BODY.TRANS_SUM.SUBMIT_TIME = SystemService.getDatetime14();
        this.BODY.TRANS_SUM.TOTAL_ITEM = String.valueOf(cutpaydetlList.size());
        BigDecimal totalAmt = new BigDecimal(0);
        // TODO 总金额 /分
        for (FipCutpaydetl record : cutpaydetlList) {
            Body.BodyDetail detail = new Body.BodyDetail();
            detail.SN = record.getTxpkgDetlSn();
            detail.BANK_CODE = record.getBiActopeningbank();
            // TODO 账号类型 银行卡或存折   detail.ACCOUNT_TYPE = record.
            detail.ACCOUNT_NO = record.getBiBankactno();
            detail.ACCOUNT_NAME = record.getBiBankactname();
            //detail.PROVINCE = record.getBiProvince();
            //detail.CITY = record.getBiCity();
            //detail.BANK_NAME = record.getBiActopeningbankUd();
            detail.ACCOUNT_PROP = "0"; // 个人
            detail.AMOUNT = String.valueOf(record.getPaybackamt().multiply(new BigDecimal("100")).longValue());
            totalAmt = totalAmt.add(record.getPaybackamt());
            //detail.ID_TYPE = record.getClientidtype();
            //detail.ID = record.getClientid();
            // TODO 其他非空忽略
            this.BODY.TRANS_DETAILS.add(detail);
        }
        this.BODY.TRANS_SUM.TOTAL_SUM = String.valueOf(totalAmt.multiply(new BigDecimal("100")).longValue());

        return this.toString();
    }

}
