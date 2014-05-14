/*
 * 创建日期 2005-1-24
 */
package pub.platform.advance.utils;

import java.util.Iterator;
import java.util.Vector;

/**
 * @author zhang
 */
/**
 * @author zhang
 */
public class TabPane
{
	//TabPane ID : 对应于生成的DIV对象id（ClassName="tab"）
	private String  Id;

	//JavaScript object Name：对应于用来在JS中操作的对象名
	private String  objectName;

	//use Cookie?
	private boolean useCookie;

	//param url：参数传递URL
	private String  paramUrl;

	//PageInfo：保存页面信息，用于生成页面
	private Vector  pageVector;

	//view? 是否只是查看页面？
	private boolean view;
	/**
	 * @param id tab对象ID
	 * @param objectName 将来客户端用来操作的JS对象ID
	 * @param useCookie 是否使用Cookie保存当前页号信息，一般不用，设成false
	 */
	public TabPane(String id, String objectName, boolean useCookie)
	{
		super();
		Id = id;
		this.objectName = objectName;
		this.useCookie = useCookie;
		this.pageVector = new Vector();
		this.paramUrl = "";
		this.view=false;	//缺省非查看状态
	}

	public TabPane(String id, String objectName, boolean useCookie, boolean view)
	{
		this(id, objectName, useCookie );
		this.view=view;
	}


	/**
	 * @return 返回 Id。
	 */
	public String getId()
	{
		return Id;
	}

	/**
	 * @param Id 要设置的 Id。
	 */
	public void setId(String Id)
	{
		this.Id = Id;
	}

	/**
	 * @return 返回 objectName。
	 */
	public String getObjectName()
	{
		return objectName;
	}

	/**
	 * @param objectName 要设置的 objectName。
	 */
	public void setObjectName(String objectName)
	{
		this.objectName = objectName;
	}

	/**
	 * @return 返回 paramUrl。
	 */
	public String getParamUrl()
	{
		return paramUrl;
	}

	/**
	 * @param paramUrl 要设置的 paramUrl。
	 */
	public void setParamUrl(String paramUrl)
	{
		this.paramUrl = paramUrl;
	}

	/**
	 * @return 返回 useCookie。
	 */
	public boolean isUseCookie()
	{
		return useCookie;
	}

	/**
	 * @param useCookie 要设置的 useCookie。
	 */
	public void setUseCookie(boolean useCookie)
	{
		this.useCookie = useCookie;
	}

	/**
	 * 新增一个Tab页
	 *
	 * @param tabPageId 新增tab页的ID，在JS操作中可以使用该ID操作TAB页
	 * @param tabTitle 新增tab页的显示名称
	 * @param useIFrame 是否使用IFrame切换页面。如果不使用IFrame，后面三个参数无效
	 * @param Url 使用IFrame的时候，本Tab页显示的页面Url
	 * @param initLoad 本页面是否在页面打开的时候加载
	 * @param autoUnload 本页面是否再页面切换后自动Unload（Unload后可以节约客户端内存，但是数据不保存将丢失）
	 */
	public void addPage(String tabPageId, String tabTitle, boolean useIFrame,
			String Url, boolean initLoad, boolean autoUnload)
	{
		if (pageVector != null)
		{
			TabPage tabPage = new TabPage(tabPageId, tabTitle, useIFrame, Url,
					initLoad, autoUnload);
			pageVector.add(tabPage);
		}
	}

	/**
	 * 生成HTML代码
	 *
	 * @return 生成的HTML代码，写在页面上
	 */
	public String getTabPane()
	{
		StringBuffer strBuf = new StringBuffer();
		//TabPane信息
		strBuf.append("<div class=\"tabContainer\" id=\""+this.Id+"Container\">\n");
		strBuf.append("	<div class = \"tab-pane\" id = \"" + this.Id + "\">\n");
		strBuf.append("		<SCRIPT LANGUAGE = \"JavaScript\">\n");
		strBuf.append("			"+this.objectName+" = new WebFXTabPane(document.getElementById(\""+this.Id+"\"), "+this.isUseCookie()+", "+this.isView()+");\n");
		strBuf.append("			"+this.objectName+".setParamUrl( \"" + paramUrl + "\");\n");
		strBuf.append("		</SCRIPT>\n");
		strBuf.append("	<div class=\"tabContentContainer\" id=\""+this.Id+"ContentContainer\"><marquee id="+this.Id+"__message isvisible=1  scrollamount=3 behavior=alternate direction=right"+
			" style=\"position: absolute; visibility: visible; width:100%; height: 22; background-color: #d4d0c8; font-size: 12; border: 1 solid silver; padding-top:3; z-index: 10000; filter:alpha(opacity=70)\">正在读取数据，请稍候……</marquee>\n");
		if (pageVector != null)
		{
			Iterator it = pageVector.iterator();
			while (it.hasNext())
			{
				TabPage tabPage = (TabPage) it.next();
				strBuf
						.append("		<div class = \"tab-page\" id = \""
								+ tabPage.getId() + "\" url=\""
								+ tabPage.getUrl() + "\" initLoad=\""
								+ tabPage.isInitLoad() + "\" autoUnload=\""
								+ tabPage.isAutoUnload() + "\">\n");
				strBuf.append("			<font class = \"tab\">" + tabPage.getTitle()
						+ "</font>\n");
				strBuf.append("			<script type = \"text/javascript\">\n");
				strBuf.append("				" + this.getObjectName()
						+ ".addTabPage(document.getElementById(\""
						+ tabPage.getId() + "\"));\n");
				strBuf.append("			</script>\n");
				strBuf.append("			<IFRAME SRC = \"\" NAME = \"frame_"
						+ tabPage.getId() + "\" id = \"frame_"
						+ tabPage.getId()
						+ "\" class=\"tabPageFrame\"></IFRAME>\n");
//                        + "\" class=\"tabPageFrame\" frameBorder=\"0\"></IFRAME>\n");
				strBuf.append("		</div>\n");
			}
		}
		strBuf.append("	</div>\n");
		strBuf.append("	</div>\n");
		strBuf.append("</div>");
		pageVector.clear();
		pageVector=null;
		return strBuf.toString();
	}

	/**
	 * 测试用的代码
	 *
	 * @param args
	 */
	public static void main(String[] args)
	{
		TabPane tabPane = new TabPane("tab", "tabPane1", false);
		tabPane.addPage("jb", "基本信息", true, "jb.html", false, true);
		tabPane.addPage("sp", "审批文件", true, "sp.html", false, true);
		tabPane.addPage("fzr", "法人代表", true, "fzr.html", false, true);
		tabPane.addPage("fr", "法人股东", true, "fr.html", false, true);
		tabPane.addPage("zr", "自然人股东", true, "zr.html", false, true);
		tabPane.addPage("yj", "人员意见", true, "yj.html", false, true);
		System.out.println(tabPane.getTabPane());
	}

	public boolean isView()
	{
		return view;
	}

	public void setView(boolean view)
	{
		this.view = view;
	}
}