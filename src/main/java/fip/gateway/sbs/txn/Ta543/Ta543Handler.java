package fip.gateway.sbs.txn.Ta543;


import fip.gateway.sbs.SbsCtgManager;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;

/**
 * 贷款还款
 * User: zhanrui
 * Date: 11-6-14
 * Time: 下午3:00
 * To change this template use File | Settings | File Templates.
 */
public class Ta543Handler {
    SbsCtgManager ctgManager;

    public Ta543Handler() {
        ctgManager = new SbsCtgManager();
        ctgManager.init();
    }
    public void shoudown() {
        ctgManager.shutdown();
    }

    public void run(SBSRequest request, SBSResponse4SingleRecord response) {
        ctgManager.processSingleResponsePkg(request, response);
    }
}
