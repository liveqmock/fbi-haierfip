package fip.gateway.newcms.domain.T201002;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import fip.gateway.newcms.domain.common.MsgHeader;

/**
 * 4.3.2.	分期还款申请查询
    业务流程：
    ?	消费信贷系统根据申请单号向信贷系统发起查询交易
    ?	信贷系统根据单号在系统内查询返回该笔申请单号信息。
        1)	如果未找到该笔记录返回"申请单号不存在"
        2)如果已放款返回
	        客户号|客户姓名|证件类型|证件号码|合同号|借据号|放款金额|放款日期|到期日期|贷款金额|贷款余额|贷款形态
        3)如果已拒绝返回”拒绝原因”。
        4)否则，返回状态”审批中”。
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 17:11:06
 * To change this template use File | Settings | File Templates.
 */

@XStreamAlias("ROOT")
public class T201002Request extends MsgHeader {
    private String stdsqdh;
    private String stdsqdzt;

    public String getStdsqdh() {
        return stdsqdh;
    }

    public void setStdsqdh(String stdsqdh) {
        this.stdsqdh = stdsqdh;
    }

    public String getStdsqdzt() {
        return stdsqdzt;
    }

    public void setStdsqdzt(String stdsqdzt) {
        this.stdsqdzt = stdsqdzt;
    }
}
