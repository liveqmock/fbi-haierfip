package pub.platform.system.manage.menu.parent;

import pub.platform.form.control.Action;

public class MenuSelectAction extends Action {
    public int doBusiness() {

        String SQLStr = "select  menuid,menulabel,menudesc,(select  count(menuid) as a from ptmenu   where (isleaf=0) and parentmenuid=me.menuid) as menucount ,Levelidx,targetmachine  from ptmenu  me where (isleaf=0) and parentmenuid='" +
                this.req.getFieldValue("parentid") + "' order by Levelidx";

        this.rs = this.dc.executeQuery(SQLStr);

        this.res.setFieldName("menuid;menulabel;menudesc;menucount;Levelidx;targetmachine");
        this.res.setFieldType("text;text;text;text;text;text");
        this.res.setEnumType("0;0;0;0;0;0");
        this.res.setType(1);

        if (this.rs == null) {
            this.res.setResult(false);
            this.res.setMessage("无返回信息记录");
            return -1;

        } else {
            this.res.setResult(true);
            this.res.setRecordset(this.rs);
        }

        return 0;
    }

}
