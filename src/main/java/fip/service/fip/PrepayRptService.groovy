package fip.service.fip

import org.apache.commons.dbcp.BasicDataSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-9-6
 * Time: обнГ1:29
 * To change this template use File | Settings | File Templates.
 */
@Service
class PrepayRptService {
    String name

    @Autowired
    private BasicDataSource fipDataSource

    public String getName(){

        name = "my name"
        return name
    }
}
