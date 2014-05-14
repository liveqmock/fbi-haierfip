package skyline.service.common;

import fip.repository.dao.PtenudetailMapper;
import fip.repository.model.Ptenudetail;
import fip.repository.model.PtenudetailExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 11-4-22
 * Time: 下午2:32
 * To change this template use File | Settings | File Templates.
 */
@Service
public class ToolsService {

    @Autowired
    private PtenudetailMapper enudetailMapper;

    /**
     * 查询枚举明细内容
     *
     * @param enuid
     * @return
     */
    public List<Ptenudetail> selectEnuDetail(String enuid) {
        PtenudetailExample example = new PtenudetailExample();
        example.createCriteria().andEnutypeEqualTo(enuid);
        example.setOrderByClause(" dispno ");
        return enudetailMapper.selectByExample(example);
    }

    /**
     * 查询枚举值
     * @param enuid
     * @return
     */
    public List<String> selectEnuItemValue(String enuid) {
        List<String> items = new ArrayList<String>();
        List<Ptenudetail> enus = selectEnuDetail(enuid);
        for (Ptenudetail enu : enus) {
            items.add(enu.getEnuitemvalue());
        }
        return items;
    }

    /**
     * 根据枚举表的内容组下拉菜单
     *
     * @param enuName     枚举名称
     * @param isSelectAll 是否添加全部项选择
     * @param isExpandID  true:正常列表（不包含ID） false：列表中包含ID
     * @return 下拉菜单
     */
    public List<SelectItem> getEnuSelectItemList(String enuName, boolean isSelectAll, boolean isAddBlank, boolean isExpandID) {
        List<Ptenudetail> records = selectEnuDetail(enuName);
        List<SelectItem> items = new ArrayList<SelectItem>();
        SelectItem item;
        if (isSelectAll) {
            item = new SelectItem("", "全部");
            items.add(item);
        }
        if (isAddBlank) {
            item = new SelectItem("", "");
            items.add(item);
        }
        for (Ptenudetail record : records) {
            if (isExpandID) {
                item = new SelectItem(record.getEnuitemvalue(), record.getEnuitemvalue() + " " + record.getEnuitemlabel());
            } else {
                item = new SelectItem(record.getEnuitemvalue(), record.getEnuitemlabel());
            }
            items.add(item);
        }
        return items;
    }
}
