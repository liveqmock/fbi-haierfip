package fip.gateway.sbs.txn.T8118;

import fip.gateway.sbs.CtgManager;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-9-22
 * Time: обнГ4:42
 * To change this template use File | Settings | File Templates.
 */
public class T8118Handler {
    public void run(SBSRequest request, SBSResponse4SingleRecord response) {
        CtgManager ctgManager = new CtgManager();
        ctgManager.processSingleResponsePkg(request, response);
    }
}
