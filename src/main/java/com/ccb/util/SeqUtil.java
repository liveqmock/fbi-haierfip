package com.ccb.util;

import java.util.Date;

import pub.platform.db.SequenceManager;
import pub.platform.utils.StringUtils;

public class SeqUtil {

    // 生成内部序号
    // 
    public static String getAPPSEQ() {
        String temp = "" + SequenceManager.nextID(CcbLoanConst.XFAPP_PKID);
        String rtn = StringUtils.toDateFormat(new Date(), "yyyyMMdd") + StringUtils.addPrefix(temp, "0", 8);
        return rtn;
    }

    // 生成流水序号
    public static String getTaskSeq() {
        String temp = "" + SequenceManager.nextID(CcbLoanConst.TASKSEQ);
        String rtn = StringUtils.toDateFormat(new Date(), "yyyyMMdd") + StringUtils.addPrefix(temp, "0", 7);
        return rtn;
    }
    public static void main(String[] args) throws Exception {
//        System.out.println(getCoop());
    }
}
