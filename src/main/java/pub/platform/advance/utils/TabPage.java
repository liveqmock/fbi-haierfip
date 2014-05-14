package pub.platform.advance.utils;

public class TabPage
{
    private String Id;
    private String Title;
    private boolean useIFrame;
    private String Url;
    private boolean initLoad;
    private boolean autoUnload;

    /**
     * @return 返回 autoUnload。
     */
    public boolean isAutoUnload()
    {
        return autoUnload;
    }

    /**
     * @param autoUnload 要设置的 autoUnload。
     */
    public void setAutoUnload(boolean autoUnload)
    {
        this.autoUnload = autoUnload;
    }

    /**
     * @return 返回 initLoad。
     */
    public boolean isInitLoad()
    {
        return initLoad;
    }

    /**
     * @param initLoad 要设置的 initLoad。
     */
    public void setInitLoad(boolean initLoad)
    {
        this.initLoad = initLoad;
    }

    /**
     * @return 返回 tabPageId。
     */
    public String getId()
    {
        return Id;
    }

    /**
     * @param tabPageId 要设置的 tabPageId。
     */
    public void setId(String tabPageId)
    {
        this.Id = tabPageId;
    }

    /**
     * @return 返回 url。
     */
    public String getUrl()
    {
        return Url;
    }

    /**
     * @param url 要设置的 url。
     */
    public void setUrl(String url)
    {
        Url = url;
    }

    /**
     * @return 返回 useIFrame。
     */
    public boolean isUseIFrame()
    {
        return useIFrame;
    }

    /**
     * @param useIFrame 要设置的 useIFrame。
     */
    public void setUseIFrame(boolean useIFrame)
    {
        this.useIFrame = useIFrame;
    }

    /**
     * @return 返回 tabTitle。
     */
    public String getTitle()
    {
        return Title;
    }

    /**
     * @param tabTitle 要设置的 tabTitle。
     */
    public void setTitle(String tabTitle)
    {
        this.Title = tabTitle;
    }

    /**
     * 新生成一个TabPage对象
     * 
     * @param Id tabPage的ID，在JS编程中可以使用这个ID操作具体的TabPage
     * @param Title 新增tab页的显示名称
     * @param useIFrame 是否使用IFrame切换页面。如果不使用IFrame，后面三个参数无效
     * @param url 使用IFrame的时候，本Tab页显示的页面Url
     * @param initLoad 本页面是否在页面打开的时候加载
     * @param autoUnload 本页面是否再页面切换后自动Unload（Unload后可以节约客户端内存，但是数据不保存将丢失）
     */
    public TabPage(String Id, String Title, boolean useIFrame, String url,
            boolean initLoad, boolean autoUnload)
    {
        super();
        this.Id = Id;
        this.Title = Title;
        this.useIFrame = useIFrame;
        Url = url;
        this.initLoad = initLoad;
        this.autoUnload = autoUnload;
    }

    /**
     * 缺省构造函数
     */
    public TabPage()
    {
        super();
        this.Id = null;
        this.Title = null;
        this.useIFrame = false;
        this.Url = null;
        this.initLoad = false;
        this.autoUnload = false;
    }

    public static void main(String[] args)
    {
        System.out.println("Hello World!");
    }

}