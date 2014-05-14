<%@include file="/global.jsp" %>
<%@ page contentType="text/html;charset=GBK"%>

<%@ page import="java.sql.*"%>

<%@ page import="pub.platform.db.*"%>
<%@ page import="pub.platform.utils.*"%>

<%
  String titles      =Basic.decode(Basic.decode(request.getParameter("titles")));
  String widths      = request.getParameter("widths");
  String alignments  = request.getParameter("alignments");
  String initdisps   = request.getParameter("initdisps");
  String printdate   = request.getParameter("printdate");
  String printtime   = request.getParameter("printtime");
  String printmemo   = request.getParameter("printmemo");
  
  
  
  
  
  

  if ( printdate == null || printdate.trim().length() == 0 ) {
	 printdate = "1";
  }
  if ( printtime == null || printtime.trim().length() == 0 ) {
	 printtime = "1";
  }
  if ( printmemo == null || printmemo.trim().length() == 0 ) {
	 printmemo = "制表时间：";
  }

  //titles = DBUtil.toDB(titles,"ISO-8859-1","GBK");

  

  String[] titleArr     = titles.split(",");
  String[] widthArr     = widths.split(",");
  String[] alignmentArr = alignments.split(",");
  String[] initdispArr  = initdisps.split(",");
  String sqlStr     =Basic.decode(Basic.decode( request.getParameter("sqlstr")));
 
  String rpttitle   = Basic.decode(Basic.decode(request.getParameter("rpttitle")));


  System.out.println(sqlStr);
  
    System.out.println(titles);

  if ( rpttitle == null || rpttitle.trim().length() == 0 ) {
       rpttitle = "查询列表";
   }

  Connection conn = null;
  PreparedStatement prepStmtNew =null;
  try
  {
	    DatabaseConnection dconn= ConnectionManager.getInstance().get();
	    if(dconn!=null){
		    conn = dconn.getConnection();
	    }

	    if(conn!=null){
		  	prepStmtNew=conn.prepareStatement(sqlStr);
	  	}
	  	
	  	
	  	
	  	
	  if(prepStmtNew!=null){
		  ResultSet rs = prepStmtNew.executeQuery();

		/*  engine.setUnicodeOption(1);
		  
		  engine.setRootPath("<%=contextPath%>/UI/system/public/print/");

		  engine.addDataSet("userlist",rs);
		  engine.bind("gridlist");*/
	  }
	  
	   //prepStmtNew.close();
  }catch(Exception e){
  	ConnectionManager.getInstance().release();
  }
  finally
  {
	 
	ConnectionManager.getInstance().release();
  }
%>
<HTML>

<HEAD>

<TITLE>通用报表打印</TITLE>
<link href="<%=contextPath%>/css/ccb.css" rel="stylesheet" type="text/css">
</HEAD>


<BODY style="margin: 0" onLoad="curinit();">

<table height="100%" width="100%" border=0 cellpadding=0 cellspacing=0>

  <tr height="100%">

    <td valign="top">


    </td>

  </tr>
</table>

</BODY>

</HTML>
<script>
   function curinit() {
	MRViewer.setVariable("rpttitle","<%=rpttitle%>");



	MRViewer.setVariable("title1","<%=titleArr[0]%>");
	MRViewer.setVariable("title2","<%=titleArr[1]%>");
	MRViewer.setVariable("title3","<%=titleArr[2]%>");
	MRViewer.setVariable("title4","<%=titleArr[3]%>");
	MRViewer.setVariable("title5","<%=titleArr[4]%>");
	MRViewer.setVariable("title6","<%=titleArr[5]%>");
	MRViewer.setVariable("title7","<%=titleArr[6]%>");
	MRViewer.setVariable("title8","<%=titleArr[7]%>");
	MRViewer.setVariable("title9","<%=titleArr[8]%>");
	MRViewer.setVariable("title10","<%=titleArr[9]%>");
	MRViewer.setVariable("title11","<%=titleArr[10]%>");
	MRViewer.setVariable("title12","<%=titleArr[11]%>");
	MRViewer.setVariable("title13","<%=titleArr[12]%>");
	MRViewer.setVariable("title14","<%=titleArr[13]%>");
	MRViewer.setVariable("title15","<%=titleArr[14]%>");
	MRViewer.setVariable("title16","<%=titleArr[15]%>");
	MRViewer.setVariable("title17","<%=titleArr[16]%>");


   MRViewer.setVariable("rpttitle","<%=rpttitle%>");
    MRViewer.setVariable("isequals","0");

    if ( <%=initdispArr[0]%>=="1" ) {
	   MRViewer.setVariable("A1","1");
	   MRViewer.setVariable("alignment1","<%=alignmentArr[0]%>");
	   MRViewer.setVariable("w1","<%=widthArr[0]%>");
    } else {
	   MRViewer.setVariable("A1","0");
	   MRViewer.setVariable("w1","0");
    }

    if ( <%=initdispArr[1]%>=="1" ) {
	   MRViewer.setVariable("A2","1");
	   MRViewer.setVariable("alignment2","<%=alignmentArr[1]%>");
	   MRViewer.setVariable("w2","<%=widthArr[1]%>");
    } else {
	   MRViewer.setVariable("A2","0");
	   MRViewer.setVariable("w2","0");
    }

	if ( <%=initdispArr[2]%>=="1" ) {
	   MRViewer.setVariable("alignment3","<%=alignmentArr[2]%>");
	   MRViewer.setVariable("A3","1");
	   MRViewer.setVariable("w3","<%=widthArr[2]%>");
    } else {
	   MRViewer.setVariable("A3","0");
	   MRViewer.setVariable("w3","0");
    }

    if ( <%=initdispArr[3]%>=="1" ) {
	   MRViewer.setVariable("A4","1");
	   MRViewer.setVariable("alignment4","<%=alignmentArr[3]%>");
	   MRViewer.setVariable("w4","<%=widthArr[3]%>");
    } else {
	   MRViewer.setVariable("A4","0");
	   MRViewer.setVariable("w4","0");
    }


    if ( <%=initdispArr[4]%>=="1" ) {
	   MRViewer.setVariable("A5","1");
	   MRViewer.setVariable("alignment5","<%=alignmentArr[4]%>");
	   MRViewer.setVariable("w5","<%=widthArr[4]%>");
    } else {
	   MRViewer.setVariable("A5","0");
	   MRViewer.setVariable("w5","0");
    }

    if ( <%=initdispArr[5]%>=="1" ) {
	   MRViewer.setVariable("A6","1");
	   MRViewer.setVariable("alignment6","<%=alignmentArr[5]%>");
	   MRViewer.setVariable("w6","<%=widthArr[5]%>");
    } else {
	   MRViewer.setVariable("A6","0");
	   MRViewer.setVariable("w6","0");
    }


   if ( <%=initdispArr[6]%>=="1" ) {
	   MRViewer.setVariable("A7","1");
	   MRViewer.setVariable("alignment7","<%=alignmentArr[6]%>");
	   MRViewer.setVariable("w7","<%=widthArr[6]%>");
    } else {
	   MRViewer.setVariable("A7","0");
	   MRViewer.setVariable("w7","0");
    }

	if ( <%=initdispArr[7]%>=="1" ) {
	   MRViewer.setVariable("A8","1");
	   MRViewer.setVariable("alignment8","<%=alignmentArr[7]%>");
	   MRViewer.setVariable("w8","<%=widthArr[7]%>");
    } else {
	   MRViewer.setVariable("A8","0");
	   MRViewer.setVariable("w8","0");
    }
   if ( <%=initdispArr[8]%>=="1" ) {
	   MRViewer.setVariable("alignment9","<%=alignmentArr[8]%>");
	   MRViewer.setVariable("A9","1");
	   MRViewer.setVariable("w9","<%=widthArr[8]%>");
    } else {
	   MRViewer.setVariable("A9","0");
	   MRViewer.setVariable("w9","0");
    }
	if ( <%=initdispArr[9]%>=="1" ) {
	   MRViewer.setVariable("alignment10","<%=alignmentArr[9]%>");
	   MRViewer.setVariable("A10","1");
	   MRViewer.setVariable("w10","<%=widthArr[9]%>");
    } else {
	   MRViewer.setVariable("A10","0");
	   MRViewer.setVariable("w10","0");
    }
    if ( <%=initdispArr[10]%>=="1" ) {

	   MRViewer.setVariable("alignment11","<%=alignmentArr[10]%>");
	   MRViewer.setVariable("A11","1");
	   MRViewer.setVariable("w11","<%=widthArr[10]%>");
    } else {
	   MRViewer.setVariable("A11","0");
	   MRViewer.setVariable("w11","0");
    }

	if ( <%=initdispArr[11]%>=="1" ) {
	   MRViewer.setVariable("A12","1");
	   MRViewer.setVariable("alignment12","<%=alignmentArr[11]%>");
	   MRViewer.setVariable("w12","<%=widthArr[11]%>");
    } else {
	   MRViewer.setVariable("A12","0");
	   MRViewer.setVariable("w12","0");
    }

	if ( <%=initdispArr[12]%>=="1" ) {
	   MRViewer.setVariable("A13","1");
	   MRViewer.setVariable("alignment13","<%=alignmentArr[12]%>");
	   MRViewer.setVariable("w13","<%=widthArr[12]%>");
    } else {
	   MRViewer.setVariable("A13","0");
	   MRViewer.setVariable("w13","0");
    }
   if ( <%=initdispArr[13]%>=="1" ) {
	   MRViewer.setVariable("alignment14","<%=alignmentArr[13]%>");
	   MRViewer.setVariable("A14","1");
	   MRViewer.setVariable("w14","<%=widthArr[13]%>");
    } else {
	   MRViewer.setVariable("A14","0");
	   MRViewer.setVariable("w14","0");
    }
	if ( <%=initdispArr[14]%>=="1" ) {
	   MRViewer.setVariable("alignment15","<%=alignmentArr[14]%>");
	   MRViewer.setVariable("A15","1");
	   MRViewer.setVariable("w15","<%=widthArr[14]%>");
    } else {
	   MRViewer.setVariable("A15","0");
	   MRViewer.setVariable("w15","0");
    }
    if ( <%=initdispArr[15]%>=="1" ) {

	   MRViewer.setVariable("alignment16","<%=alignmentArr[15]%>");
	   MRViewer.setVariable("A16","1");
	   MRViewer.setVariable("w16","<%=widthArr[15]%>");
    } else {
	   MRViewer.setVariable("A16","0");
	   MRViewer.setVariable("w16","0");
    }

	if ( <%=initdispArr[16]%>=="1" ) {

	   MRViewer.setVariable("alignment17","<%=alignmentArr[16]%>");
	   MRViewer.setVariable("A17","1");
	   MRViewer.setVariable("w17","<%=widthArr[16]%>");
    } else {
	   MRViewer.setVariable("A17","0");
	   MRViewer.setVariable("w17","0");
    }

	MRViewer.preview();
   }


</script>
