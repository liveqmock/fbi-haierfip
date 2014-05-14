<%@include file="/global.jsp" %>
<%@ page contentType="text/html; charset=GBK" %>
<HTML>
	<HEAD>
     	<LINK href="<%=contextPath%>/css/ccb.css" type="text/css" rel="stylesheet">
    		<script language="javascript" src="<%=contextPath%>/js/basic.js"></script>
          <script language="javascript" src="<%=contextPath%>/js/xmlHttp.js"></script>
           <script language="javascript" src="allmakeprt.js"></script>

	</HEAD>

	<body style="FONT-SIZE: 12px" onload="init();" MS_POSITIONING="GridLayout" >
			<fieldset style="PADDING-RIGHT: 5px; PADDING-LEFT: 5px; FONT-SIZE: 12px; Z-INDEX: 103; LEFT: 16px; PADDING-BOTTOM: 5px; WIDTH: 492px; PADDING-TOP: 5px; POSITION: absolute; TOP: 7px; HEIGHT: 20px"><legend>打印主题</legend>
          		<table style="width:100%;height:20px">
                    	<tr>
                              <td>打印主题 &nbsp; &nbsp;&nbsp;&nbsp;      <input id="prttitle" style="width:350" value="打印列表"></td>
                    	</tr>

                    </table>

          	</fieldset>

			<fieldset style="PADDING-RIGHT: 5px; PADDING-LEFT: 5px; FONT-SIZE: 12px; Z-INDEX: 103; LEFT: 16px; PADDING-BOTTOM: 5px; WIDTH: 492px; PADDING-TOP: 5px; POSITION: absolute; TOP: 60px; HEIGHT: 207px"><legend>打印字段选择</legend>
                    <SELECT ondblclick="addWebFilter(filterSelect)" style="Z-INDEX: 101; LEFT: 21px; WIDTH: 154px; POSITION: absolute; TOP: 24px; HEIGHT: 166px" size="10" name="filterSelect"></SELECT>
				<DIV style="DISPLAY: inline; Z-INDEX: 110; LEFT: 450px; WIDTH: 15px; POSITION: absolute; TOP: 141px; HEIGHT: 27px" onclick="moveDown(addedFilters)" ms_positioning="FlowLayout">下移</DIV>
				<INPUT style="Z-INDEX: 108; LEFT: 443px; WIDTH: 26px; POSITION: absolute; TOP: 129px; HEIGHT: 54px" onclick="moveDown(addedFilters)" type="button">
				<INPUT style="Z-INDEX: 104; LEFT: 190px; WIDTH: 67px; POSITION: absolute; TOP: 69px; HEIGHT: 22px" onclick="addAllWebFilter(filterSelect)" type="button" value="全部添加">
				<SELECT ondblclick="removeWebFilter(addedFilters)"  onclick="changefield()" style="Z-INDEX: 102; LEFT: 273px; WIDTH: 154px; POSITION: absolute; TOP: 26px; HEIGHT: 166px" size="10" name="addedFilters">
				</SELECT>
				<INPUT style="Z-INDEX: 103; LEFT: 190px; WIDTH: 67px; POSITION: absolute; TOP: 34px; HEIGHT: 22px" onclick="addWebFilter(filterSelect)" type="button" value="添加"></FONT>
				<INPUT style="Z-INDEX: 105; LEFT: 191px; WIDTH: 67px; POSITION: absolute; TOP: 157px; HEIGHT: 22px" onclick="removeAllWebFilter(addedFilters)" type="button" value="全部删除">
				<INPUT style="Z-INDEX: 106; LEFT: 191px; WIDTH: 67px; POSITION: absolute; TOP: 122px; HEIGHT: 22px" onclick="removeWebFilter(addedFilters)" type="button" value="删除">
				<INPUT style="Z-INDEX: 107; LEFT: 443px; WIDTH: 25px; POSITION: absolute; TOP: 34px; HEIGHT: 56px" onclick="moveUp(addedFilters)" type="button">
				<DIV style="DISPLAY: inline; Z-INDEX: 109; LEFT: 449px; WIDTH: 15px; POSITION: absolute; TOP: 45px; HEIGHT: 27px" onclick="moveUp(addedFilters)" ms_positioning="FlowLayout">上移</DIV>
			</fieldset>

			<fieldset style="PADDING-RIGHT: 5px; PADDING-LEFT: 5px; FONT-SIZE: 12px; Z-INDEX: 103; LEFT: 16px; PADDING-BOTTOM: 5px; WIDTH: 492px; PADDING-TOP: 5px; POSITION: absolute; TOP: 267px; HEIGHT: 80px"><legend>编辑打印字段</legend>
          		<table style="width:100%;height:20px">
                    	<tr>
                              <td align="right">标题名称</td><td><input id="childtitle" style="width:75" value="" disabled="true"></td><td align="right">标题宽度</td><td><input id="childwidth" style="width:75" value="0" disabled="true" onkeypress="onKeyPressInputInteger()"></td><td align="right">对齐位置</td><td><SELECT style="WIDTH: 75px; " name="childalign" disabled="true"><option value="0">居左</option><option value="1">居右</option><option value="2">居中</option></SELECT></td>
                    	</tr>
                         <tr>
                              <td></td><td></td><td><INPUT id="addbut" style=" WIDTH: 67px; " onclick="addfield()" type="button" value="添加" >	</td><td><INPUT id="editbut" style=" WIDTH: 67px; " onclick="editfield()" type="button" value="修改"></td><td><INPUT  id="savebut" style=" WIDTH: 67px; " onclick="savefield()" type="button" value="保存" disabled="true"></td><td><INPUT id="calbut" style=" WIDTH: 67px; " onclick="calfield()" type="button" value="撤销" disabled="true"></td>
                    	</tr>

                    </table>

          	</fieldset>



			<INPUT style="Z-INDEX: 103; LEFT: 300px; WIDTH: 67px; POSITION: absolute; TOP: 370px; HEIGHT: 22px" onclick="pint_click()" type="button" value="确认"></FONT>
               <INPUT style="Z-INDEX: 105; LEFT: 390px; WIDTH: 67px; POSITION: absolute; TOP: 370px; HEIGHT: 22px" onclick="window.close();" type="button" value="关闭">


	</body>
</HTML>
