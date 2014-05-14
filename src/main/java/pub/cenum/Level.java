package pub.cenum;

import hfc.common.AppCommonEnum;
import pub.platform.db.ConnectionManager;
import pub.platform.db.DBUtil;

import javax.sql.rowset.CachedRowSet;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: haiyuhuang
 * Date: 11-8-11
 * Time: 上午11:14
 * To change this template use File | Settings | File Templates.
 */
public class Level {
    private static HashMap<String, Vector<LevelItem>> data = new HashMap<String, Vector<LevelItem>>(40, 1);

    static {
        System.out.println("Enumeration Configuration initing ...");
        //申请数据库资源
        ConnectionManager manager = ConnectionManager.getInstance();

        //sql
        String sql1 = "select * from ptenudetail order by enutype,enuitemvalue";
        String sql2 = "select * from sccurrency order by curno";
        try {
            CachedRowSet rs1 = manager.getRs(sql1);
            CachedRowSet rs2 = manager.getRs(sql2);

            String lastenum = null;
            Vector<LevelItem> items = new Vector<LevelItem>();

            while (rs1.next()) {
                if (lastenum != null && rs1.getString("enutype").trim().compareToIgnoreCase(lastenum) != 0) {
                    data.put(lastenum, items);
                    items = new Vector<LevelItem>();
                }
                lastenum = rs1.getString("enutype").trim();
                items.add(new LevelItem(rs1.getString("enuitemvalue").trim(), DBUtil.fromDB(rs1.getString("enuitemlabel"))));

            }
            if (lastenum != null) {
                data.put(lastenum, items);
            }
            items = new Vector<LevelItem>();
            boolean flag = false;
            while (rs2.next()) {
                items.add(new LevelItem(rs2.getString("curno").trim(), DBUtil.fromDB(rs2.getString("curname"))));
                if (rs2.isLast()) flag = true;
            }
            if (flag) {
                lastenum = "CURRENCY";
                data.put(lastenum, items);
            }
            rs1.close();
            rs2.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /*20110718 haiyu 追加 用于申请页面还款方式*/
    public static String levelHereNew(String selectname, String enumid1, String enumid2, String selecteditem) throws Exception {
        return levelHereExtNew(selectname, enumid1, enumid2, selecteditem, "page_form_select");
    }

    public static String levelHereExtNew(String selectname, String enumid1,
                                         String enumid2, String selecteditem, String selectclass) throws Exception {
        if (enumid1 == null || selectname == null) return null;
        if (selecteditem != null) selecteditem = selecteditem.trim();
        if (selecteditem != null && selecteditem.trim().length() <= 0)
            selecteditem = null;

        String temp = "<select name=\"" + selectname + "\" ";
        if (selectclass != null) temp += " class=\"" + selectclass + "\"";
        temp += ">";
        try {
            Vector<LevelItem> items = data.get(enumid1);
            Vector<LevelItem> items2 = data.get(enumid2);
            if (items == null) return null;

            String strTemp = "-通过银联";
            if (items != null) {
                for (int i = 0; i < items.size(); i++) {
                    String itemno = (items.elementAt(i)).getItemNo() + "-" + AppCommonEnum.APP_UNIONPAY_NULL;
                    String itemLabel = (items.elementAt(i)).getItemLabel();
                    if (selecteditem != null &&
                            itemno.compareToIgnoreCase(selecteditem) == 0) {
                        temp += "<option value='" + itemno + "' selected>"
                                + itemLabel + "</option>";
                    } else {
                        temp += "<option value='" + itemno + "'>" +
                                itemLabel + "</option>";
                    }

                }
            }
            if (items2 != null) {
                for (int i = 0; i < items2.size(); i++) {
                    String itemno2 = (items2.elementAt(i)).getItemNo() + "-" + AppCommonEnum.APP_UNIONPAY;
                    String itemLabel2 = (items2.elementAt(i)).getItemLabel();
                    itemLabel2 = itemLabel2 + strTemp;
                    if (selecteditem != null &&
                            itemno2.compareToIgnoreCase(selecteditem) == 0) {
                        temp += "<option value='" + itemno2 + "'selected > "
                                + itemLabel2 + " </option > ";
                    } else {
                        temp += "<option value='" + itemno2 + "'>" +
                                itemLabel2 + "</option>";
                    }
                }
            }
            temp += "</select>";

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return temp;
    }

    public static String radioHere(String selectname, String enumid, String selecteditem) throws Exception {
        return radioHereExt(selectname, enumid, selecteditem, "");
    }

    public static String levelHere(String selectname, String enumid, String selecteditem) throws Exception {
        return levelHereExt(selectname, enumid, selecteditem, null, "page_form_select");
    }

    /**
     * @param selectname   selectname
     * @param enumid       enumid
     * @param selecteditem use default splitechar '-'
     * @return checkGroup
     * @throws Exception Exception
     */
    public static String checkHere(String selectname, String enumid, String selecteditem) throws Exception {
        return checkGroupHereExt(selectname, enumid, selecteditem, EnumValue.SPLIT_STR, "");
    }

    /**
     * checkGroupHereExt
     *
     * @param selectname   String: Control Name of HTML
     * @param enumid       String: EnumID matching with enutype field of DB
     * @param selecteditem String:the focused checkboxs,splite by input character.
     * @param splitechar   String:the splite character.
     * @param rangetype    String: if u want to range the checkbox group ,put <br> or other tags on this param,
     *                     if null,"" will be detail.
     * @return String      String
     * @throws Exception Exception
     */
    public static String checkGroupHereExt(String selectname, String enumid, String selecteditem, String splitechar, String rangetype) throws Exception {

        if (enumid == null || selectname == null || splitechar == null) return null;
        if (rangetype == null) rangetype = "";

        String selectitem[] = new String[0];
        if (selecteditem != null) selectitem = selecteditem.split(splitechar);
        String temp = "";
        String ifcheck;

        try {
            Vector<LevelItem> items = data.get(enumid);
            if (items == null) return null;
            int j = 0;
            for (int i = 0; i < items.size(); i++) {
                temp += "<label>";
                String itemno = (items.elementAt(i)).getItemNo();
                if (j < selectitem.length && itemno.compareToIgnoreCase(selectitem[j]) == 0) {
                    ifcheck = "checked";
                    j++;
                } else ifcheck = "";
                temp += "<input type=\"checkbox\" class=\"page_form_checkbox\" " + ifcheck +
                        " name=\"" + selectname + "\" id='" + itemno + "' value=\"" + itemno + "\">" + (items.elementAt(i)).getItemLabel() + "" +
                        "</label>" + rangetype;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return temp;
    }

    /**
     * @param selectname     String
     * @param enumid         String
     * @param selecteditem   String:the focused Option when the List Control is first shown.
     * @param extraitemlabel String:
     * @param selectclass    String:CSS class id.
     * @return String
     * @throws Exception Exception
     */
    private static String levelHereExt(String selectname, String enumid, String selecteditem, String extraitemlabel, String selectclass) throws Exception {

        if (enumid == null || selectname == null) return null;

        if (selecteditem != null) selecteditem = selecteditem.trim();
        if (selecteditem != null && selecteditem.trim().length() <= 0) selecteditem = null;

        String temp = "<select name=\"" + selectname + "\" ";
        if (selectclass != null) temp += " class=\"" + selectclass + "\"";
        temp += ">";


        try {
            Vector<LevelItem> items = data.get(enumid);
            if (items == null) return null;

            if (extraitemlabel != null) temp += "<option value=''>" + extraitemlabel + "</option>";

            for (int i = 0; i < items.size(); i++) {
                String itemno = (items.elementAt(i)).getItemNo();
                if (selecteditem != null && itemno.compareToIgnoreCase(selecteditem) == 0)
                    temp += "<option value='" + itemno + "' selected>" + (items.elementAt(i)).getItemLabel() + "</option>";
                else
                    temp += "<option value='" + itemno + "'>" + (items.elementAt(i)).getItemLabel() + "</option>";
            }
            temp += "</select>";

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return temp;
    }

    /**
     * radioHereExt
     *
     * @param selectname   String: Control Name of HTML
     * @param enumid       String: EnumID matching with enutype field of DB
     * @param selecteditem String:the focused radio when the List Control is first shown.
     * @param rangetype    String: if u want to range the radio group ,put <br> or other tags on this param,
     *                     if null,"" will be detail.
     * @return String     String
     * @throws Exception Exception
     */
    public static String radioHereExt(String selectname, String enumid, String selecteditem, String rangetype) throws Exception {

        if (enumid == null || selectname == null) return null;
        if (rangetype == null) rangetype = "";

        String temp = "";
        String ifcheck;

        try {
            Vector<LevelItem> items = data.get(enumid);
            if (items == null) return null;

            for (int i = 0; i < items.size(); i++) {
                temp += "<label>";
                String itemno = (items.elementAt(i)).getItemNo();
                if (selecteditem != null && itemno.compareToIgnoreCase(selecteditem) == 0) ifcheck = "checked";
                else ifcheck = "";
                temp += "<input type=\"radio\" class=\"page_form_radio\" " + ifcheck +
                        " name=\"" + selectname + "\" id='" + itemno + "' value=\"" + itemno + "\">" + (items.elementAt(i)).getItemLabel() + "" +
                        "</label>" + rangetype;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return temp;
    }

    public static String getEnumItemName(String enumid, String itemno) throws Exception {
        Vector<LevelItem> items = data.get(enumid);
        if (items == null) return null;
        if (itemno == null) return "";
        itemno = itemno.trim();

        for (int i = 0; i < items.size(); i++) {
            String itemno1 = (items.elementAt(i)).getItemNo().trim();
            if (itemno1.compareToIgnoreCase(itemno) == 0)
                return (items.elementAt(i)).getItemLabel();
        }

        return itemno;

    }
}
