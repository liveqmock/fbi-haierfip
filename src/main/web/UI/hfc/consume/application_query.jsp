<%@ page contentType="text/html; charset=GBK" %>
<%@ page import="javax.xml.ws.Response" %>
<%@ page import="fip.repository.model.Xfapp" %>
<%@ page import="java.util.List" %>
<%@ page import="pub.platform.db.DB2_81" %>
<%@ page import="javax.sql.rowset.CachedRowSet" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="net.sf.json.JSONObject" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="net.sf.json.JSONArray" %>
<%@ page import="fip.service.fip.T8118Service" %>
<%@ page import="org.apache.commons.lang.StringUtils" %>
<%
    request.setCharacterEncoding("GBK");
    String sql1 = "";
    CachedRowSet crs = null;
    DecimalFormat df = new DecimalFormat("###,##0.00");
    String doTYpe = request.getParameterMap().get("dotype")[0].toString();
    String appno = request.getParameterMap().get("appno")[0].toString();      //该笔申请单号
    appno = StringUtils.isEmpty(appno) ? " " : appno;
    String idtype = request.getParameterMap().get("idtype")[0].toString();
    String idvalue = request.getParameterMap().get("idval")[0].toString();
    String addr = new String(request.getParameterMap().get("addr")[0].toString().getBytes("ISO-8859-1"), "UTF-8");
    JSONArray ja = new JSONArray();
    //获取list数据
    try {
        sql1 = "select t.appno,t.name,t.appamt from xfapp t where t.idtype='" + idtype + "' and t.id='" + idvalue +
                "' and t.appstatus in ('1','2') and t.appno <>'" + appno + "'";
        crs = DB2_81.getRs(sql1);
        JSONObject result = null;
        JSONObject rsttmp = null;
        List chkIDlist = new ArrayList();     //客户姓名 申请单信息
        List totalamtlist = new ArrayList();  //贷款余额
        List chkADDRlist = new ArrayList();   //配送地址check
        List allList = new ArrayList();      //所有数据
        String idExists = "0";  //不存在0
        double totalamt = 0;
        while (crs.next()) {
            result = new JSONObject();
            result.put("name", crs.getString("name"));
            result.put("appno", crs.getString("appno"));
            result.put("appamt", crs.getString("appamt"));
            chkIDlist.add(result);
            //SBS T8118 查询账户余额
            String sqlActno = "select distinct t.clientact from fip_cutpaydetl t where t.appno='" + crs.getString("appno") + "'" +
                    "  and t.Origin_Bizid = 'XF'";
            CachedRowSet crsActno = DB2_81.getRs(sqlActno);
            if (crsActno.size() > 0) {
                crsActno.next();
                T8118Service t8118Service = new T8118Service();
                totalamt += t8118Service.qryActBal(crsActno.getString("clientact"));
            } else {
                totalamt += Double.parseDouble(crs.getString("appamt"));
            }
        }
        rsttmp = new JSONObject();
        rsttmp.put("chkID", chkIDlist);
        allList.add(rsttmp);
        result = new JSONObject();
        result.put("totalamt", String.valueOf(totalamt));
        allList.add(result);

        sql1 = "select t.appno,t.name,t.appamt from xfapp t where t.appstatus in ('1','2') and t.addr='" + addr + "' and (" +
                "t.idtype<>'" + idtype + "' or t.id<>'" + idvalue + "') and t.appno <>'" + appno + "'";
        crs = DB2_81.getRs(sql1);
        String addrexists = "0";  //存在重复 1 不存在重复 0
        if (crs.size() > 0) {
            addrexists = "申请单号:";
            while (crs.next()) {
                addrexists += crs.getString("appno") + ";  ";
            }
        }
        result = new JSONObject();
        result.put("chkaddr", addrexists);
        allList.add(result);
        ja = JSONArray.fromObject(allList);
    } catch (Exception ex) {
        ex.printStackTrace();
    }
    System.out.print(ja.toString());
    response.setContentType("application/json");
    String bb = ja.toString();
    response.getWriter().write(bb);
//    response.setHeader("Charset","gb2312");
//    String bb = java.net.URLEncoder.encode(aa.toString(),"UTF-8");
//    response.setCharacterEncoding("GBK");
%>