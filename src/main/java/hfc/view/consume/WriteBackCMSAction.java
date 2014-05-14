package hfc.view.consume;

import fip.common.utils.MessageUtil;
import fip.gateway.newcms.domain.T201001.T201001Request;
import fip.gateway.newcms.domain.T201001.T201001Response;
import fip.gateway.newcms.domain.common.BaseBean;
import fip.repository.model.Xfapp;
import hfc.common.CmsAppStatusEnum;
import hfc.service.WriteBackCMSService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pgw.NewCmsManager;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhangxiaobo
 * Date: 11-8-12
 * Time: 下午5:18
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class WriteBackCMSAction {
    private Log logger = LogFactory.getLog(this.getClass());
    @ManagedProperty(value = "#{writeBackCMSService}")
    private WriteBackCMSService writeBackCMSService;
    private List<T201001Request> requestList;
    private T201001Request[] selectedRecords;
    private T201001Request selectedRecord;

    @PostConstruct
    public void init() {
            requestList = writeBackCMSService.getWriteBackRecords();
            /*if (requestList == null || requestList.isEmpty()) {
                MessageUtil.addInfo("没有查询到待上传记录! ");
            }*/
    }

    // 全部发送
    public String onSendRequestAll() {
        for (T201001Request request : requestList) {
            if (!sendOneRequest(request)) {
                MessageUtil.addError("申请上传失败,申请单号为：" + request.getStdsqdh());
            }
        }
        MessageUtil.addInfo("数据上传结束！");
        init();
        return null;
    }

    // 多笔发送
    public String onSendRequestMulti() {
        if(selectedRecords == null || selectedRecords.length <= 0){
            MessageUtil.addInfo("请选择至少一项纪录！");
            return null;
        }
        for (T201001Request request : selectedRecords) {
            if (!sendOneRequest(request)) {
                MessageUtil.addError("申请上传失败,申请单号为：" + request.getStdsqdh());
            }
        }
        MessageUtil.addInfo("数据上传结束！");
        init();
        return null;
    }

    // 上传单笔申请
    private boolean sendOneRequest(T201001Request request) {
        try {
            Xfapp xfapp = writeBackCMSService.getXfappByPkid(request.getStdsqlsh());
            if (writeBackCMSService.isSendable(xfapp)) {
                String strXml = request.toXml();
                logger.info(strXml);
                NewCmsManager ncm = new NewCmsManager();
                String responseStr = ncm.doPostXml(strXml);
                logger.info(responseStr);
                T201001Response response = (T201001Response) BaseBean.toObject(T201001Response.class, responseStr);
                if ("AAAAAAA".equalsIgnoreCase(response.getStd400mgid())) {
                    writeBackCMSService.updateAppStatus(xfapp, CmsAppStatusEnum.SEND_SUCCESS.getCode());
                    return true;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("发送异常");
        }
        return false;
    }

    public List<T201001Request> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<T201001Request> requestList) {
        this.requestList = requestList;
    }

    public T201001Request[] getSelectedRecords() {
        return selectedRecords;
    }

    public void setSelectedRecords(T201001Request[] selectedRecords) {
        this.selectedRecords = selectedRecords;
    }

    public T201001Request getSelectedRecord() {
        return selectedRecord;
    }

    public void setSelectedRecord(T201001Request selectedRecord) {
        this.selectedRecord = selectedRecord;
    }

    public WriteBackCMSService getWriteBackCMSService() {
        return writeBackCMSService;
    }

    public void setWriteBackCMSService(WriteBackCMSService writeBackCMSService) {
        this.writeBackCMSService = writeBackCMSService;
    }
}
