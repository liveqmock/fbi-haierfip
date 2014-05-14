package pub.platform.security;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-21
 * Time: 16:24:32
 * To change this template use File | Settings | File Templates.
 */
public class TreeNode {
    private String id;
    private String text;
    private String tooltip;
    private List<TreeUserDataBean> userdata = new ArrayList();
    private List<TreeNode> item = new ArrayList();

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TreeUserDataBean> getUserdata() {
        return userdata;
    }

    public void addUserData(TreeUserDataBean userdatabean) {
       this.userdata.add(userdatabean);
    }

    public List getItem() {
        return item;
    }

    public void addItem(TreeNode node) {
       this.item.add(node);
    }

}
