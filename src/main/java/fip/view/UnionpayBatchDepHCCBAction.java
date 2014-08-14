package fip.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-11-18
 * Time: 12:52:46
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
public class UnionpayBatchDepHCCBAction extends UnionpayBatchAction implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(UnionpayBatchDepHCCBAction.class);

    @PostConstruct
    public void init() {
        super.init();
    }
    protected void initDataList(){
        super.initBaseDataList();
    }

}

