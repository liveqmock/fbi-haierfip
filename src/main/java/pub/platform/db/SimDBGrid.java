package pub.platform.db;

import java.util.*;

import pub.platform.form.config.*;
import pub.platform.utils.Basic;



public class SimDBGrid {
	
	
	private String fid;		
	
	private String fsqlStr   = "";
	private String fWhereStr = "";
	
	////页记录条树
	private int fpageSize     = 50;
	//绝对页数
	private int fAbsolutePage = 1 ;
	///记录条树
	private int fRecordCount  = 0 ;
	///总页页数
	private int fTotalPage    = 0 ;
	
	//列表宽度
	private int ftableWidth    = 30 ;
	
	
	private ArrayList fieldCNNameContainer   = new ArrayList();  ////字段中文解释数组容器	
	private ArrayList fieldWidthArrContainer = new ArrayList(); ////字段宽度树组容器
	private ArrayList fieldenumContainer     = new ArrayList(); ////字段宽度树组容器
	private ArrayList fieldAlignContainer    = new ArrayList(); ///字段对齐组容器

	private String fieldCNStr     = "";
	private String fieldWidthStr  = "";
	private String fieldenumStr  = "";
	private String fieldAlignStr  = "";

	private String enumStr ="";

	ConnectionManager manager; //////连接管理

	///////////////////////////////////////////////////////////////////////
	private String dataPilotID=""; /////;   数据按钮集
	private String buttons ="moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast";     ///按钮集
	private String[] buttonsArr; ///按钮数组


	///////////////////////////////////////////////////////////////////////

	public SimDBGrid() {
		manager = ConnectionManager.getInstance();
	}

	public void setGridID(String gridID){
		this.fid = gridID;
	}
	
	public void setFieldValue(String fieldCNName,int fieldWidth)
	{
		setFieldValue(fieldCNName,fieldWidth,"");
	}
	public void setFieldValue(String fieldCNName,int fieldWidth,String enumType)
	{
		setFieldValue(fieldCNName,fieldWidth,enumType,"left");
	}
	public void setFieldValue(String fieldCNName,int fieldWidth,String enumType,String fieldAlign)
	{
		fieldCNNameContainer.add(fieldCNName);
		fieldWidthArrContainer.add(fieldWidth + "");
		fieldAlignContainer.add(fieldAlign);
		fieldenumContainer.add(enumType);
		
		if (fieldCNStr.equals(""))
		{
			fieldCNStr    = fieldCNName;
			fieldWidthStr = fieldWidth + "";
			fieldAlignStr = fieldAlign ;
			fieldenumStr  =  enumType;
		}else
		{
			fieldCNStr    = fieldCNStr    + "," + fieldCNName;
			fieldWidthStr = fieldWidthStr + "," + fieldWidth + "";
			fieldAlignStr = fieldAlignStr + "," + fieldAlign ;
			fieldenumStr  = fieldenumStr  + "," + enumType;
		}
	 
	}

	public void setfieldSQL(String fieldSQL){

	    this.fsqlStr = fieldSQL;
    }

    public void setwhereStr(String whereStr){

	   this.fWhereStr = whereStr;
   }
    
    public void setpagesize(int pagesize ){
	    this.fpageSize = pagesize;
    }


  


///////////////////////////////////////////////读取数据集数性质值//////////////////////////////////////////////
  

	///取得记录条数
	private void getRecordCount(){
	   if (fRecordCount <= 0) {
		   
		   String selectSql ="";

		   selectSql = " select count(*) as counnum from (" + fsqlStr + fWhereStr + ")";

			DatabaseConnection DBCon = manager.getConnection();
			try{
				RecordSet rs = DBCon.executeQuery(selectSql);//add
				
				if (rs.next()){
					
					fRecordCount = rs.getInt(0);
				}				
				
			} catch ( Exception e ) {
				e.printStackTrace();
			} finally{
				manager.releaseConnection(DBCon);
//                    DBCon.close();
			}

		}
    }

    ////返回总页数
	private void getTotalPage(){
		if ( (fRecordCount % fpageSize) == 0) {
			fTotalPage =  fRecordCount / fpageSize;
		} else {
			fTotalPage =  fRecordCount / fpageSize + 1;
		}
	}

	

	////返回字段宽度数组
	private void gettableWidth(){
		for(int i=0; i < fieldCNNameContainer.size(); i++)
		{
			ftableWidth  = ftableWidth +Integer.parseInt((String)fieldWidthArrContainer.get(i));
		}		
	}

///////////////////////////////////////构造数据集控件///////////////////////////////////////////////////

    ///生成可编辑的grid方法
    public String getDBGrid() {

	    String returnStr ="";

		returnStr = "<div  id=\"div_" + fid + "\" style=\"OVERFLOW: auto; width:100%\" >" +getEditDataTable()+ "</div>";


		return returnStr;
	}

   ///生成可编辑的数据表
	public String getEditDataTable() {

		 String returnStr ="";
		 
		 gettableWidth();
		 getRecordCount();
		 
		 getTotalPage();

		 String gridfieldStr = getFieldGrid();

	    
		 returnStr =getGridTitle()+getDataGridTitle()+gridfieldStr+getGridEnd();



		return returnStr;
	}


   ///取得数据表格的标题
    private String getGridTitle(){
		try{
			StringBuffer StrBuf = new StringBuffer();
		


			
			StrBuf.append("<table id=\"All_" + this.fid + "\"   bgcolor=\"#ffffff\" class=\"borderGroove\"   width=\"100%\" border=\"0\"  cellspacing=\"0\" cellpadding=\"0\"  >");


			StrBuf.append("<tr ><td>");

			StrBuf.append("<div  class=\"simdivt\"  id=\"divt_" + fid + "\" >");
			StrBuf.append("<table id=\"Title_" + fid + "\" width=\"" + ftableWidth + "\" border=\"0\"  cellspacing=\"0\" cellpadding=\"0\"  >");

			StrBuf.append("<tr onclick = 'makeTableSortable(\"Title_" + fid + "\",\"" + fid + "\")'>");
						
			StrBuf.append("<td   align=\"center\" valign=\"center\"  width=\"30\" >&nbsp;</td>");

			for (int i = 0; i < fieldCNNameContainer.size(); i++) {				
					StrBuf.append("<td  noWrap align=\"center\" valign=\"center\"  width=\"" + (String)fieldWidthArrContainer.get(i)+ "\" > " + (String)fieldCNNameContainer.get(i) + "</td>");

			}

			
			StrBuf.append("</tr></table></div>");
			return StrBuf.toString();

		}catch(Exception e){
			e.printStackTrace();
			return "";
		}

	}


	///取得数据表格的标题
	private String getDataGridTitle(){
		try{
			StringBuffer StrBuf = new StringBuffer();

			StrBuf.append("<tr><td>");

			StrBuf.append("<div  class=\"simdivbody\"  id=\"divfd_" + fid + "\">");
			StrBuf.append("<table id=\"" + fid + "\"  width=\"" + ftableWidth + "\" border=\"0\" cellspacing=\"1\" cellpadding=\"1\" activeIndex=0  " +
					"  SQLStr=\"" + fsqlStr + "\" fieldCN=\"" + fieldCNStr + "\"  enumType=\"" + fieldenumStr + "\"  whereStr=\"" + Basic.encode(fWhereStr)
					    + "\"  pageSize=\"" + fpageSize + "\"  AbsolutePage=\"" + fAbsolutePage + "\" TotalPage=\"" + fTotalPage +
					    "\" RecordCount=\"" + fRecordCount + "\" fieldwidth=\""  + fieldWidthStr + "\" tralign=\"" + fieldAlignStr + "\" >");
     
			return StrBuf.toString();

		}catch(Exception e){
			e.printStackTrace();
			return "";
		}

	}


	////获取字段值
	private String getFieldValue(RecordSet RS,int i){

		//初始化枚举信息
		try{
			EnumerationBean enumBean;

			String fieldValue = RS.getString(i);
			 if(!((String)fieldenumContainer.get(i)).equals("")) 
			 {
				
				enumStr    = fieldValue;
				
				if (fieldValue != null)
				{
					enumBean = EnumerationType.getEnu((String)fieldenumContainer.get(i));
					if ( enumBean != null ) 
					{
						enumStr = ( String )enumBean.getValue ( fieldValue.trim () ) ;
					} else 
					{
						enumStr = fieldValue.trim ();
					}
				}

				if (enumStr == null)
					enumStr ="";
				
			} 


		   return fieldValue;
		   
	   }catch(Exception e)
	   {
		  e.printStackTrace();
		  return "";
	   }


    }


  ///////////获取数据表格的只读cell///////////////////////////////////////////////////////
	private String getReadFieldCell(RecordSet RS,int i){
	    try{
		    String cellStr="";			
			String value = getFieldValue(RS,i);
			
			if (!((String)fieldenumContainer.get(i)).equals("")) 
			{
				cellStr = "<td noWrap width=\"" + (String)fieldWidthArrContainer.get(i) + "\"   align =\"" + (String)fieldAlignContainer.get(i) + "\"   attr=\"" + value + "\">" + enumStr + "</td>";
			} else 
			{
				cellStr = "<td noWrap width=\"" + (String)fieldWidthArrContainer.get(i) + "\"   align =\"" +(String)fieldAlignContainer.get(i)  + "\" >" + value + "</td>";
			}
		  
		   return  cellStr;
	   }catch(Exception e){
		  e.printStackTrace();
		  return "";
	  }

   }

  /////获取数据表格的row//////////////////////////////////////////////////////////////
  private String getFieldROW(RecordSet RS,int RecorderIndex){
	  try{
		  
		  String cellStr = "";
          
		  StringBuffer StrBuf = new StringBuffer();

		  if(RecorderIndex % 2 == 0)
		  {
			  StrBuf.append(
				  "<tr class=\"gridOddRow\"   OnMouseUp=\"Gride_OnMouseUp(this)\"  onclick=\"TR_click(this)\"  >");
		  } else 
		  {
			  StrBuf.append(
				  "<tr  class=\"gridEvenRow\"  OnMouseUp=\"Gride_OnMouseUp(this)\"    onclick=\"TR_click(this)\" >");
		  }

		 

		  cellStr = "<td align=\"center\" class=\"bodyHead\">&nbsp;</td>";
		  StrBuf.append(cellStr);
		

		  /////////////////判断数据集是否只读////////////////////////////////////////////////
		  for(int i = 0; i < fieldCNNameContainer.size(); i++) {
			   StrBuf.append(getReadFieldCell(RS, i));
		  }

		  StrBuf.append("</tr>");

		  return StrBuf.toString();
	   }catch(Exception e){
			e.printStackTrace();
			return "";
		}

  }

 



  ////获取数据集
   private String getFieldGrid(){
	   DatabaseConnection DBCon = manager.getConnection();

	   try{

		   int RecorderIndex =0;
		   String selectSql = fsqlStr + fWhereStr;


		   RecordSet rs =  DBCon.executeQuery(selectSql, fpageSize * (fAbsolutePage - 1) + 1, fpageSize);



		   StringBuffer StrBuf = new StringBuffer();

		   while (rs.next()) {
			   
			   StrBuf.append(getFieldROW(rs,RecorderIndex));

			   RecorderIndex++;
			  
		   }

		   rs.close();

		   return StrBuf.toString();

	   }catch(Exception e){
		  e.printStackTrace();
		  return "";

	  }finally{
		   manager.release();
		   manager.releaseConnection(DBCon);
	  }


   }

   /////获取数据集BOTTOM
   private String getGridEnd(){
	   try{
		   StringBuffer StrBuf = new StringBuffer();
		  
		   StrBuf.append("</table></div></td></tr>");

		   StrBuf.append("<tr class=\"gridEvenRow\" >");
		   StrBuf.append("<td>");
		   StrBuf.append("<table width=\"100%\" height=\"100%\" cellspacing=\"0\" cellpadding=\"0\"><tr  class=\"gridHead\" ><td id=\"all_recordcount\" class=\"statusBarText\" width=\"30%\">总条目:" + fRecordCount + "</td>");

		   StrBuf.append("<td width=\"30%\" align =\"right\" > <input class=\"buttonGrooveDisable\"  id=\"Covert_Button" + fid + "\" type=button class=button hideFocus=true style=\"height: 20px;width:30px\"   value=转到 onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\" onclick='Covert_Click(\"" +
				   fid + "\",\"Covert_Button" + fid + "\",\"Covert_Text" + fid + "\")'>");
		    StrBuf.append("<td width=\"10%\" class=\"statusBarText\" valign=\"top\"><input id=\"Covert_Text" + fid + "\" tableid=\"" + fid + "\"  buttonid=\"Covert_Button" + fid + "\"  style=\"height: 20px;width:30px\"   value=" + fAbsolutePage + " onKeyPress='return newturnPage1_1(\"" +
		    		fid + "\",\"Covert_Button" + fid + "\",\"Covert_Text" + fid + "\");\' >页</td>");
		   StrBuf.append("<td id=\"all_page\" class=\"statusBarText\" width=\"30%\" > 总页数:" + fTotalPage + "</td>");

		   StrBuf.append("</tr></table> </td></tr>");

		   StrBuf.append("</table></form>");

		   return StrBuf.toString();
	   }catch(Exception e){
		  e.printStackTrace();
		  return "";
	  }


   }

   ///////////////////////////////////////////////////////////////////////////////////////////////


   //////数据按钮集ID
   public void setdataPilotID(String dataPilotID){
	    this.dataPilotID =dataPilotID;
    }

    ///////数据按钮
    public void setbuttons(String buttons){

	    if(buttons.toLowerCase().equals("readonly"))
		    this.buttons ="moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast";
	   else if (buttons.toLowerCase().equals("default"))
		   this.buttons ="moveFirst,prevPage,movePrev,moveNext,nextPage,moveLast,appendRecord,editRecord,deleteRecord,cancelRecord,updateRecord";
	   else if (!buttons.toLowerCase().equals(""))
		   this.buttons =buttons;


	   if (!this.buttons.toLowerCase().equals(""))
		   buttonsArr = this.buttons.split(",",-2);
   }

   public String getDataPilot(){


       this.dataPilotID = this.fid +"Buttons";

	   StringBuffer StrBuf = new StringBuffer();

	   StrBuf.append("<table class=\"bordergrov\" attrib=datapilot  id=\""+dataPilotID+"\" tableName=\"" + fid +
			"\"  confirmdelete=true  confirmcancel=true  readonly=true border=0  cellspacing=0 cellpadding=0>");


	  StrBuf.append("<tr align=\"center\">");

	  for (int i = 0; i < buttonsArr.length; i++) {

		 if (buttonsArr[i].equals("moveFirst")) {
			 StrBuf.append("<td  > <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_moveFirst   buttontype=\"moveFirst\"   type=button hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"9\"  title=\"移动到第一页记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else if (buttonsArr[i].equals("prevPage")) {
			 StrBuf.append("<td > <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_prevPage  buttontype=\"prevPage\"   type=button  hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"7\"  title=\"向前翻页\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else if (buttonsArr[i].equals("movePrev")) {
			 StrBuf.append("<td > <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_movePrev   buttontype=\"movePrev\"   type=button  hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"3\"  title=\"移动到上一条记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else  if (buttonsArr[i].equals("moveNext")) {
			 StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_moveNext   buttontype=\"moveNext\"   type=button hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"4\"  title=\"移动到下一条记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\" ></td>");
		 } else if (buttonsArr[i].equals("nextPage")) {
			 StrBuf.append("<td > <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_nextPage   buttontype=\"nextPage\"   type=button  hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\"8\"  title=\"向后翻页\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else  if (buttonsArr[i].equals("moveLast")) {
			 StrBuf.append("<td > <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_moveLast   buttontype=\"moveLast\"  type=button  hideFocus=true style=\"height: 22px;width:30px; font-family: Webdings\" value=\":\"  title=\"移动到最后一页记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 }
		
		 if (buttonsArr[i].equals("insertRecord")) {
			 StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_insertRecord   buttontype=\"insertRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"插入\"  title=\"插入一条新记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else   if (buttonsArr[i].equals("appendRecord")) {
			 StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_appendRecord   buttontype=\"appendRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"添加\"  title=\"添加一条新记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else   if (buttonsArr[i].equals("editRecord")) {
			 StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_editRecord   buttontype=\"editRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"修改\"  title=\"修改当前记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else if (buttonsArr[i].equals("deleteRecord")) {
			 StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_deleteRecord   buttontype=\"deleteRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"删除\"  title=\"删除当前记录\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else  if (buttonsArr[i].equals("cancelRecord")) {
			 StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_cancelRecord   buttontype=\"cancelRecord\"  type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"撤销\"  title=\"撤销对当前记录的修改\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 } else  if (buttonsArr[i].equals("updateRecord")) {
			 StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_updateRecord   buttontype=\"updateRecord\"   type=button  hideFocus=true style=\"height: 22px;width:45px\" value=\"确认\"  title=\"确认对当前记录的修改\"  onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		 }
		
		 String[] buttonArr = Basic.splite(buttonsArr[i], "=");
		 if (buttonArr.length > 1) {
				 StrBuf.append("<td> <input class=\"buttonGrooveDisable\" id=" + dataPilotID + "_" +buttonArr[1] + " buttontype=\"" + buttonArr[1] +
				"\"   type=button  hideFocus=true style=\"height: 22px; fontFamily: Webdings\" value=\"" +buttonArr[0] + "\"    onmouseover=\"button_onmouseover()\" onmouseout=\"button_onmouseout()\"  onclick=\"dataPilotButton_onclick()\"></td>");
		}


	}
	StrBuf.append("</tr></table>");
	return StrBuf.toString();

   }



}
