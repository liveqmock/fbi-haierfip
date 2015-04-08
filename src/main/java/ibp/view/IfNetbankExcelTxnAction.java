package ibp.view;

import fip.common.SystemService;
import fip.common.constant.BillStatus;
import fip.common.utils.MessageUtil;
import ibp.repository.model.IbpIfNetbnkTxn;
import ibp.repository.model.IbpSbsAct;
import ibp.repository.model.IbpSbsTranstxn;
import ibp.service.IbpNetbankExcelTxnService;
import ibp.service.IbpSbsActService;
import ibp.service.IbpSbsTransTxnService;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import oracle.jdbc.OracleTypeMetaData;
import org.apache.commons.lang.StringUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pub.platform.security.OperatorManager;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.InputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * ���е�����ϸ-EXCEL���룬SBS����
 */
@ManagedBean
@ViewScoped
public class IfNetbankExcelTxnAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(IfNetbankExcelTxnAction.class);

    private IbpIfNetbnkTxn selectedRecord;
    private List<IbpIfNetbnkTxn> detlList = new ArrayList<IbpIfNetbnkTxn>();
    private List<IbpSbsTranstxn> sbsTxnList = new ArrayList<IbpSbsTranstxn>();
    private List<IbpIfNetbnkTxn> filteredDetlList;
    private List<SelectItem> sbsActList = new ArrayList<SelectItem>();

    private BillStatus status = BillStatus.INIT;
    private String sbsAct = "";
    private String sbsActName = "";
    private String sbsOutAct = "801000026131041001";
    private UploadedFile file;
    private String totalamt = "0.00";
    private int cnt = 0;
    // 2017 2301 2033

    @ManagedProperty(value = "#{ibpNetbankExcelTxnService}")
    private IbpNetbankExcelTxnService ibpNetbankExcelTxnService;
    //    @ManagedProperty(value = "#{ibpSbsTransTxnService}")
//    private IbpSbsTransTxnService ibpSbsTransTxnService;
    @ManagedProperty(value = "#{ibpSbsActService}")
    private IbpSbsActService ibpSbsActService;
    private Map<String, String> actMap = new HashMap<String, String>();
    DecimalFormat df = new DecimalFormat("0.00");

    @PostConstruct
    public void init() {
        try {

            initList();
//            sbsTxnList = ibpSbsTransTxnService.qryTodayTrans("N102");
        } catch (Exception e) {
            logger.error("��ʼ��ʱ���ִ���", e);
            FacesContext context = FacesContext.getCurrentInstance();
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "��ʼ��ʱ���ִ���", "�������ݿ�������⡣"));
        }

    }

    public void initList() {
        detlList = ibpNetbankExcelTxnService.qryTxnsByNotBookFlag(BillStatus.ACCOUNT_SUCCESS);
        cnt = detlList.size();
        BigDecimal bd = new BigDecimal(0.00);
        for (IbpIfNetbnkTxn record : detlList) {
            bd = bd.add(new BigDecimal(record.getCramount().replace(",", "")));
        }
        totalamt = bd.toString();

    }

    public void onImp() {
        // TODO ����EXCEL
        if (file == null) {
            MessageUtil.addError("��ѡ��Ҫ�����EXCEL�ļ���");
            return;
        }
        String fileName = file.getFileName();

        try {
            if (fileName.endsWith("xls") || fileName.endsWith("xlsx")) {
                InputStream is = file.getInputstream();
                Workbook rwb = Workbook.getWorkbook(is);
                Sheet st = rwb.getSheet(0);
//                int cCnt = st.getColumns();
                int rCnt = st.getRows();

                ArrayList<IbpIfNetbnkTxn> implList = new ArrayList<IbpIfNetbnkTxn>();
                int impCnt = 0;
                int exitCnt = 0;
                for (int k = 9; k < rCnt; k++) {//��
                    IbpIfNetbnkTxn txn = new IbpIfNetbnkTxn();
                    txn.setPkid(UUID.randomUUID().toString());
//                    for (int i = 0; i < cCnt; i++) {//��
                    String bkSerialNo = st.getCell(12, k).getContents();
                    if (StringUtils.isEmpty(bkSerialNo)) {
                        continue;
                    }
                    txn.setTxdate(st.getCell(0, k).getContents());
                    txn.setTxtime(st.getCell(1, k).getContents());
                    txn.setVouchertype(st.getCell(2, k).getContents());
                    txn.setVoucherid(st.getCell(3, k).getContents());
                    txn.setDbamount(st.getCell(4, k).getContents());
                    txn.setCramount(st.getCell(5, k).getContents());


                    txn.setActtbal(st.getCell(6, k).getContents());
                    txn.setCashtype(st.getCell(7, k).getContents());
                    txn.setInacctname(st.getCell(8, k).getContents());      // �Է�����
                    txn.setInacctid(st.getCell(9, k).getContents());        // �Է��˺�
                    txn.setAbstractstr(st.getCell(10, k).getContents());
                    txn.setRemark(st.getCell(11, k).getContents());
                    txn.setBkserialno(bkSerialNo);     // ��ˮ��
                    txn.setEntserialno(st.getCell(13, k).getContents());
                    txn.setOutacctid(st.getCell(14, k).getContents());
                    txn.setOutacctname(st.getCell(15, k).getContents());
                    txn.setActbank(st.getCell(16, k).getContents());        // �����˻���������

                    if (ibpNetbankExcelTxnService.isExist(txn)) {
                        exitCnt++;
                    } else {
                        implList.add(txn);
                        impCnt++;
                    }
                }
                ibpNetbankExcelTxnService.saveTxns(implList);
                initList();
                rwb.close();
                is.close();
                MessageUtil.addInfo("����" + impCnt + " �ʣ��ظ�" + exitCnt + "�ʡ�");
            } else {
                MessageUtil.addError("�ļ���ʽ����");
                file = null;
                return;
            }
        } catch (Exception e) {
            MessageUtil.addError("����ʧ�ܣ����ݶ�ȡ�쳣��");
            logger.error("����ʧ�ܣ����ݶ�ȡ�쳣��", e);
            file = null;
            return;
        }

    }

    public void onBook() {

    }

    public void onConfirm() {

        if (selectedRecord == null) {
            MessageUtil.addError("����ѡ��һ�ʴ����˼�¼!");
            return;
        }

        if (ibpNetbankExcelTxnService.isConflict(selectedRecord)) {
            MessageUtil.addError("������ͻ��ˢ��ҳ������²�����");
            return;
        }

        try {
            selectedRecord.setSbsactno(sbsAct);
            selectedRecord.setSbsactname(actMap.get(sbsAct));
            selectedRecord.setBookflag(BillStatus.ACCOUNT_PEND.getCode());
            ibpNetbankExcelTxnService.update(selectedRecord);
            sbsAct = "";
            sbsActName = "";
            actMap.clear();
            sbsActList.clear();
        } catch (Exception e) {
            logger.error("���׳����쳣��", e);
            MessageUtil.addError("���׳����쳣!");
            return;
        }
    }


    public void qryActs() {


        if (StringUtils.isEmpty(sbsActName)) {
            MessageUtil.addWarn("��������SBSת���˻�����");
            return;
        }

        if (sbsActName.trim().length() < 2) {
            return;
        }

        sbsActName = sbsActName.trim();
        List<IbpSbsAct> acts = ibpSbsActService.qrySbsActByName(sbsActName);
        actMap.clear();
        sbsActList.clear();

        for (IbpSbsAct act : acts) {
            SelectItem item = new SelectItem(act.getActnum(), act.getActnam() + act.getActnum());
            sbsActList.add(item);
            actMap.put(act.getActnum(), act.getActnam());
        }
        if (sbsActList.isEmpty()) {
            MessageUtil.addError("û�в�ѯ���˻���" + sbsActName);
            return;
        }

    }


    //====================================================================================


    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public String getTotalamt() {
        return totalamt;
    }

    public void setTotalamt(String totalamt) {
        this.totalamt = totalamt;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public IbpIfNetbnkTxn getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(IbpIfNetbnkTxn selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public List<IbpIfNetbnkTxn> getDetlList() {
        return detlList;
    }

    public void setDetlList(List<IbpIfNetbnkTxn> detlList) {
        this.detlList = detlList;
    }

    public List<IbpIfNetbnkTxn> getFilteredDetlList() {
        return filteredDetlList;
    }

    public void setFilteredDetlList(List<IbpIfNetbnkTxn> filteredDetlList) {
        this.filteredDetlList = filteredDetlList;
    }

    public IbpNetbankExcelTxnService getIbpNetbankExcelTxnService() {
        return ibpNetbankExcelTxnService;
    }

    public void setIbpNetbankExcelTxnService(IbpNetbankExcelTxnService ibpNetbankExcelTxnService) {
        this.ibpNetbankExcelTxnService = ibpNetbankExcelTxnService;
    }

    public Map<String, String> getActMap() {
        return actMap;
    }

    public void setActMap(Map<String, String> actMap) {
        this.actMap = actMap;
    }

    public DecimalFormat getDf() {
        return df;
    }

    public void setDf(DecimalFormat df) {
        this.df = df;
    }

    public BillStatus getStatus() {
        return status;
    }

    public void setStatus(BillStatus status) {
        this.status = status;
    }


    /*public IbpSbsTransTxnService getIbpSbsTransTxnService() {
        return ibpSbsTransTxnService;
    }

    public void setIbpSbsTransTxnService(IbpSbsTransTxnService ibpSbsTransTxnService) {
        this.ibpSbsTransTxnService = ibpSbsTransTxnService;
    }
*/
    public List<SelectItem> getSbsActList() {
        return sbsActList;
    }

    public void setSbsActList(List<SelectItem> sbsActList) {
        this.sbsActList = sbsActList;
    }

    public IbpSbsActService getIbpSbsActService() {
        return ibpSbsActService;
    }

    public void setIbpSbsActService(IbpSbsActService IbpSbsActService) {
        this.ibpSbsActService = IbpSbsActService;
    }

    public String getSbsAct() {
        return sbsAct;
    }

    public void setSbsAct(String sbsAct) {
        this.sbsAct = sbsAct;
    }

    public String getSbsActName() {
        return sbsActName;
    }

    public void setSbsActName(String sbsActName) {
        this.sbsActName = sbsActName;
    }

    public String getSbsOutAct() {
        return sbsOutAct;
    }

    public void setSbsOutAct(String sbsOutAct) {
        this.sbsOutAct = sbsOutAct;
    }

    public List<IbpSbsTranstxn> getSbsTxnList() {
        return sbsTxnList;
    }

    public void setSbsTxnList(List<IbpSbsTranstxn> sbsTxnList) {
        this.sbsTxnList = sbsTxnList;
    }
}
