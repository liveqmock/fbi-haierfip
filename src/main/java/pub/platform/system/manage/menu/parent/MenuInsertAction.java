package pub.platform.system.manage.menu.parent;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;
import pub.platform.utils.*;

public class MenuInsertAction extends Action {
    public int doBusiness() {


        PtMenuBean ptmenu = new PtMenuBean();
        PtResourceBean ptresBean = new PtResourceBean();

        String maxCoun = "0";
        int menulevel = 1;


        for (int i = 0; i < this.req.getRecorderCount(); i++) {


            /////////////////取出父节点层次
            ptmenu = (PtMenuBean) ptmenu.findFirstByWhere("where (MenuID='" + this.req.getFieldValue(i, "parentmenuid") + "')");

            if (ptmenu != null)
                menulevel = ptmenu.getMenulevel() + 1;

            ptmenu = new PtMenuBean();

            ////////////////求出菜单最大值
            maxCoun = Util.getFieldMax(this.dc, "menuid", "ptmenu");
            if (Integer.parseInt(maxCoun) < 10)
                maxCoun = "0" + maxCoun;
            ptmenu.setMenuid(maxCoun);

            ptmenu.setParentmenuid(this.req.getFieldValue("parentmenuid"));
            ptmenu.setIsleaf(0);

            ptmenu.setMenulevel(menulevel);

            if (this.req.getFieldValue("levelindex") != null)
                ptmenu.setLevelidx(Integer.parseInt(this.req.getFieldValue("levelindex")));
            else
                ptmenu.setLevelidx(0);

            ptmenu.setMenulabel(this.req.getFieldValue("menulabel"));
            ptmenu.setMenudesc(this.req.getFieldValue("menudesc"));
            ptmenu.setTargetmachine(this.req.getFieldValue(i, "targetmachine"));


            if (ptmenu.insert() < 0) {
                this.res.setType(0);
                this.res.setResult(false);
                this.res.setMessage("添加菜单失败");
                return -1;
            }


            /////////////////添加资源信息
            ptresBean.setResid("m" + maxCoun);
            ptresBean.setResdesc(this.req.getFieldValue("menulabel"));
            ptresBean.setParentresid("m" + this.req.getFieldValue("parentmenuid"));
            ptresBean.setResname(maxCoun);
            ptresBean.setRestype("4");

            if (ptresBean.insert() < 0) {
                this.res.setType(0);
                this.res.setResult(false);
                this.res.setMessage("添加资源失败");
                return -1;
            }


        }

        this.res.setType(0);
        this.res.setResult(true);
        this.res.setMessage("添加记录成功");

        return 0;
    }


}
