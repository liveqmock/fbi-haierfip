package fip.batch.crontask;

/**
 * 后台自动代扣处理.
 * User: zhanrui
 * Date: 12-12-3
 * Time: 下午4:11
 */
public interface AutoCutpayManager {
    void obtainBills();
    void performCutpayTxn();
    void performResultQueryTxn();
    void writebackBills();
    void archiveBills();
}
