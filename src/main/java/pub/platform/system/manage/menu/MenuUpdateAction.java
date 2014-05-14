package pub.platform.system.manage.menu;

import pub.platform.form.control.Action;
import pub.platform.system.manage.dao.*;

public class MenuUpdateAction extends Action
{
    public int doBusiness() {

         PtMenuBean ptmenu = new PtMenuBean();
         PtResourceBean ptresBean = new PtResourceBean();

          int menulevel=1;

		for (int i=0; i<this.req.getRecorderCount();i++ ){


               /////////////////取出父节点层次
                ptmenu = (PtMenuBean)ptmenu.findFirstByWhere("where (MenuID='"+this.req.getFieldValue(i,"parentmenuid")+"')");
                if (ptmenu != null)
                     menulevel =ptmenu.getMenulevel()+1;

                ptmenu = new PtMenuBean();

                ptmenu.setMenulevel(menulevel);

                try{
                     if (this.req.getFieldValue(i,"levelindex") != null)
                          ptmenu.setLevelidx(Integer.parseInt(this.req.getFieldValue(i,"levelindex")));
                     else
                          ptmenu.setLevelidx(0);

                 }catch (Exception e){

                 }

                  //ptmenu.setTargetmachine(this.req.getFieldValue(i,"targetMachine"));
                  ptmenu.setMenulabel(this.req.getFieldValue(i,"MenuLabel"));
                  ptmenu.setMenuaction(this.req.getFieldValue(i,"MenuAction"));
                  ptmenu.setMenudesc(this.req.getFieldValue(i,"MenuDesc"));

                  if (this.req.getFieldValue(i,"OpenWindow") != null){
                        ptmenu.setOpenwindow(this.req.getFieldValue(i,"OpenWindow"));

                        try{
                             if (this.req.getFieldValue(i,"WindowHeight") != null )
                               ptmenu.setWindowheight(Integer.parseInt(this.req.getFieldValue(i,"WindowHeight")));
                        }catch (Exception e){

                        }

                        try{
                             if (this.req.getFieldValue(i,"WindowWidth") != null )
                                  ptmenu.setWindowwidth(Integer.parseInt(this.req.getFieldValue(i,"WindowWidth")));
                        }catch (Exception e){

                        }

                   }

                  if (ptmenu.updateByWhere(" where (MenuID='"+this.req.getFieldValue(i,"keycode")+"')") <0){
                       this.res.setType(0);
                       this.res.setResult(false);
                       this.res.setMessage("更新记录失败");
                       return -1;
                  }

                   /////////////////更新资源信息
                    ptresBean.setResdesc(this.req.getFieldValue(i,"menulabel"));
                    ptresBean.setParentresid("m"+this.req.getFieldValue(i,"parentmenuid"));

                    if (ptresBean.updateByWhere(" where (ResID='m"+this.req.getFieldValue(i,"keycode")+"')") <0){
                         this.res.setType(0);
                         this.res.setResult(false);
                         this.res.setMessage("更新资源失败");
                         return -1;
                    }

       }

       this.res.setType(0);
       this.res.setResult(true);
       this.res.setMessage("更新记录成功");

       return 0;
   }


}
