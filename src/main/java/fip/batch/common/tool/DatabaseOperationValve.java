package fip.batch.common.tool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 操作闸口参数由数据库表维护.
 * User: zhanrui
 * Date: 12-12-5
 * Time: 上午9:21
 * To change this template use File | Settings | File Templates.
 */

@Component
public class DatabaseOperationValve implements OperationValve {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseOperationValve.class);

    @Override
    public boolean isOpen(String valveID) {
        try {
            //TODO
            return false;
        } catch (Exception e) {
            logger.error("读取参数文件错误", e);
            return false;
        }
    }

    @Override
    public void openValve(String valveID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void closeValve(String valveID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
