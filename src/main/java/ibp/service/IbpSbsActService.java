package ibp.service;

import ibp.repository.dao.IbpSbsActMapper;
import ibp.repository.model.IbpSbsAct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by lenovo on 2014-11-05.
 */
@Service
public class IbpSbsActService {
    private static final Logger logger = LoggerFactory.getLogger(IbpSbsActService.class);

    @Autowired
    private IbpSbsActMapper ibpSbsActMapper;

    public List<IbpSbsAct> qrySbsActByName(String actnam) {
        return ibpSbsActMapper.qrySbsActToTrans("%" + actnam + "%");
    }

}
