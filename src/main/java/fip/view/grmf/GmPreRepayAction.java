package fip.view.grmf;

import fip.gateway.newcms.controllers.T100103CTL;
import fip.gateway.newcms.controllers.T100104CTL;
import fip.gateway.newcms.domain.T100103.T100103ResponseRecord;
import fip.gateway.newcms.domain.T100104.T100104RequestList;
import fip.gateway.newcms.domain.T100104.T100104RequestRecord;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.db.ConnectionManager;
import pub.platform.db.DatabaseConnection;
import pub.platform.db.RecordSet;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 查询信贷系统中 个人买方信贷 的当前代扣数据  （提前还款）
 * User: zhanrui
 * Date: 2011-1-27
 * Time: 10:59:40
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class GmPreRepayAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    List<GmRepayDetlBean> dbrecords;
    List<T100103ResponseRecord> responseMFList;

    T100103CTL t100103ctl = new T100103CTL();

    private int totalcount;
    private BigDecimal totalamt;
    private BigDecimal totalPrincipalAmt;   //本金
    private BigDecimal totalInterestAmt;    //利息
    private BigDecimal totalFxjeAmt;    //罚息

    private String contractno;
    private String clientname;

    private T100103ResponseRecord[] selectedRecords;
    private T100103ResponseRecord selectedRecord;

    private String tableTitle;

    public List<GmRepayDetlBean> getDbrecords() {
        return dbrecords;
    }

    public List<T100103ResponseRecord> getResponseMFList() {
        return responseMFList;
    }

    public void setResponseMFList(List<T100103ResponseRecord> responseMFList) {
        this.responseMFList = responseMFList;
    }

    public int getTotalcount() {
        return totalcount;
    }

    public void setTotalcount(int totalcount) {
        this.totalcount = totalcount;
    }

    public BigDecimal getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(BigDecimal totalamt) {
        this.totalamt = totalamt;
    }

    public BigDecimal getTotalPrincipalAmt() {
        return totalPrincipalAmt;
    }

    public void setTotalPrincipalAmt(BigDecimal totalPrincipalAmt) {
        this.totalPrincipalAmt = totalPrincipalAmt;
    }

    public BigDecimal getTotalInterestAmt() {
        return totalInterestAmt;
    }

    public void setTotalInterestAmt(BigDecimal totalInterestAmt) {
        this.totalInterestAmt = totalInterestAmt;
    }

    public BigDecimal getTotalFxjeAmt() {
        return totalFxjeAmt;
    }

    public void setTotalFxjeAmt(BigDecimal totalFxjeAmt) {
        this.totalFxjeAmt = totalFxjeAmt;
    }

    public String getContractno() {
        return contractno;
    }

    public void setContractno(String contractno) {
        this.contractno = contractno;
    }

    public String getClientname() {
        return clientname;
    }

    public void setClientname(String clientname) {
        this.clientname = clientname;
    }

    public T100103ResponseRecord[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(T100103ResponseRecord[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public T100103ResponseRecord getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(T100103ResponseRecord selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public String getTableTitle() {
        return tableTitle;
    }

    public void setTableTitle(String tableTitle) {
        this.tableTitle = tableTitle;
    }
//==========================================================================

    @PostConstruct
    public void init() {
        selectRecords();
        initAmt();
        sumRecords();
    }

    public String query() {

        try {
            responseMFList = t100103ctl.getAllGRMFRecords();
        } catch (Exception e) {
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    e.getMessage(), null));
        }
        return null;
    }

    public String doGetCmsRecord() {
        FacesContext context = FacesContext.getCurrentInstance();
        try {
            if (!checkCurrentRecordStatus()) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "存在未处理完记录。", null));
                return null;
            }
            responseMFList = t100103ctl.getAllGRMFRecords();
            if (responseMFList.size() == 0) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "信贷系统中无待处理记录。", null));
                return null;
            }
            //dbrecords = transform(responseMFList);
            insertRecords();
            selectRecords();
            sumRecords();
        } catch (Exception e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), null));
        }
        return null;
    }

    /**
     * 将转换后的接口数据存入本地表中
     *
     * @return
     */
    private void insertRecords() {

        String inputdate = new SimpleDateFormat("yyyyMMdd").format(new Date());

        int rtn = 0;
        ConnectionManager cm = null;
        DatabaseConnection conn = null;
        RecordSet rs = null;
        PreparedStatement ps = null;
        try {
            cm = ConnectionManager.getInstance();
            conn = cm.getConnection();

            String sql = "select max(seqno) from fdcutpaydetl where substr(seqno,1,8) = '" + inputdate + "'";
            rs = conn.executeQuery(sql);

            int maxno = 0;
            if (rs.next()) {
                String max = rs.getString(0);
                if (max != null) {
                    maxno = Integer.parseInt(max.substring(8));
                }
            }

            conn.setAuto(false);

            sql = "insert into gmrepaydetl " +
                    "     (seqno, " +
                    "     iouno, " +
                    "     issueno, " +
                    "     contractno, " +
                    "     clientno, " +
                    "     clientname, " +
                    "     loanactno, " +
                    "     repaymentactno, " +
                    "     repaymentamt, " +
                    "     principalamt, " +
                    "     interestamt, " +
                    "     penaltyamt, " +
                    "     otheramt, " +
                    "     repaymentdate,  " +
                    "     regioncd, " +
                    "     bankcd, " +
                    "     billstatus, " +
                    "     createtime, " +
                    "     failreason, " +
                    "     remark, " +
                    "     preflag, " +
                    "     journalno) " +
                    " values " +
                    " (?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,?,?,?,?," +
                    "?,?)";

            logger.info("sql=" + sql);
            ps = conn.getPreparedStatement(sql);

            int count = 0;
            for (T100103ResponseRecord record : responseMFList) {
                //TODO 地区字段为临时方案 待修正
                String tmpStr = record.getStddqh();
                String regioncdTmp, bankcdTmp, nameTmp;
                if (tmpStr == null || tmpStr.equals("null")) {
                    continue;
                } else {
                    String[] code = tmpStr.split("-");
                    regioncdTmp = code[0].trim();
                    bankcdTmp = code[1].trim();
                }

                count++;
                ps.setString(1, inputdate + StringUtils.leftPad(String.valueOf(maxno + count), 7, '0'));
                ps.setString(2, record.getStdjjh()); //借据号
                ps.setString(3, record.getStdqch()); //期次号
                ps.setString(4, record.getStdhth());  //合同号
                ps.setString(5, record.getStdkhh());  //客户号
                ps.setString(6, record.getStdkhmc()); //客户名称
                ps.setString(7, record.getStddkzh());//贷款帐号（内部帐号）
                ps.setString(8, record.getStdhkzh());//还款帐号（银行帐号）

                ps.setDouble(9, Double.valueOf(record.getStdhkje()));//总还款金额
                ps.setDouble(10, Double.valueOf(record.getStdhkbj()));//本金
                ps.setDouble(11, Double.valueOf(record.getStdhklx()));//利息
                ps.setDouble(12, 0);//罚息  (提前还款无此接口字段)
                ps.setDouble(13, 0);//其他

                ps.setString(14, record.getStdjhhkr());//计划还款日 YYYYMMDD (提前还款时 放入审批号 用于返还信贷系统)
                ps.setString(15, regioncdTmp);//客户地区编号 青岛：0532    enum=FDRegionCode
                ps.setString(16, bankcdTmp);//客户开户银行代码 中行：104  建行：105   enum=Bank

                ps.setString(17, "0");//帐单状态               enum=FDBillStatus
//                ps.setDate(18, (java.sql.Date) (new Date()));//房贷系统帐单获取时间  (应用服务器的系统时间)
                ps.setDate(18, new java.sql.Date(new Date().getTime()));    //房贷系统帐单获取时间  (应用服务器的系统时间)

                ps.setString(19, "");//扣款失败原因
                ps.setString(20, "");//备注
                //提前还款
                ps.setString(21, "1");//提前还款标志            enum=FDCutpayType   ‘0’=正常还款  ‘1’=提前还款
                ps.setString(22, "");//通讯包流水号（ 8位日期+8位顺序号）
                //end

                ps.addBatch();
            }

            int[] results = ps.executeBatch();//执行批处理

            for (int i = 0; i < results.length; i++) {
                if (results[i] < 0) {
                    //throw new RuntimeException("将接口记录存入本地表中时出现错误。");
                }
            }
            conn.commit();

        } catch (Exception e) {
            conn.rollback();
            logger.error("将接口记录存入本地表中时出现错误。", e);
            throw new RuntimeException(e);
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                    conn.getConnection().close();
                } catch (SQLException e) {
                    logger.error("Connection closed error!", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }


    private boolean checkCurrentRecordStatus() {
        return true;
    }

    private void selectRecords() {
        dbrecords = new ArrayList<GmRepayDetlBean>();
        ConnectionManager cm = null;
        DatabaseConnection conn = null;
        RecordSet rs = null;
        String sql = "select t.* from gmrepaydetl t where preflag='1' and billstatus = '0' and bankcd='105'";

        try {
            cm = ConnectionManager.getInstance();
            conn = cm.getConnection();
            rs = conn.executeQuery(sql);
            while (rs.next()){
                GmRepayDetlBean record = new GmRepayDetlBean();
                record.setSeqno(rs.getString("seqno"));
                record.setIouno(rs.getString("iouno"));
                record.setIssueno(rs.getString("issueno"));
                record.setContractno(rs.getString("contractno"));
                record.setClientno(rs.getString("clientno"));
                record.setClientname(rs.getString("clientname"));
                record.setLoanactno(rs.getString("loanactno"));
                record.setRepaymentactno(rs.getString("repaymentactno"));
                record.setRepaymentamt(new BigDecimal(rs.getDouble("repaymentamt")));
                record.setPrincipalamt(new BigDecimal(rs.getDouble("principalamt")));
                record.setInterestamt(new BigDecimal(rs.getDouble("interestamt")));
                record.setPenaltyamt(new BigDecimal(rs.getDouble("penaltyamt")));
                record.setOtheramt(new BigDecimal(rs.getDouble("otheramt")));
                record.setRepaymentdate(rs.getString("repaymentdate"));
                record.setRegioncd(rs.getString("regioncd"));
                record.setBankcd(rs.getString("bankcd"));
                record.setBillstatus(rs.getString("billstatus"));
//                record.setCreatetime(rs.getCalendar("createtime").getTime());
                record.setCreatetime(rs.getCalendar("createtime"));
                record.setFailreason(rs.getString("failreason"));
                record.setRemark(rs.getString("remark"));
                record.setPreflag(rs.getString("preflag"));
                record.setJournalno(rs.getString("journalno"));
                dbrecords.add(record);
            }
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                    conn.getConnection().close();
                } catch (SQLException e) {
                    logger.error("Connection closed error!", e);
                    throw new RuntimeException(e);
                }
            }
        }

    }

    private void initAmt() {
        totalamt = new BigDecimal(0);
        totalPrincipalAmt = new BigDecimal(0);
        totalInterestAmt = new BigDecimal(0);
        totalFxjeAmt = new BigDecimal(0);
    }

    private void sumRecords(){
        this.totalcount = dbrecords.size();
        for (GmRepayDetlBean record : this.dbrecords){
             this.totalamt = this.totalamt.add(record.getRepaymentamt());
        }
        this.tableTitle = "总笔数:"+totalcount+"／总金额:" + totalamt.setScale(2,BigDecimal.ROUND_HALF_UP).toString();
    }

    public String writebackAll() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (selectedRecords.length > 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "回写时出现错误。", "请勿选择明细记录。"));
            return null;
        }
        T100103ResponseRecord[] records = new T100103ResponseRecord[this.responseMFList.size()];
        startWriteBack(this.responseMFList.toArray(records));
        init();
        return null;
    }

    public String writebackMulti() {

        FacesContext context = FacesContext.getCurrentInstance();

        if (selectedRecords.length == 0) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "回写时出现错误。", "未选择明细记录。"));
            return null;
        }

        startWriteBack(selectedRecords);
        init();
        return null;

    }

    private void startWriteBack(T100103ResponseRecord[] detls) {
        FacesContext context = FacesContext.getCurrentInstance();

        try {
            int result = processWriteBack(detls);
            if (result != detls.length) {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "回写结果。", "成功笔数：" + result + "  失败笔数：" + (detls.length - result)));
            } else {
                context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,
                        "回写新信贷系统成功。", "  笔数：" + result));
            }
        } catch (Exception e) {
            logger.error("回写时出现错误。", e);
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "回写时出现错误。", null));
        }
        init();

    }

    /*
    20101020 单笔处理
    查询房贷系统的扣款记录表，对SBS入帐成功的记录进行回写（to 新信贷）
    返回成功处理笔数
     */

    public int processWriteBack(T100103ResponseRecord[] detls) throws Exception {

        int count = 0;

//        T100102CTL t100102ctl = new T100102CTL();
        T100104CTL t100104ctl = new T100104CTL();
//        XfactcutpaydetlDao detlDao = XfactcutpaydetlDaoFactory.create();
//        XfactcutpaydetlPk cutpaydetlPk = new XfactcutpaydetlPk();

        for (T100103ResponseRecord detl : detls) {
/*
            if (!detl.getBillstatus().equals(XFBillStatus.BILLSTATUS_CORE_SUCCESS)) {
                logger.error("状态检查失败" + detl.getJournalno());
                continue;
            }
*/
            boolean txResult = false;
            T100104RequestRecord record = new T100104RequestRecord();
            record.setStdjjh(detl.getStdjjh());
            record.setStdqch(detl.getStdqch());
            record.setStdjhkkr(detl.getStdjhhkr());
            //1-成功 2-失败
            record.setStdkkjg("1");
            T100104RequestList list = new T100104RequestList();
            list.add(record);
            //单笔发送处理
            txResult = t100104ctl.start(list);

            if (txResult) {
//                cutpaydetlPk.setJournalno(detl.getJournalno());
//                detl.setBillstatus(XFBillStatus.FD_WRITEBACK_SUCCESS);
//                detlDao.update(cutpaydetlPk, detl);
                count++;
            }
        }

        return count;
    }


}
