package hfc.service.fenqi;

import fip.repository.dao.PtenudetailMapper;
import fip.repository.dao.XfappMapper;
import fip.repository.model.Ptenudetail;
import fip.repository.model.PtenudetailExample;
import fip.repository.model.PtenudetailKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-17
 * Time: 上午11:59
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ListBeanService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private PtenudetailMapper ptenudetailMapper;
    /**
     * 获取枚举表枚举list*/
    public List<SelectItem> getEnumOptions(String enumType) {
        return createEnumOptions(enumType,null);
    }
    public List<SelectItem> getEnumOptions(String enumType,String newAdd) {
        return createEnumOptions(enumType,newAdd);
    }

    public List<SelectItem> createEnumOptions(String enumtype,String newAdd) {
        List<SelectItem> enumOptions = new ArrayList<SelectItem>();
        if (newAdd != null) {
            SelectItem siNew = new SelectItem("",newAdd);
            enumOptions.add(siNew);
        }
        PtenudetailExample ptenudetailExample = new PtenudetailExample();
        ptenudetailExample.clear();
        ptenudetailExample.createCriteria().andEnutypeEqualTo(enumtype);
        ptenudetailExample.setOrderByClause("dispno");
        List<Ptenudetail> ptenudtlList = ptenudetailMapper.selectByExample(ptenudetailExample);
        if (ptenudtlList.size() > 0) {
            for (Ptenudetail ptenudetail:ptenudtlList) {
                SelectItem si = new SelectItem(ptenudetail.getEnuitemvalue(),ptenudetail.getEnuitemlabel());
                enumOptions.add(si);
            }
        }
        return enumOptions;
    }

    public PtenudetailMapper getPtenudetailMapper() {
        return ptenudetailMapper;
    }

    public void setPtenudetailMapper(PtenudetailMapper ptenudetailMapper) {
        this.ptenudetailMapper = ptenudetailMapper;
    }
}
