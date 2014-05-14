<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="javax.sql.rowset.CachedRowSet" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="java.util.Vector" %>
<%@ page import="pub.platform.db.ConnectionManager" %>
<%@ page import="pub.platform.db.DatabaseConnection" %>
<%@ page import="pub.platform.db.DB2_81" %>
<%@ page import="hfc.xf.XFConf" %>
<%@ page import="pub.cenum.Level" %>
<%@ page import="pub.platform.utils.BusinessDate" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%@ page import="hfc.common.AppCommonEnum" %>
<%--
===============================================
Title: 消费信贷-个人消费分期付款申请书
Description: 个人消费分期付款申请书。
 * @version  $Revision: 1.0 $  $Date: 2009/03/02 08:20:31 $
 * @author liujian
 * <p/>修改：$Author: liuj $
===============================================
--%>
<%
    request.setCharacterEncoding("GBK");
    DecimalFormat df = new DecimalFormat("###,###,###,##0.00");

    String APPNO = request.getParameter("appno");         //申请单编号
    String SENDFLAG = request.getParameter("sendflag");   //传送来源 1 查询 页面只读 2 保存成功 3 修改失败 4 并发错误
    String readonly = "";
    if (SENDFLAG != null) {
        if (SENDFLAG.equals("2")) {
%>
<script type="text/javascript">
    alert("成功录入。");
    top.window.opener.document.getElementById("queryForm:btnQuery").click();
    window.close();
</script>
<%
} else if (SENDFLAG.equals("1")) {
    readonly = "readonly";
} else if (SENDFLAG.equals("4")) {
%>
<script type="text/javascript">
    alert("信息修改失败，\r\n其他终端已更新该数据，请重新打开该页面进行修改操作。");
    top.window.opener.document.getElementById("queryForm:btnQuery").click();
    window.close();
</script>
<%
} else if (SENDFLAG.equals("3")) {
%>
<script type="text/javascript">
    alert("信息修改出现异常，请检查录入信息是否正确。");
    top.window.opener.document.getElementById("queryForm:btnQuery").click();
    window.close();
</script>
<%
        }
    }
    String SID = "";                                       //合作商城编号
    String ORDERNO = "";                                   //合作商城单号
    String REQUESTTIME = "";                               //合作商城定单生成时间
    APPNO = (APPNO == null) ? "" : APPNO;
    if (APPNO != null) {
        DB2_81 manager = new DB2_81();
        String sql1 = "", sql2 = "";
        if (!APPNO.equals("")) {
            sql1 = "select a.PREPAY_CUTPAY_TYPE,xp.CITY,xp.PROVINCE,a.RECVERSION,xp.CHANNEL as UNIONCHANNEL,a.COMMISSIONRATE,a.SHCOMMISSIONRATE,COMPAYDATE,a.SHARETYPE,a.COMMISSIONTYPE," +
                    " c.SERVFROMMONTH,c.LIVEFROMMONTH,p.SERVFROMMONTH PSERVFROMMONTH,p.LIVEFROMMONTH PLIVEFROMMONTH," +
                    " xp.BANKACTNAME,ct.SOCIALSECURITYID,ct.ANNUALINCOME,ct.ETPSCOPTYPE,a.SALER,a.APPSALARYRATE,a.MONTHPAYSLRRATE,c.SLRYEVETYPE, " +
                    " ccd.CCVALIDPERIOD,ccd.CCDYNUM,ccd.CCXYNUM,ccd.CCCDNUM,ccd.CCDYAMT,ccd.CCXYAMT,ccd.CCCDAMT," +
                    " ccd.CCDYNOWBAL,ccd.CCXYNOWBAL , ccd.CCCDNOWBAL ,ccd.CCDYRPMON , ccd.CCXYRPMON , ccd.CCCDRPMON ," +
                    " ccd.CCDYYEARRPTIME,ccd.CCXYYEARRPTIME,ccd.CCCDYEARRPTIME,ccd.CCDYNOOVERDUE,ccd.CCXYNOOVERDUE,ccd.CCCDNOOVERDUE," +
                    " ccd.CCDY1TIMEOVERDUE,ccd.CCXY1TIMEOVERDUE,ccd.CCCD1TIMEOVERDUE,ccd.CCDY2TIMEOVERDUE,ccd.CCXY2TIMEOVERDUE,ccd.CCCD2TIMEOVERDUE," +
                    " ccd.CCDYM3TIMEOVERDUE,ccd.CCXY3TIMEOVERDUE,ccd.CCCD3TIMEOVERDUE,ccd.ACAGE,ccd.ACWAGE,ccd.ACZX1,ccd.ACZX2,ccd.ACZX3," +
                    " ccd.ACFACILITY,ccd.ACRATE,ccd.CCRPTOTALAMT,ccd.CCDIVIDAMT," +
                    "c.CLIENTNO,to_char(c.BIRTHDAY,'yyyyMMdd') BIRTHDAY,c.GENDER,c.NATIONALITY,c.MARRIAGESTATUS,c.HUKOUADDRESS,c.CURRENTADDRESS," +
                    "c.COMPANY,c.TITLE,c.QUALIFICATION,c.EDULEVEL,c.PHONE1,c.PHONE2," +
                    "c.PHONE3,c.NAME,c.CLIENTTYPE,c.DEGREETYPE,c.COMADDR,c.SERVFROM,c.RESIDENCEADR,c.HOUSINGSTS," +
                    "c.HEALTHSTATUS,c.MONTHLYPAY,c.BURDENSTATUS,c.SOCIALSECURITY,c.LIVEFROM,c.PC,c.COMPC,c.RESDPC,c.RESDADDR,c.EMAIL," +
                    "p.NAME  PNAME ,p.IDTYPE PIDTYPE ,p.ID PID ,p.COMPANY PCOMPANY ,p.TITLE PTITLE ,p.PHONE1 PPHONE1 ," +
                    "p.PHONE3 PPHONE3 ,p.CLIENTTYPE PCLIENTTYPE ,p.SERVFROM PSERVFROM ,p.MONTHLYPAY PMONTHLYPAY ," +
                    "p.LIVEFROM PLIVEFROM," +
                    "a.CHANNEL,a.ADDR,a.TOTALNUM,a.TOTALAMT,a.RECEIVEAMT,a.APPAMT,a.DIVID," +
                    "d.ACTOPENINGBANK,d.BANKACTNO,d.XY,d.XYR,d.DY,d.DYW,d.ZY,d.ZYW,d.BZ,d.BZR,d.CREDITTYPE,d.MONPAYAMT," +
                    "d.LINKMAN,d.LINKMANGENDER,d.LINKMANPHONE1,d.LINKMANPHONE2,d.APPRELATION,d.LINKMANADD,d.LINKMANCOMPANY,d.ACTOPENINGBANK_UD," +
                    "a.IDTYPE,a.ID,to_char(a.APPDATE,'yyyyMMdd') as APPDATE,a.APPSTATUS,a.SID,a.ORDERNO,a.REQUESTTIME " +
                    "from XFCLIENT c,CMINDVCLIENT ct,XFAPPADD d,XFAPP a " +
                    "left outer join XFCLIENT p on a.APPNO=p.APPNO and p.XFCLTP='2' " +
                    "left join  xfcreditinfobatch ccd on a.appno=ccd.appno left join XFAPPREPAYMENT xp on a.appno=xp.appno " +
                    "where a.APPNO='" + APPNO + "' and a.APPNO=c.APPNO and c.XFCLTP='1' " +
                    "and a.APPNO=d.APPNO and c.IDTYPE=ct.IDTYPE and c.id=ct.id ";
            sql2 = " select t.COMMNMTYPE,t.NUM,t.AMT,t.RECEIVEAMT,COMMNO,APPTYPE from xfappcommbatch t " +
                    " where t.appno='" + APPNO + "' order by t.APPNO ";
        }
        String PREPAY_CUTPAY_TYPE = "";              //首付款是否代收
        String RECVERSION = "";                //版本号
        String COMMISSIONRATE = "";            //客户手续费率
        String SHCOMMISSIONRATE = "";          //商户手续费率
        String COMPAYDATE = "";                //还款日
        String SHARETYPE = "";                 //分摊方式
        String COMMISSIONTYPE = "";            //客户手续费缴纳方式
        //征信信息
        String CCVALIDPERIOD = "";               //征信信息有效时段 ENUM=CCValidPeriod
        String CCDYNUM = "";                     //抵押贷款数量(笔数)
        String CCXYNUM = "";                     //信用贷款数量(笔数)
        String CCCDNUM = "";                     //信用卡数量(笔数)
        String CCDYAMT = "";                     //抵押贷款总贷款额／额度
        String CCXYAMT = "";                     //CAP=信用贷款总贷款额／额度
        String CCCDAMT = "";                     //CAP=信用卡总贷款额／额度
        String CCDYNOWBAL = "";                     //抵押贷款余额／额度
        String CCXYNOWBAL = "";                     //信用贷款余额／额度
        String CCCDNOWBAL = "";                     //信用卡余额／额度
        String CCDYRPMON = "";                     //抵押贷款当月还款额
        String CCXYRPMON = "";                     // 信用贷款当月还款额
        String CCCDRPMON = "";                     // 信用卡贷款当月还款额
        String CCDYYEARRPTIME = "";                  //抵押贷款12个月还款记录 (次数)
        String CCXYYEARRPTIME = "";                 // 信用贷款12个月还款记录 (次数)
        String CCCDYEARRPTIME = "";                 //信用卡12个月还款记录 (次数)
        String CCDYNOOVERDUE = "";                     // 抵押无逾期 enum=YesNo
        String CCXYNOOVERDUE = "";                     //信用无逾期 enum=YesNo
        String CCCDNOOVERDUE = "";                     // 信用卡无逾期 enum=YesNo
        String CCDY1TIMEOVERDUE = "";                     //抵押逾期1-30天(1期)
        String CCXY1TIMEOVERDUE = "";                     // 信用逾期1-30天(1期)
        String CCCD1TIMEOVERDUE = "";                     //信用卡逾期1-30天(1期)
        String CCDY2TIMEOVERDUE = "";                     //抵押逾期31-60天(2期)
        String CCXY2TIMEOVERDUE = "";                     //信用逾期31-60天(2期)
        String CCCD2TIMEOVERDUE = "";                     //信用卡逾期31-60天(2期)
        String CCDYM3TIMEOVERDUE = "";                     //抵押逾期60天以上(3期)
        String CCXY3TIMEOVERDUE = "";                     //信用逾期60天以上(3期)
        String CCCD3TIMEOVERDUE = "";                     //信用卡逾期60天以上(3期)
        String ACAGE = "";                     //年龄要求 enum=YesNo
        String ACWAGE = "";                     //工资要求 enum=YesNo
        String ACZX1 = "";                     //征信系统内最近一期无未还款记录 enum=YesNo
        String ACZX2 = "";                     //征信系统内过去12个月最高逾期期数不超过2次 enum=YesNo
        String ACZX3 = "";                     //征信系统内过去3个月最长逾期天数在60天以下 enum=YesNo
        String ACFACILITY = "";                     //分期还款与收入比要求(低于30%) enum=YesNo
        String ACRATE = "";                    //每月还款与收入比要求(低于60%) enum=YesNo
        String CCRPTOTALAMT = "";              //总还款额
        String CCDIVIDAMT = "";                //该笔分期还款额

        String IDTYPE = "";                    //证件名称
        String ID = "";                        //证件号码
        String NAME = "";                      //客户名称 desc=企业(个人)名称
        String APPSTATUS = "";                 //申请状态
        String CLIENTNO = "";                  //客户号
        String BIRTHDAY = "";                  //出生日期
        String GENDER = "";                    //性别 enum=Gender
        String NATIONALITY = "";               //国籍
        String MARRIAGESTATUS = "";            //婚姻状况 enum=MarriageStatus
        String HUKOUADDRESS = "";              //户籍所在地
        String CURRENTADDRESS = "";            //现住址
        String COMPANY = "";                   //工作单位
        String TITLE = "";                     //职务 enum=Title
        String QUALIFICATION = "";             //职称 enum=Qualification
        String EDULEVEL = "";                  //学历 enum=EduLevel
        String PHONE1 = "";                    //移动电话
        String PHONE2 = "";                    //家庭电话
        String PHONE3 = "";                    //办公电话
        String CLIENTTYPE = "";                //客户性质 enum=ClientType1
        String DEGREETYPE = "";                //最高学位 enum=DegreeType
        String COMADDR = "";                   //单位地址
        String SERVFROM = "";                  //现单位工作年数
        String SERVFROMMONTH = "";             //现单位工作月数
        String RESIDENCEADR = "";              //户籍所在地(本外地) enum=ResidenceADR
        String HOUSINGSTS = "";                //居住状况 enum=HousingSts
        String HEALTHSTATUS = "";              //健康状况 enum=HealthStatus
        String MONTHLYPAY = "";                //个人月收入
        String SLRYEVETYPE = "";               //收入证明类型
        String BURDENSTATUS = "";              //负担状况 enum=BurdenStatus
        String SOCIALSECURITYID = "";          //社保编号
        String ANNUALINCOME = "";              //年总收入
        String ETPSCOPTYPE = "";                //行业性质
        String SOCIALSECURITY = "";            //社会保障 enum=SocialSecurity
        String LIVEFROM = "";                  //本地居住年数
        String LIVEFROMMONTH = "";             //本地居住月数
        String PC = "";                        //住宅邮编
        String COMPC = "";                     //单位邮编
        String RESDPC = "";                    //寄送地址邮编
        String RESDADDR = "";                  //寄送地址
        String EMAIL = "";                     //电子邮件

        String PNAME = "";                     //配偶名称
        String PIDTYPE = "";                   //配偶证件名称
        String PID = "";                       //配偶证件号码
        String PCOMPANY = "";                  //配偶工作单位
        String PTITLE = "";                    //配偶职务 enum=Title
        String PPHONE1 = "";                   //配偶移动电话
        String PPHONE3 = "";                   //配偶办公电话
        String PCLIENTTYPE = "";               //配偶客户性质(单位性质) enum=ClientType1
        String PSERVFROM = "";                 //配偶现单位工作时间
        String PMONTHLYPAY = "";               //配偶个人月收入
        String PLIVEFROM = "";                 //配偶本地居住时间
        String PSERVFROMMONTH = "";            //配偶现单位工作月数
        String PLIVEFROMMONTH = "";            //配偶本地居住月数
        String CHANNEL = "";                   //销售单位(渠道名称)
        String ADDR = "";                      //配送地址
        String TOTALNUM = "";                  //购买数量
        String TOTALAMT = "";                  //总金额
        String SALER = "";                     //销售人员
//        String
        String RECEIVEAMT = "";                //已付金额
        String APPAMT = "";                    //分期金额
        String DIVID = "";                     //分期期数
        String APPSALARYRATE = "";             //分期付款与收入比率
        String MONTHPAYSLRRATE = "";           //总债务余额均还款与收入比率
        String ACTOPENINGBANK = "";            //开户行 enum=Bank
        String UNIONCHANNEL = "";              //代理渠道 01 通过银联 00 不
        String BANKACTNO = "";                 //还款帐号
        String PROVINCE = "";                  //银行省份-中国银行时用
        String CITY = "";                      //银行所在城市 商业银行
        String XY = "";                        //信用 enum=YesNo
        String XYR = "";                       //信用人名称
        String DY = "";                        //抵押 enum=YesNo
        String DYW = "";                       //抵押物名称
        String ZY = "";                        //质押 enum=YesNo
        String ZYW = "";                       //质押物名称
        String BZ = "";                        //保证 enum=YesNo
        String BZR = "";                       //保证人名称
        String CREDITTYPE = "";                //信用种类 enum=CreditType
        String MONPAYAMT = "";                 //月均还款额
        String LINKMAN = "";                   //联系人姓名
        String LINKMANGENDER = "";             //联系人性别
        String LINKMANPHONE1 = "";              //联系人移动电话
        String LINKMANPHONE2 = "";              //联系人固定电话
        String APPRELATION = "";               //与申请人关系 enum=AppRelation
        String LINKMANADD = "";                //联系人住址
        String LINKMANCOMPANY = "";            //联系人工作单位
        String ACTOPENINGBANK_UD = "";         //开户行名称（电汇、网银——录入名称）
        String BANKACTNAME = "";               //户名
        String APPDATE = BusinessDate.getToday();                   //申请日期


        boolean ifErrClient = false;
        CachedRowSet crs = null;
        if (!sql1.equals("")) crs = manager.getRs(sql1);
        if (crs != null && crs.size() > 0) {
            crs.next();
            RECVERSION = crs.getString("RECVERSION");
            //if (NAME.compareTo(crs.getString("NAME")) != 0) ifErrClient = true;  //客户名称 desc=企业(个人)名称)
            NAME = crs.getString("NAME");                           //客户名称 desc=企业(个人)名称)
            CLIENTNO = crs.getString("CLIENTNO");                //客户号
            BIRTHDAY = crs.getString("BIRTHDAY");                //出生日期
            GENDER = crs.getString("GENDER");                    //性别 enum=Gender
            NATIONALITY = crs.getString("NATIONALITY");         //国籍
            MARRIAGESTATUS = crs.getString("MARRIAGESTATUS");   //婚姻状况 enum=MarriageStatus
            HUKOUADDRESS = crs.getString("HUKOUADDRESS");       //户籍所在地
            CURRENTADDRESS = crs.getString("CURRENTADDRESS");   //现住址
            COMPANY = crs.getString("COMPANY");                  //工作单位
            TITLE = crs.getString("TITLE");                      //职务 enum=Title
            QUALIFICATION = crs.getString("QUALIFICATION");     //职称 enum=Qualification
            EDULEVEL = crs.getString("EDULEVEL");                //学历 enum=EduLevel
            PHONE1 = crs.getString("PHONE1");                   //移动电话
            PHONE2 = crs.getString("PHONE2");                   //家庭电话
            PHONE3 = crs.getString("PHONE3");                   //办公电话
            CLIENTTYPE = crs.getString("CLIENTTYPE");           //客户性质 enum=ClientType1
            DEGREETYPE = crs.getString("DEGREETYPE");          //最高学位 enum=DegreeType
            COMADDR = crs.getString("COMADDR");                 //单位地址
            SERVFROM = crs.getString("SERVFROM");               //现单位工作时间
            RESIDENCEADR = crs.getString("RESIDENCEADR");       //户籍所在地(本外地) enum=ResidenceADR
            HOUSINGSTS = crs.getString("HOUSINGSTS");           //居住状况 enum=HousingSts
            HEALTHSTATUS = crs.getString("HEALTHSTATUS");       //健康状况 enum=HealthStatus
            MONTHLYPAY = crs.getString("MONTHLYPAY");           //个人月收入
            SLRYEVETYPE = crs.getString("SLRYEVETYPE");         //收入证明类型
            BURDENSTATUS = crs.getString("BURDENSTATUS");       //负担状况 enum=BurdenStatus
            SOCIALSECURITYID = crs.getString("SOCIALSECURITYID");  //员工卡号码
            ANNUALINCOME = crs.getString("ANNUALINCOME");       //年总收入
            ETPSCOPTYPE = crs.getString("ETPSCOPTYPE");           //行业性质
            SOCIALSECURITY = crs.getString("SOCIALSECURITY");   //社会保障 enum=SocialSecurity
            LIVEFROM = crs.getString("LIVEFROM");               //本地居住时间
            PC = crs.getString("PC");                            //住宅邮编
            COMPC = crs.getString("COMPC");                      //单位邮编
            RESDPC = crs.getString("RESDPC");                    //寄送地址邮编
            RESDADDR = crs.getString("RESDADDR");                //寄送地址
            EMAIL = crs.getString("EMAIL");                      //电子邮件
            SERVFROMMONTH = crs.getString("SERVFROMMONTH");     //现单位工作月数
            LIVEFROMMONTH = crs.getString("LIVEFROMMONTH");     //本地居住月数
            PNAME = crs.getString("PNAME");                      //配偶名称
            PIDTYPE = crs.getString("PIDTYPE");                  //配偶证件名称
            PID = crs.getString("PID");                          //配偶证件号码
            PCOMPANY = crs.getString("PCOMPANY");                //配偶工作单位
            PTITLE = crs.getString("PTITLE");                    //配偶职务 enum=Title
            PPHONE1 = crs.getString("PPHONE1");                  //配偶移动电话
            PPHONE3 = crs.getString("PPHONE3");                  //配偶办公电话
            PCLIENTTYPE = crs.getString("PCLIENTTYPE");         //配偶客户性质(单位性质) enum=ClientType1
            PSERVFROM = crs.getString("PSERVFROM");             //配偶现单位工作时间
            PMONTHLYPAY = crs.getString("PMONTHLYPAY");         //配偶个人月收入
            PLIVEFROM = crs.getString("PLIVEFROM");             //配偶本地居住时间
            PSERVFROMMONTH = crs.getString("PSERVFROMMONTH");  //配偶现单位工作月数
            PLIVEFROMMONTH = crs.getString("PLIVEFROMMONTH");  //配偶本地居住月数

            CHANNEL = crs.getString("CHANNEL");                  //销售单位(渠道名称)
            ADDR = crs.getString("ADDR");                         //配送地址
            TOTALNUM = crs.getString("TOTALNUM");                //购买数量
            TOTALAMT = crs.getString("TOTALAMT");                 //总金额
            RECEIVEAMT = crs.getString("RECEIVEAMT");            //已付金额
            APPAMT = crs.getString("APPAMT");                     //分期金额
            DIVID = crs.getString("DIVID");                       //分期期数
            APPSALARYRATE = crs.getString("APPSALARYRATE");      //分期付款与收入比率
            MONTHPAYSLRRATE = crs.getString("MONTHPAYSLRRATE");
            SALER = crs.getString("SALER");                       //销售人员
            COMMISSIONRATE = crs.getString("COMMISSIONRATE");
            SHCOMMISSIONRATE = crs.getString("SHCOMMISSIONRATE");
            COMPAYDATE = crs.getString("COMPAYDATE");
            SHARETYPE = crs.getString("SHARETYPE");
            COMMISSIONTYPE = crs.getString("COMMISSIONTYPE");
            PREPAY_CUTPAY_TYPE = crs.getString("PREPAY_CUTPAY_TYPE");        //首付款是否代收
            ACTOPENINGBANK = crs.getString("ACTOPENINGBANK");   //开户行 enum=Bank
            UNIONCHANNEL = crs.getString("UNIONCHANNEL");       //代理渠道
            PROVINCE = crs.getString("PROVINCE");               //银行省份
            CITY = crs.getString("CITY");                       //银行城市
            BANKACTNO = crs.getString("BANKACTNO");              //还款帐号
            XY = crs.getString("XY");                             //信用 enum=YesNo
            XYR = crs.getString("XYR");                           //信用人名称
            DY = crs.getString("DY");                             //抵押 enum=YesNo
            DYW = crs.getString("DYW");                           //抵押物名称
            ZY = crs.getString("ZY");                             //质押 enum=YesNo
            ZYW = crs.getString("ZYW");                           //质押物名称
            BZ = crs.getString("BZ");                             //保证 enum=YesNo
            BZR = crs.getString("BZR");                           //保证人名称
            CREDITTYPE = crs.getString("CREDITTYPE");            //信用种类 enum=CreditType
            MONPAYAMT = crs.getString("MONPAYAMT");              //月均还款额
            LINKMAN = crs.getString("LINKMAN");                   //联系人姓名
            LINKMANGENDER = crs.getString("LINKMANGENDER");      //联系人性别
            LINKMANPHONE1 = crs.getString("LINKMANPHONE1");      //联系人移动电话
            LINKMANPHONE2 = crs.getString("LINKMANPHONE2");      //联系人固定电话
            APPRELATION = crs.getString("APPRELATION");          //与申请人关系 enum=AppRelation
            LINKMANADD = crs.getString("LINKMANADD");            //联系人住址
            LINKMANCOMPANY = crs.getString("LINKMANCOMPANY");    //联系人工作单位
            ACTOPENINGBANK_UD = crs.getString("ACTOPENINGBANK_UD");    //开户行名称（电汇、网银——录入名称）
            BANKACTNAME = crs.getString("BANKACTNAME");          //户名
            //征信信息
            CCVALIDPERIOD = crs.getString("CCVALIDPERIOD");               //征信信息有效时段 ENUM=CCValidPeriod
            CCDYNUM = crs.getString("CCDYNUM");                     //抵押贷款数量(笔数)
            CCXYNUM = crs.getString("CCXYNUM");                     //信用贷款数量(笔数)
            CCCDNUM = crs.getString("CCCDNUM");                     //信用卡数量(笔数)
            CCDYAMT = crs.getString("CCDYAMT");                     //抵押贷款总贷款额／额度
            CCXYAMT = crs.getString("CCXYAMT");                     //CAP=信用贷款总贷款额／额度
            CCCDAMT = crs.getString("CCCDAMT");                     //CAP=信用卡总贷款额／额度
            CCDYNOWBAL = crs.getString("CCDYNOWBAL");                     //抵押贷款余额／额度
            CCXYNOWBAL = crs.getString("CCXYNOWBAL");                     //信用贷款余额／额度
            CCCDNOWBAL = crs.getString("CCCDNOWBAL");                     //信用卡余额／额度
            CCDYRPMON = crs.getString("CCDYRPMON");                     //抵押贷款当月还款额
            CCXYRPMON = crs.getString("CCXYRPMON");                     // 信用贷款当月还款额
            CCCDRPMON = crs.getString("CCCDRPMON");                     // 信用卡贷款当月还款额
            CCDYYEARRPTIME = crs.getString("CCDYYEARRPTIME");                  //抵押贷款12个月还款记录 (次数)
            CCXYYEARRPTIME = crs.getString("CCXYYEARRPTIME");                 // 信用贷款12个月还款记录 (次数)
            CCCDYEARRPTIME = crs.getString("CCCDYEARRPTIME");                 //信用卡12个月还款记录 (次数)
            CCDYNOOVERDUE = crs.getString("CCDYNOOVERDUE");                     // 抵押无逾期 enum=YesNo
            CCXYNOOVERDUE = crs.getString("CCXYNOOVERDUE");                     //信用无逾期 enum=YesNo
            CCCDNOOVERDUE = crs.getString("CCCDNOOVERDUE");                     // 信用卡无逾期 enum=YesNo
            CCDY1TIMEOVERDUE = crs.getString("CCDY1TIMEOVERDUE");                     //抵押逾期1-30天(1期)
            CCXY1TIMEOVERDUE = crs.getString("CCXY1TIMEOVERDUE");                     // 信用逾期1-30天(1期)
            CCCD1TIMEOVERDUE = crs.getString("CCCD1TIMEOVERDUE");                     //信用卡逾期1-30天(1期)
            CCDY2TIMEOVERDUE = crs.getString("CCDY2TIMEOVERDUE");                     //抵押逾期31-60天(2期)
            CCXY2TIMEOVERDUE = crs.getString("CCXY2TIMEOVERDUE");                     //信用逾期31-60天(2期)
            CCCD2TIMEOVERDUE = crs.getString("CCCD2TIMEOVERDUE");                     //信用卡逾期31-60天(2期)
            CCDYM3TIMEOVERDUE = crs.getString("CCDYM3TIMEOVERDUE");                     //抵押逾期60天以上(3期)
            CCXY3TIMEOVERDUE = crs.getString("CCXY3TIMEOVERDUE");                     //信用逾期60天以上(3期)
            CCCD3TIMEOVERDUE = crs.getString("CCCD3TIMEOVERDUE");                     //信用卡逾期60天以上(3期)
            ACAGE = crs.getString("ACAGE");                     //年龄要求 enum=YesNo
            ACWAGE = crs.getString("ACWAGE");                     //工资要求 enum=YesNo
            ACZX1 = crs.getString("ACZX1");                     //征信系统内最近一期无未还款记录 enum=YesNo
            ACZX2 = crs.getString("ACZX2");                     //征信系统内过去12个月最高逾期期数不超过2次 enum=YesNo
            ACZX3 = crs.getString("ACZX3");                     //征信系统内过去3个月最长逾期天数在60天以下 enum=YesNo
            ACFACILITY = crs.getString("ACFACILITY");                     //分期还款与收入比要求(低于30%) enum=YesNo
            ACRATE = crs.getString("ACRATE");                    //每月还款与收入比要求(低于60%) enum=YesNo
            CCRPTOTALAMT = crs.getString("CCRPTOTALAMT");              //总还款额
            CCDIVIDAMT = crs.getString("CCDIVIDAMT");

            ID = crs.getString("ID");                              //证件号码
            IDTYPE = crs.getString("IDTYPE");                     //证件名称
            APPDATE = crs.getString("APPDATE");                   //申请日期
            APPSTATUS = crs.getString("APPSTATUS");               //申请状态


            SID = crs.getString("SID");                            //合作商城编号
            ORDERNO = crs.getString("ORDERNO");                    //合作商城单号
            REQUESTTIME = crs.getString("REQUESTTIME");           //合作商城定单生成时间
        }
        CachedRowSet crs2 = null;
        if (!sql2.equals("")) crs2 = manager.getRs(sql2);


        NAME = (NAME == null) ? "" : NAME.trim();
        PNAME = (PNAME == null) ? "" : PNAME.trim();
        APPSTATUS = (APPSTATUS == null) ? "" : APPSTATUS;
        BIRTHDAY = (BIRTHDAY == null) ? "" : BIRTHDAY;
        ETPSCOPTYPE = (ETPSCOPTYPE == null) ? "" : ETPSCOPTYPE;

        SID = (SID == null) ? "" : SID.trim();                          //合作商城编号——“”：直接|001：海尔商城
        ORDERNO = (ORDERNO == null) ? "" : ORDERNO;
        REQUESTTIME = (REQUESTTIME == null) ? "" : REQUESTTIME.trim();
        CHANNEL = (CHANNEL == null) ? "" : CHANNEL.trim();
        TOTALNUM = (TOTALNUM == null) ? "" : TOTALNUM.trim();
        TOTALAMT = (TOTALAMT == null) ? "" : TOTALAMT.trim();
        PREPAY_CUTPAY_TYPE = (PREPAY_CUTPAY_TYPE == null) ? "0" : PREPAY_CUTPAY_TYPE.trim();
        //征信信息
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

        COMPAYDATE = (COMPAYDATE == null || StringUtils.isEmpty(COMPAYDATE)) ? (Level.getEnumItemName("COMPAYDATE", "1")) : COMPAYDATE;
        PROVINCE = (PROVINCE == null) ? "" : PROVINCE;
        String ACTOPENINGBANKUP = (ACTOPENINGBANK + "-" + UNIONCHANNEL);
        if (ACTOPENINGBANK.equals("313")) {
            if (CITY.equals(AppCommonEnum.CITY_GUANGZHOU)) {
                ACTOPENINGBANKUP = "2@" + ACTOPENINGBANKUP;
            } else if (CITY.equals(AppCommonEnum.CITY_SHENZHEN)) {
                ACTOPENINGBANKUP = "1@" + ACTOPENINGBANKUP;
            }
        } else if (ACTOPENINGBANK.equals("403")) {
            if (PROVINCE.equals(AppCommonEnum.PROVINCE_GUANGDONG)) {
                ACTOPENINGBANKUP = "1@" + ACTOPENINGBANKUP;
            } else if (PROVINCE.equals(AppCommonEnum.PROVINCE_HENAN)) {
                ACTOPENINGBANKUP = "2@" + ACTOPENINGBANKUP;
            }
        }

        String readonly_input = "readonly";
        String submit = "class='btn_2k3'";
        String title = "个人消费分期付款申请书";

        if (ifErrClient) {
            String mess = "客户信息错误，此证件已被其他客户使用！";
            mess = "<li class='error_message_li'>" + mess.trim() + "</li>";
            session.setAttribute("msg", mess);
            response.sendRedirect("../showinfo.jsp");
        }
%>
<html>
<head id="head1">
    <title>消费信贷</title>
    <link href="../../../css/platform.css" rel="stylesheet" type="text/css">
    <meta http-equiv="Content-Type" content="text/html; charset=GBK">
    <style type="text/css">
        <!--
        body {
            margin-top: 0px;
            margin-left: 0px;
            margin-right: 0px;
        }

        -->
    </style>
</head>
<SCRIPT type="text/javascript">
    var hkey_root,hkey_path,hkey_key;
    hkey_root = "HKEY_CURRENT_USER";

    var hk_pageHead,hk_pageFoot;
    <!--地址的写法很严格的用双斜杠-->
    hkey_path = "\\Software\\Microsoft\\Internet Explorer\\PageSetup";
    //设置网页打印的页眉页脚为空
    function pagesetup_null() {
        try {
            var RegWsh = new ActiveXObject("WScript.Shell");
            hkey_key = "\\header";
            hk_pageHead = RegWsh.RegRead(hkey_root + hkey_path + hkey_key);
            RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, "");
            hkey_key = "\\footer";
            hk_pageFoot = RegWsh.RegRead(hkey_root + hkey_path + hkey_key);
            RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, "");
        } catch(e) {
            //alert('需要运行运行Activex才能进行打印设置。');
        }
    }
    //设置网页打印的页眉页脚为默认值
    function pagesetup_default() {
        try {
            var RegWsh = new ActiveXObject("WScript.Shell");
            hkey_key = "\\header";
            //RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, "&w&b页码,&p/&P");
            RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, hk_pageHead);
            hkey_key = "\\footer";
            //RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, "&u&b&d");
            RegWsh.RegWrite(hkey_root + hkey_path + hkey_key, hk_pageFoot);
        } catch(e) {
        }
    }


</SCRIPT>
<!--media=print 这个属性说明可以在打印时有效-->
<!--希望打印时不显示的内容设置class="Noprint"样式-->
<!--希望人为设置分页的位置设置class="PageNext"样式-->
<style media=print>
    .Noprint {
        display: none;
    }

    .PageNext {
        page-break-after: always;
    }

    table {
        border-color: #000 !important;
        color: #000 !important;
    }

    tr {
        border-color: #000 !important;
        color: #000 !important;
    }

    td {
        border-color: #000 !important;
        color: #000 !important;
    }
</style>

<body>
<table width="100%" height="100%" border="0" cellspacing="0" cellpadding="0">
<tr class='page_form_tr'>
<td align="center" valign="middle">
<table height="325" border="0" style="margin-top:10px;" align="center"
       cellspacing="0" cellpadding="0" bordercolor="#816A82" bgcolor="#ffffff" width="678">
<tr align="left">
    <td height="30" bgcolor="#A4AEB5">
        <img src="../../../images/formtile1.gif" style="margin-left:4px;" height="22px" width="22px" align="absmiddle">
        <font size="2" color="#FFFFFF"><b>海尔集团财务有限责任公司个人消费分期付款申请书</b></font>
    </td>
</tr>
<tr align="center">
<td height="260" valign="middle">
<table width="100%" height="100%" cellspacing="0" cellpadding="0" border="0">
<tr class='page_form_tr'>
<td width="20">&nbsp;</td>
<td align="center" valign="middle" width="638">
<script src='../../../js/xf/main.js' type='text/javascript'></script>
<script src='../../../js/xf/check.js' type='text/javascript'></script>
<script src='../../../js/xf/meizzDate.js' type='text/javascript'></script>
<script src='../../../js/xf/checkID2.js' type='text/javascript'></script>
<script src='../../../js/xf/pagebutton.js' type='text/javascript'></script>
<script src='../../../js/xf/xfutil.js' type='text/javascript'></script>

<script src='../../../js/xf/checkReName.js' type='text/javascript'></script>

<form id='winform' method='post' action='./application_save.jsp'>
<table class='page_form_regTable' id='page_form_table' width="638" cellspacing="1" cellpadding="0" border=0>
<col width="69"/>
<col width="110"/>
<col width="160"/>
<col width="31"/>
<col width="95"/>
<col width="160"/>
<col width="13"/>
<tr class='page_form_tr'>
    <td colspan="7" align="center" class="page_form_List_title">海尔集团财务有限责任公司个人消费分期付款申请书</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="4">
        <input type="hidden" name="APPACTFLAG" value="1"><%--执行动作标志：1、正常，2、退回，3、作废--%>
        <input type="hidden" name="CLIENTNO" value="<%=CLIENTNO%>">
        <input type="hidden" name="APPNO" value="<%=APPNO%>" id="APPNO">
        <input type="hidden" name="APPSTATUS" value="1" id="APPSTATUS">
        <input type="hidden" name="RECVERSION" value="<%=RECVERSION%>" id="RECVERSION"/>
        <input type="hidden" name="SID" value="<%=SID%>">
        <input type="hidden" name="ORDERNO" value="<%=ORDERNO%>">
        <input type="hidden" name="REQUESTTIME" value="<%=REQUESTTIME%>">
        申请日期:<%
        out.print(APPDATE == null ? "" : APPDATE);
    %>
        &nbsp;申请状态:<%=(APPSTATUS == null || APPSTATUS.equals("")) ? "新申请" : Level.getEnumItemName("AppStatus", APPSTATUS)%>
    </td>
    <td align="right">申请单号:&nbsp;<%
        String XFAPPNO = "";
        if (!APPNO.equals("")) {
            XFAPPNO = APPNO;
        }
    %>
    </td>
    <td>
        <input <%=readonly%> class="page_form_text" type="text" name="XFAPPNO" id="XFAPPNO"
                             value="<%=XFAPPNO%>" maxlength="22">
    </td>
    <td>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td rowspan="12" class="page_left_table_title" nowrap>申请人情况</td>
    <td class="page_form_title_td" nowrap>&nbsp;姓&nbsp;&nbsp;名：</td>
    <td class="page_form_td" nowrap><input type="text" <%=readonly%> name="NAME"
                                           value="<%=NAME==null?"":NAME%>"
                                           class="page_form_text" maxlength="40"></td>
    <td class="page_form_td" nowrap>&nbsp;</td>
    <td class="page_form_title_td" nowrap>&nbsp;性&nbsp;&nbsp;别：</td>
    <td colspan="2" nowrap class="page_form_td"><%=Level.radioHere("GENDER", "Gender", GENDER)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;手&nbsp;&nbsp;机：</td>
    <td class="page_form_td" nowrap><input type="text" <%=readonly%> name="PHONE1"
                                           value="<%=PHONE1==null?"":PHONE1%>"
                                           class="page_form_text" maxlength="15"></td>
    <td class="page_form_td" nowrap>&nbsp;</td>
    <td class="page_form_title_td" nowrap>&nbsp;社保编号：</td>
    <td class="page_form_td" nowrap><input type="text" <%=readonly%> name="SOCIALSECURITYID"
                                           value="<%=SOCIALSECURITYID==null?"":SOCIALSECURITYID%>"
                                           class="page_form_text" maxlength="28">

    </td>
    <td class="page_form_td" nowrap><span style="color:red">*</span></td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;身份证件名称：</td>
    <td colspan="2" nowrap class="page_form_td">
        <%=Level.levelHere("IDTYPE", "IDType", IDTYPE)%>
    </td>
    <td class="page_form_title_td" nowrap>&nbsp;证件号码：</td>
    <td class="page_form_td" nowrap><input type="text" <%=readonly%> name="ID" id="ID"
                                           value="<%=ID==null?"":ID%>" class="page_form_text"
                                           maxlength="18"></td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;出生日期：</td>
    <td class="page_form_td" nowrap><input type="text" <%=readonly_input%> name="BIRTHDAY"
                                           value="<%=BIRTHDAY.equals("")?"":BIRTHDAY%>"
    <%--value="<%=BIRTHDAY.equals("")?"":DBUtil.to_Date(BIRTHDAY)%>"--%>
                                           class="page_form_text"></td>
    <td class="page_form_td" nowrap><input type="button" value="…" class="page_form_refbutton"
                                           onClick="setday(this,winform.BIRTHDAY)"></td>
    <td class="page_form_title_td" nowrap>&nbsp;健康状况：</td>
    <td colspan="2" nowrap class="page_form_td"><%=Level.radioHere("HEALTHSTATUS", "HealthStatus", HEALTHSTATUS)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;婚姻状况：</td>
    <td colspan="2" nowrap class="page_form_td"><%=Level.radioHere("MARRIAGESTATUS", "MarriageStatus", MARRIAGESTATUS)%>
    </td>
    <td class="page_form_title_td" nowrap>&nbsp;是否有子女：</td>
    <td colspan="2" nowrap class="page_form_td"><%=Level.radioHere("BURDENSTATUS", "BurdenStatus", BURDENSTATUS)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;户籍所在地：</td>
    <td colspan="5" class="page_form_td" nowrap><%=Level.radioHere("RESIDENCEADR", "ResidenceADR", RESIDENCEADR)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;居住情况：</td>
    <td colspan="4" class="page_form_td">
        <%=Level.radioHere("HOUSINGSTS", "HousingSts", HOUSINGSTS)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;家庭住址：</td>
    <td colspan="4" class="page_form_td">
        <input type="text" <%=readonly%> name="CURRENTADDRESS"
               value="<%=CURRENTADDRESS==null?"":CURRENTADDRESS%>"
               class="page_form_text" style="width:478px" maxlength="40"></td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;住宅邮编：</td>
    <td colspan="2" class="page_form_td"><input type="text" <%=readonly%> name="PC"
                                                value="<%=PC==null?"":PC%>"
                                                class="page_form_text" maxlength="6"></td>
    <td class="page_form_title_td" nowrap>&nbsp;电子邮箱：</td>
    <td class="page_form_td">
        <input type="text" <%=readonly%> name="EMAIL"
               value="<%=EMAIL==null?"":EMAIL%>"
               class="page_form_text" maxlength="40"></td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;家庭电话：</td>
    <td colspan="2" class="page_form_td"><input type="text" <%=readonly%> name="PHONE2"
                                                value="<%=PHONE2==null?"":PHONE2%>"
                                                class="page_form_text" maxlength="15"></td>
    <td class="page_form_title_td" nowrap>&nbsp;本地居住时间：</td>
    <td colspan="2" class="page_form_td">
        <input type="text" <%=readonly%> name="LIVEFROM"
               value="<%=LIVEFROM==null?"":LIVEFROM%>"
               class="page_form_text" style="width:70px" maxlength="3">年
        <input type="text" <%=readonly%> name="LIVEFROMMONTH"
               value="<%=LIVEFROMMONTH==null?"":LIVEFROMMONTH%>"
               class="page_form_text" style="width:70px" maxlength="2">月
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;受教育程度：</td>
    <td colspan="5" class="page_form_td" nowrap><%=Level.radioHere("EDULEVEL", "EduLevel", EDULEVEL)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;职&nbsp;&nbsp;称：</td>
    <td colspan="5" class="page_form_td"
        nowrap><%=Level.radioHere("QUALIFICATION", "Qualification", QUALIFICATION)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td rowspan="9" class="page_left_table_title">工作资料：</td>
    <td class="page_form_title_td" nowrap>&nbsp;工作单位：</td>
    <td colspan="4" class="page_form_td">
        <input type="text" <%=readonly%> name="COMPANY"
               value="<%=COMPANY==null?"":COMPANY%>"
               class="page_form_text" style="width:478px" maxlength="40">
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;单位电话：</td>
    <td colspan="2" class="page_form_td">
        <input type="text" <%=readonly%> name="PHONE3"
               value="<%=PHONE3==null?"":PHONE3%>"
               class="page_form_text" maxlength="15">
    </td>
    <td class="page_form_title_td" nowrap>&nbsp;单位邮编：</td>
    <td class="page_form_td">
        <input type="text" <%=readonly%> name="COMPC"
               value="<%=COMPC==null?"":COMPC%>"
               class="page_form_text" maxlength="6">
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;单位地址：</td>
    <td colspan="4" class="page_form_td">
        <input type="text" <%=readonly%> name="COMADDR"
               value="<%=COMADDR==null?"":COMADDR%>"
               class="page_form_text" style="width:478px" maxlength="40">
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;单位性质：</td>
    <td colspan="5" class="page_form_td" nowrap><%=Level.radioHere("CLIENTTYPE", "ClientType1", CLIENTTYPE)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;行业性质：</td>
    <td colspan="2" class="page_form_td" nowrap><%=Level.levelHere("ETPSCOPTYPE", "EtpScopType1", ETPSCOPTYPE)%>
    </td>
    <td class="page_form_title_td" nowrap>&nbsp;职&nbsp;&nbsp;务：</td>
    <td colspan="2" class="page_form_td" nowrap><%=Level.levelHere("TITLE", "Title", TITLE)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;现单位工作时间：</td>
    <td colspan="5" class="page_form_td">
        <input type="text" <%=readonly%> name="SERVFROM"
               value="<%=SERVFROM==null?"":SERVFROM%>"
               class="page_form_text" style="width:70px" maxlength="3">年
        <input type="text" <%=readonly%> name="SERVFROMMONTH"
               value="<%=SERVFROMMONTH==null?"":SERVFROMMONTH%>"
               class="page_form_text" style="width:70px" maxlength="2">月

    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;个人月收入：</td>
    <td class="page_form_td">
        <input type="text" <%=readonly%> name="MONTHLYPAY"
               value="<%=MONTHLYPAY==null?"":MONTHLYPAY%>" onblur="blurMonthpay(this)"
               class="page_form_text" maxlength="12"></td>
    <td class="page_form_td">元</td>
    <td class="page_form_title_td" nowrap>&nbsp;年收入总额：</td>
    <td class="page_form_td">
        <input type="text" <%=readonly%> name="ANNUALINCOME"
               value="<%=ANNUALINCOME==null?"":ANNUALINCOME%>"
               class="page_form_text" maxlength="12"></td>
    <td colspan="2" class="page_form_td">元</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;收入证明类型：</td>
    <td colspan="5" class="page_form_td">
        <input type="text" <%=readonly%> name="SLRYEVETYPE"
               value="<%=SLRYEVETYPE==null?"":SLRYEVETYPE%>"
               class="page_form_text" maxlength="40">
        <span style="color:red">*</span>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;建立社会福利<br>&nbsp;保障制度情况：</td>
    <td colspan="5" class="page_form_td"
        nowrap><%=Level.checkHere("SOCIALSECURITY", "SocialSecurity", SOCIALSECURITY)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>

<tr class='page_form_tr' onClick="showList(1);" id="HT2">
    <td class="page_slide_table_title" colspan="7" height="18" id="HT1" title="点击收缩" onMouseOver="mOvr(this);"
        onMouseOut="mOut(this);">
        若个人还款压力大，需配偶共同还款，请填写配偶基本信息 -- ↓<font color="red">(点击展开)</font>
    </td>
</tr>
<tr>
    <td colspan="7" bgcolor="#3366FF" style="margin:0; padding:0" width="100%">
        <table class="page_form_table_slide" width="100%" id="HA1" style="display:none;border:1px dashed #FDF8DF"
               cellspacing="0" cellpadding="0">
            <COL width=66>
            <COL width=111>
            <COL width=160>
            <COL width=31>
            <COL width=96>
            <COL width=160>
            <COL width=13>
            <tr class='page_form_tr'>
                <td rowspan="8" class="page_left_table_title" nowrap>配偶基本情况(仅在配偶为共同还款人情况下填列)</td>
                <td class="page_form_title_td" nowrap>&nbsp;配偶姓名：</td>
                <td colspan="5" class="page_form_td" nowrap><input type="text" <%=readonly%> name="PNAME"
                                                                   value="<%=PNAME==null?"":PNAME%>"
                                                                   class="page_form_text" maxlength="40"></td>
            </tr>
            <tr class='page_form_tr'>
                <td class="page_form_title_td" nowrap>配偶身份证件名称：</td>
                <td class="page_form_td" colspan="2" nowrap><%=Level.levelHere("PIDTYPE", "IDType", PIDTYPE)%>
                </td>
                <td class="page_form_title_td" nowrap>&nbsp;配偶证件号码：</td>
                <td class="page_form_td">
                    <input type="text" <%=readonly%> name="PID" id="PID" value="<%=PID==null?"":PID%>"
                           onblur="if(this.value.Trim()!=''&&document.getElementById('PNAME').value.Trim()==''){alert('配偶姓名不能为空！');document.getElementById('PNAME').focus();}else checkIDCard(document.getElementById('PIDTYPE'),this);"
                           class="page_form_text" maxlength="18"></td>
                <td class="page_form_td">&nbsp;</td>
            </tr>
            <tr class='page_form_tr'>
                <td class="page_form_title_td" nowrap>&nbsp;工作单位：</td>
                <td colspan="4" class="page_form_td">
                    <input type="text" <%=readonly%> name="PCOMPANY"
                           value="<%=PCOMPANY==null?"":PCOMPANY%>"
                           class="page_form_text" style="width:510px" maxlength="40"></td>
                <td class="page_form_td">&nbsp;</td>
            </tr>
            <tr class='page_form_tr'>
                <td class="page_form_title_td" nowrap>&nbsp;单位电话：</td>
                <td colspan="5" class="page_form_td">
                    <input type="text" <%=readonly%> name="PPHONE3"
                           value="<%=PPHONE3==null?"":PPHONE3%>"
                           class="page_form_text" maxlength="15">
                </td>
            </tr>
            <tr class='page_form_tr'>
                <td class="page_form_title_td" nowrap>&nbsp;单位性质：</td>
                <td colspan="5" class="page_form_td"
                    nowrap><%=Level.radioHere("PCLIENTTYPE", "ClientType1", PCLIENTTYPE)%>
                </td>
            </tr>
            <tr class='page_form_tr'>
                <td class="page_form_title_td" nowrap>&nbsp;职&nbsp;&nbsp;务：</td>
                <td colspan="5" class="page_form_td" nowrap><%=Level.levelHere("PTITLE", "Title", PTITLE)%>
                </td>
            </tr>
            <tr class='page_form_tr'>
                <td class="page_form_title_td" nowrap>&nbsp;现单位工作时间：</td>
                <td colspan="2" class="page_form_td">
                    <input type="text" <%=readonly%> name="PSERVFROM"
                           value="<%=PSERVFROM==null?"":PSERVFROM%>"
                           class="page_form_text" style="width:70px" maxlength="3">年
                    <input type="text" <%=readonly%> name="PSERVFROMMONTH"
                           value="<%=PSERVFROMMONTH==null?"":PSERVFROMMONTH%>"
                           class="page_form_text" style="width:70px" maxlength="2">月

                </td>
                <td class="page_form_title_td" nowrap>&nbsp;本地居住时间：</td>
                <td colspan="2" class="page_form_td">
                    <input type="text" <%=readonly%> name="PLIVEFROM"
                           value="<%=PLIVEFROM==null?"":PLIVEFROM%>"
                           class="page_form_text" style="width:70px" maxlength="3">年
                    <input type="text" <%=readonly%> name="PLIVEFROMMONTH"
                           value="<%=PLIVEFROMMONTH==null?"":PLIVEFROMMONTH%>"
                           class="page_form_text" style="width:70px" maxlength="2">月

                </td>
            </tr>
            <tr class='page_form_tr'>
                <td class="page_form_title_td" nowrap>&nbsp;个人电话：</td>
                <td class="page_form_td">
                    <input type="text" <%=readonly%> name="PPHONE1"
                           value="<%=PPHONE1==null?"":PPHONE1%>"
                           class="page_form_text" maxlength="15"></td>
                <td class="page_form_td">&nbsp;</td>
                <td class="page_form_title_td" nowrap>&nbsp;个人月收入：</td>
                <td class="page_form_td">
                    <input type="text" <%=readonly%> name="PMONTHLYPAY"
                           value="<%=PMONTHLYPAY==null?"":PMONTHLYPAY%>"
                           class="page_form_text" maxlength="12">
                </td>
                <td class="page_form_td">元</td>
            </tr>
        </table>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td colspan="7" width="100%">
        <table id="tabCommContent" class='page_form_regTable' cellpadding="0" cellspacing="0" border="1">
            <tr>
                <td height="30px" class="page_left_table_title" style="text-align:left;" colspan="6">
                    拟购商品情况<span style="color:red">&nbsp;(自上往下逐行录入)</span>
                </td>
            </tr>
            <tr height="25px">
                <td style="display:none">1</td>
                <td width="200" class="page_form_title_th">商品名称及型号</td>
                <td width="150" class="page_form_title_th">商品类型</td>
                <td width="80" class="page_form_title_th">数量(台/套)</td>
                <td width="110" class="page_form_title_th">价格(元)</td>
                <td width="110" class="page_form_title_th">首付金额(元)</td>
            </tr>
            <%
                String noCOMMNO = "";
                String noCOMMNMTYPE = "";
                String noNUM = "";
                String noAMT = "";
                String noRECEIVEAMT = "";
                String noAPPTYPE = "";
                int rowCnt = 1;
                if (crs2 != null && crs2.size() > 0) {
                    while (crs2.next()) {
                        String strRowCnt = String.valueOf(rowCnt);
                        noCOMMNO = "COMMNO" + strRowCnt;
                        noCOMMNMTYPE = "COMMNMTYPE" + strRowCnt;
                        noNUM = "NUM" + strRowCnt;
                        noAMT = "AMT" + strRowCnt;
                        noRECEIVEAMT = "RECEIVEAMT" + strRowCnt;
                        noAPPTYPE = "APPTYPE" + strRowCnt;
                        String vaCOMMNO = crs2.getString("COMMNO");
                        String vaCOMMNMTYPE = crs2.getString("COMMNMTYPE");
                        String vaNUM = crs2.getString("NUM");
                        String vaAMT = crs2.getString("AMT");
                        String vaRECEIVEAMT = crs2.getString("RECEIVEAMT");
                        String vaAPPTYPE = crs2.getString("APPTYPE");
            %>
            <tr class='page_form_tr'>
                <td style="display:none;">
                    <input type="text" name="noCOMMNO" id="<%=noCOMMNO%>" value="<%=vaCOMMNO%>"/>
                </td>
                <td class="page_form_td">
                    <input maxlength="500" <%=readonly%> onkeyup="checkComm(this)"
                           name="noCOMMNMTYPE" id="<%=noCOMMNMTYPE%>" style="width:190px;"
                           value="<%=vaCOMMNMTYPE%>"/>
                </td>
                <td class="page_form_td">
                    <%=Level.radioHere(noAPPTYPE, "AppType", vaAPPTYPE)%>
                </td>
                <td class="page_form_td">
                    <input maxlength="10" <%=readonly%> onkeyup="checkComm(this)" id="<%=noNUM%>"
                           onblur="countTotalNum(this,'TOTALNUM')"
                           style="ime-mode:disabled;width:70px;"
                           name="noNUM" value="<%=vaNUM%>"/>
                </td>
                <td class="page_form_td">
                    <input maxlength="12" <%=readonly%> onkeyup="checkComm(this)" id="<%=noAMT%>"
                           onblur="countTotalNum(this,'TOTALAMT')"
                           style="ime-mode:disabled;width:100px;"
                           name="noAMT" value="<%=vaAMT%>"/>
                </td>
                <td class="page_form_td">
                    <input maxlength="12" <%=readonly%> onkeyup="checkComm(this)" id="<%=noRECEIVEAMT%>"
                           onblur="countTotalNum(this,'RECEIVEAMT')"
                           style="ime-mode:disabled;width:100px;"
                           name="noRECEIVEAMT" value="<%=vaRECEIVEAMT%>"/>
                </td>
            </tr>
            <%
                        rowCnt++;
                    }
                }
                String strRowCnt = String.valueOf(rowCnt);
                noCOMMNO = "COMMNO" + strRowCnt;
                noCOMMNMTYPE = "COMMNMTYPE" + strRowCnt;
                noNUM = "NUM" + strRowCnt;
                noAMT = "AMT" + strRowCnt;
                noRECEIVEAMT = "RECEIVEAMT" + strRowCnt;
                noAPPTYPE = "APPTYPE" + strRowCnt;
            %>
            <tr>
                <td style="display:none;">
                    <input type="text" name="noCOMMNO" id="<%=noCOMMNO%>" value="<%=strRowCnt%>"/>
                </td>
                <td class="page_form_td">
                    <input maxlength="500" <%=readonly%> onkeyup="checkComm(this)"
                           id="<%=noCOMMNMTYPE%>" style="width:190px;"
                           name="noCOMMNMTYPE" value=""/>
                </td>
                <td class="page_form_td">
                    <%=Level.radioHere(noAPPTYPE, "AppType", "")%>
                </td>
                <td class="page_form_td">
                    <input maxlength="12" <%=readonly%> onkeyup="checkComm(this)" id="<%=noNUM%>"
                           onblur="countTotalNum(this,'TOTALNUM')"
                           style="ime-mode:disabled;width:70px;"
                           name="noNUM" value=""/>
                </td>
                <td class="page_form_td">
                    <input maxlength="12" <%=readonly%> onkeyup="checkComm(this)" id="<%=noAMT%>"
                           onblur="countTotalNum(this,'TOTALAMT')"
                           style="ime-mode:disabled;width:100px;"
                           name="noAMT" value=""/>
                </td>
                <td class="page_form_td">
                    <input maxlength="12" <%=readonly%> onkeyup="checkComm(this)" id="<%=noRECEIVEAMT%>"
                           onblur="countTotalNum(this,'RECEIVEAMT')"
                           style="ime-mode:disabled;width:100px;"
                           name="noRECEIVEAMT" value=""/>
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr class='page_form_tr'>
    <td colspan="2" class="page_form_title_td">销售单位（名称）：</td>
    <td colspan="4" class="page_form_td">
        <textarea name="CHANNEL" <%=readonly%> class="page_form_textfield" style="width:478px" rows="3"
                  onkeyup="limitTextarea(this)"
                  onchange="limitTextarea(this)"
                  onpropertychange="limitTextarea(this)"><%=CHANNEL == null ? "" : CHANNEL.trim()%>
        </textarea>
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="2" class="page_form_title_td" nowrap>拟购商品使用地址：</td>
    <td colspan="4" class="page_form_td">
        <input type="text" <%=readonly%> name="ADDR" id="ADDR"
               value="<%=ADDR==null?"":ADDR%>"
               class="page_form_text" style="width:478px" maxlength="40">
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="2" class="page_form_title_td" nowrap>&nbsp;拟购商品总数量：</td>
    <td colspan="1" class="page_form_td">
        <input type="text" readonly="true" name="TOTALNUM"
               value="<%=TOTALNUM==null?"":TOTALNUM%>"
               class="page_form_text" maxlength="10"></td>
    <td class="page_form_td">台/套</td>
    <td class="page_form_title_td" nowrap>&nbsp;商品总金额：</td>
    <td class="page_form_td">
        <input type="text" readonly="true" name="TOTALAMT"
               value="<%=TOTALAMT==null?"":TOTALAMT%>"
               class="page_form_text" maxlength="12" onKeyUp="countAppAmt()">
    </td>
    <td class="page_form_td">元</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="2" class="page_form_title_td" nowrap>&nbsp;销售员：</td>
    <td colspan="1" class="page_form_td">
        <input type="text" <%=readonly%> name="SALER"
               value="<%=SALER==null?"":SALER%>"
               class="page_form_text" maxlength="12">
    </td>
    <td class="page_form_td"><span style="color:red">*</span></td>
    <td class="page_form_title_td">&nbsp;总首付款：</td>
    <td class="page_form_td">
        <input type="text" name="RECEIVEAMT"
               value="<%=RECEIVEAMT==null?"":RECEIVEAMT%>"
               class="page_form_text" maxlength="12" onblur="countAppAmtAndRate(this)"
                ></td>
    <td class="page_form_td">元</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="2" class="page_form_title_td" nowrap>&nbsp;首付款是否代收：</td>
    <td colspan="5" class="page_form_td">
        <%=Level.radioHere("PREPAY_CUTPAY_TYPE", "YesNo", PREPAY_CUTPAY_TYPE)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td rowspan="6" class="page_left_table_title">申请分期情况</td>
    <td class="page_form_title_td" nowrap>申请分期总金额￥：</td>
    <td class="page_form_td">
        <input type="text" readonly="true" name="APPAMT" id="APPAMT"
               value="<%=APPAMT==null?"":APPAMT%>"
               class="page_form_text" maxlength="15"></td>
    <td class="page_form_td">元</td>
    <td class="page_form_title_td" nowrap>&nbsp;分期期限：</td>
    <td colspan="2" nowrap class="page_form_td"><%=Level.radioHere("DIVID", "Divid", DIVID)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;还款方式：
        <input type="hidden" id="UNIONCHANNEL" name="UNIONCHANNEL"
               value="<%=UNIONCHANNEL==null?"0":UNIONCHANNEL%>"/>
        <input type="hidden" id="ACTOPENINGBANK" name="ACTOPENINGBANK"
               value="<%=ACTOPENINGBANK==null?"0":ACTOPENINGBANK%>"/>
        <input type="hidden" id="CITY" name="CITY" value="<%=CITY==null?"":CITY%>"/>
    </td>
    <td colspan="2" nowrap class="page_form_td">
        <%=Level.levelHereNew("ACTOPENINGBANKUP", "Bank4App", "UnionPay", ACTOPENINGBANKUP)%>
    </td>
    <td class="page_form_title_td" nowrap>帐户名：</td>
    <td nowrap class="page_form_td">
        <input maxlength="40" <%=readonly%> name="BANKACTNAME" id="BANKACTNAME"
               class="page_form_text" value="<%=BANKACTNAME==null?NAME:BANKACTNAME%>"/>
    </td>
    <td class="page_form_td"></td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;还款账号：</td>
    <td class="page_form_td">
        <input type="text" <%=readonly%> name="BANKACTNO"
               value="<%=BANKACTNO==null?"":BANKACTNO%>"
               class="page_form_text" maxlength="30">
    </td>
    <td class="page_form_td">&nbsp;</td>
    <td class="page_form_title_td" nowrap><span style="display:none;" id="lblPROVINCE">选择省份：</span></td>
    <td nowrap class="page_form_td">
        <%=Level.levelHere("PROVINCE", "PROVINCE", PROVINCE)%>
    </td>
    <td style="display:none;"><span id="BANK_UD1">&nbsp;开户行名称：</span>
        <input type="text" id="BANK_UD2" style="display:none" <%=readonly%>
               name="ACTOPENINGBANK_UD"
               value="<%=ACTOPENINGBANK_UD==null?"":ACTOPENINGBANK_UD%>"
               class="page_form_text" maxlength="40"></td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap><span>&nbsp;分摊方式：</span></td>
    <td nowrap class="page_form_td">
        <%=Level.levelHere("SHARETYPE", "SHARETYPE", SHARETYPE)%>
    </td>
    <td class="page_form_td">&nbsp;</td>
    <td class="page_form_title_td" nowrap>&nbsp;还款日：</td>
    <td class="page_form_td">
        <input type="text" readonly="true" name="COMPAYDATE"
               value="<%=COMPAYDATE%>" class="page_form_text">
    </td>
    <td class="page_form_td" nowrap>日</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;客户手续费率：</td>
    <td class="page_form_td">
        <input type="text" <%=readonly%> name="COMMISSIONRATE"
               value="<%=COMMISSIONRATE==null?"":COMMISSIONRATE%>" onblur="blurcountMONTHPAYSLRRATE()"
               class="page_form_text" maxlength="13">
    </td>
    <td class="page_form_td">‰</td>
    <td class="page_form_title_td" nowrap><span>&nbsp;商户手续费率：</span></td>
    <td nowrap class="page_form_td"><input type="text" <%=readonly%>
                                           name="SHCOMMISSIONRATE"
                                           value="<%=SHCOMMISSIONRATE==null?"":SHCOMMISSIONRATE%>"
                                           class="page_form_text" maxlength="13"></td>
    <td class="page_form_td">%</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap><span>&nbsp;客户手续费缴纳方式：</span></td>
    <td nowrap class="page_form_td">
        <%=Level.levelHere("COMMISSIONTYPE", "COMMISSIONTYPE", COMMISSIONTYPE)%>
    </td>
    <td colspan="4">&nbsp;</td>
</tr>

<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td rowspan="4" class="page_left_table_title">现有信用使用情况</td>
    <td class="page_form_title_td" nowrap>&nbsp;信用种类：</td>
    <td colspan="5" class="page_form_td" nowrap><%=Level.checkHere("CREDITTYPE", "CreditType", CREDITTYPE)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;现有贷款月均还款额：</td>
    <td class="page_form_td">
        <input type="text" readonly="true" name="MONPAYAMT"
               value="<%=MONPAYAMT==null?"":MONPAYAMT%>"
               class="page_form_text" maxlength="12"></td>
    <td class="page_form_td">元</td>
    <td class="page_form_title_td" nowrap>&nbsp;分期付款与收入比率：</td>
    <td class="page_form_td">
        <input type="text" readonly="true" name="APPSALARYRATE"
               value="<%=APPSALARYRATE==null?"":APPSALARYRATE%>"
               class="page_form_text" maxlength="8"></td>
    <td class="page_form_td">%</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;该笔分期还款额：</td>
    <td class="page_form_td">
        <input type="text" readonly="true" name="CCDIVIDAMT"
               value="<%=CCDIVIDAMT==null?"":CCDIVIDAMT%>"
               class="page_form_text" maxlength="12"></td>
    <td class="page_form_td">元</td>
    <td class="page_form_title_td" nowrap>&nbsp;每月还款与收入比率：</td>
    <td class="page_form_td">
        <input type="text" readonly="true" name="MONTHPAYSLRRATE"
               value="<%=MONTHPAYSLRRATE==null?"":MONTHPAYSLRRATE%>"
               class="page_form_text" maxlength="8"></td>
    <td class="page_form_td">%</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;月总还款额：</td>
    <td class="page_form_td">
        <input type="text" readonly name="CCRPTOTALAMT"
               value="<%=CCRPTOTALAMT==null?"":CCRPTOTALAMT%>"
               class="page_form_text" maxlength="12"></td>
    <td colspan="4" class="page_form_td">元</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td rowspan="2" class="page_left_table_title">征信机构<br/>
        信贷纪录
    </td>
    <td class="page_form_title_td" nowrap>
        信用记录期间：
    </td>
    <td colspan="5">
        <%=Level.radioHere("CCVALIDPERIOD", "CCValidPeriod", CCVALIDPERIOD)%>
    </td>
</tr>
<tr>
    <td colspan="7" width="600">
        <table class='page_form_regTable' width="100%" cellpadding="0" cellspacing="0" border="1">
            <col width="150"/>
            <col width="150"/>
            <col width="150"/>
            <col width="150"/>
            <tr>
                <td class="page_form_title_td">&nbsp;</td>
                <td class="page_form_title_td">抵押贷款</td>
                <td class="page_form_title_td">信用贷款</td>
                <td class="page_form_title_td">信用卡</td>
            </tr>
            <tr>
                <td class="page_form_title_td">贷款(卡)数量：</td>
                <td>
                    <input type="text" <%=readonly%> name="CCDYNUM" onblur="blurChkDb(this)"
                           value="<%=CCDYNUM==null?"":CCDYNUM%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCXYNUM" onblur="blurChkDb(this)"
                           value="<%=CCXYNUM==null?"":CCXYNUM%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCCDNUM" onblur="blurChkDb(this)"
                           value="<%=CCCDNUM==null?"":CCCDNUM%>" class="page_form_text" maxlength="12"/>
                </td>
            </tr>
            <tr>
                <td class="page_form_title_td">总贷款额/额度：</td>
                <td>
                    <input type="text" <%=readonly%> name="CCDYAMT" onblur="blurChkDb(this)"
                           value="<%=CCDYAMT==null?"":CCDYAMT%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCXYAMT" onblur="blurChkDb(this)"
                           value="<%=CCXYAMT==null?"":CCXYAMT%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCCDAMT" onblur="blurChkDb(this)"
                           value="<%=CCCDAMT==null?"":CCCDAMT%>" class="page_form_text" maxlength="12"/>
                </td>
            </tr>
            <tr>
                <td class="page_form_title_td">总贷款余额/额度：</td>
                <td>
                    <input type="text" <%=readonly%> name="CCDYNOWBAL" onblur="blurChkDb(this)"
                           value="<%=CCDYNOWBAL==null?"":CCDYNOWBAL%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCXYNOWBAL" onblur="blurChkDb(this)"
                           value="<%=CCXYNOWBAL==null?"":CCXYNOWBAL%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCCDNOWBAL" onblur="blurChkDb(this)"
                           value="<%=CCCDNOWBAL==null?"":CCCDNOWBAL%>" class="page_form_text" maxlength="12"/>
                </td>
            </tr>
            <tr>
                <td class="page_form_title_td">当月还款额：</td>
                <td>
                    <input type="text" <%=readonly%> name="CCDYRPMON" onblur="countMonPayAmt(this)"
                           value="<%=CCDYRPMON==null?"":CCDYRPMON%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCXYRPMON" onblur="countMonPayAmt(this)"
                           value="<%=CCXYRPMON==null?"":CCXYRPMON%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCCDRPMON" onblur="countMonPayAmt(this)"
                           value="<%=CCCDRPMON==null?"":CCCDRPMON%>" class="page_form_text" maxlength="12"/>
                </td>
            </tr>
            <tr>
                <td class="page_form_title_td">12个月还款记录(次数)：</td>
                <td>
                    <input type="text" <%=readonly%> name="CCDYYEARRPTIME" onblur="blurChkDb(this)"
                           value="<%=CCDYYEARRPTIME==null?"":CCDYYEARRPTIME%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCXYYEARRPTIME" onblur="blurChkDb(this)"
                           value="<%=CCXYYEARRPTIME==null?"":CCXYYEARRPTIME%>" class="page_form_text" maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCCDYEARRPTIME" onblur="blurChkDb(this)"
                           value="<%=CCCDYEARRPTIME==null?"":CCCDYEARRPTIME%>" class="page_form_text" maxlength="12"/>
                </td>
            </tr>
            <tr>
                <td class="page_form_title_td">无逾期：</td>
                <td>
                    <%=Level.radioHere("CCDYNOOVERDUE", "YesNo", CCDYNOOVERDUE)%>
                </td>
                <td>
                    <%=Level.radioHere("CCXYNOOVERDUE", "YesNo", CCXYNOOVERDUE)%>
                </td>
                <td>
                    <%=Level.radioHere("CCCDNOOVERDUE", "YesNo", CCCDNOOVERDUE)%>
                </td>
            </tr>
            <tr>
                <td class="page_form_title_td">逾期1-30天(1)：</td>
                <td>
                    <input type="text" <%=readonly%> name="CCDY1TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCDY1TIMEOVERDUE==null?"":CCDY1TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCXY1TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCXY1TIMEOVERDUE==null?"":CCXY1TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCCD1TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCCD1TIMEOVERDUE==null?"":CCCD1TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
            </tr>
            <tr>
                <td class="page_form_title_td">逾期31-60天(2)：</td>
                <td>
                    <input type="text" <%=readonly%> name="CCDY2TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCDY2TIMEOVERDUE==null?"":CCDY2TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCXY2TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCXY2TIMEOVERDUE==null?"":CCXY2TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCCD2TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCCD2TIMEOVERDUE==null?"":CCCD2TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
            </tr>
            <tr>
                <td class="page_form_title_td">逾期60天以上(≧3)：</td>
                <td>
                    <input type="text" <%=readonly%> name="CCDYM3TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCDYM3TIMEOVERDUE==null?"":CCDYM3TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCXY3TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCXY3TIMEOVERDUE==null?"":CCXY3TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
                <td>
                    <input type="text" <%=readonly%> name="CCCD3TIMEOVERDUE" onblur="blurChkDb(this)"
                           value="<%=CCCD3TIMEOVERDUE==null?"":CCCD3TIMEOVERDUE%>" class="page_form_text"
                           maxlength="12"/>
                </td>
            </tr>
        </table>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td rowspan="7" class="page_left_table_title">授信要求
    </td>
    <td colspan="4" class="page_form_title_td" nowrap>&nbsp;年龄要求：21-60</td>
    <td colspan="2" class="page_form_td">
        <%=Level.radioHere("ACAGE", "YesNo", ACAGE)%>
    </td>
    <td class="page_form_td">&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="4" class="page_form_title_td" nowrap>&nbsp;每月最低工资要求：不低于人民币2,000元</td>
    <td colspan="2" class="page_form_td">
        <%=Level.radioHere("ACWAGE", "YesNo", ACWAGE)%>
    </td>
    <td class="page_form_td">&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="4" class="page_form_title_td" nowrap>&nbsp;征信系统内最近一期无未还款记录：</td>
    <td colspan="2" class="page_form_td">
        <%=Level.radioHere("ACZX1", "YesNo", ACZX1)%>
    </td>
    <td class="page_form_td">&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="4" class="page_form_title_td" nowrap>&nbsp;征信系统内过去12个月逾期在31-60天内不超过2次：</td>
    <td colspan="2" class="page_form_td">
        <%=Level.radioHere("ACZX2", "YesNo", ACZX2)%>
    </td>
    <td class="page_form_td">&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="4" class="page_form_title_td" nowrap>&nbsp;征信系统内过去3个月没有一次逾期超过60天：</td>
    <td colspan="2" class="page_form_td">
        <%=Level.radioHere("ACZX3", "YesNo", ACZX3)%>
    </td>
    <td class="page_form_td">&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="4" class="page_form_title_td" nowrap>&nbsp;分期付款与收入比是否低于30%：</td>
    <td colspan="2" class="page_form_td">
        <%=Level.radioHere("ACFACILITY", "YesNo", ACFACILITY)%>
    </td>
    <td class="page_form_td">&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td colspan="4" class="page_form_title_td" nowrap>&nbsp;每月还款与收入比是否低于60%：</td>
    <td colspan="2" class="page_form_td">
        <%=Level.radioHere("ACRATE", "YesNo", ACRATE)%>
    </td>
    <td class="page_form_td">&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td rowspan="2" class="page_left_table_title" nowrap>邮寄地址<br><span
            style="font-size:12px;font-weight:normal; color:red;">（信函、账单地址）</span>
    </td>
    <td class="page_form_title_td" nowrap>&nbsp;邮寄地址：</td>
    <td colspan="4" class="page_form_td" nowrap>
        <input type="text" <%=readonly%> name="RESDADDR"
               value="<%=RESDADDR==null?"":RESDADDR%>"
               class="page_form_text" style="width:478px" maxlength="40">
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;邮政编码：</td>
    <td colspan="4" class="page_form_td"><input type="text" <%=readonly%> id="RESDPC" name="RESDPC"
                                                value="<%=RESDPC==null?"":RESDPC%>"
                                                class="page_form_text" maxlength="6"></td>

    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_button_tbl_tr" colspan="7" height="5"></td>
</tr>
<tr class='page_form_tr'>
    <td rowspan="5" class="page_left_table_title">第三联系人</td>
    <td class="page_form_title_td" nowrap>&nbsp;姓&nbsp;&nbsp;名：</td>
    <td class="page_form_td">
        <input type="text" <%=readonly%> name="LINKMAN"
               value="<%=LINKMAN==null?"":LINKMAN%>"
               class="page_form_text" maxlength="40"
               onblur="checkLinkname('LINKMAN')"></td>
    <td class="page_form_td">&nbsp;</td>
    <td class="page_form_title_td" nowrap>&nbsp;性&nbsp;&nbsp;别：</td>
    <td colspan="2" nowrap class="page_form_td"><%=Level.radioHere("LINKMANGENDER", "Gender", LINKMANGENDER)%>
    </td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;手&nbsp;&nbsp;机：</td>
    <td class="page_form_td">
        <input type="text" <%=readonly%> name="LINKMANPHONE1"
               value="<%=LINKMANPHONE1==null?"":LINKMANPHONE1%>"
               class="page_form_text" maxlength="15"
               onblur="checkLinkPhone('LINKMANPHONE1');"></td>
    <td class="page_form_td">&nbsp;</td>
    <td class="page_form_title_td" nowrap>&nbsp;固定电话：</td>
    <td nowrap class="page_form_td"><input type="text" <%=readonly%> name="LINKMANPHONE2"
                                           value="<%=LINKMANPHONE2==null?"":LINKMANPHONE2%>"
                                           class="page_form_text" maxlength="15"></td>
    <td class="page_form_td">&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;与申请人关系：</td>
    <td colspan="4" class="page_form_td">
        <%=Level.radioHere("APPRELATION", "AppRelation", APPRELATION)%>
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;家庭地址：</td>
    <td colspan="4" class="page_form_td">
        <input type="text" <%=readonly%> name="LINKMANADD"
               value="<%=LINKMANADD==null?"":LINKMANADD%>"
               class="page_form_text" style="width:478px" maxlength="40">
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>
<tr class='page_form_tr'>
    <td class="page_form_title_td" nowrap>&nbsp;工作单位：</td>
    <td colspan="4" class="page_form_td">
        <input type="text" <%=readonly%> name="LINKMANCOMPANY"
               value="<%=LINKMANCOMPANY==null?"":LINKMANCOMPANY%>"
               class="page_form_text" style="width:478px" maxlength="40">
    </td>
    <td class="page_form_td" nowrap>&nbsp;</td>
</tr>

</table>

<input type='hidden' name='Plat_Form_Request_Instance_ID' value='2'>
<input type='hidden' name='Plat_Form_Request_Event_ID' value=''>
<input type='hidden' name='Plat_Form_Request_Event_Value' value='12'>
<input type='hidden' name='Plat_Form_Request_Button_Event' value=''>
</form>
</td>
<td width="20">&nbsp;</td>
</tr>
</table>
</td>
</tr>
<tr height="35" align="center" valign="middle">
    <td align="center">
        <table border="0" cellspacing="0" cellpadding="0" width="538" class="Noprint">
            <tr class='page_form_tr'>
                <td nowrap align="center">
                    <table bgcolor="#ffffff">
                        <tr class='page_button_tbl_tr'>
                            <%
                                if (SENDFLAG == null || !SENDFLAG.equals("1")) {
                            %>
                            <td class='page_button_tbl_td'><input type='button' <%=submit%> id='saveadd' name='save'
                                                                  value=' 提 交 '/></td>
                            <%
                                }
                            %>
                            <td class='page_button_tbl_td'><input type='button' <%=submit%> id='reback' name='reback'
                                                                  value=' 关 闭 '
                                                                  onClick="winClose()">
                            </td>
                        </tr>
                    </table>
                </td>
            </tr>
        </table>
    </td>
</tr>
</table>
</td>
</tr>
</table>
</body>

</html>
<script src='../../../js/jquery-1.6.js' type="text/javascript"></script>
<script language="javascript" type="text/javascript">
var citySZ = "深圳";
var cityGZ = "广州";
var provinceGD = "广东";
var provinceHN = "河南";
document.onkeydown = function TabReplace() {
    if (event.keyCode == 13) {
        if (event.srcElement.getAttribute("id") != 'saveadd')
            event.keyCode = 9;
//        else
//            event.srcElement.click();
    }
}
function winClose() {
    window.close();
}

$(document).ready(function() {
    setIDblur();
    setADDRblur();
});

/**
 * 检查申请人证件信息*/
function setIDblur() {
    $("#ID").blur(function() {
        var idtypeobj = document.getElementById('IDTYPE');
        var idobj = document.getElementById("ID");
        if (checkIDCard(document.getElementById('IDTYPE'), idobj)) getbirthday(idobj.value, 'BIRTHDAY');
        var appno = $("#APPNO").val();
        if (idobj.value != "") {
            var data = {"appno":appno,"idtype":idtypeobj.value,"idval":idobj.value,"addr":$("#ADDR").val(),"dotype":"checkID"};
            $.ajax({
                url:"application_query.jsp",
                type:"get",
                data:data,
                success:function(json) {
                    var applen = json[0].chkID.length;
                    if (applen != 0) {
                        alert(checkOrigApp(json));
                    }
//                alert(json[0].chkID.length);alert(json[1].totalamt);alert(json[2].chkaddr);
                }
            });
        }

    });
}

/*检查证件客户原有贷款数据*/
function checkOrigApp(json) {
    var applen = json[0].chkID.length;
    var alertstr = "";
    var plan = "---------------------------------------------------------------------------\r\n";
    alertstr += "该客户已存在贷款申请：\r\n" + plan;
    for (var i = 0; i < applen; i++) {
        alertstr += "客户名:" + json[0].chkID[i].name + ";  申请单号:" + json[0].chkID[i].appno + ";  分期金额:" + json[0].chkID[i].appamt + "\r\n";
        alertstr += plan;
    }
    var appamt = parseFloat($("#APPAMT").val() == "" ? "0" : $("#APPAMT").val());
    var totalamt = parseFloat(json[1].totalamt) + appamt;
    alertstr += "贷款余额:" + totalamt + "\r\n";
    if (totalamt >= 50000) {
        alertstr += plan;
        alertstr += "该客户现有贷款余额过高,禁止再申请。";
//        $("#saveadd").attr("disabled", "true");
    }
//    alert(alertstr);
    return alertstr;
}

/**
 * 为了配送地址检查 将提交按钮click事件移至此处  todo return false*/

function setADDRblur() {
    $("#saveadd").click(function() {
        var idtypeobj = document.getElementById('IDTYPE');
        var idobj = document.getElementById("ID");
        var appno = $("#APPNO").val();
        var data = {"appno":appno,"idtype":idtypeobj.value,"idval":idobj.value,"addr":$("#ADDR").val(),"dotype":"checkaddr"};
        var boolflag4id = true;
        var boolflag = "0";
        $.ajax({
            url:"application_query.jsp",
            type:"get",
            data:data,
            async:false,
            success:function(json) {
                var applen = json[0].chkID.length;
                var chkaddr = json[2].chkaddr;
                if (applen != 0) {
                    //检查客户原有贷款数据
                    if (!confirm(checkOrigApp(json) + "\r\n是否提交！")) {
                        boolflag4id = false;
                    }
                }
                if (chkaddr != 0) {
                    boolflag = chkaddr;
                }

            }
        });
        if (boolflag4id == false) {
            $("#ID").css("color","red");
            $("#ID").focus();
            return false;
        } else if (boolflag != "0") {
            if (!confirm('商品使用地址存在重复！\r\n重复'+boolflag + "\r\n是否确定提交。")) {
                $("#ADDR").css("color","red");
                $("#ADDR").focus();
                return false;
            }
        }
        Regvalid();
    });
}

/*
 * blur事件数字检查*/

function blurChkDb(obj) {
    if (isNaN(obj.value)) {
        obj.value = "";
        obj.focus();
        alert("请输入数字");
        return false;
    } else return true;
}
/**
 * 根据征信信息的当月还款合计
 * 获取现有贷款月均还款额 MONPAYAMT
 * 计算 现有贷款月均还款额
 *      每月还款与收入比率
 *      每月总还款额 11*/

function countMonPayAmt(obj) {
    if (!blurChkDb(obj)) return false;
    var ccdymon = document.getElementById("CCDYRPMON").value;
    var ccxymon = document.getElementById("CCXYRPMON").value;
    var cccdmon = document.getElementById("CCCDRPMON").value;
    ccdymon = (ccdymon == "") ? "0" : ccdymon;
    ccxymon = (ccxymon == "") ? "0" : ccxymon;
    cccdmon = (cccdmon == "") ? "0" : cccdmon;
    var monpayamt = parseFloat(ccdymon) + parseFloat(ccxymon) + parseFloat(cccdmon);
    document.getElementById("MONPAYAMT").value = Math.round(monpayamt * 100) / 100;  //现有贷款月均还款额
    var ccdividamt = document.getElementById("CCDIVIDAMT").value; //该笔分期还款额
    ccdividamt = (ccdividamt == "") ? "0" : ccdividamt;
    document.getElementById("CCRPTOTALAMT").value = Math.round((parseFloat(ccdividamt) + monpayamt) * 100) / 100;  //月总还款额
    blurcountMONTHPAYSLRRATE();   //计算每月还款与收入比率
}
/**
 * 自动增加行调用的
 * 商品信息录入检查*/
function checkCommExtend() {
    var obj = getElement();
    checkComm(obj);
}

/*        COMMNMTYPE
 * 商品信息录入检查*/

function checkComm(obj) {
    var comnumber = obj.getAttribute("id");
    comnumber = comnumber.substring(comnumber.length - 1, comnumber.length);
    for (var i = 1; i < comnumber; i++) {
        if (!isEmptyItem1("COMMNMTYPE" + i, "请逐行完整录入商品信息。")) {
            obj.value = "";
            return false;
        } else if (!isEmptyItem1("NUM" + i, "请逐行完整录入商品信息。")) {
            obj.value = "";
            return false;
        } else if (!isEmptyItem1("AMT" + i, "请逐行完整录入商品信息。")) {
            obj.value = "";
            return false;
        } else if (!isEmptyItem1("RECEIVEAMT" + i, "请逐行完整录入商品信息。")) {
            obj.value = "";
            return false;
        }
    }
}

/**
 * 判断是否为空 返回 true or false*/

function textIsEmpty(objId) {
    var obj = document.getElementById(objId);
    if (obj.value.Trim() == "") return true;
    else return false;
}
/**商品信息最后一行*/

function jurgeLastRow() {
    var tabObj = document.getElementById("tabCommContent");
    var rowLen = tabObj.rows.length;
    var comnumber = rowLen - 2;
    if ((textIsEmpty("COMMNMTYPE" + comnumber) && textIsEmpty("NUM" + comnumber) && textIsEmpty("AMT" + comnumber)
            && textIsEmpty("RECEIVEAMT" + comnumber)) || (!textIsEmpty("COMMNMTYPE" + comnumber) && !textIsEmpty("NUM" + comnumber)
            && !textIsEmpty("AMT" + comnumber) && !textIsEmpty("RECEIVEAMT" + comnumber))) {
        return true;
    } else {
        document.getElementById("tabCommContent").focus();
        alert("请完整录入最后一行商品信息。");
        return false;
    }
}
/** 判断是否行录入完整 blur中调用*/
function jurgeTurnRow(obj) {
    var comnumber = obj.getAttribute("id");
    comnumber = comnumber.substring(comnumber.length - 1, comnumber.length);
    if (!isEmptyItem1("COMMNMTYPE" + comnumber, "请完整录入商品信息。")) {
        obj.value = "";
        return false;
    } else if (!isEmptyItem1("NUM" + comnumber, "请完整录入商品信息。")) {
        obj.value = "";
        return false;
    } else if (!isEmptyItem1("AMT" + comnumber, "请完整录入商品信息。")) {
        obj.value = "";
        return false;
    } else if (!isEmptyItem1("RECEIVEAMT" + comnumber, "请完整录入商品信息。")) {
        obj.value = "";
        return false;
    }
    return true;
}

/* 自动增加行调用的
 * 计算商品总数，总额，总首付*/

function countTotalNumExtd(totalId) {
    var obj = getElement();
    countTotalNum(obj, totalId);
}

function countTotalNum(obj, totalId) {
    if (isNaN(obj.value)) {
        alert("请输入数字.");
        obj.value = "";
        obj.focus();
        return false;
    }
    var objNoId = obj.getAttribute("id");
    var objId = objNoId.substr(0, objNoId.length - 1);
    var tabObj = document.getElementById("tabCommContent");
    var rowLen = tabObj.rows.length;
    var totalnum = 0;
    for (var i = 1; i < (rowLen - 1); i++) {
        var num = document.getElementById(objId + i).value;
        num = num == "" ? "0" : num;
        totalnum += parseFloat(num, 10);
    }
    if (objId == "NUM") {
        totalnum = parseInt(totalnum, 10);
    } else {
        totalnum = Math.round(totalnum * 100) / 100;
    }
    document.getElementById(totalId).value = totalnum;
    if (objId != "NUM") {
        countAppAmt();
        blurcountMONTHPAYSLRRATE();
    }
    var rowNum = parseInt(objNoId.substr(objNoId.length - 1, objNoId.length), 10) + 2;
    if (rowNum == rowLen && objId == "RECEIVEAMT") {
        if (!jurgeTurnRow(obj)) return false;
        appendCommRow(tabObj);
        document.getElementById("COMMNMTYPE" + (rowNum - 1)).focus();
    }
}

/**
 * 总首付款录入项blur
 * 计算 分期总付款 */
function countAppAmtAndRate(obj) {
    if (isNaN(obj.value)) {
        alert("请输入数字.");
        obj.value = "";
        obj.focus();
        return false;
    }
    countAppAmt();
    blurcountMONTHPAYSLRRATE();
}
/*
 * 月还款额 blur*/

function blurMonthpay(obj) {
    if (isNaN(obj.value)) {
        alert("请输入数字.");
        obj.value = "";
        obj.focus();
        return false;
    }
    blurcountMONTHPAYSLRRATE();
}

/*
 * 计算 总债务每月还款金额与收入比率 月分期还款与收入比*/
function blurcountMONTHPAYSLRRATE() {
    var e = document.getElementsByName("DIVID");
    var obj = null;
    for (var i = 0; i < e.length; i++) {
        if (e[i].checked) {
            obj = e[i];
            break;
        }
    }
    if (obj != null) countMONTHPAYSLRRATE(obj);
}
/*
 * 计算分期付款与收入比率
 * 总债务每月还款金额与收入比率 11*/

function countMONTHPAYSLRRATE(obj) {
    var slrypay = document.getElementById("MONTHLYPAY").value;             //月收入
    var monpayamt = document.getElementById("MONPAYAMT").value;            //月还款额
    slrypay = slrypay == "" ? "0" : slrypay;
    monpayamt = monpayamt == "" ? "0" : monpayamt;
    if (slrypay != "0") {
        var appamt = document.getElementById("APPAMT").value;
        var commrate = document.getElementById("COMMISSIONRATE").value;
        appamt = parseFloat((appamt == "" ? "0" : appamt));
        var divid = (obj.value == "" ? "0" : obj.value);
        divid = parseFloat(divid);
        monpayamt = parseFloat(monpayamt);
        slrypay = parseFloat(slrypay);
        //总债务每月还款金额与收入比率
        var totalmonpay = ((commrate / 1000 * divid + 1) * appamt / divid) + monpayamt;
        var monpayslrrate = Math.round((totalmonpay / slrypay * 100) * 100) / 100;
        document.getElementById("MONTHPAYSLRRATE").value = monpayslrrate;
        //计算分期付款与收入比率
        var appslrypay = ((commrate / 1000 * divid + 1) * appamt / divid);     //该笔月分期还款金额
        var apppayrate = Math.round((appslrypay / slrypay * 100) * 100) / 100;
        document.getElementById("APPSALARYRATE").value = apppayrate;
        //该笔分期还款金额
        document.getElementById("CCDIVIDAMT").value = Math.round(appslrypay * 100) / 100; //该笔分期还款额
        var monpayamt = document.getElementById("MONPAYAMT").value; //现有贷款月均还款额
        monpayamt = (monpayamt == "") ? "0" : monpayamt;
        document.getElementById("CCRPTOTALAMT").value = Math.round((parseFloat(monpayamt) + appslrypay) * 100) / 100;  //月总还款额
    }
}

function setDIVIDRadio(str) {
    var e = document.getElementsByName(str);
    for (var i = 0; i < e.length; i++) {
        if (window.addEventListener) { // Mozilla, Netscape, Firefox
            e[i].addEventListener('click', resetDIVIDRadio(), false);
        }
        else {
            e[i].attachEvent("onclick", function() {
                resetDIVIDRadio();
            });
        }
    }
}

function resetDIVIDRadio() {
    var obj = getElement();
    countMONTHPAYSLRRATE(obj);
}

document.title = "<%=title%>";
document.focus();

function setBankRadio(str) {
    var obj = document.getElementsByName(str)[0];
    var labelName = obj.options[obj.selectedIndex].text.split("-")[0];

    var obj2 = document.getElementsByName("BANKACTNO");
    inputObjpreObj(obj2).innerText = " " + labelName + "帐号：";
    document.getElementById("BANK_UD2").value = labelName;
    if (obj.value == '901') {
        //haiyu 2010-08-11 for 修改cell背景色
        getObject("BANK_UD2").style.display = "block";
        getObject('BANK_UD1').parentNode.display = "block";
    }
    //判断是否通过银联
    document.getElementById("UNIONCHANNEL").value = obj.value.split("-")[1];
    //获取银行代号
    var bankno = obj.value.split("-")[0];
    var bankno2 = new Array();
    bankno2 = bankno.split("@");
    if (bankno == "104") {
        document.getElementById("PROVINCE").style.display = "block";
        document.getElementById("lblPROVINCE").style.display = "block";
    } else {
        document.getElementById("PROVINCE").style.display = "none";
        document.getElementById("lblPROVINCE").style.display = "none";
    }
    if (bankno2.length == 1) {
        document.getElementById("ACTOPENINGBANK").value = bankno;
        document.getElementById("CITY").value = "";  //所在城市设置为空
    }
    else {
        //城市商业银行
        if (bankno2[1] == "313") {
            if (bankno2[0] == "1") {
                document.getElementById("CITY").value = citySZ;  //所在城市
                document.getElementById("PROVINCE").value = provinceGD;      //广东
            } else if (bankno2[0] == "2") {
                document.getElementById("CITY").value = cityGZ;  //所在城市
                document.getElementById("PROVINCE").value = provinceGD;      //广东
            }
        } else if (bankno2[1] == "403") {                           //邮储银行
            if (bankno2[0] == "1") {
                document.getElementById("PROVINCE").value = provinceGD;      //广东
                document.getElementById("CITY").value = "";  //所在城市空
            } else if (bankno2[0] == "2") {
                document.getElementById("PROVINCE").value = provinceHN;       //河南
                document.getElementById("CITY").value = "";  //所在城市空
            }
        }
    }
    if (window.addEventListener) { // Mozilla, Netscape, Firefox
        obj.addEventListener('click', reSetActnoText("BANKACTNO"), false);
    }
    else {
        obj.attachEvent("onclick", function() {
            reSetActnoText("BANKACTNO");
        });
    }
}

document.getElementsByName("CREDITTYPE")[0].onclick = function() {
    listCheck("CREDITTYPE", 0);
}

document.body.onload = function() {
    setBankRadio("ACTOPENINGBANKUP");
    listCheck("CREDITTYPE", 0);
    setDIVIDRadio("DIVID");
    document.getElementById("XFAPPNO").focus();
}

function reSetActnoText(str) {
    var obj1 = getElement();
    var obj2 = document.getElementsByName(str);
    if (obj1.value == '901') {
        getObject("BANK_UD2").style.display = "block";
        getObject('BANK_UD1').parentNode.style.display = "block";
    } else {
        getObject("BANK_UD2").style.display = "none";
        getObject('BANK_UD1').parentNode.style.display = "none";
    }
    var bankname = obj1.options[obj1.selectedIndex].text.split("-")[0];
    inputObjpreObj(obj2).innerText = " " + bankname + "帐号：";
    obj2[0].value = "";
    document.getElementById("BANK_UD2").value = bankname;
    //判断是否通过银联
    document.getElementById("UNIONCHANNEL").value = obj1.value.split("-")[1];
    //获取银行代号
//    document.getElementById("ACTOPENINGBANK").value = obj1.value.split("-")[0];
    var bankno = obj1.value.split("-")[0];
    if (bankno == "104") {
        document.getElementById("PROVINCE").style.display = "block";
        document.getElementById("lblPROVINCE").style.display = "block";
    } else {
        document.getElementById("PROVINCE").style.display = "none";
        document.getElementById("lblPROVINCE").style.display = "none";
    }
    var bankno2 = new Array();
    bankno2 = bankno.split("@");
    if (bankno2.length == 1) {
        document.getElementById("ACTOPENINGBANK").value = bankno;
        document.getElementById("CITY").value = "";  //所在城市设置为空
    }
    else {
        document.getElementById("ACTOPENINGBANK").value = bankno2[1]; //设置银行代码
        //设置银行所在省市 城市商业银行
        if (bankno2[1] == "313") {
            if (bankno2[0] == "1") {
                document.getElementById("CITY").value = citySZ;  //所在城市
                document.getElementById("PROVINCE").value = provinceGD;      //广东
            } else if (bankno2[0] == "2") {
                document.getElementById("CITY").value = cityGZ;  //所在城市
                document.getElementById("PROVINCE").value = provinceGD;      //广东
            }
        } else if (bankno2[1] == "403") {                           //邮储银行
            if (bankno2[0] == "1") {
                document.getElementById("PROVINCE").value = provinceGD;      //广东
                document.getElementById("CITY").value = "";  //所在城市空
            } else if (bankno2[0] == "2") {
                document.getElementById("PROVINCE").value = provinceHN;       //河南
                document.getElementById("CITY").value = "";  //所在城市空
            }
        }
    }
}

function limitTextarea(obj) {
    var len = 150;
    //    if (obj.value.getBytes() > len){
    if (obj.value.length > len) {
        obj.value = obj.value.substr(0, len);
    }
}

function listCheck(objnm, id) {
    var crdtps = document.getElementsByName(objnm);
    if (crdtps[id].checked) {
        for (var i = 0; i < crdtps.length; i++) {
            if (i == id)continue;
            crdtps[i].checked = false;
            crdtps[i].disabled = true;
        }
        document.getElementById("MONPAYAMT").value = 0;
//        document.getElementById("MONPAYAMT").readOnly = true;
        document.getElementById("CCDYRPMON").readOnly = true;
        document.getElementById("CCXYRPMON").readOnly = true;
        document.getElementById("CCCDRPMON").readOnly = true;
        blurcountMONTHPAYSLRRATE();
    } else {
        for (var i = 0; i < crdtps.length; i++) {
            if (i == id)continue;
            crdtps[i].disabled = false;
        }
//        document.getElementById("MONPAYAMT").readOnly = false;
        document.getElementById("CCDYRPMON").readOnly = false;
        document.getElementById("CCXYRPMON").readOnly = false;
        document.getElementById("CCCDRPMON").readOnly = false;
    }
}

function fileup(appno, appstatus) {
    var filetp = "APP";
    var url = "../fileupdown/fileup.jsp?OPNO=" + appno + "&FILETP=" + filetp + "&APPSTATUS=" + appstatus;

    var x = window.screen.width;
    var y = window.screen.height;
    x = (x - 390) / 2;
    y = (y - 470) / 2;
    //window.showModalDialog(url, 'fileup', 'dialogHeight:390px;dialogWidth:450px;status:no;scroll:no;help:no;resizable:yes');
    window.open(url, 'fileup', 'left=' + x + ',top=' + y + ',height=390,width=470,toolbar=no,scrollbars=yes,resizable=yes');
}


function goSave(bank) {
    if (!isEmptyItem("XFAPPNO")) return false;
    if (!isEmptyItem("NAME")) return false;
    if (!isEmptyItem("GENDER"))return false;
    if (!isEmptyItem("PHONE1"))return false;
    else if (!isPhone("PHONE1"))return false;
    if (!isEmptyItem("ID"))return false;
    if (!isEmptyItem("HEALTHSTATUS"))return false;
    if (!isEmptyItem("MARRIAGESTATUS"))return false;
    if (!isEmptyItem("BURDENSTATUS"))return false;
    if (!isEmptyItem("RESIDENCEADR"))return false;
    if (!isEmptyItem("CURRENTADDRESS"))return false;
    if (!isZipCode("PC"))return false;
    if (!isEmail("EMAIL"))return false;
    if (!isEmptyItem("PHONE2"))return false;
    else if (!isPhone("PHONE2"))return false;
    if (!isEmptyItem("LIVEFROM"))return false;
    else if (!chkintWithAlStr("LIVEFROM", "本地居住时间请保留整数！"))return false;
    if (!isEmptyItem("EDULEVEL"))return false;
    if (!isEmptyItem("QUALIFICATION"))return false;
    if (!isEmptyItem("COMPANY"))return false;
    if (!isEmptyItem("PHONE3"))return false;
    else if (!isPhone("PHONE3"))return false;
    if (!isZipCode("COMPC"))return false;
    if (!isEmptyItem("COMADDR"))return false;
    if (!isEmptyItem("CLIENTTYPE"))return false;
    if (!isEmptyItem("SERVFROM"))return false;
    else if (!chkintWithAlStr("SERVFROM", "现单位工作时间请保留整数！"))return false;

    if (!isEmptyItem("MONTHLYPAY"))return false;
    else if (!chkdecWithAlStr("MONTHLYPAY", "个人月收入请录入数字！"))return false;
    //if (!isEmptyItem("SOCIALSECURITY"))return false;
    if (document.getElementById("PID").value.Trim() != "") {
        if (document.getElementById("ID").value == document.getElementById("PID").value) {
            alert("配偶证件号码有误，请核实！");
            document.getElementById("PID").focus();
            return false;
        }
    }
    if (document.getElementById("PNAME").value.Trim() != "") {
        if (!isEmptyItem("PID"))return false;
        if (!isEmptyItem("PCLIENTTYPE"))return false;
        if (!isEmptyItem("PMONTHLYPAY"))return false;
        else if (!chkdecWithAlStr("PMONTHLYPAY", "个人月收入请录入数字！"))return false;
    }
    if (!isPhone("PPHONE1"))return false;
    if (!isPhone("PPHONE3"))return false;

    if (!isEmptyItem("CHANNEL"))return false;
    if (!isEmptyItem("TOTALNUM"))return false;
    else if (!chkintWithAlStr("TOTALNUM", "拟购商品数量请输入整数！"))return false;

    if (!isEmptyItem("TOTALAMT"))return false;
    else if (!chkdecWithAlStr("TOTALAMT", "商品总金额请录入数字！"))return false;
    if (!chkdecWithAlStr("RECEIVEAMT", "已付价款请录入数字！"))return false;
    if (!isEmptyItem("DIVID"))return false;
    if (!isEmptyItem("ACTOPENINGBANKUP"))return false;
    if (!isEmptyItem("BANKACTNAME")) return false;
    if (bank != "802")
        if (!isEmptyItem("BANKACTNO"))return false;
    if (bank == "901")
        if (!isEmptyItem("ACTOPENINGBANK_UD"))return false;
    if (!isEmptyItem("CREDITTYPE"))return false;
    if (!isEmptyItem("MONPAYAMT"))return false;
    else if (!chkdecWithAlStr("MONPAYAMT", "月均还款额请录入数字！"))return false;
    if (!isEmptyItem("RESDADDR"))return false;
    if (!isEmptyItem("RESDPC"))return false;
    else if (!isZipCode("RESDPC"))return false;
    if (!isEmptyItem("LINKMAN"))return false;
    if (!isEmptyItem("LINKMANPHONE1"))return false;
    else if (!isPhone("LINKMANPHONE1"))return false;
    if (!isPhone("LINKMANPHONE2"))return false;
    if (!isEmptyItem("APPRELATION"))return false;
    if (!checkLinkname('LINKMAN')) return false;
    if (!checkLinkPhone('LINKMANPHONE1')) return false;
    if (!isEmptyItem1("COMMNMTYPE1", "商品名称不能为空!")) return false;
    if (!jurgeLastRow()) return false;   //判断最后一行商品录入是否完整
    if (!isEmptyItem("HOUSINGSTS")) return false;
    if (!isEmptyItem("ETPSCOPTYPE")) return false;
    if (!isEmptyItem("ANNUALINCOME")) return false;
    else if (!chkdecWithAlStr("ANNUALINCOME", "年收入请输入数字！")) return false;
    if (!isEmptyItem("ACAGE")) return false;
    if (!isEmptyItem("ACWAGE")) return false;
    if (!isEmptyItem("ACZX1")) return false;
    if (!isEmptyItem("ACZX2")) return false;
    if (!isEmptyItem("ACZX3")) return false;
    if (!isEmptyItem("ACFACILITY")) return false;
    if (!isEmptyItem("ACRATE")) return false;
    if (!checkAmt()) return false;
    return true;
}


function Regvalid() {//提交
    var bank;
    var e = document.getElementsByName("ACTOPENINGBANK");
    bank = e[0].value;
    if (goSave(bank)) {
        //第三方支付新窗口控制
        //if (bank == "801" || bank == "802")document.forms[0].target = "_blank";
//        document.forms[0].target = "_blank";
        document.forms[0].action = "application_save.jsp";
        document.forms[0].submit();
    }
}

function Regvalid1() {//作废
    if (confirm("确实要作废申请单？")) {
//        document.forms[0].target = "_blank";
        document.all.APPACTFLAG.value = "3";
        document.forms[0].action = "application_save.jsp";
        document.forms[0].submit();
    }
}

function Regvalid2() {//退回
    if (confirm("确实要将申请单退回上一级审批？")) {
        document.all.APPACTFLAG.value = "2";
        document.forms[0].action = "application_save.jsp";
        document.forms[0].submit();
    }
}

function countAppAmt() {//自动计算分期金额
    var obj1 = document.getElementsByName("TOTALAMT")[0];
    var obj2 = document.getElementsByName("RECEIVEAMT")[0];
    var obj3 = document.getElementsByName("APPAMT")[0];

    if (obj1.value != null && chkdec("TOTALAMT")) {
        var countamt = 0;
        if (obj2.value != null && chkdec("RECEIVEAMT")) {
            countamt = obj1.value - obj2.value;
        } else countamt = obj1.value;
        obj3.value = Math.round(countamt * 100) / 100;
    }
}


function getObject(objectId) {
    if (document.getElementById && document.getElementById(objectId)) {

        return document.getElementById(objectId);
    } else if (document.all && document.all(objectId)) {

        return document.all(objectId);
    } else if (document.layers && document.layers[objectId]) {

        return document.layers[objectId];
    } else {
        return false;
    }
}

function showList(n) {
    var itext = getObject('HT' + n).innerText;
    itext = itext.substr(0, itext.indexOf(" -- "));
    if (getObject("HA" + n).style.display == "none") {
        disList(n, itext);
    }
    else {
        hidList(n, itext);
    }
}

function disList(n, itext) {
    getObject('HT' + n).title = "点击收缩";
    getObject('HT' + n).innerText = itext + " -- ↑(点击收缩)";
    getObject("HA" + n).style.display = "block";
}

function hidList(n, itext) {
    getObject('HT' + n).title = "点击展开";
    getObject('HT' + n).innerText = itext + " -- ↓(点击展开)";
    getObject("HA" + n).style.display = "none";
}


function mOvr(src) {
    if (!src.contains(event.fromElement)) {
        dataBgColor = src.bgColor;
        src.style.cursor = "hand";
        src.style.backgroundColor = "#E1FFF0";
    }
}

function mOut(src) {
    if (!src.contains(event.toElement)) {
        src.style.cursor = "default";
        src.style.backgroundColor = "#FDF8DF";
    }
}

//20100108  zhanrui 增加对第三联系人的检查
//function checkName() {
//    if (document.getElementById("RESDADDR").value == "")
//        document.getElementById("RESDADDR").value = document.getElementById("CURRENTADDRESS").value;
//}

</script>

<%if (APPNO.equals("")) {%>
<script type="text/javascript">
    document.forms[0].CHANNEL.focus();
</script>

<%
    }
    if (!PNAME.equals("")) {%>
<script type="text/javascript">
    showList(1);
</script>
<%
    }
    if (APPSTATUS.equals("")) {
%>
<script type="text/javascript">
    //    document.getElementById("print").className = "page_button_disabled";
    //    document.getElementById("print").disabled = true;
</script>
<%
    }
    if (!SID.equals("")) {
%>
<script type="text/javascript">
    countAppAmt();
    document.forms[0].CHANNEL.readOnly = true;
    document.forms[0].TOTALNUM.readOnly = true;
    document.forms[0].TOTALAMT.readOnly = true;
</script>
<%}%>
<%}%>


