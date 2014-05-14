package hfc.common;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-12
 * Time: ÏÂÎç11:03
 * To change this template use File | Settings | File Templates.
 */
public class ConstSqlString {
    public static final String WRITE_BACK_RECORDS_SQL = "select  a.appno,a.pkid,c.CLIENTNO,to_char(c.BIRTHDAY,'yyyyMMdd') BIRTHDAY,    " +
            "     (select enuitemlabel from ptenudetail where enutype = 'NewGender' and  enuitemvalue = c.GENDER ) as GENDER,  c.NATIONALITY,    " +
            "     (select enuitemlabel from ptenudetail where enutype = 'NewMarriageStatus' and  enuitemvalue = c.MARRIAGESTATUS ) as MARRIAGESTATUS,    " +
            "     c.HUKOUADDRESS,c.CURRENTADDRESS,   c.COMPANY,    " +
            "     (select enuitemlabel from ptenudetail where enutype = 'NewTitle' and  enuitemvalue = c.TITLE ) as TITLE,    " +
            "     (select enuitemlabel from ptenudetail where enutype = 'NewQualification' and  enuitemvalue = c.QUALIFICATION ) as QUALIFICATION,    " +
            "     (select enuitemlabel from ptenudetail where enutype = 'NewEduLevel' and  enuitemvalue = c.EDULEVEL ) as EDULEVEL, c.PHONE1,c.PHONE2,    " +
            "     c.PHONE3,c.NAME,c.CLIENTTYPE,c.DEGREETYPE,c.COMADDR,c.SERVFROM,c.RESIDENCEADR,c.HOUSINGSTS,    " +
            "     c.HEALTHSTATUS,c.MONTHLYPAY,c.BURDENSTATUS,c.EMPNO,c.SOCIALSECURITY,c.LIVEFROM,c.PC,c.COMPC,c.RESDPC,c.RESDADDR,c.EMAIL,    " +
            "     p.NAME  PNAME ,p.IDTYPE PIDTYPE ,p.ID PID ,p.COMPANY PCOMPANY ,p.TITLE PTITLE ,p.PHONE1 PPHONE1 ,    " +
            "     (select enuitemlabel from ptenudetail where enutype = 'NewHousingSts' and  enuitemvalue = c.housingsts) as chousingsts,  " +
            "     p.PHONE3 PPHONE3 ,p.CLIENTTYPE PCLIENTTYPE ,p.SERVFROM PSERVFROM ,p.MONTHLYPAY PMONTHLYPAY , p.LIVEFROM PLIVEFROM,   " +
            "     a.CHANNEL,a.ADDR,a.TOTALNUM,a.TOTALAMT,a.RECEIVEAMT,a.APPAMT,a.DIVID,a.COMMISSIONRATE,a.SHCOMMISSIONRATE, " +
            "     a.COMPAYDATE,a.SHARETYPE ,  m.commnmtype,m.num as commnum,m.amt as commamt,  " +
            "     d.ACTOPENINGBANK,d.BANKACTNO,d.XY,d.XYR,d.DY,d.DYW,d.ZY,d.ZYW,d.BZ,d.BZR,d.CREDITTYPE,d.MONPAYAMT,    " +
            "     d.LINKMAN,d.LINKMANGENDER,d.LINKMANPHONE1,d.LINKMANPHONE2,d.APPRELATION,d.LINKMANADD,d.LINKMANCOMPANY,d.ACTOPENINGBANK_UD,    " +
            "     (select enuitemlabel from ptenudetail where enutype = 'NewIDType' and  enuitemvalue = a.IDTYPE ) as IDTYPE,    " +
            "     a.ID,to_char(a.APPDATE,'yyyyMMdd') as APPDATE,a.APPTYPE,a.APPSTATUS,a.SID,a.ORDERNO,a.REQUESTTIME, n.operid,n.BANKACTNO     " +
            "     from XFCLIENT c, xfappcommbatch m,XFAPPADD d,xfapprepayment n, XFAPP a    " +
            "     left outer join XFCLIENT p on a.appno=p.APPNO and p.XFCLTP='2'    " +
            "     where a.APPNO=c.APPNO  and a.appno=n.appno   and a.APPNO=m.APPNO and m.commno = '1' and a.APPNO=d.APPNO and c.XFCLTP='1' and a.appstatus = '1' ";

}
