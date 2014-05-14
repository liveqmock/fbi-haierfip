package hfc.service;

import fip.repository.dao.PtenudetailMapper;
import fip.repository.model.Ptenudetail;
import fip.repository.model.PtenudetailExample;
import fip.repository.model.PtenudetailKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-10
 * Time: 下午5:07
 * To change this template use File | Settings | File Templates.
 */
@Service
public class EnumService {
    @Autowired
    private PtenudetailMapper ptenudetailmapper;

    /**
     * 根据主键获取Label值
     */
    public String getEnumLabel(String enutype, String enuitemvalue) {
        PtenudetailKey enukey = new PtenudetailKey();
        enukey.setEnutype(enutype);
        enukey.setEnuitemvalue(enuitemvalue);
        Ptenudetail ptenudetail = ptenudetailmapper.selectByPrimaryKey(enukey);
        return ptenudetail.getEnuitemlabel();
    }

    /**
     * 根据enutype获取value,Label值对
     */
    public HashMap<String, String> getEnumValueLabel(String enutype) {
        PtenudetailExample example = new PtenudetailExample();
        example.createCriteria().andEnutypeEqualTo(enutype);
        List<Ptenudetail> ptenudetailList = ptenudetailmapper.selectByExample(example);
        HashMap<String, String> valueLabels = null;
        if (ptenudetailList != null && !ptenudetailList.isEmpty()) {
            valueLabels = new HashMap<String, String>();
            for (Ptenudetail p : ptenudetailList) {
                valueLabels.put(p.getEnuitemvalue(), p.getEnuitemlabel());
            }
        }
        return valueLabels;
    }
}
