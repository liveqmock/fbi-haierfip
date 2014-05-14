package pub.platform.security;

import pub.platform.advance.utils.PropertyManager;

import java.io.Serializable;


/**
 * 菜单项
 *
 * @author WangHaiLei
 * @version 1.5
 *          $ UpdateDate: Y-M-D-H-M:
 *          2003-10-28-13-43
 *          2004-02-28-10-36
 *          2004-03-02-09-54
 *          $
 */
public class
        MenuItemBean implements Serializable {

    /**
     * The nodeid for this node.
     */
    private String menuItemId = null;

    public String getMenuItemId() {
        return (this.menuItemId);
    }

    /**
     * The nodeid for parent node.
     */
    private String menuItemPId = null;

    public String getMenuItemPId() {
        return (this.menuItemPId);
    }

    private int menuLevel = 0;

    public int getMenuLevel() {
        return menuLevel;
    }

    public void setMenuLevel(int menuLevel) {
        this.menuLevel = menuLevel;
    }

    /**
     * Defines a isBranch attribute
     */
    private String isLeaf = null;

    public String getIsLeaf() {
        return (this.isLeaf);
    }

    public void setIsLeaf(String isLeaf) {
        this.isLeaf = isLeaf;
    }

    /**
     * Defines a label attribute
     */
    private String label = null;

    public String getLabel() {
        return (this.label);
    }

    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Defines a url attribute
     */
    public String url = null;

    public String getUrl() {
        return (this.url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Defines a description
     */
    public String description = null;

    public String getDescription() {
        return (this.description);
    }

    public void setDescription(String description) {
        this.description = description;
    }
    ///////打开方式 0 ----表示潜入方式；1-----表示弹出方式
    private String openwindow = "0";

    public String getOpenWindow() {
        return this.openwindow;
    }

    public void setOpenWindow(String openwindow) {
        this.openwindow = openwindow;
    }

    private String childcount = "0";

    public String getchildcount() {
        return this.childcount;
    }

    public void setchildcount(String childcount) {
        this.childcount = childcount;
    }

    ////////////如果是弹出方式 则窗体的宽度
    private String winWidth = "";

    public String getwinWidth() {
        return this.winWidth;

    }

    public void setwinWidth(String winWidth) {
        this.winWidth = winWidth;
    }
    /////////////如果弹出方式 则窗体的高的
    private String winHeight = "";
    private String targetmachine;

    public String getwinHeight() {
        return this.winHeight;

    }

    public void setwinHeight(String winHeight) {
        this.winHeight = winHeight;
    }

////////////////////////////////////////////////////////////////////////////////////////////////////////////// Constructors

    /**
     * Constructor: Creates an empty Node object
     */
    public MenuItemBean() {
    }

    public MenuItemBean(String nodeid) {
        super();
        this.menuItemId = nodeid;
    }

    public MenuItemBean(String menuItemId, String label, String isLeaf, String url) {
        super();
        this.menuItemId = menuItemId;
        this.label = label;
        this.isLeaf = isLeaf;
        this.url = url;
    }

    public MenuItemBean(String menuItemId, String label, String isLeaf, String url, String description) {
        super();
        this.menuItemId = menuItemId;
        this.label = label;
        this.isLeaf = isLeaf;
        this.url = url;
        this.description = description;
    }

    public MenuItemBean(String menuItemId, String label, String isLeaf, String url, String description, String openwindow, String winWidth, String winHeight) {
        super();
        this.menuItemId = menuItemId;
        this.label = label;
        this.isLeaf = isLeaf;
        this.url = url;
        this.description = description;
        this.openwindow = openwindow;
        this.winWidth = winWidth;
        this.winHeight = winHeight;

    }

    public MenuItemBean(String menuItemId, String label, String isLeaf, String url, String description, String openwindow, String winWidth, String winHeight, String targetmachine, String childcount) {
        super();
        this.menuItemId = menuItemId;
        this.label = label;
        this.isLeaf = isLeaf;
        this.url = url;
        this.description = description;
        this.openwindow = openwindow;
        this.winWidth = winWidth;
        this.winHeight = winHeight;
        this.targetmachine = targetmachine;
        this.childcount = childcount;
    }

    public MenuItemBean(String menuItemId, String menuItemPId, int menuLevel,String label, String isLeaf, String url, String description, String openwindow, String winWidth, String winHeight, String targetmachine, String childcount) {
        super();
        this.menuItemId = menuItemId;
        this.menuItemPId = menuItemPId;
        this.menuLevel = menuLevel;
        this.label = label;
        this.isLeaf = isLeaf;
        this.url = url;
        this.description = description;
        this.openwindow = openwindow;
        this.winWidth = winWidth;
        this.winHeight = winHeight;
        this.targetmachine = targetmachine;
        this.childcount = childcount;
    }


////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Return a String representation of this object.
     */
    public String convertToString()
            throws Exception {

        // 下面注释掉的部分代码，是Flash Tree版本的。
//          StringBuffer sb = new StringBuffer("<node label=\"");
//          sb.append(label);
//          sb.append("\"");
//
//          if(!isLeaf.equals("")) {
//               String isBranch = null;
//               if(isLeaf.equals("0")) {
//                    isBranch = "false";
//               }
//               if(isLeaf.equals("1")) {
//                    isBranch = "true";
//               }
//               sb.append(" isBranch=\"");
//               sb.append(isBranch);
//               sb.append("\"");
//          }
//
//          sb.append(" url=\"");
//          sb.append(url);
//          sb.append("\"");
//          sb.append(">");

        // 下面的代码，是JavaScript Tree版本的。
        StringBuffer sb = new StringBuffer("<tree text=\"");
        sb.append(label);
        sb.append("\"");
        if (url != null && url.trim().length() > 0) {
            sb.append(" action=\"");
            try {
                if (!url.equals("#") && targetmachine != null && targetmachine.trim().length() > 0) {
                    String realMachine = PropertyManager.getProperty(targetmachine);
                    if (realMachine != null && realMachine.trim().length() > 0)
                        sb.append(specialCodeTranslate(realMachine));
                }

                sb.append(specialCodeTranslate(url));
            } catch (Exception ex) {
                System.out.println("Wrong at MenuItemBean.convertToString : [ " + ex + " ] ");
            }
            sb.append("\"");
        }
        sb.append(" title=\"");
        sb.append(description);
        sb.append("\"");
        sb.append(" openwin=\"");
        sb.append(openwindow);
        sb.append("\"");
        sb.append(" winwidth=\"");
        sb.append(winWidth);
        sb.append("\"");
        sb.append(" winheight=\"");
        sb.append(winHeight);
        sb.append("\"");
        sb.append(" childcount=\"");
        sb.append(childcount);
        sb.append("\"");
        sb.append(" menuItemId=\"");
        sb.append(menuItemId);
        sb.append("\">");

        return (sb.toString());
    }

    /**
     * ///////////////////////////////////////////////////////////////////////////////////////////////////////////
     */

    private String specialCodeTranslate(String codeToBeTrans) {

        String codeAfterTrans = "";

        // 先转换&；后转换其他的符号。
        codeAfterTrans = codeToBeTrans.replaceAll("&", "&amp;");

        codeAfterTrans = codeAfterTrans.replaceAll("<", "&lt;");
        codeAfterTrans = codeAfterTrans.replaceAll(">", "&gt");

        // 先转换双引号；后转换单引号。
        codeAfterTrans = codeAfterTrans.replaceAll("\"", "&quot;"); //
        codeAfterTrans = codeAfterTrans.replaceAll("'", "&apos;");

        return codeAfterTrans;
    }

    public String getTargetmachine() {
        return targetmachine;
    }

    public void setTargetmachine(String targetmachine) {
        this.targetmachine = targetmachine;
    }
}
