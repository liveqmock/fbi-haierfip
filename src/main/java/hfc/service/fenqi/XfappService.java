package hfc.service.fenqi;

import fip.repository.dao.XfappMapper;
import fip.repository.model.Xfapp;
import fip.repository.model.XfappExample;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-9
 * Time: ÏÂÎç3:47
 * To change this template use File | Settings | File Templates.
 */
@Service
public class XfappService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private XfappMapper xfappmapper;

    public List<Xfapp> selectXfappInfoRecords(String appno, String custname,String appStatus,String idType,String strId) {
        XfappExample xfappexam = new XfappExample();
        xfappexam.clear();
        List<String> appstatusList = new ArrayList<String>();
        appstatusList.add(0,"1");
        appstatusList.add(1,"2");
        appstatusList.add(2,"0");
        XfappExample.Criteria xfappexamCrit = xfappexam.createCriteria();
        xfappexamCrit.andAppstatusIn(appstatusList);
        if (!StringUtils.isEmpty(custname.trim())) {
            xfappexamCrit.andNameLike(custname.trim() + "%");
        }
        if (!StringUtils.isEmpty(appno.trim())) {
            xfappexamCrit.andAppnoEqualTo(appno.trim());
        }
        if (!StringUtils.isEmpty(appStatus.trim())) {
            xfappexamCrit.andAppstatusEqualTo(appStatus.trim());
        }
        if (!StringUtils.isEmpty(idType.trim())) {
            xfappexamCrit.andIdtypeEqualTo(idType);
        }
        if (!StringUtils.isEmpty(strId.trim())) {
            xfappexamCrit.andIdEqualTo(strId);
        }
        xfappexam.setOrderByClause("APPSTATUS,APPDATE DESC");
        return xfappmapper.selectByExample(xfappexam);
    }

    public int updateXfappStatus(String appno, String appstatus) {
        XfappExample xfappexam = new XfappExample();
        xfappexam.clear();
        xfappexam.createCriteria().andAppnoEqualTo(appno);
        Xfapp xfapp = new Xfapp();
        xfapp.setAppstatus(appstatus);
        return xfappmapper.updateByExampleSelective(xfapp, xfappexam);

    }

    public XfappMapper getXfappmapper() {
        return xfappmapper;
    }

    public void setXfappmapper(XfappMapper xfappmapper) {
        this.xfappmapper = xfappmapper;
    }
}
