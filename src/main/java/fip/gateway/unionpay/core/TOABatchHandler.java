package fip.gateway.unionpay.core;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 12-2-23
 * Time: обнГ3:17
 * To change this template use File | Settings | File Templates.
 */
public abstract class TOABatchHandler<T> extends TOAHandler{
    public abstract   List<T> getToaDetailList() ;
}
