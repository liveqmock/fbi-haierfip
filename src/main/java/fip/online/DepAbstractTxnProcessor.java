package fip.online;

import org.fbi.dep.model.base.TiaXml;
import org.fbi.dep.model.base.ToaXml;
import org.springframework.transaction.annotation.Transactional;


public abstract class DepAbstractTxnProcessor {

    public abstract ToaXml process(TiaXml tia) throws Exception;

    @Transactional
    public ToaXml run(TiaXml tia) throws Exception {
        return process(tia);
    }
}
