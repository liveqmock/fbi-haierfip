package pub.platform.security;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * 菜单
 *
 * @author WangHaiLei
 * @version 1.2
 *          $UpdateDate: Y-M-D-H-M:
 *          2003-11-07-09-32
 *          2004-02-28-10-48
 *          $
 */
public class MenuBean
        implements Serializable {

    private transient DatabaseAgent database;

    /**
     * Generate XML file Stream, formatted into a String,
     * which is to be used by the menu tree generator program, such as a JavaScript.
     *
     * @throws Exception
     */
    public String generateStream(String operatorId)
            throws Exception {
        try {
            database = new DatabaseAgent();
            StringBuffer sb = new StringBuffer();
//               sb.append("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
            sb.append("<tree>");

            // Print entries for each defined node and associated subNodes
            /*
            * 标注：菜单的角色分配待整理、 分析
            */
            MenuItemBean[] menuItemsLevel1 = database.getMenuItems(operatorId, 1);

            for (int i = 0; i < menuItemsLevel1.length; i++) {
                //sb.append(menuItemsLevel1[i].convertToString());
//                    String menuItemLevel1Id = menuItemsLevel1[i].getMenuItemId();
//
//                    MenuItemBean[] menuItemsLevel2 = database.getMenuItems(operatorId, 2, menuItemLevel1Id);
//                    if(menuItemsLevel2.length<1){
//                         if ( menuItemsLevel1[i].getIsLeaf()!= null && menuItemsLevel1[i].getIsLeaf().equals("1") ) {
//                              sb.append(menuItemsLevel1[i].convertToString());
//                              sb.append("</tree>");
//                         }
//                         continue;
//                    }

                sb.append(menuItemsLevel1[i].convertToString());


//                    for(int j = 0; j < menuItemsLevel2.length; j++) {
//                         sb.append(menuItemsLevel2[j].convertToString());
//                         String menuItemLevel2Id = menuItemsLevel2[j].getMenuItemId();
//
//                         MenuItemBean[] menuItemsLevel3 = database.getMenuItems(operatorId, 3, menuItemLevel2Id);
//                         for(int k = 0; k < menuItemsLevel3.length; k++) {
//                              sb.append(menuItemsLevel3[k].convertToString());
//                              String menuItemLevel3Id = menuItemsLevel3[k].getMenuItemId();
//
//                              MenuItemBean[] menuItemsLevel4 = database.getMenuItems(operatorId, 4, menuItemLevel3Id);
//                              for(int l = 0; l < menuItemsLevel4.length; l++) {
//                                   sb.append(menuItemsLevel4[l].convertToString());
//                                   String menuItemLevel4Id = menuItemsLevel4[l].getMenuItemId();
//
//                                   MenuItemBean[] menuItemsLevel5 = database.getMenuItems(operatorId, 5, menuItemLevel4Id);
//                                   for(int m = 0; m < menuItemsLevel5.length; m++) {
//                                        sb.append(menuItemsLevel5[m].convertToString());
//                                        sb.insert((sb.length() - 1), "/");
//                                   }
//                                   sb.append("</tree>");
//                              }
//                              sb.append("</tree>");
//                         }
//                         sb.append("</tree>");
//                    }
                sb.append("</tree>");
            }
            sb.append("</tree>"); // This is the very last end of the XML file.

            String xmlString = sb.toString();

            return xmlString;

        } catch (IOException e) {
            System.err.println("IOException, when generating XML file : [" + e + "]");
            throw e;
        }
    }


    /**
     * 20100820 zhanrui
     *
     * @param operatorId
     * @return
     * @throws Exception
     */
    public String generateJsonStream(String operatorId, String target)
            throws Exception {

        database = new DatabaseAgent();

        List<MenuItemBean> menuItems = database.getMenuItems(operatorId,target);

        TreeNode treenode = new TreeNode();
        treenode.setId("0");

        assembleTreeNode(treenode, menuItems);

        net.sf.json.JSONObject jsonObject = net.sf.json.JSONObject.fromObject(treenode);
        String json = jsonObject.toString();

        json = json.replaceAll("\"(\\w+)\"(\\s*:\\s*)", "$1$2");
        return json;
    }

    private void assembleTreeNode(TreeNode treenode, List<MenuItemBean> menuItems) {

        for (MenuItemBean item : menuItems) {
            if (item.getMenuItemPId().equals(treenode.getId())) {
                TreeNode node = new TreeNode();
                node.setId(item.getMenuItemId());
                node.setText(item.getLabel());
                node.setTooltip(item.getDescription());
                TreeUserDataBean udb = new TreeUserDataBean("url", item.getUrl());
                node.addUserData(udb);
                udb = new TreeUserDataBean("description", item.getDescription());
                node.addUserData(udb);
                treenode.addItem(node);
                assembleTreeNode(node, menuItems);
            }
        }
    }

    public static void main(String argv[]) {
        try {
            MenuBean mb = new MenuBean();
            System.out.println(mb.generateJsonStream("9999", "default"));
            //System.out.println(mb.generateJsonStream("9999", "system"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
