package fip.view.grmf;

import fip.gateway.newcms.controllers.T100103CTL;
import fip.gateway.newcms.controllers.T100104CTL;
import fip.gateway.newcms.domain.T100103.T100103ResponseRecord;
import fip.gateway.newcms.domain.T100104.T100104RequestList;
import fip.gateway.newcms.domain.T100104.T100104RequestRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.math.BigDecimal;
import java.util.List;

/**
 * 查询信贷系统中 个人买方信贷 的当前代扣数据
 * User: zhanrui
 * Date: 2011-1-27
 * Time: 10:59:40
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class GmPreRepayQueryAction {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

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

    //==========================================================================

    @PostConstruct
    public void init() {
        initAmt();
        query();
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

    private void initAmt() {
        totalamt = new BigDecimal(0);
        totalPrincipalAmt = new BigDecimal(0);
        totalInterestAmt = new BigDecimal(0);
        totalFxjeAmt = new BigDecimal(0);
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
