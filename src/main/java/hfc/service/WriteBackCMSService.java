package hfc.service;

import fip.gateway.newcms.domain.T201001.T201001Request;
import fip.repository.dao.XfappMapper;
import fip.repository.model.Xfapp;
import hfc.common.CmsAppStatusEnum;
import hfc.common.ConstSqlString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.platform.db.ConnectionManager;
import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-12
 * Time: 下午10:59
 * To change this template use File | Settings | File Templates.
 */
@Service("writeBackCMSService")
public class WriteBackCMSService {

    private Log logger = LogFactory.getLog(this.getClass());
    @Autowired
    private XfappMapper xfappMapper;

    public Xfapp getXfappByPkid(String pkid) {

        return xfappMapper.selectByPrimaryKey(pkid);
    }

    // appstatus==‘1’（申请提交）可发送
    public boolean isSendable(Xfapp xfapp) {
        if (xfapp != null && CmsAppStatusEnum.NEED_SEND.getCode().equalsIgnoreCase(xfapp.getAppstatus())) {
            return true;
        }
        return false;
    }

    // update appstatus
    public int updateAppStatus(Xfapp xfapp, String status) {
        int rtnCnt = 0;
        if (xfapp != null) {
            xfapp.setAppstatus(status);
            rtnCnt = xfappMapper.updateByPrimaryKey(xfapp);
        }
        return rtnCnt;
    }

    public List<T201001Request> getWriteBackRecords() {
        T201001Request reqRecord = null;
        List<T201001Request> requestList = null;
        RecordSet recordSet = qryRecordsBySql(ConstSqlString.WRITE_BACK_RECORDS_SQL);
        if (recordSet != null || recordSet.getRecordCount() >= 1) {
            requestList = new ArrayList<T201001Request>();
            while (recordSet.next()) {
                reqRecord = new T201001Request();
                reqRecord.initHeader("0200", "201001", "2");
                reqRecord.setStdtermtrc(new SimpleDateFormat("hhmmss").format(new Date()));    //TODO   流水号
                reqRecord.setStdsqdh(recordSet.getString("appno"));    //申请单号
                reqRecord.setStdsqlsh(recordSet.getString("pkid")); // 申请流水号
                reqRecord.setStdurl("http://10.143.19.203:7001/faces/attachment/download.xhtml?appno=" + recordSet.getString("appno"));    //文件URL
                reqRecord.setStdkhxm(recordSet.getString("name"));    //客户姓名
                reqRecord.setStdzjlx(recordSet.getString("idtype"));    // 证件类型
                reqRecord.setStdzjhm(recordSet.getString("id"));          // 证件号码
                reqRecord.setStdlxfs(recordSet.getString("phone1"));    //联系方式
                reqRecord.setStdxb(recordSet.getString("gender"));    //  性别
                reqRecord.setStdcsrq(recordSet.getString("birthday"));    // 出生日期
                reqRecord.setStdgj("CHN");    // TODO 国籍
                reqRecord.setStdkhxz("0");    // TODO 客户性质
                /**
                 * 0-其他个人
                 1-	本行行员

                 */
                reqRecord.setStdhyzk(recordSet.getString("marriagestatus"));    // 婚姻状况
                reqRecord.setStdjycd(recordSet.getString("edulevel"));    // 教育程度
                reqRecord.setStdkhly("99");    //  客户来源 99-其他
                /**
                 01-	朋友介绍
                 02-	传单海报
                 03-	媒体广告
                 04-	商户推荐
                 05-	专业推荐
                 06-	网络
                 99-其他
                 */
                reqRecord.setStdhjszd(recordSet.getString("residenceadr"));    //户籍所在地
                int adr = Integer.parseInt(recordSet.getString("residenceadr"));
                switch (adr) {
                    case 1://  本地
                        reqRecord.setStdhkxz("1");
                        break;
                    case 2:   //  外地 //  本地 // 外籍或港澳台
                    case 3:
                        reqRecord.setStdhkxz("4");
                        break;
                    default: // 其他
                        reqRecord.setStdhkxz("5");
                        break;
                }
                /**
                 * 1-	本地户口且居住本地5年以上
                 2-	本地户口且居住本地5年以下
                 3-	非本地户口但居住本地5年以上
                 4-	非本地户口但居住本地5年以下
                 5-	其他
                 */
                reqRecord.setStdrhzxqk("6");    // 人行征信情况
                /**
                 * 0-贷款或信用卡有六期以上逾期拖欠记录
                 1-贷款或信用卡有三期以上、六期以下逾期拖欠记录
                 2-贷款或信用卡有三期以下逾期拖欠记录
                 3-从未借款
                 4-有借款正在偿还且信用
                 6-	有借款已还清且信用良好

                 */
                reqRecord.setStdfdyqqs("0");    //  征信记录中零售房贷最高逾期期数  0
                reqRecord.setStdffdyqqs("0");    //  征信记录中零售非房贷最高逾期期数  0
                reqRecord.setStdjtdz(recordSet.getString("currentaddress"));    //家庭地址
                reqRecord.setStdjtdzdh(recordSet.getString("phone2"));    //家庭地址电话
                reqRecord.setStdzzxz(recordSet.getString("chousingsts"));    // 住宅性质
                //职业
                int zy = Integer.parseInt(recordSet.getString("clienttype"));
                switch (zy) {
                    case 1:
                        reqRecord.setStdzy("0");
                        break;
                    case 2:
                        reqRecord.setStdzy("3");
                        break;
                    case 3:
                        reqRecord.setStdzy("4");
                        break;
                    case 4:
                        reqRecord.setStdzy("X");
                        break;
                    default:
                        reqRecord.setStdzy("Y");
                        break;
                }
                reqRecord.setStdzw(recordSet.getString("title"));    //  职务
                reqRecord.setStdzc(recordSet.getString("qualification"));    //  职称
                reqRecord.setStdgzdw(recordSet.getString("company"));    //工作单位
                reqRecord.setStdsshy("U");    //  所属行业    U
                /**
                 * A-	农、林、牧、渔业
                 B-	采掘业
                 C-	制造业
                 D-	电力、燃气及水的生产和供应业
                 E-	建筑业
                 F-	交通运输、仓储和邮政业
                 G-	信息传输、计算机服务和软件业
                 H-	批发和零售业
                 I-	住宿和餐饮业
                 J-	金融业
                 K-	房地产业
                 L-	租赁和商务服务业
                 M-	科学研究、技术服务业和地质勘察业
                 N-	水利、环境和公共设施管理业
                 O-	居民服务和其他服务业
                 P-	教育
                 Q-	卫生、社会保障和社会福利业
                 R-	文化、体育和娱乐业
                 S-	公共管理和社会组织
                 T-	国际组织
                 U-	未知
                 */
                reqRecord.setStdszqyrs("2");    //  所在企业人数       2
                /**
                 * 1-  	500人以上
                 2-    	100-500人
                 3-	  10-99人
                 4-	  1-9人
                 */
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
                reqRecord.setStdgznx(servyears);    //目前工作持续年限
                reqRecord.setStdlxr(recordSet.getString("linkman"));    //联系人
                reqRecord.setStdlxrdh(recordSet.getString("linkmanphone1"));    //联系人电话/
                String grysr = recordSet.getString("monthlypay");
                reqRecord.setStdgrysr(grysr);    //个人月收入
                String pgrysr = recordSet.getString("PMONTHLYPAY");
                double dl_pgrysr = (pgrysr == null) ? 0 : Double.parseDouble(pgrysr);
                double jtysr = Double.parseDouble(grysr) + dl_pgrysr;
                reqRecord.setStdjtwdsr(String.format("%.2f", jtysr));    //  家庭稳定收入 个人或与配偶合计
                reqRecord.setStdzwzc(recordSet.getString("MONPAYAMT"));    //  每月其他债务支出
                reqRecord.setStddkzje(recordSet.getString("APPAMT"));     //贷款总金额
                reqRecord.setStddkqx(String.valueOf(recordSet.getString("DIVID")));  //贷款期限
                reqRecord.setStdftfs(recordSet.getString("SHARETYPE"));     //分摊方式
                reqRecord.setStdkhsxfl(String.valueOf(Double.parseDouble(recordSet.getString("COMMISSIONRATE")) / 1000));   // 客户手续费率
                reqRecord.setStdshsxfl(String.valueOf(Double.parseDouble(recordSet.getString("SHCOMMISSIONRATE")) / 100)); //商户手续费率
                reqRecord.setStdhkr(recordSet.getString("COMPAYDATE"));    // 还款日
                reqRecord.setStdkhjl(recordSet.getString("operid"));   // 客户经理
                reqRecord.setStdspmc(recordSet.getString("commnmtype"));  //商品名称
                reqRecord.setStdspxh(recordSet.getString("commnmtype"));    //商品型号
                double spamt = Double.parseDouble(recordSet.getString("commamt"));
                int spnum = Integer.parseInt(recordSet.getString("commnum"));
                String spdj = String.format("%.2f", spamt / spnum);
                reqRecord.setStdspdj(spdj); // 商品单价
                reqRecord.setStdspsl(String.valueOf(spnum));  // 商品数量
                reqRecord.setStdsfk(recordSet.getString("RECEIVEAMT"));  // 首付款
                reqRecord.setStdhkzh(recordSet.getString("BANKACTNO"));// 还款账号
                requestList.add(reqRecord);
            }
        }
        return requestList;
    }

    private RecordSet qryRecordsBySql(String sql) {
        DatabaseConnection conn = null;
        RecordSet recordSet = null;
        try {
            conn = ConnectionManager.getInstance().getConnection();
            logger.info(sql);
            recordSet = conn.executeQuery(sql);
            if (recordSet != null && recordSet.getRecordCount() == 0) {
                logger.info("未找到要查询的信息" + sql);
               // throw new RuntimeException("未找到要查询的信息");
            }
            return recordSet;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ConnectionManager.getInstance().releaseConnection(conn);
        }
    }
}
