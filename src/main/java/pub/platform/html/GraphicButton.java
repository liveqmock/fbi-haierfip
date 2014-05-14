package pub.platform.html;

/**
 *  <p>
 *
 *  Title: </p> <p>
 *
 *  Description: </p> <p>
 *
 *  Copyright: Copyright (c) 2003</p> <p>
 *
 *  Company: </p>
 *
 *@author     not attributable
 *@created    2003年10月11日
 *@version    1.0
 */

public class GraphicButton {
    String header = "<table width=\"80\" border=\"0\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\">" +
            "<tr><td width=\"7\" height=\"7\"><img src=\"images/jiao_01.gif\" width=\"7\" height=\"7\"></td>" +
            "<td width=\"66\" height=\"7\" background=\"images/jiao_05.gif\"><img src=\"images/kongbai.gif\" width=\"1\" height=\"1\"></td>" +
            "<td width=\"7\" height=\"7\"><img src=\"images/jiao_04.gif\" width=\"7\" height=\"7\"></td>" +
            "</tr><tr><td width=\"7\" background=\"images/jiao_06.gif\"><img src=\"images/kongbai.gif\" width=\"1\" height=\"1\"></td>";
    String footer = "<td width=\"7\" background=\"images/jiao_08.gif\"><img src=\"images/kongbai.gif\" width=\"1\" height=\"1\"></td>" +
            "</tr><tr><td width=\"7\"><img src=\"images/jiao_02.gif\" width=\"7\" height=\"7\"></td>" +
            "<td width=\"66\" height=\"7\" background=\"images/jiao_07.gif\"><img src=\"images/kongbai.gif\" width=\"1\" height=\"1\"></td>" +
            "<td width=\"7\"><img src=\"images/jiao_03.gif\" width=\"7\" height=\"7\"></td></tr></table>";

    String caption = "提交";
    String operation = "submit()";


    /**
     *  Constructor for the GraphicButton object
     *
     *@param  caption    Description of the Parameter
     *@param  operation  Description of the Parameter
     */
    public GraphicButton(String caption, String operation) {
        this.caption = caption;
        this.operation = operation;
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toHtml() {
        return header + "<td class=\"page_form_button\" onclick=\"" + operation + "\" nowrap>" + caption + "</td>" + footer;
    }


    /**
     *  Gets the submitHtml attribute of the GraphicButton class
     *
     *@return    The submitHtml value
     */
    public static String getSubmitHtml() {
        GraphicButton button = new GraphicButton("提交", "submit()");
        return button.toHtml();
    }


    /**
     *  Gets the resetHtml attribute of the GraphicButton class
     *
     *@return    The resetHtml value
     */
    public static String getResetHtml() {
        GraphicButton button = new GraphicButton("清空", "reset()");
        return button.toHtml();
    }


    /**
     *  Gets the linkHtml attribute of the GraphicButton class
     *
     *@return    The linkHtml value
     */
    public static String getLinkHtml(String linkName,String linkUrl) {
        GraphicButton button = new GraphicButton("清空", "reset()");
        return button.toHtml();
    }


}
