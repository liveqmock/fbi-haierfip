package hfc.view.consume;

import hfc.service.EnumService;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-10
 * Time: ÏÂÎç5:16
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean(name = "enumaction")
@RequestScoped
public class EnumAction implements Serializable {
    @ManagedProperty(value = "#{enumService}")
    private EnumService enumService;

    public String getEnumLbl() {
        FacesContext context = FacesContext.getCurrentInstance();
        String enutype = context.getExternalContext().getRequestParameterMap().get("enutype");
        String enuitemvalue = context.getExternalContext().getRequestParameterMap().get("enuitemvalue");
        return enumService.getEnumLabel(enutype, enuitemvalue);
    }

    public EnumService getEnumService() {
        return enumService;
    }

    public void setEnumService(EnumService enumService) {
        this.enumService = enumService;
    }
}
