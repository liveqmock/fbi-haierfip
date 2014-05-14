package pub.platform.security;

/**
 * Created by IntelliJ IDEA.
 * User: zhanrui
 * Date: 2010-8-21
 * Time: 23:27:19
 * To change this template use File | Settings | File Templates.
 */
public class TreeUserDataBean {
    private String name;
    private String content;

    public TreeUserDataBean(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
