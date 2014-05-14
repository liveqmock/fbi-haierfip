//Source file: D:\\zt\\platform\\form\\control\\ReferenceGenerator.java

package pub.platform.form.control;

import java.util.StringTokenizer;

import pub.platform.db.*;
import pub.platform.form.component.*;
import pub.platform.form.config.*;
import pub.platform.form.util.*;
import pub.platform.form.util.datatype.ComponentType;
import pub.platform.form.util.event.*;

/**
 * 参考页面生成器
 *
 * @author 请替换
 * @version 1.0
 */
public class ReferenceGenerator {

    public static final String TARGET_TEMPLATE = "/templates/ref.jsp";
    public static final String TARGET_JAVASCRIPT = "/js/ref.js";

    /**
     * 1、取得参考性查询结果ctx.getRequestAttribute(SessionAttribute.REQUEST_REFERENCE_
     * RESULT_NAME)
     * 2、声明body
     *       body = "<table class='reference_list'>"
     *       body += "<tr><td>参考值</td><td>注释</td></tr>"
     * 3、对每一条结果执行下述操作
     *       <tr>
     *            <td>取值字段</td>
     *            <td>取注释字段</td>
     *       </tr>
     * 4、body += "</table>"
     * @param ctx
     * @param event
     * @return String
     * @roseuid 3F7F3D4C03E7
     */
    public static String[] run(SessionContext ctx, Event event, ErrorMessages msgs, int result)
    {
        RecordSet rs = (RecordSet)ctx.getRequestAttribute(SessionAttributes.REQUEST_REF_RESULT_NAME);
        String pageno = (String)ctx.getRequestAttribute(SessionAttributes.REQUEST_LIST_PAGENO_NAME);
        String pagecount = (String)ctx.getRequestAttribute(SessionAttributes.REQUEST_LIST_PAGECOUNT_NAME);

        int iPageno = 0;
        int iPageCount = 0;

        try {
            iPageno = Integer.parseInt(pageno);
        } catch ( Exception e ) {
            iPageno = 0;
        }
        try {
            iPageCount = Integer.parseInt(pagecount);
        } catch ( Exception e ) {
            iPageCount = 0;
        }

        String body = "";
        String sysButton = "";
        String[] rtnMsg = new String[2];

        if ( event.getBefore_result() < 0 || rs == null ) {
            rtnMsg[0] = PageGenerator.getErrorString(msgs,result);
            return rtnMsg;
        } else {
            //1.获得参考表和参考栏位
            body += "<script src='" + ctx.getUrl(TARGET_JAVASCRIPT) + "' type='text/javascript'></script>";
            body += "<script src='" + ctx.getUrl("/js/meizzDate.js") + "' type='text/javascript'></script>";

            try {
                FormInstanceManager fiManager = (FormInstanceManager) ctx.getAttribute(
                    SessionAttributes.SESSION_FORM_INSTANCE_MANAGER_NAME);
                String instanceid = event.getId();
                String reffldname = ctx.getParameter(SessionAttributes.REQUEST_REFERENCE_FIELD_NAME);
                String oldreffldnm = reffldname;
                //如果是QueryForm的过滤参考性字段，则截掉largelarge(在QueryGenerator中添加的)
                if (reffldname != null && reffldname.endsWith("largelarge")) {
                    reffldname = reffldname.substring(0, reffldname.length() - "largelarge".length());
                }

                FormInstance fi = fiManager.getFormInstance(instanceid);
                FormBean fb = FormBeanManager.getForm(fi.getFormid());
                String tblname = fb.getTbl();
                TableBean tb = TableBeanManager.getTable(tblname);
                FieldBean field = tb.getField(reffldname);
                String reftbl = field.getReftbl();
                String refname = field.getRefnamefld();
                String[] refnames;
                try {
                    StringTokenizer st = new StringTokenizer(refname, ",");
                    int refnamecount = st.countTokens();
                    refnames = new String[refnamecount];
                    for (int i = 0; i < refnamecount && st.hasMoreTokens(); i++) {
                        refnames[i] = st.nextToken();
                    }
                } catch ( Exception e ) {
                    e.printStackTrace();
                    refnames = new String[1];
                    refnames[0] = refname;
                }


                String refvalue = field.getRefvaluefld();
                //2.获得参考字段

                TableBean tb1 = TableBeanManager.getTable(reftbl);
                if (tb1 == null) {
                    rtnMsg[0] = "参考表定义错误[" + reftbl + "]";
                    return rtnMsg;
                }
                FieldBean[] fb1s = tb1.getSearchKey();
                FieldBean fbvalue = tb1.getField(refvalue);
                if (fbvalue == null) {
                    rtnMsg[0] = "参考字段定义错误[" + refname + "和" + refvalue + "]";
                    return rtnMsg;
                }
                //组成隐含串和查询条件组件
                String hiddentxt = "";
                String searchtxt = "";
                if (fb1s != null) {
                    for (int i = 0; i < fb1s.length; i++) {
                        FieldBean fbt = fb1s[i];
                        String name = fbt.getName();
                        String[] value = ctx.getParameters(name);
                        if (value != null) {
                            for (int j = 0; j < value.length; j++) {
                                hiddentxt += "<input type='hidden' name='" + name +
                                    "' value='" + value[j] + "'>";
                            }
                        }
                        try {
                            ElementBean e = new ElementBean(fbt);
                            if (e.getComponetTp() == ComponentType.HIDDEN_TYPE) {
                                e.setComponetTp(ComponentType.TEXT_TYPE);
                            }

                            e.setDefaultValue("");
                            e.setIsnull(true);
                            e.setVisible(true);

                            e.setReadonly(false);

                            AbstractFormComponent afc = AbstractFormComponent.getInstance(e);
                            if (value != null) {
                                for (int j = 0; j < value.length; j++) {
                                    if (value[j] == null)
                                        value[j] = "";
                                }
                                afc.setValues(value);
                            }
                            afc.setCtx(ctx);
                            searchtxt += "<tr class='filter_table_tr'>";
                            String csrc = afc.toHTML();
                            if ( csrc != null ) {
                                csrc = csrc.replaceFirst("<option","<option value=\"\"> <option");
                            }
                            if (!afc.useTd()) {
                                searchtxt += "<td class='filter_table_td'>" + csrc +
                                    "</td>";
                            }
                            else {
                                String tmp = csrc;
                                if (tmp != null) {
                                    tmp = tmp.replaceAll("<td class=\"page_form_title_td\">",
                                        "<td class='filter_table_title_td'>");
                                    tmp = tmp.replaceAll("<td class=\"page_form_td\">",
                                        "<td class='filter_table_td'>");
                                }
                                searchtxt += tmp;
                            }
                            searchtxt += "</tr>";
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                body += "<table class='reference_tbl'>";
                //1.形成表头
                body += "<tr class='reference_tbl_head_tr'>";
                body += "<td class='reference_tbl_head_value_td'>";
                body += fbvalue.getCaption();
                body += "</td>";
                for ( int i = 0 ; i < refnames.length ; i++ ) {
                    FieldBean fbname = tb1.getField(refnames[i]);
                    body += "<td class='reference_tbl_head_desc_td'>";
                    body += fbname.getCaption();
                    body += "</td>";
                }
                body += "</tr>";
                //2.输出表体
                try {
                    while (rs.next()) {
                        body += "<tr class='reference_tbl_content_tr'>";
                        body += "<td class='reference_tbl_content_value_td'>";
                        String name = DBUtil.fromDB(rs.getString(refvalue));
                        body += "<a href=\"#\" onClick=\"return refselect(opener.winform." +
                            oldreffldnm + ",'" + name + "')\">" + name + "</a>";
                        body += "</td>";
                        for ( int i = 0 ; i < refnames.length ; i++ ) {
                            body += "<td class='reference_tbl_content_desc_td'>";
                            body += DBUtil.fromDB(rs.getString(refnames[i]));
                            body += "</td>";
                        }
                        body += "</tr>";
                    }
                }
                catch (Exception e) {

                }
                body += "</table>";

                //3.构造公共参数
                String commPara = "<input type='hidden' name='" +
                    SessionAttributes.REQUEST_EVENT_ID_NAME + "' value='" +
                    EventType.REFERENCE_FIELD_EVENT_TYPE + "'>";
                commPara += "<input type='hidden' name='" +
                    SessionAttributes.REQUEST_INSATNCE_ID_NAME + "' value='" + event.getId() + "'>";
                commPara += "<input type='hidden' name='" +
                    SessionAttributes.REQUEST_REFERENCE_FIELD_NAME + "' value='" + oldreffldnm +
                    "'>";
                String commText = commPara + hiddentxt;
                String searchPara = commPara + searchtxt;
                body += "<table class='blank_table'></table>";
                //新添加11－17
                String filter = ctx.getParameter(SessionAttributes.REQUEST_REFERENCE_TEXT_NAME);
                body += "<form id='winform' method='post' action='" + ctx.getUrl(TARGET_TEMPLATE) +
                    "'>";
                body += commText;
                if (filter != null) {
                    body += "<input type='hidden' name='" +
                        SessionAttributes.REQUEST_REFERENCE_TEXT_NAME + "' value='" + filter + "'>";
                }
                body += "<input type='hidden' name='" + SessionAttributes.REQUEST_LIST_PAGENO_NAME +
                    "' value=''>";
                body += "<input type='hidden' name='" +
                    SessionAttributes.REQUEST_LIST_PAGECOUNT_NAME + "' value=''>";

                body += "</form>";
                sysButton = getReferenceButton(ctx,searchPara,iPageno,iPageCount,fb1s);
            } catch ( Exception e ) {
                e.printStackTrace();
            }
            //3.输出控制按钮
            rtnMsg[0] = body;
            rtnMsg[1] = sysButton;
        }

        return rtnMsg;
    }

    private static String getReferenceButton(SessionContext ctx,String searchPara,int iPageno,int iPageCount,FieldBean[] fb1s) {
        String body = "";
        body += "<table class='reference_button_tbl'>";
        body += "<tr class='reference_button_tbl_tr'>";

        if (fb1s != null) {
            body += "<td class='reference_button_tbl_td'>";
            String onclick = "if ( filter.style.visibility == 'hidden' ) { filter.style.visibility ='';} else { filter.style.visibility = 'hidden';}";
            body +=
                "<input class='reference_button_active' type='button' name='submit' value=' 过 滤 ' onclick=\"" +
                onclick + "\">";
            body += "</td>";
        }
        //输出首页
        body += "<td class='reference_button_tbl_td'>";
        if (iPageno == 0) {
            body += "<input class='reference_button_disabled' type='submit' name='submit' value=' 首 页 ' disabled='true'>";
        }
        else {
            body +=
                "<input class=\"reference_button_active\" type=\"button\" name=\"submit\" value=\" 首 页 \" onClick=\"buttonClick('0','" +
                iPageCount + "');\">";
        }
        body += "</td>";

        //生成上一页按钮
        body += "<td class='reference_button_tbl_td'>";
        if (iPageno <= 0) {
            body += "<input class='reference_button_disabled' type='submit' name='submit' value=' 上一页 ' disabled='true'>";
        }
        else {
            body += "<input type=\"button\" name=\"submit\" class=\"reference_button_active\" value=\" 上一页 \" onClick=\"buttonClick('" +
                (iPageno - 1) + "','" + iPageCount + "');\">";
        }
        body += "</td>";
        //生成下一页按钮
        body += "<td class='reference_button_tbl_td'>";
        if (iPageno >= iPageCount) {
            body += "<input class='reference_button_disabled' type='submit' name='submit' value=' 下一页 ' disabled='true'>";
        }
        else {
            body += "<input type=\"button\" name=\"submit\" class=\"reference_button_active\" value=\" 下一页 \" onClick=\"buttonClick('" +
                (iPageno + 1) + "','" + iPageCount + "');\">";
        }
        body += "</td>";
        //生成尾页按钮
        body += "<td class='reference_button_tbl_td'>";
        if (iPageno >= iPageCount) {
            body += "<input class='reference_button_disabled' type='submit' name='submit' value=' 尾 页 ' disabled='true'>";
        }
        else {
            body += "<input type=\"button\" name=\"submit\" class=\"reference_button_active\" value=\" 尾 页 \" onClick=\"buttonClick('" +
                iPageCount + "','" + iPageCount + "');\">";
        }
        body += "</td>";
        //生成关闭按钮
        body += "<td class='reference_button_tbl_td'>";
        body += "<input type=\"button\" name=\"submit\" class=\"reference_button_active\" value=\" 退 出 \" onClick=\"window.close();\">";
        body += "</td>";
        body += "</tr>";
        body += "</table>";
        body += "<table class='blank_table'></table>";
        if (fb1s != null) {
            //形成隐含域
            body += "<div id='filter' style='visibility:hidden'>";
            body += "<table class='filter_table'>";
            body += "<form id='winform7' method='post' action='" + ctx.getUrl(TARGET_TEMPLATE) +
                "'>";
            body += searchPara;
            body += "</form>";
            body += "</table>";
            body += "<table class='filter_button_table'>";
            body += "<tr class='filter_button_table_tr'>";
            body += "<td class='filter_button_table_td'>";
            body +=
                "<input type='submit' class='filter_button_active' name='submit' value=' 确 定 ' onClick='return winform7.submit();'>";
            body +=
                "<input type='reset' class='filter_button_active' name='reset' value='重新填写' onClick='return winform7.reset();'>";
            body += "</td>";
            body += "</tr>";
            body += "</table>";
            body += "</div>";
        }

        return body;
    }
}