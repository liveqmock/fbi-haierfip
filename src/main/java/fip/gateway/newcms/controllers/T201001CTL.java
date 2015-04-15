package fip.gateway.newcms.controllers;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import fip.gateway.newcms.domain.T100101.T100101ResponseRecord;
import fip.gateway.newcms.domain.T100102.T100102RequestList;
import fip.gateway.newcms.domain.T100102.T100102RequestRecord;
import fip.gateway.newcms.domain.T201001.T201001Request;
import fip.gateway.newcms.domain.T201001.T201001Response;
import fip.gateway.newcms.domain.common.BaseBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pgw.NewCmsManager;
import pub.platform.db.ConnectionManager;
import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;

import javax.faces.bean.ManagedBean;
import java.util.List;


/**
 * �����Ŵ����������ϴ�
 * User: zhanrui
 * Date: 2010-8-27
 * Time: 13:22:35
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "T201001")
public class T201001CTL   implements java.io.Serializable {
    private Log logger = LogFactory.getLog(this.getClass());
    private String ID = "aaa";

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public final static void main(String[] args) throws Exception {

        T201001CTL ctl = new T201001CTL();
        ctl.start();
    }

    public void start() {
        //�����
        //T201001Request request = getAppFormInfo("4113241986060409140001");
        T201001Request request = getAppFormInfo("3702251969052900300001");
        request.initHeader("0200", "201001", "3");
        System.out.println(request);
        String strXml = request.toXml();//"<?xml version=\"1.0\" encoding=\"GBK\"?>" + xstream.toXML(request);
        System.out.println(strXml);

        //��������
        NewCmsManager ncm = new NewCmsManager();
        String responseBody = ncm.doPostXml(strXml);
        //  201001�޷��ر���
        /*T201001Response response = (T201001Response) BaseBean.toObject(T201001Response.class, responseBody);
        System.out.println(response);*/


    }

    /**
     * ���Ŵ������������ϴ����пۿ���
     */
/*    private void uploadCutpayResultBatch(List<T100101ResponseRecord> records) {

        int count = 0;

        T100102RequestList t012 = new T100102RequestList();

        for (T100101ResponseRecord record : records) {
            System.out.println(record.getStdjjh() + " " + record.getStdqch() + " " + record.getStdkhmc() + " " + record.getStdjhhkr());
            count++;

            T100102RequestRecord recordT102 = new T100102RequestRecord();
            recordT102.setStdjjh(record.getStdjjh());
            recordT102.setStdqch(record.getStdqch());
            recordT102.setStdjhhkr(record.getStdjhhkr());
            //1-�ɹ� 2-ʧ��
            recordT102.setStdkkjg("1");

            t012.add(recordT102);
        }
        System.out.println(count);

        T100102CTL ctlT102 = new T100102CTL();
        ctlT102.start(t012);

    }*/


    private T201001Request getAppFormInfo(String AppFormNo) {
        T201001Request reqRecord = new T201001Request();
        DatabaseConnection conn = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();

            String sql = "select c.CLIENTNO,to_char(c.BIRTHDAY,'yyyyMMdd') BIRTHDAY, " +
                    "                    (select enuitemlabel from ptenudetail where enutype = 'NewGender' and  enuitemvalue = c.GENDER ) as GENDER, " +
                    "                    c.NATIONALITY, " +
                    "                    (select enuitemlabel from ptenudetail where enutype = 'NewMarriageStatus' and  enuitemvalue = c.MARRIAGESTATUS ) as MARRIAGESTATUS, " +
                    "                    c.HUKOUADDRESS,c.CURRENTADDRESS, " +
                    "                    c.COMPANY, " +
                    "                    (select enuitemlabel from ptenudetail where enutype = 'NewTitle' and  enuitemvalue = c.TITLE ) as TITLE, " +
                    "                    (select enuitemlabel from ptenudetail where enutype = 'NewQualification' and  enuitemvalue = c.QUALIFICATION ) as QUALIFICATION, " +
                    "                    (select enuitemlabel from ptenudetail where enutype = 'NewEduLevel' and  enuitemvalue = c.EDULEVEL ) as EDULEVEL, " +
                    "                    c.PHONE1,c.PHONE2, " +
                    "                    c.PHONE3,c.NAME,c.CLIENTTYPE,c.DEGREETYPE,c.COMADDR,c.SERVFROM,c.RESIDENCEADR,c.HOUSINGSTS, " +
                    "                    c.HEALTHSTATUS,c.MONTHLYPAY,c.BURDENSTATUS,c.EMPNO,c.SOCIALSECURITY,c.LIVEFROM,c.PC,c.COMPC,c.RESDPC,c.RESDADDR,c.EMAIL, " +
                    "                    p.NAME  PNAME ,p.IDTYPE PIDTYPE ,p.ID PID ,p.COMPANY PCOMPANY ,p.TITLE PTITLE ,p.PHONE1 PPHONE1 , " +
                    "                    p.PHONE3 PPHONE3 ,p.CLIENTTYPE PCLIENTTYPE ,p.SERVFROM PSERVFROM ,p.MONTHLYPAY PMONTHLYPAY , " +
                    "                    p.LIVEFROM PLIVEFROM, " +
                    "                    m.CHANNEL,m.COMMNAME,m.COMMTYPE,m.ADDR,m.NUM,m.AMT,m.RECEIVEAMT,m.APPAMT,m.DIVID, " +
                    "                    d.ACTOPENINGBANK,d.BANKACTNO,d.XY,d.XYR,d.DY,d.DYW,d.ZY,d.ZYW,d.BZ,d.BZR,d.CREDITTYPE,d.MONPAYAMT, " +
                    "                    d.LINKMAN,d.LINKMANGENDER,d.LINKMANPHONE1,d.LINKMANPHONE2,d.APPRELATION,d.LINKMANADD,d.LINKMANCOMPANY,d.ACTOPENINGBANK_UD, " +
                    "                    (select enuitemlabel from ptenudetail where enutype = 'NewIDType' and  enuitemvalue = a.IDTYPE ) as IDTYPE, " +
                    "                    a.ID,to_char(a.APPDATE,'yyyyMMdd') as APPDATE,a.APPTYPE,a.APPSTATUS,a.SID,a.ORDERNO,a.REQUESTTIME  " +
                    "                    from XFCLIENT c,XFAPPCOMM m,XFAPPADD d,XFAPP a  " +
                    "                    left outer join XFCLIENT p on a.APPNO=p.APPNO and p.XFCLTP='2' " +
                   // "                    where a.APPNO='" + AppFormNo + "' and a.APPNO=c.APPNO and c.XFCLTP='1' " +
                    "                    where a.APPNO=c.APPNO and c.XFCLTP='1' " +
                    "                    and a.APPNO=m.APPNO and a.APPNO=d.APPNO";

            logger.info(sql);
            RecordSet recordSet = conn.executeQuery(sql);

            if (recordSet.getRecordCount()==0) {
                logger.error("δ�ҵ���Ӧ������Ϣ" + sql);
                throw new RuntimeException("δ�ҵ���Ӧ������Ϣ");
            }

            while (recordSet.next()) {
                //reqRecord.setStdsqdh(recordSet.getString("appno"));    //���뵥��
                reqRecord.setStdsqdh(AppFormNo);    //���뵥��
//                reqRecord.setStdurl("http://192.168.91.20/faces/dnldfile.xhtml?appno=" + AppFormNo);    //�ļ�URL  TODO ������
                reqRecord.setStdurl("http://10.143.19.203��7001/faces/attachment/download.xhtml?appno=" + AppFormNo);    //�ļ�URL  TODO ������
                reqRecord.setStdkhxm(recordSet.getString("name"));    //�ͻ�����
                reqRecord.setStdzjlx(recordSet.getString("idtype"));    //֤������
                reqRecord.setStdzjhm(recordSet.getString("id"));    //֤������
                reqRecord.setStdlxfs(recordSet.getString("phone1"));    //��ϵ��ʽ
                reqRecord.setStdxb(recordSet.getString("gender"));    //�Ա�
                reqRecord.setStdcsrq(recordSet.getString("birthday"));    //��������
                reqRecord.setStdgj("CHN");    //����
                reqRecord.setStdkhxz(recordSet.getString("clienttype"));    //�ͻ����� TODO
                reqRecord.setStdhyzk(recordSet.getString("marriagestatus"));    //����״��
                reqRecord.setStdjycd(recordSet.getString("edulevel"));    //�����̶�
                reqRecord.setStdkhly("99");    //�ͻ���Դ
                reqRecord.setStdhjszd(recordSet.getString("residenceadr"));    //�������ڵ�
                reqRecord.setStdhkxz(recordSet.getString("residenceadr"));    //��������
                reqRecord.setStdrhzxqk("6");    //�����������
                reqRecord.setStdfdyqqs("0");    //���ż�¼�����۷��������������
                reqRecord.setStdffdyqqs("0");    //���ż�¼�����۷Ƿ��������������
                reqRecord.setStdjtdz(recordSet.getString("currentaddress"));    //��ͥ��ַ
                reqRecord.setStdjtdzdh(recordSet.getString("phone2"));    //��ͥ��ַ�绰
                reqRecord.setStdzzxz("9");    //סլ����
                reqRecord.setStdzy(recordSet.getString("clienttype"));    //ְҵ TODO
                reqRecord.setStdzw(recordSet.getString("title"));    //ְ��
                reqRecord.setStdzc(recordSet.getString("qualification"));    //ְ��
                reqRecord.setStdgzdw(recordSet.getString("company"));    //������λ
                reqRecord.setStdsshy("U");    //������ҵ
                reqRecord.setStdszqyrs("0");    //������ҵ����

                String servyears = recordSet.getString("servfrom");
                if (servyears == null) {
                    servyears = "0000";
                } else {
                    int length = servyears.trim().length();
                    switch (length) {
                        case 1:
                            servyears = "0" + servyears + "00";
                            break;
                        case 2:
                            servyears = servyears + "00";
                            break;
                        case 0:
                            servyears = "0000";
                            break;
                        default:
                            servyears = "0000";
                    }
                }
                reqRecord.setStdgznx(servyears);    //Ŀǰ������������
                reqRecord.setStdlxr(recordSet.getString("linkman"));    //��ϵ��
                reqRecord.setStdlxrdh(recordSet.getString("linkmanphone1"));    //��ϵ�˵绰
                reqRecord.setStdgrysr(recordSet.getString("monthlypay"));    //����������
                reqRecord.setStdjtwdsr("0");    //��ͥ�ȶ�����
                reqRecord.setStdzwzc("0");    //ÿ������ծ��֧��
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.getInstance().releaseConnection(conn);
        }

        return reqRecord;
    }
}
