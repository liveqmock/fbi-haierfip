package fip.view.onekeyactchk.wsclient.spc1;

import java.net.URL;

public class SCFDzTest {
    public static void main(String[] args) throws Exception {
        URL wsdlUrl = new URL("http://10.143.20.144:10002/SupplyChainSysPortal/services/SBSSysService?wsdl");
//		URL wsdlUrl = new URL("http://10.143.19.141:8081/SupplyChainSysPortal/services/SBSSysService?wsdl");
        System.out.println(wsdlUrl);
        SBSSysServiceSoapBindingStub service = (SBSSysServiceSoapBindingStub) new SBSSysServiceServiceLocator()
                .getSBSSysService(wsdlUrl);
        System.out.println("service.hashcode:" + service.hashCode());

        ScfDzInfoVO vo = new ScfDzInfoVO();
        vo.setTxnCode("1001");
        vo.setVersion("01");
        vo.setReqSn("20141128041611001");
        vo.setTxnDate("20141128");
        vo.setTxnTime("121212");
        vo.setAction("1");
        vo.setChnCode("SPC1");

        ScfDzInfoVO respVo = service.acceptB2BDzInfo(vo);
        System.out.println(respVo.getRtnCode());
        System.out.println(respVo.getRtnMsg());
        System.out.println(respVo);

//---
        vo.setTxnCode("1002");
        vo.setChannel("SPC1");
        ScfDzInfoVO respVo2 = service.getScfDzResult(vo);
        System.out.println(respVo2.getRtnCode());
        System.out.println(respVo2.getRtnMsg());


    }
}
