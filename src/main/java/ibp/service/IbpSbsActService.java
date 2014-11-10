package ibp.service;

import ibp.repository.dao.IbpSbsactMapper;
import ibp.repository.model.IbpSbsact;
import ibp.repository.model.IbpSbsactExample;
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
    private IbpSbsactMapper ibpSbsactMapper;

    public List<IbpSbsact> qrySbsActByName(String actnam) {
        return ibpSbsactMapper.qrySbsactToTrans("%" + actnam + "%");
    }

}
