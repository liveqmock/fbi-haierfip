<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="javax.sql.rowset.CachedRowSet" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="pub.cenum.EnumValue" %>
<%@ page import="pub.platform.db.ConnectionManager" %>
<%@ page import="hfc.xf.XFConf" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="pub.platform.security.OperatorManager" %>
<%@ page import="fip.common.SystemService" %>
<%@ page import="pub.platform.form.config.SystemAttributeNames" %>
<%@ page import="com.ccb.util.SeqUtil" %>
<%@ page import="pub.platform.advance.utils.PropertyManager" %>

<%--
===============================================
Title: 消费信贷-个人消费分期付款申请书提交
Description: 消费信贷-个人消费分期付款申请书提交。
 * @version  $Revision: 1.0 $  $Date: 2009/03/11 06:33:37 $
 * @author
 * <p/>修改：$Author: liuj $
===============================================
--%>
<SCRIPT language=JavaScript>
    <!--

    self.moveTo((screen.width - 450) / 2, (screen.height - 400) / 2);
    self.resizeTo(450, 420);

    this.onunload = function() {
        window.opener.location.reload();
    }
    //-->
</SCRIPT>
<%
    request.setCharacterEncoding("GBK");
    String CLIENTNO = request.getParameter("CLIENTNO");   //客户号
    String RECVERSION = request.getParameter("RECVERSION");
    String COMMISSIONRATE = request.getParameter("COMMISSIONRATE");   //客户手续费率
    String SHCOMMISSIONRATE = request.getParameter("SHCOMMISSIONRATE");//商户手续费率
    String COMPAYDATE = request.getParameter("COMPAYDATE");
    String SHARETYPE = request.getParameter("SHARETYPE");
    String COMMISSIONTYPE = request.getParameter("COMMISSIONTYPE");
    /*SERVFROMMONTH,LIVEFROMMONTH  PSERVFROMMONTH  PLIVEFROMMONTH*/
    String SERVFROMMONTH = request.getParameter("SERVFROMMONTH");        //现居月
    String LIVEFROMMONTH = request.getParameter("LIVEFROMMONTH");        //现
    String PSERVFROMMONTH = request.getParameter("PSERVFROMMONTH");
    String PLIVEFROMMONTH = request.getParameter("PLIVEFROMMONTH");
    SERVFROMMONTH = (SERVFROMMONTH == null || StringUtils.isEmpty(SERVFROMMONTH)) ? "0" : SERVFROMMONTH;
    LIVEFROMMONTH = (LIVEFROMMONTH == null || StringUtils.isEmpty(LIVEFROMMONTH)) ? "0" : LIVEFROMMONTH;
    PSERVFROMMONTH = (PSERVFROMMONTH == null || StringUtils.isEmpty(PSERVFROMMONTH)) ? "0" : PSERVFROMMONTH;
    PLIVEFROMMONTH = (PLIVEFROMMONTH == null || StringUtils.isEmpty(PLIVEFROMMONTH)) ? "0" : PLIVEFROMMONTH;
    String SOCIALSECURITYID = request.getParameter("SOCIALSECURITYID");
    String ANNUALINCOME = request.getParameter("ANNUALINCOME");
    String ETPSCOPTYPE = request.getParameter("ETPSCOPTYPE");
    String SALER = request.getParameter("SALER");
    String APPSALARYRATE = request.getParameter("APPSALARYRATE");
    String MONTHPAYSLRRATE = request.getParameter("MONTHPAYSLRRATE");
    String SLRYEVETYPE = request.getParameter("SLRYEVETYPE");
    //信用信息
    String CCRPTOTALAMT = request.getParameter("CCRPTOTALAMT");
    String CCVALIDPERIOD = request.getParameter("CCVALIDPERIOD");
    String CCDYNUM = request.getParameter("CCDYNUM");
    String CCXYNUM = request.getParameter("CCXYNUM");
    String CCCDNUM = request.getParameter("CCCDNUM");
    String CCDYAMT = request.getParameter("CCDYAMT");
    String CCXYAMT = request.getParameter("CCXYAMT");
    String CCCDAMT = request.getParameter("CCCDAMT");
    String CCDYNOWBAL = request.getParameter("CCDYNOWBAL");
    String CCXYNOWBAL = request.getParameter("CCXYNOWBAL");
    String CCCDNOWBAL = request.getParameter("CCCDNOWBAL");
    String CCDYRPMON = request.getParameter("CCDYRPMON");
    String CCXYRPMON = request.getParameter("CCXYRPMON");
    String CCCDRPMON = request.getParameter("CCCDRPMON");
    String CCDYYEARRPTIME = request.getParameter("CCDYYEARRPTIME");
    String CCXYYEARRPTIME = request.getParameter("CCXYYEARRPTIME");
    String CCCDYEARRPTIME = request.getParameter("CCCDYEARRPTIME");
    String CCDYNOOVERDUE = request.getParameter("CCDYNOOVERDUE");
    String CCXYNOOVERDUE = request.getParameter("CCXYNOOVERDUE");
    String CCCDNOOVERDUE = request.getParameter("CCCDNOOVERDUE");
    String CCDY1TIMEOVERDUE = request.getParameter("CCDY1TIMEOVERDUE");
    String CCXY1TIMEOVERDUE = request.getParameter("CCXY1TIMEOVERDUE");
    String CCCD1TIMEOVERDUE = request.getParameter("CCCD1TIMEOVERDUE");
    String CCDY2TIMEOVERDUE = request.getParameter("CCDY2TIMEOVERDUE");
    String CCXY2TIMEOVERDUE = request.getParameter("CCXY2TIMEOVERDUE");
    String CCCD2TIMEOVERDUE = request.getParameter("CCCD2TIMEOVERDUE");
    String CCDYM3TIMEOVERDUE = request.getParameter("CCDYM3TIMEOVERDUE");
    String CCXY3TIMEOVERDUE = request.getParameter("CCXY3TIMEOVERDUE");
    String CCCD3TIMEOVERDUE = request.getParameter("CCCD3TIMEOVERDUE");
    String ACAGE = request.getParameter("ACAGE");
    String ACWAGE = request.getParameter("ACWAGE");
    String ACZX1 = request.getParameter("ACZX1");
    String ACZX2 = request.getParameter("ACZX2");
    String ACZX3 = request.getParameter("ACZX3");
    String ACFACILITY = request.getParameter("ACFACILITY");
    String ACRATE = request.getParameter("ACRATE");
    String CCDIVIDAMT = request.getParameter("CCDIVIDAMT");

    String APPACTFLAG = request.getParameter("APPACTFLAG");           //申请单执行状态   执行动作标志：1、正常，2、退回，3、作废
    String APPNO = request.getParameter("APPNO");                      //申请单编号
    String XFAPPNO = request.getParameter("XFAPPNO");                  //新申请单编号
    String APPDATE = request.getParameter("APPDATE");                  //申请日期
    String IDTYPE = request.getParameter("IDTYPE");                    //证件名称
    String ID = request.getParameter("ID");                             //证件号码
    String APPTYPE = "";                                               //申请类型 废除
    String APPSTATUS = request.getParameter("APPSTATUS");              //申请状态
    String BIRTHDAY = request.getParameter("BIRTHDAY");                //出生日期
    String GENDER = request.getParameter("GENDER");                    //性别 enum=Gender
    String NATIONALITY = request.getParameter("NATIONALITY");         //国籍
    String MARRIAGESTATUS = request.getParameter("MARRIAGESTATUS");   //婚姻状况 enum=MarriageStatus
    String HUKOUADDRESS = request.getParameter("HUKOUADDRESS");       //户籍所在地
    String CURRENTADDRESS = request.getParameter("CURRENTADDRESS");   //现住址
    String COMPANY = request.getParameter("COMPANY");                  //工作单位
    String TITLE = request.getParameter("TITLE");                      //职务 enum=Title
    String QUALIFICATION = request.getParameter("QUALIFICATION");     //职称 enum=Qualification
    String EDULEVEL = request.getParameter("EDULEVEL");                //学历 enum=EduLevel
    String PHONE1 = request.getParameter("PHONE1");                   //移动电话
    String PHONE2 = request.getParameter("PHONE2");                   //家庭电话
    String PHONE3 = request.getParameter("PHONE3");                   //办公电话
    String NAME = request.getParameter("NAME");                       //客户名称 desc=企业(个人)名称
    String CLIENTTYPE = request.getParameter("CLIENTTYPE");           //客户性质 enum=ClientType1
    String DEGREETYPE = request.getParameter("DEGREETYPE");          //最高学位 enum=DegreeType
    String COMADDR = request.getParameter("COMADDR");                 //单位地址
    String SERVFROM = request.getParameter("SERVFROM");               //现单位工作时间
    String RESIDENCEADR = request.getParameter("RESIDENCEADR");       //户籍所在地(本外地) enum=ResidenceADR
    String HOUSINGSTS = request.getParameter("HOUSINGSTS");           //居住状况 enum=HousingSts
    String HEALTHSTATUS = request.getParameter("HEALTHSTATUS");       //健康状况 enum=HealthStatus
    String MONTHLYPAY = request.getParameter("MONTHLYPAY");           //个人月收入
    String BURDENSTATUS = request.getParameter("BURDENSTATUS");       //负担状况 enum=BurdenStatus
    String EMPNO = request.getParameter("EMPNO");                      //员工卡号码
    String SOCIALSECURITY_G[] = request.getParameterValues("SOCIALSECURITY");   //社会保障 enum=SocialSecurity
    String LIVEFROM = request.getParameter("LIVEFROM");                //本地居住时间
    String PC = request.getParameter("PC");                             //住宅邮编
    String COMPC = request.getParameter("COMPC");                      //单位邮编
    String RESDPC = request.getParameter("RESDPC");                    //寄送地址邮编
    String RESDADDR = request.getParameter("RESDADDR");                //寄送地址
    String EMAIL = request.getParameter("EMAIL");                      //电子邮件

    String PNAME = request.getParameter("PNAME");                      //配偶名称
    String PIDTYPE = request.getParameter("PIDTYPE");                  //配偶证件名称
    String PID = request.getParameter("PID");                          //配偶证件号码
    String PCOMPANY = request.getParameter("PCOMPANY");                //配偶工作单位
    String PTITLE = request.getParameter("PTITLE");                    //配偶职务 enum=Title
    String PPHONE1 = request.getParameter("PPHONE1");                  //配偶移动电话
    String PPHONE3 = request.getParameter("PPHONE3");                  //配偶办公电话
    String PCLIENTTYPE = request.getParameter("PCLIENTTYPE");         //配偶客户性质(单位性质) enum=ClientType1
    String PSERVFROM = request.getParameter("PSERVFROM");             //配偶现单位工作时间
    String PMONTHLYPAY = request.getParameter("PMONTHLYPAY");         //配偶个人月收入
    String PLIVEFROM = request.getParameter("PLIVEFROM");             //配偶本地居住时间

    String CHANNEL = request.getParameter("CHANNEL");                  //销售单位(渠道名称)
//    String COMMNAME = request.getParameter("COMMNAME");                //商品名称
//    String COMMTYPE = request.getParameter("COMMTYPE");                //商品型号
    String ADDR = request.getParameter("ADDR");                         //配送地址
    String TOTALNUM = request.getParameter("TOTALNUM");                           //购买数量
    String TOTALAMT = request.getParameter("TOTALAMT");                           //总金额
    String RECEIVEAMT = request.getParameter("RECEIVEAMT");            //已付金额
    String APPAMT = request.getParameter("APPAMT");                     //分期金额
    String DIVID = request.getParameter("DIVID");                       //分期期数
    String PREPAY_CUTPAY_TYPE = request.getParameter("PREPAY_CUTPAY_TYPE");         //首付款是否代收

    String ACTOPENINGBANK = request.getParameter("ACTOPENINGBANK");   //开户行 enum=Bank
    String BANKACTNO = request.getParameter("BANKACTNO");              //还款帐号
    String PROVINCE = request.getParameter("PROVINCE");                //银行所在省份
    String CITY = request.getParameter("CITY");                        //银行所在城市
    String XY = request.getParameter("XY");                             //信用 enum=YesNo
    String XYR = request.getParameter("XYR");                           //信用人名称
    String DY = request.getParameter("DY");                             //抵押 enum=YesNo
    String DYW = request.getParameter("DYW");                           //抵押物名称
    String ZY = request.getParameter("ZY");                             //质押 enum=YesNo
    String ZYW = request.getParameter("ZYW");                           //质押物名称
    String BZ = request.getParameter("BZ");                             //保证 enum=YesNo
    String BZR = request.getParameter("BZR");                           //保证人名称
    String CREDITTYPE_G[] = request.getParameterValues("CREDITTYPE");  //信用种类 enum=CreditType
    String MONPAYAMT = request.getParameter("MONPAYAMT");              //月均还款额
    String LINKMAN = request.getParameter("LINKMAN");                   //联系人姓名
    String LINKMANGENDER = request.getParameter("LINKMANGENDER");      //联系人性别
    String LINKMANPHONE1 = request.getParameter("LINKMANPHONE1");       //联系人移动电话
    String LINKMANPHONE2 = request.getParameter("LINKMANPHONE2");       //联系人固定电话
    String APPRELATION = request.getParameter("APPRELATION");           //与申请人关系 enum=AppRelation
    String LINKMANADD = request.getParameter("LINKMANADD");             //联系人住址
    String LINKMANCOMPANY = request.getParameter("LINKMANCOMPANY");    //联系人工作单位
    String ACTOPENINGBANK_UD = request.getParameter("ACTOPENINGBANK_UD");    //开户行名称（电汇、网银——录入名称）
    String BANKACTNAME = request.getParameter("BANKACTNAME");          //户名
    String UNIONCHANNEL = request.getParameter("UNIONCHANNEL");        //是或否通过银联  01=通过银联 00=不通过
    String SID = request.getParameter("SID");                           //合作商城编号
    String ORDERNO = request.getParameter("ORDERNO");                   //合作商城单号
    String REQUESTTIME = request.getParameter("REQUESTTIME");          //合作商城定单生成时间

    //征信信息
    CCRPTOTALAMT = (CCRPTOTALAMT == null || StringUtils.isEmpty( CCRPTOTALAMT.trim())) ? "0" : CCRPTOTALAMT.trim();
    CCDYNUM = (CCDYNUM == null || StringUtils.isEmpty( CCDYNUM.trim())) ? "0" : CCDYNUM.trim();
    CCXYNUM = (CCXYNUM == null || StringUtils.isEmpty( CCXYNUM.trim())) ? "0" : CCXYNUM.trim();
    CCCDNUM = (CCCDNUM == null || StringUtils.isEmpty( CCCDNUM.trim())) ? "0" : CCCDNUM.trim();
    CCDYAMT = (CCDYAMT == null || StringUtils.isEmpty( CCDYAMT.trim())) ? "0" : CCDYAMT.trim();
    CCXYAMT = (CCXYAMT == null || StringUtils.isEmpty( CCXYAMT.trim())) ? "0" : CCXYAMT.trim();
    CCCDAMT = (CCCDAMT == null || StringUtils.isEmpty( CCCDAMT.trim())) ? "0" : CCCDAMT.trim();
    CCDYNOWBAL = (CCDYNOWBAL == null || StringUtils.isEmpty( CCDYNOWBAL.trim())) ? "0" : CCDYNOWBAL.trim();
    CCXYNOWBAL = (CCXYNOWBAL == null || StringUtils.isEmpty( CCXYNOWBAL.trim())) ? "0" : CCXYNOWBAL.trim();
    CCCDNOWBAL = (CCCDNOWBAL == null || StringUtils.isEmpty( CCCDNOWBAL.trim())) ? "0" : CCCDNOWBAL.trim();
    CCDYRPMON = (CCDYRPMON == null || StringUtils.isEmpty( CCDYRPMON.trim())) ? "0" : CCDYRPMON.trim();
    CCXYRPMON = (CCXYRPMON == null || StringUtils.isEmpty( CCXYRPMON.trim())) ? "0" : CCXYRPMON.trim();
    CCCDRPMON = (CCCDRPMON == null || StringUtils.isEmpty( CCCDRPMON.trim())) ? "0" : CCCDRPMON.trim();
    CCDYYEARRPTIME = (CCDYYEARRPTIME == null || StringUtils.isEmpty( CCDYYEARRPTIME.trim())) ? "0" : CCDYYEARRPTIME.trim();
    CCXYYEARRPTIME = (CCXYYEARRPTIME == null || StringUtils.isEmpty( CCXYYEARRPTIME.trim())) ? "0" : CCXYYEARRPTIME.trim();
    CCCDYEARRPTIME = (CCCDYEARRPTIME == null || StringUtils.isEmpty( CCCDYEARRPTIME.trim())) ? "0" : CCCDYEARRPTIME.trim();
    CCDY1TIMEOVERDUE = (CCDY1TIMEOVERDUE == null || StringUtils.isEmpty( CCDY1TIMEOVERDUE.trim())) ? "0" : CCDY1TIMEOVERDUE.trim();
    CCXY1TIMEOVERDUE = (CCXY1TIMEOVERDUE == null || StringUtils.isEmpty( CCXY1TIMEOVERDUE.trim())) ? "0" : CCXY1TIMEOVERDUE.trim();
    CCCD1TIMEOVERDUE = (CCCD1TIMEOVERDUE == null || StringUtils.isEmpty( CCCD1TIMEOVERDUE.trim())) ? "0" : CCCD1TIMEOVERDUE.trim();
    CCDY2TIMEOVERDUE = (CCDY2TIMEOVERDUE == null || StringUtils.isEmpty( CCDY2TIMEOVERDUE.trim())) ? "0" : CCDY2TIMEOVERDUE.trim();
    CCXY2TIMEOVERDUE = (CCXY2TIMEOVERDUE == null || StringUtils.isEmpty( CCXY2TIMEOVERDUE.trim())) ? "0" : CCXY2TIMEOVERDUE.trim();
    CCCD2TIMEOVERDUE = (CCCD2TIMEOVERDUE == null || StringUtils.isEmpty( CCCD2TIMEOVERDUE.trim())) ? "0" : CCCD2TIMEOVERDUE.trim();
    CCDYM3TIMEOVERDUE = (CCDYM3TIMEOVERDUE == null || StringUtils.isEmpty( CCDYM3TIMEOVERDUE.trim())) ? "0" : CCDYM3TIMEOVERDUE.trim();
    CCXY3TIMEOVERDUE = (CCXY3TIMEOVERDUE == null || StringUtils.isEmpty( CCXY3TIMEOVERDUE.trim())) ? "0" : CCXY3TIMEOVERDUE.trim();
    CCCD3TIMEOVERDUE = (CCCD3TIMEOVERDUE == null || StringUtils.isEmpty( CCCD3TIMEOVERDUE.trim())) ? "0" : CCCD3TIMEOVERDUE.trim();
    CCDIVIDAMT = (CCDIVIDAMT == null || StringUtils.isEmpty(CCDIVIDAMT.trim())) ? "0" : CCDIVIDAMT.trim();
    CCVALIDPERIOD = (CCVALIDPERIOD == null) ? "" : CCVALIDPERIOD.trim();
    CCDYNOOVERDUE = (CCDYNOOVERDUE == null) ? "" : CCDYNOOVERDUE.trim();
    CCXYNOOVERDUE = (CCXYNOOVERDUE == null) ? "" : CCXYNOOVERDUE.trim();
    CCCDNOOVERDUE = (CCCDNOOVERDUE == null) ? "" : CCCDNOOVERDUE.trim();
    ACAGE = (ACAGE == null) ? "" : ACAGE.trim();
    ACWAGE = (ACWAGE == null) ? "" : ACWAGE.trim();
    ACZX1 = (ACZX1 == null) ? "" : ACZX1.trim();
    ACZX2 = (ACZX2 == null) ? "" : ACZX2.trim();
    ACZX3 = (ACZX3 == null) ? "" : ACZX3.trim();
    ACFACILITY = (ACFACILITY == null) ? "" : ACFACILITY.trim();
    ACRATE = (ACRATE == null) ? "" : ACRATE.trim();

    COMMISSIONRATE = (COMMISSIONRATE == null || StringUtils.isEmpty(COMMISSIONRATE)) ? "0" : COMMISSIONRATE;
    SHCOMMISSIONRATE = (SHCOMMISSIONRATE == null || StringUtils.isEmpty(SHCOMMISSIONRATE)) ? "0" : SHCOMMISSIONRATE;
    COMPAYDATE = (COMPAYDATE == null) ? "0" : COMPAYDATE;
    SHARETYPE = (SHARETYPE == null || StringUtils.isEmpty(SHARETYPE)) ? "0" : SHARETYPE;
    COMMISSIONTYPE = (COMMISSIONTYPE == null || StringUtils.isEmpty(COMMISSIONTYPE)) ? "0" : COMMISSIONTYPE;
    CLIENTNO = (CLIENTNO == null || StringUtils.isEmpty(CLIENTNO)) ? "0" : CLIENTNO;
    APPNO = (APPNO == null) ? "" : APPNO.trim();                    //申请单编号
    APPDATE = (APPDATE == null) ? "SYSDATE" : "to_date('" + APPDATE.trim() + "','YYYY-MM-DD')";      //申请日期
    IDTYPE = (IDTYPE == null) ? "" : IDTYPE;                        //证件名称
    ID = (ID == null) ? "" : ID;                                    //证件号码
    APPTYPE = (APPTYPE == null) ? "" : APPTYPE;                     //申请类型
    APPTYPE = (StringUtils.isEmpty(APPTYPE)) ? "0" : APPTYPE;            //默认为0 外部客户
    APPSTATUS = (APPSTATUS == null) ? "" : APPSTATUS;               //申请状态
    BIRTHDAY = (BIRTHDAY == null) ? "" : BIRTHDAY;                  //出生日期
    GENDER = (GENDER == null) ? "" : GENDER;                        //性别 enum=Gender
    NATIONALITY = (NATIONALITY == null) ? "" : NATIONALITY;         //国籍
    MARRIAGESTATUS = (MARRIAGESTATUS == null) ? "" : MARRIAGESTATUS;//婚姻状况 enum=MarriageStatus
    HUKOUADDRESS = (HUKOUADDRESS == null) ? "" : HUKOUADDRESS;      //户籍所在地
    CURRENTADDRESS = (CURRENTADDRESS == null) ? "" : CURRENTADDRESS;//现住址
    COMPANY = (COMPANY == null) ? "" : COMPANY;                     //工作单位
    TITLE = (TITLE == null) ? "" : TITLE;                           //职务 enum=Title
    QUALIFICATION = (QUALIFICATION == null) ? "" : QUALIFICATION;   //职称 enum=Qualification
    EDULEVEL = (EDULEVEL == null) ? "" : EDULEVEL;                  //学历 enum=EduLevel
    PHONE1 = (PHONE1 == null) ? "" : PHONE1;                        //移动电话
    PHONE2 = (PHONE2 == null) ? "" : PHONE2;                        //家庭电话
    PHONE3 = (PHONE3 == null) ? "" : PHONE3;                        //办公电话
    NAME = (NAME == null) ? "" : NAME;                              //客户名称 desc=(企业(个人)名称
    CLIENTTYPE = (CLIENTTYPE == null) ? "" : CLIENTTYPE;            //客户性质 enum=ClientType1
    DEGREETYPE = (DEGREETYPE == null) ? "" : DEGREETYPE;            //最高学位 enum=DegreeType
    COMADDR = (COMADDR == null) ? "" : COMADDR;                     //单位地址
    SERVFROM = (SERVFROM == null) ? "" : SERVFROM;                  //现单位工作时间
    RESIDENCEADR = (RESIDENCEADR == null) ? "" : RESIDENCEADR;      //户籍所在地(本外地) enum=ResidenceADR
    HOUSINGSTS = (HOUSINGSTS == null) ? "" : HOUSINGSTS;            //居住状况 enum=HousingSts
    HEALTHSTATUS = (HEALTHSTATUS == null) ? "" : HEALTHSTATUS;      //健康状况 enum=HealthStatus
    MONTHLYPAY = (MONTHLYPAY == null) ? "" : MONTHLYPAY;           //个人月收入
    BURDENSTATUS = (BURDENSTATUS == null) ? "" : BURDENSTATUS;      //负担状况 enum=BurdenStatus
    EMPNO = (EMPNO == null) ? "" : EMPNO.trim();                    //员工卡号码
    LIVEFROM = (LIVEFROM == null) ? "" : LIVEFROM;                  //本地居住时间
    PC = (PC == null) ? "" : PC;                                    //住宅邮编
    COMPC = (COMPC == null) ? "" : COMPC;                           //单位邮编
    RESDPC = (RESDPC == null) ? "" : RESDPC;                        //寄送地址邮编
    RESDADDR = (RESDADDR == null) ? "" : RESDADDR;                  //寄送地址
    EMAIL = (EMAIL == null) ? "" : EMAIL;                           //电子邮件

    PNAME = (PNAME == null) ? "" : PNAME;                           //配偶名称
    PIDTYPE = (PIDTYPE == null) ? "" : PIDTYPE;                     //配偶证件名称
    PID = (PID == null) ? "" : PID;                                 //配偶证件号码
    PCOMPANY = (PCOMPANY == null) ? "" : PCOMPANY;                  //配偶工作单位
    PTITLE = (PTITLE == null) ? "" : PTITLE;                        //配偶职务 enum=Title
    PPHONE1 = (PPHONE1 == null) ? "" : PPHONE1;                     //配偶移动电话
    PPHONE3 = (PPHONE3 == null) ? "" : PPHONE3;                     //配偶办公电话
    PCLIENTTYPE = (PCLIENTTYPE == null) ? "" : PCLIENTTYPE;         //配偶客户性质(单位性质) enum=ClientType1
    PSERVFROM = (PSERVFROM == null) ? "" : PSERVFROM;               //配偶现单位工作时间
    PMONTHLYPAY = (PMONTHLYPAY == null) ? "" : PMONTHLYPAY;        //配偶个人月收入
    PLIVEFROM = (PLIVEFROM == null) ? "" : PLIVEFROM;               //配偶本地居住时间

    CHANNEL = (CHANNEL == null) ? "" : CHANNEL.trim();              //销售单位(渠道名称)
//    COMMNAME = (COMMNAME == null) ? "" : COMMNAME.trim();           //商品名称
//    COMMTYPE = (COMMTYPE == null) ? "" : COMMTYPE.trim();           //商品型号
    ADDR = (ADDR == null) ? "" : ADDR;                              //配送地址
    TOTALNUM = (TOTALNUM == null) ? "" : TOTALNUM;                  //购买数量
    TOTALAMT = (TOTALAMT == null) ? "" : TOTALAMT;                  //总金额
    RECEIVEAMT = (RECEIVEAMT == null) ? "" : RECEIVEAMT;            //已付金额
    APPAMT = (APPAMT == null) ? "" : APPAMT;                        //分期金额
    DIVID = (DIVID == null) ? "" : DIVID;                           //分期期数
    PREPAY_CUTPAY_TYPE = (PREPAY_CUTPAY_TYPE == null)?"0":PREPAY_CUTPAY_TYPE;         //首付款是否代收
    ACTOPENINGBANK = (ACTOPENINGBANK == null) ? "" : ACTOPENINGBANK;//开户行 enum=Bank
    BANKACTNO = (BANKACTNO == null) ? "" : BANKACTNO;               //还款帐号
    PROVINCE = (PROVINCE == null) ? "":PROVINCE;                    //银行所在省份
    CITY = (CITY == null) ? "":CITY;                                //银行所在城市
    XY = (XY == null) ? "" : XY;                                    //信用 enum=YesNo
    XYR = (XYR == null) ? "" : XYR;                                 //信用人名称
    DY = (DY == null) ? "" : DY;                                    //抵押 enum=YesNo
    DYW = (DYW == null) ? "" : DYW;                                 //抵押物名称
    ZY = (ZY == null) ? "" : ZY;                                    //质押 enum=YesNo
    ZYW = (ZYW == null) ? "" : ZYW;                                 //质押物名称
    BZ = (BZ == null) ? "" : BZ;                                    //保证 enum=YesNo
    BZR = (BZR == null) ? "" : BZR;                                 //保证人名称
    MONPAYAMT = (MONPAYAMT == null) ? "" : MONPAYAMT;               //月均还款额
    LINKMAN = (LINKMAN == null) ? "" : LINKMAN;                     //联系人姓名
    LINKMANGENDER = (LINKMANGENDER == null) ? "" : LINKMANGENDER;   //联系人性别
    LINKMANPHONE1 = (LINKMANPHONE1 == null) ? "" : LINKMANPHONE1;   //联系人移动电话
    LINKMANPHONE2 = (LINKMANPHONE2 == null) ? "" : LINKMANPHONE2;   //联系人固定电话
    APPRELATION = (APPRELATION == null) ? "" : APPRELATION;         //与申请人关系 enum=AppRelation
    LINKMANADD = (LINKMANADD == null) ? "" : LINKMANADD;            //联系人住址
    LINKMANCOMPANY = (LINKMANCOMPANY == null) ? "" : LINKMANCOMPANY;//联系人工作单位
    ACTOPENINGBANK_UD = (ACTOPENINGBANK_UD == null) ? "" : ACTOPENINGBANK_UD;//开户行名称（电汇、网银——录入名称）
    BANKACTNAME = (BANKACTNAME == null) ? "" : BANKACTNAME;
    UNIONCHANNEL = (UNIONCHANNEL == null) ? "" : UNIONCHANNEL;          //通过银联
    SID = (SID == null) ? "" : SID;                                 //合作商城编号
    ORDERNO = (ORDERNO == null) ? "" : ORDERNO;                     //合作商城单号
    REQUESTTIME = (REQUESTTIME == null) ? "" : REQUESTTIME;         //合作商城定单生成时间

    BigDecimal dMONTHLYPAY = new BigDecimal((MONTHLYPAY.equals("")) ? "0" : MONTHLYPAY);
    BigDecimal dPMONTHLYPAY = new BigDecimal((PMONTHLYPAY.equals("")) ? "0" : PMONTHLYPAY);
    String CONFMONPAY = String.valueOf(dMONTHLYPAY.add(dPMONTHLYPAY));//核定月收入


    if (IDTYPE.equals("") || ID.equals("")) {
        session.setAttribute("msg", "没有发现传送入的参数！");
        response.sendRedirect("showinfo.jsp");
    }

    boolean temp = false;
    ConnectionManager manager = ConnectionManager.getInstance();
    CachedRowSet crs;
    String prevRecversion = "";
    String nowRecversion = "";
    if (!StringUtils.isEmpty(APPNO)) {
        prevRecversion = RECVERSION;
        CachedRowSet crsRecversion = manager.getRs("select RECVERSION from XFAPP where appno='" + APPNO + "'");
        crsRecversion.next();
        if (crsRecversion != null && crsRecversion.size() > 0)
            nowRecversion = crsRecversion.getString("RECVERSION");
    }
    if (!prevRecversion.equals(nowRecversion)) {
        //并发控制
        response.sendRedirect("application_start.jsp?sendflag=4&appno=" + APPNO);
    } else {

        //对于多选项，用默认的 splitechar"_" 分隔组串
        String splitechar = EnumValue.SPLIT_STR, SOCIALSECURITY = "", CREDITTYPE = "";
        if (SOCIALSECURITY_G != null) {
            for (String aSOCIALSECURITY_G : SOCIALSECURITY_G) {
                SOCIALSECURITY += splitechar + aSOCIALSECURITY_G;
            }
            SOCIALSECURITY = SOCIALSECURITY.replaceFirst(splitechar, "");
        }
        if (CREDITTYPE_G != null) {
            for (String aCREDITTYPE_G : CREDITTYPE_G) {
                CREDITTYPE += splitechar + aCREDITTYPE_G;
            }
            CREDITTYPE = CREDITTYPE.replaceFirst(splitechar, "");
        }
        //商品信息读取
        String COMMNOARY[] = request.getParameterValues("noCOMMNO");
        String COMMNMTYPEARY[] = request.getParameterValues("noCOMMNMTYPE");
        String NUMARY[] = request.getParameterValues("noNUM");
        String AMTARY[] = request.getParameterValues("noAMT");
        String RECEIVEAMTARY[] = request.getParameterValues("noRECEIVEAMT");
        int commCnt = COMMNMTYPEARY.length; //商品个数
        if (StringUtils.isEmpty(COMMNMTYPEARY[commCnt - 1].trim())) {
            commCnt -= 1;
        }
        String[] sql = new String[9 + commCnt];
        String sql11, sql12, sql13;
        String SEQNO;
        try {
            if (APPNO.equals("")) {
                APPNO = XFAPPNO;
                if (ACTOPENINGBANK.equals("801") || ACTOPENINGBANK.equals("802"))
                    APPSTATUS = XFConf.APPSTATUS_QIANYUE; //支付宝、快钱等需要签约状态。
                else APPSTATUS = XFConf.APPSTATUS_TIJIAO;
                String XFAPP_PKID = SeqUtil.getAPPSEQ();
                sql[0] = "insert into XFAPP (appno,CLIENTNO,name, idtype, id, spouseidtype, spouseid, CONFMONPAY, " +
                        "APPTYPE, APPSTATUS, APPDATE, SID, ORDERNO, REQUESTTIME," +
                        "SALER,APPSALARYRATE,MONTHPAYSLRRATE,COMMISSIONRATE,CHANNEL,ADDR,TOTALNUM,TOTALAMT," +
                        "RECEIVEAMT,APPAMT,DIVID,SHCOMMISSIONRATE,COMPAYDATE,SHARETYPE,PKID,PREPAY_CUTPAY_TYPE,COMMISSIONTYPE) " +
                        "values ('" + APPNO + "','" + CLIENTNO + "','" + NAME + "','" + IDTYPE + "','" + ID + "','" + PIDTYPE + "','" + PID + "','" + CONFMONPAY + "','"
                        + APPTYPE + "','" + APPSTATUS + "'," + APPDATE + ",'" + SID + "','" + ORDERNO + "', to_date('" + REQUESTTIME + "','YYYYMMDDHH24MISS')," +
                        "'" + SALER + "'," + APPSALARYRATE + "," + MONTHPAYSLRRATE + "," + COMMISSIONRATE + ",'" + CHANNEL + "'," +
                        "'" + ADDR + "','" + TOTALNUM + "'," + TOTALAMT + "," + RECEIVEAMT + "," + APPAMT + ",'" + DIVID + "'," +
                        SHCOMMISSIONRATE + "," + COMPAYDATE + ",'" + SHARETYPE + "','" + XFAPP_PKID + "','" + PREPAY_CUTPAY_TYPE + "','" + COMMISSIONTYPE + "')";
                sql[1] = " insert into xfcreditinfobatch (appno, ccvalidperiod, ccdynum, ccxynum, cccdnum, ccdyamt, ccxyamt, cccdamt, ccdynowbal, ccxynowbal," +
                        " cccdnowbal, ccdyrpmon, ccxyrpmon, cccdrpmon, ccdyyearrptime, ccdynooverdue, ccxynooverdue, cccdnooverdue, ccdy1timeoverdue," +
                        " ccxy1timeoverdue, cccd1timeoverdue, ccdy2timeoverdue, ccxy2timeoverdue, cccd2timeoverdue, ccdym3timeoverdue, ccxy3timeoverdue," +
                        " cccd3timeoverdue, ccloanyearquerytime, cccardyearquerytime," +
                        " acage, acwage,acfacility,ACRATE,ccrptotalamt, ccrprate,aczx1, aczx2, aczx3," +
                        " ccxyyearrptime, cccdyearrptime,CCDIVIDAMT,CCRPNOWAMT) " +
                        " values ('" + APPNO + "','" + CCVALIDPERIOD + "'," + CCDYNUM + "," + CCXYNUM + "," + CCCDNUM + "," + CCDYAMT + "," +
                        " " + CCXYAMT + "," + CCCDAMT + "," + CCDYNOWBAL + "," + CCXYNOWBAL + "," + CCCDNOWBAL + "," + CCDYRPMON + "," + CCXYRPMON + "," +
                        " " + CCCDRPMON + "," + CCDYYEARRPTIME + ",'" + CCDYNOOVERDUE + "','" + CCXYNOOVERDUE + "','" + CCCDNOOVERDUE + "'," + CCDY1TIMEOVERDUE + "," + CCXY1TIMEOVERDUE + "," +
                        " " + CCCD1TIMEOVERDUE + "," + CCDY2TIMEOVERDUE + "," + CCXY2TIMEOVERDUE + "," + CCCD2TIMEOVERDUE + "," + CCDYM3TIMEOVERDUE + "," + CCXY3TIMEOVERDUE + "," + CCCD3TIMEOVERDUE + "," +
                        " 0,0,'" + ACAGE + "','" + ACWAGE + "','" + ACFACILITY + "','" + ACRATE + "'," + CCRPTOTALAMT + "," + MONTHPAYSLRRATE + ",'" + ACZX1 + "','" +
                        "" + ACZX2 + "','" + ACZX3 + "'," + CCXYYEARRPTIME + "," + CCCDYEARRPTIME + "," + CCDIVIDAMT + "," + MONPAYAMT + ")";
                sql[2] = "insert into XFAPPADD (appno, idtype, id, actopeningbank, bankactno, xy, xyr, dy, dyw, zy, zyw, bz, bzr, credittype, monpayamt, linkman, linkmangender, linkmanphone1,linkmanphone2,apprelation,LINKMANADD,LINKMANCOMPANY,ACTOPENINGBANK_UD) " +
                        "values ('" + APPNO + "','" + IDTYPE + "','" + ID + "','" + ACTOPENINGBANK + "','" + BANKACTNO + "','" + XY + "','" + XYR + "','" + DY + "','" + DYW + "','" + ZY + "','" + ZYW + "','" + BZ + "','" + BZR + "','" + CREDITTYPE + "','" + MONPAYAMT + "','" + LINKMAN + "','" + LINKMANGENDER + "','" + LINKMANPHONE1 + "','" + LINKMANPHONE2 + "','" + APPRELATION + "','" + LINKMANADD + "','" + LINKMANCOMPANY + "','" + ACTOPENINGBANK_UD + "')";
                sql[3] = "insert into XFCLIENT (APPNO,XFCLTP,LASTMODIFIED,APPDATE,BIRTHDAY,GENDER,NATIONALITY," +
                        "MARRIAGESTATUS,HUKOUADDRESS,CURRENTADDRESS,COMPANY,TITLE,QUALIFICATION,EDULEVEL," +
                        "PHONE1,PHONE2,PHONE3,CLIENTNO,NAME,IDTYPE,ID,CLIENTTYPE,DEGREETYPE,COMADDR,SERVFROM," +
                        "RESIDENCEADR,HOUSINGSTS,HEALTHSTATUS,MONTHLYPAY,BURDENSTATUS,EMPNO,SOCIALSECURITY," +
                        "LIVEFROM,PC,COMPC,RESDPC,RESDADDR,EMAIL,actopeningbank,bankactno,SLRYEVETYPE," +
                        "SERVFROMMONTH,LIVEFROMMONTH) " +
                        "values ('" + APPNO + "',1,SYSDATE," + APPDATE + ",to_date('" + BIRTHDAY + "','YYYY-MM-DD'),'" +
                        GENDER + "','" + NATIONALITY + "','" + MARRIAGESTATUS + "','" + HUKOUADDRESS + "','" + CURRENTADDRESS +
                        "','" + COMPANY + "','" + TITLE + "','" + QUALIFICATION + "','" + EDULEVEL + "','" + PHONE1 +
                        "','" + PHONE2 + "','" + PHONE3 + "','" + CLIENTNO + "','" + NAME + "','" + IDTYPE + "','" + ID +
                        "','" + CLIENTTYPE + "','" + DEGREETYPE + "','" + COMADDR + "'," + SERVFROM + ",'" + RESIDENCEADR +
                        "','" + HOUSINGSTS + "','" + HEALTHSTATUS + "','" + MONTHLYPAY + "','" + BURDENSTATUS + "','" + EMPNO +
                        "','" + SOCIALSECURITY + "'," + LIVEFROM + ",'" + PC + "','" + COMPC + "','" + RESDPC + "','" + RESDADDR +
                        "','" + EMAIL + "','" + ACTOPENINGBANK + "','" + BANKACTNO + "','" + SLRYEVETYPE + "'," +
                        SERVFROMMONTH + "," + LIVEFROMMONTH + ")";
                if (!PID.equals(""))
                    sql[4] = "insert into XFCLIENT (APPNO,XFCLTP,LASTMODIFIED,APPDATE,MARRIAGESTATUS,NAME,IDTYPE,ID," +
                            "COMPANY,TITLE,PHONE1,PHONE3,CLIENTTYPE,SERVFROM,MONTHLYPAY,LIVEFROM," +
                            "SERVFROMMONTH,LIVEFROMMONTH) " +
                            "values ('" + APPNO + "',2,SYSDATE," + APPDATE + ",'" + MARRIAGESTATUS + "','" + PNAME + "','" + PIDTYPE +
                            "','" + PID + "','" + PCOMPANY + "','" + PTITLE + "','" + PPHONE1 + "','" + PPHONE3 + "','" + PCLIENTTYPE +
                            "'," + PSERVFROM + "','" + PMONTHLYPAY + "'," + PLIVEFROM + "," +
                            PSERVFROMMONTH + "," + PLIVEFROMMONTH + ")";
                //20110718 haiyu 增加表
                OperatorManager opm = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
                sql[5] = " insert into XFAPPREPAYMENT (appno, actopeningbank, bankactno, actopeningbank_ud, bankactname, channel, recversion, operid, operdate,PROVINCE,CITY) " +
                        "values ('" + APPNO + "','" + ACTOPENINGBANK + "','" + BANKACTNO + "','" + ACTOPENINGBANK_UD + "','" + BANKACTNAME + "'," +
                        "'" + UNIONCHANNEL + "',0,'" + opm.getOperatorId() + "',SYSDATE,'" + PROVINCE +  "','" + CITY + "')";

            } else {

                sql[0] = "update XFAPP set CLIENTNO='" + CLIENTNO + "', name='" + NAME + "',idtype='" + IDTYPE + "'," +
                        "id='" + ID + "',spouseidtype='" + PIDTYPE + "',spouseid='" + PID + "',CONFMONPAY='" + CONFMONPAY + "'," +
                        "APPTYPE='" + APPTYPE + "',APPSTATUS='" + APPSTATUS + "',APPDATE=" + APPDATE + ",SALER='" + SALER + "'," +
                        " APPSALARYRATE=" + APPSALARYRATE + ",MONTHPAYSLRRATE=" + MONTHPAYSLRRATE + ",COMMISSIONRATE=" + COMMISSIONRATE +
                        ",CHANNEL='" + CHANNEL + "',ADDR='" + ADDR + "',TOTALNUM='" + TOTALNUM + "',TOTALAMT=" + TOTALAMT +
                        ",RECEIVEAMT=" + RECEIVEAMT + ",APPAMT=" + APPAMT + ",DIVID='" + DIVID + "'" +
                        ",SHCOMMISSIONRATE=" + SHCOMMISSIONRATE +
                        ",COMPAYDATE=" + COMPAYDATE + ",SHARETYPE='" + SHARETYPE + "',RECVERSION=(RECVERSION+1),PREPAY_CUTPAY_TYPE='" + PREPAY_CUTPAY_TYPE + "'" +
                        ",COMMISSIONTYPE='" + COMMISSIONTYPE + "'" +
                        ",APPNO='" + XFAPPNO + "'" +
                        " where appno= '" + APPNO + "'";
                /*CCDIVIDAMT  CCRPNOWAMT
                * */
                sql[1] = "update xfcreditinfobatch set CCVALIDPERIOD ='" + CCVALIDPERIOD + "',CCDYNUM=" + CCDYNUM + ",CCXYNUM=" + CCXYNUM + "," +
                        "CCCDNUM=" + CCCDNUM + ",CCDYAMT=" + CCDYAMT + ",CCXYAMT=" + CCXYAMT + ",CCCDAMT=" + CCCDAMT + ",CCDYNOWBAL=" + CCDYNOWBAL + "," +
                        "CCXYNOWBAL=" + CCXYNOWBAL + ",CCCDNOWBAL=" + CCCDNOWBAL + ",CCDYRPMON=" + CCDYRPMON + ",CCXYRPMON=" + CCXYRPMON + "," +
                        "CCCDRPMON=" + CCCDRPMON + ",CCDYYEARRPTIME=" + CCDYYEARRPTIME + ",CCXYYEARRPTIME=" + CCXYYEARRPTIME + ",CCCDYEARRPTIME=" + CCCDYEARRPTIME +"," +
                        "CCDYNOOVERDUE='" + CCDYNOOVERDUE + "',CCXYNOOVERDUE='" + CCXYNOOVERDUE + "',CCCDNOOVERDUE='" + CCCDNOOVERDUE + "'," +
                        "CCDY1TIMEOVERDUE=" + CCDY1TIMEOVERDUE + ",CCXY1TIMEOVERDUE=" + CCXY1TIMEOVERDUE + ",CCCD1TIMEOVERDUE=" + CCCD1TIMEOVERDUE + "," +
                        "CCDY2TIMEOVERDUE=" + CCDY2TIMEOVERDUE +"," + "CCXY2TIMEOVERDUE=" + CCXY2TIMEOVERDUE +"," + "CCCD2TIMEOVERDUE=" + CCCD2TIMEOVERDUE +"," +
                        "CCDYM3TIMEOVERDUE=" + CCDYM3TIMEOVERDUE +"," + "CCXY3TIMEOVERDUE=" + CCXY3TIMEOVERDUE +"," + "CCCD3TIMEOVERDUE=" + CCCD3TIMEOVERDUE +"," +
                        "ACAGE='" + ACAGE + "',ACWAGE='" + ACWAGE + "',ACZX1='" + ACZX1 + "'," + "ACZX2='" + ACZX2 + "',ACZX3='" + ACZX3 + "'," +
                        "ACFACILITY='" + ACFACILITY + "'," + "ACRATE='" + ACRATE + "'," + "CCRPTOTALAMT=" + CCRPTOTALAMT + "," +
                        "CCRPNOWAMT=" + MONPAYAMT + ",CCDIVIDAMT=" + CCDIVIDAMT +
                        ",APPNO='" + XFAPPNO + "'" +
                        " where appno= '" + APPNO + "'";
                sql[2] = "update XFAPPADD set idtype='" + IDTYPE + "',id='" + ID + "',actopeningbank='" + ACTOPENINGBANK + "',bankactno='" + BANKACTNO + "'," +
                        "xy='" + XY + "',xyr='" + XYR + "',dy='" + DY + "',dyw='" + DYW + "',zy='" + ZY + "',zyw='" + ZYW + "',bz='" + BZ + "'," +
                        "bzr='" + BZR + "',credittype='" + CREDITTYPE + "',monpayamt='" + MONPAYAMT + "',linkman='" + LINKMAN + "'," +
                        "linkmangender='" + LINKMANGENDER + "',linkmanphone1='" + LINKMANPHONE1 + "',linkmanphone2='" + LINKMANPHONE2 + "'," +
                        "apprelation='" + APPRELATION + "',LINKMANADD='" + LINKMANADD + "',LINKMANCOMPANY='" + LINKMANCOMPANY + "'," +
                        "ACTOPENINGBANK_UD='" + ACTOPENINGBANK_UD + "'" +
                        ",APPNO='" + XFAPPNO + "'" +
                        " where appno= '" + APPNO + "'";
                sql[3] = "update XFCLIENT set LASTMODIFIED=SYSDATE,BIRTHDAY=to_date('" + BIRTHDAY + "','YYYY-MM-DD')," +
                        "GENDER='" + GENDER + "',NATIONALITY='" + NATIONALITY + "',MARRIAGESTATUS='" + MARRIAGESTATUS + "'," +
                        "HUKOUADDRESS='" + HUKOUADDRESS + "',CURRENTADDRESS='" + CURRENTADDRESS + "',COMPANY='" + COMPANY + "'," +
                        "TITLE='" + TITLE + "',QUALIFICATION='" + QUALIFICATION + "',EDULEVEL='" + EDULEVEL + "',PHONE1='" + PHONE1 + "'," +
                        "PHONE2='" + PHONE2 + "',PHONE3='" + PHONE3 + "',CLIENTNO='" + CLIENTNO + "',NAME='" + NAME + "',IDTYPE='" + IDTYPE + "'," +
                        "ID='" + ID + "',CLIENTTYPE='" + CLIENTTYPE + "',DEGREETYPE='" + DEGREETYPE + "',COMADDR='" + COMADDR + "'," +
                        "SERVFROM=" + SERVFROM + ",RESIDENCEADR='" + RESIDENCEADR + "',HOUSINGSTS='" + HOUSINGSTS + "'," +
                        "HEALTHSTATUS='" + HEALTHSTATUS + "',MONTHLYPAY='" + MONTHLYPAY + "',BURDENSTATUS='" + BURDENSTATUS + "'," +
                        "EMPNO='" + EMPNO + "',SOCIALSECURITY='" + SOCIALSECURITY + "',LIVEFROM=" + LIVEFROM + ",PC='" + PC + "'," +
                        "COMPC='" + COMPC + "',RESDPC='" + RESDPC + "',RESDADDR='" + RESDADDR + "',EMAIL='" + EMAIL + "'," +
                        "actopeningbank='" + ACTOPENINGBANK + "',bankactno='" + BANKACTNO + "',SLRYEVETYPE='" + SLRYEVETYPE + "'" +
                        /*SERVFROMMONTH,LIVEFROMMONTH  PSERVFROMMONTH  PLIVEFROMMONTH*/
                        ",SERVFROMMONTH=" + SERVFROMMONTH + ",LIVEFROMMONTH=" + LIVEFROMMONTH +
                        ",APPNO='" + XFAPPNO + "'" +
                        " where APPNO='" + APPNO + "' and XFCLTP='1' ";
                if (!PID.equals("")) {
                    sql11 = "select * from XFCLIENT where APPNO='" + APPNO + "' and XFCLTP='2'";
                    crs = manager.getRs(sql11);
                    if (crs.next()) {
                        sql[4] = "update XFCLIENT set LASTMODIFIED=SYSDATE, MARRIAGESTATUS='" + MARRIAGESTATUS + "', NAME='" + PNAME + "',IDTYPE='" + PIDTYPE + "'," +
                                "ID='" + PID + "',COMPANY='" + PCOMPANY + "',TITLE='" + PTITLE + "',PHONE1='" + PPHONE1 + "',PHONE3='" + PPHONE3 + "'," +
                                "CLIENTTYPE='" + PCLIENTTYPE + "',SERVFROM=" + PSERVFROM + ",MONTHLYPAY='" + PMONTHLYPAY + "',LIVEFROM=" + PLIVEFROM + "" +
                                ",SERVFROMMONTH=" + PSERVFROMMONTH + ",LIVEFROMMONTH=" + PLIVEFROMMONTH +
                                ",APPNO='" + XFAPPNO + "'" +
                                " where APPNO='" + APPNO + "' and XFCLTP='2' ";
                    } else
                        sql[4] = "insert into XFCLIENT (APPNO,XFCLTP,LASTMODIFIED,APPDATE,MARRIAGESTATUS,NAME,IDTYPE,ID,COMPANY," +
                                "TITLE,PHONE1,PHONE3,CLIENTTYPE,SERVFROM,MONTHLYPAY,LIVEFROM,SERVFROMMONTH,LIVEFROMMONTH) " +
                                "values ('" + XFAPPNO + "',2,SYSDATE," + APPDATE + ",'" + MARRIAGESTATUS + "','" + PNAME + "','" + PIDTYPE +
                                "','" + PID + "','" + PCOMPANY + "','" + PTITLE + "','" + PPHONE1 + "','" + PPHONE3 + "','" + PCLIENTTYPE +
                                "'," + PSERVFROM + ",'" + PMONTHLYPAY + "'," + PLIVEFROM + "," +
                                PSERVFROMMONTH + "," + PLIVEFROMMONTH + ")";
                } else {
                    sql[4] = "delete from XFCLIENT where APPNO='" + APPNO + "' and XFCLTP='2' ";
                }
                OperatorManager opm = (OperatorManager) session.getAttribute(SystemAttributeNames.USER_INFO_NAME);
                sql[5] = " update XFAPPREPAYMENT set ACTOPENINGBANK='" + ACTOPENINGBANK + "',BANKACTNO='" + BANKACTNO + "'," +
                        "ACTOPENINGBANK_UD='" + ACTOPENINGBANK_UD + "',BANKACTNAME='" + BANKACTNAME + "',CHANNEL='" + UNIONCHANNEL + "'," +
                        "OPERID='" + opm.getOperatorId() + "',OPERDATE=SYSDATE,PROVINCE='" + PROVINCE + "',CITY='" + CITY + "'" +
                        ",APPNO='" + XFAPPNO + "'" +
                        " where APPNO='" + APPNO + "'";
            }
            /*
             * 操作商品表 先删后插*/
            String COMMNO = "";
            String COMMNMTYPE = "";
            String NUM = "";
            String AMT = "";
            String chRECEIVEAMT = "";
            String vaAPPTYPE = "";
//            String request.getParameter("BZ");
            sql[6] = "delete from xfappcommbatch where APPNO='" + APPNO + "'";
            for (int i = 0; i < commCnt; i++) {
                COMMNMTYPE = COMMNMTYPEARY[i].trim();
                if (COMMNMTYPE != null && !StringUtils.isEmpty(COMMNMTYPE)) {
                    COMMNO = COMMNOARY[i].trim();
                    NUM = NUMARY[i].trim();
                    AMT = AMTARY[i].trim();
                    chRECEIVEAMT = RECEIVEAMTARY[i].trim();
                    String strAPPTYPE = "APPTYPE" + (i+1);
                    vaAPPTYPE = request.getParameter(strAPPTYPE);
                    sql[(7 + i)] = "insert into xfappcommbatch (appno, commnmtype, num, amt, receiveamt, recversion, commno,apptype)" +
                            " values('" + XFAPPNO + "','" + COMMNMTYPE + "'," + NUM + "," + AMT + "," + chRECEIVEAMT + ",0,'" + COMMNO + "','" + vaAPPTYPE + "')";
                }
            }
            /*
            * SOCIALSECURITYID,ct.ANNUALINCOME,ct.ETPSCOPTYPE*/
            sql[7 + commCnt] = "delete from CMINDVCLIENT  where IDTYPE='" + IDTYPE + "' and ID='" + ID + "'";
            sql[8 + commCnt] = "insert into CMINDVCLIENT (LASTMODIFIED,BIRTHDAY,GENDER,NATIONALITY,MARRIAGESTATUS,HUKOUADDRESS," +
                    "CURRENTADDRESS,COMPANY,TITLE,QUALIFICATION,EDULEVEL,PHONE1,PHONE2,PHONE3,NAME,CLIENTTYPE,DEGREETYPE," +
                    "COMADDR,SERVFROM,RESIDENCEADR,HOUSINGSTS,HEALTHSTATUS,MONTHLYPAY,BURDENSTATUS,SOCIALSECURITY,LIVEFROM," +
                    "PC,COMPC,RESDPC,RESDADDR,EMAIL,actopeningbank,bankactno,SOCIALSECURITYID,ANNUALINCOME,ETPSCOPTYPE,IDTYPE,ID,APPDATE) " +
                    "values(SYSDATE,to_date('" + BIRTHDAY + "','YYYY-MM-DD'),'" + GENDER + "','" + NATIONALITY + "','" + MARRIAGESTATUS + "'," +
                    "'" + HUKOUADDRESS + "','" + CURRENTADDRESS + "','" + COMPANY + "','" + TITLE + "','" + QUALIFICATION + "','" + EDULEVEL + "'," +
                    "'" + PHONE1 + "','" + PHONE2 + "','" + PHONE3 + "','" + NAME + "','" + CLIENTTYPE + "','" + DEGREETYPE + "'," +
                    "'" + COMADDR + "','" + SERVFROM + "','" + RESIDENCEADR + "','" + HOUSINGSTS + "','" + HEALTHSTATUS + "'," +
                    "'" + MONTHLYPAY + "','" + BURDENSTATUS + "','" + SOCIALSECURITY + "','" + LIVEFROM + "','" + PC + "'," +
                    "'" + COMPC + "','" + RESDPC + "','" + RESDADDR + "','" + EMAIL + "','" + ACTOPENINGBANK + "','" + BANKACTNO + "'," +
                    "'" + SOCIALSECURITYID + "'," + ANNUALINCOME + ",'" + ETPSCOPTYPE + "','" + IDTYPE + "','" + ID + "',SYSDATE)";
            for (int sqli = 0; sqli < sql.length; sqli++) {
                System.out.println(sql[sqli]);
            }
            temp = manager.execBatch(sql);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("application_start.jsp?sendflag=3&appno=" + XFAPPNO);
        }
        if (temp) {
            response.sendRedirect("application_start.jsp?sendflag=2&appno=" + XFAPPNO);
        }
    }
%>