package fip.gateway.sbs.txn.Taa56;


import fip.gateway.sbs.CtgManager;
import fip.gateway.sbs.core.SBSRequest;
import fip.gateway.sbs.core.SBSResponse4SingleRecord;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-6-14
 * Time: обнГ3:00
 * To change this template use File | Settings | File Templates.
 */
public class Taa56Handler {

    public void run(SBSRequest request, SBSResponse4SingleRecord response) {
        CtgManager ctgManager = new CtgManager();
        ctgManager.processSingleResponsePkg(request, response);
    }
}
